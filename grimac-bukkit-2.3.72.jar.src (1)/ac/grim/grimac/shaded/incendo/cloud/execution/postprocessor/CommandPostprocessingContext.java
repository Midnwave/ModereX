/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution.postprocessor;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ @Immutable
/*    */ public interface CommandPostprocessingContext<C>
/*    */ {
/*    */   static <C> CommandPostprocessingContext<C> of(CommandContext<C> commandContext, Command<C> command) {
/* 55 */     return CommandPostprocessingContextImpl.of(commandContext, command);
/*    */   }
/*    */   
/*    */   CommandContext<C> commandContext();
/*    */   
/*    */   Command<C> command();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\postprocessor\CommandPostprocessingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */