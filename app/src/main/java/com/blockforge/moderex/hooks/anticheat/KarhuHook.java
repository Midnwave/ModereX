package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class KarhuHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Karhu";
    private Class<? extends Event> eventClass;

    public KarhuHook(ModereX plugin) {
        super(plugin, "Karhu");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            eventClass = (Class<? extends Event>) Class.forName("me.liwax.karhu.api.event.KarhuFlagEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleKarhuEvent(event);
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
            plugin.logError("Failed to hook into Karhu", e);
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

    private void handleKarhuEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getCheckMethod = event.getClass().getMethod("getCheck");
            Method getViolationsMethod = event.getClass().getMethod("getViolations");

            Player player = (Player) getPlayerMethod.invoke(event);
            Object check = getCheckMethod.invoke(event);
            int violations = (int) getViolationsMethod.invoke(event);

            String checkName = check.getClass().getSimpleName();

            handleAlert(new AnticheatAlert(
                    getName(), player, checkName, "A", violations, violations, "Karhu detection"
            ));
        } catch (Exception ignored) {}
    }
}
