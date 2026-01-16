package zoruafan.foxanticheat.manager.hooks;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.choco.veinminer.VeinMinerPlugin;
import wtf.choco.veinminer.integration.WorldGuardIntegration;
import wtf.choco.veinminer.player.VeinMinerPlayer;
import wtf.choco.veinminer.tool.VeinMinerToolCategory;
import wtf.choco.veinminer.util.VMConstants;

public class VeinMinerManager {
   private final VeinMinerPlugin veinMinerPlugin;

   public VeinMinerManager(JavaPlugin main) throws ReflectiveOperationException {
      this.veinMinerPlugin = (VeinMinerPlugin)main.getServer().getPluginManager().getPlugin("VeinMiner");
   }

   public boolean isInVeinMiner(Player e, Block block) {
      try {
         ItemStack item = e.getInventory().getItemInHand();
         VeinMinerToolCategory category = this.veinMinerPlugin.getToolCategoryRegistry().get(item, (cat) -> {
            return e.hasPermission((String)VMConstants.PERMISSION_VEINMINE.apply(cat));
         });
         if (category == null) {
            return false;
         } else {
            VeinMinerPlayer veinMinerPlayer = this.veinMinerPlugin.getPlayerManager().get(e);
            if (veinMinerPlayer == null) {
               return false;
            } else if (veinMinerPlayer.isVeinMinerActive() && veinMinerPlayer.isVeinMinerEnabled(category) && !this.veinMinerPlugin.getConfiguration().isDisabledGameMode(e.getGameMode()) && !category.getConfiguration().isDisabledWorld(block.getWorld().getName()) && e.hasPermission((String)VMConstants.PERMISSION_VEINMINE.apply(category))) {
               boolean worldGuard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
               return worldGuard && !WorldGuardIntegration.queryFlagVeinMiner(block, e) ? false : veinMinerPlayer.isVeinMinerActive();
            } else {
               return false;
            }
         }
      } catch (Exception var8) {
         return false;
      }
   }
}
