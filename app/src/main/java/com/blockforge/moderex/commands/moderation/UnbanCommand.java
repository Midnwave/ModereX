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

public class UnbanCommand extends BaseCommand {

    public UnbanCommand(ModereX plugin) {
        super(plugin, "moderex.command.unban", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /unban <player> [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        if (!plugin.getPunishmentManager().isBanned(targetUuid)) {
            sendMessage(sender, MessageKey.UNBAN_NOT_BANNED, "player", displayName);
            return;
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        String reason = args.length > 1 ? joinArgs(args, 1) : null;

        plugin.getPunishmentManager().removePunishment(targetUuid, PunishmentType.BAN, staffUuid, staffName, reason)
                .thenAccept(success -> {
                    if (success) {
                        sendMessage(sender, MessageKey.UNBAN_SUCCESS, "player", displayName);
                    } else {
                        sendMessage(sender, "<red>Failed to unban player.");
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
