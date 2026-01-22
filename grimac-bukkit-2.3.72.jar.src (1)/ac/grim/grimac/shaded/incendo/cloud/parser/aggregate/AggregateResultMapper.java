/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public interface AggregateResultMapper<C, O>
/*    */ {
/*    */   CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> paramCommandContext, AggregateParsingContext<C> paramAggregateParsingContext);
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   public static interface DirectSuccessMapper<C, O>
/*    */     extends AggregateResultMapper<C, O>
/*    */   {
/*    */     O mapSuccess(CommandContext<C> param1CommandContext, AggregateParsingContext<C> param1AggregateParsingContext);
/*    */     
/*    */     default CompletableFuture<ArgumentParseResult<O>> map(CommandContext<C> commandContext, AggregateParsingContext<C> context) {
/* 74 */       return ArgumentParseResult.successFuture(mapSuccess(commandContext, context));
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateResultMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */