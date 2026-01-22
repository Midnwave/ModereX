/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*    */ import ac.grim.grimac.utils.math.GrimMath;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import java.util.OptionalInt;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class JumpPower {
/*    */   @Generated
/*    */   private JumpPower() {
/* 15 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static void jumpFromGround(GrimPlayer player, Vector3dm vector) {
/* 17 */     float jumpPower = getJumpPower(player);
/*    */     
/* 19 */     OptionalInt jumpBoost = player.compensatedEntities.getPotionLevelForPlayer(PotionTypes.JUMP_BOOST);
/* 20 */     if (jumpBoost.isPresent()) {
/* 21 */       jumpPower += 0.1F * (jumpBoost.getAsInt() + 1);
/*    */     }
/*    */     
/* 24 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) && jumpPower <= 1.0E-5F) {
/*    */       return;
/*    */     }
/* 27 */     vector.setY(player.getClientVersion().isOlderThan(ClientVersion.V_1_21_2) ? jumpPower : Math.max(jumpPower, vector.getY()));
/*    */     
/* 29 */     if (player.isSprinting) {
/* 30 */       float radRotation = GrimMath.radians(player.xRot);
/* 31 */       vector.add(new Vector3dm(-player.trigHandler.sin(radRotation) * 0.2D, 0.0D, player.trigHandler.cos(radRotation) * 0.2D));
/*    */     } 
/*    */   }
/*    */   
/*    */   public static float getJumpPower(GrimPlayer player) {
/* 36 */     return (float)player.compensatedEntities.self.getAttributeValue(Attributes.JUMP_STRENGTH) * getPlayerJumpFactor(player);
/*    */   }
/*    */   
/*    */   public static float getPlayerJumpFactor(GrimPlayer player) {
/* 40 */     return BlockProperties.onHoneyBlock(player, player.mainSupportingBlockData, new Vector3d(player.lastX, player.lastY, player.lastZ)) ? 0.5F : 1.0F;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\JumpPower.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */