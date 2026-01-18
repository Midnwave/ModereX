package com.blockforge.moderex.vanish.packet;

import com.blockforge.moderex.ModereX;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Netty channel handler that intercepts outbound packets and filters entity packets
 * for vanished players
 */
public class VanishPacketFilter extends ChannelDuplexHandler {

    private final ModereX plugin;
    private final Player recipient;
    private final PacketReflectionCache reflectionCache;
    private final Set<Integer> vanishedEntityIds;

    private final AtomicLong packetsChecked = new AtomicLong(0);
    private final AtomicLong packetsFiltered = new AtomicLong(0);

    /**
     * Create a new packet filter for a player
     *
     * @param plugin The plugin instance
     * @param recipient The player receiving packets (the observer)
     * @param reflectionCache The reflection cache for entity ID extraction
     * @param vanishedEntityIds Thread-safe set of entity IDs that are vanished
     */
    public VanishPacketFilter(ModereX plugin, Player recipient, PacketReflectionCache reflectionCache, Set<Integer> vanishedEntityIds) {
        this.plugin = plugin;
        this.recipient = recipient;
        this.reflectionCache = reflectionCache;
        this.vanishedEntityIds = vanishedEntityIds;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (vanishedEntityIds.isEmpty()) {
            ctx.write(packet, promise);
            return;
        }

        if (!PacketTypes.isVanishRelevant(packet)) {
            ctx.write(packet, promise);
            return;
        }

        packetsChecked.incrementAndGet();

        int entityId = reflectionCache.extractEntityId(packet);

        if (entityId == -1) {
            ctx.write(packet, promise);
            return;
        }

        if (vanishedEntityIds.contains(entityId)) {
            if (recipient.hasPermission("moderex.command.vanish")) {
                ctx.write(packet, promise);
            } else {
                packetsFiltered.incrementAndGet();

                // Debug logging
                if (plugin.getConfigManager() != null &&
                    plugin.getConfigManager().getSettings() != null &&
                    plugin.getConfigManager().getSettings().isVanishDebugPacketFiltering()) {
                    plugin.logDebug("[PacketVanish] Filtered " + packet.getClass().getSimpleName() +
                            " for " + recipient.getName() + " (entity ID: " + entityId + ")");
                }

                promise.setSuccess();
            }
        } else {
            ctx.write(packet, promise);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Log errors but don't crash the pipeline
        plugin.logDebug("[PacketVanish] Error in packet filter for " + recipient.getName() + ": " + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    /**
     * Get the number of packets checked by this filter
     */
    public long getPacketsChecked() {
        return packetsChecked.get();
    }

    /**
     * Get the number of packets filtered (blocked) by this filter
     */
    public long getPacketsFiltered() {
        return packetsFiltered.get();
    }

    /**
     * Get the recipient player
     */
    public Player getRecipient() {
        return recipient;
    }

    /**
     * Add a vanished entity ID to filter
     */
    public void addVanishedEntity(int entityId) {
        vanishedEntityIds.add(entityId);
    }

    /**
     * Remove a vanished entity ID (player unvanished)
     */
    public void removeVanishedEntity(int entityId) {
        vanishedEntityIds.remove(entityId);
    }
}
