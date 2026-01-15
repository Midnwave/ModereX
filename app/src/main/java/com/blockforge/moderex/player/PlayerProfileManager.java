package com.blockforge.moderex.player;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfileManager {

    private final ModereX plugin;
    private final Map<UUID, PlayerProfile> profileCache = new ConcurrentHashMap<>();
    private final Set<UUID> knownPlayers = ConcurrentHashMap.newKeySet();

    public PlayerProfileManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        loadAllPlayersFromDatabase();
        loadPlayersFromBukkitCache();
    }

    private void loadAllPlayersFromDatabase() {
        try {
            plugin.getDatabaseManager().query(
                "SELECT uuid, username, ip_address, first_join, last_join, last_server FROM moderex_players",
                rs -> {
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        PlayerProfile profile = new PlayerProfile(
                            uuid,
                            rs.getString("username"),
                            rs.getString("ip_address"),
                            rs.getLong("first_join"),
                            rs.getLong("last_join"),
                            rs.getString("last_server")
                        );
                        profileCache.put(uuid, profile);
                        knownPlayers.add(uuid);
                    }
                    return null;
                }
            );
            plugin.getLogger().info("Loaded " + profileCache.size() + " player profiles from database");
        } catch (SQLException e) {
            plugin.logError("Failed to load player profiles from database", e);
        }
    }

    private void loadPlayersFromBukkitCache() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int newPlayers = 0;
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.hasPlayedBefore() && !knownPlayers.contains(offlinePlayer.getUniqueId())) {
                    // Create a profile for this player
                    PlayerProfile profile = new PlayerProfile(
                        offlinePlayer.getUniqueId(),
                        offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown",
                        null,
                        offlinePlayer.getFirstPlayed(),
                        offlinePlayer.getLastPlayed(),
                        null
                    );

                    // Save to database
                    saveProfile(profile);
                    profileCache.put(offlinePlayer.getUniqueId(), profile);
                    knownPlayers.add(offlinePlayer.getUniqueId());
                    newPlayers++;
                }
            }

            if (newPlayers > 0) {
                plugin.getLogger().info("Imported " + newPlayers + " players from Bukkit cache");
            }
        });
    }

    public void handlePlayerJoin(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : null;
        long now = System.currentTimeMillis();

        PlayerProfile profile = profileCache.get(uuid);
        if (profile == null) {
            // New player
            profile = new PlayerProfile(uuid, name, ip, now, now, null);
            profileCache.put(uuid, profile);
            knownPlayers.add(uuid);
        } else {
            // Returning player - update info
            profile.setUsername(name);
            profile.setIpAddress(ip);
            profile.setLastJoin(now);
        }

        saveProfile(profile);
    }

    private void saveProfile(PlayerProfile profile) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getDatabaseManager().update(
                    "INSERT OR REPLACE INTO moderex_players (uuid, username, ip_address, first_join, last_join, last_server) VALUES (?, ?, ?, ?, ?, ?)",
                    profile.getUuid().toString(),
                    profile.getUsername(),
                    profile.getIpAddress(),
                    profile.getFirstJoin(),
                    profile.getLastJoin(),
                    profile.getLastServer()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to save player profile for " + profile.getUsername(), e);
            }
        });
    }

    public PlayerProfile getProfile(UUID uuid) {
        return profileCache.get(uuid);
    }

    public PlayerProfile getProfileByName(String username) {
        for (PlayerProfile profile : profileCache.values()) {
            if (profile.getUsername().equalsIgnoreCase(username)) {
                return profile;
            }
        }
        return null;
    }

    public Collection<PlayerProfile> getAllProfiles() {
        return Collections.unmodifiableCollection(profileCache.values());
    }

    public Set<UUID> getKnownPlayers() {
        return Collections.unmodifiableSet(knownPlayers);
    }

    public int getPlayerCount() {
        return knownPlayers.size();
    }

    public List<PlayerProfile> searchPlayers(String query) {
        String lowerQuery = query.toLowerCase();
        List<PlayerProfile> results = new ArrayList<>();

        for (PlayerProfile profile : profileCache.values()) {
            if (profile.getUsername().toLowerCase().contains(lowerQuery)) {
                results.add(profile);
            }
        }

        // Sort by relevance (exact match first, then starts with, then contains)
        results.sort((a, b) -> {
            String aName = a.getUsername().toLowerCase();
            String bName = b.getUsername().toLowerCase();

            // Exact match first
            if (aName.equals(lowerQuery) && !bName.equals(lowerQuery)) return -1;
            if (!aName.equals(lowerQuery) && bName.equals(lowerQuery)) return 1;

            // Starts with second
            if (aName.startsWith(lowerQuery) && !bName.startsWith(lowerQuery)) return -1;
            if (!aName.startsWith(lowerQuery) && bName.startsWith(lowerQuery)) return 1;

            // Alphabetical
            return aName.compareTo(bName);
        });

        return results;
    }

    public CompletableFuture<PlayerStats> getPlayerStats(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerStats stats = new PlayerStats();

            try {
                // Get punishment counts
                plugin.getDatabaseManager().query(
                    "SELECT type, COUNT(*) as count FROM moderex_punishments WHERE player_uuid = ? GROUP BY type",
                    rs -> {
                        while (rs.next()) {
                            String type = rs.getString("type");
                            int count = rs.getInt("count");
                            switch (type) {
                                case "BAN" -> stats.bans = count;
                                case "MUTE" -> stats.mutes = count;
                                case "KICK" -> stats.kicks = count;
                            }
                        }
                        return null;
                    },
                    uuid.toString()
                );

                // Get warning count
                plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as count FROM moderex_warnings WHERE player_uuid = ?",
                    rs -> {
                        if (rs.next()) {
                            stats.warnings = rs.getInt("count");
                        }
                        return null;
                    },
                    uuid.toString()
                );

                // Get active warning count
                plugin.getDatabaseManager().query(
                    "SELECT COUNT(*) as count FROM moderex_warnings WHERE player_uuid = ? AND active = TRUE",
                    rs -> {
                        if (rs.next()) {
                            stats.activeWarnings = rs.getInt("count");
                        }
                        return null;
                    },
                    uuid.toString()
                );

            } catch (SQLException e) {
                plugin.logError("Failed to get player stats for " + uuid, e);
            }

            return stats;
        });
    }

    public static class PlayerStats {
        public int bans = 0;
        public int mutes = 0;
        public int kicks = 0;
        public int warnings = 0;
        public int activeWarnings = 0;
    }
}
