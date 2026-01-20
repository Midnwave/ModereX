package com.blockforge.moderex.disguise.packet;

import org.bukkit.Bukkit;

/**
 * Handles protocol version detection for multi-version support.
 * Supports Minecraft 1.19 through 1.21.11+
 */
public class ProtocolVersion {

    private static Integer cachedVersion = null;
    private static String cachedVersionString = null;

    /**
     * Get the server's Minecraft version as a comparable integer.
     * Format: 1.19 = 119, 1.20.1 = 1201, 1.21.11 = 12111
     *
     * @return The version integer
     */
    public static int getVersion() {
        if (cachedVersion != null) {
            return cachedVersion;
        }

        String version = getVersionString();

        try {
            // Extract version numbers from "1.20.1" format
            String[] parts = version.split("\\.");

            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                int patch = parts.length >= 3 ? Integer.parseInt(parts[2]) : 0;

                // Convert to comparable integer: 1.20.1 = 1201
                cachedVersion = (major * 100) + (minor * 10) + patch;
            } else {
                cachedVersion = 0;
            }
        } catch (NumberFormatException e) {
            cachedVersion = 0;
        }

        return cachedVersion;
    }

    /**
     * Get the raw version string.
     *
     * @return The version string (e.g., "1.20.1")
     */
    public static String getVersionString() {
        if (cachedVersionString != null) {
            return cachedVersionString;
        }

        String fullVersion = Bukkit.getVersion();

        // Extract version from "git-Paper-123 (MC: 1.20.1)" format
        if (fullVersion.contains("MC: ")) {
            int start = fullVersion.indexOf("MC: ") + 4;
            int end = fullVersion.indexOf(")", start);
            if (end > start) {
                cachedVersionString = fullVersion.substring(start, end);
            }
        }

        // Fallback: try Bukkit.getBukkitVersion() which returns "1.20.1-R0.1-SNAPSHOT"
        if (cachedVersionString == null) {
            String bukkitVersion = Bukkit.getBukkitVersion();
            if (bukkitVersion.contains("-")) {
                cachedVersionString = bukkitVersion.substring(0, bukkitVersion.indexOf("-"));
            } else {
                cachedVersionString = bukkitVersion;
            }
        }

        return cachedVersionString != null ? cachedVersionString : "1.21";
    }

    /**
     * Check if the server version is at least the specified version.
     *
     * @param major Major version (e.g., 1)
     * @param minor Minor version (e.g., 20)
     * @param patch Patch version (e.g., 1)
     * @return true if server version >= specified version
     */
    public static boolean isAtLeast(int major, int minor, int patch) {
        int targetVersion = (major * 100) + (minor * 10) + patch;
        return getVersion() >= targetVersion;
    }

    /**
     * Check if the server version is between two versions (inclusive).
     *
     * @param minMajor Min major version
     * @param minMinor Min minor version
     * @param maxMajor Max major version
     * @param maxMinor Max minor version
     * @return true if version is in range
     */
    public static boolean isBetween(int minMajor, int minMinor, int maxMajor, int maxMinor) {
        int version = getVersion();
        int minVersion = (minMajor * 100) + (minMinor * 10);
        int maxVersion = (maxMajor * 100) + (maxMinor * 10) + 99; // Include all patches
        return version >= minVersion && version <= maxVersion;
    }

    /**
     * Get the protocol-specific packet class name prefix.
     *
     * @return The packet class prefix (e.g., "net.minecraft.network.protocol.game")
     */
    public static String getPacketPrefix() {
        if (isAtLeast(1, 20, 5)) {
            // 1.20.5+ uses different package structure
            return "net.minecraft.network.protocol.game";
        } else if (isAtLeast(1, 19, 0)) {
            // 1.19-1.20.4
            return "net.minecraft.network.protocol.game";
        }

        // Fallback for older versions
        return "net.minecraft.server." + getNMSVersion() + ".Packet";
    }

    /**
     * Get the NMS version string (e.g., "v1_20_R1").
     *
     * @return The NMS version string
     */
    public static String getNMSVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    /**
     * Check if this version supports modern packet system.
     * Modern system: 1.19+ with mojang mappings
     *
     * @return true if modern packet system is supported
     */
    public static boolean isModernPacketSystem() {
        return isAtLeast(1, 19, 0);
    }

    /**
     * Get a description of the current version.
     *
     * @return Version description
     */
    public static String getDescription() {
        return String.format("Minecraft %s (Protocol Version: %d, Modern: %s)",
                getVersionString(),
                getVersion(),
                isModernPacketSystem() ? "Yes" : "No");
    }
}
