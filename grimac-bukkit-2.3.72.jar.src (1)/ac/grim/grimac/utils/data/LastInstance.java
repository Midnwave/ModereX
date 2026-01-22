/*    */ package ac.grim.grimac.utils.data;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class LastInstance {
/*  6 */   int lastInstance = 100;
/*    */   
/*    */   public LastInstance(GrimPlayer player) {
/*  9 */     player.lastInstanceManager.addInstance(this);
/*    */   }
/*    */   
/*    */   public boolean hasOccurredSince(int time) {
/* 13 */     return (this.lastInstance <= time);
/*    */   }
/*    */   
/*    */   public void reset() {
/* 17 */     this.lastInstance = 0;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void tick() {
/* 23 */     if (this.lastInstance == Integer.MAX_VALUE) this.lastInstance = 100; 
/* 24 */     this.lastInstance++;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\data\LastInstance.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */