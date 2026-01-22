/*    */ package ac.grim.grimac.shaded.incendo.cloud.help;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import java.util.function.Predicate;
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
/*    */ @FunctionalInterface
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface CommandPredicate<C>
/*    */   extends Predicate<Command<C>>
/*    */ {
/*    */   static <C> CommandPredicate<C> acceptAll() {
/* 42 */     return cmd -> true;
/*    */   }
/*    */   
/*    */   boolean test(Command<C> paramCommand);
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\CommandPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */