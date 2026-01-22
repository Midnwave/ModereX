/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ComponentDecoder<S, O extends ac.grim.grimac.shaded.kyori.adventure.text.Component>
/*    */ {
/*    */   @NotNull
/*    */   O deserialize(@NotNull S paramS);
/*    */   
/*    */   @Contract(value = "!null -> !null; null -> null", pure = true)
/*    */   @Nullable
/*    */   default O deserializeOrNull(@Nullable S input) {
/* 61 */     return deserializeOr(input, null);
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
/*    */   @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
/*    */   @Nullable
/*    */   default O deserializeOr(@Nullable S input, @Nullable O fallback) {
/* 76 */     if (input == null) return fallback;
/*    */     
/* 78 */     return deserialize(input);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\ComponentDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */