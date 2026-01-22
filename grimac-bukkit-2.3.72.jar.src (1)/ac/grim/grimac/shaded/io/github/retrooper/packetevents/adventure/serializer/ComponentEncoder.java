/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.serializer;
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
/*    */ public interface ComponentEncoder<I extends ac.grim.grimac.shaded.kyori.adventure.text.Component, R>
/*    */ {
/*    */   @NotNull
/*    */   R serialize(@NotNull I paramI);
/*    */   
/*    */   @Contract(value = "!null -> !null; null -> null", pure = true)
/*    */   @Nullable
/*    */   default R serializeOrNull(@Nullable I component) {
/* 61 */     return serializeOr(component, null);
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
/*    */   default R serializeOr(@Nullable I component, @Nullable R fallback) {
/* 76 */     if (component == null) return fallback;
/*    */     
/* 78 */     return serialize(component);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\serializer\ComponentEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */