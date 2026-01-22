/*    */ package ac.grim.grimac.shaded.snakeyaml.inspector;
/*    */ 
/*    */ import ac.grim.grimac.shaded.snakeyaml.nodes.Tag;
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
/*    */ public final class UnTrustedTagInspector
/*    */   implements TagInspector
/*    */ {
/*    */   public boolean isGlobalTagAllowed(Tag tag) {
/* 32 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\inspector\UnTrustedTagInspector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */