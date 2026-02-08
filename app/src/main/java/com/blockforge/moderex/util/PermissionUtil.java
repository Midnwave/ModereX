package com.blockforge.moderex.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;
import java.util.UUID;

/**
 * Utility class for permission checking with OP bypass support.
 * OPs bypass all permission checks EXCEPT for specific protected permissions.
 */
public class PermissionUtil {

    /**
     * Permissions that OPs do NOT automatically have.
     * These require explicit permission grants even for operators.
     */
    private static final Set<String> PROTECTED_PERMISSIONS = Set.of(
        "moderex.webpanel"
    );

    /**
     * Check if a permissible (player/sender) has a permission.
     * OPs automatically have all permissions EXCEPT protected ones.
     * Also checks wildcard permissions (moderex.*, moderex.command.*, etc.)
     *
     * @param permissible The player or command sender
     * @param permission The permission to check
     * @return true if they have the permission
     */
    public static boolean hasPermission(Permissible permissible, String permission) {
        if (permissible == null || permission == null) {
            return false;
        }

        // Check if this is a protected permission that OPs don't get automatically
        if (isProtectedPermission(permission)) {
            return permissible.hasPermission(permission);
        }

        // For non-protected permissions, OPs bypass
        if (permissible.isOp()) {
            return true;
        }

        // Check exact permission first
        if (permissible.hasPermission(permission)) {
            return true;
        }

        // Check wildcard permissions
        // e.g., for "moderex.command.ban", check "moderex.*" and "moderex.command.*"
        return hasWildcardPermission(permissible, permission);
    }

    /**
     * Check if a permissible has a wildcard permission that grants the requested permission.
     * e.g., moderex.* grants moderex.command.ban
     * e.g., moderex.command.* grants moderex.command.ban
     */
    private static boolean hasWildcardPermission(Permissible permissible, String permission) {
        if (!permission.startsWith("moderex.")) {
            return false;
        }

        // Check moderex.* (grants all moderex permissions except protected)
        if (permissible.hasPermission("moderex.*")) {
            return !isProtectedPermission(permission);
        }

        // Build wildcard paths from the permission
        // e.g., "moderex.command.ban" -> check "moderex.command.*"
        // e.g., "moderex.history.watchlist.bans" -> check "moderex.history.*", "moderex.history.watchlist.*"
        String[] parts = permission.split("\\.");
        StringBuilder wildcardPath = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) wildcardPath.append(".");
            wildcardPath.append(parts[i]);

            String wildcard = wildcardPath + ".*";
            if (permissible.hasPermission(wildcard)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a permission is protected (OPs don't auto-get it).
     */
    public static boolean isProtectedPermission(String permission) {
        if (permission == null) return false;

        // Check exact match
        if (PROTECTED_PERMISSIONS.contains(permission.toLowerCase())) {
            return true;
        }

        // Check if any protected permission is a prefix
        String lowerPerm = permission.toLowerCase();
        for (String protected_ : PROTECTED_PERMISSIONS) {
            if (lowerPerm.startsWith(protected_ + ".")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a player has any of the given permissions.
     * OPs bypass for non-protected permissions.
     */
    public static boolean hasAnyPermission(Permissible permissible, String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permissible, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a player has all of the given permissions.
     * OPs bypass for non-protected permissions.
     */
    public static boolean hasAllPermissions(Permissible permissible, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permissible, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the permissible is a staff member (has moderex.staff permission).
     * OPs are automatically considered staff.
     */
    public static boolean isStaff(Permissible permissible) {
        return hasPermission(permissible, "moderex.staff");
    }

    /**
     * Check if the permissible is an admin (has moderex.admin permission).
     * OPs are automatically considered admins.
     */
    public static boolean isAdmin(Permissible permissible) {
        return hasPermission(permissible, "moderex.admin");
    }

    // --- Staff Weight System ---

    /**
     * Get the staff weight for a permissible.
     * Scans for moderex.weight.<number> permissions and returns the highest value.
     * Higher weight = higher rank. Default is 0 (no weight set).
     *
     * @param permissible The player or command sender
     * @return The highest weight value found, or 0 if none
     */
    public static int getWeight(Permissible permissible) {
        if (permissible == null) return 0;

        int maxWeight = 0;

        for (PermissionAttachmentInfo info : permissible.getEffectivePermissions()) {
            if (!info.getValue()) continue; // Skip negated permissions
            String perm = info.getPermission();
            if (perm.startsWith("moderex.weight.")) {
                try {
                    int weight = Integer.parseInt(perm.substring("moderex.weight.".length()));
                    if (weight > maxWeight) {
                        maxWeight = weight;
                    }
                } catch (NumberFormatException ignored) {
                    // Not a valid weight number, skip
                }
            }
        }

        return maxWeight;
    }

    /**
     * Get the staff weight for an offline player by UUID.
     * Falls back to checking online player, then returns 0.
     */
    public static int getWeight(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return getWeight(player);
        }
        return 0;
    }

    /**
     * Check if an executor can perform staff actions on a target based on weight.
     * The executor must have equal or higher weight than the target.
     * If neither has a weight set (both 0), actions are allowed.
     * If only the target has weight, the executor is blocked.
     *
     * @param executor The staff member performing the action
     * @param target The target of the action
     * @return true if the executor can act on the target
     */
    public static boolean canActOn(Permissible executor, Permissible target) {
        if (executor == null || target == null) return true;

        int executorWeight = getWeight(executor);
        int targetWeight = getWeight(target);

        // If target has no weight, anyone can act on them
        if (targetWeight == 0) return true;

        // Executor must have equal or higher weight
        return executorWeight >= targetWeight;
    }

    /**
     * Check if an executor can perform staff actions on a target by UUID.
     */
    public static boolean canActOn(Permissible executor, UUID targetUuid) {
        Player target = Bukkit.getPlayer(targetUuid);
        if (target != null) {
            return canActOn(executor, target);
        }
        // Offline player - can't check weight effectively without LuckPerms
        // Allow action on offline players by default
        return true;
    }
}
