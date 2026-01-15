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

public class PolarHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Polar";
    private Class<? extends Event> eventClass;
    private Plugin polarPlugin;

    public PolarHook(ModereX plugin) {
        super(plugin, "Polar");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        polarPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (polarPlugin == null) {
            return false;
        }

        plugin.logDebug("[Anticheat] Found Polar v" + polarPlugin.getDescription().getVersion());
        ClassLoader polarClassLoader = polarPlugin.getClass().getClassLoader();

        String[] eventClasses = {
            "top.polar.api.event.PolarViolationEvent",
            "top.polar.api.events.PolarViolationEvent",
            "top.polar.api.event.detection.DetectionEvent"
        };

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, polarClassLoader);
                plugin.logDebug("[Anticheat] Found Polar event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Polar detected but event hooking unavailable - using passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handlePolarEvent(event);
            };

            plugin.getServer().getPluginManager().registerEvent(
                    eventClass, this, EventPriority.MONITOR, executor, plugin, true
            );

            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into Polar");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook into Polar", e);
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

    private void handlePolarEvent(Event event) {
        try {
            Player player = null;
            String checkType = "Unknown";
            double violations = 1;

            for (String methodName : new String[]{"getPlayer", "player"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Player) {
                        player = (Player) result;
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String methodName : new String[]{"getCheckType", "getCheck", "getType", "checkType"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    checkType = result != null ? result.toString() : "Unknown";
                    break;
                } catch (NoSuchMethodException ignored) {}
            }

            for (String methodName : new String[]{"getViolations", "getVl", "violations"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Number) {
                        violations = ((Number) result).doubleValue();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(
                        getName(), player, checkType, "A", (int) violations, violations, "Polar detection"
                ));
            }
        } catch (Exception ignored) {}
    }
}
