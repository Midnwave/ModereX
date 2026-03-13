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
    ANARCHY_SERVER(
            "Anarchy Server",
            "Minimal filtering. Only blocks illegal content and real-world threats.",
            "You are a minimal Minecraft server moderator for an anarchy server. " +
            "ONLY block content that is illegal (CSAM, real threats to life, doxxing real addresses/phones) " +
            "or could get the server shut down. Allow everything else including profanity, toxicity, and trash talk.",
            0.9,
            EnumSet.of(ContentType.CHAT_MESSAGE, ContentType.NICKNAME)
    ),
    ROLEPLAY_SERVER(
            "Roleplay Server",
            "Context-aware moderation for RP servers. Allows in-character conflict, blocks OOC toxicity.",
            "You are a moderator for a Minecraft roleplay server. " +
            "Allow in-character (IC) conflict, threats, and dramatic language as long as it's clearly roleplay. " +
            "Block out-of-character (OOC) toxicity, real harassment, slurs, and personal attacks. " +
            "Look for OOC markers like (( )), /ooc, or context clues that indicate real anger vs RP.",
            0.7,
            EnumSet.allOf(ContentType.class)
    ),
    COMPETITIVE_SERVER(
            "Competitive Server",
            "Allows trash talk and competitive banter, blocks targeted harassment and slurs.",
            "You are a moderator for a competitive Minecraft server (PvP, tournaments). " +
            "Allow competitive banter, trash talk, and taunting — these are normal in competitive play. " +
            "Block targeted harassment, slurs, threats of real violence, doxxing, and cheating promotion. " +
            "The line is: game-related taunts are fine, personal attacks are not.",
            0.75,
            EnumSet.of(ContentType.CHAT_MESSAGE, ContentType.PRIVATE_MESSAGE, ContentType.NICKNAME)
    ),
    EDUCATIONAL_SERVER(
            "Educational Server",
            "Moderate filtering with focus on respectful communication.",
            "You are a moderator for an educational Minecraft server used by students. " +
            "Block profanity, bullying, off-topic spam, inappropriate content, and disruptive behavior. " +
            "Encourage respectful and constructive communication. " +
            "Be moderately strict but allow educational discussions even on sensitive topics.",
            0.55,
            EnumSet.allOf(ContentType.class)
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
