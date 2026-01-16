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
 * Hook for GrimAC Anticheat
 * API: ac.grim.grimac.api.events.FlagEvent
 *
 * FlagEvent provides:
 * - getPlayer() -> GrimUser (has getBukkitPlayer(), getName(), getUniqueId())
 * - getCheck() -> AbstractCheck (has getCheckName(), getViolations())
 * - getVerbose() -> String (detailed info about the flag)
 * - isCancelled() / setCancelled(boolean)
 */
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

        ClassLoader grimClassLoader = grimPlugin.getClass().getClassLoader();

        // GrimAPI event classes (current versions first)
        String[] eventClasses = {
            "ac.grim.grimac.api.events.FlagEvent",      // GrimAPI 2.3.x+
            "ac.grim.grimac.events.FlagEvent",          // Older direct package
            "ac.grim.grimac.api.events.CheckFlagEvent", // Alternative name
        };

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
                plugin.logDebug("[Anticheat] Successfully hooked into Grim API via " + eventClass.getSimpleName());
                return true;
            } catch (Exception e) {
                plugin.logDebug("[Anticheat] Failed to register Grim event: " + e.getMessage());
            }
        }

        // Mark as enabled in passive mode if plugin is present
        enabled = true;
        plugin.getLogger().info("Grim detected - passive mode (no API event found)");
        return true;
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
            String verbose = "";

            // Get player - FlagEvent.getPlayer() returns GrimUser
            // GrimUser has getBukkitPlayer() method
            try {
                Method getPlayer = event.getClass().getMethod("getPlayer");
                Object grimUser = getPlayer.invoke(event);
                if (grimUser != null) {
                    // Try getBukkitPlayer() first
                    try {
                        Method getBukkitPlayer = grimUser.getClass().getMethod("getBukkitPlayer");
                        Object bp = getBukkitPlayer.invoke(grimUser);
                        if (bp instanceof Player) {
                            player = (Player) bp;
                        }
                    } catch (NoSuchMethodException e) {
                        // Try getPlayer() on GrimUser
                        try {
                            Method getPlayerFromUser = grimUser.getClass().getMethod("getPlayer");
                            Object p = getPlayerFromUser.invoke(grimUser);
                            if (p instanceof Player) {
                                player = (Player) p;
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }

                    // If still no player but we have GrimUser, try getName() for lookup
                    if (player == null) {
                        try {
                            Method getName = grimUser.getClass().getMethod("getName");
                            String playerName = (String) getName.invoke(grimUser);
                            player = Bukkit.getPlayer(playerName);
                        } catch (NoSuchMethodException ignored) {}
                    }
                }
            } catch (NoSuchMethodException ignored) {}

            // Get check - FlagEvent.getCheck() returns AbstractCheck
            // AbstractCheck has getCheckName() and getViolations()
            try {
                Method getCheck = event.getClass().getMethod("getCheck");
                Object check = getCheck.invoke(event);
                if (check != null) {
                    // Get check name
                    try {
                        Method getCheckName = check.getClass().getMethod("getCheckName");
                        Object name = getCheckName.invoke(check);
                        if (name instanceof String) {
                            checkName = (String) name;
                        }
                    } catch (NoSuchMethodException e) {
                        // Fallback to class name
                        checkName = check.getClass().getSimpleName();
                    }

                    // Get violations from check
                    try {
                        Method getViolations = check.getClass().getMethod("getViolations");
                        Object vl = getViolations.invoke(check);
                        if (vl instanceof Number) {
                            violations = ((Number) vl).intValue();
                        }
                    } catch (NoSuchMethodException e) {
                        // Try getVl
                        try {
                            Method getVl = check.getClass().getMethod("getVl");
                            Object vl = getVl.invoke(check);
                            if (vl instanceof Number) {
                                violations = ((Number) vl).intValue();
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }
                }
            } catch (NoSuchMethodException ignored) {}

            // Get verbose info - FlagEvent.getVerbose() returns String
            try {
                Method getVerbose = event.getClass().getMethod("getVerbose");
                Object result = getVerbose.invoke(event);
                if (result instanceof String) {
                    verbose = (String) result;
                }
            } catch (NoSuchMethodException ignored) {}

            if (player != null) {
                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    checkName,
                    "A",
                    violations,
                    violations,
                    verbose.isEmpty() ? "Grim detection" : verbose
                ));
            }
        } catch (Exception e) {
            plugin.logDebug("[Anticheat] Error handling Grim event: " + e.getMessage());
        }
    }
}
