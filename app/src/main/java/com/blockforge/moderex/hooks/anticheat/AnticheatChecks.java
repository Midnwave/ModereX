package com.blockforge.moderex.hooks.anticheat;

import java.util.*;

/**
 * Registry of all known anticheat check types.
 * This allows ModereX to display and configure checks even before they're detected.
 */
public class AnticheatChecks {

    // Check categories
    public enum Category {
        COMBAT("Combat", "Combat-related checks (KillAura, Reach, etc.)"),
        MOVEMENT("Movement", "Movement-related checks (Fly, Speed, etc.)"),
        PLAYER("Player", "Player action checks (BadPackets, Inventory, etc.)"),
        WORLD("World", "World interaction checks (Scaffold, FastBreak, etc.)"),
        MISC("Misc", "Miscellaneous checks");

        private final String displayName;
        private final String description;

        Category(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }

    // Check definition
    public static class CheckInfo {
        private final String name;
        private final String displayName;
        private final Category category;
        private final String description;
        private final String[] aliases;

        public CheckInfo(String name, String displayName, Category category, String description, String... aliases) {
            this.name = name.toLowerCase();
            this.displayName = displayName;
            this.category = category;
            this.description = description;
            this.aliases = aliases;
        }

        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public Category getCategory() { return category; }
        public String getDescription() { return description; }
        public String[] getAliases() { return aliases; }

        public boolean matches(String checkName) {
            String lower = checkName.toLowerCase();
            if (name.equals(lower) || lower.startsWith(name)) return true;
            for (String alias : aliases) {
                if (lower.equals(alias.toLowerCase()) || lower.startsWith(alias.toLowerCase())) return true;
            }
            return false;
        }
    }

    // ========== GRIM CHECKS ==========
    private static final List<CheckInfo> GRIM_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("reach", "Reach", Category.COMBAT, "Detects extended attack reach", "combat.reach"),
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Detects automated combat", "aura", "aimbot"),
        new CheckInfo("autoclicker", "AutoClicker", Category.COMBAT, "Detects automated clicking"),
        new CheckInfo("aim", "Aim Assist", Category.COMBAT, "Detects aim assistance"),
        new CheckInfo("hitbox", "Hitbox", Category.COMBAT, "Detects hitbox expansion"),

        // Movement
        new CheckInfo("simulation", "Simulation", Category.MOVEMENT, "Movement prediction check"),
        new CheckInfo("timer", "Timer", Category.MOVEMENT, "Detects game timer manipulation", "timerbalance"),
        new CheckInfo("ground", "Ground Spoof", Category.MOVEMENT, "Detects ground spoofing", "groundspoof"),
        new CheckInfo("prediction", "Prediction", Category.MOVEMENT, "Predictive movement check"),
        new CheckInfo("vehicle", "Vehicle", Category.MOVEMENT, "Vehicle movement checks", "entitycontrol"),
        new CheckInfo("phase", "Phase", Category.MOVEMENT, "Detects phasing through blocks"),
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "Detects fall damage bypass"),
        new CheckInfo("fly", "Fly", Category.MOVEMENT, "Detects flight hacks", "flight"),
        new CheckInfo("speed", "Speed", Category.MOVEMENT, "Detects speed hacks"),
        new CheckInfo("step", "Step", Category.MOVEMENT, "Detects step hacks"),
        new CheckInfo("noslow", "NoSlow", Category.MOVEMENT, "Detects slowdown bypass"),
        new CheckInfo("strafe", "Strafe", Category.MOVEMENT, "Detects strafe hacks"),
        new CheckInfo("antikb", "AntiKnockback", Category.MOVEMENT, "Detects knockback bypass", "velocity", "knockback"),
        new CheckInfo("elytra", "Elytra", Category.MOVEMENT, "Detects elytra exploits"),
        new CheckInfo("jesus", "Jesus/WaterWalk", Category.MOVEMENT, "Detects water walking", "waterwalk", "liquidwalk"),

        // Player
        new CheckInfo("badpackets", "Bad Packets", Category.PLAYER, "Detects invalid packets", "packet"),
        new CheckInfo("inventory", "Inventory", Category.PLAYER, "Inventory interaction checks"),
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Detects scaffold/bridging", "tower"),
        new CheckInfo("crash", "Crash", Category.PLAYER, "Detects crash attempts"),
        new CheckInfo("exploit", "Exploit", Category.PLAYER, "General exploit detection"),
        new CheckInfo("post", "Post", Category.PLAYER, "Post packet timing checks"),
        new CheckInfo("baritone", "Baritone", Category.PLAYER, "Detects Baritone bot usage")
    );

    // ========== VULCAN CHECKS ==========
    private static final List<CheckInfo> VULCAN_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("aim", "Aim", Category.COMBAT, "Aim analysis checks (A-T variants)"),
        new CheckInfo("autoblock", "AutoBlock", Category.COMBAT, "Detects auto blocking (A-C)"),
        new CheckInfo("autoclicker", "AutoClicker", Category.COMBAT, "Detects auto clicking (A-T)"),
        new CheckInfo("criticals", "Criticals", Category.COMBAT, "Detects critical hit exploits (A-B)"),
        new CheckInfo("hitbox", "Hitbox", Category.COMBAT, "Detects hitbox expansion (A-B)"),
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Detects combat hacks (A-I)", "aura"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Detects reach hacks ~3.15 (A-B)"),
        new CheckInfo("velocity", "Velocity", Category.COMBAT, "Detects knockback bypass (A-C)", "antikb"),

        // Movement
        new CheckInfo("boatfly", "BoatFly", Category.MOVEMENT, "Detects boat flying (A-B)"),
        new CheckInfo("entityspeed", "EntitySpeed", Category.MOVEMENT, "Detects entity speed hacks"),
        new CheckInfo("elytra", "Elytra", Category.MOVEMENT, "Detects elytra exploits"),
        new CheckInfo("fastclimb", "FastClimb", Category.MOVEMENT, "Detects fast climbing"),
        new CheckInfo("flight", "Flight", Category.MOVEMENT, "Detects flight hacks (A-E)", "fly"),
        new CheckInfo("jesus", "Jesus", Category.MOVEMENT, "Detects water walking (A-E)", "waterwalk"),
        new CheckInfo("jump", "Jump", Category.MOVEMENT, "Detects jump hacks (A-B)"),
        new CheckInfo("motion", "Motion", Category.MOVEMENT, "Motion analysis (A-G)"),
        new CheckInfo("noslow", "NoSlow", Category.MOVEMENT, "Detects slowdown bypass (A-C)"),
        new CheckInfo("speed", "Speed", Category.MOVEMENT, "Detects speed hacks (A-D)"),
        new CheckInfo("sprint", "Sprint", Category.MOVEMENT, "Detects sprint exploits (A-D)"),
        new CheckInfo("step", "Step", Category.MOVEMENT, "Detects step hacks (A-C)"),
        new CheckInfo("strafe", "Strafe", Category.MOVEMENT, "Detects strafe hacks (A-B)"),
        new CheckInfo("wallclimb", "WallClimb", Category.MOVEMENT, "Detects wall climbing"),

        // Player
        new CheckInfo("badpackets", "BadPackets", Category.PLAYER, "Bad packet checks (A-Y)", "packet"),
        new CheckInfo("baritone", "Baritone", Category.PLAYER, "Detects Baritone usage (A-B)"),
        new CheckInfo("crash", "Crash", Category.PLAYER, "Detects crash attempts (A-C)"),
        new CheckInfo("fastbreak", "FastBreak", Category.PLAYER, "Detects fast block breaking"),
        new CheckInfo("fastplace", "FastPlace", Category.PLAYER, "Detects fast block placing"),
        new CheckInfo("fastuse", "FastUse", Category.PLAYER, "Detects fast item usage"),
        new CheckInfo("groundspoof", "GroundSpoof", Category.PLAYER, "Detects ground spoofing (A-C)"),
        new CheckInfo("hackedclient", "HackedClient", Category.PLAYER, "Detects hacked clients"),
        new CheckInfo("improbable", "Improbable", Category.PLAYER, "Improbable action detection (A-D)"),
        new CheckInfo("invalid", "Invalid", Category.PLAYER, "Invalid action detection (A-F)"),
        new CheckInfo("inventory", "Inventory", Category.PLAYER, "Inventory checks (A-B)"),
        new CheckInfo("pingspoof", "PingSpoof", Category.PLAYER, "Detects ping spoofing (A-C)"),
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Scaffold detection (A-K)", "tower"),
        new CheckInfo("timer", "Timer", Category.PLAYER, "Timer manipulation detection")
    );

    // ========== MATRIX CHECKS ==========
    private static final List<CheckInfo> MATRIX_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Combat hack detection", "aura", "aimbot"),
        new CheckInfo("hitbox", "HitBox", Category.COMBAT, "Hitbox/reach detection"),
        new CheckInfo("click", "Click", Category.COMBAT, "Click pattern analysis"),
        new CheckInfo("velocity", "Velocity", Category.COMBAT, "Knockback bypass detection", "antikb"),

        // Movement
        new CheckInfo("move", "Move", Category.MOVEMENT, "Movement hack detection", "fly", "speed"),
        new CheckInfo("jesus", "Jesus", Category.MOVEMENT, "Water walking detection", "waterwalk"),
        new CheckInfo("elytra", "Elytra", Category.MOVEMENT, "Elytra exploit detection"),
        new CheckInfo("vehicle", "Vehicle", Category.MOVEMENT, "Vehicle exploit detection"),
        new CheckInfo("phase", "Phase", Category.MOVEMENT, "Block phasing detection"),

        // Player
        new CheckInfo("badpackets", "BadPackets", Category.PLAYER, "Bad packet detection", "packet"),
        new CheckInfo("delay", "Delay", Category.PLAYER, "Action speed detection"),
        new CheckInfo("block", "Block", Category.WORLD, "Block break/place detection"),
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Scaffold/tower detection", "tower"),
        new CheckInfo("interact", "Interact", Category.PLAYER, "Illegal interaction detection"),
        new CheckInfo("chat", "Chat", Category.PLAYER, "Chat spam detection"),
        new CheckInfo("autobot", "AutoBot", Category.PLAYER, "Bot usage detection")
    );

    // ========== SPARTAN CHECKS ==========
    private static final List<CheckInfo> SPARTAN_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Combat automation detection", "aura", "aimbot", "fightbot"),
        new CheckInfo("fastbow", "FastBow", Category.COMBAT, "Fast bow detection"),
        new CheckInfo("velocity", "Velocity", Category.COMBAT, "Knockback bypass detection", "antikb", "antiknockback"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Extended reach detection", "tpaura"),
        new CheckInfo("criticals", "Criticals", Category.COMBAT, "Critical hit exploit detection"),
        new CheckInfo("autoclicker", "AutoClicker", Category.COMBAT, "Auto click detection"),

        // Movement
        new CheckInfo("flight", "Flight", Category.MOVEMENT, "Flight hack detection", "fly"),
        new CheckInfo("glide", "Glide", Category.MOVEMENT, "Glide hack detection"),
        new CheckInfo("speed", "Speed", Category.MOVEMENT, "Speed hack detection"),
        new CheckInfo("highjump", "HighJump", Category.MOVEMENT, "High jump detection", "longjump"),
        new CheckInfo("clip", "Clip", Category.MOVEMENT, "Clip through blocks detection", "vclip", "hclip"),
        new CheckInfo("teleport", "Teleport", Category.MOVEMENT, "Teleport hack detection", "clicktp"),
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "No fall damage detection"),
        new CheckInfo("spider", "Spider", Category.MOVEMENT, "Wall climb detection", "wallclimb"),
        new CheckInfo("jesus", "Jesus", Category.MOVEMENT, "Water walk detection", "waterwalk"),
        new CheckInfo("fastladder", "FastLadder", Category.MOVEMENT, "Fast ladder climbing", "fastclimb"),
        new CheckInfo("timer", "Timer", Category.MOVEMENT, "Timer manipulation detection"),
        new CheckInfo("step", "Step", Category.MOVEMENT, "Step hack detection"),
        new CheckInfo("noweb", "NoWeb", Category.MOVEMENT, "Web slowdown bypass", "noslowdown"),
        new CheckInfo("boatfly", "BoatFly", Category.MOVEMENT, "Boat fly detection", "boatspeed"),
        new CheckInfo("elytra", "Elytra", Category.MOVEMENT, "Elytra exploit detection"),
        new CheckInfo("blink", "Blink", Category.MOVEMENT, "Blink hack detection"),
        new CheckInfo("groundspoof", "GroundSpoof", Category.MOVEMENT, "Ground spoofing detection"),

        // World
        new CheckInfo("fastplace", "FastPlace", Category.WORLD, "Fast block placing detection"),
        new CheckInfo("blockreach", "BlockReach", Category.WORLD, "Extended block reach detection"),
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Scaffold building detection", "tower"),
        new CheckInfo("xray", "X-Ray", Category.WORLD, "X-Ray usage detection"),
        new CheckInfo("ghosthand", "GhostHand", Category.WORLD, "Ghost hand detection"),
        new CheckInfo("fastbreak", "FastBreak", Category.WORLD, "Fast breaking detection", "nuker"),

        // Player
        new CheckInfo("fasteat", "FastEat", Category.PLAYER, "Fast eating detection"),
        new CheckInfo("regen", "Regen", Category.PLAYER, "Fast regeneration detection", "fastheal"),
        new CheckInfo("noswing", "NoSwing", Category.PLAYER, "No animation detection"),
        new CheckInfo("autorespawn", "AutoRespawn", Category.PLAYER, "Auto respawn detection"),
        new CheckInfo("inventorymove", "InventoryMove", Category.PLAYER, "Inventory movement detection"),
        new CheckInfo("improbable", "Improbable", Category.PLAYER, "Improbable action detection"),
        new CheckInfo("morepackets", "MorePackets", Category.PLAYER, "Packet spam detection")
    );

    // ========== NCP (NoCheatPlus) CHECKS ==========
    private static final List<CheckInfo> NCP_CHECKS = Arrays.asList(
        // Combat (Fight)
        new CheckInfo("angle", "Angle", Category.COMBAT, "Attack angle check"),
        new CheckInfo("critical", "Critical", Category.COMBAT, "Critical hit exploit check"),
        new CheckInfo("direction", "Direction", Category.COMBAT, "Attack direction check"),
        new CheckInfo("fastheal", "FastHeal", Category.COMBAT, "Fast healing check"),
        new CheckInfo("godmode", "GodMode", Category.COMBAT, "God mode check"),
        new CheckInfo("knockback", "Knockback", Category.COMBAT, "Knockback exploit check"),
        new CheckInfo("noswing", "NoSwing", Category.COMBAT, "No swing animation check"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Attack reach check"),
        new CheckInfo("selfhit", "SelfHit", Category.COMBAT, "Self hit check"),
        new CheckInfo("speed", "AttackSpeed", Category.COMBAT, "Attack speed check"),

        // Movement (Moving)
        new CheckInfo("survivalfly", "SurvivalFly", Category.MOVEMENT, "Survival flight check", "fly"),
        new CheckInfo("creativefly", "CreativeFly", Category.MOVEMENT, "Creative flight check"),
        new CheckInfo("morepackets", "MorePackets", Category.MOVEMENT, "Packet rate check"),
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "Fall damage bypass check"),
        new CheckInfo("passable", "Passable", Category.MOVEMENT, "Block passthrough check", "phase"),
        new CheckInfo("vehicle", "Vehicle", Category.MOVEMENT, "Vehicle movement check"),
        new CheckInfo("waterwalk", "WaterWalk", Category.MOVEMENT, "Water walking check", "jesus"),

        // Block Break
        new CheckInfo("fastbreak", "FastBreak", Category.WORLD, "Fast block breaking check"),
        new CheckInfo("frequency", "Frequency", Category.WORLD, "Block break frequency check"),
        new CheckInfo("noswing_break", "NoSwing", Category.WORLD, "No swing on break check"),
        new CheckInfo("wrongblock", "WrongBlock", Category.WORLD, "Wrong block break check"),

        // Block Place
        new CheckInfo("against", "Against", Category.WORLD, "Block place against check"),
        new CheckInfo("autoplace", "AutoSign", Category.WORLD, "Auto sign/place check"),
        new CheckInfo("fastplace", "FastPlace", Category.WORLD, "Fast block placing check"),
        new CheckInfo("noswing_place", "NoSwing", Category.WORLD, "No swing on place check"),
        new CheckInfo("speed_place", "PlaceSpeed", Category.WORLD, "Block place speed check"),

        // Block Interact
        new CheckInfo("direction_interact", "Direction", Category.PLAYER, "Interaction direction check"),
        new CheckInfo("reach_interact", "Reach", Category.PLAYER, "Interaction reach check"),
        new CheckInfo("speed_interact", "Speed", Category.PLAYER, "Interaction speed check"),
        new CheckInfo("visible", "Visible", Category.PLAYER, "Visibility check"),

        // Inventory
        new CheckInfo("fastclick", "FastClick", Category.PLAYER, "Fast inventory clicking"),
        new CheckInfo("instantbow", "InstantBow", Category.PLAYER, "Instant bow check"),
        new CheckInfo("instanteat", "InstantEat", Category.PLAYER, "Instant eating check"),
        new CheckInfo("open", "Open", Category.PLAYER, "Inventory open check"),

        // Chat
        new CheckInfo("captcha", "Captcha", Category.MISC, "Chat captcha check"),
        new CheckInfo("commands", "Commands", Category.MISC, "Command spam check"),
        new CheckInfo("text", "Text", Category.MISC, "Chat text check"),
        new CheckInfo("logins", "Logins", Category.MISC, "Login rate check"),
        new CheckInfo("relog", "Relog", Category.MISC, "Relog spam check")
    );

    // ========== THEMIS CHECKS ==========
    private static final List<CheckInfo> THEMIS_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Combat automation detection", "aura"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Extended reach detection"),

        // Movement
        new CheckInfo("bhop", "BHop", Category.MOVEMENT, "Bunny hop detection", "speed"),
        new CheckInfo("blink", "Blink", Category.MOVEMENT, "Blink/teleport detection"),
        new CheckInfo("boatfly", "BoatFly", Category.MOVEMENT, "Boat fly detection", "boatspeed", "boatmovement"),
        new CheckInfo("climb", "Climb", Category.MOVEMENT, "Wall climb detection", "spider"),
        new CheckInfo("elytrafly", "ElytraFly", Category.MOVEMENT, "Elytra fly detection"),
        new CheckInfo("fly", "Fly", Category.MOVEMENT, "Flight detection", "flight"),
        new CheckInfo("jetpack", "Jetpack", Category.MOVEMENT, "Jetpack detection"),
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "Fall damage bypass detection"),
        new CheckInfo("speed", "Speed", Category.MOVEMENT, "Speed hack detection"),
        new CheckInfo("step", "Step", Category.MOVEMENT, "Step hack detection"),
        new CheckInfo("timer", "Timer", Category.MOVEMENT, "Timer manipulation detection"),
        new CheckInfo("waterwalk", "WaterWalk", Category.MOVEMENT, "Water walking detection", "jesus"),

        // World
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Scaffold detection"),
        new CheckInfo("fastplace", "FastPlace", Category.WORLD, "Fast block placing detection"),

        // Player
        new CheckInfo("badpackets", "BadPackets", Category.PLAYER, "Bad packet detection", "packet", "illegalpacket")
    );

    // ========== FOXADDITION CHECKS ==========
    private static final List<CheckInfo> FOXADDITION_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Combat automation detection", "aura", "aimbot"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Extended reach detection"),
        new CheckInfo("autoclicker", "AutoClicker", Category.COMBAT, "Auto click detection"),
        new CheckInfo("velocity", "Velocity", Category.COMBAT, "Knockback bypass detection", "antikb"),

        // Movement
        new CheckInfo("fly", "Fly", Category.MOVEMENT, "Flight detection", "flight"),
        new CheckInfo("speed", "Speed", Category.MOVEMENT, "Speed hack detection"),
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "Fall damage bypass detection"),
        new CheckInfo("jesus", "Jesus", Category.MOVEMENT, "Water walking detection", "waterwalk"),
        new CheckInfo("step", "Step", Category.MOVEMENT, "Step hack detection"),
        new CheckInfo("timer", "Timer", Category.MOVEMENT, "Timer manipulation detection"),
        new CheckInfo("phase", "Phase", Category.MOVEMENT, "Block phasing detection"),
        new CheckInfo("elytra", "Elytra", Category.MOVEMENT, "Elytra exploit detection"),

        // Player
        new CheckInfo("badpackets", "BadPackets", Category.PLAYER, "Bad packet detection", "packet"),
        new CheckInfo("inventory", "Inventory", Category.PLAYER, "Inventory checks"),
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Scaffold detection")
    );

    // ========== LIGHTANTICHEAT CHECKS ==========
    private static final List<CheckInfo> LIGHTAC_CHECKS = Arrays.asList(
        // Combat
        new CheckInfo("killaura", "KillAura", Category.COMBAT, "Combat automation detection (A-D)", "aura"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Extended reach detection (A-B)"),
        new CheckInfo("criticals", "Criticals", Category.COMBAT, "Critical hit exploit detection (A-B)"),
        new CheckInfo("autoclicker", "AutoClicker", Category.COMBAT, "Auto click detection (A-B)"),
        new CheckInfo("velocity", "Velocity", Category.COMBAT, "Knockback bypass detection"),

        // Movement
        new CheckInfo("flight", "Flight", Category.MOVEMENT, "Flight detection (A-C)", "fly"),
        new CheckInfo("speed", "Speed", Category.MOVEMENT, "Speed hack detection (A-F)"),
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "Fall damage bypass detection (A-B)"),
        new CheckInfo("jump", "Jump", Category.MOVEMENT, "Jump hack detection (A-B)"),
        new CheckInfo("liquidwalk", "LiquidWalk", Category.MOVEMENT, "Water walking detection (A-B)", "jesus", "waterwalk"),
        new CheckInfo("fastclimb", "FastClimb", Category.MOVEMENT, "Fast ladder climbing detection"),
        new CheckInfo("noslow", "NoSlow", Category.MOVEMENT, "Slowdown bypass detection"),
        new CheckInfo("step", "Step", Category.MOVEMENT, "Step hack detection"),
        new CheckInfo("boat", "Boat", Category.MOVEMENT, "Boat exploit detection"),
        new CheckInfo("vehicle", "Vehicle", Category.MOVEMENT, "Vehicle exploit detection"),
        new CheckInfo("elytra", "Elytra", Category.MOVEMENT, "Elytra exploit detection (A-C)"),
        new CheckInfo("trident", "Trident", Category.MOVEMENT, "Trident riptide exploit detection"),

        // World
        new CheckInfo("airplace", "AirPlace", Category.WORLD, "Air placement detection"),
        new CheckInfo("fastplace", "FastPlace", Category.WORLD, "Fast block placing detection"),
        new CheckInfo("blockplace", "BlockPlace", Category.WORLD, "Block placement checks (A-B)"),
        new CheckInfo("ghostbreak", "GhostBreak", Category.WORLD, "Ghost breaking detection"),
        new CheckInfo("fastbreak", "FastBreak", Category.WORLD, "Fast breaking detection"),
        new CheckInfo("blockbreak", "BlockBreak", Category.WORLD, "Block breaking checks (A-B)"),
        new CheckInfo("scaffold", "Scaffold", Category.WORLD, "Scaffold detection (A-B)"),

        // Player
        new CheckInfo("morepackets", "MorePackets", Category.PLAYER, "Packet rate detection (A-B)"),
        new CheckInfo("timer", "Timer", Category.PLAYER, "Timer manipulation detection (A-B)"),
        new CheckInfo("badpackets", "BadPackets", Category.PLAYER, "Bad packet detection (A-D)", "packet"),
        new CheckInfo("sorting", "Sorting", Category.PLAYER, "Inventory sorting detection"),
        new CheckInfo("itemswap", "ItemSwap", Category.PLAYER, "Fast item swapping detection"),
        new CheckInfo("autobot", "AutoBot", Category.PLAYER, "Bot usage detection"),
        new CheckInfo("skinblinker", "SkinBlinker", Category.PLAYER, "Skin blinker detection")
    );

    // ========== CHECK REGISTRY ==========
    private static final Map<String, List<CheckInfo>> ANTICHEAT_CHECKS = new HashMap<>();

    static {
        ANTICHEAT_CHECKS.put("grim", GRIM_CHECKS);
        ANTICHEAT_CHECKS.put("vulcan", VULCAN_CHECKS);
        ANTICHEAT_CHECKS.put("matrix", MATRIX_CHECKS);
        ANTICHEAT_CHECKS.put("spartan", SPARTAN_CHECKS);
        ANTICHEAT_CHECKS.put("ncp", NCP_CHECKS);
        ANTICHEAT_CHECKS.put("nocheatplus", NCP_CHECKS);
        ANTICHEAT_CHECKS.put("themis", THEMIS_CHECKS);
        ANTICHEAT_CHECKS.put("foxaddition", FOXADDITION_CHECKS);
        ANTICHEAT_CHECKS.put("fox", FOXADDITION_CHECKS);
        ANTICHEAT_CHECKS.put("lightac", LIGHTAC_CHECKS);
        ANTICHEAT_CHECKS.put("lightanticheat", LIGHTAC_CHECKS);
        ANTICHEAT_CHECKS.put("lac", LIGHTAC_CHECKS);
    }

    /**
     * Get all known checks for an anticheat
     */
    public static List<CheckInfo> getChecks(String anticheat) {
        return ANTICHEAT_CHECKS.getOrDefault(anticheat.toLowerCase(), Collections.emptyList());
    }

    /**
     * Get checks by category for an anticheat
     */
    public static List<CheckInfo> getChecksByCategory(String anticheat, Category category) {
        List<CheckInfo> result = new ArrayList<>();
        for (CheckInfo check : getChecks(anticheat)) {
            if (check.getCategory() == category) {
                result.add(check);
            }
        }
        return result;
    }

    /**
     * Find a check info by name (with alias matching)
     */
    public static CheckInfo findCheck(String anticheat, String checkName) {
        for (CheckInfo check : getChecks(anticheat)) {
            if (check.matches(checkName)) {
                return check;
            }
        }
        return null;
    }

    /**
     * Normalize a check name to its canonical form
     */
    public static String normalizeCheckName(String anticheat, String checkName) {
        CheckInfo info = findCheck(anticheat, checkName);
        return info != null ? info.getDisplayName() : checkName;
    }

    /**
     * Get category for a check
     */
    public static Category getCategory(String anticheat, String checkName) {
        CheckInfo info = findCheck(anticheat, checkName);
        if (info != null) {
            return info.getCategory();
        }

        // Guess category from name
        String lower = checkName.toLowerCase();
        if (lower.contains("aura") || lower.contains("reach") || lower.contains("hit") ||
            lower.contains("click") || lower.contains("aim") || lower.contains("velocity") ||
            lower.contains("kb") || lower.contains("knockback") || lower.contains("crit")) {
            return Category.COMBAT;
        }
        if (lower.contains("fly") || lower.contains("speed") || lower.contains("move") ||
            lower.contains("motion") || lower.contains("jump") || lower.contains("step") ||
            lower.contains("climb") || lower.contains("jesus") || lower.contains("water") ||
            lower.contains("elytra") || lower.contains("vehicle") || lower.contains("boat") ||
            lower.contains("timer") || lower.contains("phase") || lower.contains("blink") ||
            lower.contains("nofall") || lower.contains("noslow") || lower.contains("strafe")) {
            return Category.MOVEMENT;
        }
        if (lower.contains("scaffold") || lower.contains("place") || lower.contains("break") ||
            lower.contains("block") || lower.contains("xray") || lower.contains("nuker")) {
            return Category.WORLD;
        }
        if (lower.contains("packet") || lower.contains("inventory") || lower.contains("ping") ||
            lower.contains("invalid") || lower.contains("crash") || lower.contains("improbable")) {
            return Category.PLAYER;
        }

        return Category.MISC;
    }

    /**
     * Get all supported anticheat names
     */
    public static Set<String> getSupportedAnticheats() {
        return new HashSet<>(Arrays.asList("grim", "vulcan", "matrix", "spartan", "ncp", "themis", "foxaddition", "lightac"));
    }

    /**
     * Check if an anticheat is supported
     */
    public static boolean isSupported(String anticheat) {
        return ANTICHEAT_CHECKS.containsKey(anticheat.toLowerCase());
    }
}
