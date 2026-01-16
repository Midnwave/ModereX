package zoruafan.foxanticheat.checks.badpackets;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class Regen implements Listener {
   private final FilesManager file;
   private final Map<Player, Long> lastRegenTimeMap;
   private boolean useFloodGate;
   private GeyserManager floodgate;
   private String path = "badpackets.modules.regen";

   public Regen(FilesManager files, boolean useFloodgate) {
      this.file = files;
      this.lastRegenTimeMap = new HashMap();
      this.useFloodGate = useFloodgate;
      if (this.useFloodGate) {
         this.floodgate = new GeyserManager(files);
      } else {
         this.floodgate = null;
      }

   }

   @EventHandler
   public void onEntityRegainHealth(EntityRegainHealthEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.useFloodGate) {
            if (!this.floodgate.isAllowDetect_Mode(player, this.path + ".detect")) {
               return;
            }
         } else if (!this.file.getChecks().getBoolean(this.path + ".detect.java", true)) {
            return;
         }

         if (!player.hasPermission("foxac.bypass.badpackets")) {
            double amount = event.getAmount();
            long currentTime = System.currentTimeMillis();
            if (amount > 0.0D && !player.isDead()) {
               if (!player.isOnline()) {
                  return;
               }

               long minRegenDelay = this.file.getChecks().getLong(this.path + ".min", 600L);
               if (this.lastRegenTimeMap.containsKey(player)) {
                  long lastRegenTime = (Long)this.lastRegenTimeMap.get(player);
                  long regenTimeDifference = currentTime - lastRegenTime;
                  if (regenTimeDifference <= minRegenDelay) {
                     event.setCancelled(true);
                  }

                  this.lastRegenTimeMap.put(player, currentTime);
               }
            }

         }
      }
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.lastRegenTimeMap.remove(player);
   }
}
