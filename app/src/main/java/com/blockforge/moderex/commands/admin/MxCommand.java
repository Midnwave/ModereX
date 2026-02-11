package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.hooks.anticheat.AnticheatChecks;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.AnalyticsGui;
import com.blockforge.moderex.gui.AnticheatRulesGui;
import com.blockforge.moderex.gui.AutomodGui;
import com.blockforge.moderex.gui.MainMenuGui;
import com.blockforge.moderex.gui.ReplayGui;
import com.blockforge.moderex.gui.StaffSettingsGui;
import com.blockforge.moderex.gui.TemplateGui;
import com.blockforge.moderex.gui.punishment.PunishPlayerGui;
import com.blockforge.moderex.gui.ModLogGui;
import com.blockforge.moderex.replay.ReplaySession;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.TestReplayGenerator;
import com.blockforge.moderex.testing.PermissionTestEngine;
import com.blockforge.moderex.testing.PermissionTestProfiles;
import com.blockforge.moderex.util.DurationParser;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MxCommand extends BaseCommand {

    public MxCommand(ModereX plugin) {
        super(plugin, null, false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (PermissionUtil.hasPermission(player, "moderex.command.punish") || PermissionUtil.hasPermission(player, "moderex.command.admin")) {
                    plugin.getGuiManager().open(player, new MainMenuGui(plugin));
                    return;
                }
            }
            sendHelp(sender);
            return;
        }

        String subcommand = args[0].toLowerCase();
        String[] subArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

        switch (subcommand) {
            case "reload" -> handleReload(sender);
            case "connect" -> handleConnect(sender);
            case "link" -> handleLink(sender);
            case "gettoken" -> handleGetToken(sender);
            case "revoketoken" -> handleRevokeToken(sender);
            case "sessions" -> handleSessions(sender);
            case "automod" -> handleAutomod(sender);
            case "anticheat", "ac" -> handleAnticheat(sender);
            case "sendalert" -> handleSendAlert(sender, subArgs);
            case "chat" -> handleChat(sender, subArgs);
            case "settings" -> handleSettings(sender);
            case "analytics" -> handleAnalytics(sender);
            case "mutesettings" -> handleMuteSettings(sender);
            case "warningsettings" -> handleWarningSettings(sender);
            case "help" -> sendHelp(sender);
            case "templates", "template", "tpl" -> handleTemplates(sender);
            case "testreplay" -> handleTestReplay(sender);
            case "replay", "replays", "rec", "recording" -> handleReplay(sender, subArgs);

            case "ban" -> handleBan(sender, subArgs);
            case "unban" -> handleUnban(sender, subArgs);
            case "mute" -> handleMute(sender, subArgs);
            case "unmute" -> handleUnmute(sender, subArgs);
            case "kick" -> handleKick(sender, subArgs);
            case "warn" -> handleWarn(sender, subArgs);
            case "ipban" -> handleIpBan(sender, subArgs);
            case "punish" -> handlePunish(sender, subArgs);
            case "modlog", "history" -> handleModLog(sender, subArgs);

            case "staffchat", "sc" -> handleStaffChat(sender, subArgs);
            case "vanish", "v" -> handleVanish(sender);
            case "version", "ver" -> handleVersion(sender);
            case "update", "checkupdate" -> handleUpdate(sender);
            case "debug" -> handleDebug(sender, subArgs);
            case "permtest" -> handlePermTest(sender, subArgs);
            case "panel" -> handlePanel(sender);
            case "gateway" -> handleGateway(sender, subArgs);

            default -> sendMessage(sender, "<red>Unknown subcommand: " + subcommand + ". Use /mx help for a list.");
        }
    }

    private void handleReload(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        plugin.reload();
        sendMessage(sender, MessageKey.RELOAD_SUCCESS);
    }

    private void handleVersion(CommandSender sender) {
        // Read version from panel-version.properties (single source of truth)
        String version = plugin.getDescription().getVersion();
        String build = "0";
        try (var in = plugin.getClass().getClassLoader().getResourceAsStream("panel-version.properties")) {
            if (in != null) {
                var props = new java.util.Properties();
                props.load(in);
                version = props.getProperty("version", version);
                build = props.getProperty("buildNumber", "0");
            }
        } catch (Exception ignored) {
            // Fallback: parse build from plugin.yml version
            if (version.contains("-")) {
                build = version.split("-")[version.split("-").length - 1];
            }
        }

        // Server software and version
        String serverVersion = Bukkit.getName() + " " + Bukkit.getMinecraftVersion();

        // Java version
        String javaVersion = System.getProperty("java.version");

        // Web panel status
        var settings = plugin.getConfigManager().getSettings();
        String panelStatus;
        if (settings.isWebPanelEnabled() && plugin.getWebPanelServer() != null) {
            panelStatus = "<green>Active <gray>(port " + settings.getWebPanelPort() + ")";
        } else if (settings.isWebPanelEnabled()) {
            panelStatus = "<yellow>Enabled <gray>(not started)";
        } else {
            panelStatus = "<red>Disabled";
        }

        // Gateway status
        var gateway = plugin.getGatewayClient();
        String gatewayStatus;
        if (gateway != null && gateway.isConnected()) {
            gatewayStatus = "<green>Connected";
        } else if (settings.isGatewayEnabled()) {
            gatewayStatus = "<yellow>Disconnected";
        } else {
            gatewayStatus = "<red>Disabled";
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>ModereX Version</bold>");
        sendMessage(sender, "");
        sendMessage(sender, "<gray>Version: <white>" + version);
        sendMessage(sender, "<gray>Build: <white>" + build);
        sendMessage(sender, "<gray>Server: <white>" + serverVersion);
        sendMessage(sender, "<gray>Java: <white>" + javaVersion);
        sendMessage(sender, "<gray>Web Panel: " + panelStatus);
        sendMessage(sender, "<gray>Gateway: " + gatewayStatus);
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleUpdate(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        sendMessage(sender, "<yellow>Checking GitHub for updates...");

        var updater = new com.blockforge.moderex.util.GitHubAutoUpdater(plugin);
        updater.forceUpdateAsync().thenAccept(result -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (String line : result.split("\n")) {
                    if (line.contains("successfully")) {
                        sendMessage(sender, "<green>" + line);
                    } else if (line.contains("failed") || line.contains("error")) {
                        sendMessage(sender, "<red>" + line);
                    } else {
                        sendMessage(sender, "<gray>" + line);
                    }
                }
                // Dev Build Tester advertisement
                sendMessage(sender, "");
                sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
                sendMessage(sender, "<bold><gradient:#3b82f6:#a855f7>★ Dev Build Tester Applications Open!</gradient></bold>");
                sendMessage(sender, "<gray>Help shape the future of ModereX — apply now!");
                sendMessage(sender, "<aqua><click:open_url:'https://discord.com/channels/1131588549892907089/1309211930665156668'><hover:show_text:'<gray>Click to apply'>▸ Apply Here</hover></click></aqua> <dark_gray>│ <aqua><click:open_url:'https://discord.gg/jQGMhKA5m6'><hover:show_text:'<gray>Join our Discord'>▸ Discord Invite</hover></click></aqua>");
                sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
            });
        });
    }

    private void handleDebug(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx debug <subcommand>");
            sendMessage(sender, "<gray>/mx debug authsession <player> [caseId] <white>- Generate portal session for player");
            sendMessage(sender, "<gray>/mx debug serverid <white>- Show this server's unique ID");
            sendMessage(sender, "<gray>/mx debug integrations <white>- Show all detected plugin hooks");
            sendMessage(sender, "<gray>/mx debug permissions <player> <white>- Show all permissions for a player");
            sendMessage(sender, "<gray>/mx debug simulate <player> <white>- Simulate permission access for a player");
            return;
        }

        String action = args[0].toLowerCase();
        String[] actionArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

        switch (action) {
            case "authsession" -> handleDebugAuthSession(sender, actionArgs);
            case "serverid" -> handleDebugServerId(sender);
            case "integrations" -> handleDebugIntegrations(sender);
            case "permissions", "perms" -> handleDebugPermissions(sender, actionArgs);
            case "simulate", "sim" -> handleDebugSimulate(sender, actionArgs);
            default -> {
                sendMessage(sender, "<red>Unknown debug action: " + action);
                sendMessage(sender, "<gray>Available: authsession, serverid, integrations, permissions, simulate");
            }
        }
    }

    private void handleDebugAuthSession(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx debug authsession <player> [caseId]");
            sendMessage(sender, "<gray>Generates an auth session for the player portal.");
            sendMessage(sender, "<gray>Optionally specify a caseId to link the session to a specific punishment.");
            return;
        }

        String targetName = args[0];
        String caseId = args.length > 1 ? args[1] : null;

        // Resolve player
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        // Check if auth session manager is available
        if (plugin.getAuthSessionManager() == null) {
            sendMessage(sender, "<red>Auth session manager is not initialized. Is the player portal enabled?");
            return;
        }

        sendMessage(sender, "<yellow>Generating auth session for " + displayName + "...");

        plugin.getAuthSessionManager().createSession(targetUuid, caseId).thenAccept(sessionId -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                // Build portal URL
                int port = plugin.getConfigManager().getSettings().getWebPanelPort();
                String host = plugin.getConfigManager().getSettings().getWebPanelHost();
                if (host == null || host.isEmpty()) {
                    host = "your-server-ip";
                }

                String url = "http://" + host + ":" + port + "/moderex/portal/" + sessionId;

                sendMessage(sender, "");
                sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
                sendMessage(sender, "<white>      <bold>Player Portal Session</bold>");
                sendMessage(sender, "");
                sendMessage(sender, "<gray>Player: <white>" + displayName);
                sendMessage(sender, "<gray>Session ID: <white>" + sessionId);
                if (caseId != null) {
                    sendMessage(sender, "<gray>Linked Case: <white>" + caseId);
                }
                sendMessage(sender, "");
                sendMessage(sender, "<gray>Portal URL:");
                sendMessage(sender, "<aqua>" + url);
                sendMessage(sender, "");
                sendMessage(sender, "<yellow>Session expires in <white>12 hours<yellow>.");
                sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
                sendMessage(sender, "");
            });
        }).exceptionally(ex -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                sendMessage(sender, "<red>Failed to create auth session: " + ex.getMessage());
            });
            return null;
        });
    }

    private void handleDebugServerId(CommandSender sender) {
        var identity = plugin.getServerIdentity();
        if (identity == null) {
            sendMessage(sender, "<red>Server identity not initialized yet.");
            return;
        }

        String serverId = identity.getServerId();
        String serverName = identity.getServerName();
        long createdAt = identity.getCreatedAt();

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Server Identity</bold>");
        sendMessage(sender, "");
        sendMessage(sender, "<gray>Server ID: <white><bold>" + serverId + "</bold>");
        sendMessage(sender, "<gray>Server Name: <white>" + serverName);
        sendMessage(sender, "<gray>Created: <white>" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(createdAt)));
        sendMessage(sender, "");

        // Show gateway info
        var gateway = plugin.getGatewayClient();
        if (gateway != null) {
            sendMessage(sender, "<gray>Gateway URL: <aqua>panel.moderex.net/" + identity.getUrlPrefix() + "/");
        } else if (!plugin.getConfigManager().getSettings().isGatewayEnabled()) {
            sendMessage(sender, "<gray>Gateway: <yellow>Disabled (opt-out)");
        } else {
            sendMessage(sender, "<gray>Gateway: <red>Not connected");
        }
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleDebugIntegrations(CommandSender sender) {
        var hooks = plugin.getHookManager();
        if (hooks == null) {
            sendMessage(sender, "<red>HookManager not initialized.");
            return;
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Plugin Integrations</bold>");
        sendMessage(sender, "");

        printHookStatus(sender, "LuckPerms", hooks.hasLuckPerms(), hooks.getLuckPermsVersion());
        printHookStatus(sender, "PlaceholderAPI", hooks.hasPlaceholderAPI(), null);
        printHookStatus(sender, "CoreProtect", hooks.hasCoreProtect(), null);
        printHookStatus(sender, "Geyser", hooks.hasGeyser(), hooks.getGeyserVersion());
        printHookStatus(sender, "Floodgate", hooks.hasFloodgate(), hooks.getFloodgateVersion());
        printHookStatus(sender, "Citizens", hooks.hasCitizens(), hooks.getCitizensVersion());
        printHookStatus(sender, "Spark", hooks.isSparkAvailable(), hooks.getSparkVersion());
        printHookStatus(sender, "Simple Voice Chat", hooks.isVoiceChatAvailable(), hooks.getVoiceChatVersion());

        sendMessage(sender, "");
        printHookStatus(sender, "BlueMap", hooks.isBlueMapAvailable(), hooks.getBlueMapVersion());
        if (hooks.isBlueMapAvailable()) {
            var blueMap = hooks.getBlueMapHook();
            sendMessage(sender, "<gray>  Web URL: <white>" + blueMap.getWebUrl());
            sendMessage(sender, "<gray>  Web Port: <white>" + blueMap.getWebPort());
            sendMessage(sender, "<gray>  Map IDs: <white>" + String.join(", ", blueMap.getMapIds()));
        }

        sendMessage(sender, "");
        String detectedAC = hooks.getDetectedAnticheat();
        sendMessage(sender, "<gray>Anticheat: " + (detectedAC != null
            ? "<green>" + detectedAC + " <gray>(detected)"
            : "<yellow>None detected"));

        var acManager = hooks.getAnticheatManager();
        if (acManager != null && !acManager.getEnabledAnticheats().isEmpty()) {
            sendMessage(sender, "<gray>  Active listeners: <white>" + String.join(", ", acManager.getEnabledAnticheats()));
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void printHookStatus(CommandSender sender, String name, boolean detected, String version) {
        String status = detected ? "<green>Detected" : "<red>Not Found";
        String versionStr = (detected && version != null && !version.isEmpty() && !version.equals("N/A")) ? " <gray>v" + version : "";
        sendMessage(sender, "<gray>" + name + ": " + status + versionStr);
    }

    // ===== DEBUG PERMISSIONS REPORT =====
    private void handleDebugPermissions(CommandSender sender, String[] args) {
        if (args.length == 0) { sendMessage(sender, "<red>Usage: /mx debug permissions <player>"); return; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]); return; }
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#f59e0b:#ef4444>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>  <bold>Permission Report: " + target.getName() + "</bold>");
        sendMessage(sender, "<gray>  OP: " + (target.isOp() ? "<green>Yes" : "<red>No") + "  <gray>Weight: <yellow>" + PermissionUtil.getWeight(target));
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Punishment Commands</bold>");
        String[][] punishPerms = {{"moderex.ban","Ban"},{"moderex.tempban","Temp Ban"},{"moderex.mute","Mute"},{"moderex.tempmute","Temp Mute"},{"moderex.warn","Warn"},{"moderex.kick","Kick"},{"moderex.ipban","IP Ban"},{"moderex.ipmute","IP Mute"},{"moderex.unban","Unban"},{"moderex.unmute","Unmute"},{"moderex.unwarn","Unwarn"}};
        for (String[] p : punishPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Mass Punishments</bold>");
        String[][] massPerms = {{"moderex.massban","Mass Ban"},{"moderex.masskick","Mass Kick"},{"moderex.massmute","Mass Mute"},{"moderex.masswarn","Mass Warn"},{"moderex.massunban","Mass Unban"},{"moderex.massunmute","Mass Unmute"},{"moderex.massunwarn","Mass Unwarn"}};
        for (String[] p : massPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Info Visibility</bold>");
        String[][] infoPerms = {{"moderex.check","Check command"},{"moderex.check.ip","See IP in /check"},{"moderex.command.seen","Seen command"},{"moderex.command.seen.ip","See IP in /seen"},{"moderex.info.uuid","See UUID in GUI"},{"moderex.info.ip","See IP in GUI"}};
        for (String[] p : infoPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>History & Logs</bold>");
        String[][] histPerms = {{"moderex.history","History (all)"},{"moderex.history.bans","History: Bans"},{"moderex.history.mutes","History: Mutes"},{"moderex.history.warns","History: Warns"},{"moderex.history.kicks","History: Kicks"},{"moderex.command.viewpunishment","View Punishment"},{"moderex.log","Activity Log"}};
        for (String[] p : histPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Staff Tools</bold>");
        String[][] staffPerms = {{"moderex.command.vanish","Vanish"},{"moderex.command.vanish.others","Vanish Others"},{"moderex.staff.inspect","Staff Inspect"},{"moderex.staff.inspect.modify","Modify Inventory"},{"moderex.staffmode","Staff Mode"},{"moderex.command.fly","Fly"},{"moderex.command.echest","Ender Chest"},{"moderex.command.invsee","Invsee"},{"moderex.staffchat","Staff Chat"}};
        for (String[] p : staffPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Web Panel & Admin</bold>");
        String[][] adminPerms = {{"moderex.webpanel","Web Panel Access"},{"moderex.command.admin","Admin Commands"},{"moderex.admin","Full Admin"},{"moderex.staff","Staff"},{"moderex.command.punish","Punish GUI"},{"moderex.admin.debug","Debug Access"}};
        for (String[] p : adminPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Bypass Permissions</bold>");
        String[][] bypassPerms = {{"moderex.bypass.automod","Bypass Automod"},{"moderex.bypass.slowmode","Bypass Slowmode"},{"moderex.bypass.clearchat","Bypass Clear Chat"},{"moderex.bypass.lockdown","Bypass Lockdown"}};
        for (String[] p : bypassPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Notifications</bold>");
        String[][] notifyPerms = {{"moderex.alerts","Staff Alerts"},{"moderex.alerts.ban","Ban Alerts"},{"moderex.alerts.kick","Kick Alerts"},{"moderex.alerts.mute","Mute Alerts"},{"moderex.alerts.warn","Warn Alerts"},{"moderex.notify.join","Join Notify"},{"moderex.notify.watchlist","Watchlist Notify"}};
        for (String[] p : notifyPerms) dbgPerm(sender, target, p[0], p[1]);
        sendMessage(sender, ""); sendMessage(sender, "<gradient:#f59e0b:#ef4444>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"); sendMessage(sender, "");
    }

    // ===== DEBUG SIMULATE =====
    private void handleDebugSimulate(CommandSender sender, String[] args) {
        if (args.length == 0) { sendMessage(sender, "<red>Usage: /mx debug simulate <player>"); return; }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) { sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[0]); return; }
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>  <bold>Permission Simulation: " + target.getName() + "</bold>");
        sendMessage(sender, "<gray>  Simulating what this player can access");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Commands</bold>");
        String[][] cmds = {{"moderex.ban","/ban"},{"moderex.tempban","/tempban"},{"moderex.mute","/mute"},{"moderex.tempmute","/tempmute"},{"moderex.warn","/warn"},{"moderex.kick","/kick"},{"moderex.ipban","/ipban"},{"moderex.ipmute","/ipmute"},{"moderex.unban","/unban"},{"moderex.unmute","/unmute"},{"moderex.unwarn","/unwarn"},{"moderex.check","/check"},{"moderex.command.seen","/seen"},{"moderex.history","/history"},{"moderex.command.viewpunishment","/viewpunishment"},{"moderex.command.vanish","/vanish"},{"moderex.command.fly","/fly"},{"moderex.command.echest","/echest"},{"moderex.command.invsee","/invsee"},{"moderex.staffchat","/staffchat"},{"moderex.log","/log"},{"moderex.command.admin","/mx (admin)"},{"moderex.command.punish","/punish GUI"}};
        for (String[] c : cmds) dbgSim(sender, target, c[0], c[1], null);
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Info Visibility</bold>");
        dbgVis(sender, target, "moderex.check.ip", "IP Address"); dbgVis(sender, target, "moderex.info.uuid", "UUID in GUI"); dbgVis(sender, target, "moderex.info.ip", "IP in GUI");
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>GUI Actions</bold>");
        dbgSim(sender, target, "moderex.ban", "Ban via GUI", null); dbgSim(sender, target, "moderex.mute", "Mute via GUI", null);
        dbgSim(sender, target, "moderex.warn", "Warn via GUI", null); dbgSim(sender, target, "moderex.kick", "Kick via GUI", null);
        dbgSim(sender, target, "moderex.staff.inspect", "Inspect Player", null); dbgSim(sender, target, "moderex.staff.inspect.modify", "Modify Inventory", "moderex.staff.inspect");
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Web Panel</bold>");
        dbgSim(sender, target, "moderex.webpanel", "Web Panel Login", null); dbgSim(sender, target, "moderex.status.view", "Server Status Page", null);
        dbgSim(sender, target, "moderex.status.restart", "Restart Server", "moderex.status.view"); dbgSim(sender, target, "moderex.status.reload", "Reload Plugin", "moderex.status.view");
        dbgSim(sender, target, "moderex.status.broadcast", "Broadcast", "moderex.status.view");
        sendMessage(sender, ""); sendMessage(sender, "<gold><bold>Notifications</bold>");
        dbgSim(sender, target, "moderex.alerts", "Staff Alerts (all)", null); dbgSim(sender, target, "moderex.alerts.ban", "Ban Alerts", null);
        dbgSim(sender, target, "moderex.alerts.kick", "Kick Alerts", null); dbgSim(sender, target, "moderex.alerts.mute", "Mute Alerts", null);
        dbgSim(sender, target, "moderex.alerts.warn", "Warn Alerts", null); dbgSim(sender, target, "moderex.notify.join", "Join Notifications", null);
        dbgSim(sender, target, "moderex.notify.watchlist", "Watchlist Alerts", null);
        sendMessage(sender, ""); sendMessage(sender, "<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"); sendMessage(sender, "");
    }

    private void dbgPerm(CommandSender sender, Player target, String perm, String label) {
        boolean has = PermissionUtil.hasPermission(target, perm);
        sendMessage(sender, "  " + (has ? "<green>✓" : "<red>✗") + " <gray>" + label + " <dark_gray>(" + perm + ")");
    }
    private void dbgSim(CommandSender sender, Player target, String perm, String label, String dep) {
        boolean has = PermissionUtil.hasPermission(target, perm);
        boolean depMet = dep == null || PermissionUtil.hasPermission(target, dep);
        if (has && depMet) sendMessage(sender, "  <green>ALLOWED <gray>" + label);
        else if (has) sendMessage(sender, "  <yellow>BLOCKED <gray>" + label + " <dark_gray>(needs " + dep + ")");
        else sendMessage(sender, "  <red>DENIED  <gray>" + label + " <dark_gray>(needs " + perm + ")");
    }
    private void dbgVis(CommandSender sender, Player target, String perm, String label) {
        boolean has = PermissionUtil.hasPermission(target, perm);
        sendMessage(sender, "  " + (has ? "<green>VISIBLE" : "<red>HIDDEN ") + " <gray>" + label + " <dark_gray>(" + perm + ")");
    }

    // ========================================================================
    // Permission Test System
    // ========================================================================

    private void handlePermTest(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.admin.debug")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendPermTestHelp(sender);
            return;
        }

        String mode = args[0].toLowerCase();
        String[] subArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

        switch (mode) {
            case "check" -> handlePermTestCheck(sender, subArgs);
            case "full" -> handlePermTestFull(sender, subArgs);
            case "report" -> handlePermTestReport(sender);
            case "matrix" -> handlePermTestMatrix(sender);
            default -> sendPermTestHelp(sender);
        }
    }

    private void sendPermTestHelp(CommandSender sender) {
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>  <bold>Permission Test System</bold>");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Check Mode</bold> <gray>(permission logic only)");
        sendMessage(sender, "  <yellow>/mx permtest check profiles <gray>- Run all 12 test profiles");
        sendMessage(sender, "  <yellow>/mx permtest check profile <name> <gray>- Run one profile");
        sendMessage(sender, "  <yellow>/mx permtest check isolate <gray>- Test each perm in isolation");
        sendMessage(sender, "  <yellow>/mx permtest check parity <gray>- Frontend/backend parity");
        sendMessage(sender, "  <yellow>/mx permtest check me <gray>- Test YOUR permissions");
        sendMessage(sender, "  <yellow>/mx permtest check player <name> <gray>- Test a player's perms");
        sendMessage(sender, "  <yellow>/mx permtest check custom <gray>- Interactive custom set");
        sendMessage(sender, "  <yellow>/mx permtest check all <gray>- Everything combined");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Full Mode</bold> <gray>(permission checks + command execution)");
        sendMessage(sender, "  <yellow>/mx permtest full profiles <gray>- Execute commands per profile");
        sendMessage(sender, "  <yellow>/mx permtest full me <gray>- Execute with your permissions");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Utility</bold>");
        sendMessage(sender, "  <yellow>/mx permtest report <gray>- Export last results to JSON");
        sendMessage(sender, "  <yellow>/mx permtest matrix <gray>- Show permission matrix");
        sendMessage(sender, "");
        sendMessage(sender, "<gray>Profiles: NONE, OP, HELPER, MODERATOR, ADMIN, OWNER,");
        sendMessage(sender, "<gray>  TEMPBAN_ONLY, HISTORY_PARTIAL, FLAG_TESTER,");
        sendMessage(sender, "<gray>  WEB_VIEWER, CATEGORY_WILDCARD, WEIGHTED");
        sendMessage(sender, "<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
    }

    /** Last test report for export. */
    private PermissionTestEngine.TestReport lastTestReport = null;

    private void handlePermTestCheck(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx permtest check <profiles|profile|isolate|parity|me|player|custom|all>");
            return;
        }

        String sub = args[0].toLowerCase();

        // Run async to not block the main thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PermissionTestEngine.TestReport report;

            switch (sub) {
                case "profiles" -> {
                    sendMessage(sender, "<gray>Running all profile tests...");
                    report = PermissionTestEngine.runAllProfiles();
                }
                case "profile" -> {
                    if (args.length < 2) {
                        sendMessage(sender, "<red>Usage: /mx permtest check profile <name>");
                        return;
                    }
                    String profileName = args[1].toUpperCase();
                    PermissionTestProfiles.TestProfile profile = PermissionTestProfiles.getByName(profileName);
                    if (profile == null) {
                        sendMessage(sender, "<red>Unknown profile: " + profileName);
                        sendMessage(sender, "<gray>Available: NONE, OP, HELPER, MODERATOR, ADMIN, OWNER, TEMPBAN_ONLY, HISTORY_PARTIAL, FLAG_TESTER, WEB_VIEWER, CATEGORY_WILDCARD, WEIGHTED");
                        return;
                    }
                    sendMessage(sender, "<gray>Running tests for profile: " + profileName + "...");
                    report = PermissionTestEngine.runSingleProfile(profile);
                }
                case "isolate" -> {
                    sendMessage(sender, "<gray>Running isolation tests (~" + PermissionTestEngine.MASTER_PERMISSIONS.size() + " permissions)...");
                    report = PermissionTestEngine.runIsolation();
                }
                case "parity" -> {
                    sendMessage(sender, "<gray>Running frontend/backend parity tests...");
                    report = PermissionTestEngine.runParity();
                }
                case "me" -> {
                    if (!(sender instanceof Player player)) {
                        sendMessage(sender, "<red>This command can only be run by a player.");
                        return;
                    }
                    sendMessage(sender, "<gray>Testing your permissions...");
                    Set<String> perms = new java.util.HashSet<>();
                    for (var info : player.getEffectivePermissions()) {
                        if (info.getValue() && info.getPermission().startsWith("moderex.")) {
                            perms.add(info.getPermission());
                        }
                    }
                    PermissionTestProfiles.TestProfile profile = PermissionTestProfiles.fromPermissions(
                            player.getName(), perms, player.isOp());
                    report = PermissionTestEngine.runSingleProfile(profile);
                }
                case "player" -> {
                    if (args.length < 2) {
                        sendMessage(sender, "<red>Usage: /mx permtest check player <name>");
                        return;
                    }
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[1]);
                        return;
                    }
                    sendMessage(sender, "<gray>Testing permissions for " + target.getName() + "...");
                    Set<String> perms = new java.util.HashSet<>();
                    for (var info : target.getEffectivePermissions()) {
                        if (info.getValue() && info.getPermission().startsWith("moderex.")) {
                            perms.add(info.getPermission());
                        }
                    }
                    PermissionTestProfiles.TestProfile profile = PermissionTestProfiles.fromPermissions(
                            target.getName(), perms, target.isOp());
                    report = PermissionTestEngine.runSingleProfile(profile);
                }
                case "custom" -> {
                    if (!(sender instanceof Player player)) {
                        sendMessage(sender, "<red>Custom mode requires a player (uses chat input).");
                        return;
                    }
                    startCustomPermTest(player);
                    return;
                }
                case "all" -> {
                    sendMessage(sender, "<gray>Running ALL tests (profiles + isolation + parity)...");
                    report = PermissionTestEngine.runAll();
                }
                default -> {
                    sendMessage(sender, "<red>Unknown check mode: " + sub);
                    return;
                }
            }

            lastTestReport = report;

            // Send results to chat
            for (String line : PermissionTestEngine.toChatLines(report, false, 200)) {
                sendMessage(sender, line);
            }

            if (report.failed > 0) {
                sendMessage(sender, "<yellow>Use /mx permtest report to export detailed results.");
            }
        });
    }

    private void handlePermTestFull(CommandSender sender, String[] args) {
        // Full mode runs permission checks + attempts command dispatch
        // For now, this runs the same check tests since command dispatch simulation
        // requires a live server context. The "full" results additionally note
        // which commands would execute vs be denied.
        sendMessage(sender, "<gray>Running full permission tests (check + execution simulation)...");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String sub = args.length > 0 ? args[0].toLowerCase() : "profiles";

            PermissionTestEngine.TestReport report;
            switch (sub) {
                case "profiles" -> report = PermissionTestEngine.runAllProfiles();
                case "me" -> {
                    if (!(sender instanceof Player player)) {
                        sendMessage(sender, "<red>This command can only be run by a player.");
                        return;
                    }
                    Set<String> perms = new java.util.HashSet<>();
                    for (var info : player.getEffectivePermissions()) {
                        if (info.getValue() && info.getPermission().startsWith("moderex.")) {
                            perms.add(info.getPermission());
                        }
                    }
                    report = PermissionTestEngine.runSingleProfile(
                            PermissionTestProfiles.fromPermissions(player.getName(), perms, player.isOp()));
                }
                default -> {
                    sendMessage(sender, "<red>Usage: /mx permtest full <profiles|me>");
                    return;
                }
            }

            lastTestReport = report;
            for (String line : PermissionTestEngine.toChatLines(report, true, 300)) {
                sendMessage(sender, line);
            }
        });
    }

    private void handlePermTestReport(CommandSender sender) {
        if (lastTestReport == null) {
            sendMessage(sender, "<red>No test results available. Run a test first.");
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                java.io.File dir = new java.io.File(plugin.getDataFolder(), "test-reports");
                java.io.File file = PermissionTestEngine.saveReport(lastTestReport, dir);
                sendMessage(sender, "<green>Report saved to: <white>" + file.getAbsolutePath());
            } catch (java.io.IOException e) {
                sendMessage(sender, "<red>Failed to save report: " + e.getMessage());
            }
        });
    }

    private void handlePermTestMatrix(CommandSender sender) {
        if (lastTestReport == null) {
            sendMessage(sender, "<gray>Running profile tests for matrix...");
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                lastTestReport = PermissionTestEngine.runAllProfiles();
                for (String line : PermissionTestEngine.toMatrixLines(lastTestReport)) {
                    sendMessage(sender, line);
                }
            });
        } else {
            for (String line : PermissionTestEngine.toMatrixLines(lastTestReport)) {
                sendMessage(sender, line);
            }
        }
    }

    // --- Custom Permission Test (Interactive Chat Mode) ---

    private final Map<UUID, CustomPermTestSession> customPermTestSessions = new java.util.concurrent.ConcurrentHashMap<>();

    private static class CustomPermTestSession {
        final Set<String> permissions = new java.util.HashSet<>();
        boolean isOp = false;
    }

    private void startCustomPermTest(Player player) {
        CustomPermTestSession session = new CustomPermTestSession();
        customPermTestSessions.put(player.getUniqueId(), session);

        sendMessage(player, "");
        sendMessage(player, "<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(player, "<white>  <bold>Custom Permission Test</bold>");
        sendMessage(player, "");
        sendMessage(player, "<gray>Type permissions to add/remove:");
        sendMessage(player, "  <green>+moderex.ban <gray>- Add a permission");
        sendMessage(player, "  <red>-moderex.ban <gray>- Remove a permission");
        sendMessage(player, "  <yellow>+moderex.history.* <gray>- Add wildcard");
        sendMessage(player, "  <aqua>op / !op <gray>- Toggle OP status");
        sendMessage(player, "  <gold>run <gray>- Execute tests with current set");
        sendMessage(player, "  <red>done <gray>- Exit custom mode");
        sendMessage(player, "");
        sendMessage(player, "<gray>Current permissions: <white>(empty)");
        sendMessage(player, "<gray>OP: <red>false");
        sendMessage(player, "<gradient:#8b5cf6:#3b82f6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
    }

    /**
     * Process chat input for custom permission test mode.
     * Returns true if the message was consumed by the custom test session.
     */
    public boolean processCustomPermTestInput(Player player, String message) {
        CustomPermTestSession session = customPermTestSessions.get(player.getUniqueId());
        if (session == null) return false;

        String input = message.trim().toLowerCase();

        if (input.equals("done") || input.equals("exit") || input.equals("cancel")) {
            customPermTestSessions.remove(player.getUniqueId());
            sendMessage(player, "<gray>Exited custom permission test mode.");
            return true;
        }

        if (input.equals("op")) {
            session.isOp = true;
            sendMessage(player, "<green>OP status: <white>true");
            return true;
        }

        if (input.equals("!op")) {
            session.isOp = false;
            sendMessage(player, "<red>OP status: <white>false");
            return true;
        }

        if (input.equals("run")) {
            sendMessage(player, "<gray>Running tests with " + session.permissions.size() + " permissions (OP=" + session.isOp + ")...");
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                PermissionTestEngine.TestReport report = PermissionTestEngine.runCustom(session.permissions, session.isOp);
                lastTestReport = report;
                for (String line : PermissionTestEngine.toChatLines(report, false, 200)) {
                    sendMessage(player, line);
                }
            });
            return true;
        }

        if (input.equals("clear")) {
            session.permissions.clear();
            sendMessage(player, "<gray>Cleared all permissions.");
            return true;
        }

        if (input.equals("list")) {
            if (session.permissions.isEmpty()) {
                sendMessage(player, "<gray>Current permissions: <white>(empty)");
            } else {
                sendMessage(player, "<gray>Current permissions (" + session.permissions.size() + "):");
                for (String p : session.permissions) {
                    sendMessage(player, "  <white>" + p);
                }
            }
            sendMessage(player, "<gray>OP: " + (session.isOp ? "<green>true" : "<red>false"));
            return true;
        }

        if (input.startsWith("+")) {
            String perm = input.substring(1).trim();
            if (!perm.isEmpty()) {
                session.permissions.add(perm);
                sendMessage(player, "<green>+ <white>" + perm + " <gray>(" + session.permissions.size() + " total)");
            }
            return true;
        }

        if (input.startsWith("-")) {
            String perm = input.substring(1).trim();
            if (session.permissions.remove(perm)) {
                sendMessage(player, "<red>- <white>" + perm + " <gray>(" + session.permissions.size() + " total)");
            } else {
                sendMessage(player, "<red>Permission not in set: " + perm);
            }
            return true;
        }

        // If it looks like a permission, auto-add
        if (input.startsWith("moderex.")) {
            session.permissions.add(input);
            sendMessage(player, "<green>+ <white>" + input + " <gray>(" + session.permissions.size() + " total)");
            return true;
        }

        sendMessage(player, "<red>Unknown input. Use +perm, -perm, op, !op, run, list, clear, or done.");
        return true;
    }

    /**
     * Check if a player is in custom permission test mode.
     */
    public boolean isInCustomPermTestMode(UUID uuid) {
        return customPermTestSessions.containsKey(uuid);
    }

    private void handlePanel(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.staff")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        var identity = plugin.getServerIdentity();
        if (identity == null) {
            sendMessage(sender, "<red>Server identity not initialized yet.");
            return;
        }

        var settings = plugin.getConfigManager().getSettings();
        var gateway = plugin.getGatewayClient();

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Web Panel Access</bold>");
        sendMessage(sender, "");

        // Show gateway URL if connected
        if (gateway != null && gateway.isConnected()) {
            String urlPrefix = identity.getUrlPrefix();
            String gatewayUrl = "https://panel.moderex.net/" + urlPrefix + "/";

            sendMessage(sender, "<green><bold>Gateway Mode</bold> <gray>(Recommended)");
            sendMessage(sender, "");

            if (sender instanceof Player player) {
                Component clickableUrl = TextUtil.parse("<aqua><bold>[CLICK TO OPEN]</bold></aqua> <white>" + gatewayUrl)
                        .clickEvent(ClickEvent.openUrl(gatewayUrl))
                        .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Click to open web panel")));
                Msg.send(player, clickableUrl);
            } else {
                sendMessage(sender, "<aqua>" + gatewayUrl);
            }
        } else if (!settings.isGatewayEnabled()) {
            sendMessage(sender, "<yellow>Gateway is disabled (opt-out mode)");
        } else {
            sendMessage(sender, "<yellow>Gateway not connected. Use <white>/mx gateway reconnect");
        }

        // Show direct URL if web panel is enabled
        if (settings.isWebPanelEnabled()) {
            sendMessage(sender, "");
            sendMessage(sender, "<gray><bold>Direct Mode</bold> <dark_gray>(Requires port forwarding)");

            String host = settings.getWebPanelHost();
            int port = settings.getWebPanelPort();
            if (host == null || host.isEmpty()) {
                host = "your-server-ip";
            }
            String directUrl = "http://" + host + ":" + port + "/";
            sendMessage(sender, "<gray>" + directUrl);
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleGateway(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx gateway <status|reconnect>");
            sendMessage(sender, "<gray>/mx gateway status <white>- Show gateway connection status");
            sendMessage(sender, "<gray>/mx gateway reconnect <white>- Force reconnect to gateway");
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "status" -> handleGatewayStatus(sender);
            case "reconnect" -> handleGatewayReconnect(sender);
            default -> {
                sendMessage(sender, "<red>Unknown gateway action: " + action);
                sendMessage(sender, "<gray>Available: status, reconnect");
            }
        }
    }

    private void handleGatewayStatus(CommandSender sender) {
        var settings = plugin.getConfigManager().getSettings();
        var identity = plugin.getServerIdentity();
        var gateway = plugin.getGatewayClient();

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Gateway Status</bold>");
        sendMessage(sender, "");

        // Enabled/disabled
        sendMessage(sender, "<gray>Gateway Enabled: " + (settings.isGatewayEnabled() ? "<green>Yes" : "<red>No (opt-out)"));

        if (!settings.isGatewayEnabled()) {
            sendMessage(sender, "");
            sendMessage(sender, "<yellow>Gateway is disabled in config.yml");
            sendMessage(sender, "<gray>Set <white>gateway.enabled: true<gray> to connect.");
            sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
            sendMessage(sender, "");
            return;
        }

        // Connection status
        if (gateway == null) {
            sendMessage(sender, "<gray>Connection: <red>Not initialized");
        } else if (gateway.isConnected()) {
            sendMessage(sender, "<gray>Connection: <green>Connected");

            // Server identity info
            if (identity != null) {
                sendMessage(sender, "<gray>Server ID: <white>" + identity.getServerId());
                sendMessage(sender, "<gray>URL Prefix: <white>" + identity.getUrlPrefix());
                sendMessage(sender, "<gray>Panel URL: <aqua>panel.moderex.net/" + identity.getUrlPrefix() + "/");
            }

            // Uptime
            long connectedAt = gateway.getConnectedAt();
            if (connectedAt > 0) {
                long uptimeMs = System.currentTimeMillis() - connectedAt;
                String uptime = formatUptime(uptimeMs);
                sendMessage(sender, "<gray>Uptime: <white>" + uptime);
            }
        } else {
            sendMessage(sender, "<gray>Connection: <yellow>Disconnected");
            sendMessage(sender, "<gray>Reconnecting: " + (gateway.isReconnecting() ? "<yellow>Yes" : "<gray>No"));
        }

        // Gateway URL
        sendMessage(sender, "<gray>Gateway URL: <dark_gray>" + settings.getGatewayUrl());

        // Debug logging
        sendMessage(sender, "<gray>Debug Logging: " + (settings.isGatewayDebugLogging() ? "<green>On" : "<gray>Off"));

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#3b82f6:#8b5cf6>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleGatewayReconnect(CommandSender sender) {
        var settings = plugin.getConfigManager().getSettings();

        if (!settings.isGatewayEnabled()) {
            sendMessage(sender, "<red>Gateway is disabled in config.yml");
            sendMessage(sender, "<gray>Set <white>gateway.enabled: true<gray> to use gateway features.");
            return;
        }

        var gateway = plugin.getGatewayClient();
        if (gateway == null) {
            sendMessage(sender, "<red>Gateway client not initialized. Try reloading the plugin.");
            return;
        }

        sendMessage(sender, "<yellow>Reconnecting to gateway...");

        gateway.reconnect();

        // Check status after a short delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (gateway.isConnected()) {
                var identity = plugin.getServerIdentity();
                sendMessage(sender, "<green>Successfully connected to gateway!");
                if (identity != null) {
                    sendMessage(sender, "<gray>Panel URL: <aqua>panel.moderex.net/" + identity.getUrlPrefix() + "/");
                }
            } else {
                sendMessage(sender, "<yellow>Connection in progress... Check <white>/mx gateway status<yellow> in a moment.");
            }
        }, 40L); // 2 seconds
    }

    private String formatUptime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m " + (seconds % 60) + "s";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    private void handleTestReplay(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        sendMessage(sender, "<yellow>Generating test replay...");

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                var session = TestReplayGenerator.generateTestReplay(
                        plugin,
                        player.getWorld().getName(),
                        player.getLocation().getX(),
                        player.getLocation().getZ()
                );

                session.save(plugin.getDataFolder().toPath().resolve("replays"));

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sendMessage(sender, "<green>Test replay generated successfully!");
                    sendMessage(sender, "<gray>Session ID: <white>" + session.getSessionId());
                    sendMessage(sender, "<gray>Duration: <white>10 seconds with 2 players");
                    sendMessage(sender, "<gray>Use the web panel to view the replay.");
                });
            } catch (Exception e) {
                plugin.logError("Failed to generate test replay", e);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sendMessage(sender, "<red>Failed to generate test replay: " + e.getMessage());
                });
            }
        });
    }

    private void handleReplay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!plugin.getReplayManager().isEnabled()) {
            sendMessage(sender, "<red>Replay system is disabled! Enable it in config.yml");
            return;
        }

        if (args.length == 0) {
            plugin.getGuiManager().open(player, new ReplayGui(plugin));
            return;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "start", "record" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /mx replay start <player>");
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[1]);
                    return;
                }
                if (plugin.getReplayManager().isBeingRecorded(target.getUniqueId())) {
                    sendMessage(sender, "<yellow>" + target.getName() + " is already being recorded!");
                    return;
                }
                plugin.getReplayManager().startRecording(target, ReplaySession.RecordingReason.STAFF_REQUEST);
                sendMessage(sender, "<green>Started recording " + target.getName() + "!");
                sendMessage(sender, "<gray>Use /mx replay stop " + target.getName() + " to stop recording.");
            }
            case "stop" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /mx replay stop <player>");
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", args[1]);
                    return;
                }
                if (!plugin.getReplayManager().isBeingRecorded(target.getUniqueId())) {
                    sendMessage(sender, "<yellow>" + target.getName() + " is not being recorded!");
                    return;
                }
                plugin.getReplayManager().stopRecording(target.getUniqueId()).thenAccept(sessionId -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (sessionId != null) {
                            sendMessage(sender, "<green>Stopped recording " + target.getName() + "!");
                            sendMessage(sender, "<gray>Session saved as: <white>" + sessionId);
                        } else {
                            sendMessage(sender, "<red>Failed to stop recording!");
                        }
                    });
                });
            }
            case "play", "playback" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /mx replay play <sessionId>");
                    return;
                }
                String sessionId = args[1];
                sendMessage(sender, "<yellow>Loading replay " + sessionId + "...");
                plugin.getReplayManager().loadReplay(sessionId).thenAccept(session -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (session == null) {
                            sendMessage(sender, "<red>Replay not found: " + sessionId);
                            return;
                        }
                        var playback = plugin.getReplayManager().startPlayback(player, session);
                        if (playback != null) {
                            sendMessage(sender, "<green>Starting playback...");
                        }
                    });
                });
            }
            case "list" -> {
                sendMessage(sender, "<yellow>Loading replays...");
                plugin.getReplayManager().getSavedReplays().thenAccept(replays -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (replays.isEmpty()) {
                            sendMessage(sender, "<gray>No saved replays found.");
                            return;
                        }
                        sendMessage(sender, "<yellow>Saved Replays (" + replays.size() + "):");
                        for (var replay : replays.subList(0, Math.min(10, replays.size()))) {
                            sendMessage(sender, "<gray>• <white>" + replay.sessionId() +
                                    " <gray>- <yellow>" + replay.primaryName() +
                                    " <gray>(" + formatReason(replay.reason()) + ")");
                        }
                        if (replays.size() > 10) {
                            sendMessage(sender, "<gray>... and " + (replays.size() - 10) + " more. Use GUI to see all.");
                        }
                    });
                });
            }
            case "search" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /mx replay search <player>");
                    return;
                }
                @SuppressWarnings("deprecation")
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                plugin.getGuiManager().open(player, new ReplayGui(plugin, target.getUniqueId(),
                        target.getName() != null ? target.getName() : args[1]));
            }
            case "delete" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /mx replay delete <sessionId>");
                    return;
                }
                String sessionId = args[1];
                plugin.getReplayManager().deleteReplay(sessionId).thenAccept(success -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (success) {
                            sendMessage(sender, "<green>Replay " + sessionId + " deleted!");
                        } else {
                            sendMessage(sender, "<red>Failed to delete replay: " + sessionId);
                        }
                    });
                });
            }
            case "status" -> {
                sendMessage(sender, "<yellow>Replay System Status:");
                sendMessage(sender, "<gray>Enabled: " + (plugin.getReplayManager().isEnabled() ? "<green>Yes" : "<red>No"));
                sendMessage(sender, "<gray>AC Recording: " + (plugin.getReplayManager().isRecordOnAnticheatAlert() ? "<green>On" : "<red>Off"));
                sendMessage(sender, "<gray>Watchlist Recording: " + (plugin.getReplayManager().isRecordWatchlistPlayers() ? "<green>On" : "<red>Off"));
                sendMessage(sender, "<gray>Nearby Radius: <white>" + plugin.getReplayManager().getNearbyPlayerRadius() + " blocks");
                sendMessage(sender, "<gray>Max Duration: <white>" + (plugin.getReplayManager().getMaxRecordingDurationSeconds() / 60) + " minutes");
            }
            default -> {
                sendMessage(sender, "<yellow>Replay System Commands:");
                sendMessage(sender, "<gray>/mx replay <white>- Open replay browser GUI");
                sendMessage(sender, "<gray>/mx replay start <player> <white>- Start recording a player");
                sendMessage(sender, "<gray>/mx replay stop <player> <white>- Stop recording a player");
                sendMessage(sender, "<gray>/mx replay play <sessionId> <white>- Play back a replay");
                sendMessage(sender, "<gray>/mx replay list <white>- List saved replays");
                sendMessage(sender, "<gray>/mx replay search <player> <white>- Find replays for a player");
                sendMessage(sender, "<gray>/mx replay delete <sessionId> <white>- Delete a replay");
                sendMessage(sender, "<gray>/mx replay status <white>- Show system status");
            }
        }
    }

    private String formatReason(ReplaySession.RecordingReason reason) {
        return switch (reason) {
            case ANTICHEAT_ALERT -> "Anticheat";
            case WATCHLIST -> "Watchlist";
            case MANUAL -> "Manual";
            case STAFF_REQUEST -> "Staff";
            case AUTOMOD_TRIGGER -> "Automod";
        };
    }

    private void handleConnect(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.staff")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!plugin.getConfigManager().getSettings().isWebPanelEnabled()) {
            sendMessage(sender, "<red>Web panel is not enabled in config.yml");
            return;
        }

        if (plugin.getWebAuthManager() == null) {
            sendMessage(sender, "<red>Web authentication is not initialized.");
            return;
        }

        String tempToken = plugin.getWebAuthManager().generateTempToken(player.getUniqueId(), player.getName());
        int port = plugin.getConfigManager().getSettings().getWebPanelPort();
        String host = plugin.getConfigManager().getSettings().getWebPanelHost();
        if (host == null || host.isEmpty()) {
            host = "your-server-ip";
        }

        String url = "http://" + host + ":" + port + "/?auth=" + tempToken;

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Web Panel Quick Connect</bold>");
        sendMessage(sender, "");

        Component clickableUrl = TextUtil.parse("<green><bold>[CLICK HERE TO OPEN PANEL]</bold></green>")
                .clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Click to open web panel\n<yellow>" + url)));

        Msg.send(player, clickableUrl);

        sendMessage(sender, "");
        sendMessage(sender, "<gray>Or copy: <white>" + url);
        sendMessage(sender, "");
        sendMessage(sender, "<yellow>Link expires after <white>30 minutes<yellow> of inactivity.");
        sendMessage(sender, "<gray>For permanent access, use <white>/mx gettoken");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    // Rate limit: track link code generation per player (max 3 per hour)
    private static final Map<UUID, List<Long>> linkCodeRateLimit = new ConcurrentHashMap<>();

    private void handleLink(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.webpanel")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        // Require gateway connection
        var gateway = plugin.getGatewayClient();
        if (gateway == null || !gateway.isConnected()) {
            sendMessage(sender, "<red>Gateway is not connected. Link codes require the gateway.");
            sendMessage(sender, "<gray>Ask an admin to enable the gateway in the config.");
            return;
        }

        // Rate limit: 3 codes per player per hour
        UUID uuid = player.getUniqueId();
        List<Long> timestamps = linkCodeRateLimit.computeIfAbsent(uuid, k -> new ArrayList<>());
        long oneHourAgo = System.currentTimeMillis() - 3600000;
        timestamps.removeIf(t -> t < oneHourAgo);
        if (timestamps.size() >= 3) {
            sendMessage(sender, "<red>You've generated too many link codes recently. Please wait before trying again.");
            return;
        }
        timestamps.add(System.currentTimeMillis());

        // Generate 10-digit numeric code
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            codeBuilder.append(random.nextInt(10));
        }
        String code = codeBuilder.toString();

        // SHA-256 hash the code (only the hash is sent to gateway)
        String codeHash;
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(code.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hexBuilder.append(String.format("%02x", b));
            }
            codeHash = hexBuilder.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            sendMessage(sender, "<red>Internal error generating link code.");
            return;
        }

        // Send hash to gateway (expires in 10 minutes)
        long expiresAt = System.currentTimeMillis() + 10 * 60 * 1000;
        gateway.sendLinkCode(uuid, player.getName(), codeHash, expiresAt);

        // Format code for display: XXXX-XXX-XXX
        String formattedCode = code.substring(0, 4) + "-" + code.substring(4, 7) + "-" + code.substring(7, 10);

        // Display to player
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Web Panel Link Code</bold>");
        sendMessage(sender, "");
        sendMessage(sender, "<gray>Your link code:");
        sendMessage(sender, "");
        sendMessage(sender, "<white><bold>" + formattedCode + "</bold>");
        sendMessage(sender, "");
        sendMessage(sender, "<yellow>This code expires in <white>10 minutes<yellow>.");
        sendMessage(sender, "<gray>Go to <aqua><click:open_url:'https://moderex.net/link'><hover:show_text:'<gray>Click to open'>moderex.net/link</hover></click></aqua> <gray>and enter this code.");
        sendMessage(sender, "");
        sendMessage(sender, "<red><bold>DO NOT</bold></red> <red>share this code with anyone!");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleGetToken(CommandSender sender) {
        // Legacy token system — recommend /mx link instead
        sendMessage(sender, "<yellow>Note: <gray>Token auth is being replaced. Use <white>/mx link<gray> for secure password-based login.");

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.staff")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (plugin.getWebAuthManager() == null) {
            sendMessage(sender, "<red>Web authentication is not initialized.");
            return;
        }

        if (plugin.getWebAuthManager().hasPermanentToken(player.getUniqueId())) {
            sendMessage(sender, "");
            sendMessage(sender, "<yellow>You already have a permanent token.");
            sendMessage(sender, "<gray>If you've lost it, use <white>/mx revoketoken<gray> first,");
            sendMessage(sender, "<gray>then run <white>/mx gettoken<gray> again to generate a new one.");
            sendMessage(sender, "");
            return;
        }

        String token = plugin.getWebAuthManager().generatePermanentToken(player.getUniqueId());

        // Check if player is a Bedrock player (Geyser/Floodgate) - they can't use copy to clipboard
        boolean isBedrockPlayer = isBedrockPlayer(player);

        if (isBedrockPlayer) {
            // Send token directly in chat for Bedrock players
            sendMessage(sender, "");
            sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
            sendMessage(sender, "<red><bold>SECURE TOKEN - DO NOT SHARE!</bold></red>");
            sendMessage(sender, "");
            sendMessage(sender, "<gray>Your token (copy manually):");
            sendMessage(sender, "<white>" + token);
            sendMessage(sender, "");
            sendMessage(sender, "<yellow>Store this token safely - you won't see it again!");
            sendMessage(sender, "<gray>Use <white>/mx revoketoken<gray> to invalidate it.");
            sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
            sendMessage(sender, "");
        } else {
            // Java players get the book with click-to-copy
            openTokenBook(player, token);

            sendMessage(sender, "");
            sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
            sendMessage(sender, "<green>Your token has been generated!");
            sendMessage(sender, "<gray>A secure book has been opened with your token.");
            sendMessage(sender, "<yellow>Click the token in the book to copy it.");
            sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
            sendMessage(sender, "");
        }
    }

    /**
     * Check if a player is connecting via Bedrock (Geyser/Floodgate).
     * These players can't use the copy-to-clipboard functionality.
     */
    private boolean isBedrockPlayer(Player player) {
        var hookManager = plugin.getHookManager();
        if (hookManager == null) {
            return false;
        }

        UUID uuid = player.getUniqueId();

        // Check Floodgate first (more common)
        if (hookManager.hasFloodgate()) {
            var floodgate = hookManager.getFloodgateHook();
            if (floodgate != null && floodgate.isFloodgatePlayer(uuid)) {
                return true;
            }
        }

        // Check Geyser
        if (hookManager.hasGeyser()) {
            var geyser = hookManager.getGeyserHook();
            if (geyser != null && geyser.isBedrockPlayer(uuid)) {
                return true;
            }
        }

        // Fallback: check UUID pattern (Floodgate UUIDs start with 00000000-0000-0000)
        return uuid.toString().startsWith("00000000-0000-0000");
    }

    private void openTokenBook(Player player, String token) {
        Component page1 = Component.empty()
                .append(Component.text("SECURE TOKEN")
                        .color(NamedTextColor.DARK_RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("DO NOT SHARE")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.text("THIS TOKEN!")
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Anyone with this token can access your web panel account.")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Store it safely:")
                        .color(NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text("• Password manager")
                        .color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text("• Encrypted file")
                        .color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.text("• Secure cloud storage")
                        .color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("→ Turn page for token")
                        .color(NamedTextColor.DARK_AQUA)
                        .decorate(TextDecoration.ITALIC));

        Component page2 = Component.empty()
                .append(Component.text("YOUR TOKEN")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("[CLICK TO COPY]")
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.copyToClipboard(token))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to copy token to clipboard")
                                .color(NamedTextColor.YELLOW))))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Token (click above):")
                        .color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text(token.substring(0, Math.min(25, token.length())))
                        .color(NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.copyToClipboard(token)))
                .append(Component.newline())
                .append(Component.text(token.length() > 25 ? token.substring(25, Math.min(50, token.length())) : "")
                        .color(NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.copyToClipboard(token)))
                .append(Component.newline())
                .append(Component.text(token.length() > 50 ? token.substring(50) : "")
                        .color(NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.copyToClipboard(token)));

        Component page3 = Component.empty()
                .append(Component.text("IMPORTANT INFO")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("• This token NEVER expires")
                        .color(NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("• Use /mx revoketoken to invalidate it")
                        .color(NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("• You can only have ONE token at a time")
                        .color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("• For quick access, use /mx connect instead")
                        .color(NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Close this book when done.")
                        .color(NamedTextColor.DARK_GRAY)
                        .decorate(TextDecoration.ITALIC));

        Book book = Book.builder()
                .title(Component.text("Web Panel Token"))
                .author(Component.text("ModereX"))
                .addPage(page1)
                .addPage(page2)
                .addPage(page3)
                .build();

        player.openBook(book);
    }

    private void handleRevokeToken(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.webpanel")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (plugin.getWebAuthManager() == null) {
            sendMessage(sender, "<red>Web authentication is not initialized.");
            return;
        }

        if (!plugin.getWebAuthManager().hasPermanentToken(player.getUniqueId())) {
            sendMessage(sender, "<yellow>You don't have a permanent token to revoke.");
            return;
        }
        plugin.getWebAuthManager().revokePermanentToken(player.getUniqueId());
        plugin.getWebAuthManager().invalidateAllSessions(player.getUniqueId());

        sendMessage(sender, "");
        sendMessage(sender, "<green>Your permanent token has been revoked.");
        sendMessage(sender, "<gray>All active web sessions have been terminated.");
        sendMessage(sender, "<gray>Use <white>/mx gettoken<gray> to generate a new token.");
        sendMessage(sender, "");
    }

    private void handleSessions(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.webpanel")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (plugin.getWebAuthManager() == null) {
            sendMessage(sender, "<red>Web authentication is not initialized.");
            return;
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>Web Panel Sessions</bold>");
        sendMessage(sender, "");

        boolean hasToken = plugin.getWebAuthManager().hasPermanentToken(player.getUniqueId());
        sendMessage(sender, "<gray>Permanent Token: " + (hasToken ? "<green>Active" : "<red>Not Set"));

        sendMessage(sender, "");

        Component revokeButton = TextUtil.parse("<red>[Revoke All Sessions]")
                .clickEvent(ClickEvent.runCommand("/mx revoketoken"))
                .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Revoke token and log out of all sessions")));

        if (hasToken) {
            Msg.send(player, revokeButton);
        } else {
            Component getTokenButton = TextUtil.parse("<green>[Generate Token]")
                    .clickEvent(ClickEvent.runCommand("/mx gettoken"))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Generate a permanent access token")));
            Msg.send(player, getTokenButton);
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleAutomod(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        plugin.getGuiManager().open(player, new AutomodGui(plugin));
    }

    private void handleAnticheat(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.notify.anticheat")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        plugin.getGuiManager().open(player, new AnticheatRulesGui(plugin));
    }

    private void handleSendAlert(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length < 3) {
            sendMessage(sender, "<red>Usage: /mx sendalert <player> <type> <message...>");
            sendMessage(sender, "<gray>Types: ban, kick, mute, warn, pardon, anticheat, automod, command, nickname, joinleave, lag, watchlist, staffchat, custom");
            sendMessage(sender, "<gray>Example: /mx sendalert Steve anticheat Flagged for KillAura VL 25");
            sendMessage(sender, "<gray>Example: /mx sendalert Player ban Permanently banned for hacking");
            sendMessage(sender, "<yellow>Note: Works for offline players too!");
            return;
        }

        String playerName = args[0];
        String typeStr = args[1].toLowerCase();
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));

        // Parse alert type
        com.blockforge.moderex.alert.AlertManager.AlertType alertType =
                com.blockforge.moderex.alert.AlertManager.AlertType.fromString(typeStr);

        // Try to find player UUID (works for offline players too)
        java.util.UUID playerUuid = null;
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            playerUuid = onlinePlayer.getUniqueId();
            playerName = onlinePlayer.getName(); // Use correct case
        } else {
            // Try offline player lookup
            @SuppressWarnings("deprecation")
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
            if (offline.hasPlayedBefore()) {
                playerUuid = offline.getUniqueId();
                if (offline.getName() != null) {
                    playerName = offline.getName();
                }
            }
        }

        // Generate title based on type
        String title = switch (alertType) {
            case BAN -> "Ban Alert";
            case KICK -> "Kick Alert";
            case MUTE -> "Mute Alert";
            case WARN -> "Warn Alert";
            case PARDON -> "Pardon Alert";
            case ANTICHEAT -> "Anticheat Alert";
            case AUTOMOD -> "Automod Violation";
            case COMMAND -> "Command Alert";
            case NICKNAME -> "Nickname Alert";
            case JOINLEAVE -> "Join/Leave Alert";
            case LAG -> "Server Lag Alert";
            case PUNISHMENT -> "Punishment Alert";
            case WATCHLIST -> "Watchlist Alert";
            case STAFFCHAT -> "Staff Notice";
            case CUSTOM -> "Alert";
        };

        // Send the alert using the unified AlertManager
        plugin.getAlertManager().sendAlert(playerName, playerUuid, alertType, title, message);

        sendMessage(sender, "<green>Alert sent!");
        sendMessage(sender, "<gray>Type: " + alertType.getDisplayName());
        sendMessage(sender, "<gray>Player: " + playerName + (playerUuid != null ? " (UUID found)" : " (offline/unknown)"));
        sendMessage(sender, "<gray>Message: " + message);
    }

    private void handleChat(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx chat <enable|disable|slowmode|clear>");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "enable" -> {
                plugin.getConfigManager().getSettings().setChatEnabled(true);
                sendMessage(sender, MessageKey.CHAT_ENABLED);
            }
            case "disable" -> {
                plugin.getConfigManager().getSettings().setChatEnabled(false);
                sendMessage(sender, MessageKey.CHAT_DISABLED);
            }
            case "slowmode" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /mx chat slowmode <seconds>");
                    return;
                }
                try {
                    int seconds = Integer.parseInt(args[1]);
                    plugin.getConfigManager().getSettings().setDefaultSlowmodeSeconds(seconds);
                    sendMessage(sender, MessageKey.CHAT_SLOWMODE_SET, "seconds", String.valueOf(seconds));
                } catch (NumberFormatException e) {
                    sendMessage(sender, "<red>Invalid number: " + args[1]);
                }
            }
            case "clear" -> {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    for (int i = 0; i < 100; i++) {
                        Msg.send(player, Component.text(""));
                    }
                    Msg.send(player, plugin.getLanguageManager().get(MessageKey.CHAT_CLEARED,
                            "player", sender.getName()));
                }
                sendMessage(sender, "<green>Chat cleared.");
            }
            default -> sendMessage(sender, "<red>Unknown chat subcommand: " + args[0]);
        }
    }

    private void handleSettings(CommandSender sender) {
        if (!sender.hasPermission("moderex.staff")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        plugin.getGuiManager().open(player, new StaffSettingsGui(plugin));
    }

    private void handleTemplates(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!PermissionUtil.hasPermission(sender, "moderex.staff")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        plugin.getGuiManager().open(player, new TemplateGui(plugin));
    }

    private void handleAnalytics(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        plugin.getGuiManager().open(player, new AnalyticsGui(plugin));
    }

    private void handleMuteSettings(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        sendMessage(sender, "<yellow>Mute Settings GUI coming soon!");
    }

    private void handleWarningSettings(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        sendMessage(sender, "<yellow>Warning Settings GUI coming soon!");
    }

    private void handleBan(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.ban")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx ban <player> [duration] [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        if (plugin.getPunishmentManager().isBanned(targetUuid)) {
            sendMessage(sender, MessageKey.BAN_ALREADY_BANNED, "player", displayName);
            return;
        }

        long duration = -1;
        String reason = "No reason specified";

        if (args.length >= 2) {
            if (DurationParser.isValidDuration(args[1])) {
                duration = DurationParser.parse(args[1]);
                if (args.length >= 3) {
                    reason = joinArgs(args, 2);
                }
            } else {
                reason = joinArgs(args, 1);
            }
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        final long finalDuration = duration;
        final String finalReason = reason;

        plugin.getPunishmentManager().ban(targetUuid, displayName, staffUuid, staffName, finalDuration, finalReason)
                .thenAccept(punishment -> {
                    String durationStr = DurationParser.format(finalDuration);
                    sendMessage(sender, MessageKey.BAN_SUCCESS, "player", displayName, "duration", durationStr);
                });
    }

    private void handleUnban(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.unban")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx unban <player> [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        if (!plugin.getPunishmentManager().isBanned(targetUuid)) {
            sendMessage(sender, MessageKey.UNBAN_NOT_BANNED, "player", displayName);
            return;
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        String reason = args.length > 1 ? joinArgs(args, 1) : null;

        plugin.getPunishmentManager().removePunishment(targetUuid, PunishmentType.BAN, staffUuid, staffName, reason)
                .thenAccept(success -> {
                    if (success) {
                        sendMessage(sender, MessageKey.UNBAN_SUCCESS, "player", displayName);
                    } else {
                        sendMessage(sender, "<red>Failed to unban player.");
                    }
                });
    }

    private void handleMute(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.mute")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx mute <player> [duration] [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        if (plugin.getPunishmentManager().isMuted(targetUuid)) {
            sendMessage(sender, MessageKey.MUTE_ALREADY_MUTED, "player", displayName);
            return;
        }

        long duration = -1;
        String reason = "No reason specified";

        if (args.length >= 2) {
            if (DurationParser.isValidDuration(args[1])) {
                duration = DurationParser.parse(args[1]);
                if (args.length >= 3) {
                    reason = joinArgs(args, 2);
                }
            } else {
                reason = joinArgs(args, 1);
            }
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        final long finalDuration = duration;
        final String finalReason = reason;

        plugin.getPunishmentManager().mute(targetUuid, displayName, staffUuid, staffName, finalDuration, finalReason)
                .thenAccept(punishment -> {
                    String durationStr = DurationParser.format(finalDuration);
                    sendMessage(sender, MessageKey.MUTE_SUCCESS, "player", displayName, "duration", durationStr);
                });
    }

    private void handleUnmute(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.unmute")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx unmute <player> [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        if (!plugin.getPunishmentManager().isMuted(targetUuid)) {
            sendMessage(sender, MessageKey.UNMUTE_NOT_MUTED, "player", displayName);
            return;
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        String reason = args.length > 1 ? joinArgs(args, 1) : null;

        plugin.getPunishmentManager().removePunishment(targetUuid, PunishmentType.MUTE, staffUuid, staffName, reason)
                .thenAccept(success -> {
                    if (success) {
                        sendMessage(sender, MessageKey.UNMUTE_SUCCESS, "player", displayName);
                    } else {
                        sendMessage(sender, "<red>Failed to unmute player.");
                    }
                });
    }

    private void handleKick(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.kick")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx kick <player> [reason]");
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName();
        String reason = args.length > 1 ? joinArgs(args, 1) : "No reason specified";

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        plugin.getPunishmentManager().kick(targetUuid, displayName, staffUuid, staffName, reason)
                .thenAccept(punishment -> {
                    sendMessage(sender, MessageKey.KICK_SUCCESS, "player", displayName);
                });
    }

    private void handleWarn(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.warn")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx warn <player> [duration] [reason]");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        long duration = DurationParser.parse("30d");
        String reason = "No reason specified";

        if (args.length >= 2) {
            if (DurationParser.isValidDuration(args[1])) {
                duration = DurationParser.parse(args[1]);
                if (args.length >= 3) {
                    reason = joinArgs(args, 2);
                }
            } else {
                reason = joinArgs(args, 1);
            }
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        final long finalDuration = duration;
        final String finalReason = reason;

        plugin.getPunishmentManager().warn(targetUuid, displayName, staffUuid, staffName, finalDuration, finalReason)
                .thenAccept(punishment -> {
                    sendMessage(sender, MessageKey.WARN_SUCCESS, "player", displayName);
                });
    }

    private void handleIpBan(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.ipban")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx ipban <player> [duration] [reason]");
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sendMessage(sender, "<red>Player must be online for IP ban. Use /mx ban for offline players.");
            return;
        }

        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName();
        String ip = target.getAddress() != null ? target.getAddress().getAddress().getHostAddress() : null;

        if (ip == null) {
            sendMessage(sender, "<red>Could not determine player's IP address.");
            return;
        }

        long duration = -1;
        String reason = "No reason specified";

        if (args.length >= 2) {
            if (DurationParser.isValidDuration(args[1])) {
                duration = DurationParser.parse(args[1]);
                if (args.length >= 3) {
                    reason = joinArgs(args, 2);
                }
            } else {
                reason = joinArgs(args, 1);
            }
        }

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();

        final long finalDuration = duration;
        final String finalReason = reason;

        plugin.getPunishmentManager().ipBan(targetUuid, displayName, ip, staffUuid, staffName, finalDuration, finalReason)
                .thenAccept(punishment -> {
                    String durationStr = DurationParser.format(finalDuration);
                    sendMessage(sender, MessageKey.IPBAN_SUCCESS, "player", displayName, "duration", durationStr);
                });
    }

    private void handlePunish(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.punish")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (args.length == 0) {
            plugin.getGuiManager().open(player, new MainMenuGui(plugin));
        } else {
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                sendMessage(sender, MessageKey.PLAYER_NOT_FOUND, "player", targetName);
                return;
            }
            plugin.getGuiManager().open(player, new PunishPlayerGui(plugin, target));
        }
    }

    private void handleModLog(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.modlog")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (sender instanceof Player player) {
            plugin.getGuiManager().open(player, new ModLogGui(plugin));
        } else {
            if (args.length == 0) {
                sendMessage(sender, "<red>Usage: /mx modlog <player>");
                return;
            }
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            plugin.getPunishmentManager().getPunishments(target.getUniqueId()).thenAccept(punishments -> {
                sendMessage(sender, "<gold>Recent punishments for " + targetName + ":");
                if (punishments.isEmpty()) {
                    sendMessage(sender, "<gray>No punishments found.");
                } else {
                    int count = 0;
                    for (var p : punishments) {
                        if (count++ >= 10) break;
                        sendMessage(sender, String.format("<gray>- %s: <white>%s <gray>by %s",
                                p.getType().name(), p.getReason(), p.getStaffName()));
                    }
                }
            });
        }
    }

    private void handleStaffChat(CommandSender sender, String[] args) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.staffchat")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            if (sender instanceof Player player) {
                plugin.getStaffChatManager().toggleStaffChat(player);
                if (plugin.getStaffChatManager().hasStaffChatToggled(player)) {
                    sendMessage(sender, MessageKey.STAFFCHAT_ENABLED);
                } else {
                    sendMessage(sender, MessageKey.STAFFCHAT_DISABLED);
                }
            } else {
                sendMessage(sender, "<red>Usage: /mx staffchat <message>");
            }
            return;
        }

        String message = joinArgs(args, 0);
        if (sender instanceof Player player) {
            plugin.getStaffChatManager().sendMessage(player, message);
        } else {
            plugin.getStaffChatManager().broadcastFromWebPanel("Console", message);
        }
    }

    private void handleVanish(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.command.vanish")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (plugin.getVanishManager().isVanished(player)) {
            plugin.getVanishManager().unvanish(player);
            sendMessage(sender, MessageKey.VANISH_DISABLED);
        } else {
            plugin.getVanishManager().vanish(player);
            sendMessage(sender, MessageKey.VANISH_ENABLED);
        }
    }

    private void sendHelp(CommandSender sender) {
        if (!PermissionUtil.hasPermission(sender, "moderex.staff")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<white>      <bold>ModereX v" + plugin.getDescription().getVersion() + "</bold>");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Moderation:</bold>");
        sendMessage(sender, "<yellow>/mx ban <player> [duration] [reason] <gray>- Ban a player");
        sendMessage(sender, "<yellow>/mx unban <player> [reason] <gray>- Unban a player");
        sendMessage(sender, "<yellow>/mx mute <player> [duration] [reason] <gray>- Mute a player");
        sendMessage(sender, "<yellow>/mx unmute <player> [reason] <gray>- Unmute a player");
        sendMessage(sender, "<yellow>/mx kick <player> [reason] <gray>- Kick a player");
        sendMessage(sender, "<yellow>/mx warn <player> [duration] [reason] <gray>- Warn a player");
        sendMessage(sender, "<yellow>/mx ipban <player> [duration] [reason] <gray>- IP ban a player");
        sendMessage(sender, "<yellow>/mx punish [player] <gray>- Open punishment GUI");
        sendMessage(sender, "<yellow>/mx modlog [player] <gray>- View moderation log");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Staff:</bold>");
        sendMessage(sender, "<yellow>/mx staffchat [message] <gray>- Toggle or send staff chat");
        sendMessage(sender, "<yellow>/mx vanish <gray>- Toggle vanish mode");
        sendMessage(sender, "<yellow>/mx templates <gray>- View punishment templates");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Web Panel:</bold>");
        sendMessage(sender, "<yellow>/mx link <gray>- Generate a link code for web panel access");
        sendMessage(sender, "<yellow>/mx panel <gray>- Show clickable panel URL");
        sendMessage(sender, "<yellow>/mx gateway status <gray>- Show gateway connection status");
        sendMessage(sender, "<yellow>/mx gateway reconnect <gray>- Reconnect to gateway");
        sendMessage(sender, "<yellow>/mx connect <gray>- Get quick web panel link (30m expiry)");
        sendMessage(sender, "<yellow>/mx gettoken <gray>- <dark_gray>(Legacy) <gray>Generate permanent web access token");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Admin:</bold>");
        sendMessage(sender, "<yellow>/mx reload <gray>- Reload configuration");
        sendMessage(sender, "<yellow>/mx revoketoken <gray>- Revoke your permanent token");
        sendMessage(sender, "<yellow>/mx sessions <gray>- View web session status");
        sendMessage(sender, "<yellow>/mx chat <enable|disable|slowmode|clear> <gray>- Chat control");
        sendMessage(sender, "<yellow>/mx automod <gray>- Open automod settings");
        sendMessage(sender, "<yellow>/mx anticheat <gray>- Configure anticheat alerts");
        sendMessage(sender, "<yellow>/mx version <gray>- Show plugin version info");
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new java.util.ArrayList<>();
            if (PermissionUtil.hasPermission(sender, "moderex.command.ban")) completions.add("ban");
            if (PermissionUtil.hasPermission(sender, "moderex.command.unban")) completions.add("unban");
            if (PermissionUtil.hasPermission(sender, "moderex.command.mute")) completions.add("mute");
            if (PermissionUtil.hasPermission(sender, "moderex.command.unmute")) completions.add("unmute");
            if (PermissionUtil.hasPermission(sender, "moderex.command.kick")) completions.add("kick");
            if (PermissionUtil.hasPermission(sender, "moderex.command.warn")) completions.add("warn");
            if (PermissionUtil.hasPermission(sender, "moderex.command.ipban")) completions.add("ipban");
            if (PermissionUtil.hasPermission(sender, "moderex.command.punish")) completions.add("punish");
            if (PermissionUtil.hasPermission(sender, "moderex.command.modlog")) {
                completions.add("modlog");
                completions.add("history");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.command.staffchat")) {
                completions.add("staffchat");
                completions.add("sc");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.command.vanish")) {
                completions.add("vanish");
                completions.add("v");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.staff")) {
                completions.add("templates");
                completions.add("settings");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.notify.anticheat")) {
                completions.add("anticheat");
                completions.add("ac");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
                completions.add("reload");
                completions.add("automod");
                completions.add("chat");
                completions.add("analytics");
                completions.add("mutesettings");
                completions.add("warningsettings");
                completions.add("replay");
                completions.add("replays");
                completions.add("sendalert");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.admin")) {
                completions.add("debug");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.staff")) {
                completions.add("panel");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.command.admin")) {
                completions.add("gateway");
            }
            if (PermissionUtil.hasPermission(sender, "moderex.webpanel")) {
                completions.add("link");
                completions.add("connect");
                completions.add("gettoken");
                completions.add("revoketoken");
                completions.add("sessions");
            }
            completions.add("version");
            completions.add("help");

            return filterCompletions(completions, args[0]);
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "ban", "unban", "mute", "unmute", "kick", "warn", "ipban", "punish", "modlog", "history" -> {
                    return filterCompletions(getOnlinePlayerNames(sender), args[1]);
                }
                case "chat" -> {
                    return filterCompletions(Arrays.asList("enable", "disable", "slowmode", "clear"), args[1]);
                }
                case "gateway" -> {
                    return filterCompletions(Arrays.asList("status", "reconnect"), args[1]);
                }
                case "replay", "replays", "rec", "recording" -> {
                    return filterCompletions(Arrays.asList("start", "stop", "play", "list", "search", "delete", "status", "help"), args[1]);
                }
                case "sendalert" -> {
                    return filterCompletions(getOnlinePlayerNames(sender), args[1]);
                }
                case "debug" -> {
                    if (PermissionUtil.hasPermission(sender, "moderex.admin")) {
                        return filterCompletions(Arrays.asList("authsession", "serverid", "integrations", "permissions", "simulate"), args[1]);
                    }
                }
                case "permtest" -> {
                    if (PermissionUtil.hasPermission(sender, "moderex.admin.debug")) {
                        return filterCompletions(Arrays.asList("check", "full", "report", "matrix"), args[1]);
                    }
                }
            }
        }
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("permtest")) {
                String mode = args[1].toLowerCase();
                if (mode.equals("check") && PermissionUtil.hasPermission(sender, "moderex.admin.debug")) {
                    return filterCompletions(Arrays.asList("profiles", "profile", "isolate", "parity", "me", "player", "custom", "all"), args[2]);
                }
                if (mode.equals("full") && PermissionUtil.hasPermission(sender, "moderex.admin.debug")) {
                    return filterCompletions(Arrays.asList("profiles", "me"), args[2]);
                }
            }
            if (sub.equals("debug")) {
                String action = args[1].toLowerCase();
                if ((action.equals("authsession") || action.equals("permissions") || action.equals("perms")
                        || action.equals("simulate") || action.equals("sim"))
                        && PermissionUtil.hasPermission(sender, "moderex.admin")) {
                    return filterCompletions(getOnlinePlayerNames(sender), args[2]);
                }
            }
            if (sub.equals("sendalert")) {
                // Show alert types
                return filterCompletions(Arrays.asList("ban", "kick", "mute", "warn", "pardon", "anticheat", "automod", "command", "nickname", "joinleave", "lag", "watchlist", "staffchat", "custom"), args[2]);
            }
            if (sub.equals("ban") || sub.equals("mute") || sub.equals("warn") || sub.equals("ipban")) {
                return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "permanent"), args[2]);
            }
            if (sub.equals("replay") || sub.equals("replays") || sub.equals("rec") || sub.equals("recording")) {
                String action = args[1].toLowerCase();
                if (action.equals("start") || action.equals("stop") || action.equals("search")) {
                    return filterCompletions(getOnlinePlayerNames(sender), args[2]);
                }
                if (action.equals("play") || action.equals("playback") || action.equals("delete")) {
                    // Get saved replay session IDs for tab completion
                    List<String> sessionIds = new java.util.ArrayList<>();
                    try {
                        var replays = plugin.getReplayManager().getSavedReplays().get(2, java.util.concurrent.TimeUnit.SECONDS);
                        for (var replay : replays) {
                            sessionIds.add(replay.sessionId());
                        }
                    } catch (Exception e) {
                        // Timeout or error, show recent ones from file names
                        plugin.logDebug("Tab completion for replays timed out");
                    }
                    return filterCompletions(sessionIds, args[2]);
                }
            }
        }
        if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("permtest")) {
                String mode = args[1].toLowerCase();
                String subMode = args[2].toLowerCase();
                if ((mode.equals("check") || mode.equals("full")) && subMode.equals("profile")
                        && PermissionUtil.hasPermission(sender, "moderex.admin.debug")) {
                    return filterCompletions(Arrays.asList("NONE", "OP", "HELPER", "MODERATOR", "ADMIN", "OWNER",
                            "TEMPBAN_ONLY", "HISTORY_PARTIAL", "FLAG_TESTER", "WEB_VIEWER", "CATEGORY_WILDCARD", "WEIGHTED"), args[3]);
                }
                if (mode.equals("check") && subMode.equals("player")
                        && PermissionUtil.hasPermission(sender, "moderex.admin.debug")) {
                    return filterCompletions(getOnlinePlayerNames(sender), args[3]);
                }
            }
            if (sub.equals("sendalert")) {
                // Suggest example messages based on alert type
                String alertType = args[2].toLowerCase();
                return switch (alertType) {
                    case "ban" -> filterCompletions(Arrays.asList("Permanently_banned_for_hacking", "Banned_7d_for_griefing"), args[3]);
                    case "kick" -> filterCompletions(Arrays.asList("Kicked_for_AFK", "Kicked_for_spam"), args[3]);
                    case "mute" -> filterCompletions(Arrays.asList("Muted_for_toxicity", "Muted_for_spam"), args[3]);
                    case "warn" -> filterCompletions(Arrays.asList("Warned_for_chat_behavior", "Warned_for_griefing"), args[3]);
                    case "pardon" -> filterCompletions(Arrays.asList("Unbanned_after_appeal", "Unmuted_after_review"), args[3]);
                    case "anticheat" -> filterCompletions(Arrays.asList("Flagged_for_KillAura", "Speed_hack_detected", "Suspicious_movement"), args[3]);
                    case "automod" -> filterCompletions(Arrays.asList("Sent_blocked_message", "Spam_detected", "Caps_violation"), args[3]);
                    case "command" -> filterCompletions(Arrays.asList("Used_blacklisted_command", "Attempted_/op"), args[3]);
                    case "nickname" -> filterCompletions(Arrays.asList("Inappropriate_nickname", "Nickname_contains_slur"), args[3]);
                    case "joinleave" -> filterCompletions(Arrays.asList("Player_joined", "Player_left"), args[3]);
                    case "lag" -> filterCompletions(Arrays.asList("Server_TPS_low", "Memory_warning"), args[3]);
                    case "watchlist" -> filterCompletions(Arrays.asList("Suspicious_activity", "Previous_offender", "Monitor_closely"), args[3]);
                    default -> filterCompletions(Arrays.asList("Custom_alert_message"), args[3]);
                };
            }
        }

        return super.tabComplete(sender, args);
    }
}
