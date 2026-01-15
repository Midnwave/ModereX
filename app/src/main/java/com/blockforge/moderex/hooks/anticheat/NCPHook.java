package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class NCPHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "NoCheatPlus";
    private Class<? extends Event> eventClass;

    public NCPHook(ModereX plugin) {
        super(plugin, "NCP");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            // NCP uses a different event structure - ViolationEvent
            eventClass = (Class<? extends Event>) Class.forName("fr.neatmonster.nocheatplus.hooks.ViolationEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleNCPEvent(event);
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
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            plugin.logError("Failed to hook into NoCheatPlus", e);
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
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getCheckTypeMethod = event.getClass().getMethod("getCheckType");
            Method getAddedVLMethod = event.getClass().getMethod("getAddedVL");

            Player player = (Player) getPlayerMethod.invoke(event);
            Object checkType = getCheckTypeMethod.invoke(event);
            double addedVL = (double) getAddedVLMethod.invoke(event);

            handleAlert(new AnticheatAlert(
                    getName(), player, checkType.toString(), "A", (int) addedVL, addedVL, "NCP detection"
            ));
        } catch (Exception ignored) {}
    }
}
