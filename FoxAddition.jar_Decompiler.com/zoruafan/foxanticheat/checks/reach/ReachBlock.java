package zoruafan.foxanticheat.checks.reach;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;
import zoruafan.foxanticheat.manager.hooks.VeinMinerManager;
import zoruafan.foxanticheat.manager.hooks.mcMMOManager;

public class ReachBlock extends ChecksManager implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private boolean uVM;
   private VeinMinerManager vM;
   private boolean usFg;
   private GeyserManager fG;
   private boolean usMo;
   private mcMMOManager mO;
   private final String p = "reach.modules.block";

   public ReachBlock(JavaPlugin plugin, FilesManager files, boolean uVm, boolean uFg, boolean uMo) {
      super(files);
      this.plugin = plugin;
      this.file = files;
      this.usFg = uFg;
      if (this.usFg) {
         try {
            this.fG = new GeyserManager(files);
         } catch (Exception var9) {
            this.fG = null;
            this.usFg = false;
         }
      } else {
         this.fG = null;
      }

      this.usFg = false;
      this.uVM = uVm;
      if (this.uVM) {
         try {
            this.vM = new VeinMinerManager(plugin);
         } catch (Exception var8) {
         }
      } else {
         this.vM = null;
      }

      this.usMo = uMo;
      if (this.usMo) {
         try {
            this.mO = new mcMMOManager(files);
         } catch (Exception var7) {
            this.mO = null;
            this.usMo = false;
         }
      } else {
         this.mO = null;
      }

   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      Player e = event.getPlayer();
      if (!this.iAD(e, "reach")) {
         if (this.usFg && this.fG != null) {
            if (!this.fG.isAllowDetect_Mode(e, "reach.modules.block.detect")) {
               return;
            }
         } else if (!this.file.getChecks().getBoolean("reach.modules.block.detect.java", true)) {
            return;
         }

         if (event.getPlayer().getType() == EntityType.PLAYER && !e.getGameMode().name().contains("CREATIVE")) {
            String typePlaced = event.getBlock().getType().name().toLowerCase();
            if (!typePlaced.contains("bamboo") && !typePlaced.contains("scaffolding") && !typePlaced.contains("sugar")) {
               double thresholdHorizontal = this.file.getChecks().getDouble("reach.modules.block.threshold.place.horizontal", 4.82D);
               double thresholdVertical = this.file.getChecks().getDouble("reach.modules.block.threshold.place.vertical", 5.1D);
               double horizontalDistance = Math.abs(e.getLocation().getX() - event.getBlock().getLocation().getX());
               double verticalDistance = Math.abs(e.getLocation().getY() - event.getBlock().getLocation().getY());
               boolean detected = false;
               String type = "";
               String value = "";
               if (horizontalDistance > thresholdHorizontal) {
                  type = "horizontal";
                  detected = true;
                  value = String.valueOf(horizontalDistance) + "/" + thresholdHorizontal;
               } else if (verticalDistance > thresholdVertical) {
                  type = "vertical";
                  detected = true;
                  value = String.valueOf(verticalDistance) + "/" + thresholdVertical;
               }

               if (detected) {
                  FoxFlagEvent fE = new FoxFlagEvent(e, "reach", this.file.getChecks().getInt("reach.modules.block.vls", 1), "In: `" + type + "`, distance: `" + value + "`.", "Reach [Place] (Block)", "[in:" + type + "] [block:" + event.getBlock().getType() + "] [distance:" + value + "]");
                  Bukkit.getPluginManager().callEvent(fE);
                  if (!fE.isCancelled() && this.file.getChecks().getBoolean("reach.modules.block.cancel.enable", true)) {
                     boolean giveBlock = this.file.getChecks().getBoolean("reach.modules.block.cancel.giveBlock", true);
                     if (!giveBlock) {
                        Block placedBlock = event.getBlock();
                        placedBlock.setType(Material.AIR);
                     } else {
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
      Player e = event.getPlayer();
      if (!this.iAD(e, "reach")) {
         if (this.usFg) {
            if (!this.fG.isAllowDetect_Mode(e, "reach.modules.block.detect")) {
               return;
            }

            if (!this.file.getChecks().getBoolean("reach.modules.block.detect.java", true)) {
               return;
            }
         }

         if (event.getPlayer().getType() == EntityType.PLAYER && !e.getGameMode().name().contains("CREATIVE")) {
            if (!this.uVM || this.vM == null || !this.vM.isInVeinMiner(e, event.getBlock())) {
               if (!this.usMo || this.mO == null || !this.mO.isTreeFeller(e)) {
                  String typePlaced = event.getBlock().getType().name().toLowerCase();
                  if (!typePlaced.contains("bamboo") && !typePlaced.contains("scaffolding") && !typePlaced.contains("sugar")) {
                     double thresholdHorizontal = this.file.getChecks().getDouble("reach.modules.block.threshold.break.horizontal", 5.25D);
                     double thresholdVertical = this.file.getChecks().getDouble("reach.modules.block.threshold.break.vertical", 5.83D);
                     double horizontalDistance = Math.abs(e.getLocation().getX() - event.getBlock().getLocation().getX());
                     double verticalDistance = Math.abs(e.getLocation().getY() - event.getBlock().getLocation().getY());
                     boolean detected = false;
                     String type = "";
                     String value = "";
                     if (horizontalDistance > thresholdHorizontal) {
                        type = "horizontal";
                        detected = true;
                        value = String.valueOf(horizontalDistance) + "/" + thresholdHorizontal;
                     } else if (verticalDistance > thresholdVertical) {
                        type = "vertical";
                        detected = true;
                        value = String.valueOf(verticalDistance) + "/" + thresholdVertical;
                     }

                     if (detected) {
                        FoxFlagEvent flagEvent = new FoxFlagEvent(e, "reach", this.file.getChecks().getInt("reach.modules.block.vls", 1), "In: `" + type + "`, distance: `" + value + "`.", "Reach [Break] (Block)", "[in:" + type + "] [block:" + event.getBlock().getType() + "] [distance:" + value + "]");
                        Bukkit.getPluginManager().callEvent(flagEvent);
                        if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean("reach.modules.block.cancel.enable", true)) {
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
