/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Obsolete
/*    */ public class Ingredient
/*    */ {
/*    */   private final ItemStack[] options;
/*    */   
/*    */   public Ingredient(ItemStack... options) {
/* 34 */     this.options = options;
/*    */   }
/*    */   
/*    */   public static Ingredient read(PacketWrapper<?> wrapper) {
/* 38 */     ItemStack[] options = (ItemStack[])wrapper.readArray(PacketWrapper::readItemStack, ItemStack.class);
/* 39 */     return new Ingredient(options);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, Ingredient ingredient) {
/* 43 */     wrapper.writeArray((Object[])ingredient.options, PacketWrapper::writeItemStack);
/*    */   }
/*    */   
/*    */   public ItemStack[] getOptions() {
/* 47 */     return this.options;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\Ingredient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */