package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StaffHelpCommand extends BaseCommand {

    public StaffHelpCommand(ModereX plugin) {
        super(plugin, "moderex.command.staffhelp", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        plugin.getStaffChatManager().sendHelpRequest(player);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }
}
