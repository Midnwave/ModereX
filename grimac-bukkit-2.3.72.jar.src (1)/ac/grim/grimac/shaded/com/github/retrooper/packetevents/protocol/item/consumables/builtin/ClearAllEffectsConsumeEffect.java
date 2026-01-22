/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffectTypes;
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
/*    */ public class ClearAllEffectsConsumeEffect
/*    */   extends ConsumeEffect<ClearAllEffectsConsumeEffect>
/*    */ {
/* 28 */   public static final ClearAllEffectsConsumeEffect INSTANCE = new ClearAllEffectsConsumeEffect();
/*    */   
/*    */   private ClearAllEffectsConsumeEffect() {
/* 31 */     super(ConsumeEffectTypes.CLEAR_ALL_EFFECTS);
/*    */   }
/*    */   
/*    */   public static ClearAllEffectsConsumeEffect read(PacketWrapper<?> ignoredWrapper) {
/* 35 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> ignoredWrapper, ClearAllEffectsConsumeEffect ignoredEffect) {}
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\builtin\ClearAllEffectsConsumeEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */