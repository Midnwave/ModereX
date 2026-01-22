/*    */ package ac.grim.grimac.utils.chunks;public final class Column extends Record { private final int x;
/*    */   private final int z;
/*    */   private final BaseChunk[] chunks;
/*    */   private final int transaction;
/*    */   
/*  6 */   public Column(int x, int z, BaseChunk[] chunks, int transaction) { this.x = x; this.z = z; this.chunks = chunks; this.transaction = transaction; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/chunks/Column;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #6	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  6 */     //   0	7	0	this	Lac/grim/grimac/utils/chunks/Column; } public int x() { return this.x; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/chunks/Column;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #6	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/chunks/Column; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/chunks/Column;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #6	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/chunks/Column;
/*  6 */     //   0	8	1	o	Ljava/lang/Object; } public int z() { return this.z; } public BaseChunk[] chunks() { return this.chunks; } public int transaction() { return this.transaction; }
/*    */ 
/*    */ 
/*    */   
/*    */   public void mergeChunks(BaseChunk[] toMerge) {
/* 11 */     for (int i = 0; i < 16; i++) {
/* 12 */       if (toMerge[i] != null) this.chunks[i] = toMerge[i]; 
/*    */     } 
/*    */   } }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\chunks\Column.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */