/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.MultiplePlayerSelector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.PlayerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public final class MultiplePlayerSelectorParser<C>
/*     */   extends SelectorUtils.PlayerSelectorParser<C, MultiplePlayerSelector>
/*     */ {
/*     */   private final boolean allowEmpty;
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, MultiplePlayerSelector> multiplePlayerSelectorParser() {
/*  62 */     return multiplePlayerSelectorParser(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, MultiplePlayerSelector> multiplePlayerSelectorParser(boolean allowEmpty) {
/*  75 */     return ParserDescriptor.of((ArgumentParser)new MultiplePlayerSelectorParser(allowEmpty), MultiplePlayerSelector.class);
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
/*     */   public static <C> CommandComponent.Builder<C, MultiplePlayerSelector> multiplePlayerSelectorComponent() {
/*  87 */     return CommandComponent.builder().parser(multiplePlayerSelectorParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "1.8.0")
/*     */   public MultiplePlayerSelectorParser(boolean allowEmpty) {
/* 100 */     super(false);
/* 101 */     this.allowEmpty = allowEmpty;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultiplePlayerSelectorParser() {
/* 108 */     this(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public MultiplePlayerSelector mapResult(final String input, SelectorUtils.EntitySelectorWrapper wrapper) {
/* 117 */     final List<Player> players = wrapper.players();
/* 118 */     if (players.isEmpty() && !this.allowEmpty) {
/* 119 */       (new SelectorUtils.SelectorParser.Thrower(NO_PLAYERS_EXCEPTION_TYPE.get())).throwIt();
/*     */     }
/* 121 */     return new MultiplePlayerSelector()
/*     */       {
/*     */         public String inputString() {
/* 124 */           return input;
/*     */         }
/*     */ 
/*     */         
/*     */         public Collection<Player> values() {
/* 129 */           return Collections.unmodifiableCollection(players);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CompletableFuture<ArgumentParseResult<MultiplePlayerSelector>> legacyParse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 140 */     String input = commandInput.peekString();
/* 141 */     final Player player = Bukkit.getPlayer(input);
/*     */     
/* 143 */     if (player == null) {
/* 144 */       return CompletableFuture.completedFuture(
/* 145 */           ArgumentParseResult.failure((Throwable)new PlayerParser.PlayerParseException(input, commandContext)));
/*     */     }
/*     */     
/* 148 */     final String pop = commandInput.readString();
/* 149 */     return ArgumentParseResult.successFuture(new MultiplePlayerSelector()
/*     */         {
/*     */           public String inputString() {
/* 152 */             return pop;
/*     */           }
/*     */ 
/*     */           
/*     */           public Collection<Player> values() {
/* 157 */             return Collections.singletonList(player);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\selector\MultiplePlayerSelectorParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */