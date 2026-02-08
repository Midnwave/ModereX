package com.blockforge.moderex.api.anticheat;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks;
import com.blockforge.moderex.hooks.anticheat.AnticheatHook;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Central registry for external anticheat providers.
 *
 * <p>This registry allows third-party anticheat plugins to integrate
 * with ModereX by registering their providers and checks.
 *
 * <p><b>Usage from External Plugins:</b>
 * <pre>{@code
 * // In your plugin's onEnable:
 * Plugin moderex = getServer().getPluginManager().getPlugin("ModereX");
 * if (moderex != null && moderex.isEnabled()) {
 *     ModereXAPI api = ModereXAPI.getInstance();
 *     api.registerAnticheat(new MyAnticheatProvider());
 * }
 *
 * // In your plugin's onDisable:
 * ModereXAPI api = ModereXAPI.getInstance();
 * api.unregisterAnticheat("MyAnticheat");
 * }</pre>
 *
 * @see ExternalAnticheatProvider
 */
public class AnticheatRegistry {

    private final ModereX plugin;
    private final Map<String, ExternalAnticheatProvider> providers;

    /**
     * Create a new anticheat registry.
     *
     * @param plugin the ModereX plugin instance
     */
    public AnticheatRegistry(ModereX plugin) {
        this.plugin = plugin;
        this.providers = new ConcurrentHashMap<>();
    }

    /**
     * Register an external anticheat provider.
     *
     * <p>This will:
     * <ul>
     *   <li>Initialize the provider with an alert handler</li>
     *   <li>Register all checks from the provider</li>
     *   <li>Create default rules for each check in the database</li>
     * </ul>
     *
     * @param provider the provider to register
     * @throws IllegalArgumentException if a provider with the same name is already registered
     */
    public void register(ExternalAnticheatProvider provider) {
        String name = provider.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Provider name cannot be null or blank");
        }

        if (providers.containsKey(name.toLowerCase())) {
            throw new IllegalArgumentException("Provider '" + name + "' is already registered");
        }

        plugin.getLogger().info("[AnticheatRegistry] Registering external provider: " + name + " v" + provider.getVersion());

        // Initialize provider with alert handler
        provider.initialize(violation -> handleViolation(name, violation));

        // Register all checks with the check registry
        Collection<AnticheatCheck> checks = provider.getChecks();
        if (checks != null && !checks.isEmpty()) {
            for (AnticheatCheck check : checks) {
                AnticheatChecks.registerExternalCheck(name, new AnticheatChecks.CheckInfo(
                        check.getName(),
                        check.getDisplayName(),
                        mapCategory(check.getCategory()),
                        check.getDescription(),
                        check.getAliases().toArray(new String[0])
                ));
            }
            plugin.getLogger().info("[AnticheatRegistry] Registered " + checks.size() + " checks from " + name);
        }

        // Pre-register checks with alert manager (creates DB rules)
        if (plugin.getAnticheatManager() != null && plugin.getAnticheatManager().getAlertManager() != null) {
            plugin.getAnticheatManager().getAlertManager().preRegisterChecks(name);
        }

        providers.put(name.toLowerCase(), provider);
        plugin.getLogger().info("[AnticheatRegistry] External anticheat '" + name + "' registered successfully");
    }

    /**
     * Unregister an external anticheat provider.
     *
     * @param name the name of the provider to unregister
     * @return true if the provider was unregistered, false if not found
     */
    public boolean unregister(String name) {
        if (name == null) return false;

        ExternalAnticheatProvider provider = providers.remove(name.toLowerCase());
        if (provider != null) {
            try {
                provider.shutdown();
                AnticheatChecks.unregisterExternalChecks(name);
                plugin.getLogger().info("[AnticheatRegistry] Unregistered external anticheat: " + name);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "[AnticheatRegistry] Error shutting down provider: " + name, e);
            }
            return true;
        }
        return false;
    }

    /**
     * Get a registered provider by name.
     *
     * @param name the provider name
     * @return the provider, or null if not found
     */
    public ExternalAnticheatProvider getProvider(String name) {
        if (name == null) return null;
        return providers.get(name.toLowerCase());
    }

    /**
     * Get all registered providers.
     *
     * @return unmodifiable collection of providers
     */
    public Collection<ExternalAnticheatProvider> getProviders() {
        return Collections.unmodifiableCollection(providers.values());
    }

    /**
     * Check if a provider is registered.
     *
     * @param name the provider name
     * @return true if registered
     */
    public boolean isRegistered(String name) {
        if (name == null) return false;
        return providers.containsKey(name.toLowerCase());
    }

    /**
     * Shutdown all providers and clear the registry.
     * Called when ModereX disables.
     */
    public void shutdown() {
        for (Map.Entry<String, ExternalAnticheatProvider> entry : providers.entrySet()) {
            try {
                entry.getValue().shutdown();
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "[AnticheatRegistry] Error shutting down provider: " + entry.getKey(), e);
            }
        }
        providers.clear();
    }

    /**
     * Handle a violation from an external provider.
     */
    private void handleViolation(String anticheatName, AnticheatViolation violation) {
        if (plugin.getAnticheatManager() == null) {
            return;
        }

        // Convert to internal AnticheatAlert format and send through the manager
        AnticheatHook.AnticheatAlert alert = new AnticheatHook.AnticheatAlert(
                anticheatName,
                plugin.getServer().getPlayer(violation.getPlayerUuid()),
                violation.getCheckName(),
                null, // checkType
                violation.getTotalViolations(),
                violation.getViolationLevel(),
                violation.getDetails()
        );

        // Send through the anticheat manager pipeline
        plugin.getAnticheatManager().handleAlert(alert);
    }

    /**
     * Map API category to internal category.
     */
    private AnticheatChecks.Category mapCategory(AnticheatCheck.Category category) {
        return switch (category) {
            case COMBAT -> AnticheatChecks.Category.COMBAT;
            case MOVEMENT -> AnticheatChecks.Category.MOVEMENT;
            case PLAYER -> AnticheatChecks.Category.PLAYER;
            case WORLD -> AnticheatChecks.Category.WORLD;
            case MISC -> AnticheatChecks.Category.MISC;
        };
    }
}
