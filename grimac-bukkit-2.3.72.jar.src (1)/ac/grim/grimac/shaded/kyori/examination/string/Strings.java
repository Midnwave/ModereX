/*    */ package ac.grim.grimac.shaded.kyori.examination.string;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.stream.Stream;
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
/*    */ final class Strings
/*    */ {
/*    */   @NotNull
/*    */   static String withSuffix(String string, char suffix) {
/* 34 */     return string + suffix;
/*    */   }
/*    */   @NotNull
/*    */   static String wrapIn(String string, char wrap) {
/* 38 */     return wrap + string + wrap;
/*    */   }
/*    */   
/*    */   static int maxLength(Stream<String> strings) {
/* 42 */     return strings.mapToInt(String::length).max().orElse(0);
/*    */   }
/*    */   @NotNull
/*    */   static String repeat(@NotNull String string, int count) {
/* 46 */     if (count == 0)
/* 47 */       return ""; 
/* 48 */     if (count == 1) {
/* 49 */       return string;
/*    */     }
/* 51 */     StringBuilder sb = new StringBuilder(string.length() * count);
/* 52 */     for (int i = 0; i < count; i++) {
/* 53 */       sb.append(string);
/*    */     }
/* 55 */     return sb.toString();
/*    */   }
/*    */   @NotNull
/*    */   static String padEnd(@NotNull String string, int minLength, char padding) {
/* 59 */     return (string.length() >= minLength) ? 
/* 60 */       string : 
/* 61 */       String.format("%-" + minLength + "s", new Object[] { Character.valueOf(padding) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\examination\string\Strings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */