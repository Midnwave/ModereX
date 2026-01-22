/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ public final class HitData extends Record {
/*    */   private final Vector3i position;
/*    */   private final Vector3dm blockHitLocation;
/*    */   private final BlockFace closestDirection;
/*    */   private final WrappedBlockState state;
/*    */   
/*  9 */   public HitData(Vector3i position, Vector3dm blockHitLocation, BlockFace closestDirection, WrappedBlockState state) { this.position = position; this.blockHitLocation = blockHitLocation; this.closestDirection = closestDirection; this.state = state; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/data/HitData;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #9	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  9 */     //   0	7	0	this	Lac/grim/grimac/utils/data/HitData; } public Vector3i position() { return this.position; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/data/HitData;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #9	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/HitData; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/data/HitData;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #9	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/data/HitData;
/*  9 */     //   0	8	1	o	Ljava/lang/Object; } public Vector3dm blockHitLocation() { return this.blockHitLocation; } public BlockFace closestDirection() { return this.closestDirection; } public WrappedBlockState state() { return this.state; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Vector3d getRelativeBlockHitLocation() {
/* 16 */     return new Vector3d(this.blockHitLocation.getX() - this.position.getX(), this.blockHitLocation.getY() - this.position.getY(), this.blockHitLocation.getZ() - this.position.getZ());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\HitData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */