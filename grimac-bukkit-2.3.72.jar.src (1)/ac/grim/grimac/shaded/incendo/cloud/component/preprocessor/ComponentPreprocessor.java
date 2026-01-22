/*    */ package ac.grim.grimac.shaded.incendo.cloud.component.preprocessor;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*    */ import java.util.Objects;
/*    */ import java.util.function.BiFunction;
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
/*    */ @FunctionalInterface
/*    */ public interface ComponentPreprocessor<C>
/*    */ {
/*    */   static <C> ComponentPreprocessor<C> wrap(BiFunction<CommandContext<C>, CommandInput, ArgumentParseResult<Boolean>> function) {
/* 48 */     Objects.requireNonNull(function); return function::apply;
/*    */   }
/*    */   
/*    */   ArgumentParseResult<Boolean> preprocess(CommandContext<C> paramCommandContext, CommandInput paramCommandInput);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\component\preprocessor\ComponentPreprocessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */