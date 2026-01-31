package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class MuteGui extends BasePunishmentGui {

    public MuteGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, target, "<gold>Mute: <yellow>" + target.getName());
    }

    @Override
    protected PunishmentType getPunishmentType() {
        return PunishmentType.MUTE;
    }

    @Override
    protected Material getInfoMaterial() {
        return Material.PAPER;
    }

    @Override
    protected String getInfoTitle() {
        return "<gold>Mute Details";
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

            // Execute mute for main target
            String command = String.format("mute %s %s %s",
                    target.getName(),
                    durationStr,
                    reasonStr);
            plugin.getServer().dispatchCommand(viewer, command);

            // Execute for additional targets (mass mute)
            for (UUID additionalUuid : getAdditionalTargets()) {
                OfflinePlayer additional = plugin.getServer().getOfflinePlayer(additionalUuid);
                String additionalCmd = String.format("mute %s %s %s",
                        additional.getName(),
                        durationStr,
                        reasonStr);
                plugin.getServer().dispatchCommand(viewer, additionalCmd);
            }
        });
    }
}
