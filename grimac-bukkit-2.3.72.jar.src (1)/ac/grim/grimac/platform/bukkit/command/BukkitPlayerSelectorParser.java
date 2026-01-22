/*    */ package ac.grim.grimac.platform.bukkit.command;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.command.AbstractPlayerSelectorParser;
/*    */ import ac.grim.grimac.platform.api.command.PlayerSelector;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.SinglePlayerSelector;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.SinglePlayerSelectorParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ 
/*    */ public class BukkitPlayerSelectorParser<C>
/*    */   extends AbstractPlayerSelectorParser<C>
/*    */ {
/*    */   public ParserDescriptor<C, PlayerSelector> descriptor() {
/* 15 */     return createDescriptor();
/*    */   }
/*    */ 
/*    */   
/*    */   protected ParserDescriptor<C, ?> getPlatformSpecificDescriptor() {
/* 20 */     return SinglePlayerSelectorParser.singlePlayerSelectorParser();
/*    */   }
/*    */ 
/*    */   
/*    */   protected CompletableFuture<PlayerSelector> adaptToCommonSelector(CommandContext<C> context, Object platformSpecificSelector) {
/* 25 */     return CompletableFuture.completedFuture(new BukkitPlayerSelectorAdapter((SinglePlayerSelector)platformSpecificSelector));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\command\BukkitPlayerSelectorParser.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */