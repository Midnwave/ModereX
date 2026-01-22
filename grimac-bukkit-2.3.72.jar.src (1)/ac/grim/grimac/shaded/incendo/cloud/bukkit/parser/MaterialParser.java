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
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Material;
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
/*     */ public final class MaterialParser<C>
/*     */   implements ArgumentParser<C, Material>, BlockingSuggestionProvider<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, Material> materialParser() {
/*  55 */     return ParserDescriptor.of(new MaterialParser(), Material.class);
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
/*     */   public static <C> CommandComponent.Builder<C, Material> materialComponent() {
/*  67 */     return CommandComponent.builder().parser(materialParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Material> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  75 */     String input = commandInput.readString();
/*     */     try {
/*  77 */       Material material = Material.valueOf(input.toUpperCase(Locale.ROOT));
/*  78 */       return ArgumentParseResult.success(material);
/*  79 */     } catch (IllegalArgumentException exception) {
/*  80 */       return ArgumentParseResult.failure((Throwable)new MaterialParseException(input, commandContext));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<Suggestion> suggestions(CommandContext<C> commandContext, CommandInput input) {
/*  89 */     return (Iterable<Suggestion>)Arrays.<Material>stream(Material.values())
/*  90 */       .map(Enum::name)
/*  91 */       .map(String::toLowerCase)
/*  92 */       .map(Suggestion::suggestion)
/*  93 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class MaterialParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MaterialParseException(String input, CommandContext<?> context) {
/* 111 */       super(MaterialParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_MATERIAL, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 115 */             CaptionVariable.of("input", input)
/*     */           });
/* 117 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 126 */       return this.input;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\MaterialParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */