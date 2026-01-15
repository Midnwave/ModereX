package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import org.bukkit.event.Listener;

public class ListenerManager {

    private final ModereX plugin;

    public ListenerManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // Core listeners
        registerListener(new ChatListener(plugin));
        registerListener(new JoinQuitListener(plugin));
        registerListener(new CommandListener(plugin));
        registerListener(new SignListener(plugin));
        registerListener(new GuiListener(plugin));
        registerListener(new VanishListener(plugin));
        registerListener(new PlayerInteractionListener(plugin));

        plugin.getLogger().info("Registered all event listeners.");
    }

    private void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
