/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage.internal;
/*    */ 
/*    */ import ac.grim.grimac.shaded.intellij.lang.annotations.RegExp;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.TagPattern;
/*    */ import java.util.Locale;
/*    */ import java.util.Objects;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Internal
/*    */ public final class TagInternals
/*    */ {
/*    */   @RegExp
/*    */   public static final String TAG_NAME_REGEX = "[!?#]?[a-z0-9_-]*";
/* 42 */   private static final Pattern TAG_NAME_PATTERN = Pattern.compile("[!?#]?[a-z0-9_-]*");
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
/*    */   public static void assertValidTagName(@TagPattern @NotNull String tagName) {
/* 55 */     if (!TAG_NAME_PATTERN.matcher(Objects.<CharSequence>requireNonNull(tagName)).matches()) {
/* 56 */       throw new IllegalArgumentException("Tag name must match pattern " + TAG_NAME_PATTERN.pattern() + ", was " + tagName);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean sanitizeAndCheckValidTagName(@TagPattern @NotNull String tagName) {
/* 69 */     return TAG_NAME_PATTERN.matcher(((String)Objects.<String>requireNonNull(tagName)).toLowerCase(Locale.ROOT)).matches();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void sanitizeAndAssertValidTagName(@TagPattern @NotNull String tagName) {
/* 81 */     assertValidTagName(((String)Objects.<String>requireNonNull(tagName)).toLowerCase(Locale.ROOT));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\internal\TagInternals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */