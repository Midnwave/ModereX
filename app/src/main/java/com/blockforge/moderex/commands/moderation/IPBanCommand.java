package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class IPBanCommand extends BaseCommand {

    public IPBanCommand(ModereX plugin) {
        super(plugin, "moderex.command.ipban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /ipban <player> [duration] [reason]");
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sendMessage(sender, "<red>Player must be online for IP ban. Use /ban for offline players.");
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName();
        String ipAddress = target.getAddress() != null ?
                target.getAddress().getAddress().getHostAddress() : null;

        if (ipAddress == null) {
            sendMessage(sender, "<red>Could not get player's IP address.");
            return;
        }

        long duration = -1;
        String reason = "No reason specified";

        if (args.length >= 2) {
            if (DurationParser.isValidDuration(args[1])) {
                duration = DurationParser.parse(args[1]);
                if (args.length >= 3) {
                    reason = joinArgs(args, 2);
                }
            } else {
                reason = joinArgs(args, 1);
            }
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        final long finalDuration = duration;
        final String finalReason = reason;

        plugin.getPunishmentManager().ipBan(targetUuid, displayName, ipAddress, staffUuid, staffName, finalDuration, finalReason)
                .thenAccept(punishment -> {
                    String durationStr = DurationParser.format(finalDuration);
                    sendMessage(sender, MessageKey.IPBAN_SUCCESS,
                            "player", displayName,
                            "duration", durationStr);
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
        return super.tabComplete(sender, args);
    }
}
