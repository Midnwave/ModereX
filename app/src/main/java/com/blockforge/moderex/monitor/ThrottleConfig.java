package com.blockforge.moderex.monitor;

import com.google.gson.JsonObject;

public class ThrottleConfig {

    private final ThrottleAction action;
    private boolean enabled = false;
    private double engageTpsThreshold = 15.0;
    private double disengageTpsThreshold = 18.0;
    private int cooldownSeconds = 60;
    private double intensity = 0.5; // 0.0-1.0
    private long lastEngagedAt = 0;
    private boolean currentlyEngaged = false;

    public ThrottleConfig(ThrottleAction action) {
        this.action = action;
    }

    public ThrottleAction getAction() { return action; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public double getEngageTpsThreshold() { return engageTpsThreshold; }
    public void setEngageTpsThreshold(double val) { this.engageTpsThreshold = val; }
    public double getDisengageTpsThreshold() { return disengageTpsThreshold; }
    public void setDisengageTpsThreshold(double val) { this.disengageTpsThreshold = val; }
    public int getCooldownSeconds() { return cooldownSeconds; }
    public void setCooldownSeconds(int val) { this.cooldownSeconds = val; }
    public double getIntensity() { return intensity; }
    public void setIntensity(double val) { this.intensity = Math.max(0.0, Math.min(1.0, val)); }
    public long getLastEngagedAt() { return lastEngagedAt; }
    public void setLastEngagedAt(long val) { this.lastEngagedAt = val; }
    public boolean isCurrentlyEngaged() { return currentlyEngaged; }
    public void setCurrentlyEngaged(boolean val) { this.currentlyEngaged = val; }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("action", action.name());
        json.addProperty("displayName", action.getDisplayName());
        json.addProperty("description", action.getDescription());
        json.addProperty("enabled", enabled);
        json.addProperty("engageTpsThreshold", engageTpsThreshold);
        json.addProperty("disengageTpsThreshold", disengageTpsThreshold);
        json.addProperty("cooldownSeconds", cooldownSeconds);
        json.addProperty("intensity", intensity);
        json.addProperty("currentlyEngaged", currentlyEngaged);
        json.addProperty("lastEngagedAt", lastEngagedAt);
        return json;
    }

    public void updateFromJson(JsonObject data) {
        if (data.has("enabled")) enabled = data.get("enabled").getAsBoolean();
        if (data.has("engageTpsThreshold")) engageTpsThreshold = data.get("engageTpsThreshold").getAsDouble();
        if (data.has("disengageTpsThreshold")) disengageTpsThreshold = data.get("disengageTpsThreshold").getAsDouble();
        if (data.has("cooldownSeconds")) cooldownSeconds = data.get("cooldownSeconds").getAsInt();
        if (data.has("intensity")) setIntensity(data.get("intensity").getAsDouble());
    }
}
