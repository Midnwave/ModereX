/*    */ package ac.grim.grimac.shaded.incendo.cloud.exception;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.permission.PermissionResult;
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
/*    */ 
/*    */ @API(status = API.Status.STABLE)
/*    */ public class NoPermissionException
/*    */   extends CommandParseException
/*    */ {
/*    */   private final PermissionResult result;
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public NoPermissionException(PermissionResult permissionResult, Object commandSender, List<CommandComponent<?>> currentChain) {
/* 57 */     super(commandSender, currentChain);
/* 58 */     if (permissionResult.allowed()) {
/* 59 */       throw new IllegalArgumentException("Provided permission result was one that succeeded instead of failed");
/*    */     }
/* 61 */     this.result = permissionResult;
/*    */   }
/*    */ 
/*    */   
/*    */   public final String getMessage() {
/* 66 */     return String.format("Missing permission '%s'", new Object[] { missingPermission() });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   public Permission missingPermission() {
/* 76 */     return this.result.permission();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   public PermissionResult permissionResult() {
/* 86 */     return this.result;
/*    */   }
/*    */ 
/*    */   
/*    */   public final synchronized Throwable fillInStackTrace() {
/* 91 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public final synchronized Throwable initCause(Throwable cause) {
/* 96 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\NoPermissionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */