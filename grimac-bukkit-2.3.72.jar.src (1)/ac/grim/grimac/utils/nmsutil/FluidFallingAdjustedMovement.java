/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public final class FluidFallingAdjustedMovement {
/*    */   @Generated
/*    */   private FluidFallingAdjustedMovement() {
/* 10 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*    */   } public static Vector3dm getFluidFallingAdjustedMovement(@NotNull GrimPlayer player, double gravity, boolean isFalling, Vector3dm velocity) {
/* 12 */     if (!player.hasGravity || player.isSprinting) return velocity; 
/* 13 */     isFalling = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) ? isFalling : ((velocity.getY() < 0.0D));
/* 14 */     double newY = (isFalling && Math.abs(velocity.getY() - 0.005D) >= 0.003D && Math.abs(velocity.getY() - gravity / 16.0D) < 0.003D) ? -0.003D : (velocity.getY() - gravity / 16.0D);
/* 15 */     return new Vector3dm(velocity.getX(), newY, velocity.getZ());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\FluidFallingAdjustedMovement.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */