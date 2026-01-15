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

public class MatrixHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Matrix";
    private Class<? extends Event> eventClass;
    private Plugin matrixPlugin;

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

            plugin.getServer().getPluginManager().registerEvent(
                    eventClass, this, EventPriority.MONITOR, executor, plugin, true
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
            Player player = null;
            String hackType = "Unknown";
            int violations = 1;

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

            for (String methodName : new String[]{"getHackType", "getCheckType", "getType", "hackType"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    hackType = result != null ? result.toString() : "Unknown";
                    break;
                } catch (NoSuchMethodException ignored) {}
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

            if (player != null) {
                handleAlert(new AnticheatAlert(
                        getName(), player, hackType, "A", violations, violations, "Matrix detection"
                ));
            }
        } catch (Exception ignored) {}
    }
}
