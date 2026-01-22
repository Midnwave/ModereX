/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import java.util.Objects;
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
/*    */ public class Either<L, R>
/*    */ {
/*    */   @Nullable
/*    */   private final L left;
/*    */   @Nullable
/*    */   private final R right;
/*    */   
/*    */   private Either(@Nullable L left, @Nullable R right) {
/* 31 */     this.left = left;
/* 32 */     this.right = right;
/*    */   }
/*    */   
/*    */   public static <L, R> Either<L, R> createLeft(L left) {
/* 36 */     return new Either<>(left, null);
/*    */   }
/*    */   
/*    */   public static <L, R> Either<L, R> createRight(R right) {
/* 40 */     return new Either<>(null, right);
/*    */   }
/*    */   
/*    */   public Object get() {
/* 44 */     return (this.left != null) ? this.left : this.right;
/*    */   }
/*    */   
/*    */   public boolean isLeft() {
/* 48 */     return (this.left != null);
/*    */   }
/*    */   @Nullable
/*    */   public L getLeft() {
/* 52 */     return this.left;
/*    */   }
/*    */   
/*    */   public boolean isRight() {
/* 56 */     return (this.right != null);
/*    */   }
/*    */   @Nullable
/*    */   public R getRight() {
/* 60 */     return this.right;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 65 */     if (!(obj instanceof Either)) return false; 
/* 66 */     Either<?, ?> either = (Either<?, ?>)obj;
/* 67 */     if (!Objects.equals(this.left, either.left)) return false; 
/* 68 */     return Objects.equals(this.right, either.right);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 73 */     return Objects.hash(new Object[] { this.left, this.right });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\Either.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */