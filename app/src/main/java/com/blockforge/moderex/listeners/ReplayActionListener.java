package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplaySnapshot.ActionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for capturing player actions during replay recording.
 * Records various player activities beyond just movement for detailed replay logs.
 */
public class ReplayActionListener implements Listener {

    private final ModereX plugin;

    public ReplayActionListener(ModereX plugin) {
        this.plugin = plugin;
    }

    // ===== MOVEMENT ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ActionType action = event.isSneaking() ? ActionType.SNEAK_START : ActionType.SNEAK_END;
        plugin.getReplayManager().recordAction(player, action, null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ActionType action = event.isSprinting() ? ActionType.SPRINT_START : ActionType.SPRINT_END;
        plugin.getReplayManager().recordAction(player, action, null);
    }

    // ===== INTERACTION ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        // Record arm swing
        if (event.getAction().name().contains("LEFT_CLICK")) {
            plugin.getReplayManager().recordAction(player, ActionType.SWING_ARM, null);
        }

        // Record item use (right click with item)
        ItemStack item = event.getItem();
        if (item != null && event.getAction().name().contains("RIGHT_CLICK")) {
            String itemName = item.getType().name();
            plugin.getReplayManager().recordAction(player, ActionType.ITEM_USE, itemName);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            plugin.getReplayManager().recordAction(player, ActionType.SWING_ARM, null);
        }
    }

    // ===== INVENTORY ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String invType = event.getInventory().getType().name();
        String title = event.getView().title().toString();
        plugin.getReplayManager().recordAction(player, ActionType.INVENTORY_OPEN, invType + ": " + title);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String invType = event.getInventory().getType().name();
        plugin.getReplayManager().recordAction(player, ActionType.INVENTORY_CLOSE, invType);
    }

    // ===== ITEM ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ItemStack item = event.getItem().getItemStack();
        String itemInfo = item.getAmount() + "x " + item.getType().name();
        plugin.getReplayManager().recordAction(player, ActionType.ITEM_PICKUP, itemInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ItemStack item = event.getItemDrop().getItemStack();
        String itemInfo = item.getAmount() + "x " + item.getType().name();
        plugin.getReplayManager().recordAction(player, ActionType.DROP_ITEM, itemInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ItemStack item = event.getItem();
        plugin.getReplayManager().recordAction(player, ActionType.CONSUME_ITEM, item.getType().name());
    }

    // ===== COMBAT ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ItemStack bow = event.getBow();
        String bowType = bow != null ? bow.getType().name() : "BOW";
        String force = String.format("%.0f%%", event.getForce() * 100);
        plugin.getReplayManager().recordAction(player, ActionType.BOW_SHOOT, bowType + " (" + force + " power)");
    }

    // ===== COMMAND & CHAT =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String command = event.getMessage();
        // Don't log sensitive commands
        if (command.toLowerCase().startsWith("/login") ||
            command.toLowerCase().startsWith("/register") ||
            command.toLowerCase().startsWith("/l ") ||
            command.toLowerCase().startsWith("/reg ")) {
            plugin.getReplayManager().recordAction(player, ActionType.COMMAND, "[REDACTED AUTH COMMAND]");
        } else {
            plugin.getReplayManager().recordAction(player, ActionType.COMMAND, command);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        // Schedule on main thread since we're in async
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getReplayManager().recordAction(player, ActionType.CHAT, event.getMessage());
        });
    }

    // ===== LIFE EVENTS =====

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String deathMessage = event.getDeathMessage() != null ? event.getDeathMessage() : "Unknown cause";
        plugin.getReplayManager().recordAction(player, ActionType.DEATH, deathMessage);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String location = String.format("%.1f, %.1f, %.1f",
            event.getRespawnLocation().getX(),
            event.getRespawnLocation().getY(),
            event.getRespawnLocation().getZ());
        plugin.getReplayManager().recordAction(player, ActionType.RESPAWN, location);
    }

    // ===== TELEPORT & PORTALS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String cause = event.getCause().name();
        String from = formatLocation(event.getFrom());
        String to = formatLocation(event.getTo());
        plugin.getReplayManager().recordAction(player, ActionType.TELEPORT, cause + ": " + from + " -> " + to);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        String cause = event.getCause().name();
        plugin.getReplayManager().recordAction(player, ActionType.PORTAL_ENTER, cause);
    }

    // ===== FISHING =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        switch (event.getState()) {
            case FISHING -> plugin.getReplayManager().recordAction(player, ActionType.FISH_CAST, null);
            case CAUGHT_FISH -> {
                if (event.getCaught() != null) {
                    plugin.getReplayManager().recordAction(player, ActionType.FISH_REEL,
                        "Caught: " + event.getCaught().getType().name());
                }
            }
            case REEL_IN -> plugin.getReplayManager().recordAction(player, ActionType.FISH_REEL, "Reeled in");
            default -> {}
        }
    }

    // ===== BLOCK ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        Material type = event.getBlockPlaced().getType();
        String location = formatBlockLocation(event.getBlockPlaced().getLocation());
        plugin.getReplayManager().recordAction(player, ActionType.PLACE_BLOCK, type.name() + " at " + location);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        Material type = event.getBlock().getType();
        String location = formatBlockLocation(event.getBlock().getLocation());
        plugin.getReplayManager().recordAction(player, ActionType.BREAK_BLOCK, type.name() + " at " + location);
    }

    // ===== UTILITY METHODS =====

    private String formatLocation(org.bukkit.Location loc) {
        if (loc == null) return "null";
        return String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ());
    }

    private String formatBlockLocation(org.bukkit.Location loc) {
        if (loc == null) return "null";
        return String.format("%d, %d, %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
