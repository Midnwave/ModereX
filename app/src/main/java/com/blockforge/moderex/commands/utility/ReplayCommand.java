package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.gui.ReplayGui;
import com.blockforge.moderex.replay.ReplaySession;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Standalone /replay and /replays command that mirrors /mx replay functionality.
 * Provides convenient aliases for replay system access.
 */
public class ReplayCommand extends BaseCommand {

    public ReplayCommand(ModereX plugin) {
        super(plugin, "moderex.command.admin", true);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, MessageKey.PLAYER_ONLY);
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
                    sendMessage(sender, "<red>Usage: /replay start <player>");
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
                sendMessage(sender, "<gray>Use /replay stop " + target.getName() + " to stop recording.");
            }
            case "stop" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /replay stop <player>");
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
                    sendMessage(sender, "<red>Usage: /replay play <sessionId>");
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
                    sendMessage(sender, "<red>Usage: /replay search <player>");
                    return;
                }
                @SuppressWarnings("deprecation")
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                plugin.getGuiManager().open(player, new ReplayGui(plugin, target.getUniqueId(),
                        target.getName() != null ? target.getName() : args[1]));
            }
            case "delete" -> {
                if (args.length < 2) {
                    sendMessage(sender, "<red>Usage: /replay delete <sessionId>");
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
                sendMessage(sender, "<gray>/replay <white>- Open replay browser GUI");
                sendMessage(sender, "<gray>/replay start <player> <white>- Start recording a player");
                sendMessage(sender, "<gray>/replay stop <player> <white>- Stop recording a player");
                sendMessage(sender, "<gray>/replay play <sessionId> <white>- Play back a replay");
                sendMessage(sender, "<gray>/replay list <white>- List saved replays");
                sendMessage(sender, "<gray>/replay search <player> <white>- Find replays for a player");
                sendMessage(sender, "<gray>/replay delete <sessionId> <white>- Delete a replay");
                sendMessage(sender, "<gray>/replay status <white>- Show system status");
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

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(
                    Arrays.asList("start", "stop", "play", "list", "search", "delete", "status", "help"),
                    args[0]);
        }
        if (args.length == 2) {
            String action = args[0].toLowerCase();
            if (action.equals("start") || action.equals("stop") || action.equals("search")) {
                return filterCompletions(getOnlinePlayerNames(sender), args[1]);
            }
            if (action.equals("play") || action.equals("playback") || action.equals("delete")) {
                // Get saved replay session IDs for tab completion
                List<String> sessionIds = new java.util.ArrayList<>();
                try {
                    var replays = plugin.getReplayManager().getSavedReplays().get(2, TimeUnit.SECONDS);
                    for (var replay : replays) {
                        sessionIds.add(replay.sessionId());
                    }
                } catch (Exception e) {
                    // Timeout or error
                    plugin.logDebug("Tab completion for replays timed out");
                }
                return filterCompletions(sessionIds, args[1]);
            }
        }
        return super.tabComplete(sender, args);
    }
}
