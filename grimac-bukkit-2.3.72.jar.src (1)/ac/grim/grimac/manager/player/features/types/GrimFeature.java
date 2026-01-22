/*    */ package ac.grim.grimac.manager.player.features.types;
/*    */ public abstract class GrimFeature {
/*    */   private final String name;
/*    */   
/*    */   public abstract void setState(GrimPlayer paramGrimPlayer, ConfigManager paramConfigManager, FeatureState paramFeatureState);
/*    */   
/*    */   @Generated
/*    */   public GrimFeature(String name) {
/*  9 */     this.name = name;
/*    */   } public abstract boolean isEnabled(GrimPlayer paramGrimPlayer); public abstract boolean isEnabledInConfig(GrimPlayer paramGrimPlayer, ConfigManager paramConfigManager);
/*    */   @Generated
/*    */   public String getName() {
/* 13 */     return this.name;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\types\GrimFeature.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */