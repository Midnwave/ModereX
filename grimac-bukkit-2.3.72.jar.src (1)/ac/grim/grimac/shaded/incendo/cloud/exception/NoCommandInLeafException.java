/*    */ package ac.grim.grimac.shaded.incendo.cloud.exception;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
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
/*    */ public final class NoCommandInLeafException
/*    */   extends IllegalStateException
/*    */ {
/*    */   private final CommandComponent<?> commandComponent;
/*    */   
/*    */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */   public NoCommandInLeafException(CommandComponent<?> commandComponent) {
/* 48 */     super(String.format("Leaf node '%s' does not have associated owning command", new Object[] { commandComponent.name() }));
/* 49 */     this.commandComponent = commandComponent;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommandComponent<?> commandComponent() {
/* 58 */     return this.commandComponent;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\NoCommandInLeafException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */