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

public class FoxAdditionHook extends AnticheatHook implements Listener {

    private static final String[] PLUGIN_NAMES = {"FoxAddition", "FoxEdition", "Fox"};
    private Class<? extends Event> eventClass;
    private Plugin foxPlugin;

    public FoxAdditionHook(ModereX plugin) {
        super(plugin, "FoxAddition");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        for (String name : PLUGIN_NAMES) {
            foxPlugin = Bukkit.getPluginManager().getPlugin(name);
            if (foxPlugin != null) {
                plugin.logDebug("[Anticheat] Found Fox plugin: " + name + " v" + foxPlugin.getDescription().getVersion());
                break;
            }
        }

        if (foxPlugin == null) {
            return false;
        }

        String[] eventClasses = {
            "de.idcteam.foxaddition.api.event.FoxFlagEvent",
            "de.idcteam.foxaddition.api.events.PlayerFlagEvent",
            "de.idcteam.fox.api.event.FlagEvent"
        };

        ClassLoader classLoader = foxPlugin.getClass().getClassLoader();

        for (String className : eventClasses) {
            try {
                eventClass = (Class<? extends Event>) Class.forName(className, true, classLoader);
                plugin.logDebug("[Anticheat] Found Fox event class: " + className);
                break;
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (eventClass == null) {
            enabled = true;
            plugin.getLogger().info("FoxAddition detected - passive mode");
            return true;
        }

        try {
            EventExecutor executor = (listener, event) -> {
                if (!enabled || !eventClass.isInstance(event)) return;
                handleFoxEvent(event);
            };
            plugin.getServer().getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, plugin, true);
            enabled = true;
            return true;
        } catch (Exception e) {
            plugin.logError("Failed to hook FoxAddition", e);
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

    private void handleFoxEvent(Event event) {
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

            for (String m : new String[]{"getCheckName", "getCheck", "getType"}) {
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
                handleAlert(new AnticheatAlert(getName(), player, checkName, "A", vl, vl, "FoxAddition detection"));
            }
        } catch (Exception ignored) {}
    }
}
