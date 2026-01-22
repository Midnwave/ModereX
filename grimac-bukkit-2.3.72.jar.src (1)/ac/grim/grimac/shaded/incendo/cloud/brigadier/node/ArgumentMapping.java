/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.node;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.SuggestionsType;
/*    */ import com.mojang.brigadier.arguments.ArgumentType;
/*    */ import com.mojang.brigadier.suggestion.SuggestionProvider;
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
/*    */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*    */ @Immutable
/*    */ interface ArgumentMapping<S>
/*    */ {
/*    */   ArgumentType<?> argumentType();
/*    */   
/*    */   default SuggestionsType suggestionsType() {
/* 43 */     return SuggestionsType.BRIGADIER_SUGGESTIONS;
/*    */   }
/*    */   
/*    */   SuggestionProvider<S> suggestionProvider();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\node\ArgumentMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */