package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Removes mass warnings by batch ID.
 * Usage: /massunwarn MASS-1234567890
 */
public class MassUnwarnCommand extends BaseCommand {

    public MassUnwarnCommand(ModereX plugin) {
        super(plugin, "moderex.massunwarn", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, "<red>Usage: /massunwarn <batchId>");
            sendMessage(sender, "<gray>Example: /massunwarn MASS-1234567890");
            return;
        }

        String batchId = args[0];
        if (!batchId.startsWith("MASS-")) {
            sendMessage(sender, "<red>Invalid batch ID format. Should be like: MASS-1234567890");
            return;
        }

        UUID executorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String executorName = sender.getName();

        sendMessage(sender, "<yellow>Searching for warnings with batch ID: " + batchId + "...");

        // Search for punishments with this batch ID in reason
        CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query(
                    "SELECT player_uuid FROM moderex_punishments WHERE type = ? AND reason LIKE ? AND active = 1",
                    rs -> {
                        List<UUID> playerUuids = new ArrayList<>();
                        while (rs.next()) {
                            playerUuids.add(UUID.fromString(rs.getString("player_uuid")));
                        }
                        return playerUuids;
                    },
                    PunishmentType.WARN.name(),
                    "%" + batchId + "%"
                );
            } catch (SQLException e) {
                plugin.logError("Failed to query mass warnings", e);
                return new ArrayList<UUID>();
            }
        }).thenAccept(playerUuids -> {
            if (playerUuids.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sendMessage(sender, "<red>No active warnings found with batch ID: " + batchId);
                });
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<yellow>Found " + playerUuids.size() + " warnings. Removing...");
            });

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();

            for (UUID playerUuid : playerUuids) {
                CompletableFuture<Boolean> future = plugin.getPunishmentManager().removePunishment(
                    playerUuid,
                    PunishmentType.WARN,
                    executorUuid,
                    executorName,
                    "Mass unwarn - " + batchId
                ).thenApply(success -> {
                    if (success) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                    return success;
                }).exceptionally(ex -> {
                    failCount.incrementAndGet();
                    return false;
                });

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sendMessage(sender, "<green>Mass unwarn complete!");
                    sendMessage(sender, "<gray>Successfully removed: <green>" + successCount.get() +
                            "<gray>, Failed: <red>" + failCount.get());

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (PermissionUtil.hasPermission(staff, "moderex.alerts.punishments")) {
                            Msg.send(staff, TextUtil.parse("<gray>" + executorName +
                                    " mass unwarned " + successCount.get() + " players (Batch: " + batchId + ")"));
                        }
                    }
                });
            });
        });
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(Collections.singletonList("MASS-"), args[0]);
        }
        return Collections.emptyList();
    }
}
