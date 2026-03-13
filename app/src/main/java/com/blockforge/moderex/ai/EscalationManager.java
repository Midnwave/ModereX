package com.blockforge.moderex.ai;

import com.blockforge.moderex.ModereX;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages escalation tiers for AI moderation.
 * Tracks violations per player and escalates actions based on history.
 */
public class EscalationManager {

    private final ModereX plugin;

    // Player UUID -> list of recent violations (last 24h)
    private final Map<UUID, List<ViolationRecord>> playerHistory = new ConcurrentHashMap<>();

    // Configurable escalation tiers (ordered by threshold)
    private final List<EscalationTier> tiers = new ArrayList<>();

    public EscalationManager(ModereX plugin) {
        this.plugin = plugin;
        loadDefaultTiers();
    }

    private void loadDefaultTiers() {
        tiers.clear();
        // Default escalation: warn -> mute -> tempban
        tiers.add(new EscalationTier("Warning", 1, ModerationAction.WARN, null, 0));
        tiers.add(new EscalationTier("Mute 5m", 3, ModerationAction.BLOCK, "mute", 300));
        tiers.add(new EscalationTier("Mute 30m", 5, ModerationAction.BLOCK, "mute", 1800));
        tiers.add(new EscalationTier("Tempban 1h", 8, ModerationAction.BLOCK, "tempban", 3600));
        tiers.add(new EscalationTier("Tempban 1d", 12, ModerationAction.BLOCK, "tempban", 86400));
    }

    /**
     * Record a violation and return the escalated action.
     */
    public EscalatedAction recordAndEscalate(UUID playerUuid, ViolationCategory category,
                                              ModerationAction originalAction) {
        if (originalAction == ModerationAction.ALLOW) {
            return new EscalatedAction(ModerationAction.ALLOW, null, 0, null);
        }

        // Add to history
        playerHistory.computeIfAbsent(playerUuid, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new ViolationRecord(category, System.currentTimeMillis()));

        // Clean old entries (24h window)
        cleanHistory(playerUuid);

        // Count recent violations
        int recentCount = getRecentViolationCount(playerUuid);

        // Find matching tier
        EscalationTier matchedTier = null;
        for (int i = tiers.size() - 1; i >= 0; i--) {
            if (recentCount >= tiers.get(i).threshold) {
                matchedTier = tiers.get(i);
                break;
            }
        }

        if (matchedTier != null) {
            return new EscalatedAction(matchedTier.action, matchedTier.punishmentType,
                    matchedTier.durationSeconds, matchedTier.name);
        }

        return new EscalatedAction(originalAction, null, 0, null);
    }

    public int getRecentViolationCount(UUID playerUuid) {
        List<ViolationRecord> history = playerHistory.get(playerUuid);
        if (history == null) return 0;
        long cutoff = System.currentTimeMillis() - 86400000L; // 24h
        return (int) history.stream().filter(r -> r.timestamp > cutoff).count();
    }

    private void cleanHistory(UUID playerUuid) {
        List<ViolationRecord> history = playerHistory.get(playerUuid);
        if (history == null) return;
        long cutoff = System.currentTimeMillis() - 86400000L;
        history.removeIf(r -> r.timestamp < cutoff);
    }

    public void clearPlayerHistory(UUID playerUuid) {
        playerHistory.remove(playerUuid);
    }

    public void updateTiers(List<EscalationTier> newTiers) {
        tiers.clear();
        tiers.addAll(newTiers);
        tiers.sort(Comparator.comparingInt(t -> t.threshold));
    }

    public List<EscalationTier> getTiers() {
        return Collections.unmodifiableList(tiers);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray tiersArr = new JsonArray();
        for (EscalationTier tier : tiers) {
            JsonObject t = new JsonObject();
            t.addProperty("name", tier.name);
            t.addProperty("threshold", tier.threshold);
            t.addProperty("action", tier.action.name());
            t.addProperty("punishmentType", tier.punishmentType != null ? tier.punishmentType : "");
            t.addProperty("durationSeconds", tier.durationSeconds);
            tiersArr.add(t);
        }
        json.add("tiers", tiersArr);
        return json;
    }

    public record EscalationTier(String name, int threshold, ModerationAction action,
                                  String punishmentType, long durationSeconds) {}

    public record EscalatedAction(ModerationAction action, String punishmentType,
                                   long durationSeconds, String tierName) {}

    private record ViolationRecord(ViolationCategory category, long timestamp) {}
}
