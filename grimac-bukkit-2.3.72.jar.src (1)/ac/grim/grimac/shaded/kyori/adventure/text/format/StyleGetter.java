/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.event.HoverEvent;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
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
/*     */ public interface StyleGetter
/*     */ {
/*     */   @Nullable
/*     */   Key font();
/*     */   
/*     */   @Nullable
/*     */   TextColor color();
/*     */   
/*     */   @Nullable
/*     */   ShadowColor shadowColor();
/*     */   
/*     */   default boolean hasDecoration(@NotNull TextDecoration decoration) {
/*  78 */     return (decoration(decoration) == TextDecoration.State.TRUE);
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
/*     */   TextDecoration.State decoration(@NotNull TextDecoration paramTextDecoration);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default Map<TextDecoration, TextDecoration.State> decorations() {
/* 100 */     Map<TextDecoration, TextDecoration.State> decorations = new EnumMap<>(TextDecoration.class);
/* 101 */     for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
/* 102 */       TextDecoration decoration = DecorationMap.DECORATIONS[i];
/* 103 */       TextDecoration.State value = decoration(decoration);
/* 104 */       decorations.put(decoration, value);
/*     */     } 
/* 106 */     return decorations;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   ClickEvent clickEvent();
/*     */   
/*     */   @Nullable
/*     */   HoverEvent<?> hoverEvent();
/*     */   
/*     */   @Nullable
/*     */   String insertion();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\StyleGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */