/*    */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.Command;
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Immutable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ @Immutable
/*    */ public interface CommandEntry<C>
/*    */   extends Comparable<CommandEntry<C>>
/*    */ {
/*    */   static <C> CommandEntry<C> of(Command<C> command, String syntax) {
/* 49 */     return CommandEntryImpl.of(command, syntax);
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
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default int compareTo(CommandEntry<C> other) {
/* 68 */     return syntax().compareTo(other.syntax());
/*    */   }
/*    */   
/*    */   Command<C> command();
/*    */   
/*    */   String syntax();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\CommandEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */