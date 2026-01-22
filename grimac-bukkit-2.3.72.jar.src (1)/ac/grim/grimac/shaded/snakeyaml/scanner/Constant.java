/*    */ package ac.grim.grimac.shaded.snakeyaml.scanner;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ public final class Constant
/*    */ {
/*    */   private static final String ALPHA_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
/*    */   private static final String LINEBR_S = "\n  ";
/*    */   private static final String FULL_LINEBR_S = "\r\n  ";
/*    */   private static final String NULL_OR_LINEBR_S = "\000\r\n  ";
/*    */   private static final String NULL_BL_LINEBR_S = " \000\r\n  ";
/*    */   private static final String NULL_BL_T_LINEBR_S = "\t \000\r\n  ";
/*    */   private static final String NULL_BL_T_S = "\000 \t";
/*    */   private static final String URI_CHARS_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%";
/* 34 */   public static final Constant LINEBR = new Constant("\n  ");
/* 35 */   public static final Constant NULL_OR_LINEBR = new Constant("\000\r\n  ");
/* 36 */   public static final Constant NULL_BL_LINEBR = new Constant(" \000\r\n  ");
/* 37 */   public static final Constant NULL_BL_T_LINEBR = new Constant("\t \000\r\n  ");
/* 38 */   public static final Constant NULL_BL_T = new Constant("\000 \t");
/* 39 */   public static final Constant URI_CHARS = new Constant("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%");
/*    */   
/* 41 */   public static final Constant ALPHA = new Constant("abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_");
/*    */   
/*    */   private String content;
/* 44 */   boolean[] contains = new boolean[128];
/*    */   boolean noASCII = false;
/*    */   
/*    */   private Constant(String content) {
/* 48 */     Arrays.fill(this.contains, false);
/* 49 */     StringBuilder sb = new StringBuilder();
/* 50 */     for (int i = 0; i < content.length(); i++) {
/* 51 */       int c = content.codePointAt(i);
/* 52 */       if (c < 128) {
/* 53 */         this.contains[c] = true;
/*    */       } else {
/* 55 */         sb.appendCodePoint(c);
/*    */       } 
/*    */     } 
/* 58 */     if (sb.length() > 0) {
/* 59 */       this.noASCII = true;
/* 60 */       this.content = sb.toString();
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean has(int c) {
/* 65 */     return (c < 128) ? this.contains[c] : ((this.noASCII && this.content.indexOf(c) != -1));
/*    */   }
/*    */   
/*    */   public boolean hasNo(int c) {
/* 69 */     return !has(c);
/*    */   }
/*    */   
/*    */   public boolean has(int c, String additional) {
/* 73 */     return (has(c) || additional.indexOf(c) != -1);
/*    */   }
/*    */   
/*    */   public boolean hasNo(int c, String additional) {
/* 77 */     return !has(c, additional);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\scanner\Constant.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */