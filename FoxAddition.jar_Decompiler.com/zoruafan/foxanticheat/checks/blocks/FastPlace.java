package zoruafan.foxanticheat.checks.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class FastPlace implements Listener {
   private final FilesManager file;
   private final Map<Player, Long> lastPlacementTime;
   private boolean useFloodGate;
   private GeyserManager floodgate;
   private String path = "blocks.modules.fastplace";

   public FastPlace(FilesManager files, boolean useFloodgate) {
      this.file = files;
      this.lastPlacementTime = new HashMap();
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
   public void onBlockPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      if (this.useFloodGate) {
         if (!this.floodgate.isAllowDetect_Mode(player, this.path + ".detect")) {
            return;
         }
      } else if (!this.file.getChecks().getBoolean(this.path + ".detect.java", true)) {
         return;
      }

      if (!player.hasPermission("foxac.bypass.blocks")) {
         List<String> disabledWorlds = this.file.getChecks().getStringList("blocks.disabled-worlds");
         if (disabledWorlds == null || !disabledWorlds.contains(player.getWorld().getName())) {
            long placementInterval = this.file.getChecks().getLong(this.path + ".interval", 60L);
            Material bT = event.getBlock().getType();
            if (!bT.isTransparent() && bT.isSolid()) {
               long currentTime = System.currentTimeMillis();
               long lastPlacement = (Long)this.lastPlacementTime.getOrDefault(player, 0L);
               if (currentTime - lastPlacement < placementInterval) {
                  if (!player.isOnline()) {
                     return;
                  }

                  long placementValue = currentTime - lastPlacement;
                  FoxFlagEvent flagEvent = new FoxFlagEvent(player, "blocks", this.file.getChecks().getInt(this.path + ".vls", 1), "Interval: `" + placementValue + "`/`" + placementInterval + "`.", "FastPlace [Blocks]", "[interval:" + placementValue + "/" + placementInterval + "]");
                  Bukkit.getPluginManager().callEvent(flagEvent);
                  if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean(this.path + ".cancel.enable", true)) {
                     boolean giveBlock = this.file.getChecks().getBoolean(this.path + ".cancel.giveBlock", true);
                     if (!giveBlock) {
                        Block placedBlock = event.getBlock();
                        placedBlock.setType(Material.AIR);
                     } else {
                        event.setCancelled(true);
                     }
                  }
               }

               this.lastPlacementTime.put(player, currentTime);
            }
         }
      }
   }
}
