/*    */ package ac.grim.grimac.utils.lists;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class EvictingQueue<K>
/*    */   extends ArrayList<K>
/*    */ {
/*    */   private final int maxSize;
/*    */   
/*    */   public EvictingQueue(int size) {
/* 11 */     this.maxSize = size;
/*    */   }
/*    */   
/*    */   public boolean add(K k) {
/* 15 */     boolean r = super.add(k);
/* 16 */     if (size() > this.maxSize) {
/* 17 */       removeRange(0, size() - this.maxSize);
/*    */     }
/* 19 */     return r;
/*    */   }
/*    */   
/*    */   public K getYoungest() {
/* 23 */     return get(size() - 1);
/*    */   }
/*    */   
/*    */   public K getOldest() {
/* 27 */     return get(0);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\lists\EvictingQueue.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */