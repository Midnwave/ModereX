package com.blockforge.moderex.commands.moderation.punishment;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.Msg;
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
 * Removes mass bans by batch ID.
 * Usage: /massunban MASS-1234567890
 */
public class MassUnbanCommand extends BaseCommand {

    public MassUnbanCommand(ModereX plugin) {
        super(plugin, "moderex.massunban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, "<red>Usage: /massunban <batchId>");
            sendMessage(sender, "<gray>Example: /massunban MASS-1234567890");
            return;
        }

        String batchId = args[0];
        if (!batchId.startsWith("MASS-")) {
            sendMessage(sender, "<red>Invalid batch ID format. Should be like: MASS-1234567890");
            return;
        }

        UUID executorUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String executorName = sender.getName();

        sendMessage(sender, "<yellow>Searching for bans with batch ID: " + batchId + "...");

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
                    PunishmentType.BAN.name(),
                    "%" + batchId + "%"
                );
            } catch (SQLException e) {
                plugin.logError("Failed to query mass bans", e);
                return new ArrayList<UUID>();
            }
        }).thenAccept(playerUuids -> {
            if (playerUuids.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sendMessage(sender, "<red>No active bans found with batch ID: " + batchId);
                });
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<yellow>Found " + playerUuids.size() + " bans. Removing...");
            });

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();

            for (UUID playerUuid : playerUuids) {
                CompletableFuture<Boolean> future = plugin.getPunishmentManager().removePunishment(
                    playerUuid,
                    PunishmentType.BAN,
                    executorUuid,
                    executorName,
                    "Mass unban - " + batchId
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
                    sendMessage(sender, "<green>Mass unban complete!");
                    sendMessage(sender, "<gray>Successfully removed: <green>" + successCount.get() +
                            "<gray>, Failed: <red>" + failCount.get());

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("moderex.alerts.punishments")) {
                            Msg.send(staff, TextUtil.parse("<gray>" + executorName +
                                    " mass unbanned " + successCount.get() + " players (Batch: " + batchId + ")"));
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
