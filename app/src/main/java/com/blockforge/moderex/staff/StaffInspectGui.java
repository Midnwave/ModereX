package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GUI for inspecting and managing a player.
 */
public class StaffInspectGui {

    private final ModereX plugin;
    private final Player staff;
    private final Player target;
    private final Inventory inventory;

    public StaffInspectGui(ModereX plugin, Player staff, Player target) {
        this.plugin = plugin;
        this.staff = staff;
        this.target = target;
        this.inventory = Bukkit.createInventory(null, 54, Component.text("Inspect: " + target.getName()));
    }

    /**
     * Open the GUI.
     */
    public void open() {
        setupItems();
        staff.openInventory(inventory);
    }

    /**
     * Setup GUI items.
     */
    private void setupItems() {
        inventory.setItem(4, createPlayerHead());

        inventory.setItem(10, createInfoItem());
        inventory.setItem(12, createInventoryItem());
        inventory.setItem(14, createEnderChestItem());
        inventory.setItem(16, createHealthItem());

        inventory.setItem(19, createPunishItem());
        inventory.setItem(21, createHistoryItem());
        inventory.setItem(23, createFreezeItem());
        inventory.setItem(25, createTeleportItem());

        inventory.setItem(28, createNotesItem());
        inventory.setItem(30, createWatchlistItem());
        inventory.setItem(32, createAnticheatItem());
        inventory.setItem(34, createAdvancedItem());

        inventory.setItem(49, createCloseItem());
    }

    /**
     * Create player head item.
     */
    private ItemStack createPlayerHead() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(target);
            Msg.displayName(meta, Component.text(target.getName()).color(NamedTextColor.YELLOW));
            Msg.lore(meta, Arrays.asList(
                    Component.text("UUID: " + target.getUniqueId()).color(NamedTextColor.GRAY),
                    Component.text("Health: " + String.format("%.1f", target.getHealth()) + "/" + String.format("%.1f", target.getMaxHealth())).color(NamedTextColor.RED),
                    Component.text("Gamemode: " + target.getGameMode()).color(NamedTextColor.AQUA),
                    Component.text("Location: " + target.getLocation().getBlockX() + ", " + target.getLocation().getBlockY() + ", " + target.getLocation().getBlockZ()).color(NamedTextColor.GREEN)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create player info item.
     */
    private ItemStack createInfoItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Player Info").color(NamedTextColor.YELLOW));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("IP: " + target.getAddress().getAddress().getHostAddress()).color(NamedTextColor.GRAY));
            lore.add(Component.text("Ping: " + target.getPing() + "ms").color(NamedTextColor.GRAY));
            lore.add(Component.text("Client: " + target.getClientBrandName()).color(NamedTextColor.GRAY));
            Msg.lore(meta, lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create inventory view item.
     */
    private ItemStack createInventoryItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("View Inventory").color(NamedTextColor.AQUA));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to view").color(NamedTextColor.GRAY),
                    Component.text("player's inventory").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create ender chest view item.
     */
    private ItemStack createEnderChestItem() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("View Ender Chest").color(NamedTextColor.LIGHT_PURPLE));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to view").color(NamedTextColor.GRAY),
                    Component.text("player's ender chest").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create health management item.
     */
    private ItemStack createHealthItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Health Manager").color(NamedTextColor.RED));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Left: Kill player").color(NamedTextColor.GRAY),
                    Component.text("Right: Heal player").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create punish item.
     */
    private ItemStack createPunishItem() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Punish Player").color(NamedTextColor.RED));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to open").color(NamedTextColor.GRAY),
                    Component.text("punishment GUI").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create history item.
     */
    private ItemStack createHistoryItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Punishment History").color(NamedTextColor.YELLOW));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to view").color(NamedTextColor.GRAY),
                    Component.text("player's history").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create freeze item.
     */
    private ItemStack createFreezeItem() {
        boolean frozen = target.hasMetadata("moderex_frozen");
        ItemStack item = new ItemStack(frozen ? Material.PACKED_ICE : Material.ICE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text(frozen ? "Unfreeze Player" : "Freeze Player").color(NamedTextColor.BLUE));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to " + (frozen ? "unfreeze" : "freeze")).color(NamedTextColor.GRAY),
                    Component.text("this player").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create teleport item.
     */
    private ItemStack createTeleportItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Teleport").color(NamedTextColor.LIGHT_PURPLE));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Left: Teleport to player").color(NamedTextColor.GRAY),
                    Component.text("Right: Teleport player to you").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create notes item.
     */
    private ItemStack createNotesItem() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Staff Notes").color(NamedTextColor.GOLD));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to view/add").color(NamedTextColor.GRAY),
                    Component.text("staff notes").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create watchlist item.
     */
    private ItemStack createWatchlistItem() {
        ItemStack item = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Watchlist").color(NamedTextColor.YELLOW));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to add/remove").color(NamedTextColor.GRAY),
                    Component.text("from watchlist").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create anticheat item.
     */
    private ItemStack createAnticheatItem() {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Anticheat Info").color(NamedTextColor.RED));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to view").color(NamedTextColor.GRAY),
                    Component.text("anticheat violations").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create advanced options item.
     */
    private ItemStack createAdvancedItem() {
        ItemStack item = new ItemStack(Material.COMPARATOR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Advanced").color(NamedTextColor.DARK_PURPLE));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click for more").color(NamedTextColor.GRAY),
                    Component.text("options").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create close item.
     */
    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Close").color(NamedTextColor.RED));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Handle click event.
     *
     * @param slot The slot clicked
     */
    public void handleClick(int slot) {
        switch (slot) {
            case 12 -> staff.openInventory(target.getInventory());
            case 14 -> staff.openInventory(target.getEnderChest());
            case 16 -> {
                target.setHealth(target.getMaxHealth());
                target.setFoodLevel(20);
                target.setSaturation(20);
                Msg.send(staff, Component.text("Healed " + target.getName()).color(NamedTextColor.GREEN));
            }
            case 19 -> Bukkit.dispatchCommand(staff, "punish " + target.getName());
            case 21 -> Bukkit.dispatchCommand(staff, "history " + target.getName());
            case 23 -> {
                if (plugin.getStaffModeManager() != null) {
                    plugin.getStaffModeManager().handleItemClick(staff, 2, target);
                    open();
                }
            }
            case 25 -> staff.teleport(target);
            case 49 -> staff.closeInventory();
        }
    }
}
