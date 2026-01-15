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

public class VulcanHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Vulcan";
    private Class<? extends Event> eventClass;
    private Plugin vulcanPlugin;

    public VulcanHook(ModereX plugin) {
        super(plugin, "Vulcan");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        vulcanPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (vulcanPlugin == null) {
            return false;
        }

        plugin.logDebug("[Anticheat] Found Vulcan v" + vulcanPlugin.getDescription().getVersion());
        ClassLoader vulcanClassLoader = vulcanPlugin.getClass().getClassLoader();

        String[] eventClasses = {
            "me.frep.vulcan.api.event.VulcanFlagEvent",
            "me.frep.vulcan.api.event.VulcanViolationEvent",
            "me.frep.vulcan.spigot.api.event.VulcanFlagEvent",
            "me.frep.vulcan.api.events.FlagEvent"
        };

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, vulcanClassLoader);
                plugin.logDebug("[Anticheat] Found Vulcan event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (eventClass == null) {
            plugin.logDebug("[Anticheat] No Vulcan event class found");
            enabled = true;
            plugin.getLogger().info("Vulcan detected but event hooking unavailable - using passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleVulcanEvent(event);
            };

            plugin.getServer().getPluginManager().registerEvent(
                    eventClass, this, EventPriority.MONITOR, executor, plugin, true
            );

            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into Vulcan");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook into Vulcan", e);
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

    private void handleVulcanEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            String checkType = "A";
            int vl = 1;

            // Get player
            for (String methodName : new String[]{"getPlayer", "getUser", "player"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Player) {
                        player = (Player) result;
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            // Get check
            for (String methodName : new String[]{"getCheck", "getCheckName", "check"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof String) {
                        checkName = (String) result;
                        break;
                    } else if (result != null) {
                        try {
                            Method getName = result.getClass().getMethod("getName");
                            checkName = (String) getName.invoke(result);
                        } catch (Exception e) {
                            checkName = result.getClass().getSimpleName();
                        }
                        try {
                            Method getType = result.getClass().getMethod("getType");
                            Object type = getType.invoke(result);
                            checkType = type != null ? type.toString() : "A";
                        } catch (Exception ignored) {}
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            // Get VL
            for (String methodName : new String[]{"getVl", "getVL", "getViolations", "vl"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Number) {
                        vl = ((Number) result).intValue();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(
                        getName(), player, checkName, checkType, vl, vl, "Vulcan detection"
                ));
            }
        } catch (Exception ignored) {}
    }
}
