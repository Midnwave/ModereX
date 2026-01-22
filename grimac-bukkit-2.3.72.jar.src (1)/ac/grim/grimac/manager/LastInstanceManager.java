/*    */ package ac.grim.grimac.manager;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import ac.grim.grimac.utils.data.LastInstance;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LastInstanceManager
/*    */   extends Check implements PostPredictionCheck {
/* 13 */   private final List<LastInstance> instances = new ArrayList<>();
/*    */   
/*    */   public LastInstanceManager(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */   
/*    */   public void addInstance(LastInstance instance) {
/* 20 */     this.instances.add(instance);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 25 */     for (LastInstance instance : this.instances)
/* 26 */       instance.tick(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\LastInstanceManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */