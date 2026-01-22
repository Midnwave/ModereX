/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.EitherParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProviderHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.Either;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.BiFunction;
/*     */ import org.apiguardian.api.API;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @FunctionalInterface
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface ArgumentParser<C, T>
/*     */   extends SuggestionProviderHolder<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   default CompletableFuture<ArgumentParseResult<T>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 108 */     return CompletableFuture.completedFuture(parse(commandContext, commandInput));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default <O> FutureArgumentParser<C, O> flatMap(MappedArgumentParser.Mapper<C, T, O> mapper) {
/* 120 */     return new MappedArgumentParserImpl<>(this, Objects.<MappedArgumentParser.Mapper<C, T, O>>requireNonNull(mapper, "mapper"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default <O> FutureArgumentParser<C, O> flatMapSuccess(BiFunction<CommandContext<C>, T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
/* 135 */     Objects.requireNonNull(mapper, "mapper");
/* 136 */     return flatMap((ctx, result) -> result.flatMapSuccessFuture(()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default <O> FutureArgumentParser<C, O> mapSuccess(BiFunction<CommandContext<C>, T, CompletableFuture<O>> mapper) {
/* 151 */     Objects.requireNonNull(mapper, "mapper");
/* 152 */     return flatMap((ctx, result) -> result.mapSuccessFuture(()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default SuggestionProvider<C> suggestionProvider() {
/* 166 */     if (this instanceof SuggestionProvider) {
/* 167 */       return (SuggestionProvider)this;
/*     */     }
/* 169 */     return SuggestionProvider.noSuggestions();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <C, U, V> ParserDescriptor<C, Either<U, V>> firstOf(ParserDescriptor<C, U> primary, ParserDescriptor<C, V> fallback) {
/* 187 */     return EitherParser.eitherParser(primary, fallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ArgumentParseResult<T> parse(CommandContext<C> paramCommandContext, CommandInput paramCommandInput);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @FunctionalInterface
/*     */   @API(status = API.Status.STABLE)
/*     */   public static interface FutureArgumentParser<C, T>
/*     */     extends ArgumentParser<C, T>
/*     */   {
/*     */     default ArgumentParseResult<T> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 207 */       throw new UnsupportedOperationException("parse should not be called on a FutureArgumentParser. Call parseFuture instead.");
/*     */     }
/*     */     
/*     */     CompletableFuture<ArgumentParseResult<T>> parseFuture(CommandContext<C> param1CommandContext, CommandInput param1CommandInput);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ArgumentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */