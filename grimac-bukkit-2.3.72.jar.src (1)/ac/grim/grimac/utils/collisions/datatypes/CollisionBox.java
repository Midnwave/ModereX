/*    */ package ac.grim.grimac.utils.collisions.datatypes;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface CollisionBox
/*    */ {
/*    */   CollisionBox union(SimpleCollisionBox paramSimpleCollisionBox);
/*    */   
/*    */   boolean isCollided(SimpleCollisionBox paramSimpleCollisionBox);
/*    */   
/*    */   boolean isIntersected(SimpleCollisionBox paramSimpleCollisionBox);
/*    */   
/*    */   CollisionBox copy();
/*    */   
/*    */   CollisionBox offset(double paramDouble1, double paramDouble2, double paramDouble3);
/*    */   
/*    */   void downCast(List<SimpleCollisionBox> paramList);
/*    */   
/*    */   int downCast(SimpleCollisionBox[] paramArrayOfSimpleCollisionBox);
/*    */   
/*    */   boolean isNull();
/*    */   
/*    */   boolean isFullBlock();
/*    */   
/*    */   default boolean isSideFullBlock(BlockFace axis) {
/* 32 */     return isFullBlock();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\CollisionBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */