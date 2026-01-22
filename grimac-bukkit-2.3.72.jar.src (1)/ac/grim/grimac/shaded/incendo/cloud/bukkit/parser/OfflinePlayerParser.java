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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class OfflinePlayerParser<C>
/*     */   implements ArgumentParser<C, OfflinePlayer>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, OfflinePlayer> offlinePlayerParser() {
/*  64 */     return ParserDescriptor.of(new OfflinePlayerParser(), OfflinePlayer.class);
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
/*     */   public static <C> CommandComponent.Builder<C, OfflinePlayer> offlinePlayerComponent() {
/*  76 */     return CommandComponent.builder().parser(offlinePlayerParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<OfflinePlayer> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*     */     OfflinePlayer player;
/*  85 */     String input = commandInput.readString();
/*  86 */     if (input.length() > 16) {
/*  87 */       return ArgumentParseResult.failure((Throwable)new OfflinePlayerParseException(input, commandContext));
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  92 */       player = Bukkit.getOfflinePlayer(input);
/*  93 */     } catch (Exception e) {
/*  94 */       return ArgumentParseResult.failure((Throwable)new OfflinePlayerParseException(input, commandContext));
/*     */     } 
/*     */     
/*  97 */     return ArgumentParseResult.success(player);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 105 */     CommandSender bukkit = (CommandSender)commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
/* 106 */     return (Iterable<String>)Bukkit.getOnlinePlayers().stream()
/* 107 */       .filter(player -> (!(bukkit instanceof Player) || ((Player)bukkit).canSee(player)))
/* 108 */       .map(OfflinePlayer::getName)
/* 109 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class OfflinePlayerParseException
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
/*     */     public OfflinePlayerParseException(String input, CommandContext<?> context) {
/* 130 */       super(OfflinePlayerParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 134 */             CaptionVariable.of("input", input)
/*     */           });
/* 136 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 145 */       return this.input;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\OfflinePlayerParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */