package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VanishListener implements Listener {

    private final ModereX plugin;
    private final Map<UUID, Long> lastSneakTime = new HashMap<>();
    private static final long DOUBLE_SNEAK_THRESHOLD = 500; // milliseconds

    public VanishListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (plugin.getVanishManager().isVanished(player)) {
                // Check if player has permission to be attacked by mobs while vanished
                if (!player.hasPermission("moderex.vanish.mobattack")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getVanishManager().isVanished(player)) {
                // Prevent damage to vanished players
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getVanishManager().isVanished(player)) {
                // Prevent hunger for vanished players
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (plugin.getVanishManager().isVanished(player)) {
                // Check if player has permission to pick up items while vanished
                if (!player.hasPermission("moderex.vanish.pickup")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVanishManager().isVanished(player)) {
            // Check if player has permission to pick up items while vanished
            if (!player.hasPermission("moderex.vanish.pickup")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Silent container opening for vanished players
        if (plugin.getVanishManager().isVanished(player) &&
                plugin.getVanishManager().isSilentContainersEnabled()) {

            if (event.getClickedBlock() != null) {
                switch (event.getClickedBlock().getType()) {
                    case CHEST, TRAPPED_CHEST, BARREL, SHULKER_BOX,
                            WHITE_SHULKER_BOX, ORANGE_SHULKER_BOX, MAGENTA_SHULKER_BOX,
                            LIGHT_BLUE_SHULKER_BOX, YELLOW_SHULKER_BOX, LIME_SHULKER_BOX,
                            PINK_SHULKER_BOX, GRAY_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX,
                            CYAN_SHULKER_BOX, PURPLE_SHULKER_BOX, BLUE_SHULKER_BOX,
                            BROWN_SHULKER_BOX, GREEN_SHULKER_BOX, RED_SHULKER_BOX,
                            BLACK_SHULKER_BOX, ENDER_CHEST -> {
                        // Paper API provides silent container opening
                        // This is handled by Paper automatically when
                        // the player is in spectator-like state
                    }
                    default -> {}
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVanishManager().isVanished(player)) {
            // Check if player has permission to chat while vanished
            if (!player.hasPermission("moderex.vanish.chat")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(net.kyori.adventure.text.Component.text("§cYou cannot chat while vanished!")));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVanishManager().isVanished(player)) {
            // Check if player has permission to place blocks while vanished
            if (!player.hasPermission("moderex.vanish.place")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(net.kyori.adventure.text.Component.text("§cYou cannot place blocks while vanished!")));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVanishManager().isVanished(player)) {
            // Check if player has permission to break blocks while vanished
            if (!player.hasPermission("moderex.vanish.break")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getLanguageManager().getPrefix()
                        .append(net.kyori.adventure.text.Component.text("§cYou cannot break blocks while vanished!")));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (plugin.getVanishManager().isVanished(player)) {
                // Check if player has permission to attack while vanished
                if (!player.hasPermission("moderex.vanish.attack")) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getLanguageManager().getPrefix()
                            .append(net.kyori.adventure.text.Component.text("§cYou cannot attack while vanished!")));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // Only process when starting to sneak, not when stopping
        if (!event.isSneaking()) {
            return;
        }

        // Check if player is vanished and has permission
        if (!plugin.getVanishManager().isVanished(player)) {
            return;
        }

        if (!plugin.getConfig().getBoolean("vanish.double-sneak-spectator", false)) {
            return;
        }

        if (!player.hasPermission("moderex.vanish.spectator")) {
            return;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long lastSneak = lastSneakTime.get(uuid);

        if (lastSneak != null && (now - lastSneak) < DOUBLE_SNEAK_THRESHOLD) {
            // Double sneak detected!
            plugin.getVanishManager().toggleSpectatorMode(player);
            lastSneakTime.remove(uuid); // Reset to prevent triple sneak
        } else {
            // First sneak
            lastSneakTime.put(uuid, now);

            // Clear after threshold to prevent stale data
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                Long currentTime = lastSneakTime.get(uuid);
                if (currentTime != null && currentTime.equals(now)) {
                    lastSneakTime.remove(uuid);
                }
            }, DOUBLE_SNEAK_THRESHOLD / 50 + 5); // Convert ms to ticks + buffer
        }
    }
}
