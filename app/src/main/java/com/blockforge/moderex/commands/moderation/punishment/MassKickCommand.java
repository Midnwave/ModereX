package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Kicks multiple players at once.
 * Usage: /masskick player1,player2,player3 reason
 */
public class MassKickCommand extends BaseCommand {

    public MassKickCommand(ModereX plugin) {
        super(plugin, "moderex.masskick", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /masskick <player1,player2,...> <reason>");
            return;
        }

        // Parse player names (comma-separated)
        String[] playerNames = args[0].split(",");
        List<Player> onlineTargets = new ArrayList<>();
        List<String> invalidPlayers = new ArrayList<>();
        List<String> offlinePlayers = new ArrayList<>();

        for (String name : playerNames) {
            Player target = Bukkit.getPlayer(name.trim());
            if (target != null && target.isOnline()) {
                onlineTargets.add(target);
            } else {
                // Check if player exists but is offline
                TargetResolver resolver = new TargetResolver(name.trim());
                if (resolver.isValid()) {
                    offlinePlayers.add(name.trim());
                } else {
                    invalidPlayers.add(name.trim());
                }
            }
        }

        if (onlineTargets.isEmpty()) {
            sendMessage(sender, "<red>No online players found to kick.");
            return;
        }

        if (!invalidPlayers.isEmpty()) {
            sendMessage(sender, "<yellow>Unknown players: " + String.join(", ", invalidPlayers));
        }
        if (!offlinePlayers.isEmpty()) {
            sendMessage(sender, "<yellow>Offline (skipped): " + String.join(", ", offlinePlayers));
        }

        // Parse reason
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (reason.length() > 100) {
            reason = reason.substring(0, 100);
        }
        final String finalReasonText = reason;

        UUID executorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String executorName = sender.getName();
        String batchId = "MASS-" + System.currentTimeMillis();
        String finalReason = finalReasonText + " [" + batchId + "]";
        AtomicInteger successCount = new AtomicInteger(0);

        sendMessage(sender, "<yellow>Processing mass kick for " + onlineTargets.size() + " players...");

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Player target : onlineTargets) {
            CompletableFuture<Void> future = plugin.getPunishmentManager().kick(
                    target.getUniqueId(),
                    target.getName(),
                    executorUuid,
                    executorName,
                    finalReason
            ).thenAccept(punishment -> {
                successCount.incrementAndGet();
            }).exceptionally(ex -> {
                plugin.logError("Failed to kick " + target.getName(), (Exception) ex);
                return null;
            });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<green>Mass kick complete!");
                sendMessage(sender, "<gray>Successfully kicked: <green>" + successCount.get() + " players");
                sendMessage(sender, "<gray>Batch ID: <yellow>" + batchId);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("moderex.alerts.punishments")) {
                        staff.sendMessage(TextUtil.parse("<gray>" + executorName +
                                " mass kicked " + successCount.get() + " players. Reason: " + finalReasonText));
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
        return Collections.emptyList();
    }
}
