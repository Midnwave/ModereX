/*    */ package ac.grim.grimac.platform.api.command;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ 
/*    */ 
/*    */ public abstract class AbstractPlayerSelectorParser<C>
/*    */ {
/*    */   public abstract ParserDescriptor<C, PlayerSelector> descriptor();
/*    */   
/*    */   protected abstract ParserDescriptor<C, ?> getPlatformSpecificDescriptor();
/*    */   
/*    */   protected abstract CompletableFuture<PlayerSelector> adaptToCommonSelector(CommandContext<C> paramCommandContext, Object paramObject);
/*    */   
/*    */   protected ParserDescriptor<C, PlayerSelector> createDescriptor() {
/* 18 */     return ParserDescriptor.of((ArgumentParser)
/* 19 */         getPlatformSpecificDescriptor().parser().mapSuccess(this::adaptToCommonSelector), PlayerSelector.class);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\api\command\AbstractPlayerSelectorParser.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */