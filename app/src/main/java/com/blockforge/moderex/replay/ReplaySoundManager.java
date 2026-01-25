package com.blockforge.moderex.replay;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

/**
 * Static utility class for playing sounds during replay playback.
 * Provides methods to play appropriate sounds for various player actions.
 */
public final class ReplaySoundManager {

    private ReplaySoundManager() {
        // Utility class
    }

    /**
     * Play a sound at a location for a specific viewer.
     *
     * @param viewer The player to play the sound to
     * @param location The location to play the sound at
     * @param sound The sound to play
     * @param volume Sound volume (0.0 - 1.0)
     * @param pitch Sound pitch (0.5 - 2.0)
     */
    public static void playSound(Player viewer, Location location, Sound sound, float volume, float pitch) {
        if (viewer == null || location == null || sound == null) return;
        viewer.playSound(location, sound, SoundCategory.BLOCKS, volume, pitch);
    }

    /**
     * Get the appropriate sound for placing a block.
     *
     * @param material The material being placed
     * @return The placement sound, or a default if not found
     */
    public static Sound getBlockPlaceSound(Material material) {
        if (material == null || !material.isBlock()) {
            return Sound.BLOCK_STONE_PLACE;
        }

        // Get sound from block's sound group
        try {
            BlockData blockData = material.createBlockData();
            return blockData.getSoundGroup().getPlaceSound();
        } catch (Exception e) {
            return getDefaultPlaceSound(material);
        }
    }

    /**
     * Get the appropriate sound for breaking a block.
     *
     * @param material The material being broken
     * @return The break sound, or a default if not found
     */
    public static Sound getBlockBreakSound(Material material) {
        if (material == null || !material.isBlock()) {
            return Sound.BLOCK_STONE_BREAK;
        }

        // Get sound from block's sound group
        try {
            BlockData blockData = material.createBlockData();
            return blockData.getSoundGroup().getBreakSound();
        } catch (Exception e) {
            return getDefaultBreakSound(material);
        }
    }

    /**
     * Play the block place sound for a material at a location.
     */
    public static void playBlockPlaceSound(Player viewer, Location location, Material material) {
        Sound sound = getBlockPlaceSound(material);
        playSound(viewer, location, sound, 1.0f, 1.0f);
    }

    /**
     * Play the block break sound for a material at a location.
     */
    public static void playBlockBreakSound(Player viewer, Location location, Material material) {
        Sound sound = getBlockBreakSound(material);
        playSound(viewer, location, sound, 1.0f, 1.0f);
    }

    /**
     * Play attack/swing sound.
     */
    public static void playAttackSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.0f);
    }

    /**
     * Play bow shoot sound.
     */
    public static void playBowSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
    }

    /**
     * Play item pickup sound.
     */
    public static void playPickupSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
    }

    /**
     * Play item drop sound.
     */
    public static void playDropSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.8f);
    }

    /**
     * Play eating/consume sound.
     */
    public static void playConsumeSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_PLAYER_BURP, 0.5f, 1.0f);
    }

    /**
     * Play teleport sound.
     */
    public static void playTeleportSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 1.0f);
    }

    /**
     * Play death sound.
     */
    public static void playDeathSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
    }

    /**
     * Play damage sound.
     */
    public static void playDamageSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_PLAYER_HURT, 0.8f, 1.0f);
    }

    /**
     * Play entity hurt sound based on entity type.
     * For players, plays the player hurt sound.
     *
     * @param viewer The player to play the sound to
     * @param location The location to play the sound at
     * @param entityType The type of entity that was hurt (e.g., "PLAYER", "ZOMBIE")
     */
    public static void playEntityHurtSound(Player viewer, Location location, String entityType) {
        if (viewer == null || location == null) return;

        Sound sound = Sound.ENTITY_PLAYER_HURT; // Default to player hurt

        if (entityType != null) {
            // Try to get entity-specific hurt sound
            String soundName = "ENTITY_" + entityType.toUpperCase() + "_HURT";
            Sound entitySound = getSoundSafe(soundName);
            if (entitySound != null) {
                sound = entitySound;
            }
        }

        playSound(viewer, location, sound, 0.8f, 1.0f);
    }

    /**
     * Play hit/attack connection sound.
     *
     * @param viewer The player to play the sound to
     * @param location The location to play the sound at
     * @param hitType The type of hit (e.g., "CRIT", "KNOCKBACK", "SWEEP", "STRONG", "WEAK")
     */
    public static void playHitSound(Player viewer, Location location, String hitType) {
        if (viewer == null || location == null) return;

        Sound sound = switch (hitType != null ? hitType.toUpperCase() : "") {
            case "CRIT", "CRITICAL" -> Sound.ENTITY_PLAYER_ATTACK_CRIT;
            case "KNOCKBACK" -> Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK;
            case "SWEEP" -> Sound.ENTITY_PLAYER_ATTACK_SWEEP;
            case "STRONG" -> Sound.ENTITY_PLAYER_ATTACK_STRONG;
            case "WEAK" -> Sound.ENTITY_PLAYER_ATTACK_WEAK;
            case "NODAMAGE" -> Sound.ENTITY_PLAYER_ATTACK_NODAMAGE;
            default -> Sound.ENTITY_PLAYER_ATTACK_STRONG;
        };

        playSound(viewer, location, sound, 0.8f, 1.0f);
    }

    /**
     * Play fishing rod cast sound.
     */
    public static void playFishCastSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_FISHING_BOBBER_THROW, 0.8f, 1.0f);
    }

    /**
     * Play fishing rod reel sound.
     */
    public static void playFishReelSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 0.8f, 1.0f);
    }

    /**
     * Play portal enter sound.
     */
    public static void playPortalSound(Player viewer, Location location) {
        playSound(viewer, location, Sound.BLOCK_PORTAL_TRIGGER, 0.5f, 1.0f);
    }

    /**
     * Play crossbow shoot sound.
     */
    public static void playCrossbowSound(Player viewer, Location location) {
        Sound crossbowSound = getSoundSafe("ITEM_CROSSBOW_SHOOT");
        if (crossbowSound != null) {
            playSound(viewer, location, crossbowSound, 1.0f, 1.0f);
        } else {
            playSound(viewer, location, Sound.ENTITY_ARROW_SHOOT, 1.0f, 0.8f);
        }
    }

    /**
     * Play spear jab sound (1.21.11+, safewrapped).
     */
    public static void playSpearJabSound(Player viewer, Location location) {
        Sound spearSound = getSoundSafe("ITEM_SPEAR_JAB");
        if (spearSound != null) {
            playSound(viewer, location, spearSound, 1.0f, 1.0f);
        } else {
            playSound(viewer, location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.2f);
        }
    }

    /**
     * Play spear charge sound (1.21.11+, safewrapped).
     */
    public static void playSpearChargeSound(Player viewer, Location location) {
        Sound spearSound = getSoundSafe("ITEM_SPEAR_CHARGE");
        if (spearSound != null) {
            playSound(viewer, location, spearSound, 1.0f, 1.0f);
        } else {
            Sound tridentSound = getSoundSafe("ITEM_TRIDENT_THROW");
            if (tridentSound != null) {
                playSound(viewer, location, tridentSound, 0.8f, 0.9f);
            } else {
                playSound(viewer, location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.9f, 0.8f);
            }
        }
    }

    /**
     * Safely get a Sound by name, handling both enum and interface-based Sound implementations.
     * Works across Minecraft versions where Sound changed from enum to interface.
     */
    private static Sound getSoundSafe(String name) {
        // Try Registry-based lookup (1.21+ where Sound is an interface)
        try {
            org.bukkit.NamespacedKey key = org.bukkit.NamespacedKey.minecraft(name.toLowerCase());
            Sound sound = org.bukkit.Registry.SOUNDS.get(key);
            if (sound != null) return sound;
        } catch (NoSuchFieldError | NoClassDefFoundError | Exception ignored) {
            // Registry.SOUNDS doesn't exist in older versions
        }

        // Try direct field access for enum-based Sound (pre-1.21)
        try {
            return (Sound) Sound.class.getField(name.toUpperCase()).get(null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
            // Field doesn't exist or isn't accessible
        }

        return null;
    }

    /**
     * Play armor equip sound.
     */
    public static void playArmorEquipSound(Player viewer, Location location, String armorType) {
        Sound sound;
        if (armorType != null && armorType.toUpperCase().contains("CHAIN")) {
            sound = Sound.ITEM_ARMOR_EQUIP_CHAIN;
        } else if (armorType != null && (armorType.toUpperCase().contains("IRON") || armorType.toUpperCase().contains("DIAMOND") || armorType.toUpperCase().contains("NETHERITE"))) {
            sound = Sound.ITEM_ARMOR_EQUIP_IRON;
        } else if (armorType != null && armorType.toUpperCase().contains("GOLD")) {
            sound = Sound.ITEM_ARMOR_EQUIP_GOLD;
        } else if (armorType != null && armorType.toUpperCase().contains("LEATHER")) {
            sound = Sound.ITEM_ARMOR_EQUIP_LEATHER;
        } else {
            sound = Sound.ITEM_ARMOR_EQUIP_GENERIC;
        }
        playSound(viewer, location, sound, 0.8f, 1.0f);
    }

    /**
     * Get default place sound based on material category.
     */
    private static Sound getDefaultPlaceSound(Material material) {
        String name = material.name();

        if (name.contains("WOOD") || name.contains("LOG") || name.contains("PLANK")) {
            return Sound.BLOCK_WOOD_PLACE;
        } else if (name.contains("GLASS")) {
            return Sound.BLOCK_GLASS_PLACE;
        } else if (name.contains("SAND") || name.contains("GRAVEL")) {
            return Sound.BLOCK_SAND_PLACE;
        } else if (name.contains("GRASS") || name.contains("DIRT")) {
            return Sound.BLOCK_GRASS_PLACE;
        } else if (name.contains("WOOL") || name.contains("CARPET")) {
            return Sound.BLOCK_WOOL_PLACE;
        } else if (name.contains("METAL") || name.contains("IRON") || name.contains("GOLD")) {
            return Sound.BLOCK_METAL_PLACE;
        } else if (name.contains("SNOW")) {
            return Sound.BLOCK_SNOW_PLACE;
        } else if (name.contains("NETHERRACK") || name.contains("NETHER")) {
            return Sound.BLOCK_NETHERRACK_PLACE;
        }

        return Sound.BLOCK_STONE_PLACE;
    }

    /**
     * Get default break sound based on material category.
     */
    private static Sound getDefaultBreakSound(Material material) {
        String name = material.name();

        if (name.contains("WOOD") || name.contains("LOG") || name.contains("PLANK")) {
            return Sound.BLOCK_WOOD_BREAK;
        } else if (name.contains("GLASS")) {
            return Sound.BLOCK_GLASS_BREAK;
        } else if (name.contains("SAND") || name.contains("GRAVEL")) {
            return Sound.BLOCK_SAND_BREAK;
        } else if (name.contains("GRASS") || name.contains("DIRT")) {
            return Sound.BLOCK_GRASS_BREAK;
        } else if (name.contains("WOOL") || name.contains("CARPET")) {
            return Sound.BLOCK_WOOL_BREAK;
        } else if (name.contains("METAL") || name.contains("IRON") || name.contains("GOLD")) {
            return Sound.BLOCK_METAL_BREAK;
        } else if (name.contains("SNOW")) {
            return Sound.BLOCK_SNOW_BREAK;
        } else if (name.contains("NETHERRACK") || name.contains("NETHER")) {
            return Sound.BLOCK_NETHERRACK_BREAK;
        }

        return Sound.BLOCK_STONE_BREAK;
    }
}
