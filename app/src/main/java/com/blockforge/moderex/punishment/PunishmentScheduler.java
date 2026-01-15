package com.blockforge.moderex.punishment;

import com.blockforge.moderex.ModereX;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class PunishmentScheduler {

    private final ModereX plugin;
    private BukkitTask task;

    public PunishmentScheduler(ModereX plugin) {
        this.plugin = plugin;
    }

    public void start() {
        // Run every 30 seconds (600 ticks)
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::checkExpiredPunishments, 600L, 600L);
        plugin.getLogger().info("Punishment scheduler started.");
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            plugin.getLogger().info("Punishment scheduler stopped.");
        }
    }

    private void checkExpiredPunishments() {
        try {
            List<Punishment> expired = plugin.getPunishmentManager().getExpiredPunishments();

            for (Punishment punishment : expired) {
                plugin.getPunishmentManager().expirePunishment(punishment);
                plugin.logDebug("Expired punishment: " + punishment.getCaseId() +
                        " (" + punishment.getType() + " for " + punishment.getPlayerName() + ")");
            }

            if (!expired.isEmpty()) {
                plugin.logDebug("Processed " + expired.size() + " expired punishments.");
            }
        } catch (Exception e) {
            plugin.logError("Error checking expired punishments", e);
        }
    }
}
