/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option;
/*    */ 
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.value.ValueType;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
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
/*    */ @NonExtendable
/*    */ public interface Option<V>
/*    */ {
/*    */   @Deprecated
/*    */   static Option<Boolean> booleanOption(String id, boolean defaultValue) {
/* 53 */     return OptionSchema.globalSchema().booleanOption(id, defaultValue);
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
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   static <E extends Enum<E>> Option<E> enumOption(String id, Class<E> enumClazz, E defaultValue) {
/* 71 */     return OptionSchema.globalSchema().enumOption(id, enumClazz, defaultValue);
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
/*    */   String id();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   default Class<V> type() {
/* 93 */     return valueType().type();
/*    */   }
/*    */   
/*    */   ValueType<V> valueType();
/*    */   
/*    */   V defaultValue();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\Option.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */