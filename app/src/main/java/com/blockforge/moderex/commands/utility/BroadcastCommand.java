package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command to broadcast a message to all players.
 * Shows as: [Staff Broadcast] [Rank] PlayerName: Message
 * Usage: /broadcast <message>
 */
public class BroadcastCommand extends BaseCommand {

    public BroadcastCommand(ModereX plugin) {
        super(plugin, "moderex.command.broadcast", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /broadcast <message>");
            return;
        }

        String message = joinArgs(args, 0);
        String senderName = sender instanceof Player ? sender.getName() : "Console";
        String rankPrefix = "";

        // Get rank prefix from LuckPerms if available and sender is a player
        if (sender instanceof Player player) {
            try {
                var luckPerms = Bukkit.getServicesManager().getRegistration(net.luckperms.api.LuckPerms.class);
                if (luckPerms != null) {
                    var user = luckPerms.getProvider().getUserManager().getUser(player.getUniqueId());
                    if (user != null) {
                        var meta = user.getCachedData().getMetaData();
                        String prefix = meta.getPrefix();
                        if (prefix != null && !prefix.isEmpty()) {
                            rankPrefix = prefix + " ";
                        } else {
                            // Try to get group name
                            String group = user.getPrimaryGroup();
                            if (group != null && !group.equalsIgnoreCase("default")) {
                                rankPrefix = "[" + capitalizeFirst(group) + "] ";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // LuckPerms not available, continue without rank
                plugin.logDebug("[Broadcast] LuckPerms not available for rank prefix");
            }
        }

        // Build the broadcast message
        Component broadcastMessage = Component.text()
            .append(Component.text("[Staff Broadcast] ", NamedTextColor.GOLD))
            .append(TextUtil.parse(rankPrefix))
            .append(Component.text(senderName, NamedTextColor.WHITE))
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(TextUtil.parse(message))
            .build();

        // Send to all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(broadcastMessage);
        }

        // Also log to console
        Bukkit.getConsoleSender().sendMessage(broadcastMessage);

        // Confirmation to sender
        sendMessage(sender, "<green>Broadcast sent.");

        plugin.logDebug("[Broadcast] " + senderName + " sent broadcast: " + message);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        // No tab completion for broadcast message
        return List.of();
    }
}
