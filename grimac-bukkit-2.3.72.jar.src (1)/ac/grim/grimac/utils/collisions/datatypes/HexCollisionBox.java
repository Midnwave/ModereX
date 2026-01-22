/*    */ package ac.grim.grimac.utils.collisions.datatypes;
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
/*    */ public class HexCollisionBox
/*    */   extends SimpleCollisionBox
/*    */ {
/*    */   public HexCollisionBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
/* 19 */     this.minX = minX / 16.0D;
/* 20 */     this.minY = minY / 16.0D;
/* 21 */     this.minZ = minZ / 16.0D;
/* 22 */     this.maxX = maxX / 16.0D;
/* 23 */     this.maxY = maxY / 16.0D;
/* 24 */     this.maxZ = maxZ / 16.0D;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\datatypes\HexCollisionBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */