/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ public final class Pair<A, B> extends Record {
/*    */   private final A first;
/*    */   private final B second;
/*    */   
/*    */   public final String toString() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/data/Pair;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #21	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/Pair;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/Pair<TA;TB;>;
/*    */   }
/*    */   
/*    */   public final int hashCode() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/data/Pair;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #21	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/Pair;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/Pair<TA;TB;>;
/*    */   }
/*    */   
/*    */   public final boolean equals(Object o) {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/data/Pair;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #21	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/data/Pair;
/*    */     //   0	8	1	o	Ljava/lang/Object;
/*    */     // Local variable type table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/data/Pair<TA;TB;>;
/*    */   }
/*    */   
/*    */   public Pair(A first, B second)
/*    */   {
/* 21 */     this.first = first; this.second = second; } public A first() { return this.first; } public B second() { return this.second; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\Pair.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */