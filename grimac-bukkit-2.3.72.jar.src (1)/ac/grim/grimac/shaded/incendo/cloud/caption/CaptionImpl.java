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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "Caption", generator = "Immutables")
/*     */ @Immutable
/*     */ final class CaptionImpl
/*     */   implements Caption
/*     */ {
/*     */   private final String key;
/*     */   
/*     */   private CaptionImpl(String key) {
/*  53 */     this.key = Objects.<String>requireNonNull(key, "key");
/*     */   }
/*     */   
/*     */   private CaptionImpl(CaptionImpl original, String key) {
/*  57 */     this.key = key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String key() {
/*  65 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CaptionImpl withKey(String value) {
/*  75 */     String newValue = Objects.<String>requireNonNull(value, "key");
/*  76 */     if (this.key.equals(newValue)) return this; 
/*  77 */     return new CaptionImpl(this, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/*  86 */     if (this == another) return true; 
/*  87 */     return (another instanceof CaptionImpl && 
/*  88 */       equalTo(0, (CaptionImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, CaptionImpl another) {
/*  92 */     return this.key.equals(another.key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 101 */     int h = 5381;
/* 102 */     h += (h << 5) + this.key.hashCode();
/* 103 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 112 */     return "Caption{key=" + this.key + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CaptionImpl of(String key) {
/* 123 */     return new CaptionImpl(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CaptionImpl copyOf(Caption instance) {
/* 134 */     if (instance instanceof CaptionImpl) {
/* 135 */       return (CaptionImpl)instance;
/*     */     }
/* 137 */     return of(instance.key());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\CaptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */