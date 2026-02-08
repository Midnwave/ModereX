package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bans multiple players at once with a shared batch ID.
 * Usage: /massban player1,player2,player3 duration reason
 */
public class MassBanCommand extends BaseCommand {

    public MassBanCommand(ModereX plugin) {
        super(plugin, "moderex.massban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /massban <player1,player2,...> <duration> <reason>");
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
        long duration;
        if (args[1].equalsIgnoreCase("permanent") || args[1].equalsIgnoreCase("perm")) {
            if (!PermissionUtil.hasPermission(sender, "moderex.ban")) {
                sendMessage(sender, "<red>You don't have permission to issue permanent bans.");
                return;
            }
            duration = -1; // Permanent
        } else if (!DurationParser.isValidDuration(args[1])) {
            sendMessage(sender, "<red>Invalid duration format. Use: 1h, 1d, 1w, 1mo, permanent");
            return;
        } else {
            duration = DurationParser.parse(args[1]);
        }

        // Parse reason
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (reason.length() > 100) {
            reason = reason.substring(0, 100);
        }
        final String finalReasonText = reason;

        UUID executorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String executorName = sender.getName();
        String batchId = "MASS-" + System.currentTimeMillis();
        String finalReason = finalReasonText;
        long finalDuration = duration;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);

        sendMessage(sender, "<yellow>Processing mass ban for " + targets.size() + " players...");

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (TargetResolver target : targets) {
            // Staff weight check - skip targets with higher weight
            if (!checkWeight(sender, target.getUuid())) {
                skippedCount.incrementAndGet();
                continue;
            }

            CompletableFuture<Void> future = plugin.getPunishmentManager().ban(
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
                return null;
            });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<green>Mass ban complete!");
                sendMessage(sender, "<gray>Successfully banned: <green>" + successCount.get() +
                        "<gray>, Failed: <red>" + failCount.get() +
                        (skippedCount.get() > 0 ? "<gray>, Skipped (higher rank): <yellow>" + skippedCount.get() : ""));
                sendMessage(sender, "<gray>Batch ID: <yellow>" + batchId);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (PermissionUtil.hasPermission(staff, "moderex.alerts.punishments")) {
                        Msg.send(staff, TextUtil.parse("<gray>" + executorName +
                                " mass banned " + targets.size() + " players. Reason: " + finalReasonText));
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
            return filterCompletions(Arrays.asList("1d", "7d", "30d", "permanent"), args[1]);
        }
        return Collections.emptyList();
    }
}
