/*    */ package ac.grim.grimac.utils.change;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentLinkedDeque;
/*    */ import java.util.function.Predicate;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerBlockHistory
/*    */ {
/* 10 */   private final ConcurrentLinkedDeque<BlockModification> blockHistory = new ConcurrentLinkedDeque<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(BlockModification modification) {
/* 18 */     this.blockHistory.add(modification);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Iterable<BlockModification> getRecentModifications(Predicate<BlockModification> filter) {
/* 28 */     return this.blockHistory.stream().filter(filter).toList();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void cleanup(int maxTick) {
/* 37 */     while (!this.blockHistory.isEmpty() && maxTick - ((BlockModification)this.blockHistory.peekFirst()).tick() > 0) {
/* 38 */       this.blockHistory.pollFirst();
/*    */     }
/*    */   }
/*    */   
/*    */   public int size() {
/* 43 */     return this.blockHistory.size();
/*    */   }
/*    */   
/*    */   public void clear() {
/* 47 */     this.blockHistory.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\change\PlayerBlockHistory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */