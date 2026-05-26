package com.blockforge.moderex.disguise.packet;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.DisguiseProfile;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepts outbound ClientboundPlayerInfoUpdatePackets and replaces each
 * disguised player's GameProfile name + sets an explicit displayName component.
 *
 * Setting an explicit displayName component (instead of nulling it) ensures
 * the correct name shows in every client's tab list, including the player's own
 * entry (which never receives a fresh ADD_PLAYER after respawnForAll skips them).
 *
 * All field writes use UnsafeHelper (which handles record components correctly)
 * so Java 17+ final fields including Entry.profile and Entry.displayName can
 * be replaced without an UnsupportedOperationException.
 */
public class DisguisePacketFilter extends ChannelDuplexHandler {

    // Per-class field offset caches
    private static final Map<Class<?>, Long> ENTRIES_OFFSET      = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Long> PROFILE_ID_OFFSET   = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Long> PROFILE_OFFSET      = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Long> DISPLAY_NAME_OFFSET = new ConcurrentHashMap<>();

    private final ModereX plugin;
    private final Map<UUID, DisguiseProfile> disguiseProfiles;

    public DisguisePacketFilter(ModereX plugin, Map<UUID, DisguiseProfile> disguiseProfiles) {
        this.plugin = plugin;
        this.disguiseProfiles = disguiseProfiles;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (UnsafeHelper.UNSAFE != null && !disguiseProfiles.isEmpty()) {
            try {
                String name = packet.getClass().getSimpleName();
                if (name.contains("PlayerInfoUpdate") || (name.contains("PlayerInfo") && !name.contains("Remove"))) {
                    processInfoPacket(packet);
                }
            } catch (Exception e) {
                plugin.logDebug("[DisguisePacket] write error: " + e.getMessage());
            }
        }
        super.write(ctx, packet, promise);
    }

    private void processInfoPacket(Object packet) {
        long off = resolveEntriesOffset(packet.getClass());
        if (off == -1L) return;
        Object raw = UnsafeHelper.UNSAFE.getObject(packet, off);
        if (!(raw instanceof List<?> list)) return;
        for (Object entry : list) {
            processEntry(entry);
        }
    }

    private void processEntry(Object entry) {
        Class<?> cls = entry.getClass();

        long pidOff = resolveField(cls, "profileId", UUID.class, PROFILE_ID_OFFSET);
        if (pidOff == -1L) return;
        UUID profileId = (UUID) UnsafeHelper.UNSAFE.getObject(entry, pidOff);
        if (profileId == null) return;

        DisguiseProfile disguise = disguiseProfiles.get(profileId);
        if (disguise == null) return;

        // Replace the GameProfile entirely — UnsafeHelper.offset handles record components
        long profOff = resolveField(cls, "profile", GameProfile.class, PROFILE_OFFSET);
        if (profOff != -1L) {
            GameProfile existing = (GameProfile) UnsafeHelper.UNSAFE.getObject(entry, profOff);
            if (existing != null) {
                UnsafeHelper.UNSAFE.putObject(entry, profOff,
                        NmsProfileHelper.buildDisguised(profileId, disguise.getDisplayName(), existing));
                plugin.logDebug("[DisguisePacket] Replaced profile → " + disguise.getDisplayName());
            }
        }

        // Set an explicit displayName component so the tab list is correct for all clients,
        // including the disguised player's own tab entry.
        long dnOff = resolveDisplayNameOffset(cls);
        if (dnOff != -1L) {
            UnsafeHelper.UNSAFE.putObject(entry, dnOff, createComponent(disguise.getDisplayName()));
        }
    }

    // ── NMS Component creation ──────────────────────────────────────────────

    private static volatile Method COMPONENT_LITERAL;

    private Object createComponent(String text) {
        if (COMPONENT_LITERAL == null) {
            try {
                Class<?> cls = Class.forName("net.minecraft.network.chat.Component");
                COMPONENT_LITERAL = cls.getMethod("literal", String.class);
                plugin.logDebug("[DisguisePacket] Cached Component.literal");
            } catch (Exception e) {
                plugin.logDebug("[DisguisePacket] NMS Component unavailable: " + e.getMessage());
                return null;
            }
        }
        try {
            return COMPONENT_LITERAL.invoke(null, text);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Offset resolution ───────────────────────────────────────────────────

    private long resolveEntriesOffset(Class<?> cls) {
        return ENTRIES_OFFSET.computeIfAbsent(cls, c -> {
            for (Field f : c.getDeclaredFields()) {
                if (f.getName().equals("entries") && List.class.isAssignableFrom(f.getType())) {
                    plugin.logDebug("[DisguisePacket] entries field '" + f.getName() + "' in " + c.getSimpleName());
                    return UnsafeHelper.offset(f);
                }
            }
            for (Field f : c.getDeclaredFields()) {
                if (List.class.isAssignableFrom(f.getType())) {
                    plugin.logDebug("[DisguisePacket] entries fallback '" + f.getName() + "' in " + c.getSimpleName());
                    return UnsafeHelper.offset(f);
                }
            }
            plugin.logDebug("[DisguisePacket] no entries/List field in " + c.getSimpleName());
            return -1L;
        });
    }

    /** Finds a field by Mojang name + type, falling back to type-only scan. */
    private long resolveField(Class<?> cls, String mojangName, Class<?> type, Map<Class<?>, Long> cache) {
        return cache.computeIfAbsent(cls, c -> {
            Class<?> cl = c;
            while (cl != null && cl != Object.class) {
                for (Field f : cl.getDeclaredFields()) {
                    if (f.getName().equals(mojangName) && type.isAssignableFrom(f.getType())) {
                        plugin.logDebug("[DisguisePacket] '" + mojangName + "' → " + cl.getSimpleName() + "." + f.getName());
                        return UnsafeHelper.offset(f);
                    }
                }
                cl = cl.getSuperclass();
            }
            // Type-based fallback
            cl = c;
            while (cl != null && cl != Object.class) {
                for (Field f : cl.getDeclaredFields()) {
                    if (type.isAssignableFrom(f.getType())) {
                        plugin.logDebug("[DisguisePacket] '" + mojangName + "' type-fallback → " + cl.getSimpleName() + "." + f.getName());
                        return UnsafeHelper.offset(f);
                    }
                }
                cl = cl.getSuperclass();
            }
            plugin.logDebug("[DisguisePacket] field '" + mojangName + "' not found in " + c.getSimpleName());
            return -1L;
        });
    }

    /** Finds the displayName field by Mojang name (no useful type to distinguish it). */
    private long resolveDisplayNameOffset(Class<?> cls) {
        return DISPLAY_NAME_OFFSET.computeIfAbsent(cls, c -> {
            Class<?> cl = c;
            while (cl != null && cl != Object.class) {
                for (Field f : cl.getDeclaredFields()) {
                    if (f.getName().equals("displayName")) {
                        plugin.logDebug("[DisguisePacket] displayName → " + cl.getSimpleName() + "." + f.getName());
                        return UnsafeHelper.offset(f);
                    }
                }
                cl = cl.getSuperclass();
            }
            plugin.logDebug("[DisguisePacket] displayName not found in " + c.getSimpleName());
            return -1L;
        });
    }
}
