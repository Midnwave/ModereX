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

    // ========== GRIM CHECKS (from decompiled v2.3.72) ==========
    private static final List<CheckInfo> GRIM_CHECKS = Arrays.asList(
        // Aim
        new CheckInfo("aimduplicatelook", "AimDuplicateLook", Category.COMBAT, "Detects duplicate look packets"),
        new CheckInfo("aimmodulo360", "AimModulo360", Category.COMBAT, "Detects modulo 360 aim snapping"),

        // BadPackets (A-Y)
        new CheckInfo("badpacketsa", "BadPacketsA", Category.PLAYER, "Sent duplicate slot id"),
        new CheckInfo("badpacketsb", "BadPacketsB", Category.PLAYER, "Sent duplicate sneaking status"),
        new CheckInfo("badpacketsc", "BadPacketsC", Category.PLAYER, "Sent duplicate sprinting status"),
        new CheckInfo("badpacketsd", "BadPacketsD", Category.PLAYER, "Sent spectate packets while not in spectator"),
        new CheckInfo("badpacketse", "BadPacketsE", Category.PLAYER, "Sent creative mode inventory click while not creative"),
        new CheckInfo("badpacketsf", "BadPacketsF", Category.PLAYER, "Sent out of bounds slot id"),
        new CheckInfo("badpacketsg", "BadPacketsG", Category.PLAYER, "Sent out of bounds cursor position"),
        new CheckInfo("badpacketsh", "BadPacketsH", Category.PLAYER, "Claimed to be in a vehicle while not in one"),
        new CheckInfo("badpacketsi", "BadPacketsI", Category.PLAYER, "Sent boat paddle states while not in a boat"),
        new CheckInfo("badpacketsj", "BadPacketsJ", Category.PLAYER, "Sent incorrect boat paddle states"),
        new CheckInfo("badpacketsk", "BadPacketsK", Category.PLAYER, "Jumped in a vehicle that cannot jump"),
        new CheckInfo("badpacketsl", "BadPacketsL", Category.PLAYER, "Invalid click packets"),
        new CheckInfo("badpacketsm", "BadPacketsM", Category.PLAYER, "Sent impossible use item packet"),
        new CheckInfo("badpacketsn", "BadPacketsN", Category.PLAYER, "Sent impossible dig packet"),
        new CheckInfo("badpacketso", "BadPacketsO", Category.PLAYER, "Sent impossible block face id"),
        new CheckInfo("badpacketsp", "BadPacketsP", Category.PLAYER, "Sent non-finite position or rotation"),
        new CheckInfo("badpacketsq", "BadPacketsQ", Category.PLAYER, "Sent invalid cursor position"),
        new CheckInfo("badpacketsr", "BadPacketsR", Category.PLAYER, "Impossible pitch value"),
        new CheckInfo("badpacketss", "BadPacketsS", Category.PLAYER, "Claimed to be flying while unable to fly"),
        new CheckInfo("badpacketst", "BadPacketsT", Category.PLAYER, "Impossible input values"),
        new CheckInfo("badpacketsu", "BadPacketsU", Category.PLAYER, "Sent negative sequence id"),
        new CheckInfo("badpacketsv", "BadPacketsV", Category.PLAYER, "Sent unexpected sequence id"),
        new CheckInfo("badpacketsw", "BadPacketsW", Category.PLAYER, "Clicking slots in lectern window"),
        new CheckInfo("badpacketsx", "BadPacketsX", Category.PLAYER, "Ignored set rotation packet"),
        new CheckInfo("badpacketsy", "BadPacketsY", Category.PLAYER, "Invalid book edit"),

        // Breaking
        new CheckInfo("airliquidbreak", "AirLiquidBreak", Category.WORLD, "Breaking a block that cannot be broken"),
        new CheckInfo("farbreak", "FarBreak", Category.WORLD, "Breaking blocks too far away"),
        new CheckInfo("fastbreak", "FastBreak", Category.WORLD, "Breaking blocks too quickly"),
        new CheckInfo("invalidbreak", "InvalidBreak", Category.WORLD, "Invalid block break"),
        new CheckInfo("multibreak", "MultiBreak", Category.WORLD, "Breaking multiple blocks in one tick"),
        new CheckInfo("noswingbreak", "NoSwingBreak", Category.WORLD, "Did not swing while breaking block"),
        new CheckInfo("positionbreaka", "PositionBreakA", Category.WORLD, "Invalid break position"),
        new CheckInfo("positionbreakb", "PositionBreakB", Category.WORLD, "Invalid break position (variant)"),
        new CheckInfo("rotationbreak", "RotationBreak", Category.WORLD, "Invalid rotation while breaking"),
        new CheckInfo("wrongbreak", "WrongBreak", Category.WORLD, "Breaking the wrong block"),

        // Chat
        new CheckInfo("chata", "ChatA", Category.MISC, "Invalid chat message"),
        new CheckInfo("chatb", "ChatB", Category.MISC, "Chatting while chat is hidden"),
        new CheckInfo("chatc", "ChatC", Category.MISC, "Moving while chatting"),
        new CheckInfo("chatd", "ChatD", Category.MISC, "Too long item name in anvil"),

        // Combat
        new CheckInfo("hitboxes", "Hitboxes", Category.COMBAT, "Hitbox expansion detection"),
        new CheckInfo("multiinteracta", "MultiInteractA", Category.COMBAT, "Interacted with multiple entities in the same tick"),
        new CheckInfo("multiinteractb", "MultiInteractB", Category.COMBAT, "Multiple interact variant"),
        new CheckInfo("reach", "Reach", Category.COMBAT, "Detects extended attack reach"),

        // Crash
        new CheckInfo("crasha", "CrashA", Category.PLAYER, "Crash attempt type A"),
        new CheckInfo("crashb", "CrashB", Category.PLAYER, "Crash attempt type B"),
        new CheckInfo("crashc", "CrashC", Category.PLAYER, "Crash attempt type C"),
        new CheckInfo("crashd", "CrashD", Category.PLAYER, "Crash attempt type D"),
        new CheckInfo("crashe", "CrashE", Category.PLAYER, "Crash attempt type E"),
        new CheckInfo("crashf", "CrashF", Category.PLAYER, "Crash attempt type F"),
        new CheckInfo("crashg", "CrashG", Category.PLAYER, "Crash attempt type G"),
        new CheckInfo("crashh", "CrashH", Category.PLAYER, "Crash attempt type H"),
        new CheckInfo("crashi", "CrashI", Category.PLAYER, "Crash attempt type I"),

        // Elytra
        new CheckInfo("elytraa", "ElytraA", Category.MOVEMENT, "Started gliding without an elytra"),
        new CheckInfo("elytrab", "ElytraB", Category.MOVEMENT, "Started gliding on ground"),
        new CheckInfo("elytrac", "ElytraC", Category.MOVEMENT, "Started gliding while flying"),
        new CheckInfo("elytrad", "ElytraD", Category.MOVEMENT, "Started gliding with levitation"),
        new CheckInfo("elytrae", "ElytraE", Category.MOVEMENT, "Started gliding without jumping"),
        new CheckInfo("elytraf", "ElytraF", Category.MOVEMENT, "Started gliding while already gliding"),
        new CheckInfo("elytrag", "ElytraG", Category.MOVEMENT, "Started gliding too frequently"),
        new CheckInfo("elytrah", "ElytraH", Category.MOVEMENT, "Started gliding in vehicle"),
        new CheckInfo("elytrai", "ElytraI", Category.MOVEMENT, "Started gliding in water"),

        // Exploit
        new CheckInfo("exploita", "ExploitA", Category.PLAYER, "Exploit type A"),
        new CheckInfo("exploitb", "ExploitB", Category.PLAYER, "Tried to respawn while alive"),

        // GroundSpoof
        new CheckInfo("nofall", "NoFall", Category.MOVEMENT, "Ground spoof / no fall damage"),
        new CheckInfo("groundspoof", "GroundSpoof", Category.MOVEMENT, "Ground spoofing detection"),

        // Misc
        new CheckInfo("post", "Post", Category.PLAYER, "Post packet timing check"),
        new CheckInfo("transactionorder", "TransactionOrder", Category.PLAYER, "Transaction order check"),
        new CheckInfo("clientbrand", "ClientBrand", Category.MISC, "Client brand check"),

        // Movement
        new CheckInfo("noslow", "NoSlow", Category.MOVEMENT, "Was not slowed while using an item"),

        // MultiActions
        new CheckInfo("multiactionsa", "MultiActionsA", Category.PLAYER, "Attacked while using an item"),
        new CheckInfo("multiactionsb", "MultiActionsB", Category.PLAYER, "Breaking blocks while using an item"),
        new CheckInfo("multiactionsc", "MultiActionsC", Category.PLAYER, "Swinging while using an item"),
        new CheckInfo("multiactionsd", "MultiActionsD", Category.PLAYER, "Attacking while rowing a boat"),
        new CheckInfo("multiactionse", "MultiActionsE", Category.PLAYER, "Interacting with block and entity in same tick"),
        new CheckInfo("multiactionsf", "MultiActionsF", Category.PLAYER, "Did not swing for attack"),
        new CheckInfo("multiactionsg", "MultiActionsG", Category.PLAYER, "Closed inventory while moving"),

        // PacketOrder
        new CheckInfo("packetordera", "PacketOrderA", Category.PLAYER, "Packet order check A"),
        new CheckInfo("packetorderb", "PacketOrderB", Category.PLAYER, "Packet order check B"),
        new CheckInfo("packetorderc", "PacketOrderC", Category.PLAYER, "Interacted with non-existent entity"),
        new CheckInfo("packetorderd", "PacketOrderD", Category.PLAYER, "Interacted with self"),
        new CheckInfo("packetordere", "PacketOrderE", Category.PLAYER, "Packet order check E"),
        new CheckInfo("packetorderf", "PacketOrderF", Category.PLAYER, "Packet order check F"),
        new CheckInfo("packetorderg", "PacketOrderG", Category.PLAYER, "Rotation in use item did not match tick"),
        new CheckInfo("packetorderh", "PacketOrderH", Category.PLAYER, "Clicked in inventory while moving"),
        new CheckInfo("packetorderi", "PacketOrderI", Category.PLAYER, "Packet order check I"),
        new CheckInfo("packetorderj", "PacketOrderJ", Category.PLAYER, "Packet order check J"),
        new CheckInfo("packetorderk", "PacketOrderK", Category.PLAYER, "Packet order check K"),
        new CheckInfo("packetorderl", "PacketOrderL", Category.PLAYER, "Packet order check L"),
        new CheckInfo("packetorderm", "PacketOrderM", Category.PLAYER, "Packet order check M"),
        new CheckInfo("packetordern", "PacketOrderN", Category.PLAYER, "Packet order check N"),
        new CheckInfo("packetordero", "PacketOrderO", Category.PLAYER, "Packet order check O"),

        // Prediction
        new CheckInfo("simulation", "Simulation", Category.MOVEMENT, "Movement prediction simulation"),
        new CheckInfo("phase", "Phase", Category.MOVEMENT, "Phasing through blocks"),

        // Scaffolding
        new CheckInfo("airliquidplace", "AirLiquidPlace", Category.WORLD, "Placed a block in air/liquid"),
        new CheckInfo("duplicaterotplace", "DuplicateRotPlace", Category.WORLD, "Duplicate rotation while placing"),
        new CheckInfo("fabricatedplace", "FabricatedPlace", Category.WORLD, "Fabricated block placement"),
        new CheckInfo("farplace", "FarPlace", Category.WORLD, "Placing blocks from too far away"),
        new CheckInfo("invalidplacea", "InvalidPlaceA", Category.WORLD, "Placed a block against a hidden face"),
        new CheckInfo("invalidplaceb", "InvalidPlaceB", Category.WORLD, "Placed a block against invalid support"),
        new CheckInfo("multiplace", "MultiPlace", Category.WORLD, "Placed multiple blocks in a tick"),
        new CheckInfo("positionplace", "PositionPlace", Category.WORLD, "Placed a block while not looking at it"),
        new CheckInfo("rotationplace", "RotationPlace", Category.WORLD, "Invalid rotation while placing"),

        // Sprint
        new CheckInfo("sprinta", "SprintA", Category.MOVEMENT, "Sprinting while colliding with a wall"),
        new CheckInfo("sprintb", "SprintB", Category.MOVEMENT, "Sprinting while sneaking or crawling"),
        new CheckInfo("sprintc", "SprintC", Category.MOVEMENT, "Sprinting while using an item"),
        new CheckInfo("sprintd", "SprintD", Category.MOVEMENT, "Sprinting with too low hunger"),
        new CheckInfo("sprinte", "SprintE", Category.MOVEMENT, "Started sprinting while having blindness"),
        new CheckInfo("sprintf", "SprintF", Category.MOVEMENT, "Sprinting while gliding"),
        new CheckInfo("sprintg", "SprintG", Category.MOVEMENT, "Sprinting while in water"),

        // Timer
        new CheckInfo("timer", "Timer", Category.MOVEMENT, "Game timer manipulation"),
        new CheckInfo("negativetimer", "NegativeTimer", Category.MOVEMENT, "Negative timer detection"),
        new CheckInfo("ticktimer", "TickTimer", Category.MOVEMENT, "Tick timer manipulation"),
        new CheckInfo("timerlimit", "TimerLimit", Category.MOVEMENT, "Did not move far enough"),
        new CheckInfo("vehicletimer", "VehicleTimer", Category.MOVEMENT, "Vehicle timer manipulation"),

        // Vehicle
        new CheckInfo("vehiclea", "VehicleA", Category.MOVEMENT, "Vehicle check A"),
        new CheckInfo("vehicleb", "VehicleB", Category.MOVEMENT, "Vehicle check B"),
        new CheckInfo("vehiclec", "VehicleC", Category.MOVEMENT, "Vehicle check C"),
        new CheckInfo("vehicled", "VehicleD", Category.MOVEMENT, "Vehicle check D"),
        new CheckInfo("vehiclee", "VehicleE", Category.MOVEMENT, "Vehicle check E"),
        new CheckInfo("vehiclef", "VehicleF", Category.MOVEMENT, "Vehicle check F"),

        // Velocity
        new CheckInfo("antikb", "AntiKB", Category.COMBAT, "Knockback bypass detection", "antiknockback"),
        new CheckInfo("antiexplosion", "AntiExplosion", Category.COMBAT, "Explosion knockback bypass")
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
