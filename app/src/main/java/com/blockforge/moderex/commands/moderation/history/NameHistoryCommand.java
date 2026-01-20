package com.blockforge.moderex.commands.moderation.history;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * /namehistory <user>
 *
 * Display previous usernames for a player.
 */
public class NameHistoryCommand extends BaseCommand {

    public NameHistoryCommand(ModereX plugin) {
        super(plugin, "moderex.namehistory", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /namehistory <user>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid() || !target.isPlayer() || target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        // Name history service currently unavailable
        sendMessage(sender, MessageKey.NAMEHISTORY_NOT_AVAILABLE);
        // Full implementation would:
        // 1. Fetch from database or Mojang API
        // 2. Display previous names with dates of changes
        // Example: "<yellow>1. <gold>OldName <gray>(2020-01-15 to 2021-06-20)"
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
