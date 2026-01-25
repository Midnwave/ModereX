package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplaySnapshot.ActionType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
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

        ItemStack item = event.getItem();

        // Record arm swing for left clicks
        if (event.getAction().name().contains("LEFT_CLICK")) {
            // Check for spear jab attack (1.21.11+)
            if (item != null && isSpear(item.getType())) {
                String tier = getSpearTier(item.getType());
                plugin.getReplayManager().recordAction(player, ActionType.SPEAR_JAB, tier + " Spear jab");
            } else {
                plugin.getReplayManager().recordAction(player, ActionType.SWING_ARM, null);
            }
        }

        // Record item use (right click with item)
        if (item != null && event.getAction().name().contains("RIGHT_CLICK")) {
            String itemName = item.getType().name();

            // Check for spear charge attack (1.21.11+)
            if (isSpear(item.getType())) {
                String tier = getSpearTier(item.getType());
                plugin.getReplayManager().recordAction(player, ActionType.SPEAR_CHARGE, tier + " Spear charge");
            } else {
                plugin.getReplayManager().recordAction(player, ActionType.ITEM_USE, itemName);
            }
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

        // Check if it's a crossbow
        if (bow != null && isCrossbow(bow.getType())) {
            plugin.getReplayManager().recordAction(player, ActionType.CROSSBOW_SHOOT, bowType + " (" + force + " power)");
        } else {
            plugin.getReplayManager().recordAction(player, ActionType.BOW_SHOOT, bowType + " (" + force + " power)");
        }
    }

    /**
     * Check if a material is a crossbow (safewrapped for version compatibility).
     */
    private boolean isCrossbow(Material material) {
        try {
            return material == Material.CROSSBOW;
        } catch (NoSuchFieldError e) {
            // Crossbow doesn't exist in this version
            return false;
        }
    }

    /**
     * Check if a material is a spear type (1.21.11+ only, safewrapped).
     * Spears were added in Minecraft 1.21.11 (Mounts of Mayhem update).
     */
    private boolean isSpear(Material material) {
        if (material == null) return false;
        String name = material.name();
        // Spear types: WOODEN_SPEAR, STONE_SPEAR (if exists), IRON_SPEAR, GOLDEN_SPEAR, DIAMOND_SPEAR, NETHERITE_SPEAR, COPPER_SPEAR
        return name.endsWith("_SPEAR") || name.equals("SPEAR");
    }

    /**
     * Get the tier of a spear for display purposes.
     */
    private String getSpearTier(Material material) {
        if (material == null) return "Unknown";
        String name = material.name();
        if (name.startsWith("WOODEN_")) return "Wooden";
        if (name.startsWith("COPPER_")) return "Copper";
        if (name.startsWith("IRON_")) return "Iron";
        if (name.startsWith("GOLDEN_")) return "Golden";
        if (name.startsWith("DIAMOND_")) return "Diamond";
        if (name.startsWith("NETHERITE_")) return "Netherite";
        return "Unknown";
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

    // ===== EQUIPMENT ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        ItemStack mainHand = event.getMainHandItem();
        ItemStack offHand = event.getOffHandItem();

        String mainHandName = mainHand != null && mainHand.getType() != Material.AIR ? mainHand.getType().name() : "empty";
        String offHandName = offHand != null && offHand.getType() != Material.AIR ? offHand.getType().name() : "empty";

        plugin.getReplayManager().recordAction(player, ActionType.OFFHAND_SWAP,
                "Main: " + mainHandName + " <-> Off: " + offHandName);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerArmorChange(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;

        // Check if clicking on armor slots (36-39 in player inventory)
        int slot = event.getRawSlot();
        if (event.getClickedInventory() == player.getInventory()) {
            // Armor slots: 36=boots, 37=leggings, 38=chestplate, 39=helmet
            if (slot >= 36 && slot <= 39) {
                ItemStack cursor = event.getCursor();
                String armorPiece = switch (slot) {
                    case 36 -> "Boots";
                    case 37 -> "Leggings";
                    case 38 -> "Chestplate";
                    case 39 -> "Helmet";
                    default -> "Armor";
                };

                String itemName = cursor != null && cursor.getType() != Material.AIR ?
                        cursor.getType().name() : "removed";

                // Delay recording to capture the final state
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        plugin.getReplayManager().recordAction(player, ActionType.ARMOR_EQUIP,
                                armorPiece + ": " + itemName);
                    }
                }, 1L);
            }
        }
    }

    // ===== BLOCK ACTIONS =====
    // NOTE: Block place/break actions are handled by BlockLogListener which properly logs
    // block positions and states. We don't duplicate logging here to avoid issues.

    // Combat damage tracking - adds opponent to recording for 1v1 tracking
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        String hitType = "NORMAL";
        boolean isProjectile = false;

        // Direct player attack
        if (event.getDamager() instanceof Player player) {
            attacker = player;
            hitType = determineHitType(player, event);
        }
        // Projectile from player
        else if (event.getDamager() instanceof Projectile projectile &&
                 projectile.getShooter() instanceof Player shooter) {
            attacker = shooter;
            isProjectile = true;
            hitType = "PROJECTILE:" + projectile.getType().name();
        }

        if (attacker != null && !attacker.equals(victim)) {
            // Track combat - this adds both players to each other's sessions
            plugin.getReplayManager().onCombat(attacker, victim, event.getFinalDamage());

            // Record detailed hit information for attacker
            if (plugin.getReplayManager().isBeingRecorded(attacker.getUniqueId())) {
                String damageInfo = String.format("hit %s for %.1f damage (%s)",
                        victim.getName(), event.getFinalDamage(), hitType);
                plugin.getReplayManager().recordAction(attacker, ActionType.DAMAGE_DEALT, damageInfo);
            }

            // Record damage received for victim with hit type
            if (plugin.getReplayManager().isBeingRecorded(victim.getUniqueId())) {
                String damageInfo = String.format("took %.1f damage from %s (%s)",
                        event.getFinalDamage(), attacker.getName(), hitType);
                plugin.getReplayManager().recordAction(victim, ActionType.DAMAGE_RECEIVED, damageInfo);
            }
        }
    }

    /**
     * Determine the type of hit based on attacker state and attack properties.
     */
    private String determineHitType(Player attacker, EntityDamageByEntityEvent event) {
        // Check for critical hit (falling + not on ground + not in water/ladder/etc.)
        boolean isCritical = !attacker.isOnGround() &&
                attacker.getFallDistance() > 0 &&
                !attacker.isInsideVehicle() &&
                !attacker.isInWater() &&
                !attacker.isClimbing() &&
                attacker.getAttackCooldown() > 0.9f;

        // Check for sweep attack (sword attack hitting multiple entities)
        boolean isSweep = false;
        org.bukkit.inventory.ItemStack mainHand = attacker.getInventory().getItemInMainHand();
        if (mainHand != null && mainHand.getType().name().endsWith("_SWORD")) {
            // Sweep happens when on ground and attack cooldown is ready
            if (attacker.isOnGround() && attacker.getAttackCooldown() > 0.9f) {
                isSweep = true;
            }
        }

        // Check for knockback enchantment
        boolean hasKnockback = false;
        if (mainHand != null) {
            hasKnockback = mainHand.containsEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK);
        }

        // Check if attack cooldown wasn't ready (weak hit)
        boolean isWeak = attacker.getAttackCooldown() < 0.9f;

        // Build hit type string
        if (isWeak) {
            return "WEAK";
        } else if (isCritical && hasKnockback) {
            return "CRIT_KNOCKBACK";
        } else if (isCritical) {
            return "CRIT";
        } else if (isSweep) {
            return "SWEEP";
        } else if (hasKnockback) {
            return "KNOCKBACK";
        } else {
            return "STRONG";
        }
    }

    // ===== PROJECTILE ACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;
        if (!plugin.getConfigManager().getSettings().isReplayLogItems()) return;

        String projectileType = projectile.getType().name();
        ActionType action = getProjectileActionType(projectileType);

        if (action != null) {
            plugin.getReplayManager().recordAction(player, action, projectileType);
        }
    }

    private ActionType getProjectileActionType(String type) {
        return switch (type.toUpperCase()) {
            case "WIND_CHARGE" -> ActionType.WIND_CHARGE_THROW; //FIX: explosion RDI
            case "ENDER_PEARL" -> ActionType.ENDER_PEARL_THROW;
            case "TRIDENT" -> ActionType.TRIDENT_THROW;
            case "EGG" -> ActionType.EGG_THROW;
            case "SNOWBALL" -> ActionType.SNOWBALL_THROW;
            case "SPLASH_POTION", "LINGERING_POTION" -> ActionType.POTION_THROW;
            case "FIREWORK" -> ActionType.FIREWORK_USE;
            default -> null;
        };
    }

    // ===== RIPTIDE =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;
        if (!plugin.getConfigManager().getSettings().isReplayLogItems()) return;

        plugin.getReplayManager().recordAction(player, ActionType.RIPTIDE_USE,
                event.getItem().getType().name());
    }

    // ===== ENTITY INTERACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;
        if (!plugin.getConfigManager().getSettings().isReplayLogEntities()) return;

        Entity entity = event.getRightClicked();
        String entityInfo = entity.getType().name();
        if (entity.getCustomName() != null) {
            entityInfo = entity.getCustomName() + " (" + entity.getType().name() + ")";
        }

        plugin.getReplayManager().recordAction(player, ActionType.ENTITY_INTERACT, entityInfo);
    }

    // Mount/dismount tracking is handled via vehicle events in EntityLogListener

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        if (!plugin.getReplayManager().isBeingRecorded(killer.getUniqueId())) return;
        if (!plugin.getConfigManager().getSettings().isReplayLogEntities()) return;

        Entity killed = event.getEntity();
        String entityInfo = killed.getType().name();
        if (killed.getCustomName() != null) {
            entityInfo = killed.getCustomName() + " (" + killed.getType().name() + ")";
        }

        plugin.getReplayManager().recordAction(killer, ActionType.ENTITY_KILL, "Killed " + entityInfo);
    }

    // ===== SHIELD BLOCKING =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShieldBlock(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!plugin.getReplayManager().isBeingRecorded(player.getUniqueId())) return;
        if (!plugin.getConfigManager().getSettings().isReplayLogCombat()) return;

        // Check if player is blocking with a shield
        if (player.isBlocking()) {
            Entity damager = event.getDamager();
            String attackerInfo = damager.getType().name();
            if (damager instanceof Player attacker) {
                attackerInfo = attacker.getName();
            }

            plugin.getReplayManager().recordAction(player, ActionType.SHIELD_BLOCK,
                    String.format("Blocked %.1f damage from %s", event.getDamage(), attackerInfo));
        }
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
