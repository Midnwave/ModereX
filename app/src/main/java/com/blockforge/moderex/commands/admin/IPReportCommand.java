package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * /ipreport
 *
 * Shows associated accounts of all online players.
 * Shows which online players share IP addresses.
 */
public class IPReportCommand extends BaseCommand {

    public IPReportCommand(ModereX plugin) {
        super(plugin, "moderex.ipreport", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        sendMessage(sender, "<yellow>Generating IP report for online players...");

        Map<String, List<String>> ipMap = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String ip = player.getAddress() != null ?
                    player.getAddress().getAddress().getHostAddress() : null;

            if (ip != null) {
                ipMap.computeIfAbsent(ip, k -> new java.util.ArrayList<>()).add(player.getName());
            }
        }

        boolean foundDupes = false;
        for (Map.Entry<String, List<String>> entry : ipMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                foundDupes = true;
                sendMessage(sender, "<gold>IP " + entry.getKey() + "<yellow>: " +
                        String.join(", ", entry.getValue()));
            }
        }

        if (!foundDupes) {
            sendMessage(sender, "<green>No duplicate IPs found among online players.");
        }

        sendMessage(sender, "<gray>Total unique IPs: " + ipMap.size() +
                " | Total players: " + Bukkit.getOnlinePlayers().size());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args);
    }
}
