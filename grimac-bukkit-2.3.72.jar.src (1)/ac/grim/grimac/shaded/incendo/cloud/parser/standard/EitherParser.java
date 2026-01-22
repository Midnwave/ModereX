/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeFactory;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.Either;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.stream.StreamSupport;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class EitherParser<C, U, V>
/*     */   implements ArgumentParser.FutureArgumentParser<C, Either<U, V>>, SuggestionProvider<C>
/*     */ {
/*     */   private final ParserDescriptor<C, U> primary;
/*     */   private final ParserDescriptor<C, V> fallback;
/*     */   
/*     */   public static <C, U, V> ParserDescriptor<C, Either<U, V>> eitherParser(ParserDescriptor<C, U> primary, ParserDescriptor<C, V> fallback) {
/*  75 */     return ParserDescriptor.of((ArgumentParser)new EitherParser<>(primary, fallback), 
/*     */         
/*  77 */         TypeToken.get(
/*  78 */           TypeFactory.parameterizedClass(Either.class, new Type[] {
/*     */               
/*  80 */               primary.valueType().getType(), fallback
/*  81 */               .valueType().getType()
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
/*     */   public EitherParser(ParserDescriptor<C, U> primary, ParserDescriptor<C, V> fallback) {
/*  97 */     this.primary = Objects.<ParserDescriptor<C, U>>requireNonNull(primary, "primary");
/*  98 */     this.fallback = Objects.<ParserDescriptor<C, V>>requireNonNull(fallback, "fallback");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserDescriptor<C, U> primary() {
/* 107 */     return this.primary;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserDescriptor<C, V> fallback() {
/* 116 */     return this.fallback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<ArgumentParseResult<Either<U, V>>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 124 */     String input = commandInput.peekString();
/* 125 */     int originalCursor = commandInput.cursor();
/*     */     
/* 127 */     return this.primary.parser().parseFuture(commandContext, commandInput).thenCompose(primaryResult -> {
/*     */           if (primaryResult.parsedValue().isPresent()) {
/*     */             return ArgumentParseResult.successFuture(Either.ofPrimary(primaryResult.parsedValue().get()));
/*     */           }
/*     */           commandInput.cursor(originalCursor);
/*     */           return this.fallback.parser().parseFuture(commandContext, commandInput).thenApply(());
/*     */         });
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
/*     */   public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> context, CommandInput input) {
/* 160 */     if (!(this.primary.parser() instanceof SuggestionProvider)) {
/* 161 */       if (!(this.fallback.parser() instanceof SuggestionProvider)) {
/* 162 */         return CompletableFuture.completedFuture(Collections.emptyList());
/*     */       }
/* 164 */       return ((SuggestionProvider)this.fallback.parser()).suggestionsFuture(context, input);
/*     */     } 
/* 166 */     if (!(this.fallback.parser() instanceof SuggestionProvider)) {
/* 167 */       return ((SuggestionProvider)this.primary.parser()).suggestionsFuture(context, input);
/*     */     }
/*     */ 
/*     */     
/* 171 */     CompletableFuture[] arrayOfCompletableFuture = { ((SuggestionProvider)this.primary.parser()).suggestionsFuture(context, input.copy()), ((SuggestionProvider)this.fallback.parser()).suggestionsFuture(context, input) };
/*     */     
/* 173 */     return CompletableFuture.allOf((CompletableFuture<?>[])arrayOfCompletableFuture).thenApply(ignored -> (List)Stream.concat(StreamSupport.stream(((Iterable)suggestionFutures[0].getNow(Collections.emptyList())).spliterator(), false), StreamSupport.stream(((Iterable)suggestionFutures[1].getNow(Collections.emptyList())).spliterator(), false)).collect(Collectors.toList()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class EitherParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final Throwable primaryFailure;
/*     */ 
/*     */ 
/*     */     
/*     */     private final Throwable fallbackFailure;
/*     */ 
/*     */ 
/*     */     
/*     */     private final TypeToken<?> primaryType;
/*     */ 
/*     */ 
/*     */     
/*     */     private final TypeToken<?> fallbackType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private EitherParseException(Throwable primaryFailure, Throwable fallbackFailure, TypeToken<?> primaryType, TypeToken<?> fallbackType, CommandContext<?> context, String input) {
/* 200 */       super(fallbackFailure, EitherParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_EITHER, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 205 */             CaptionVariable.of("input", input), 
/* 206 */             CaptionVariable.of("primary", GenericTypeReflector.erase(primaryType.getType()).getSimpleName()), 
/* 207 */             CaptionVariable.of("fallback", GenericTypeReflector.erase(fallbackType.getType()).getSimpleName())
/*     */           });
/* 209 */       this.primaryFailure = primaryFailure;
/* 210 */       this.fallbackFailure = fallbackFailure;
/* 211 */       this.primaryType = primaryType;
/* 212 */       this.fallbackType = fallbackType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Throwable primaryFailure() {
/* 221 */       return this.primaryFailure;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Throwable fallbackFailure() {
/* 230 */       return this.fallbackFailure;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TypeToken<?> primaryType() {
/* 239 */       return this.primaryType;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TypeToken<?> fallbackType() {
/* 248 */       return this.fallbackType;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\EitherParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */