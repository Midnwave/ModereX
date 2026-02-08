package com.blockforge.moderex.api.anticheat;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * API interface for external anticheat plugins to integrate with ModereX.
 *
 * <p>Implementing this interface allows your anticheat plugin to:
 * <ul>
 *   <li>Register custom checks that appear in ModereX GUIs and web panel</li>
 *   <li>Send alerts that respect staff notification preferences</li>
 *   <li>Integrate with automod rules for automatic punishments</li>
 *   <li>Have violations tracked with ModereX's threshold system</li>
 * </ul>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * public class MyAnticheatProvider implements ExternalAnticheatProvider {
 *     @Override
 *     public String getName() {
 *         return "MyAnticheat";
 *     }
 *
 *     @Override
 *     public String getVersion() {
 *         return "1.0.0";
 *     }
 *
 *     @Override
 *     public Collection<AnticheatCheck> getChecks() {
 *         return List.of(
 *             new AnticheatCheck("speed", "Speed", Category.MOVEMENT, "Detects speed hacks"),
 *             new AnticheatCheck("reach", "Reach", Category.COMBAT, "Detects extended reach")
 *         );
 *     }
 *
 *     @Override
 *     public void initialize(Consumer<AnticheatViolation> alertHandler) {
 *         this.alertHandler = alertHandler;
 *         // Set up your detection listeners here
 *     }
 *
 *     // When a violation is detected in your plugin:
 *     public void onPlayerFlag(Player player, String check, int vl, String details) {
 *         alertHandler.accept(new AnticheatViolation.Builder()
 *             .player(player)
 *             .checkName(check)
 *             .violationLevel(vl)
 *             .details(details)
 *             .build());
 *     }
 * }
 *
 * // Register with ModereX:
 * ModereXAPI api = ModereXAPI.getInstance();
 * api.registerAnticheat(new MyAnticheatProvider());
 * }</pre>
 *
 * @see AnticheatCheck
 * @see AnticheatViolation
 */
public interface ExternalAnticheatProvider {

    /**
     * Get the name of this anticheat.
     * This will be displayed in GUIs and logs.
     *
     * @return the anticheat name (e.g., "MyAnticheat")
     */
    String getName();

    /**
     * Get the version of this anticheat.
     *
     * @return the version string (e.g., "1.0.0")
     */
    String getVersion();

    /**
     * Get all checks provided by this anticheat.
     * These will be registered with ModereX for configuration.
     *
     * @return collection of available checks
     */
    Collection<AnticheatCheck> getChecks();

    /**
     * Initialize this provider with the alert handler.
     * Called when ModereX registers this provider.
     *
     * <p>Store the alertHandler and call it when your anticheat detects a violation.
     *
     * @param alertHandler consumer to receive violation alerts
     */
    void initialize(Consumer<AnticheatViolation> alertHandler);

    /**
     * Shutdown this provider and clean up resources.
     * Called when ModereX disables or when unregistering.
     */
    default void shutdown() {
        // Default implementation does nothing
    }

    /**
     * Whether this provider is currently active and sending alerts.
     *
     * @return true if active, false if disabled or not initialized
     */
    default boolean isActive() {
        return true;
    }

    /**
     * Get custom configuration options for this anticheat.
     * These can be displayed in the ModereX GUI for per-anticheat settings.
     *
     * @return collection of configuration options, or empty if none
     */
    default Collection<ConfigOption> getConfigOptions() {
        return java.util.Collections.emptyList();
    }

    /**
     * Represents a configuration option for the anticheat.
     */
    record ConfigOption(String key, String displayName, String description, Object defaultValue, Class<?> type) {
    }
}
