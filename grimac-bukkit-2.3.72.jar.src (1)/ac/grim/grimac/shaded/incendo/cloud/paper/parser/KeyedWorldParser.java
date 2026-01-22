/*     */ package ac.grim.grimac.shaded.incendo.cloud.paper.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.WorldParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.World;
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
/*     */ public final class KeyedWorldParser<C>
/*     */   implements ArgumentParser<C, World>, SuggestionProvider<C>
/*     */ {
/*     */   private final ArgumentParser<C, World> parser;
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, World> keyedWorldParser() {
/*  65 */     return ParserDescriptor.of(new KeyedWorldParser(), World.class);
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
/*     */   public static <C> CommandComponent.Builder<C, World> keyedWorldComponent() {
/*  77 */     return CommandComponent.builder().parser(keyedWorldParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyedWorldParser() {
/*  86 */     Class<?> keyed = CraftBukkitReflection.findClass("org.bukkit.Keyed");
/*  87 */     if (keyed != null && keyed.isAssignableFrom(World.class)) {
/*  88 */       this.parser = null;
/*     */     } else {
/*  90 */       this.parser = (ArgumentParser<C, World>)new WorldParser();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<World> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  99 */     if (this.parser != null) {
/* 100 */       return this.parser.parse(commandContext, commandInput);
/*     */     }
/*     */     
/* 103 */     String input = commandInput.readString();
/*     */     
/* 105 */     NamespacedKey key = NamespacedKey.fromString(input);
/* 106 */     if (key == null) {
/* 107 */       return ArgumentParseResult.failure((Throwable)new WorldParser.WorldParseException(input, commandContext));
/*     */     }
/*     */     
/* 110 */     World world = Bukkit.getWorld(key);
/* 111 */     if (world == null) {
/* 112 */       return ArgumentParseResult.failure((Throwable)new WorldParser.WorldParseException(input, commandContext));
/*     */     }
/*     */     
/* 115 */     return ArgumentParseResult.success(world);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> commandContext, CommandInput input) {
/* 123 */     if (this.parser != null) {
/* 124 */       return this.parser.suggestionProvider().suggestionsFuture(commandContext, input);
/*     */     }
/*     */     
/* 127 */     List<World> worlds = Bukkit.getWorlds();
/* 128 */     List<Suggestion> completions = new ArrayList<>(worlds.size() * 2);
/* 129 */     for (World world : worlds) {
/* 130 */       NamespacedKey key = world.getKey();
/* 131 */       if (input.hasRemainingInput() && key.getNamespace().equals("minecraft")) {
/* 132 */         completions.add(Suggestion.suggestion(key.getKey()));
/*     */       }
/* 134 */       completions.add(Suggestion.suggestion(key.getNamespace() + ':' + key.getKey()));
/*     */     } 
/* 136 */     return CompletableFuture.completedFuture(completions);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\paper\parser\KeyedWorldParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */