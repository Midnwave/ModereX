package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command to view another player's ender chest.
 * Usage: /echest [player]
 */
public class EchestCommand extends BaseCommand {

    public EchestCommand(ModereX plugin) {
        super(plugin, "moderex.command.echest", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player staff = (Player) sender;
        Player target;

        if (args.length == 0) {
            // View own ender chest
            target = staff;
        } else {
            // View another player's ender chest
            if (!staff.hasPermission("moderex.command.echest.others")) {
                sender.sendMessage(plugin.getLanguageManager().get(MessageKey.NO_PERMISSION));
                return;
            }

            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[0]);
                return;
            }

            // Check if target is exempt (has higher permission)
            if (target.hasPermission("moderex.exempt.echest") && !staff.hasPermission("moderex.exempt.bypass")) {
                sendMessage(sender, "<red>You cannot view this player's ender chest.");
                return;
            }
        }

        // Open the ender chest
        staff.openInventory(target.getEnderChest());

        if (target.equals(staff)) {
            sendMessage(sender, "<green>Opened your ender chest.");
        } else {
            sendMessage(sender, "<green>Viewing ender chest of <white>" + target.getName());
            plugin.logDebug("[Echest] " + staff.getName() + " opened ender chest of " + target.getName());
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("moderex.command.echest.others")) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return List.of();
    }
}
