/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
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
/*    */ public class ItemTooltipStyle
/*    */ {
/*    */   private ResourceLocation tooltipLoc;
/*    */   
/*    */   public ItemTooltipStyle(ResourceLocation tooltipLoc) {
/* 31 */     this.tooltipLoc = tooltipLoc;
/*    */   }
/*    */   
/*    */   public static ItemTooltipStyle read(PacketWrapper<?> wrapper) {
/* 35 */     return new ItemTooltipStyle(wrapper.readIdentifier());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemTooltipStyle style) {
/* 39 */     wrapper.writeIdentifier(style.tooltipLoc);
/*    */   }
/*    */   
/*    */   public ResourceLocation getTooltipLoc() {
/* 43 */     return this.tooltipLoc;
/*    */   }
/*    */   
/*    */   public void setTooltipLoc(ResourceLocation tooltipLoc) {
/* 47 */     this.tooltipLoc = tooltipLoc;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 52 */     if (this == obj) return true; 
/* 53 */     if (!(obj instanceof ItemTooltipStyle)) return false; 
/* 54 */     ItemTooltipStyle that = (ItemTooltipStyle)obj;
/* 55 */     return this.tooltipLoc.equals(that.tooltipLoc);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(this.tooltipLoc);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "ItemTooltipStyle{tooltipLoc=" + this.tooltipLoc + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemTooltipStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */