/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffectTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
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
/*    */ public class RemoveEffectsConsumeEffect
/*    */   extends ConsumeEffect<RemoveEffectsConsumeEffect>
/*    */ {
/*    */   private final MappedEntitySet<PotionType> effects;
/*    */   
/*    */   public RemoveEffectsConsumeEffect(MappedEntitySet<PotionType> effects) {
/* 33 */     super(ConsumeEffectTypes.REMOVE_EFFECTS);
/* 34 */     this.effects = effects;
/*    */   }
/*    */   
/*    */   public static RemoveEffectsConsumeEffect read(PacketWrapper<?> wrapper) {
/* 38 */     MappedEntitySet<PotionType> effects = MappedEntitySet.read(wrapper, PotionTypes::getById);
/* 39 */     return new RemoveEffectsConsumeEffect(effects);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, RemoveEffectsConsumeEffect effect) {
/* 43 */     MappedEntitySet.write(wrapper, effect.effects);
/*    */   }
/*    */   
/*    */   public MappedEntitySet<PotionType> getEffects() {
/* 47 */     return this.effects;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\builtin\RemoveEffectsConsumeEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */