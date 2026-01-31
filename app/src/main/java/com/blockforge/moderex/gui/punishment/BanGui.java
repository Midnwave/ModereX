package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BanGui extends BasePunishmentGui {

    public BanGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, target, "<dark_red>Ban: <red>" + target.getName());
    }

    @Override
    protected PunishmentType getPunishmentType() {
        return PunishmentType.BAN;
    }

    @Override
    protected Material getInfoMaterial() {
        return Material.BARRIER;
    }

    @Override
    protected String getInfoTitle() {
        return "<dark_red>Ban Details";
    }

    @Override
    protected boolean supportsDuration() {
        return true;
    }

    @Override
    protected void executePunishment() {
        close();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String durationStr = getDurationForCommand();
            String reasonStr = getReason();

            // Execute ban for main target
            String command = String.format("ban %s %s %s",
                    target.getName(),
                    durationStr,
                    reasonStr);
            plugin.getServer().dispatchCommand(viewer, command);

            // Execute for additional targets (mass ban)
            for (UUID additionalUuid : getAdditionalTargets()) {
                OfflinePlayer additional = plugin.getServer().getOfflinePlayer(additionalUuid);
                String additionalCmd = String.format("ban %s %s %s",
                        additional.getName(),
                        durationStr,
                        reasonStr);
                plugin.getServer().dispatchCommand(viewer, additionalCmd);
            }
        });
    }
}
