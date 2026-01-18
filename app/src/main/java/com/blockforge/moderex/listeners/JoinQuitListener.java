package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.punishment.Punishment;
import com.blockforge.moderex.punishment.PunishmentType;
import net.kyori.adventure.text.Component;
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

        if (plugin.getConfigManager().getSettings().isVanishHideRealJoinLeave() &&
                plugin.getVanishManager().isVanished(player)) {
            event.joinMessage(Component.empty());
        }

        // Watchlist notification
        if (plugin.getWatchlistManager().isWatched(uuid)) {
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

        // Notify web panel of player join
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastPlayerJoin(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getConfigManager().getSettings().isVanishHideRealJoinLeave() &&
                plugin.getVanishManager().isVanished(player)) {
            event.quitMessage(Component.empty());
        }

        // Clear caches
        plugin.getPunishmentManager().unloadPlayerPunishments(uuid);
        plugin.getAutomodManager().clearPlayerData(uuid);
        plugin.getStaffChatManager().onPlayerQuit(player);
        plugin.getVanishManager().onPlayerQuit(player);

        // Watchlist notification
        if (plugin.getWatchlistManager().isWatched(uuid)) {
            plugin.getWatchlistManager().onPlayerQuit(player);
        }

        // Notify web panel of player quit
        if (plugin.getWebPanelServer() != null) {
            plugin.getWebPanelServer().broadcastPlayerQuit(player);
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
