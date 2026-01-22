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
/*     */ import java.time.Duration;
/*     */ import java.util.Collections;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.IntStream;
/*     */ import java.util.stream.Stream;
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
/*     */ public final class DurationParser<C>
/*     */   implements ArgumentParser<C, Duration>, BlockingSuggestionProvider.Strings<C>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> ParserDescriptor<C, Duration> durationParser() {
/*  62 */     return ParserDescriptor.of(new DurationParser(), Duration.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static <C> CommandComponent.Builder<C, Duration> durationComponent() {
/*  73 */     return CommandComponent.builder().parser(durationParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   private static final Pattern DURATION_PATTERN = Pattern.compile("(([1-9][0-9]+|[1-9])[dhms])");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArgumentParseResult<Duration> parse(CommandContext<C> commandContext, CommandInput commandInput) {
/*  86 */     String input = commandInput.readString();
/*     */     
/*  88 */     Matcher matcher = DURATION_PATTERN.matcher(input);
/*     */     
/*  90 */     Duration duration = Duration.ofNanos(0L);
/*     */     
/*  92 */     while (matcher.find()) {
/*  93 */       String group = matcher.group();
/*  94 */       String timeUnit = String.valueOf(group.charAt(group.length() - 1));
/*  95 */       int timeValue = Integer.parseInt(group.substring(0, group.length() - 1));
/*  96 */       switch (timeUnit) {
/*     */         case "d":
/*  98 */           duration = duration.plusDays(timeValue);
/*     */           continue;
/*     */         case "h":
/* 101 */           duration = duration.plusHours(timeValue);
/*     */           continue;
/*     */         case "m":
/* 104 */           duration = duration.plusMinutes(timeValue);
/*     */           continue;
/*     */         case "s":
/* 107 */           duration = duration.plusSeconds(timeValue);
/*     */           continue;
/*     */       } 
/* 110 */       return ArgumentParseResult.failure((Throwable)new DurationParseException(input, commandContext));
/*     */     } 
/*     */ 
/*     */     
/* 114 */     if (duration.isZero()) {
/* 115 */       return ArgumentParseResult.failure((Throwable)new DurationParseException(input, commandContext));
/*     */     }
/*     */     
/* 118 */     return ArgumentParseResult.success(duration);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 126 */     if (input.isEmpty(true)) {
/* 127 */       return (Iterable<String>)IntStream.range(1, 10).boxed()
/* 128 */         .sorted()
/* 129 */         .map(String::valueOf)
/* 130 */         .collect(Collectors.toList());
/*     */     }
/*     */ 
/*     */     
/* 134 */     if (Character.isLetter(input.lastRemainingCharacter())) {
/* 135 */       return Collections.emptyList();
/*     */     }
/*     */ 
/*     */     
/* 139 */     String string = input.readString();
/* 140 */     return (Iterable<String>)Stream.<String>of(new String[] { "d", "h", "m", "s"
/* 141 */         }).filter(unit -> !string.contains(unit))
/* 142 */       .map(unit -> string + unit)
/* 143 */       .collect(Collectors.toList());
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
/*     */   public static final class DurationParseException
/*     */     extends ParserException
/*     */   {
/*     */     private final String input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public DurationParseException(String input, CommandContext<?> context) {
/* 166 */       super(DurationParser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION, new CaptionVariable[] {
/*     */ 
/*     */ 
/*     */             
/* 170 */             CaptionVariable.of("input", input)
/*     */           });
/* 172 */       this.input = input;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String input() {
/* 181 */       return this.input;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\standard\DurationParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */