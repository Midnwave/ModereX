/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.legacyimpl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface NBTLegacyHoverEventSerializer
/*    */   extends LegacyHoverEventSerializer
/*    */ {
/*    */   @NotNull
/*    */   static LegacyHoverEventSerializer get() {
/* 43 */     return NBTLegacyHoverEventSerializerImpl.INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\serializer\json\legacyimpl\NBTLegacyHoverEventSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */