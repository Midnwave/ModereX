package com.blockforge.moderex.staff.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Hook for EssentialsX join/leave messages.
 */
public class EssentialsXHook extends AbstractVanishPluginHook {

    public EssentialsXHook(ModereX plugin) {
        super(plugin);
    }

    @Override
    public String getPluginName() {
        return "Essentials";
    }

    @Override
    protected boolean getHideMessagesSetting() {
        return plugin.getConfigManager().getSettings().isVanishHookEssentialsXHideMessages();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!shouldHideMessage()) return;

        if (isVanished(event.getPlayer().getUniqueId())) {
            event.setJoinMessage(null);
            plugin.logDebug("[VanishHook] Suppressed EssentialsX join message for vanished player: " + event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!shouldHideMessage()) return;

        if (isVanished(event.getPlayer().getUniqueId())) {
            event.setQuitMessage(null);
            plugin.logDebug("[VanishHook] Suppressed EssentialsX quit message for vanished player: " + event.getPlayer().getName());
        }
    }
}
