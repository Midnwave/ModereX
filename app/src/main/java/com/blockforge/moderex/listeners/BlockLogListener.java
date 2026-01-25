package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplaySession;
import com.blockforge.moderex.replay.block.BlockLogEntry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Captures block events during active replay recordings.
 * Logs block changes for visualization during replay playback.
 */
public class BlockLogListener implements Listener {

    private final ModereX plugin;

    // Track player-placed explosive blocks (TNT, respawn anchors, beds in wrong dimension)
    // Key: "world:x:y:z" -> Player UUID who placed/interacted
    private final Map<String, UUID> playerPlacedExplosives = new ConcurrentHashMap<>();

    // Track TNT entities back to the player who ignited them
    // Key: TNT entity UUID -> Player UUID
    private final Map<UUID, UUID> tntIgniters = new ConcurrentHashMap<>();

    public BlockLogListener(ModereX plugin) {
        this.plugin = plugin;
    }

    private String getLocationKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private String getLocationKey(Block block) {
        return block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();

        // Track explosive blocks for attribution when they explode
        if (type == Material.TNT || type == Material.RESPAWN_ANCHOR) {
            playerPlacedExplosives.put(getLocationKey(block), player.getUniqueId());
        }

        // Track beds placed in nether/end (will explode when used)
        if (type.name().endsWith("_BED")) {
            World.Environment env = block.getWorld().getEnvironment();
            if (env == World.Environment.NETHER || env == World.Environment.THE_END) {
                playerPlacedExplosives.put(getLocationKey(block), player.getUniqueId());
            }
        }

        // Find sessions recording this player
        List<ReplaySession> sessions = plugin.getReplayManager().getActiveSessionsForPlayer(player.getUniqueId());
        if (sessions.isEmpty()) {
            return;
        }

        for (ReplaySession session : sessions) {
            BlockLogEntry entry = new BlockLogEntry.Builder()
                    .sessionId(session.getSessionId())
                    .player(player.getUniqueId(), player.getName())
                    .action(BlockLogEntry.Action.PLACE)
                    .location(block.getWorld().getName(), block.getX(), block.getY(), block.getZ())
                    .oldBlock(event.getBlockReplacedState().getType(), event.getBlockReplacedState().getBlockData())
                    .newBlock(block.getType(), block.getBlockData())
                    .build();

            plugin.getBlockLogManager().logBlockChange(session.getSessionId(), entry);
        }
    }

    /**
     * Track when a player interacts with a respawn anchor (charging or triggering explosion).
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material type = block.getType();
        Player player = event.getPlayer();

        // Track respawn anchor interaction (player who uses it is responsible for explosion)
        if (type == Material.RESPAWN_ANCHOR) {
            // If not in nether, it will explode - attribute to this player
            if (block.getWorld().getEnvironment() != World.Environment.NETHER) {
                playerPlacedExplosives.put(getLocationKey(block), player.getUniqueId());
            }
        }

        // Track bed interaction in wrong dimension
        if (type.name().endsWith("_BED")) {
            World.Environment env = block.getWorld().getEnvironment();
            if (env == World.Environment.NETHER || env == World.Environment.THE_END) {
                playerPlacedExplosives.put(getLocationKey(block), player.getUniqueId());
            }
        }

        // Track TNT ignition with flint and steel
        if (type == Material.TNT) {
            Material heldItem = player.getInventory().getItemInMainHand().getType();
            if (heldItem == Material.FLINT_AND_STEEL || heldItem == Material.FIRE_CHARGE) {
                playerPlacedExplosives.put(getLocationKey(block), player.getUniqueId());
            }
        }
    }

    /**
     * Track when TNT is primed (ignited) to attribute to a player.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTntPrime(org.bukkit.event.block.TNTPrimeEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Block block = event.getBlock();
        Entity primer = event.getPrimingEntity();

        // If a player primed it, track them
        if (primer instanceof Player player) {
            // Store the TNT location -> player for when the TNT entity spawns
            playerPlacedExplosives.put(getLocationKey(block), player.getUniqueId());
        } else {
            // Check if TNT was placed by a player (for chain reactions)
            UUID placerUuid = playerPlacedExplosives.get(getLocationKey(block));
            if (placerUuid != null) {
                // Keep the placer attribution for chain reactions
                plugin.logDebug("[BlockLog] TNT primed at " + getLocationKey(block) + " - attributed to original placer");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Clean up tracking for broken explosive blocks
        playerPlacedExplosives.remove(getLocationKey(block));

        // Find sessions recording this player
        List<ReplaySession> sessions = plugin.getReplayManager().getActiveSessionsForPlayer(player.getUniqueId());
        if (sessions.isEmpty()) {
            return;
        }

        for (ReplaySession session : sessions) {
            BlockLogEntry entry = new BlockLogEntry.Builder()
                    .sessionId(session.getSessionId())
                    .player(player.getUniqueId(), player.getName())
                    .action(BlockLogEntry.Action.BREAK)
                    .location(block.getWorld().getName(), block.getX(), block.getY(), block.getZ())
                    .oldBlock(block.getType(), block.getBlockData())
                    .newBlock(Material.AIR, (String) null)
                    .build();

            plugin.getBlockLogManager().logBlockChange(session.getSessionId(), entry);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Block piston = event.getBlock();
        List<Block> movedBlocks = event.getBlocks();

        // Find any active sessions in this area
        List<ReplaySession> sessions = getSessionsNearLocation(piston.getLocation());
        if (sessions.isEmpty()) {
            return;
        }

        for (ReplaySession session : sessions) {
            // Log each moved block
            for (Block moved : movedBlocks) {
                Location newLoc = moved.getLocation().add(event.getDirection().getDirection());
                Block destination = newLoc.getBlock();

                BlockLogEntry entry = new BlockLogEntry.Builder()
                        .sessionId(session.getSessionId())
                        .player(null, null) // No player for piston
                        .action(BlockLogEntry.Action.PISTON_EXTEND)
                        .location(destination.getWorld().getName(), destination.getX(), destination.getY(), destination.getZ())
                        .oldBlock(destination.getType(), destination.getBlockData())
                        .newBlock(moved.getType(), moved.getBlockData())
                        .build();

                plugin.getBlockLogManager().logBlockChange(session.getSessionId(), entry);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Block piston = event.getBlock();
        List<Block> movedBlocks = event.getBlocks();

        // Find any active sessions in this area
        List<ReplaySession> sessions = getSessionsNearLocation(piston.getLocation());
        if (sessions.isEmpty()) {
            return;
        }

        for (ReplaySession session : sessions) {
            for (Block moved : movedBlocks) {
                Location newLoc = moved.getLocation().add(event.getDirection().getDirection());
                Block destination = newLoc.getBlock();

                BlockLogEntry entry = new BlockLogEntry.Builder()
                        .sessionId(session.getSessionId())
                        .player(null, null)
                        .action(BlockLogEntry.Action.PISTON_RETRACT)
                        .location(destination.getWorld().getName(), destination.getX(), destination.getY(), destination.getZ())
                        .oldBlock(destination.getType(), destination.getBlockData())
                        .newBlock(moved.getType(), moved.getBlockData())
                        .build();

                plugin.getBlockLogManager().logBlockChange(session.getSessionId(), entry);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Entity entity = event.getEntity();
        List<Block> destroyedBlocks = event.blockList();

        if (destroyedBlocks.isEmpty()) {
            return;
        }

        // Exclude wind charges from block explosion logging (they don't destroy blocks, just push)
        // Wind charges are cosmetic "explosions" that shouldn't be logged as block destruction
        String entityTypeName = entity.getType().name();
        if (entityTypeName.equals("WIND_CHARGE") || entityTypeName.equals("BREEZE_WIND_CHARGE")) {
            plugin.logDebug("[BlockLog] Ignoring wind charge explosion - no block destruction");
            return;
        }

        // Try to attribute explosion to a player
        UUID responsiblePlayer = null;
        String responsibleName = null;

        // Check if this is TNT and we tracked the igniter
        if (entity instanceof TNTPrimed tnt) {
            Entity source = tnt.getSource();
            if (source instanceof Player player) {
                responsiblePlayer = player.getUniqueId();
                responsibleName = player.getName();
            } else {
                // Check our tracking map for the TNT spawn location
                Location tntLoc = tnt.getOrigin();
                if (tntLoc != null) {
                    String key = getLocationKey(tntLoc);
                    UUID trackedPlayer = playerPlacedExplosives.remove(key);
                    if (trackedPlayer != null) {
                        responsiblePlayer = trackedPlayer;
                        Player player = plugin.getServer().getPlayer(trackedPlayer);
                        responsibleName = player != null ? player.getName() : "Unknown";
                    }
                }
            }
        }

        // Find any active sessions in this area
        List<ReplaySession> sessions = getSessionsNearLocation(event.getLocation());
        if (sessions.isEmpty()) {
            return;
        }

        for (ReplaySession session : sessions) {
            for (Block block : destroyedBlocks) {
                BlockLogEntry entry = new BlockLogEntry.Builder()
                        .sessionId(session.getSessionId())
                        .player(responsiblePlayer, responsibleName)
                        .action(BlockLogEntry.Action.EXPLOSION)
                        .location(block.getWorld().getName(), block.getX(), block.getY(), block.getZ())
                        .oldBlock(block.getType(), block.getBlockData())
                        .newBlock(Material.AIR, (String) null)
                        .build();

                plugin.getBlockLogManager().logBlockChange(session.getSessionId(), entry);
            }
        }
    }

    /**
     * Handle block explosions (beds in nether/end, respawn anchors in overworld/end).
     * Excludes wind burst (mace enchantment) explosions which don't destroy blocks.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!plugin.getConfigManager().getSettings().isReplayBlockLoggingEnabled()) {
            return;
        }

        Block sourceBlock = event.getBlock();
        List<Block> destroyedBlocks = event.blockList();

        if (destroyedBlocks.isEmpty()) {
            return;
        }

        // Check if this is a wind burst explosion (mace enchantment) by checking the exploded block type
        // Wind burst explosions typically have AIR as the source block since they're player-triggered
        // and don't actually originate from a physical block that explodes
        if (sourceBlock.getType() == Material.AIR) {
            // Check if any nearby player has a mace with wind burst - if so, this is likely a wind burst explosion
            boolean isLikelyWindBurst = false;
            for (Player nearby : sourceBlock.getWorld().getNearbyPlayers(sourceBlock.getLocation(), 5)) {
                if (isHoldingMaceWithWindBurst(nearby)) {
                    isLikelyWindBurst = true;
                    break;
                }
            }

            if (isLikelyWindBurst) {
                plugin.logDebug("[BlockLog] Ignoring wind burst explosion from mace - no block destruction logged");
                return;
            }
        }

        // Try to attribute explosion to a player who placed/interacted with the source block
        UUID responsiblePlayer = null;
        String responsibleName = null;

        String sourceKey = getLocationKey(sourceBlock);
        UUID trackedPlayer = playerPlacedExplosives.remove(sourceKey);
        if (trackedPlayer != null) {
            responsiblePlayer = trackedPlayer;
            Player player = plugin.getServer().getPlayer(trackedPlayer);
            responsibleName = player != null ? player.getName() : "Unknown";
            plugin.logDebug("[BlockLog] Block explosion attributed to " + responsibleName + " at " + sourceKey);
        }

        // Find any active sessions in this area
        List<ReplaySession> sessions = getSessionsNearLocation(event.getBlock().getLocation());
        if (sessions.isEmpty()) {
            return;
        }

        // Log the source block itself if it was destroyed
        for (ReplaySession session : sessions) {
            // Log the exploding block (bed/anchor) first
            if (sourceBlock.getType() != Material.AIR) {
                BlockLogEntry sourceEntry = new BlockLogEntry.Builder()
                        .sessionId(session.getSessionId())
                        .player(responsiblePlayer, responsibleName)
                        .action(BlockLogEntry.Action.EXPLOSION)
                        .location(sourceBlock.getWorld().getName(), sourceBlock.getX(), sourceBlock.getY(), sourceBlock.getZ())
                        .oldBlock(sourceBlock.getType(), sourceBlock.getBlockData())
                        .newBlock(Material.AIR, (String) null)
                        .build();

                plugin.getBlockLogManager().logBlockChange(session.getSessionId(), sourceEntry);
            }

            // Log all affected blocks
            for (Block block : destroyedBlocks) {
                BlockLogEntry entry = new BlockLogEntry.Builder()
                        .sessionId(session.getSessionId())
                        .player(responsiblePlayer, responsibleName)
                        .action(BlockLogEntry.Action.EXPLOSION)
                        .location(block.getWorld().getName(), block.getX(), block.getY(), block.getZ())
                        .oldBlock(block.getType(), block.getBlockData())
                        .newBlock(Material.AIR, (String) null)
                        .build();

                plugin.getBlockLogManager().logBlockChange(session.getSessionId(), entry);
            }
        }
    }

    /**
     * Check if a player is holding a mace with the Wind Burst enchantment.
     */
    private boolean isHoldingMaceWithWindBurst(Player player) {
        try {
            org.bukkit.inventory.ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand == null || mainHand.getType() == Material.AIR) {
                return false;
            }

            // Check if holding a mace (1.21+)
            if (!mainHand.getType().name().equals("MACE")) {
                return false;
            }

            // Check for Wind Burst enchantment
            org.bukkit.enchantments.Enchantment windBurst = org.bukkit.Registry.ENCHANTMENT.get(
                    org.bukkit.NamespacedKey.minecraft("wind_burst"));
            if (windBurst != null && mainHand.containsEnchantment(windBurst)) {
                return true;
            }
        } catch (NoSuchFieldError | NoClassDefFoundError | Exception ignored) {
            // Mace or Wind Burst doesn't exist in this version
        }

        return false;
    }

    /**
     * Get active replay sessions near a location.
     */
    private List<ReplaySession> getSessionsNearLocation(Location location) {
        int radius = plugin.getConfigManager().getSettings().getReplayNearbyRadius();
        return plugin.getReplayManager().getActiveSessionsNearLocation(location, radius);
    }
}
