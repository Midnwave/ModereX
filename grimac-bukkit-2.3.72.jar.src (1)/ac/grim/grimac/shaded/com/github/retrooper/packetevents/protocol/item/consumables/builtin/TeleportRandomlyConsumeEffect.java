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
/*    */ public class TeleportRandomlyConsumeEffect
/*    */   extends ConsumeEffect<TeleportRandomlyConsumeEffect>
/*    */ {
/*    */   private final float diameter;
/*    */   
/*    */   public TeleportRandomlyConsumeEffect(float diameter) {
/* 30 */     super(ConsumeEffectTypes.TELEPORT_RANDOMLY);
/* 31 */     this.diameter = diameter;
/*    */   }
/*    */   
/*    */   public static TeleportRandomlyConsumeEffect read(PacketWrapper<?> wrapper) {
/* 35 */     float diameter = wrapper.readFloat();
/* 36 */     return new TeleportRandomlyConsumeEffect(diameter);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, TeleportRandomlyConsumeEffect effect) {
/* 40 */     wrapper.writeFloat(effect.diameter);
/*    */   }
/*    */   
/*    */   public float getDiameter() {
/* 44 */     return this.diameter;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\builtin\TeleportRandomlyConsumeEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */