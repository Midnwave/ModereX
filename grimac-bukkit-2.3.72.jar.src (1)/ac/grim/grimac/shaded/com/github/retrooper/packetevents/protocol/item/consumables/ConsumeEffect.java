/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
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
/*    */ public abstract class ConsumeEffect<T extends ConsumeEffect<?>>
/*    */ {
/*    */   protected final ConsumeEffectType<T> type;
/*    */   
/*    */   protected ConsumeEffect(ConsumeEffectType<T> type) {
/* 28 */     this.type = type;
/*    */   }
/*    */   
/*    */   public static ConsumeEffect<?> readFull(PacketWrapper<?> wrapper) {
/* 32 */     ConsumeEffectType<?> type = (ConsumeEffectType)wrapper.readMappedEntity((IRegistry)ConsumeEffectTypes.getRegistry());
/* 33 */     return (ConsumeEffect<?>)type.read(wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   public static <T extends ConsumeEffect<?>> void writeFull(PacketWrapper<?> wrapper, ConsumeEffect<T> effect) {
/* 38 */     wrapper.writeMappedEntity(effect.getType());
/* 39 */     effect.getType().write(wrapper, effect);
/*    */   }
/*    */   
/*    */   public ConsumeEffectType<T> getType() {
/* 43 */     return this.type;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\ConsumeEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */