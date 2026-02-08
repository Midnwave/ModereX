package com.blockforge.moderex.commands.moderation.history;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

/**
 * Shows associated accounts and IP addresses of user/IP.
 * Console-only by default (requires moderex.iphistory permission for players).
 */
public class IPHistoryCommand extends BaseCommand {

    public IPHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.iphistory", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("moderex.iphistory")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /iphistory <user|ip>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        if (target.isIp()) {
            sendMessage(sender, MessageKey.IPHISTORY_HEADER, "target", target.getIp());
            sendMessage(sender, "<gray>IP history will be implemented with full tracking system");
        } else if (target.isPlayer()) {
            sendMessage(sender, MessageKey.IPHISTORY_HEADER, "target", target.getDisplayName());
            sendMessage(sender, "<gray>IP history will be implemented with full tracking system");
        } else {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && (sender instanceof ConsoleCommandSender || sender.hasPermission("moderex.iphistory"))) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
