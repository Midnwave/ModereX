package com.blockforge.moderex.resourcepack;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Manages the custom resource pack for ModereX staff GUIs.
 * Provides custom textures for GUI items using custom model data.
 */
public class ResourcePackManager implements Listener {

    private final ModereX plugin;
    private final Set<UUID> pendingPlayers = new HashSet<>();
    private final Set<UUID> loadedPlayers = new HashSet<>();

    private boolean enabled;
    private boolean staffOnly;
    private String hostingMethod;
    private String externalUrl;
    private String promptMessage;
    private boolean required;

    private Path packFile;
    private String packHash;
    private URI packUri;

    public ResourcePackManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        loadConfig();

        if (!enabled) {
            plugin.logDebug("[ResourcePack] Resource pack is disabled in config");
            return;
        }

        // Generate the resource pack ZIP
        try {
            generateResourcePack();
        } catch (IOException e) {
            plugin.logError("Failed to generate resource pack", e);
            enabled = false;
            return;
        }

        // Register listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getLogger().info("Resource pack initialized" +
                (staffOnly ? " (staff only)" : " (all players)"));
    }

    private void loadConfig() {
        var config = plugin.getConfigManager().getConfig();
        enabled = config.getBoolean("resourcepack.enabled", true);
        staffOnly = config.getBoolean("resourcepack.staff-only", true);
        hostingMethod = config.getString("resourcepack.hosting", "bundled");
        externalUrl = config.getString("resourcepack.external-url", "");
        promptMessage = config.getString("resourcepack.prompt-message",
                "&7ModereX requires a resource pack for the best experience.");
        required = config.getBoolean("resourcepack.required", false);
    }

    private void generateResourcePack() throws IOException {
        // Create temp directory for pack
        Path packDir = plugin.getDataFolder().toPath().resolve("resourcepack");
        Files.createDirectories(packDir);

        packFile = packDir.resolve("ModereX-GUI.zip");

        // Copy bundled resources to temp location and create ZIP
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(packFile.toFile()))) {
            // Get the resource pack assets from the JAR
            copyResourceToZip(zos, "resourcepack/pack.mcmeta", "pack.mcmeta");

            // Copy all texture files
            String[] textures = {
                    "moderex_logo", "shield_blue", "shield_green", "shield_red",
                    "sword", "gavel", "bell", "scroll", "gear", "eye",
                    "checkmark", "x_mark", "warning", "lightning",
                    "user", "staff", "combat", "movement", "packet", "world", "misc",
                    "arrow_right", "arrow_left", "preset"
            };

            for (String texture : textures) {
                copyResourceToZip(zos,
                        "resourcepack/assets/moderex/textures/item/" + texture + ".png",
                        "assets/moderex/textures/item/" + texture + ".png");
                copyResourceToZip(zos,
                        "resourcepack/assets/moderex/models/item/" + texture + ".json",
                        "assets/moderex/models/item/" + texture + ".json");
            }

            // Copy paper override
            copyResourceToZip(zos,
                    "resourcepack/assets/minecraft/models/item/paper.json",
                    "assets/minecraft/models/item/paper.json");
        }

        // Calculate SHA-1 hash
        packHash = calculateSHA1(packFile);
        plugin.logDebug("[ResourcePack] Generated pack with hash: " + packHash);

        // Set up URI based on hosting method
        if ("external".equalsIgnoreCase(hostingMethod) && !externalUrl.isEmpty()) {
            packUri = URI.create(externalUrl);
        } else {
            // Bundled hosting - we'll serve it via the web panel
            if (plugin.getWebPanelServer() != null) {
                int port = plugin.getConfigManager().getSettings().getWebPanelPort();
                String host = plugin.getConfigManager().getSettings().getWebPanelHost();
                if (host == null || host.isEmpty()) {
                    host = "localhost";
                }
                packUri = URI.create("http://" + host + ":" + port + "/resourcepack/ModereX-GUI.zip");
            } else {
                plugin.getLogger().warning("Resource pack bundled hosting requires web panel to be enabled!");
                plugin.getLogger().warning("Set webpanel.enabled: true or use external hosting");
                enabled = false;
            }
        }
    }

    private void copyResourceToZip(ZipOutputStream zos, String resourcePath, String zipPath) throws IOException {
        try (InputStream is = plugin.getResource(resourcePath)) {
            if (is == null) {
                plugin.logDebug("[ResourcePack] Resource not found: " + resourcePath);
                return;
            }
            zos.putNextEntry(new ZipEntry(zipPath));
            is.transferTo(zos);
            zos.closeEntry();
        }
    }

    private String calculateSHA1(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream is = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
            }
            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-1 algorithm not available", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();

        // Check if staff only
        if (staffOnly && !player.hasPermission("moderex.staff")) {
            return;
        }

        // Delay sending pack to avoid connection issues
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            sendResourcePack(player);
        }, 40L); // 2 seconds delay
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED -> {
                pendingPlayers.remove(uuid);
                loadedPlayers.add(uuid);
                plugin.logDebug("[ResourcePack] " + player.getName() + " loaded resource pack");
            }
            case DECLINED -> {
                pendingPlayers.remove(uuid);
                if (required && player.hasPermission("moderex.staff")) {
                    Msg.kick(player, Component.text("Resource pack is required for staff members"));
                }
                plugin.logDebug("[ResourcePack] " + player.getName() + " declined resource pack");
            }
            case FAILED_DOWNLOAD -> {
                pendingPlayers.remove(uuid);
                plugin.logDebug("[ResourcePack] " + player.getName() + " failed to download resource pack");
            }
            case ACCEPTED -> {
                plugin.logDebug("[ResourcePack] " + player.getName() + " accepted resource pack, downloading...");
            }
            case DOWNLOADED -> {
                plugin.logDebug("[ResourcePack] " + player.getName() + " downloaded resource pack");
            }
            case INVALID_URL -> {
                pendingPlayers.remove(uuid);
                plugin.logDebug("[ResourcePack] Invalid URL for " + player.getName());
            }
            case FAILED_RELOAD -> {
                plugin.logDebug("[ResourcePack] Failed reload for " + player.getName());
            }
            case DISCARDED -> {
                loadedPlayers.remove(uuid);
                plugin.logDebug("[ResourcePack] " + player.getName() + " discarded resource pack");
            }
        }
    }

    public void sendResourcePack(Player player) {
        if (!enabled || packUri == null) return;

        pendingPlayers.add(player.getUniqueId());

        try {
            ResourcePackInfo packInfo = ResourcePackInfo.resourcePackInfo()
                    .uri(packUri)
                    .hash(packHash)
                    .build();

            ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                    .packs(packInfo)
                    .prompt(Component.text(promptMessage.replace("&", "\u00A7")))
                    .required(required && player.hasPermission("moderex.staff"))
                    .build();

            player.sendResourcePacks(request);
        } catch (Exception e) {
            plugin.logDebug("[ResourcePack] Failed to send pack to " + player.getName() + ": " + e.getMessage());
            // Fallback to legacy method
            player.setResourcePack(packUri.toString(), packHash);
        }
    }

    public boolean hasResourcePack(Player player) {
        return loadedPlayers.contains(player.getUniqueId());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Path getPackFile() {
        return packFile;
    }

    public String getPackHash() {
        return packHash;
    }

    public void shutdown() {
        pendingPlayers.clear();
        loadedPlayers.clear();
    }
}
