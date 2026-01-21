package com.blockforge.moderex.commands.moderation.check;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.TargetResolver;
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

        sendMessage(sender, MessageKey.CHECK_HEADER, "player", playerName);

        Player player = Bukkit.getPlayer(playerUUID);
        String onlineStatus = player != null ? "<green>Online</green>" : "<gray>Offline</gray>";
        sendMessage(sender, MessageKey.CHECK_STATUS, "status", onlineStatus);

        sendMessage(sender, MessageKey.CHECK_UUID, "uuid", playerUUID.toString());

        Punishment activeBan = plugin.getPunishmentManager().getActivePunishment(playerUUID, PunishmentType.BAN);
        if (activeBan != null) {
            String duration = activeBan.isPermanent() ? "Permanent" :
                com.blockforge.moderex.util.DurationParser.formatRemaining(activeBan.getExpiresAt());
            sendMessage(sender, MessageKey.CHECK_BAN_ACTIVE,
                "duration", duration,
                "reason", activeBan.getReason(),
                "staff", activeBan.getStaffName());
        } else {
            sendMessage(sender, MessageKey.CHECK_BAN_NONE);
        }

        Punishment activeMute = plugin.getPunishmentManager().getActivePunishment(playerUUID, PunishmentType.MUTE);
        if (activeMute != null) {
            String duration = activeMute.isPermanent() ? "Permanent" :
                com.blockforge.moderex.util.DurationParser.formatRemaining(activeMute.getExpiresAt());
            sendMessage(sender, MessageKey.CHECK_MUTE_ACTIVE,
                "duration", duration,
                "reason", activeMute.getReason(),
                "staff", activeMute.getStaffName());
        } else {
            sendMessage(sender, MessageKey.CHECK_MUTE_NONE);
        }

        plugin.getPunishmentManager().getActiveWarnings(playerUUID).thenAccept(activeWarnings -> {
            if (activeWarnings != null && !activeWarnings.isEmpty()) {
                sendMessage(sender, MessageKey.CHECK_WARNINGS, "count", String.valueOf(activeWarnings.size()));
            } else {
                sendMessage(sender, MessageKey.CHECK_WARNINGS_NONE);
            }
        });

        if (player != null && player.getAddress() != null) {
            String ip = player.getAddress().getAddress().getHostAddress();

            if (sender.hasPermission("moderex.check.ip") || sender.hasPermission("moderex.admin")) {
                sendMessage(sender, MessageKey.CHECK_IP, "ip", ip);
            }

            if (sender.hasPermission("moderex.geoip") || sender.hasPermission("moderex.admin")) {
                sendMessage(sender, MessageKey.CHECK_REGION, "region", "Unknown (GeoIP not configured)");
            }

            if (sender.hasPermission("moderex.dupeip") || sender.hasPermission("moderex.admin")) {
                sendMessage(sender, MessageKey.CHECK_ALTS, "count", "0 (Alt detection pending full implementation)");
            }
        } else {
            if (sender.hasPermission("moderex.check.ip") || sender.hasPermission("moderex.admin")) {
                sendMessage(sender, MessageKey.CHECK_IP_OFFLINE);
            }
        }

        plugin.getPunishmentManager().getPunishments(playerUUID).thenAccept(history -> {
            if (history != null) {
                sendMessage(sender, MessageKey.CHECK_TOTAL_PUNISHMENTS, "count", String.valueOf(history.size()));
            }
        });

        sendMessage(sender, MessageKey.CHECK_FOOTER);
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
