/*    */ package ac.grim.grimac.manager.player.features;
/*    */ 
/*    */ import ac.grim.grimac.manager.player.features.types.GrimFeature;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import com.google.common.collect.ImmutableMap;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ 
/*    */ public class FeatureBuilder
/*    */ {
/* 11 */   private static final Pattern VALID = Pattern.compile("[a-zA-Z0-9_]{1,64}");
/* 12 */   private final ImmutableMap.Builder<String, GrimFeature> mapBuilder = ImmutableMap.builder();
/*    */   
/*    */   public <T extends GrimFeature> void register(T feature) {
/* 15 */     if (!VALID.matcher(feature.getName()).matches()) {
/* 16 */       LogUtil.error("Invalid feature name: " + feature.getName());
/*    */       return;
/*    */     } 
/* 19 */     this.mapBuilder.put(feature.getName(), feature);
/*    */   }
/*    */   
/*    */   public ImmutableMap<String, GrimFeature> buildMap() {
/* 23 */     return this.mapBuilder.build();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\features\FeatureBuilder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */