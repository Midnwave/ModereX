package com.blockforge.moderex.commands.moderation.base;

import com.blockforge.moderex.util.FlagParser;
import com.blockforge.moderex.util.TargetResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Holds the parsed context for a punishment command.
 * This includes the target, duration, reason, flags, and executor information.
 */
public class PunishmentContext {

    private final CommandSender sender;
    private final TargetResolver target;
    private final FlagParser flags;
    private final Long duration;  // null means no duration specified
    private final String reason;
    private final UUID executorUuid;
    private final String executorName;
    private final String customExecutorName;
    private final UUID customExecutorUuid;

    private PunishmentContext(Builder builder) {
        this.sender = builder.sender;
        this.target = builder.target;
        this.flags = builder.flags;
        this.duration = builder.duration;
        this.reason = builder.reason;
        this.executorUuid = builder.executorUuid;
        this.executorName = builder.executorName;
        this.customExecutorName = builder.customExecutorName;
        this.customExecutorUuid = builder.customExecutorUuid;
    }

    public CommandSender getSender() {
        return sender;
    }

    public TargetResolver getTarget() {
        return target;
    }

    public FlagParser getFlags() {
        return flags;
    }

    public Long getDuration() {
        return duration;
    }

    public String getReason() {
        return reason;
    }

    public UUID getExecutorUuid() {
        return customExecutorUuid != null ? customExecutorUuid : executorUuid;
    }

    public String getExecutorName() {
        return customExecutorName != null ? customExecutorName : executorName;
    }

    public boolean hasCustomExecutor() {
        return customExecutorName != null || customExecutorUuid != null;
    }

    public boolean isIpBased() {
        return flags.isIpBased();
    }

    public boolean isGlobal() {
        return flags.isGlobal();
    }

    public boolean isSilent() {
        return flags.isSilent();
    }

    public boolean isExtraSilent() {
        return flags.isExtraSilent();
    }

    public boolean isHidden() {
        return flags.isHidden();
    }

    public boolean isPublic() {
        return flags.isPublic();
    }

    public boolean isModify() {
        return flags.isModify();
    }

    public boolean isDelete() {
        return flags.isDelete();
    }

    public boolean isSkip() {
        return flags.isSkip();
    }

    public boolean isNoOverride() {
        return flags.isNoOverride();
    }

    public boolean isNoQueue() {
        return flags.isNoQueue();
    }

    public boolean isConfirm() {
        return flags.isConfirm();
    }

    public int getSilenceLevel() {
        return flags.getSilenceLevel();
    }

    public String getServerOrigin() {
        String origin = flags.getServerOrigin();
        if (origin != null) {
            return origin;
        }
        return flags.isGlobal() ? "global" : "local";
    }

    public static Builder builder(CommandSender sender) {
        return new Builder(sender);
    }

    public static class Builder {
        private final CommandSender sender;
        private TargetResolver target;
        private FlagParser flags;
        private Long duration;
        private String reason = "No reason specified";
        private UUID executorUuid;
        private String executorName;
        private String customExecutorName;
        private UUID customExecutorUuid;

        private Builder(CommandSender sender) {
            this.sender = sender;
            this.executorName = sender.getName();
            if (sender instanceof Player player) {
                this.executorUuid = player.getUniqueId();
            }
        }

        public Builder target(TargetResolver target) {
            this.target = target;
            return this;
        }

        public Builder target(String targetString) {
            this.target = new TargetResolver(targetString);
            return this;
        }

        public Builder flags(FlagParser flags) {
            this.flags = flags;

            // Parse custom executor if provided
            if (flags.getSender() != null) {
                this.customExecutorName = flags.getSender();
            }
            if (flags.getSenderUuid() != null) {
                try {
                    this.customExecutorUuid = UUID.fromString(flags.getSenderUuid());
                } catch (IllegalArgumentException ignored) {
                    // Invalid UUID, ignore
                }
            }

            return this;
        }

        public Builder duration(Long duration) {
            this.duration = duration;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason != null && !reason.isEmpty() ? reason : "No reason specified";
            return this;
        }

        public Builder executorUuid(UUID executorUuid) {
            this.executorUuid = executorUuid;
            return this;
        }

        public Builder executorName(String executorName) {
            this.executorName = executorName;
            return this;
        }

        public PunishmentContext build() {
            if (target == null) {
                throw new IllegalStateException("Target must be set");
            }
            if (flags == null) {
                throw new IllegalStateException("Flags must be set");
            }
            return new PunishmentContext(this);
        }
    }
}
