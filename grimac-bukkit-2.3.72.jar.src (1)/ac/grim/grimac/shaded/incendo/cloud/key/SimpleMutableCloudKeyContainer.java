/*     */ package ac.grim.grimac.shaded.incendo.cloud.key;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
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
/*     */ @API(status = API.Status.INTERNAL)
/*     */ public final class SimpleMutableCloudKeyContainer
/*     */   implements MutableCloudKeyContainer
/*     */ {
/*     */   private final Map<CloudKey<?>, Object> map;
/*     */   
/*     */   public SimpleMutableCloudKeyContainer(Map<CloudKey<?>, Object> map) {
/*  45 */     this.map = map;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> Optional<V> optional(CloudKey<V> key) {
/*  51 */     return Optional.ofNullable((V)this.map.get(key));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> Optional<V> optional(String key) {
/*  57 */     return optional((CloudKey)CloudKey.of(key));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(CloudKey<?> key) {
/*  62 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<CloudKey<?>, ? extends Object> all() {
/*  67 */     return Collections.unmodifiableMap(this.map);
/*     */   }
/*     */ 
/*     */   
/*     */   public <V> void store(CloudKey<V> key, V value) {
/*  72 */     this.map.put(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <V> void store(String key, V value) {
/*  77 */     this.map.put(CloudKey.of(key), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(CloudKey<?> key) {
/*  82 */     this.map.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> V computeIfAbsent(CloudKey<V> key, Function<CloudKey<V>, V> defaultFunction) {
/*  91 */     return (V)this.map.computeIfAbsent(key, $ -> defaultFunction.apply(key));
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
/*     */   public <V> V getOrNull(CloudKey<V> key) {
/* 105 */     return (V)this.map.get(key);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\key\SimpleMutableCloudKeyContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */