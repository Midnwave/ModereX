/*     */ package ac.grim.grimac.shaded.kyori.adventure.title;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Ticks;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.time.Duration;
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
/*     */ public interface Title
/*     */   extends Examinable
/*     */ {
/*  48 */   public static final Times DEFAULT_TIMES = Times.times(Ticks.duration(10L), Ticks.duration(70L), Ticks.duration(20L));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Title title(@NotNull Component title, @NotNull Component subtitle) {
/*  59 */     return title(title, subtitle, DEFAULT_TIMES);
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
/*     */   @NotNull
/*     */   static Title title(@NotNull Component title, @NotNull Component subtitle, @Nullable Times times) {
/*  72 */     return new TitleImpl(title, subtitle, times);
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
/*     */   Component title();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Component subtitle();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Times times();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   <T> T part(@NotNull TitlePart<T> paramTitlePart);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Times
/*     */     extends Examinable
/*     */   {
/*     */     @Deprecated
/*     */     @ScheduledForRemoval(inVersion = "5.0.0")
/*     */     @NotNull
/*     */     static Times of(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
/* 128 */       return times(fadeIn, stay, fadeOut);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     static Times times(@NotNull Duration fadeIn, @NotNull Duration stay, @NotNull Duration fadeOut) {
/* 141 */       return new TitleImpl.TimesImpl(fadeIn, stay, fadeOut);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     Duration fadeIn();
/*     */     
/*     */     @NotNull
/*     */     Duration stay();
/*     */     
/*     */     @NotNull
/*     */     Duration fadeOut();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\title\Title.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */