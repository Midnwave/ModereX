/*    */ package ac.grim.grimac.shaded.kyori.adventure.text;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NonExtendable
/*    */ public interface TranslationArgument
/*    */   extends TranslationArgumentLike, Examinable
/*    */ {
/*    */   @NotNull
/*    */   static TranslationArgument bool(boolean value) {
/* 48 */     return new TranslationArgumentImpl(Boolean.valueOf(value));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static TranslationArgument numeric(@NotNull Number value) {
/* 60 */     return new TranslationArgumentImpl(Objects.requireNonNull(value, "value"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   static TranslationArgument component(@NotNull ComponentLike value) {
/* 72 */     if (value instanceof TranslationArgumentLike) return ((TranslationArgumentLike)value).asTranslationArgument(); 
/* 73 */     return new TranslationArgumentImpl(Objects.requireNonNull(((ComponentLike)Objects.<ComponentLike>requireNonNull(value, "value")).asComponent(), "value.asComponent()"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   Object value();
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   default TranslationArgument asTranslationArgument() {
/* 86 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\TranslationArgument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */