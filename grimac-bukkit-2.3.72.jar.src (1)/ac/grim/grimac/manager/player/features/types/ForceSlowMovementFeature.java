/*    */ package ac.grim.grimac.manager.player.features.types;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.api.feature.FeatureState;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class ForceSlowMovementFeature
/*    */   extends GrimFeature {
/*    */   public ForceSlowMovementFeature() {
/* 10 */     super("ForceSlowMovement");
/*    */   }
/*    */ 
/*    */   
/*    */   public void setState(GrimPlayer player, ConfigManager config, FeatureState state) {
/* 15 */     switch (state) { case ENABLED:
/* 16 */         player.setForceSlowMovement(true); return;
/* 17 */       case DISABLED: player.setForceSlowMovement(false); return; }
/* 18 */      player.setForceSlowMovement(isEnabledInConfig(player, config));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEnabled(GrimPlayer player) {
/* 24 */     return player.isForceSlowMovement();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabledInConfig(GrimPlayer player, ConfigManager config) {
/* 29 */     return config.getBooleanElse("force-slow-movement", true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\types\ForceSlowMovementFeature.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */