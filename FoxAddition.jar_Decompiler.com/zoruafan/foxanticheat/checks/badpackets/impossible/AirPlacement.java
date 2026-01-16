package zoruafan.foxanticheat.checks.badpackets.impossible;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class AirPlacement implements Listener {
   private final FilesManager file;
   private boolean useFloodGate;
   private GeyserManager floodgate;
   private String path = "badpackets.modules.impossible.modules.blocks.airplacement";

   public AirPlacement(FilesManager files, boolean useFloodgate) {
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
   public void onBlockPlace(BlockPlaceEvent event) {
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
            String typePlaced = event.getBlock().getType().name().toLowerCase();
            if (!typePlaced.contains("bamboo") && !typePlaced.contains("scaffolding") && !typePlaced.contains("sugar") && !typePlaced.contains("powder_snow") && !typePlaced.contains("carpet")) {
               if (disabledWorlds == null || !disabledWorlds.contains(player.getWorld().getName())) {
                  Block placedBlock = event.getBlock();
                  List ignoredMaterials = Arrays.asList(event.getBlock().getRelative(BlockFace.UP).getType(), event.getBlock().getRelative(BlockFace.DOWN).getType(), event.getBlock().getRelative(BlockFace.EAST).getType(), event.getBlock().getRelative(BlockFace.WEST).getType(), event.getBlock().getRelative(BlockFace.SOUTH).getType(), event.getBlock().getRelative(BlockFace.NORTH).getType());

                  try {
                     if (ignoredMaterials.contains(Material.valueOf("POWDER_SNOW"))) {
                        return;
                     }
                  } catch (Exception var11) {
                  }

                  try {
                     if (ignoredMaterials.contains(Material.valueOf("WATER_LiLY"))) {
                        return;
                     }
                  } catch (Exception var10) {
                  }

                  try {
                     if (ignoredMaterials.contains(Material.valueOf("SEA_LANTERN"))) {
                        return;
                     }
                  } catch (Exception var9) {
                  }

                  if (!placedBlock.getType().isTransparent()) {
                     try {
                        if (placedBlock.isEmpty()) {
                           return;
                        }
                     } catch (Exception var8) {
                     }

                     if (!this.isSupportedBySolid(placedBlock)) {
                        if (!player.isOnline()) {
                           return;
                        }

                        FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", this.file.getChecks().getInt(this.path + ".vls", 5), "Illegal placement in the air. Placed block:" + placedBlock.getType(), "AirPlacement [BadPackets]", "[block:" + placedBlock.getType() + "]");
                        Bukkit.getPluginManager().callEvent(flagEvent);
                        if (!flagEvent.isCancelled()) {
                           event.setCancelled(true);
                        }
                     }

                  }
               }
            }
         }
      }
   }

   private boolean isSupportedBySolid(Block block) {
      BlockFace[] sides = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP};
      BlockFace[] var6 = sides;
      int var5 = sides.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         BlockFace side = var6[var4];
         Block relativeBlock = block.getRelative(side);
         Material relativeType = relativeBlock.getType();
         if (!relativeType.isSolid() && relativeBlock.getType() != Material.AIR || !relativeType.isTransparent() && relativeBlock.getType() != Material.AIR) {
            return true;
         }
      }

      return false;
   }
}
