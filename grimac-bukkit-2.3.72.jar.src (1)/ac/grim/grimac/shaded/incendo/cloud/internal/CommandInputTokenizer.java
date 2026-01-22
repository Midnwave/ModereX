/*    */ package ac.grim.grimac.shaded.incendo.cloud.internal;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.StringTokenizer;
/*    */ import org.apiguardian.api.API;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ public final class CommandInputTokenizer
/*    */ {
/*    */   private static final String DELIMITER = " ";
/*    */   private static final String EMPTY = "";
/* 42 */   private final StringTokenizerFactory stringTokenizerFactory = new StringTokenizerFactory();
/*    */ 
/*    */ 
/*    */   
/*    */   private final String input;
/*    */ 
/*    */ 
/*    */   
/*    */   public CommandInputTokenizer(String input) {
/* 51 */     this.input = input;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LinkedList<String> tokenize() {
/* 61 */     StringTokenizer stringTokenizer = this.stringTokenizerFactory.createStringTokenizer();
/* 62 */     LinkedList<String> tokens = new LinkedList<>();
/* 63 */     while (stringTokenizer.hasMoreElements()) {
/* 64 */       tokens.add(stringTokenizer.nextToken());
/*    */     }
/* 66 */     if (this.input.endsWith(" ")) {
/* 67 */       tokens.add("");
/*    */     }
/* 69 */     return tokens;
/*    */   }
/*    */ 
/*    */   
/*    */   private final class StringTokenizerFactory
/*    */   {
/*    */     private StringTokenizerFactory() {}
/*    */ 
/*    */     
/*    */     private StringTokenizer createStringTokenizer() {
/* 79 */       return new StringTokenizer(CommandInputTokenizer.this.input, " ");
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\internal\CommandInputTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */