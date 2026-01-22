package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Displays when a player was last seen and other player info.
 * Usage: /seen <player>
 */
public class SeenCommand extends BaseCommand {

    public SeenCommand(ModereX plugin) {
        super(plugin, "moderex.command.seen", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /seen <player>");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID uuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        // Check if player is online
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        boolean isOnline = onlinePlayer != null && !plugin.getVanishManager().isVanished(onlinePlayer);

        // Get data from database asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PlayerInfo info = fetchPlayerInfo(uuid);

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sendPlayerInfo(sender, displayName, uuid, isOnline, onlinePlayer, info);
                });
            } catch (SQLException e) {
                plugin.logError("Failed to fetch player info for /seen", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to fetch player information."));
            }
        });
    }

    private PlayerInfo fetchPlayerInfo(UUID uuid) throws SQLException {
        return plugin.getDatabaseManager().query("""
                SELECT username, ip_address, first_join, last_join, last_server
                FROM moderex_players WHERE uuid = ?
                """,
                rs -> {
                    if (rs.next()) {
                        return new PlayerInfo(
                                rs.getString("username"),
                                rs.getString("ip_address"),
                                rs.getLong("first_join"),
                                rs.getLong("last_join"),
                                rs.getString("last_server")
                        );
                    }
                    return null;
                },
                uuid.toString()
        );
    }

    private void sendPlayerInfo(CommandSender sender, String playerName, UUID uuid, boolean isOnline,
                                 Player onlinePlayer, PlayerInfo info) {
        // Header
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("━━━━━━━━━━━━━ ", NamedTextColor.DARK_GRAY)
                .append(Component.text("Player Info", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                .append(Component.text(" ━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY)));

        // Player name with online status
        Component statusBadge = isOnline
                ? Component.text(" [ONLINE]", NamedTextColor.GREEN)
                : Component.text(" [OFFLINE]", NamedTextColor.GRAY);

        sender.sendMessage(Component.text(" Player: ", NamedTextColor.GRAY)
                .append(Component.text(playerName, NamedTextColor.YELLOW))
                .append(statusBadge));

        // UUID (clickable to copy)
        sender.sendMessage(Component.text(" UUID: ", NamedTextColor.GRAY)
                .append(Component.text(uuid.toString(), NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.copyToClipboard(uuid.toString()))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy UUID", NamedTextColor.YELLOW)))));

        if (info != null) {
            // First join
            sender.sendMessage(Component.text(" First Join: ", NamedTextColor.GRAY)
                    .append(Component.text(TimeUtil.formatDateTime(info.firstJoin), NamedTextColor.WHITE))
                    .append(Component.text(" (" + TimeUtil.formatRelative(info.firstJoin) + ")", NamedTextColor.DARK_GRAY)));

            // Last seen / Currently online
            if (isOnline) {
                sender.sendMessage(Component.text(" Last Seen: ", NamedTextColor.GRAY)
                        .append(Component.text("Currently online", NamedTextColor.GREEN)));
            } else {
                sender.sendMessage(Component.text(" Last Seen: ", NamedTextColor.GRAY)
                        .append(Component.text(TimeUtil.formatDateTime(info.lastJoin), NamedTextColor.WHITE))
                        .append(Component.text(" (" + TimeUtil.formatRelative(info.lastJoin) + ")", NamedTextColor.DARK_GRAY)));
            }

            // Last server
            if (info.lastServer != null && !info.lastServer.isEmpty()) {
                sender.sendMessage(Component.text(" Last Server: ", NamedTextColor.GRAY)
                        .append(Component.text(info.lastServer, NamedTextColor.WHITE)));
            }

            // IP Address (requires permission)
            if (sender.hasPermission("moderex.command.seen.ip") || sender.hasPermission("moderex.admin")) {
                if (info.ipAddress != null && !info.ipAddress.isEmpty()) {
                    sender.sendMessage(Component.text(" IP Address: ", NamedTextColor.GRAY)
                            .append(Component.text(info.ipAddress, NamedTextColor.WHITE)
                                    .clickEvent(ClickEvent.copyToClipboard(info.ipAddress))
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to copy IP", NamedTextColor.YELLOW)))));
                }
            }
        } else {
            sender.sendMessage(Component.text(" No additional data available", NamedTextColor.GRAY));
        }

        // Punishment summary
        sendPunishmentSummary(sender, uuid, playerName);

        // Watchlist status
        boolean isWatched = plugin.getWatchlistManager().isWatched(uuid);
        if (isWatched) {
            sender.sendMessage(Component.text(" Watchlist: ", NamedTextColor.GRAY)
                    .append(Component.text("WATCHED", NamedTextColor.RED).decorate(TextDecoration.BOLD)));
        }

        // Quick actions (clickable)
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(" Quick Actions: ", NamedTextColor.GRAY)
                .append(Component.text("[Check]", NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.runCommand("/check " + playerName))
                        .hoverEvent(HoverEvent.showText(Component.text("View detailed check", NamedTextColor.YELLOW))))
                .append(Component.text(" "))
                .append(Component.text("[History]", NamedTextColor.GOLD)
                        .clickEvent(ClickEvent.runCommand("/history " + playerName))
                        .hoverEvent(HoverEvent.showText(Component.text("View punishment history", NamedTextColor.YELLOW))))
                .append(Component.text(" "))
                .append(Component.text("[Commands]", NamedTextColor.GREEN)
                        .clickEvent(ClickEvent.runCommand("/mx commandhistory " + playerName))
                        .hoverEvent(HoverEvent.showText(Component.text("View command history", NamedTextColor.YELLOW)))));

        sender.sendMessage(Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY));
    }

    private void sendPunishmentSummary(CommandSender sender, UUID uuid, String playerName) {
        // Get active punishments
        Punishment activeBan = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.BAN);
        Punishment activeMute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);

        // Active ban status
        if (activeBan != null) {
            String duration = activeBan.isPermanent() ? "Permanent" :
                    com.blockforge.moderex.util.DurationParser.formatRemaining(activeBan.getExpiresAt());
            sender.sendMessage(Component.text(" Status: ", NamedTextColor.GRAY)
                    .append(Component.text("BANNED", NamedTextColor.RED).decorate(TextDecoration.BOLD))
                    .append(Component.text(" (" + duration + ")", NamedTextColor.DARK_GRAY)));
        } else if (activeMute != null) {
            String duration = activeMute.isPermanent() ? "Permanent" :
                    com.blockforge.moderex.util.DurationParser.formatRemaining(activeMute.getExpiresAt());
            sender.sendMessage(Component.text(" Status: ", NamedTextColor.GRAY)
                    .append(Component.text("MUTED", NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" (" + duration + ")", NamedTextColor.DARK_GRAY)));
        } else {
            sender.sendMessage(Component.text(" Status: ", NamedTextColor.GRAY)
                    .append(Component.text("Clear", NamedTextColor.GREEN)));
        }

        // Async fetch punishment count
        plugin.getPunishmentManager().getPunishments(uuid).thenAccept(history -> {
            if (history != null && !history.isEmpty()) {
                int bans = 0, mutes = 0, warns = 0, kicks = 0;
                for (Punishment p : history) {
                    switch (p.getType()) {
                        case BAN, IPBAN -> bans++;
                        case MUTE, IPMUTE -> mutes++;
                        case WARN -> warns++;
                        case KICK -> kicks++;
                    }
                }

                int finalBans = bans;
                int finalMutes = mutes;
                int finalWarns = warns;
                int finalKicks = kicks;

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(Component.text(" History: ", NamedTextColor.GRAY)
                            .append(Component.text(finalBans + " bans", NamedTextColor.RED))
                            .append(Component.text(", ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(finalMutes + " mutes", NamedTextColor.GOLD))
                            .append(Component.text(", ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(finalWarns + " warns", NamedTextColor.YELLOW))
                            .append(Component.text(", ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(finalKicks + " kicks", NamedTextColor.GRAY)));
                });
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

    private record PlayerInfo(String username, String ipAddress, long firstJoin, long lastJoin, String lastServer) {
    }
}
