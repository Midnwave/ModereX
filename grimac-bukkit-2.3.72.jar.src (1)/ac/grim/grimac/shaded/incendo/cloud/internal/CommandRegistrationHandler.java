/*    */ package ac.grim.grimac.shaded.incendo.cloud.internal;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
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
/*    */ @FunctionalInterface
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface CommandRegistrationHandler<C>
/*    */ {
/*    */   static <C> CommandRegistrationHandler<C> nullCommandRegistrationHandler() {
/* 49 */     return new NullCommandRegistrationHandler<>();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean registerCommand(Command<C> paramCommand);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   default void unregisterRootCommand(CommandComponent<C> rootCommand) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public static final class NullCommandRegistrationHandler<C>
/*    */     implements CommandRegistrationHandler<C>
/*    */   {
/*    */     private NullCommandRegistrationHandler() {}
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public boolean registerCommand(Command<C> command) {
/* 79 */       return true;
/*    */     }
/*    */     
/*    */     public void unregisterRootCommand(CommandComponent<C> rootCommand) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\internal\CommandRegistrationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */