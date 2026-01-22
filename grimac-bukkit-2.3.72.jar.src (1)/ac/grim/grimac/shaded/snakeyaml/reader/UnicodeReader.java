/*     */ package ac.grim.grimac.shaded.snakeyaml.reader;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PushbackInputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class UnicodeReader
/*     */   extends Reader
/*     */ {
/*  45 */   private static final Charset UTF8 = StandardCharsets.UTF_8;
/*  46 */   private static final Charset UTF16BE = StandardCharsets.UTF_16BE;
/*  47 */   private static final Charset UTF16LE = StandardCharsets.UTF_16LE;
/*     */   
/*     */   PushbackInputStream internalIn;
/*  50 */   InputStreamReader internalIn2 = null;
/*     */ 
/*     */   
/*     */   private static final int BOM_SIZE = 3;
/*     */ 
/*     */ 
/*     */   
/*     */   public UnicodeReader(InputStream in) {
/*  58 */     this.internalIn = new PushbackInputStream(in, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEncoding() {
/*  68 */     return this.internalIn2.getEncoding();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void init() throws IOException {
/*     */     Charset encoding;
/*     */     int unread;
/*  78 */     if (this.internalIn2 != null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  83 */     byte[] bom = new byte[3];
/*     */     
/*  85 */     int n = this.internalIn.read(bom, 0, bom.length);
/*     */     
/*  87 */     if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
/*  88 */       encoding = UTF8;
/*  89 */       unread = n - 3;
/*  90 */     } else if (bom[0] == -2 && bom[1] == -1) {
/*  91 */       encoding = UTF16BE;
/*  92 */       unread = n - 2;
/*  93 */     } else if (bom[0] == -1 && bom[1] == -2) {
/*  94 */       encoding = UTF16LE;
/*  95 */       unread = n - 2;
/*     */     } else {
/*     */       
/*  98 */       encoding = UTF8;
/*  99 */       unread = n;
/*     */     } 
/*     */     
/* 102 */     if (unread > 0) {
/* 103 */       this.internalIn.unread(bom, n - unread, unread);
/*     */     }
/*     */ 
/*     */     
/* 107 */     CharsetDecoder decoder = encoding.newDecoder().onUnmappableCharacter(CodingErrorAction.REPORT);
/* 108 */     this.internalIn2 = new InputStreamReader(this.internalIn, decoder);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 112 */     init();
/* 113 */     this.internalIn2.close();
/*     */   }
/*     */   
/*     */   public int read(char[] cbuf, int off, int len) throws IOException {
/* 117 */     init();
/* 118 */     return this.internalIn2.read(cbuf, off, len);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\reader\UnicodeReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */