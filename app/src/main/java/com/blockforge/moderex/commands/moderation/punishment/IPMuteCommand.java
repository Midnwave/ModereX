package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.moderation.base.PunishmentCommandBase;
import com.blockforge.moderex.commands.moderation.base.PunishmentContext;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * /ipmute <player> [<duration | reason>]{reason} [flags]
 * Alias: /muteip
 *
 * Mutes a player by their account and IP address.
 * Player must be online to obtain IP address.
 */
public class IPMuteCommand extends PunishmentCommandBase {

    public IPMuteCommand(ModereX plugin) {
        super(plugin, "moderex.ipmute", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /ipmute <player> [duration] [reason] [flags]");
            return;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            if (flagParser.getSender() != null || flagParser.getSenderUuid() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
        }

        if (!checkFlagPermissions(sender, flagParser, false, true, true)) {
            return;
        }

        Player target = Bukkit.getPlayer(regularArgs.get(0));

        if (target == null) {
            sendMessage(sender, "<red>Player must be online for IP mute. Use /mute for offline players.");
            return;
        }

        String ipAddress = target.getAddress() != null ?
                target.getAddress().getAddress().getHostAddress() : null;

        if (ipAddress == null) {
            sendMessage(sender, "<red>Could not get player's IP address.");
            return;
        }

        long duration = -1;
        String reason = "No reason specified";

        if (regularArgs.size() >= 2) {
            if (DurationParser.isValidDuration(regularArgs.get(1))) {
                duration = DurationParser.parse(regularArgs.get(1));
                if (regularArgs.size() >= 3) {
                    reason = flagParser.joinArgs(2);
                }
            } else {
                reason = flagParser.joinArgs(1);
            }
        }

        PunishmentContext context = PunishmentContext.builder(sender)
                .target(new TargetResolver(target.getName()))
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

        executeIPMute(context, target, ipAddress);
    }

    private void executeIPMute(PunishmentContext context, Player target, String ipAddress) {
        long duration = context.getDuration() != null ? context.getDuration() : -1;
        String reason = context.getReason();

        plugin.getPunishmentManager().ipMute(
                target.getUniqueId(),
                target.getName(),
                ipAddress,
                context.getExecutorUuid(),
                context.getExecutorName(),
                duration,
                reason
        ).thenAccept(punishment -> {
            String durationStr = DurationParser.format(duration);
            sendMessage(context.getSender(), MessageKey.IPMUTE_SUCCESS,
                    "player", target.getName(),
                    "duration", durationStr);

            broadcastPunishment(context, target.getName(), durationStr, reason);
        });
    }

    private void broadcastPunishment(PunishmentContext context, String playerName, String duration, String reason) {
        if (context.isHidden()) {
            return;
        }

        if (context.isExtraSilent()) {
            plugin.getLogger().info(String.format("%s IP muted %s for %s. Reason: %s",
                    context.getExecutorName(), playerName, duration, reason));
            return;
        }

        if (context.isSilent()) {
            return;
        }

        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component message = plugin.getLanguageManager().get(MessageKey.IPMUTE_BROADCAST,
                    "staff", context.getExecutorName(),
                    "player", playerName,
                    "duration", duration,
                    "reason", reason);

            for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("moderex.notify.punishments")) {
                    Msg.send(player, message);
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
                "-d", "-g", "-m", "-N", "-p", "-s", "-S",
                "--delete", "--modify", "--sender", "--sender-uuid",
                "--server-origin", "--confirm", "--hide", "--skip", "--no-queue"
            ), currentArg);
        }

        if (regularArgIndex == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), currentArg);
        }
        if (regularArgIndex == 2) {
            return filterCompletions(Arrays.asList("10m", "30m", "1h", "6h", "12h", "1d", "3d", "7d", "14d", "30d", "1y", "permanent"), currentArg);
        }
        return super.tabComplete(sender, args);
    }
}
