/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitPluginRegistrationHandler;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.PluginHolder;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.BukkitHelper;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.paper.LegacyPaperCommandManager;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
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
/*    */ class AsyncCommandSuggestionListener<C>
/*    */   implements SuggestionListener<C>
/*    */ {
/*    */   private final LegacyPaperCommandManager<C> paperCommandManager;
/*    */   
/*    */   AsyncCommandSuggestionListener(LegacyPaperCommandManager<C> paperCommandManager) {
/* 43 */     this.paperCommandManager = paperCommandManager;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @EventHandler
/*    */   void onTabCompletion(AsyncTabCompleteEvent event) {
/* 51 */     String strippedBuffer = event.getBuffer().startsWith("/") ? event.getBuffer().substring(1) : event.getBuffer();
/* 52 */     if (strippedBuffer.trim().isEmpty()) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 57 */     BukkitPluginRegistrationHandler<C> bukkitPluginRegistrationHandler = (BukkitPluginRegistrationHandler<C>)this.paperCommandManager.commandRegistrationHandler();
/*    */ 
/*    */     
/* 60 */     String commandLabel = strippedBuffer.split(" ")[0];
/* 61 */     if (!bukkitPluginRegistrationHandler.isRecognized(commandLabel)) {
/*    */       return;
/*    */     }
/*    */     
/* 65 */     String input = event.getBuffer();
/*    */     
/* 67 */     if (input.charAt(0) == '/') {
/* 68 */       input = input.substring(1);
/*    */     }
/*    */     
/* 71 */     setSuggestions(event, (C)this.paperCommandManager
/*    */         
/* 73 */         .senderMapper().map(event.getSender()), 
/* 74 */         BukkitHelper.stripNamespace((PluginHolder)this.paperCommandManager, input));
/*    */ 
/*    */     
/* 77 */     event.setHandled(true);
/*    */   }
/*    */   
/*    */   protected Suggestions<C, ?> querySuggestions(C commandSender, String input) {
/* 81 */     return this.paperCommandManager.suggestionFactory().suggestImmediately(commandSender, input);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setSuggestions(AsyncTabCompleteEvent event, C commandSender, String input) {
/* 89 */     Suggestions<C, ?> suggestions = querySuggestions(commandSender, input);
/* 90 */     event.setCompletions((List)suggestions.list().stream()
/* 91 */         .map(Suggestion::suggestion)
/* 92 */         .map(suggestion -> StringUtils.trimBeforeLastSpace(suggestion, suggestions.commandInput()))
/* 93 */         .filter(Objects::nonNull)
/* 94 */         .collect(Collectors.toList()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\AsyncCommandSuggestionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */