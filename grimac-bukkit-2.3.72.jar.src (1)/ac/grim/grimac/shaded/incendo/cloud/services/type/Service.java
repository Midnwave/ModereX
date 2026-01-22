/*    */ package ac.grim.grimac.shaded.incendo.cloud.services.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.ExecutionOrder;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.PipelineException;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public interface Service<Context, Result>
/*    */   extends Function<Context, Result>
/*    */ {
/*    */   Result handle(Context paramContext) throws Exception;
/*    */   
/*    */   default Result apply(Context context) {
/*    */     try {
/* 60 */       return handle(context);
/* 61 */     } catch (Exception exception) {
/* 62 */       throw new PipelineException(exception);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default ExecutionOrder order() {
/* 73 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\type\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */