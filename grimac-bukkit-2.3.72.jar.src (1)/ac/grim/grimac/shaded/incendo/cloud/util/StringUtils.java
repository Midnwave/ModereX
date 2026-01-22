/*     */ package ac.grim.grimac.shaded.incendo.cloud.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import java.util.Locale;
/*     */ import java.util.function.Function;
/*     */ import java.util.regex.MatchResult;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class StringUtils
/*     */ {
/*     */   public static int countCharOccurrences(String haystack, char needle) {
/*  53 */     int occurrences = 0;
/*  54 */     for (int i = 0; i < haystack.length(); i++) {
/*  55 */       if (haystack.charAt(i) == needle) {
/*  56 */         occurrences++;
/*     */       }
/*     */     } 
/*  59 */     return occurrences;
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
/*     */   
/*     */   public static String replaceAll(String string, Pattern pattern, Function<MatchResult, String> replacer) {
/*  75 */     Matcher matcher = pattern.matcher(string);
/*  76 */     matcher.reset();
/*  77 */     boolean result = matcher.find();
/*  78 */     if (result) {
/*  79 */       StringBuffer sb = new StringBuffer();
/*     */       while (true) {
/*  81 */         String replacement = replacer.apply(matcher);
/*  82 */         matcher.appendReplacement(sb, replacement);
/*  83 */         result = matcher.find();
/*  84 */         if (!result)
/*  85 */         { matcher.appendTail(sb);
/*  86 */           return sb.toString(); } 
/*     */       } 
/*  88 */     }  return string;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String trimBeforeLastSpace(String suggestion, String input) {
/*  99 */     int lastSpace = input.lastIndexOf(' ');
/*     */     
/* 101 */     if (lastSpace == -1) {
/* 102 */       return suggestion;
/*     */     }
/*     */     
/* 105 */     if (suggestion.toLowerCase(Locale.ROOT).startsWith(input.toLowerCase(Locale.ROOT).substring(0, lastSpace))) {
/* 106 */       return suggestion.substring(lastSpace + 1);
/*     */     }
/*     */     
/* 109 */     return null;
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
/*     */   public static String trimBeforeLastSpace(String suggestion, CommandInput commandInput) {
/*     */     String input;
/* 122 */     if (commandInput.isEmpty(true)) {
/* 123 */       input = "";
/*     */     } else {
/* 125 */       input = commandInput.copy().skipWhitespace().remainingInput();
/*     */     } 
/* 127 */     return trimBeforeLastSpace(suggestion, input);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\clou\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */