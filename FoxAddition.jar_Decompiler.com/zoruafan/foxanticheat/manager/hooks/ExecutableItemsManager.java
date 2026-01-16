package zoruafan.foxanticheat.manager.hooks;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExecutableItemsManager {
   public boolean isExecutableItem(Player player) {
      try {
         ItemStack currentItem = player.getInventory().getItemInHand();
         Optional<ExecutableItemInterface> optionalItem = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(currentItem);
         return optionalItem.isPresent();
      } catch (Exception var4) {
         return false;
      }
   }
}
