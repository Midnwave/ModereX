package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class IntaveHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Intave";
    private Class<? extends Event> eventClass;

    public IntaveHook(ModereX plugin) {
        super(plugin, "Intave");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            eventClass = (Class<? extends Event>) Class.forName("de.jpx3.intave.access.api.event.IntaveViolationEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleIntaveEvent(event);
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
            plugin.logError("Failed to hook into Intave", e);
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

    private void handleIntaveEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getCheckNameMethod = event.getClass().getMethod("getCheckName");
            Method getViolationLevelMethod = event.getClass().getMethod("getViolationLevel");

            Player player = (Player) getPlayerMethod.invoke(event);
            String checkName = (String) getCheckNameMethod.invoke(event);
            double vl = (double) getViolationLevelMethod.invoke(event);

            handleAlert(new AnticheatAlert(
                    getName(), player, checkName, "A", (int) vl, vl, "Intave detection"
            ));
        } catch (Exception ignored) {}
    }
}
