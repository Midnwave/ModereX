package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class SpartanHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Spartan";
    private Class<? extends Event> eventClass;

    public SpartanHook(ModereX plugin) {
        super(plugin, "Spartan");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            eventClass = (Class<? extends Event>) Class.forName("me.vagdedes.spartan.api.PlayerViolationEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleSpartanEvent(event);
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
            plugin.logError("Failed to hook into Spartan", e);
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

    private void handleSpartanEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getHackTypeMethod = event.getClass().getMethod("getHackType");
            Method getViolationMethod = event.getClass().getMethod("getViolation");

            Player player = (Player) getPlayerMethod.invoke(event);
            Object hackType = getHackTypeMethod.invoke(event);
            int violation = (int) getViolationMethod.invoke(event);

            handleAlert(new AnticheatAlert(
                    getName(), player, hackType.toString(), "A", violation, violation, "Spartan detection"
            ));
        } catch (Exception ignored) {}
    }
}
