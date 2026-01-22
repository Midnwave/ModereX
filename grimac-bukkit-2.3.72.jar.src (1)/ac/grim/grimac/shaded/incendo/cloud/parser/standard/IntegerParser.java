/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser.standard;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.parsing.NumberParseException;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.IntRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.Range;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ public final class IntegerParser<C>
/*     */   extends NumberParser<C, Integer, IntRange>
/*     */   implements BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final int DEFAULT_MINIMUM = -2147483648;
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final int DEFAULT_MAXIMUM = 2147483647;
/*     */   private static final int MAX_SUGGESTIONS_INCREMENT = 10;
/*     */   private static final int NUMBER_SHIFT_MULTIPLIER = 10;
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Integer> integerParser() {
/*  72 */     return integerParser(-2147483648, 2147483647);
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
/*     */   public static <C> ParserDescriptor<C, Integer> integerParser(int minValue) {
/*  86 */     return ParserDescriptor.of(new IntegerParser(minValue, 2147483647), Integer.class);
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
/*     */   public static <C> ParserDescriptor<C, Integer> integerParser(int minValue, int maxValue) {
/* 102 */     return ParserDescriptor.of(new IntegerParser(minValue, maxValue), Integer.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Integer> integerComponent() {
/* 113 */     return CommandComponent.builder().parser(integerParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntegerParser(int min, int max) {
/* 123 */     super(Range.intRange(min, max));
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
/*     */   public static List<String> getSuggestions(Range<? extends Number> range, CommandInput input) {
/* 138 */     Set<Long> numbers = new TreeSet<>();
/* 139 */     String token = input.peekString();
/*     */     
/*     */     try {
/* 142 */       long inputNum = Long.parseLong(token.equals("-") ? "-0" : (token.isEmpty() ? "0" : token));
/* 143 */       long inputNumAbsolute = Math.abs(inputNum);
/*     */       
/* 145 */       long min = range.min().longValue();
/* 146 */       long max = range.max().longValue();
/*     */       
/* 148 */       numbers.add(Long.valueOf(inputNumAbsolute));
/* 149 */       int i = 0;
/* 150 */       for (; i < 10 && inputNum * 10L + i <= max; i++) {
/* 151 */         numbers.add(Long.valueOf(inputNumAbsolute * 10L + i));
/*     */       }
/*     */       
/* 154 */       List<String> suggestions = new LinkedList<>();
/* 155 */       for (Iterator<Long> iterator = numbers.iterator(); iterator.hasNext(); ) { long number = ((Long)iterator.next()).longValue();
/* 156 */         if (token.startsWith("-")) {
/* 157 */           number = -number;
/*     */         }
/* 159 */         if (number < min || number > max) {
/*     */           continue;
/*     */         }
/* 162 */         suggestions.add(String.valueOf(number)); }
/*     */ 
/*     */       
/* 165 */       return suggestions;
/* 166 */     } catch (Exception ignored) {
/* 167 */       return Collections.emptyList();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Integer> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 176 */     if (!commandInput.isValidInteger(range())) {
/* 177 */       return ArgumentParseResult.failure((Throwable)new IntegerParseException(commandInput
/* 178 */             .peekString(), this, commandContext));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 183 */     return ArgumentParseResult.success(Integer.valueOf(commandInput.readInteger()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMax() {
/* 188 */     return (range().maxInt() != Integer.MAX_VALUE);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasMin() {
/* 193 */     return (range().minInt() != Integer.MIN_VALUE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 201 */     return getSuggestions((Range<? extends Number>)range(), input);
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
/*     */   public static final class IntegerParseException
/*     */     extends NumberParseException
/*     */   {
/*     */     @API(status = API.Status.STABLE)
/*     */     public IntegerParseException(String input, IntegerParser<?> parser, CommandContext<?> commandContext) {
/* 221 */       super(input, parser, commandContext);
/*     */     }
/*     */ 
/*     */     
/*     */     public String numberType() {
/* 226 */       return "integer";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\IntegerParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */