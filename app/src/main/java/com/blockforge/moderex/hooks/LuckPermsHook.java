package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LuckPermsHook {

    private final LuckPerms luckPerms;

    public LuckPermsHook() {
        this.luckPerms = LuckPermsProvider.get();
    }

    /**
     * Get the LuckPerms API instance.
     */
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public String getPrefix(Player player) {
        return getPrefix(player.getUniqueId());
    }

    public String getPrefix(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        return prefix != null ? prefix : "";
    }

    public String getSuffix(Player player) {
        return getSuffix(player.getUniqueId());
    }

    public String getSuffix(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String suffix = metaData.getSuffix();
        return suffix != null ? suffix : "";
    }

    public String getPrimaryGroup(Player player) {
        return getPrimaryGroup(player.getUniqueId());
    }

    public String getPrimaryGroup(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }
        return user.getPrimaryGroup();
    }

    public int getGroupWeight(String groupName) {
        var group = luckPerms.getGroupManager().getGroup(groupName);
        if (group != null) {
            var weight = group.getWeight();
            return weight.isPresent() ? weight.getAsInt() : 0;
        }
        return 0;
    }

    public boolean groupExists(String groupName) {
        return luckPerms.getGroupManager().getGroup(groupName) != null;
    }

    public String getMeta(Player player, String key) {
        return getMeta(player.getUniqueId(), key);
    }

    public String getMeta(UUID uuid, String key) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return "";
        }

        CachedMetaData metaData = user.getCachedData().getMetaData();
        String value = metaData.getMetaValue(key);
        return value != null ? value : "";
    }

    public String getFormattedName(Player player) {
        String prefix = getPrefix(player);
        String suffix = getSuffix(player);
        return prefix + player.getName() + suffix;
    }

    public boolean hasPermission(UUID uuid, String permission) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            // Try to load user data
            try {
                user = luckPerms.getUserManager().loadUser(uuid).join();
            } catch (Exception e) {
                return false;
            }
        }

        if (user == null) {
            return false;
        }

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    // ========================================================================
    // Gateway Permission Sync
    // ========================================================================

    /**
     * Register a listener for permission changes that affect web panel access.
     * When a player's permissions change, syncs to the gateway.
     */
    public void registerPermissionChangeListener(ModereX plugin) {
        luckPerms.getEventBus().subscribe(plugin, UserDataRecalculateEvent.class, event -> {
            User user = event.getUser();
            UUID uuid = user.getUniqueId();

            // Run async to avoid blocking LP event processing
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    if (plugin.getGatewayClient() == null || !plugin.getGatewayClient().isConnected()) return;

                    boolean hasWebPanel = user.getCachedData().getPermissionData()
                            .checkPermission("moderex.webpanel").asBoolean();
                    boolean hasStaff = user.getCachedData().getPermissionData()
                            .checkPermission("moderex.staff").asBoolean();
                    boolean hasAccess = hasWebPanel || hasStaff;

                    String username = user.getUsername();
                    String rank = user.getPrimaryGroup();
                    List<String> permissions = getModerexPermissions(user);

                    plugin.getGatewayClient().syncPlayerPermission(uuid, username, rank, permissions, hasAccess);
                    plugin.logDebug("[Gateway] Permission change synced for " + username + " (access: " + hasAccess + ")");
                } catch (Exception e) {
                    plugin.logDebug("[Gateway] Failed to sync permission change: " + e.getMessage());
                }
            });
        });
    }

    /**
     * Get all moderex.* permissions for a LuckPerms user.
     */
    public List<String> getModerexPermissions(User user) {
        CachedPermissionData permData = user.getCachedData().getPermissionData();
        List<String> perms = new ArrayList<>();

        // Check common moderex permissions
        String[] checks = {
            "moderex.webpanel", "moderex.staff", "moderex.*",
            "moderex.command.*", "moderex.bypass.*", "moderex.info.*",
            "moderex.info.ip", "moderex.punish.*", "moderex.punish.ban",
            "moderex.punish.mute", "moderex.punish.kick", "moderex.punish.warn",
            "moderex.replay.*", "moderex.replay.view", "moderex.replay.manage"
        };

        for (String perm : checks) {
            if (permData.checkPermission(perm).asBoolean()) {
                perms.add(perm);
            }
        }
        return perms;
    }

    /**
     * Get all moderex permissions for a UUID (loads user if needed).
     */
    public List<String> getModerexPermissions(UUID uuid) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            try {
                user = luckPerms.getUserManager().loadUser(uuid).join();
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
        if (user == null) return Collections.emptyList();
        return getModerexPermissions(user);
    }

    /**
     * Get all players who have moderex.webpanel or moderex.staff permission.
     * Used for startup sync — loads all unique users from LuckPerms and checks permissions.
     * Runs asynchronously and calls the callback with results.
     */
    public void getAllWebPanelPlayers(ModereX plugin, java.util.function.Consumer<List<Map<String, Object>>> callback) {
        CompletableFuture.runAsync(() -> {
            List<Map<String, Object>> players = new ArrayList<>();

            try {
                UserManager userManager = luckPerms.getUserManager();

                // Get all unique user UUIDs that LuckPerms knows about
                Set<UUID> uniqueUsers = userManager.getUniqueUsers().join();
                plugin.logDebug("[Gateway] Checking " + uniqueUsers.size() + " LuckPerms users for web panel access...");

                for (UUID uuid : uniqueUsers) {
                    try {
                        User user = userManager.loadUser(uuid).join();
                        if (user == null) continue;

                        boolean hasWebPanel = user.getCachedData().getPermissionData()
                                .checkPermission("moderex.webpanel").asBoolean();
                        boolean hasStaff = user.getCachedData().getPermissionData()
                                .checkPermission("moderex.staff").asBoolean();

                        if (hasWebPanel || hasStaff) {
                            Map<String, Object> playerData = new HashMap<>();
                            playerData.put("uuid", uuid.toString());
                            playerData.put("username", user.getUsername());
                            playerData.put("rank", user.getPrimaryGroup());
                            playerData.put("permissions", getModerexPermissions(user));
                            players.add(playerData);
                        }
                    } catch (Exception e) {
                        // Skip individual user failures
                        plugin.logDebug("[Gateway] Failed to load user " + uuid + ": " + e.getMessage());
                    }
                }

                plugin.logDebug("[Gateway] Found " + players.size() + " players with web panel access");
            } catch (Exception e) {
                plugin.logDebug("[Gateway] Failed to query all web panel players: " + e.getMessage());
            }

            // Run callback on main thread
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(players));
        });
    }
}
