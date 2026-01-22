/*    */ package ac.grim.grimac.utils.data;public final class KnownInput extends Record { private final boolean forward; private final boolean backward; private final boolean left; private final boolean right; private final boolean jump;
/*    */   private final boolean shift;
/*    */   private final boolean sprint;
/*    */   
/*  5 */   public KnownInput(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean shift, boolean sprint) { this.forward = forward; this.backward = backward; this.left = left; this.right = right; this.jump = jump; this.shift = shift; this.sprint = sprint; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/data/KnownInput;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #5	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*  5 */     //   0	7	0	this	Lac/grim/grimac/utils/data/KnownInput; } public boolean forward() { return this.forward; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/data/KnownInput;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #5	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lac/grim/grimac/utils/data/KnownInput; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/data/KnownInput;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #5	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lac/grim/grimac/utils/data/KnownInput;
/*  5 */     //   0	8	1	o	Ljava/lang/Object; } public boolean backward() { return this.backward; } public boolean left() { return this.left; } public boolean right() { return this.right; } public boolean jump() { return this.jump; } public boolean shift() { return this.shift; } public boolean sprint() { return this.sprint; }
/*    */   
/*  7 */   public static final KnownInput DEFAULT = new KnownInput(false, false, false, false, false, false, false);
/*    */   
/*    */   @Contract(pure = true)
/*    */   public boolean moving() {
/* 11 */     return (this.forward || this.backward || this.left || this.right || this.jump);
/*    */   } }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\KnownInput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */