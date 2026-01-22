/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.enchantments.Enchantment;
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
/*     */ public final class EnchantmentParser<C>
/*     */   implements ArgumentParser<C, Enchantment>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, Enchantment> enchantmentParser() {
/*  55 */     return ParserDescriptor.of(new EnchantmentParser(), Enchantment.class);
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
/*     */   public static <C> CommandComponent.Builder<C, Enchantment> enchantmentComponent() {
/*  67 */     return CommandComponent.builder().parser(enchantmentParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Enchantment> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*     */     NamespacedKey key;
/*  76 */     String input = commandInput.peekString();
/*     */ 
/*     */     
/*     */     try {
/*  80 */       if (input.contains(":")) {
/*  81 */         key = new NamespacedKey(commandInput.readUntilAndSkip(':'), commandInput.readString());
/*     */       } else {
/*  83 */         key = NamespacedKey.minecraft(commandInput.readString());
/*     */       } 
/*  85 */     } catch (Exception ex) {
/*  86 */       return ArgumentParseResult.failure((Throwable)new EnchantmentParseException(input, commandContext));
/*     */     } 
/*     */     
/*  89 */     Enchantment enchantment = Enchantment.getByKey(key);
/*  90 */     if (enchantment == null) {
/*  91 */       return ArgumentParseResult.failure((Throwable)new EnchantmentParseException(input, commandContext));
/*     */     }
/*  93 */     return ArgumentParseResult.success(enchantment);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/*  99 */     List<String> completions = new ArrayList<>();
/* 100 */     for (Enchantment value : Enchantment.values()) {
/* 101 */       if (value.getKey().getNamespace().equals("minecraft")) {
/* 102 */         completions.add(value.getKey().getKey());
/*     */       } else {
/* 104 */         completions.add(value.getKey().toString());
/*     */       } 
/*     */     } 
/* 107 */     return completions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class EnchantmentParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EnchantmentParseException(String input, CommandContext<?> context) {
/* 125 */       super(EnchantmentParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_ENCHANTMENT, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 129 */             CaptionVariable.of("input", input)
/*     */           });
/* 131 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 140 */       return this.input;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\EnchantmentParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */