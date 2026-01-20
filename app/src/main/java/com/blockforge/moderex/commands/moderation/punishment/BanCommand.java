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
 * /ban <player> [<duration | reason>]{reason} [flags]
 *
 * Bans a player, preventing them from joining the server.
 * Supports various flags for customization.
 */
public class BanCommand extends PunishmentCommandBase {

    public BanCommand(ModereX plugin) {
        super(plugin, "moderex.ban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        // Parse flags from arguments
        FlagParser flagParser = new FlagParser(args);
        List<String> regularArgs = flagParser.getRegularArgs();

        if (regularArgs.isEmpty()) {
            sendMessage(sender, "<red>Usage: /ban <player> [duration] [reason] [flags]");
            return;
        }

        // Check console-only flags
        if (!(sender instanceof ConsoleCommandSender)) {
            if (flagParser.getSender() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
            if (flagParser.getSenderUuid() != null) {
                sendMessage(sender, MessageKey.NO_PERMISSION);
                return;
            }
        }

        // Check flag permissions
        if (!checkFlagPermissions(sender, flagParser)) {
            return;
        }

        // Resolve target
        TargetResolver target = new TargetResolver(regularArgs.get(0));

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", regularArgs.get(0));
            return;
        }

        if (target.getUuid() == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", target.getDisplayName());
            return;
        }

        // Check if already banned (unless modify or delete flag)
        if (!flagParser.isModify() && !flagParser.isDelete()) {
            if (plugin.getPunishmentManager().isBanned(target.getUuid())) {
                sendMessage(sender, MessageKey.BAN_ALREADY_BANNED, "player", target.getDisplayName());
                return;
            }
        }

        // Parse duration and reason
        long duration = -1;  // Default to permanent
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

        // Build punishment context
        PunishmentContext context = PunishmentContext.builder(sender)
                .target(target)
                .flags(flagParser)
                .duration(duration)
                .reason(reason)
                .build();

        // Handle special cases
        if (flagParser.isDelete()) {
            handleDelete(context);
            return;
        }

        if (flagParser.isModify()) {
            handleModify(context);
            return;
        }

        // Execute the ban
        executeBan(context);
    }


    private void handleDelete(PunishmentContext context) {
        // Extract punishment ID from target (e.g., #123)
        String targetStr = context.getTarget().getInput();
        if (!targetStr.startsWith("#")) {
            sendMessage(context.getSender(), "<red>Delete requires a punishment ID (e.g., #123)");
            return;
        }

        try {
            long punishmentId = Long.parseLong(targetStr.substring(1));

            plugin.getPunishmentManager().getPunishmentById(punishmentId).thenAccept(punishment -> {
                if (punishment == null) {
                    sendMessage(context.getSender(), MessageKey.PUNISHMENT_NOT_FOUND, "id", String.valueOf(punishmentId));
                    return;
                }

                plugin.getPunishmentManager().deletePunishment(
                    punishmentId,
                    context.getExecutorUuid(),
                    context.getExecutorName()
                ).thenAccept(success -> {
                    if (success) {
                        sendMessage(context.getSender(), MessageKey.DELETE_SUCCESS,
                            "id", String.valueOf(punishmentId));
                        sendMessage(context.getSender(), MessageKey.DELETE_REVERTED_TEMPLATE,
                            "player", punishment.getPlayerName());
                    } else {
                        sendMessage(context.getSender(), "<red>Failed to delete punishment.");
                    }
                });
            });
        } catch (NumberFormatException e) {
            sendMessage(context.getSender(), "<red>Invalid punishment ID format.");
        }
    }

    private void handleModify(PunishmentContext context) {
        // Extract punishment ID from target (e.g., #123)
        String targetStr = context.getTarget().getInput();
        if (!targetStr.startsWith("#")) {
            sendMessage(context.getSender(), "<red>Modify requires a punishment ID (e.g., #123)");
            return;
        }

        try {
            long punishmentId = Long.parseLong(targetStr.substring(1));
            long newDuration = context.getDuration() != null ? context.getDuration() : -1;
            String newReason = context.getReason();

            plugin.getPunishmentManager().modifyPunishment(
                punishmentId,
                newDuration,
                newReason,
                context.getExecutorUuid(),
                context.getExecutorName()
            ).thenAccept(success -> {
                if (success) {
                    sendMessage(context.getSender(), MessageKey.MODIFY_SUCCESS,
                        "id", String.valueOf(punishmentId));
                } else {
                    sendMessage(context.getSender(), "<red>Failed to modify punishment. Check if ID exists.");
                }
            });
        } catch (NumberFormatException e) {
            sendMessage(context.getSender(), "<red>Invalid punishment ID format.");
        }
    }

    private void executeBan(PunishmentContext context) {
        TargetResolver target = context.getTarget();
        long duration = context.getDuration() != null ? context.getDuration() : -1;
        String reason = context.getReason();

        // Determine if this should be an IP ban
        if (context.isIpBased()) {
            // Get player's IP address
            org.bukkit.entity.Player onlinePlayer = org.bukkit.Bukkit.getPlayer(target.getUuid());
            if (onlinePlayer == null || onlinePlayer.getAddress() == null) {
                sendMessage(context.getSender(), MessageKey.PLAYER_NOT_ONLINE, "player", target.getDisplayName());
                sendMessage(context.getSender(), "<gray>Use /ipban <ip> for offline players.");
                return;
            }

            String ipAddress = onlinePlayer.getAddress().getAddress().getHostAddress();

            plugin.getPunishmentManager().ipBan(
                    target.getUuid(),
                    target.getDisplayName(),
                    ipAddress,
                    context.getExecutorUuid(),
                    context.getExecutorName(),
                    duration,
                    reason
            ).thenAccept(punishment -> {
                String durationStr = DurationParser.format(duration);
                sendMessage(context.getSender(), MessageKey.IPBAN_SUCCESS,
                        "player", target.getDisplayName(),
                        "duration", durationStr);

                broadcastPunishment(context, target.getDisplayName(), durationStr, reason, MessageKey.IPBAN_BROADCAST);
            });
            return;
        }

        // Execute the ban
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

            broadcastPunishment(context, target.getDisplayName(), durationStr, reason, MessageKey.BAN_BROADCAST);
        });
    }

    private void broadcastPunishment(PunishmentContext context, String playerName, String duration, String reason, MessageKey broadcastKey) {
        // Handle broadcast based on silence level
        if (context.isHidden()) {
            // --hide: Completely hidden, no broadcast at all
            return;
        }

        if (context.isExtraSilent()) {
            // -S: Extra silent - console only
            plugin.getLogger().info(String.format("%s banned %s for %s. Reason: %s",
                    context.getExecutorName(), playerName, duration, reason));
            return;
        }

        if (context.isSilent()) {
            // -s: Silent - don't broadcast to other players, only to sender
            return;
        }

        // Normal broadcast to all staff with permission
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            net.kyori.adventure.text.Component message = plugin.getLanguageManager().get(broadcastKey,
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
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "1y", "permanent"), args[1]);
        }
        return super.tabComplete(sender, args);
    }
}
