package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Command to toggle staff mode.
 */
public class StaffModeCommand extends BaseCommand {

    public StaffModeCommand(ModereX plugin) {
        super(plugin, "moderex.staffmode", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.").color(NamedTextColor.RED));
            return;
        }

        if (!player.hasPermission("moderex.command.staffmode")) {
            player.sendMessage(Component.text("You don't have permission to use this command.").color(NamedTextColor.RED));
            return;
        }

        if (plugin.getStaffModeManager() == null) {
            player.sendMessage(Component.text("Staff mode is not available.").color(NamedTextColor.RED));
            return;
        }

        plugin.getStaffModeManager().toggleStaffMode(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
