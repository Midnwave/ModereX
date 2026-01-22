/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ 
/*    */ public class VelocityData {
/*    */   public final Vector3dm vector;
/*    */   public final int entityID;
/*    */   public final int transaction;
/*  9 */   public double offset = 2.147483647E9D;
/*    */   public boolean isSetback;
/*    */   
/*    */   public VelocityData(int entityID, int transaction, boolean isSetback, Vector3dm vector) {
/* 13 */     this.entityID = entityID;
/* 14 */     this.vector = vector;
/* 15 */     this.transaction = transaction;
/* 16 */     this.isSetback = isSetback;
/*    */   }
/*    */ 
/*    */   
/*    */   public VelocityData(int entityID, int transaction, Vector3dm vector, boolean isSetback, double offset) {
/* 21 */     this.entityID = entityID;
/* 22 */     this.vector = vector;
/* 23 */     this.transaction = transaction;
/* 24 */     this.isSetback = isSetback;
/* 25 */     this.offset = offset;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\VelocityData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */