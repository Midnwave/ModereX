/*    */ package ac.grim.grimac.shaded.incendo.cloud.exception.handling;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
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
/*    */ @API(status = API.Status.INTERNAL)
/*    */ public final class ExceptionContextFactory<C>
/*    */ {
/*    */   private final ExceptionController<C> controller;
/*    */   
/*    */   public ExceptionContextFactory(ExceptionController<C> controller) {
/* 41 */     this.controller = controller;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <T extends Throwable> ExceptionContext<C, T> createContext(CommandContext<C> context, T exception) {
/* 56 */     return new ExceptionContext.ExceptionContextImpl<>(exception, context, this.controller);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\handling\ExceptionContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */