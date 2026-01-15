package com.blockforge.moderex.punishment;

public enum PunishmentType {
    BAN("Ban", "banned", true),
    MUTE("Mute", "muted", true),
    KICK("Kick", "kicked", false),
    WARN("Warning", "warned", true),
    IPBAN("IP Ban", "ip banned", true);

    private final String displayName;
    private final String pastTense;
    private final boolean hasDuration;

    PunishmentType(String displayName, String pastTense, boolean hasDuration) {
        this.displayName = displayName;
        this.pastTense = pastTense;
        this.hasDuration = hasDuration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPastTense() {
        return pastTense;
    }

    public boolean hasDuration() {
        return hasDuration;
    }

    public static PunishmentType fromString(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
