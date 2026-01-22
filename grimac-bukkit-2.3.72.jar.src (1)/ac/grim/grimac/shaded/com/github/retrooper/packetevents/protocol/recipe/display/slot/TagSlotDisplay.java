/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
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
/*    */ public class TagSlotDisplay
/*    */   extends SlotDisplay<TagSlotDisplay>
/*    */ {
/*    */   private ResourceLocation itemTag;
/*    */   
/*    */   public TagSlotDisplay(ResourceLocation itemTag) {
/* 31 */     super(SlotDisplayTypes.TAG);
/* 32 */     this.itemTag = itemTag;
/*    */   }
/*    */   
/*    */   public static TagSlotDisplay read(PacketWrapper<?> wrapper) {
/* 36 */     ResourceLocation itemTag = wrapper.readIdentifier();
/* 37 */     return new TagSlotDisplay(itemTag);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, TagSlotDisplay display) {
/* 41 */     wrapper.writeIdentifier(display.itemTag);
/*    */   }
/*    */   
/*    */   public ResourceLocation getItemTag() {
/* 45 */     return this.itemTag;
/*    */   }
/*    */   
/*    */   public void setItemTag(ResourceLocation itemTag) {
/* 49 */     this.itemTag = itemTag;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 54 */     if (this == obj) return true; 
/* 55 */     if (!(obj instanceof TagSlotDisplay)) return false; 
/* 56 */     TagSlotDisplay that = (TagSlotDisplay)obj;
/* 57 */     return this.itemTag.equals(that.itemTag);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(this.itemTag);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "TagSlotDisplay{itemTag=" + this.itemTag + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\TagSlotDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */