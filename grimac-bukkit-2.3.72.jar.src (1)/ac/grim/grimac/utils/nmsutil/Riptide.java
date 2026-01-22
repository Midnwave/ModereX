/*    */ package ac.grim.grimac.utils.nmsutil;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.utils.math.GrimMath;
/*    */ import ac.grim.grimac.utils.math.Vector3dm;
/*    */ 
/*    */ public final class Riptide {
/*    */   @Generated
/* 12 */   private Riptide() { throw new UnsupportedOperationException("This is a utility class and cannot be instantiated"); } public static Vector3dm getRiptideVelocity(GrimPlayer player) {
/*    */     int j;
/* 14 */     ItemStack main = player.inventory.getHeldItem();
/* 15 */     ItemStack off = player.inventory.getOffHand();
/*    */ 
/*    */     
/* 18 */     if (main.getType() == ItemTypes.TRIDENT) {
/* 19 */       j = main.getEnchantmentLevel(EnchantmentTypes.RIPTIDE);
/* 20 */     } else if (off.getType() == ItemTypes.TRIDENT) {
/* 21 */       j = off.getEnchantmentLevel(EnchantmentTypes.RIPTIDE);
/*    */     } else {
/* 23 */       return new Vector3dm();
/*    */     } 
/*    */     
/* 26 */     float yaw = GrimMath.radians(player.xRot);
/* 27 */     float pitch = GrimMath.radians(player.yRot);
/* 28 */     float pitchCos = player.trigHandler.cos(pitch);
/* 29 */     float f1 = -player.trigHandler.sin(yaw) * pitchCos;
/* 30 */     float f2 = -player.trigHandler.sin(pitch);
/* 31 */     float f3 = player.trigHandler.cos(yaw) * pitchCos;
/* 32 */     float f4 = (float)Math.sqrt((f1 * f1 + f2 * f2 + f3 * f3));
/* 33 */     float f5 = 3.0F * (1.0F + j) / 4.0F / f4;
/*    */ 
/*    */ 
/*    */     
/* 37 */     return new Vector3dm(f1 * f5, player.verticalCollision ? 0.0F : (f2 * f5), f3 * f5);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\Riptide.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */