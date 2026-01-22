/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.wolfvariant;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sounds;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
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
/*    */ public final class WolfSoundVariants
/*    */ {
/* 28 */   private static final VersionedRegistry<WolfSoundVariant> REGISTRY = new VersionedRegistry("wolf_sound_variant");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<WolfSoundVariant> getRegistry() {
/* 35 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static WolfSoundVariant define(String name, String suffix) {
/* 40 */     return define(name, Sounds.getByName("entity.wolf" + suffix + ".ambient"), 
/* 41 */         Sounds.getByName("entity.wolf" + suffix + ".death"), 
/* 42 */         Sounds.getByName("entity.wolf" + suffix + ".growl"), 
/* 43 */         Sounds.getByName("entity.wolf" + suffix + ".hurt"), 
/* 44 */         Sounds.getByName("entity.wolf" + suffix + ".pant"), 
/* 45 */         Sounds.getByName("entity.wolf" + suffix + ".whine"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static WolfSoundVariant define(String name, Sound ambientSound, Sound deathSound, Sound growlSound, Sound hurtSound, Sound pantSound, Sound whineSound) {
/* 53 */     return (WolfSoundVariant)REGISTRY.define(name, data -> new StaticWolfSoundVariant(data, ambientSound, deathSound, growlSound, hurtSound, pantSound, whineSound));
/*    */   }
/*    */ 
/*    */   
/* 57 */   public static final WolfSoundVariant CLASSIC = define("classic", "");
/* 58 */   public static final WolfSoundVariant PUGLIN = define("puglin", "_puglin");
/* 59 */   public static final WolfSoundVariant SAD = define("sad", "_sad");
/* 60 */   public static final WolfSoundVariant ANGRY = define("angry", "_angry");
/* 61 */   public static final WolfSoundVariant GRUMPY = define("grumpy", "_grumpy");
/* 62 */   public static final WolfSoundVariant BIG = define("big", "_big");
/* 63 */   public static final WolfSoundVariant CUTE = define("cute", "_cute");
/*    */   
/*    */   static {
/* 66 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\wolfvariant\WolfSoundVariants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */