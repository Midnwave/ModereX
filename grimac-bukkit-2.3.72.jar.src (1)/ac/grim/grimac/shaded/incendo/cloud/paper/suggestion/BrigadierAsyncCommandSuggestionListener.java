/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.paper.LegacyPaperCommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.tooltips.CompletionMapper;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.tooltips.CompletionMapperFactory;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionFactory;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestions;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.util.StringUtils;
/*    */ import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
/*    */ import java.util.stream.Collectors;
/*    */ import org.bukkit.event.EventHandler;
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
/*    */ class BrigadierAsyncCommandSuggestionListener<C>
/*    */   extends AsyncCommandSuggestionListener<C>
/*    */ {
/* 42 */   private final CompletionMapperFactory completionMapperFactory = CompletionMapperFactory.detectingRelocation();
/*    */   private final SuggestionFactory<C, ? extends TooltipSuggestion> suggestionFactory;
/*    */   
/*    */   BrigadierAsyncCommandSuggestionListener(LegacyPaperCommandManager<C> paperCommandManager) {
/* 46 */     super(paperCommandManager);
/* 47 */     this.suggestionFactory = paperCommandManager.suggestionFactory().mapped(TooltipSuggestion::tooltipSuggestion);
/*    */   }
/*    */ 
/*    */   
/*    */   @EventHandler
/*    */   void onTabCompletion(AsyncTabCompleteEvent event) {
/* 53 */     super.onTabCompletion(event);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected Suggestions<C, ? extends TooltipSuggestion> querySuggestions(C commandSender, String input) {
/* 61 */     return this.suggestionFactory.suggestImmediately(commandSender, input);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setSuggestions(AsyncTabCompleteEvent event, C commandSender, String input) {
/* 70 */     CompletionMapper completionMapper = this.completionMapperFactory.createMapper();
/* 71 */     Suggestions<C, ? extends TooltipSuggestion> suggestions = querySuggestions(commandSender, input);
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
/* 82 */     Objects.requireNonNull(completionMapper); event.completions((List)suggestions.list().stream().map(suggestion -> { String trim = StringUtils.trimBeforeLastSpace(suggestion.suggestion(), suggestions.commandInput()); return (trim == null) ? null : suggestion.withSuggestion(trim); }).filter(Objects::nonNull).map(completionMapper::map)
/* 83 */         .collect(Collectors.toList()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\BrigadierAsyncCommandSuggestionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */