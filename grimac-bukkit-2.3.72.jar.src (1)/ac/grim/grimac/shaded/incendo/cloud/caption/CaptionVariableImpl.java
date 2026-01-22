/*     */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "CaptionVariable", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CaptionVariableImpl
/*     */   implements CaptionVariable
/*     */ {
/*     */   private final String key;
/*     */   private final String value;
/*     */   
/*     */   private CaptionVariableImpl(String key, String value) {
/*  56 */     this.key = Objects.<String>requireNonNull(key, "key");
/*  57 */     this.value = Objects.<String>requireNonNull(value, "value");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CaptionVariableImpl(CaptionVariableImpl original, String key, String value) {
/*  64 */     this.key = key;
/*  65 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String key() {
/*  73 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String value() {
/*  81 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CaptionVariableImpl withKey(String value) {
/*  91 */     String newValue = Objects.<String>requireNonNull(value, "key");
/*  92 */     if (this.key.equals(newValue)) return this; 
/*  93 */     return new CaptionVariableImpl(this, newValue, this.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CaptionVariableImpl withValue(String value) {
/* 103 */     String newValue = Objects.<String>requireNonNull(value, "value");
/* 104 */     if (this.value.equals(newValue)) return this; 
/* 105 */     return new CaptionVariableImpl(this, this.key, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 114 */     if (this == another) return true; 
/* 115 */     return (another instanceof CaptionVariableImpl && 
/* 116 */       equalTo(0, (CaptionVariableImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CaptionVariableImpl another) {
/* 120 */     return (this.key.equals(another.key) && this.value
/* 121 */       .equals(another.value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 130 */     int h = 5381;
/* 131 */     h += (h << 5) + this.key.hashCode();
/* 132 */     h += (h << 5) + this.value.hashCode();
/* 133 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 142 */     return "CaptionVariable{key=" + this.key + ", value=" + this.value + "}";
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
/*     */   public static CaptionVariableImpl of(String key, String value) {
/* 155 */     return new CaptionVariableImpl(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CaptionVariableImpl copyOf(CaptionVariable instance) {
/* 166 */     if (instance instanceof CaptionVariableImpl) {
/* 167 */       return (CaptionVariableImpl)instance;
/*     */     }
/* 169 */     return of(instance.key(), instance.value());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\CaptionVariableImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */