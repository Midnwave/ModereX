/*     */ package ac.grim.grimac.shaded.snakeyaml.reader;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.Mark;
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.scanner.Constant;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.util.Arrays;
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
/*     */ public class StreamReader
/*     */ {
/*     */   private String name;
/*     */   private final Reader stream;
/*     */   private int[] dataWindow;
/*     */   private int dataLength;
/*  45 */   private int pointer = 0;
/*     */   private boolean eof;
/*  47 */   private int index = 0;
/*  48 */   private int documentIndex = 0;
/*  49 */   private int line = 0;
/*  50 */   private int column = 0;
/*     */   
/*     */   private final char[] buffer;
/*     */   
/*     */   private static final int BUFFER_SIZE = 1025;
/*     */   
/*     */   public StreamReader(String stream) {
/*  57 */     this(new StringReader(stream));
/*  58 */     this.name = "'string'";
/*     */   }
/*     */   
/*     */   public StreamReader(Reader reader) {
/*  62 */     if (reader == null) {
/*  63 */       throw new NullPointerException("Reader must be provided.");
/*     */     }
/*  65 */     this.name = "'reader'";
/*  66 */     this.dataWindow = new int[0];
/*  67 */     this.dataLength = 0;
/*  68 */     this.stream = reader;
/*  69 */     this.eof = false;
/*  70 */     this.buffer = new char[1025];
/*     */   }
/*     */   
/*     */   public static boolean isPrintable(String data) {
/*  74 */     int length = data.length();
/*  75 */     for (int offset = 0; offset < length; ) {
/*  76 */       int codePoint = data.codePointAt(offset);
/*     */       
/*  78 */       if (!isPrintable(codePoint)) {
/*  79 */         return false;
/*     */       }
/*     */       
/*  82 */       offset += Character.charCount(codePoint);
/*     */     } 
/*     */     
/*  85 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean isPrintable(int c) {
/*  89 */     return ((c >= 32 && c <= 126) || c == 9 || c == 10 || c == 13 || c == 133 || (c >= 160 && c <= 55295) || (c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Mark getMark() {
/*  95 */     return new Mark(this.name, this.index, this.line, this.column, this.dataWindow, this.pointer);
/*     */   }
/*     */   
/*     */   public void forward() {
/*  99 */     forward(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forward(int length) {
/* 109 */     for (int i = 0; i < length && ensureEnoughData(); i++) {
/* 110 */       int c = this.dataWindow[this.pointer++];
/* 111 */       moveIndices(1);
/* 112 */       if (Constant.LINEBR.has(c) || (c == 13 && 
/* 113 */         ensureEnoughData() && this.dataWindow[this.pointer] != 10)) {
/* 114 */         this.line++;
/* 115 */         this.column = 0;
/* 116 */       } else if (c != 65279) {
/* 117 */         this.column++;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int peek() {
/* 123 */     return ensureEnoughData() ? this.dataWindow[this.pointer] : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int peek(int index) {
/* 133 */     return ensureEnoughData(index) ? this.dataWindow[this.pointer + index] : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String prefix(int length) {
/* 143 */     if (length == 0)
/* 144 */       return ""; 
/* 145 */     if (ensureEnoughData(length)) {
/* 146 */       return new String(this.dataWindow, this.pointer, length);
/*     */     }
/* 148 */     return new String(this.dataWindow, this.pointer, Math.min(length, this.dataLength - this.pointer));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String prefixForward(int length) {
/* 159 */     String prefix = prefix(length);
/* 160 */     this.pointer += length;
/* 161 */     moveIndices(length);
/*     */     
/* 163 */     this.column += length;
/* 164 */     return prefix;
/*     */   }
/*     */   
/*     */   private boolean ensureEnoughData() {
/* 168 */     return ensureEnoughData(0);
/*     */   }
/*     */   
/*     */   private boolean ensureEnoughData(int size) {
/* 172 */     if (!this.eof && this.pointer + size >= this.dataLength) {
/* 173 */       update();
/*     */     }
/* 175 */     return (this.pointer + size < this.dataLength);
/*     */   }
/*     */   
/*     */   private void update() {
/*     */     try {
/* 180 */       int read = this.stream.read(this.buffer, 0, 1024);
/* 181 */       if (read > 0) {
/* 182 */         int cpIndex = this.dataLength - this.pointer;
/* 183 */         this.dataWindow = Arrays.copyOfRange(this.dataWindow, this.pointer, this.dataLength + read);
/*     */         
/* 185 */         if (Character.isHighSurrogate(this.buffer[read - 1])) {
/* 186 */           if (this.stream.read(this.buffer, read, 1) == -1) {
/* 187 */             this.eof = true;
/*     */           } else {
/* 189 */             read++;
/*     */           } 
/*     */         }
/*     */         
/* 193 */         int nonPrintable = 32;
/* 194 */         for (int i = 0; i < read; cpIndex++) {
/* 195 */           int codePoint = Character.codePointAt(this.buffer, i);
/* 196 */           this.dataWindow[cpIndex] = codePoint;
/* 197 */           if (isPrintable(codePoint)) {
/* 198 */             i += Character.charCount(codePoint);
/*     */           } else {
/* 200 */             nonPrintable = codePoint;
/* 201 */             i = read;
/*     */           } 
/*     */         } 
/*     */         
/* 205 */         this.dataLength = cpIndex;
/* 206 */         this.pointer = 0;
/* 207 */         if (nonPrintable != 32) {
/* 208 */           throw new ReaderException(this.name, cpIndex - 1, nonPrintable, "special characters are not allowed");
/*     */         }
/*     */       } else {
/*     */         
/* 212 */         this.eof = true;
/*     */       } 
/* 214 */     } catch (IOException ioe) {
/* 215 */       throw new YAMLException(ioe);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumn() {
/* 221 */     return this.column;
/*     */   }
/*     */   
/*     */   private void moveIndices(int length) {
/* 225 */     this.index += length;
/* 226 */     this.documentIndex += length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDocumentIndex() {
/* 235 */     return this.documentIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetDocumentIndex() {
/* 242 */     this.documentIndex = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndex() {
/* 249 */     return this.index;
/*     */   }
/*     */   
/*     */   public int getLine() {
/* 253 */     return this.line;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\reader\StreamReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */