package com.blockforge.moderex.config.lang;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final ModereX plugin;
    private final Map<String, FileConfiguration> languages;
    private String currentLanguage;
    private FileConfiguration currentMessages;

    // Default messages as fallback
    private static final Map<String, String> DEFAULTS = new HashMap<>();

    static {
        // General
        DEFAULTS.put("general.prefix", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> ");
        DEFAULTS.put("general.no-permission", "<red>You don't have permission to do that.");
        DEFAULTS.put("general.player-not-found", "<red>Player not found: <player>");
        DEFAULTS.put("general.player-only", "<red>This command can only be used by players.");
        DEFAULTS.put("general.invalid-duration", "<red>Invalid duration format. Use: 1mo, 3d, 1h, 30m, 15s, or permanent");
        DEFAULTS.put("general.invalid-arguments", "<red>Invalid arguments. Usage: <usage>");
        DEFAULTS.put("general.reload-success", "<green>Configuration reloaded successfully!");
        DEFAULTS.put("general.plugin-info", "<gray>ModereX v<version> by BlockForge");

        // Mute
        DEFAULTS.put("mute.success", "<green>Successfully muted <yellow><player></yellow> for <duration>.");
        DEFAULTS.put("mute.broadcast", "<gray><staff> muted <player> for <duration>. Reason: <reason>");
        DEFAULTS.put("mute.message", "<red>You have been muted for <duration>. Reason: <reason>");
        DEFAULTS.put("mute.already-muted", "<red><player> is already muted.");
        DEFAULTS.put("unmute.success", "<green>Successfully unmuted <yellow><player></yellow>.");
        DEFAULTS.put("unmute.broadcast", "<gray><staff> unmuted <player>.");
        DEFAULTS.put("unmute.not-muted", "<red><player> is not muted.");
        DEFAULTS.put("mute.chat-attempt", "<red>You are muted for <duration>. Reason: <reason>");

        // Ban
        DEFAULTS.put("ban.success", "<green>Successfully banned <yellow><player></yellow> for <duration>.");
        DEFAULTS.put("ban.broadcast", "<gray><staff> banned <player> for <duration>. Reason: <reason>");
        DEFAULTS.put("ban.disconnect", "<prefix>\n<red><bold>You have been Banned!</bold></red>\n\n<gray>Banned on: <white><date></white>\n<gray>Banned by: <white><staff></white>\n<gray>Duration: <white><duration></white>\n<gray>Reason: <white><reason></white>\n\n<yellow>Appeal at: discord.gg/example");
        DEFAULTS.put("ban.already-banned", "<red><player> is already banned.");
        DEFAULTS.put("unban.success", "<green>Successfully unbanned <yellow><player></yellow>.");
        DEFAULTS.put("unban.broadcast", "<gray><staff> unbanned <player>.");
        DEFAULTS.put("unban.not-banned", "<red><player> is not banned.");

        // IP Ban
        DEFAULTS.put("ipban.success", "<green>Successfully IP banned <yellow><player></yellow> for <duration>.");
        DEFAULTS.put("ipban.broadcast", "<gray><staff> IP banned <player> for <duration>. Reason: <reason>");
        DEFAULTS.put("ipban.disconnect", "<prefix>\n<red><bold>You have been IP Banned!</bold></red>\n\n<gray>Banned on: <white><date></white>\n<gray>Banned by: <white><staff></white>\n<gray>Duration: <white><duration></white>\n<gray>Reason: <white><reason></white>\n\n<yellow>Appeal at: discord.gg/example");

        // Kick
        DEFAULTS.put("kick.success", "<green>Successfully kicked <yellow><player></yellow>.");
        DEFAULTS.put("kick.broadcast", "<gray><staff> kicked <player>. Reason: <reason>");
        DEFAULTS.put("kick.disconnect", "<prefix>\n<red><bold>You have been Kicked!</bold></red>\n\n<gray>Kicked by: <white><staff></white>\n<gray>Reason: <white><reason></white>");

        // Warn
        DEFAULTS.put("warn.success", "<green>Successfully warned <yellow><player></yellow>.");
        DEFAULTS.put("warn.broadcast", "<gray><staff> warned <player>. Reason: <reason>");
        DEFAULTS.put("warn.message", "<red>You have been warned! Reason: <reason>");
        DEFAULTS.put("warn.cleared", "<green>Successfully cleared all warnings for <yellow><player></yellow>.");
        DEFAULTS.put("warn.cleared-broadcast", "<gray><staff> cleared all warnings for <player>.");

        // Staff Chat
        DEFAULTS.put("staffchat.format", "<light_purple><bold>Staff</bold></light_purple> <dark_gray>|</dark_gray> <#FF6B6B><underlined><player></underlined></#FF6B6B><gray>: <white><message>");
        DEFAULTS.put("staffchat.enabled", "<green>Staff chat enabled. All messages will go to staff chat.");
        DEFAULTS.put("staffchat.disabled", "<red>Staff chat disabled.");
        DEFAULTS.put("staffchat.help-request", "<red><bold>STAFF HELP</bold></red> <dark_gray>|</dark_gray> <yellow><player></yellow> <gray>needs assistance!");

        // Vanish
        DEFAULTS.put("vanish.enabled", "<green>You are now vanished.");
        DEFAULTS.put("vanish.disabled", "<red>You are no longer vanished.");

        // Chat
        DEFAULTS.put("chat.disabled", "<green>Chat has been disabled.");
        DEFAULTS.put("chat.enabled", "<green>Chat has been enabled.");
        DEFAULTS.put("chat.slowmode-set", "<green>Chat slowmode set to <yellow><seconds></yellow> seconds.");
        DEFAULTS.put("chat.slowmode-message", "<red>You must wait <seconds> seconds before chatting again.");
        DEFAULTS.put("chat.cleared", "<gray>Chat has been cleared by <player>.");
        DEFAULTS.put("chat.disabled-message", "<red>Chat is currently disabled.");

        // Command Blacklist
        DEFAULTS.put("cmdblacklist.success", "<green>Successfully blacklisted <yellow><command></yellow> for <player>.");
        DEFAULTS.put("cmdblacklist.removed", "<green>Successfully removed command blacklist for <player>.");
        DEFAULTS.put("cmdblacklist.blocked", "<red>You are not allowed to use this command.");

        // Modlog
        DEFAULTS.put("modlog.header", "<gold>===== Modlog for <player> =====");
        DEFAULTS.put("modlog.entry", "<gray>[<date>] <type>: <reason> (by <staff>) - <duration>");
        DEFAULTS.put("modlog.empty", "<gray>No moderation history found for <player>.");
        DEFAULTS.put("modlog.footer", "<gold>===== Page <page>/<total> =====");

        // Automod
        DEFAULTS.put("automod.blocked", "<red>Your message was blocked by automod.");
        DEFAULTS.put("automod.alert", "<red>[Automod]</red> <gray><player> triggered rule: <rule>");
        DEFAULTS.put("automod.spam-blocked", "<red>Please don't spam.");

        // Anticheat
        DEFAULTS.put("anticheat.alert", "<red>[AC]</red> <yellow><player></yellow> <gray>failed <check> <gray>x<count>");

        // Watchlist
        DEFAULTS.put("watchlist.alert", "<gold>[Watchlist]</gold> <gray><player> <action>");

        // Web Panel
        DEFAULTS.put("webpanel.connected", "<green>Connected to web panel.");
        DEFAULTS.put("webpanel.disconnected", "<red>Disconnected from web panel.");

        // Update
        DEFAULTS.put("update.available", "<green>A new version of ModereX is available: <version>");
        DEFAULTS.put("update.current", "<gray>You are running the latest version of ModereX.");

        // Command History
        DEFAULTS.put("cmdhistory.header", "<gold>===== Command History for <player> =====");
        DEFAULTS.put("cmdhistory.entry", "<gray>[<date>] <command>");
        DEFAULTS.put("cmdhistory.empty", "<gray>No command history found for <player>.");
        DEFAULTS.put("cmdhistory.footer", "<gold>===== Page <page>/<total> =====");

        // GUI
        DEFAULTS.put("gui.punish.title", "Punish Player");
        DEFAULTS.put("gui.punish.select-title", "Select Player");
        DEFAULTS.put("gui.mute.title", "Mute <player>");
        DEFAULTS.put("gui.ban.title", "Ban <player>");

        // Analytics
        DEFAULTS.put("analytics.header", "<gold>===== ModereX Analytics =====");
    }

    public LanguageManager(ModereX plugin) {
        this.plugin = plugin;
        this.languages = new HashMap<>();
    }

    public void load() {
        // Create messages folder
        File messagesFolder = new File(plugin.getDataFolder(), "messages");
        if (!messagesFolder.exists()) {
            messagesFolder.mkdirs();
        }

        // Save default language files
        saveDefaultLanguages();

        // Load current language
        currentLanguage = plugin.getConfigManager().getSettings().getLanguage();
        loadLanguage(currentLanguage);
    }

    private void saveDefaultLanguages() {
        String[] defaultLanguages = {"en_US", "es_ES", "de_DE", "fr_FR", "pt_BR", "zh_CN"};

        for (String lang : defaultLanguages) {
            File langFile = new File(plugin.getDataFolder(), "messages/" + lang + ".yml");
            if (!langFile.exists()) {
                // Try to save from resources
                String resourcePath = "messages/" + lang + ".yml";
                if (plugin.getResource(resourcePath) != null) {
                    plugin.saveResource(resourcePath, false);
                } else if (lang.equals("en_US")) {
                    // Create default English file
                    createDefaultEnglish(langFile);
                }
            }
        }
    }

    private void createDefaultEnglish(File file) {
        FileConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.logError("Failed to create default English language file", e);
        }
    }

    public void loadLanguage(String language) {
        File langFile = new File(plugin.getDataFolder(), "messages/" + language + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file not found: " + language + ".yml, falling back to en_US");
            langFile = new File(plugin.getDataFolder(), "messages/en_US.yml");

            if (!langFile.exists()) {
                createDefaultEnglish(langFile);
            }
        }

        currentMessages = YamlConfiguration.loadConfiguration(langFile);
        languages.put(language, currentMessages);
        currentLanguage = language;

        plugin.getLogger().info("Loaded language: " + language);
    }

    public String getRaw(MessageKey key) {
        return getRaw(key.getPath());
    }

    public String getRaw(String path) {
        String message = currentMessages.getString(path);
        if (message == null) {
            message = DEFAULTS.getOrDefault(path, "<red>Missing message: " + path);
        }
        return message;
    }

    public Component get(MessageKey key) {
        return TextUtil.parse(getRaw(key));
    }

    public Component get(MessageKey key, String... replacements) {
        String message = getRaw(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace("<" + replacements[i] + ">", replacements[i + 1]);
        }
        return TextUtil.parse(message);
    }

    public Component getPrefix() {
        return TextUtil.parse(getRaw(MessageKey.PREFIX));
    }

    public Component getPrefixed(MessageKey key, String... replacements) {
        return getPrefix().append(get(key, replacements));
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public void setLanguage(String language) {
        loadLanguage(language);
    }

    public boolean isLanguageAvailable(String language) {
        File langFile = new File(plugin.getDataFolder(), "messages/" + language + ".yml");
        return langFile.exists();
    }
}
