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
import java.util.*;

public class LanguageManager {

    private final ModereX plugin;
    private final Map<String, FileConfiguration> loadedLocales;
    private String currentLanguage;
    private FileConfiguration currentMessages;
    private FileConfiguration englishFallback;

    // Bundled language files shipped with the plugin
    private static final String[] BUNDLED_LANGUAGES = {"en_US", "es_ES", "de_DE", "fr_FR", "pt_BR", "zh_CN"};

    // Language display names for UI/commands
    private static final Map<String, String> LANGUAGE_NAMES = new LinkedHashMap<>();

    static {
        LANGUAGE_NAMES.put("en_US", "English");
        LANGUAGE_NAMES.put("es_ES", "Espa\u00f1ol");
        LANGUAGE_NAMES.put("de_DE", "Deutsch");
        LANGUAGE_NAMES.put("fr_FR", "Fran\u00e7ais");
        LANGUAGE_NAMES.put("pt_BR", "Portugu\u00eas (Brasil)");
        LANGUAGE_NAMES.put("zh_CN", "\u4e2d\u6587 (\u7b80\u4f53)");
    }

    // Default messages as hardcoded fallback (used when both YAML files are missing a key)
    private static final Map<String, String> DEFAULTS = new HashMap<>();

    static {
        // General
        DEFAULTS.put("general.prefix", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> ");
        DEFAULTS.put("general.no-permission", "<red>You don't have permission to do that.");
        DEFAULTS.put("general.player-not-found", "<red>Player not found: <white><player>");
        DEFAULTS.put("general.player-only", "<red>This command can only be used by players.");
        DEFAULTS.put("general.invalid-duration", "<red>Invalid duration format. Use: 1mo, 3d, 1h, 30m, 15s, or permanent");
        DEFAULTS.put("general.invalid-arguments", "<red>Invalid arguments. Usage: <white><usage>");
        DEFAULTS.put("general.reload-success", "<green>Configuration reloaded successfully!");
        DEFAULTS.put("general.plugin-info", "<gray>ModereX v<version> by BlockForge Studios & ADF Industries LLC.");

        // Mute
        DEFAULTS.put("mute.success", "<green>Successfully muted <yellow><player></yellow> for <white><duration></white>.");
        DEFAULTS.put("mute.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <gold>muted</gold> <yellow><player></yellow> <gray>for</gray> <white><duration></white> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("mute.message", "<red>You have been muted for <white><duration></white>.\n<gray>Reason: <white><reason><evidence>");
        DEFAULTS.put("mute.already-muted", "<red><player> is already muted.");
        DEFAULTS.put("mute.chat-attempt", "<red>You are muted for <white><duration></white>.\n<gray>Reason: <white><reason>");
        DEFAULTS.put("unmute.success", "<green>Successfully unmuted <yellow><player></yellow>.");
        DEFAULTS.put("unmute.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <green>unmuted</green> <yellow><player></yellow> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>");
        DEFAULTS.put("unmute.not-muted", "<red><player> is not muted.");

        // Ban
        DEFAULTS.put("ban.success", "<green>Successfully banned <yellow><player></yellow> for <white><duration></white>.");
        DEFAULTS.put("ban.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <dark_red>banned</dark_red> <yellow><player></yellow> <gray>for</gray> <white><duration></white> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("ban.disconnect", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient>\n\n<red><bold>You have been Banned!</bold></red>\n\n<gray>Banned on: <white><date></white>\n<gray>Banned by: <white><staff></white>\n<gray>Duration: <white><duration></white>\n<gray>Reason: <white><reason></white><evidence>\n\n<yellow>Appeal at: discord.gg/example");
        DEFAULTS.put("ban.already-banned", "<red><player> is already banned.");
        DEFAULTS.put("unban.success", "<green>Successfully unbanned <yellow><player></yellow>.");
        DEFAULTS.put("unban.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <green>unbanned</green> <yellow><player></yellow> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>");
        DEFAULTS.put("unban.not-banned", "<red><player> is not banned.");

        // IP Ban
        DEFAULTS.put("ipban.success", "<green>Successfully IP banned <yellow><player></yellow> for <white><duration></white>.");
        DEFAULTS.put("ipban.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <dark_red>IP banned</dark_red> <yellow><player></yellow> <gray>for</gray> <white><duration></white> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("ipban.disconnect", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient>\n\n<red><bold>You have been IP Banned!</bold></red>\n\n<gray>Banned on: <white><date></white>\n<gray>Banned by: <white><staff></white>\n<gray>Duration: <white><duration></white>\n<gray>Reason: <white><reason></white><evidence>\n\n<yellow>Appeal at: discord.gg/example");

        // Kick
        DEFAULTS.put("kick.success", "<green>Successfully kicked <yellow><player></yellow>.");
        DEFAULTS.put("kick.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <red>kicked</red> <yellow><player></yellow> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("kick.disconnect", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient>\n\n<red><bold>You have been Kicked!</bold></red>\n\n<gray>Kicked by: <white><staff></white>\n<gray>Reason: <white><reason></white><evidence>");

        // Warn
        DEFAULTS.put("warn.success", "<green>Successfully warned <yellow><player></yellow>.");
        DEFAULTS.put("warn.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <gold>warned</gold> <yellow><player></yellow> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("warn.message", "<red><bold>WARNING!</bold></red> <gray>You have been warned.\n<gray>Reason: <white><reason><evidence>");
        DEFAULTS.put("warn.cleared", "<green>Successfully cleared all warnings for <yellow><player></yellow>.");
        DEFAULTS.put("warn.cleared-broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <gray>cleared all warnings for</gray> <yellow><player></yellow>");

        // Staff Chat
        DEFAULTS.put("staffchat.format", "<light_purple><bold>Staff</bold></light_purple> <dark_gray>|</dark_gray> <#FF6B6B><underlined><player></underlined></#FF6B6B><gray>: <white><message>");
        DEFAULTS.put("staffchat.enabled", "<green>Staff chat <bold>enabled</bold>. All messages will go to staff chat.");
        DEFAULTS.put("staffchat.disabled", "<red>Staff chat <bold>disabled</bold>.");
        DEFAULTS.put("staffchat.help-request", "<red><bold>STAFF HELP</bold></red> <dark_gray>|</dark_gray> <yellow><player></yellow> <gray>needs assistance!");

        // Vanish
        DEFAULTS.put("vanish.enabled", "<green>You are now <bold>vanished</bold>.");
        DEFAULTS.put("vanish.disabled", "<red>You are no longer <bold>vanished</bold>.");

        // Chat
        DEFAULTS.put("chat.disabled", "<green>Chat has been <bold>disabled</bold>.");
        DEFAULTS.put("chat.enabled", "<green>Chat has been <bold>enabled</bold>.");
        DEFAULTS.put("chat.slowmode-set", "<green>Chat slowmode set to <yellow><seconds></yellow> seconds.");
        DEFAULTS.put("chat.slowmode-message", "<red>Slow down! Wait <white><seconds></white> seconds before chatting again.");
        DEFAULTS.put("chat.cleared", "<gray>Chat has been cleared by <yellow><player></yellow>.");
        DEFAULTS.put("chat.disabled-message", "<red>Chat is currently disabled.");

        // Command Blacklist
        DEFAULTS.put("cmdblacklist.success", "<green>Successfully blacklisted <yellow><command></yellow> for <white><player></white>.");
        DEFAULTS.put("cmdblacklist.removed", "<green>Successfully removed command blacklist from <yellow><player></yellow>.");
        DEFAULTS.put("cmdblacklist.blocked", "<red>You are not allowed to use this command.");

        // Modlog
        DEFAULTS.put("modlog.header", "<gold><strikethrough>     </strikethrough> Modlog for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("modlog.entry", "<gray>[<white><date></white>] <yellow><type></yellow>: <white><reason></white> <gray>(by <yellow><staff></yellow>) - <white><duration></white> [<status>]");
        DEFAULTS.put("modlog.empty", "<gray>No moderation history found for <yellow><player></yellow>.");
        DEFAULTS.put("modlog.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");

        // Activity Log (for /log command)
        DEFAULTS.put("log.header", "<gold>--- Activity Log for <yellow><player></yellow> <gold>--- <dark_gray>(<white><total></white> entries)");
        DEFAULTS.put("log.entry", "<gray>[<type_color><type_label><gray>] <white><time> <gray>- <white><content>");
        DEFAULTS.put("log.empty", "<gray>No activity logs found for <yellow><player></yellow>.");
        DEFAULTS.put("log.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("log.filter-info", "<gray>Filters: <white><filters>");

        // Automod
        DEFAULTS.put("automod.blocked", "<red>Your message was blocked by automod.");
        DEFAULTS.put("automod.alert", "<red>[Automod]</red> <yellow><player></yellow> <gray>triggered rule: <white><rule>");
        DEFAULTS.put("automod.spam-blocked", "<red>Please don't spam!");

        // Anticheat
        DEFAULTS.put("anticheat.alert", "<red>[AC]</red> <yellow><player></yellow> <gray>failed <white><check></white> <gray>x<white><count></white>");

        // Watchlist
        DEFAULTS.put("watchlist.alert", "<gold>[Watchlist]</gold> <yellow><player></yellow> <gray><action>");

        // Web Panel
        DEFAULTS.put("webpanel.connected", "<green>Connected to web panel.");
        DEFAULTS.put("webpanel.disconnected", "<red>Disconnected from web panel.");

        // Update
        DEFAULTS.put("update.available", "<green>A new version of ModereX is available: <yellow><version></yellow>\n<gray>Download at: <aqua><click:open_url:'https://modrinth.com/plugin/moderex'>modrinth.com/plugin/moderex</click>");
        DEFAULTS.put("update.current", "<gray>You are running the latest version of ModereX.");

        // GitHub Auto-Update
        DEFAULTS.put("github.update_available", "<green>GitHub Update Available: <yellow><version></yellow> <gray>(current: <current>)\n<gray>Auto-download is enabled. The update will download automatically.");
        DEFAULTS.put("github.update_downloaded", "<green>Update <yellow><version></yellow> downloaded successfully!\n<gray>Restart the server to apply the update.");
        DEFAULTS.put("github.update_pending", "<yellow>Update <version> is pending. <gray>Restart the server to apply.");
        DEFAULTS.put("github.update_downloading", "<gray>Downloading update <yellow><version></yellow>...");
        DEFAULTS.put("github.update_failed", "<red>Failed to download update. Check console for details.");

        // Command History
        DEFAULTS.put("cmdhistory.header", "<gold><strikethrough>     </strikethrough> Command History for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("cmdhistory.entry", "<gray>[<white><date></white>] <white><command>");
        DEFAULTS.put("cmdhistory.empty", "<gray>No command history found for <yellow><player></yellow>.");
        DEFAULTS.put("cmdhistory.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");

        // GUI
        DEFAULTS.put("gui.punish.title", "<dark_gray>Punish Player");
        DEFAULTS.put("gui.punish.select-title", "<dark_gray>Select Player");
        DEFAULTS.put("gui.mute.title", "<dark_gray>Mute <yellow><player>");
        DEFAULTS.put("gui.ban.title", "<dark_gray>Ban <yellow><player>");
        DEFAULTS.put("gui.automod.title", "<dark_gray>Automod Settings");
        DEFAULTS.put("gui.settings.title", "<dark_gray>ModereX Settings");

        // Analytics
        DEFAULTS.put("analytics.header", "<gold><strikethrough>     </strikethrough> ModereX Analytics <strikethrough>     </strikethrough>");

        // IP Mute
        DEFAULTS.put("ipmute.success", "<green>Successfully IP muted <yellow><player></yellow> for <white><duration></white>.");
        DEFAULTS.put("ipmute.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <gold>IP muted</gold> <yellow><player></yellow> <gray>for</gray> <white><duration></white> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("ipmute.message", "<red>You have been IP muted for <white><duration></white>.\n<gray>Reason: <white><reason>");
        DEFAULTS.put("ipmute.already-muted", "<red><player> is already IP muted.");

        // Temporary Punishments
        DEFAULTS.put("tempban.success", "<green>Successfully temporarily banned <yellow><player></yellow> for <white><duration></white>.");
        DEFAULTS.put("tempban.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <dark_red>temp banned</dark_red> <yellow><player></yellow> <gray>for</gray> <white><duration></white> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("tempban.duration-required", "<red>Duration is required for temporary bans. Use: /tempban <player> <duration> [reason]");
        DEFAULTS.put("tempmute.success", "<green>Successfully temporarily muted <yellow><player></yellow> for <white><duration></white>.");
        DEFAULTS.put("tempmute.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <gold>temp muted</gold> <yellow><player></yellow> <gray>for</gray> <white><duration></white> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>\n<gray>Reason: <white><reason>");
        DEFAULTS.put("tempmute.duration-required", "<red>Duration is required for temporary mutes. Use: /tempmute <player> <duration> [reason]");

        // Unwarn
        DEFAULTS.put("unwarn.success", "<green>Successfully removed warning for <yellow><player></yellow>.");
        DEFAULTS.put("unwarn.broadcast", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient> <gray><staff></gray> <green>removed a warning</green> <gray>from</gray> <yellow><player></yellow> <dark_gray>|</dark_gray> <gray>Case:</gray> <yellow><case_id></yellow>");
        DEFAULTS.put("unwarn.not-warned", "<red><player> has no active warnings.");
        DEFAULTS.put("unwarn.by-id", "<green>Successfully removed warning with ID <yellow><id></yellow>.");

        // Check Commands
        DEFAULTS.put("checkban.header", "<gold><strikethrough>     </strikethrough> Ban Status for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("checkban.banned", "<red><player> is currently banned.");
        DEFAULTS.put("checkban.not-banned", "<green><player> is not banned.");
        DEFAULTS.put("checkban.details", "<gray>Banned by: <white><staff>\n<gray>Banned on: <white><date>\n<gray>Duration: <white><duration>\n<gray>Reason: <white><reason>\n<gray>Expires: <white><expiry>\n<gray>Case ID: <white><case_id>");
        DEFAULTS.put("checkmute.header", "<gold><strikethrough>     </strikethrough> Mute Status for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("checkmute.muted", "<red><player> is currently muted.");
        DEFAULTS.put("checkmute.not-muted", "<green><player> is not muted.");
        DEFAULTS.put("checkmute.details", "<gray>Muted by: <white><staff>\n<gray>Muted on: <white><date>\n<gray>Duration: <white><duration>\n<gray>Reason: <white><reason>\n<gray>Expires: <white><expiry>\n<gray>Case ID: <white><case_id>");
        DEFAULTS.put("checkwarn.header", "<gold><strikethrough>     </strikethrough> Warning Status for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("checkwarn.has-warnings", "<yellow><player> has <white><count></white> active warning(s).");
        DEFAULTS.put("checkwarn.no-warnings", "<green><player> has no active warnings.");
        DEFAULTS.put("checkwarn.warning-entry", "<gray>[<white><date></white>] <white><reason></white> <gray>(by <yellow><staff></yellow>) - Expires: <white><expiry>");
        DEFAULTS.put("check.header", "<gold><strikethrough>     </strikethrough> Player Info: <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("check.footer", "<gold><strikethrough>                                    </strikethrough>");
        DEFAULTS.put("check.status", "<gray>Status: <status>");
        DEFAULTS.put("check.uuid", "<gray>UUID: <white><uuid>");
        DEFAULTS.put("check.ban-active", "<red>\u26A0 BANNED</red> <gray>- <white><duration></white> <gray>(<white><reason></white>) by <yellow><staff></yellow>");
        DEFAULTS.put("check.ban-none", "<green>\u2713 Not Banned</green>");
        DEFAULTS.put("check.mute-active", "<gold>\u26A0 MUTED</gold> <gray>- <white><duration></white> <gray>(<white><reason></white>) by <yellow><staff></yellow>");
        DEFAULTS.put("check.mute-none", "<green>\u2713 Not Muted</green>");
        DEFAULTS.put("check.warnings", "<yellow>\u26A0 Warnings:</yellow> <white><count></white> active");
        DEFAULTS.put("check.warnings-none", "<green>\u2713 No Active Warnings</green>");
        DEFAULTS.put("check.ip", "<gray>IP Address: <white><ip>");
        DEFAULTS.put("check.ip-offline", "<gray>IP Address: <dark_gray>Unavailable (player offline)");
        DEFAULTS.put("check.region", "<gray>Region: <white><region>");
        DEFAULTS.put("check.alts", "<gray>Alt Accounts: <white><count>");
        DEFAULTS.put("check.total-punishments", "<gray>Total Punishments: <white><count>");

        // History Commands
        DEFAULTS.put("history.header", "<gold><strikethrough>     </strikethrough> Punishment History for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("history.entry", "<gray>[<white><date></white>] <yellow><type></yellow>: <white><reason></white> <gray>by <yellow><staff></yellow> - <white><duration></white> [<status>]");
        DEFAULTS.put("history.empty", "<gray>No punishment history found for <yellow><player></yellow>.");
        DEFAULTS.put("history.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("history.type-filter", "<gray>Showing: <white><type></white> punishments");
        DEFAULTS.put("staffhistory.header", "<gold><strikethrough>     </strikethrough> Staff History for <yellow><staff></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("staffhistory.entry", "<gray>[<white><date></white>] <yellow><type></yellow> on <yellow><player></yellow>: <white><reason></white> - <white><duration></white> [<status>]");
        DEFAULTS.put("staffhistory.empty", "<gray>No staff history found for <yellow><staff></yellow>.");
        DEFAULTS.put("staffhistory.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("warnings.header", "<gold><strikethrough>     </strikethrough> Active Warnings for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("warnings.entry", "<gray>[<white><date></white>] <white><reason></white> <gray>by <yellow><staff></yellow> <gray>- Expires: <white><expiry>");
        DEFAULTS.put("warnings.empty", "<gray>No active warnings found.");
        DEFAULTS.put("warnings.self-empty", "<green>You have no active warnings.");
        DEFAULTS.put("warnings.count", "<yellow>Total warnings: <white><count>");
        DEFAULTS.put("banlist.header", "<gold><strikethrough>     </strikethrough> Active Bans <strikethrough>     </strikethrough>");
        DEFAULTS.put("banlist.entry", "<gray><white><player></white> <gray>by <yellow><staff></yellow> <gray>- <white><reason></white> <gray>(<white><duration></white>)");
        DEFAULTS.put("banlist.empty", "<gray>No active bans found.");
        DEFAULTS.put("banlist.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("mutelist.header", "<gold><strikethrough>     </strikethrough> Active Mutes <strikethrough>     </strikethrough>");
        DEFAULTS.put("mutelist.entry", "<gray><white><player></white> <gray>by <yellow><staff></yellow> <gray>- <white><reason></white> <gray>(<white><duration></white>)");
        DEFAULTS.put("mutelist.empty", "<gray>No active mutes found.");
        DEFAULTS.put("mutelist.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("warnlist.header", "<gold><strikethrough>     </strikethrough> Active Warnings <strikethrough>     </strikethrough>");
        DEFAULTS.put("warnlist.entry", "<gray><white><player></white> <gray>by <yellow><staff></yellow> <gray>- <white><reason></white> <gray>(Expires: <white><expiry></white>)");
        DEFAULTS.put("warnlist.empty", "<gray>No active warnings found.");
        DEFAULTS.put("warnlist.footer", "<gold><strikethrough>     </strikethrough> Page <yellow><page></yellow>/<yellow><total></yellow> <strikethrough>     </strikethrough>");

        // Account & IP Commands
        DEFAULTS.put("dupeip.header", "<gold><strikethrough>     </strikethrough> Alt Accounts for <yellow><target></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("dupeip.entry", "<gray>- <white><player></white> <gray>(<white><uuid></white>)");
        DEFAULTS.put("dupeip.ip-entry", "<gray>- <white><ip></white>");
        DEFAULTS.put("dupeip.empty", "<gray>No alt accounts found.");
        DEFAULTS.put("dupeip.count", "<yellow>Total accounts: <white><count>");
        DEFAULTS.put("dupeip.shared-ip", "<gray>Shared IP: <white><ip>");
        DEFAULTS.put("iphistory.header", "<gold><strikethrough>     </strikethrough> IP History for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("iphistory.entry", "<gray>[<white><date></white>] <white><ip></white>");
        DEFAULTS.put("iphistory.empty", "<gray>No IP history found for <yellow><player></yellow>.");
        DEFAULTS.put("iphistory.count", "<yellow>Total IPs: <white><count>");
        DEFAULTS.put("iphistory.console-only", "<red>This command can only be executed from console by default due to privacy concerns.");
        DEFAULTS.put("ipreport.header", "<gold><strikethrough>     </strikethrough> Duplicate IP Report <strikethrough>     </strikethrough>");
        DEFAULTS.put("ipreport.entry", "<gray>IP <white><ip></white> <gray>is shared by:");
        DEFAULTS.put("ipreport.player-entry", "<gray>  - <yellow><player></yellow>");
        DEFAULTS.put("ipreport.empty", "<green>No duplicate IPs detected among online players.");
        DEFAULTS.put("ipreport.count", "<yellow>Total duplicate IPs: <white><count>");
        DEFAULTS.put("geoip.header", "<gold><strikethrough>     </strikethrough> GeoIP for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("geoip.country", "<gray>Country: <white><country></white> <flag>");
        DEFAULTS.put("geoip.region", "<gray>Region: <white><region>");
        DEFAULTS.put("geoip.city", "<gray>City: <white><city>");
        DEFAULTS.put("geoip.ip", "<gray>IP: <white><ip>");
        DEFAULTS.put("geoip.not-available", "<red>GeoIP information not available for <yellow><player></yellow>.");
        DEFAULTS.put("geoip.database-missing", "<red>GeoIP database not configured. Please configure GeoIP in config.yml.");
        DEFAULTS.put("lastuuid.format", "<gray>UUID for <yellow><player></yellow>: <white><uuid>");
        DEFAULTS.put("lastuuid.not-found", "<red>Could not find UUID for <yellow><player></yellow>.");
        DEFAULTS.put("namehistory.header", "<gold><strikethrough>     </strikethrough> Name History for <yellow><player></yellow> <strikethrough>     </strikethrough>");
        DEFAULTS.put("namehistory.entry", "<gray>[<white><date></white>] <white><name>");
        DEFAULTS.put("namehistory.current", "<gray>[Current] <white><name>");
        DEFAULTS.put("namehistory.empty", "<gray>No name history found for <yellow><player></yellow>.");
        DEFAULTS.put("namehistory.not-available", "<red>Name history service is currently unavailable.");

        // Admin Commands
        DEFAULTS.put("lockdown.enabled", "<red><bold>LOCKDOWN ENABLED</bold></red> <gray>- Server is now in lockdown mode.");
        DEFAULTS.put("lockdown.disabled", "<green><bold>LOCKDOWN DISABLED</bold></green> <gray>- Server lockdown has been lifted.");
        DEFAULTS.put("lockdown.broadcast-enabled", "<red>[Lockdown]</red> <gray><staff> enabled lockdown mode <gray>(<white><scope></white>)");
        DEFAULTS.put("lockdown.broadcast-disabled", "<green>[Lockdown]</green> <gray><staff> disabled lockdown mode <gray>(<white><scope></white>)");
        DEFAULTS.put("lockdown.kick-message", "<gradient:#FF6B6B:#4ECDC4>[ModereX]</gradient>\n\n<red><bold>Server is in Lockdown</bold></red>\n\n<gray>The server is temporarily in lockdown mode.\n<gray>Please try again later.");
        DEFAULTS.put("lockdown.already-enabled", "<red>Lockdown is already enabled.");
        DEFAULTS.put("lockdown.already-disabled", "<red>Lockdown is already disabled.");
        DEFAULTS.put("lockdown.status", "<gray>Lockdown status: <status>");
        DEFAULTS.put("lockdown.scope-local", "<yellow>Local");
        DEFAULTS.put("lockdown.scope-global", "<gold>Global");
        DEFAULTS.put("prunehistory.success", "<green>Successfully pruned <white><count></white> inactive punishment(s) for <yellow><player></yellow>.");
        DEFAULTS.put("prunehistory.confirm", "<yellow>This will remove <white><count></white> inactive punishment(s) for <yellow><player></yellow>. Use --confirm to proceed.");
        DEFAULTS.put("prunehistory.empty", "<gray>No inactive punishments found for <yellow><player></yellow>.");
        DEFAULTS.put("prunehistory.duration-info", "<gray>Pruning punishments older than: <white><duration>");
        DEFAULTS.put("staffrollback.success", "<green>Successfully rolled back <white><count></white> punishment(s) by <yellow><staff></yellow>.");
        DEFAULTS.put("staffrollback.confirm", "<yellow>This will rollback <white><count></white> punishment(s) by <yellow><staff></yellow>. Use --confirm to proceed.");
        DEFAULTS.put("staffrollback.empty", "<gray>No punishments found by <yellow><staff></yellow>.");
        DEFAULTS.put("staffrollback.duration-info", "<gray>Rolling back punishments from the last: <white><duration>");
        DEFAULTS.put("staffrollback.broadcast", "<gray><executor> rolled back <white><count></white> punishment(s) by <yellow><staff></yellow>.");

        // ModereX Subcommands
        DEFAULTS.put("moderex.help-header", "<gold><strikethrough>          </strikethrough> ModereX Commands <strikethrough>          </strikethrough>");
        DEFAULTS.put("moderex.help-entry", "<yellow>/<command></yellow> <gray>- <white><description>");
        DEFAULTS.put("moderex.help-footer", "<gold><strikethrough>                                    </strikethrough>");

        // Punishment Flags
        DEFAULTS.put("flags.delete-success", "<green>Successfully deleted punishment with ID <yellow><id></yellow>.");
        DEFAULTS.put("flags.delete-reverted-template", "<gray>Template progression has been reverted for <yellow><player></yellow>.");
        DEFAULTS.put("flags.modify-success", "<green>Successfully modified punishment with ID <yellow><id></yellow>.");
        DEFAULTS.put("flags.no-override", "<yellow>Did not override existing punishment for <yellow><player></yellow> (use without -N to override).");
        DEFAULTS.put("flags.global", "<gray>(Global)");
        DEFAULTS.put("flags.silent", "<gray>(Silent)");
        DEFAULTS.put("flags.extra-silent", "<gray>(Extra Silent)");
        DEFAULTS.put("flags.hidden", "<gray>(Hidden)");

        // Target Resolution
        DEFAULTS.put("target.invalid", "<red>Invalid target format. Use player name, UUID, IP address, or punishment ID.");
        DEFAULTS.put("target.not-found", "<red>Could not find target: <white><target>");
        DEFAULTS.put("target.offline-no-ip", "<red><player> is not online. Cannot resolve IP address.");
        DEFAULTS.put("target.multiple-found", "<yellow>Multiple players found matching <white><target></white>. Please be more specific.");

        // Errors
        DEFAULTS.put("error.database", "<red>A database error occurred. Please contact an administrator.");
        DEFAULTS.put("error.internal", "<red>An internal error occurred. Please contact an administrator.");
        DEFAULTS.put("error.no-permission-flag", "<red>You don't have permission to use the flag: <white><flag>");
        DEFAULTS.put("error.console-only-flag", "<red>The flag <white><flag></white> can only be used from console.");
        DEFAULTS.put("error.invalid-page", "<red>Invalid page number. Use a number between 1 and <white><max></white>.");
        DEFAULTS.put("error.player-not-online", "<red>Player <yellow><player></yellow> is not online.");
        DEFAULTS.put("error.invalid-ip", "<red>Invalid IP address format.");
        DEFAULTS.put("error.invalid-uuid", "<red>Invalid UUID format.");
        DEFAULTS.put("error.punishment-not-found", "<red>Punishment not found with ID: <white><id>");
        DEFAULTS.put("error.no-active-punishment", "<red><player> does not have an active <type> punishment.");

        // AFK
        DEFAULTS.put("afk.kick-message", "<red>You were kicked for being AFK.");
        DEFAULTS.put("afk.kick-reason", "AFK - Inactive for too long");
        DEFAULTS.put("afk.kick-broadcast", "<gray>[<red>AFK</red>] <yellow><player></yellow> <gray>was kicked for being AFK.");

        // Replay
        DEFAULTS.put("replay.not-available", "<red>Replay playback requires the Citizens plugin to be installed.");
        DEFAULTS.put("replay.started", "<green>Replay playback started for <yellow><player></yellow>.");
        DEFAULTS.put("replay.stopped", "<gray>Replay playback ended.");
        DEFAULTS.put("replay.not-found", "<red>No replay found for <yellow><player></yellow>.");
        DEFAULTS.put("replay.paused", "<yellow>Replay paused.");
        DEFAULTS.put("replay.resumed", "<green>Replay resumed.");
    }

    public LanguageManager(ModereX plugin) {
        this.plugin = plugin;
        this.loadedLocales = new HashMap<>();
    }

    public void load() {
        // Clear cached locales on reload
        loadedLocales.clear();

        // Create messages folder
        File messagesFolder = new File(plugin.getDataFolder(), "messages");
        if (!messagesFolder.exists()) {
            messagesFolder.mkdirs();
        }

        // Save default language files from resources
        saveDefaultLanguages();

        // Load English as the fallback (always loaded)
        File enFile = new File(plugin.getDataFolder(), "messages/en_US.yml");
        if (!enFile.exists()) {
            createDefaultEnglish(enFile);
        }
        englishFallback = YamlConfiguration.loadConfiguration(enFile);

        // Load embedded English resource as secondary fallback for new keys added in updates
        InputStream embeddedEn = plugin.getResource("messages/en_US.yml");
        if (embeddedEn != null) {
            YamlConfiguration embedded = YamlConfiguration.loadConfiguration(new InputStreamReader(embeddedEn));
            englishFallback.setDefaults(embedded);
        }

        loadedLocales.put("en_US", englishFallback);

        // Load current language from config
        currentLanguage = plugin.getConfigManager().getSettings().getLanguage();
        loadLanguage(currentLanguage);
    }

    private void saveDefaultLanguages() {
        for (String lang : BUNDLED_LANGUAGES) {
            File langFile = new File(plugin.getDataFolder(), "messages/" + lang + ".yml");
            if (!langFile.exists()) {
                // Try to save from resources
                String resourcePath = "messages/" + lang + ".yml";
                if (plugin.getResource(resourcePath) != null) {
                    plugin.saveResource(resourcePath, false);
                } else if (lang.equals("en_US")) {
                    // Create default English file from DEFAULTS map
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
            currentMessages = englishFallback;
            currentLanguage = "en_US";
            return;
        }

        currentMessages = YamlConfiguration.loadConfiguration(langFile);
        loadedLocales.put(language, currentMessages);
        currentLanguage = language;

        plugin.getLogger().info("Loaded language: " + language + " (" + getLanguageDisplayName(language) + ")");
    }

    /**
     * Get a raw message string by MessageKey.
     * Lookup order: current locale -> English fallback file -> hardcoded DEFAULTS
     */
    public String getRaw(MessageKey key) {
        return getRaw(key.getPath());
    }

    /**
     * Get a raw message string by path.
     * Lookup order: current locale -> English fallback file -> hardcoded DEFAULTS
     */
    public String getRaw(String path) {
        // Try current language first
        String message = currentMessages.getString(path);

        // Fall back to English file if not found in current language
        if (message == null && englishFallback != null && currentMessages != englishFallback) {
            message = englishFallback.getString(path);
        }

        // Fall back to hardcoded defaults
        if (message == null) {
            message = DEFAULTS.getOrDefault(path, "<red>Missing message: " + path);
        }

        return message;
    }

    /**
     * Get a parsed Component from a MessageKey (MiniMessage formatted).
     */
    public Component get(MessageKey key) {
        return TextUtil.parse(getRaw(key));
    }

    /**
     * Get a parsed Component with placeholder replacements.
     * Placeholders use angle brackets: <player>, <reason>, etc.
     */
    public Component get(MessageKey key, String... replacements) {
        String message = getRaw(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace("<" + replacements[i] + ">", replacements[i + 1]);
        }
        return TextUtil.parse(message);
    }

    /**
     * Get the configured prefix as a Component.
     */
    public Component getPrefix() {
        return TextUtil.parse(getRaw(MessageKey.PREFIX));
    }

    /**
     * Get a message prefixed with the configured prefix.
     */
    public Component getPrefixed(MessageKey key, String... replacements) {
        return getPrefix().append(get(key, replacements));
    }

    /**
     * Get the current language code (e.g., "en_US").
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Switch to a different language at runtime.
     */
    public void setLanguage(String language) {
        loadLanguage(language);
    }

    /**
     * Check if a language file exists in the messages folder.
     */
    public boolean isLanguageAvailable(String language) {
        File langFile = new File(plugin.getDataFolder(), "messages/" + language + ".yml");
        return langFile.exists();
    }

    /**
     * Get a set of all available locale codes found in the messages folder.
     * Scans for .yml files and returns their names without extension.
     */
    public Set<String> getAvailableLocales() {
        Set<String> locales = new LinkedHashSet<>();
        File messagesFolder = new File(plugin.getDataFolder(), "messages");
        if (messagesFolder.exists() && messagesFolder.isDirectory()) {
            File[] files = messagesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().replace(".yml", "");
                    locales.add(name);
                }
            }
        }
        return locales;
    }

    /**
     * Get the human-readable display name for a language code.
     * Returns the code itself if no display name is registered.
     */
    public String getLanguageDisplayName(String languageCode) {
        return LANGUAGE_NAMES.getOrDefault(languageCode, languageCode);
    }

    /**
     * Get the map of all registered language display names.
     */
    public Map<String, String> getLanguageNames() {
        return Collections.unmodifiableMap(LANGUAGE_NAMES);
    }

    /**
     * Get all bundled language codes shipped with the plugin.
     */
    public String[] getBundledLanguages() {
        return BUNDLED_LANGUAGES.clone();
    }
}
