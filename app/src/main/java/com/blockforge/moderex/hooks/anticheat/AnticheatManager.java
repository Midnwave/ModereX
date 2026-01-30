package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffSettings;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnticheatManager {

    private final ModereX plugin;
    private final Map<String, AnticheatHook> hooks = new HashMap<>();
    private final List<String> enabledAnticheats = new ArrayList<>();
    private AnticheatAlertManager alertManager;

    public AnticheatManager(ModereX plugin) {
        this.plugin = plugin;
        this.alertManager = new AnticheatAlertManager(plugin);
    }

    public void initialize() {
        plugin.getLogger().info("[AnticheatManager] Starting initialization...");

        // Load alert manager
        try {
            alertManager.load();
            plugin.getLogger().info("[AnticheatManager] Alert manager loaded");
        } catch (Exception e) {
            plugin.getLogger().severe("[AnticheatManager] Failed to load alert manager: " + e.getMessage());
            e.printStackTrace();
        }

        // Register all anticheat hooks
        plugin.getLogger().info("[AnticheatManager] Registering anticheat hooks...");
        try {
            registerHook(new GrimHook(plugin));
            registerHook(new VulcanHook(plugin));
            registerHook(new MatrixHook(plugin));
            registerHook(new SpartanHook(plugin));
            registerHook(new NCPHook(plugin));
            registerHook(new ThemisHook(plugin));
            registerHook(new FoxAdditionHook(plugin));
            registerHook(new LightAntiCheatHook(plugin));
            plugin.getLogger().info("[AnticheatManager] Registered " + hooks.size() + " hooks: " + String.join(", ", hooks.keySet()));
        } catch (Exception e) {
            plugin.getLogger().severe("[AnticheatManager] Failed to register hooks: " + e.getMessage());
            e.printStackTrace();
        }

        // Try to hook into each anticheat
        plugin.getLogger().info("[AnticheatManager] Attempting to hook into detected anticheats...");
        for (AnticheatHook hook : hooks.values()) {
            try {
                plugin.getLogger().info("[AnticheatManager] Trying " + hook.getName() + "...");
                if (hook.hook()) {
                    enabledAnticheats.add(hook.getName());
                    hook.setAlertHandler(this::handleAlert);
                    plugin.getLogger().info("[AnticheatManager] Successfully hooked into " + hook.getDisplayName());

                    // Pre-register all known checks for alert manager (so they show in GUI immediately)
                    alertManager.preRegisterChecks(hook.getName());

                    // Register automod rules for each check from this anticheat (if automod is initialized)
                    if (plugin.getAutomodManager() != null) {
                        plugin.getAutomodManager().registerAnticheatRules(hook.getName(), hook.getVersion());
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("[AnticheatManager] EXCEPTION hooking " + hook.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (enabledAnticheats.isEmpty()) {
            plugin.getLogger().info("[AnticheatManager] No anticheat plugins detected.");
        } else {
            plugin.getLogger().info("[AnticheatManager] Active anticheats: " + String.join(", ", enabledAnticheats));
        }
    }

    public void shutdown() {
        for (AnticheatHook hook : hooks.values()) {
            if (hook.isEnabled()) {
                try {
                    hook.unhook();
                } catch (Exception e) {
                    plugin.logError("Failed to unhook from " + hook.getName(), e);
                }
            }
        }
        hooks.clear();
        enabledAnticheats.clear();
    }

    private void registerHook(AnticheatHook hook) {
        hooks.put(hook.getName().toLowerCase(), hook);
    }

    public AnticheatHook getHook(String name) {
        return hooks.get(name.toLowerCase());
    }

    public List<String> getEnabledAnticheats() {
        return new ArrayList<>(enabledAnticheats);
    }

    public boolean hasAnyHook() {
        return !enabledAnticheats.isEmpty();
    }

    /**
     * Register a late-hooked anticheat (e.g., when GrimAPI wasn't ready on startup).
     * Called by anticheat hooks when their delayed initialization succeeds.
     */
    public void registerLateHook(AnticheatHook hook) {
        if (!enabledAnticheats.contains(hook.getName())) {
            enabledAnticheats.add(hook.getName());
            hook.setAlertHandler(this::handleAlert);
            plugin.getLogger().info("[AnticheatManager] Late-registered " + hook.getDisplayName());

            // Pre-register all known checks for alert manager (so they show in GUI immediately)
            alertManager.preRegisterChecks(hook.getName());

            // Register automod rules for each check from this anticheat (if automod is initialized)
            if (plugin.getAutomodManager() != null) {
                plugin.getAutomodManager().registerAnticheatRules(hook.getName(), hook.getVersion());
            }
        }
    }

    /**
     * Handle an anticheat alert. This is called by anticheat hooks when a player triggers an alert.
     * Can also be called directly by external anticheats via the API.
     */
    public void handleAlert(AnticheatHook.AnticheatAlert alert) {
        Player target = alert.getPlayer();
        String anticheat = alert.getAnticheat();
        String checkName = alert.getCheckName();
        int rawVl = (int) alert.getVlLevel();

        // Increment and get cumulative VL (tracks violations over time, resets after 5 min)
        int cumulativeVl = alertManager.incrementCumulativeVL(
                target.getUniqueId(), anticheat, checkName, Math.max(1, rawVl));

        // Console logging for tracing
        plugin.getLogger().info("[AC] Processing alert: " + target.getName() + " " + anticheat + ":" + checkName + " VL:" + cumulativeVl + " (raw:" + rawVl + ")");
        plugin.logDebug("[AC] Received alert: " + target.getName() + " " + anticheat + ":" + checkName + " VL:" + cumulativeVl);

        // Process through alert manager for auto-punishments
        alertManager.processAlert(alert);

        // Pass to automod for rule processing (always, let automod decide)
        plugin.getAutomodManager().handleAnticheatAlert(
                target,
                anticheat,
                checkName,
                alert.getCheckType(),
                cumulativeVl,  // Use cumulative VL
                cumulativeVl
        );

        // Notify staff with permission (filtered by per-staff preferences and thresholds)
        notifyStaff(target, anticheat, checkName, cumulativeVl);

        // Notify watchlist if player is watched
        if (plugin.getWatchlistManager().isWatched(target.getUniqueId())) {
            plugin.getWatchlistManager().onAnticheatAlert(target, anticheat + ":" + checkName + " x" + cumulativeVl);
        }

        // Trigger replay recording
        if (plugin.getReplayManager() != null) {
            plugin.getReplayManager().onAnticheatAlert(
                    target,
                    checkName,
                    cumulativeVl
            );
        }

        // Broadcast to web panel with cumulative VL
        broadcastAlertToWebPanel(alert, cumulativeVl);
    }

    private void broadcastAlertToWebPanel(AnticheatHook.AnticheatAlert alert, int cumulativeVl) {
        plugin.getLogger().info("[AC] Broadcasting to web panel: " + alert.getPlayer().getName() + " " + alert.getCheckName() + " VL:" + cumulativeVl);
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastAnticheatAlert(
                    alert.getAnticheat(),
                    alert.getPlayer().getUniqueId(),
                    alert.getPlayer().getName(),
                    alert.getCheckName(),
                    alert.getCheckType(),
                    cumulativeVl,
                    cumulativeVl
            );
        }
    }

    /**
     * Notify staff about an anticheat alert. Can be called directly for testing.
     */
    public void notifyStaff(Player target, String anticheat, String checkName, int vl) {
        plugin.getLogger().info("[AC] notifyStaff called for " + target.getName() + " " + anticheat + ":" + checkName);

        if (!plugin.getConfigManager().getSettings().isAnticheatAlertsEnabled()) {
            plugin.getLogger().warning("[AC] Alerts disabled globally in config! Skipping alert for " + target.getName());
            plugin.logDebug("[AC] Alerts disabled globally, skipping alert for " + target.getName());
            return;
        }

        boolean isWatched = plugin.getWatchlistManager().isWatched(target.getUniqueId());
        boolean debug = plugin.getConfigManager().getSettings().isDebugMode();

        for (Player staff : plugin.getServer().getOnlinePlayers()) {
            if (!staff.hasPermission("moderex.notify.anticheat")) {
                if (debug) {
                    plugin.logDebug("[AC] " + staff.getName() + " lacks moderex.notify.anticheat permission");
                }
                continue;
            }

            // Get staff's preference for this specific check (defaults to EVERYONE with threshold=1)
            var staffSettings = plugin.getStaffSettingsManager().getSettings(staff);
            var checkPref = staffSettings.getCheckAlertPreference(anticheat, checkName);

            // Check alert level (default: EVERYONE)
            switch (checkPref.getAlertLevel()) {
                case OFF -> {
                    if (debug) {
                        plugin.logDebug("[AC] " + staff.getName() + " has " + anticheat + ":" + checkName + " set to OFF");
                    }
                    continue;
                }
                case WATCHLIST_ONLY -> {
                    if (!isWatched) {
                        if (debug) {
                            plugin.logDebug("[AC] " + staff.getName() + " has " + anticheat + ":" + checkName +
                                    " set to WATCHLIST_ONLY, but " + target.getName() + " is not watched");
                        }
                        continue;
                    }
                }
                case EVERYONE -> {} // proceed
            }

            // Track alert and check if threshold is met
            if (!alertManager.shouldSendAlertToStaff(staff.getUniqueId(), anticheat, checkName,
                    checkPref.getThresholdCount(), checkPref.getTimeWindowSeconds())) {
                if (debug) {
                    plugin.logDebug("[AC] " + staff.getName() + " threshold not met for " + anticheat + ":" + checkName +
                            " (need " + checkPref.getThresholdCount() + " in " + checkPref.getTimeWindowSeconds() + "s)");
                }
                continue;
            }

            // Always use ModereX prefix for alerts (no anticheat name shown)
            Component message = TextUtil.parse(
                    "<dark_gray>[<gradient:#ff6b6b:#ee5a5a>ModereX</gradient><dark_gray>] <white>" +
                            target.getName() + " <gray>flagged <yellow>" +
                            checkName + " <red>x" + vl
            );

            staff.sendMessage(message);

            if (debug) {
                plugin.logDebug("[AC] Sent alert to " + staff.getName() + " for " + target.getName() +
                        " " + anticheat + ":" + checkName + " x" + vl);
            }
        }
    }

    public boolean toggleAlerts(Player staff) {
        var staffSettingsManager = plugin.getStaffSettingsManager();
        var settings = staffSettingsManager.getSettings(staff);

        boolean enabled;
        if (settings.getAnticheatAlerts() == StaffSettings.AlertLevel.OFF) {
            settings.setAnticheatAlerts(StaffSettings.AlertLevel.EVERYONE);
            enabled = true;
        } else {
            settings.setAnticheatAlerts(StaffSettings.AlertLevel.OFF);
            enabled = false;
        }

        staffSettingsManager.saveSettings(settings);

        plugin.logDebug("[AC] " + staff.getName() +
                " toggled anticheat alerts to " + (enabled ? "ON" : "OFF"));

        return enabled;
    }


    public AnticheatAlertManager getAlertManager() {
        return alertManager;
    }
}
