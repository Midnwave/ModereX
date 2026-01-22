/*    */ package ac.grim.grimac.shaded.incendo.cloud.paper.suggestion;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.paper.LegacyPaperCommandManager;
/*    */ import org.apiguardian.api.API;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*    */ public interface SuggestionListenerFactory<C>
/*    */ {
/*    */   static <C> SuggestionListenerFactory<C> create(LegacyPaperCommandManager<C> commandManager) {
/* 43 */     return new SuggestionListenerFactoryImpl<>(commandManager);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   SuggestionListener<C> createListener();
/*    */ 
/*    */ 
/*    */   
/*    */   public static final class SuggestionListenerFactoryImpl<C>
/*    */     implements SuggestionListenerFactory<C>
/*    */   {
/*    */     private final LegacyPaperCommandManager<C> commandManager;
/*    */ 
/*    */     
/*    */     private SuggestionListenerFactoryImpl(LegacyPaperCommandManager<C> commandManager) {
/* 59 */       this.commandManager = commandManager;
/*    */     }
/*    */ 
/*    */     
/*    */     public SuggestionListener<C> createListener() {
/* 64 */       Class<?> completionCls = CraftBukkitReflection.findClass("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent$Completion");
/*    */ 
/*    */       
/* 67 */       if (completionCls != null) {
/* 68 */         return new BrigadierAsyncCommandSuggestionListener<>(this.commandManager);
/*    */       }
/* 70 */       return new AsyncCommandSuggestionListener<>(this.commandManager);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\suggestion\SuggestionListenerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */