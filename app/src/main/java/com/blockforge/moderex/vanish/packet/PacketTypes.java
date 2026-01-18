package com.blockforge.moderex.vanish.packet;

import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive packet type registry for vanishing across Minecraft 1.17-1.21+.
 * Includes all packets that could reveal a player's presence.
 */
public enum PacketTypes {

    // critical visibility - taken from awesomevanish's methods
    /**
     * Spawn player entity packet (1.19+: combined with ADD_ENTITY)
     * Critical: Prevents player from appearing visually
     */
    ADD_PLAYER("ClientboundAddPlayerPacket", "ClientboundPlayerInfoPacket", true, PacketPriority.CRITICAL),

    /**
     * Spawn entity packet (1.19+: includes players)
     * Critical: Prevents entity from spawning in world
     */
    ADD_ENTITY("ClientboundAddEntityPacket", "ClientboundSpawnEntityPacket", true, PacketPriority.CRITICAL),

    /**
     * Player info update packet (tab list, etc.)
     * Critical: Prevents player from appearing in tab list
     */
    PLAYER_INFO_UPDATE("ClientboundPlayerInfoUpdatePacket", "ClientboundPlayerInfoPacket", false, PacketPriority.CRITICAL),

    /**
     * Remove player from tab list
     * Should NOT be blocked (we want to remove vanished players from tab)
     */
    PLAYER_INFO_REMOVE("ClientboundPlayerInfoRemovePacket", "ClientboundPlayerInfoPacket", false, PacketPriority.IGNORE),

    // metadata & appearance

    /**
     * Entity metadata (armor, effects, crouching, invisibility, etc.)
     * High priority: Reveals player state changes
     */
    SET_ENTITY_DATA("ClientboundSetEntityDataPacket", "ClientboundEntityMetadataPacket", true, PacketPriority.HIGH),

    /**
     * Equipment changes (armor, held items)
     * High priority: Reveals what player is wearing/holding
     */
    SET_EQUIPMENT("ClientboundSetEquipmentPacket", "ClientboundEntityEquipmentPacket", true, PacketPriority.HIGH),

    /**
     * Entity attributes (health, speed, etc.)
     * Medium priority: Less visually obvious but reveals presence
     */
    UPDATE_ATTRIBUTES("ClientboundUpdateAttributesPacket", "ClientboundEntityPropertiesPacket", true, PacketPriority.MEDIUM),

    /**
     * Head rotation
     * Medium priority: Shows where player is looking
     */
    ROTATE_HEAD("ClientboundRotateHeadPacket", "ClientboundEntityHeadRotationPacket", true, PacketPriority.MEDIUM),

    // movement

    /**
     * Entity position change
     * High priority: Shows player moving
     */
    MOVE_ENTITY_POS("ClientboundMoveEntityPacket$Pos", "ClientboundEntity$PacketPlayOutRelEntityMove", true, PacketPriority.HIGH),

    /**
     * Entity rotation change
     * High priority: Shows player turning
     */
    MOVE_ENTITY_ROT("ClientboundMoveEntityPacket$Rot", "ClientboundEntity$PacketPlayOutEntityLook", true, PacketPriority.HIGH),

    /**
     * Entity position and rotation change
     * High priority: Shows player moving and turning
     */
    MOVE_ENTITY_POS_ROT("ClientboundMoveEntityPacket$PosRot", "ClientboundEntity$PacketPlayOutRelEntityMoveLook", true, PacketPriority.HIGH),

    /**
     * Base movement packet class
     * High priority: Catches any movement
     */
    MOVE_ENTITY("ClientboundMoveEntityPacket", "ClientboundEntity", true, PacketPriority.HIGH),

    /**
     * Teleport entity (large movements)
     * High priority: Shows player teleporting/falling
     */
    TELEPORT_ENTITY("ClientboundTeleportEntityPacket", "ClientboundEntityTeleportPacket", true, PacketPriority.HIGH),

    /**
     * Entity velocity/knockback
     * Medium priority: Shows player being pushed
     */
    SET_ENTITY_MOTION("ClientboundSetEntityMotionPacket", "ClientboundEntityVelocityPacket", true, PacketPriority.MEDIUM),

    // animations & fxs

    /**
     * Entity animations (swing arm, hurt, etc.)
     * High priority: Very visible player actions
     */
    ANIMATE("ClientboundAnimatePacket", "ClientboundEntityAnimationPacket", true, PacketPriority.HIGH),

    /**
     * Entity status (death, hurt flash, etc.)
     * High priority: Shows player taking damage or dying
     */
    ENTITY_EVENT("ClientboundEntityEventPacket", "ClientboundEntityStatusPacket", true, PacketPriority.HIGH),

    /**
     * Damage event (1.19.3+)
     * High priority: Shows damage source and amount
     */
    DAMAGE_EVENT("ClientboundDamageEventPacket", "ClientboundHurtAnimationPacket", true, PacketPriority.HIGH),

    /**
     * Entity effect (potion effects)
     * Medium priority: Shows effects on player
     */
    UPDATE_MOB_EFFECT("ClientboundUpdateMobEffectPacket", "ClientboundEntityEffectPacket", true, PacketPriority.MEDIUM),

    /**
     * Remove entity effect
     * Medium priority: Shows effects being removed
     */
    REMOVE_MOB_EFFECT("ClientboundRemoveMobEffectPacket", "ClientboundRemoveEntityEffectPacket", true, PacketPriority.MEDIUM),

    // interaction packets

    /**
     * Item pickup animation
     * Medium priority: Shows player collecting items
     */
    TAKE_ITEM_ENTITY("ClientboundTakeItemEntityPacket", "ClientboundCollectPacket", true, PacketPriority.MEDIUM),

    /**
     * Entity attach/mount
     * Medium priority: Shows player riding something
     */
    SET_ENTITY_LINK("ClientboundSetEntityLinkPacket", "ClientboundAttachEntityPacket", true, PacketPriority.MEDIUM),

    /**
     * Entity passengers (vehicle mounting)
     * Medium priority: Shows who is riding what
     */
    SET_PASSENGERS("ClientboundSetPassengersPacket", "ClientboundMountPacket", true, PacketPriority.MEDIUM),

    /**
     * Remove entities packet
     * Should NOT be blocked (we want to hide vanished players)
     */
    REMOVE_ENTITIES("ClientboundRemoveEntitiesPacket", "ClientboundRemoveEntityPacket", false, PacketPriority.IGNORE),

    // sfx packets

    /**
     * Entity sound (footsteps, hurt, etc.)
     * High priority: Audio reveals player location
     */
    SOUND_ENTITY("ClientboundSoundEntityPacket", "ClientboundNamedSoundEffectPacket", true, PacketPriority.HIGH),

    /**
     * General sound packet (may include entity source)
     * Medium priority: Can reveal player if entity-sourced
     */
    SOUND("ClientboundSoundPacket", "ClientboundNamedSoundEffectPacket", false, PacketPriority.MEDIUM),

    // spawn & bed

    /**
     * Player sleep packet
     * High priority: Shows player in bed
     */
    SET_ENTITY_SLEEP("ClientboundSetEntitySleepPacket", "ClientboundBedPacket", true, PacketPriority.HIGH),

    /**
     * Player abilities (flying, invulnerable, etc.)
     * Low priority: Usually only for the player themselves
     */
    PLAYER_ABILITIES("ClientboundPlayerAbilitiesPacket", "ClientboundAbilitiesPacket", false, PacketPriority.LOW),

    // chunk / tracking (wrapper)

    /**
     * Entity destroy/remove (legacy)
     * Should NOT be blocked
     */
    ENTITY_DESTROY("ClientboundRemoveEntitiesPacket", "ClientboundEntityDestroyPacket", false, PacketPriority.IGNORE),

    // combat / dmg

    /**
     * Combat event (enter/leave combat)
     * Medium priority: Shows combat state
     */
    PLAYER_COMBAT_START("ClientboundPlayerCombatStartPacket", "ClientboundCombatEventPacket", false, PacketPriority.MEDIUM),

    /**
     * Combat end event
     * Medium priority: Shows combat ending
     */
    PLAYER_COMBAT_END("ClientboundPlayerCombatEndPacket", "ClientboundCombatEventPacket", false, PacketPriority.MEDIUM),

    /**
     * Player death packet
     * High priority: Shows player died
     */
    PLAYER_COMBAT_KILL("ClientboundPlayerCombatKillPacket", "ClientboundCombatEventPacket", false, PacketPriority.HIGH);

    private final List<String> classNames;
    private final boolean hasEntityId;
    private final PacketPriority priority;

    PacketTypes(String primaryName, String alternateName, boolean hasEntityId, PacketPriority priority) {
        this.classNames = Arrays.asList(primaryName, alternateName);
        this.hasEntityId = hasEntityId;
        this.priority = priority;
    }

    /**
     * Get all possible class names for this packet type.
     */
    public List<String> getClassNames() {
        return classNames;
    }

    /**
     * Get the primary (modern 1.17+) class name.
     */
    public String getPrimaryClassName() {
        return classNames.get(0);
    }

    /**
     * Check if this packet type typically contains an entity ID field.
     */
    public boolean hasEntityId() {
        return hasEntityId;
    }

    /**
     * Get the filtering priority for this packet.
     */
    public PacketPriority getPriority() {
        return priority;
    }

    /**
     * Check if this packet should be ignored (not filtered).
     */
    public boolean shouldIgnore() {
        return priority == PacketPriority.IGNORE;
    }

    /**
     * Check if a packet matches this type by class name.
     *
     * @param packet The packet object
     * @return true if the packet matches this type
     */
    public boolean matches(Object packet) {
        if (packet == null) return false;

        String packetClassName = packet.getClass().getSimpleName();
        String packetFullName = packet.getClass().getName();

        for (String className : classNames) {
            if (packetClassName.equals(className)) {
                return true;
            }

            if (className.contains("$")) {
                String outerClass = className.substring(0, className.indexOf('$'));
                String innerClass = className.substring(className.indexOf('$') + 1);

                if (packetClassName.equals(innerClass) &&
                    packet.getClass().getEnclosingClass() != null &&
                    packet.getClass().getEnclosingClass().getSimpleName().equals(outerClass)) {
                    return true;
                }
            }

            if (packetFullName.contains(className.replace("Clientbound", ""))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find a matching packet type for the given packet.
     *
     * @param packet The packet object
     * @return The matching PacketType, or null if not a vanish-relevant packet
     */
    public static PacketTypes findMatchingType(Object packet) {
        if (packet == null) return null;

        for (PacketTypes type : values()) {
            if (type.matches(packet)) {
                return type;
            }
        }

        return null;
    }

    /**
     * Check if a packet is relevant for vanishing (should be checked for filtering).
     *
     * @param packet The packet object
     * @return true if this packet should be checked for vanishing
     */
    public static boolean isVanishRelevant(Object packet) {
        PacketTypes type = findMatchingType(packet);
        return type != null && !type.shouldIgnore();
    }

    /**
     * Packet filtering priority levels.
     */
    public enum PacketPriority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW,
        IGNORE
    }
}
