/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound.Sounds;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
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
/*    */ public final class Instruments
/*    */ {
/* 30 */   private static final VersionedRegistry<Instrument> REGISTRY = new VersionedRegistry("instrument");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static Instrument define(String key, Sound sound) {
/* 38 */     return define(key, sound, 140, 256.0F);
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static Instrument define(String key, Sound sound, int useDuration, float range) {
/* 43 */     return (Instrument)REGISTRY.define(key, data -> new StaticInstrument(data, sound, useDuration, range, (Component)Component.translatable("instrument.minecraft." + key)));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<Instrument> getRegistry() {
/* 49 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   public static Instrument getByName(String name) {
/* 53 */     return (Instrument)REGISTRY.getByName(name);
/*    */   }
/*    */   
/*    */   public static Instrument getById(ClientVersion version, int id) {
/* 57 */     return (Instrument)REGISTRY.getById(version, id);
/*    */   }
/*    */   
/* 60 */   public static final Instrument PONDER_GOAT_HORN = define("ponder_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_0);
/* 61 */   public static final Instrument SING_GOAT_HORN = define("sing_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_1);
/* 62 */   public static final Instrument SEEK_GOAT_HORN = define("seek_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_2);
/* 63 */   public static final Instrument FEEL_GOAT_HORN = define("feel_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_3);
/* 64 */   public static final Instrument ADMIRE_GOAT_HORN = define("admire_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_4);
/* 65 */   public static final Instrument CALL_GOAT_HORN = define("call_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_5);
/* 66 */   public static final Instrument YEARN_GOAT_HORN = define("yearn_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_6);
/* 67 */   public static final Instrument DREAM_GOAT_HORN = define("dream_goat_horn", Sounds.ITEM_GOAT_HORN_SOUND_7);
/*    */   
/*    */   static {
/* 70 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\instrument\Instruments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */