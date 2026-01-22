/*    */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class Tokens
/*    */ {
/*    */   static final char COMPOUND_BEGIN = '{';
/*    */   static final char COMPOUND_END = '}';
/*    */   static final char COMPOUND_KEY_TERMINATOR = ':';
/*    */   static final char ARRAY_BEGIN = '[';
/*    */   static final char ARRAY_END = ']';
/*    */   static final char ARRAY_SIGNATURE_SEPARATOR = ';';
/*    */   static final char VALUE_SEPARATOR = ',';
/*    */   static final char SINGLE_QUOTE = '\'';
/*    */   static final char DOUBLE_QUOTE = '"';
/*    */   static final char ESCAPE_MARKER = '\\';
/*    */   static final char TYPE_BYTE = 'b';
/*    */   static final char TYPE_SHORT = 's';
/*    */   static final char TYPE_INT = 'i';
/*    */   static final char TYPE_LONG = 'l';
/*    */   static final char TYPE_FLOAT = 'f';
/*    */   static final char TYPE_DOUBLE = 'd';
/*    */   static final char TYPE_SIGNED = 's';
/*    */   static final char TYPE_UNSIGNED = 'u';
/*    */   static final String LITERAL_TRUE = "true";
/*    */   static final String LITERAL_FALSE = "false";
/* 56 */   static final String NEWLINE = System.getProperty("line.separator", "\n");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static final char EOF = '\000';
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static boolean id(char c) {
/* 71 */     return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.' || c == '+');
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
/*    */ 
/*    */   
/*    */   static boolean numericType(char c) {
/* 85 */     c = Character.toLowerCase(c);
/* 86 */     return (c == 'b' || c == 's' || c == 'i' || c == 'l' || c == 'f' || c == 'd');
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\Tokens.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */