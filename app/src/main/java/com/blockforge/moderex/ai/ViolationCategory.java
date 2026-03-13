package com.blockforge.moderex.ai;

/**
 * Categories of content violations detected by AI moderation.
 * Each category can have its own severity level and escalation rules.
 */
public enum ViolationCategory {
    NONE("None", "No violation", Severity.NONE),
    PROFANITY("Profanity", "Swearing, vulgar language", Severity.LOW),
    SLURS("Slurs", "Racial, ethnic, or identity-based slurs", Severity.CRITICAL),
    HARASSMENT("Harassment", "Targeted bullying or harassment", Severity.HIGH),
    TOXICITY("Toxicity", "General toxic behavior, negativity", Severity.MEDIUM),
    SEXUAL_CONTENT("Sexual Content", "Sexually explicit or suggestive content", Severity.HIGH),
    THREATS("Threats", "Threats of violence or harm", Severity.CRITICAL),
    DOXXING("Doxxing", "Sharing personal information", Severity.CRITICAL),
    SPAM("Spam", "Repetitive messages, flooding", Severity.LOW),
    ADVERTISING("Advertising", "Server ads, links, promotions", Severity.MEDIUM),
    SCAM("Scam", "Fraud attempts, phishing", Severity.HIGH),
    IMPERSONATION("Impersonation", "Pretending to be staff or other players", Severity.MEDIUM),
    DISCRIMINATION("Discrimination", "Discriminatory content", Severity.HIGH),
    SELF_HARM("Self Harm", "References to self-harm or suicide", Severity.CRITICAL),
    CHEATING("Cheating", "Discussion of hacks, exploits, cheats", Severity.LOW),
    REAL_MONEY_TRADING("RMT", "Real money trading offers", Severity.MEDIUM);

    private final String displayName;
    private final String description;
    private final Severity defaultSeverity;

    ViolationCategory(String displayName, String description, Severity defaultSeverity) {
        this.displayName = displayName;
        this.description = description;
        this.defaultSeverity = defaultSeverity;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Severity getDefaultSeverity() { return defaultSeverity; }

    /**
     * Parse a category string from AI response, falling back to NONE.
     */
    public static ViolationCategory fromString(String str) {
        if (str == null || str.isEmpty() || "NONE".equalsIgnoreCase(str)) return NONE;
        // Try exact match first
        try {
            return valueOf(str.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException ignored) {}
        // Try display name match
        for (ViolationCategory cat : values()) {
            if (cat.displayName.equalsIgnoreCase(str)) return cat;
        }
        return NONE;
    }

    public enum Severity {
        NONE(0), LOW(1), MEDIUM(2), HIGH(3), CRITICAL(4);

        private final int level;
        Severity(int level) { this.level = level; }
        public int getLevel() { return level; }
    }
}
