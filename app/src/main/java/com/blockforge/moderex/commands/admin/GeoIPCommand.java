package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * /geoip <user>
 *
 * Display user's country based on their IP address.
 * Requires GeoIP database to be configured.
 */
public class GeoIPCommand extends BaseCommand {

    public GeoIPCommand(ModereX plugin) {
        super(plugin, "moderex.geoip", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /geoip <user>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]);
            return;
        }

        String ip = target.getAddress() != null ?
                target.getAddress().getAddress().getHostAddress() : null;

        if (ip == null) {
            sendMessage(sender, "<red>Could not get player's IP address.");
            return;
        }

        // GeoIP database not configured - show appropriate message
        sendMessage(sender, MessageKey.GEOIP_DATABASE_MISSING);
        // Full implementation would:
        // 1. Check if GeoIP database is configured
        // 2. Look up IP address in GeoIP database
        // 3. Display: Country, Region, City (if available)
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
