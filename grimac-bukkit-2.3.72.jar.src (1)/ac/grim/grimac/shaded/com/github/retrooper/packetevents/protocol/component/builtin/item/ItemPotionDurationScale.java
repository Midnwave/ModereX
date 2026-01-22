/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
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
/*    */ public class ItemPotionDurationScale
/*    */ {
/*    */   private float scale;
/*    */   
/*    */   public ItemPotionDurationScale(float scale) {
/* 30 */     this.scale = scale;
/*    */   }
/*    */   
/*    */   public static ItemPotionDurationScale read(PacketWrapper<?> wrapper) {
/* 34 */     float scale = wrapper.readFloat();
/* 35 */     return new ItemPotionDurationScale(scale);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemPotionDurationScale scale) {
/* 39 */     wrapper.writeFloat(scale.scale);
/*    */   }
/*    */   
/*    */   public float getScale() {
/* 43 */     return this.scale;
/*    */   }
/*    */   
/*    */   public void setScale(float scale) {
/* 47 */     this.scale = scale;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 52 */     if (!(obj instanceof ItemPotionDurationScale)) return false; 
/* 53 */     ItemPotionDurationScale that = (ItemPotionDurationScale)obj;
/* 54 */     return (Float.compare(that.scale, this.scale) == 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 59 */     return Objects.hashCode(Float.valueOf(this.scale));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemPotionDurationScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */