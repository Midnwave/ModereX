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

public class SpartanHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Spartan";
    private Class<? extends Event> eventClass;
    private Plugin spartanPlugin;

    public SpartanHook(ModereX plugin) {
        super(plugin, "Spartan");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        spartanPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (spartanPlugin == null) {
            return false;
        }

        plugin.logDebug("[Anticheat] Found Spartan v" + spartanPlugin.getDescription().getVersion());
        ClassLoader spartanClassLoader = spartanPlugin.getClass().getClassLoader();

        String[] eventClasses = {
            "me.vagdedes.spartan.api.PlayerViolationEvent",
            "me.vagdedes.spartan.api.event.PlayerViolationEvent",
            "me.vagdedes.spartan.api.events.PlayerViolationEvent"
        };

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, spartanClassLoader);
                plugin.logDebug("[Anticheat] Found Spartan event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Spartan detected but event hooking unavailable - using passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleSpartanEvent(event);
            };

            plugin.getServer().getPluginManager().registerEvent(
                    eventClass, this, EventPriority.MONITOR, executor, plugin, true
            );

            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into Spartan");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook into Spartan", e);
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

    private void handleSpartanEvent(Event event) {
        try {
            Player player = null;
            String hackType = "Unknown";
            int violation = 1;

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

            for (String methodName : new String[]{"getViolation", "getViolations", "getVl", "violation"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Number) {
                        violation = ((Number) result).intValue();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(
                        getName(), player, hackType, "A", violation, violation, "Spartan detection"
                ));
            }
        } catch (Exception ignored) {}
    }
}
