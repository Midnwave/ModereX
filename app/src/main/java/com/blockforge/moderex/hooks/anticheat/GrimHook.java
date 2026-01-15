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

public class GrimHook extends AnticheatHook implements Listener {
    private static final String[] PLUGIN_NAMES = {"Grim", "GrimAC"};
    private Class<? extends Event> eventClass;
    private Plugin grimPlugin;

    public GrimHook(ModereX plugin) {
        super(plugin, "Grim");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        // Find Grim plugin
        for (String name : PLUGIN_NAMES) {
            grimPlugin = Bukkit.getPluginManager().getPlugin(name);
            if (grimPlugin != null) {
                plugin.logDebug("[Anticheat] Found Grim plugin: " + name + " v" + grimPlugin.getDescription().getVersion());
                break;
            }
        }

        if (grimPlugin == null) {
            return false;
        }

        // Try to find event class from Grim's classloader
        String[] eventClasses = {
            // GrimAC 2.3.x+
            "ac.grim.grimac.events.FlagEvent",
            "ac.grim.grimac.api.events.FlagEvent",
            // Older versions
            "ac.grim.grimac.events.packets.PacketPlayerAbilities",
            // GrimAPI
            "com.github.grimanticheat.grimapi.events.FlagEvent",
            // Alternative event names
            "ac.grim.grimac.events.CompletePredictionEvent"
        };

        ClassLoader grimClassLoader = grimPlugin.getClass().getClassLoader();

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, grimClassLoader);
                plugin.logDebug("[Anticheat] Found Grim event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {
            } catch (Exception e) {
                plugin.logDebug("[Anticheat] Error loading " + className + ": " + e.getMessage());
            }
        }

        if (eventClass != null) {
            try {
                EventExecutor executor = (listener, event) -> {
                    if (!enabled) return;
                    if (!eventClass.isInstance(event)) return;
                    handleGrimEvent(event);
                };

                plugin.getServer().getPluginManager().registerEvent(
                        eventClass, this, EventPriority.MONITOR, executor, plugin, true
                );

                enabled = true;
                plugin.logDebug("[Anticheat] Successfully hooked into Grim via " + eventClass.getSimpleName());
                return true;
            } catch (Exception e) {
                plugin.logDebug("[Anticheat] Failed to register Grim event: " + e.getMessage());
            }
        }

        // Fallback: Listen to all Grim events by scanning
        try {
            return tryReflectiveHook(grimClassLoader);
        } catch (Exception e) {
            plugin.logDebug("[Anticheat] Reflective hook failed: " + e.getMessage());
        }

        // Last resort: just mark as enabled since Grim is present
        // Alerts will come through chat/console parsing if needed
        enabled = true;
        plugin.log("Grim detected but event hooking unavailable - using passive mode");
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean tryReflectiveHook(ClassLoader classLoader) {
        // Try to find any Event class in Grim's packages
        String[] packages = {
            "ac.grim.grimac.events",
            "ac.grim.grimac.api.events",
            "ac.grim.grimac.checks"
        };

        for (String pkg : packages) {
            try {
                // Try common event naming patterns
                String[] suffixes = {"FlagEvent", "ViolationEvent", "AlertEvent", "CheckEvent"};
                for (String suffix : suffixes) {
                    try {
                        String className = pkg + "." + suffix;
                        Class<?> clazz = Class.forName(className, true, classLoader);
                        if (Event.class.isAssignableFrom(clazz)) {
                            eventClass = (Class<? extends Event>) clazz;

                            EventExecutor executor = (listener, event) -> {
                                if (!enabled) return;
                                handleGrimEvent(event);
                            };

                            plugin.getServer().getPluginManager().registerEvent(
                                    eventClass, this, EventPriority.MONITOR, executor, plugin, true
                            );

                            enabled = true;
                            plugin.logDebug("[Anticheat] Hooked Grim via reflective scan: " + className);
                            return true;
                        }
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    @Override
    public void unhook() {
        if (enabled) {
            HandlerList.unregisterAll(this);
            enabled = false;
        }
    }

    private void handleGrimEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            int violations = 1;
            String details = "";

            // Try to get player
            for (String methodName : new String[]{"getPlayer", "getUser", "player", "getGrimPlayer"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Player) {
                        player = (Player) result;
                        break;
                    } else if (result != null) {
                        // GrimPlayer object - try to get bukkit player from it
                        try {
                            Method getBukkitPlayer = result.getClass().getMethod("getBukkitPlayer");
                            Object bp = getBukkitPlayer.invoke(result);
                            if (bp instanceof Player) {
                                player = (Player) bp;
                                break;
                            }
                        } catch (Exception ignored) {}
                        try {
                            Method getPlayer = result.getClass().getMethod("getPlayer");
                            Object p = getPlayer.invoke(result);
                            if (p instanceof Player) {
                                player = (Player) p;
                                break;
                            }
                        } catch (Exception ignored) {}
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }

            // Try to get check name
            for (String methodName : new String[]{"getCheckName", "getCheck", "checkName", "check", "getCheckType"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof String) {
                        checkName = (String) result;
                        break;
                    } else if (result != null) {
                        // Check object - get its name
                        try {
                            Method getName = result.getClass().getMethod("getName");
                            checkName = (String) getName.invoke(result);
                            break;
                        } catch (Exception ignored) {
                            try {
                                Method getCheckName = result.getClass().getMethod("getCheckName");
                                checkName = (String) getCheckName.invoke(result);
                                break;
                            } catch (Exception ignored2) {
                                checkName = result.getClass().getSimpleName();
                                break;
                            }
                        }
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }

            // Try to get violations
            for (String methodName : new String[]{"getViolations", "getVl", "violations", "vl", "getViolation"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof Number) {
                        violations = ((Number) result).intValue();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }

            // Try to get details/verbose
            for (String methodName : new String[]{"getVerbose", "getMessage", "getDetails", "verbose"}) {
                try {
                    Method method = event.getClass().getMethod(methodName);
                    Object result = method.invoke(event);
                    if (result instanceof String) {
                        details = (String) result;
                        break;
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(
                        getName(),
                        player,
                        checkName,
                        "A",
                        violations,
                        violations,
                        details.isEmpty() ? "Grim detection" : details
                ));
            }
        } catch (Exception e) {
            // Silent fail
        }
    }
}
