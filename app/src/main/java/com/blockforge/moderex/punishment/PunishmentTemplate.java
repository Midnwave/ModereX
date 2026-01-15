package com.blockforge.moderex.punishment;

import com.google.gson.JsonObject;

import java.util.UUID;

public class PunishmentTemplate {

    private String id;
    private String name;
    private PunishmentType type;
    private String duration;
    private String reason;
    private String category;
    private int priority; // For ordering in GUI
    private boolean active;
    private UUID createdBy;
    private String createdByName;
    private long createdAt;
    private long updatedAt;

    public PunishmentTemplate() {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.active = true;
        this.priority = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public PunishmentTemplate(String name, PunishmentType type, String duration, String reason) {
        this();
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.reason = reason;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public PunishmentType getType() { return type; }
    public void setType(PunishmentType type) {
        this.type = type;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getDuration() { return duration; }
    public void setDuration(String duration) {
        this.duration = duration;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getReason() { return reason; }
    public void setReason(String reason) {
        this.reason = reason;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = System.currentTimeMillis();
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) {
        this.priority = priority;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getDisplayDuration() {
        if (duration == null || duration.isEmpty() || duration.equals("0")) {
            return type == PunishmentType.WARN || type == PunishmentType.KICK ? "N/A" : "Permanent";
        }
        return duration;
    }

    public String getTypeColor() {
        return switch (type) {
            case WARN -> "<yellow>";
            case MUTE -> "<gold>";
            case KICK -> "<red>";
            case BAN -> "<dark_red>";
            case IPBAN -> "<dark_purple>";
        };
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("type", type.name());
        json.addProperty("duration", duration);
        json.addProperty("reason", reason);
        json.addProperty("category", category);
        json.addProperty("priority", priority);
        json.addProperty("active", active);
        json.addProperty("createdBy", createdBy != null ? createdBy.toString() : null);
        json.addProperty("createdByName", createdByName);
        json.addProperty("createdAt", createdAt);
        json.addProperty("updatedAt", updatedAt);
        return json;
    }

    public static PunishmentTemplate fromJson(JsonObject json) {
        PunishmentTemplate template = new PunishmentTemplate();
        template.id = json.get("id").getAsString();
        template.name = json.get("name").getAsString();
        template.type = PunishmentType.valueOf(json.get("type").getAsString());
        template.duration = json.has("duration") && !json.get("duration").isJsonNull()
                ? json.get("duration").getAsString() : "";
        template.reason = json.has("reason") && !json.get("reason").isJsonNull()
                ? json.get("reason").getAsString() : "";
        template.category = json.has("category") && !json.get("category").isJsonNull()
                ? json.get("category").getAsString() : null;
        template.priority = json.has("priority") ? json.get("priority").getAsInt() : 0;
        template.active = !json.has("active") || json.get("active").getAsBoolean();
        template.createdBy = json.has("createdBy") && !json.get("createdBy").isJsonNull()
                ? UUID.fromString(json.get("createdBy").getAsString()) : null;
        template.createdByName = json.has("createdByName") && !json.get("createdByName").isJsonNull()
                ? json.get("createdByName").getAsString() : null;
        template.createdAt = json.has("createdAt") ? json.get("createdAt").getAsLong() : System.currentTimeMillis();
        template.updatedAt = json.has("updatedAt") ? json.get("updatedAt").getAsLong() : System.currentTimeMillis();
        return template;
    }

    @Override
    public String toString() {
        return String.format("Template[%s: %s %s - %s]", id, type, duration, reason);
    }
}
