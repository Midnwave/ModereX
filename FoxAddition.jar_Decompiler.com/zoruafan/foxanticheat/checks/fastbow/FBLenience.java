package zoruafan.foxanticheat.checks.fastbow;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class FBLenience extends ChecksManager implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private final Map<Player, Long> chargeTimes = new HashMap();
   private final Map<Player, Long> lastShootTime = new HashMap();
   private boolean usFg;
   private GeyserManager fG;
   private Timer timer;
   private final String p = "fastbow.modules.lenience";

   public FBLenience(JavaPlugin plugin, FilesManager files, boolean uFg) {
      super(files);
      this.plugin = plugin;
      this.file = files;
      this.usFg = uFg;
      if (this.usFg) {
         try {
            this.fG = new GeyserManager(files);
         } catch (Exception var5) {
            this.fG = null;
            this.usFg = false;
         }
      } else {
         this.fG = null;
      }

      this.usFg = false;
      this.timer = new Timer();
      this.timer.scheduleAtFixedRate(new TimerTask() {
         public void run() {
            FBLenience.this.resetLastShootTime();
         }
      }, 0L, 6000L);
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEntityShootBow(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getProjectile() instanceof Arrow) {
            Player e = (Player)event.getEntity();
            if (!this.iAD(e, "fastbow")) {
               if (this.usFg && this.fG != null) {
                  if (!this.fG.isAllowDetect_Mode(e, "fastbow.modules.lenience.detect")) {
                     return;
                  }
               } else if (!this.file.getChecks().getBoolean("fastbow.modules.lenience.detect.java", true)) {
                  return;
               }

               String i = e.getInventory().getItemInHand().getType().name().toLowerCase();
               if (!i.equals("crossbow")) {
                  int draw = this.file.getChecks().getInt(e + ".lenience", 151);
                  long interval = this.file.getChecks().getLong(e + ".interval", 950L);
                  float force = event.getForce();
                  long drawbackTime = (long)(force * (float)draw);
                  long previousChargeTime = (Long)this.chargeTimes.getOrDefault(e, -1L);
                  long currentTime = System.currentTimeMillis();
                  if (!this.lastShootTime.containsKey(e)) {
                     this.lastShootTime.put(e, currentTime);
                  } else {
                     long lastShoot = (Long)this.lastShootTime.get(e);
                     long logInterval = currentTime - lastShoot;
                     if (previousChargeTime <= (long)draw && currentTime - lastShoot <= interval && drawbackTime > 80L) {
                        if (!e.isOnline()) {
                           return;
                        }

                        FoxFlagEvent flagEvent = new FoxFlagEvent(e, "fastbow", this.file.getChecks().getInt("fastbow.modules.lenience.vls", 1), "Lenience: `" + drawbackTime + "`/`" + draw + "`, interval: `" + logInterval + "`/`" + interval + "`.", "Lenience [FastBow]", "[lenience:" + drawbackTime + "/" + draw + "] [interval:" + logInterval + "/" + interval + "]");
                        Bukkit.getPluginManager().callEvent(flagEvent);
                        if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean("fastbow.modules.lenience.cancel.enable", true)) {
                           String cancel_type = this.file.getChecks().getString("fastbow.modules.lenience.cancel.type", "block").toLowerCase();
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

                     this.chargeTimes.put(e, drawbackTime);
                     this.lastShootTime.put(e, currentTime);
                  }
               }
            }
         }
      }
   }

   private void resetLastShootTime() {
      this.lastShootTime.clear();
   }

   @EventHandler
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin().equals(this.plugin) && this.timer != null) {
         this.timer.cancel();
      }

   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.lastShootTime.remove(player);
      this.chargeTimes.remove(player);
   }
}
