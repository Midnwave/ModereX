package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.util.PermissionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Full OpenInv-style inventory viewer for staff inspection.
 * Layout (6 rows, 54 slots):
 *   Row 1 (0-8):   Target's hotbar (player inv slots 0-8)
 *   Row 2-4 (9-35): Target's main inventory (player inv slots 9-35)
 *   Row 5 (36-44): Armor (36-39) + Offhand (40) + empty (41) + Ender Chest (42) + Refresh (43) + Close (44)
 *   Row 6 (45-53): Info/status items
 */
public class StaffInspectGui {

    private final ModereX plugin;
    private final Player staff;
    private final Player target;
    private final Inventory inventory;
    private final boolean canModify;

    // Slot constants for action buttons (row 5)
    private static final int ENDER_CHEST_SLOT = 42;
    private static final int REFRESH_SLOT = 43;
    private static final int CLOSE_SLOT = 44;

    // Row 5 armor/offhand mapping
    private static final int HELMET_SLOT = 36;
    private static final int CHESTPLATE_SLOT = 37;
    private static final int LEGGINGS_SLOT = 38;
    private static final int BOOTS_SLOT = 39;
    private static final int OFFHAND_SLOT = 40;
    private static final int SEPARATOR_SLOT = 41;

    // Cached static items — identical across all inspect GUIs
    private static final ItemStack CACHED_PLACEHOLDER_GRAY = buildStaticItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    private static final ItemStack CACHED_NO_HELMET = buildStaticItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "<gray>No Helmet");
    private static final ItemStack CACHED_NO_CHESTPLATE = buildStaticItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "<gray>No Chestplate");
    private static final ItemStack CACHED_NO_LEGGINGS = buildStaticItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "<gray>No Leggings");
    private static final ItemStack CACHED_NO_BOOTS = buildStaticItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "<gray>No Boots");
    private static final ItemStack CACHED_NO_OFFHAND = buildStaticItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "<gray>No Offhand");
    private static final ItemStack CACHED_ENDER_CHEST = buildStaticAction(Material.ENDER_CHEST, "<light_purple>Ender Chest", "<gray>Click to view ender chest");
    private static final ItemStack CACHED_REFRESH = buildStaticAction(Material.LIME_DYE, "<green>Refresh", "<gray>Click to reload inventory");
    private static final ItemStack CACHED_CLOSE = buildStaticAction(Material.BARRIER, "<red>Close", "<gray>Click to close");

    private static ItemStack buildStaticItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, com.blockforge.moderex.util.TextUtil.parseLore(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack buildStaticAction(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, com.blockforge.moderex.util.TextUtil.parseLore(name));
            if (loreLines != null && loreLines.length > 0) {
                List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
                for (String line : loreLines) {
                    lore.add(com.blockforge.moderex.util.TextUtil.parseLore(line));
                }
                Msg.lore(meta, lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public StaffInspectGui(ModereX plugin, Player staff, Player target) {
        this.plugin = plugin;
        this.staff = staff;
        this.target = target;
        this.canModify = PermissionUtil.hasPermission(staff, "moderex.staff.inspect.modify");
        this.inventory = Msg.createInventory(null, 54, Component.text("Inspect: " + target.getName()));
    }

    /**
     * Open the GUI.
     */
    public void open() {
        populateInventory();
        populateArmorRow();
        populateInfoRow();
        staff.openInventory(inventory);
    }

    /**
     * Populate the target's inventory contents into the GUI.
     * Row 1 (slots 0-8): Hotbar
     * Rows 2-4 (slots 9-35): Main inventory
     */
    private void populateInventory() {
        PlayerInventory playerInv = target.getInventory();

        // Row 1: Hotbar (player slots 0-8 -> GUI slots 0-8)
        for (int i = 0; i <= 8; i++) {
            ItemStack item = playerInv.getItem(i);
            inventory.setItem(i, item != null ? item.clone() : null);
        }

        // Rows 2-4: Main inventory (player slots 9-35 -> GUI slots 9-35)
        for (int i = 9; i <= 35; i++) {
            ItemStack item = playerInv.getItem(i);
            inventory.setItem(i, item != null ? item.clone() : null);
        }
    }

    /**
     * Populate Row 5 with armor, offhand, and action buttons.
     */
    private void populateArmorRow() {
        PlayerInventory playerInv = target.getInventory();

        // Armor slots (GUI 36-39): Helmet, Chestplate, Leggings, Boots
        ItemStack helmet = playerInv.getHelmet();
        ItemStack chestplate = playerInv.getChestplate();
        ItemStack leggings = playerInv.getLeggings();
        ItemStack boots = playerInv.getBoots();

        inventory.setItem(HELMET_SLOT, helmet != null ? helmet.clone() : CACHED_NO_HELMET.clone());
        inventory.setItem(CHESTPLATE_SLOT, chestplate != null ? chestplate.clone() : CACHED_NO_CHESTPLATE.clone());
        inventory.setItem(LEGGINGS_SLOT, leggings != null ? leggings.clone() : CACHED_NO_LEGGINGS.clone());
        inventory.setItem(BOOTS_SLOT, boots != null ? boots.clone() : CACHED_NO_BOOTS.clone());

        // Offhand (GUI slot 40)
        ItemStack offhand = playerInv.getItemInOffHand();
        inventory.setItem(OFFHAND_SLOT, offhand.getType() != Material.AIR ? offhand.clone() : CACHED_NO_OFFHAND.clone());

        // Separator
        inventory.setItem(SEPARATOR_SLOT, CACHED_PLACEHOLDER_GRAY.clone());

        // Ender Chest button
        inventory.setItem(ENDER_CHEST_SLOT, CACHED_ENDER_CHEST.clone());

        // Refresh button
        inventory.setItem(REFRESH_SLOT, CACHED_REFRESH.clone());

        // Close button
        inventory.setItem(CLOSE_SLOT, CACHED_CLOSE.clone());
    }

    /**
     * Populate Row 6 with player info and status.
     */
    private void populateInfoRow() {
        // Player head with info (center)
        inventory.setItem(49, createPlayerHead());

        // Health info
        inventory.setItem(46, createStatusItem(Material.RED_DYE,
                "<red>Health",
                "<gray>Health: <white>" + String.format("%.1f", target.getHealth()) + "/" + String.format("%.1f", target.getMaxHealth()),
                "<gray>Food: <white>" + target.getFoodLevel() + "/20",
                "<gray>Saturation: <white>" + String.format("%.1f", target.getSaturation())));

        // Location info
        inventory.setItem(47, createStatusItem(Material.COMPASS,
                "<green>Location",
                "<gray>World: <white>" + target.getWorld().getName(),
                "<gray>X: <white>" + target.getLocation().getBlockX(),
                "<gray>Y: <white>" + target.getLocation().getBlockY(),
                "<gray>Z: <white>" + target.getLocation().getBlockZ()));

        // Game info
        inventory.setItem(48, createStatusItem(Material.DIAMOND_SWORD,
                "<aqua>Game Info",
                "<gray>Gamemode: <white>" + target.getGameMode().name(),
                "<gray>Exp Level: <white>" + target.getLevel(),
                "<gray>Fly: <white>" + (target.getAllowFlight() ? "Enabled" : "Disabled")));

        // Connection info
        inventory.setItem(50, createStatusItem(Material.PAPER,
                "<yellow>Connection",
                "<gray>Ping: <white>" + target.getPing() + "ms",
                "<gray>Client: <white>" + (Msg.getClientBrand(target) != null ? Msg.getClientBrand(target) : "Unknown")));

        // Permission indicator
        inventory.setItem(51, createStatusItem(
                canModify ? Material.LIME_DYE : Material.GRAY_DYE,
                canModify ? "<green>Mode: Edit" : "<gray>Mode: Read-Only",
                canModify ? "<gray>You can modify items" : "<gray>View only - no modifications",
                canModify ? "<yellow>Changes sync to player" : "<yellow>Need moderex.staff.inspect.modify"));

        // Fill remaining empty info row slots with glass
        for (int i = 45; i <= 53; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, CACHED_PLACEHOLDER_GRAY.clone());
            }
        }
    }

    /**
     * Handle click event from the inventory.
     *
     * @param slot The slot clicked
     * @param isShiftClick Whether shift was held
     * @return true if the event should be cancelled (no item movement allowed)
     */
    public boolean handleClick(int slot, boolean isShiftClick) {
        // Action buttons - always cancel
        if (slot == CLOSE_SLOT) {
            staff.closeInventory();
            return true;
        }

        if (slot == REFRESH_SLOT) {
            refreshInventory();
            staff.playSound(staff.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            return true;
        }

        if (slot == ENDER_CHEST_SLOT) {
            openEnderChest();
            return true;
        }

        // Separator and info row - always cancel
        if (slot == SEPARATOR_SLOT || slot >= 45) {
            return true;
        }

        // Placeholder items (armor/offhand empty slots) - always cancel
        if (slot >= HELMET_SLOT && slot <= OFFHAND_SLOT) {
            ItemStack clicked = inventory.getItem(slot);
            if (clicked != null && clicked.getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE) {
                return true;
            }
        }

        // Inventory content slots (0-35) and armor/offhand (36-40)
        if (canModify) {
            // Allow the click - item movement will be synced
            return false;
        } else {
            // Read-only mode - cancel all item movements
            return true;
        }
    }

    /**
     * Sync changes from the GUI inventory back to the target player.
     * Called after an allowed inventory click event completes.
     */
    public void syncToTarget() {
        if (!canModify || !target.isOnline()) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!target.isOnline()) return;

            PlayerInventory playerInv = target.getInventory();

            // Sync hotbar (slots 0-8)
            for (int i = 0; i <= 8; i++) {
                playerInv.setItem(i, inventory.getItem(i));
            }

            // Sync main inventory (slots 9-35)
            for (int i = 9; i <= 35; i++) {
                playerInv.setItem(i, inventory.getItem(i));
            }

            // Sync armor
            ItemStack helmet = inventory.getItem(HELMET_SLOT);
            ItemStack chestplate = inventory.getItem(CHESTPLATE_SLOT);
            ItemStack leggings = inventory.getItem(LEGGINGS_SLOT);
            ItemStack boots = inventory.getItem(BOOTS_SLOT);

            playerInv.setHelmet(isPlaceholder(helmet) ? null : helmet);
            playerInv.setChestplate(isPlaceholder(chestplate) ? null : chestplate);
            playerInv.setLeggings(isPlaceholder(leggings) ? null : leggings);
            playerInv.setBoots(isPlaceholder(boots) ? null : boots);

            // Sync offhand
            ItemStack offhand = inventory.getItem(OFFHAND_SLOT);
            playerInv.setItemInOffHand(isPlaceholder(offhand) ? new ItemStack(Material.AIR) : offhand);
        }, 1L);
    }

    /**
     * Refresh the GUI with current target inventory data.
     */
    public void refreshInventory() {
        if (!target.isOnline()) {
            staff.closeInventory();
            Msg.send(staff, Component.text("Player is no longer online.").color(NamedTextColor.RED));
            return;
        }

        populateInventory();
        populateArmorRow();
        populateInfoRow();
    }

    /**
     * Open the target's ender chest in a separate GUI.
     */
    private void openEnderChest() {
        if (!target.isOnline()) {
            Msg.send(staff, Component.text("Player is no longer online.").color(NamedTextColor.RED));
            return;
        }
        staff.openInventory(target.getEnderChest());
    }

    /**
     * Check if an ItemStack is one of our placeholder glass panes.
     */
    private boolean isPlaceholder(ItemStack item) {
        if (item == null) return true;
        return item.getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE
                || item.getType() == Material.GRAY_STAINED_GLASS_PANE;
    }

    // --- Item creation helpers ---

    private ItemStack createPlayerHead() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(target);
            Msg.displayName(meta, Component.text(target.getName()).color(NamedTextColor.YELLOW));

            List<Component> lore = Arrays.asList(
                    Component.text("UUID: " + target.getUniqueId()).color(NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Inventory Viewer").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true),
                    Component.text(canModify ? "Edit Mode" : "Read-Only Mode")
                            .color(canModify ? NamedTextColor.GREEN : NamedTextColor.GRAY)
            );
            Msg.lore(meta, lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createActionItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, com.blockforge.moderex.util.TextUtil.parseLore(name));
            if (loreLines != null && loreLines.length > 0) {
                List<Component> lore = new java.util.ArrayList<>();
                for (String line : loreLines) {
                    lore.add(com.blockforge.moderex.util.TextUtil.parseLore(line));
                }
                Msg.lore(meta, lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createStatusItem(Material material, String name, String... loreLines) {
        return createActionItem(material, name, loreLines);
    }

    private ItemStack createPlaceholder(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, com.blockforge.moderex.util.TextUtil.parseLore(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    // --- Accessors ---

    public Inventory getInventory() {
        return inventory;
    }

    public Player getStaff() {
        return staff;
    }

    public Player getTarget() {
        return target;
    }

    public boolean canModify() {
        return canModify;
    }
}
