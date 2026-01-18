package com.blockforge.moderex.staff.hooks;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Interface for plugin hooks that modify join/leave messages.
 */
public interface VanishPluginHook extends Listener {

    /**
     * Get the name of the plugin this hook targets.
     */
    String getPluginName();

    /**
     * Check if this plugin is installed on the server.
     */
    boolean isPluginAvailable();

    /**
     * Called when a player vanishes.
     * Hooks should suppress join/leave messages if configured.
     */
    void onVanish(Player player);

    /**
     * Called when a player unvanishes.
     * Hooks should restore normal join/leave message behavior.
     */
    void onUnvanish(Player player);

    /**
     * Called when the hook is enabled.
     * This is where you should register event listeners.
     */
    void enable();

    /**
     * Called when the hook is disabled.
     * This is where you should unregister event listeners.
     */
    void disable();
}
