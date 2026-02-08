package com.blockforge.moderex.api.anticheat;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a violation event from an anticheat.
 *
 * <p>This class carries all information about a detected violation that
 * ModereX needs to process alerts, notifications, and automod actions.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * // Simple violation
 * AnticheatViolation violation = new AnticheatViolation.Builder()
 *     .player(player)
 *     .checkName("speed")
 *     .violationLevel(15)
 *     .build();
 *
 * // Detailed violation
 * AnticheatViolation violation = new AnticheatViolation.Builder()
 *     .player(player)
 *     .checkName("reach")
 *     .violationLevel(8)
 *     .details("Distance: 4.5 blocks (max: 3.0)")
 *     .experimental(false)
 *     .ping(player.getPing())
 *     .build();
 *
 * // Send to ModereX
 * alertHandler.accept(violation);
 * }</pre>
 */
public class AnticheatViolation {

    private final UUID playerUuid;
    private final String playerName;
    private final String checkName;
    private final int violationLevel;
    private final int totalViolations;
    private final String details;
    private final boolean experimental;
    private final int ping;
    private final long timestamp;

    private AnticheatViolation(Builder builder) {
        this.playerUuid = builder.playerUuid;
        this.playerName = builder.playerName;
        this.checkName = builder.checkName;
        this.violationLevel = builder.violationLevel;
        this.totalViolations = builder.totalViolations;
        this.details = builder.details;
        this.experimental = builder.experimental;
        this.ping = builder.ping;
        this.timestamp = builder.timestamp > 0 ? builder.timestamp : System.currentTimeMillis();
    }

    /**
     * Get the UUID of the flagged player.
     *
     * @return player UUID
     */
    public UUID getPlayerUuid() {
        return playerUuid;
    }

    /**
     * Get the name of the flagged player.
     *
     * @return player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get the name of the check that was triggered.
     *
     * @return check name
     */
    public String getCheckName() {
        return checkName;
    }

    /**
     * Get the violation level (VL) for this alert.
     * This represents the severity or confidence of the detection.
     *
     * @return violation level
     */
    public int getViolationLevel() {
        return violationLevel;
    }

    /**
     * Get the total cumulative violations for this check on this player.
     *
     * @return total violations, or 0 if not tracked
     */
    public int getTotalViolations() {
        return totalViolations;
    }

    /**
     * Get additional details about this violation.
     *
     * @return details string, or empty string if none
     */
    public String getDetails() {
        return details;
    }

    /**
     * Whether this check is marked as experimental.
     * Experimental checks may have higher false positive rates.
     *
     * @return true if experimental
     */
    public boolean isExperimental() {
        return experimental;
    }

    /**
     * Get the player's ping at the time of detection.
     *
     * @return ping in milliseconds, or -1 if unknown
     */
    public int getPing() {
        return ping;
    }

    /**
     * Get the timestamp when this violation occurred.
     *
     * @return timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Builder for creating AnticheatViolation instances.
     */
    public static class Builder {
        private UUID playerUuid;
        private String playerName;
        private String checkName;
        private int violationLevel = 1;
        private int totalViolations = 0;
        private String details = "";
        private boolean experimental = false;
        private int ping = -1;
        private long timestamp = 0;

        public Builder() {
        }

        /**
         * Set the player who triggered this violation.
         * This automatically sets both UUID and name.
         *
         * @param player the Bukkit player
         * @return this builder
         */
        public Builder player(Player player) {
            Objects.requireNonNull(player, "Player cannot be null");
            this.playerUuid = player.getUniqueId();
            this.playerName = player.getName();
            return this;
        }

        /**
         * Set the player UUID directly.
         *
         * @param uuid player UUID
         * @return this builder
         */
        public Builder playerUuid(UUID uuid) {
            this.playerUuid = uuid;
            return this;
        }

        /**
         * Set the player name directly.
         *
         * @param name player name
         * @return this builder
         */
        public Builder playerName(String name) {
            this.playerName = name;
            return this;
        }

        /**
         * Set the name of the check that was triggered.
         *
         * @param checkName check name (e.g., "speed", "reach", "killaura")
         * @return this builder
         */
        public Builder checkName(String checkName) {
            this.checkName = checkName;
            return this;
        }

        /**
         * Set the violation level for this alert.
         * Higher values indicate more severe or confident detections.
         *
         * @param vl violation level
         * @return this builder
         */
        public Builder violationLevel(int vl) {
            this.violationLevel = vl;
            return this;
        }

        /**
         * Set the total cumulative violations.
         * Use this if your anticheat tracks total VL over time.
         *
         * @param total total violations
         * @return this builder
         */
        public Builder totalViolations(int total) {
            this.totalViolations = total;
            return this;
        }

        /**
         * Set additional details about this violation.
         * This can include specific values that triggered the detection.
         *
         * @param details details string
         * @return this builder
         */
        public Builder details(String details) {
            this.details = details != null ? details : "";
            return this;
        }

        /**
         * Mark this check as experimental.
         *
         * @param experimental true if experimental
         * @return this builder
         */
        public Builder experimental(boolean experimental) {
            this.experimental = experimental;
            return this;
        }

        /**
         * Set the player's ping.
         *
         * @param ping ping in milliseconds
         * @return this builder
         */
        public Builder ping(int ping) {
            this.ping = ping;
            return this;
        }

        /**
         * Set a custom timestamp.
         * If not set, the current time will be used.
         *
         * @param timestamp timestamp in milliseconds
         * @return this builder
         */
        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * Build the AnticheatViolation.
         *
         * @return the built violation
         * @throws IllegalStateException if required fields are missing
         */
        public AnticheatViolation build() {
            if (playerUuid == null) {
                throw new IllegalStateException("Player UUID is required");
            }
            if (playerName == null) {
                throw new IllegalStateException("Player name is required");
            }
            if (checkName == null || checkName.isBlank()) {
                throw new IllegalStateException("Check name is required");
            }
            return new AnticheatViolation(this);
        }
    }
}
