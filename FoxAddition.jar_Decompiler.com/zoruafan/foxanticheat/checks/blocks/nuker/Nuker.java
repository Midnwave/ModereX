package zoruafan.foxanticheat.checks.blocks.nuker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;
import zoruafan.foxanticheat.manager.hooks.VeinMinerManager;
import zoruafan.foxanticheat.manager.hooks.mcMMOManager;

public class Nuker extends ChecksManager implements Listener {
   private final JavaPlugin plugin;
   private final FilesManager file;
   private final Map<Player, Long> lastBreakTime = new HashMap();
   private final Map<Player, Integer> breakCount = new HashMap();
   private final Map<Player, Integer> temporaryVls = new HashMap();
   private final Map<Player, Set<Block>> brokenBlocks = new HashMap();
   private boolean usFg;
   private GeyserManager fG;
   private boolean uVM;
   private VeinMinerManager vM;
   private boolean usMo;
   private mcMMOManager mO;
   private final String p = "blocks.modules.nuker.limit";

   public Nuker(JavaPlugin plugin, FilesManager files, boolean uVm, boolean uFg, boolean uMo) {
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
   public void onBlockBreak(BlockBreakEvent event) {
      Player e = event.getPlayer();
      if (!this.iAD(e, "blocks")) {
         if (this.usFg && this.fG != null) {
            if (!this.fG.isAllowDetect_Mode(e, "blocks.modules.nuker.limit.detect")) {
               return;
            }
         } else if (!this.file.getChecks().getBoolean("blocks.modules.nuker.limit.detect.java", true)) {
            return;
         }

         if (event.getPlayer().getType() == EntityType.PLAYER) {
            if (!this.uVM || this.vM == null || !this.vM.isInVeinMiner(e, event.getBlock())) {
               if (!this.usMo || this.mO == null || !this.mO.isSuperBreaker(e)) {
                  Block b = event.getBlock();
                  if (!this.file.getChecks().getBoolean("blocks.modules.nuker.limit.blocklist.enable", false) || this.isAllowed(e, b.getType())) {
                     int max = this.file.getChecks().getInt("blocks.modules.nuker.limit.max", 6);
                     int divisor = this.file.getChecks().getInt("blocks.modules.nuker.limit.divisor", 50);
                     int delay = this.file.getChecks().getInt("blocks.modules.nuker.limit.delay", 100);
                     long currentTime = System.currentTimeMillis();
                     if (!this.lastBreakTime.containsKey(e)) {
                        this.lastBreakTime.put(e, currentTime);
                        this.breakCount.put(e, 1);
                     } else {
                        long lastBreak = (Long)this.lastBreakTime.get(e);
                        int count = (Integer)this.breakCount.getOrDefault(e, 0);
                        Set playerBrokenBlocks;
                        if (currentTime - lastBreak <= (long)delay) {
                           ++count;
                           if (count <= max + 1) {
                              playerBrokenBlocks = (Set)this.brokenBlocks.getOrDefault(e, new HashSet());
                              playerBrokenBlocks.add(event.getBlock());
                              this.brokenBlocks.put(e, playerBrokenBlocks);
                           } else {
                              this.restoreBlocks(e);
                           }
                        } else {
                           count = 1;
                           playerBrokenBlocks = (Set)this.brokenBlocks.get(e);
                           if (playerBrokenBlocks != null) {
                              Iterator var14 = playerBrokenBlocks.iterator();

                              while(var14.hasNext()) {
                                 Block block = (Block)var14.next();
                                 if (block.getType() != Material.AIR) {
                                    block.getState().update(true, false);
                                 }
                              }

                              this.brokenBlocks.remove(e);
                           }
                        }

                        this.lastBreakTime.put(e, currentTime);
                        this.breakCount.put(e, count);
                        if (count >= max) {
                           if (!e.isOnline()) {
                              return;
                           }

                           if (this.file.getChecks().getBoolean("blocks.modules.nuker.limit.cancel", true)) {
                              this.restoreBlocks(e);
                              event.setCancelled(true);
                           }

                           int sumTVls = true;
                           this.incrementTemporaryVLS(e, 1);
                           int playerTVls = (Integer)this.temporaryVls.getOrDefault(e, 0);
                           if (playerTVls % divisor == 0) {
                              long timeValue = currentTime - lastBreak;
                              FoxFlagEvent flagEvent = new FoxFlagEvent(e, "blocks", this.file.getChecks().getInt("blocks.modules.nuker.limit.vls", 1), "Breaks: `" + count + "`/`" + max + "`, interval: `" + timeValue + "`/`" + delay + "`.", "Nuker (Limit)", "[Breaks:" + count + "/" + max + "] [interval:" + timeValue + "/" + delay + "]");
                              Bukkit.getPluginManager().callEvent(flagEvent);
                              if (!flagEvent.isCancelled()) {
                                 this.restoreBlocks(e);
                              }

                              this.temporaryVls.remove(e);
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private boolean isAllowed(Player e, Material block) {
      List<String> list = this.file.getChecks().getStringList("blocks.modules.nuker.limit.blocklist.list");
      String type = this.file.getConfig().getString("blocks.modules.nuker.limit.blocklist.type", "whitelist");
      if (type.equalsIgnoreCase("whitelist")) {
         return list.contains(block.toString().toUpperCase());
      } else if (type.equalsIgnoreCase("blacklist")) {
         return !list.contains(block.toString().toUpperCase());
      } else {
         this.plugin.getLogger().severe("[NUKER] Invalid option in 'type'. Use 'whitelist'/'blacklist' for this option.");
         this.plugin.getLogger().severe("[NUKER] The nuker checker don't works correctly.");
         return false;
      }
   }

   private void restoreBlocks(Player player) {
      Set<Block> playerBrokenBlocks = (Set)this.brokenBlocks.getOrDefault(player, new HashSet());
      Iterator var4 = playerBrokenBlocks.iterator();

      while(var4.hasNext()) {
         Block block = (Block)var4.next();
         if (block.getType() != Material.AIR) {
            block.getState().update(true, false);
         }
      }

      this.brokenBlocks.remove(player);
   }

   private void incrementTemporaryVLS(Player player, int amount) {
      int currentVls = (Integer)this.temporaryVls.getOrDefault(player, 0);
      this.temporaryVls.put(player, currentVls + amount);
   }
}
