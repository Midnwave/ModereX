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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public class InvalidSyntaxException
/*    */   extends CommandParseException
/*    */ {
/*    */   private final String correctSyntax;
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public InvalidSyntaxException(String correctSyntax, Object commandSender, List<CommandComponent<?>> currentChain) {
/* 53 */     super(commandSender, currentChain);
/* 54 */     this.correctSyntax = correctSyntax;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String correctSyntax() {
/* 63 */     return this.correctSyntax;
/*    */   }
/*    */ 
/*    */   
/*    */   public final String getMessage() {
/* 68 */     return String.format("Invalid command syntax. Correct syntax is: %s", new Object[] { this.correctSyntax });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\InvalidSyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */