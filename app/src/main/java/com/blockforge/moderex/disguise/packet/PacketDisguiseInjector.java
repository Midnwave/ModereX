package com.blockforge.moderex.disguise.packet;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.DisguiseProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketDisguiseInjector {

    private static final String FILTER_NAME = "moderex_disguise_filter";

    private final ModereX plugin;
    private final DisguisePacketReflectionCache reflectionCache;

    private final Map<UUID, DisguisePacketFilter> injectedFilters = new ConcurrentHashMap<>();
    private final Map<UUID, DisguiseProfile> disguiseProfiles = new ConcurrentHashMap<>();

    public PacketDisguiseInjector(ModereX plugin) {
        this.plugin = plugin;
        this.reflectionCache = new DisguisePacketReflectionCache(plugin);
    }

    public boolean initialize() {
        return reflectionCache.initialize();
    }

    public boolean injectPlayer(Player player) {
        if (player == null) return false;
        if (injectedFilters.containsKey(player.getUniqueId())) return true;

        try {
            if (!reflectionCache.isInitialized() && !initialize()) {
                plugin.logDebug("[PacketDisguise] Failed to initialize reflection cache");
                return false;
            }

            Channel channel = reflectionCache.getChannel(player);
            if (channel == null) {
                plugin.logDebug("[PacketDisguise] Failed to get channel for " + player.getName());
                return false;
            }

            ChannelPipeline pipeline = channel.pipeline();
            if (pipeline.get(FILTER_NAME) != null) return true;

            DisguisePacketFilter filter = new DisguisePacketFilter(plugin, disguiseProfiles);
            boolean injected = false;

            // Inject AFTER the encoder so we run last in the outbound chain and override TAB plugin.
            // Netty outbound writes flow from packet_handler toward HEAD, so the handler
            // closest to HEAD runs LAST.  Pipeline order:
            //   ... → encoder → [OUR_FILTER] → [TAB_FILTER] → packet_handler → ...
            // Outbound execution order: packet_handler → TAB_FILTER → OUR_FILTER → encoder
            for (String encoderName : new String[]{"encoder", "packet_encoder"}) {
                try {
                    if (pipeline.get(encoderName) != null) {
                        pipeline.addAfter(encoderName, FILTER_NAME, filter);
                        injected = true;
                        plugin.logDebug("[PacketDisguise] Injected after '" + encoderName + "' for " + player.getName());
                        break;
                    }
                } catch (Exception ignored) {}
            }

            // Fallback: addBefore packet_handler (may be overridden by TAB, but better than nothing)
            if (!injected) {
                for (String handlerName : new String[]{"packet_handler", "outbound_config", "handler"}) {
                    try {
                        if (pipeline.get(handlerName) != null) {
                            pipeline.addBefore(handlerName, FILTER_NAME, filter);
                            injected = true;
                            plugin.logDebug("[PacketDisguise] Injected before '" + handlerName + "' (fallback) for " + player.getName());
                            break;
                        }
                    } catch (Exception ignored) {}
                }
            }

            if (!injected) {
                pipeline.addLast(FILTER_NAME, filter);
                injected = true;
                plugin.logDebug("[PacketDisguise] Injected at tail (last resort) for " + player.getName());
            }

            if (injected) {
                injectedFilters.put(player.getUniqueId(), filter);
            }

            return injected;
        } catch (Exception e) {
            plugin.logDebug("[PacketDisguise] Error injecting filter for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean removePlayer(Player player) {
        if (player == null) return false;
        try {
            UUID uuid = player.getUniqueId();
            DisguisePacketFilter filter = injectedFilters.remove(uuid);
            if (filter == null) return false;

            Channel channel = reflectionCache.getChannel(player);
            if (channel == null) return false;

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

    public void addDisguisedPlayer(UUID uuid, DisguiseProfile profile) {
        disguiseProfiles.put(uuid, profile);
    }

    public void removeDisguisedPlayer(UUID uuid) {
        disguiseProfiles.remove(uuid);
    }

    public void removeAll() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            removePlayer(player);
        }
        disguiseProfiles.clear();
        injectedFilters.clear();
    }

    public boolean isInjected(Player player) {
        return injectedFilters.containsKey(player.getUniqueId());
    }

    public boolean isInitialized() {
        return reflectionCache.isInitialized();
    }
}
