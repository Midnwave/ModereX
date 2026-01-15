package com.blockforge.moderex.hooks.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdvancedBanHook implements ModerationHook {

    private final ModereX plugin;
    private boolean available = false;

    public AdvancedBanHook(ModereX plugin) {
        this.plugin = plugin;
        this.available = Bukkit.getPluginManager().getPlugin("AdvancedBan") != null;
    }

    @Override
    public String getPluginName() {
        return "AdvancedBan";
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public List<ExternalPunishment> getPunishments(UUID uuid) {
        List<ExternalPunishment> punishments = new ArrayList<>();

        String query = """
            SELECT punishment_type, name, reason, operator, start, end, calculation
            FROM Punishments WHERE uuid = ?
            """;

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String punType = rs.getString("punishment_type");
                    String playerName = rs.getString("name");
                    String reason = rs.getString("reason");
                    String staff = rs.getString("operator");
                    long created = rs.getLong("start");
                    long expires = rs.getLong("end");
                    String calculation = rs.getString("calculation");

                    // Determine if active
                    boolean active = expires == -1 || System.currentTimeMillis() < expires;

                    // Map punishment type
                    String type = mapAdvancedBanType(punType);

                    ExternalPunishment punishment = new ExternalPunishment(
                        "AdvancedBan", uuid, playerName, type, reason, staff,
                        created, expires, active, null, null
                    );

                    punishments.add(punishment);
                }
            }
        } catch (SQLException e) {
            plugin.logError("Failed to query AdvancedBan punishments", e);
        }

        return punishments;
    }

    @Override
    public List<ExternalPunishment> getActivePunishments(UUID uuid) {
        List<ExternalPunishment> all = getPunishments(uuid);
        return all.stream().filter(ExternalPunishment::isActive).toList();
    }

    @Override
    public boolean importPunishment(ExternalPunishment punishment) {
        try {
            String caseId = plugin.getDatabaseManager().generateCaseId();
            PunishmentType type = mapType(punishment.getType());

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
                    null,
                    punishment.getStaff() + " (via " + punishment.getSource() + ")",
                    punishment.getCreatedAt(),
                    punishment.getExpiresAt(),
                    punishment.isActive(),
                    punishment.getIpAddress(),
                    plugin.getServer().getName()
            );

            return true;
        } catch (Exception e) {
            plugin.logError("Failed to import punishment from AdvancedBan", e);
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

    private String mapAdvancedBanType(String abType) {
        return switch (abType.toUpperCase()) {
            case "BAN" -> "BAN";
            case "TEMP_BAN" -> "BAN";
            case "IP_BAN" -> "IPBAN";
            case "TEMP_IP_BAN" -> "IPBAN";
            case "MUTE" -> "MUTE";
            case "TEMP_MUTE" -> "MUTE";
            case "WARNING" -> "WARN";
            case "KICK" -> "KICK";
            default -> "BAN";
        };
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
}
