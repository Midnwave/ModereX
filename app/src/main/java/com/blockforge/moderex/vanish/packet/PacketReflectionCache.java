package com.blockforge.moderex.vanish.packet;

import com.blockforge.moderex.ModereX;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Caches NMS reflection lookups for packet manipulation.
 * Handles version differences between Minecraft versions using flexible reflection.
 */
public class PacketReflectionCache {

    private final ModereX plugin;

    private Method getHandleMethod;
    private Field connectionField;
    private Field networkManagerField;
    private Field channelField;

    private final Map<String, Field> packetEntityIdFields = new HashMap<>();

    private boolean initialized = false;
    private boolean initializationFailed = false;

    public PacketReflectionCache(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize all reflection caches.
     * @return true if initialization succeeded
     */
    public boolean initialize() {
        if (initialized) return true;
        if (initializationFailed) return false;

        try {
            plugin.logDebug("[PacketVanish] Initializing reflection cache...");

            if (!cacheGetHandleMethod()) {
                plugin.logDebug("[PacketVanish] Failed to cache getHandle method");
                initializationFailed = true;
                return false;
            }

            if (!cacheConnectionField()) {
                plugin.logDebug("[PacketVanish] Failed to cache connection field");
                initializationFailed = true;
                return false;
            }

            if (!cacheChannelField()) {
                plugin.logDebug("[PacketVanish] Failed to cache channel field");
                initializationFailed = true;
                return false;
            }

            initialized = true;
            plugin.logDebug("[PacketVanish] Reflection cache initialized successfully");
            return true;

        } catch (Exception e) {
            plugin.logDebug("[PacketVanish] Failed to initialize reflection cache: " + e.getMessage());
            initializationFailed = true;
            return false;
        }
    }

    /**
     * Cache the CraftPlayer.getHandle() method.
     */
    private boolean cacheGetHandleMethod() {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".entity.CraftPlayer");
            getHandleMethod = craftPlayerClass.getMethod("getHandle");
            plugin.logDebug("[PacketVanish] Cached getHandle method");
            return true;
        } catch (Exception e) {
            try {
                getHandleMethod = Player.class.getMethod("getHandle");
                plugin.logDebug("[PacketVanish] Cached getHandle method (direct)");
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
    }

    /**
     * Cache the ServerPlayer connection field.
     */
    private boolean cacheConnectionField() {
        try {
            Player testPlayer = plugin.getServer().getOnlinePlayers().stream().findFirst().orElse(null);
            if (testPlayer == null) {
                plugin.logDebug("[PacketVanish] No players online to test reflection");
                return false;
            }

            Object serverPlayer = getHandleMethod.invoke(testPlayer);
            Class<?> serverPlayerClass = serverPlayer.getClass();

            String[] fieldNames = {"connection", "c", "b", "playerConnection"};

            for (String fieldName : fieldNames) {
                try {
                    connectionField = serverPlayerClass.getField(fieldName);
                    plugin.logDebug("[PacketVanish] Cached connection field: " + fieldName);
                    return true;
                } catch (NoSuchFieldException ignored) {}
            }

            for (String fieldName : fieldNames) {
                try {
                    connectionField = serverPlayerClass.getDeclaredField(fieldName);
                    connectionField.setAccessible(true);
                    plugin.logDebug("[PacketVanish] Cached connection field (private): " + fieldName);
                    return true;
                } catch (NoSuchFieldException ignored) {}
            }

            for (Field field : serverPlayerClass.getDeclaredFields()) {
                String className = field.getType().getSimpleName();
                if (className.contains("PlayerConnection") || className.contains("ServerGamePacketListener")) {
                    field.setAccessible(true);
                    connectionField = field;
                    plugin.logDebug("[PacketVanish] Cached connection field by type: " + field.getName());
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            plugin.logDebug("[PacketVanish] Error caching connection field: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cache the NetworkManager/Connection channel field.
     */
    private boolean cacheChannelField() {
        try {
            Player testPlayer = plugin.getServer().getOnlinePlayers().stream().findFirst().orElse(null);
            if (testPlayer == null) {
                plugin.logDebug("[PacketVanish] No players online to test reflection");
                return false;
            }

            Object serverPlayer = getHandleMethod.invoke(testPlayer);
            Object playerConnection = connectionField.get(serverPlayer);
            Class<?> connectionClass = playerConnection.getClass();

            String[] networkManagerFieldNames = {"connection", "networkManager", "network", "h", "i"};

            Field networkManagerFieldTemp = null;
            for (String fieldName : networkManagerFieldNames) {
                try {
                    networkManagerFieldTemp = connectionClass.getField(fieldName);
                    plugin.logDebug("[PacketVanish] Found network manager field: " + fieldName);
                    break;
                } catch (NoSuchFieldException ignored) {}
            }

            if (networkManagerFieldTemp == null) {
                for (String fieldName : networkManagerFieldNames) {
                    try {
                        networkManagerFieldTemp = connectionClass.getDeclaredField(fieldName);
                        networkManagerFieldTemp.setAccessible(true);
                        plugin.logDebug("[PacketVanish] Found network manager field (private): " + fieldName);
                        break;
                    } catch (NoSuchFieldException ignored) {}
                }
            }

            if (networkManagerFieldTemp == null) {
                for (Field field : connectionClass.getDeclaredFields()) {
                    String className = field.getType().getSimpleName();
                    if (className.contains("NetworkManager") || className.equals("Connection")) {
                        field.setAccessible(true);
                        networkManagerFieldTemp = field;
                        plugin.logDebug("[PacketVanish] Found network manager field by type: " + field.getName());
                        break;
                    }
                }
            }

            if (networkManagerFieldTemp == null) {
                return false;
            }

            networkManagerField = networkManagerFieldTemp;
            Object networkManager = networkManagerField.get(playerConnection);
            Class<?> networkManagerClass = networkManager.getClass();

            String[] channelFieldNames = {"channel", "m", "k", "n"};

            for (String fieldName : channelFieldNames) {
                try {
                    channelField = networkManagerClass.getField(fieldName);
                    plugin.logDebug("[PacketVanish] Cached channel field: " + fieldName);
                    return true;
                } catch (NoSuchFieldException ignored) {}
            }

            for (String fieldName : channelFieldNames) {
                try {
                    channelField = networkManagerClass.getDeclaredField(fieldName);
                    channelField.setAccessible(true);
                    plugin.logDebug("[PacketVanish] Cached channel field (private): " + fieldName);
                    return true;
                } catch (NoSuchFieldException ignored) {}
            }

            for (Field field : networkManagerClass.getDeclaredFields()) {
                if (Channel.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    channelField = field;
                    plugin.logDebug("[PacketVanish] Cached channel field by type: " + field.getName());
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            plugin.logDebug("[PacketVanish] Error caching channel field: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the Netty channel for a player

     * @param player The player
     * @return The player's channel, or null if failed
     */
    public Channel getChannel(Player player) {
        if (!initialized) {
            if (!initialize()) {
                return null;
            }
        }

        try {
            Object serverPlayer = getHandleMethod.invoke(player);
            Object playerConnection = connectionField.get(serverPlayer);
            Object networkManager = networkManagerField.get(playerConnection);
            return (Channel) channelField.get(networkManager);
        } catch (Exception e) {
            plugin.logDebug("[PacketVanish] Failed to get channel for " + player.getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract entity ID from a packet
     * Uses cached field lookups for performance

     * @param packet The packet object
     * @return The entity ID, or -1 if not found or not applicable
     */
    public int extractEntityId(Object packet) {
        if (packet == null) return -1;

        String packetClassName = packet.getClass().getSimpleName();

        Field cachedField = packetEntityIdFields.get(packetClassName);
        if (cachedField != null) {
            try {
                Object value = cachedField.get(packet);
                if (value instanceof Integer) {
                    return (Integer) value;
                }
            } catch (Exception ignored) {

            }
        }

        String[] fieldNames = {"entityId", "a", "b", "c", "d", "e"};

        for (String fieldName : fieldNames) {
            try {
                Field field = packet.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(packet);

                if (value instanceof Integer) {
                    packetEntityIdFields.put(packetClassName, field);
                    return (Integer) value;
                }
            } catch (Exception ignored) {}
        }

        for (Field field : packet.getClass().getDeclaredFields()) {
            if (field.getType() == int.class || field.getType() == Integer.class) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(packet);
                    if (value instanceof Integer) {
                        int id = (Integer) value;
                        if (id > 0) {
                            packetEntityIdFields.put(packetClassName, field);
                            return id;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        return -1;
    }

    /**
     * Get the server version string (e.g., "v1_21_R1").
     */
    private String getServerVersion() {
        String packageName = plugin.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    /**
     * Check if the reflection cache is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Check if initialization failed.
     */
    public boolean hasInitializationFailed() {
        return initializationFailed;
    }
}
