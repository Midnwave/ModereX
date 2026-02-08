package com.blockforge.moderex.evidence;

import com.blockforge.moderex.log.ActivityLogEntry;
import com.blockforge.moderex.util.TargetResolver;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents an active in-game evidence selection session.
 * Used when a staff member is selecting activity log entries as evidence
 * for a punishment being issued from in-game.
 */
public class EvidenceSelectionSession {

    private final UUID staffUuid;
    private final String staffName;
    private final TargetResolver target;
    private final String punishmentType;  // BAN, MUTE, WARN, KICK
    private final long duration;
    private final String reason;
    private final long createdAt;
    private final List<ActivityLogEntry> availableEntries;
    private final Set<Long> selectedEntryIds;
    private final Consumer<EvidenceSelectionResult> callback;
    private boolean completed;
    private boolean cancelled;

    // Maximum entries that can be selected
    public static final int MAX_SELECTIONS = 5;

    // Session timeout (5 minutes)
    public static final long SESSION_TIMEOUT_MS = 5 * 60 * 1000;

    public EvidenceSelectionSession(UUID staffUuid, String staffName, TargetResolver target,
                                     String punishmentType, long duration, String reason,
                                     List<ActivityLogEntry> availableEntries,
                                     Consumer<EvidenceSelectionResult> callback) {
        this.staffUuid = staffUuid;
        this.staffName = staffName;
        this.target = target;
        this.punishmentType = punishmentType;
        this.duration = duration;
        this.reason = reason;
        this.createdAt = System.currentTimeMillis();
        this.availableEntries = new ArrayList<>(availableEntries);
        this.selectedEntryIds = new LinkedHashSet<>();
        this.callback = callback;
        this.completed = false;
        this.cancelled = false;
    }

    public UUID getStaffUuid() {
        return staffUuid;
    }

    public String getStaffName() {
        return staffName;
    }

    public TargetResolver getTarget() {
        return target;
    }

    public String getPunishmentType() {
        return punishmentType;
    }

    public long getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<ActivityLogEntry> getAvailableEntries() {
        return Collections.unmodifiableList(availableEntries);
    }

    public Set<Long> getSelectedEntryIds() {
        return Collections.unmodifiableSet(selectedEntryIds);
    }

    public List<ActivityLogEntry> getSelectedEntries() {
        List<ActivityLogEntry> selected = new ArrayList<>();
        for (Long id : selectedEntryIds) {
            for (ActivityLogEntry entry : availableEntries) {
                if (entry.getId() == id) {
                    selected.add(entry);
                    break;
                }
            }
        }
        return selected;
    }

    public int getSelectionCount() {
        return selectedEntryIds.size();
    }

    public boolean hasSelections() {
        return !selectedEntryIds.isEmpty();
    }

    public boolean canSelectMore() {
        return selectedEntryIds.size() < MAX_SELECTIONS;
    }

    /**
     * Toggle selection of an entry by its display number (1-based).
     * @param displayNumber The 1-based number shown in the chat list
     * @return true if toggle was successful, false if invalid number
     */
    public boolean toggleByNumber(int displayNumber) {
        if (displayNumber < 1 || displayNumber > availableEntries.size()) {
            return false;
        }
        ActivityLogEntry entry = availableEntries.get(displayNumber - 1);
        return toggleEntry(entry.getId());
    }

    /**
     * Toggle selection of an entry by ID.
     * @param entryId The activity log entry ID
     * @return true if toggle was successful
     */
    public boolean toggleEntry(long entryId) {
        if (selectedEntryIds.contains(entryId)) {
            selectedEntryIds.remove(entryId);
            return true;
        } else if (canSelectMore()) {
            selectedEntryIds.add(entryId);
            return true;
        }
        return false;
    }

    /**
     * Select entry by ID (add if not already selected).
     */
    public boolean selectEntry(long entryId) {
        if (selectedEntryIds.contains(entryId)) {
            return true; // Already selected
        }
        if (!canSelectMore()) {
            return false; // At max capacity
        }
        selectedEntryIds.add(entryId);
        return true;
    }

    /**
     * Deselect entry by ID.
     */
    public boolean deselectEntry(long entryId) {
        return selectedEntryIds.remove(entryId);
    }

    /**
     * Select multiple entries by display numbers (1-based).
     * @param numbers Comma or space separated numbers like "1,3,5" or "1 3 5"
     * @return List of successfully toggled display numbers
     */
    public List<Integer> selectMultiple(String numbers) {
        List<Integer> toggled = new ArrayList<>();
        String[] parts = numbers.split("[,\\s]+");
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part.trim());
                if (toggleByNumber(num)) {
                    toggled.add(num);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return toggled;
    }

    /**
     * Check if an entry is selected.
     */
    public boolean isSelected(long entryId) {
        return selectedEntryIds.contains(entryId);
    }

    /**
     * Check if an entry (by display number) is selected.
     */
    public boolean isSelectedByNumber(int displayNumber) {
        if (displayNumber < 1 || displayNumber > availableEntries.size()) {
            return false;
        }
        return selectedEntryIds.contains(availableEntries.get(displayNumber - 1).getId());
    }

    /**
     * Check if session has expired.
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > SESSION_TIMEOUT_MS;
    }

    /**
     * Check if session is still active (not completed, cancelled, or expired).
     */
    public boolean isActive() {
        return !completed && !cancelled && !isExpired();
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Confirm the evidence selection and execute the punishment.
     */
    public void confirm() {
        if (completed || cancelled) return;
        completed = true;
        if (callback != null) {
            callback.accept(new EvidenceSelectionResult(this, true));
        }
    }

    /**
     * Cancel the evidence selection (skip without evidence).
     */
    public void cancel() {
        if (completed || cancelled) return;
        cancelled = true;
        if (callback != null) {
            callback.accept(new EvidenceSelectionResult(this, false));
        }
    }

    /**
     * Skip evidence selection and proceed with punishment (no evidence).
     */
    public void skip() {
        selectedEntryIds.clear();
        confirm();
    }

    /**
     * Get time remaining until session expires.
     */
    public long getRemainingTimeMs() {
        long elapsed = System.currentTimeMillis() - createdAt;
        return Math.max(0, SESSION_TIMEOUT_MS - elapsed);
    }

    /**
     * Result of evidence selection.
     */
    public static class EvidenceSelectionResult {
        private final EvidenceSelectionSession session;
        private final boolean confirmed;

        public EvidenceSelectionResult(EvidenceSelectionSession session, boolean confirmed) {
            this.session = session;
            this.confirmed = confirmed;
        }

        public EvidenceSelectionSession getSession() {
            return session;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public boolean hasEvidence() {
            return confirmed && session.hasSelections();
        }

        public List<ActivityLogEntry> getSelectedEntries() {
            return session.getSelectedEntries();
        }

        public TargetResolver getTarget() {
            return session.getTarget();
        }

        public String getPunishmentType() {
            return session.getPunishmentType();
        }

        public long getDuration() {
            return session.getDuration();
        }

        public String getReason() {
            return session.getReason();
        }
    }
}
