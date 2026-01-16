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
 * Hook for FoxAddition Anticheat by zoruafan
 * API: zoruafan.foxanticheat.api.events.FoxFlagEvent
 *
 * FoxFlagEvent provides:
 * - getPlayer() -> Player
 * - getCheckType() -> String (check name)
 * - getVLS() -> int (violation level)
 * - getDetails() -> String (additional info)
 * - getModule() -> String (module name)
 * - getLog() -> String (log message)
 */
public class FoxAdditionHook extends AnticheatHook implements Listener {

    private static final String[] PLUGIN_NAMES = {"FoxAddition", "FoxEdition", "Fox"};
    private Class<? extends Event> eventClass;
    private Plugin foxPlugin;

    public FoxAdditionHook(ModereX plugin) {
        super(plugin, "FoxAddition");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        for (String name : PLUGIN_NAMES) {
            foxPlugin = Bukkit.getPluginManager().getPlugin(name);
            if (foxPlugin != null) {
                plugin.logDebug("[Anticheat] Found Fox plugin: " + name + " v" + foxPlugin.getDescription().getVersion());
                break;
            }
        }

        if (foxPlugin == null) {
            return false;
        }

        // FoxAddition API class paths (current version first)
        String[] eventClasses = {
            "zoruafan.foxanticheat.api.events.FoxFlagEvent",  // Current FoxAddition
            "de.idcteam.foxaddition.api.event.FoxFlagEvent",  // IDCTeam fork
            "de.idcteam.foxaddition.api.events.PlayerFlagEvent"
        };

        ClassLoader classLoader = foxPlugin.getClass().getClassLoader();

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, classLoader);
                plugin.logDebug("[Anticheat] Found Fox event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("FoxAddition detected - passive mode (no API event found)");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleFoxEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
            plugin.logDebug("[Anticheat] Successfully hooked into FoxAddition API");
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook FoxAddition", e);
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

    private void handleFoxEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            String module = "";
            int vls = 1;
            String details = "";

            // Get player - FoxFlagEvent.getPlayer() returns Player
            try {
                Method getPlayer = event.getClass().getMethod("getPlayer");
                Object result = getPlayer.invoke(event);
                if (result instanceof Player) {
                    player = (Player) result;
                }
            } catch (NoSuchMethodException ignored) {}

            // Get check type - FoxFlagEvent.getCheckType() returns String
            try {
                Method getCheckType = event.getClass().getMethod("getCheckType");
                Object result = getCheckType.invoke(event);
                if (result instanceof String) {
                    checkName = (String) result;
                }
            } catch (NoSuchMethodException ignored) {}

            // Get VLS - FoxFlagEvent.getVLS() returns int
            try {
                Method getVLS = event.getClass().getMethod("getVLS");
                Object result = getVLS.invoke(event);
                if (result instanceof Number) {
                    vls = ((Number) result).intValue();
                }
            } catch (NoSuchMethodException ignored) {}

            // Get module - FoxFlagEvent.getModule() returns String
            try {
                Method getModule = event.getClass().getMethod("getModule");
                Object result = getModule.invoke(event);
                if (result instanceof String) {
                    module = (String) result;
                }
            } catch (NoSuchMethodException ignored) {}

            // Get details - FoxFlagEvent.getDetails() returns String
            try {
                Method getDetails = event.getClass().getMethod("getDetails");
                Object result = getDetails.invoke(event);
                if (result instanceof String) {
                    details = (String) result;
                }
            } catch (NoSuchMethodException ignored) {}

            if (player != null) {
                // Format check name with module if available
                String fullCheckName = module.isEmpty() ? checkName : module + "/" + checkName;
                String info = details.isEmpty() ? "FoxAddition detection" : details;

                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    fullCheckName,
                    "A",
                    vls,
                    vls,
                    info
                ));
            }
        } catch (Exception e) {
            plugin.logDebug("[Anticheat] Error handling FoxAddition event: " + e.getMessage());
        }
    }
}
