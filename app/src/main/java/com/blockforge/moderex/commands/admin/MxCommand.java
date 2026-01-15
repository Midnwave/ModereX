package com.blockforge.moderex.commands.admin;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.AnalyticsGui;
import com.blockforge.moderex.gui.AutomodGui;
import com.blockforge.moderex.gui.MainMenuGui;
import com.blockforge.moderex.gui.ReplayGui;
import com.blockforge.moderex.gui.StaffSettingsGui;
import com.blockforge.moderex.gui.punishment.PunishPlayerGui;
import com.blockforge.moderex.gui.ModLogGui;
import com.blockforge.moderex.replay.ReplaySession;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.replay.TestReplayGenerator;
import com.blockforge.moderex.util.DurationParser;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MxCommand extends BaseCommand {

    public MxCommand(ModereX plugin) {
        super(plugin, null, false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                if (player.hasPermission("moderex.command.punish") || player.hasPermission("moderex.command.admin")) {
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
            case "gettoken" -> handleGetToken(sender);
            case "gentoken" -> handleGenToken(sender, subArgs);
            case "revoketoken" -> handleRevokeToken(sender);
            case "sessions" -> handleSessions(sender);
            case "automod" -> handleAutomod(sender);
            case "chat" -> handleChat(sender, subArgs);
            case "settings" -> handleSettings(sender);
            case "analytics" -> handleAnalytics(sender);
            case "mutesettings" -> handleMuteSettings(sender);
            case "warningsettings" -> handleWarningSettings(sender);
            case "help" -> sendHelp(sender);
            case "testreplay" -> handleTestReplay(sender);
            case "replay", "replays", "rec", "recording" -> handleReplay(sender, subArgs);

            case "ban" -> handleBan(sender, subArgs);
            case "unban" -> handleUnban(sender, subArgs);
            case "mute" -> handleMute(sender, subArgs);
            case "unmute" -> handleUnmute(sender, subArgs);
            case "kick" -> handleKick(sender, subArgs);
            case "warn" -> handleWarn(sender, subArgs);
            case "clearwarnings" -> handleClearWarnings(sender, subArgs);
            case "ipban" -> handleIpBan(sender, subArgs);
            case "punish" -> handlePunish(sender, subArgs);
            case "modlog", "history" -> handleModLog(sender, subArgs);

            case "staffchat", "sc" -> handleStaffChat(sender, subArgs);
            case "vanish", "v" -> handleVanish(sender);

            default -> sendMessage(sender, "<red>Unknown subcommand: " + subcommand + ". Use /mx help for a list.");
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        plugin.reload();
        sendMessage(sender, MessageKey.RELOAD_SUCCESS);
    }

    private void handleTestReplay(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!sender.hasPermission("moderex.command.admin")) {
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

                // Save the replay
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

        if (!sender.hasPermission("moderex.command.admin")) {
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
                        plugin.getReplayManager().startPlayback(player, session);
                        sendMessage(sender, "<green>Starting playback...");
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

        if (!sender.hasPermission("moderex.webpanel")) {
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

        // Generate a temporary URL token for this player
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

        // Create clickable URL
        Component clickableUrl = TextUtil.parse("<green><bold>[CLICK HERE TO OPEN PANEL]</bold></green>")
                .clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Click to open web panel\n<yellow>" + url)));

        player.sendMessage(clickableUrl);

        sendMessage(sender, "");
        sendMessage(sender, "<gray>Or copy: <white>" + url);
        sendMessage(sender, "");
        sendMessage(sender, "<yellow>Link expires after <white>30 minutes<yellow> of inactivity.");
        sendMessage(sender, "<gray>For permanent access, use <white>/mx gettoken");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleGetToken(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        if (!sender.hasPermission("moderex.webpanel")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (plugin.getWebAuthManager() == null) {
            sendMessage(sender, "<red>Web authentication is not initialized.");
            return;
        }

        // Check if player already has a token
        if (plugin.getWebAuthManager().hasPermanentToken(player.getUniqueId())) {
            sendMessage(sender, "");
            sendMessage(sender, "<yellow>You already have a permanent token.");
            sendMessage(sender, "<gray>If you've lost it, use <white>/mx revoketoken<gray> first,");
            sendMessage(sender, "<gray>then run <white>/mx gettoken<gray> again to generate a new one.");
            sendMessage(sender, "");
            return;
        }

        // Generate permanent token
        String token = plugin.getWebAuthManager().generatePermanentToken(player.getUniqueId());

        // Create a secure book to display the token (not logged in chat)
        openTokenBook(player, token);

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<green>Your token has been generated!");
        sendMessage(sender, "<gray>A secure book has been opened with your token.");
        sendMessage(sender, "<yellow>Click the token in the book to copy it.");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleGenToken(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (plugin.getWebAuthManager() == null) {
            sendMessage(sender, "<red>Web authentication is not initialized.");
            return;
        }

        if (args.length < 1) {
            sendMessage(sender, "<yellow>Usage: /mx gentoken <uuid> [username]");
            sendMessage(sender, "<gray>Example: /mx gentoken 069a79f4-44e9-4726-a5be-fca90e38aaf5 Notch");
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            sendMessage(sender, "<red>Invalid UUID format. Use: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            return;
        }

        String username = args.length > 1 ? args[1] : "Unknown";

        // Check if already has token
        if (plugin.getWebAuthManager().hasPermanentToken(uuid)) {
            sendMessage(sender, "<yellow>UUID " + uuid + " already has a token.");
            sendMessage(sender, "<gray>Use /mx revoketoken to revoke it first (player must be online).");
            return;
        }
        String token = plugin.getWebAuthManager().generatePermanentToken(uuid);

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "<green>Token generated for: <white>" + username + " <gray>(" + uuid + ")");
        sendMessage(sender, "");
        sendMessage(sender, "<yellow>Token: <white>" + token);
        sendMessage(sender, "");
        sendMessage(sender, "<red><bold>WARNING:</bold> <gray>This token will not be shown again!");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void openTokenBook(Player player, String token) {
        // Page 1: Warning and instructions
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

        // Page 2: The actual token with copy button
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

        // Page 3: Additional info
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

        // Create and open the book
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

        if (!sender.hasPermission("moderex.webpanel")) {
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

        if (!sender.hasPermission("moderex.webpanel")) {
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
            player.sendMessage(revokeButton);
        } else {
            Component getTokenButton = TextUtil.parse("<green>[Generate Token]")
                    .clickEvent(ClickEvent.runCommand("/mx gettoken"))
                    .hoverEvent(HoverEvent.showText(TextUtil.parse("<gray>Generate a permanent access token")));
            player.sendMessage(getTokenButton);
        }

        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
        sendMessage(sender, "");
    }

    private void handleAutomod(CommandSender sender) {
        if (!sender.hasPermission("moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        plugin.getGuiManager().open(player, new AutomodGui(plugin));
    }

    private void handleChat(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.command.admin")) {
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
                        player.sendMessage("");
                    }
                    player.sendMessage(plugin.getLanguageManager().get(MessageKey.CHAT_CLEARED,
                            "player", sender.getName()));
                }
                sendMessage(sender, "<green>Chat cleared.");
            }
            default -> sendMessage(sender, "<red>Unknown chat subcommand: " + args[0]);
        }
    }

    private void handleSettings(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        plugin.getGuiManager().open(player, new StaffSettingsGui(plugin));
    }

    private void handleAnalytics(CommandSender sender) {
        if (!sender.hasPermission("moderex.command.admin")) {
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
        if (!sender.hasPermission("moderex.command.admin")) {
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
        if (!sender.hasPermission("moderex.command.admin")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
            return;
        }

        sendMessage(sender, "<yellow>Warning Settings GUI coming soon!");
    }

    // ==================== Moderation Commands ====================

    private void handleBan(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.command.ban")) {
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
        if (!sender.hasPermission("moderex.command.unban")) {
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
        if (!sender.hasPermission("moderex.command.mute")) {
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
        if (!sender.hasPermission("moderex.command.unmute")) {
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
        if (!sender.hasPermission("moderex.command.kick")) {
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
        if (!sender.hasPermission("moderex.command.warn")) {
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

        long duration = DurationParser.parse("30d"); // Default 30 day warning expiry
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

    private void handleClearWarnings(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.command.clearwarnings")) {
            sendMessage(sender, MessageKey.NO_PERMISSION);
            return;
        }

        if (args.length == 0) {
            sendMessage(sender, "<red>Usage: /mx clearwarnings <player>");
            return;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetUuid = target.getUniqueId();
        String displayName = target.getName() != null ? target.getName() : targetName;

        UUID staffUuid = sender instanceof Player p ? p.getUniqueId() : null;
        String staffName = sender.getName();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int cleared = plugin.getDatabaseManager().update("""
                        UPDATE moderex_warnings SET active = FALSE,
                        removed_by_uuid = ?, removed_by_name = ?, removed_at = ?
                        WHERE player_uuid = ? AND active = TRUE
                        """,
                        staffUuid != null ? staffUuid.toString() : null,
                        staffName,
                        System.currentTimeMillis(),
                        targetUuid.toString()
                );

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (cleared > 0) {
                        sendMessage(sender, MessageKey.WARN_CLEARED, "player", displayName);
                    } else {
                        sendMessage(sender, "<yellow>No active warnings found for " + displayName);
                    }
                });
            } catch (java.sql.SQLException e) {
                plugin.logError("Failed to clear warnings", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendMessage(sender, "<red>Failed to clear warnings."));
            }
        });
    }

    private void handleIpBan(CommandSender sender, String[] args) {
        if (!sender.hasPermission("moderex.command.ipban")) {
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
        if (!sender.hasPermission("moderex.command.punish")) {
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
        if (!sender.hasPermission("moderex.command.modlog")) {
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
        if (!sender.hasPermission("moderex.command.staffchat")) {
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
        if (!sender.hasPermission("moderex.command.vanish")) {
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
        sendMessage(sender, "<yellow>/mx clearwarnings <player> <gray>- Clear warnings");
        sendMessage(sender, "<yellow>/mx ipban <player> [duration] [reason] <gray>- IP ban a player");
        sendMessage(sender, "<yellow>/mx punish [player] <gray>- Open punishment GUI");
        sendMessage(sender, "<yellow>/mx modlog [player] <gray>- View moderation log");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Staff:</bold>");
        sendMessage(sender, "<yellow>/mx staffchat [message] <gray>- Toggle or send staff chat");
        sendMessage(sender, "<yellow>/mx vanish <gray>- Toggle vanish mode");
        sendMessage(sender, "");
        sendMessage(sender, "<gold><bold>Admin:</bold>");
        sendMessage(sender, "<yellow>/mx reload <gray>- Reload configuration");
        sendMessage(sender, "<yellow>/mx connect <gray>- Get quick web panel link (30m expiry)");
        sendMessage(sender, "<yellow>/mx gettoken <gray>- Generate permanent web access token");
        sendMessage(sender, "<yellow>/mx revoketoken <gray>- Revoke your permanent token");
        sendMessage(sender, "<yellow>/mx sessions <gray>- View web session status");
        sendMessage(sender, "<yellow>/mx chat <enable|disable|slowmode|clear> <gray>- Chat control");
        sendMessage(sender, "<yellow>/mx automod <gray>- Open automod settings");
        sendMessage(sender, "");
        sendMessage(sender, "<gradient:#a855f7:#ec4899>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>");
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new java.util.ArrayList<>();
            if (sender.hasPermission("moderex.command.ban")) completions.add("ban");
            if (sender.hasPermission("moderex.command.unban")) completions.add("unban");
            if (sender.hasPermission("moderex.command.mute")) completions.add("mute");
            if (sender.hasPermission("moderex.command.unmute")) completions.add("unmute");
            if (sender.hasPermission("moderex.command.kick")) completions.add("kick");
            if (sender.hasPermission("moderex.command.warn")) completions.add("warn");
            if (sender.hasPermission("moderex.command.clearwarnings")) completions.add("clearwarnings");
            if (sender.hasPermission("moderex.command.ipban")) completions.add("ipban");
            if (sender.hasPermission("moderex.command.punish")) completions.add("punish");
            if (sender.hasPermission("moderex.command.modlog")) {
                completions.add("modlog");
                completions.add("history");
            }
            if (sender.hasPermission("moderex.command.staffchat")) {
                completions.add("staffchat");
                completions.add("sc");
            }
            if (sender.hasPermission("moderex.command.vanish")) {
                completions.add("vanish");
                completions.add("v");
            }
            if (sender.hasPermission("moderex.command.admin")) {
                completions.add("reload");
                completions.add("automod");
                completions.add("chat");
                completions.add("settings");
                completions.add("analytics");
                completions.add("mutesettings");
                completions.add("warningsettings");
            }
            if (sender.hasPermission("moderex.webpanel")) {
                completions.add("connect");
                completions.add("gettoken");
                completions.add("revoketoken");
                completions.add("sessions");
            }
            completions.add("help");

            return filterCompletions(completions, args[0]);
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "ban", "unban", "mute", "unmute", "kick", "warn", "clearwarnings", "ipban", "punish", "modlog", "history" -> {
                    return filterCompletions(getOnlinePlayerNames(sender), args[1]);
                }
                case "chat" -> {
                    return filterCompletions(Arrays.asList("enable", "disable", "slowmode", "clear"), args[1]);
                }
            }
        }
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("ban") || sub.equals("mute") || sub.equals("warn") || sub.equals("ipban")) {
                return filterCompletions(Arrays.asList("1h", "1d", "7d", "30d", "permanent"), args[2]);
            }
        }

        return super.tabComplete(sender, args);
    }
}
