/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion.tooltips;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
/*    */ import com.mojang.brigadier.Message;
/*    */ import io.papermc.paper.brigadier.PaperBrigadier;
/*    */ import io.papermc.paper.command.brigadier.MessageComponentSerializer;
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
/*    */ final class NativeCompletionMapper
/*    */   implements CompletionMapper
/*    */ {
/*    */   public AsyncTabCompleteEvent.Completion map(TooltipSuggestion suggestion) {
/* 39 */     if (!CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.MessageComponentSerializer")) {
/* 40 */       return mapLegacy(suggestion);
/*    */     }
/* 42 */     return AsyncTabCompleteEvent.Completion.completion(suggestion
/* 43 */         .suggestion(), 
/* 44 */         MessageComponentSerializer.message().deserializeOrNull(suggestion.tooltip()));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static AsyncTabCompleteEvent.Completion mapLegacy(@NotNull TooltipSuggestion suggestion) {
/* 50 */     Message tooltip = suggestion.tooltip();
/* 51 */     if (tooltip == null) {
/* 52 */       return AsyncTabCompleteEvent.Completion.completion(suggestion.suggestion());
/*    */     }
/* 54 */     return AsyncTabCompleteEvent.Completion.completion(suggestion
/* 55 */         .suggestion(), 
/* 56 */         PaperBrigadier.componentFromMessage(tooltip));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\tooltips\NativeCompletionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */