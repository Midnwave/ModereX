package com.blockforge.moderex.ai;

/**
 * Result of an AI moderation check on content.
 */
public class ModerationResult {

    public static final ModerationResult ALLOW_FALLBACK = new ModerationResult(
            ModerationAction.ALLOW, 0.0, "AI unavailable - fallback to allow", "NONE");

    private ModerationAction action = ModerationAction.ALLOW;
    private double confidence = 0.0;
    private String reason = "";
    private String category = "NONE";
    private ViolationCategory violationCategory = ViolationCategory.NONE;
    private String detectedLanguage = "en";

    public ModerationResult() {
    }

    public ModerationResult(ModerationAction action, double confidence, String reason, String category) {
        this.action = action;
        this.confidence = confidence;
        this.reason = reason;
        this.category = category;
    }

    public ModerationAction getAction() {
        return action;
    }

    public void setAction(ModerationAction action) {
        this.action = action;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ViolationCategory getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(ViolationCategory violationCategory) {
        this.violationCategory = violationCategory;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public boolean shouldBlock() {
        return action == ModerationAction.BLOCK;
    }

    public boolean shouldWarn() {
        return action == ModerationAction.WARN;
    }

    public boolean shouldFlag() {
        return action == ModerationAction.FLAG_FOR_REVIEW;
    }

    @Override
    public String toString() {
        return "ModerationResult{action=" + action + ", confidence=" + confidence +
                ", reason='" + reason + "', category='" + category +
                "', violationCategory=" + violationCategory +
                ", detectedLanguage='" + detectedLanguage + "'}";
    }
}
