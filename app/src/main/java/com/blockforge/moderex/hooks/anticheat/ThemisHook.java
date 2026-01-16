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

public class ThemisHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Themis";
    private Class<? extends Event> eventClass;
    private Plugin themisPlugin;

    public ThemisHook(ModereX plugin) {
        super(plugin, "Themis");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        themisPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (themisPlugin == null) return false;

        plugin.logDebug("[Anticheat] Found Themis v" + themisPlugin.getDescription().getVersion());
        ClassLoader cl = themisPlugin.getClass().getClassLoader();

        String[] classes = {
            // Themis API ViolationEvent (primary)
            "com.jedk1.themis.api.ViolationEvent",
            "com.jedk1.themis.api.event.ViolationEvent",
            "me.jedk1.themis.api.event.ViolationEvent",
            // Alternative patterns
            "com.jedk1.themis.event.ViolationEvent",
            "me.jedk1.themis.event.ViolationEvent"
        };

        for (String className : classes) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, cl);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Themis detected - passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleThemisEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook Themis", e);
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

    private void handleThemisEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            int vl = 1;

            for (String m : new String[]{"getPlayer", "player"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Player) { player = (Player) r; break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getCheckType", "getCheck", "getType"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof String) { checkName = (String) r; break; }
                    else if (r != null) { checkName = r.toString(); break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getViolations", "getVl", "getViolation"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Number) { vl = ((Number) r).intValue(); break; }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(getName(), player, checkName, "A", vl, vl, "Themis detection"));
            }
        } catch (Exception ignored) {}
    }
}
