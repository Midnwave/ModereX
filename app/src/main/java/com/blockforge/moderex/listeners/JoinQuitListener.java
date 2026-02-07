package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    private final ModereX plugin;

    public JoinQuitListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String ip = event.getAddress().getHostAddress();

        // Check if banned
        Punishment ban = plugin.getPunishmentManager().getActivePunishment(uuid, PunishmentType.BAN);
        if (ban != null && !ban.isExpired()) {
            Component kickMessage = plugin.getPunishmentManager().buildDisconnectMessage(ban);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
            return;
        }

        // Check if IP banned
        if (plugin.getPunishmentManager().isIpBanned(ip)) {
            // Get the IP ban punishment for message
            Punishment ipBan = getIpBanPunishment(ip);
            if (ipBan != null) {
                Component kickMessage = plugin.getPunishmentManager().buildDisconnectMessage(ipBan);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                        Component.text("You are IP banned from this server."));
            }
        }
    }

    private Punishment getIpBanPunishment(String ip) {
        try {
            return plugin.getDatabaseManager().query("""
                    SELECT * FROM moderex_punishments
                    WHERE ip_address = ? AND type = 'IPBAN' AND active = TRUE
                    AND (expires_at = -1 OR expires_at > ?)
                    ORDER BY created_at DESC LIMIT 1
                    """,
                    rs -> {
                        if (rs.next()) {
                            Punishment p = new Punishment();
                            p.setCaseId(rs.getString("case_id"));
                            p.setPlayerUuid(UUID.fromString(rs.getString("player_uuid")));
                            p.setPlayerName(rs.getString("player_name"));
                            p.setType(PunishmentType.IPBAN);
                            p.setReason(rs.getString("reason"));
                            p.setStaffName(rs.getString("staff_name"));
                            p.setCreatedAt(rs.getLong("created_at"));
                            p.setExpiresAt(rs.getLong("expires_at"));
                            return p;
                        }
                        return null;
                    },
                    ip, System.currentTimeMillis()
            );
        } catch (Exception e) {
            plugin.logError("Failed to get IP ban punishment", e);
            return null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Load punishments into cache
        plugin.getPunishmentManager().loadPlayerPunishments(uuid);

        plugin.getVanishManager().onPlayerJoin(player);
        plugin.getDisguiseManager().onPlayerJoin(player);

        // Handle join message visibility
        var settings = plugin.getConfigManager().getSettings();
        boolean isVanished = plugin.getVanishManager().isVanished(player);
        boolean isWatched = plugin.getWatchlistManager().isWatched(uuid);

        // Always suppress vanilla join message if configured
        if (settings.isJoinLeaveSuppressVanilla() ||
            (settings.isVanishHideRealJoinLeave() && isVanished)) {
            Msg.setJoinMessage(event, Component.empty());
        }

        // Send custom ModereX join message based on visibility (but not for vanished players)
        if (!isVanished) {
            String visibility = settings.getJoinLeaveVisibility();

            // If player is on watchlist, only show watchlist notification (not regular join)
            if (!isWatched || !"ALL".equalsIgnoreCase(visibility)) {
                sendJoinMessage(player, visibility);
            }
        }

        // Watchlist notification - always send regardless of visibility setting
        if (isWatched) {
            plugin.getWatchlistManager().onPlayerJoin(player);
        }

        // Update checker notification for admins
        if (player.hasPermission("moderex.admin")) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                // Notify about updates if available
            }, 40L); // 2 seconds delay
        }

        // Save/update player data
        savePlayerData(player);

        // Update player profile cache
        plugin.getPlayerProfileManager().handlePlayerJoin(player);

        // Notify web panel of player join - always regardless of visibility
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastPlayerJoin(player);
        }
    }

    private void sendJoinMessage(Player player, String visibility) {
        if ("OFF".equalsIgnoreCase(visibility)) {
            return; // No join message
        }

        var settings = plugin.getConfigManager().getSettings();
        String format = settings.getJoinLeaveJoinFormat();
        String prefix = plugin.getPlayerProfileManager().getPrefix(player);

        Component message = TextUtil.parse(format
                .replace("<player>", player.getName())
                .replace("<player_prefix>", prefix != null ? prefix : ""));

        if ("MODERATORS_ONLY".equalsIgnoreCase(visibility)) {
            // Only send to staff
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("moderex.notify.joinleave")) {
                    Msg.send(staff, message);
                }
            }
        } else {
            // Send to all players (ALL)
            for (Player online : Bukkit.getOnlinePlayers()) {
                Msg.send(online, message);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        var settings = plugin.getConfigManager().getSettings();
        boolean isVanished = plugin.getVanishManager().isVanished(player);
        boolean isWatched = plugin.getWatchlistManager().isWatched(uuid);

        // Always suppress vanilla quit message if configured
        if (settings.isJoinLeaveSuppressVanilla() ||
            (settings.isVanishHideRealJoinLeave() && isVanished)) {
            Msg.setQuitMessage(event, Component.empty());
        }

        // Send custom ModereX leave message based on visibility (but not for vanished players)
        if (!isVanished) {
            String visibility = settings.getJoinLeaveVisibility();

            // If player is on watchlist, only show watchlist notification (not regular leave)
            if (!isWatched || !"ALL".equalsIgnoreCase(visibility)) {
                sendLeaveMessage(player, visibility);
            }
        }

        // Clear caches
        plugin.getPunishmentManager().unloadPlayerPunishments(uuid);
        plugin.getAutomodManager().clearPlayerData(uuid);
        plugin.getStaffChatManager().onPlayerQuit(player);
        plugin.getVanishManager().onPlayerQuit(player);
        plugin.getDisguiseManager().onPlayerQuit(player);

        // Watchlist notification - always send regardless of visibility setting
        if (isWatched) {
            plugin.getWatchlistManager().onPlayerQuit(player);
        }

        // Notify web panel of player quit - always regardless of visibility
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastPlayerQuit(player);
        }
    }

    private void sendLeaveMessage(Player player, String visibility) {
        if ("OFF".equalsIgnoreCase(visibility)) {
            return; // No leave message
        }

        var settings = plugin.getConfigManager().getSettings();
        String format = settings.getJoinLeaveLeaveFormat();
        String prefix = plugin.getPlayerProfileManager().getPrefix(player);

        Component message = TextUtil.parse(format
                .replace("<player>", player.getName())
                .replace("<player_prefix>", prefix != null ? prefix : ""));

        if ("MODERATORS_ONLY".equalsIgnoreCase(visibility)) {
            // Only send to staff
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("moderex.notify.joinleave")) {
                    Msg.send(staff, message);
                }
            }
        } else {
            // Send to all players (ALL)
            for (Player online : Bukkit.getOnlinePlayers()) {
                Msg.send(online, message);
            }
        }
    }

    private void savePlayerData(Player player) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String ip = player.getAddress() != null ?
                        player.getAddress().getAddress().getHostAddress() : null;

                plugin.getDatabaseManager().update("""
                        INSERT INTO moderex_players (uuid, username, ip_address, first_join, last_join, last_server)
                        VALUES (?, ?, ?, ?, ?, ?)
                        ON CONFLICT(uuid) DO UPDATE SET
                            username = excluded.username,
                            ip_address = excluded.ip_address,
                            last_join = excluded.last_join,
                            last_server = excluded.last_server
                        """,
                        player.getUniqueId().toString(),
                        player.getName(),
                        ip,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        plugin.getServer().getName()
                );
            } catch (Exception e) {
                plugin.logError("Failed to save player data", e);
            }
        });
    }
}
