package com.blockforge.moderex.hooks.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LiteBansHook implements ModerationHook {

    private final ModereX plugin;
    private boolean available = false;

    public LiteBansHook(ModereX plugin) {
        this.plugin = plugin;
        this.available = Bukkit.getPluginManager().getPlugin("LiteBans") != null;
    }

    @Override
    public String getPluginName() {
        return "LiteBans";
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public List<ExternalPunishment> getPunishments(UUID uuid) {
        List<ExternalPunishment> punishments = new ArrayList<>();

        // Get bans
        punishments.addAll(queryPunishments("bans", uuid, "BAN"));

        // Get mutes
        punishments.addAll(queryPunishments("mutes", uuid, "MUTE"));

        // Get warnings
        punishments.addAll(queryPunishments("warnings", uuid, "WARN"));

        // Get kicks
        punishments.addAll(queryPunishments("kicks", uuid, "KICK"));

        return punishments;
    }

    @Override
    public List<ExternalPunishment> getActivePunishments(UUID uuid) {
        List<ExternalPunishment> all = getPunishments(uuid);
        List<ExternalPunishment> active = new ArrayList<>();

        for (ExternalPunishment p : all) {
            if (p.isActive()) {
                active.add(p);
            }
        }

        return active;
    }

    private List<ExternalPunishment> queryPunishments(String table, UUID uuid, String type) {
        List<ExternalPunishment> punishments = new ArrayList<>();

        String query = String.format(
            "SELECT uuid, banned_by_name, reason, time, until, active, ipban, server_origin " +
            "FROM {litebans_%s} WHERE uuid = ?", table
        );

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                    String staff = rs.getString("banned_by_name");
                    String reason = rs.getString("reason");
                    long created = rs.getLong("time");
                    long expires = rs.getLong("until");
                    boolean active = rs.getBoolean("active");
                    String server = rs.getString("server_origin");
                    String ipAddress = null;

                    // Check for IP ban
                    if (table.equals("bans") && rs.getBoolean("ipban")) {
                        type = "IPBAN";
                        // Try to get IP address
                        ipAddress = getPlayerIP(uuid);
                    }

                    ExternalPunishment punishment = new ExternalPunishment(
                        "LiteBans", uuid, playerName, type, reason, staff,
                        created, expires, active, server, ipAddress
                    );

                    punishments.add(punishment);
                }
            }
        } catch (SQLException e) {
            plugin.logError("Failed to query LiteBans " + table, e);
        }

        return punishments;
    }

    @Override
    public boolean importPunishment(ExternalPunishment punishment) {
        try {
            // Generate case ID
            String caseId = plugin.getDatabaseManager().generateCaseId();

            // Map punishment type
            PunishmentType type = mapType(punishment.getType());

            // Save directly to database
            plugin.getDatabaseManager().update("""
                    INSERT INTO moderex_punishments
                    (case_id, player_uuid, player_name, type, reason, staff_uuid, staff_name,
                     created_at, expires_at, active, ip_address, server)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    caseId,
                    punishment.getPlayerUuid().toString(),
                    punishment.getPlayerName(),
                    type.name(),
                    "[Imported from " + punishment.getSource() + "] " + punishment.getReason(),
                    null, // staff UUID not available from external plugin
                    punishment.getStaff() + " (via " + punishment.getSource() + ")",
                    punishment.getCreatedAt(),
                    punishment.getExpiresAt(),
                    punishment.isActive(),
                    punishment.getIpAddress(),
                    punishment.getServerId() != null ? punishment.getServerId() : plugin.getServer().getName()
            );

            plugin.logDebug("Imported punishment " + caseId + " from " + punishment.getSource());
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to import punishment from " + punishment.getSource(), e);
            return false;
        }
    }

    @Override
    public int importAllPunishments(UUID uuid) {
        List<ExternalPunishment> punishments = getPunishments(uuid);
        int imported = 0;

        for (ExternalPunishment punishment : punishments) {
            if (importPunishment(punishment)) {
                imported++;
            }
        }

        return imported;
    }

    private PunishmentType mapType(String type) {
        return switch (type.toUpperCase()) {
            case "BAN" -> PunishmentType.BAN;
            case "MUTE" -> PunishmentType.MUTE;
            case "KICK" -> PunishmentType.KICK;
            case "WARN" -> PunishmentType.WARN;
            case "IPBAN" -> PunishmentType.IPBAN;
            default -> PunishmentType.BAN;
        };
    }

    private String getPlayerIP(UUID uuid) {
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT ip FROM {litebans_history} WHERE uuid = ? ORDER BY date DESC LIMIT 1")) {

            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ip");
                }
            }
        } catch (SQLException e) {
            // Ignore
        }
        return null;
    }
}
