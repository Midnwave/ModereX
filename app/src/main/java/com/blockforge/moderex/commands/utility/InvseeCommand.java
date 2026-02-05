package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command to view another player's inventory.
 * Usage: /invsee <player>
 */
public class InvseeCommand extends BaseCommand {

    public InvseeCommand(ModereX plugin) {
        super(plugin, "moderex.command.invsee", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player staff = (Player) sender;

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /invsee <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, args[0]);
            return;
        }

        if (target.equals(staff)) {
            sendMessage(sender, "<yellow>Use your own inventory key to view your inventory.");
            return;
        }

        // Check if target is exempt (has higher permission)
        if (target.hasPermission("moderex.exempt.invsee") && !staff.hasPermission("moderex.exempt.bypass")) {
            sendMessage(sender, "<red>You cannot view this player's inventory.");
            return;
        }

        // Open the target's inventory to the staff member
        staff.openInventory(target.getInventory());
        sendMessage(sender, "<green>Viewing inventory of <white>" + target.getName());

        plugin.logDebug("[Invsee] " + staff.getName() + " opened inventory of " + target.getName());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return List.of();
    }
}
