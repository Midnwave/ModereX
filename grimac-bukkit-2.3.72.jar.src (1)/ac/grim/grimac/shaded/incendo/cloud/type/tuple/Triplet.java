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
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public class Triplet<U, V, W>
/*     */   implements Tuple
/*     */ {
/*     */   private final U first;
/*     */   private final V second;
/*     */   private final W third;
/*     */   
/*     */   protected Triplet(U first, V second, W third) {
/*  49 */     this.first = first;
/*  50 */     this.second = second;
/*  51 */     this.third = third;
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
/*     */   
/*     */   public static <U, V, W> Triplet<U, V, W> of(U first, V second, W third) {
/*  70 */     return new Triplet<>(first, second, third);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final U first() {
/*  79 */     return this.first;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final V second() {
/*  88 */     return this.second;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final W third() {
/*  97 */     return this.third;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object o) {
/* 102 */     if (this == o) {
/* 103 */       return true;
/*     */     }
/* 105 */     if (o == null || getClass() != o.getClass()) {
/* 106 */       return false;
/*     */     }
/* 108 */     Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>)o;
/* 109 */     return (Objects.equals(first(), triplet.first()) && 
/* 110 */       Objects.equals(second(), triplet.second()) && 
/* 111 */       Objects.equals(third(), triplet.third()));
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 116 */     return Objects.hash(new Object[] { first(), second(), third() });
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 121 */     return String.format("(%s, %s, %s)", new Object[] { this.first, this.second, this.third });
/*     */   }
/*     */ 
/*     */   
/*     */   public final int size() {
/* 126 */     return 3;
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object[] toArray() {
/* 131 */     Object[] array = new Object[3];
/* 132 */     array[0] = this.first;
/* 133 */     array[1] = this.second;
/* 134 */     array[2] = this.third;
/* 135 */     return array;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\type\tuple\Triplet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */