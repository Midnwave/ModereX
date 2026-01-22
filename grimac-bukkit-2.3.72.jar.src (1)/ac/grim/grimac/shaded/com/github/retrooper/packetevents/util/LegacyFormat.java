/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class LegacyFormat
/*    */ {
/*    */   public static String trimLegacyFormat(String text, int length) {
/* 10 */     if (text.length() <= length) {
/* 11 */       return text;
/*    */     }
/*    */     
/* 14 */     if (text.charAt(length - 1) == '§') {
/* 15 */       return text.substring(0, length - 1);
/*    */     }
/*    */     
/* 18 */     return text.substring(0, length);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\LegacyFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */