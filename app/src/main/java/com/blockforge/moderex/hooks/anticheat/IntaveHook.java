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

public class IntaveHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Intave";
    private Class<? extends Event> eventClass;
    private Plugin intavePlugin;

    public IntaveHook(ModereX plugin) {
        super(plugin, "Intave");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        intavePlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (intavePlugin == null) return false;

        plugin.logDebug("[Anticheat] Found Intave v" + intavePlugin.getDescription().getVersion());
        ClassLoader cl = intavePlugin.getClass().getClassLoader();

        String[] classes = {
            "de.jpx3.intave.access.api.event.IntaveViolationEvent",
            "de.jpx3.intave.api.event.IntaveViolationEvent"
        };

        for (String className : classes) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, cl);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Intave detected - passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleIntaveEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook Intave", e);
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
            Player player = null;
            String checkName = "Unknown";
            double vl = 1;

            for (String m : new String[]{"getPlayer", "player"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Player) { player = (Player) r; break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getCheckName", "getCheck"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof String) { checkName = (String) r; break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getViolationLevel", "getVl", "getViolations"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Number) { vl = ((Number) r).doubleValue(); break; }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(getName(), player, checkName, "A", (int) vl, vl, "Intave detection"));
            }
        } catch (Exception ignored) {}
    }
}
