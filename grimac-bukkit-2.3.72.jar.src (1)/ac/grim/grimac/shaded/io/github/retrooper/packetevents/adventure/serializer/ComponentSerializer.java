/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public interface ComponentSerializer<I extends ac.grim.grimac.shaded.kyori.adventure.text.Component, O extends ac.grim.grimac.shaded.kyori.adventure.text.Component, R>
/*     */   extends ComponentEncoder<I, R>, ComponentDecoder<R, O>
/*     */ {
/*     */   @NotNull
/*     */   O deserialize(@NotNull R paramR);
/*     */   
/*     */   @Deprecated
/*     */   @ScheduledForRemoval(inVersion = "5.0.0")
/*     */   @Contract(value = "!null -> !null; null -> null", pure = true)
/*     */   @Nullable
/*     */   default O deseializeOrNull(@Nullable R input) {
/*  65 */     return super.deserializeOrNull(input);
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
/*     */   @Contract(value = "!null -> !null; null -> null", pure = true)
/*     */   @Nullable
/*     */   default O deserializeOrNull(@Nullable R input) {
/*  80 */     return super.deserializeOr(input, null);
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
/*     */   @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
/*     */   @Nullable
/*     */   default O deserializeOr(@Nullable R input, @Nullable O fallback) {
/*  96 */     return super.deserializeOr(input, fallback);
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
/*     */   R serialize(@NotNull I paramI);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(value = "!null -> !null; null -> null", pure = true)
/*     */   @Nullable
/*     */   default R serializeOrNull(@Nullable I component) {
/* 121 */     return serializeOr(component, null);
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
/*     */   @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
/*     */   @Nullable
/*     */   default R serializeOr(@Nullable I component, @Nullable R fallback) {
/* 137 */     if (component == null) return fallback;
/*     */     
/* 139 */     return serialize(component);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\ComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */