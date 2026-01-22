/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.ComponentLike;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.Style;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.StyleBuilderApplicable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.OptionalDouble;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Tag
/*     */ {
/*     */   @NotNull
/*     */   static PreProcess preProcessParsed(@NotNull String content) {
/*  58 */     return new PreProcessTagImpl(Objects.<String>requireNonNull(content, "content"));
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
/*     */   static Tag inserting(@NotNull Component content) {
/*  71 */     return new InsertingImpl(true, Objects.<Component>requireNonNull(content, "content must not be null"));
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
/*     */   static Tag inserting(@NotNull ComponentLike value) {
/*  84 */     return inserting(((ComponentLike)Objects.<ComponentLike>requireNonNull(value, "value")).asComponent());
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
/*     */   static Tag selfClosingInserting(@NotNull Component content) {
/*  97 */     return new InsertingImpl(false, Objects.<Component>requireNonNull(content, "content must not be null"));
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
/*     */   static Tag selfClosingInserting(@NotNull ComponentLike value) {
/* 110 */     return selfClosingInserting(((ComponentLike)Objects.<ComponentLike>requireNonNull(value, "value")).asComponent());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Tag styling(Consumer<Style.Builder> styles) {
/* 121 */     return new CallbackStylingTagImpl(styles);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   static Tag styling(@NotNull StyleBuilderApplicable... actions) {
/* 132 */     Objects.requireNonNull(actions, "actions");
/* 133 */     for (int i = 0, length = actions.length; i < length; i++) {
/* 134 */       if (actions[i] == null) {
/* 135 */         throw new NullPointerException("actions[" + i + "]");
/*     */       }
/*     */     } 
/* 138 */     return new StylingTagImpl(Arrays.<StyleBuilderApplicable>copyOf(actions, actions.length));
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
/*     */   @NonExtendable
/*     */   public static interface Argument
/*     */   {
/*     */     @NotNull
/*     */     String value();
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
/*     */     default String lowerValue() {
/* 165 */       return value().toLowerCase(Locale.ROOT);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default boolean isTrue() {
/* 175 */       return ("true".equals(value()) || "on".equals(value()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     default boolean isFalse() {
/* 185 */       return ("false".equals(value()) || "off".equals(value()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     default OptionalInt asInt() {
/*     */       try {
/* 198 */         return OptionalInt.of(Integer.parseInt(value()));
/* 199 */       } catch (NumberFormatException ex) {
/* 200 */         return OptionalInt.empty();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     default OptionalDouble asDouble() {
/*     */       try {
/* 214 */         return OptionalDouble.of(Double.parseDouble(value()));
/* 215 */       } catch (NumberFormatException ex) {
/* 216 */         return OptionalDouble.empty();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\Tag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */