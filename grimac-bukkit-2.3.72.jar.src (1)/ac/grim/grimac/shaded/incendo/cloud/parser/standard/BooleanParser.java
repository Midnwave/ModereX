/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.CaptionVariable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.caption.StandardCaptionKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.ParserException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class BooleanParser<C>
/*     */   implements ArgumentParser<C, Boolean>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   private static final List<String> STRICT_LOWER;
/*     */   private static final List<String> LIBERAL_LOWER;
/*     */   private final boolean liberal;
/*     */   
/*     */   static {
/*  47 */     STRICT_LOWER = (List<String>)CommandInput.BOOLEAN_STRICT.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
/*     */     
/*  49 */     LIBERAL_LOWER = (List<String>)CommandInput.BOOLEAN_LIBERAL.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Boolean> booleanParser() {
/*  61 */     return booleanParser(false);
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
/*     */   public static <C> ParserDescriptor<C, Boolean> booleanParser(boolean liberal) {
/*  74 */     return ParserDescriptor.of(new BooleanParser(liberal), Boolean.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Boolean> booleanComponent() {
/*  85 */     return CommandComponent.builder().parser(booleanParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BooleanParser(boolean liberal) {
/*  94 */     this.liberal = liberal;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Boolean> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 102 */     if (!commandInput.isValidBoolean(this.liberal)) {
/* 103 */       return ArgumentParseResult.failure((Throwable)new BooleanParseException(commandInput
/* 104 */             .peekString(), this.liberal, commandContext));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 109 */     return ArgumentParseResult.success(Boolean.valueOf(commandInput.readBoolean()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 115 */     if (!this.liberal) {
/* 116 */       return STRICT_LOWER;
/*     */     }
/*     */     
/* 119 */     return LIBERAL_LOWER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class BooleanParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final boolean liberal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public BooleanParseException(String input, boolean liberal, CommandContext<?> context) {
/* 144 */       super(BooleanParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 148 */             CaptionVariable.of("input", input)
/*     */           });
/* 150 */       this.input = input;
/* 151 */       this.liberal = liberal;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 161 */       return this.input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean liberal() {
/* 170 */       return this.liberal;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 175 */       if (this == o) {
/* 176 */         return true;
/*     */       }
/* 178 */       if (o == null || getClass() != o.getClass()) {
/* 179 */         return false;
/*     */       }
/* 181 */       BooleanParseException that = (BooleanParseException)o;
/* 182 */       return (this.liberal == that.liberal && this.input.equals(that.input));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 187 */       return Objects.hash(new Object[] { this.input, Boolean.valueOf(this.liberal) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\BooleanParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */