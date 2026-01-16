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

/**
 * Hook for LightAntiCheat by Vekster
 * API: me.vekster.lightanticheat.api.event.LACViolationEvent
 *
 * LACViolationEvent provides:
 * - getPlayer() -> Player
 * - getCheckType() -> CheckType enum (has getName())
 * - getCheckSubType() -> String (sub-type like "A", "B")
 * - getBuffer() -> double (violation buffer/level)
 * - isCancelled() / setCancelled(boolean)
 */
public class LightAntiCheatHook extends AnticheatHook implements Listener {

    private static final String[] PLUGIN_NAMES = {"LightAntiCheat", "LAC", "PGLAC"};
    private Class<? extends Event> eventClass;
    private Plugin lacPlugin;

    public LightAntiCheatHook(ModereX plugin) {
        super(plugin, "LightAC");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        for (String name : PLUGIN_NAMES) {
            lacPlugin = Bukkit.getPluginManager().getPlugin(name);
            if (lacPlugin != null) {
                plugin.logDebug("[Anticheat] Found LightAntiCheat plugin: " + name + " v" + lacPlugin.getDescription().getVersion());
                break;
            }
        }

        if (lacPlugin == null) {
            return false;
        }

        // LightAntiCheat API class paths (current version first)
        String[] eventClasses = {
            "me.vekster.lightanticheat.api.event.LACViolationEvent",  // Current LAC
            "me.vekster.lightanticheat.api.event.LACFlagEvent",       // Alternative name
            "me.pagofr.pglac.api.event.LACViolationEvent",            // PGLAC fork
            "me.pagofr.pglac.api.event.FlagEvent"
        };

        ClassLoader classLoader = lacPlugin.getClass().getClassLoader();

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, classLoader);
                plugin.logDebug("[Anticheat] Found LAC event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("LightAntiCheat detected - passive mode (no API event found)");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleLACEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into LightAntiCheat API");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook LightAntiCheat", e);
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

    private void handleLACEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            String subType = "";
            double buffer = 1;

            // Get player - LACViolationEvent.getPlayer() returns Player
            try {
                Method getPlayer = event.getClass().getMethod("getPlayer");
                Object result = getPlayer.invoke(event);
                if (result instanceof Player) {
                    player = (Player) result;
                }
            } catch (NoSuchMethodException ignored) {}

            // Get check type - LACViolationEvent.getCheckType() returns CheckType enum
            // CheckType has getName() method
            try {
                Method getCheckType = event.getClass().getMethod("getCheckType");
                Object checkType = getCheckType.invoke(event);
                if (checkType != null) {
                    // Try to get the name from CheckType enum
                    try {
                        Method getName = checkType.getClass().getMethod("getName");
                        Object name = getName.invoke(checkType);
                        if (name instanceof String) {
                            checkName = (String) name;
                        }
                    } catch (NoSuchMethodException e) {
                        // Fallback to enum name
                        checkName = checkType.toString();
                    }
                }
            } catch (NoSuchMethodException ignored) {
                // Try alternative method names
                for (String methodName : new String[]{"getCheck", "getCheckName"}) {
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
                    } catch (NoSuchMethodException ignored2) {}
                }
            }

            // Get sub-type - LACViolationEvent.getCheckSubType() returns String (like "A", "B")
            try {
                Method getSubType = event.getClass().getMethod("getCheckSubType");
                Object result = getSubType.invoke(event);
                if (result instanceof String && !((String) result).isEmpty()) {
                    subType = (String) result;
                }
            } catch (NoSuchMethodException ignored) {}

            // Get buffer/VL - LACViolationEvent.getBuffer() returns double
            try {
                Method getBuffer = event.getClass().getMethod("getBuffer");
                Object result = getBuffer.invoke(event);
                if (result instanceof Number) {
                    buffer = ((Number) result).doubleValue();
                }
            } catch (NoSuchMethodException ignored) {
                // Try alternative method names
                for (String methodName : new String[]{"getVL", "getViolations", "getVl"}) {
                    try {
                        Method method = event.getClass().getMethod(methodName);
                        Object result = method.invoke(event);
                        if (result instanceof Number) {
                            buffer = ((Number) result).doubleValue();
                            break;
                        }
                    } catch (NoSuchMethodException ignored2) {}
                }
            }

            if (player != null) {
                // Format check name with sub-type if available (e.g., "Speed A", "Fly B")
                String fullCheckName = subType.isEmpty() ? checkName : checkName + " " + subType;

                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    fullCheckName,
                    subType.isEmpty() ? "A" : subType,
                    (int) Math.ceil(buffer),
                    buffer,
                    "LightAC detection"
                ));
            }
        } catch (Exception e) {
            plugin.logDebug("[Anticheat] Error handling LightAC event: " + e.getMessage());
        }
    }
}
