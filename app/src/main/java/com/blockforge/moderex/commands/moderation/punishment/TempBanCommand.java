package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.moderation.base.PunishmentCommandBase;
import com.blockforge.moderex.commands.moderation.base.PunishmentContext;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Temporarily bans a player with required duration.
 */
public class TempBanCommand extends PunishmentCommandBase {

    public TempBanCommand(ModereX plugin) {
        super(plugin, "moderex.tempban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.size() < 2) {
            sendMessage(sender, "<red>Usage: /tempban <player> <duration> [reason] [flags]");
            return;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            if (flagParser.getSender() != null || flagParser.getSenderUuid() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
        }

        if (!checkFlagPermissions(sender, flagParser)) {
            return;
        }

        TargetResolver target = new TargetResolver(regularArgs.get(0));

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
            return;
        }

        if (target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", target.getDisplayName());
            return;
        }

        if (!DurationParser.isValidDuration(regularArgs.get(1))) {
            sendMessage(sender, "<red>Invalid duration format. Example: 1d, 7d, 30d");
            return;
        }

        long duration = DurationParser.parse(regularArgs.get(1));

        if (duration == -1) {
            sendMessage(sender, "<red>Temporary bans cannot be permanent. Use /ban instead.");
            return;
        }

        String reason = regularArgs.size() >= 3 ? flagParser.joinArgs(2) : "No reason specified";

        if (!flagParser.isModify() && !flagParser.isDelete()) {
            if (plugin.getPunishmentManager().isBanned(target.getUuid())) {
                sendMessage(sender, MessageKey.BAN_ALREADY_BANNED, "player", target.getDisplayName());
                return;
            }
        }

        PunishmentContext context = PunishmentContext.builder(sender)
                .target(target)
                .flags(flagParser)
                .duration(duration)
                .reason(reason)
                .build();

        if (flagParser.isDelete()) {
            sendMessage(context.getSender(), "<yellow>Delete functionality will be implemented soon.");
            return;
        }

        if (flagParser.isModify()) {
            sendMessage(context.getSender(), "<yellow>Modify functionality will be implemented soon.");
            return;
        }

        executeBan(context);
    }

    private void executeBan(PunishmentContext context) {
        TargetResolver target = context.getTarget();
        long duration = context.getDuration();
        String reason = context.getReason();

        if (context.isIpBased()) {
            sendMessage(context.getSender(), "<yellow>IP ban functionality will be implemented with IP tracking.");
            return;
        }

        plugin.getPunishmentManager().ban(
                target.getUuid(),
                target.getDisplayName(),
                context.getExecutorUuid(),
                context.getExecutorName(),
                duration,
                reason
        ).thenAccept(punishment -> {
            String durationStr = DurationParser.format(duration);
            sendMessage(context.getSender(), MessageKey.BAN_SUCCESS,
                    "player", target.getDisplayName(),
                    "duration", durationStr);

            broadcastPunishment(context, target.getDisplayName(), durationStr, reason);
        });
    }

    private void broadcastPunishment(PunishmentContext context, String playerName, String duration, String reason) {
        if (context.isHidden()) {
            return;
        }

        if (context.isExtraSilent()) {
            plugin.getLogger().info(String.format("%s banned %s for %s. Reason: %s",
                    context.getExecutorName(), playerName, duration, reason));
            return;
        }

        if (context.isSilent()) {
            return;
        }

        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component message = plugin.getLanguageManager().get(MessageKey.BAN_BROADCAST,
                    "staff", context.getExecutorName(),
                    "player", playerName,
                    "duration", duration,
                    "reason", reason);

            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.notify.punishments")) {
                    player.sendMessage(message);
                }
            }
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();
        int regularArgIndex = regularArgs.size();

        String currentArg = args.length > 0 ? args[args.length - 1] : "";
        if (currentArg.startsWith("-")) {
            return filterCompletions(Arrays.asList(
                "-d", "-g", "-I", "-m", "-N", "-p", "-s", "-S",
                "--delete", "--modify", "--sender", "--sender-uuid",
                "--server-origin", "--confirm", "--hide", "--skip", "--no-queue"
            ), currentArg);
        }

        if (regularArgIndex == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), currentArg);
        }
        if (regularArgIndex == 2) {
            return filterCompletions(Arrays.asList("1h", "6h", "12h", "1d", "3d", "7d", "14d", "30d", "1y"), currentArg);
        }
        return super.tabComplete(sender, args);
    }
}
