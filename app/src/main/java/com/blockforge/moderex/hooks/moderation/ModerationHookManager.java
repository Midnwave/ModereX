package com.blockforge.moderex.hooks.moderation;

import com.blockforge.moderex.ModereX;

import java.util.*;

public class ModerationHookManager {

    private final ModereX plugin;
    private final Map<String, ModerationHook> hooks = new LinkedHashMap<>();

    public ModerationHookManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        plugin.logDebug("Initializing moderation plugin hooks...");

        // Register all hooks
        registerHook(new LiteBansHook(plugin));
        registerHook(new AdvancedBanHook(plugin));
        registerHook(new EssentialsHook(plugin));
        registerHook(new CMIHook(plugin));

        // Log available hooks
        List<String> available = hooks.values().stream()
                .filter(ModerationHook::isAvailable)
                .map(ModerationHook::getPluginName)
                .toList();

        if (available.isEmpty()) {
            plugin.getLogger().info("No external moderation plugins detected");
        } else {
            plugin.getLogger().info("Detected moderation plugins: " + String.join(", ", available));
        }
    }

    private void registerHook(ModerationHook hook) {
        hooks.put(hook.getPluginName(), hook);
    }

    public ModerationHook getHook(String pluginName) {
        ModerationHook hook = hooks.get(pluginName);
        return (hook != null && hook.isAvailable()) ? hook : null;
    }

    public List<ModerationHook> getAvailableHooks() {
        return hooks.values().stream()
                .filter(ModerationHook::isAvailable)
                .toList();
    }

    public Map<String, List<ModerationHook.ExternalPunishment>> getAllPunishments(UUID uuid) {
        Map<String, List<ModerationHook.ExternalPunishment>> result = new LinkedHashMap<>();

        for (ModerationHook hook : getAvailableHooks()) {
            try {
                List<ModerationHook.ExternalPunishment> punishments = hook.getPunishments(uuid);
                if (!punishments.isEmpty()) {
                    result.put(hook.getPluginName(), punishments);
                }
            } catch (Exception e) {
                plugin.logError("Failed to get punishments from " + hook.getPluginName(), e);
            }
        }

        return result;
    }

    public Map<String, List<ModerationHook.ExternalPunishment>> getActivePunishments(UUID uuid) {
        Map<String, List<ModerationHook.ExternalPunishment>> result = new LinkedHashMap<>();

        for (ModerationHook hook : getAvailableHooks()) {
            try {
                List<ModerationHook.ExternalPunishment> punishments = hook.getActivePunishments(uuid);
                if (!punishments.isEmpty()) {
                    result.put(hook.getPluginName(), punishments);
                }
            } catch (Exception e) {
                plugin.logError("Failed to get active punishments from " + hook.getPluginName(), e);
            }
        }

        return result;
    }

    public int importPunishments(String pluginName, UUID uuid) {
        ModerationHook hook = getHook(pluginName);
        if (hook == null) {
            plugin.logDebug("Hook not available: " + pluginName);
            return 0;
        }

        try {
            return hook.importAllPunishments(uuid);
        } catch (Exception e) {
            plugin.logError("Failed to import punishments from " + pluginName, e);
            return 0;
        }
    }

    public int importAllPunishments(UUID uuid) {
        int total = 0;

        for (ModerationHook hook : getAvailableHooks()) {
            try {
                int imported = hook.importAllPunishments(uuid);
                total += imported;
                if (imported > 0) {
                    plugin.getLogger().info("Imported " + imported + " punishment(s) from " + hook.getPluginName() + " for player " + uuid);
                }
            } catch (Exception e) {
                plugin.logError("Failed to import punishments from " + hook.getPluginName(), e);
            }
        }

        return total;
    }

    public boolean hasAvailableHooks() {
        return !getAvailableHooks().isEmpty();
    }

    public List<DetectedPlugin> getDetectedPlugins() {
        return getAvailableHooks().stream()
                .map(hook -> new DetectedPlugin(hook.getPluginName(), hook.getTotalPunishmentCount()))
                .toList();
    }

    public record DetectedPlugin(String name, int punishmentCount) {
        public String getName() { return name; }
        public int getPunishmentCount() { return punishmentCount; }
    }
}
