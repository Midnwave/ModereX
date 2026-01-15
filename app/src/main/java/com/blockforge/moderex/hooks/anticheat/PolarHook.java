package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class PolarHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Polar";
    private Class<? extends Event> eventClass;

    public PolarHook(ModereX plugin) {
        super(plugin, "Polar");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            eventClass = (Class<? extends Event>) Class.forName("top.polar.api.event.PolarViolationEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handlePolarEvent(event);
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
            plugin.logError("Failed to hook into Polar", e);
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

    private void handlePolarEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getCheckTypeMethod = event.getClass().getMethod("getCheckType");
            Method getViolationsMethod = event.getClass().getMethod("getViolations");

            Player player = (Player) getPlayerMethod.invoke(event);
            String checkType = String.valueOf(getCheckTypeMethod.invoke(event));
            double violations = (double) getViolationsMethod.invoke(event);

            handleAlert(new AnticheatAlert(
                    getName(), player, checkType, "A", (int) violations, violations, "Polar detection"
            ));
        } catch (Exception ignored) {}
    }
}
