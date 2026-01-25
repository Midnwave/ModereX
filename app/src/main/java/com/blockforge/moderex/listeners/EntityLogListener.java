package com.blockforge.moderex.listeners;

import com.blockforge.moderex.ModereX;
import com.blockforge.moderex.replay.ReplaySession;
import com.blockforge.moderex.replay.entity.EntityLogEntry;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.List;
import java.util.UUID;

/**
 * Listener for capturing entity events during replay recording.
 * Tracks entity spawns, deaths, damage, interactions, etc.
 */
public class EntityLogListener implements Listener {

    private final ModereX plugin;

    public EntityLogListener(ModereX plugin) {
        this.plugin = plugin;
    }

    private boolean isEnabled() {
        return plugin.getConfigManager().getSettings().isReplayEntityTrackingEnabled() &&
               plugin.getConfigManager().getSettings().isReplayLogEntities();
    }

    private int getTrackingRadius() {
        return plugin.getConfigManager().getSettings().getReplayEntityTrackingRadius();
    }

    private String getEntityName(Entity entity) {
        if (entity.getCustomName() != null) {
            return entity.getCustomName();
        }
        return entity.getType().name();
    }

    /**
     * Log an entity event to all nearby active sessions.
     */
    private void logToNearbySessions(Entity entity, EntityLogEntry.Action action, String data, UUID involvedPlayer) {
        if (!isEnabled()) return;
        if (entity == null || entity.getLocation().getWorld() == null) return;

        Location loc = entity.getLocation();
        List<ReplaySession> sessions = plugin.getReplayManager()
                .getActiveSessionsNearLocation(loc, getTrackingRadius());

        for (ReplaySession session : sessions) {
            EntityLogEntry entry = new EntityLogEntry.Builder()
                    .sessionId(session.getSessionId())
                    .entity(entity.getUniqueId(), entity.getType(), getEntityName(entity))
                    .action(action)
                    .location(loc)
                    .data(data)
                    .involvedPlayer(involvedPlayer)
                    .build();

            plugin.getEntityLogManager().logEntityEvent(session.getSessionId(), entry);
        }
    }

    // ===== ENTITY SPAWN/DEATH =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        // Skip items and projectiles for spawn events (too spammy)
        if (entity instanceof Item || entity instanceof Projectile || entity instanceof ExperienceOrb) {
            return;
        }
        logToNearbySessions(entity, EntityLogEntry.Action.SPAWN, null, null);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        String data = null;

        if (killer != null) {
            data = "Killed by " + killer.getName();
        } else if (entity.getLastDamageCause() != null) {
            data = "Cause: " + entity.getLastDamageCause().getCause().name();
        }

        logToNearbySessions(entity, EntityLogEntry.Action.DEATH, data,
                killer != null ? killer.getUniqueId() : null);
    }

    // EntityRemoveEvent is deprecated - we track entity removal through death events and despawn instead

    // ===== ENTITY DAMAGE =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!isEnabled()) return;

        Entity entity = event.getEntity();
        double damage = event.getFinalDamage();
        String data = String.format("%.1f damage from %s", damage, event.getCause().name());

        UUID involvedPlayer = null;
        if (event instanceof EntityDamageByEntityEvent edbee) {
            Entity damager = edbee.getDamager();

            // Track the actual attacker
            if (damager instanceof Player player) {
                involvedPlayer = player.getUniqueId();
                data = String.format("%.1f damage from %s", damage, player.getName());
            } else if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
                involvedPlayer = shooter.getUniqueId();
                data = String.format("%.1f damage from %s (%s)", damage, shooter.getName(), damager.getType().name());
            } else {
                data = String.format("%.1f damage from %s", damage, damager.getType().name());
            }

            // Also log the damager dealing damage
            if (damager instanceof LivingEntity) {
                logToNearbySessions(damager, EntityLogEntry.Action.DAMAGE_DEALT,
                        "Dealt " + String.format("%.1f", damage) + " to " + getEntityName(entity),
                        involvedPlayer);
            }
        }

        logToNearbySessions(entity, EntityLogEntry.Action.DAMAGE_TAKEN, data, involvedPlayer);
    }

    // ===== ENTITY INTERACTIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        String data = "Interacted by " + player.getName();
        if (player.getInventory().getItemInMainHand().getType() != org.bukkit.Material.AIR) {
            data += " with " + player.getInventory().getItemInMainHand().getType().name();
        }

        logToNearbySessions(entity, EntityLogEntry.Action.INTERACT, data, player.getUniqueId());
    }

    // ===== MOUNT/DISMOUNT =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entered = event.getEntered();
        Vehicle vehicle = event.getVehicle();

        UUID playerUuid = entered instanceof Player player ? player.getUniqueId() : null;
        String data = getEntityName(entered) + " mounted";

        logToNearbySessions(vehicle, EntityLogEntry.Action.MOUNT, data, playerUuid);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {
        Entity exited = event.getExited();
        Vehicle vehicle = event.getVehicle();

        UUID playerUuid = exited instanceof Player player ? player.getUniqueId() : null;
        String data = getEntityName(exited) + " dismounted";

        logToNearbySessions(vehicle, EntityLogEntry.Action.DISMOUNT, data, playerUuid);
    }

    // ===== PROJECTILES =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        UUID shooterUuid = null;
        String data = projectile.getType().name();

        if (projectile.getShooter() instanceof Player player) {
            shooterUuid = player.getUniqueId();
            data = projectile.getType().name() + " by " + player.getName();
        } else if (projectile.getShooter() instanceof Entity entity) {
            data = projectile.getType().name() + " by " + getEntityName(entity);
        }

        logToNearbySessions(projectile, EntityLogEntry.Action.PROJECTILE_LAUNCH, data, shooterUuid);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity hitEntity = event.getHitEntity();

        String data = null;
        UUID shooterUuid = null;

        if (projectile.getShooter() instanceof Player player) {
            shooterUuid = player.getUniqueId();
        }

        if (hitEntity != null) {
            data = "Hit " + getEntityName(hitEntity);
            logToNearbySessions(projectile, EntityLogEntry.Action.PROJECTILE_HIT, data, shooterUuid);
        } else if (event.getHitBlock() != null) {
            data = "Hit block at " + event.getHitBlock().getX() + "," +
                   event.getHitBlock().getY() + "," + event.getHitBlock().getZ();
            logToNearbySessions(projectile, EntityLogEntry.Action.PROJECTILE_HIT, data, shooterUuid);
        }
    }

    // ===== ENTITY TARGETING =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (target == null) {
            logToNearbySessions(entity, EntityLogEntry.Action.TARGET_CHANGE, "Cleared target", null);
        } else {
            UUID playerUuid = target instanceof Player player ? player.getUniqueId() : null;
            logToNearbySessions(entity, EntityLogEntry.Action.TARGET_CHANGE,
                    "Now targeting " + getEntityName(target), playerUuid);
        }
    }

    // ===== TAMING/BREEDING =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        Entity entity = event.getEntity();
        AnimalTamer owner = event.getOwner();

        UUID playerUuid = owner instanceof Player player ? player.getUniqueId() : null;
        String data = owner != null ? "Tamed by " + owner.getName() : "Tamed";

        logToNearbySessions(entity, EntityLogEntry.Action.TAME, data, playerUuid);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBreed(EntityBreedEvent event) {
        Entity child = event.getEntity();
        Entity mother = event.getMother();
        Entity father = event.getFather();
        LivingEntity breeder = event.getBreeder();

        UUID playerUuid = breeder instanceof Player player ? player.getUniqueId() : null;
        String data = "Bred from " + getEntityName(mother) + " and " + getEntityName(father);

        logToNearbySessions(child, EntityLogEntry.Action.BREED, data, playerUuid);
    }

    // ===== EXPLOSIONS =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        String data = event.blockList().size() + " blocks affected";
        logToNearbySessions(entity, EntityLogEntry.Action.EXPLODE, data, null);
    }

    // ===== ENTITY TRANSFORM =====

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent event) {
        Entity original = event.getEntity();
        Entity transformed = event.getTransformedEntity();

        String data = getEntityName(original) + " -> " + getEntityName(transformed) +
                " (" + event.getTransformReason().name() + ")";

        logToNearbySessions(original, EntityLogEntry.Action.TRANSFORM, data, null);
    }
}
