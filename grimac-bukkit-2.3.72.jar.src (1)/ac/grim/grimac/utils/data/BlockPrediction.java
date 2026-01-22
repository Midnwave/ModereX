/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class BlockPrediction {
/*    */   List<Vector3i> forBlockUpdate;
/*    */   Vector3i blockPosition;
/*    */   
/*    */   @Generated
/* 11 */   public BlockPrediction(List<Vector3i> forBlockUpdate, Vector3i blockPosition, int originalBlockId, Vector3d playerPosition) { this.forBlockUpdate = forBlockUpdate; this.blockPosition = blockPosition; this.originalBlockId = originalBlockId; this.playerPosition = playerPosition; } int originalBlockId; Vector3d playerPosition; @Generated
/*    */   public void setForBlockUpdate(List<Vector3i> forBlockUpdate) {
/* 13 */     this.forBlockUpdate = forBlockUpdate; } @Generated public void setBlockPosition(Vector3i blockPosition) { this.blockPosition = blockPosition; } @Generated public void setOriginalBlockId(int originalBlockId) { this.originalBlockId = originalBlockId; } @Generated public void setPlayerPosition(Vector3d playerPosition) { this.playerPosition = playerPosition; }
/*    */   @Generated
/* 15 */   public List<Vector3i> getForBlockUpdate() { return this.forBlockUpdate; } @Generated
/* 16 */   public Vector3i getBlockPosition() { return this.blockPosition; } @Generated
/* 17 */   public int getOriginalBlockId() { return this.originalBlockId; } @Generated
/* 18 */   public Vector3d getPlayerPosition() { return this.playerPosition; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\BlockPrediction.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */