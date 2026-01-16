package com.blockforge.moderex.webpanel.netty;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;

public class ServerVersionUtil {

    private static final String VERSION;
    private static final int MAJOR_VERSION;
    private static final int MINOR_VERSION;
    private static final boolean IS_PAPER;
    private static final boolean IS_FOLIA;

    static {
        String versionString = Bukkit.getServer().getVersion();
        String bukkitVersion = Bukkit.getBukkitVersion();

        String[] parts = bukkitVersion.split("-")[0].split("\\.");
        MAJOR_VERSION = parts.length > 0 ? parseInt(parts[0], 1) : 1;
        MINOR_VERSION = parts.length > 1 ? parseInt(parts[1], 0) : 0;
        VERSION = MAJOR_VERSION + "." + MINOR_VERSION;

        IS_PAPER = hasClass("io.papermc.paper.configuration.Configuration") ||
                   hasClass("com.destroystokyo.paper.PaperConfig");

        IS_FOLIA = hasClass("io.papermc.paper.threadedregions.RegionizedServer");
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String getVersion() {
        return VERSION;
    }

    public static int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public static int getMinorVersion() {
        return MINOR_VERSION;
    }

    public static boolean isPaper() {
        return IS_PAPER;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static boolean isAtLeast(int major, int minor) {
        return MAJOR_VERSION > major || (MAJOR_VERSION == major && MINOR_VERSION >= minor);
    }

    public static String getNmsVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String[] parts = packageName.split("\\.");
        if (parts.length > 3 && parts[3].startsWith("v")) {
            return parts[3];
        }
        return null;
    }

    public static boolean hasRelocatedNetty() {
        return hasClass("io.papermc.paper.network.ChannelInitializeListener");
    }

    public static boolean hasNetty() {
        return hasClass("io.netty.channel.Channel") ||
               hasClass("net.minecraft.network.Connection");
    }

    public static NettyInjector getInjector() {
        if (!hasNetty()) {
            return null;
        }

        return new CompositeNettyInjector();
    }

    private static class CompositeNettyInjector implements NettyInjector {
        private NettyInjector activeInjector;

        @Override
        public boolean inject(ModereX plugin, HttpRequestHandler httpHandler) {
            if (IS_PAPER && hasRelocatedNetty()) {
                PaperNettyInjector paperInjector = new PaperNettyInjector();
                if (paperInjector.inject(plugin, httpHandler)) {
                    activeInjector = paperInjector;
                    return true;
                }
                plugin.logDebug("[Netty] Paper API injection failed, trying reflection...");
            }

            ReflectionNettyInjector reflectionInjector = new ReflectionNettyInjector();
            if (reflectionInjector.inject(plugin, httpHandler)) {
                activeInjector = reflectionInjector;
                return true;
            }

            plugin.logDebug("[Netty] All injection methods failed");
            return false;
        }

        @Override
        public boolean remove() {
            return activeInjector != null && activeInjector.remove();
        }

        @Override
        public boolean isInjected() {
            return activeInjector != null && activeInjector.isInjected();
        }

        @Override
        public String getName() {
            return activeInjector != null ? activeInjector.getName() : "Composite";
        }
    }
}

