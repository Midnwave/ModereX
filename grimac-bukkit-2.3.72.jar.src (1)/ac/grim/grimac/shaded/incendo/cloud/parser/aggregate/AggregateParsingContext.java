/*    */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.MutableCloudKeyContainer;
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
/*    */ public interface AggregateParsingContext<C>
/*    */   extends MutableCloudKeyContainer
/*    */ {
/*    */   static <C> AggregateParsingContext<C> argumentContext(AggregateParser<C, ?> parser) {
/* 50 */     return new AggregateParsingContextImpl<>(parser);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParsingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */