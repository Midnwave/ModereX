/*   */ package ac.grim.grimac.utils.payload;
/*   */ 
/*   */ 
/*   */ public final class PayloadBookEdit extends Record {
/*   */   private final ItemStack itemStack;
/*   */   public static final PayloadCodec<PayloadBookEdit> CODEC;
/*   */   
/* 8 */   public PayloadBookEdit(ItemStack itemStack) { this.itemStack = itemStack; } public final String toString() { // Byte code:
/*   */     //   0: aload_0
/*   */     //   1: <illegal opcode> toString : (Lac/grim/grimac/utils/payload/PayloadBookEdit;)Ljava/lang/String;
/*   */     //   6: areturn
/*   */     // Line number table:
/*   */     //   Java source line number -> byte code offset
/*   */     //   #8	-> 0
/*   */     // Local variable table:
/*   */     //   start	length	slot	name	descriptor
/* 8 */     //   0	7	0	this	Lac/grim/grimac/utils/payload/PayloadBookEdit; } public ItemStack itemStack() { return this.itemStack; }
/*   */   public final int hashCode() { // Byte code:
/*   */     //   0: aload_0
/*   */     //   1: <illegal opcode> hashCode : (Lac/grim/grimac/utils/payload/PayloadBookEdit;)I
/*   */     //   6: ireturn
/*   */     // Line number table:
/*   */     //   Java source line number -> byte code offset
/*   */     //   #8	-> 0
/*   */     // Local variable table:
/*   */     //   start	length	slot	name	descriptor
/*   */     //   0	7	0	this	Lac/grim/grimac/utils/payload/PayloadBookEdit; } public final boolean equals(Object o) { // Byte code:
/*   */     //   0: aload_0
/*   */     //   1: aload_1
/*   */     //   2: <illegal opcode> equals : (Lac/grim/grimac/utils/payload/PayloadBookEdit;Ljava/lang/Object;)Z
/*   */     //   7: ireturn
/*   */     // Line number table:
/*   */     //   Java source line number -> byte code offset
/*   */     //   #8	-> 0
/*   */     // Local variable table:
/*   */     //   start	length	slot	name	descriptor
/*   */     //   0	8	0	this	Lac/grim/grimac/utils/payload/PayloadBookEdit;
/* 9 */     //   0	8	1	o	Ljava/lang/Object; } static { CODEC = new PayloadCodec<>(wrapper -> new PayloadBookEdit(wrapper.readItemStack()), (wrapper, payload) -> wrapper.writeItemStack(payload.itemStack)); }
/*   */ 
/*   */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\payload\PayloadBookEdit.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */