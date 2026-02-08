package com.blockforge.moderex.automod.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all automod events.
 * Other plugins can listen to these events to react to automod actions.
 */
public abstract class AutomodEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    protected final Player player;
    protected final String ruleName;
    protected final String ruleId;

    public AutomodEvent(Player player, String ruleId, String ruleName) {
        this.player = player;
        this.ruleId = ruleId;
        this.ruleName = ruleName;
    }

    /**
     * Get the player involved in this event.
     *
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the rule ID that triggered this event.
     *
     * @return The rule ID
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Get the rule name that triggered this event.
     *
     * @return The rule name
     */
    public String getRuleName() {
        return ruleName;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // ==================== SPECIFIC EVENTS ====================

    /**
     * Event fired when a message is blocked by automod.
     * This event is cancellable - cancelling it will allow the message through.
     */
    public static class MessageBlockedEvent extends AutomodEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private final String message;
        private final String reason;
        private boolean cancelled = false;

        public MessageBlockedEvent(Player player, String ruleId, String ruleName, String message, String reason) {
            super(player, ruleId, ruleName);
            this.message = message;
            this.reason = reason;
        }

        /**
         * Get the message that was blocked.
         *
         * @return The blocked message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Get the reason for blocking.
         *
         * @return The block reason
         */
        public String getReason() {
            return reason;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    /**
     * Event fired when a message is modified by automod (e.g., caps lowered).
     * This event is cancellable - cancelling it will use the original message.
     */
    public static class MessageModifiedEvent extends AutomodEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private final String originalMessage;
        private String modifiedMessage;
        private boolean cancelled = false;

        public MessageModifiedEvent(Player player, String ruleId, String ruleName, String originalMessage, String modifiedMessage) {
            super(player, ruleId, ruleName);
            this.originalMessage = originalMessage;
            this.modifiedMessage = modifiedMessage;
        }

        /**
         * Get the original message before modification.
         *
         * @return The original message
         */
        public String getOriginalMessage() {
            return originalMessage;
        }

        /**
         * Get the modified message.
         *
         * @return The modified message
         */
        public String getModifiedMessage() {
            return modifiedMessage;
        }

        /**
         * Set a custom modified message.
         *
         * @param modifiedMessage The new modified message
         */
        public void setModifiedMessage(String modifiedMessage) {
            this.modifiedMessage = modifiedMessage;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    /**
     * Event fired when a violation is recorded against a player.
     */
    public static class ViolationEvent extends AutomodEvent {

        private static final HandlerList handlers = new HandlerList();
        private final int violationCount;
        private final int triggerThreshold;

        public ViolationEvent(Player player, String ruleId, String ruleName, int violationCount, int triggerThreshold) {
            super(player, ruleId, ruleName);
            this.violationCount = violationCount;
            this.triggerThreshold = triggerThreshold;
        }

        /**
         * Get the current violation count for this player.
         *
         * @return Current violation count
         */
        public int getViolationCount() {
            return violationCount;
        }

        /**
         * Get the threshold that triggers auto-punishment.
         *
         * @return Trigger threshold
         */
        public int getTriggerThreshold() {
            return triggerThreshold;
        }

        /**
         * Check if this violation will trigger auto-punishment.
         *
         * @return true if punishment will be triggered
         */
        public boolean willTriggerPunishment() {
            return violationCount >= triggerThreshold;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    /**
     * Event fired when auto-punishment is about to be applied.
     * This event is cancellable - cancelling it will prevent the punishment.
     */
    public static class AutoPunishmentEvent extends AutomodEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private final AutomodAPI.AutoPunishmentType punishmentType;
        private final long duration;
        private final String reason;
        private boolean cancelled = false;

        public AutoPunishmentEvent(Player player, String ruleId, String ruleName,
                                   AutomodAPI.AutoPunishmentType punishmentType, long duration, String reason) {
            super(player, ruleId, ruleName);
            this.punishmentType = punishmentType;
            this.duration = duration;
            this.reason = reason;
        }

        /**
         * Get the punishment type.
         *
         * @return The punishment type
         */
        public AutomodAPI.AutoPunishmentType getPunishmentType() {
            return punishmentType;
        }

        /**
         * Get the punishment duration in milliseconds.
         *
         * @return Duration (-1 for permanent)
         */
        public long getDuration() {
            return duration;
        }

        /**
         * Get the punishment reason.
         *
         * @return The reason
         */
        public String getReason() {
            return reason;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    /**
     * Event fired when an anticheat alert is processed.
     */
    public static class AnticheatAlertEvent extends AutomodEvent {

        private static final HandlerList handlers = new HandlerList();
        private final String anticheat;
        private final String checkName;
        private final int violations;
        private final double vlLevel;

        public AnticheatAlertEvent(Player player, String ruleId, String ruleName,
                                   String anticheat, String checkName, int violations, double vlLevel) {
            super(player, ruleId, ruleName);
            this.anticheat = anticheat;
            this.checkName = checkName;
            this.violations = violations;
            this.vlLevel = vlLevel;
        }

        /**
         * Get the anticheat name (e.g., "Grim", "Vulcan").
         *
         * @return The anticheat name
         */
        public String getAnticheat() {
            return anticheat;
        }

        /**
         * Get the check name (e.g., "Reach", "Speed").
         *
         * @return The check name
         */
        public String getCheckName() {
            return checkName;
        }

        /**
         * Get the number of violations.
         *
         * @return Violation count
         */
        public int getViolations() {
            return violations;
        }

        /**
         * Get the violation level.
         *
         * @return VL level
         */
        public double getVlLevel() {
            return vlLevel;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    /**
     * Event fired when a nickname is blocked.
     */
    public static class NicknameBlockedEvent extends AutomodEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private final String nickname;
        private boolean cancelled = false;

        public NicknameBlockedEvent(Player player, String ruleId, String ruleName, String nickname) {
            super(player, ruleId, ruleName);
            this.nickname = nickname;
        }

        /**
         * Get the blocked nickname.
         *
         * @return The nickname
         */
        public String getNickname() {
            return nickname;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        @NotNull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }
}
