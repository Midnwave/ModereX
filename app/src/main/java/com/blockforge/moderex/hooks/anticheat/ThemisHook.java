package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hook for Themis Anticheat by olexorus
 * API: com.gmail.olexorus.themis.api.ViolationEvent
 *
 * ViolationEvent provides:
 * - getPlayer() -> Player
 * - getType() -> CheckType enum (has getCheckName(), getDescription())
 * - getSeverity() -> double (violation level)
 * - getDragback() -> boolean
 */
public class ThemisHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Themis";
    private Class<? extends Event> eventClass;
    private Plugin themisPlugin;
    private final Set<String> discoveredChecks = ConcurrentHashMap.newKeySet();

    public ThemisHook(ModereX plugin) {
        super(plugin, "Themis");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        themisPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (themisPlugin == null) return false;

        plugin.logDebug("[Anticheat] Found Themis v" + themisPlugin.getDescription().getVersion());
        ClassLoader cl = themisPlugin.getClass().getClassLoader();

        // Themis API class paths (current version first)
        String[] classes = {
            "com.gmail.olexorus.themis.api.ViolationEvent",  // Current Themis 0.17.x+
            "com.jedk1.themis.api.ViolationEvent",           // Legacy versions
            "com.jedk1.themis.api.event.ViolationEvent"
        };

        for (String className : classes) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, cl);
                plugin.logDebug("[Anticheat] Found Themis event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Themis detected - passive mode (no API event found)");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleThemisEvent(event);
            };
            // Use HIGHEST priority to run before Themis sends messages and allow cancellation
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.HIGHEST, executor, plugin, true);
            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into Themis API");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook Themis", e);
            return false;
        }
    }

    @Override
    public void unhook() {
        if (enabled) {
            HandlerList.unregisterAll(this);
            enabled = false;
        }
    }

    private void handleThemisEvent(Event event) {
        try {
            plugin.logDebug("[Themis] Received event: " + event.getClass().getName());

            Player player = null;
            String checkName = "Unknown";
            double severity = 1;

            // Get player - ViolationEvent.getPlayer() returns Player
            try {
                Method getPlayer = event.getClass().getMethod("getPlayer");
                Object result = getPlayer.invoke(event);
                if (result instanceof Player) {
                    player = (Player) result;
                    plugin.logDebug("[Themis] Got player: " + player.getName());
                }
            } catch (NoSuchMethodException ignored) {}

            // Get check type - ViolationEvent.getType() returns CheckType enum
            // CheckType has getCheckName() method
            try {
                Method getType = event.getClass().getMethod("getType");
                Object checkType = getType.invoke(event);
                if (checkType != null) {
                    // Try to get the check name from CheckType enum
                    try {
                        Method getCheckName = checkType.getClass().getMethod("getCheckName");
                        Object name = getCheckName.invoke(checkType);
                        if (name instanceof String) {
                            checkName = (String) name;
                        }
                    } catch (NoSuchMethodException e) {
                        // Fallback to enum name or toString
                        checkName = checkType.toString();
                    }
                }
            } catch (NoSuchMethodException ignored) {}

            plugin.logDebug("[Themis] Check name: " + checkName);

            // Track discovered checks
            if (!discoveredChecks.contains(checkName)) {
                discoveredChecks.add(checkName);
                plugin.logDebug("[Themis] Discovered new check type: " + checkName);
            }

            // Get severity - ViolationEvent.getSeverity() returns double
            try {
                Method getSeverity = event.getClass().getMethod("getSeverity");
                Object result = getSeverity.invoke(event);
                if (result instanceof Number) {
                    severity = ((Number) result).doubleValue();
                }
            } catch (NoSuchMethodException ignored) {}

            plugin.logDebug("[Themis] Severity: " + severity);

            // Cancel the event to prevent Themis from sending its own alert messages
            try {
                Method setCancelled = event.getClass().getMethod("setCancelled", boolean.class);
                setCancelled.invoke(event, true);
                plugin.logDebug("[Themis] Cancelled event to block Themis's native message");
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[Themis] setCancelled method not found - Themis messages may still show");
            } catch (Exception e) {
                plugin.logDebug("[Themis] Failed to cancel event: " + e.getMessage());
            }

            if (player != null) {
                plugin.logDebug("[Themis] Creating alert for " + player.getName() + " - " + checkName + " x" + (int) Math.ceil(severity));
                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    checkName,
                    "A",
                    (int) Math.ceil(severity),
                    severity,
                    "Themis detection"
                ));
            } else {
                plugin.logDebug("[Themis] Could not determine player for alert");
            }
        } catch (Exception e) {
            plugin.logDebug("[Themis] Error handling event: " + e.getMessage());
        }
    }

    public Set<String> getDiscoveredChecks() {
        return discoveredChecks;
    }
}
