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
/*    */ public class ItemModel
/*    */ {
/*    */   private ResourceLocation modelLocation;
/*    */   
/*    */   public ItemModel(ResourceLocation modelLocation) {
/* 31 */     this.modelLocation = modelLocation;
/*    */   }
/*    */   
/*    */   public static ItemModel read(PacketWrapper<?> wrapper) {
/* 35 */     return new ItemModel(wrapper.readIdentifier());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemModel model) {
/* 39 */     wrapper.writeIdentifier(model.modelLocation);
/*    */   }
/*    */   
/*    */   public ResourceLocation getModelLocation() {
/* 43 */     return this.modelLocation;
/*    */   }
/*    */   
/*    */   public void setModelLocation(ResourceLocation modelLocation) {
/* 47 */     this.modelLocation = modelLocation;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 52 */     if (this == obj) return true; 
/* 53 */     if (!(obj instanceof ItemModel)) return false; 
/* 54 */     ItemModel itemModel = (ItemModel)obj;
/* 55 */     return this.modelLocation.equals(itemModel.modelLocation);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(this.modelLocation);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "ItemModel{modelLocation=" + this.modelLocation + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */