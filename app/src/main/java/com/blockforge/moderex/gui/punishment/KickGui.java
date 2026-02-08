package com.blockforge.moderex.gui.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class KickGui extends BasePunishmentGui {

    public KickGui(ModereX plugin, OfflinePlayer target) {
        super(plugin, target, "<red>Kick: <white>" + target.getName());
    }

    @Override
    protected PunishmentType getPunishmentType() {
        return PunishmentType.KICK;
    }

    @Override
    protected Material getInfoMaterial() {
        return Material.LEATHER_BOOTS;
    }

    @Override
    protected String getInfoTitle() {
        return "<red>Kick Details";
    }

    @Override
    protected boolean supportsDuration() {
        return false; // Kicks don't have duration
    }

    @Override
    protected void executePunishment() {
        if (!target.isOnline()) {
            Msg.send(viewer, TextUtil.parse(
                    "<red>Player " + target.getName() + " is not online!"));
            close();
            return;
        }

        close();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String reasonStr = getReason();

            // Execute kick for main target
            String command = String.format("kick %s %s", target.getName(), reasonStr);
            plugin.getServer().dispatchCommand(viewer, command);

            // Execute for additional targets (mass kick)
            for (UUID additionalUuid : getAdditionalTargets()) {
                OfflinePlayer additional = plugin.getServer().getOfflinePlayer(additionalUuid);
                if (additional.isOnline()) {
                    String additionalCmd = String.format("kick %s %s", additional.getName(), reasonStr);
                    plugin.getServer().dispatchCommand(viewer, additionalCmd);
                }
            }
        });
    }

    @Override
    protected boolean canExecute() {
        // Kicks don't require duration, always executable
        return true;
    }
}
