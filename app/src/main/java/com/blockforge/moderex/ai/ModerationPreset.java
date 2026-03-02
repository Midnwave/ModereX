package com.blockforge.moderex.ai;

import java.util.EnumSet;
import java.util.Set;

/**
 * Community presets for AI moderation behavior.
 * Users pick a preset as a starting point then customize toggles.
 */
public enum ModerationPreset {
    FAMILY_SERVER(
            "Family Server",
            "Strict moderation suitable for all ages. Blocks profanity, suggestive content, and toxic behavior.",
            "You are a strict Minecraft server moderator for a family-friendly server. " +
            "Block ANY profanity (even mild), suggestive content, toxic behavior, bullying, " +
            "discrimination, excessive caps, spam, advertising, and inappropriate references. " +
            "Be very strict - when in doubt, block the message.",
            0.5, // Low confidence threshold = more blocking
            EnumSet.allOf(ContentType.class)
    ),
    TEEN_SERVER(
            "Teen Server",
            "Moderate filtering. Allows mild language but blocks slurs, harassment, and explicit content.",
            "You are a Minecraft server moderator for a teen-friendly server (13+). " +
            "Block slurs, harassment, explicit sexual content, serious threats, doxxing, " +
            "and extreme toxicity. Allow mild language, playful trash-talk, and gaming banter. " +
            "Focus on intent - friendly teasing is fine, targeted harassment is not.",
            0.65,
            EnumSet.allOf(ContentType.class)
    ),
    MATURE_SERVER(
            "Mature Server",
            "Light filtering. Only blocks severe content like slurs, threats, and doxxing.",
            "You are a Minecraft server moderator for a mature server (18+). " +
            "Only block severe content: racial/ethnic slurs, real threats of violence, " +
            "doxxing/personal information sharing, sexual content involving minors, " +
            "and targeted harassment campaigns. Allow strong language and adult humor.",
            0.8,
            EnumSet.of(ContentType.CHAT_MESSAGE, ContentType.PRIVATE_MESSAGE, ContentType.NICKNAME)
    ),
    CUSTOM(
            "Custom",
            "Fully customizable moderation settings.",
            "You are a Minecraft server chat moderator. Analyze content for rule violations. " +
            "Respond based on your configuration.",
            0.7,
            EnumSet.allOf(ContentType.class)
    );

    private final String displayName;
    private final String description;
    private final String systemPrompt;
    private final double defaultConfidenceThreshold;
    private final Set<ContentType> defaultContentTypes;

    ModerationPreset(String displayName, String description, String systemPrompt,
                     double defaultConfidenceThreshold, Set<ContentType> defaultContentTypes) {
        this.displayName = displayName;
        this.description = description;
        this.systemPrompt = systemPrompt;
        this.defaultConfidenceThreshold = defaultConfidenceThreshold;
        this.defaultContentTypes = defaultContentTypes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public double getDefaultConfidenceThreshold() {
        return defaultConfidenceThreshold;
    }

    public Set<ContentType> getDefaultContentTypes() {
        return defaultContentTypes;
    }
}
