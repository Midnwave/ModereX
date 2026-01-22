/*    */ package ac.grim.grimac.shaded.incendo.cloud.exception;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*    */ import java.util.List;
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
/*    */ @API(status = API.Status.STABLE)
/*    */ public class ArgumentParseException
/*    */   extends CommandParseException
/*    */ {
/*    */   private final Throwable cause;
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public ArgumentParseException(Throwable throwable, Object commandSender, List<CommandComponent<?>> currentChain) {
/* 49 */     super(commandSender, currentChain);
/* 50 */     this.cause = throwable;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized Throwable getCause() {
/* 60 */     return this.cause;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\ArgumentParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */