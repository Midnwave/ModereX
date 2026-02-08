package com.blockforge.moderex.disguise.packet;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.DisguiseProfile;
import com.blockforge.moderex.vanish.packet.PacketReflectionCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages packet filtering injection for disguising players.
 * Injects DisguisePacketFilter into player Netty pipelines to modify packets.
 */
public class PacketDisguiseInjector {

    private static final String FILTER_NAME = "moderex_disguise_filter";

    private final ModereX plugin;
    private final PacketReflectionCache reflectionCache;

    private final Map<UUID, DisguisePacketFilter> injectedFilters = new ConcurrentHashMap<>();
    private final Map<UUID, DisguiseProfile> disguiseProfiles = new ConcurrentHashMap<>();

    /**
     * Create a new packet disguise injector.
     *
     * @param plugin The plugin instance
     */
    public PacketDisguiseInjector(ModereX plugin) {
        this.plugin = plugin;
        this.reflectionCache = new PacketReflectionCache(plugin);
    }

    /**
     * Initialize the reflection cache.
     *
     * @return true if initialization succeeded
     */
    public boolean initialize() {
        return reflectionCache.initialize();
    }

    /**
     * Inject a packet filter into a player's channel.
     *
     * @param player The player to inject into
     * @return true if injection succeeded
     */
    public boolean injectPlayer(Player player) {
        if (player == null) return false;

        if (injectedFilters.containsKey(player.getUniqueId())) {
            return true;
        }

        try {
            if (!reflectionCache.isInitialized()) {
                if (!initialize()) {
                    plugin.logDebug("[PacketDisguise] Failed to initialize reflection cache");
                    return false;
                }
            }

            Channel channel = reflectionCache.getChannel(player);
            if (channel == null) {
                plugin.logDebug("[PacketDisguise] Failed to get channel for " + player.getName());
                return false;
            }

            ChannelPipeline pipeline = channel.pipeline();

            if (pipeline.get(FILTER_NAME) != null) {
                plugin.logDebug("[PacketDisguise] Filter already exists for " + player.getName());
                return true;
            }

            DisguisePacketFilter filter = new DisguisePacketFilter(plugin, player, reflectionCache, disguiseProfiles);

            String[] handlerNames = {"packet_handler", "encoder", "outbound_config", "handler"};
            boolean injected = false;

            for (String handlerName : handlerNames) {
                try {
                    if (pipeline.get(handlerName) != null) {
                        pipeline.addBefore(handlerName, FILTER_NAME, filter);
                        injected = true;
                        break;
                    }
                } catch (Exception ignored) {}
            }

            if (!injected) {
                pipeline.addLast(FILTER_NAME, filter);
                injected = true;
            }

            if (injected) {
                injectedFilters.put(player.getUniqueId(), filter);
                plugin.logDebug("[PacketDisguise] Injected filter for " + player.getName());
                return true;
            }

            plugin.logDebug("[PacketDisguise] Failed to inject filter for " + player.getName());
            return false;

        } catch (Exception e) {
            plugin.logDebug("[PacketDisguise] Error injecting filter for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove the packet filter from a player's channel.
     *
     * @param player The player to remove from
     * @return true if removal succeeded
     */
    public boolean removePlayer(Player player) {
        if (player == null) return false;

        try {
            UUID uuid = player.getUniqueId();
            DisguisePacketFilter filter = injectedFilters.remove(uuid);

            if (filter == null) {
                return false;
            }

            Channel channel = reflectionCache.getChannel(player);
            if (channel == null) {
                return false;
            }

            ChannelPipeline pipeline = channel.pipeline();
            if (pipeline.get(FILTER_NAME) != null) {
                pipeline.remove(FILTER_NAME);
                plugin.logDebug("[PacketDisguise] Removed filter for " + player.getName());
            }

            return true;

        } catch (Exception e) {
            plugin.logDebug("[PacketDisguise] Error removing filter for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a disguised player with their profile.
     *
     * @param uuid The player UUID
     * @param profile The disguise profile
     */
    public void addDisguisedPlayer(UUID uuid, DisguiseProfile profile) {
        disguiseProfiles.put(uuid, profile);
        plugin.logDebug("[PacketDisguise] Added disguise profile for UUID: " + uuid);
    }

    /**
     * Remove a disguised player.
     *
     * @param uuid The player UUID
     */
    public void removeDisguisedPlayer(UUID uuid) {
        disguiseProfiles.remove(uuid);
        plugin.logDebug("[PacketDisguise] Removed disguise profile for UUID: " + uuid);
    }

    /**
     * Remove all filters and clean up.
     */
    public void removeAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removePlayer(player);
        }
        disguiseProfiles.clear();
        injectedFilters.clear();
        plugin.logDebug("[PacketDisguise] Removed all filters");
    }

    /**
     * Check if a player has a filter injected.
     *
     * @param player The player to check
     * @return true if the player has a filter
     */
    public boolean isInjected(Player player) {
        return injectedFilters.containsKey(player.getUniqueId());
    }

    /**
     * Get the filter for a player.
     *
     * @param player The player
     * @return The filter, or null if not injected
     */
    public DisguisePacketFilter getFilter(Player player) {
        return injectedFilters.get(player.getUniqueId());
    }

    /**
     * Check if the reflection cache is initialized.
     */
    public boolean isInitialized() {
        return reflectionCache.isInitialized();
    }
}
