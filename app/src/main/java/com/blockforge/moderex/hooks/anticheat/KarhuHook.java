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

public class KarhuHook extends AnticheatHook implements Listener {

    private static final String PLUGIN_NAME = "Karhu";
    private Class<? extends Event> eventClass;
    private Plugin karhuPlugin;

    public KarhuHook(ModereX plugin) {
        super(plugin, "Karhu");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        karhuPlugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        if (karhuPlugin == null) return false;

        plugin.logDebug("[Anticheat] Found Karhu v" + karhuPlugin.getDescription().getVersion());
        ClassLoader cl = karhuPlugin.getClass().getClassLoader();

        String[] classes = {
            "me.liwax.karhu.api.event.KarhuFlagEvent",
            "me.liwax.karhu.api.events.KarhuFlagEvent"
        };

        for (String className : classes) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, cl);
                break;
            } catch (ClassNotFoundException ignored) {}
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("Karhu detected - passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleKarhuEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook Karhu", e);
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
            Player player = null;
            String checkName = "Unknown";
            int vl = 1;

            for (String m : new String[]{"getPlayer", "player"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Player) { player = (Player) r; break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getCheck", "getCheckName"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof String) { checkName = (String) r; break; }
                    else if (r != null) { checkName = r.getClass().getSimpleName(); break; }
                } catch (NoSuchMethodException ignored) {}
            }

            for (String m : new String[]{"getViolations", "getVl"}) {
                try {
                    Object r = event.getClass().getMethod(m).invoke(event);
                    if (r instanceof Number) { vl = ((Number) r).intValue(); break; }
                } catch (NoSuchMethodException ignored) {}
            }

            if (player != null) {
                handleAlert(new AnticheatAlert(getName(), player, checkName, "A", vl, vl, "Karhu detection"));
            }
        } catch (Exception ignored) {}
    }
}
