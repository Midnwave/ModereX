/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitParserParameters;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserParameters;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.NamespacedKey;
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
/*     */ public final class NamespacedKeyParser<C>
/*     */   implements ArgumentParser<C, NamespacedKey>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   private final boolean requireExplicitNamespace;
/*     */   private final String defaultNamespace;
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, NamespacedKey> namespacedKeyParser() {
/*  66 */     return namespacedKeyParser(false);
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
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, NamespacedKey> namespacedKeyParser(boolean requireExplicitNamespace) {
/*  82 */     return namespacedKeyParser(requireExplicitNamespace, "minecraft");
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, NamespacedKey> namespacedKeyParser(boolean requireExplicitNamespace, String defaultNamespace) {
/*  99 */     return ParserDescriptor.of(new NamespacedKeyParser(requireExplicitNamespace, defaultNamespace), NamespacedKey.class);
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
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> CommandComponent.Builder<C, NamespacedKey> namespacedKeyComponent() {
/* 114 */     return CommandComponent.builder().parser(namespacedKeyParser());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NamespacedKeyParser(boolean requireExplicitNamespace, String defaultNamespace) {
/* 131 */     this.requireExplicitNamespace = requireExplicitNamespace;
/* 132 */     this.defaultNamespace = defaultNamespace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<NamespacedKey> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 141 */     String input = commandInput.peekString();
/* 142 */     String[] split = input.split(":");
/* 143 */     int maxSemi = (split.length > 1) ? 1 : 0;
/* 144 */     if (input.length() - input.replace(":", "").length() > maxSemi)
/*     */     {
/* 146 */       return ArgumentParseResult.failure((Throwable)new NamespacedKeyParseException(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY, input, commandContext));
/*     */     }
/*     */     
/*     */     try {
/*     */       NamespacedKey ret;
/*     */       
/* 152 */       if (split.length == 1) {
/* 153 */         if (this.requireExplicitNamespace)
/*     */         {
/* 155 */           return ArgumentParseResult.failure((Throwable)new NamespacedKeyParseException(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE, input, commandContext));
/*     */         }
/*     */ 
/*     */         
/* 159 */         ret = new NamespacedKey(this.defaultNamespace, commandInput.readString());
/* 160 */       } else if (split.length == 2) {
/* 161 */         ret = new NamespacedKey(commandInput.readUntilAndSkip(':'), commandInput.readString());
/*     */       } else {
/*     */         
/* 164 */         return ArgumentParseResult.failure((Throwable)new NamespacedKeyParseException(BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY, input, commandContext));
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 170 */       return ArgumentParseResult.success(ret);
/* 171 */     } catch (IllegalArgumentException ex) {
/*     */       NamespacedKey ret;
/*     */ 
/*     */       
/* 175 */       Caption caption = ret.getMessage().contains("namespace") ? BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NAMESPACE : BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY;
/* 176 */       return ArgumentParseResult.failure((Throwable)new NamespacedKeyParseException(caption, input, commandContext));
/*     */     } 
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
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 189 */     List<String> ret = new ArrayList<>();
/* 190 */     ret.add(this.defaultNamespace + ":");
/*     */     
/* 192 */     String token = input.peekString();
/* 193 */     if (!token.contains(":") && !token.isEmpty()) {
/* 194 */       ret.add(token + ":");
/*     */     }
/*     */     
/* 197 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <C> void registerParserSupplier(CommandManager<C> commandManager) {
/* 208 */     commandManager.parserRegistry()
/* 209 */       .registerParserSupplier(TypeToken.get(NamespacedKey.class), params -> new NamespacedKeyParser(params.has(BukkitParserParameters.REQUIRE_EXPLICIT_NAMESPACE), (String)params.get(BukkitParserParameters.DEFAULT_NAMESPACE, "minecraft")));
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
/*     */ 
/*     */   
/*     */   public static final class NamespacedKeyParseException
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
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public NamespacedKeyParseException(Caption caption, String input, CommandContext<?> context) {
/* 238 */       super(NamespacedKeyParser.class, context, caption, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 242 */             CaptionVariable.of("input", input)
/*     */           });
/* 244 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 254 */       return this.input;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 259 */       if (this == o) {
/* 260 */         return true;
/*     */       }
/* 262 */       if (o == null || getClass() != o.getClass()) {
/* 263 */         return false;
/*     */       }
/* 265 */       NamespacedKeyParseException that = (NamespacedKeyParseException)o;
/* 266 */       return (this.input.equals(that.input) && errorCaption().equals(that.errorCaption()));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 271 */       return Objects.hash(new Object[] { this.input, errorCaption() });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\NamespacedKeyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */