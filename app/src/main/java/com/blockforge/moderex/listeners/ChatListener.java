package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.ReplaySnapshot;
import com.blockforge.moderex.util.DurationParser;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private final ModereX plugin;
    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();

    public ChatListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check GUI input handler first
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (plugin.getGuiManager().hasPendingInput(player)) {
            event.setCancelled(true);
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    plugin.getGuiManager().handleChatInput(player, message));
            return;
        }

        // Check if chat is disabled
        if (!plugin.getConfigManager().getSettings().isChatEnabled()) {
            if (!player.hasPermission("moderex.bypass.chatdisable")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getLanguageManager().get(MessageKey.CHAT_DISABLED_MESSAGE));
                return;
            }
        }

        // Check staff chat toggle
        if (plugin.getStaffChatManager().hasStaffChatToggled(player)) {
            event.setCancelled(true);
            plugin.getStaffChatManager().sendMessage(player, message);
            return;
        }

        // Check if player is muted
        if (!player.hasPermission("moderex.bypass.mute")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);
            if (mute != null && !mute.isExpired()) {
                event.setCancelled(true);
                String duration = DurationParser.formatRemaining(mute.getExpiresAt());
                player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
                        "duration", duration,
                        "reason", mute.getReason()));
                return;
            }
        }

        // Check slowmode
        if (!player.hasPermission("moderex.bypass.slowmode")) {
            int slowmode = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();
            if (slowmode > 0) {
                Long lastTime = lastMessageTime.get(uuid);
                long now = System.currentTimeMillis();

                if (lastTime != null) {
                    long elapsed = (now - lastTime) / 1000;
                    if (elapsed < slowmode) {
                        event.setCancelled(true);
                        long remaining = slowmode - elapsed;
                        player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.CHAT_SLOWMODE_MESSAGE,
                                "seconds", String.valueOf(remaining)));
                        return;
                    }
                }

                lastMessageTime.put(uuid, now);
            }
        }

        // Process through automod
        AutomodManager.FilterResult result = plugin.getAutomodManager().processMessage(player, message);

        if (result.isBlocked()) {
            event.setCancelled(true);
            player.sendMessage(plugin.getLanguageManager().getPrefixed(MessageKey.AUTOMOD_BLOCKED));

            // Notify watchlist
            if (plugin.getWatchlistManager().isWatched(uuid)) {
                plugin.getWatchlistManager().onAutomod(player, result.getReason(), message);
            }
            return;
        }

        if (result.isModified()) {
            // Replace message with modified version
            event.message(Component.text(result.getModifiedMessage()));
        }

        // Record to replay if active
        if (plugin.getReplayManager() != null) {
            String chatMessage = result.isModified() ? result.getModifiedMessage() : message;
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.CHAT, chatMessage);
        }
    }

    public void clearSlowmodeData(UUID uuid) {
        lastMessageTime.remove(uuid);
    }
}
