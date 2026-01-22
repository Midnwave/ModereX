/*    */ package ac.grim.grimac.shaded.incendo.cloud;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SenderMapperImpl<A, B>
/*    */   implements SenderMapper<A, B>
/*    */ {
/* 33 */   static final SenderMapper<?, ?> IDENTITY = new SenderMapperImpl(
/* 34 */       Function.identity(), Function.identity());
/*    */ 
/*    */   
/*    */   private final Function<A, B> map;
/*    */   
/*    */   private final Function<B, A> reverse;
/*    */ 
/*    */   
/*    */   SenderMapperImpl(Function<A, B> map, Function<B, A> reverse) {
/* 43 */     this.map = Objects.<Function<A, B>>requireNonNull(map, "map function");
/* 44 */     this.reverse = Objects.<Function<B, A>>requireNonNull(reverse, "reverse function");
/*    */   }
/*    */ 
/*    */   
/*    */   public B map(A base) {
/* 49 */     return this.map.apply(base);
/*    */   }
/*    */ 
/*    */   
/*    */   public A reverse(B mapped) {
/* 54 */     return this.reverse.apply(mapped);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 59 */     if (this == o) {
/* 60 */       return true;
/*    */     }
/* 62 */     if (o == null || getClass() != o.getClass()) {
/* 63 */       return false;
/*    */     }
/* 65 */     SenderMapperImpl<?, ?> that = (SenderMapperImpl<?, ?>)o;
/* 66 */     return (Objects.equals(this.map, that.map) && Objects.equals(this.reverse, that.reverse));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 71 */     return Objects.hash(new Object[] { this.map, this.reverse });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "SenderMapperImpl{map=" + this.map + ", reverse=" + this.reverse + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\SenderMapperImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */