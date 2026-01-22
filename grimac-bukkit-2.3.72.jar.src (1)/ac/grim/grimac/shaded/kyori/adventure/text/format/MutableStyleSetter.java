/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.format;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @NonExtendable
/*    */ public interface MutableStyleSetter<T extends MutableStyleSetter<?>>
/*    */   extends StyleSetter<T>
/*    */ {
/*    */   @Contract("_ -> this")
/*    */   @NotNull
/*    */   T decorate(@NotNull TextDecoration... decorations) {
/* 56 */     for (int i = 0, length = decorations.length; i < length; i++) {
/* 57 */       decorate(decorations[i]);
/*    */     }
/* 59 */     return (T)this;
/*    */   }
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
/*    */   @Contract("_ -> this")
/*    */   @NotNull
/*    */   default T decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations) {
/* 75 */     Objects.requireNonNull(decorations, "decorations");
/* 76 */     for (Map.Entry<TextDecoration, TextDecoration.State> entry : decorations.entrySet()) {
/* 77 */       decoration(entry.getKey(), entry.getValue());
/*    */     }
/* 79 */     return (T)this;
/*    */   }
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
/*    */   @Contract("_, _ -> this")
/*    */   @NotNull
/*    */   default T decorations(@NotNull Set<TextDecoration> decorations, boolean flag) {
/* 95 */     TextDecoration.State state = TextDecoration.State.byBoolean(flag);
/* 96 */     decorations.forEach(decoration -> decoration(decoration, state));
/* 97 */     return (T)this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\format\MutableStyleSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */