/*    */ package ac.grim.grimac.shaded.incendo.cloud.help;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface HelpHandlerFactory<C>
/*    */ {
/*    */   static <C> HelpHandlerFactory<C> standard(CommandManager<C> commandManager) {
/* 43 */     return new StandardHelpHandlerFactory<>(commandManager);
/*    */   }
/*    */   
/*    */   HelpHandler<C> createHelpHandler(CommandPredicate<C> paramCommandPredicate);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\HelpHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */