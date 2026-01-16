package zoruafan.foxanticheat.manager.hooks;

import me.deecaad.weaponmechanics.WeaponMechanicsAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeaponMechanicsManager {
   public boolean isWeapon(Player player) {
      ItemStack weaponStack = player.getInventory().getItemInHand();
      String weaponTitle = WeaponMechanicsAPI.getWeaponTitle(weaponStack);
      return weaponTitle != null;
   }
}
