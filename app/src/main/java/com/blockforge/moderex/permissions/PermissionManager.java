package com.blockforge.moderex.permissions;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.database.DatabaseManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Built-in permission system for ModereX.
 * Acts as a fallback when LuckPerms is not available.
 * Priority: LuckPerms > ModereX built-in.
 */
public class PermissionManager {

    private final ModereX plugin;
    private final DatabaseManager db;

    // Cache: rankId -> Rank
    private final Map<Integer, Rank> rankCache = new ConcurrentHashMap<>();

    // Cache: playerUUID -> List<rank IDs>
    private final Map<String, List<Integer>> playerRankCache = new ConcurrentHashMap<>();

    // Cache: playerUUID -> resolved permissions (permission -> granted)
    private final Map<String, Map<String, Boolean>> resolvedPermissionCache = new ConcurrentHashMap<>();

    public PermissionManager(ModereX plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
    }

    /**
     * Initialize the permission system - load ranks and create defaults if needed.
     */
    public void initialize() {
        try {
            loadAllRanks();

            // Create default ranks if none exist
            if (rankCache.isEmpty()) {
                createDefaultRanks();
                loadAllRanks(); // Reload after creating defaults
            }

            plugin.getLogger().info("[Permissions] Loaded " + rankCache.size() + " ranks");
        } catch (Exception e) {
            plugin.logError("[Permissions] Failed to initialize permission system", e);
        }
    }

    // ==================== Rank CRUD ====================

    /**
     * Get all ranks sorted by weight.
     */
    public List<Rank> getAllRanks() {
        return rankCache.values().stream()
                .sorted(Comparator.comparingInt(Rank::getWeight))
                .collect(Collectors.toList());
    }

    /**
     * Get a rank by ID.
     */
    public Rank getRank(int id) {
        return rankCache.get(id);
    }

    /**
     * Get a rank by name (case-insensitive).
     */
    public Rank getRankByName(String name) {
        return rankCache.values().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    /**
     * Get the default rank (assigned to new players).
     */
    public Rank getDefaultRank() {
        return rankCache.values().stream()
                .filter(Rank::isDefault)
                .findFirst().orElse(null);
    }

    /**
     * Create a new rank.
     */
    public Rank createRank(String name, String displayName, String prefix, String suffix, int weight, boolean isDefault) throws SQLException {
        long now = System.currentTimeMillis();

        // If this is set as default, unset other defaults
        if (isDefault) {
            db.update("UPDATE moderex_ranks SET is_default = 0 WHERE is_default = 1");
        }

        db.update(
                "INSERT INTO moderex_ranks (name, display_name, prefix, suffix, weight, is_default, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                name.toLowerCase(), displayName, prefix, suffix, weight, isDefault ? 1 : 0, now, now
        );

        // Get the inserted ID
        int id = db.query("SELECT id FROM moderex_ranks WHERE name = ?", rs -> {
            if (rs.next()) return rs.getInt("id");
            return -1;
        }, name.toLowerCase());

        Rank rank = new Rank(id, name.toLowerCase(), displayName, prefix, suffix, weight, isDefault);
        rank.setCreatedAt(now);
        rank.setUpdatedAt(now);
        rankCache.put(id, rank);
        invalidateAllPlayerCaches();

        return rank;
    }

    /**
     * Update a rank's properties.
     */
    public void updateRank(int id, String displayName, String prefix, String suffix, int weight, boolean isDefault) throws SQLException {
        long now = System.currentTimeMillis();

        if (isDefault) {
            db.update("UPDATE moderex_ranks SET is_default = 0 WHERE is_default = 1");
        }

        db.update(
                "UPDATE moderex_ranks SET display_name = ?, prefix = ?, suffix = ?, weight = ?, is_default = ?, updated_at = ? WHERE id = ?",
                displayName, prefix, suffix, weight, isDefault ? 1 : 0, now, id
        );

        Rank rank = rankCache.get(id);
        if (rank != null) {
            rank.setDisplayName(displayName);
            rank.setPrefix(prefix);
            rank.setSuffix(suffix);
            rank.setWeight(weight);
            rank.setDefault(isDefault);
            rank.setUpdatedAt(now);
        }

        invalidateAllPlayerCaches();
    }

    /**
     * Delete a rank. Players with this rank get reassigned to the default rank.
     */
    public void deleteRank(int id) throws SQLException {
        Rank defaultRank = getDefaultRank();

        // Reassign players to default rank
        if (defaultRank != null && defaultRank.getId() != id) {
            db.update("UPDATE moderex_player_ranks SET rank_id = ? WHERE rank_id = ?", defaultRank.getId(), id);
        } else {
            db.update("DELETE FROM moderex_player_ranks WHERE rank_id = ?", id);
        }

        // Remove permissions and inheritance
        db.update("DELETE FROM moderex_rank_permissions WHERE rank_id = ?", id);
        db.update("DELETE FROM moderex_rank_inheritance WHERE rank_id = ? OR parent_rank_id = ?", id, id);
        db.update("DELETE FROM moderex_ranks WHERE id = ?", id);

        rankCache.remove(id);
        invalidateAllPlayerCaches();
    }

    /**
     * Reorder ranks by updating their weights.
     */
    public void reorderRanks(List<Integer> orderedIds) throws SQLException {
        for (int i = 0; i < orderedIds.size(); i++) {
            int rankId = orderedIds.get(i);
            int newWeight = (i + 1) * 10;
            db.update("UPDATE moderex_ranks SET weight = ?, updated_at = ? WHERE id = ?", newWeight, System.currentTimeMillis(), rankId);

            Rank rank = rankCache.get(rankId);
            if (rank != null) {
                rank.setWeight(newWeight);
            }
        }
        invalidateAllPlayerCaches();
    }

    // ==================== Rank Permissions ====================

    /**
     * Set a permission on a rank.
     */
    public void setRankPermission(int rankId, String permission, boolean granted) throws SQLException {
        // Use REPLACE to handle both insert and update
        db.update("DELETE FROM moderex_rank_permissions WHERE rank_id = ? AND permission = ?", rankId, permission);
        db.update("INSERT INTO moderex_rank_permissions (rank_id, permission, granted) VALUES (?, ?, ?)",
                rankId, permission, granted ? 1 : 0);

        Rank rank = rankCache.get(rankId);
        if (rank != null) {
            rank.setPermission(permission, granted);
        }

        // Auto-sync to LuckPerms if available
        syncRankToLuckPerms(rankId);

        invalidateAllPlayerCaches();
    }

    /**
     * Remove a permission from a rank (inherit from parent).
     */
    public void removeRankPermission(int rankId, String permission) throws SQLException {
        db.update("DELETE FROM moderex_rank_permissions WHERE rank_id = ? AND permission = ?", rankId, permission);

        Rank rank = rankCache.get(rankId);
        if (rank != null) {
            rank.removePermission(permission);
        }

        invalidateAllPlayerCaches();
    }

    // ==================== Rank Inheritance ====================

    /**
     * Set the parent ranks for a rank.
     */
    public void setRankInheritance(int rankId, List<Integer> parentIds) throws SQLException {
        db.update("DELETE FROM moderex_rank_inheritance WHERE rank_id = ?", rankId);
        for (int parentId : parentIds) {
            if (parentId != rankId) { // Prevent self-inheritance
                db.update("INSERT INTO moderex_rank_inheritance (rank_id, parent_rank_id) VALUES (?, ?)", rankId, parentId);
            }
        }

        Rank rank = rankCache.get(rankId);
        if (rank != null) {
            rank.clearParentRanks();
            for (int parentId : parentIds) {
                if (parentId != rankId) rank.addParentRank(parentId);
            }
        }

        invalidateAllPlayerCaches();
    }

    // ==================== Player Ranks ====================

    /**
     * Get all ranks assigned to a player.
     */
    public List<Rank> getPlayerRanks(UUID playerUuid) {
        String uuid = playerUuid.toString();
        List<Integer> rankIds = playerRankCache.get(uuid);

        if (rankIds == null) {
            try {
                rankIds = loadPlayerRanks(uuid);
                playerRankCache.put(uuid, rankIds);
            } catch (SQLException e) {
                plugin.logError("[Permissions] Failed to load player ranks for " + uuid, e);
                return Collections.emptyList();
            }
        }

        return rankIds.stream()
                .map(rankCache::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Rank::getWeight))
                .collect(Collectors.toList());
    }

    /**
     * Get the player's primary (highest weight) rank.
     */
    public Rank getPrimaryRank(UUID playerUuid) {
        List<Rank> ranks = getPlayerRanks(playerUuid);
        if (ranks.isEmpty()) {
            return getDefaultRank();
        }
        // Highest weight = most important
        return ranks.stream()
                .max(Comparator.comparingInt(Rank::getWeight))
                .orElse(getDefaultRank());
    }

    /**
     * Assign a rank to a player.
     */
    public void setPlayerRank(UUID playerUuid, String playerName, int rankId, boolean primary, String assignedBy) throws SQLException {
        String uuid = playerUuid.toString();
        long now = System.currentTimeMillis();

        // If setting as primary, unset other primaries for this player
        if (primary) {
            db.update("UPDATE moderex_player_ranks SET is_primary = 0 WHERE player_uuid = ?", uuid);
        }

        // Check if already has this rank
        int existing = db.query(
                "SELECT COUNT(*) FROM moderex_player_ranks WHERE player_uuid = ? AND rank_id = ?",
                rs -> { rs.next(); return rs.getInt(1); }, uuid, rankId
        );

        if (existing > 0) {
            db.update("UPDATE moderex_player_ranks SET is_primary = ?, assigned_by = ?, assigned_at = ? WHERE player_uuid = ? AND rank_id = ?",
                    primary ? 1 : 0, assignedBy, now, uuid, rankId);
        } else {
            db.update("INSERT INTO moderex_player_ranks (player_uuid, player_name, rank_id, is_primary, assigned_by, assigned_at) VALUES (?, ?, ?, ?, ?, ?)",
                    uuid, playerName, rankId, primary ? 1 : 0, assignedBy, now);
        }

        // Invalidate cache
        playerRankCache.remove(uuid);
        resolvedPermissionCache.remove(uuid);

        // Auto-sync to LuckPerms if available
        syncPlayerRankToLuckPerms(playerUuid, rankId, true);
    }

    /**
     * Remove a rank from a player.
     */
    public void removePlayerRank(UUID playerUuid, int rankId) throws SQLException {
        String uuid = playerUuid.toString();
        db.update("DELETE FROM moderex_player_ranks WHERE player_uuid = ? AND rank_id = ?", uuid, rankId);
        playerRankCache.remove(uuid);
        resolvedPermissionCache.remove(uuid);

        // Auto-sync to LuckPerms if available
        syncPlayerRankToLuckPerms(playerUuid, rankId, false);
    }

    // ==================== Permission Resolution ====================

    /**
     * Check if a player has a specific permission via the built-in system.
     */
    public boolean hasPermission(UUID playerUuid, String permission) {
        Map<String, Boolean> effective = getEffectivePermissions(playerUuid);

        // Check exact permission
        Boolean exact = effective.get(permission);
        if (exact != null) return exact;

        // Check wildcard: moderex.*
        Boolean root = effective.get("moderex.*");
        if (root != null && root) return true;

        // Check category wildcards (e.g., moderex.command.* for moderex.command.ban)
        String[] parts = permission.split("\\.");
        StringBuilder wildcardCheck = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) wildcardCheck.append(".");
            wildcardCheck.append(parts[i]);
            Boolean wildcard = effective.get(wildcardCheck + ".*");
            if (wildcard != null && wildcard) return true;
        }

        return false;
    }

    /**
     * Get all effective permissions for a player (resolved with inheritance).
     */
    public Map<String, Boolean> getEffectivePermissions(UUID playerUuid) {
        String uuid = playerUuid.toString();

        Map<String, Boolean> cached = resolvedPermissionCache.get(uuid);
        if (cached != null) return cached;

        Map<String, Boolean> resolved = new LinkedHashMap<>();
        List<Rank> ranks = getPlayerRanks(playerUuid);

        if (ranks.isEmpty()) {
            // Use default rank
            Rank defaultRank = getDefaultRank();
            if (defaultRank != null) {
                resolveRankPermissions(defaultRank, resolved, new HashSet<>());
            }
        } else {
            // Resolve permissions from all ranks (lower weight first, higher overrides)
            for (Rank rank : ranks) {
                resolveRankPermissions(rank, resolved, new HashSet<>());
            }
        }

        resolvedPermissionCache.put(uuid, resolved);
        return resolved;
    }

    /**
     * Recursively resolve permissions for a rank including inheritance.
     */
    private void resolveRankPermissions(Rank rank, Map<String, Boolean> resolved, Set<Integer> visited) {
        if (visited.contains(rank.getId())) return; // Prevent circular inheritance
        visited.add(rank.getId());

        // First resolve parent permissions (so child can override)
        for (int parentId : rank.getParentRankIds()) {
            Rank parent = rankCache.get(parentId);
            if (parent != null) {
                resolveRankPermissions(parent, resolved, visited);
            }
        }

        // Then apply this rank's permissions (overrides parent)
        for (Map.Entry<String, Boolean> entry : rank.getPermissions().entrySet()) {
            resolved.put(entry.getKey(), entry.getValue());
        }
    }

    // ==================== Cache Management ====================

    private void invalidateAllPlayerCaches() {
        playerRankCache.clear();
        resolvedPermissionCache.clear();
    }

    public void invalidatePlayerCache(UUID playerUuid) {
        String uuid = playerUuid.toString();
        playerRankCache.remove(uuid);
        resolvedPermissionCache.remove(uuid);
    }

    // ==================== Database Loading ====================

    private void loadAllRanks() throws SQLException {
        rankCache.clear();

        // Load ranks
        List<Rank> ranks = db.query("SELECT * FROM moderex_ranks ORDER BY weight", rs -> {
            List<Rank> list = new ArrayList<>();
            while (rs.next()) {
                Rank rank = new Rank();
                rank.setId(rs.getInt("id"));
                rank.setName(rs.getString("name"));
                rank.setDisplayName(rs.getString("display_name"));
                rank.setPrefix(rs.getString("prefix"));
                rank.setSuffix(rs.getString("suffix"));
                rank.setWeight(rs.getInt("weight"));
                rank.setDefault(rs.getInt("is_default") == 1);
                rank.setCreatedAt(rs.getLong("created_at"));
                rank.setUpdatedAt(rs.getLong("updated_at"));
                list.add(rank);
            }
            return list;
        });

        for (Rank rank : ranks) {
            rankCache.put(rank.getId(), rank);
        }

        // Load permissions for each rank
        for (Rank rank : ranks) {
            db.query("SELECT permission, granted FROM moderex_rank_permissions WHERE rank_id = ?", rs -> {
                while (rs.next()) {
                    rank.setPermission(rs.getString("permission"), rs.getInt("granted") == 1);
                }
                return null;
            }, rank.getId());
        }

        // Load inheritance
        for (Rank rank : ranks) {
            db.query("SELECT parent_rank_id FROM moderex_rank_inheritance WHERE rank_id = ?", rs -> {
                while (rs.next()) {
                    rank.addParentRank(rs.getInt("parent_rank_id"));
                }
                return null;
            }, rank.getId());
        }
    }

    private List<Integer> loadPlayerRanks(String uuid) throws SQLException {
        return db.query(
                "SELECT rank_id FROM moderex_player_ranks WHERE player_uuid = ? AND (expires_at IS NULL OR expires_at > ?) ORDER BY is_primary DESC",
                rs -> {
                    List<Integer> ids = new ArrayList<>();
                    while (rs.next()) {
                        ids.add(rs.getInt("rank_id"));
                    }
                    return ids;
                }, uuid, System.currentTimeMillis()
        );
    }

    // ==================== Default Ranks ====================

    private void createDefaultRanks() throws SQLException {
        plugin.getLogger().info("[Permissions] Creating default ranks...");

        long now = System.currentTimeMillis();

        // Helper
        createDefaultRank("helper", "Helper", "<#55ff55>[Helper]", null, 10, true, now,
                "moderex.webpanel", "moderex.info.basic", "moderex.history.view");

        // Moderator
        createDefaultRank("moderator", "Moderator", "<#55ffff>[Mod]", null, 20, false, now,
                "moderex.webpanel", "moderex.command.ban", "moderex.command.tempban",
                "moderex.command.mute", "moderex.command.tempmute",
                "moderex.command.warn", "moderex.command.kick",
                "moderex.info.*", "moderex.history.*");

        // Admin
        createDefaultRank("admin", "Admin", "<#ff5555>[Admin]", null, 30, false, now,
                "moderex.webpanel", "moderex.command.*", "moderex.info.*",
                "moderex.history.*", "moderex.admin.*", "moderex.automod.*");

        // Developer
        createDefaultRank("developer", "Developer", "<#aa00ff>[Dev]", null, 40, false, now,
                "moderex.webpanel", "moderex.command.*", "moderex.info.*",
                "moderex.history.*", "moderex.admin.*", "moderex.automod.*",
                "moderex.anticheat.*", "moderex.monitoring.*");

        // Owner
        createDefaultRank("owner", "Owner", "<#ff0000>[Owner]", null, 50, false, now,
                "moderex.*");

        // Set up inheritance: Mod inherits Helper, Admin inherits Mod, Dev inherits Admin, Owner has wildcard
        Rank helper = getRankByName("helper");
        Rank mod = getRankByName("moderator");
        Rank admin = getRankByName("admin");
        Rank dev = getRankByName("developer");

        if (helper != null && mod != null) {
            db.update("INSERT INTO moderex_rank_inheritance (rank_id, parent_rank_id) VALUES (?, ?)", mod.getId(), helper.getId());
        }
        if (mod != null && admin != null) {
            db.update("INSERT INTO moderex_rank_inheritance (rank_id, parent_rank_id) VALUES (?, ?)", admin.getId(), mod.getId());
        }
        if (admin != null && dev != null) {
            db.update("INSERT INTO moderex_rank_inheritance (rank_id, parent_rank_id) VALUES (?, ?)", dev.getId(), admin.getId());
        }
    }

    private void createDefaultRank(String name, String displayName, String prefix, String suffix, int weight, boolean isDefault, long now, String... permissions) throws SQLException {
        db.update(
                "INSERT INTO moderex_ranks (name, display_name, prefix, suffix, weight, is_default, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                name, displayName, prefix, suffix, weight, isDefault ? 1 : 0, now, now
        );

        int id = db.query("SELECT id FROM moderex_ranks WHERE name = ?", rs -> {
            if (rs.next()) return rs.getInt("id");
            return -1;
        }, name);

        if (id > 0) {
            for (String perm : permissions) {
                db.update("INSERT INTO moderex_rank_permissions (rank_id, permission, granted) VALUES (?, ?, 1)", id, perm);
            }
        }
    }

    // ==================== JSON Serialization (for Web Panel) ====================

    /**
     * Convert all ranks to JSON for the web panel.
     */
    public JsonArray ranksToJson() {
        JsonArray arr = new JsonArray();
        for (Rank rank : getAllRanks()) {
            arr.add(rankToJson(rank));
        }
        return arr;
    }

    /**
     * Convert a single rank to JSON.
     */
    public JsonObject rankToJson(Rank rank) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", rank.getId());
        obj.addProperty("name", rank.getName());
        obj.addProperty("displayName", rank.getDisplayName());
        obj.addProperty("prefix", rank.getPrefix() != null ? rank.getPrefix() : "");
        obj.addProperty("suffix", rank.getSuffix() != null ? rank.getSuffix() : "");
        obj.addProperty("weight", rank.getWeight());
        obj.addProperty("isDefault", rank.isDefault());
        obj.addProperty("createdAt", rank.getCreatedAt());
        obj.addProperty("updatedAt", rank.getUpdatedAt());

        // Permissions
        JsonObject perms = new JsonObject();
        for (Map.Entry<String, Boolean> entry : rank.getPermissions().entrySet()) {
            perms.addProperty(entry.getKey(), entry.getValue());
        }
        obj.add("permissions", perms);

        // Parent rank IDs
        JsonArray parents = new JsonArray();
        for (int parentId : rank.getParentRankIds()) {
            parents.add(parentId);
        }
        obj.add("parentRankIds", parents);

        return obj;
    }

    /**
     * Get player ranks as JSON for the web panel.
     */
    public JsonArray playerRanksToJson(UUID playerUuid) {
        JsonArray arr = new JsonArray();
        try {
            String uuid = playerUuid.toString();
            db.query("SELECT pr.*, r.name as rank_name, r.display_name FROM moderex_player_ranks pr " +
                            "JOIN moderex_ranks r ON pr.rank_id = r.id WHERE pr.player_uuid = ? " +
                            "ORDER BY pr.is_primary DESC, r.weight DESC",
                    rs -> {
                        while (rs.next()) {
                            JsonObject obj = new JsonObject();
                            obj.addProperty("id", rs.getInt("id"));
                            obj.addProperty("rankId", rs.getInt("rank_id"));
                            obj.addProperty("rankName", rs.getString("rank_name"));
                            obj.addProperty("displayName", rs.getString("display_name"));
                            obj.addProperty("isPrimary", rs.getInt("is_primary") == 1);
                            obj.addProperty("assignedBy", rs.getString("assigned_by"));
                            obj.addProperty("assignedAt", rs.getLong("assigned_at"));
                            long expiresAt = rs.getLong("expires_at");
                            if (!rs.wasNull()) {
                                obj.addProperty("expiresAt", expiresAt);
                            }
                            arr.add(obj);
                        }
                        return null;
                    }, uuid);
        } catch (SQLException e) {
            plugin.logError("[Permissions] Failed to load player ranks as JSON", e);
        }
        return arr;
    }

    // ==================== LuckPerms Export ====================

    /**
     * Sync a single rank to LuckPerms (auto-sync on permission changes).
     */
    private void syncRankToLuckPerms(int rankId) {
        if (!isLuckPermsAvailable()) return;

        try {
            Rank rank = rankCache.get(rankId);
            if (rank == null) return;

            net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();

            // Create or get group
            net.luckperms.api.model.group.Group group = lp.getGroupManager().getGroup(rank.getName());
            if (group == null) {
                lp.getGroupManager().createAndLoadGroup(rank.getName()).join();
                group = lp.getGroupManager().getGroup(rank.getName());
            }

            if (group != null) {
                // Clear old permissions and add new ones
                group.data().clear(net.luckperms.api.node.NodeType.PERMISSION.predicate());
                for (Map.Entry<String, Boolean> perm : rank.getPermissions().entrySet()) {
                    group.data().add(net.luckperms.api.node.types.PermissionNode.builder(perm.getKey()).value(perm.getValue()).build());
                }

                // Update meta
                group.data().clear(net.luckperms.api.node.NodeType.META.predicate(n -> n.getMetaKey().equals("displayname")));
                group.data().add(net.luckperms.api.node.types.MetaNode.builder("displayname", rank.getDisplayName()).build());

                // Update prefix/suffix
                if (rank.getPrefix() != null && !rank.getPrefix().isEmpty()) {
                    group.data().clear(net.luckperms.api.node.NodeType.PREFIX.predicate());
                    group.data().add(net.luckperms.api.node.types.PrefixNode.builder(rank.getPrefix(), rank.getWeight()).build());
                }
                if (rank.getSuffix() != null && !rank.getSuffix().isEmpty()) {
                    group.data().clear(net.luckperms.api.node.NodeType.SUFFIX.predicate());
                    group.data().add(net.luckperms.api.node.types.SuffixNode.builder(rank.getSuffix(), rank.getWeight()).build());
                }

                lp.getGroupManager().saveGroup(group).join();
                plugin.logDebug("[Permissions] Auto-synced rank '" + rank.getName() + "' to LuckPerms");
            }
        } catch (Exception e) {
            plugin.logError("[Permissions] Failed to auto-sync rank to LuckPerms", e);
        }
    }

    /**
     * Sync a player's rank assignment to LuckPerms (auto-sync on player rank changes).
     */
    private void syncPlayerRankToLuckPerms(UUID playerUuid, int rankId, boolean add) {
        if (!isLuckPermsAvailable()) return;

        try {
            Rank rank = rankCache.get(rankId);
            if (rank == null) return;

            net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = lp.getUserManager().loadUser(playerUuid).join();

            if (user != null) {
                if (add) {
                    // Add the group to the user
                    user.data().add(net.luckperms.api.node.types.InheritanceNode.builder(rank.getName()).build());
                    plugin.logDebug("[Permissions] Auto-added LuckPerms group '" + rank.getName() + "' to " + playerUuid);
                } else {
                    // Remove the group from the user
                    user.data().remove(net.luckperms.api.node.types.InheritanceNode.builder(rank.getName()).build());
                    plugin.logDebug("[Permissions] Auto-removed LuckPerms group '" + rank.getName() + "' from " + playerUuid);
                }
                lp.getUserManager().saveUser(user).join();
            }
        } catch (Exception e) {
            plugin.logError("[Permissions] Failed to auto-sync player rank to LuckPerms", e);
        }
    }

    /**
     * Export all ranks and player assignments to LuckPerms.
     * Returns a summary of what was exported.
     */
    public JsonObject exportToLuckPerms() {
        JsonObject result = new JsonObject();

        try {
            // Check if LuckPerms is available
            if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) {
                result.addProperty("success", false);
                result.addProperty("error", "LuckPerms is not installed");
                return result;
            }

            net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();
            int ranksExported = 0;
            int playersExported = 0;

            for (Rank rank : getAllRanks()) {
                // Create or get group
                net.luckperms.api.model.group.Group group = lp.getGroupManager().getGroup(rank.getName());
                if (group == null) {
                    lp.getGroupManager().createAndLoadGroup(rank.getName()).join();
                    group = lp.getGroupManager().getGroup(rank.getName());
                }

                if (group != null) {
                    // Set display name
                    group.data().clear(net.luckperms.api.node.NodeType.META.predicate(n -> n.getMetaKey().equals("displayname")));
                    group.data().add(net.luckperms.api.node.types.MetaNode.builder("displayname", rank.getDisplayName()).build());

                    // Set prefix
                    if (rank.getPrefix() != null && !rank.getPrefix().isEmpty()) {
                        group.data().clear(net.luckperms.api.node.NodeType.PREFIX.predicate());
                        group.data().add(net.luckperms.api.node.types.PrefixNode.builder(rank.getPrefix(), rank.getWeight()).build());
                    }

                    // Set suffix
                    if (rank.getSuffix() != null && !rank.getSuffix().isEmpty()) {
                        group.data().clear(net.luckperms.api.node.NodeType.SUFFIX.predicate());
                        group.data().add(net.luckperms.api.node.types.SuffixNode.builder(rank.getSuffix(), rank.getWeight()).build());
                    }

                    // Set weight
                    group.data().clear(net.luckperms.api.node.NodeType.META.predicate(n -> n.getMetaKey().equals("weight")));
                    group.data().add(net.luckperms.api.node.types.MetaNode.builder("weight", String.valueOf(rank.getWeight())).build());

                    // Set permissions
                    for (Map.Entry<String, Boolean> perm : rank.getPermissions().entrySet()) {
                        group.data().add(net.luckperms.api.node.types.PermissionNode.builder(perm.getKey()).value(perm.getValue()).build());
                    }

                    // Set inheritance
                    for (int parentId : rank.getParentRankIds()) {
                        Rank parent = rankCache.get(parentId);
                        if (parent != null) {
                            group.data().add(net.luckperms.api.node.types.InheritanceNode.builder(parent.getName()).build());
                        }
                    }

                    lp.getGroupManager().saveGroup(group).join();
                    ranksExported++;
                }
            }

            // Export player rank assignments
            List<String[]> playerAssignments = db.query(
                    "SELECT DISTINCT pr.player_uuid, r.name FROM moderex_player_ranks pr JOIN moderex_ranks r ON pr.rank_id = r.id",
                    rs -> {
                        List<String[]> list = new ArrayList<>();
                        while (rs.next()) {
                            list.add(new String[]{rs.getString("player_uuid"), rs.getString("name")});
                        }
                        return list;
                    }
            );

            for (String[] assignment : playerAssignments) {
                try {
                    UUID uuid = UUID.fromString(assignment[0]);
                    String groupName = assignment[1];

                    net.luckperms.api.model.user.User user = lp.getUserManager().loadUser(uuid).join();
                    if (user != null) {
                        user.data().add(net.luckperms.api.node.types.InheritanceNode.builder(groupName).build());
                        lp.getUserManager().saveUser(user).join();
                        playersExported++;
                    }
                } catch (Exception ignored) {}
            }

            result.addProperty("success", true);
            result.addProperty("ranksExported", ranksExported);
            result.addProperty("playersExported", playersExported);

        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("error", e.getMessage());
            plugin.logError("[Permissions] Failed to export to LuckPerms", e);
        }

        return result;
    }

    /**
     * Check if LuckPerms is available.
     */
    public boolean isLuckPermsAvailable() {
        return plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null;
    }

    /**
     * Search players by name for rank assignment.
     */
    public JsonArray searchPlayers(String query) throws SQLException {
        return db.query(
                "SELECT DISTINCT player_uuid, player_name FROM moderex_players WHERE LOWER(player_name) LIKE ? LIMIT 20",
                rs -> {
                    JsonArray arr = new JsonArray();
                    while (rs.next()) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("uuid", rs.getString("player_uuid"));
                        obj.addProperty("name", rs.getString("player_name"));
                        arr.add(obj);
                    }
                    return arr;
                }, "%" + query.toLowerCase() + "%"
        );
    }
}
