/*    */ package ac.grim.grimac.shaded.incendo.cloud.help;
/*    */ 
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Immutable;
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
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ @Immutable
/*    */ public interface HelpQuery<C>
/*    */ {
/*    */   static <C> HelpQuery<C> of(C sender, String query) {
/* 48 */     return HelpQueryImpl.of(sender, query);
/*    */   }
/*    */   
/*    */   C sender();
/*    */   
/*    */   String query();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\HelpQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */