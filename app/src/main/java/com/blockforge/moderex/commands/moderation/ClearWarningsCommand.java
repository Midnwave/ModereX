package com.blockforge.moderex.commands.moderation;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ClearWarningsCommand extends BaseCommand {

    public ClearWarningsCommand(ModereX plugin) {
        super(plugin, "moderex.command.clearwarnings", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /clearwarnings <player>");
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

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int cleared = plugin.getDatabaseManager().update("""
                        UPDATE moderex_warnings SET active = FALSE,
                        removed_by_uuid = ?, removed_by_name = ?, removed_at = ?
                        WHERE player_uuid = ? AND active = TRUE
                        """,
                        staffUuid != null ? staffUuid.toString() : null,
                        staffName,
                        System.currentTimeMillis(),
                        targetUuid.toString()
                );

                if (cleared > 0) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        sendMessage(sender, MessageKey.WARN_CLEARED, "player", displayName);

                        // Broadcast
                        for (Player staff : Bukkit.getOnlinePlayers()) {
                            if (staff.hasPermission("moderex.notify.punishments") && !staff.equals(sender)) {
                                staff.sendMessage(plugin.getLanguageManager().get(MessageKey.WARN_CLEARED_BROADCAST,
                                        "staff", staffName,
                                        "player", displayName));
                            }
                        }
                    });
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            sendMessage(sender, "<yellow>No active warnings found for " + displayName));
                }
            } catch (SQLException e) {
                plugin.logError("Failed to clear warnings", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to clear warnings."));
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
