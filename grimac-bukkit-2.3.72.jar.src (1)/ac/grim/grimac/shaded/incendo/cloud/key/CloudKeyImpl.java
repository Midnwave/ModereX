/*     */ package ac.grim.grimac.shaded.incendo.cloud.key;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "CloudKey", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CloudKeyImpl<T>
/*     */   extends CloudKey<T>
/*     */ {
/*     */   private final String name;
/*     */   private final TypeToken<T> type;
/*     */   
/*     */   private CloudKeyImpl(String name, TypeToken<T> type) {
/*  54 */     this.name = Objects.<String>requireNonNull(name, "name");
/*  55 */     this.type = Objects.<TypeToken<T>>requireNonNull(type, "type");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CloudKeyImpl(CloudKeyImpl<T> original, String name, TypeToken<T> type) {
/*  62 */     this.name = name;
/*  63 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String name() {
/*  71 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TypeToken<T> type() {
/*  79 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CloudKeyImpl<T> withName(String value) {
/*  89 */     String newValue = Objects.<String>requireNonNull(value, "name");
/*  90 */     if (this.name.equals(newValue)) return this; 
/*  91 */     return new CloudKeyImpl(this, newValue, this.type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CloudKeyImpl<T> withType(TypeToken<T> value) {
/* 101 */     if (this.type == value) return this; 
/* 102 */     TypeToken<T> newValue = Objects.<TypeToken<T>>requireNonNull(value, "type");
/* 103 */     return new CloudKeyImpl(this, this.name, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 112 */     return "CloudKey{name=" + this.name + ", type=" + this.type + "}";
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
/*     */   public static <T> CloudKeyImpl<T> of(String name, TypeToken<T> type) {
/* 126 */     return new CloudKeyImpl<>(name, type);
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
/*     */   public static <T> CloudKeyImpl<T> copyOf(CloudKey<T> instance) {
/* 138 */     if (instance instanceof CloudKeyImpl) {
/* 139 */       return (CloudKeyImpl<T>)instance;
/*     */     }
/* 141 */     return of(instance.name(), instance.type());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\key\CloudKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */