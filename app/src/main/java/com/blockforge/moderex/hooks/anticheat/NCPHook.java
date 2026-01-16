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

public class NCPHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "NoCheatPlus";
    private Class<? extends Event> eventClass;
    private Plugin ncpPlugin;
    private final Set<String> discoveredChecks = ConcurrentHashMap.newKeySet();

    public NCPHook(ModereX plugin) {
        super(plugin, "NCP");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        ncpPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (ncpPlugin == null) return false;

        plugin.logDebug("[Anticheat] Found NCP v" + ncpPlugin.getDescription().getVersion());
        ClassLoader cl = ncpPlugin.getClass().getClassLoader();

        String[] classes = {
            "fr.neatmonster.nocheatplus.hooks.ViolationEvent",
            "fr.neatmonster.nocheatplus.checks.ViolationEvent"
        };

        for (String className : classes) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, cl);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("NCP detected - passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleNCPEvent(event);
            };
            // Use HIGHEST priority to run before NCP sends messages and allow cancellation
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.HIGHEST, executor, plugin, true);
            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into NCP");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook NCP", e);
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

    private void handleNCPEvent(Event event) {
        try {
            plugin.logDebug("[NCP] Received event: " + event.getClass().getName());

            Player player = null;
            String checkType = "Unknown";
            double vl = 1;

            for (String m : new String[]{"getPlayer", "player"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Player) {
                        player = (Player) r;
                        plugin.logDebug("[NCP] Got player via " + m + "(): " + player.getName());
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getCheckType", "getCheck"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    checkType = r != null ? r.toString() : "Unknown";
                    break;
                } catch (NoSuchMethodException ignored) {}
            }

            plugin.logDebug("[NCP] Check type: " + checkType);

            // Track discovered checks
            if (!discoveredChecks.contains(checkType)) {
                discoveredChecks.add(checkType);
                plugin.logDebug("[NCP] Discovered new check type: " + checkType);
            }

            for (String m : new String[]{"getAddedVL", "getVL", "getViolations"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Number) {
                        vl = ((Number) r).doubleValue();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {}
            }

            plugin.logDebug("[NCP] Violations: " + vl);

            // Cancel the event to prevent NCP from sending its own alert messages
            try {
                Method setCancelled = event.getClass().getMethod("setCancelled", boolean.class);
                setCancelled.invoke(event, true);
                plugin.logDebug("[NCP] Cancelled event to block NCP's native message");
            } catch (NoSuchMethodException e) {
                plugin.logDebug("[NCP] setCancelled method not found - NCP messages may still show");
            } catch (Exception e) {
                plugin.logDebug("[NCP] Failed to cancel event: " + e.getMessage());
            }

            if (player != null) {
                plugin.logDebug("[NCP] Creating alert for " + player.getName() + " - " + checkType + " x" + (int) vl);
                handleAlert(new AnticheatAlert(getName(), player, checkType, "A", (int) vl, vl, "NCP detection"));
            } else {
                plugin.logDebug("[NCP] Could not determine player for alert");
            }
        } catch (Exception e) {
            plugin.logDebug("[NCP] Error handling event: " + e.getMessage());
        }
    }

    public Set<String> getDiscoveredChecks() {
        return discoveredChecks;
    }
}
