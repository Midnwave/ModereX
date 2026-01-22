/*     */ package ac.grim.grimac.shaded.incendo.cloud.key;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Supplier;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface CloudKeyContainer
/*     */ {
/*     */   default <V> Optional<V> optional(CloudKeyHolder<V> keyHolder) {
/*  62 */     return optional(keyHolder.key());
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
/*     */   default <V> V getOrDefault(CloudKey<V> key, V defaultValue) {
/*  75 */     return optional(key).orElse(defaultValue);
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
/*     */   default <V> V getOrDefault(String key, V defaultValue) {
/*  88 */     return optional(key).orElse(defaultValue);
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
/*     */   default <V> V getOrDefault(CloudKeyHolder<V> keyHolder, V defaultValue) {
/* 101 */     return getOrDefault(keyHolder.key(), defaultValue);
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
/*     */   default <V> V getOrSupplyDefault(CloudKey<V> key, Supplier<V> supplier) {
/* 114 */     return optional(key).orElseGet(supplier);
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
/*     */   default <V> V getOrSupplyDefault(String key, Supplier<V> supplier) {
/* 127 */     return optional(key).orElseGet(supplier);
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
/*     */   default <V> V getOrSupplyDefault(CloudKeyHolder<V> keyHolder, Supplier<V> supplier) {
/* 140 */     return optional(keyHolder).orElseGet(supplier);
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
/*     */   default <V> V get(CloudKey<V> key) {
/* 152 */     return (V)optional(key).orElseThrow(() -> new NullPointerException(String.format("There is no object in the registry identified by the key '%s'", new Object[] { key.name() })));
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
/*     */   default <V> V get(String key) {
/* 166 */     return (V)optional(key).map(value -> value).orElseThrow(() -> new NullPointerException(String.format("There is no object in the registry identified by the key '%s'", new Object[] { key })));
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
/*     */   default <V> V get(CloudKeyHolder<V> keyHolder) {
/* 180 */     return get(keyHolder.key());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean contains(String key) {
/* 198 */     return contains(CloudKey.of(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default boolean contains(CloudKeyHolder<?> keyHolder) {
/* 208 */     return contains(keyHolder.key());
/*     */   }
/*     */   
/*     */   <V> Optional<V> optional(CloudKey<V> paramCloudKey);
/*     */   
/*     */   <V> Optional<V> optional(String paramString);
/*     */   
/*     */   boolean contains(CloudKey<?> paramCloudKey);
/*     */   
/*     */   Map<CloudKey<?>, ? extends Object> all();
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\key\CloudKeyContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */