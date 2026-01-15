package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class StaffChatCommand extends BaseCommand {

    public StaffChatCommand(ModereX plugin) {
        super(plugin, "moderex.command.staffchat", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            // Toggle staff chat
            plugin.getStaffChatManager().toggleStaffChat(player);
            return;
        }

        String firstArg = args[0].toLowerCase();

        switch (firstArg) {
            case "on" -> plugin.getStaffChatManager().enableStaffChat(player);
            case "off" -> plugin.getStaffChatManager().disableStaffChat(player);
            default -> {
                // Send as message
                String message = joinArgs(args, 0);
                plugin.getStaffChatManager().sendMessage(player, message);
            }
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(Arrays.asList("on", "off"), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
