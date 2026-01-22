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
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public final class NoSuchCommandException
/*    */   extends CommandParseException
/*    */ {
/*    */   private final String suppliedCommand;
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public NoSuchCommandException(Object commandSender, List<CommandComponent<?>> currentChain, String command) {
/* 54 */     super(commandSender, currentChain);
/* 55 */     this.suppliedCommand = command;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 61 */     StringBuilder builder = new StringBuilder();
/* 62 */     for (CommandComponent<?> commandComponent : currentChain()) {
/* 63 */       if (commandComponent == null) {
/*    */         continue;
/*    */       }
/* 66 */       builder.append(" ").append(commandComponent.name());
/*    */     } 
/* 68 */     return String.format("Unrecognized command input '%s' following chain%s", new Object[] { this.suppliedCommand, builder.toString() });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String suppliedCommand() {
/* 77 */     return this.suppliedCommand;
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized Throwable fillInStackTrace() {
/* 82 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public synchronized Throwable initCause(Throwable cause) {
/* 87 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\NoSuchCommandException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */