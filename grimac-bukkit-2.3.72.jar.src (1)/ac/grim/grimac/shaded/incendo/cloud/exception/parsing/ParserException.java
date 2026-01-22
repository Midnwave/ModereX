/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception.parsing;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.Caption;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionFormatter;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import java.util.Arrays;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public class ParserException
/*     */   extends IllegalArgumentException
/*     */ {
/*     */   private final Class<?> argumentParser;
/*     */   private final CommandContext<?> context;
/*     */   private final Caption errorCaption;
/*     */   private final CaptionVariable[] captionVariables;
/*     */   
/*     */   protected ParserException(Throwable cause, Class<?> argumentParser, CommandContext<?> context, Caption errorCaption, CaptionVariable... captionVariables) {
/*  51 */     super(cause);
/*  52 */     this.argumentParser = argumentParser;
/*  53 */     this.context = context;
/*  54 */     this.errorCaption = errorCaption;
/*  55 */     this.captionVariables = captionVariables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ParserException(Class<?> argumentParser, CommandContext<?> context, Caption errorCaption, CaptionVariable... captionVariables) {
/*  64 */     this(null, argumentParser, context, errorCaption, captionVariables);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String getMessage() {
/*  69 */     return this.context.formatCaption(this.errorCaption, this.captionVariables);
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public final <T> T formatCaption(CaptionFormatter<?, T> formatter) {
/*  82 */     return (T)this.context.formatCaption(formatter, this.errorCaption, captionVariables());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public Caption errorCaption() {
/*  92 */     return this.errorCaption;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public CaptionVariable[] captionVariables() {
/* 103 */     return Arrays.<CaptionVariable>copyOf(this.captionVariables, this.captionVariables.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Class<?> argumentParserClass() {
/* 112 */     return this.argumentParser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CommandContext<?> context() {
/* 121 */     return this.context;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\parsing\ParserException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */