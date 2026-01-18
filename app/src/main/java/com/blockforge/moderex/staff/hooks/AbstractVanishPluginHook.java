package com.blockforge.moderex.staff.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Base class for vanish plugin hooks.
 */
public abstract class AbstractVanishPluginHook implements VanishPluginHook {

    protected final ModereX plugin;
    protected final Set<UUID> vanishedPlayers = new HashSet<>();
    protected boolean enabled = false;

    public AbstractVanishPluginHook(ModereX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isPluginAvailable() {
        return Bukkit.getPluginManager().getPlugin(getPluginName()) != null;
    }

    @Override
    public void onVanish(Player player) {
        vanishedPlayers.add(player.getUniqueId());
    }

    @Override
    public void onUnvanish(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
    }

    @Override
    public void enable() {
        if (isPluginAvailable()) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            enabled = true;
            plugin.logDebug("[VanishHook] Enabled hook for " + getPluginName());
        }
    }

    @Override
    public void disable() {
        vanishedPlayers.clear();
        enabled = false;
        plugin.logDebug("[VanishHook] Disabled hook for " + getPluginName());
    }

    protected boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    protected boolean shouldHideMessage() {
        return enabled && getHideMessagesSetting();
    }

    protected abstract boolean getHideMessagesSetting();
}
