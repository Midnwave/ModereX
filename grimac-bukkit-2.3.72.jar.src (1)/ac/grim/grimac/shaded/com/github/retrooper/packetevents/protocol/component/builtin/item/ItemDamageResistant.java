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
/*    */ public class ItemDamageResistant
/*    */ {
/*    */   private ResourceLocation typesTagKey;
/*    */   
/*    */   public ItemDamageResistant(ResourceLocation typesTagKey) {
/* 31 */     this.typesTagKey = typesTagKey;
/*    */   }
/*    */   
/*    */   public static ItemDamageResistant read(PacketWrapper<?> wrapper) {
/* 35 */     return new ItemDamageResistant(wrapper.readIdentifier());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemDamageResistant resistant) {
/* 39 */     wrapper.writeIdentifier(resistant.typesTagKey);
/*    */   }
/*    */   
/*    */   public ResourceLocation getTypesTagKey() {
/* 43 */     return this.typesTagKey;
/*    */   }
/*    */   
/*    */   public void setTypesTagKey(ResourceLocation typesTagKey) {
/* 47 */     this.typesTagKey = typesTagKey;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 52 */     if (this == obj) return true; 
/* 53 */     if (!(obj instanceof ItemDamageResistant)) return false; 
/* 54 */     ItemDamageResistant that = (ItemDamageResistant)obj;
/* 55 */     return this.typesTagKey.equals(that.typesTagKey);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(this.typesTagKey);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "ItemDamageResistant{typesTagKey=" + this.typesTagKey + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemDamageResistant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */