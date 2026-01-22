/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
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
/*    */ 
/*    */ public class ItemUseRemainder
/*    */ {
/*    */   private ItemStack target;
/*    */   
/*    */   public ItemUseRemainder(ItemStack target) {
/* 31 */     this.target = target;
/*    */   }
/*    */   
/*    */   public static ItemUseRemainder read(PacketWrapper<?> wrapper) {
/* 35 */     ItemStack target = wrapper.readItemStack();
/* 36 */     return new ItemUseRemainder(target);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemUseRemainder remainder) {
/* 40 */     wrapper.writeItemStack(remainder.target);
/*    */   }
/*    */   
/*    */   public ItemStack getTarget() {
/* 44 */     return this.target;
/*    */   }
/*    */   
/*    */   public void setTarget(ItemStack target) {
/* 48 */     this.target = target;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 53 */     if (this == obj) return true; 
/* 54 */     if (!(obj instanceof ItemUseRemainder)) return false; 
/* 55 */     ItemUseRemainder that = (ItemUseRemainder)obj;
/* 56 */     return this.target.equals(that.target);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 61 */     return Objects.hashCode(this.target);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return "ItemUseRemainder{target=" + this.target + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemUseRemainder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */