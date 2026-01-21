package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * /dupeip <user|ip>
 * Aliases: /alts, /checkalts
 *
 * Shows associated accounts of user/IP.
 */
public class DupeIPCommand extends BaseCommand {

    public DupeIPCommand(ModereX plugin) {
        super(plugin, "moderex.dupeip", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /dupeip <user|ip>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        if (target.isIp()) {
            sendMessage(sender, MessageKey.DUPEIP_HEADER, "target", target.getIp());
            sendMessage(sender, "<gray>Alt account detection will be implemented with full IP tracking system");
        } else if (target.isPlayer()) {
            sendMessage(sender, MessageKey.DUPEIP_HEADER, "target", target.getDisplayName());
            sendMessage(sender, "<gray>Alt account detection will be implemented with full IP tracking system");
        } else {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
