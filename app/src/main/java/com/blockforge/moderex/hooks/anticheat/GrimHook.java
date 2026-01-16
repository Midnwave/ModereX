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

    // Track discovered check names for web panel
    private final Set<String> discoveredChecks = ConcurrentHashMap.newKeySet();

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

                // Use HIGHEST priority so we run before Grim sends its messages,
                // and can cancel the event to block those messages
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
            plugin.logDebug("[Grim] Received FlagEvent: " + event.getClass().getName());

            Player player = null;
            String checkName = "Unknown";
            int violations = 1;
            String verbose = "";

            // Get player - FlagEvent.getPlayer() returns GrimUser
            // GrimUser has getBukkitPlayer() method
            try {
                Method getPlayer = event.getClass().getMethod("getPlayer");
                Object grimUser = getPlayer.invoke(event);
                plugin.logDebug("[Grim] Got GrimUser: " + (grimUser != null ? grimUser.getClass().getName() : "null"));

                if (grimUser != null) {
                    // Try getBukkitPlayer() first
                    try {
                        Method getBukkitPlayer = grimUser.getClass().getMethod("getBukkitPlayer");
                        Object bp = getBukkitPlayer.invoke(grimUser);
                        if (bp instanceof Player) {
                            player = (Player) bp;
                            plugin.logDebug("[Grim] Got player via getBukkitPlayer(): " + player.getName());
                        }
                    } catch (NoSuchMethodException e) {
                        // Try getPlayer() on GrimUser
                        try {
                            Method getPlayerFromUser = grimUser.getClass().getMethod("getPlayer");
                            Object p = getPlayerFromUser.invoke(grimUser);
                            if (p instanceof Player) {
                                player = (Player) p;
                                plugin.logDebug("[Grim] Got player via getPlayer(): " + player.getName());
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }

                    // If still no player but we have GrimUser, try getName() for lookup
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

            // Get check - FlagEvent.getCheck() returns AbstractCheck
            // AbstractCheck has getCheckName() and getViolations()
            try {
                Method getCheck = event.getClass().getMethod("getCheck");
                Object check = getCheck.invoke(event);
                plugin.logDebug("[Grim] Got check: " + (check != null ? check.getClass().getName() : "null"));

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

                    plugin.logDebug("[Grim] Check name: " + checkName);

                    // Track discovered checks for web panel
                    if (!discoveredChecks.contains(checkName)) {
                        discoveredChecks.add(checkName);
                        plugin.logDebug("[Grim] Discovered new check type: " + checkName);
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

                    plugin.logDebug("[Grim] Violations: " + violations);
                }
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[Grim] No getCheck() method found on event");
            }

            // Get verbose info - FlagEvent.getVerbose() returns String
            try {
                Method getVerbose = event.getClass().getMethod("getVerbose");
                Object result = getVerbose.invoke(event);
                if (result instanceof String) {
                    verbose = (String) result;
                    plugin.logDebug("[Grim] Verbose: " + verbose);
                }
            } catch (NoSuchMethodException ignored) {}

            // Cancel the event to prevent Grim from sending its own alert messages
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

    /**
     * Get all check names that have been discovered during runtime
     */
    public Set<String> getDiscoveredChecks() {
        return discoveredChecks;
    }
}
