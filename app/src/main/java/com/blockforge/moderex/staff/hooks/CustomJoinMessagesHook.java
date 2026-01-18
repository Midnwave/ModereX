package com.blockforge.moderex.staff.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Hook for custom-join-messages plugin.
 * This plugin handles join/leave messages through events.
 */
public class CustomJoinMessagesHook extends AbstractVanishPluginHook {

    public CustomJoinMessagesHook(ModereX plugin) {
        super(plugin);
    }

    @Override
    public String getPluginName() {
        return "CustomJoinMessages";
    }

    @Override
    protected boolean getHideMessagesSetting() {
        return plugin.getConfigManager().getSettings().isVanishHookCustomJoinMessagesHideMessages();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!shouldHideMessage()) return;

        if (isVanished(event.getPlayer().getUniqueId())) {
            event.setJoinMessage(null);
            plugin.logDebug("[VanishHook] Suppressed CustomJoinMessages join message for vanished player: " + event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!shouldHideMessage()) return;

        if (isVanished(event.getPlayer().getUniqueId())) {
            event.setQuitMessage(null);
            plugin.logDebug("[VanishHook] Suppressed CustomJoinMessages quit message for vanished player: " + event.getPlayer().getName());
        }
    }
}
