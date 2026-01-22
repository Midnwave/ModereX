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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ final class StandardHelpHandlerFactory<C>
/*    */   implements HelpHandlerFactory<C>
/*    */ {
/*    */   private final CommandManager<C> commandManager;
/*    */   
/*    */   StandardHelpHandlerFactory(CommandManager<C> commandManager) {
/* 36 */     this.commandManager = commandManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public HelpHandler<C> createHelpHandler(CommandPredicate<C> filter) {
/* 41 */     return new StandardHelpHandler<>(this.commandManager, filter);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\StandardHelpHandlerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */