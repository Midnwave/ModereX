/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.value;
/*    */ 
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.adventure.option.Option;
/*    */ import java.util.Locale;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ValueSources
/*    */ {
/* 33 */   static final ValueSource ENVIRONMENT = new EnvironmentVariable("");
/* 34 */   static final ValueSource SYSTEM_PROPERTIES = new SystemProperty("");
/*    */ 
/*    */   
/*    */   static final class EnvironmentVariable
/*    */     implements ValueSource
/*    */   {
/* 40 */     private static final Pattern ENVIRONMENT_SUBST_PATTERN = Pattern.compile("[:\\-/]");
/*    */     
/*    */     private static final String ENVIRONMENT_VAR_SEPARATOR = "_";
/*    */     private final String prefix;
/*    */     
/*    */     EnvironmentVariable(String prefix) {
/* 46 */       this.prefix = prefix.isEmpty() ? "" : (prefix.toUpperCase(Locale.ROOT) + "_");
/*    */     }
/*    */ 
/*    */     
/*    */     public <T> T value(Option<T> option) {
/* 51 */       StringBuffer buf = new StringBuffer(option.id().length() + this.prefix.length());
/* 52 */       buf.append(this.prefix);
/* 53 */       Matcher match = ENVIRONMENT_SUBST_PATTERN.matcher(option.id());
/* 54 */       while (match.find()) {
/* 55 */         match.appendReplacement(buf, "_");
/*    */       }
/* 57 */       match.appendTail(buf);
/*    */       
/* 59 */       String value = System.getenv(buf.toString().toUpperCase(Locale.ROOT));
/* 60 */       if (value == null) {
/* 61 */         return null;
/*    */       }
/*    */       
/* 64 */       return option.valueType().parse(value);
/*    */     }
/*    */   }
/*    */   
/*    */   static final class SystemProperty implements ValueSource {
/* 69 */     private static final Pattern SYSTEM_PROP_SUBST_PATTERN = Pattern.compile("[:/]");
/*    */     
/*    */     private static final String SYSTEM_PROPERTY_SEPARATOR = ".";
/*    */     private final String prefix;
/*    */     
/*    */     SystemProperty(String prefix) {
/* 75 */       this.prefix = prefix.isEmpty() ? "" : (prefix + ".");
/*    */     }
/*    */ 
/*    */     
/*    */     public <T> T value(Option<T> option) {
/* 80 */       StringBuffer buf = new StringBuffer(option.id().length() + this.prefix.length());
/* 81 */       buf.append(this.prefix);
/* 82 */       Matcher match = SYSTEM_PROP_SUBST_PATTERN.matcher(option.id());
/* 83 */       while (match.find()) {
/* 84 */         match.appendReplacement(buf, ".");
/*    */       }
/* 86 */       match.appendTail(buf);
/*    */       
/* 88 */       String value = System.getProperty(buf.toString());
/* 89 */       if (value == null) {
/* 90 */         return null;
/*    */       }
/*    */       
/* 93 */       return option.valueType().parse(value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\adventure\option\value\ValueSources.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */