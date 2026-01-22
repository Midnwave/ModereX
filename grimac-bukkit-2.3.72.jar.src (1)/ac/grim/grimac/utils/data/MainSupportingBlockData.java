/*    */ package ac.grim.grimac.utils.data;
/*    */ public final class MainSupportingBlockData extends Record {
/*    */   @Nullable
/*    */   private final Vector3i blockPos;
/*    */   private final boolean onGround;
/*    */   
/*  7 */   public MainSupportingBlockData(@Nullable Vector3i blockPos, boolean onGround) { this.blockPos = blockPos; this.onGround = onGround; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/data/MainSupportingBlockData;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #7	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  7 */     //   0	7	0	this	Lac/grim/grimac/utils/data/MainSupportingBlockData; } @Nullable public Vector3i blockPos() { return this.blockPos; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/data/MainSupportingBlockData;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #7	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/MainSupportingBlockData; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/data/MainSupportingBlockData;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #7	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/data/MainSupportingBlockData;
/*  7 */     //   0	8	1	o	Ljava/lang/Object; } public boolean onGround() { return this.onGround; }
/*    */    @Contract(pure = true)
/*    */   public boolean lastOnGroundAndNoBlock() {
/* 10 */     return (this.blockPos == null && this.onGround);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\MainSupportingBlockData.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */