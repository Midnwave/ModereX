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

public class MatrixHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Matrix";
    private Class<? extends Event> eventClass;
    private Plugin matrixPlugin;
    private final Set<String> discoveredChecks = ConcurrentHashMap.newKeySet();

    public MatrixHook(ModereX plugin) {
        super(plugin, "Matrix");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        matrixPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (matrixPlugin == null) {
            return false;
        }

        plugin.logDebug("[Anticheat] Found Matrix v" + matrixPlugin.getDescription().getVersion());
        ClassLoader matrixClassLoader = matrixPlugin.getClass().getClassLoader();

        String[] eventClasses = {
            "me.rerere.matrix.api.events.PlayerViolationEvent",
            "me.rerere.matrix.api.event.PlayerViolationEvent",
            "me.rerere.matrix.events.PlayerViolationEvent"
        };

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, matrixClassLoader);
                plugin.logDebug("[Anticheat] Found Matrix event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Matrix detected but event hooking unavailable - using passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleMatrixEvent(event);
            };

            // Use HIGHEST priority to run before Matrix sends messages and allow cancellation
            plugin.getServer().getPluginManager().registerEvent(
                    eventClass, this, EventPriority.HIGHEST, executor, plugin, true
            );

            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into Matrix");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook into Matrix", e);
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

    private void handleMatrixEvent(Event event) {
        try {
            plugin.logDebug("[Matrix] Received event: " + event.getClass().getName());

            Player player = null;
            String hackType = "Unknown";
            int violations = 1;

            for (String methodName : new String[]{"getPlayer", "player"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Player) {
                        player = (Player) result;
                        plugin.logDebug("[Matrix] Got player via " + methodName + "(): " + player.getName());
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String methodName : new String[]{"getHackType", "getCheckType", "getType", "hackType"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    hackType = result != null ? result.toString() : "Unknown";
                    break;
                } catch (NoSuchMethodException ignored) {}
            }

            plugin.logDebug("[Matrix] Hack type: " + hackType);

            // Track discovered checks
            if (!discoveredChecks.contains(hackType)) {
                discoveredChecks.add(hackType);
                plugin.logDebug("[Matrix] Discovered new check type: " + hackType);
            }

            for (String methodName : new String[]{"getViolations", "getVl", "violations"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Number) {
                        violations = ((Number) result).intValue();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            plugin.logDebug("[Matrix] Violations: " + violations);

            // Cancel the event to prevent Matrix from sending its own alert messages
            try {
                Method setCancelled = event.getClass().getMethod("setCancelled", boolean.class);
                setCancelled.invoke(event, true);
                plugin.logDebug("[Matrix] Cancelled event to block Matrix's native message");
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[Matrix] setCancelled method not found - Matrix messages may still show");
            } catch (Exception e) {
                plugin.logDebug("[Matrix] Failed to cancel event: " + e.getMessage());
            }

            if (player != null) {
                plugin.logDebug("[Matrix] Creating alert for " + player.getName() + " - " + hackType + " x" + violations);
                handleAlert(new AnticheatAlert(
                        getName(), player, hackType, "A", violations, violations, "Matrix detection"
                ));
            } else {
                plugin.logDebug("[Matrix] Could not determine player for alert");
            }
        } catch (Exception e) {
            plugin.logDebug("[Matrix] Error handling event: " + e.getMessage());
        }
    }

    public Set<String> getDiscoveredChecks() {
        return discoveredChecks;
    }
}
