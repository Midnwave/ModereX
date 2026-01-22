/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandContextKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
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
/*     */ public final class PlayerParser<C>
/*     */   implements ArgumentParser<C, Player>, BlockingSuggestionProvider<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, Player> playerParser() {
/*  56 */     return ParserDescriptor.of(new PlayerParser(), Player.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> CommandComponent.Builder<C, Player> playerComponent() {
/*  68 */     return CommandComponent.builder().parser(playerParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Player> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  77 */     String input = commandInput.readString();
/*     */     
/*  79 */     Player player = Bukkit.getPlayer(input);
/*     */     
/*  81 */     if (player == null) {
/*  82 */       return ArgumentParseResult.failure((Throwable)new PlayerParseException(input, commandContext));
/*     */     }
/*     */     
/*  85 */     return ArgumentParseResult.success(player);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<Suggestion> suggestions(CommandContext<C> commandContext, CommandInput input) {
/*  93 */     CommandSender bukkit = (CommandSender)commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
/*  94 */     return (Iterable<Suggestion>)Bukkit.getOnlinePlayers().stream()
/*  95 */       .filter(player -> (!(bukkit instanceof Player) || ((Player)bukkit).canSee(player)))
/*  96 */       .map(OfflinePlayer::getName)
/*  97 */       .map(Suggestion::suggestion)
/*  98 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class PlayerParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PlayerParseException(String input, CommandContext<?> context) {
/* 119 */       super(PlayerParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 123 */             CaptionVariable.of("input", input)
/*     */           });
/* 125 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 134 */       return this.input;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\PlayerParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */