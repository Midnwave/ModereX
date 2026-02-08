package com.blockforge.moderex.hooks;

import com.blockforge.moderex.ModereX;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Hook for BlueMap - 3D map renderer.
 * Provides terrain tile data for the 3D replay viewer when BlueMap is installed.
 * Uses pure reflection (no compile-time dependency).
 */
public class BlueMapHook {

    private final ModereX plugin;
    private boolean available = false;
    private String version;
    private String webUrl;
    private int webPort = 8100;
    private final List<String> mapIds = new ArrayList<>();

    public BlueMapHook(ModereX plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        try {
            Plugin blueMapPlugin = Bukkit.getPluginManager().getPlugin("BlueMap");
            if (blueMapPlugin == null || !blueMapPlugin.isEnabled()) return;

            available = true;
            version = blueMapPlugin.getDescription().getVersion();

            // Read BlueMap webserver config for the port
            readBlueMapConfig(blueMapPlugin.getDataFolder().toPath());

            // Discover available map IDs
            discoverMaps(blueMapPlugin.getDataFolder().toPath());

            plugin.getLogger().info("BlueMap integration enabled (v" + version +
                    ", web: " + webUrl + ", maps: " + mapIds + ")");
        } catch (Exception e) {
            plugin.logDebug("BlueMap not available: " + e.getMessage());
        }
    }

    private void readBlueMapConfig(Path dataFolder) {
        try {
            Path webserverConf = dataFolder.resolve("webserver.conf");
            if (Files.exists(webserverConf)) {
                List<String> lines = Files.readAllLines(webserverConf);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("port") && trimmed.contains(":")) {
                        String val = trimmed.substring(trimmed.indexOf(':') + 1).trim();
                        val = val.replaceAll("[^0-9]", "");
                        if (!val.isEmpty()) {
                            webPort = Integer.parseInt(val);
                        }
                    }
                }
            }
        } catch (Exception e) {
            plugin.logDebug("Could not read BlueMap webserver config: " + e.getMessage());
        }
        webUrl = "http://localhost:" + webPort;
    }

    private void discoverMaps(Path dataFolder) {
        // Try BlueMap API via reflection first
        try {
            Class<?> apiClass = Class.forName("de.bluecolored.bluemap.api.BlueMapAPI");
            Object optApi = apiClass.getMethod("getInstance").invoke(null);
            boolean present = (boolean) optApi.getClass().getMethod("isPresent").invoke(optApi);
            if (present) {
                Object api = optApi.getClass().getMethod("get").invoke(optApi);
                Collection<?> maps = (Collection<?>) api.getClass().getMethod("getMaps").invoke(api);
                for (Object map : maps) {
                    String id = (String) map.getClass().getMethod("getId").invoke(map);
                    mapIds.add(id);
                }
                return;
            }
        } catch (Exception e) {
            plugin.logDebug("Could not discover BlueMap maps via API: " + e.getMessage());
        }

        // Fallback: scan web/maps directory
        Path webMaps = dataFolder.resolve("web").resolve("maps");
        if (Files.isDirectory(webMaps)) {
            try (Stream<Path> dirs = Files.list(webMaps)) {
                dirs.filter(Files::isDirectory)
                        .forEach(d -> mapIds.add(d.getFileName().toString()));
            } catch (IOException e) {
                plugin.logDebug("Could not scan BlueMap maps directory: " + e.getMessage());
            }
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public String getVersion() {
        return version;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public int getWebPort() {
        return webPort;
    }

    public List<String> getMapIds() {
        return mapIds;
    }
}
