package com.blockforge.moderex.disguise;

/**
 * Represents a rank that can be used for disguises.
 */
public class DisguiseRank {

    private final String rankName;
    private final String displayName;
    private final String description;
    private final String prefix;
    private final String skullTexture;

    /**
     * Create a new disguise rank.
     *
     * @param rankName The rank name (identifier)
     * @param displayName The display name
     * @param description The description
     * @param prefix The prefix (for chat/tablist)
     * @param skullTexture The skull texture (Base64 or player name)
     */
    public DisguiseRank(String rankName, String displayName, String description, String prefix, String skullTexture) {
        this.rankName = rankName;
        this.displayName = displayName;
        this.description = description;
        this.prefix = prefix;
        this.skullTexture = skullTexture;
    }

    /**
     * Get the rank name.
     *
     * @return The rank name
     */
    public String getRankName() {
        return rankName;
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
     * Get the description.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the prefix.
     *
     * @return The prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the skull texture.
     *
     * @return The skull texture
     */
    public String getSkullTexture() {
        return skullTexture;
    }
}
