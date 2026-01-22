/*     */ package ac.grim.grimac.shaded.incendo.cloud.meta;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public class SimpleCommandMeta
/*     */   extends CommandMeta
/*     */ {
/*     */   private final Map<CloudKey<?>, Object> metaMap;
/*     */   
/*     */   protected SimpleCommandMeta(Map<CloudKey<?>, Object> metaMap) {
/*  46 */     this.metaMap = Collections.unmodifiableMap(new HashMap<>(metaMap));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final <V> Optional<V> optional(CloudKey<V> key) {
/*  52 */     Object value = this.metaMap.get(key);
/*  53 */     if (value == null) {
/*  54 */       return Optional.empty();
/*     */     }
/*  56 */     if (!GenericTypeReflector.isSuperType(key.type().getType(), value.getClass())) {
/*  57 */       throw new IllegalArgumentException("Conflicting argument types between key type of " + key
/*  58 */           .type().getType().getTypeName() + " and value type of " + value.getClass());
/*     */     }
/*     */     
/*  61 */     return Optional.of((V)value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <V> Optional<V> optional(String key) {
/*  70 */     Object value = this.metaMap.get(CloudKey.of(key));
/*  71 */     if (value == null) {
/*  72 */       return Optional.empty();
/*     */     }
/*     */     
/*  75 */     return Optional.of((V)value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(CloudKey<?> key) {
/*  83 */     return this.metaMap.containsKey(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Map<CloudKey<?>, ? extends Object> all() {
/*  88 */     return new HashMap<>(this.metaMap);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object other) {
/*  93 */     if (this == other) {
/*  94 */       return true;
/*     */     }
/*  96 */     if (other == null || getClass() != other.getClass()) {
/*  97 */       return false;
/*     */     }
/*  99 */     SimpleCommandMeta that = (SimpleCommandMeta)other;
/* 100 */     return Objects.equals(this.metaMap, that.metaMap);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 105 */     return Objects.hashCode(this.metaMap);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\meta\SimpleCommandMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */