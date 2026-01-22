/*    */ package ac.grim.grimac.platform.bukkit.player;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.player.PlatformInventory;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotConversionUtil;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ 
/*    */ public class BukkitPlatformInventory implements PlatformInventory {
/*    */   private final Player bukkitPlayer;
/*    */   
/*    */   public BukkitPlatformInventory(Player bukkitPlayer) {
/* 13 */     this.bukkitPlayer = bukkitPlayer;
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getItemInHand() {
/* 18 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getItemInHand());
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getItemInOffHand() {
/* 23 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getItemInOffHand());
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getStack(int bukkitSlot, int vanillaSlot) {
/* 28 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getItem(bukkitSlot));
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getHelmet() {
/* 33 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getHelmet());
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getChestplate() {
/* 38 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getChestplate());
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getLeggings() {
/* 43 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getLeggings());
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack getBoots() {
/* 48 */     return SpigotConversionUtil.fromBukkitItemStack(this.bukkitPlayer.getInventory().getBoots());
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack[] getContents() {
/* 53 */     ItemStack[] bukkitItems = this.bukkitPlayer.getInventory().getContents();
/* 54 */     ItemStack[] items = new ItemStack[bukkitItems.length];
/* 55 */     for (int i = 0; i < bukkitItems.length; i++) {
/* 56 */       if (bukkitItems[i] != null)
/* 57 */         items[i] = SpigotConversionUtil.fromBukkitItemStack(bukkitItems[i]); 
/*    */     } 
/* 59 */     return items;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getOpenInventoryKey() {
/* 64 */     return this.bukkitPlayer.getOpenInventory().getType().toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\player\BukkitPlatformInventory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */