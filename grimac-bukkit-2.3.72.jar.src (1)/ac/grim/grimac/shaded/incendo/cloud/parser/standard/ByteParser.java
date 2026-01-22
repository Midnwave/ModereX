/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.NumberParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.ByteRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.Range;
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
/*     */ public final class ByteParser<C>
/*     */   extends NumberParser<C, Byte, ByteRange>
/*     */   implements BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final byte DEFAULT_MINIMUM = -128;
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final byte DEFAULT_MAXIMUM = 127;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Byte> byteParser() {
/*  64 */     return byteParser(-128, 127);
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
/*     */   public static <C> ParserDescriptor<C, Byte> byteParser(byte minValue) {
/*  78 */     return ParserDescriptor.of(new ByteParser(minValue, 127), Byte.class);
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
/*     */   public static <C> ParserDescriptor<C, Byte> byteParser(byte minValue, byte maxValue) {
/*  94 */     return ParserDescriptor.of(new ByteParser(minValue, maxValue), Byte.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Byte> byteComponent() {
/* 105 */     return CommandComponent.builder().parser(byteParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteParser(byte min, byte max) {
/* 115 */     super(Range.byteRange(min, max));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Byte> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 123 */     if (!commandInput.isValidByte(range())) {
/* 124 */       return ArgumentParseResult.failure((Throwable)new ByteParseException(commandInput
/* 125 */             .peekString(), this, commandContext));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 130 */     return ArgumentParseResult.success(Byte.valueOf(commandInput.readByte()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMax() {
/* 135 */     return (range().maxByte() != Byte.MAX_VALUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMin() {
/* 140 */     return (range().minByte() != Byte.MIN_VALUE);
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
/*     */   public static final class ByteParseException
/*     */     extends NumberParseException
/*     */   {
/*     */     @API(status = API.Status.STABLE)
/*     */     public ByteParseException(String input, ByteParser<?> parser, CommandContext<?> commandContext) {
/* 168 */       super(input, parser, commandContext);
/*     */     }
/*     */ 
/*     */     
/*     */     public String numberType() {
/* 173 */       return "byte";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\ByteParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */