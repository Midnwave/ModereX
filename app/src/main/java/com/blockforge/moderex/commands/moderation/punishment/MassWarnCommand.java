package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Warns multiple players at once with a shared batch ID.
 * Usage: /masswarn player1,player2,player3 [duration] reason
 */
public class MassWarnCommand extends BaseCommand {

    public MassWarnCommand(ModereX plugin) {
        super(plugin, "moderex.masswarn", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /masswarn <player1,player2,...> [duration] <reason>");
            return;
        }

        // Parse player names (comma-separated)
        String[] playerNames = args[0].split(",");
        if (playerNames.length < 1) {
            sendMessage(sender, "<red>Please specify at least one player.");
            return;
        }

        // Resolve targets
        List<TargetResolver> targets = new ArrayList<>();
        List<String> invalidPlayers = new ArrayList<>();

        for (String name : playerNames) {
            TargetResolver target = new TargetResolver(name.trim());
            if (target.isValid() && target.isPlayer() && target.getUuid() != null) {
                targets.add(target);
            } else {
                invalidPlayers.add(name.trim());
            }
        }

        if (targets.isEmpty()) {
            sendMessage(sender, "<red>No valid players found.");
            return;
        }

        if (!invalidPlayers.isEmpty()) {
            sendMessage(sender, "<yellow>Warning: Could not find players: " + String.join(", ", invalidPlayers));
        }

        // Parse duration and reason
        long duration = DurationParser.parse("30d"); // Default 30 days for warns
        String reason;
        int reasonStartIndex = 1;

        if (args.length >= 2 && DurationParser.isValidDuration(args[1])) {
            duration = DurationParser.parse(args[1]);
            reasonStartIndex = 2;
        }

        if (args.length <= reasonStartIndex) {
            reason = "Mass warning";
        } else {
            reason = String.join(" ", Arrays.copyOfRange(args, reasonStartIndex, args.length));
        }

        // Truncate reason to 100 characters
        if (reason.length() > 100) {
            reason = reason.substring(0, 100);
        }

        // Make reason effectively final for lambda
        final String finalReasonText = reason;

        // Get executor info
        UUID executorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String executorName = sender.getName();

        // Generate batch ID for tracking
        String batchId = "MASS-" + System.currentTimeMillis();

        // Execute mass warn
        String finalReason = finalReasonText;
        long finalDuration = duration;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);

        sendMessage(sender, "<yellow>Processing mass warning for " + targets.size() + " players...");

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (TargetResolver target : targets) {
            // Staff weight check - skip targets with higher weight
            if (!checkWeight(sender, target.getUuid())) {
                skippedCount.incrementAndGet();
                continue;
            }

            CompletableFuture<Void> future = plugin.getPunishmentManager().warn(
                    target.getUuid(),
                    target.getDisplayName(),
                    executorUuid,
                    executorName,
                    finalDuration,
                    finalReason
            ).thenAccept(punishment -> {
                if (punishment != null) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            }).exceptionally(ex -> {
                failCount.incrementAndGet();
                plugin.logError("Failed to warn " + target.getDisplayName(), (Exception) ex);
                return null;
            });

            futures.add(future);
        }

        // When all complete, send summary
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<green>Mass warning complete!");
                sendMessage(sender, "<gray>Successfully warned: <green>" + successCount.get() +
                        "<gray>, Failed: <red>" + failCount.get() +
                        (skippedCount.get() > 0 ? "<gray>, Skipped (higher rank): <yellow>" + skippedCount.get() : ""));
                sendMessage(sender, "<gray>Batch ID: <yellow>" + batchId);

                // Broadcast to staff
                String playerList = targets.stream()
                        .map(TargetResolver::getDisplayName)
                        .collect(Collectors.joining(", "));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (PermissionUtil.hasPermission(staff, "moderex.alerts.punishments")) {
                        Msg.send(staff, TextUtil.parse("<gray>" + executorName +
                                " mass warned " + targets.size() + " players. Reason: " + finalReasonText));
                    }
                }
            });
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Suggest online player names
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("1d", "7d", "30d", "1y", "permanent"), args[1]);
        }
        return Collections.emptyList();
    }
}
