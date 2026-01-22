/*     */ package ac.grim.grimac.shaded.snakeyaml.error;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.scanner.Constant;
/*     */ import java.io.Serializable;
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
/*     */ public final class Mark
/*     */   implements Serializable
/*     */ {
/*     */   private final String name;
/*     */   private final int index;
/*     */   private final int line;
/*     */   private final int column;
/*     */   private final int[] buffer;
/*     */   private final int pointer;
/*     */   
/*     */   private static int[] toCodePoints(char[] str) {
/*  33 */     int[] codePoints = new int[Character.codePointCount(str, 0, str.length)];
/*  34 */     for (int i = 0, c = 0; i < str.length; c++) {
/*  35 */       int cp = Character.codePointAt(str, i);
/*  36 */       codePoints[c] = cp;
/*  37 */       i += Character.charCount(cp);
/*     */     } 
/*  39 */     return codePoints;
/*     */   }
/*     */   
/*     */   public Mark(String name, int index, int line, int column, char[] str, int pointer) {
/*  43 */     this(name, index, line, column, toCodePoints(str), pointer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Mark(String name, int index, int line, int column, int[] buffer, int pointer) {
/*  48 */     this.name = name;
/*  49 */     this.index = index;
/*  50 */     this.line = line;
/*  51 */     this.column = column;
/*  52 */     this.buffer = buffer;
/*  53 */     this.pointer = pointer;
/*     */   }
/*     */   
/*     */   private boolean isLineBreak(int c) {
/*  57 */     return Constant.NULL_OR_LINEBR.has(c);
/*     */   }
/*     */   
/*     */   public String get_snippet(int indent, int max_length) {
/*  61 */     float half = max_length / 2.0F - 1.0F;
/*  62 */     int start = this.pointer;
/*  63 */     String head = "";
/*  64 */     while (start > 0 && !isLineBreak(this.buffer[start - 1])) {
/*  65 */       start--;
/*  66 */       if ((this.pointer - start) > half) {
/*  67 */         head = " ... ";
/*  68 */         start += 5;
/*     */         break;
/*     */       } 
/*     */     } 
/*  72 */     String tail = "";
/*  73 */     int end = this.pointer;
/*  74 */     while (end < this.buffer.length && !isLineBreak(this.buffer[end])) {
/*  75 */       end++;
/*  76 */       if ((end - this.pointer) > half) {
/*  77 */         tail = " ... ";
/*  78 */         end -= 5;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*  83 */     StringBuilder result = new StringBuilder(); int i;
/*  84 */     for (i = 0; i < indent; i++) {
/*  85 */       result.append(" ");
/*     */     }
/*  87 */     result.append(head);
/*  88 */     for (i = start; i < end; i++) {
/*  89 */       result.appendCodePoint(this.buffer[i]);
/*     */     }
/*  91 */     result.append(tail);
/*  92 */     result.append("\n");
/*  93 */     for (i = 0; i < indent + this.pointer - start + head.length(); i++) {
/*  94 */       result.append(" ");
/*     */     }
/*  96 */     result.append("^");
/*  97 */     return result.toString();
/*     */   }
/*     */   
/*     */   public String get_snippet() {
/* 101 */     return get_snippet(4, 75);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     String snippet = get_snippet();
/* 107 */     String builder = " in " + this.name + ", line " + (this.line + 1) + ", column " + (this.column + 1) + ":\n" + snippet;
/*     */     
/* 109 */     return builder;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 113 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLine() {
/* 122 */     return this.line;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumn() {
/* 131 */     return this.column;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndex() {
/* 140 */     return this.index;
/*     */   }
/*     */   
/*     */   public int[] getBuffer() {
/* 144 */     return this.buffer;
/*     */   }
/*     */   
/*     */   public int getPointer() {
/* 148 */     return this.pointer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\error\Mark.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */