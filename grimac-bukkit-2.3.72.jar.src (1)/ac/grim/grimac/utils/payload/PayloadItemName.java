/*   */ package ac.grim.grimac.utils.payload;public final class PayloadItemName extends Record { @NotNull
/*   */   private final String itemName;
/*   */   public static final PayloadCodec<PayloadItemName> CODEC;
/*   */   
/* 5 */   public PayloadItemName(@NotNull String itemName) { this.itemName = itemName; } public final String toString() { // Byte code:
/*   */     //   0: aload_0
/*   */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/payload/PayloadItemName;)Ljava/lang/String;
/*   */     //   6: areturn
/*   */     // Line number table:
/*   */     //   Java source line number -> byte code offset
/*   */     //   #5	-> 0
/*   */     // Local variable table:
/*   */     //   start	length	slot	name	descriptor
/* 5 */     //   0	7	0	this	Lac/grim/grimac/utils/payload/PayloadItemName; } @NotNull public String itemName() { return this.itemName; }
/*   */   public final int hashCode() { // Byte code:
/*   */     //   0: aload_0
/*   */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/payload/PayloadItemName;)I
/*   */     //   6: ireturn
/*   */     // Line number table:
/*   */     //   Java source line number -> byte code offset
/*   */     //   #5	-> 0
/*   */     // Local variable table:
/*   */     //   start	length	slot	name	descriptor
/*   */     //   0	7	0	this	Lac/grim/grimac/utils/payload/PayloadItemName; } public final boolean equals(Object o) { // Byte code:
/*   */     //   0: aload_0
/*   */     //   1: aload_1
/*   */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/payload/PayloadItemName;Ljava/lang/Object;)Z
/*   */     //   7: ireturn
/*   */     // Line number table:
/*   */     //   Java source line number -> byte code offset
/*   */     //   #5	-> 0
/*   */     // Local variable table:
/*   */     //   start	length	slot	name	descriptor
/*   */     //   0	8	0	this	Lac/grim/grimac/utils/payload/PayloadItemName;
/* 6 */     //   0	8	1	o	Ljava/lang/Object; } static { CODEC = new PayloadCodec<>(wrapper -> new PayloadItemName(wrapper.readString()), (wrapper, payload) -> wrapper.writeString(payload.itemName)); }
/*   */    }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\payload\PayloadItemName.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */