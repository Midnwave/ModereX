package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.automod.AutomodManager;
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

    public AnticheatManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        plugin.getLogger().info("Initializing anticheat integrations...");

        // Register all anticheat hooks
        plugin.logDebug("[Anticheat] Registering anticheat hooks...");
        registerHook(new GrimHook(plugin));
        registerHook(new VulcanHook(plugin));
        registerHook(new MatrixHook(plugin));
        registerHook(new SpartanHook(plugin));
        registerHook(new IntaveHook(plugin));
        registerHook(new KarhuHook(plugin));
        registerHook(new PolarHook(plugin));
        registerHook(new NCPHook(plugin));
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
        // Pass to automod for rule processing
        plugin.getAutomodManager().handleAnticheatAlert(
                alert.getPlayer(),
                alert.getAnticheat(),
                alert.getCheckName(),
                alert.getCheckType(),
                alert.getViolations(),
                alert.getVlLevel()
        );

        // Notify staff with permission
        notifyStaff(alert);

        // Notify watchlist if player is watched
        if (plugin.getWatchlistManager().isWatched(alert.getPlayer().getUniqueId())) {
            plugin.getWatchlistManager().onAnticheatAlert(alert.getPlayer(), alert.toString());
        }

        // Trigger replay recording
        if (plugin.getReplayManager() != null) {
            plugin.getReplayManager().onAnticheatAlert(
                    alert.getPlayer(),
                    alert.getCheckName(),
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

        Component message = TextUtil.parse(
                "<dark_gray>[<red>" + alert.getAnticheat() + "<dark_gray>] <white>" +
                        alert.getPlayer().getName() + " <gray>failed <yellow>" +
                        alert.getCheckName() + " <gray>(<white>" + alert.getCheckType() +
                        "<gray>) <red>VL: " + String.format("%.1f", alert.getVlLevel())
        );

        for (Player staff : plugin.getServer().getOnlinePlayers()) {
            if (staff.hasPermission("moderex.notify.anticheat")) {
                staff.sendMessage(message);
            }
        }
    }
}
