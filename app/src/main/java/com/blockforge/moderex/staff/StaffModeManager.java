package com.blockforge.moderex.staff;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.util.Msg;
import com.blockforge.moderex.webpanel.debug.WebPanelDebugger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages staff mode functionality for moderators.
 * Provides items and utilities for staff members.
 */
public class StaffModeManager {

    private final ModereX plugin;
    private final Map<UUID, StaffModeData> staffModePlayers = new ConcurrentHashMap<>();

    private static final int VANISH_SLOT = 0;
    private static final int INSPECT_SLOT = 1;
    private static final int FREEZE_SLOT = 2;
    private static final int ANTICHEAT_SLOT = 4;
    private static final int TELEPORT_SLOT = 7;
    private static final int EXIT_SLOT = 8;

    public StaffModeManager(ModereX plugin) {
        this.plugin = plugin;
    }

    /**
     * Enable staff mode for a player.
     *
     * @param player The player
     */
    public void enableStaffMode(Player player) {
        if (isInStaffMode(player)) {
            Msg.send(player, Component.text("You are already in staff mode.").color(NamedTextColor.RED));
            return;
        }

        StaffModeData data = new StaffModeData(player);
        staffModePlayers.put(player.getUniqueId(), data);

        player.setGameMode(GameMode.CREATIVE);
        player.setFlying(true);
        player.setAllowFlight(true);

        giveStaffItems(player);

        if (plugin.getVanishManager() != null && !plugin.getVanishManager().isVanished(player)) {
            plugin.getVanishManager().vanish(player);
        }

        Msg.send(player, Component.text("Staff mode enabled.").color(NamedTextColor.GREEN));
        plugin.logDebug("[StaffMode] " + player.getName() + " enabled staff mode");

        // Web panel debug
        WebPanelDebugger debugger = plugin.getWebPanelDebugger();
        if (debugger != null) {
            debugger.staffModeEnabled(player.getName());
        }
    }

    /**
     * Disable staff mode for a player.
     *
     * @param player The player
     */
    public void disableStaffMode(Player player) {
        StaffModeData data = staffModePlayers.remove(player.getUniqueId());
        if (data == null) {
            Msg.send(player, Component.text("You are not in staff mode.").color(NamedTextColor.RED));
            return;
        }

        data.restore(player);

        if (plugin.getVanishManager() != null && plugin.getVanishManager().isVanished(player)) {
            plugin.getVanishManager().unvanish(player);
        }

        Msg.send(player, Component.text("Staff mode disabled.").color(NamedTextColor.GREEN));
        plugin.logDebug("[StaffMode] " + player.getName() + " disabled staff mode");

        // Web panel debug
        WebPanelDebugger debugger = plugin.getWebPanelDebugger();
        if (debugger != null) {
            debugger.staffModeDisabled(player.getName());
        }
    }

    /**
     * Toggle staff mode for a player.
     *
     * @param player The player
     */
    public void toggleStaffMode(Player player) {
        if (isInStaffMode(player)) {
            disableStaffMode(player);
        } else {
            enableStaffMode(player);
        }
    }

    /**
     * Check if a player is in staff mode.
     *
     * @param player The player
     * @return true if in staff mode
     */
    public boolean isInStaffMode(Player player) {
        return staffModePlayers.containsKey(player.getUniqueId());
    }

    /**
     * Give staff mode items to a player.
     *
     * @param player The player
     */
    private void giveStaffItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(VANISH_SLOT, createVanishItem());
        player.getInventory().setItem(INSPECT_SLOT, createInspectItem());
        player.getInventory().setItem(FREEZE_SLOT, createFreezeItem());
        player.getInventory().setItem(ANTICHEAT_SLOT, createAnticheatItem());
        player.getInventory().setItem(TELEPORT_SLOT, createTeleportItem());
        player.getInventory().setItem(EXIT_SLOT, createExitItem());

        player.updateInventory();
    }

    /**
     * Create vanish toggle item.
     */
    private ItemStack createVanishItem() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Vanish").color(NamedTextColor.AQUA));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to toggle vanish").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create player inspect item.
     */
    private ItemStack createInspectItem() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Inspect Player").color(NamedTextColor.YELLOW));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Right-click a player").color(NamedTextColor.GRAY),
                    Component.text("to open management GUI").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create freeze player item.
     */
    private ItemStack createFreezeItem() {
        ItemStack item = new ItemStack(Material.PACKED_ICE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Freeze Player").color(NamedTextColor.BLUE));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Right-click a player").color(NamedTextColor.GRAY),
                    Component.text("to freeze/unfreeze them").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create anticheat management item.
     */
    private ItemStack createAnticheatItem() {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Anticheat Manager").color(NamedTextColor.RED));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to toggle").color(NamedTextColor.GRAY),
                    Component.text("anticheat alerts").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create random teleport item.
     */
    private ItemStack createTeleportItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Random Teleport").color(NamedTextColor.LIGHT_PURPLE));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to teleport").color(NamedTextColor.GRAY),
                    Component.text("to a random player").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Create exit staff mode item.
     */
    private ItemStack createExitItem() {
        ItemStack item = new ItemStack(Material.RED_BED);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Msg.displayName(meta, Component.text("Exit Staff Mode").color(NamedTextColor.RED));
            Msg.lore(meta, Arrays.asList(
                    Component.text("Click to disable").color(NamedTextColor.GRAY),
                    Component.text("staff mode").color(NamedTextColor.GRAY)
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Handle staff item interaction.
     *
     * @param player The player
     * @param slot The slot clicked
     * @param target The target player (if applicable)
     */
    public void handleItemClick(Player player, int slot, Player target) {
        if (!isInStaffMode(player)) return;

        switch (slot) {
            case VANISH_SLOT -> handleVanishClick(player);
            case INSPECT_SLOT -> handleInspectClick(player, target);
            case FREEZE_SLOT -> handleFreezeClick(player, target);
            case ANTICHEAT_SLOT -> handleAnticheatClick(player);
            case TELEPORT_SLOT -> handleTeleportClick(player);
            case EXIT_SLOT -> disableStaffMode(player);
        }
    }

    /**
     * Handle vanish item click.
     */
    private void handleVanishClick(Player player) {
        if (plugin.getVanishManager() != null) {
            plugin.getVanishManager().toggleVanish(player);
        }
    }

    /**
     * Handle inspect item click.
     */
    private void handleInspectClick(Player player, Player target) {
        if (target == null) {
            Msg.send(player, Component.text("Right-click a player to inspect them.").color(NamedTextColor.RED));
            return;
        }

        new StaffInspectGui(plugin, player, target).open();
    }

    /**
     * Handle freeze item click.
     */
    private void handleFreezeClick(Player player, Player target) {
        if (target == null) {
            Msg.send(player, Component.text("Right-click a player to freeze them.").color(NamedTextColor.RED));
            return;
        }

        boolean frozen = target.hasMetadata("moderex_frozen");
        if (frozen) {
            target.removeMetadata("moderex_frozen", plugin);
            target.setWalkSpeed(0.2f);
            target.setFlySpeed(0.1f);
            Msg.send(target, Component.text("You have been unfrozen.").color(NamedTextColor.GREEN));
            Msg.send(player, Component.text("Unfroze " + target.getName()).color(NamedTextColor.GREEN));
        } else {
            target.setMetadata("moderex_frozen", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
            target.setWalkSpeed(0);
            target.setFlySpeed(0);
            Msg.send(target, Component.text("You have been frozen by a staff member.").color(NamedTextColor.RED));
            Msg.send(player, Component.text("Froze " + target.getName()).color(NamedTextColor.GREEN));
        }
    }

    /**
     * Handle anticheat item click.
     */
    private void handleAnticheatClick(Player player) {
        if (plugin.getAnticheatManager() != null) {
            boolean enabled = plugin.getAnticheatManager().toggleAlerts(player);
            if (enabled) {
                Msg.send(player, Component.text("Anticheat alerts enabled.").color(NamedTextColor.GREEN));
            } else {
                Msg.send(player, Component.text("Anticheat alerts disabled.").color(NamedTextColor.RED));
            }
        } else {
            Msg.send(player, Component.text("No anticheat plugin detected.").color(NamedTextColor.RED));
        }
    }

    /**
     * Handle teleport item click.
     */
    private void handleTeleportClick(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(player);

        if (onlinePlayers.isEmpty()) {
            Msg.send(player, Component.text("No players online to teleport to.").color(NamedTextColor.RED));
            return;
        }

        Player randomPlayer = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
        player.teleport(randomPlayer);
        Msg.send(player, Component.text("Teleported to " + randomPlayer.getName()).color(NamedTextColor.GREEN));
    }

    /**
     * Clean up staff mode for all players on disable.
     */
    public void cleanup() {
        for (UUID uuid : new HashSet<>(staffModePlayers.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                disableStaffMode(player);
            }
        }
        staffModePlayers.clear();
    }

    /**
     * Data class to store player state before staff mode.
     */
    private static class StaffModeData {
        private final GameMode previousGameMode;
        private final ItemStack[] previousInventory;
        private final ItemStack[] previousArmor;
        private final boolean wasFlying;
        private final boolean couldFly;

        public StaffModeData(Player player) {
            this.previousGameMode = player.getGameMode();
            this.previousInventory = player.getInventory().getContents().clone();
            this.previousArmor = player.getInventory().getArmorContents().clone();
            this.wasFlying = player.isFlying();
            this.couldFly = player.getAllowFlight();
        }

        public void restore(Player player) {
            player.setGameMode(previousGameMode);
            player.getInventory().setContents(previousInventory);
            player.getInventory().setArmorContents(previousArmor);
            player.setFlying(wasFlying);
            player.setAllowFlight(couldFly);
            player.updateInventory();
        }
    }
}
