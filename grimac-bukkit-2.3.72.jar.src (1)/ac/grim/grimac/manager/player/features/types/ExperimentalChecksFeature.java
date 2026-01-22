/*    */ package ac.grim.grimac.manager.player.features.types;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.api.feature.FeatureState;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class ExperimentalChecksFeature
/*    */   extends GrimFeature {
/*    */   public ExperimentalChecksFeature() {
/* 10 */     super("ExperimentalChecks");
/*    */   }
/*    */ 
/*    */   
/*    */   public void setState(GrimPlayer player, ConfigManager config, FeatureState state) {
/* 15 */     switch (state) { case ENABLED:
/* 16 */         player.setExperimentalChecks(true); return;
/* 17 */       case DISABLED: player.setExperimentalChecks(false); return; }
/* 18 */      player.setExperimentalChecks(isEnabledInConfig(player, config));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEnabled(GrimPlayer player) {
/* 24 */     return player.isExperimentalChecks();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabledInConfig(GrimPlayer player, ConfigManager config) {
/* 29 */     return config.getBooleanElse("experimental-checks", false);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\types\ExperimentalChecksFeature.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */