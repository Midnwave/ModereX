package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import java.lang.reflect.Method;

public class VulcanHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Vulcan";
    private Class<? extends Event> eventClass;

    public VulcanHook(ModereX plugin) {
        super(plugin, "Vulcan");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        if (!isPluginAvailable(PLUGIN_NAME)) {
            return false;
        }

        try {
            eventClass = (Class<? extends Event>) Class.forName("me.frep.vulcan.api.event.VulcanFlagEvent");

            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!eventClass.isInstance(event)) return;
                handleVulcanEvent(event);
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
            plugin.logError("Failed to hook into Vulcan", e);
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

    private void handleVulcanEvent(Event event) {
        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            Method getCheckMethod = event.getClass().getMethod("getCheck");
            Method getVLMethod = event.getClass().getMethod("getVl");

            Player player = (Player) getPlayerMethod.invoke(event);
            Object check = getCheckMethod.invoke(event);
            int vl = (int) getVLMethod.invoke(event);

            String checkName = check.getClass().getSimpleName();
            String checkType = "A";
            try {
                Method getTypeMethod = check.getClass().getMethod("getType");
                checkType = String.valueOf(getTypeMethod.invoke(check));
            } catch (Exception ignored) {}

            handleAlert(new AnticheatAlert(
                    getName(), player, checkName, checkType, vl, vl, "Vulcan detection"
            ));
        } catch (Exception ignored) {}
    }
}
