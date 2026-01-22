/*    */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpQuery;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpRenderer;
/*    */ import org.apiguardian.api.API;
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
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface HelpQueryResult<C>
/*    */ {
/*    */   HelpQuery<C> query();
/*    */   
/*    */   default void render(HelpRenderer<C> renderer) {
/* 52 */     renderer.render(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\HelpQueryResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */