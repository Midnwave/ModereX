package ac.grim.grimac.platform.api.player;

import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;

public interface PlatformInventory {
  ItemStack getItemInHand();
  
  ItemStack getItemInOffHand();
  
  ItemStack getStack(int paramInt1, int paramInt2);
  
  ItemStack getHelmet();
  
  ItemStack getChestplate();
  
  ItemStack getLeggings();
  
  ItemStack getBoots();
  
  ItemStack[] getContents();
  
  String getOpenInventoryKey();
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\player\PlatformInventory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */