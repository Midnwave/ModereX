package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mutes multiple players at once with a shared batch ID.
 * Usage: /massmute player1,player2,player3 duration reason
 */
public class MassMuteCommand extends BaseCommand {

    public MassMuteCommand(ModereX plugin) {
        super(plugin, "moderex.massmute", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /massmute <player1,player2,...> <duration> <reason>");
            return;
        }

        // Parse player names (comma-separated)
        String[] playerNames = args[0].split(",");
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

        // Parse duration
        if (!DurationParser.isValidDuration(args[1])) {
            sendMessage(sender, "<red>Invalid duration format. Use: 1h, 1d, 1w, 1mo, permanent");
            return;
        }
        long duration = DurationParser.parse(args[1]);

        // Parse reason
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (reason.length() > 100) {
            reason = reason.substring(0, 100);
        }
        final String finalReasonText = reason;

        UUID executorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String executorName = sender.getName();
        String batchId = "MASS-" + System.currentTimeMillis();
        String finalReason = finalReasonText + " [" + batchId + "]";
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        sendMessage(sender, "<yellow>Processing mass mute for " + targets.size() + " players...");

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (TargetResolver target : targets) {
            CompletableFuture<Void> future = plugin.getPunishmentManager().mute(
                    target.getUuid(),
                    target.getDisplayName(),
                    executorUuid,
                    executorName,
                    duration,
                    finalReason
            ).thenAccept(punishment -> {
                if (punishment != null) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            }).exceptionally(ex -> {
                failCount.incrementAndGet();
                return null;
            });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<green>Mass mute complete!");
                sendMessage(sender, "<gray>Successfully muted: <green>" + successCount.get() +
                        "<gray>, Failed: <red>" + failCount.get());
                sendMessage(sender, "<gray>Batch ID: <yellow>" + batchId);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("moderex.alerts.punishments")) {
                        staff.sendMessage(TextUtil.parse("<gray>" + executorName +
                                " mass muted " + targets.size() + " players. Reason: " + finalReasonText));
                    }
                }
            });
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "permanent"), args[1]);
        }
        return Collections.emptyList();
    }
}
