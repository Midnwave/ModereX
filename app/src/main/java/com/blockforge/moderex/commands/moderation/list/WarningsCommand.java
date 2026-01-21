package com.blockforge.moderex.commands.moderation.list;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Shows active warnings for a player. If no user specified, shows your own warnings.
 */
public class WarningsCommand extends BaseCommand {

    public WarningsCommand(ModereX plugin) {
        super(plugin, "moderex.warnings", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        TargetResolver target;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sendMessage(sender, "<red>Console must specify a player: /warnings <player>");
                return;
            }
            target = new TargetResolver(player.getName());
        } else {
            target = new TargetResolver(args[0]);
        }

        if (!target.isValid() || !target.isPlayer() || target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args.length > 0 ? args[0] : "self");
            return;
        }

        boolean isSelf = sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUuid());

        plugin.getPunishmentManager().getActiveWarnings(target.getUuid()).thenAccept(warnings -> {
            if (warnings.isEmpty()) {
                sendMessage(sender, MessageKey.WARNINGS_HEADER, "player", target.getDisplayName());
                if (isSelf) {
                    sendMessage(sender, MessageKey.WARNINGS_SELF_EMPTY);
                } else {
                    sendMessage(sender, MessageKey.WARNINGS_EMPTY, "player", target.getDisplayName());
                }
                return;
            }

            sendMessage(sender, MessageKey.WARNINGS_HEADER,
                    "player", target.getDisplayName());
            sendMessage(sender, MessageKey.WARNINGS_COUNT,
                    "count", String.valueOf(warnings.size()),
                    "player", target.getDisplayName());

            for (Punishment warning : warnings) {
                String expiry = warning.isPermanent() ? "Never" :
                        DurationParser.formatRemaining(warning.getExpiresAt());
                String date = TimeUtil.formatFull(warning.getCreatedAt());

                sendMessage(sender, MessageKey.WARNINGS_ENTRY,
                        "id", String.valueOf(warning.getId()),
                        "reason", warning.getReason(),
                        "date", date,
                        "expiry", expiry,
                        "staff", warning.getStaffName());
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
