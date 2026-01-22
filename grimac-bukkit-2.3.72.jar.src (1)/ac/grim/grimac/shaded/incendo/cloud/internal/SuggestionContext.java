/*     */ package ac.grim.grimac.shaded.incendo.cloud.internal;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProcessor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class SuggestionContext<C, S extends Suggestion>
/*     */ {
/*  45 */   private final List<S> suggestions = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private final CommandPreprocessingContext<C> preprocessingContext;
/*     */ 
/*     */ 
/*     */   
/*     */   private final SuggestionMapper<S> mapper;
/*     */ 
/*     */ 
/*     */   
/*     */   private final SuggestionProcessor<C> processor;
/*     */ 
/*     */   
/*     */   private final CommandContext<C> commandContext;
/*     */ 
/*     */ 
/*     */   
/*     */   public SuggestionContext(SuggestionProcessor<C> processor, CommandContext<C> commandContext, CommandInput commandInput, SuggestionMapper<S> mapper) {
/*  65 */     this.processor = processor;
/*  66 */     this.commandContext = commandContext;
/*  67 */     this.preprocessingContext = CommandPreprocessingContext.of(this.commandContext, commandInput);
/*  68 */     this.mapper = mapper;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Suggestions<C, S> makeSuggestions() {
/*     */     List<S> list;
/*  78 */     Stream<S> stream = this.suggestions.stream();
/*  79 */     Stream<Suggestion> processedStream = this.processor.process(this.preprocessingContext, stream);
/*     */     
/*  81 */     if (stream == processedStream) {
/*     */       
/*  83 */       list = Collections.unmodifiableList(this.suggestions);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  88 */       Objects.requireNonNull(this.mapper); list = Collections.unmodifiableList((List<? extends S>)processedStream.peek(obj -> Objects.requireNonNull(obj, "suggestion")).map(this.mapper::map)
/*  89 */           .collect(Collectors.toList()));
/*     */     } 
/*     */     
/*  92 */     return Suggestions.create(this.commandContext, list, this.preprocessingContext.commandInput());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandContext<C> commandContext() {
/* 101 */     return this.commandContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSuggestions(Iterable<? extends Suggestion> suggestions) {
/* 110 */     suggestions.forEach(this::addSuggestion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSuggestion(Suggestion suggestion) {
/* 119 */     Objects.requireNonNull(suggestion, "suggestion");
/* 120 */     this.suggestions.add((S)this.mapper.map(suggestion));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\internal\SuggestionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */