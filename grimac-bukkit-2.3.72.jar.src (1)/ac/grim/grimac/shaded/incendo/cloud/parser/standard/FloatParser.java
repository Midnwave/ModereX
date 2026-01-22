/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.NumberParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.FloatRange;
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
/*     */ public final class FloatParser<C>
/*     */   extends NumberParser<C, Float, FloatRange>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final float DEFAULT_MINIMUM = -InfinityF;
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final float DEFAULT_MAXIMUM = InfinityF;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Float> floatParser() {
/*  63 */     return floatParser(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
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
/*     */   public static <C> ParserDescriptor<C, Float> floatParser(float minValue) {
/*  77 */     return ParserDescriptor.of(new FloatParser(minValue, Float.POSITIVE_INFINITY), Float.class);
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
/*     */   public static <C> ParserDescriptor<C, Float> floatParser(float minValue, float maxValue) {
/*  93 */     return ParserDescriptor.of(new FloatParser(minValue, maxValue), Float.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Float> floatComponent() {
/* 104 */     return CommandComponent.builder().parser(floatParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FloatParser(float min, float max) {
/* 114 */     super(Range.floatRange(min, max));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Float> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 122 */     if (!commandInput.isValidFloat(range())) {
/* 123 */       return ArgumentParseResult.failure((Throwable)new FloatParseException(commandInput
/* 124 */             .peekString(), this, commandContext));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 129 */     return ArgumentParseResult.success(Float.valueOf(commandInput.readFloat()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMax() {
/* 134 */     return (range().maxFloat() != Float.POSITIVE_INFINITY);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMin() {
/* 139 */     return (range().minFloat() != Float.NEGATIVE_INFINITY);
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
/*     */   public static final class FloatParseException
/*     */     extends NumberParseException
/*     */   {
/*     */     @API(status = API.Status.STABLE)
/*     */     public FloatParseException(String input, FloatParser<?> parser, CommandContext<?> commandContext) {
/* 159 */       super(input, parser, commandContext);
/*     */     }
/*     */ 
/*     */     
/*     */     public String numberType() {
/* 164 */       return "float";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\FloatParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */