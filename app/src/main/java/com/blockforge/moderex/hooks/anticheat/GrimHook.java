package com.blockforge.moderex.hooks.anticheat;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hook for GrimAC using their FlagEvent API
 * Grim 2.3.72+ uses an internal EventBus, not Bukkit events
 * FlagEvent gives us player, check name, violations, and verbose info
 *
 * TODO: Grim Auto-Punishment in Automod
 * - Add automod rules that can auto-punish based on Grim VL thresholds
 * - Configurable per-check punishments (e.g., "Reach VL > 50 = ban 7d")
 * - Web panel UI for configuring these rules under automod page
 * - Support for warning -> mute -> tempban -> ban escalation
 * - Track VL across sessions (database storage)
 *
 * TODO: Older Grim Version Support (1.21+)
 * - Support older Grim versions that may use different FlagEvent API
 * - Auto-detect Grim version and use appropriate hook method
 * - May need separate listeners for pre-EventBus versions
 * - Test compatibility with Grim 2.3.x series variations
 * - Handle different check names between versions
 */
public class GrimHook extends AnticheatHook implements Listener {

    private static final String[] PLUGIN_NAMES = {"Grim", "GrimAC"};
    private Class<?> flagEventClass; // Can be internal or Bukkit event
    private Plugin grimPlugin;
    private ClassLoader grimClassLoader;
    private Object grimApi;
    private Object eventBus;

    // keeps track of what checks we've seen for the web panel
    private final Set<String> discoveredChecks = ConcurrentHashMap.newKeySet();

    // all Grim check types from decompiled source (v2.3.72)
    private static final String[] KNOWN_CHECKS = {
        // Aim
        "AimDuplicateLook", "AimModulo360",
        // BadPackets (A-Y)
        "BadPacketsA", "BadPacketsB", "BadPacketsC", "BadPacketsD", "BadPacketsE",
        "BadPacketsF", "BadPacketsG", "BadPacketsH", "BadPacketsI", "BadPacketsJ",
        "BadPacketsK", "BadPacketsL", "BadPacketsM", "BadPacketsN", "BadPacketsO",
        "BadPacketsP", "BadPacketsQ", "BadPacketsR", "BadPacketsS", "BadPacketsT",
        "BadPacketsU", "BadPacketsV", "BadPacketsW", "BadPacketsX", "BadPacketsY",
        // Breaking
        "AirLiquidBreak", "FarBreak", "FastBreak", "InvalidBreak", "MultiBreak",
        "NoSwingBreak", "PositionBreakA", "PositionBreakB", "RotationBreak", "WrongBreak",
        // Chat
        "ChatA", "ChatB", "ChatC", "ChatD",
        // Combat
        "Hitboxes", "MultiInteractA", "MultiInteractB", "Reach",
        // Crash
        "CrashA", "CrashB", "CrashC", "CrashD", "CrashE",
        "CrashF", "CrashG", "CrashH", "CrashI",
        // Elytra
        "ElytraA", "ElytraB", "ElytraC", "ElytraD", "ElytraE",
        "ElytraF", "ElytraG", "ElytraH", "ElytraI",
        // Exploit
        "ExploitA", "ExploitB",
        // GroundSpoof
        "NoFall", "GroundSpoof",
        // Misc
        "Post", "TransactionOrder", "ClientBrand",
        // Movement
        "NoSlow",
        // MultiActions
        "MultiActionsA", "MultiActionsB", "MultiActionsC", "MultiActionsD",
        "MultiActionsE", "MultiActionsF", "MultiActionsG",
        // PacketOrder
        "PacketOrderA", "PacketOrderB", "PacketOrderC", "PacketOrderD", "PacketOrderE",
        "PacketOrderF", "PacketOrderG", "PacketOrderH", "PacketOrderI", "PacketOrderJ",
        "PacketOrderK", "PacketOrderL", "PacketOrderM", "PacketOrderN", "PacketOrderO",
        // Prediction
        "Simulation", "Phase",
        // Scaffolding
        "AirLiquidPlace", "DuplicateRotPlace", "FabricatedPlace", "FarPlace",
        "InvalidPlaceA", "InvalidPlaceB", "MultiPlace", "PositionPlace", "RotationPlace",
        // Sprint
        "SprintA", "SprintB", "SprintC", "SprintD", "SprintE", "SprintF", "SprintG",
        // Timer
        "Timer", "NegativeTimer", "TickTimer", "TimerLimit", "VehicleTimer",
        // Vehicle
        "VehicleA", "VehicleB", "VehicleC", "VehicleD", "VehicleE", "VehicleF",
        // Velocity
        "AntiKB", "AntiExplosion"
    };

    public GrimHook(ModereX plugin) {
        super(plugin, "Grim");
        plugin.getLogger().info("[GrimHook] Constructor called");
        // pre-populate known checks
        discoveredChecks.addAll(Arrays.asList(KNOWN_CHECKS));
        plugin.getLogger().info("[GrimHook] Initialized with " + KNOWN_CHECKS.length + " known checks");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean hook() {
        // look for Grim plugin under any of its names
        plugin.getLogger().info("[Grim] Looking for Grim plugin...");
        for (String name : PLUGIN_NAMES) {
            grimPlugin = Bukkit.getPluginManager().getPlugin(name);
            if (grimPlugin != null) {
                plugin.getLogger().info("[Grim] Found Grim plugin: " + name + " v" + grimPlugin.getDescription().getVersion());
                break;
            }
        }

        if (grimPlugin == null) {
            plugin.getLogger().info("[Grim] Grim plugin not found");
            return false;
        }

        grimClassLoader = grimPlugin.getClass().getClassLoader();
        plugin.getLogger().info("[Grim] Using classloader: " + grimClassLoader.getClass().getName());

        // Try to use Grim's internal EventBus API (required for 2.3.72+)
        if (tryGrimInternalEventBus()) {
            // Auto-disable Grim's native alert messages
            disableGrimAlerts();
            return true;
        }

        // Fallback: Try Bukkit event registration for older Grim versions
        if (tryBukkitEventRegistration()) {
            // Auto-disable Grim's native alert messages
            disableGrimAlerts();
            return true;
        }

        // still mark it as enabled even if we couldn't hook the API
        enabled = true;
        plugin.getLogger().info("[Grim] Running in passive mode (no event API)");
        return true;
    }

    /**
     * Try to register using Grim's internal EventBus API (2.3.72+)
     */
    @SuppressWarnings("unchecked")
    private boolean tryGrimInternalEventBus() {
        plugin.getLogger().info("[Grim] Attempting to use Grim's internal EventBus API...");

        try {
            // Load the internal FlagEvent class
            flagEventClass = Class.forName("ac.grim.grimac.api.event.events.FlagEvent", true, grimClassLoader);
            plugin.getLogger().info("[Grim] Found internal FlagEvent class: " + flagEventClass.getName());

            // Get GrimAPIProvider.get() to access the API
            Class<?> apiProviderClass = Class.forName("ac.grim.grimac.api.GrimAPIProvider", true, grimClassLoader);
            Method getMethod = apiProviderClass.getMethod("get");
            grimApi = getMethod.invoke(null);

            if (grimApi == null) {
                plugin.getLogger().warning("[Grim] GrimAPI returned null");
                return false;
            }

            plugin.getLogger().info("[Grim] Got GrimAPI instance: " + grimApi.getClass().getName());

            // Get the EventBus
            Method getEventBusMethod = grimApi.getClass().getMethod("getEventBus");
            eventBus = getEventBusMethod.invoke(grimApi);

            if (eventBus == null) {
                plugin.getLogger().warning("[Grim] EventBus is null");
                return false;
            }

            plugin.getLogger().info("[Grim] Got EventBus: " + eventBus.getClass().getName());

            // Create a GrimEventListener proxy to handle events
            Class<?> listenerInterface = Class.forName("ac.grim.grimac.api.event.GrimEventListener", true, grimClassLoader);

            Object listenerProxy = java.lang.reflect.Proxy.newProxyInstance(
                grimClassLoader,
                new Class<?>[] { listenerInterface },
                (proxy, method, args) -> {
                    if (method.getName().equals("handle") && args != null && args.length == 1) {
                        handleGrimInternalEvent(args[0]);
                    }
                    return null;
                }
            );

            plugin.getLogger().info("[Grim] Created event listener proxy");

            // Find the subscribe method - try different overloads
            Method subscribeMethod = null;
            for (Method m : eventBus.getClass().getMethods()) {
                if (m.getName().equals("subscribe")) {
                    Class<?>[] params = m.getParameterTypes();
                    // Look for: subscribe(GrimPlugin, Class, GrimEventListener, int, boolean, Class)
                    // or: subscribe(GrimPlugin, Class, GrimEventListener)
                    if (params.length == 6 || params.length == 5 || params.length == 3) {
                        subscribeMethod = m;
                        plugin.getLogger().info("[Grim] Found subscribe method with " + params.length + " params");
                        break;
                    }
                }
            }

            if (subscribeMethod == null) {
                plugin.getLogger().warning("[Grim] Could not find suitable subscribe method");
                return false;
            }

            // Call subscribe with appropriate parameters
            Class<?>[] paramTypes = subscribeMethod.getParameterTypes();
            if (paramTypes.length == 3) {
                // subscribe(GrimPlugin, Class<T>, GrimEventListener<T>)
                subscribeMethod.invoke(eventBus, null, flagEventClass, listenerProxy);
            } else if (paramTypes.length == 5) {
                // subscribe(GrimPlugin, Class<T>, GrimEventListener<T>, int priority, boolean ignoreCancelled)
                subscribeMethod.invoke(eventBus, null, flagEventClass, listenerProxy, 0, false);
            } else if (paramTypes.length == 6) {
                // subscribe(GrimPlugin, Class<T>, GrimEventListener<T>, int priority, boolean ignoreCancelled, Class source)
                subscribeMethod.invoke(eventBus, null, flagEventClass, listenerProxy, 0, false, this.getClass());
            }

            enabled = true;
            plugin.getLogger().info("[Grim] Successfully subscribed to FlagEvent via Grim's EventBus!");
            plugin.getLogger().info("[Grim] ModereX will now receive Grim alerts");
            plugin.getLogger().info("[Grim] TIP: To hide Grim's messages, set 'alert-interval: -1' in punishments.yml");
            return true;

        } catch (IllegalStateException e) {
            plugin.getLogger().warning("[Grim] GrimAPI not initialized yet: " + e.getMessage());
            // Schedule a delayed retry
            plugin.getServer().getScheduler().runTaskLater(plugin, this::retryGrimHook, 40L);
            return false;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("[Grim] Internal EventBus API not available: " + e.getMessage());
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("[Grim] Failed to use Grim's EventBus API: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retry hooking after Grim is fully loaded
     */
    private void retryGrimHook() {
        plugin.getLogger().info("[Grim] Retrying hook after delay...");
        if (!enabled && tryGrimInternalEventBus()) {
            plugin.getLogger().info("[Grim] Delayed hook successful!");
            // Auto-disable Grim's native alert messages
            disableGrimAlerts();
            // Notify AnticheatManager about this late hook
            if (plugin.getAnticheatManager() != null) {
                plugin.getAnticheatManager().registerLateHook(this);
            }
        }
    }

    /**
     * Try Bukkit event registration (for older Grim versions with Bukkit events)
     */
    @SuppressWarnings("unchecked")
    private boolean tryBukkitEventRegistration() {
        plugin.getLogger().info("[Grim] Trying Bukkit event registration (for older Grim)...");

        // Try to find Bukkit-compatible FlagEvent
        String[] bukkitEventClasses = {
            "ac.grim.grimac.api.events.FlagEvent",
            "ac.grim.grimac.events.FlagEvent",
        };

        Class<? extends Event> bukkitEventClass = null;
        for (String className : bukkitEventClasses) {
            try {
                Class<?> clazz = Class.forName(className, true, grimClassLoader);
                if (Event.class.isAssignableFrom(clazz)) {
                    bukkitEventClass = (Class<? extends Event>) clazz;
                    plugin.getLogger().info("[Grim] Found Bukkit event class: " + className);
                    break;
                }
            } catch (ClassNotFoundException e) {
                plugin.getLogger().info("[Grim] Bukkit event not found: " + className);
            }
        }

        if (bukkitEventClass == null) {
            plugin.getLogger().info("[Grim] No Bukkit-compatible FlagEvent found");
            return false;
        }

        try {
            final Class<? extends Event> finalEventClass = bukkitEventClass;
            EventExecutor executor = (listener, event) -> {
                if (!enabled) return;
                if (!finalEventClass.isInstance(event)) return;
                handleGrimEvent(event);
            };

            plugin.getServer().getPluginManager().registerEvent(
                bukkitEventClass, this, EventPriority.LOWEST, executor, plugin, false
            );

            enabled = true;
            plugin.getLogger().info("[Grim] Successfully registered via Bukkit events!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("[Grim] Bukkit event registration failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handle events from Grim's internal EventBus (2.3.72+)
     * NOTE: We do NOT cancel the event - this allows Grim to still do setbacks/teleports
     * To hide Grim's alert messages, we auto-modify punishments.yml
     */
    private void handleGrimInternalEvent(Object event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            double violationsDouble = 0;
            String verbose = "";

            // Get user from the event
            Object grimUser = null;
            try {
                Method getUser = event.getClass().getMethod("getUser");
                grimUser = getUser.invoke(event);
            } catch (NoSuchMethodException e) {
                try {
                    Method getPlayer = event.getClass().getMethod("getPlayer");
                    grimUser = getPlayer.invoke(event);
                } catch (NoSuchMethodException ignored) {}
            }

            if (grimUser != null) {
                // Get UUID from GrimUser
                try {
                    Method getUniqueId = grimUser.getClass().getMethod("getUniqueId");
                    Object uuid = getUniqueId.invoke(grimUser);
                    if (uuid instanceof UUID) {
                        player = Bukkit.getPlayer((UUID) uuid);
                    }
                } catch (Exception ignored) {}

                // Fallback to name
                if (player == null) {
                    try {
                        Method getName = grimUser.getClass().getMethod("getName");
                        String playerName = (String) getName.invoke(grimUser);
                        if (playerName != null) {
                            player = Bukkit.getPlayer(playerName);
                        }
                    } catch (Exception ignored) {}
                }
            }

            // Try to get violations directly from the event first (most reliable)
            try {
                Method getViolations = event.getClass().getMethod("getViolations");
                Object vl = getViolations.invoke(event);
                if (vl instanceof Number) {
                    violationsDouble = ((Number) vl).doubleValue();
                }
            } catch (Exception ignored) {}

            // Get check info
            try {
                Method getCheck = event.getClass().getMethod("getCheck");
                Object check = getCheck.invoke(event);
                if (check != null) {
                    try {
                        Method getCheckName = check.getClass().getMethod("getCheckName");
                        Object name = getCheckName.invoke(check);
                        if (name instanceof String) {
                            checkName = (String) name;
                        }
                    } catch (NoSuchMethodException e) {
                        checkName = check.getClass().getSimpleName();
                    }

                    // If we didn't get violations from event, try from check
                    if (violationsDouble == 0) {
                        try {
                            Method getViolations = check.getClass().getMethod("getViolations");
                            Object vl = getViolations.invoke(check);
                            if (vl instanceof Number) {
                                violationsDouble = ((Number) vl).doubleValue();
                            }
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}

            // Get verbose info
            try {
                Method getVerbose = event.getClass().getMethod("getVerbose");
                Object result = getVerbose.invoke(event);
                if (result instanceof String) {
                    verbose = (String) result;
                }
            } catch (Exception ignored) {}

            // Track new check types
            if (!discoveredChecks.contains(checkName)) {
                discoveredChecks.add(checkName);
                plugin.logDebug("[Grim] Discovered new check type: " + checkName);
            }

            // Convert violations to int - use ceiling to show at least 1 if there's any violation
            int violations = (int) Math.ceil(violationsDouble);
            if (violations < 1) violations = 1;

            // Do NOT cancel the event - we want Grim to still do setbacks
            // To hide Grim's messages, use disableGrimAlerts() or set alert-interval: -1 in punishments.yml

            if (player != null) {
                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    checkName,
                    "A",
                    violations,
                    violations,
                    verbose.isEmpty() ? "Grim detection" : verbose
                ));
            }
        } catch (Exception e) {
            plugin.logDebug("[Grim] Error handling internal event: " + e.getMessage());
        }
    }

    @Override
    public void unhook() {
        if (enabled) {
            HandlerList.unregisterAll(this);
            enabled = false;
        }
    }

    /**
     * Handle events from Bukkit event API (older Grim versions)
     * NOTE: We do NOT cancel the event - this allows Grim to still do setbacks/teleports
     */
    private void handleGrimEvent(Event event) {
        try {
            Player player = null;
            String checkName = "Unknown";
            double violationsDouble = 0;
            String verbose = "";

            // GrimAPI uses getUser() which returns a GrimUser, not a Player
            Object grimUser = null;

            // try getUser() first (newer API)
            try {
                Method getUser = event.getClass().getMethod("getUser");
                grimUser = getUser.invoke(event);
            } catch (NoSuchMethodException e) {
                // fallback to getPlayer() (older API, returns GrimUser not Player)
                try {
                    Method getPlayer = event.getClass().getMethod("getPlayer");
                    grimUser = getPlayer.invoke(event);
                } catch (NoSuchMethodException ignored) {}
            }

            if (grimUser != null) {
                // GrimUser has getUniqueId() from GrimIdentity interface
                try {
                    Method getUniqueId = grimUser.getClass().getMethod("getUniqueId");
                    Object uuid = getUniqueId.invoke(grimUser);
                    if (uuid instanceof UUID) {
                        player = Bukkit.getPlayer((UUID) uuid);
                    }
                } catch (NoSuchMethodException ignored) {}

                // fallback to getName() if UUID didn't work
                if (player == null) {
                    try {
                        Method getName = grimUser.getClass().getMethod("getName");
                        String playerName = (String) getName.invoke(grimUser);
                        if (playerName != null) {
                            player = Bukkit.getPlayer(playerName);
                        }
                    } catch (NoSuchMethodException ignored) {}
                }
            }

            // Try to get violations directly from the event first (most reliable)
            try {
                Method getViolations = event.getClass().getMethod("getViolations");
                Object vl = getViolations.invoke(event);
                if (vl instanceof Number) {
                    violationsDouble = ((Number) vl).doubleValue();
                }
            } catch (NoSuchMethodException ignored) {}

            // get the check object which has the name and violation count
            try {
                Method getCheck = event.getClass().getMethod("getCheck");
                Object check = getCheck.invoke(event);

                if (check != null) {
                    // grab the check name
                    try {
                        Method getCheckName = check.getClass().getMethod("getCheckName");
                        Object name = getCheckName.invoke(check);
                        if (name instanceof String) {
                            checkName = (String) name;
                        }
                    } catch (NoSuchMethodException e) {
                        // try getAlternativeName
                        try {
                            Method getAltName = check.getClass().getMethod("getAlternativeName");
                            Object name = getAltName.invoke(check);
                            if (name instanceof String) {
                                checkName = (String) name;
                            }
                        } catch (NoSuchMethodException e2) {
                            // use class name if no name methods exist
                            checkName = check.getClass().getSimpleName();
                        }
                    }

                    // keep track of new check types for the web panel
                    if (!discoveredChecks.contains(checkName)) {
                        discoveredChecks.add(checkName);
                    }

                    // Get violations from check if not already set
                    if (violationsDouble == 0) {
                        try {
                            Method getViolations = check.getClass().getMethod("getViolations");
                            Object vl = getViolations.invoke(check);
                            if (vl instanceof Number) {
                                violationsDouble = ((Number) vl).doubleValue();
                            }
                        } catch (NoSuchMethodException ignored) {}
                    }
                }
            } catch (NoSuchMethodException ignored) {}

            // get detailed info about the flag
            try {
                Method getVerbose = event.getClass().getMethod("getVerbose");
                Object result = getVerbose.invoke(event);
                if (result instanceof String) {
                    verbose = (String) result;
                }
            } catch (NoSuchMethodException ignored) {}

            // Convert violations to int - use ceiling to show at least 1 if there's any violation
            int violations = (int) Math.ceil(violationsDouble);
            if (violations < 1) violations = 1;

            // Do NOT cancel the event - we want Grim to still do setbacks
            // To hide Grim's messages, use disableGrimAlerts() or set alert-interval: -1 in punishments.yml

            if (player != null) {
                handleAlert(new AnticheatAlert(
                    getName(),
                    player,
                    checkName,
                    "A",
                    violations,
                    violations,
                    verbose.isEmpty() ? "Grim detection" : verbose
                ));
            }
        } catch (Exception e) {
            plugin.logDebug("[Grim] Error handling Bukkit event: " + e.getMessage());
        }
    }

    // returns all check types we've seen so far plus known ones
    public Set<String> getDiscoveredChecks() {
        return discoveredChecks;
    }

    // returns the list of known Grim checks
    public static String[] getKnownChecks() {
        return KNOWN_CHECKS.clone();
    }

    /**
     * Attempts to disable Grim's native alert messages by modifying punishments.yml
     * Sets alert-interval to -1 for all checks, which disables alerts but keeps setbacks
     * @return true if successful, false if failed or not needed
     */
    public boolean disableGrimAlerts() {
        if (grimPlugin == null) {
            return false;
        }

        try {
            File grimDataFolder = grimPlugin.getDataFolder();
            File punishmentsFile = new File(grimDataFolder, "punishments.yml");

            if (!punishmentsFile.exists()) {
                plugin.getLogger().warning("[Grim] punishments.yml not found - cannot auto-disable alerts");
                return false;
            }

            YamlConfiguration punishments = YamlConfiguration.loadConfiguration(punishmentsFile);
            boolean modified = false;

            // Get all check sections and set alert-interval to -1
            if (punishments.isConfigurationSection("Checks")) {
                for (String checkName : punishments.getConfigurationSection("Checks").getKeys(false)) {
                    String alertIntervalPath = "Checks." + checkName + ".alert-interval";
                    int currentInterval = punishments.getInt(alertIntervalPath, 1);

                    if (currentInterval != -1) {
                        punishments.set(alertIntervalPath, -1);
                        modified = true;
                    }
                }
            }

            if (modified) {
                punishments.save(punishmentsFile);
                plugin.getLogger().info("[Grim] Auto-disabled Grim alert messages (set alert-interval: -1)");
                plugin.getLogger().info("[Grim] ModereX will handle all anticheat alerts. Setbacks still work.");

                // Try to reload Grim's config
                tryReloadGrimConfig();
                return true;
            } else {
                plugin.logDebug("[Grim] Grim alerts already disabled");
                return true;
            }

        } catch (Exception e) {
            plugin.getLogger().warning("[Grim] Failed to auto-disable Grim alerts: " + e.getMessage());
            plugin.getLogger().info("[Grim] To manually disable, set 'alert-interval: -1' for all checks in Grim's punishments.yml");
            return false;
        }
    }

    /**
     * Try to reload Grim's configuration so the alert-interval changes take effect
     */
    private void tryReloadGrimConfig() {
        try {
            if (grimApi != null) {
                Method reloadMethod = grimApi.getClass().getMethod("reload");
                reloadMethod.invoke(grimApi);
                plugin.logDebug("[Grim] Reloaded Grim config");
            }
        } catch (Exception e) {
            plugin.logDebug("[Grim] Could not reload Grim config: " + e.getMessage());
            plugin.getLogger().info("[Grim] Please run '/grim reload' to apply alert-interval changes");
        }
    }
}
