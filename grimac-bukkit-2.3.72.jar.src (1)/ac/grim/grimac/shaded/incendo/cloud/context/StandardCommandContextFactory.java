/*    */ package ac.grim.grimac.shaded.incendo.cloud.context;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ public final class StandardCommandContextFactory<C>
/*    */   implements CommandContextFactory<C>
/*    */ {
/*    */   private final CommandManager<C> commandManager;
/*    */   
/*    */   public StandardCommandContextFactory(CommandManager<C> commandManager) {
/* 41 */     this.commandManager = commandManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public CommandContext<C> create(boolean suggestions, C sender) {
/* 46 */     return new CommandContext<>(suggestions, sender, this.commandManager);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\context\StandardCommandContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */