/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.intellij.lang.annotations.RegExp;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.IntFunction2;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.MatchResult;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface TextReplacementConfig
/*     */   extends Buildable<TextReplacementConfig, TextReplacementConfig.Builder>, Examinable
/*     */ {
/*     */   @NotNull
/*     */   static Builder builder() {
/*  58 */     return new TextReplacementConfigImpl.Builder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   Pattern matchPattern();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface Condition
/*     */   {
/*     */     @NotNull
/*     */     PatternReplacementResult shouldReplace(@NotNull MatchResult param1MatchResult, int param1Int1, int param1Int2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Builder
/*     */     extends AbstractBuilder<TextReplacementConfig>, Buildable.Builder<TextReplacementConfig>
/*     */   {
/*     */     @Contract("_ -> this")
/*     */     default Builder matchLiteral(String literal) {
/*  92 */       return match(Pattern.compile(literal, 16));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder match(@NotNull @RegExp String pattern) {
/* 104 */       return match(Pattern.compile(pattern));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */     default Builder once() {
/* 130 */       return times(1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder times(int times) {
/* 142 */       return condition((index, replaced) -> (replaced < times) ? PatternReplacementResult.REPLACE : PatternReplacementResult.STOP);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder condition(@NotNull IntFunction2<PatternReplacementResult> condition) {
/* 155 */       return condition((result, matchCount, replaced) -> (PatternReplacementResult)condition.apply(matchCount, replaced));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder replacement(@NotNull String replacement) {
/* 184 */       Objects.requireNonNull(replacement, "replacement");
/* 185 */       return replacement(builder -> builder.content(replacement));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder replacement(@Nullable ComponentLike replacement) {
/* 197 */       Component baked = ComponentLike.unbox(replacement);
/* 198 */       return replacement((result, input) -> baked);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder replacement(@NotNull Function<TextComponent.Builder, ComponentLike> replacement) {
/* 210 */       Objects.requireNonNull(replacement, "replacement");
/* 211 */       return replacement((result, input) -> (ComponentLike)replacement.apply(input));
/*     */     }
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder match(@NotNull Pattern param1Pattern);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder condition(@NotNull TextReplacementConfig.Condition param1Condition);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder replacement(@NotNull BiFunction<MatchResult, TextComponent.Builder, ComponentLike> param1BiFunction);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder replaceInsideHoverEvents(boolean param1Boolean);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TextReplacementConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */