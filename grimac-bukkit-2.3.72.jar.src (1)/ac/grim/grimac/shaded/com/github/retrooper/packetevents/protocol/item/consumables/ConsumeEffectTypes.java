/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin.ApplyEffectsConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin.ClearAllEffectsConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin.PlaySoundConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin.RemoveEffectsConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin.TeleportRandomlyConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
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
/*    */ public final class ConsumeEffectTypes
/*    */ {
/* 33 */   private static final VersionedRegistry<ConsumeEffectType<?>> REGISTRY = new VersionedRegistry("consume_effect_type");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends ConsumeEffect<?>> ConsumeEffectType<T> define(String name, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 41 */     return (ConsumeEffectType<T>)REGISTRY.define(name, data -> new StaticConsumeEffectType<>(data, reader, writer));
/*    */   }
/*    */ 
/*    */   
/* 45 */   public static final ConsumeEffectType<ApplyEffectsConsumeEffect> APPLY_EFFECTS = define("apply_effects", ApplyEffectsConsumeEffect::read, ApplyEffectsConsumeEffect::write);
/*    */   
/* 47 */   public static final ConsumeEffectType<RemoveEffectsConsumeEffect> REMOVE_EFFECTS = define("remove_effects", RemoveEffectsConsumeEffect::read, RemoveEffectsConsumeEffect::write);
/*    */   
/* 49 */   public static final ConsumeEffectType<ClearAllEffectsConsumeEffect> CLEAR_ALL_EFFECTS = define("clear_all_effects", ClearAllEffectsConsumeEffect::read, ClearAllEffectsConsumeEffect::write);
/*    */   
/* 51 */   public static final ConsumeEffectType<TeleportRandomlyConsumeEffect> TELEPORT_RANDOMLY = define("teleport_randomly", TeleportRandomlyConsumeEffect::read, TeleportRandomlyConsumeEffect::write);
/*    */   
/* 53 */   public static final ConsumeEffectType<PlaySoundConsumeEffect> PLAY_SOUND = define("play_sound", PlaySoundConsumeEffect::read, PlaySoundConsumeEffect::write);
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<ConsumeEffectType<?>> getRegistry() {
/* 57 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   static {
/* 61 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\ConsumeEffectTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */