package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class GrimHook extends AnticheatHook implements Listener {
    private static final String[] PLUGIN_NAMES = {"Grim", "GrimAC"};
    private Class<? extends Event> eventClass;

    public GrimHook(ModereX plugin) {
        super(plugin, "Grim");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        boolean found = false;
        for (String name : PLUGIN_NAMES) {
            if (isPluginAvailable(name)) {
                plugin.logDebug("[Anticheat] Found Grim plugin registered as: " + name);
                found = true;
                break;
            }
        }

        if (!found) {
            plugin.logDebug("[Anticheat] Grim plugin not found (checked: " + String.join(", ", PLUGIN_NAMES) + ")");
            return false;
        }

        try {
            String[] eventClasses = {
                "ac.grim.grimac.api.events.FlagEvent",
                "ac.grim.grimac.events.FlagEvent",
                "com.github.grimanticheat.grimapi.events.FlagEvent"
            };

            for (String className : eventClasses) {
                try {
                    eventClass = (Class<? extends Event>) Class.forName(className);
                    plugin.logDebug("[Anticheat] Found Grim event class: " + className);
                    break;
                } catch (ClassNotFoundException ignored) {
                    plugin.logDebug("[Anticheat] Grim event class not found: " + className);
                }
            }

            if (eventClass == null) {
                plugin.logDebug("[Anticheat] No Grim event class found, trying alternative hook method");
                return tryAlternativeHook();
            }
            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleGrimEvent(event);
            };

            plugin.getServer().getPluginManager().registerEvent(
                    eventClass,
                    this,
                    EventPriority.MONITOR,
                    executor,
                    plugin,
                    true
            );

            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into Grim via FlagEvent");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook into GrimAC", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean tryAlternativeHook() {
        String[] alternativeClasses = {
            "ac.grim.grimac.api.events.ViolationEvent",
            "ac.grim.grimac.events.packets.PacketEvent",
            "ac.grim.grimac.api.events.CommandExecuteEvent"
        };

        for (String className : alternativeClasses) {
            try {
                Class<? extends Event> altEventClass = (Class<? extends Event>) Class.forName(className);
                plugin.logDebug("[Anticheat] Found alternative Grim event class: " + className);

                EventExecutor executor = (listener, event) -> {
                    if (!enabled) return;
                    if (!altEventClass.isInstance(event)) return;
                    handleAlternativeGrimEvent(event);
                };

                plugin.getServer().getPluginManager().registerEvent(
                        altEventClass,
                        this,
                        EventPriority.MONITOR,
                        executor,
                        plugin,
                        true
                );

                enabled = true;
                plugin.logDebug("[Anticheat] Successfully hooked into Grim via alternative event: " + className);
                return true;
            } catch (ClassNotFoundException ignored) {
            } catch (Exception e) {
                plugin.logDebug("[Anticheat] Failed to hook alternative Grim event " + className + ": " + e.getMessage());
            }
        }

        plugin.logDebug("[Anticheat] All Grim hook methods failed");
        return false;
    }

    @Override
    public void unhook() {
        if (enabled) {
            HandlerList.unregisterAll(this);
            enabled = false;
        }
    }

    private void handleAlternativeGrimEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            int violations = 1;
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
            for (String methodName : new String[]{"getCheckName", "getCheck", "checkName", "check"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof String) {
                        checkName = (String) result;
                        break;
                    } else if (result != null) {
                        checkName = result.toString();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }
            for (String methodName : new String[]{"getViolations", "getVl", "violations", "vl"}) {
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
                        getName(),
                        player,
                        checkName,
                        "A",
                        violations,
                        violations,
                        "Grim detection (alt)"
                ));
            }
        } catch (Exception e) {
        }
    }

    private void handleGrimEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getCheckMethod = event.getClass().getMethod("getCheck");
            Method getViolationsMethod = event.getClass().getMethod("getViolations");

            Player player = (Player) getPlayerMethod.invoke(event);
            Object check = getCheckMethod.invoke(event);
            int violations = (int) getViolationsMethod.invoke(event);

            String checkName = "Unknown";
            try {
                Method getCheckNameMethod = check.getClass().getMethod("getCheckName");
                checkName = (String) getCheckNameMethod.invoke(check);
            } catch (Exception ignored) {
                checkName = check.getClass().getSimpleName();
            }

            handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    checkName,
                    "A",
                    violations,
                    violations,
                    "Grim detection"
            ));
        } catch (Exception e) {
        }
    }
}
