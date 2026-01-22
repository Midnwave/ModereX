/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffect;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.consumables.ConsumeEffectTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
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
/*    */ public class PlaySoundConsumeEffect
/*    */   extends ConsumeEffect<PlaySoundConsumeEffect>
/*    */ {
/*    */   private final Sound sound;
/*    */   
/*    */   public PlaySoundConsumeEffect(Sound sound) {
/* 31 */     super(ConsumeEffectTypes.PLAY_SOUND);
/* 32 */     this.sound = sound;
/*    */   }
/*    */   
/*    */   public static PlaySoundConsumeEffect read(PacketWrapper<?> wrapper) {
/* 36 */     Sound sound = Sound.read(wrapper);
/* 37 */     return new PlaySoundConsumeEffect(sound);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, PlaySoundConsumeEffect effect) {
/* 41 */     Sound.write(wrapper, effect.sound);
/*    */   }
/*    */   
/*    */   public Sound getSound() {
/* 45 */     return this.sound;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\consumables\builtin\PlaySoundConsumeEffect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */