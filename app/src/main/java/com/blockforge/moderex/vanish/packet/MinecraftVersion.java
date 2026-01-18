package com.blockforge.moderex.vanish.packet;

import org.bukkit.Bukkit;

/**
 * Detects and manages Minecraft version for packet compatibility
 * Supports 1.17+ with unified "Clientbound" packet naming
 */
public class MinecraftVersion {

    private static MinecraftVersion instance;

    private final String nmsVersion;
    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;
    private final boolean isPaper;
    private final boolean isFolia;

    private MinecraftVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        String[] parts = bukkitVersion.split("-")[0].split("\\.");

        this.majorVersion = parts.length > 0 ? parseVersion(parts[0]) : 1;
        this.minorVersion = parts.length > 1 ? parseVersion(parts[1]) : 0;
        this.patchVersion = parts.length > 2 ? parseVersion(parts[2]) : 0;

        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        this.nmsVersion = packageName.substring(packageName.lastIndexOf('.') + 1);

        this.isPaper = detectPaper();
        this.isFolia = detectFolia();
    }

    private int parseVersion(String version) {
        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean detectPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("io.papermc.paper.configuration.Configuration");
                return true;
            } catch (ClassNotFoundException ex) {
                return false;
            }
        }
    }

    private boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static MinecraftVersion getInstance() {
        if (instance == null) {
            instance = new MinecraftVersion();
        }
        return instance;
    }

    /**
     * Check if version is 1.17 or higher (unified packet naming).
     */
    public boolean isModern() {
        return majorVersion == 1 && minorVersion >= 17;
    }

    /**
     * Check if version is 1.19.3 or higher (new damage packets).
     */
    public boolean hasDamagePackets() {
        if (majorVersion != 1) return majorVersion > 1;
        return minorVersion > 19 || (minorVersion == 19 && patchVersion >= 3);
    }

    /**
     * Check if version is 1.19 or higher (chat changes).
     */
    public boolean hasModernChat() {
        return majorVersion == 1 && minorVersion >= 19;
    }

    /**
     * Check if version is 1.20.5 or higher (component changes).
     */
    public boolean hasComponentChanges() {
        if (majorVersion != 1) return majorVersion > 1;
        return minorVersion > 20 || (minorVersion == 20 && patchVersion >= 5);
    }

    /**
     * Get version string for display.
     */
    public String getVersionString() {
        return majorVersion + "." + minorVersion + "." + patchVersion;
    }

    /**
     * Get NMS version string (e.g., "v1_21_R1").
     */
    public String getNmsVersion() {
        return nmsVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public boolean isFolia() {
        return isFolia;
    }

    /**
     * Get user-friendly server type string.
     */
    public String getServerType() {
        if (isFolia) return "Folia";
        if (isPaper) return "Paper";
        return "Spigot/CraftBukkit";
    }

    @Override
    public String toString() {
        return String.format("Minecraft %s (%s) - NMS: %s",
                getVersionString(), getServerType(), nmsVersion);
    }
}
