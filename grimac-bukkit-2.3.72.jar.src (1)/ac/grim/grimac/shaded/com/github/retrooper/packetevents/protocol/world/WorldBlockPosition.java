/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ public class WorldBlockPosition
/*    */ {
/*    */   private ResourceLocation world;
/*    */   private Vector3i blockPosition;
/*    */   
/*    */   public WorldBlockPosition(@NotNull ResourceLocation world, @NotNull Vector3i blockPosition) {
/* 31 */     this.world = world;
/* 32 */     this.blockPosition = blockPosition;
/*    */   }
/*    */   
/*    */   public WorldBlockPosition(@NotNull ResourceLocation world, int x, int y, int z) {
/* 36 */     this.world = world;
/* 37 */     this.blockPosition = new Vector3i(x, y, z);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   public WorldBlockPosition(@NotNull Dimension dimension, @NotNull Vector3i blockPosition) {
/* 42 */     this(new ResourceLocation(dimension.getDimensionName()), blockPosition);
/*    */   }
/*    */   
/*    */   public WorldBlockPosition(@NotNull DimensionType dimensionType, @NotNull Vector3i blockPosition) {
/* 46 */     this(dimensionType.getName(), blockPosition);
/*    */   }
/*    */   
/*    */   public ResourceLocation getWorld() {
/* 50 */     return this.world;
/*    */   }
/*    */   
/*    */   public void setWorld(ResourceLocation world) {
/* 54 */     this.world = world;
/*    */   }
/*    */   
/*    */   public Vector3i getBlockPosition() {
/* 58 */     return this.blockPosition;
/*    */   }
/*    */   
/*    */   public void setBlockPosition(Vector3i blockPosition) {
/* 62 */     this.blockPosition = blockPosition;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\WorldBlockPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */