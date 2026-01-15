package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.gui.BaseGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class GuiListener implements Listener {

    private final ModereX plugin;

    public GuiListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // Check if we have a GUI registered for this player
        BaseGui gui = plugin.getGuiManager().getOpenGui(player);
        if (gui == null) {
            plugin.logDebug("[GUI] No GUI registered for " + player.getName() + " - ignoring click");
            return;
        }

        // Always cancel the event for our GUIs to prevent item movement
        event.setCancelled(true);

        // Get the clicked inventory
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) {
            plugin.logDebug("[GUI] Clicked inventory is null for " + player.getName());
            return;
        }

        // Only handle clicks on the top inventory (the GUI), not the player's inventory
        Inventory topInv = event.getView().getTopInventory();
        if (!clickedInv.equals(topInv)) {
            plugin.logDebug("[GUI] Click was in player inventory, not GUI - slot: " + event.getSlot());
            return;
        }

        // Verify the top inventory matches our GUI
        if (!topInv.equals(gui.getInventory())) {
            plugin.logDebug("[GUI] WARNING: Top inventory doesn't match GUI inventory for " + player.getName());
            plugin.logDebug("[GUI] Top inv hash: " + System.identityHashCode(topInv) +
                          ", GUI inv hash: " + System.identityHashCode(gui.getInventory()));
            return;
        }

        int slot = event.getSlot();
        if (slot < 0 || slot >= gui.getInventory().getSize()) {
            plugin.logDebug("[GUI] Invalid slot " + slot + " for GUI size " + gui.getInventory().getSize());
            return;
        }

        plugin.logDebug("[GUI] Processing click for " + player.getName() +
                       " in " + gui.getClass().getSimpleName() +
                       " slot " + slot + " click type " + event.getClick());

        // Handle the click
        try {
            gui.handleClick(slot, event.getClick());
        } catch (Exception e) {
            plugin.logError("[GUI] Error handling click in " + gui.getClass().getSimpleName(), e);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (plugin.getGuiManager().hasGuiOpen(player)) {
            // Prevent dragging items in GUI
            event.setCancelled(true);
            plugin.logDebug("[GUI] Cancelled drag event for " + player.getName());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        BaseGui gui = plugin.getGuiManager().getOpenGui(player);
        if (gui != null) {
            plugin.logDebug("[GUI] " + player.getName() + " closed " + gui.getClass().getSimpleName());
            plugin.getGuiManager().handleClose(player);
        }
    }
}
