/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.NumberParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.DoubleRange;
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
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class DoubleParser<C>
/*     */   extends NumberParser<C, Double, DoubleRange>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final double DEFAULT_MINIMUM = -InfinityD;
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final double DEFAULT_MAXIMUM = InfinityD;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Double> doubleParser() {
/*  63 */     return doubleParser(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
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
/*     */   public static <C> ParserDescriptor<C, Double> doubleParser(double minValue) {
/*  77 */     return ParserDescriptor.of(new DoubleParser(minValue, Double.POSITIVE_INFINITY), Double.class);
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
/*     */   public static <C> ParserDescriptor<C, Double> doubleParser(double minValue, double maxValue) {
/*  93 */     return ParserDescriptor.of(new DoubleParser(minValue, maxValue), Double.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Double> doubleComponent() {
/* 104 */     return CommandComponent.builder().parser(doubleParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DoubleParser(double min, double max) {
/* 114 */     super(Range.doubleRange(min, max));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Double> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 122 */     if (!commandInput.isValidDouble(range())) {
/* 123 */       return ArgumentParseResult.failure((Throwable)new DoubleParseException(commandInput
/* 124 */             .peekString(), this, commandContext));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 129 */     return ArgumentParseResult.success(Double.valueOf(commandInput.readDouble()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMax() {
/* 134 */     return (range().maxDouble() != Double.POSITIVE_INFINITY);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMin() {
/* 139 */     return (range().minDouble() != Double.NEGATIVE_INFINITY);
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
/*     */   public static final class DoubleParseException
/*     */     extends NumberParseException
/*     */   {
/*     */     @API(status = API.Status.STABLE)
/*     */     public DoubleParseException(String input, DoubleParser<?> parser, CommandContext<?> commandContext) {
/* 159 */       super(input, parser, commandContext);
/*     */     }
/*     */ 
/*     */     
/*     */     public String numberType() {
/* 164 */       return "double";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\DoubleParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */