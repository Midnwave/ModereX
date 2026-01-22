/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.Objects;
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
/*    */ public class ItemStackSlotDisplay
/*    */   extends SlotDisplay<ItemStackSlotDisplay>
/*    */ {
/*    */   private ItemStack stack;
/*    */   
/*    */   public ItemStackSlotDisplay(ItemStack stack) {
/* 31 */     super(SlotDisplayTypes.ITEM_STACK);
/* 32 */     this.stack = stack;
/*    */   }
/*    */   
/*    */   public static ItemStackSlotDisplay read(PacketWrapper<?> wrapper) {
/* 36 */     ItemStack stack = wrapper.readItemStack();
/* 37 */     return new ItemStackSlotDisplay(stack);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemStackSlotDisplay display) {
/* 41 */     wrapper.writeItemStack(display.stack);
/*    */   }
/*    */   
/*    */   public ItemStack getStack() {
/* 45 */     return this.stack;
/*    */   }
/*    */   
/*    */   public void setStack(ItemStack stack) {
/* 49 */     this.stack = stack;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (this == obj) return true; 
/* 55 */     if (!(obj instanceof ItemStackSlotDisplay)) return false; 
/* 56 */     ItemStackSlotDisplay that = (ItemStackSlotDisplay)obj;
/* 57 */     return this.stack.equals(that.stack);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(this.stack);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "ItemStackSlotDisplay{stack=" + this.stack + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\ItemStackSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */