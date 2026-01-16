package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
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
        plugin.getLogger().info("Initializing anticheat integrations...");

        // Load alert manager
        alertManager.load();

        // Register all anticheat hooks
        plugin.logDebug("[Anticheat] Registering anticheat hooks...");
        registerHook(new GrimHook(plugin));
        registerHook(new VulcanHook(plugin));
        registerHook(new MatrixHook(plugin));
        registerHook(new SpartanHook(plugin));
        registerHook(new NCPHook(plugin));
        registerHook(new ThemisHook(plugin));
        registerHook(new FoxAdditionHook(plugin));
        registerHook(new LightAntiCheatHook(plugin));
        plugin.logDebug("[Anticheat] Registered " + hooks.size() + " hooks: " + String.join(", ", hooks.keySet()));

        // Try to hook into each anticheat
        plugin.logDebug("[Anticheat] Attempting to hook into anticheats...");
        for (AnticheatHook hook : hooks.values()) {
            try {
                plugin.logDebug("[Anticheat] Trying to hook into " + hook.getName() + "...");
                if (hook.hook()) {
                    enabledAnticheats.add(hook.getName());
                    hook.setAlertHandler(this::handleAlert);
                    plugin.getLogger().info("Hooked into " + hook.getName());
                } else {
                    plugin.logDebug("[Anticheat] " + hook.getName() + " hook returned false (not installed or API unavailable)");
                }
            } catch (Exception e) {
                plugin.logError("Failed to hook into " + hook.getName(), e);
            }
        }

        if (enabledAnticheats.isEmpty()) {
            plugin.getLogger().info("No anticheat plugins detected.");
        } else {
            plugin.getLogger().info("Hooked into " + enabledAnticheats.size() + " anticheat(s): " +
                    String.join(", ", enabledAnticheats));
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

    private void handleAlert(AnticheatHook.AnticheatAlert alert) {
        Player target = alert.getPlayer();
        String anticheat = alert.getAnticheat();
        String checkName = alert.getCheckName();
        int vl = (int) alert.getVlLevel();

        // Process through alert manager for auto-punishments
        alertManager.processAlert(alert);

        // Check if alert should be shown (threshold check)
        if (!alertManager.shouldShowAlert(target, anticheat, checkName, vl)) {
            return;
        }

        // Pass to automod for rule processing
        plugin.getAutomodManager().handleAnticheatAlert(
                target,
                anticheat,
                checkName,
                alert.getCheckType(),
                alert.getViolations(),
                alert.getVlLevel()
        );

        // Notify staff with permission (filtered by preferences)
        notifyStaff(alert);

        // Notify watchlist if player is watched
        if (plugin.getWatchlistManager().isWatched(target.getUniqueId())) {
            plugin.getWatchlistManager().onAnticheatAlert(target, alert.toString());
        }

        // Trigger replay recording
        if (plugin.getReplayManager() != null) {
            plugin.getReplayManager().onAnticheatAlert(
                    target,
                    checkName,
                    alert.getViolations()
            );
        }

        // Broadcast to web panel
        broadcastAlertToWebPanel(alert);
    }

    private void broadcastAlertToWebPanel(AnticheatHook.AnticheatAlert alert) {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastAnticheatAlert(
                    alert.getAnticheat(),
                    alert.getPlayer().getUniqueId(),
                    alert.getPlayer().getName(),
                    alert.getCheckName(),
                    alert.getCheckType(),
                    alert.getViolations(),
                    alert.getVlLevel()
            );
        }
    }

    private void notifyStaff(AnticheatHook.AnticheatAlert alert) {
        if (!plugin.getConfigManager().getSettings().isAnticheatAlertsEnabled()) {
            return;
        }

        Player target = alert.getPlayer();
        String anticheat = alert.getAnticheat();
        String checkName = alert.getCheckName();
        int vl = (int) alert.getVlLevel();
        boolean isWatched = plugin.getWatchlistManager().isWatched(target.getUniqueId());

        for (Player staff : plugin.getServer().getOnlinePlayers()) {
            if (!staff.hasPermission("moderex.notify.anticheat")) {
                continue;
            }

            // Get staff's preference for this specific check
            var staffSettings = plugin.getStaffSettingsManager().getSettings(staff);
            var checkPref = staffSettings.getCheckAlertPreference(anticheat, checkName);

            // If not configured, skip (default is OFF/unconfigured)
            if (!checkPref.isConfigured()) {
                continue;
            }

            // Check alert level
            switch (checkPref.getAlertLevel()) {
                case OFF -> { continue; }
                case WATCHLIST_ONLY -> {
                    if (!isWatched) continue;
                }
                case EVERYONE -> {} // proceed
            }

            // Track alert and check if threshold is met
            if (!alertManager.shouldSendAlertToStaff(staff.getUniqueId(), anticheat, checkName,
                    checkPref.getThresholdCount(), checkPref.getTimeWindowSeconds())) {
                continue;
            }

            // Always use ModereX prefix for alerts
            Component message = TextUtil.parse(
                    "<dark_gray>[<gradient:#ff6b6b:#ee5a5a>ModereX</gradient><dark_gray>] <white>" +
                            target.getName() + " <gray>flagged <yellow>" +
                            checkName + " <dark_gray>(<gray>" + anticheat +
                            "<dark_gray>) <red>x" + vl
            );

            staff.sendMessage(message);
        }
    }

    public AnticheatAlertManager getAlertManager() {
        return alertManager;
    }
}
