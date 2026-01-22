/*    */ package ac.grim.grimac.shaded.incendo.cloud.services.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.services.State;
/*    */ import java.util.function.Consumer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public interface ConsumerService<Context>
/*    */   extends SideEffectService<Context>, Consumer<Context>
/*    */ {
/*    */   static void interrupt() throws PipeBurst {
/* 50 */     throw new PipeBurst();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   default State handle(Context context) {
/*    */     try {
/* 57 */       accept(context);
/* 58 */     } catch (PipeBurst burst) {
/* 59 */       return State.ACCEPTED;
/*    */     } 
/* 61 */     return State.REJECTED;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void accept(Context paramContext);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static final class PipeBurst
/*    */     extends RuntimeException
/*    */   {
/*    */     private PipeBurst() {}
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public synchronized Throwable fillInStackTrace() {
/* 82 */       return this;
/*    */     }
/*    */ 
/*    */     
/*    */     public synchronized Throwable initCause(Throwable cause) {
/* 87 */       return this;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\services\type\ConsumerService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */