package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplaySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractionListener implements Listener {

    private final ModereX plugin;

    public PlayerInteractionListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (plugin.getReplayManager() != null) {
            String data = event.getBlock().getType().name() + " at " +
                    event.getBlock().getX() + "," +
                    event.getBlock().getY() + "," +
                    event.getBlock().getZ();
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.BREAK_BLOCK, data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (plugin.getReplayManager() != null) {
            String data = event.getBlock().getType().name() + " at " +
                    event.getBlock().getX() + "," +
                    event.getBlock().getY() + "," +
                    event.getBlock().getZ();
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.PLACE_BLOCK, data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (plugin.getReplayManager() != null && event.getClickedBlock() != null) {
            String data = event.getAction().name() + " " +
                    event.getClickedBlock().getType().name() + " at " +
                    event.getClickedBlock().getX() + "," +
                    event.getClickedBlock().getY() + "," +
                    event.getClickedBlock().getZ();
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.INTERACT, data);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Record when player deals damage
        if (event.getDamager() instanceof Player attacker) {
            if (plugin.getReplayManager() != null) {
                String data = "Dealt " + String.format("%.1f", event.getFinalDamage()) +
                        " dmg to " + event.getEntity().getType().name();
                plugin.getReplayManager().recordAction(attacker, ReplaySnapshot.ActionType.DAMAGE_DEALT, data);
            }
        }

        // Record when player receives damage
        if (event.getEntity() instanceof Player victim) {
            if (plugin.getReplayManager() != null) {
                String attackerName = event.getDamager() instanceof Player ?
                        ((Player) event.getDamager()).getName() : event.getDamager().getType().name();
                String data = "Received " + String.format("%.1f", event.getFinalDamage()) +
                        " dmg from " + attackerName;
                plugin.getReplayManager().recordAction(victim, ReplaySnapshot.ActionType.DAMAGE_RECEIVED, data);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (plugin.getReplayManager() != null) {
            String data = event.getItemDrop().getItemStack().getType().name() +
                    " x" + event.getItemDrop().getItemStack().getAmount();
            plugin.getReplayManager().recordAction(player, ReplaySnapshot.ActionType.DROP_ITEM, data);
        }
    }
}
