package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class IpBanGui extends BasePunishmentGui {

    public IpBanGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, target, "<dark_red>IP Ban: <red>" + target.getName());
    }

    @Override
    protected PunishmentType getPunishmentType() {
        return PunishmentType.IPBAN;
    }

    @Override
    protected Material getInfoMaterial() {
        return Material.IRON_BARS;
    }

    @Override
    protected String getInfoTitle() {
        return "<dark_red>IP Ban Details";
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

            // Execute IP ban for main target
            String command = String.format("ipban %s %s %s",
                    target.getName(),
                    durationStr,
                    reasonStr);
            plugin.getServer().dispatchCommand(viewer, command);

            // Execute for additional targets (mass IP ban)
            for (UUID additionalUuid : getAdditionalTargets()) {
                OfflinePlayer additional = plugin.getServer().getOfflinePlayer(additionalUuid);
                String additionalCmd = String.format("ipban %s %s %s",
                        additional.getName(),
                        durationStr,
                        reasonStr);
                plugin.getServer().dispatchCommand(viewer, additionalCmd);
            }
        });
    }
}
