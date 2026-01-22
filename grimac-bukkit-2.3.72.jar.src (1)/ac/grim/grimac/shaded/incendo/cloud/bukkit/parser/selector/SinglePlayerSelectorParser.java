/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.SinglePlayerSelector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.PlayerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
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
/*     */ public final class SinglePlayerSelectorParser<C>
/*     */   extends SelectorUtils.PlayerSelectorParser<C, SinglePlayerSelector>
/*     */ {
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, SinglePlayerSelector> singlePlayerSelectorParser() {
/*  59 */     return ParserDescriptor.of((ArgumentParser)new SinglePlayerSelectorParser(), SinglePlayerSelector.class);
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
/*     */   public static <C> CommandComponent.Builder<C, SinglePlayerSelector> singlePlayerSelectorComponent() {
/*  71 */     return CommandComponent.builder().parser(singlePlayerSelectorParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SinglePlayerSelectorParser() {
/*  78 */     super(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public SinglePlayerSelector mapResult(final String input, SelectorUtils.EntitySelectorWrapper wrapper) {
/*  87 */     final Player player = wrapper.singlePlayer();
/*  88 */     return new SinglePlayerSelector()
/*     */       {
/*     */         public Player single() {
/*  91 */           return player;
/*     */         }
/*     */ 
/*     */         
/*     */         public String inputString() {
/*  96 */           return input;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CompletableFuture<ArgumentParseResult<SinglePlayerSelector>> legacyParse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 106 */     String input = commandInput.peekString();
/* 107 */     final Player player = Bukkit.getPlayer(input);
/*     */     
/* 109 */     if (player == null) {
/* 110 */       return CompletableFuture.completedFuture(ArgumentParseResult.failure((Throwable)new PlayerParser.PlayerParseException(input, commandContext)));
/*     */     }
/*     */ 
/*     */     
/* 114 */     final String pop = commandInput.readString();
/* 115 */     return ArgumentParseResult.successFuture(new SinglePlayerSelector()
/*     */         {
/*     */           public Player single()
/*     */           {
/* 119 */             return player;
/*     */           }
/*     */ 
/*     */           
/*     */           public String inputString() {
/* 124 */             return pop;
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\selector\SinglePlayerSelectorParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */