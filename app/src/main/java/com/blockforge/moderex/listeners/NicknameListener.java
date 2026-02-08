package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listener for nickname/display name changes.
 * Monitors for changes via:
 * - Common nickname commands (/nick, /nickname, /name)
 * - Periodic polling of displayName changes
 * - API calls from other plugins
 */
public class NicknameListener implements Listener {

    private final ModereX plugin;
    private final Map<UUID, String> cachedDisplayNames = new ConcurrentHashMap<>();
    private int pollTaskId = -1;

    public NicknameListener(ModereX plugin) {
        this.plugin = plugin;
    }

    public void startPolling() {
        // Poll every 5 seconds for displayName changes (async-safe read)
        pollTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::pollDisplayNames, 100L, 100L).getTaskId();
    }

    public void stopPolling() {
        if (pollTaskId != -1) {
            Bukkit.getScheduler().cancelTask(pollTaskId);
            pollTaskId = -1;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Cache initial display name
        cachedDisplayNames.put(player.getUniqueId(), getDisplayName(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cachedDisplayNames.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Monitor common nickname commands to detect changes immediately.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNicknameCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        String[] parts = message.split("\\s+");
        if (parts.length < 1) return;

        String cmd = parts[0];
        // Monitor common nickname commands
        if (cmd.equals("/nick") || cmd.equals("/nickname") || cmd.equals("/name") ||
            cmd.equals("/essentials:nick") || cmd.equals("/ess:nick") ||
            cmd.equals("/cmi:nick") || cmd.equals("/displayname")) {

            // Schedule a delayed check after the command executes
            Player player = event.getPlayer();
            String oldNick = cachedDisplayNames.getOrDefault(player.getUniqueId(), player.getName());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) return;
                checkNicknameChange(player, oldNick);
            }, 2L); // 2 ticks delay to allow command to process
        }
    }

    /**
     * Poll all online players for displayName changes.
     */
    private void pollDisplayNames() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String oldNick = cachedDisplayNames.get(player.getUniqueId());
            if (oldNick != null) {
                // Must run sync for displayName access
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) return;
                    checkNicknameChange(player, oldNick);
                });
            }
        }
    }

    /**
     * Check if a player's nickname has changed and handle it.
     */
    private void checkNicknameChange(Player player, String oldNick) {
        String newNick = getDisplayName(player);
        if (newNick == null || newNick.equals(oldNick)) return;

        // Update cache
        cachedDisplayNames.put(player.getUniqueId(), newNick);

        plugin.logDebug("[Nickname] Detected nickname change for " + player.getName() +
                ": '" + oldNick + "' -> '" + newNick + "'");

        // Check against automod rules
        AutomodManager.FilterResult result = plugin.getAutomodManager().processNickname(player, newNick);

        if (result.isBlocked()) {
            plugin.logDebug("[Nickname] Nickname blocked by automod: " + result.getReason());
            // Notify the player that their nickname was blocked
            Msg.send(player, TextUtil.parse("<red>Your nickname was blocked by automod."));
            return;
        }

        // Log the nickname change
        plugin.getActivityLogManager().logNicknameChange(player, oldNick, newNick);
    }

    /**
     * Get player's display name, stripping color codes for comparison.
     */
    private String getDisplayName(Player player) {
        // Get the display name as plain text via Msg utility
        String displayName = Msg.getDisplayName(player);
        if (displayName == null) {
            return player.getName();
        }

        return displayName;
    }

    /**
     * Called by external APIs when a nickname change is detected.
     * This is the preferred way for other plugins to notify ModereX of nickname changes.
     *
     * @param player The player whose nickname changed
     * @param newNickname The new nickname
     * @return true if the nickname is allowed, false if blocked by automod
     */
    public boolean handleNicknameChange(Player player, String newNickname) {
        String oldNick = cachedDisplayNames.getOrDefault(player.getUniqueId(), player.getName());

        // Check against automod
        AutomodManager.FilterResult result = plugin.getAutomodManager().processNickname(player, newNickname);

        if (result.isBlocked()) {
            plugin.logDebug("[Nickname] API: Nickname blocked by automod: " + result.getReason());
            return false;
        }

        // Log the change
        plugin.getActivityLogManager().logNicknameChange(player, oldNick, newNickname);

        // Update cache
        cachedDisplayNames.put(player.getUniqueId(), newNickname);

        return true;
    }

    /**
     * Get the last known nickname for a player.
     */
    public String getLastKnownNickname(UUID playerUuid) {
        return cachedDisplayNames.get(playerUuid);
    }
}
