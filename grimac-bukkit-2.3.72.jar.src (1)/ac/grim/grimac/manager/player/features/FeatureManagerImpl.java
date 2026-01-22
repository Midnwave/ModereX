/*    */ package ac.grim.grimac.manager.player.features;
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.api.feature.FeatureState;
/*    */ import ac.grim.grimac.manager.player.features.types.ExperimentalChecksFeature;
/*    */ import ac.grim.grimac.manager.player.features.types.ForceSlowMovementFeature;
/*    */ import ac.grim.grimac.manager.player.features.types.GrimFeature;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.utils.common.ConfigReloadObserver;
/*    */ import com.google.common.collect.ImmutableSet;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class FeatureManagerImpl implements FeatureManager, ConfigReloadObserver {
/*    */   @Generated
/*    */   public static Map<String, GrimFeature> getFEATURES() {
/* 20 */     return FEATURES;
/*    */   }
/*    */   private static final Map<String, GrimFeature> FEATURES;
/*    */   static {
/* 24 */     FeatureBuilder builder = new FeatureBuilder();
/* 25 */     builder.register(new ExperimentalChecksFeature());
/* 26 */     builder.register(new ExemptElytraFeature());
/* 27 */     builder.register(new ForceStuckSpeedFeature());
/* 28 */     builder.register(new ForceSlowMovementFeature());
/* 29 */     FEATURES = (Map<String, GrimFeature>)builder.buildMap();
/*    */   }
/*    */   
/* 32 */   private final Map<String, FeatureState> states = new HashMap<>();
/*    */   
/*    */   private final GrimPlayer player;
/*    */   
/*    */   public FeatureManagerImpl(GrimPlayer player) {
/* 37 */     this.player = player;
/* 38 */     for (GrimFeature value : FEATURES.values()) this.states.put(value.getName(), FeatureState.UNSET);
/*    */   
/*    */   }
/*    */   
/*    */   public Collection<String> getFeatureKeys() {
/* 43 */     return (Collection<String>)ImmutableSet.copyOf(FEATURES.keySet());
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public FeatureState getFeatureState(String key) {
/* 48 */     return this.states.get(key);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFeatureEnabled(String key) {
/* 53 */     GrimFeature feature = FEATURES.get(key);
/* 54 */     if (feature == null) return false; 
/* 55 */     return feature.isEnabled(this.player);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean setFeatureState(String key, FeatureState tristate) {
/* 60 */     GrimFeature feature = FEATURES.get(key);
/* 61 */     if (feature == null) return false; 
/* 62 */     this.states.put(key, tristate);
/* 63 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void reload() {
/* 68 */     onReload(GrimAPI.INSTANCE.getExternalAPI().getConfigManager());
/*    */   }
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 73 */     for (Map.Entry<String, FeatureState> entry : this.states.entrySet()) {
/* 74 */       String key = entry.getKey();
/* 75 */       FeatureState state = entry.getValue();
/* 76 */       GrimFeature feature = FEATURES.get(key);
/* 77 */       if (feature == null)
/* 78 */         continue;  feature.setState(this.player, config, state);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\FeatureManagerImpl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */