/*    */ package ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
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
/*    */ public interface CommandPreprocessingContext<C>
/*    */ {
/*    */   static <C> CommandPreprocessingContext<C> of(CommandContext<C> commandContext, CommandInput commandInput) {
/* 55 */     return CommandPreprocessingContextImpl.of(commandContext, commandInput);
/*    */   }
/*    */   
/*    */   CommandContext<C> commandContext();
/*    */   
/*    */   CommandInput commandInput();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\execution\preprocessor\CommandPreprocessingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */