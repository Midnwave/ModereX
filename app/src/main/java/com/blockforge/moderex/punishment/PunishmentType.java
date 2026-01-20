package com.blockforge.moderex.punishment;

public enum PunishmentType {
    BAN("Ban", "banned", true, "<dark_red>"),
    MUTE("Mute", "muted", true, "<gold>"),
    KICK("Kick", "kicked", false, "<red>"),
    WARN("Warning", "warned", true, "<yellow>"),
    IPBAN("IP Ban", "ip banned", true, "<dark_red>"),
    IPMUTE("IP Mute", "ip muted", true, "<gold>");

    private final String displayName;
    private final String pastTense;
    private final boolean hasDuration;
    private final String color;

    PunishmentType(String displayName, String pastTense, boolean hasDuration, String color) {
        this.displayName = displayName;
        this.pastTense = pastTense;
        this.hasDuration = hasDuration;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public static PunishmentType fromString(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
