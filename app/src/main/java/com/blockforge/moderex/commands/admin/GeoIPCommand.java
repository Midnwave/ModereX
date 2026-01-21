package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.geoip.GeoIPData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Shows geographic information for a player's IP address.
 * Needs the GeoIP database to be configured in config.yml.
 */
public class GeoIPCommand extends BaseCommand {

    public GeoIPCommand(ModereX plugin) {
        super(plugin, "moderex.geoip", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /geoip <player>");
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

        if (!plugin.getGeoIPManager().isEnabled()) {
            sendMessage(sender, MessageKey.GEOIP_DATABASE_MISSING);
            return;
        }

        Optional<GeoIPData> geoData = plugin.getGeoIPManager().lookup(ip);

        if (geoData.isEmpty()) {
            sendMessage(sender, MessageKey.GEOIP_NOT_AVAILABLE, "player", target.getName());
            return;
        }

        GeoIPData data = geoData.get();

        sendMessage(sender, MessageKey.GEOIP_HEADER, "player", target.getName());
        sendMessage(sender, MessageKey.GEOIP_COUNTRY,
                "country", data.getCountry(),
                "flag", data.getFlagEmoji());
        sendMessage(sender, MessageKey.GEOIP_REGION, "region", data.getRegion());
        sendMessage(sender, MessageKey.GEOIP_CITY, "city", data.getCity());
        sendMessage(sender, MessageKey.GEOIP_IP, "ip", data.getIpAddress());
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(getOnlinePlayerNames(sender), args[0]);
        }
        return super.tabComplete(sender, args);
    }
}
