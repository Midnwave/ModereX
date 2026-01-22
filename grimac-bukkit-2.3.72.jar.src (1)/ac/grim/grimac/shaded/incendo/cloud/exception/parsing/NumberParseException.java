/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception.parsing;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.NumberParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.Range;
/*     */ import java.util.Objects;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public abstract class NumberParseException
/*     */   extends ParserException
/*     */ {
/*     */   private final String input;
/*     */   private final NumberParser<?, ?, ?> parser;
/*     */   
/*     */   protected NumberParseException(String input, NumberParser<?, ?, ?> parser, CommandContext<?> context) {
/*  53 */     super(parser
/*  54 */         .getClass(), context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER, new CaptionVariable[] {
/*     */ 
/*     */           
/*  57 */           CaptionVariable.of("input", input), 
/*  58 */           CaptionVariable.of("min", String.valueOf(parser.range().min())), 
/*  59 */           CaptionVariable.of("max", String.valueOf(parser.range().max()))
/*     */         });
/*  61 */     this.input = input;
/*  62 */     this.parser = parser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String numberType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final NumberParser<?, ?, ?> parser() {
/*  78 */     return this.parser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasMax() {
/*  87 */     return this.parser.hasMax();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean hasMin() {
/*  96 */     return this.parser.hasMax();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String input() {
/* 105 */     return this.input;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Range<? extends Number> range() {
/* 114 */     return this.parser.range();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object o) {
/* 119 */     if (this == o) {
/* 120 */       return true;
/*     */     }
/* 122 */     if (o == null || getClass() != o.getClass()) {
/* 123 */       return false;
/*     */     }
/* 125 */     NumberParseException that = (NumberParseException)o;
/* 126 */     return parser().equals(that.parser());
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 131 */     return Objects.hash(new Object[] { parser() });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\parsing\NumberParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */