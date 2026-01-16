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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class LiquidInteract implements Listener {
   private List<Material> liquidMaterials;
   private final FilesManager file;
   private boolean useFloodGate;
   private GeyserManager floodgate;
   private String path = "badpackets.modules.impossible.modules.blocks.liquidinteract";

   public LiquidInteract(FilesManager files, boolean useFloodgate) {
      this.file = files;

      try {
         this.liquidMaterials = Arrays.asList(Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);
      } catch (NoSuchFieldError var4) {
         this.liquidMaterials = Arrays.asList(Material.WATER, Material.LAVA);
      }

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
      } else if (!this.file.getChecks().getBoolean(this.path + ".detect.java")) {
         return;
      }

      if (!player.hasPermission("foxac.bypass.badpackets")) {
         if (player instanceof Player) {
            List<String> disabledWorlds = this.file.getChecks().getStringList("badpackets.disabled-worlds");
            if (disabledWorlds == null || !disabledWorlds.contains(player.getWorld().getName())) {
               Block placedBlock = event.getBlock();
               Material getType = placedBlock.getType();
               String typePlaced = event.getBlock().getType().name().toLowerCase();
               if (!typePlaced.contains("lily") && !typePlaced.contains("sea") && !typePlaced.contains("scaffolding")) {
                  Block blockBelow = placedBlock.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
                  if (this.liquidMaterials.contains(blockBelow.getType()) && !this.isSupportedBySolid(placedBlock)) {
                     FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", this.file.getChecks().getInt(this.path + ".vls", 4), "Illegal interaction in liquids.", "LiquidInteract [BadPackets]", "[Block:" + getType + "]");
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

   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (this.useFloodGate && this.floodgate != null) {
         if (!this.floodgate.isAllowDetect_Mode(player, this.path + ".detect")) {
            return;
         }

         if (!this.file.getChecks().getBoolean(this.path + ".detect.java", true)) {
            return;
         }
      }

      if (!player.hasPermission("foxac.bypass.badpackets")) {
         Block block = event.getBlock();
         Material getType = block.getType();
         if (this.liquidMaterials.contains(getType)) {
            FoxFlagEvent flagEvent = new FoxFlagEvent(player, "badpackets", this.file.getChecks().getInt(this.path + ".vls", 4), "Illegal interaction in liquids.", "LiquidInteract [BadPackets]", "[Block:" + getType + "]");
            if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean(this.path + ".cancel", true)) {
               event.setCancelled(true);
            }
         }

      }
   }

   private boolean isSupportedBySolid(Block block) {
      BlockFace[] sides = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP};
      BlockFace[] var6 = sides;
      int var5 = sides.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         BlockFace side = var6[var4];
         Block relativeBlock = block.getRelative(side);
         if (relativeBlock.getType().isSolid() && !relativeBlock.getType().isTransparent()) {
            return true;
         }
      }

      return false;
   }
}
