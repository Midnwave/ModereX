package com.blockforge.moderex.disguise;

/**
 * Represents disguise configuration flags.
 */
public class DisguiseFlags {

    private final boolean hideRank;
    private final String fakeRank;
    private final boolean changeTab;
    private final boolean changeTabComplete;

    /**
     * Create disguise flags with default values (all features enabled).
     */
    public DisguiseFlags() {
        this.hideRank = false;
        this.fakeRank = null;
        this.changeTab = true;
        this.changeTabComplete = true;
    }

    /**
     * Create disguise flags with specific values.
     *
     * @param hideRank Hide the player's rank
     * @param fakeRank Display a fake rank (null if not set)
     * @param changeTab Change tab list name
     * @param changeTabComplete Change tab completion
     */
    public DisguiseFlags(boolean hideRank, String fakeRank, boolean changeTab, boolean changeTabComplete) {
        this.hideRank = hideRank;
        this.fakeRank = fakeRank;
        this.changeTab = changeTab;
        this.changeTabComplete = changeTabComplete;
    }

    public boolean isHideRank() {
        return hideRank;
    }

    public String getFakeRank() {
        return fakeRank;
    }

    public boolean isChangeTab() {
        return changeTab;
    }

    public boolean isChangeTabComplete() {
        return changeTabComplete;
    }

    public boolean hasFakeRank() {
        return fakeRank != null && !fakeRank.isEmpty();
    }
}
