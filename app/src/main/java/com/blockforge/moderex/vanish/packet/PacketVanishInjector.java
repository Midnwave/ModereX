package com.blockforge.moderex.vanish.packet;

import com.blockforge.moderex.ModereX;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages packet filtering injection for vanishing players.
 * Injects VanishPacketFilter into player Netty pipelines to intercept entity packets.
 */
public class PacketVanishInjector {

    private static final String FILTER_NAME = "moderex_vanish_filter";

    private final ModereX plugin;
    private final PacketReflectionCache reflectionCache;

    private final Map<UUID, VanishPacketFilter> injectedFilters = new ConcurrentHashMap<>();

    private final Set<Integer> vanishedEntityIds = ConcurrentHashMap.newKeySet();

    /**
     * Create a new packet vanish injector.
     *
     * @param plugin The plugin instance
     */
    public PacketVanishInjector(ModereX plugin) {
        this.plugin = plugin;
        this.reflectionCache = new PacketReflectionCache(plugin);
    }

    /**
     * Initialize the reflection cache.
     * Should be called when at least one player is online.
     *
     * @return true if initialization succeeded
     */
    public boolean initialize() {
        return reflectionCache.initialize();
    }

    /**
     * Inject a packet filter into a player's channel.
     * This allows the player to see or not see vanished entities.
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
                    plugin.logDebug("[PacketVanish] Failed to initialize reflection cache");
                    return false;
                }
            }

            Channel channel = reflectionCache.getChannel(player);
            if (channel == null) {
                plugin.logDebug("[PacketVanish] Failed to get channel for " + player.getName());
                return false;
            }

            ChannelPipeline pipeline = channel.pipeline();

            if (pipeline.get(FILTER_NAME) != null) {
                plugin.logDebug("[PacketVanish] Filter already exists for " + player.getName());
                return true;
            }

            VanishPacketFilter filter = new VanishPacketFilter(plugin, player, reflectionCache, vanishedEntityIds);

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
                plugin.logDebug("[PacketVanish] Injected filter for " + player.getName());
                return true;
            }

            plugin.logDebug("[PacketVanish] Failed to inject filter for " + player.getName());
            return false;

        } catch (Exception e) {
            plugin.logDebug("[PacketVanish] Error injecting filter for " + player.getName() + ": " + e.getMessage());
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
            VanishPacketFilter filter = injectedFilters.remove(uuid);

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
                plugin.logDebug("[PacketVanish] Removed filter for " + player.getName());
            }

            return true;

        } catch (Exception e) {
            plugin.logDebug("[PacketVanish] Error removing filter for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a vanished entity ID to the filter set
     * All players (without permission) will stop receiving packets for this entity
     *
     * @param entityId The entity ID to vanish
     */
    public void addVanishedEntity(int entityId) {
        vanishedEntityIds.add(entityId);
        plugin.logDebug("[PacketVanish] Added vanished entity ID: " + entityId);
    }

    /**
     * Remove a vanished entity ID from the filter set
     * All players will start receiving packets for this entity again
     *
     * @param entityId The entity ID to unvanish
     */
    public void removeVanishedEntity(int entityId) {
        vanishedEntityIds.remove(entityId);
        plugin.logDebug("[PacketVanish] Removed vanished entity ID: " + entityId);
    }

    /**
     * Remove all filters and clean up
     */
    public void removeAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removePlayer(player);
        }
        vanishedEntityIds.clear();
        injectedFilters.clear();
        plugin.logDebug("[PacketVanish] Removed all filters");
    }

    /**
     * Check if a player has a filter injected

     * @param player The player to check
     * @return true if the player has a filter
     */
    public boolean isInjected(Player player) {
        return injectedFilters.containsKey(player.getUniqueId());
    }

    /**
     * Get the filter for a player

     * @param player The player
     * @return The filter, or null if not injected
     */
    public VanishPacketFilter getFilter(Player player) {
        return injectedFilters.get(player.getUniqueId());
    }

    /**
     * Check if the reflection cache is initialized
     */
    public boolean isInitialized() {
        return reflectionCache.isInitialized();
    }

    /**
     * Check if initialization failed
     */
    public boolean hasInitializationFailed() {
        return reflectionCache.hasInitializationFailed();
    }

    /**
     * Get debug statistics for a player's filter

     * @param player The player
     * @return String with statistics, or null if no filter
     */
    public String getDebugStats(Player player) {
        VanishPacketFilter filter = getFilter(player);
        if (filter == null) {
            return null;
        }

        return String.format("Packets checked: %d, Packets filtered: %d",
                filter.getPacketsChecked(), filter.getPacketsFiltered());
    }
}
