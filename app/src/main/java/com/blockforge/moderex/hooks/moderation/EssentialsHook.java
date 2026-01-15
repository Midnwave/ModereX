package com.blockforge.moderex.hooks.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EssentialsHook implements ModerationHook {

    private final ModereX plugin;
    private Plugin essentials;
    private boolean available = false;

    public EssentialsHook(ModereX plugin) {
        this.plugin = plugin;
        try {
            this.essentials = Bukkit.getPluginManager().getPlugin("Essentials");
            this.available = essentials != null && essentials.isEnabled();
        } catch (Exception e) {
            plugin.logDebug("Essentials not available: " + e.getMessage());
        }
    }

    @Override
    public String getPluginName() {
        return "Essentials";
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public List<ExternalPunishment> getPunishments(UUID uuid) {
        List<ExternalPunishment> punishments = new ArrayList<>();

        if (!available) return punishments;

        try {
            // Use reflection to get User object
            Method getUserMethod = essentials.getClass().getMethod("getUser", UUID.class);
            Object user = getUserMethod.invoke(essentials, uuid);

            if (user == null) return punishments;

            // Get player name
            Method getNameMethod = user.getClass().getMethod("getName");
            String playerName = (String) getNameMethod.invoke(user);

            // Check if muted
            Method isMutedMethod = user.getClass().getMethod("isMuted");
            boolean isMuted = (boolean) isMutedMethod.invoke(user);

            if (isMuted) {
                Method getMuteTimeoutMethod = user.getClass().getMethod("getMuteTimeout");
                long muteTimeout = (long) getMuteTimeoutMethod.invoke(user);

                Method getMuteReasonMethod = user.getClass().getMethod("getMuteReason");
                String reason = (String) getMuteReasonMethod.invoke(user);

                if (reason == null || reason.isEmpty()) {
                    reason = "No reason specified";
                }

                ExternalPunishment mute = new ExternalPunishment(
                    "Essentials", uuid, playerName, "MUTE", reason, "Unknown",
                    System.currentTimeMillis(), muteTimeout, true, null, null
                );
                punishments.add(mute);
            }

        } catch (Exception e) {
            plugin.logError("Failed to query Essentials data for " + uuid, e);
        }

        return punishments;
    }

    @Override
    public List<ExternalPunishment> getActivePunishments(UUID uuid) {
        // Essentials only exposes active punishments
        return getPunishments(uuid);
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
            plugin.logError("Failed to import punishment from Essentials", e);
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

