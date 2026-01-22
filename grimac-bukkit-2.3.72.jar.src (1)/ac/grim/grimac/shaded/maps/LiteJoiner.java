/*    */ package ac.grim.grimac.shaded.maps;
/*    */ 
/*    */ import java.util.Iterator;
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
/*    */ public class LiteJoiner
/*    */ {
/*    */   private final String separator;
/*    */   
/*    */   public static LiteJoiner on(String separator) {
/* 24 */     return new LiteJoiner(separator);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private LiteJoiner(String separator) {
/* 30 */     this.separator = separator;
/*    */   }
/*    */   
/*    */   public String join(Iterable<?> parts) {
/* 34 */     StringBuilder joined = new StringBuilder();
/* 35 */     Iterator<?> partIterator = parts.iterator();
/* 36 */     while (partIterator.hasNext()) {
/* 37 */       joined.append(partIterator.next());
/* 38 */       if (partIterator.hasNext()) joined.append(this.separator); 
/*    */     } 
/* 40 */     return joined.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\LiteJoiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */