package com.blockforge.moderex.automod;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.config.lang.MessageKey;
import com.blockforge.moderex.util.Msg;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages AFK detection and automatic kicks for inactive players.
 */
public class AfkManager implements Listener {

    private final ModereX plugin;
    private final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastLocation = new ConcurrentHashMap<>();
    private BukkitRunnable checkTask;

    public AfkManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Start the AFK check task.
     */
    public void start() {
        if (checkTask != null) {
            checkTask.cancel();
        }

        // Check every 30 seconds
        checkTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkAfkPlayers();
            }
        };
        checkTask.runTaskTimer(plugin, 600L, 600L); // 30 seconds
    }

    /**
     * Stop the AFK check task.
     */
    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
        }
    }

    /**
     * Check all players for AFK status.
     */
    private void checkAfkPlayers() {
        AutomodRule afkRule = plugin.getAutomodManager().getRule("afk_kick");
        if (afkRule == null || !afkRule.isEnabled() || !afkRule.isAfkKickEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        long timeoutMs = afkRule.getAfkTimeoutMinutes() * 60 * 1000L;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            // Skip players with bypass permission
            if (player.hasPermission("moderex.bypass.afk")) {
                continue;
            }

            UUID uuid = player.getUniqueId();
            Long lastTime = lastActivity.get(uuid);

            if (lastTime == null) {
                lastActivity.put(uuid, now);
                lastLocation.put(uuid, player.getLocation().clone());
                continue;
            }

            // Check if player has moved
            Location lastLoc = lastLocation.get(uuid);
            Location currentLoc = player.getLocation();

            if (lastLoc != null && hasMovedSignificantly(lastLoc, currentLoc)) {
                // Player moved, reset timer
                lastActivity.put(uuid, now);
                lastLocation.put(uuid, currentLoc.clone());
                continue;
            }

            // Check timeout
            if (now - lastTime >= timeoutMs) {
                // Kick for AFK
                final UUID playerUuid = uuid;
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) return;

                    Msg.kick(player, plugin.getLanguageManager().get(MessageKey.AFK_KICK_MESSAGE));

                    // Notify staff
                    for (Player staff : plugin.getServer().getOnlinePlayers()) {
                        if (staff.hasPermission("moderex.notify.afk")) {
                            Msg.send(staff, plugin.getLanguageManager().get(MessageKey.AFK_KICK_BROADCAST,
                                    "player", player.getName()));
                        }
                    }
                });

                lastActivity.remove(playerUuid);
                lastLocation.remove(playerUuid);
            }
        }
    }

    /**
     * Check if player has moved significantly (not just head rotation).
     */
    private boolean hasMovedSignificantly(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) return true;

        double distanceSquared = from.distanceSquared(to);
        return distanceSquared > 0.5; // More than ~0.7 blocks
    }

    /**
     * Record activity for a player.
     */
    public void recordActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        lastLocation.put(player.getUniqueId(), player.getLocation().clone());
    }

    /**
     * Clean up data for a player.
     */
    public void cleanup(UUID uuid) {
        lastActivity.remove(uuid);
        lastLocation.remove(uuid);
    }

    // === Event Listeners ===

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only count actual movement, not just head rotation
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            recordActivity(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        recordActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        recordActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        recordActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        recordActivity(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        cleanup(event.getPlayer().getUniqueId());
    }
}
