/*     */ package ac.grim.grimac.shaded.incendo.cloud.type.tuple;
/*     */ 
/*     */ import java.util.Objects;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public class Pair<U, V>
/*     */   implements Tuple
/*     */ {
/*     */   private final U first;
/*     */   private final V second;
/*     */   
/*     */   protected Pair(U first, V second) {
/*  46 */     this.first = first;
/*  47 */     this.second = second;
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
/*     */   public static <U, V> Pair<U, V> of(U first, V second) {
/*  63 */     return new Pair<>(first, second);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final U first() {
/*  72 */     return this.first;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final V second() {
/*  81 */     return this.second;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object o) {
/*  86 */     if (this == o) {
/*  87 */       return true;
/*     */     }
/*  89 */     if (o == null || getClass() != o.getClass()) {
/*  90 */       return false;
/*     */     }
/*  92 */     Pair<?, ?> pair = (Pair<?, ?>)o;
/*  93 */     return (Objects.equals(first(), pair.first()) && 
/*  94 */       Objects.equals(second(), pair.second()));
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/*  99 */     return Objects.hash(new Object[] { first(), second() });
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 104 */     return String.format("(%s, %s)", new Object[] { this.first, this.second });
/*     */   }
/*     */ 
/*     */   
/*     */   public final int size() {
/* 109 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object[] toArray() {
/* 114 */     Object[] array = new Object[2];
/* 115 */     array[0] = this.first;
/* 116 */     array[1] = this.second;
/* 117 */     return array;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\tuple\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */