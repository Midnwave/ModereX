package zoruafan.foxanticheat.manager.hooks;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import zoruafan.foxanticheat.FoxAddition;
import zoruafan.foxanticheat.api.FoxAdditionAPI;
import zoruafan.foxanticheat.manager.FilesManager;

public class mcMMOManager {
   private final FilesManager file;
   FoxAdditionAPI api = FoxAddition.getAPI();

   public mcMMOManager(FilesManager files) {
      this.file = files;
   }

   public boolean isTreeFeller(Player e) {
      if (!this.file.getConfig().getBoolean("hooks.mcmmo.faster", false)) {
         if (WorldBlacklist.isWorldBlacklisted(e.getWorld())) {
            return false;
         }

         if (!WorldGuardUtils.isWorldGuardLoaded() && !WorldGuardManager.getInstance().hasMainFlag(e)) {
            return false;
         }

         if (e.getGameMode() == GameMode.CREATIVE) {
            return false;
         }

         ItemStack i = e.getInventory().getItemInHand();
         if (!i.getType().name().toLowerCase().contains("axe")) {
            return false;
         }
      }

      if (UserManager.getPlayer(e) == null) {
         return false;
      } else {
         McMMOPlayer mP = UserManager.getPlayer(e);
         if (mP == null) {
            return false;
         } else {
            Boolean en = mP.getAbilityMode(SuperAbilityType.TREE_FELLER);
            return en;
         }
      }
   }

   public boolean isSuperBreaker(Player e) {
      if (!this.file.getConfig().getBoolean("hooks.mcmmo.faster", false)) {
         if (WorldBlacklist.isWorldBlacklisted(e.getWorld())) {
            return false;
         }

         if (!WorldGuardUtils.isWorldGuardLoaded() && !WorldGuardManager.getInstance().hasMainFlag(e)) {
            return false;
         }

         if (e.getGameMode() == GameMode.CREATIVE) {
            return false;
         }
      }

      if (UserManager.getPlayer(e) == null) {
         return false;
      } else {
         McMMOPlayer mP = UserManager.getPlayer(e);
         if (mP == null) {
            return false;
         } else {
            Boolean en = mP.getAbilityMode(SuperAbilityType.SUPER_BREAKER);
            return en;
         }
      }
   }
}
