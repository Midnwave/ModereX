package com.blockforge.moderex.watchlist;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.punishment.Punishment;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class WatchlistManager {

    private final ModereX plugin;
    private final Set<UUID> watchedPlayers = ConcurrentHashMap.newKeySet();

    public WatchlistManager(ModereX plugin) {
        this.plugin = plugin;
        loadWatchlist();
    }

    private void loadWatchlist() {
        CompletableFuture.runAsync(() -> {
            try {
                plugin.getDatabaseManager().query("""
                        SELECT player_uuid FROM moderex_watchlist WHERE active = TRUE
                        """,
                        rs -> {
                            while (rs.next()) {
                                watchedPlayers.add(UUID.fromString(rs.getString("player_uuid")));
                            }
                            return null;
                        }
                );
                plugin.getLogger().info("Loaded " + watchedPlayers.size() + " players to watchlist.");
            } catch (SQLException e) {
                plugin.logError("Failed to load watchlist", e);
            }
        });
    }

    public CompletableFuture<Boolean> addToWatchlist(UUID playerUuid, String playerName,
                                                      UUID addedByUuid, String addedByName, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_watchlist (player_uuid, player_name, added_by_uuid, added_by_name, reason, added_at, active)
                        VALUES (?, ?, ?, ?, ?, ?, TRUE)
                        """,
                        playerUuid.toString(), playerName,
                        addedByUuid != null ? addedByUuid.toString() : null,
                        addedByName, reason, System.currentTimeMillis()
                );
                watchedPlayers.add(playerUuid);
                return true;
            } catch (SQLException e) {
                plugin.logError("Failed to add to watchlist", e);
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> removeFromWatchlist(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                plugin.getDatabaseManager().update("""
                        UPDATE moderex_watchlist SET active = FALSE WHERE player_uuid = ?
                        """,
                        playerUuid.toString()
                );
                watchedPlayers.remove(playerUuid);
                return true;
            } catch (SQLException e) {
                plugin.logError("Failed to remove from watchlist", e);
                return false;
            }
        });
    }

    public boolean isWatched(UUID playerUuid) {
        return watchedPlayers.contains(playerUuid);
    }

    public CompletableFuture<Boolean> updateNote(UUID playerUuid, String note) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int updated = plugin.getDatabaseManager().update("""
                        UPDATE moderex_watchlist SET reason = ? WHERE player_uuid = ? AND active = TRUE
                        """,
                        note, playerUuid.toString()
                );
                return updated > 0;
            } catch (SQLException e) {
                plugin.logError("Failed to update watchlist note", e);
                return false;
            }
        });
    }

    public CompletableFuture<String> getNote(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return plugin.getDatabaseManager().query("""
                        SELECT reason FROM moderex_watchlist WHERE player_uuid = ? AND active = TRUE
                        """,
                        rs -> rs.next() ? rs.getString("reason") : null,
                        playerUuid.toString()
                );
            } catch (SQLException e) {
                plugin.logError("Failed to get watchlist note", e);
                return null;
            }
        });
    }

    public Set<UUID> getWatchedPlayers() {
        return new HashSet<>(watchedPlayers);
    }

    public void onPlayerJoin(Player player) {
        if (!isWatched(player.getUniqueId())) return;

        alertStaff("joined the server", player.getName());
        alertWebPanel("Player Join", player.getName(), "joined the server");
    }

    public void onPlayerQuit(Player player) {
        if (!isWatched(player.getUniqueId())) return;

        alertStaff("left the server", player.getName());
        alertWebPanel("Player Quit", player.getName(), "left the server");
    }

    public void onCommand(Player player, String command) {
        if (!isWatched(player.getUniqueId())) return;

        alertStaff("ran command: " + command, player.getName());
        alertWebPanel("Command", player.getName(), "ran: " + command);
    }

    public void onSignPlace(Player player, String[] lines) {
        if (!isWatched(player.getUniqueId())) return;

        String signText = String.join(" | ", lines);
        alertStaff("placed sign: " + signText, player.getName());
        alertWebPanel("Sign Place", player.getName(), "placed sign: " + signText);
    }

    public void onAutomod(Player player, String rule, String message) {
        if (!isWatched(player.getUniqueId())) return;

        alertStaff("triggered automod (" + rule + "): " + message, player.getName());
        alertWebPanel("Automod", player.getName(), "triggered " + rule);
    }

    public void onPunishment(Punishment punishment) {
        if (!isWatched(punishment.getPlayerUuid())) return;

        String action = "was " + punishment.getType().getPastTense() + " by " +
                punishment.getStaffName() + " - " + punishment.getReason();
        alertStaff(action, punishment.getPlayerName());
        alertWebPanel("Punishment", punishment.getPlayerName(), action);
    }

    public void onAnticheatAlert(Player player, String alertDetails) {
        if (!isWatched(player.getUniqueId())) return;

        alertStaff("triggered anticheat: " + alertDetails, player.getName());
        alertWebPanel("Anticheat", player.getName(), alertDetails);
    }

    private void alertStaff(String action, String playerName) {
        Component message = plugin.getLanguageManager().get(MessageKey.WATCHLIST_ALERT,
                "player", playerName,
                "action", action
        );

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("moderex.notify.punishments")) {
                staff.sendMessage(message);
            }
        }
    }

    private void alertWebPanel(String type, String playerName, String details) {
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastWatchlistAlert(type, playerName, details);
        }
    }
}
