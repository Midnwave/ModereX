package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class UnmuteCommand extends BaseCommand {

    public UnmuteCommand(ModereX plugin) {
        super(plugin, "moderex.command.unmute", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /unmute <player> [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        if (!plugin.getPunishmentManager().isMuted(targetUuid)) {
            sendMessage(sender, MessageKey.UNMUTE_NOT_MUTED, "player", displayName);
            return;
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        String reason = args.length > 1 ? joinArgs(args, 1) : null;

        plugin.getPunishmentManager().removePunishment(targetUuid, PunishmentType.MUTE, staffUuid, staffName, reason)
                .thenAccept(success -> {
                    if (success) {
                        sendMessage(sender, MessageKey.UNMUTE_SUCCESS, "player", displayName);
                    } else {
                        sendMessage(sender, "<red>Failed to unmute player.");
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
}
