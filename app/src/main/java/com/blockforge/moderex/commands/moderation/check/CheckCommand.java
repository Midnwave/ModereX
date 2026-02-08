package com.blockforge.moderex.commands.moderation.check;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.TargetResolver;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Comprehensive command that displays all relevant information about a player:
 * - Active bans
 * - Active mutes
 * - Active warnings
 * - IP address
 * - Alt accounts count
 * - Geographic region
 * - UUID
 * - Name history
 */
public class CheckCommand extends BaseCommand {

    public CheckCommand(ModereX plugin) {
        super(plugin, "moderex.check", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /check <player>");
            return;
        }

        TargetResolver target = new TargetResolver(args[0]);

        if (!target.isValid() || !target.isPlayer()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        UUID playerUUID = target.getUuid();
        String playerName = target.getDisplayName();

        Player onlinePlayer = Bukkit.getPlayer(playerUUID);
        boolean isOnline = onlinePlayer != null && !plugin.getVanishManager().isVanished(onlinePlayer);
        boolean isWatched = plugin.getWatchlistManager().isWatched(playerUUID);

        // Header with box-drawing (compact)
        sender.sendMessage(TextUtil.parse(""));
        sender.sendMessage(TextUtil.parse("<dark_gray>┌──────────────────┐"));

        String statusTags = (isOnline ? "<green>●" : "<gray>○") + (isWatched ? " <red>⚑" : "");
        sender.sendMessage(TextUtil.parse("<dark_gray>│ " + statusTags + " <yellow>" + playerName));
        sender.sendMessage(TextUtil.parse("<dark_gray>├──────────────────┤"));

        // UUID (clickable, shortened)
        String shortUuid = playerUUID.toString().substring(0, 8);
        if (sender instanceof Player) {
            Component uuidLine = TextUtil.parse("<dark_gray>│ <gray>ID: <white>" + shortUuid + "...")
                .clickEvent(ClickEvent.copyToClipboard(playerUUID.toString()))
                .hoverEvent(HoverEvent.showText(TextUtil.parse("<white>" + playerUUID + "\n<yellow>Click to copy")));
            sender.sendMessage(uuidLine);
        } else {
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>ID: <white>" + playerUUID));
        }

        // Nickname (if player has one different from their name)
        if (onlinePlayer != null) {
            String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(onlinePlayer.displayName());
            if (!displayName.equals(onlinePlayer.getName())) {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Nick: <light_purple>" + displayName));
            }
        }

        Punishment activeBan = plugin.getPunishmentManager().getActivePunishment(playerUUID, PunishmentType.BAN);
        Punishment activeIpBan = plugin.getPunishmentManager().getActivePunishment(playerUUID, PunishmentType.IPBAN);
        Punishment activeMute = plugin.getPunishmentManager().getActivePunishment(playerUUID, PunishmentType.MUTE);
        Punishment activeIpMute = plugin.getPunishmentManager().getActivePunishment(playerUUID, PunishmentType.IPMUTE);

        // Ban status (compact)
        if (activeBan != null || activeIpBan != null) {
            Punishment ban = activeBan != null ? activeBan : activeIpBan;
            String type = activeBan != null ? "Ban" : "IP-Ban";
            String dur = ban.isPermanent() ? "Perm" : DurationParser.formatRemaining(ban.getExpiresAt());

            if (sender instanceof Player) {
                Component banLine = TextUtil.parse("<dark_gray>│ <red>✗ " + type + " <dark_gray>(<white>" + dur + "<dark_gray>)")
                    .clickEvent(ClickEvent.runCommand("/viewpunishment " + ban.getCaseId()))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse(
                        "<gray>Case: <white>" + ban.getCaseId() + "\n" +
                        "<gray>Reason: <white>" + ban.getReason() + "\n" +
                        "<gray>By: <white>" + ban.getStaffName() + "\n" +
                        "<yellow>Click for details")));
                sender.sendMessage(banLine);
            } else {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <red>✗ " + type + ": " + dur));
            }
        } else {
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <green>✓ <gray>Not banned"));
        }

        // Mute status (compact)
        if (activeMute != null || activeIpMute != null) {
            Punishment mute = activeMute != null ? activeMute : activeIpMute;
            String type = activeMute != null ? "Mute" : "IP-Mute";
            String dur = mute.isPermanent() ? "Perm" : DurationParser.formatRemaining(mute.getExpiresAt());

            if (sender instanceof Player) {
                Component muteLine = TextUtil.parse("<dark_gray>│ <gold>✗ " + type + " <dark_gray>(<white>" + dur + "<dark_gray>)")
                    .clickEvent(ClickEvent.runCommand("/viewpunishment " + mute.getCaseId()))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse(
                        "<gray>Case: <white>" + mute.getCaseId() + "\n" +
                        "<gray>Reason: <white>" + mute.getReason() + "\n" +
                        "<gray>By: <white>" + mute.getStaffName() + "\n" +
                        "<yellow>Click for details")));
                sender.sendMessage(muteLine);
            } else {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gold>✗ " + type + ": " + dur));
            }
        } else {
            sender.sendMessage(TextUtil.parse("<dark_gray>│ <green>✓ <gray>Not muted"));
        }

        // Warnings count (compact)
        plugin.getPunishmentManager().getActiveWarnings(playerUUID).thenAccept(activeWarnings -> {
            int warnCount = activeWarnings != null ? activeWarnings.size() : 0;
            if (warnCount > 0) {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <aqua>⚠ <white>" + warnCount + " <gray>warning" + (warnCount > 1 ? "s" : "")));
            } else {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <green>✓ <gray>No warnings"));
            }
        });

        // IP Address (compact, with permission)
        if (sender.hasPermission("moderex.check.ip") || sender.hasPermission("moderex.admin")) {
            if (onlinePlayer != null && onlinePlayer.getAddress() != null) {
                String ip = onlinePlayer.getAddress().getAddress().getHostAddress();
                if (sender instanceof Player) {
                    Component ipLine = TextUtil.parse("<dark_gray>│ <gray>IP: <white>" + ip)
                        .clickEvent(ClickEvent.copyToClipboard(ip))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<yellow>Click to copy")));
                    sender.sendMessage(ipLine);
                } else {
                    sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>IP: <white>" + ip));
                }
            } else {
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>IP: <dark_gray>-"));
            }
        }

        // Punishment history summary (compact)
        plugin.getPunishmentManager().getPunishments(playerUUID).thenAccept(history -> {
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
                sender.sendMessage(TextUtil.parse("<dark_gray>│ <gray>Past: <red>" + bans + "<dark_gray>/<gold>" +
                        mutes + "<dark_gray>/<aqua>" + warns + "<dark_gray>/<gray>" + kicks));
            }
        });

        sender.sendMessage(TextUtil.parse("<dark_gray>└──────────────────┘"));

        // Quick actions for players (with full names)
        if (sender instanceof Player) {
            Component actions = TextUtil.parse(" ")
                .append(TextUtil.parse("<yellow>[History]")
                    .clickEvent(ClickEvent.runCommand("/history " + playerName))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>View punishment history"))))
                .append(TextUtil.parse(" "))
                .append(TextUtil.parse("<aqua>[Commands]")
                    .clickEvent(ClickEvent.runCommand("/cmdhistory " + playerName))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>View command history"))))
                .append(TextUtil.parse(" "))
                .append(TextUtil.parse("<light_purple>[Activity]")
                    .clickEvent(ClickEvent.runCommand("/activity " + playerName))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>View activity log"))))
                .append(TextUtil.parse(" "))
                .append(TextUtil.parse("<green>[Punish]")
                    .clickEvent(ClickEvent.runCommand("/punish " + playerName))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Open punishment menu"))));

            if (!isWatched) {
                actions = actions.append(TextUtil.parse(" "))
                    .append(TextUtil.parse("<red>[Watch]")
                        .clickEvent(ClickEvent.suggestCommand("/watchlist add " + playerName + " "))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Add to watchlist"))));
            }

            sender.sendMessage(actions);
        }
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
