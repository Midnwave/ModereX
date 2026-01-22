/*     */ package ac.grim.grimac.shaded.maps.weak;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
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
/*     */ public interface Weak<Self extends Weak<Self>>
/*     */ {
/*     */   boolean isPresent();
/*     */   
/*     */   Object asObject();
/*     */   
/*     */   default Optional<Object> asOptional() {
/*  47 */     return isPresent() ? Optional.<Object>of(asObject()) : Optional.<Object>empty();
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
/*     */   default <T> T as(Class<T> type) {
/*  59 */     return type.cast(asObject());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String asString() {
/*  67 */     return as(String.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default <T> List<T> asList() {
/*  75 */     return as((Class)List.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default <K, V> Map<K, V> asMap() {
/*  83 */     return as((Class)Map.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean is(Class<?> type) {
/*  91 */     return (isPresent() && type.isInstance(asObject()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean isMap() {
/*  99 */     return is(Map.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean isString() {
/* 107 */     return is(String.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean isList() {
/* 115 */     return is(List.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Converter convert() {
/* 125 */     return Converter.convert(asObject());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default OptionalWeak<Self> maybe() {
/* 134 */     return isPresent() ? OptionalWeak.<Self>of((Self)this) : OptionalWeak.<Self>empty();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\Weak.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */