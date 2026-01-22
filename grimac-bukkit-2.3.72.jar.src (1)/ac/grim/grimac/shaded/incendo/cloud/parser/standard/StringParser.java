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
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.StringUtils;
/*     */ import java.util.StringJoiner;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class StringParser<C>
/*     */   implements ArgumentParser<C, String>
/*     */ {
/*  46 */   private static final Pattern QUOTED_DOUBLE = Pattern.compile("\"(?<inner>(?:[^\"\\\\]|\\\\.)*)\"");
/*  47 */   private static final Pattern QUOTED_SINGLE = Pattern.compile("'(?<inner>(?:[^'\\\\]|\\\\.)*)'");
/*  48 */   private static final Pattern FLAG_PATTERN = Pattern.compile("(-[A-Za-z_\\-0-9])|(--[A-Za-z_\\-0-9]*)");
/*     */ 
/*     */ 
/*     */   
/*     */   private final StringMode stringMode;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String> stringParser(StringMode mode) {
/*  59 */     return ParserDescriptor.of(new StringParser(mode), String.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String> stringParser() {
/*  70 */     return stringParser(StringMode.SINGLE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String> greedyStringParser() {
/*  81 */     return stringParser(StringMode.GREEDY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, String> greedyFlagYieldingStringParser() {
/*  92 */     return stringParser(StringMode.GREEDY_FLAG_YIELDING);
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
/*     */   public static <C> ParserDescriptor<C, String> quotedStringParser() {
/* 104 */     return stringParser(StringMode.QUOTED);
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
/*     */   public static <C> CommandComponent.Builder<C, String> stringComponent(StringMode mode) {
/* 116 */     return CommandComponent.builder().parser(stringParser(mode));
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
/*     */   public static <C> CommandComponent.Builder<C, String> stringComponent() {
/* 128 */     return CommandComponent.builder().parser(stringParser(StringMode.SINGLE));
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
/*     */   public StringParser(StringMode stringMode) {
/* 141 */     this.stringMode = stringMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<String> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 149 */     if (this.stringMode == StringMode.SINGLE)
/* 150 */       return ArgumentParseResult.success(commandInput.readString()); 
/* 151 */     if (this.stringMode == StringMode.QUOTED) {
/* 152 */       return parseQuoted(commandContext, commandInput);
/*     */     }
/* 154 */     return parseGreedy(commandInput);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArgumentParseResult<String> parseQuoted(CommandContext<C> commandContext, CommandInput commandInput) {
/* 162 */     char peek = commandInput.peek();
/* 163 */     if (peek != '\'' && peek != '"') {
/* 164 */       return ArgumentParseResult.success(commandInput.readString());
/*     */     }
/*     */     
/* 167 */     String string = commandInput.remainingInput();
/*     */     
/* 169 */     Matcher doubleMatcher = QUOTED_DOUBLE.matcher(string);
/* 170 */     String doubleMatch = null;
/* 171 */     if (doubleMatcher.find()) {
/* 172 */       doubleMatch = doubleMatcher.group("inner");
/*     */     }
/* 174 */     Matcher singleMatcher = QUOTED_SINGLE.matcher(string);
/* 175 */     String singleMatch = null;
/* 176 */     if (singleMatcher.find()) {
/* 177 */       singleMatch = singleMatcher.group("inner");
/*     */     }
/*     */     
/* 180 */     String inner = null;
/* 181 */     if (singleMatch != null && doubleMatch != null) {
/* 182 */       int singleIndex = string.indexOf(singleMatch);
/* 183 */       int doubleIndex = string.indexOf(doubleMatch);
/* 184 */       inner = (doubleIndex < singleIndex) ? doubleMatch : singleMatch;
/* 185 */     } else if (singleMatch == null && doubleMatch != null) {
/* 186 */       inner = doubleMatch;
/* 187 */     } else if (singleMatch != null) {
/* 188 */       inner = singleMatch;
/*     */     } 
/*     */     
/* 191 */     if (inner != null) {
/* 192 */       int numSpaces = StringUtils.countCharOccurrences(inner, ' ');
/* 193 */       for (int i = 0; i <= numSpaces; i++) {
/* 194 */         commandInput.readString();
/*     */       }
/*     */     } else {
/* 197 */       inner = commandInput.peekString();
/* 198 */       if (inner.startsWith("\"") || inner.startsWith("'")) {
/* 199 */         return ArgumentParseResult.failure((Throwable)new StringParseException(commandInput
/* 200 */               .remainingInput(), StringMode.QUOTED, commandContext));
/*     */       }
/*     */ 
/*     */       
/* 204 */       commandInput.readString();
/*     */     } 
/*     */ 
/*     */     
/* 208 */     inner = inner.replace("\\\"", "\"").replace("\\'", "'");
/*     */     
/* 210 */     return ArgumentParseResult.success(inner);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ArgumentParseResult<String> parseGreedy(CommandInput commandInput) {
/* 216 */     int size = commandInput.remainingTokens();
/* 217 */     StringJoiner stringJoiner = new StringJoiner(" ");
/*     */     
/* 219 */     for (int i = 0; i < size; i++) {
/* 220 */       String string = commandInput.peekString();
/*     */       
/* 222 */       if (string.isEmpty()) {
/*     */         break;
/*     */       }
/*     */       
/* 226 */       if (this.stringMode == StringMode.GREEDY_FLAG_YIELDING)
/*     */       {
/* 228 */         if (FLAG_PATTERN.matcher(string).matches()) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       
/* 233 */       stringJoiner.add(commandInput.readStringSkipWhitespace(false));
/*     */     } 
/*     */     
/* 236 */     return ArgumentParseResult.success(stringJoiner.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringMode stringMode() {
/* 245 */     return this.stringMode;
/*     */   }
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public enum StringMode
/*     */   {
/* 251 */     SINGLE,
/* 252 */     QUOTED,
/* 253 */     GREEDY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     GREEDY_FLAG_YIELDING;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class StringParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final StringParser.StringMode stringMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StringParseException(String input, StringParser.StringMode stringMode, CommandContext<?> context) {
/* 281 */       super(StringParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 285 */             CaptionVariable.of("input", input), 
/* 286 */             CaptionVariable.of("stringMode", stringMode.name())
/*     */           });
/* 288 */       this.input = input;
/* 289 */       this.stringMode = stringMode;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 299 */       return this.input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StringParser.StringMode stringMode() {
/* 308 */       return this.stringMode;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\StringParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */