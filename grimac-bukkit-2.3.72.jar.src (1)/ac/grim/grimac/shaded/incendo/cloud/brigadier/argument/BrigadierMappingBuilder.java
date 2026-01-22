/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.argument;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*    */ import com.mojang.brigadier.arguments.ArgumentType;
/*    */ import com.mojang.brigadier.suggestion.SuggestionProvider;
/*    */ import java.util.Objects;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface BrigadierMappingBuilder<K extends ArgumentParser<?, ?>, S>
/*    */ {
/*    */   BrigadierMappingBuilder<K, S> toConstant(ArgumentType<?> paramArgumentType);
/*    */   
/*    */   BrigadierMappingBuilder<K, S> to(Function<K, ? extends ArgumentType<?>> paramFunction);
/*    */   
/*    */   BrigadierMappingBuilder<K, S> nativeSuggestions();
/*    */   
/*    */   BrigadierMappingBuilder<K, S> cloudSuggestions();
/*    */   
/*    */   default BrigadierMappingBuilder<K, S> suggestedByConstant(SuggestionProvider<S> provider) {
/* 93 */     Objects.requireNonNull(provider, "provider");
/* 94 */     return suggestedBy((argument, useCloud) -> provider);
/*    */   }
/*    */   
/*    */   BrigadierMappingBuilder<K, S> suggestedBy(SuggestionProviderSupplier<K, S> paramSuggestionProviderSupplier);
/*    */   
/*    */   BrigadierMapping<?, K, S> build();
/*    */   
/*    */   @FunctionalInterface
/*    */   public static interface SuggestionProviderSupplier<K extends ArgumentParser<?, ?>, S> {
/*    */     SuggestionProvider<? super S> provide(K param1K, SuggestionProvider<S> param1SuggestionProvider);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\argument\BrigadierMappingBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */