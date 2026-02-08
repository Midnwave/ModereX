package com.blockforge.moderex.disguise;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Represents a disguise profile for a player.
 */
public class DisguiseProfile {

    private final String displayName;
    private final String skinName;
    private final UUID skinUUID;
    private final String rank;
    private final PlayerProfile playerProfile;
    private final DisguiseFlags flags;

    /**
     * Create a new disguise profile.
     *
     * @param displayName The display name
     * @param skinName The skin name (player to copy skin from)
     * @param rank The rank name (for LuckPerms)
     */
    public DisguiseProfile(String displayName, String skinName, String rank) {
        this(displayName, skinName, rank, new DisguiseFlags());
    }

    /**
     * Create a new disguise profile with flags.
     *
     * @param displayName The display name
     * @param skinName The skin name (player to copy skin from)
     * @param rank The rank name (for LuckPerms)
     * @param flags The disguise flags
     */
    public DisguiseProfile(String displayName, String skinName, String rank, DisguiseFlags flags) {
        this.displayName = displayName;
        this.skinName = skinName;
        this.skinUUID = generateUUID(skinName);
        this.rank = rank;
        this.flags = flags;
        this.playerProfile = createPlayerProfile();
    }

    /**
     * Create a new disguise profile with UUID.
     *
     * @param displayName The display name
     * @param skinUUID The UUID of the player to copy skin from
     * @param rank The rank name (for LuckPerms)
     */
    public DisguiseProfile(String displayName, UUID skinUUID, String rank) {
        this(displayName, skinUUID, rank, new DisguiseFlags());
    }

    /**
     * Create a new disguise profile with UUID and flags.
     *
     * @param displayName The display name
     * @param skinUUID The UUID of the player to copy skin from
     * @param rank The rank name (for LuckPerms)
     * @param flags The disguise flags
     */
    public DisguiseProfile(String displayName, UUID skinUUID, String rank, DisguiseFlags flags) {
        this.displayName = displayName;
        this.skinName = null;
        this.skinUUID = skinUUID;
        this.rank = rank;
        this.flags = flags;
        this.playerProfile = createPlayerProfile();
    }

    /**
     * Get the display name.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the skin name.
     *
     * @return The skin name
     */
    public String getSkinName() {
        return skinName;
    }

    /**
     * Get the skin UUID.
     *
     * @return The skin UUID
     */
    public UUID getSkinUUID() {
        return skinUUID;
    }

    /**
     * Get the rank name.
     *
     * @return The rank name
     */
    public String getRank() {
        return rank;
    }

    /**
     * Get the disguise flags.
     *
     * @return The disguise flags
     */
    public DisguiseFlags getFlags() {
        return flags;
    }

    /**
     * Get the player profile.
     *
     * @return The player profile
     */
    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    /**
     * Create a player profile for this disguise.
     *
     * @return The player profile
     */
    private PlayerProfile createPlayerProfile() {
        PlayerProfile profile = Bukkit.createProfile(skinUUID, displayName);

        // If we have a skin name/UUID, complete the profile to fetch skin data
        if (skinUUID != null) {
            profile.complete(true);
        }

        return profile;
    }

    /**
     * Generate a deterministic UUID from a name.
     *
     * @param name The name
     * @return The UUID
     */
    private UUID generateUUID(String name) {
        if (name == null) {
            return UUID.randomUUID();
        }

        // Use UUID v3 (name-based) to generate deterministic UUID
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
    }
}
