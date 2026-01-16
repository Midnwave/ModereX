package zoruafan.foxanticheat.checks.phase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;

public class Phase extends ChecksManager implements Listener {
   private final FilesManager file;
   private final PhaseData pd;
   private final Map<UUID, Long> joined = new HashMap();
   private final Set<Location> verifiedLocations = new HashSet();
   private boolean usFg;
   private GeyserManager fG;
   private final List<String> medium = Arrays.asList("CAKE", "SLAB", "STAIRS", "FENCE", "_WALL", "STEP", "SCULK_SENSOR", "SCULK_SHRIEKER", "CALIBRATED_SCULK_SENSOR");
   private final List<String> FixY_1 = Arrays.asList("CHEST", "BREWING", "SOUL_SAND", "SOULSAND", "LECTERN", "MUD");
   private final List<String> FixY_2 = Arrays.asList("PORTAL_FRAME", "_TABLE");
   private final List<String> FixY_3 = Arrays.asList("BED", "STONECUTTER", "LANTERN", "SOUL_LANTERN");
   private final List<String> FixY_4 = Arrays.asList("BREWING", "COMPOSTER", "LECTERN");
   private final List<String> FixY_5 = Arrays.asList("DAYLIGHT");
   private final List<String> FixY_6 = Arrays.asList("HOPPER");
   private final List<String> FixY_7 = Arrays.asList("CAULDRON", "WATER_CAULDRON");
   private final List<String> FixY_8 = Arrays.asList("GRASS_PATH", "FARMLAND", "CACTUS", "DIRT_PATH", "HONEY_BLOCK");
   private final List<String> FixY_9 = Arrays.asList("TURTLE_EGG", "CAMPFIRE", "SOUL_CAMPFIRE", "AMETHYST_CLUSTER");
   private final List<String> FixBL = Arrays.asList("_DOOR");

   public Phase(FilesManager files, boolean uFg, PhaseData pd) {
      super(files);
      this.file = files;
      this.pd = pd;
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
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      UUID e = event.getPlayer().getUniqueId();
      this.joined.put(e, System.currentTimeMillis());
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.LOW
   )
   public void onPlayerMove(PlayerMoveEvent event) {
      Player e = event.getPlayer();
      if (!this.iAD(e, "phase")) {
         if (this.usFg && this.fG != null) {
            if (!this.fG.isAllowDetect_Mode(e, "phase.detect")) {
               return;
            }
         } else if (!this.file.getChecks().getBoolean("phase.detect.java", true)) {
            return;
         }

         if (!e.isInsideVehicle() && !e.getGameMode().name().contains("SPECTATOR") && !e.isDead() && (this.joinTime(e.getUniqueId()) > 3L || this.joinTime(e.getUniqueId()) == 0L)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            Material blockType = to.getBlock().getType();
            Block blockType_from = from.getBlock();
            Block blockType_to = to.getBlock();
            String blockName = to.getBlock().getType().name().toLowerCase();
            double from_Y = event.getFrom().getY();
            String fromY = String.valueOf(from_Y);
            String[] to_loc = fromY.split("\\.");
            float fromY_g = Float.parseFloat(to_loc[1]);
            if (!this.isFixed(e, blockType_from, blockType_to, fromY_g)) {
               if (!blockName.contains("sand") && !blockName.contains("sculk_veins") && !blockName.contains("trapdoor") && !blockName.equals("gravel") && !blockName.contains("banner") && !blockName.contains("sign") && !blockName.contains("_wall") && !blockName.contains("bars") && !blockName.contains("soil") && !blockName.contains("anvil") && !blockName.contains("fence") && !blockName.contains("glass_pane") && !blockName.contains("piston") && !blockName.contains("plate") && !blockName.contains("coral_fan")) {
                  if (blockType.isSolid() && !blockType.isTransparent() && !this.pd.getEB(blockType.toString()) && !this.verifiedLocations.contains(to)) {
                     if (!e.isOnline()) {
                        return;
                     }

                     FoxFlagEvent flagEvent = new FoxFlagEvent(e, "phase", this.file.getChecks().getInt("phase.vls", 1), "Block: `" + blockType + "`.", "Phase", "[Block:" + blockType + "] [loc:" + String.format("%.1f, %.1f, %.1f", to.getX(), to.getY(), to.getZ()) + "]");
                     Bukkit.getPluginManager().callEvent(flagEvent);
                     if (!flagEvent.isCancelled() && this.file.getChecks().getBoolean("phase.cancel", true)) {
                        event.setCancelled(true);
                     }
                  }

               }
            }
         }
      }
   }

   private long joinTime(UUID uuid) {
      return this.joined.containsKey(uuid) ? (System.currentTimeMillis() - (Long)this.joined.get(uuid)) / 1000L : 0L;
   }

   private boolean isFixed(Player player, Block fromBlock, Block toBlock, float fromY) {
      String fromMaterial = fromBlock.getType().name();
      String toMaterial = toBlock.getType().name();
      return this.checkList(player, fromMaterial, toMaterial, this.medium, "medium", (double)fromY, 5.0D, 5.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_1, "fixy_1", (double)fromY, 875.0D, 875.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_2, "fixy_2", (double)fromY, 75.0D, 75.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_3, "fixy_3", (double)fromY, 562.0D, 562.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_4, "fixy_4", (double)fromY, 125.0D, 125.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_5, "fixy_5", (double)fromY, 375.0D, 375.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_6, "fixy_6", (double)fromY, 625.0D, 625.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_7, "fixy_7", (double)fromY, 312.0D, 25.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_8, "fixy_8", (double)fromY, 937.0D, 937.0D) || this.checkList(player, fromMaterial, toMaterial, this.FixY_9, "fixy_9", (double)fromY, 437.0D, 437.0D) || this.checkBlockList(player, fromMaterial, toMaterial, this.FixBL, "door");
   }

   private boolean checkList(Player e, String fromMaterial, String toMaterial, List<String> materials, String configKey, double fromY, double threshold, double threshold2) {
      Iterator var13 = materials.iterator();

      String material;
      do {
         if (!var13.hasNext()) {
            return false;
         }

         material = (String)var13.next();
      } while(!fromMaterial.contains(material) && !toMaterial.contains(material));

      String value = String.valueOf(e.getLocation().getY());
      String[] location = value.split("\\.");
      float loc = Float.parseFloat(location[1]);
      return (double)loc >= threshold || (double)loc >= threshold2 || fromY == 0.0D || fromY == 625.0D || fromY == 9.2159999E13D;
   }

   private boolean checkBlockList(Player e, String fromMaterial, String toMaterial, List<String> materials, String configKey) {
      Iterator var7 = materials.iterator();

      String material;
      do {
         do {
            if (!var7.hasNext()) {
               return false;
            }

            material = (String)var7.next();
         } while(!toMaterial.contains(material) && !fromMaterial.contains(material));
      } while(!this.file.getChecks().getBoolean("phase.fixer.block." + configKey.toLowerCase()));

      return true;
   }
}
