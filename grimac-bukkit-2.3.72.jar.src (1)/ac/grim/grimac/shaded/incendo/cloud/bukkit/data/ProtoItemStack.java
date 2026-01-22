package ac.grim.grimac.shaded.incendo.cloud.bukkit.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ProtoItemStack {
  Material material();
  
  boolean hasExtraData();
  
  ItemStack createItemStack(int paramInt, boolean paramBoolean) throws IllegalArgumentException;
}


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\data\ProtoItemStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */