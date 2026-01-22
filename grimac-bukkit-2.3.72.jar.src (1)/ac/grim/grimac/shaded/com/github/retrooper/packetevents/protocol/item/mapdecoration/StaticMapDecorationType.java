/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.AbstractMappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticMapDecorationType
/*    */   extends AbstractMappedEntity
/*    */   implements MapDecorationType
/*    */ {
/*    */   private final ResourceLocation assetId;
/*    */   private final boolean showOnItemFrame;
/*    */   private final int mapColor;
/*    */   private final boolean explorationMapElement;
/*    */   private final boolean trackCount;
/*    */   
/*    */   @Internal
/*    */   public StaticMapDecorationType(@Nullable TypesBuilderData data, ResourceLocation assetId, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
/* 41 */     super(data);
/* 42 */     this.assetId = assetId;
/* 43 */     this.showOnItemFrame = showOnItemFrame;
/* 44 */     this.mapColor = mapColor;
/* 45 */     this.explorationMapElement = explorationMapElement;
/* 46 */     this.trackCount = trackCount;
/*    */   }
/*    */ 
/*    */   
/*    */   public ResourceLocation getAssetId() {
/* 51 */     return this.assetId;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isShowOnItemFrame() {
/* 56 */     return this.showOnItemFrame;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMapColor() {
/* 61 */     return this.mapColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isExplorationMapElement() {
/* 66 */     return this.explorationMapElement;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTrackCount() {
/* 71 */     return this.trackCount;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\mapdecoration\StaticMapDecorationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */