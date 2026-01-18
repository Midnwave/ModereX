package com.blockforge.moderex.staff.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all vanish plugin hooks.
 */
public class VanishPluginHookManager {

    private final ModereX plugin;
    private final List<VanishPluginHook> hooks = new ArrayList<>();

    public VanishPluginHookManager(ModereX plugin) {
        this.plugin = plugin;
        registerHooks();
    }

    private void registerHooks() {
        // Register all available hooks
        hooks.add(new EssentialsXHook(plugin));
        hooks.add(new CustomJoinMessagesHook(plugin));
        hooks.add(new AlonsoJoinHook(plugin));
    }

    /**
     * Enable all configured hooks.
     */
    public void enableHooks() {
        for (VanishPluginHook hook : hooks) {
            if (shouldEnableHook(hook)) {
                if (hook.isPluginAvailable()) {
                    hook.enable();
                    plugin.getLogger().info("Enabled vanish hook for " + hook.getPluginName());
                } else {
                    plugin.logDebug("[VanishHook] " + hook.getPluginName() + " not found, skipping hook");
                }
            }
        }
    }

    /**
     * Disable all hooks.
     */
    public void disableHooks() {
        for (VanishPluginHook hook : hooks) {
            hook.disable();
        }
    }

    /**
     * Notify hooks when a player vanishes.
     */
    public void onVanish(Player player) {
        for (VanishPluginHook hook : hooks) {
            hook.onVanish(player);
        }
    }

    /**
     * Notify hooks when a player unvanishes.
     */
    public void onUnvanish(Player player) {
        for (VanishPluginHook hook : hooks) {
            hook.onUnvanish(player);
        }
    }

    /**
     * Check if a hook should be enabled based on config.
     */
    private boolean shouldEnableHook(VanishPluginHook hook) {
        String pluginName = hook.getPluginName();
        switch (pluginName) {
            case "Essentials":
                return plugin.getConfigManager().getSettings().isVanishHookEssentialsXEnabled();
            case "CustomJoinMessages":
                return plugin.getConfigManager().getSettings().isVanishHookCustomJoinMessagesEnabled();
            case "AlonsoJoin":
                return plugin.getConfigManager().getSettings().isVanishHookAlonsoJoinEnabled();
            default:
                return false;
        }
    }

    /**
     * Reload hooks based on current config.
     */
    public void reload() {
        disableHooks();
        enableHooks();
    }
}
