package com.blockforge.moderex.punishment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Represents evidence attached to a punishment case.
 * Can be either a file upload or an activity log snapshot.
 */
public class PunishmentEvidence {

    public enum Type {
        FILE,           // File upload (image/video)
        ACTIVITY_LOG    // Activity log entry snapshot
    }

    private final int id;
    private final String punishmentCaseId;
    private final Type evidenceType;
    private final String evidenceId;        // For FILE type
    private final Integer activityLogId;    // For ACTIVITY_LOG type
    private final String activityLogSnapshot; // JSON snapshot
    private final String addedByUuid;
    private final String addedByName;
    private final long addedAt;

    public PunishmentEvidence(int id, String punishmentCaseId, Type evidenceType,
                               String evidenceId, Integer activityLogId, String activityLogSnapshot,
                               String addedByUuid, String addedByName, long addedAt) {
        this.id = id;
        this.punishmentCaseId = punishmentCaseId;
        this.evidenceType = evidenceType;
        this.evidenceId = evidenceId;
        this.activityLogId = activityLogId;
        this.activityLogSnapshot = activityLogSnapshot;
        this.addedByUuid = addedByUuid;
        this.addedByName = addedByName;
        this.addedAt = addedAt;
    }

    public int getId() { return id; }
    public String getPunishmentCaseId() { return punishmentCaseId; }
    public Type getEvidenceType() { return evidenceType; }
    public String getEvidenceId() { return evidenceId; }
    public Integer getActivityLogId() { return activityLogId; }
    public String getActivityLogSnapshot() { return activityLogSnapshot; }
    public String getAddedByUuid() { return addedByUuid; }
    public String getAddedByName() { return addedByName; }
    public long getAddedAt() { return addedAt; }

    public boolean isFile() { return evidenceType == Type.FILE; }
    public boolean isActivityLog() { return evidenceType == Type.ACTIVITY_LOG; }

    /**
     * Get the activity log snapshot as a parsed JSON object.
     */
    public JsonObject getActivityLogAsJson() {
        if (activityLogSnapshot == null || activityLogSnapshot.isEmpty()) {
            return null;
        }
        try {
            return JsonParser.parseString(activityLogSnapshot).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a summary of this evidence for display on disconnect screens.
     * Returns a formatted string like "[CHAT] Player said something".
     */
    public String getSummary() {
        if (isFile()) {
            return "[FILE] Evidence file attached";
        }

        JsonObject snapshot = getActivityLogAsJson();
        if (snapshot == null) {
            return "[ACTIVITY] Log entry #" + activityLogId;
        }

        String type = snapshot.has("type") ? snapshot.get("type").getAsString() : "UNKNOWN";
        String content = snapshot.has("content") ? snapshot.get("content").getAsString() : "";

        // Truncate content if too long
        if (content.length() > 50) {
            content = content.substring(0, 47) + "...";
        }

        return "[" + type.toUpperCase() + "] " + content;
    }
}
