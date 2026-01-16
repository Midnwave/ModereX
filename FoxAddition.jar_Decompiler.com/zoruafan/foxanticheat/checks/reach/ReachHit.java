package zoruafan.foxanticheat.checks.reach;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.ExecutableItemsManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;
import zoruafan.foxanticheat.manager.hooks.WeaponMechanicsManager;

public class ReachHit extends ChecksManager implements Listener {
   private final FilesManager file;
   private boolean usFg;
   private GeyserManager fG;
   private boolean usEi;
   private ExecutableItemsManager eI;
   private boolean usWm;
   private WeaponMechanicsManager wM;
   private String p = "reach.modules.hit";

   public ReachHit(FilesManager files, boolean uEi, boolean uFg, boolean uWm) {
      super(files);
      this.file = files;
      this.usFg = uFg;
      if (this.usFg) {
         try {
            this.fG = new GeyserManager(files);
         } catch (Exception var8) {
            this.fG = null;
            this.usFg = false;
         }
      } else {
         this.fG = null;
      }

      this.usFg = false;
      this.usEi = uEi;
      if (this.usEi) {
         try {
            this.eI = new ExecutableItemsManager();
         } catch (Exception var7) {
         }
      } else {
         this.eI = null;
      }

      this.usWm = uWm;
      if (this.usWm) {
         try {
            this.wM = new WeaponMechanicsManager();
         } catch (Exception var6) {
         }
      } else {
         this.wM = null;
      }

   }

   @EventHandler
   public void onPlayerAttack(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Player e = (Player)event.getDamager();
         if (!this.iAD(e, "reach")) {
            if (this.usFg && this.fG != null) {
               if (!this.fG.isAllowDetect_Mode(e, this.p + ".detect")) {
                  return;
               }
            } else if (!this.file.getChecks().getBoolean(this.p + ".detect.java", true)) {
               return;
            }

            if (!this.usWm || !this.wM.isWeapon(e)) {
               double reachThreshold = this.file.getChecks().getDouble(this.p + ".threshold.java.horizontal", 4.1D);
               double reachThresholdv = this.file.getChecks().getDouble(this.p + ".threshold.java.vertical", 3.49D);
               if (this.usFg && this.fG.isBedrock(e)) {
                  reachThreshold = this.file.getChecks().getDouble(this.p + ".threshold.bedrock.horizontal", 5.11D);
               }

               reachThresholdv = this.file.getChecks().getDouble(this.p + ".threshold.bedrock.vertical", 4.1D);
               if (!this.usEi || this.eI == null || !this.eI.isExecutableItem(e)) {
                  if (!(event.getDamager() instanceof Projectile) && event.getCause() != DamageCause.THORNS && event.getCause() != DamageCause.MAGIC && event.getCause() != DamageCause.CUSTOM) {
                     String i = e.getInventory().getItemInHand().getType().name().toLowerCase();
                     if (!i.equals("egg") && !i.equals("bow") && !i.equals("snowball") && !i.equals("snow_ball") || !this.file.getChecks().getBoolean(this.p + ".fix", false)) {
                        Entity v = event.getEntity();
                        int ping = this.gP(e);
                        if (!e.getGameMode().name().contains("CREATIVE") && !e.isInsideVehicle() && !v.isDead() && !v.isInsideVehicle() && !(v instanceof EnderDragon)) {
                           try {
                              if (v.getType() == EntityType.ARMOR_STAND) {
                                 return;
                              }
                           } catch (NoSuchFieldError var27) {
                           }

                           double horizontalDistance = this.getHorizontalDistance(e.getLocation(), v.getLocation());
                           double attackerJumpHeight = this.getJumpHeight(e);
                           double verticalDistance = Math.abs(e.getLocation().getY() - v.getLocation().getY());
                           double jumpMultiplier = this.file.getChecks().getDouble(this.p + ".multiplier", 1.5D);
                           double adjustedThreshold = reachThreshold + attackerJumpHeight * jumpMultiplier;
                           double adjustedThresholdv = reachThresholdv + attackerJumpHeight * jumpMultiplier;
                           boolean detected = false;
                           String type = "";
                           String value = "";
                           if (horizontalDistance > adjustedThreshold) {
                              type = "horizontal";
                              detected = true;
                              value = String.valueOf(horizontalDistance) + "/" + adjustedThreshold;
                           } else if (verticalDistance > adjustedThresholdv) {
                              type = "vertical";
                              detected = true;
                              value = String.valueOf(verticalDistance) + "/" + adjustedThresholdv;
                           }

                           if (detected) {
                              if (!e.isOnline()) {
                                 return;
                              }

                              FoxFlagEvent flagEvent = new FoxFlagEvent(e, "reach", this.file.getChecks().getInt(this.p + ".vls", 1), "In: `" + type + "`, distance: `" + value + "`.", "Reach (Hit)", "[in:" + type + "] [distance:" + value + "] [ping:" + ping + "]");
                              Bukkit.getPluginManager().callEvent(flagEvent);
                              if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean(this.p + ".cancel.enable", true)) {
                                 String cancel_type = this.file.getChecks().getString(this.p + ".cancel.type", "block").toLowerCase();
                                 if (cancel_type.equals("block")) {
                                    event.setCancelled(true);
                                 } else if (cancel_type.equals("silent")) {
                                    event.setDamage(0.0D);
                                 } else {
                                    Bukkit.getLogger().warning("[REACH] Invalid option type for cancel, check your checks.yml. Using for now 'block'.");
                                    event.setCancelled(true);
                                 }
                              }
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   private double getHorizontalDistance(Location location1, Location location2) {
      double dx = location1.getX() - location2.getX();
      double dz = location1.getZ() - location2.getZ();
      return Math.sqrt(dx * dx + dz * dz);
   }

   private double getJumpHeight(Player player) {
      double jumpHeight = (double)player.getFallDistance();
      return jumpHeight;
   }
}
