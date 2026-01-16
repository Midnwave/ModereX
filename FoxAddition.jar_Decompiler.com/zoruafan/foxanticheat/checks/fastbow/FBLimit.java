package zoruafan.foxanticheat.checks.fastbow;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.ExecutableItemsManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class FBLimit extends ChecksManager implements Listener {
   private final FilesManager file;
   private final Map<Player, Integer> ShootCount = new HashMap();
   private final Map<Player, Long> lastShootTime = new HashMap();
   private boolean usFg;
   private GeyserManager fG;
   private boolean usEi;
   private ExecutableItemsManager eI;
   private final String p = "fastbow.modules.limit";

   public FBLimit(FilesManager files, boolean uFg, boolean uEi) {
      super(files);
      this.file = files;
      this.usFg = uFg;
      if (this.usFg) {
         try {
            this.fG = new GeyserManager(files);
         } catch (Exception var6) {
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
         } catch (Exception var5) {
         }
      } else {
         this.eI = null;
      }

   }

   @EventHandler
   public void onProjectileLaunch(ProjectileLaunchEvent event) {
      if (event.getEntity().getShooter() instanceof Player) {
         if (event.getEntity() instanceof Arrow) {
            Player e = (Player)event.getEntity().getShooter();
            if (!this.iAD(e, "fastbow")) {
               if (this.usFg && this.fG != null) {
                  if (!this.fG.isAllowDetect_Mode(e, "fastbow.modules.limit.detect")) {
                     return;
                  }
               } else if (!this.file.getChecks().getBoolean("fastbow.modules.limit.detect.java", true)) {
                  return;
               }

               if (!this.usEi || this.eI == null || !this.eI.isExecutableItem(e)) {
                  String i = e.getInventory().getItemInHand().getType().name().toLowerCase();
                  if (!i.equals("crossbow")) {
                     int max = this.file.getChecks().getInt("fastbow.modules.limit.maxShoots", 5);
                     long interval = (long)this.file.getChecks().getInt("fastbow.modules.limit.interval", 600);
                     int currentShootCount = (Integer)this.ShootCount.getOrDefault(e, 0);
                     long currentTime = System.currentTimeMillis();
                     if (currentShootCount >= max) {
                        long lastShoot = (Long)this.lastShootTime.getOrDefault(e, 0L);
                        if (currentTime - lastShoot < interval) {
                           if (!e.isOnline()) {
                              return;
                           }

                           long ShootValue = currentTime - lastShoot;
                           FoxFlagEvent flagEvent = new FoxFlagEvent(e, "fastbow", this.file.getChecks().getInt("fastbow.modules.limit.vls", 1), "Shoots: `" + currentShootCount + "`/`" + max + "`, interval: `" + ShootValue + "`/`" + interval + "`.", "Limit [FastBow]", "[Shoots:" + currentShootCount + "/" + max + "] [interval:" + ShootValue + "/" + interval + "]");
                           Bukkit.getPluginManager().callEvent(flagEvent);
                           if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean("fastbow.modules.limit.cancel.enable", true)) {
                              String cancel_type = this.file.getChecks().getString("fastbow.modules.limit.cancel.type", "block").toLowerCase();
                              if (cancel_type.equals("block")) {
                                 event.setCancelled(true);
                              } else if (cancel_type.equals("hotbar")) {
                                 event.setCancelled(true);
                                 Random random = new Random();
                                 int currentSlot = e.getInventory().getHeldItemSlot();
                                 int newSlot = random.nextInt(8);
                                 if (newSlot >= currentSlot) {
                                    ++newSlot;
                                 }

                                 e.getInventory().setHeldItemSlot(newSlot);
                              } else {
                                 Bukkit.getLogger().warning("[FASTBOW] Invalid option type for cancel, check your checks.yml. Using for now 'block'.");
                                 event.setCancelled(true);
                              }
                           }
                        }

                        this.ShootCount.put(e, 1);
                        this.lastShootTime.put(e, currentTime);
                     } else {
                        this.ShootCount.put(e, currentShootCount + 1);
                        this.lastShootTime.put(e, currentTime);
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.lastShootTime.remove(player);
      this.ShootCount.remove(player);
   }
}
