/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffectTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.List;
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
/*    */ public class ApplyEffectsConsumeEffect
/*    */   extends ConsumeEffect<ApplyEffectsConsumeEffect>
/*    */ {
/*    */   private final List<PotionEffect> effects;
/*    */   private final float probability;
/*    */   
/*    */   public ApplyEffectsConsumeEffect(List<PotionEffect> effects, float probability) {
/* 34 */     super(ConsumeEffectTypes.APPLY_EFFECTS);
/* 35 */     this.effects = effects;
/* 36 */     this.probability = probability;
/*    */   }
/*    */   
/*    */   public static ApplyEffectsConsumeEffect read(PacketWrapper<?> wrapper) {
/* 40 */     List<PotionEffect> effects = wrapper.readList(PotionEffect::read);
/* 41 */     float probability = wrapper.readFloat();
/* 42 */     return new ApplyEffectsConsumeEffect(effects, probability);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ApplyEffectsConsumeEffect effect) {
/* 46 */     wrapper.writeList(effect.effects, PotionEffect::write);
/* 47 */     wrapper.writeFloat(effect.probability);
/*    */   }
/*    */   
/*    */   public List<PotionEffect> getEffects() {
/* 51 */     return this.effects;
/*    */   }
/*    */   
/*    */   public float getProbability() {
/* 55 */     return this.probability;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\builtin\ApplyEffectsConsumeEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */