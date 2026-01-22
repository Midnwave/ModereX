/*     */ package ac.grim.grimac.shaded.incendo.cloud.key;
/*     */ 
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
/*     */ public interface MutableCloudKeyContainer
/*     */   extends CloudKeyContainer
/*     */ {
/*     */   <V> void store(CloudKey<V> paramCloudKey, V paramV);
/*     */   
/*     */   <V> void store(String paramString, V paramV);
/*     */   
/*     */   default <V> void store(CloudKeyHolder<V> keyHolder, V value) {
/*  66 */     store(keyHolder.key(), value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void remove(CloudKey<?> paramCloudKey);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void remove(String key) {
/*  82 */     remove(CloudKey.of(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default void remove(CloudKeyHolder<?> keyHolder) {
/*  91 */     remove(keyHolder.key());
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
/*     */   default <V> void set(CloudKey<V> key, V value) {
/* 104 */     if (value == null) {
/* 105 */       remove(key);
/*     */     } else {
/* 107 */       store(key, value);
/*     */     } 
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
/*     */   default <V> void set(String key, V value) {
/* 121 */     if (value == null) {
/* 122 */       remove(key);
/*     */     } else {
/* 124 */       store(key, value);
/*     */     } 
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
/*     */   default <V> void set(CloudKeyHolder<V> keyHolder, V value) {
/* 138 */     if (value == null) {
/* 139 */       remove(keyHolder);
/*     */     } else {
/* 141 */       store(keyHolder, value);
/*     */     } 
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
/*     */   <V> V computeIfAbsent(CloudKey<V> paramCloudKey, Function<CloudKey<V>, V> paramFunction);
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
/*     */   default <V> V computeIfAbsent(CloudKeyHolder<V> keyHolder, Function<CloudKey<V>, V> defaultFunction) {
/* 169 */     return computeIfAbsent(keyHolder.key(), defaultFunction);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\key\MutableCloudKeyContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */