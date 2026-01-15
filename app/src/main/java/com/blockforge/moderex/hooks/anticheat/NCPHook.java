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

public class NCPHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "NoCheatPlus";
    private Class<? extends Event> eventClass;
    private Plugin ncpPlugin;

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
            plugin.log("NCP detected - passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleNCPEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
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
            Player player = null;
            String checkType = "Unknown";
            double vl = 1;

            for (String m : new String[]{"getPlayer", "player"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Player) { player = (Player) r; break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getCheckType", "getCheck"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    checkType = r != null ? r.toString() : "Unknown";
                    break;
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getAddedVL", "getVL", "getViolations"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Number) { vl = ((Number) r).doubleValue(); break; }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(getName(), player, checkType, "A", (int) vl, vl, "NCP detection"));
            }
        } catch (Exception ignored) {}
    }
}
