package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class WarnGui extends BasePunishmentGui {

    public WarnGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, target, "<yellow>Warn: <white>" + target.getName());
    }

    @Override
    protected PunishmentType getPunishmentType() {
        return PunishmentType.WARN;
    }

    @Override
    protected Material getInfoMaterial() {
        return Material.BOOK;
    }

    @Override
    protected String getInfoTitle() {
        return "<yellow>Warning Details";
    }

    @Override
    protected boolean supportsDuration() {
        return true; // Warnings can have expiry durations
    }

    @Override
    protected void executePunishment() {
        close();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String durationStr = getDurationForCommand();
            String reasonStr = getReason();

            // Execute warn for main target
            String command;
            if (durationStr.isEmpty()) {
                command = String.format("warn %s %s", target.getName(), reasonStr);
            } else {
                command = String.format("warn %s %s %s", target.getName(), durationStr, reasonStr);
            }
            plugin.getServer().dispatchCommand(viewer, command);

            // Execute for additional targets (mass warn)
            for (UUID additionalUuid : getAdditionalTargets()) {
                OfflinePlayer additional = plugin.getServer().getOfflinePlayer(additionalUuid);
                String additionalCmd;
                if (durationStr.isEmpty()) {
                    additionalCmd = String.format("warn %s %s", additional.getName(), reasonStr);
                } else {
                    additionalCmd = String.format("warn %s %s %s", additional.getName(), durationStr, reasonStr);
                }
                plugin.getServer().dispatchCommand(viewer, additionalCmd);
            }
        });
    }

    @Override
    protected boolean canExecute() {
        // Warnings don't require duration
        return true;
    }
}
