/*    */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public interface Suggestions<C, S extends Suggestion>
/*    */ {
/*    */   CommandContext<C> commandContext();
/*    */   
/*    */   List<S> list();
/*    */   
/*    */   CommandInput commandInput();
/*    */   
/*    */   @API(status = API.Status.INTERNAL)
/*    */   static <C, S extends Suggestion> Suggestions<C, S> create(CommandContext<C> ctx, List<S> list, CommandInput commandInput) {
/* 76 */     return SuggestionsImpl.of(ctx, list, commandInput);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\Suggestions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */