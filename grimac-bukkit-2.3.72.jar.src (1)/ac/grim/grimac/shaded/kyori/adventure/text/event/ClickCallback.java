/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.CheckReturnValue;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.audience.Audience;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.permission.PermissionChecker;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.PlatformAPI;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.time.Duration;
/*     */ import java.time.temporal.TemporalAmount;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
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
/*     */ @FunctionalInterface
/*     */ public interface ClickCallback<T extends Audience>
/*     */ {
/*  54 */   public static final Duration DEFAULT_LIFETIME = Duration.ofHours(12L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int UNLIMITED_USES = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CheckReturnValue
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static <W extends Audience, N extends W> ClickCallback<W> widen(@NotNull ClickCallback<N> original, @NotNull Class<N> type, @Nullable Consumer<? super Audience> otherwise) {
/*  77 */     return audience -> {
/*     */         if (type.isInstance(audience)) {
/*     */           original.accept(type.cast(audience));
/*     */         } else if (otherwise != null) {
/*     */           otherwise.accept(audience);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CheckReturnValue
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   static <W extends Audience, N extends W> ClickCallback<W> widen(@NotNull ClickCallback<N> original, @NotNull Class<N> type) {
/* 101 */     return widen(original, type, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CheckReturnValue
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default ClickCallback<T> filter(@NotNull Predicate<T> filter) {
/* 124 */     return filter(filter, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @CheckReturnValue
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default ClickCallback<T> filter(@NotNull Predicate<T> filter, @Nullable Consumer<? super Audience> otherwise) {
/* 138 */     return audience -> {
/*     */         if (filter.test(audience)) {
/*     */           accept((T)audience);
/*     */         } else if (otherwise != null) {
/*     */           otherwise.accept(audience);
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
/*     */ 
/*     */ 
/*     */   
/*     */   @CheckReturnValue
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default ClickCallback<T> requiringPermission(@NotNull String permission) {
/* 161 */     return requiringPermission(permission, null);
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
/*     */   @CheckReturnValue
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default ClickCallback<T> requiringPermission(@NotNull String permission, @Nullable Consumer<? super Audience> otherwise) {
/* 177 */     return filter(audience -> ((PermissionChecker)audience.getOrDefault(PermissionChecker.POINTER, ClickCallbackInternals.ALWAYS_FALSE)).test(permission), otherwise);
/*     */   }
/*     */   
/*     */   void accept(@NotNull T paramT);
/*     */   
/*     */   @PlatformAPI
/*     */   @Internal
/*     */   public static interface Provider {
/*     */     @NotNull
/*     */     ClickEvent create(@NotNull ClickCallback<Audience> param1ClickCallback, @NotNull ClickCallback.Options param1Options);
/*     */   }
/*     */   
/*     */   @NonExtendable
/*     */   public static interface Options
/*     */     extends Examinable {
/*     */     @NotNull
/*     */     static Builder builder() {
/* 194 */       return new ClickCallbackOptionsImpl.BuilderImpl();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     static Builder builder(@NotNull Options existing) {
/* 205 */       return new ClickCallbackOptionsImpl.BuilderImpl(existing);
/*     */     }
/*     */     
/*     */     int uses();
/*     */     
/*     */     @NotNull
/*     */     Duration lifetime();
/*     */     
/*     */     @NonExtendable
/*     */     public static interface Builder extends AbstractBuilder<Options> {
/*     */       @NotNull
/*     */       Builder uses(int param2Int);
/*     */       
/*     */       @NotNull
/*     */       Builder lifetime(@NotNull TemporalAmount param2TemporalAmount);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\ClickCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */