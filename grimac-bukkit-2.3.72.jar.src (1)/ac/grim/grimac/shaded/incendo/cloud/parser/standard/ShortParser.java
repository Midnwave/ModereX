/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.NumberParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.Range;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.ShortRange;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class ShortParser<C>
/*     */   extends NumberParser<C, Short, ShortRange>
/*     */   implements BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final short DEFAULT_MINIMUM = -32768;
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final short DEFAULT_MAXIMUM = 32767;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Short> shortParser() {
/*  64 */     return shortParser(-32768, '翿');
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Short> shortParser(short minValue) {
/*  78 */     return ParserDescriptor.of(new ShortParser(minValue, '翿'), Short.class);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Short> shortParser(short minValue, short maxValue) {
/*  94 */     return ParserDescriptor.of(new ShortParser(minValue, maxValue), Short.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Short> shortComponent() {
/* 105 */     return CommandComponent.builder().parser(shortParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ShortParser(short min, short max) {
/* 115 */     super(Range.shortRange(min, max));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Short> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 123 */     if (!commandInput.isValidShort(range())) {
/* 124 */       return ArgumentParseResult.failure((Throwable)new ShortParseException(commandInput
/* 125 */             .peekString(), this, commandContext));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 130 */     return ArgumentParseResult.success(Short.valueOf(commandInput.readShort()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMax() {
/* 135 */     return (range().maxShort() != Short.MAX_VALUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMin() {
/* 140 */     return (range().minShort() != Short.MIN_VALUE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 148 */     return IntegerParser.getSuggestions((Range<? extends Number>)range(), input);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class ShortParseException
/*     */     extends NumberParseException
/*     */   {
/*     */     @API(status = API.Status.STABLE)
/*     */     public ShortParseException(String input, ShortParser<?> parser, CommandContext<?> commandContext) {
/* 168 */       super(input, parser, commandContext);
/*     */     }
/*     */ 
/*     */     
/*     */     public String numberType() {
/* 173 */       return "short";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\ShortParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */