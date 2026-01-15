package com.blockforge.moderex.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TextUtil;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PunishmentManager {

    private final ModereX plugin;

    // Cache for active punishments (UUID -> List of active punishments)
    private final Map<UUID, List<Punishment>> punishmentCache = new ConcurrentHashMap<>();
    private final Map<String, List<Punishment>> ipBanCache = new ConcurrentHashMap<>();

    public PunishmentManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Punishment> mute(UUID playerUuid, String playerName, UUID staffUuid,
                                               String staffName, long duration, String reason) {
        return createPunishment(playerUuid, playerName, PunishmentType.MUTE, staffUuid, staffName, duration, reason, null);
    }

    public CompletableFuture<Punishment> ban(UUID playerUuid, String playerName, UUID staffUuid,
                                              String staffName, long duration, String reason) {
        return createPunishment(playerUuid, playerName, PunishmentType.BAN, staffUuid, staffName, duration, reason, null)
                .thenApply(punishment -> {
                    // Kick player if online
                    Player player = Bukkit.getPlayer(playerUuid);
                    if (player != null) {
                        Component kickMessage = buildDisconnectMessage(punishment);
                        Bukkit.getScheduler().runTask(plugin, () -> player.kick(kickMessage));
                    }
                    return punishment;
                });
    }

    public CompletableFuture<Punishment> ipBan(UUID playerUuid, String playerName, String ipAddress,
                                                UUID staffUuid, String staffName, long duration, String reason) {
        return createPunishment(playerUuid, playerName, PunishmentType.IPBAN, staffUuid, staffName, duration, reason, ipAddress)
                .thenApply(punishment -> {
                    // Kick player if online
                    Player player = Bukkit.getPlayer(playerUuid);
                    if (player != null) {
                        Component kickMessage = buildDisconnectMessage(punishment);
                        Bukkit.getScheduler().runTask(plugin, () -> player.kick(kickMessage));
                    }
                    return punishment;
                });
    }

    public CompletableFuture<Punishment> kick(UUID playerUuid, String playerName, UUID staffUuid,
                                               String staffName, String reason) {
        return createPunishment(playerUuid, playerName, PunishmentType.KICK, staffUuid, staffName, 0, reason, null)
                .thenApply(punishment -> {
                    // Kick player if online
                    Player player = Bukkit.getPlayer(playerUuid);
                    if (player != null) {
                        Component kickMessage = buildKickMessage(punishment);
                        Bukkit.getScheduler().runTask(plugin, () -> player.kick(kickMessage));
                    }
                    return punishment;
                });
    }

    public CompletableFuture<Punishment> warn(UUID playerUuid, String playerName, UUID staffUuid,
                                               String staffName, long duration, String reason) {
        return createPunishment(playerUuid, playerName, PunishmentType.WARN, staffUuid, staffName, duration, reason, null)
                .thenApply(punishment -> {
                    // Notify player if online
                    Player player = Bukkit.getPlayer(playerUuid);
                    if (player != null) {
                        Component message = plugin.getLanguageManager().getPrefixed(MessageKey.WARN_MESSAGE,
                                "reason", reason != null ? reason : "No reason specified");
                        Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(message));
                    }
                    return punishment;
                });
    }

    private CompletableFuture<Punishment> createPunishment(UUID playerUuid, String playerName,
                                                            PunishmentType type, UUID staffUuid, String staffName,
                                                            long duration, String reason, String ipAddress) {
        return CompletableFuture.supplyAsync(() -> {
            String caseId = plugin.getDatabaseManager().generateCaseId();
            long expiresAt = duration == -1 ? -1 : (duration == 0 ? 0 : System.currentTimeMillis() + duration);

            Punishment punishment = Punishment.builder()
                    .caseId(caseId)
                    .playerUuid(playerUuid)
                    .playerName(playerName)
                    .type(type)
                    .reason(reason != null ? reason : "No reason specified")
                    .staffUuid(staffUuid)
                    .staffName(staffName)
                    .createdAt(System.currentTimeMillis())
                    .expiresAt(expiresAt)
                    .active(type != PunishmentType.KICK) // Kicks are instant, not "active"
                    .ipAddress(ipAddress)
                    .build();

            // Save to database
            try {
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_punishments
                        (case_id, player_uuid, player_name, type, reason, staff_uuid, staff_name,
                         created_at, expires_at, active, ip_address, server)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        punishment.getCaseId(),
                        playerUuid.toString(),
                        playerName,
                        type.name(),
                        punishment.getReason(),
                        staffUuid != null ? staffUuid.toString() : null,
                        staffName,
                        punishment.getCreatedAt(),
                        punishment.getExpiresAt(),
                        punishment.isActive(),
                        ipAddress,
                        plugin.getServer().getName()
                );

                // Update cache
                updateCache(punishment);

                // Broadcast to staff
                broadcastPunishment(punishment);

                // Notify watchlist
                plugin.getWatchlistManager().onPunishment(punishment);

            } catch (SQLException e) {
                plugin.logError("Failed to save punishment", e);
            }

            return punishment;
        });
    }

    public CompletableFuture<Boolean> removePunishment(UUID playerUuid, PunishmentType type,
                                                        UUID staffUuid, String staffName, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int rows = plugin.getDatabaseManager().update("""
                        UPDATE moderex_punishments
                        SET active = FALSE, removed_by_uuid = ?, removed_by_name = ?,
                            removed_at = ?, removed_reason = ?
                        WHERE player_uuid = ? AND type = ? AND active = TRUE
                        """,
                        staffUuid != null ? staffUuid.toString() : null,
                        staffName,
                        System.currentTimeMillis(),
                        reason,
                        playerUuid.toString(),
                        type.name()
                );

                if (rows > 0) {
                    // Clear from cache
                    punishmentCache.remove(playerUuid);

                    // Broadcast
                    MessageKey broadcastKey = switch (type) {
                        case MUTE -> MessageKey.UNMUTE_BROADCAST;
                        case BAN, IPBAN -> MessageKey.UNBAN_BROADCAST;
                        default -> null;
                    };

                    if (broadcastKey != null) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
                        broadcastToStaff(plugin.getLanguageManager().get(broadcastKey,
                                "staff", staffName,
                                "player", player.getName() != null ? player.getName() : playerUuid.toString()));
                    }

                    return true;
                }
            } catch (SQLException e) {
                plugin.logError("Failed to remove punishment", e);
            }
            return false;
        });
    }

    public boolean isBanned(UUID playerUuid) {
        return getActivePunishment(playerUuid, PunishmentType.BAN) != null;
    }

    public boolean isMuted(UUID playerUuid) {
        return getActivePunishment(playerUuid, PunishmentType.MUTE) != null;
    }

    public boolean hasActivePunishment(UUID playerUuid) {
        // Check cache first
        List<Punishment> cached = punishmentCache.get(playerUuid);
        if (cached != null) {
            for (Punishment p : cached) {
                if (p.isActive() && !p.isExpired() && p.getType() != PunishmentType.KICK) {
                    return true;
                }
            }
        }

        // Check database
        try {
            return plugin.getDatabaseManager().query("""
                    SELECT 1 FROM moderex_punishments
                    WHERE player_uuid = ? AND active = TRUE AND type != 'KICK'
                    AND (expires_at = -1 OR expires_at > ?)
                    LIMIT 1
                    """,
                    rs -> rs.next(),
                    playerUuid.toString(), System.currentTimeMillis()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to check active punishment", e);
            return false;
        }
    }

    public boolean isIpBanned(String ipAddress) {
        List<Punishment> cached = ipBanCache.get(ipAddress);
        if (cached != null) {
            for (Punishment p : cached) {
                if (p.isActive() && !p.isExpired()) {
                    return true;
                }
            }
        }

        // Check database
        try {
            return plugin.getDatabaseManager().query("""
                    SELECT 1 FROM moderex_punishments
                    WHERE ip_address = ? AND type = 'IPBAN' AND active = TRUE
                    AND (expires_at = -1 OR expires_at > ?)
                    LIMIT 1
                    """,
                    rs -> rs.next(),
                    ipAddress, System.currentTimeMillis()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to check IP ban", e);
            return false;
        }
    }

    public Punishment getActivePunishment(UUID playerUuid, PunishmentType type) {
        // Check cache first
        List<Punishment> cached = punishmentCache.get(playerUuid);
        if (cached != null) {
            for (Punishment p : cached) {
                if (p.getType() == type && p.isActive() && !p.isExpired()) {
                    return p;
                }
            }
        }

        // Check database
        try {
            return plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_punishments
                    WHERE player_uuid = ? AND type = ? AND active = TRUE
                    AND (expires_at = -1 OR expires_at > ?)
                    ORDER BY created_at DESC LIMIT 1
                    """,
                    rs -> {
                        if (rs.next()) {
                            return mapPunishment(rs);
                        }
                        return null;
                    },
                    playerUuid.toString(), type.name(), System.currentTimeMillis()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to get active punishment", e);
            return null;
        }
    }

    public CompletableFuture<List<Punishment>> getPunishments(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                        SELECT * FROM moderex_punishments
                        WHERE player_uuid = ?
                        ORDER BY created_at DESC
                        """,
                        rs -> {
                            List<Punishment> punishments = new ArrayList<>();
                            while (rs.next()) {
                                punishments.add(mapPunishment(rs));
                            }
                            return punishments;
                        },
                        playerUuid.toString()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to get punishments", e);
                return Collections.emptyList();
            }
        });
    }

    public CompletableFuture<List<Punishment>> getPunishments(UUID playerUuid, PunishmentType type) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                        SELECT * FROM moderex_punishments
                        WHERE player_uuid = ? AND type = ?
                        ORDER BY created_at DESC
                        """,
                        rs -> {
                            List<Punishment> punishments = new ArrayList<>();
                            while (rs.next()) {
                                punishments.add(mapPunishment(rs));
                            }
                            return punishments;
                        },
                        playerUuid.toString(), type.name()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to get punishments by type", e);
                return Collections.emptyList();
            }
        });
    }

    public CompletableFuture<Punishment> getPunishmentByCaseId(String caseId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                        SELECT * FROM moderex_punishments WHERE case_id = ?
                        """,
                        rs -> rs.next() ? mapPunishment(rs) : null,
                        caseId
                );
            } catch (SQLException e) {
                plugin.logError("Failed to get punishment by case ID", e);
                return null;
            }
        });
    }

    public List<Punishment> getExpiredPunishments() {
        try {
            return plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_punishments
                    WHERE active = TRUE AND expires_at != -1 AND expires_at <= ?
                    """,
                    rs -> {
                        List<Punishment> punishments = new ArrayList<>();
                        while (rs.next()) {
                            punishments.add(mapPunishment(rs));
                        }
                        return punishments;
                    },
                    System.currentTimeMillis()
            );
        } catch (SQLException e) {
            plugin.logError("Failed to get expired punishments", e);
            return Collections.emptyList();
        }
    }

    public CompletableFuture<List<Punishment>> getRecentPunishments(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                        SELECT * FROM moderex_punishments
                        ORDER BY created_at DESC
                        LIMIT ?
                        """,
                        rs -> {
                            List<Punishment> punishments = new ArrayList<>();
                            while (rs.next()) {
                                punishments.add(mapPunishment(rs));
                            }
                            return punishments;
                        },
                        limit
                );
            } catch (SQLException e) {
                plugin.logError("Failed to get recent punishments", e);
                return Collections.emptyList();
            }
        });
    }

    public void expirePunishment(Punishment punishment) {
        try {
            plugin.getDatabaseManager().update("""
                    UPDATE moderex_punishments SET active = FALSE WHERE id = ?
                    """,
                    punishment.getId()
            );
            punishmentCache.remove(punishment.getPlayerUuid());
        } catch (SQLException e) {
            plugin.logError("Failed to expire punishment", e);
        }
    }

    public void loadPlayerPunishments(UUID playerUuid) {
        CompletableFuture.runAsync(() -> {
            try {
                List<Punishment> punishments = plugin.getDatabaseManager().query("""
                        SELECT * FROM moderex_punishments
                        WHERE player_uuid = ? AND active = TRUE
                        """,
                        rs -> {
                            List<Punishment> list = new ArrayList<>();
                            while (rs.next()) {
                                list.add(mapPunishment(rs));
                            }
                            return list;
                        },
                        playerUuid.toString()
                );
                punishmentCache.put(playerUuid, punishments);
            } catch (SQLException e) {
                plugin.logError("Failed to load player punishments", e);
            }
        });
    }

    public void unloadPlayerPunishments(UUID playerUuid) {
        punishmentCache.remove(playerUuid);
    }

    private void updateCache(Punishment punishment) {
        punishmentCache.computeIfAbsent(punishment.getPlayerUuid(), k -> new ArrayList<>())
                .add(punishment);

        if (punishment.getIpAddress() != null) {
            ipBanCache.computeIfAbsent(punishment.getIpAddress(), k -> new ArrayList<>())
                    .add(punishment);
        }
    }

    private Punishment mapPunishment(ResultSet rs) throws SQLException {
        Punishment p = new Punishment();
        p.setId(rs.getInt("id"));
        p.setCaseId(rs.getString("case_id"));
        p.setPlayerUuid(UUID.fromString(rs.getString("player_uuid")));
        p.setPlayerName(rs.getString("player_name"));
        p.setType(PunishmentType.valueOf(rs.getString("type")));
        p.setReason(rs.getString("reason"));
        String staffUuid = rs.getString("staff_uuid");
        if (staffUuid != null) {
            p.setStaffUuid(UUID.fromString(staffUuid));
        }
        p.setStaffName(rs.getString("staff_name"));
        p.setCreatedAt(rs.getLong("created_at"));
        p.setExpiresAt(rs.getLong("expires_at"));
        p.setActive(rs.getBoolean("active"));
        String removedByUuid = rs.getString("removed_by_uuid");
        if (removedByUuid != null) {
            p.setRemovedByUuid(UUID.fromString(removedByUuid));
        }
        p.setRemovedByName(rs.getString("removed_by_name"));
        p.setRemovedAt(rs.getLong("removed_at"));
        p.setRemovedReason(rs.getString("removed_reason"));
        p.setIpAddress(rs.getString("ip_address"));
        p.setServer(rs.getString("server"));
        return p;
    }

    private void broadcastPunishment(Punishment punishment) {
        MessageKey key = switch (punishment.getType()) {
            case BAN -> MessageKey.BAN_BROADCAST;
            case MUTE -> MessageKey.MUTE_BROADCAST;
            case KICK -> MessageKey.KICK_BROADCAST;
            case WARN -> MessageKey.WARN_BROADCAST;
            case IPBAN -> MessageKey.IPBAN_BROADCAST;
        };

        Component message = plugin.getLanguageManager().get(key,
                "staff", punishment.getStaffName(),
                "player", punishment.getPlayerName(),
                "duration", DurationParser.format(punishment.getExpiresAt() == -1 ? -1 :
                        punishment.getExpiresAt() - punishment.getCreatedAt()),
                "reason", punishment.getReason()
        );

        broadcastToStaff(message);

        // Also broadcast to web panel clients
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastPunishment(punishment);
        }
    }

    private void broadcastToStaff(Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("moderex.notify.punishments")) {
                player.sendMessage(message);
            }
        }
        // Also log to console
        plugin.getLogger().info(TextUtil.toPlainText(message));
    }

    public Component buildDisconnectMessage(Punishment punishment) {
        String template = plugin.getLanguageManager().getRaw(
                punishment.getType() == PunishmentType.IPBAN ? MessageKey.IPBAN_DISCONNECT : MessageKey.BAN_DISCONNECT
        );

        String prefix = plugin.getLanguageManager().getRaw(MessageKey.PREFIX);
        String duration = punishment.isPermanent() ? "Permanent" :
                DurationParser.formatRemaining(punishment.getExpiresAt());

        template = template
                .replace("<prefix>", prefix)
                .replace("<date>", TimeUtil.formatFull(punishment.getCreatedAt()))
                .replace("<staff>", punishment.getStaffName())
                .replace("<duration>", duration)
                .replace("<reason>", punishment.getReason());

        // Center each line
        String[] lines = template.split("\n");
        StringBuilder centered = new StringBuilder();
        for (String line : lines) {
            centered.append(TextUtil.centerMessage(line)).append("\n");
        }

        return TextUtil.parse(centered.toString().trim());
    }

    public Component buildKickMessage(Punishment punishment) {
        String template = plugin.getLanguageManager().getRaw(MessageKey.KICK_DISCONNECT);
        String prefix = plugin.getLanguageManager().getRaw(MessageKey.PREFIX);

        template = template
                .replace("<prefix>", prefix)
                .replace("<staff>", punishment.getStaffName())
                .replace("<reason>", punishment.getReason());

        // Center each line
        String[] lines = template.split("\n");
        StringBuilder centered = new StringBuilder();
        for (String line : lines) {
            centered.append(TextUtil.centerMessage(line)).append("\n");
        }

        return TextUtil.parse(centered.toString().trim());
    }
}
