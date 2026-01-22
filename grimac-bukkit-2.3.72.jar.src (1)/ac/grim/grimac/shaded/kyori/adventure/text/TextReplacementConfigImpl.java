/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Objects;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.regex.MatchResult;
/*     */ import java.util.regex.Pattern;
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
/*     */ final class TextReplacementConfigImpl
/*     */   implements TextReplacementConfig
/*     */ {
/*     */   private final Pattern matchPattern;
/*     */   private final BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement;
/*     */   private final TextReplacementConfig.Condition continuer;
/*     */   private final boolean replaceInsideHoverEvents;
/*     */   
/*     */   TextReplacementConfigImpl(Builder builder) {
/*  44 */     this.matchPattern = builder.matchPattern;
/*  45 */     this.replacement = builder.replacement;
/*  46 */     this.continuer = builder.continuer;
/*  47 */     this.replaceInsideHoverEvents = builder.replaceInsideHoverEvents;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Pattern matchPattern() {
/*  52 */     return this.matchPattern;
/*     */   }
/*     */   
/*     */   TextReplacementRenderer.State createState() {
/*  56 */     return new TextReplacementRenderer.State(this.matchPattern, this.replacement, this.continuer, this.replaceInsideHoverEvents);
/*     */   }
/*     */ 
/*     */   
/*     */   public TextReplacementConfig.Builder toBuilder() {
/*  61 */     return new Builder(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/*  66 */     return Stream.of(new ExaminableProperty[] {
/*  67 */           ExaminableProperty.of("matchPattern", this.matchPattern), 
/*  68 */           ExaminableProperty.of("replacement", this.replacement), 
/*  69 */           ExaminableProperty.of("continuer", this.continuer)
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  75 */     return Internals.toString(this);
/*     */   }
/*     */   
/*     */   static final class Builder
/*     */     implements TextReplacementConfig.Builder {
/*     */     @Nullable
/*     */     Pattern matchPattern;
/*     */     @Nullable
/*     */     BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement;
/*     */     TextReplacementConfig.Condition continuer = (matchResult, index, replacement) -> PatternReplacementResult.REPLACE;
/*     */     boolean replaceInsideHoverEvents = true;
/*     */     
/*     */     Builder(TextReplacementConfigImpl instance) {
/*  88 */       this.matchPattern = instance.matchPattern;
/*  89 */       this.replacement = instance.replacement;
/*  90 */       this.continuer = instance.continuer;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder match(@NotNull Pattern pattern) {
/*  95 */       this.matchPattern = Objects.<Pattern>requireNonNull(pattern, "pattern");
/*  96 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder condition(TextReplacementConfig.Condition condition) {
/* 101 */       this.continuer = Objects.<TextReplacementConfig.Condition>requireNonNull(condition, "continuation");
/* 102 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder replacement(@NotNull BiFunction<MatchResult, TextComponent.Builder, ComponentLike> replacement) {
/* 107 */       this.replacement = Objects.<BiFunction<MatchResult, TextComponent.Builder, ComponentLike>>requireNonNull(replacement, "replacement");
/* 108 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public TextReplacementConfig.Builder replaceInsideHoverEvents(boolean replace) {
/* 113 */       this.replaceInsideHoverEvents = replace;
/* 114 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public TextReplacementConfig build() {
/* 119 */       if (this.matchPattern == null) throw new IllegalStateException("A pattern must be provided to match against"); 
/* 120 */       if (this.replacement == null) throw new IllegalStateException("A replacement action must be provided"); 
/* 121 */       return new TextReplacementConfigImpl(this);
/*     */     }
/*     */     
/*     */     Builder() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TextReplacementConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */