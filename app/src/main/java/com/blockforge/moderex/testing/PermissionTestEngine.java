package com.blockforge.moderex.testing;

import com.blockforge.moderex.util.PermissionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core permission test engine for ModereX.
 * Runs comprehensive permission checks against MockPermissible objects
 * to verify that all permission gates work correctly.
 */
public class PermissionTestEngine {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ========================================================================
    // Result classes
    // ========================================================================

    public record TestCase(String id, String group, String description,
                           String profileName, String permission, boolean expected) {}

    public record TestResult(TestCase testCase, boolean actual, boolean passed, long durationNs) {}

    public static class TestReport {
        public final List<TestResult> results = new ArrayList<>();
        public int total = 0;
        public int passed = 0;
        public int failed = 0;
        public long totalDurationMs = 0;
        public final Map<String, List<TestResult>> byGroup = new LinkedHashMap<>();
        public final Map<String, List<TestResult>> byProfile = new LinkedHashMap<>();
        public final List<TestResult> failures = new ArrayList<>();

        public void addResult(TestResult result) {
            results.add(result);
            total++;
            if (result.passed()) passed++;
            else {
                failed++;
                failures.add(result);
            }
            byGroup.computeIfAbsent(result.testCase().group(), k -> new ArrayList<>()).add(result);
            byProfile.computeIfAbsent(result.testCase().profileName(), k -> new ArrayList<>()).add(result);
        }
    }

    // ========================================================================
    // Master permission registry
    // ========================================================================

    /** All known base permission strings in ModereX (no wildcards). */
    public static final List<String> MASTER_PERMISSIONS = List.of(
            // Core punishment commands
            "moderex.ban", "moderex.tempban", "moderex.ipban",
            "moderex.mute", "moderex.tempmute", "moderex.ipmute",
            "moderex.warn", "moderex.kick",
            "moderex.unban", "moderex.unmute", "moderex.unwarn",
            "moderex.clearwarnings",

            // Mass punishments
            "moderex.massban", "moderex.massmute", "moderex.masswarn", "moderex.masskick",
            "moderex.massunban", "moderex.massunmute", "moderex.massunwarn",

            // Punishment modifiers
            "moderex.punish", "moderex.punish.delete", "moderex.punish.modify", "moderex.punish.permanent",
            "moderex.flag.silent", "moderex.flag.extrasilent", "moderex.flag.public",
            "moderex.flag.global", "moderex.flag.hidden", "moderex.flag.skip",

            // Info viewing
            "moderex.info.ip", "moderex.info.uuid", "moderex.info.nick",
            "moderex.info.joindate", "moderex.info.time", "moderex.info.namehistory",

            // History viewing
            "moderex.logs", "moderex.logs.bans", "moderex.logs.mutes",
            "moderex.logs.warns", "moderex.logs.kicks", "moderex.logs.pardons",
            "moderex.logs.nick", "moderex.logs.automod",
            "moderex.logs.commands", "moderex.logs.chat", "moderex.logs.sessions",

            // Check/inspect commands
            "moderex.check", "moderex.check.ip",
            "moderex.checkban", "moderex.checkmute", "moderex.checkwarn",
            "moderex.command.viewpunishment", "moderex.command.seen", "moderex.command.seen.ip",

            // Admin config
            "moderex.admin", "moderex.admin.chat", "moderex.admin.kickall",
            "moderex.admin.warnings", "moderex.admin.mutes", "moderex.admin.lockdown",
            "moderex.admin.notifications", "moderex.admin.evidence",
            "moderex.admin.permissions", "moderex.admin.debug",
            "moderex.admin.discord", "moderex.admin.activitylog", "moderex.admin.automod",

            // Automod
            "moderex.automod.view", "moderex.automod.edit",
            "moderex.automod.create", "moderex.automod.delete",

            // Templates
            "moderex.template.create", "moderex.template.edit", "moderex.template.delete",

            // Staff tools
            "moderex.staff", "moderex.staffmode",
            "moderex.command.vanish", "moderex.command.vanish.others",
            "moderex.command.fly", "moderex.command.fly.others",
            "moderex.command.invsee", "moderex.command.echest", "moderex.command.echest.others",
            "moderex.disguise", "moderex.staffchat",
            "moderex.command.broadcast",
            "moderex.staff.inspect", "moderex.staff.inspect.modify",

            // Bypass
            "moderex.bypass.automod", "moderex.bypass.slowmode",
            "moderex.bypass.clearchat", "moderex.bypass.lockdown",
            "moderex.bypass.mute", "moderex.bypass.chatdisable",

            // Alerts/Notifications
            "moderex.alerts.punishments", "moderex.alerts.automod",
            "moderex.alerts.anticheat", "moderex.alerts.staffchat",
            "moderex.alerts.silent", "moderex.alerts.joinleave",
            "moderex.alerts.watchlist", "moderex.alerts.lag",
            "moderex.alerts.nickname", "moderex.alerts.commands",
            "moderex.alerts.ban", "moderex.alerts.kick",
            "moderex.alerts.mute", "moderex.alerts.warn",
            "moderex.alerts.pardon",

            // Watchlist
            "moderex.watchlist.add", "moderex.watchlist.remove",

            // Vanish detailed
            "moderex.vanish", "moderex.vanish.others",
            "moderex.vanish.flight", "moderex.vanish.keepfly",
            "moderex.vanish.spectator", "moderex.vanish.pickup",
            "moderex.vanish.chat", "moderex.vanish.place",
            "moderex.vanish.break", "moderex.vanish.attack",

            // Monitoring
            "moderex.monitoring.view.health", "moderex.monitoring.view.alerts",
            "moderex.monitoring.view.resources", "moderex.monitoring.view.diagnostics",
            "moderex.monitoring.configure.health", "moderex.monitoring.configure.alerts",
            "moderex.monitoring.configure.resources", "moderex.monitoring.configure.diagnostics",

            // Replay
            "moderex.replay.view", "moderex.replay.manage",
            "moderex.replay.record", "moderex.replay.configure",

            // Status
            "moderex.status.view", "moderex.status.restart",
            "moderex.status.reload", "moderex.status.broadcast",

            // Misc
            "moderex.reload", "moderex.log", "moderex.lockdown",
            "moderex.dupeip", "moderex.iphistory", "moderex.ipreport",
            "moderex.geoip", "moderex.namehistory", "moderex.log.automod",
            "moderex.log.nicknames", "moderex.log.teleport",
            "moderex.command.watchlist", "moderex.command.modlog",
            "moderex.staffhistory", "moderex.banlist", "moderex.mutelist", "moderex.warnlist",
            "moderex.warnings", "moderex.rules", "moderex.staffhelp",
            "moderex.broadcast", "moderex.reveal",

            // Protected
            "moderex.webpanel"
    );

    /** Command name → required permission mapping. */
    public static final Map<String, String> COMMAND_PERMISSION_MAP = Map.ofEntries(
            Map.entry("/ban", "moderex.ban"),
            Map.entry("/tempban", "moderex.tempban"),
            Map.entry("/ipban", "moderex.ipban"),
            Map.entry("/mute", "moderex.mute"),
            Map.entry("/tempmute", "moderex.tempmute"),
            Map.entry("/ipmute", "moderex.ipmute"),
            Map.entry("/warn", "moderex.warn"),
            Map.entry("/kick", "moderex.kick"),
            Map.entry("/unban", "moderex.unban"),
            Map.entry("/unmute", "moderex.unmute"),
            Map.entry("/unwarn", "moderex.unwarn"),
            Map.entry("/check", "moderex.check"),
            Map.entry("/history", "moderex.logs"),
            Map.entry("/staffhistory", "moderex.staffhistory"),
            Map.entry("/banlist", "moderex.banlist"),
            Map.entry("/mutelist", "moderex.mutelist"),
            Map.entry("/warnlist", "moderex.warnlist"),
            Map.entry("/seen", "moderex.command.seen"),
            Map.entry("/viewpunishment", "moderex.command.viewpunishment"),
            Map.entry("/vanish", "moderex.command.vanish"),
            Map.entry("/fly", "moderex.command.fly"),
            Map.entry("/invsee", "moderex.command.invsee"),
            Map.entry("/echest", "moderex.command.echest"),
            Map.entry("/staffchat", "moderex.staffchat"),
            Map.entry("/log", "moderex.log"),
            Map.entry("/punish", "moderex.punish"),
            Map.entry("/lockdown", "moderex.lockdown"),
            Map.entry("/dupeip", "moderex.dupeip"),
            Map.entry("/iphistory", "moderex.iphistory"),
            Map.entry("/geoip", "moderex.geoip"),
            Map.entry("/namehistory", "moderex.namehistory"),
            Map.entry("/warnings", "moderex.warnings"),
            Map.entry("/rules", "moderex.rules"),
            Map.entry("/disguise", "moderex.disguise"),
            Map.entry("/modlog", "moderex.command.modlog"),
            Map.entry("/broadcast", "moderex.command.broadcast")
    );

    /** Flag name → required permission. */
    public static final Map<String, String> FLAG_PERMISSION_MAP = Map.of(
            "-s (silent)", "moderex.flag.silent",
            "-es (extra silent)", "moderex.flag.extrasilent",
            "-p (public)", "moderex.flag.public",
            "-g (global)", "moderex.flag.global",
            "-h (hidden)", "moderex.flag.hidden",
            "-skip", "moderex.flag.skip"
    );

    /** GUI item → required permission. */
    public static final Map<String, String> GUI_PERMISSION_MAP = Map.ofEntries(
            Map.entry("Ban Button", "moderex.ban"),
            Map.entry("Mute Button", "moderex.mute"),
            Map.entry("Warn Button", "moderex.warn"),
            Map.entry("Kick Button", "moderex.kick"),
            Map.entry("IP Ban Button", "moderex.ipban"),
            Map.entry("UUID Display", "moderex.info.uuid"),
            Map.entry("IP Display", "moderex.info.ip"),
            Map.entry("Mass Ban", "moderex.massban"),
            Map.entry("Mass Mute", "moderex.massmute"),
            Map.entry("Mass Warn", "moderex.masswarn"),
            Map.entry("Mass Kick", "moderex.masskick"),
            Map.entry("Permanent Ban", "moderex.punish.permanent"),
            Map.entry("Delete Punishment", "moderex.punish.delete"),
            Map.entry("Modify Punishment", "moderex.punish.modify")
    );

    /** Web panel backend data → required permission. */
    public static final Map<String, String> WEB_PANEL_DATA_MAP = Map.ofEntries(
            Map.entry("Player IP Address", "moderex.info.ip"),
            Map.entry("Player UUID", "moderex.info.uuid"),
            Map.entry("Player Nickname", "moderex.info.nick"),
            Map.entry("Ban History", "moderex.logs.bans"),
            Map.entry("Mute History", "moderex.logs.mutes"),
            Map.entry("Warn History", "moderex.logs.warns"),
            Map.entry("Kick History", "moderex.logs.kicks"),
            Map.entry("Pardon History", "moderex.logs.pardons"),
            Map.entry("Command History", "moderex.logs.commands"),
            Map.entry("Chat History", "moderex.logs.chat"),
            Map.entry("Automod Logs", "moderex.logs.automod"),
            Map.entry("Session History", "moderex.logs.sessions"),
            Map.entry("Nickname History", "moderex.logs.nick"),
            Map.entry("Automod Create", "moderex.automod.create"),
            Map.entry("Automod Edit", "moderex.automod.edit"),
            Map.entry("Automod Delete", "moderex.automod.delete"),
            Map.entry("Template Create", "moderex.template.create"),
            Map.entry("Template Edit", "moderex.template.edit"),
            Map.entry("Template Delete", "moderex.template.delete"),
            Map.entry("Web Panel Login", "moderex.webpanel"),
            Map.entry("Admin Permissions", "moderex.admin.permissions")
    );

    /** Web panel config cards → required permission. */
    public static final Map<String, String> WEB_CONFIG_CARD_MAP = Map.ofEntries(
            Map.entry("Chat Management", "moderex.admin.chat"),
            Map.entry("Kick All", "moderex.admin.kickall"),
            Map.entry("Warning Settings", "moderex.admin.warnings"),
            Map.entry("Mute Settings", "moderex.admin.mutes"),
            Map.entry("Lockdown Settings", "moderex.admin.lockdown"),
            Map.entry("Notification Config", "moderex.admin.notifications"),
            Map.entry("Command Blacklist", "moderex.cmdblacklist"),
            Map.entry("Activity Log Config", "moderex.admin.activitylog"),
            Map.entry("Evidence Config", "moderex.admin.evidence"),
            Map.entry("Anticheat Integration", "moderex.anticheat.configure"),
            Map.entry("Discord Integration", "moderex.admin.discord")
    );

    /** Punishment action → required permission for web panel. */
    public static final Map<String, String> WEB_PUNISHMENT_ACTION_MAP = Map.of(
            "Issue Ban", "moderex.ban",
            "Issue Temp Ban", "moderex.tempban",
            "Issue Mute", "moderex.mute",
            "Issue Temp Mute", "moderex.tempmute",
            "Issue Warn", "moderex.warn",
            "Issue Kick", "moderex.kick",
            "Revoke Ban", "moderex.unban",
            "Revoke Mute", "moderex.unmute",
            "Revoke Warn", "moderex.unwarn"
    );

    // ========================================================================
    // Test execution
    // ========================================================================

    /**
     * Run all profile-based tests (Groups A-G).
     */
    public static TestReport runAllProfiles() {
        TestReport report = new TestReport();
        long start = System.currentTimeMillis();

        for (PermissionTestProfiles.TestProfile profile : PermissionTestProfiles.getAllProfiles()) {
            runProfileTests(profile, report);
        }

        report.totalDurationMs = System.currentTimeMillis() - start;
        return report;
    }

    /**
     * Run tests for a single profile.
     */
    public static TestReport runSingleProfile(PermissionTestProfiles.TestProfile profile) {
        TestReport report = new TestReport();
        long start = System.currentTimeMillis();
        runProfileTests(profile, report);
        report.totalDurationMs = System.currentTimeMillis() - start;
        return report;
    }

    /**
     * Run tests for a custom permission set.
     */
    public static TestReport runCustom(Set<String> permissions, boolean isOp) {
        PermissionTestProfiles.TestProfile profile =
                PermissionTestProfiles.custom("CUSTOM", permissions, isOp);
        return runSingleProfile(profile);
    }

    /**
     * Run isolation tests - each permission granted one at a time.
     */
    public static TestReport runIsolation() {
        TestReport report = new TestReport();
        long start = System.currentTimeMillis();
        int testNum = 0;

        for (String permission : MASTER_PERMISSIONS) {
            MockPermissible mock = new MockPermissible("ISOLATE_" + permission, Set.of(permission), false);
            testNum++;

            // Test that this specific permission grants what it should
            long t = System.nanoTime();
            boolean result = PermissionUtil.hasPermission(mock, permission);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "ISO-" + testNum, "Isolation", permission + " grants itself",
                    "ISOLATE(" + permission + ")", permission, true
            );
            report.addResult(new TestResult(tc, result, result == true, dur));

            // Test that granting this permission does NOT grant unrelated permissions
            // Pick a few control permissions that should NOT be granted
            List<String> controls = getControlPermissions(permission);
            for (String control : controls) {
                testNum++;
                t = System.nanoTime();
                boolean controlResult = PermissionUtil.hasPermission(mock, control);
                dur = System.nanoTime() - t;

                tc = new TestCase(
                        "ISO-" + testNum, "Isolation",
                        permission + " should NOT grant " + control,
                        "ISOLATE(" + permission + ")", control, false
                );
                report.addResult(new TestResult(tc, controlResult, controlResult == false, dur));
            }
        }

        report.totalDurationMs = System.currentTimeMillis() - start;
        return report;
    }

    /**
     * Run parity tests - compare Java PermissionUtil vs JavaScript hasPermission() logic.
     */
    public static TestReport runParity() {
        TestReport report = new TestReport();
        long start = System.currentTimeMillis();
        int testNum = 0;

        for (PermissionTestProfiles.TestProfile profile : PermissionTestProfiles.getAllProfiles()) {
            // Skip OP profile for parity (frontend doesn't check OP)
            if (profile.isOp()) continue;

            List<String> permList = new ArrayList<>(profile.permissions());
            MockPermissible mock = profile.createMock();

            for (String perm : MASTER_PERMISSIONS) {
                testNum++;
                long t = System.nanoTime();

                boolean javaResult = PermissionUtil.hasPermission(mock, perm);
                boolean jsResult = simulateFrontendHasPermission(permList, perm);
                long dur = System.nanoTime() - t;

                boolean match = (javaResult == jsResult);
                String desc = profile.name() + ": " + perm +
                        " (Java=" + javaResult + ", JS=" + jsResult + ")";

                TestCase tc = new TestCase(
                        "PAR-" + testNum, "Parity",
                        desc, profile.name(), perm, true // expected: they match
                );
                report.addResult(new TestResult(tc, match, match, dur));
            }
        }

        report.totalDurationMs = System.currentTimeMillis() - start;
        return report;
    }

    /**
     * Run all tests combined: profiles + isolation + parity.
     */
    public static TestReport runAll() {
        TestReport report = new TestReport();
        long start = System.currentTimeMillis();

        // Profiles
        for (PermissionTestProfiles.TestProfile profile : PermissionTestProfiles.getAllProfiles()) {
            runProfileTests(profile, report);
        }

        // Isolation (subset for speed - test each perm grants itself)
        int testNum = report.total;
        for (String permission : MASTER_PERMISSIONS) {
            MockPermissible mock = new MockPermissible("ISOLATE", Set.of(permission), false);
            testNum++;
            long t = System.nanoTime();
            boolean result = PermissionUtil.hasPermission(mock, permission);
            long dur = System.nanoTime() - t;
            TestCase tc = new TestCase("ISO-" + testNum, "Isolation",
                    permission + " grants itself", "ISOLATION", permission, true);
            report.addResult(new TestResult(tc, result, result, dur));
        }

        // Parity
        for (PermissionTestProfiles.TestProfile profile : PermissionTestProfiles.getAllProfiles()) {
            if (profile.isOp()) continue;
            List<String> permList = new ArrayList<>(profile.permissions());
            MockPermissible mock = profile.createMock();
            for (String perm : MASTER_PERMISSIONS) {
                testNum++;
                long t = System.nanoTime();
                boolean javaResult = PermissionUtil.hasPermission(mock, perm);
                boolean jsResult = simulateFrontendHasPermission(permList, perm);
                long dur = System.nanoTime() - t;
                boolean match = (javaResult == jsResult);
                TestCase tc = new TestCase("PAR-" + testNum, "Parity",
                        profile.name() + ": " + perm, profile.name(), perm, true);
                report.addResult(new TestResult(tc, match, match, dur));
            }
        }

        report.totalDurationMs = System.currentTimeMillis() - start;
        return report;
    }

    // ========================================================================
    // Internal test generation
    // ========================================================================

    private static void runProfileTests(PermissionTestProfiles.TestProfile profile, TestReport report) {
        MockPermissible mock = profile.createMock();
        int testNum = report.total;

        // Group A: Core permission logic
        for (String perm : MASTER_PERMISSIONS) {
            testNum++;
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "A-" + testNum, "Core Logic",
                    profile.name() + ": " + perm + " -> " + (expected ? "allowed" : "denied"),
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group B: Command access
        for (Map.Entry<String, String> entry : COMMAND_PERMISSION_MAP.entrySet()) {
            testNum++;
            String cmd = entry.getKey();
            String perm = entry.getValue();
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "B-" + testNum, "Command Access",
                    profile.name() + ": " + cmd + " (" + perm + ")",
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group C: Flag permissions
        for (Map.Entry<String, String> entry : FLAG_PERMISSION_MAP.entrySet()) {
            testNum++;
            String flag = entry.getKey();
            String perm = entry.getValue();
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "C-" + testNum, "Flags",
                    profile.name() + ": " + flag + " (" + perm + ")",
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group D: GUI visibility
        for (Map.Entry<String, String> entry : GUI_PERMISSION_MAP.entrySet()) {
            testNum++;
            String item = entry.getKey();
            String perm = entry.getValue();
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "D-" + testNum, "GUI Items",
                    profile.name() + ": " + item + " -> " + (expected ? "accessible" : "grayed out"),
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group E: Web panel backend filtering
        for (Map.Entry<String, String> entry : WEB_PANEL_DATA_MAP.entrySet()) {
            testNum++;
            String data = entry.getKey();
            String perm = entry.getValue();
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "E-" + testNum, "Web Panel Data",
                    profile.name() + ": " + data + " -> " + (expected ? "included" : "filtered"),
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group E continued: Config cards
        for (Map.Entry<String, String> entry : WEB_CONFIG_CARD_MAP.entrySet()) {
            testNum++;
            String card = entry.getKey();
            String perm = entry.getValue();
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "E-" + testNum, "Web Panel Config",
                    profile.name() + ": " + card + " -> " + (expected ? "unlocked" : "locked"),
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group E continued: Punishment actions
        for (Map.Entry<String, String> entry : WEB_PUNISHMENT_ACTION_MAP.entrySet()) {
            testNum++;
            String action = entry.getKey();
            String perm = entry.getValue();
            boolean expected = computeExpected(profile, perm);
            long t = System.nanoTime();
            boolean actual = PermissionUtil.hasPermission(mock, perm);
            long dur = System.nanoTime() - t;

            TestCase tc = new TestCase(
                    "E-" + testNum, "Web Panel Actions",
                    profile.name() + ": " + action + " -> " + (expected ? "allowed" : "denied"),
                    profile.name(), perm, expected
            );
            report.addResult(new TestResult(tc, actual, actual == expected, dur));
        }

        // Group G: Weight system (only for WEIGHTED profile)
        if (profile.name().equals("WEIGHTED")) {
            runWeightTests(mock, report, testNum);
        }
    }

    private static void runWeightTests(MockPermissible mock, TestReport report, int startNum) {
        int testNum = startNum;

        // Weight 10 vs weight 0 target -> allowed
        MockPermissible target0 = new MockPermissible("Target_W0", Set.of(), false);
        testNum++;
        long t = System.nanoTime();
        boolean result = PermissionUtil.canActOn(mock, target0);
        long dur = System.nanoTime() - t;
        report.addResult(new TestResult(
                new TestCase("G-" + testNum, "Weight System",
                        "Weight 10 vs Weight 0 -> allowed", "WEIGHTED", "weight.10 vs 0", true),
                result, result == true, dur
        ));

        // Weight 10 vs weight 50 target -> denied
        MockPermissible target50 = new MockPermissible("Target_W50", Set.of("moderex.weight.50"), false);
        testNum++;
        t = System.nanoTime();
        result = PermissionUtil.canActOn(mock, target50);
        dur = System.nanoTime() - t;
        report.addResult(new TestResult(
                new TestCase("G-" + testNum, "Weight System",
                        "Weight 10 vs Weight 50 -> denied", "WEIGHTED", "weight.10 vs 50", false),
                result, result == false, dur
        ));

        // Weight 10 vs weight 10 target -> allowed (equal)
        MockPermissible target10 = new MockPermissible("Target_W10", Set.of("moderex.weight.10"), false);
        testNum++;
        t = System.nanoTime();
        result = PermissionUtil.canActOn(mock, target10);
        dur = System.nanoTime() - t;
        report.addResult(new TestResult(
                new TestCase("G-" + testNum, "Weight System",
                        "Weight 10 vs Weight 10 -> allowed (equal)", "WEIGHTED", "weight.10 vs 10", true),
                result, result == true, dur
        ));

        // Weight 10 vs weight 5 target -> allowed
        MockPermissible target5 = new MockPermissible("Target_W5", Set.of("moderex.weight.5"), false);
        testNum++;
        t = System.nanoTime();
        result = PermissionUtil.canActOn(mock, target5);
        dur = System.nanoTime() - t;
        report.addResult(new TestResult(
                new TestCase("G-" + testNum, "Weight System",
                        "Weight 10 vs Weight 5 -> allowed", "WEIGHTED", "weight.10 vs 5", true),
                result, result == true, dur
        ));
    }

    // ========================================================================
    // Expected result computation
    // ========================================================================

    /**
     * Compute the expected result for a profile checking a specific permission.
     * This replicates the PermissionUtil logic to know what the correct answer should be.
     */
    static boolean computeExpected(PermissionTestProfiles.TestProfile profile, String permission) {
        // Protected permissions: OP does NOT bypass
        if (PermissionUtil.isProtectedPermission(permission)) {
            return hasPermissionInSet(profile.permissions(), permission);
        }

        // OP bypass for non-protected
        if (profile.isOp()) {
            return true;
        }

        // Check direct
        if (profile.permissions().contains(permission)) {
            return true;
        }

        // Check wildcards
        return hasWildcardInSet(profile.permissions(), permission);
    }

    /** Check if a permission set contains the permission via wildcard resolution. */
    private static boolean hasPermissionInSet(Set<String> permissions, String permission) {
        if (permissions.contains(permission)) return true;
        return hasWildcardInSet(permissions, permission);
    }

    /** Check wildcard grants in a permission set. */
    private static boolean hasWildcardInSet(Set<String> permissions, String permission) {
        if (!permission.startsWith("moderex.")) return false;

        // Check moderex.* (grants all except protected)
        if (permissions.contains("moderex.*")) {
            return !PermissionUtil.isProtectedPermission(permission);
        }

        // Build wildcard paths
        String[] parts = permission.split("\\.");
        StringBuilder wildcardPath = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) wildcardPath.append(".");
            wildcardPath.append(parts[i]);
            if (permissions.contains(wildcardPath + ".*")) {
                return true;
            }
        }

        return false;
    }

    // ========================================================================
    // Frontend parity simulation
    // ========================================================================

    /**
     * Replicates the JavaScript hasPermission() function from app.js exactly.
     * This is used to detect parity issues between frontend and backend.
     *
     * JS logic (app.js:35-51):
     * 1. Direct match
     * 2. Wildcard check: parts[0..i].join('.') + '.*' for i from len-1 down to 1
     * 3. Parent check: if parts.length > 2, check parts[0..len-1].join('.')
     */
    public static boolean simulateFrontendHasPermission(List<String> permissions, String perm) {
        // Direct match
        if (permissions.contains(perm)) return true;

        String[] parts = perm.split("\\.");

        // Wildcard check (same as Java PermissionUtil)
        for (int i = parts.length - 1; i >= 1; i--) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < i; j++) {
                if (j > 0) sb.append(".");
                sb.append(parts[j]);
            }
            sb.append(".*");
            if (permissions.contains(sb.toString())) return true;
        }

        // Parent permission check (JS only - NOT in Java PermissionUtil)
        // e.g., "moderex.admin" matches "moderex.admin.automod"
        if (parts.length > 2) {
            StringBuilder parent = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) parent.append(".");
                parent.append(parts[i]);
            }
            if (permissions.contains(parent.toString())) return true;
        }

        return false;
    }

    // ========================================================================
    // Utility helpers
    // ========================================================================

    /** Get control permissions that should NOT be granted by a specific permission. */
    private static List<String> getControlPermissions(String permission) {
        List<String> controls = new ArrayList<>();
        // Pick 3 permissions from different categories that should NOT be granted
        String[] candidates = {
                "moderex.ban", "moderex.info.ip", "moderex.logs.bans",
                "moderex.admin", "moderex.webpanel", "moderex.automod.edit",
                "moderex.template.create", "moderex.bypass.mute", "moderex.staffchat"
        };
        for (String c : candidates) {
            if (!c.equals(permission) && !isParentOf(permission, c)) {
                controls.add(c);
                if (controls.size() >= 3) break;
            }
        }
        return controls;
    }

    /** Check if parent is a wildcard parent of child (e.g., moderex.info.* is parent of moderex.info.ip). */
    private static boolean isParentOf(String parent, String child) {
        if (parent.endsWith(".*")) {
            String prefix = parent.substring(0, parent.length() - 2);
            return child.startsWith(prefix + ".");
        }
        return false;
    }

    // ========================================================================
    // Output formatting
    // ========================================================================

    /**
     * Convert report to JSON for file export or web panel.
     */
    public static JsonObject toJson(TestReport report) {
        JsonObject json = new JsonObject();
        json.addProperty("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        json.addProperty("total", report.total);
        json.addProperty("passed", report.passed);
        json.addProperty("failed", report.failed);
        json.addProperty("durationMs", report.totalDurationMs);

        // Groups
        JsonObject groups = new JsonObject();
        for (Map.Entry<String, List<TestResult>> entry : report.byGroup.entrySet()) {
            JsonObject group = new JsonObject();
            List<TestResult> results = entry.getValue();
            group.addProperty("total", results.size());
            group.addProperty("passed", (int) results.stream().filter(TestResult::passed).count());
            group.addProperty("failed", (int) results.stream().filter(r -> !r.passed()).count());

            JsonArray tests = new JsonArray();
            for (TestResult r : results) {
                JsonObject t = new JsonObject();
                t.addProperty("id", r.testCase().id());
                t.addProperty("description", r.testCase().description());
                t.addProperty("profile", r.testCase().profileName());
                t.addProperty("permission", r.testCase().permission());
                t.addProperty("expected", r.testCase().expected());
                t.addProperty("actual", r.actual());
                t.addProperty("passed", r.passed());
                tests.add(t);
            }
            group.add("tests", tests);
            groups.add(entry.getKey(), group);
        }
        json.add("groups", groups);

        // Failures summary
        JsonArray failures = new JsonArray();
        for (TestResult r : report.failures) {
            JsonObject f = new JsonObject();
            f.addProperty("id", r.testCase().id());
            f.addProperty("group", r.testCase().group());
            f.addProperty("profile", r.testCase().profileName());
            f.addProperty("permission", r.testCase().permission());
            f.addProperty("expected", r.testCase().expected());
            f.addProperty("actual", r.actual());
            f.addProperty("description", r.testCase().description());
            failures.add(f);
        }
        json.add("failures", failures);

        return json;
    }

    /**
     * Save report to a JSON file.
     */
    public static File saveReport(TestReport report, File directory) throws IOException {
        if (!directory.exists()) directory.mkdirs();

        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
                .replace(":", "-").replace("T", "_").substring(0, 19);
        File file = new File(directory, "permtest-" + timestamp + ".json");

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(toJson(report), writer);
        }

        return file;
    }

    /**
     * Generate chat-formatted lines for a report.
     * Returns a list of MiniMessage-formatted strings.
     */
    public static List<String> toChatLines(TestReport report, boolean showPassing, int maxLines) {
        List<String> lines = new ArrayList<>();

        // Header
        lines.add("");
        lines.add("<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        lines.add("<white>  <bold>Permission Test Report</bold>");
        lines.add("<gray>  " + report.total + " tests | " + report.totalDurationMs + "ms");
        lines.add("");

        // Summary per group
        for (Map.Entry<String, List<TestResult>> entry : report.byGroup.entrySet()) {
            String group = entry.getKey();
            List<TestResult> results = entry.getValue();
            long groupPassed = results.stream().filter(TestResult::passed).count();
            long groupFailed = results.size() - groupPassed;

            String statusColor = groupFailed == 0 ? "<green>" : "<red>";
            lines.add(statusColor + "<bold>" + group + "</bold> <gray>[" + groupPassed + "/" + results.size() + " passed]");

            // Show failures
            for (TestResult r : results) {
                if (!r.passed()) {
                    lines.add("  <red>\u2717 " + r.testCase().description());
                    lines.add("    <dark_gray>Expected: " + r.testCase().expected() + " | Actual: " + r.actual());
                } else if (showPassing && lines.size() < maxLines) {
                    lines.add("  <green>\u2713 <gray>" + r.testCase().description());
                }
            }
        }

        // Footer
        lines.add("");
        String summaryColor = report.failed == 0 ? "<green>" : "<red>";
        lines.add(summaryColor + "<bold>Total: " + report.total + " | Passed: " + report.passed +
                " | Failed: " + report.failed + "</bold>");
        lines.add("<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        lines.add("");

        return lines;
    }

    /**
     * Generate a compact permission matrix (profile x permission category).
     */
    public static List<String> toMatrixLines(TestReport report) {
        List<String> lines = new ArrayList<>();
        lines.add("");
        lines.add("<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        lines.add("<white>  <bold>Permission Matrix</bold>");
        lines.add("");

        // Group results by profile, then show pass/fail per group
        for (Map.Entry<String, List<TestResult>> profileEntry : report.byProfile.entrySet()) {
            String profileName = profileEntry.getKey();
            List<TestResult> results = profileEntry.getValue();

            // Count by group
            Map<String, int[]> groupCounts = new LinkedHashMap<>();
            for (TestResult r : results) {
                int[] counts = groupCounts.computeIfAbsent(r.testCase().group(), k -> new int[2]);
                if (r.passed()) counts[0]++;
                else counts[1]++;
            }

            StringBuilder line = new StringBuilder();
            line.append("<white><bold>").append(String.format("%-20s", profileName)).append("</bold> ");
            for (Map.Entry<String, int[]> gc : groupCounts.entrySet()) {
                int passed = gc.getValue()[0];
                int failed = gc.getValue()[1];
                if (failed == 0) {
                    line.append("<green>\u2713");
                } else {
                    line.append("<red>").append(failed).append("\u2717");
                }
                line.append(" ");
            }
            lines.add(line.toString());
        }

        lines.add("");
        lines.add("<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        return lines;
    }
}
