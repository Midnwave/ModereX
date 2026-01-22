/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeFactory;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Triplet;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface AggregateParser<C, O>
/*     */   extends ArgumentParser.FutureArgumentParser<C, O>, ParserDescriptor<C, O>
/*     */ {
/*     */   static <C> AggregateParserBuilder<C> builder() {
/*  76 */     return new AggregateParserBuilder<>();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <C, U, V> AggregateParserPairBuilder<C, U, V, Pair<U, V>> pairBuilder(String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser) {
/*  99 */     return new AggregateParserPairBuilder<>(
/* 100 */         CommandComponent.builder(firstName, firstParser).build(), 
/* 101 */         CommandComponent.builder(secondName, secondParser).build(), 
/* 102 */         AggregateParserPairBuilder.defaultMapper(), 
/* 103 */         TypeToken.get(TypeFactory.parameterizedClass(Pair.class, new Type[] {
/*     */               
/* 105 */               GenericTypeReflector.box(firstParser.valueType().getType()), 
/* 106 */               GenericTypeReflector.box(secondParser.valueType().getType())
/*     */             })));
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
/*     */   static <C, U, V, Z> AggregateParserTripletBuilder<C, U, V, Z, Triplet<U, V, Z>> tripletBuilder(String firstName, ParserDescriptor<C, U> firstParser, String secondName, ParserDescriptor<C, V> secondParser, String thirdName, ParserDescriptor<C, Z> thirdParser) {
/* 136 */     return new AggregateParserTripletBuilder<>(
/* 137 */         CommandComponent.builder(firstName, firstParser).build(), 
/* 138 */         CommandComponent.builder(secondName, secondParser).build(), 
/* 139 */         CommandComponent.builder(thirdName, thirdParser).build(), 
/* 140 */         AggregateParserTripletBuilder.defaultMapper(), 
/* 141 */         TypeToken.get(TypeFactory.parameterizedClass(Triplet.class, new Type[] {
/*     */               
/* 143 */               GenericTypeReflector.box(firstParser.valueType().getType()), 
/* 144 */               GenericTypeReflector.box(secondParser.valueType().getType()), 
/* 145 */               GenericTypeReflector.box(thirdParser.valueType().getType())
/*     */             })));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default CompletableFuture<ArgumentParseResult<O>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 170 */     AggregateParsingContext<C> aggregateParsingContext = AggregateParsingContext.argumentContext(this);
/* 171 */     CompletableFuture<ArgumentParseResult<Object>> future = CompletableFuture.completedFuture(null);
/* 172 */     for (CommandComponent<C> component : components()) {
/*     */       
/* 174 */       future = future.thenCompose(result -> {
/*     */             if (result != null && result.failure().isPresent()) {
/*     */               return ArgumentParseResult.failureFuture(result.failure().get());
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             commandInput.skipWhitespace(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             return commandInput.isEmpty() ? ArgumentParseResult.failureFuture((Throwable)new AggregateParseException(commandContext, component)) : component.parser().parseFuture(commandContext, commandInput).thenApply(());
/*     */           });
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     return future.thenCompose(result -> 
/* 206 */         (result != null && result.failure().isPresent()) ? result.asFuture() : mapper().map(commandContext, aggregateParsingContext));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default SuggestionProvider<C> suggestionProvider() {
/* 215 */     return new AggregateSuggestionProvider<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   default ArgumentParser<C, O> parser() {
/* 220 */     return (ArgumentParser<C, O>)this;
/*     */   }
/*     */ 
/*     */   
/*     */   List<CommandComponent<C>> components();
/*     */   
/*     */   AggregateResultMapper<C, O> mapper();
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class AggregateParseException
/*     */     extends ParserException
/*     */   {
/*     */     private AggregateParseException(CommandContext<?> context, String input, CommandComponent<?> component, Throwable cause) {
/* 233 */       super(cause, AggregateParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_COMPONENT_FAILURE, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 238 */             CaptionVariable.of("input", input), 
/* 239 */             CaptionVariable.of("component", component.name()), 
/* 240 */             CaptionVariable.of("failure", cause.getMessage())
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private AggregateParseException(CommandContext<?> context, CommandComponent<?> component) {
/* 248 */       super(AggregateParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_AGGREGATE_MISSING_INPUT, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 252 */             CaptionVariable.of("component", component.name())
/*     */           });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */