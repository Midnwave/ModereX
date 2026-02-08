package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.staff.StaffInspectGui;
import com.blockforge.moderex.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for staff mode events.
 */
public class StaffModeListener implements Listener {

    private final ModereX plugin;

    public StaffModeListener(ModereX plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getStaffModeManager().isInStaffMode(player)) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        int slot = player.getInventory().getHeldItemSlot();
        plugin.getStaffModeManager().handleItemClick(player, slot, null);

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getStaffModeManager().isInStaffMode(player)) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        int slot = player.getInventory().getHeldItemSlot();
        plugin.getStaffModeManager().handleItemClick(player, slot, target);

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getStaffModeManager().isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getStaffModeManager().isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (plugin.getStaffModeManager().isInStaffMode(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {
            if (plugin.getStaffModeManager().isInStaffMode(damager)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getView().getTitle().equals("Select Disguise Rank")) {
            return;
        }

        if (event.getView().getTitle().startsWith("Inspect: ")) {
            int slot = event.getRawSlot();
            if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) {
                // Click in player's own inventory while inspect GUI is open
                // Allow if staff can modify (e.g., shift-clicking items into inspect GUI)
                if (!PermissionUtil.hasPermission(player, "moderex.staff.inspect.modify")) {
                    event.setCancelled(true);
                }
                return;
            }

            String targetName = event.getView().getTitle().substring("Inspect: ".length());
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                event.setCancelled(true);
                return;
            }

            boolean canModify = PermissionUtil.hasPermission(player, "moderex.staff.inspect.modify");

            // Handle action buttons (row 5 special slots, row 6)
            if (slot == 42) {
                // Ender chest button
                event.setCancelled(true);
                player.openInventory(target.getEnderChest());
                return;
            }
            if (slot == 43) {
                // Refresh button
                event.setCancelled(true);
                StaffInspectGui gui = new StaffInspectGui(plugin, player, target);
                gui.open();
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
                return;
            }
            if (slot == 44) {
                // Close button
                event.setCancelled(true);
                player.closeInventory();
                return;
            }
            // Separator and info row - always cancel
            if (slot == 41 || slot >= 45) {
                event.setCancelled(true);
                return;
            }

            // Armor/offhand placeholder check (empty armor slots shown as glass)
            if (slot >= 36 && slot <= 40) {
                ItemStack clicked = event.getView().getTopInventory().getItem(slot);
                if (clicked != null && (clicked.getType() == org.bukkit.Material.LIGHT_GRAY_STAINED_GLASS_PANE
                        || clicked.getType() == org.bukkit.Material.GRAY_STAINED_GLASS_PANE)) {
                    if (canModify) {
                        // Remove the placeholder so item can be placed
                        event.getView().getTopInventory().setItem(slot, null);
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            // Inventory slots (0-35) and armor/offhand (36-40)
            if (canModify) {
                // Allow the item movement, then sync to target on next tick
                org.bukkit.inventory.Inventory topInv = event.getView().getTopInventory();
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!target.isOnline()) return;
                    org.bukkit.inventory.PlayerInventory playerInv = target.getInventory();

                    // Sync hotbar and main inventory
                    for (int i = 0; i <= 35; i++) {
                        playerInv.setItem(i, topInv.getItem(i));
                    }

                    // Sync armor
                    ItemStack helmet = topInv.getItem(36);
                    ItemStack chestplate = topInv.getItem(37);
                    ItemStack leggings = topInv.getItem(38);
                    ItemStack boots = topInv.getItem(39);
                    ItemStack offhand = topInv.getItem(40);

                    playerInv.setHelmet(isPlaceholderItem(helmet) ? null : helmet);
                    playerInv.setChestplate(isPlaceholderItem(chestplate) ? null : chestplate);
                    playerInv.setLeggings(isPlaceholderItem(leggings) ? null : leggings);
                    playerInv.setBoots(isPlaceholderItem(boots) ? null : boots);
                    playerInv.setItemInOffHand(isPlaceholderItem(offhand) ? new ItemStack(org.bukkit.Material.AIR) : offhand);
                }, 1L);
            } else {
                event.setCancelled(true);
            }

            return;
        }

        if (plugin.getStaffModeManager().isInStaffMode(player)) {
            if (event.getClickedInventory() == player.getInventory()) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isPlaceholderItem(ItemStack item) {
        if (item == null) return true;
        return item.getType() == org.bukkit.Material.LIGHT_GRAY_STAINED_GLASS_PANE
                || item.getType() == org.bukkit.Material.GRAY_STAINED_GLASS_PANE;
    }
}
