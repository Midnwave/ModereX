/*     */ package ac.grim.grimac.shaded.incendo.cloud.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandTree;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContextFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.ExecutionCoordinator;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.services.State;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.ManagerSetting;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Setting;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class DelegatingSuggestionFactory<C, S extends Suggestion>
/*     */   implements SuggestionFactory<C, S>
/*     */ {
/*     */   private final List<S> singleEmptySuggestion;
/*     */   private final CommandManager<C> commandManager;
/*     */   private final CommandTree<C> commandTree;
/*     */   private final CommandContextFactory<C> contextFactory;
/*     */   private final ExecutionCoordinator<C> executionCoordinator;
/*     */   private final SuggestionMapper<S> mapper;
/*     */   
/*     */   public DelegatingSuggestionFactory(CommandManager<C> commandManager, CommandTree<C> commandTree, CommandContextFactory<C> contextFactory, ExecutionCoordinator<C> executionCoordinator, SuggestionMapper<S> mapper) {
/*  72 */     this.commandManager = commandManager;
/*  73 */     this.commandTree = commandTree;
/*  74 */     this.contextFactory = contextFactory;
/*  75 */     this.executionCoordinator = executionCoordinator;
/*  76 */     this.mapper = mapper;
/*  77 */     this.singleEmptySuggestion = Collections.singletonList(mapper.map(Suggestion.suggestion("")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<Suggestions<C, S>> suggest(CommandContext<C> context, String input) {
/*  85 */     return suggestFromTree(context, input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<Suggestions<C, S>> suggest(C sender, String input) {
/*  93 */     return suggest(this.contextFactory.create(true, sender), input);
/*     */   }
/*     */ 
/*     */   
/*     */   public <S2 extends Suggestion> SuggestionFactory<C, S2> mapped(SuggestionMapper<S2> mapper) {
/*  98 */     return new DelegatingSuggestionFactory(this.commandManager, this.commandTree, this.contextFactory, this.executionCoordinator, this.mapper
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 103 */         .then(mapper));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CompletableFuture<Suggestions<C, S>> suggestFromTree(CommandContext<C> context, String input) {
/* 111 */     CommandInput commandInput = CommandInput.of(input);
/*     */     
/* 113 */     context.store("__raw_input__", commandInput.copy());
/*     */     
/* 115 */     if (this.commandManager.preprocessContext(context, commandInput) != State.ACCEPTED) {
/* 116 */       if (this.commandManager.settings().get((Setting)ManagerSetting.FORCE_SUGGESTION)) {
/* 117 */         return CompletableFuture.completedFuture(Suggestions.create(context, this.singleEmptySuggestion, commandInput));
/*     */       }
/* 119 */       return CompletableFuture.completedFuture(Suggestions.create(context, Collections.emptyList(), commandInput));
/*     */     } 
/*     */     
/* 122 */     return this.executionCoordinator.coordinateSuggestions(this.commandTree, context, commandInput, this.mapper)
/* 123 */       .thenApply(suggestions -> 
/* 124 */         (this.commandManager.settings().get((Setting)ManagerSetting.FORCE_SUGGESTION) && suggestions.list().isEmpty()) ? Suggestions.create(suggestions.commandContext(), this.singleEmptySuggestion, commandInput) : suggestions);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\suggestion\DelegatingSuggestionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */