package com.blockforge.moderex.disguise.packet;

import com.blockforge.moderex.ModereX;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lazily resolves the Netty Channel for a Player using Unsafe-based field discovery.
 *
 * The path from ServerPlayer to Channel varies across Paper versions; this class
 * discovers it at runtime with a depth-limited DFS and caches the offset chain
 * (per listener class) so subsequent lookups are just a few Unsafe reads.
 */
public class DisguisePacketReflectionCache {

    private static final int SEARCH_DEPTH = 5;

    private final ModereX plugin;
    private volatile Method getHandleMethod;

    // Listener offset: ServerPlayer → ServerGamePacketListenerImpl
    private static final ConcurrentHashMap<Class<?>, Long> LISTENER_OFFSET = new ConcurrentHashMap<>();

    // Channel path from listener: sequence of Unsafe offsets to follow
    private final ConcurrentHashMap<Class<?>, long[]> CHAN_PATH = new ConcurrentHashMap<>();

    public DisguisePacketReflectionCache(ModereX plugin) {
        this.plugin = plugin;
    }

    public boolean isInitialized() { return getHandleMethod != null; }

    public boolean initialize() { return ensureHandle(); }

    // ── Public API ──────────────────────────────────────────────────────────

    public Channel getChannel(Player player) {
        if (!ensureHandle()) return null;
        try {
            Object serverPlayer = getHandleMethod.invoke(player);

            long listenerOff = LISTENER_OFFSET.computeIfAbsent(serverPlayer.getClass(), cls -> {
                long off = findListenerOffset(cls);
                if (off != -1L) plugin.logDebug("[PacketDisguise] Listener offset found in " + cls.getSimpleName());
                else             plugin.logDebug("[PacketDisguise] No listener field in "   + cls.getSimpleName());
                return off;
            });
            if (listenerOff == -1L) return null;

            Object listener = UnsafeHelper.UNSAFE.getObject(serverPlayer, listenerOff);
            if (listener == null) return null;

            long[] path = CHAN_PATH.get(listener.getClass());
            if (path == null) {
                path = discoverChannelPath(listener);
                if (path != null) {
                    CHAN_PATH.put(listener.getClass(), path);
                    plugin.logDebug("[PacketDisguise] Channel path cached (" + path.length + " hops) for "
                            + listener.getClass().getSimpleName());
                } else {
                    plugin.logDebug("[PacketDisguise] Channel path not found in " + listener.getClass().getSimpleName());
                    return null;
                }
            }
            return readChannelAtPath(listener, path);
        } catch (Exception e) {
            plugin.logDebug("[PacketDisguise] getChannel error: " + e.getMessage());
            return null;
        }
    }

    // ── Handle resolution ───────────────────────────────────────────────────

    private boolean ensureHandle() {
        if (getHandleMethod != null) return true;
        try {
            Class<?> cp;
            try {
                // Paper 1.20.5+ — no version segment
                cp = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
            } catch (ClassNotFoundException e) {
                // Older Paper/Spigot with version segment
                String pkg = plugin.getServer().getClass().getPackage().getName();
                String ver = pkg.substring(pkg.lastIndexOf('.') + 1);
                cp = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer");
            }
            getHandleMethod = cp.getMethod("getHandle");
            plugin.logDebug("[PacketDisguise] Reflection ready (CraftPlayer.getHandle found)");
            return true;
        } catch (Exception e) {
            plugin.logDebug("[PacketDisguise] CraftPlayer.getHandle unavailable: " + e.getMessage());
            return false;
        }
    }

    // ── Listener field ──────────────────────────────────────────────────────

    private long findListenerOffset(Class<?> cls) {
        Class<?> c = cls;
        while (c != null && c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                String t = f.getType().getName();
                if (t.contains("ServerGamePacketListener") || t.contains("ServerCommonPacketListener")
                        || t.contains("PlayerConnection")) {
                    long off = UnsafeHelper.offset(f);
                    if (off != -1L) plugin.logDebug("[PacketDisguise] Listener field '" + f.getName()
                            + "' in " + c.getSimpleName());
                    return off;
                }
            }
            c = c.getSuperclass();
        }
        return -1L;
    }

    // ── Channel path discovery ──────────────────────────────────────────────

    /**
     * DFS over the listener's field graph to find the first Channel value.
     * Returns the sequence of Unsafe offsets to follow from the listener, or null.
     */
    private long[] discoverChannelPath(Object listener) {
        return dfs(listener, new long[0], SEARCH_DEPTH);
    }

    private long[] dfs(Object obj, long[] prefix, int depth) {
        if (depth == 0 || obj == null) return null;
        Class<?> cls = obj.getClass();
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                if (skip(f)) continue;
                long off = UnsafeHelper.offset(f);
                if (off == -1L) continue;
                long[] next = append(prefix, off);

                if (Channel.class.isAssignableFrom(f.getType())) {
                    Object val = safeGet(obj, off);
                    if (val instanceof Channel) {
                        plugin.logDebug("[PacketDisguise] Channel at field '" + f.getName()
                                + "' in " + cls.getSimpleName() + " (depth " + (SEARCH_DEPTH - depth) + ")");
                        return next;
                    }
                }

                if (depth > 1 && couldContainChannel(f)) {
                    Object val = safeGet(obj, off);
                    if (val != null && val != obj) {
                        long[] result = dfs(val, next, depth - 1);
                        if (result != null) return result;
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    private Channel readChannelAtPath(Object root, long[] path) {
        Object cur = root;
        for (int i = 0; i < path.length - 1; i++) {
            cur = UnsafeHelper.UNSAFE.getObject(cur, path[i]);
            if (cur == null) return null;
        }
        Object ch = UnsafeHelper.UNSAFE.getObject(cur, path[path.length - 1]);
        return ch instanceof Channel ? (Channel) ch : null;
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /** Skip primitives, arrays, strings, and common unrelated types. */
    private static boolean skip(Field f) {
        if (f.getType().isPrimitive() || f.getType().isArray()) return true;
        String n = f.getType().getName();
        return n.startsWith("java.") || n.startsWith("sun.") || n.startsWith("com.google.")
                || n.startsWith("org.slf4j") || n.startsWith("com.mojang.authlib");
    }

    /** True for types that plausibly hold a Channel somewhere inside. */
    private static boolean couldContainChannel(Field f) {
        String n = f.getType().getName();
        return n.startsWith("net.minecraft") || n.startsWith("io.netty")
                || n.startsWith("io.papermc") || n.contains("Connection")
                || n.contains("NetworkManager") || n.contains("Channel");
    }

    private static Object safeGet(Object obj, long off) {
        try { return UnsafeHelper.UNSAFE.getObject(obj, off); } catch (Exception e) { return null; }
    }

    private static long[] append(long[] arr, long val) {
        long[] r = Arrays.copyOf(arr, arr.length + 1);
        r[arr.length] = val;
        return r;
    }

    public boolean hasInitializationFailed() { return false; }
}
