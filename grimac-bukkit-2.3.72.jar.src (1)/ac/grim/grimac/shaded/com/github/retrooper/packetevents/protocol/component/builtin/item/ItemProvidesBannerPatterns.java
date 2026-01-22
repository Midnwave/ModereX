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
/*    */ public class ItemProvidesBannerPatterns
/*    */ {
/*    */   private ResourceLocation tagKey;
/*    */   
/*    */   public ItemProvidesBannerPatterns(ResourceLocation tagKey) {
/* 31 */     this.tagKey = tagKey;
/*    */   }
/*    */   
/*    */   public static ItemProvidesBannerPatterns read(PacketWrapper<?> wrapper) {
/* 35 */     ResourceLocation tagKey = wrapper.readIdentifier();
/* 36 */     return new ItemProvidesBannerPatterns(tagKey);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ItemProvidesBannerPatterns patterns) {
/* 40 */     wrapper.writeIdentifier(patterns.tagKey);
/*    */   }
/*    */   
/*    */   public ResourceLocation getTagKey() {
/* 44 */     return this.tagKey;
/*    */   }
/*    */   
/*    */   public void setTagKey(ResourceLocation tagKey) {
/* 48 */     this.tagKey = tagKey;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 53 */     if (!(obj instanceof ItemProvidesBannerPatterns)) return false; 
/* 54 */     ItemProvidesBannerPatterns that = (ItemProvidesBannerPatterns)obj;
/* 55 */     return this.tagKey.equals(that.tagKey);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 60 */     return Objects.hashCode(this.tagKey);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\builtin\item\ItemProvidesBannerPatterns.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */