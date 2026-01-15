package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class MatrixHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Matrix";
    private Class<? extends Event> eventClass;

    public MatrixHook(ModereX plugin) {
        super(plugin, "Matrix");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            eventClass = (Class<? extends Event>) Class.forName("me.rerere.matrix.api.events.PlayerViolationEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleMatrixEvent(event);
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
            plugin.logError("Failed to hook into Matrix", e);
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

    private void handleMatrixEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getHackTypeMethod = event.getClass().getMethod("getHackType");
            Method getViolationsMethod = event.getClass().getMethod("getViolations");

            Player player = (Player) getPlayerMethod.invoke(event);
            String hackType = String.valueOf(getHackTypeMethod.invoke(event));
            int violations = (int) getViolationsMethod.invoke(event);

            handleAlert(new AnticheatAlert(
                    getName(), player, hackType, "A", violations, violations, "Matrix detection"
            ));
        } catch (Exception ignored) {}
    }
}
