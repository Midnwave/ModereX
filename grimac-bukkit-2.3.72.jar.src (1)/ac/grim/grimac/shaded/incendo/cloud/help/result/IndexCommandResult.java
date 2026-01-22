/*    */ package ac.grim.grimac.shaded.incendo.cloud.help.result;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.help.HelpQuery;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.apiguardian.api.API;
/*    */ import org.immutables.value.Value.Immutable;
/*    */ import org.immutables.value.Value.Parameter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public interface IndexCommandResult<C>
/*    */   extends HelpQueryResult<C>, Iterable<CommandEntry<C>>
/*    */ {
/*    */   static <C> IndexCommandResult<C> of(HelpQuery<C> query, List<CommandEntry<C>> entries) {
/* 60 */     return IndexCommandResultImpl.of(query, entries);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   HelpQuery<C> query();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   List<CommandEntry<C>> entries();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Parameter(false)
/*    */   default boolean isEmpty() {
/* 83 */     return entries().isEmpty();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Parameter(false)
/*    */   default Iterator<CommandEntry<C>> iterator() {
/* 94 */     return entries().iterator();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\help\result\IndexCommandResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */