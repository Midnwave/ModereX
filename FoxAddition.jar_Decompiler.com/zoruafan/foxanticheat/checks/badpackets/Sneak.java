package zoruafan.foxanticheat.checks.badpackets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class Sneak implements Listener {
   private final FilesManager file;
   private final Map<Player, Long> lastSneakTime;
   private final Map<Player, Integer> sneakCount;
   private boolean useFloodGate;
   private GeyserManager floodgate;
   private String path = "badpackets.modules.sneak";

   public Sneak(FilesManager files, boolean useFloodgate) {
      this.file = files;
      this.lastSneakTime = new HashMap();
      this.sneakCount = new HashMap();
      this.useFloodGate = useFloodgate;
      if (this.useFloodGate) {
         this.floodgate = new GeyserManager(files);
      } else {
         this.floodgate = null;
      }

   }

   @EventHandler
   public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
      Player player = event.getPlayer();
      if (player instanceof Player) {
         if (this.useFloodGate) {
            if (!this.floodgate.isAllowDetect_Mode(player, this.path + ".detect")) {
               return;
            }
         } else if (!this.file.getChecks().getBoolean(this.path + ".detect.java", true)) {
            return;
         }

         if (!player.hasPermission("foxac.bypass.badpackets")) {
            List<String> disabledWorlds = this.file.getChecks().getStringList("badpackets.disabled-worlds");
            if (disabledWorlds == null || !disabledWorlds.contains(player.getWorld().getName())) {
               if (!this.isInsideUnloadedChunk(player)) {
                  int maxSneakCount = this.file.getChecks().getInt(this.path + ".max", 25);
                  long timeInterval = this.file.getChecks().getLong(this.path + ".interval", 100L);
                  long currentTime = System.currentTimeMillis();
                  if (!this.lastSneakTime.containsKey(player)) {
                     this.lastSneakTime.put(player, currentTime);
                     this.sneakCount.put(player, 1);
                  } else {
                     long lastSneak = (Long)this.lastSneakTime.get(player);
                     int count = (Integer)this.sneakCount.getOrDefault(player, 0);
                     int var10000;
                     if (currentTime - lastSneak <= timeInterval) {
                        ++count;
                        var10000 = count;
                     } else {
                        var10000 = 1;
                     }

                     count = var10000;
                     this.lastSneakTime.put(player, currentTime);
                     this.sneakCount.put(player, count);
                     if (count >= maxSneakCount) {
                        if (!player.isOnline()) {
                           return;
                        }

                        long sneakValue = currentTime - lastSneak;
                        FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", this.file.getChecks().getInt(this.path + ".vls", 5), "Toggles: `" + count + "`/`" + maxSneakCount + "`, interval: `" + sneakValue + "`/`" + timeInterval + "`.", "Sneak [BadPackets]", "[toggles:" + count + "/" + maxSneakCount + "] [interval:" + sneakValue + "/" + timeInterval + "]");
                        Bukkit.getPluginManager().callEvent(flagEvent);
                        if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean(this.path + ".cancel", true)) {
                           event.setCancelled(true);
                        }
                     }

                  }
               }
            }
         }
      }
   }

   private boolean isInsideUnloadedChunk(Player player) {
      Location location = player.getLocation();
      int chunkX = location.getBlockX() >> 4;
      int chunkZ = location.getBlockZ() >> 4;
      return !player.getWorld().isChunkLoaded(chunkX, chunkZ);
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.lastSneakTime.remove(player);
      this.sneakCount.remove(player);
   }
}
