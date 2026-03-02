package com.blockforge.moderex.rules;

import com.blockforge.moderex.ModereX;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Manages Code of Conduct integration for Minecraft 1.21.2+.
 * Auto-generates codeofconduct/en_us.txt from server rules when supported.
 * Falls back to chat-based acceptance (Java) or book (Bedrock) when not supported.
 */
public class CodeOfConductManager {

    private final ModereX plugin;
    private boolean cocSupported = false;

    public CodeOfConductManager(ModereX plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        detectCocSupport();
        if (cocSupported) {
            generateCocFile();
            plugin.getLogger().info("Code of Conduct integration enabled (1.21.2+ detected)");
        } else {
            plugin.getLogger().info("Code of Conduct not supported - using chat/book fallback");
        }
    }

    /**
     * Detect if the server supports Code of Conduct (1.21.2+ with enable-code-of-conduct).
     */
    private void detectCocSupport() {
        try {
            // Check server version
            String version = plugin.getServer().getMinecraftVersion();
            if (!isVersionAtLeast(version, "1.21.2")) {
                return;
            }

            // Check server.properties for enable-code-of-conduct
            File serverProperties = new File("server.properties");
            if (serverProperties.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(serverProperties)) {
                    props.load(fis);
                }
                String cocEnabled = props.getProperty("enable-code-of-conduct", "false");
                cocSupported = "true".equalsIgnoreCase(cocEnabled);
            }
        } catch (Exception e) {
            plugin.logError("Failed to detect CoC support", e);
        }
    }

    /**
     * Check if a version string is at least the target version.
     */
    private boolean isVersionAtLeast(String version, String target) {
        try {
            String[] vParts = version.split("\\.");
            String[] tParts = target.split("\\.");

            for (int i = 0; i < Math.max(vParts.length, tParts.length); i++) {
                int v = i < vParts.length ? Integer.parseInt(vParts[i].replaceAll("[^0-9]", "")) : 0;
                int t = i < tParts.length ? Integer.parseInt(tParts[i].replaceAll("[^0-9]", "")) : 0;
                if (v > t) return true;
                if (v < t) return false;
            }
            return true; // Equal
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generate the codeofconduct/en_us.txt file from current server rules.
     */
    public void generateCocFile() {
        if (!cocSupported) return;

        try {
            Path cocDir = Path.of("codeofconduct");
            Files.createDirectories(cocDir);

            Path cocFile = cocDir.resolve("en_us.txt");
            List<Rule> rules = plugin.getRulesManager().getEnabledRules();

            StringBuilder sb = new StringBuilder();
            sb.append("Server Rules & Code of Conduct\n");
            sb.append("==============================\n\n");

            for (int i = 0; i < rules.size(); i++) {
                Rule rule = rules.get(i);
                sb.append((i + 1)).append(". ").append(rule.getTitle()).append("\n");
                if (rule.getDescription() != null && !rule.getDescription().isEmpty()) {
                    sb.append("   ").append(rule.getDescription()).append("\n");
                }
                if (rule.getAiDescription() != null && !rule.getAiDescription().isEmpty()) {
                    sb.append("   ").append(rule.getAiDescription()).append("\n");
                }
                sb.append("\n");
            }

            sb.append("By joining this server, you agree to follow these rules.\n");
            sb.append("Violations may result in warnings, mutes, or bans.\n");

            Files.writeString(cocFile, sb.toString());
            plugin.getLogger().info("Generated Code of Conduct file with " + rules.size() + " rules");
        } catch (IOException e) {
            plugin.logError("Failed to generate CoC file", e);
        }
    }

    /**
     * Present rules to a player using the appropriate method.
     */
    public void presentRules(Player player) {
        if (cocSupported) {
            // CoC is handled natively by the server on 1.21.2+
            return;
        }

        // Detect if player is Bedrock (Floodgate/Geyser prefix)
        boolean isBedrock = isBedrockPlayer(player);

        if (isBedrock) {
            plugin.getRuleAcceptanceManager().giveRulesBook(player);
        } else {
            plugin.getRuleAcceptanceManager().showRulesInChat(player);
        }
    }

    /**
     * Check if a player is a Bedrock player (connected via Geyser/Floodgate).
     */
    private boolean isBedrockPlayer(Player player) {
        // Check for Floodgate prefix (default is '.')
        if (player.getName().startsWith(".")) {
            return true;
        }

        // Check for Floodgate API
        try {
            Class<?> floodgateApi = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Object instance = floodgateApi.getMethod("getInstance").invoke(null);
            return (boolean) floodgateApi.getMethod("isFloodgatePlayer", java.util.UUID.class)
                    .invoke(instance, player.getUniqueId());
        } catch (Exception ignored) {
            // Floodgate not present
        }

        return false;
    }

    public boolean isCocSupported() {
        return cocSupported;
    }

    /**
     * Regenerate the CoC file when rules change.
     */
    public void onRulesChanged() {
        if (cocSupported) {
            generateCocFile();
        }
    }
}
