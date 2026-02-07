package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.VersionUtil;
import org.bukkit.event.Listener;

public class ListenerManager {

    private final ModereX plugin;
    private NicknameListener nicknameListener;

    public ListenerManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // Chat listener - platform-specific (Paper uses AsyncChatEvent, Spigot uses AsyncPlayerChatEvent)
        if (VersionUtil.isPaper()) {
            registerListener(new ChatListener(plugin));
        } else {
            registerListener(new SpigotChatListener(plugin));
        }
        // Book/anvil moderation - works on both platforms
        registerListener(new ChatModerationListener(plugin));
        registerListener(new JoinQuitListener(plugin));
        registerListener(new CommandListener(plugin));
        registerListener(new SignListener(plugin));
        registerListener(new GuiListener(plugin));
        registerListener(new VanishListener(plugin));
        registerListener(new DisguiseListener(plugin));
        registerListener(new PlayerInteractionListener(plugin));
        registerListener(new ReplayListener(plugin));
        registerListener(new ReplayActionListener(plugin));
        registerListener(new BlockLogListener(plugin));
        registerListener(new EntityLogListener(plugin));
        registerListener(new ActivityLogListener(plugin));

        // Nickname listener with polling
        nicknameListener = new NicknameListener(plugin);
        registerListener(nicknameListener);
        nicknameListener.startPolling();

        plugin.getLogger().info("Registered all event listeners.");
    }

    public void shutdown() {
        if (nicknameListener != null) {
            nicknameListener.stopPolling();
        }
    }

    public NicknameListener getNicknameListener() {
        return nicknameListener;
    }

    private void registerListener(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
