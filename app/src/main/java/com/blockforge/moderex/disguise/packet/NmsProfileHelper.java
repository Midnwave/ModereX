package com.blockforge.moderex.disguise.packet;

import com.blockforge.moderex.ModereX;
import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replaces the NMS-level GameProfile on a ServerPlayer using Unsafe.
 *
 * Why: setPlayerProfile() only updates the Bukkit wrapper, not the NMS entity's
 * gameProfile field.  The NMS field is what Paper/Spigot reads for player.getName(),
 * scoreboard names, and the source of all PlayerInfo packets generated server-side.
 * Replacing it directly means TAB plugin's {player} format, tab-complete, and anything
 * else that calls player.getName() will see the disguised name with zero extra work.
 */
public class NmsProfileHelper {

    private final ModereX plugin;
    private Method getHandleMethod;
    private long gameProfileOffset = -1;
    private boolean ready = false;

    public NmsProfileHelper(ModereX plugin) {
        this.plugin = plugin;
    }

    public boolean init() {
        if (ready) return true;
        if (UnsafeHelper.UNSAFE == null) { plugin.logDebug("[NmsProfile] Unsafe unavailable"); return false; }
        try {
            Class<?> craftPlayer;
            try {
                craftPlayer = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
            } catch (ClassNotFoundException e) {
                String pkg = plugin.getServer().getClass().getPackage().getName();
                String ver = pkg.substring(pkg.lastIndexOf('.') + 1);
                craftPlayer = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer");
            }
            getHandleMethod = craftPlayer.getMethod("getHandle");
            ready = true;
            plugin.logDebug("[NmsProfile] Initialized");
            return true;
        } catch (Exception e) {
            plugin.logDebug("[NmsProfile] Init failed: " + e.getMessage());
            return false;
        }
    }

    private Object handle(Player player) {
        if (!ready && !init()) return null;
        try { return getHandleMethod.invoke(player); } catch (Exception e) { return null; }
    }

    private long profileOffset(Object handle) {
        if (gameProfileOffset != -1) return gameProfileOffset;
        Class<?> cls = handle.getClass();
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                if (GameProfile.class.isAssignableFrom(f.getType())) {
                    gameProfileOffset = UnsafeHelper.offset(f);
                    plugin.logDebug("[NmsProfile] Cached GameProfile field in " + cls.getSimpleName());
                    return gameProfileOffset;
                }
            }
            cls = cls.getSuperclass();
        }
        return -1;
    }

    public GameProfile get(Player player) {
        Object h = handle(player);
        if (h == null) return null;
        long off = profileOffset(h);
        return off == -1 ? null : (GameProfile) UnsafeHelper.UNSAFE.getObject(h, off);
    }

    public boolean set(Player player, GameProfile profile) {
        Object h = handle(player);
        if (h == null) return false;
        long off = profileOffset(h);
        if (off == -1) return false;
        UnsafeHelper.UNSAFE.putObject(h, off, profile);
        return true;
    }

    /**
     * Build a new GameProfile with the disguised name, copying all original skin properties.
     * Uses Unsafe to copy the properties field directly so this works across all authlib
     * versions — avoids calling getProperties() whose return type changed in MC 1.21.4+.
     */
    public static GameProfile buildDisguised(UUID uuid, String disguisedName, GameProfile original) {
        GameProfile gp = new GameProfile(uuid, disguisedName);
        if (original != null && UnsafeHelper.UNSAFE != null) {
            copyPropertiesUnsafe(original, gp);
        }
        return gp;
    }

    private static final ConcurrentHashMap<Class<?>, Long> PROPS_OFFSET_CACHE = new ConcurrentHashMap<>();

    private static void copyPropertiesUnsafe(GameProfile from, GameProfile to) {
        long off = PROPS_OFFSET_CACHE.computeIfAbsent(from.getClass(), cls -> {
            // Find the properties field: skip id (UUID), name (String), and primitives.
            // The first remaining reference field is the properties container.
            Class<?> c = cls;
            while (c != null && c != Object.class) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getType().isPrimitive()) continue;
                    if (f.getType() == String.class) continue;
                    if (f.getType() == UUID.class) continue;
                    return UnsafeHelper.offset(f);
                }
                c = c.getSuperclass();
            }
            return -1L;
        });
        if (off == -1L) return;
        Object props = UnsafeHelper.UNSAFE.getObject(from, off);
        if (props != null) {
            UnsafeHelper.UNSAFE.putObject(to, off, props);
        }
    }

    public boolean isReady() { return UnsafeHelper.UNSAFE != null && (ready || init()); }
}
