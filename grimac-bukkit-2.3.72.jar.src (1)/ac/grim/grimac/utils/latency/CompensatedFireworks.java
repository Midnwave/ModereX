/*    */ package ac.grim.grimac.utils.latency;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class CompensatedFireworks
/*    */   extends Check
/*    */   implements PostPredictionCheck
/*    */ {
/* 14 */   private final Set<Integer> activeFireworks = new HashSet<>();
/* 15 */   private final Set<Integer> fireworksToRemoveNextTick = new HashSet<>();
/*    */   
/*    */   public CompensatedFireworks(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 25 */     this.activeFireworks.removeAll(this.fireworksToRemoveNextTick);
/* 26 */     this.fireworksToRemoveNextTick.clear();
/*    */   }
/*    */   
/*    */   public boolean hasFirework(int entityId) {
/* 30 */     return this.activeFireworks.contains(Integer.valueOf(entityId));
/*    */   }
/*    */   
/*    */   public void addNewFirework(int entityID) {
/* 34 */     this.activeFireworks.add(Integer.valueOf(entityID));
/*    */   }
/*    */   
/*    */   public void removeFirework(int entityID) {
/* 38 */     if (this.activeFireworks.contains(Integer.valueOf(entityID))) {
/* 39 */       this.fireworksToRemoveNextTick.add(Integer.valueOf(entityID));
/*    */     }
/*    */   }
/*    */   
/*    */   public int getMaxFireworksAppliedPossible() {
/* 44 */     return this.activeFireworks.size();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\latency\CompensatedFireworks.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */