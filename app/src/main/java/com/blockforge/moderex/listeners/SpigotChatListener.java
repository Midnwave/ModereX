package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.ReplaySnapshot;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spigot-compatible chat listener using AsyncPlayerChatEvent.
 * Only registered on Spigot servers. See ChatListener for Paper.
 */
public class SpigotChatListener implements Listener {

    private final ModereX plugin;
    private final Map<UUID, Long> lastMessageTime = new ConcurrentHashMap<>();

    public SpigotChatListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        String message = event.getMessage();

        // Check GUI input handler first
        if (plugin.getGuiManager().hasPendingInput(player)) {
            event.setCancelled(true);
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    plugin.getGuiManager().handleChatInput(player, message));
            return;
        }

        // Check evidence selection session
        if (plugin.getEvidenceSelectionManager() != null &&
                plugin.getEvidenceSelectionManager().hasActiveSession(uuid)) {
            boolean consumed = plugin.getEvidenceSelectionManager().handleChatInput(player, message);
            if (consumed) {
                event.setCancelled(true);
                return;
            }
        }

        // Check if chat is disabled
        if (!plugin.getConfigManager().getSettings().isChatEnabled()) {
            if (!PermissionUtil.hasPermission(player, "moderex.bypass.chatdisable")) {
                event.setCancelled(true);
                Msg.send(player, plugin.getLanguageManager().get(MessageKey.CHAT_DISABLED_MESSAGE));
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
        if (!PermissionUtil.hasPermission(player, "moderex.bypass.mute")) {
            Punishment mute = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.MUTE);
            if (mute != null && !mute.isExpired()) {
                event.setCancelled(true);
                String duration = DurationParser.formatRemaining(mute.getExpiresAt());
                Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.MUTED_CHAT_ATTEMPT,
                        "duration", duration,
                        "reason", mute.getReason()));
                return;
            }
        }

        // Check slowmode
        if (!PermissionUtil.hasPermission(player, "moderex.bypass.slowmode")) {
            int slowmode = plugin.getConfigManager().getSettings().getDefaultSlowmodeSeconds();
            if (slowmode > 0) {
                Long lastTime = lastMessageTime.get(uuid);
                long now = System.currentTimeMillis();

                if (lastTime != null) {
                    long elapsed = (now - lastTime) / 1000;
                    if (elapsed < slowmode) {
                        event.setCancelled(true);
                        long remaining = slowmode - elapsed;
                        Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.CHAT_SLOWMODE_MESSAGE,
                                "seconds", String.valueOf(remaining)));
                        return;
                    }
                }

                lastMessageTime.put(uuid, now);
            }
        }

        // Process through automod
        AutomodManager.FilterResult result = plugin.getAutomodManager().processMessage(player, message);

        // Get server name for logging
        String serverName = plugin.getConfigManager().getSettings().getServerName();

        if (result.isBlocked()) {
            event.setCancelled(true);
            Msg.send(player, plugin.getLanguageManager().getPrefixed(MessageKey.AUTOMOD_BLOCKED));

            // Log blocked message to database
            plugin.getDatabaseManager().logChatMessage(
                uuid.toString(), player.getName(), message, "CHAT",
                true, result.getReason(), serverName);

            // Notify watchlist
            if (plugin.getWatchlistManager().isWatched(uuid)) {
                plugin.getWatchlistManager().onAutomod(player, result.getReason(), message);
            }
            return;
        }

        String finalMessage = result.isModified() ? result.getModifiedMessage() : message;

        if (result.isModified()) {
            event.setMessage(finalMessage);
        }

        // Log message to database
        plugin.getDatabaseManager().logChatMessage(
            uuid.toString(), player.getName(), finalMessage, "CHAT",
            false, null, serverName);

        // Record to replay if active
        if (plugin.getReplayManager() != null) {
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.CHAT, finalMessage);
        }

        // Broadcast to web panel live logs
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastChatMessage(player.getName(), uuid, finalMessage);
        }
    }

    public void clearSlowmodeData(UUID uuid) {
        lastMessageTime.remove(uuid);
    }
}
