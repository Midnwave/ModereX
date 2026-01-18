package com.blockforge.moderex.vanish.api;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Public API for other plugins to respect vanish status.
 * Plugins should use this API instead of direct Bukkit calls for vanish-aware behavior.
 */
public class VanishAPI {

    private final ModereX plugin;
    private final Set<String> whitelistedPlugins = new HashSet<>();

    public VanishAPI(ModereX plugin) {
        this.plugin = plugin;
        loadWhitelistedPlugins();
    }

    private void loadWhitelistedPlugins() {
        // Load from config
        if (plugin.getConfigManager() != null && plugin.getConfigManager().getSettings() != null) {
            whitelistedPlugins.addAll(plugin.getConfigManager().getSettings().getVanishApiWhitelist());
        }
        plugin.logDebug("[VanishAPI] Loaded " + whitelistedPlugins.size() + " whitelisted plugins");
    }

    /**
     * Check if a player is vanished.
     *
     * @param player The player to check
     * @return true if the player is vanished
     */
    public boolean isVanished(Player player) {
        return plugin.getVanishManager().isVanished(player);
    }

    /**
     * Check if a player is vanished by UUID.
     *
     * @param uuid The player UUID
     * @return true if the player is vanished
     */
    public boolean isVanished(UUID uuid) {
        return plugin.getVanishManager().isVanished(uuid);
    }

    /**
     * Check if a plugin can see vanished players.
     * Plugins are whitelisted in config.yml under vanish.api-whitelist
     *
     * @param plugin The plugin to check
     * @return true if the plugin can see vanished players
     */
    public boolean canSeeVanished(Plugin plugin) {
        if (plugin == null) return false;
        return whitelistedPlugins.contains(plugin.getName().toLowerCase());
    }

    /**
     * Check if a player can see another player (respects vanish).
     *
     * @param observer The player who is observing
     * @param target The player being observed
     * @return true if the observer can see the target
     */
    public boolean canSee(Player observer, Player target) {
        if (!isVanished(target)) {
            return true;
        }

        return observer.hasPermission("moderex.command.vanish");
    }

    /**
     * Get online players visible to a specific observer.
     * This filters out vanished players that the observer cannot see.
     *
     * @param observer The observing player
     * @return Collection of visible players
     */
    public Collection<? extends Player> getVisiblePlayers(Player observer) {
        return plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> canSee(observer, player))
                .collect(Collectors.toList());
    }

    /**
     * Get online players visible to a specific plugin.
     * This filters out vanished players unless the plugin is whitelisted.
     *
     * @param requestingPlugin The plugin requesting the list
     * @return Collection of visible players
     */
    public Collection<? extends Player> getVisiblePlayers(Plugin requestingPlugin) {
        if (canSeeVanished(requestingPlugin)) {
            return plugin.getServer().getOnlinePlayers();
        }

        return plugin.getServer().getOnlinePlayers().stream()
                .filter(player -> !isVanished(player))
                .collect(Collectors.toList());
    }

    /**
     * Get the count of visible online players (excludes vanished players).
     *
     * @return Number of visible players
     */
    public int getVisiblePlayerCount() {
        return plugin.getVanishManager().getVisiblePlayerCount();
    }

    /**
     * Add a plugin to the whitelist (allows it to see vanished players).
     *
     * @param pluginName The plugin name (case-insensitive)
     */
    public void whitelistPlugin(String pluginName) {
        whitelistedPlugins.add(pluginName.toLowerCase());
        plugin.logDebug("[VanishAPI] Whitelisted plugin: " + pluginName);
    }

    /**
     * Remove a plugin from the whitelist.
     *
     * @param pluginName The plugin name (case-insensitive)
     */
    public void unwhitelistPlugin(String pluginName) {
        whitelistedPlugins.remove(pluginName.toLowerCase());
        plugin.logDebug("[VanishAPI] Removed plugin from whitelist: " + pluginName);
    }

    /**
     * Check if a plugin is whitelisted.
     *
     * @param pluginName The plugin name (case-insensitive)
     * @return true if whitelisted
     */
    public boolean isPluginWhitelisted(String pluginName) {
        return whitelistedPlugins.contains(pluginName.toLowerCase());
    }

    /**
     * Get all whitelisted plugin names.
     *
     * @return Set of whitelisted plugin names
     */
    public Set<String> getWhitelistedPlugins() {
        return new HashSet<>(whitelistedPlugins);
    }

    /**
     * Reload whitelist from config.
     */
    public void reload() {
        whitelistedPlugins.clear();
        loadWhitelistedPlugins();
    }
}
