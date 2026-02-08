package com.blockforge.moderex.commands.utility;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.commands.BaseCommand;
import com.blockforge.moderex.gui.WatchlistGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WatchlistCommand extends BaseCommand {

    public WatchlistCommand(ModereX plugin) {
        super(plugin, "moderex.command.watchlist", false);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                plugin.getGuiManager().open(player, new WatchlistGui(plugin));
            } else {
                showUsage(sender);
            }
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "gui" -> {
                if (!(sender instanceof Player player)) {
                    sendMessage(sender, "<red>This command can only be used by players.");
                    return;
                }
                plugin.getGuiManager().open(player, new WatchlistGui(plugin));
            }
            case "add" -> handleAdd(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "list" -> handleList(sender);
            case "note" -> handleNote(sender, args);
            case "check" -> handleCheck(sender, args);
            default -> showUsage(sender);
        }
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /watchlist add <player> [reason]");
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (target.getUniqueId() == null) {
            sendMessage(sender, "<red>Player not found: " + targetName);
            return;
        }

        if (plugin.getWatchlistManager().isWatched(target.getUniqueId())) {
            sendMessage(sender, "<yellow>" + targetName + " is already on the watchlist.");
            return;
        }

        String reason = args.length > 2 ? joinArgs(args, 2) : "Added via command";
        UUID addedByUuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        String addedByName = sender.getName();

        plugin.getWatchlistManager().addToWatchlist(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : targetName,
                addedByUuid,
                addedByName,
                reason
        ).thenAccept(success -> {
            if (success) {
                sendMessage(sender, "<green>Added <white>" + targetName + "</white> to the watchlist.");
                // Broadcast update to web panel
                if (plugin.getWebPanelServer() != null) {
                    plugin.getWebPanelServer().broadcastWatchlistUpdate();
                }
            } else {
                sendMessage(sender, "<red>Failed to add player to watchlist.");
            }
        });
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /watchlist remove <player>");
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!plugin.getWatchlistManager().isWatched(target.getUniqueId())) {
            sendMessage(sender, "<yellow>" + targetName + " is not on the watchlist.");
            return;
        }

        plugin.getWatchlistManager().removeFromWatchlist(target.getUniqueId()).thenAccept(success -> {
            if (success) {
                sendMessage(sender, "<green>Removed <white>" + targetName + "</white> from the watchlist.");
                // Broadcast update to web panel
                if (plugin.getWebPanelServer() != null) {
                    plugin.getWebPanelServer().broadcastWatchlistUpdate();
                }
            } else {
                sendMessage(sender, "<red>Failed to remove player from watchlist.");
            }
        });
    }

    private void handleList(CommandSender sender) {
        var watchedPlayers = plugin.getWatchlistManager().getWatchedPlayers();

        if (watchedPlayers.isEmpty()) {
            sendMessage(sender, "<yellow>The watchlist is empty.");
            return;
        }

        sendMessage(sender, "<gold>Watchlist (" + watchedPlayers.size() + " players):");
        for (UUID uuid : watchedPlayers) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName() != null ? player.getName() : uuid.toString().substring(0, 8);
            boolean online = player.isOnline();
            sendMessage(sender, "<gray> - <white>" + name + (online ? " <green>(online)" : " <gray>(offline)"));
        }
    }

    private void handleNote(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /watchlist note <player> [new note]");
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!plugin.getWatchlistManager().isWatched(target.getUniqueId())) {
            sendMessage(sender, "<yellow>" + targetName + " is not on the watchlist.");
            return;
        }

        if (args.length == 2) {
            // Show current note
            plugin.getWatchlistManager().getNote(target.getUniqueId()).thenAccept(note -> {
                if (note != null && !note.isEmpty()) {
                    sendMessage(sender, "<gold>Note for " + targetName + ": <white>" + note);
                } else {
                    sendMessage(sender, "<yellow>No note set for " + targetName);
                }
            });
        } else {
            // Update note
            String newNote = joinArgs(args, 2);
            plugin.getWatchlistManager().updateNote(target.getUniqueId(), newNote).thenAccept(success -> {
                if (success) {
                    sendMessage(sender, "<green>Updated note for " + targetName);
                } else {
                    sendMessage(sender, "<red>Failed to update note.");
                }
            });
        }
    }

    private void handleCheck(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Usage: /watchlist check <player>");
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        boolean watched = plugin.getWatchlistManager().isWatched(target.getUniqueId());
        if (watched) {
            sendMessage(sender, "<green>" + targetName + " is on the watchlist.");
            plugin.getWatchlistManager().getNote(target.getUniqueId()).thenAccept(note -> {
                if (note != null && !note.isEmpty()) {
                    sendMessage(sender, "<gray>Note: <white>" + note);
                }
            });
        } else {
            sendMessage(sender, "<yellow>" + targetName + " is not on the watchlist.");
        }
    }

    private void showUsage(CommandSender sender) {
        sendMessage(sender, "<gold>Watchlist Commands:");
        sendMessage(sender, "<yellow>/watchlist <gray>- Open the watchlist GUI");
        sendMessage(sender, "<yellow>/watchlist gui <gray>- Open the watchlist GUI");
        sendMessage(sender, "<yellow>/watchlist add <player> [reason] <gray>- Add a player");
        sendMessage(sender, "<yellow>/watchlist remove <player> <gray>- Remove a player");
        sendMessage(sender, "<yellow>/watchlist list <gray>- List all watched players");
        sendMessage(sender, "<yellow>/watchlist note <player> [note] <gray>- View/set note");
        sendMessage(sender, "<yellow>/watchlist check <player> <gray>- Check if player is watched");
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return filterCompletions(Arrays.asList("gui", "add", "remove", "list", "note", "check"), args[0]);
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("add")) {
                // Suggest online players not on watchlist
                return Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !plugin.getWatchlistManager().isWatched(p.getUniqueId()))
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (sub.equals("remove") || sub.equals("note") || sub.equals("check")) {
                // Suggest watched players
                return plugin.getWatchlistManager().getWatchedPlayers().stream()
                        .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                        .filter(name -> name != null && name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return super.tabComplete(sender, args);
    }
}
