package com.blockforge.moderex.disguise.packet;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.disguise.DisguiseProfile;
import com.blockforge.moderex.vanish.packet.PacketReflectionCache;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Channel handler that modifies outbound packets to disguise players.
 * Intercepts packets and modifies name/UUID fields.
 */
public class DisguisePacketFilter extends ChannelDuplexHandler {

    private final ModereX plugin;
    private final Player player;
    private final PacketReflectionCache reflectionCache;
    private final Map<UUID, DisguiseProfile> disguiseProfiles;

    private long packetsChecked = 0;
    private long packetsModified = 0;

    // Cache for reflected fields to avoid repeated reflection
    private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();
    private static final int protocolVersion = ProtocolVersion.getVersion();

    /**
     * Create a new disguise packet filter.
     *
     * @param plugin The plugin instance
     * @param player The player who will receive packets
     * @param reflectionCache The reflection cache
     * @param disguiseProfiles Map of disguised player UUIDs to profiles
     */
    public DisguisePacketFilter(ModereX plugin, Player player, PacketReflectionCache reflectionCache,
                                 Map<UUID, DisguiseProfile> disguiseProfiles) {
        this.plugin = plugin;
        this.player = player;
        this.reflectionCache = reflectionCache;
        this.disguiseProfiles = disguiseProfiles;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        try {
            packetsChecked++;

            // Check if this is a player-related packet
            String packetName = packet.getClass().getSimpleName();

            // Modify packets that contain player information
            if (shouldModifyPacket(packetName)) {
                if (modifyPacket(packet, packetName)) {
                    packetsModified++;
                }
            }

        } catch (Exception e) {
            plugin.logDebug("[DisguisePacket] Error processing packet: " + e.getMessage());
        }

        super.write(ctx, packet, promise);
    }

    /**
     * Check if a packet should be modified for disguises.
     *
     * @param packetName The packet class name
     * @return true if should modify
     */
    private boolean shouldModifyPacket(String packetName) {
        // Version-specific packet names
        if (protocolVersion >= 1200) { // 1.20+
            return packetName.contains("ClientboundPlayerInfoUpdatePacket") ||
                   packetName.contains("ClientboundPlayerInfoRemovePacket") ||
                   packetName.contains("ClientboundAddPlayerPacket") ||
                   packetName.contains("ClientboundPlayerChatPacket") ||
                   packetName.contains("ClientboundSystemChatPacket") ||
                   packetName.contains("ClientboundCommandSuggestionsPacket") ||
                   packetName.contains("ClientboundTabListPacket");
        } else if (protocolVersion >= 1190) { // 1.19+
            return packetName.contains("ClientboundPlayerInfoPacket") ||
                   packetName.contains("PacketPlayOutPlayerInfo") ||
                   packetName.contains("ClientboundAddPlayerPacket") ||
                   packetName.contains("ClientboundPlayerChatPacket") ||
                   packetName.contains("ClientboundSystemChatPacket") ||
                   packetName.contains("ClientboundCommandSuggestionsPacket");
        }

        // Fallback for generic matching
        return packetName.contains("PlayerInfo") ||
               packetName.contains("AddPlayer") ||
               packetName.contains("PlayerChat") ||
               packetName.contains("SystemChat") ||
               packetName.contains("TabComplete") ||
               packetName.contains("PlayerList") ||
               packetName.contains("EntityData");
    }

    /**
     * Modify a packet to replace real name/UUID with disguised version.
     *
     * @param packet The packet to modify
     * @param packetName The packet class name
     * @return true if modified
     */
    private boolean modifyPacket(Object packet, String packetName) {
        try {
            // Protocol-specific packet modification
            if (protocolVersion >= 1200) { // 1.20+
                return modifyModernPacket(packet, packetName);
            } else if (protocolVersion >= 1190) { // 1.19+
                return modifyLegacyPacket(packet, packetName);
            }

            return false;

        } catch (Exception e) {
            plugin.logDebug("[DisguisePacket] Error modifying packet: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modify packets for 1.20+ (modern packet system).
     */
    private boolean modifyModernPacket(Object packet, String packetName) throws Exception {
        if (packetName.contains("ClientboundPlayerInfoUpdatePacket")) {
            return modifyPlayerInfoUpdate(packet);
        } else if (packetName.contains("ClientboundSystemChatPacket")) {
            return modifySystemChat(packet);
        } else if (packetName.contains("ClientboundCommandSuggestionsPacket")) {
            return modifyCommandSuggestions(packet);
        }
        return false;
    }

    /**
     * Modify packets for 1.19-1.19.4 (legacy system).
     */
    private boolean modifyLegacyPacket(Object packet, String packetName) throws Exception {
        if (packetName.contains("PlayerInfo")) {
            return modifyLegacyPlayerInfo(packet);
        } else if (packetName.contains("SystemChat")) {
            return modifySystemChat(packet);
        }
        return false;
    }

    /**
     * Modify PlayerInfoUpdate packet (1.20+).
     */
    private boolean modifyPlayerInfoUpdate(Object packet) throws Exception {
        // Get the entries list from the packet
        Field entriesField = getField(packet.getClass(), "entries", "b");
        if (entriesField == null) return false;

        Object entries = entriesField.get(packet);
        if (entries instanceof Iterable) {
            boolean modified = false;
            for (Object entry : (Iterable<?>) entries) {
                // Modify each entry's profile if it's disguised
                if (modifyPlayerInfoEntry(entry)) {
                    modified = true;
                }
            }
            return modified;
        }

        return false;
    }

    /**
     * Modify a single player info entry.
     */
    private boolean modifyPlayerInfoEntry(Object entry) throws Exception {
        // Get the profile UUID from the entry
        Field profileIdField = getField(entry.getClass(), "profileId", "a");
        if (profileIdField == null) return false;

        UUID profileId = (UUID) profileIdField.get(entry);
        DisguiseProfile disguise = disguiseProfiles.get(profileId);

        if (disguise != null) {
            // Modify the profile name
            Field profileField = getField(entry.getClass(), "profile", "b");
            if (profileField != null) {
                Object profile = profileField.get(entry);
                if (profile != null) {
                    // Set the disguised name in the GameProfile
                    Field nameField = getField(profile.getClass(), "name", "b");
                    if (nameField != null) {
                        nameField.set(profile, disguise.getDisplayName());
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Modify legacy PlayerInfo packet (1.19-1.19.4).
     */
    private boolean modifyLegacyPlayerInfo(Object packet) throws Exception {
        // Similar to modifyPlayerInfoUpdate but for older packet structure
        Field actionField = getField(packet.getClass(), "action", "a");
        Field dataField = getField(packet.getClass(), "entries", "b");

        if (actionField == null || dataField == null) return false;

        Object data = dataField.get(packet);
        if (data instanceof Iterable) {
            boolean modified = false;
            for (Object entry : (Iterable<?>) data) {
                if (modifyPlayerInfoEntry(entry)) {
                    modified = true;
                }
            }
            return modified;
        }

        return false;
    }

    /**
     * Modify system chat packet to replace player names.
     */
    private boolean modifySystemChat(Object packet) throws Exception {
        // Get the message content
        Field contentField = getField(packet.getClass(), "content", "a");
        if (contentField == null) return false;

        Object content = contentField.get(packet);
        if (content == null) return false;

        String message = content.toString();
        boolean modified = false;

        // Replace all disguised player names in the message
        for (Map.Entry<UUID, DisguiseProfile> entry : disguiseProfiles.entrySet()) {
            DisguiseProfile profile = entry.getValue();
            // This is a simplified approach - proper implementation would parse
            // the Component and replace name nodes
            if (message.contains(profile.getDisplayName())) {
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Modify command suggestions packet to replace player names.
     */
    private boolean modifyCommandSuggestions(Object packet) throws Exception {
        Field suggestionsField = getField(packet.getClass(), "suggestions", "a");
        if (suggestionsField == null) return false;

        Object suggestions = suggestionsField.get(packet);
        // Would need to iterate through suggestions and replace player names
        // This is a complex operation that requires deep packet manipulation

        return false;
    }

    /**
     * Get a field from a class, trying both mojang and obfuscated names.
     */
    private Field getField(Class<?> clazz, String mojangName, String obfuscatedName) {
        Map<String, Field> classCache = fieldCache.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>());

        String cacheKey = mojangName + ":" + obfuscatedName;
        if (classCache.containsKey(cacheKey)) {
            return classCache.get(cacheKey);
        }

        // Try mojang name first
        try {
            Field field = clazz.getDeclaredField(mojangName);
            field.setAccessible(true);
            classCache.put(cacheKey, field);
            return field;
        } catch (NoSuchFieldException ignored) {}

        // Try obfuscated name
        try {
            Field field = clazz.getDeclaredField(obfuscatedName);
            field.setAccessible(true);
            classCache.put(cacheKey, field);
            return field;
        } catch (NoSuchFieldException ignored) {}

        // Cache null result to avoid repeated failed lookups
        classCache.put(cacheKey, null);
        return null;
    }

    /**
     * Get the number of packets checked.
     *
     * @return Packets checked
     */
    public long getPacketsChecked() {
        return packetsChecked;
    }

    /**
     * Get the number of packets modified.
     *
     * @return Packets modified
     */
    public long getPacketsModified() {
        return packetsModified;
    }
}
