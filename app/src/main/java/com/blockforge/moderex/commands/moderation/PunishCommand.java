package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.punishment.PunishPlayerGui;
import com.blockforge.moderex.gui.punishment.PunishSelectGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PunishCommand extends BaseCommand {

    public PunishCommand(ModereX plugin) {
        super(plugin, "moderex.command.punish", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getGuiManager().open(player, new PunishSelectGui(plugin));
        } else {
            String targetName = args[0];

            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                plugin.getGuiManager().open(player, new PunishPlayerGui(plugin, target));
                return;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);
            if (offlineTarget.hasPlayedBefore() || offlineTarget.isOnline()) {
                plugin.getGuiManager().open(player, new PunishPlayerGui(plugin, offlineTarget));
            } else {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            }
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        // no tab completion for privacy
        return Collections.emptyList();
    }
}
