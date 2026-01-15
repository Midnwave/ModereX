package com.blockforge.moderex.hooks.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CMIHook implements ModerationHook {

    private final ModereX plugin;
    private boolean available = false;

    public CMIHook(ModereX plugin) {
        this.plugin = plugin;
        this.available = Bukkit.getPluginManager().getPlugin("CMI") != null;
    }

    @Override
    public String getPluginName() {
        return "CMI";
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public List<ExternalPunishment> getPunishments(UUID uuid) {
        // CMI has a complex API - would require reflection and is highly version-dependent
        // For now, return empty list - can be expanded if needed
        plugin.logDebug("CMI integration: Full punishment history not yet implemented");
        return new ArrayList<>();
    }

    @Override
    public List<ExternalPunishment> getActivePunishments(UUID uuid) {
        return new ArrayList<>();
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
            plugin.logError("Failed to import punishment from CMI", e);
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
}
