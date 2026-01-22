/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.builder.AbstractBuilder;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public interface JoinConfiguration
/*     */   extends Buildable<JoinConfiguration, JoinConfiguration.Builder>, Examinable
/*     */ {
/*     */   @NotNull
/*     */   static Builder builder() {
/*  95 */     return new JoinConfigurationImpl.BuilderImpl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static JoinConfiguration noSeparators() {
/* 105 */     return JoinConfigurationImpl.NULL;
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
/*     */   static JoinConfiguration newlines() {
/* 118 */     return JoinConfigurationImpl.STANDARD_NEW_LINES;
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
/*     */   static JoinConfiguration spaces() {
/* 131 */     return JoinConfigurationImpl.STANDARD_SPACES;
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
/*     */   static JoinConfiguration commas(boolean spaces) {
/* 145 */     return spaces ? JoinConfigurationImpl.STANDARD_COMMA_SPACE_SEPARATED : JoinConfigurationImpl.STANDARD_COMMA_SEPARATED;
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
/*     */   @NotNull
/*     */   static JoinConfiguration arrayLike() {
/* 160 */     return JoinConfigurationImpl.STANDARD_ARRAY_LIKE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static JoinConfiguration separator(@Nullable ComponentLike separator) {
/* 171 */     if (separator == null) return JoinConfigurationImpl.NULL; 
/* 172 */     return (JoinConfiguration)builder().separator(separator).build();
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
/*     */   static JoinConfiguration separators(@Nullable ComponentLike separator, @Nullable ComponentLike lastSeparator) {
/* 184 */     if (separator == null && lastSeparator == null) return JoinConfigurationImpl.NULL; 
/* 185 */     return (JoinConfiguration)builder().separator(separator).lastSeparator(lastSeparator).build();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   Component prefix();
/*     */   
/*     */   @Nullable
/*     */   Component suffix();
/*     */   
/*     */   @Nullable
/*     */   Component separator();
/*     */   
/*     */   @Nullable
/*     */   Component lastSeparator();
/*     */   
/*     */   @Nullable
/*     */   Component lastSeparatorIfSerial();
/*     */   
/*     */   @NotNull
/*     */   Function<ComponentLike, Component> convertor();
/*     */   
/*     */   @NotNull
/*     */   Predicate<ComponentLike> predicate();
/*     */   
/*     */   @NotNull
/*     */   Style parentStyle();
/*     */   
/*     */   public static interface Builder extends AbstractBuilder<JoinConfiguration>, Buildable.Builder<JoinConfiguration> {
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder prefix(@Nullable ComponentLike param1ComponentLike);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder suffix(@Nullable ComponentLike param1ComponentLike);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder separator(@Nullable ComponentLike param1ComponentLike);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder lastSeparator(@Nullable ComponentLike param1ComponentLike);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder lastSeparatorIfSerial(@Nullable ComponentLike param1ComponentLike);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder convertor(@NotNull Function<ComponentLike, Component> param1Function);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder predicate(@NotNull Predicate<ComponentLike> param1Predicate);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder parentStyle(@NotNull Style param1Style);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\JoinConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */