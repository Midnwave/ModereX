package zoruafan.foxanticheat.checks.badpackets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class FastSwitch implements Listener {
   private final FilesManager file;
   private final Map<Player, Long> lastSwitchTime = new HashMap();
   private boolean useFloodGate;
   private GeyserManager floodgate;
   long switchInterval;
   long lastSwitch;
   int lastSlot = -1;
   private String path = "badpackets.modules.fastswitch";

   public FastSwitch(FilesManager files, boolean useFloodgate) {
      this.file = files;
      this.useFloodGate = useFloodgate;
      if (this.useFloodGate) {
         this.floodgate = new GeyserManager(files);
      } else {
         this.floodgate = null;
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerItemHeld(PlayerItemHeldEvent event) {
      Player player = event.getPlayer();
      if (this.useFloodGate) {
         if (!this.floodgate.isAllowDetect_Mode(player, this.path + ".detect")) {
            return;
         }
      } else if (!this.file.getChecks().getBoolean(this.path + ".detect.java", true)) {
         return;
      }

      if (!player.hasPermission("foxac.bypass.badpackets")) {
         if (player instanceof Player) {
            List<String> disabledWorlds = this.file.getChecks().getStringList("badpackets.disabled-worlds");
            if (disabledWorlds == null || !disabledWorlds.contains(player.getWorld().getName())) {
               if (!this.isInsideUnloadedChunk(player)) {
                  int New = event.getNewSlot();
                  int Old = event.getPreviousSlot();
                  if (!checkSlot(New, Old)) {
                     long currentTime = System.currentTimeMillis();
                     if (New != this.lastSlot && currentTime - (this.lastSwitch = (Long)this.lastSwitchTime.getOrDefault(player, 0L)) < (this.switchInterval = this.file.getChecks().getLong(this.path + ".interval", 50L))) {
                        if (!player.isOnline()) {
                           return;
                        }

                        long switchValue = currentTime - this.lastSwitch;
                        FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", this.file.getChecks().getInt(this.path + ".vls", 2), "Interval: `" + switchValue + "`/`" + this.switchInterval + "`.", "FastSwitch [BadPackets]", "[interval:" + switchValue + "/" + this.switchInterval + "]");
                        Bukkit.getPluginManager().callEvent(flagEvent);
                        if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean(this.path + ".cancel", true)) {
                           event.setCancelled(true);
                        }
                     }

                     this.lastSlot = Old;
                     this.lastSwitchTime.put(player, currentTime);
                  }
               }
            }
         }
      }
   }

   private static boolean checkSlot(int slot1, int slot2) {
      return Math.abs(slot1 - slot2) == 1 || Math.abs(slot1 - slot2) <= 1;
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
      this.lastSwitchTime.remove(player);
   }
}
