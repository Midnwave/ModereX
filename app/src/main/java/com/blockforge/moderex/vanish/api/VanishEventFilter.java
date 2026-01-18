package com.blockforge.moderex.vanish.api;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Filters events to hide vanished players from other plugins
 * Runs at LOWEST priority to modify events before other plugins see them
 */
public class VanishEventFilter implements Listener {

    private final ModereX plugin;
    private final VanishAPI vanishAPI;

    public VanishEventFilter(ModereX plugin, VanishAPI vanishAPI) {
        this.plugin = plugin;
        this.vanishAPI = vanishAPI;
    }

    /**
     * Modify player join messages for vanished players
     * Other plugins won't see join messages for vanished staff
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (vanishAPI.isVanished(player)) {
            event.joinMessage(null); // silence join
            plugin.logDebug("[VanishAPI] Silenced join message for vanished player: " + player.getName());
        }
    }

    /**
     * Modify player quit messages for vanished players
     * Other plugins won't see quit messages for vanished staff
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (vanishAPI.isVanished(player)) {
            event.quitMessage(null); // silence quit
            plugin.logDebug("[VanishAPI] Silenced quit message for vanished player: " + player.getName());
        }
    }

    /**
     * Adjust server list ping to hide vanished players from player count
     * Shows accurate player count without vanished staff

     * Note: This is Paper-specific and may not work on all server implementations
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerListPing(ServerListPingEvent event) {
        int visibleCount = vanishAPI.getVisiblePlayerCount();

        if (visibleCount != plugin.getServer().getOnlinePlayers().size()) {
            try {
                try {
                    Object serverPing = event.getClass().getMethod("serverPing").invoke(event);
                    java.lang.reflect.Method setNumPlayers = serverPing.getClass().getMethod("setNumPlayers", int.class);
                    setNumPlayers.invoke(serverPing, visibleCount);
                    plugin.logDebug("[VanishAPI] Adjusted server list player count: " + visibleCount);
                    return;
                } catch (NoSuchMethodException ignored) {
                }

                try {
                    java.lang.reflect.Method setNumPlayers = event.getClass().getMethod("setNumPlayers", int.class);
                    setNumPlayers.invoke(event, visibleCount);
                    plugin.logDebug("[VanishAPI] Adjusted server list player count (legacy): " + visibleCount);
                    return;
                } catch (NoSuchMethodException ignored) {
                }

                plugin.logDebug("[VanishAPI] Server list player count adjustment not supported on this server");

            } catch (Exception e) {
                plugin.logDebug("[VanishAPI] Could not adjust server list count: " + e.getMessage());
            }
        }
    }
}
