/*    */ package ac.grim.grimac.manager.player.features.types;
/*    */ 
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.api.feature.FeatureState;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ 
/*    */ public class ExemptElytraFeature
/*    */   extends GrimFeature {
/*    */   public ExemptElytraFeature() {
/* 10 */     super("ExemptElytra");
/*    */   }
/*    */ 
/*    */   
/*    */   public void setState(GrimPlayer player, ConfigManager config, FeatureState state) {
/* 15 */     switch (state) { case ENABLED:
/* 16 */         player.setExemptElytra(true); return;
/* 17 */       case DISABLED: player.setExemptElytra(false); return; }
/* 18 */      player.setExemptElytra(isEnabledInConfig(player, config));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isEnabled(GrimPlayer player) {
/* 24 */     return player.isExemptElytra();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabledInConfig(GrimPlayer player, ConfigManager config) {
/* 29 */     return config.getBooleanElse("exempt-elytra", false);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\types\ExemptElytraFeature.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */