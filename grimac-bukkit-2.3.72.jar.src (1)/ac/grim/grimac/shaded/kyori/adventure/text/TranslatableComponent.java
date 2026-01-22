/*     */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.translation.Translatable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public interface TranslatableComponent
/*     */   extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, ScopedComponent<TranslatableComponent>
/*     */ {
/*     */   @NotNull
/*     */   String key();
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default TranslatableComponent key(@NotNull Translatable translatable) {
/*  82 */     return key(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey());
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
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   TranslatableComponent key(@NotNull String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @NotNull
/*     */   List<Component> args();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   TranslatableComponent args(@NotNull ComponentLike... args) {
/* 119 */     return arguments(args);
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
/*     */   @Deprecated
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   default TranslatableComponent args(@NotNull List<? extends ComponentLike> args) {
/* 135 */     return arguments(args);
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
/*     */   List<TranslationArgument> arguments();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   TranslatableComponent arguments(@NotNull ComponentLike... paramVarArgs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   TranslatableComponent arguments(@NotNull List<? extends ComponentLike> paramList);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   String fallback();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Contract(pure = true)
/*     */   @NotNull
/*     */   TranslatableComponent fallback(@Nullable String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   default Stream<? extends ExaminableProperty> examinableProperties() {
/* 197 */     return Stream.concat(
/* 198 */         Stream.of(new ExaminableProperty[] {
/* 199 */             ExaminableProperty.of("key", key()), 
/* 200 */             ExaminableProperty.of("arguments", arguments()), 
/* 201 */             ExaminableProperty.of("fallback", fallback())
/*     */           
/* 203 */           }), super.examinableProperties());
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
/*     */   public static interface Builder
/*     */     extends ComponentBuilder<TranslatableComponent, Builder>
/*     */   {
/*     */     @Contract(pure = true)
/*     */     @NotNull
/*     */     default Builder key(@NotNull Translatable translatable) {
/* 222 */       return key(((Translatable)Objects.<Translatable>requireNonNull(translatable, "translatable")).translationKey());
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
/*     */     Builder key(@NotNull String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder args(@NotNull ComponentBuilder<?, ?> arg) {
/* 246 */       return arguments(new ComponentLike[] { arg });
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
/*     */     @Deprecated
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder args(@NotNull ComponentBuilder<?, ?>... args) {
/* 261 */       return arguments((ComponentLike[])args);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder args(@NotNull Component arg) {
/* 275 */       return arguments(new ComponentLike[] { arg });
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
/*     */     @Deprecated
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder args(@NotNull ComponentLike... args) {
/* 291 */       return arguments(args);
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
/*     */     @Deprecated
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     default Builder args(@NotNull List<? extends ComponentLike> args) {
/* 307 */       return arguments(args);
/*     */     }
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder arguments(@NotNull ComponentLike... param1VarArgs);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder arguments(@NotNull List<? extends ComponentLike> param1List);
/*     */     
/*     */     @Contract("_ -> this")
/*     */     @NotNull
/*     */     Builder fallback(@Nullable String param1String);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TranslatableComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */