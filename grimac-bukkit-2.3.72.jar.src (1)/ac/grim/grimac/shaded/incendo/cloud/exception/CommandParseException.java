/*    */ package ac.grim.grimac.shaded.incendo.cloud.exception;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*    */ import java.util.Collections;
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
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public class CommandParseException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private final Object commandSender;
/*    */   private final List<CommandComponent<?>> currentChain;
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   protected CommandParseException(Object commandSender, List<CommandComponent<?>> currentChain) {
/* 53 */     this.commandSender = commandSender;
/* 54 */     this.currentChain = currentChain;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object commandSender() {
/* 63 */     return this.commandSender;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<CommandComponent<?>> currentChain() {
/* 72 */     return Collections.unmodifiableList(this.currentChain);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\CommandParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */