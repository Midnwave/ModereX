/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.aggregate;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ @API(status = API.Status.INTERNAL)
/*     */ final class AggregateSuggestionProvider<C>
/*     */   implements SuggestionProvider<C>
/*     */ {
/*     */   private final AggregateParser<C, ?> parser;
/*     */   
/*     */   AggregateSuggestionProvider(AggregateParser<C, ?> parser) {
/*  45 */     this.parser = parser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<Iterable<Suggestion>> suggestionsFuture(CommandContext<C> context, CommandInput input) {
/*  54 */     CommandInput originalInput = input.copy();
/*     */     
/*  56 */     return (new ParsingInstance(context, input)).parseComponent()
/*  57 */       .thenCompose(component -> component.suggestionProvider().suggestionsFuture(context, input.skipWhitespace(1, false).copy()))
/*     */ 
/*     */ 
/*     */       
/*  61 */       .thenApply(suggestions -> {
/*     */           String prefix = originalInput.difference(input, true);
/*     */           List<Suggestion> prefixedSuggestions = new ArrayList<>();
/*     */           for (Suggestion suggestion : suggestions) {
/*     */             prefixedSuggestions.add(suggestion.withSuggestion(String.format("%s%s", new Object[] { prefix, suggestion.suggestion() })));
/*     */           } 
/*     */           return prefixedSuggestions;
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private final class ParsingInstance
/*     */   {
/*  74 */     private final Iterator<CommandComponent<C>> components = AggregateSuggestionProvider.this.parser.components().iterator();
/*     */     
/*     */     private final CommandContext<C> context;
/*     */     private final CommandInput input;
/*     */     private CommandComponent<C> component;
/*     */     private int previousCursor;
/*     */     
/*     */     private ParsingInstance(CommandContext<C> context, CommandInput input) {
/*  82 */       this.context = context;
/*  83 */       this.input = input;
/*     */     }
/*     */     
/*     */     private CompletableFuture<CommandComponent<C>> parseComponent() {
/*  87 */       if (!this.components.hasNext()) {
/*  88 */         return CompletableFuture.completedFuture(this.component);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  93 */       this.component = this.components.next();
/*  94 */       this.previousCursor = this.input.cursor();
/*     */       
/*  96 */       return this.component.parser()
/*  97 */         .parseFuture(this.context, this.input.skipWhitespace(1))
/*  98 */         .thenCompose(this::handleResult);
/*     */     }
/*     */ 
/*     */     
/*     */     private CompletableFuture<CommandComponent<C>> handleResult(ArgumentParseResult<?> result) {
/* 103 */       boolean consumedAll = this.input.isEmpty();
/*     */       
/* 105 */       if (result.failure().isPresent() || !this.components.hasNext() || this.input.isEmpty())
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 110 */         this.input.cursor(this.previousCursor);
/*     */       }
/*     */ 
/*     */       
/* 114 */       if (result.failure().isPresent()) {
/* 115 */         return CompletableFuture.completedFuture(this.component);
/*     */       }
/*     */ 
/*     */       
/* 119 */       result.parsedValue().ifPresent(value -> this.context.store(this.component.name(), value));
/*     */ 
/*     */       
/* 122 */       if (consumedAll) {
/* 123 */         return CompletableFuture.completedFuture(this.component);
/*     */       }
/*     */ 
/*     */       
/* 127 */       return parseComponent();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\aggregate\AggregateSuggestionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */