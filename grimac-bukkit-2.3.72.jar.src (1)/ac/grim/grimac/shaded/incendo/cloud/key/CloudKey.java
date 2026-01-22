/*     */ package ac.grim.grimac.shaded.incendo.cloud.key;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Value.Immutable;
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
/*     */ @Immutable
/*     */ public abstract class CloudKey<T>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <T> CloudKey<T> of(String name, TypeToken<T> type) {
/*  56 */     return CloudKeyImpl.of(name, type);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <T> CloudKey<T> of(String name, Class<T> type) {
/*  72 */     return CloudKeyImpl.of(name, TypeToken.get(type));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static CloudKey<Void> of(String name) {
/*  83 */     return CloudKeyImpl.of(name, TypeToken.get(void.class));
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <T> CloudKey<T> cloudKey(String name, TypeToken<T> type) {
/*  99 */     return CloudKeyImpl.of(name, type);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <T> CloudKey<T> cloudKey(String name, Class<T> type) {
/* 115 */     return CloudKeyImpl.of(name, TypeToken.get(type));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static CloudKey<Void> cloudKey(String name) {
/* 126 */     return CloudKeyImpl.of(name, TypeToken.get(void.class));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String name();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract TypeToken<T> type();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean equals(Object other) {
/* 149 */     if (this == other) {
/* 150 */       return true;
/*     */     }
/* 152 */     if (other == null || getClass() != other.getClass()) {
/* 153 */       return false;
/*     */     }
/* 155 */     CloudKey<?> that = (CloudKey)other;
/* 156 */     return Objects.equals(name(), that.name());
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 161 */     return Objects.hashCode(name());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\key\CloudKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */