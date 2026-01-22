/*    */ package ac.grim.grimac.platform.bukkit.manager;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.command.PlayerSelector;
/*    */ import ac.grim.grimac.platform.api.manager.CommandAdapter;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.platform.bukkit.command.BukkitPlayerSelectorParser;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandContextKeys;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ public class BukkitParserDescriptorFactory
/*    */   implements CommandAdapter {
/* 22 */   private final BukkitPlayerSelectorParser<Sender> bukkitPlayerSelectorParser = new BukkitPlayerSelectorParser();
/*    */ 
/*    */   
/*    */   public ParserDescriptor<Sender, PlayerSelector> singlePlayerSelectorParser() {
/* 26 */     return this.bukkitPlayerSelectorParser.descriptor();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public SuggestionProvider<Sender> onlinePlayerSuggestions() {
/* 32 */     return (context, input) -> {
/*    */         List<Suggestion> suggestions = new ArrayList<>();
/*    */         for (Player player : Bukkit.getOnlinePlayers()) {
/*    */           CommandSender bukkit = (CommandSender)context.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
/*    */           if (!(bukkit instanceof Player) || ((Player)bukkit).canSee(player))
/*    */             suggestions.add(Suggestion.suggestion(player.getName())); 
/*    */         } 
/*    */         return CompletableFuture.completedFuture(suggestions);
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\manager\BukkitParserDescriptorFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */