package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command to toggle flight mode for staff members.
 * Usage: /fly [player]
 */
public class FlyCommand extends BaseCommand {

    public FlyCommand(ModereX plugin) {
        super(plugin, "moderex.command.fly", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target;

        if (args.length > 0) {
            // Toggle for another player
            if (!sender.hasPermission("moderex.command.fly.others")) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
                return;
            }

            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[0]);
                return;
            }
        } else {
            // Toggle for self
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.PLAYER_ONLY));
                return;
            }
            target = (Player) sender;
        }

        // Toggle flight
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);

        if (!newState) {
            // If disabling flight, also stop flying
            target.setFlying(false);
        }

        // Send messages
        if (target.equals(sender)) {
            if (newState) {
                sendMessage(sender, "<green>Flight mode enabled.");
            } else {
                sendMessage(sender, "<yellow>Flight mode disabled.");
            }
        } else {
            if (newState) {
                sendMessage(sender, "<green>Enabled flight for <white>" + target.getName());
                sendMessage(target, "<green>Flight mode enabled by <white>" + sender.getName());
            } else {
                sendMessage(sender, "<yellow>Disabled flight for <white>" + target.getName());
                sendMessage(target, "<yellow>Flight mode disabled by <white>" + sender.getName());
            }
        }

        plugin.logDebug("[Fly] " + sender.getName() + " toggled flight for " + target.getName() + " to " + newState);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("moderex.command.fly.others")) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return List.of();
    }
}
