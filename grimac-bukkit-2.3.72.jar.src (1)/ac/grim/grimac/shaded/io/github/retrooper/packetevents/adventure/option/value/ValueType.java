/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.value;
/*    */ 
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
/*    */ public interface ValueType<T>
/*    */ {
/*    */   static ValueType<String> stringType() {
/* 42 */     return ValueTypeImpl.Types.STRING;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static ValueType<Boolean> booleanType() {
/* 52 */     return ValueTypeImpl.Types.BOOLEAN;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static ValueType<Integer> integerType() {
/* 62 */     return ValueTypeImpl.Types.INT;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static ValueType<Double> doubleType() {
/* 72 */     return ValueTypeImpl.Types.DOUBLE;
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
/*    */   static <E extends Enum<E>> ValueType<E> enumType(Class<E> enumClazz) {
/* 84 */     return new ValueTypeImpl.EnumType<>(Objects.<Class<E>>requireNonNull(enumClazz, "enumClazz"));
/*    */   }
/*    */   
/*    */   Class<T> type();
/*    */   
/*    */   T parse(String paramString) throws IllegalArgumentException;
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\value\ValueType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */