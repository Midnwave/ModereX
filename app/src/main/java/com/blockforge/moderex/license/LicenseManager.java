package com.blockforge.moderex.license;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Manages license lifecycle and validation scheduling for dev builds.
 *
 * Unlicensed builds (production): Plugin runs normally without license checks.
 * Licensed builds (dev): Plugin validates token on startup and periodically re-validates.
 */
public class LicenseManager {
    private static final long REVALIDATION_INTERVAL_TICKS = 36000L; // 30 minutes (20 ticks/sec * 60 * 30)
    private static final long HEARTBEAT_INTERVAL_TICKS = 36000L;    // 30 minutes

    private final ModereX plugin;
    private final Logger logger;
    private final LicenseValidator validator;

    private String licenseToken;
    private boolean isLicensed;
    private boolean isValid;
    private Long expiresAt;
    private long buildTimestamp;

    private BukkitTask revalidationTask;
    private BukkitTask heartbeatTask;

    public LicenseManager(ModereX plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.validator = new LicenseValidator(logger);
        this.isLicensed = false;
        this.isValid = false;
    }

    /**
     * Initialize license system - called in ModereX.onEnable()
     * Returns a CompletableFuture that completes when validation finishes (or immediately if unlicensed)
     */
    public CompletableFuture<Boolean> init() {
        // Load license token from embedded properties
        loadLicenseToken();

        if (!isLicensed) {
            logger.info("[License] Running as unlicensed build (production mode)");
            return CompletableFuture.completedFuture(true);
        }

        logger.info("[License] Running as licensed dev build - validating...");

        // Validate asynchronously
        return validateLicenseAsync().thenApply(result -> {
            if (result) {
                // Start periodic revalidation and heartbeat
                schedulePeriodicValidation();
                schedulePeriodicHeartbeat();
                return true;
            } else {
                // Validation failed - disable plugin
                disablePlugin();
                return false;
            }
        });
    }

    /**
     * Load license token from embedded resource file
     */
    private void loadLicenseToken() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("license-token.properties");

            if (inputStream == null) {
                // No license token file = unlicensed build
                this.isLicensed = false;
                return;
            }

            Properties props = new Properties();
            props.load(inputStream);

            String token = props.getProperty("token", "").trim();

            if (token.isEmpty() || token.equals("UNLICENSED")) {
                this.isLicensed = false;
                return;
            }

            this.licenseToken = token;
            this.buildTimestamp = Long.parseLong(props.getProperty("buildTimestamp", "0"));
            this.isLicensed = true;

            logger.info("[License] License token loaded: " + maskToken(token));
        } catch (Exception e) {
            logger.warning("[License] Failed to load license token: " + e.getMessage());
            this.isLicensed = false;
        }
    }

    /**
     * Validate license asynchronously
     */
    public CompletableFuture<Boolean> validateLicenseAsync() {
        if (!isLicensed) {
            return CompletableFuture.completedFuture(true);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String serverId = plugin.getServerIdentity().getServerId();
                String serverName = Bukkit.getServer().getName();
                String version = plugin.getPluginMeta().getVersion();

                LicenseValidator.ValidationResult result = validator.validate(
                    licenseToken,
                    serverId,
                    serverName,
                    version
                );

                if (result.isValid()) {
                    this.isValid = true;
                    this.expiresAt = result.getExpiresAt();

                    if (expiresAt != null) {
                        long daysUntilExpiry = (expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
                        logger.info("[License] License valid - expires in " + daysUntilExpiry + " days");
                    } else {
                        logger.info("[License] License valid - no expiration");
                    }

                    return true;
                } else {
                    this.isValid = false;
                    logger.severe("[License] License validation failed: " + result.getError());
                    logger.severe("[License] This dev build will not function without a valid license.");
                    logger.severe("[License] Contact the developer for assistance.");
                    return false;
                }
            } catch (Exception e) {
                logger.severe("[License] Unexpected error during validation: " + e.getMessage());
                e.printStackTrace();
                this.isValid = false;
                return false;
            }
        });
    }

    /**
     * Schedule periodic license revalidation (every 30 minutes)
     */
    private void schedulePeriodicValidation() {
        revalidationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            logger.info("[License] Performing periodic license revalidation...");

            validateLicenseAsync().thenAccept(valid -> {
                if (!valid) {
                    logger.severe("[License] Periodic validation failed - license may have been revoked");
                    Bukkit.getScheduler().runTask(plugin, this::disablePlugin);
                }
            });
        }, REVALIDATION_INTERVAL_TICKS, REVALIDATION_INTERVAL_TICKS);
    }

    /**
     * Schedule periodic heartbeat (every 30 minutes)
     */
    private void schedulePeriodicHeartbeat() {
        heartbeatTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                String serverId = plugin.getServerIdentity().getServerId();
                int playerCount = Bukkit.getOnlinePlayers().size();
                long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

                boolean success = validator.sendHeartbeat(licenseToken, serverId, playerCount, uptime);

                if (!success) {
                    logger.warning("[License] Heartbeat failed - may indicate revoked license");
                }
            } catch (Exception e) {
                logger.warning("[License] Heartbeat error: " + e.getMessage());
            }
        }, HEARTBEAT_INTERVAL_TICKS, HEARTBEAT_INTERVAL_TICKS);
    }

    /**
     * Disable plugin gracefully due to license failure
     */
    private void disablePlugin() {
        logger.severe("╔═══════════════════════════════════════════════════════════╗");
        logger.severe("║                                                           ║");
        logger.severe("║              LICENSE VALIDATION FAILED                    ║");
        logger.severe("║                                                           ║");
        logger.severe("║  This is a licensed development build of ModereX.        ║");
        logger.severe("║  The license token embedded in this build is invalid,    ║");
        logger.severe("║  revoked, or expired.                                    ║");
        logger.severe("║                                                           ║");
        logger.severe("║  The plugin will now disable itself.                     ║");
        logger.severe("║                                                           ║");
        logger.severe("║  If you believe this is an error, contact the developer. ║");
        logger.severe("║                                                           ║");
        logger.severe("╚═══════════════════════════════════════════════════════════╝");

        // Cancel tasks
        if (revalidationTask != null) {
            revalidationTask.cancel();
        }
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
        }

        // Disable plugin
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.getPluginManager().disablePlugin(plugin);
        });
    }

    /**
     * Shutdown - cancel scheduled tasks
     */
    public void shutdown() {
        if (revalidationTask != null) {
            revalidationTask.cancel();
        }
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
        }
    }

    /**
     * Check if this is a licensed build
     */
    public boolean isLicensed() {
        return isLicensed;
    }

    /**
     * Check if license is currently valid
     */
    public boolean isValid() {
        return !isLicensed || isValid;
    }

    /**
     * Get license info for display
     */
    public String getLicenseInfo() {
        if (!isLicensed) {
            return "Unlicensed (Production Build)";
        }

        if (!isValid) {
            return "Licensed (INVALID)";
        }

        if (expiresAt != null) {
            long daysUntilExpiry = (expiresAt - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            return "Licensed (Expires in " + daysUntilExpiry + " days)";
        }

        return "Licensed (No Expiration)";
    }

    /**
     * Mask license token for logging (show first 8 chars)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 8) + "..." + token.substring(token.length() - 4);
    }
}
