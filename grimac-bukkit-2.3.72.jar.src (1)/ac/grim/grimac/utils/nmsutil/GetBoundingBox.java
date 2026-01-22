/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ 
/*    */ public final class GetBoundingBox {
/*    */   @Generated
/*    */   private GetBoundingBox() {
/* 10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static SimpleCollisionBox getCollisionBoxForPlayer(GrimPlayer player, double centerX, double centerY, double centerZ) {
/* 12 */     if (player.inVehicle()) {
/* 13 */       return getPacketEntityBoundingBox(player, centerX, centerY, centerZ, player.compensatedEntities.self.getRiding());
/*    */     }
/*    */     
/* 16 */     return getPlayerBoundingBox(player, centerX, centerY, centerZ);
/*    */   }
/*    */   
/*    */   public static SimpleCollisionBox getPacketEntityBoundingBox(GrimPlayer player, double centerX, double minY, double centerZ, PacketEntity entity) {
/* 20 */     float width = BoundingBoxSize.getWidth(player, entity);
/* 21 */     float height = BoundingBoxSize.getHeight(player, entity);
/* 22 */     return getBoundingBoxFromPosAndSize(entity, centerX, minY, centerZ, width, height);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SimpleCollisionBox getPlayerBoundingBox(GrimPlayer player, double centerX, double minY, double centerZ) {
/* 30 */     float width = player.pose.width;
/* 31 */     float height = player.pose.height;
/* 32 */     return getBoundingBoxFromPosAndSize(player, centerX, minY, centerZ, width, height);
/*    */   }
/*    */   
/*    */   public static SimpleCollisionBox getBoundingBoxFromPosAndSize(GrimPlayer player, double centerX, double minY, double centerZ, float width, float height) {
/* 36 */     return getBoundingBoxFromPosAndSize((PacketEntity)player.compensatedEntities.self, centerX, minY, centerZ, width, height);
/*    */   }
/*    */   
/*    */   public static SimpleCollisionBox getBoundingBoxFromPosAndSize(PacketEntity entity, double centerX, double minY, double centerZ, float width, float height) {
/* 40 */     float scale = (float)entity.getAttributeValue(Attributes.SCALE);
/* 41 */     return getBoundingBoxFromPosAndSizeRaw(centerX, minY, centerZ, width * scale, height * scale);
/*    */   }
/*    */   
/*    */   public static SimpleCollisionBox getBoundingBoxFromPosAndSizeRaw(double centerX, double minY, double centerZ, float width, float height) {
/* 45 */     double minX = centerX - (width / 2.0F);
/* 46 */     double maxX = centerX + (width / 2.0F);
/* 47 */     double maxY = minY + height;
/* 48 */     double minZ = centerZ - (width / 2.0F);
/* 49 */     double maxZ = centerZ + (width / 2.0F);
/*    */     
/* 51 */     return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ, false);
/*    */   }
/*    */   
/*    */   public static double[] getEntityDimensions(GrimPlayer player, PacketEntity entity) {
/* 55 */     float scale = (float)entity.getAttributeValue(Attributes.SCALE);
/* 56 */     float width = BoundingBoxSize.getWidth(player, entity) * scale;
/* 57 */     float height = BoundingBoxSize.getHeight(player, entity) * scale;
/* 58 */     return new double[] { width, height, width };
/*    */   }
/*    */   
/*    */   public static void expandBoundingBoxByEntityDimensions(SimpleCollisionBox box, GrimPlayer player, PacketEntity entity) {
/* 62 */     double[] dimensions = getEntityDimensions(player, entity);
/* 63 */     double halfWidth = dimensions[0] / 2.0D;
/* 64 */     double height = dimensions[1];
/* 65 */     double halfDepth = dimensions[2] / 2.0D;
/*    */     
/* 67 */     box.minX -= halfWidth;
/* 68 */     box.minY -= 0.0D;
/* 69 */     box.minZ -= halfDepth;
/* 70 */     box.maxX += halfWidth;
/* 71 */     box.maxY += height;
/* 72 */     box.maxZ += halfDepth;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\GetBoundingBox.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */