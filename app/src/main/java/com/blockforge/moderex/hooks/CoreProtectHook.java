package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoreProtectHook {

    private final ModereX plugin;
    private CoreProtectAPI api;
    private boolean enabled = false;

    public CoreProtectHook(ModereX plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        try {
            CoreProtect coreProtect = CoreProtect.getInstance();
            if (coreProtect == null) {
                plugin.logDebug("CoreProtect not found");
                return false;
            }

            api = coreProtect.getAPI();
            if (api == null || !api.isEnabled()) {
                plugin.logDebug("CoreProtect API not enabled");
                return false;
            }

            // Check API version (minimum 9 for modern features)
            if (api.APIVersion() < 9) {
                plugin.getLogger().warning("CoreProtect API version " + api.APIVersion() +
                        " is outdated. Some features may not work. Version 9+ recommended.");
            }

            enabled = true;
            plugin.getLogger().info("Hooked into CoreProtect v" + api.APIVersion());
            return true;
        } catch (Exception e) {
            plugin.logDebug("Failed to hook CoreProtect: " + e.getMessage());
            return false;
        }
    }

    public boolean isEnabled() {
        return enabled && api != null && api.isEnabled();
    }

    public CoreProtectAPI getAPI() {
        return api;
    }

    // ========== Block Lookup Methods ==========

    public List<String[]> blockLookup(Block block, int time) {
        if (!isEnabled()) return null;
        try {
            return api.blockLookup(block, time);
        } catch (Exception e) {
            plugin.logDebug("CoreProtect blockLookup failed: " + e.getMessage());
            return null;
        }
    }

    public CompletableFuture<List<String[]>> performLookup(int time, List<String> users,
                                                           List<String> exclude, int restrictActionType,
                                                           Location radiusFrom, int radiusBlocks) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isEnabled()) return null;
            try {
                // CoreProtect API: performLookup(time, users, exclude, restrict, excludeBlocks, actionTypes, radius, location)
                List<Integer> actionTypes = restrictActionType > 0 ? List.of(restrictActionType) : null;
                return api.performLookup(time, users, exclude, null, null, actionTypes, radiusBlocks, radiusFrom);
            } catch (Exception e) {
                plugin.logDebug("CoreProtect performLookup failed: " + e.getMessage());
                return null;
            }
        });
    }

    public List<String[]> sessionLookup(String user, int time) {
        if (!isEnabled()) return null;
        try {
            return api.sessionLookup(user, time);
        } catch (Exception e) {
            plugin.logDebug("CoreProtect sessionLookup failed: " + e.getMessage());
            return null;
        }
    }

    // ========== Lookup by Player UUID ==========

    public CompletableFuture<List<String[]>> lookupPlayer(UUID uuid, int time) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isEnabled()) return null;
            try {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
                String name = player.getName();
                if (name == null) return null;

                // CoreProtect API: performLookup(time, users, exclude, restrict, excludeBlocks, actionTypes, radius, location)
                return api.performLookup(time, List.of(name), null, null, null, null, 0, null);
            } catch (Exception e) {
                plugin.logDebug("CoreProtect player lookup failed: " + e.getMessage());
                return null;
            }
        });
    }

    public CompletableFuture<List<String[]>> lookupPlayerPlacements(UUID uuid, int time) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isEnabled()) return null;
            try {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
                String name = player.getName();
                if (name == null) return null;

                // Action type 1 = block place
                // CoreProtect API: performLookup(time, users, exclude, restrict, excludeBlocks, actionTypes, radius, location)
                return api.performLookup(time, List.of(name), null, null, null, List.of(1), 0, null);
            } catch (Exception e) {
                plugin.logDebug("CoreProtect placement lookup failed: " + e.getMessage());
                return null;
            }
        });
    }

    public CompletableFuture<List<String[]>> lookupPlayerBreaks(UUID uuid, int time) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isEnabled()) return null;
            try {
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
                String name = player.getName();
                if (name == null) return null;

                // Action type 0 = block break
                // CoreProtect API: performLookup(time, users, exclude, restrict, excludeBlocks, actionTypes, radius, location)
                return api.performLookup(time, List.of(name), null, null, null, List.of(0), 0, null);
            } catch (Exception e) {
                plugin.logDebug("CoreProtect break lookup failed: " + e.getMessage());
                return null;
            }
        });
    }

    // ========== Logging Methods ==========

    public boolean logPlacement(String user, Location location) {
        if (!isEnabled()) return false;
        try {
            return api.logPlacement(user, location, location.getBlock().getType(), location.getBlock().getBlockData());
        } catch (Exception e) {
            plugin.logDebug("CoreProtect logPlacement failed: " + e.getMessage());
            return false;
        }
    }

    public boolean logRemoval(String user, Location location) {
        if (!isEnabled()) return false;
        try {
            return api.logRemoval(user, location, location.getBlock().getType(), location.getBlock().getBlockData());
        } catch (Exception e) {
            plugin.logDebug("CoreProtect logRemoval failed: " + e.getMessage());
            return false;
        }
    }

    // ========== Rollback Methods ==========

    public List<String[]> performRollback(int time, List<String> users, Location radiusFrom, int radius) {
        if (!isEnabled()) return null;
        try {
            return api.performRollback(time, users, null, null, null, null, radius, radiusFrom);
        } catch (Exception e) {
            plugin.logDebug("CoreProtect rollback failed: " + e.getMessage());
            return null;
        }
    }

    public List<String[]> performRestore(int time, List<String> users, Location radiusFrom, int radius) {
        if (!isEnabled()) return null;
        try {
            return api.performRestore(time, users, null, null, null, null, radius, radiusFrom);
        } catch (Exception e) {
            plugin.logDebug("CoreProtect restore failed: " + e.getMessage());
            return null;
        }
    }

    // ========== Utility Methods ==========

    public String parseResult(String[] result) {
        if (result == null || result.length < 8) return "Invalid result";

        /*
         * CoreProtect result format:
         * [0] = time (Unix timestamp)
         * [1] = user
         * [2] = x
         * [3] = y
         * [4] = z
         * [5] = type (block type or action)
         * [6] = data
         * [7] = action (0=removed, 1=placed, 2=interaction)
         * [8] = rolled_back (0/1)
         */

        try {
            long time = Long.parseLong(result[0]);
            String user = result[1];
            int x = Integer.parseInt(result[2]);
            int y = Integer.parseInt(result[3]);
            int z = Integer.parseInt(result[4]);
            String type = result[5];
            int action = Integer.parseInt(result[7]);

            String actionStr = switch (action) {
                case 0 -> "broke";
                case 1 -> "placed";
                case 2 -> "interacted with";
                default -> "modified";
            };

            String timeAgo = formatTimeAgo(time * 1000);

            return String.format("%s %s %s at %d,%d,%d (%s)",
                    user, actionStr, type, x, y, z, timeAgo);
        } catch (Exception e) {
            return "Parse error: " + e.getMessage();
        }
    }

    public CompletableFuture<Integer> getActionCount(UUID uuid, int time) {
        return lookupPlayer(uuid, time).thenApply(results -> {
            if (results == null) return 0;
            return results.size();
        });
    }

    public CompletableFuture<Boolean> hasSuspiciousActivity(UUID uuid, int time, int threshold) {
        return lookupPlayerBreaks(uuid, time).thenApply(results -> {
            if (results == null) return false;
            return results.size() > threshold;
        });
    }

    private String formatTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d ago";
        if (hours > 0) return hours + "h ago";
        if (minutes > 0) return minutes + "m ago";
        return seconds + "s ago";
    }
}
