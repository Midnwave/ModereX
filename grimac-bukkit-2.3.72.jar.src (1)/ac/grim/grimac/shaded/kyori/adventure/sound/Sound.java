/*     */ package ac.grim.grimac.shaded.kyori.adventure.sound;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Keyed;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Index;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.Objects;
/*     */ import java.util.OptionalLong;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @NonExtendable
/*     */ public interface Sound
/*     */   extends Examinable
/*     */ {
/*     */   @NotNull
/*     */   static Builder sound() {
/*  75 */     return new SoundImpl.BuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Builder sound(@NotNull Sound existing) {
/*  86 */     return new SoundImpl.BuilderImpl(existing);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Consumer<Builder> configurer) {
/*  97 */     return (Sound)AbstractBuilder.configureAndBuild(sound(), configurer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Key name, @NotNull Source source, float volume, float pitch) {
/* 111 */     return (Sound)sound().type(name).source(source).volume(volume).pitch(pitch).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Type type, @NotNull Source source, float volume, float pitch) {
/* 125 */     Objects.requireNonNull(type, "type");
/* 126 */     return sound(type.key(), source, volume, pitch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Supplier<? extends Type> type, @NotNull Source source, float volume, float pitch) {
/* 140 */     return (Sound)sound().type(type).source(source).volume(volume).pitch(pitch).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Key name, Source.Provider source, float volume, float pitch) {
/* 154 */     return sound(name, source.soundSource(), volume, pitch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Type type, Source.Provider source, float volume, float pitch) {
/* 168 */     return sound(type, source.soundSource(), volume, pitch);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Sound sound(@NotNull Supplier<? extends Type> type, Source.Provider source, float volume, float pitch) {
/* 182 */     return sound(type, source.soundSource(), volume, pitch);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Key name();
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Source source();
/*     */ 
/*     */   
/*     */   float volume();
/*     */ 
/*     */   
/*     */   float pitch();
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   OptionalLong seed();
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   SoundStop asStop();
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */     extends AbstractBuilder<Sound>
/*     */   {
/*     */     @NotNull
/*     */     Builder type(@NotNull Key param1Key);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder type(@NotNull Sound.Type param1Type);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder type(@NotNull Supplier<? extends Sound.Type> param1Supplier);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder source(@NotNull Sound.Source param1Source);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder source(Sound.Source.Provider param1Provider);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder volume(float param1Float);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder pitch(float param1Float);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder seed(long param1Long);
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     Builder seed(@NotNull OptionalLong param1OptionalLong);
/*     */   }
/*     */ 
/*     */   
/*     */   public enum Source
/*     */   {
/* 250 */     MASTER("master"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 259 */     MUSIC("music"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 268 */     RECORD("record"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 277 */     WEATHER("weather"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 286 */     BLOCK("block"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 295 */     HOSTILE("hostile"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 304 */     NEUTRAL("neutral"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 313 */     PLAYER("player"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 322 */     AMBIENT("ambient"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 331 */     VOICE("voice"),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 341 */     UI("ui");
/*     */     
/*     */     public static final Index<String, Source> NAMES;
/*     */     
/*     */     private final String name;
/*     */     
/*     */     static {
/* 348 */       NAMES = Index.create(Source.class, source -> source.name);
/*     */     }
/*     */     
/*     */     Source(String name) {
/* 352 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static interface Provider
/*     */     {
/*     */       @NotNull
/*     */       Sound.Source soundSource();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Type
/*     */     extends Keyed
/*     */   {
/*     */     @NotNull
/*     */     Key key();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Provider
/*     */   {
/*     */     @NotNull
/*     */     Sound.Source soundSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Emitter
/*     */   {
/*     */     @NotNull
/*     */     static Emitter self() {
/* 403 */       return SoundImpl.EMITTER_SELF;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\sound\Sound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */