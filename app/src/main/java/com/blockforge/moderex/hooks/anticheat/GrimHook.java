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
 * Hook for GrimAC using their FlagEvent API
 * FlagEvent gives us player, check name, violations, and verbose info
 */
public class GrimHook extends AnticheatHook implements Listener {

    private static final String[] PLUGIN_NAMES = {"Grim", "GrimAC"};
    private Class<? extends Event> eventClass;
    private Plugin grimPlugin;

    // keeps track of what checks we've seen for the web panel
    private final Set<String> discoveredChecks = ConcurrentHashMap.newKeySet();

    public GrimHook(ModereX plugin) {
        super(plugin, "Grim");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        // look for Grim plugin under any of its names
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

        // try different event class names, newer ones first
        String[] eventClasses = {
            "ac.grim.grimac.api.events.FlagEvent",
            "ac.grim.grimac.events.FlagEvent",
            "ac.grim.grimac.api.events.CheckFlagEvent",
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

                // run first so we can cancel the event before Grim broadcasts it
                plugin.getServer().getPluginManager().registerEvent(
                    eventClass, this, EventPriority.HIGHEST, executor, plugin, true
                );

                enabled = true;
                plugin.logDebug("[Anticheat] Successfully hooked into Grim API via " + eventClass.getSimpleName());
                return true;
            } catch (Exception e) {
                plugin.logDebug("[Anticheat] Failed to register Grim event: " + e.getMessage());
            }
        }

        // still mark it as enabled even if we couldn't hook the API
        enabled = true;
        plugin.getLogger().info("Grim detected but API not available, running in passive mode");
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
            plugin.logDebug("[Grim] Received FlagEvent: " + event.getClass().getName());

            Player player = null;
            String checkName = "Unknown";
            int violations = 1;
            String verbose = "";

            // getPlayer() returns a GrimUser object, we need to extract the actual Player from it
            try {
                Method getPlayer = event.getClass().getMethod("getPlayer");
                Object grimUser = getPlayer.invoke(event);
                plugin.logDebug("[Grim] Got GrimUser: " + (grimUser != null ? grimUser.getClass().getName() : "null"));

                if (grimUser != null) {
                    // try getBukkitPlayer() first
                    try {
                        Method getBukkitPlayer = grimUser.getClass().getMethod("getBukkitPlayer");
                        Object bp = getBukkitPlayer.invoke(grimUser);
                        if (bp instanceof Player) {
                            player = (Player) bp;
                            plugin.logDebug("[Grim] Got player via getBukkitPlayer(): " + player.getName());
                        }
                    } catch (NoSuchMethodException e) {
                        // maybe GrimUser has getPlayer() instead
                        try {
                            Method getPlayerFromUser = grimUser.getClass().getMethod("getPlayer");
                            Object p = getPlayerFromUser.invoke(grimUser);
                            if (p instanceof Player) {
                                player = (Player) p;
                                plugin.logDebug("[Grim] Got player via getPlayer(): " + player.getName());
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }

                    // last resort, look up by name
                    if (player == null) {
                        try {
                            Method getName = grimUser.getClass().getMethod("getName");
                            String playerName = (String) getName.invoke(grimUser);
                            player = Bukkit.getPlayer(playerName);
                            if (player != null) {
                                plugin.logDebug("[Grim] Got player via getName() lookup: " + player.getName());
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }
                }
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[Grim] No getPlayer() method found on event");
            }

            // get the check object which has the name and violation count
            try {
                Method getCheck = event.getClass().getMethod("getCheck");
                Object check = getCheck.invoke(event);
                plugin.logDebug("[Grim] Got check: " + (check != null ? check.getClass().getName() : "null"));

                if (check != null) {
                    // grab the check name
                    try {
                        Method getCheckName = check.getClass().getMethod("getCheckName");
                        Object name = getCheckName.invoke(check);
                        if (name instanceof String) {
                            checkName = (String) name;
                        }
                    } catch (NoSuchMethodException e) {
                        // use class name if getCheckName doesn't exist
                        checkName = check.getClass().getSimpleName();
                    }

                    plugin.logDebug("[Grim] Check name: " + checkName);

                    // keep track of new check types for the web panel
                    if (!discoveredChecks.contains(checkName)) {
                        discoveredChecks.add(checkName);
                        plugin.logDebug("[Grim] Discovered new check type: " + checkName);
                    }

                    // get violation count
                    try {
                        Method getViolations = check.getClass().getMethod("getViolations");
                        Object vl = getViolations.invoke(check);
                        if (vl instanceof Number) {
                            violations = ((Number) vl).intValue();
                        }
                    } catch (NoSuchMethodException e) {
                        // maybe it's called getVl instead
                        try {
                            Method getVl = check.getClass().getMethod("getVl");
                            Object vl = getVl.invoke(check);
                            if (vl instanceof Number) {
                                violations = ((Number) vl).intValue();
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }

                    plugin.logDebug("[Grim] Violations: " + violations);
                }
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[Grim] No getCheck() method found on event");
            }

            // get detailed info about the flag
            try {
                Method getVerbose = event.getClass().getMethod("getVerbose");
                Object result = getVerbose.invoke(event);
                if (result instanceof String) {
                    verbose = (String) result;
                    plugin.logDebug("[Grim] Verbose: " + verbose);
                }
            } catch (NoSuchMethodException ignored) {}

            // cancel the event so Grim doesn't send its own messages
            try {
                Method setCancelled = event.getClass().getMethod("setCancelled", boolean.class);
                setCancelled.invoke(event, true);
                plugin.logDebug("[Grim] Cancelled event to block Grim's native message");
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[Grim] setCancelled method not found - Grim messages may still show");
            } catch (Exception e) {
                plugin.logDebug("[Grim] Failed to cancel event: " + e.getMessage());
            }

            if (player != null) {
                plugin.logDebug("[Grim] Creating alert for " + player.getName() + " - " + checkName + " x" + violations);
                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    checkName,
                    "A",
                    violations,
                    violations,
                    verbose.isEmpty() ? "Grim detection" : verbose
                ));
            } else {
                plugin.logDebug("[Grim] Could not determine player for alert");
            }
        } catch (Exception e) {
            plugin.logDebug("[Anticheat] Error handling Grim event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // returns all check types we've seen so far
    public Set<String> getDiscoveredChecks() {
        return discoveredChecks;
    }
}
