/*    */ package ac.grim.grimac.manager.player.features.types;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.api.feature.FeatureState;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class ForceStuckSpeedFeature
/*    */   extends GrimFeature {
/*    */   public ForceStuckSpeedFeature() {
/* 10 */     super("ForceStuckSpeed");
/*    */   }
/*    */ 
/*    */   
/*    */   public void setState(GrimPlayer player, ConfigManager config, FeatureState state) {
/* 15 */     switch (state) { case ENABLED:
/* 16 */         player.setForceStuckSpeed(true); return;
/* 17 */       case DISABLED: player.setForceStuckSpeed(false); return; }
/* 18 */      player.setForceStuckSpeed(isEnabledInConfig(player, config));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEnabled(GrimPlayer player) {
/* 24 */     return player.isForceStuckSpeed();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabledInConfig(GrimPlayer player, ConfigManager config) {
/* 29 */     return config.getBooleanElse("force-stuck-speed", true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\types\ForceStuckSpeedFeature.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */