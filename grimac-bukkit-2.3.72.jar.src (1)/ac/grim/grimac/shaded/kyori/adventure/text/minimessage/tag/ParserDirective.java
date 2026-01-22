/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @NonExtendable
/*    */ public interface ParserDirective
/*    */   extends Tag
/*    */ {
/* 44 */   public static final Tag RESET = new ParserDirective()
/*    */     {
/*    */       public String toString() {
/* 47 */         return "ParserDirective.RESET";
/*    */       }
/*    */     };
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\tag\ParserDirective.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */