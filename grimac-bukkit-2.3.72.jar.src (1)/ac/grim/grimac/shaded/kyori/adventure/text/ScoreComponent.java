/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface ScoreComponent
/*     */   extends BuildableComponent<ScoreComponent, ScoreComponent.Builder>, ScopedComponent<ScoreComponent>
/*     */ {
/*     */   @NotNull
/*     */   String name();
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   ScoreComponent name(@NotNull String paramString);
/*     */   
/*     */   @NotNull
/*     */   String objective();
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   ScoreComponent objective(@NotNull String paramString);
/*     */   
/*     */   @Deprecated
/*     */   @Nullable
/*     */   String value();
/*     */   
/*     */   @Deprecated
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   ScoreComponent value(@Nullable String paramString);
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 115 */     return Stream.concat(
/* 116 */         Stream.of(new ExaminableProperty[] {
/* 117 */             ExaminableProperty.of("name", name()), 
/* 118 */             ExaminableProperty.of("objective", objective()), 
/* 119 */             ExaminableProperty.of("value", value())
/*     */           
/* 121 */           }), super.examinableProperties());
/*     */   }
/*     */   
/*     */   public static interface Builder extends ComponentBuilder<ScoreComponent, Builder> {
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder name(@NotNull String param1String);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder objective(@NotNull String param1String);
/*     */     
/*     */     @Deprecated
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder value(@Nullable String param1String);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\ScoreComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */