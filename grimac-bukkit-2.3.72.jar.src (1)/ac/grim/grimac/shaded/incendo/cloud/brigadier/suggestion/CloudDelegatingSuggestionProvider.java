/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*    */ import com.mojang.brigadier.context.CommandContext;
/*    */ import com.mojang.brigadier.exceptions.CommandSyntaxException;
/*    */ import com.mojang.brigadier.suggestion.SuggestionProvider;
/*    */ import com.mojang.brigadier.suggestion.Suggestions;
/*    */ import com.mojang.brigadier.suggestion.SuggestionsBuilder;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*    */ public final class CloudDelegatingSuggestionProvider<C, S>
/*    */   implements SuggestionProvider<S>
/*    */ {
/*    */   private final BrigadierSuggestionFactory<C, S> brigadierSuggestionFactory;
/*    */   private final CommandNode<C> node;
/*    */   
/*    */   public CloudDelegatingSuggestionProvider(BrigadierSuggestionFactory<C, S> suggestionFactory, CommandNode<C> node) {
/* 59 */     this.brigadierSuggestionFactory = suggestionFactory;
/* 60 */     this.node = node;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) throws CommandSyntaxException {
/* 68 */     return this.brigadierSuggestionFactory.buildSuggestions(context, this.node
/*    */         
/* 70 */         .parent(), builder);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\suggestion\CloudDelegatingSuggestionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */