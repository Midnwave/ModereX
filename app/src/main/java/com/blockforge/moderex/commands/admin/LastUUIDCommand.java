package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Shows user\'s UUID.
 */
public class LastUUIDCommand extends BaseCommand {

    public LastUUIDCommand(ModereX plugin) {
        super(plugin, "moderex.lastuuid", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /lastuuid <user>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid() || !target.isPlayer() || target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        sendMessage(sender, "<yellow>UUID for <gold>" + target.getDisplayName() + "<yellow>: <aqua>" + target.getUuid().toString());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
