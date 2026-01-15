package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.DurationParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.UUID;

public class SignListener implements Listener {

    private final ModereX plugin;

    public SignListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if muted players can write signs
        if (!plugin.getConfigManager().getSettings().isMutedPlayersCanWriteSigns()) {
            if (!player.hasPermission("moderex.bypass.mute")) {
                Punishment mute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);
                if (mute != null && !mute.isExpired()) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
                            "duration", DurationParser.formatRemaining(mute.getExpiresAt()),
                            "reason", mute.getReason()));
                    return;
                }
            }
        }

        // Watchlist notification for sign placement
        if (plugin.getWatchlistManager().isWatched(uuid)) {
            String[] lines = new String[4];
            for (int i = 0; i < 4; i++) {
                lines[i] = event.getLine(i) != null ? event.getLine(i) : "";
            }
            plugin.getWatchlistManager().onSignPlace(player, lines);
        }
    }
}
