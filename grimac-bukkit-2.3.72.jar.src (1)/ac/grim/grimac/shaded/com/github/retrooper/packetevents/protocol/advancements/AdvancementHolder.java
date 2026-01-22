/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.advancements;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public final class AdvancementHolder
/*    */ {
/*    */   private ResourceLocation identifier;
/*    */   private Advancement advancement;
/*    */   
/*    */   public AdvancementHolder(ResourceLocation identifier, Advancement advancement) {
/* 30 */     this.identifier = identifier;
/* 31 */     this.advancement = advancement;
/*    */   }
/*    */   
/*    */   public static AdvancementHolder read(PacketWrapper<?> wrapper) {
/* 35 */     ResourceLocation identifier = wrapper.readIdentifier();
/* 36 */     Advancement advancement = Advancement.read(wrapper);
/* 37 */     return new AdvancementHolder(identifier, advancement);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, AdvancementHolder holder) {
/* 41 */     wrapper.writeIdentifier(holder.identifier);
/* 42 */     Advancement.write(wrapper, holder.advancement);
/*    */   }
/*    */   
/*    */   public ResourceLocation getIdentifier() {
/* 46 */     return this.identifier;
/*    */   }
/*    */   
/*    */   public void setIdentifier(ResourceLocation identifier) {
/* 50 */     this.identifier = identifier;
/*    */   }
/*    */   
/*    */   public Advancement getAdvancement() {
/* 54 */     return this.advancement;
/*    */   }
/*    */   
/*    */   public void setAdvancement(Advancement advancement) {
/* 58 */     this.advancement = advancement;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\advancements\AdvancementHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */