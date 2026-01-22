/*    */ package ac.grim.grimac.command;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class null
/*    */   implements SuggestionProvider<Sender>
/*    */ {
/*    */   public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext context, CommandInput input) {
/* 21 */     return CompletableFuture.completedFuture(suggestions);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\command\CommandUtils$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */