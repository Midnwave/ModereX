package com.blockforge.moderex.commands.moderation.base;

import java.util.Arrays;
import java.util.List;

/**
 * Helper class providing common tab completion lists for moderation commands.
 */
public final class TabCompletionHelper {

    private TabCompletionHelper() {
    }

    /**
     * All available flags for punishment commands
     */
    public static final List<String> ALL_FLAGS = Arrays.asList(
            "-d", "-g", "-I", "-m", "-N", "-p", "-s", "-S",
            "--delete", "--modify", "--sender=", "--sender-uuid=",
            "--server-origin=", "--confirm", "--hide", "--skip", "--no-queue", "--"
    );

    /**
     * Common duration suggestions
     */
    public static final List<String> COMMON_DURATIONS = Arrays.asList(
            "10m", "30m", "1h", "6h", "12h", "1d", "3d", "7d", "14d", "30d", "1y", "permanent"
    );

    /**
     * Short duration suggestions (for temporary bans/mutes)
     */
    public static final List<String> SHORT_DURATIONS = Arrays.asList(
            "1h", "6h", "12h", "1d", "3d", "7d", "14d", "30d", "1y"
    );

    /**
     * Punishment type filters for history commands
     */
    public static final List<String> PUNISHMENT_TYPES = Arrays.asList(
            "all", "bans", "mutes", "warnings", "kicks"
    );

    /**
     * Page number suggestions for list commands
     */
    public static final List<String> PAGE_NUMBERS = Arrays.asList(
            "1", "2", "3", "4", "5"
    );

    /**
     * Server scope options
     */
    public static final List<String> SERVER_SCOPES = Arrays.asList(
            "server:local", "server:global"
    );

    /**
     * Allow command actions
     */
    public static final List<String> ALLOW_ACTIONS = Arrays.asList(
            "add", "check", "remove"
    );
}
