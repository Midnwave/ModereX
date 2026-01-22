/*     */ package ac.grim.grimac.shaded.incendo.cloud.caption;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface CaptionFormatter<C, T>
/*     */ {
/*     */   static <C> CaptionFormatter<C, String> patternReplacing(Pattern pattern) {
/*  50 */     return new PatternReplacingCaptionFormatter<>(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <C> CaptionFormatter<C, String> placeholderReplacing() {
/*  61 */     return new PatternReplacingCaptionFormatter<>(placeholderPattern());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Pattern placeholderPattern() {
/*  71 */     return Pattern.compile("<(\\S+)>");
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
/*     */ 
/*     */   
/*     */   T formatCaption(Caption captionKey, C recipient, String caption, CaptionVariable... variables) {
/*  89 */     return formatCaption(captionKey, recipient, caption, Arrays.asList(variables));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   T formatCaption(Caption paramCaption, C paramC, String paramString, List<CaptionVariable> paramList);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class PatternReplacingCaptionFormatter<C>
/*     */     implements CaptionFormatter<C, String>
/*     */   {
/*     */     private final Pattern pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private PatternReplacingCaptionFormatter(Pattern pattern) {
/* 114 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String formatCaption(Caption captionKey, C recipient, String caption, List<CaptionVariable> variables) {
/* 124 */       Map<String, String> replacements = new HashMap<>();
/* 125 */       for (CaptionVariable variable : variables) {
/* 126 */         replacements.put(variable.key(), variable.value());
/*     */       }
/*     */       
/* 129 */       Matcher matcher = this.pattern.matcher(caption);
/* 130 */       StringBuffer stringBuffer = new StringBuffer();
/* 131 */       while (matcher.find()) {
/* 132 */         String replacement = replacements.get(matcher.group(1));
/* 133 */         matcher.appendReplacement(stringBuffer, (replacement == null) ? "$0" : replacement);
/*     */       } 
/* 135 */       matcher.appendTail(stringBuffer);
/*     */       
/* 137 */       return stringBuffer.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\caption\CaptionFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */