package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.log.ActivityLogEntry.ActivityType;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for capturing player activities for the activity log system.
 */
public class ActivityLogListener implements Listener {

    private final ModereX plugin;

    // Track session start times for duration calculation
    private final Map<UUID, Long> sessionStartTimes = new HashMap<>();

    // Track previous usernames for change detection
    private final Map<UUID, String> previousUsernames = new HashMap<>();

    public ActivityLogListener(ModereX plugin) {
        this.plugin = plugin;
    }

    // ===== CHAT =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        String message = event.getMessage();

        // Log asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getActivityLogManager().logChat(player, message);
        });
    }

    // ===== COMMANDS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        String command = event.getMessage();

        plugin.getActivityLogManager().logCommand(player, command);
    }

    // ===== SIGNS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        String[] lines = new String[4];

        for (int i = 0; i < 4; i++) {
            lines[i] = event.getLine(i) != null ? event.getLine(i) : "";
        }

        // Build location string
        org.bukkit.Location loc = event.getBlock().getLocation();
        String location = String.format("%s:%d,%d,%d",
                loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        plugin.getActivityLogManager().logSign(player, lines, location);
    }

    // ===== ITEMS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        String itemInfo = formatItem(item);

        plugin.getActivityLogManager().logItemDrop(player, itemInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        String itemInfo = formatItem(item);

        plugin.getActivityLogManager().logItemPickup(player, itemInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        String itemInfo = formatItem(item);

        plugin.getActivityLogManager().logItemConsume(player, itemInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemUse(PlayerInteractEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        // Only log right-click uses
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        // Skip blocks being placed (handled by block place listener)
        if (item.getType().isBlock() && event.getAction().name().contains("BLOCK")) return;

        Player player = event.getPlayer();
        String itemInfo = formatItem(item);

        plugin.getActivityLogManager().logItemUse(player, itemInfo);
    }

    // ===== PROJECTILES =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) return;

        String projectileInfo = projectile.getType().name();
        plugin.getActivityLogManager().logProjectile(player, projectileInfo);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack bow = event.getBow();
        String bowInfo = bow != null ? bow.getType().name() : "BOW";
        String forceInfo = String.format(" (%.0f%% power)", event.getForce() * 100);

        plugin.getActivityLogManager().logProjectile(player, bowInfo + forceInfo);
    }

    // ===== ANVIL RENAMING =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAnvilRename(InventoryClickEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (!(event.getInventory() instanceof AnvilInventory anvil)) return;

        // Only log when clicking the result slot
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        ItemStack result = anvil.getResult();
        if (result == null || result.getType() == Material.AIR) return;

        // Get the original item (first slot)
        ItemStack original = anvil.getItem(0);
        if (original == null || original.getType() == Material.AIR) return;

        // Get original and new names
        String originalName = getItemDisplayName(original);
        String newName = getItemDisplayName(result);

        // Only log if the name actually changed
        if (originalName.equals(newName)) return;

        plugin.getActivityLogManager().logAnvilRename(player, originalName, newName);
    }

    /**
     * Get the display name of an item, or material name if no custom name.
     */
    private String getItemDisplayName(ItemStack item) {
        if (item == null) return "AIR";
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return item.getType().name();
    }

    // ===== SESSIONS =====

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Track session start
        sessionStartTimes.put(uuid, System.currentTimeMillis());

        // Check for username change
        String previousName = previousUsernames.get(uuid);
        if (previousName != null && !previousName.equals(player.getName())) {
            plugin.getActivityLogManager().logUsernameChange(uuid, previousName, player.getName());
        }
        previousUsernames.put(uuid, player.getName());

        // Log join
        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown";
        plugin.getActivityLogManager().logSessionJoin(player, ip);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getActivityLogManager().isEnabled()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Calculate session duration
        Long startTime = sessionStartTimes.remove(uuid);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        plugin.getActivityLogManager().logSessionQuit(player, duration);
    }

    // ===== UTILITY =====

    /**
     * Format an item stack for logging.
     */
    private String formatItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "AIR";
        }

        String name = item.getType().name();
        int amount = item.getAmount();

        if (amount > 1) {
            return amount + "x " + name;
        }
        return name;
    }

    /**
     * Load previous usernames from database on startup.
     */
    public void loadPreviousUsernames() {
        // This will be populated as players join
        // Could optionally preload from database for better tracking
    }
}
