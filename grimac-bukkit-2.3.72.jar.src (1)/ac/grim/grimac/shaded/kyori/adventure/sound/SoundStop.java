/*     */ package ac.grim.grimac.shaded.kyori.adventure.sound;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.Objects;
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
/*     */ @NonExtendable
/*     */ public interface SoundStop
/*     */   extends Examinable
/*     */ {
/*     */   @NotNull
/*     */   static SoundStop all() {
/*  56 */     return SoundStopImpl.ALL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop named(@NotNull final Key sound) {
/*  67 */     Objects.requireNonNull(sound, "sound");
/*  68 */     return new SoundStopImpl(null) {
/*     */         @NotNull
/*     */         public Key sound() {
/*  71 */           return sound;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop named(final Sound.Type sound) {
/*  84 */     Objects.requireNonNull(sound, "sound");
/*  85 */     return new SoundStopImpl(null) {
/*     */         @NotNull
/*     */         public Key sound() {
/*  88 */           return sound.key();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop named(@NotNull final Supplier<? extends Sound.Type> sound) {
/* 101 */     Objects.requireNonNull(sound, "sound");
/* 102 */     return new SoundStopImpl(null) {
/*     */         @NotNull
/*     */         public Key sound() {
/* 105 */           return ((Sound.Type)sound.get()).key();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop source(Sound.Source source) {
/* 118 */     Objects.requireNonNull(source, "source");
/* 119 */     return new SoundStopImpl(source) {
/*     */         @Nullable
/*     */         public Key sound() {
/* 122 */           return null;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop namedOnSource(@NotNull final Key sound, Sound.Source source) {
/* 136 */     Objects.requireNonNull(sound, "sound");
/* 137 */     Objects.requireNonNull(source, "source");
/* 138 */     return new SoundStopImpl(source) {
/*     */         @NotNull
/*     */         public Key sound() {
/* 141 */           return sound;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop namedOnSource(Sound.Type sound, Sound.Source source) {
/* 155 */     Objects.requireNonNull(sound, "sound");
/* 156 */     return namedOnSource(sound.key(), source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static SoundStop namedOnSource(@NotNull final Supplier<? extends Sound.Type> sound, Sound.Source source) {
/* 168 */     Objects.requireNonNull(sound, "sound");
/* 169 */     Objects.requireNonNull(source, "source");
/* 170 */     return new SoundStopImpl(source) {
/*     */         @NotNull
/*     */         public Key sound() {
/* 173 */           return ((Sound.Type)sound.get()).key();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   Key sound();
/*     */   
/*     */   Sound.Source source();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\sound\SoundStop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */