/*    */ package ac.grim.grimac.utils.collisions.datatypes;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class NoCollisionBox
/*    */   implements CollisionBox {
/*  7 */   public static final NoCollisionBox INSTANCE = new NoCollisionBox();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CollisionBox union(SimpleCollisionBox other) {
/* 14 */     return other;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCollided(SimpleCollisionBox other) {
/* 19 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isIntersected(SimpleCollisionBox other) {
/* 24 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public CollisionBox offset(double x, double y, double z) {
/* 29 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void downCast(List<SimpleCollisionBox> list) {}
/*    */ 
/*    */   
/*    */   public int downCast(SimpleCollisionBox[] list) {
/* 37 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull() {
/* 42 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFullBlock() {
/* 47 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public CollisionBox copy() {
/* 52 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\NoCollisionBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */