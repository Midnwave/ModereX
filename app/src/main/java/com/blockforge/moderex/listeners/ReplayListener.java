package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplayPlayback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Listener for handling player interactions during replay playback.
 * Manages hotbar control clicks and prevents inventory modifications.
 */
public class ReplayListener implements Listener {

    private final ModereX plugin;

    public ReplayListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            return;
        }

        // Cancel the event to prevent block placement/breaking
        event.setCancelled(true);

        // Handle hotbar clicks - only if player is actually holding a control item
        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.RIGHT_CLICK_BLOCK ||
            event.getAction() == Action.LEFT_CLICK_AIR ||
            event.getAction() == Action.LEFT_CLICK_BLOCK) {

            ReplayPlayback playback = plugin.getReplayManager().getPlayback(player.getUniqueId());
            if (playback != null) {
                // Check if player is holding a valid control item
                org.bukkit.inventory.ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (heldItem != null && heldItem.getType() != org.bukkit.Material.AIR && heldItem.hasItemMeta()) {
                    int slot = player.getInventory().getHeldItemSlot();
                    playback.handleHotbarClick(slot);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Stop playback if player quits while viewing
        if (plugin.getReplayManager().isViewingReplay(player.getUniqueId())) {
            plugin.getReplayManager().stopPlayback(player.getUniqueId());
        }
    }
}
