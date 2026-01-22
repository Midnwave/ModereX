/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierCommand;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.tuple.Pair;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.context.StringRange;
/*     */ import com.mojang.brigadier.suggestion.Suggestions;
/*     */ import com.mojang.brigadier.suggestion.SuggestionsBuilder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*     */ public final class BrigadierSuggestionFactory<C, S>
/*     */ {
/*     */   private final CloudBrigadierManager<C, S> cloudBrigadierManager;
/*     */   private final CommandManager<C> commandManager;
/*     */   private final SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory;
/*     */   
/*     */   public BrigadierSuggestionFactory(CloudBrigadierManager<C, S> cloudBrigadierManager, CommandManager<C> commandManager, SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory) {
/*  72 */     this.cloudBrigadierManager = cloudBrigadierManager;
/*  73 */     this.commandManager = commandManager;
/*  74 */     this.suggestionFactory = suggestionFactory;
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
/*     */   public CompletableFuture<Suggestions> buildSuggestions(CommandContext<S> senderContext, CommandNode<C> parentNode, SuggestionsBuilder builder) {
/*  90 */     C cloudSender = (C)this.cloudBrigadierManager.senderMapper().map(senderContext.getSource());
/*  91 */     CommandContext<C> commandContext = new CommandContext(true, cloudSender, this.commandManager);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     commandContext.store("_cloud_brigadier_native_sender", senderContext.getSource());
/*     */     
/*  98 */     String command = builder.getInput().substring(((StringRange)((Pair)CloudBrigadierCommand.parsedNodes(senderContext.getLastChild()).get(0)).second()).getStart());
/*     */ 
/*     */     
/* 101 */     String leading = command.split(" ")[0];
/* 102 */     if (leading.contains(":")) {
/* 103 */       command = command.substring(leading.split(":")[0].length() + 1);
/*     */     }
/*     */     
/* 106 */     return this.suggestionFactory.suggest(commandContext.sender(), command).thenApply(suggestionsResult -> {
/*     */           List<TooltipSuggestion> suggestions = new ArrayList<>(suggestionsResult.list());
/*     */ 
/*     */           
/*     */           if (parentNode != null) {
/*     */             Set<String> siblingLiterals = (Set<String>)parentNode.children().stream().map(CommandNode::component).filter(Objects::nonNull).filter(()).flatMap(()).collect(Collectors.toSet());
/*     */ 
/*     */             
/*     */             suggestions.removeIf(());
/*     */           } 
/*     */ 
/*     */           
/*     */           int trimmed = builder.getInput().length() - suggestionsResult.commandInput().length();
/*     */           
/*     */           int rawOffset = suggestionsResult.commandInput().cursor();
/*     */           
/*     */           SuggestionsBuilder suggestionsBuilder = builder.createOffset(rawOffset + trimmed);
/*     */           
/*     */           for (TooltipSuggestion suggestion : suggestions) {
/*     */             try {
/*     */               suggestionsBuilder.suggest(Integer.parseInt(suggestion.suggestion()), suggestion.tooltip());
/* 127 */             } catch (NumberFormatException e) {
/*     */               suggestionsBuilder.suggest(suggestion.suggestion(), suggestion.tooltip());
/*     */             } 
/*     */           } 
/*     */           return suggestionsBuilder.build();
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\suggestion\BrigadierSuggestionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */