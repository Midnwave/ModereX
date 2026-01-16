package zoruafan.foxanticheat.checks.badpackets;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import zoruafan.foxanticheat.FoxAddition;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.api.events.FoxFlagEvent;
import zoruafan.foxanticheat.manager.ChecksManager;
import zoruafan.foxanticheat.manager.FilesManager;
import zoruafan.foxanticheat.manager.hooks.GeyserManager;
import zoruafan.foxanticheat.manager.hooks.mcMMOManager;

public class FastUse extends ChecksManager implements Listener {
   private final FilesManager file;
   private final Map<Player, Long> lastUseTime = new HashMap();
   private final Map<Player, Integer> useCount = new HashMap();
   private final Map<Player, Integer> temporaryVls = new HashMap();
   private boolean usFg;
   private GeyserManager fG;
   private boolean usMo;
   private mcMMOManager mO;
   private final String p = "badpackets.modules.fastuse";
   private final String p2 = ".effiency.items";

   public FastUse(FilesManager files, boolean uFg, boolean uMo) {
      super(files);
      this.file = files;
      this.usFg = uFg;
      if (this.usFg) {
         try {
            this.fG = new GeyserManager(files);
         } catch (Exception var6) {
            this.fG = null;
            this.usFg = false;
         }
      } else {
         this.fG = null;
      }

      this.usFg = false;
      this.usMo = uMo;
      if (this.usMo) {
         try {
            this.mO = new mcMMOManager(files);
         } catch (Exception var5) {
            this.mO = null;
            this.usMo = false;
         }
      } else {
         this.mO = null;
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player e = event.getPlayer();
      FoxAdditionAPI api = FoxAddition.getAPI();
      if (e instanceof Player) {
         if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (!this.iAD(e, "badpackets")) {
               if (this.usFg && this.fG != null) {
                  if (!this.fG.isAllowDetect_Mode(e, "badpackets.modules.fastuse.detect")) {
                     return;
                  }
               } else if (!this.file.getChecks().getBoolean("badpackets.modules.fastuse.detect.java", true)) {
                  return;
               }

               if (!this.usMo || this.mO == null || !this.mO.isTreeFeller(e) && !this.mO.isSuperBreaker(e)) {
                  ItemStack item = e.getInventory().getItemInHand();
                  if (item != null && this.isTool(item.getType())) {
                     int efficiencyLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);
                     int maxEfficiency = this.getMaxEfficiency(item.getType());
                     if (efficiencyLevel > maxEfficiency) {
                        return;
                     }
                  }

                  long currentTime = System.currentTimeMillis();
                  int maxUseCount = this.file.getChecks().getInt("badpackets.modules.fastuse.max", 30);
                  int divisor = this.file.getChecks().getInt("badpackets.modules.fastuse.divisor", 50);
                  long timeInterval = this.file.getChecks().getLong("badpackets.modules.fastuse.interval", 145L);
                  if (!this.lastUseTime.containsKey(e)) {
                     this.lastUseTime.put(e, currentTime);
                     this.useCount.put(e, 1);
                  } else {
                     long useTime = (Long)this.lastUseTime.get(e);
                     int count = (Integer)this.useCount.getOrDefault(e, 0);
                     int var10000;
                     if (currentTime - useTime <= timeInterval) {
                        ++count;
                        var10000 = count;
                     } else {
                        var10000 = 1;
                     }

                     count = var10000;
                     this.lastUseTime.put(e, currentTime);
                     this.useCount.put(e, count);
                     if (count >= maxUseCount) {
                        if (!e.isOnline()) {
                           return;
                        }

                        int vls = api.getVLS(e, "badpackets");
                        if (this.file.getChecks().getBoolean("badpackets.modules.fastuse.cancel.enable", true) && vls >= this.file.getChecks().getInt("badpackets.modules.fastuse.cancel.vl", 2)) {
                           event.setCancelled(true);
                        }

                        this.temporaryVls.put(e, (Integer)this.temporaryVls.getOrDefault(e, 0) + 1);
                        int playerTVls = (Integer)this.temporaryVls.getOrDefault(e, 0);
                        long useValue = currentTime - useTime;
                        if (playerTVls % divisor == 0) {
                           FoxFlagEvent flagEvent = new FoxFlagEvent(e, "badpackets", this.file.getChecks().getInt("badpackets.modules.fastuse.vls", 1), "Toggles: `" + count + "`/`" + maxUseCount + "`, interval: `" + useValue + "`/`" + timeInterval + "`.", "FastUse (Limit) [BadPackets]", "[toggles:" + count + "/" + maxUseCount + "] [interval:" + useValue + "/" + timeInterval + "]");
                           Bukkit.getPluginManager().callEvent(flagEvent);
                           this.temporaryVls.remove(e);
                        }
                     }

                  }
               }
            }
         }
      }
   }

   private boolean isTool(Material material) {
      String itemName = material.name().toLowerCase();
      if (itemName.contains("_pickaxe") && this.file.getChecks().getBoolean("badpackets.modules.fastuse.effiency.items.pickaxe.enable", false)) {
         return true;
      } else if (itemName.contains("_axe") && this.file.getChecks().getBoolean("badpackets.modules.fastuse.effiency.items.axe.enable", false)) {
         return true;
      } else {
         return itemName.contains("_shovel") && this.file.getChecks().getBoolean("badpackets.modules.fastuse.effiency.items.shovel.enable", false);
      }
   }

   private int getMaxEfficiency(Material material) {
      String itemName = material.name().toLowerCase();
      int maxEfficiency = 15;
      if (itemName.contains("_pickaxe")) {
         maxEfficiency = this.file.getChecks().getInt("badpackets.modules.fastuse.effiency.items.pickaxe.max", 15);
      } else if (itemName.contains("_axe")) {
         maxEfficiency = this.file.getChecks().getInt("badpackets.modules.fastuse.effiency.items.axe.max", 15);
      } else if (itemName.contains("_shovel")) {
         maxEfficiency = this.file.getChecks().getInt("badpackets.modules.fastuse.effiency.items.shovel.max", 15);
      }

      return maxEfficiency;
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.lastUseTime.remove(player);
      this.useCount.remove(player);
      this.temporaryVls.remove(player);
   }
}
