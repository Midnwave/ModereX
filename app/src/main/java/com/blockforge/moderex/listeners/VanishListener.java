package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class VanishListener implements Listener {

    private final ModereX plugin;

    public VanishListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (plugin.getVanishManager().isVanished(player)) {
                event.setCancelled(true);
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
                // Prevent item pickup while vanished
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        if (plugin.getVanishManager().isVanished(player)) {
            event.setCancelled(true);
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
}
