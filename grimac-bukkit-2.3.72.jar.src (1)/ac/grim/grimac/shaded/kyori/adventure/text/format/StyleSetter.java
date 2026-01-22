/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEventSource;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.ARGBLike;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ import java.util.stream.Collectors;
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
/*     */ public interface StyleSetter<T extends StyleSetter<?>>
/*     */ {
/*     */   @NotNull
/*     */   T font(@Nullable Key paramKey);
/*     */   
/*     */   @NotNull
/*     */   T color(@Nullable TextColor paramTextColor);
/*     */   
/*     */   @NotNull
/*     */   T colorIfAbsent(@Nullable TextColor paramTextColor);
/*     */   
/*     */   @NotNull
/*     */   T shadowColor(@Nullable ARGBLike paramARGBLike);
/*     */   
/*     */   @NotNull
/*     */   T shadowColorIfAbsent(@Nullable ARGBLike paramARGBLike);
/*     */   
/*     */   @NotNull
/*     */   default T decorate(@NotNull TextDecoration decoration) {
/* 109 */     return decoration(decoration, TextDecoration.State.TRUE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   T decorate(@NotNull TextDecoration... decorations) {
/* 120 */     Map<TextDecoration, TextDecoration.State> map = new EnumMap<>(TextDecoration.class);
/* 121 */     for (int i = 0, length = decorations.length; i < length; i++) {
/* 122 */       map.put(decorations[i], TextDecoration.State.TRUE);
/*     */     }
/* 124 */     return decorations(map);
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
/*     */   default T decoration(@NotNull TextDecoration decoration, boolean flag) {
/* 137 */     return decoration(decoration, TextDecoration.State.byBoolean(flag));
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
/*     */   T decoration(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
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
/*     */   T decorationIfAbsent(@NotNull TextDecoration paramTextDecoration, TextDecoration.State paramState);
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
/*     */   T decorations(@NotNull Map<TextDecoration, TextDecoration.State> paramMap);
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
/*     */   default T decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
/* 184 */     return decorations((Map<TextDecoration, TextDecoration.State>)decorations.stream().collect(Collectors.toMap(Function.identity(), decoration -> TextDecoration.State.byBoolean(flag))));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   T clickEvent(@Nullable ClickEvent paramClickEvent);
/*     */   
/*     */   @NotNull
/*     */   T hoverEvent(@Nullable HoverEventSource<?> paramHoverEventSource);
/*     */   
/*     */   @NotNull
/*     */   T insertion(@Nullable String paramString);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\StyleSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */