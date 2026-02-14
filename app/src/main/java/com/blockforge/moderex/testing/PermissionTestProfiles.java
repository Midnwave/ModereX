package com.blockforge.moderex.testing;

import java.util.*;

/**
 * Pre-built permission test profiles for the ModereX permission testing system.
 * Each profile represents a realistic permission configuration to test against.
 */
public class PermissionTestProfiles {

    /**
     * A named test profile with a set of permissions and OP status.
     */
    public record TestProfile(String name, String description, Set<String> permissions, boolean isOp) {
        public MockPermissible createMock() {
            return new MockPermissible(name, permissions, isOp);
        }
    }

    /** All built-in profiles */
    private static final List<TestProfile> ALL_PROFILES = new ArrayList<>();

    static {
        ALL_PROFILES.add(none());
        ALL_PROFILES.add(op());
        ALL_PROFILES.add(helper());
        ALL_PROFILES.add(moderator());
        ALL_PROFILES.add(admin());
        ALL_PROFILES.add(owner());
        ALL_PROFILES.add(tempbanOnly());
        ALL_PROFILES.add(historyPartial());
        ALL_PROFILES.add(flagTester());
        ALL_PROFILES.add(webViewer());
        ALL_PROFILES.add(categoryWildcard());
        ALL_PROFILES.add(weighted());
    }

    public static List<TestProfile> getAllProfiles() {
        return Collections.unmodifiableList(ALL_PROFILES);
    }

    public static TestProfile getByName(String name) {
        for (TestProfile p : ALL_PROFILES) {
            if (p.name().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    // ========================================================================
    // Profile Definitions
    // ========================================================================

    /** No permissions at all, not OP. Everything should be denied. */
    public static TestProfile none() {
        return new TestProfile("NONE", "No permissions - baseline denial test",
                Set.of(), false);
    }

    /** OP player with no explicit permissions. Should bypass all except moderex.webpanel. */
    public static TestProfile op() {
        return new TestProfile("OP", "Operator - bypasses all except protected perms",
                Set.of(), true);
    }

    /** Low-level staff with basic viewing permissions. */
    public static TestProfile helper() {
        return new TestProfile("HELPER", "Low-level staff with basic view access",
                Set.of(
                        "moderex.webpanel",
                        "moderex.staff",
                        "moderex.check",
                        "moderex.logs",
                        "moderex.logs.bans",
                        "moderex.logs.mutes",
                        "moderex.logs.warns",
                        "moderex.logs.kicks",
                        "moderex.info.uuid",
                        "moderex.info.nick",
                        "moderex.warnings",
                        "moderex.alerts.punishments",
                        "moderex.command.seen"
                ), false);
    }

    /** Mid-level staff with punishment capabilities. Inherits helper perms + more. */
    public static TestProfile moderator() {
        return new TestProfile("MODERATOR", "Mid-level staff with punishment access",
                Set.of(
                        // Helper permissions
                        "moderex.webpanel",
                        "moderex.staff",
                        "moderex.check",
                        "moderex.logs",
                        "moderex.warnings",
                        "moderex.command.seen",
                        // Info wildcards
                        "moderex.info.*",
                        // History wildcard
                        "moderex.logs.*",
                        // Punishment commands
                        "moderex.ban",
                        "moderex.tempban",
                        "moderex.mute",
                        "moderex.tempmute",
                        "moderex.warn",
                        "moderex.kick",
                        "moderex.unban",
                        "moderex.unmute",
                        "moderex.unwarn",
                        // Staff tools
                        "moderex.staffchat",
                        "moderex.command.vanish",
                        "moderex.log",
                        "moderex.punish",
                        "moderex.command.viewpunishment",
                        // Alerts
                        "moderex.alerts.punishments",
                        "moderex.alerts.automod",
                        "moderex.alerts.joinleave",
                        "moderex.alerts.watchlist",
                        // Watchlist
                        "moderex.watchlist.add",
                        "moderex.watchlist.remove",
                        // Flags
                        "moderex.flag.silent"
                ), false);
    }

    /** Full admin with all admin features. */
    public static TestProfile admin() {
        return new TestProfile("ADMIN", "Full admin with all management features",
                Set.of(
                        // Moderator core
                        "moderex.webpanel",
                        "moderex.staff",
                        "moderex.admin",
                        "moderex.check",
                        "moderex.logs",
                        "moderex.warnings",
                        "moderex.command.seen",
                        "moderex.info.*",
                        "moderex.logs.*",
                        "moderex.ban",
                        "moderex.tempban",
                        "moderex.ipban",
                        "moderex.mute",
                        "moderex.tempmute",
                        "moderex.ipmute",
                        "moderex.warn",
                        "moderex.kick",
                        "moderex.unban",
                        "moderex.unmute",
                        "moderex.unwarn",
                        "moderex.staffchat",
                        "moderex.command.vanish",
                        "moderex.log",
                        "moderex.punish",
                        "moderex.command.viewpunishment",
                        // Admin wildcards
                        "moderex.admin.*",
                        "moderex.automod.*",
                        "moderex.template.*",
                        "moderex.alerts.*",
                        // Mass
                        "moderex.massban",
                        "moderex.massmute",
                        "moderex.masswarn",
                        "moderex.masskick",
                        // Punishment modifiers
                        "moderex.punish.delete",
                        "moderex.punish.modify",
                        "moderex.punish.permanent",
                        // Flags
                        "moderex.flag.*",
                        // Commands
                        "moderex.cmdblacklist",
                        "moderex.cmdunblacklist",
                        // Watchlist
                        "moderex.watchlist.add",
                        "moderex.watchlist.remove",
                        // Staff tools
                        "moderex.command.fly",
                        "moderex.command.invsee",
                        "moderex.command.echest",
                        "moderex.disguise",
                        "moderex.staffmode",
                        "moderex.lockdown",
                        // Monitoring
                        "moderex.monitoring.*",
                        // Replay
                        "moderex.replay.*",
                        // Status
                        "moderex.status.*"
                ), false);
    }

    /** Owner with moderex.* wildcard (NOT OP). Tests wildcard grants all except protected. */
    public static TestProfile owner() {
        return new TestProfile("OWNER", "Wildcard moderex.* - grants all except protected",
                Set.of("moderex.*"), false);
    }

    /** Edge case: can tempban but not permanent ban. */
    public static TestProfile tempbanOnly() {
        return new TestProfile("TEMPBAN_ONLY", "Edge case - tempban without permanent ban",
                Set.of("moderex.tempban"), false);
    }

    /** Edge case: can see bans and mutes but not warns or kicks in history. */
    public static TestProfile historyPartial() {
        return new TestProfile("HISTORY_PARTIAL", "Edge case - partial history access",
                Set.of(
                        "moderex.logs",
                        "moderex.logs.bans",
                        "moderex.logs.mutes"
                ), false);
    }

    /** Edge case: has some flags but not all. */
    public static TestProfile flagTester() {
        return new TestProfile("FLAG_TESTER", "Edge case - some punishment flags only",
                Set.of(
                        "moderex.ban",
                        "moderex.flag.silent",
                        "moderex.flag.public"
                ), false);
    }

    /** Minimal web panel access - can log in and see limited data. */
    public static TestProfile webViewer() {
        return new TestProfile("WEB_VIEWER", "Minimal web panel with limited data access",
                Set.of(
                        "moderex.webpanel",
                        "moderex.staff",
                        "moderex.logs.bans",
                        "moderex.info.uuid"
                ), false);
    }

    /** Tests category wildcard resolution. moderex.logs.* should grant all history sub-perms. */
    public static TestProfile categoryWildcard() {
        return new TestProfile("CATEGORY_WILDCARD", "Tests category wildcard resolution",
                Set.of("moderex.logs.*"), false);
    }

    /** Tests weight system - has weight.10 which limits who they can punish. */
    public static TestProfile weighted() {
        return new TestProfile("WEIGHTED", "Weight system test - weight.10 staff member",
                Set.of(
                        "moderex.ban",
                        "moderex.mute",
                        "moderex.kick",
                        "moderex.warn",
                        "moderex.weight.10"
                ), false);
    }

    // ========================================================================
    // Custom profile builder
    // ========================================================================

    /** Create a custom profile from a set of permissions. */
    public static TestProfile custom(String name, Set<String> permissions, boolean isOp) {
        return new TestProfile(name, "Custom profile", new HashSet<>(permissions), isOp);
    }

    /** Create a profile from a real player's permissions. */
    public static TestProfile fromPermissions(String playerName, Set<String> permissions, boolean isOp) {
        return new TestProfile(playerName, "Live permissions for " + playerName,
                new HashSet<>(permissions), isOp);
    }
}
