package com.blockforge.moderex.util;

import org.bukkit.Bukkit;

public final class VersionUtil {

    private static String serverVersion;
    private static int majorVersion;
    private static int minorVersion;
    private static int patchVersion;

    static {
        parseVersion();
    }

    private VersionUtil() {
    }

    private static void parseVersion() {
        // Get version string like "1.21.1-R0.1-SNAPSHOT"
        String bukkitVersion = Bukkit.getBukkitVersion();
        serverVersion = bukkitVersion.split("-")[0];

        String[] parts = serverVersion.split("\\.");
        try {
            majorVersion = parts.length > 0 ? Integer.parseInt(parts[0]) : 1;
            minorVersion = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            patchVersion = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        } catch (NumberFormatException e) {
            majorVersion = 1;
            minorVersion = 21;
            patchVersion = 0;
        }
    }

    public static String getServerVersion() {
        return serverVersion;
    }

    public static int getMajorVersion() {
        return majorVersion;
    }

    public static int getMinorVersion() {
        return minorVersion;
    }

    public static int getPatchVersion() {
        return patchVersion;
    }

    public static boolean isAtLeast(String version) {
        String[] parts = version.split("\\.");
        int targetMajor = parts.length > 0 ? Integer.parseInt(parts[0]) : 1;
        int targetMinor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int targetPatch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        return isAtLeast(targetMajor, targetMinor, targetPatch);
    }

    public static boolean isAtLeast(int major, int minor, int patch) {
        if (majorVersion > major) return true;
        if (majorVersion < major) return false;

        if (minorVersion > minor) return true;
        if (minorVersion < minor) return false;

        return patchVersion >= patch;
    }

    public static boolean supportsDialogs() {
        return isAtLeast(1, 21, 6);
    }

    public static boolean isPaper() {
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.destroystokyo.paper.PaperConfig");
                return true;
            } catch (ClassNotFoundException e2) {
                return false;
            }
        }
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String getFormattedVersion() {
        String serverType = "Unknown";
        if (isFolia()) {
            serverType = "Folia";
        } else if (isPaper()) {
            serverType = "Paper";
        } else {
            // Check for Spigot
            try {
                Class.forName("org.spigotmc.SpigotConfig");
                serverType = "Spigot";
            } catch (ClassNotFoundException e) {
                serverType = "CraftBukkit";
            }
        }
        return serverType + " " + serverVersion;
    }
}
