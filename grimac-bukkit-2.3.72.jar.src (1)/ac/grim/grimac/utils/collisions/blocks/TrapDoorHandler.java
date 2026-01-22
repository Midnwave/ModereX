/*    */ package ac.grim.grimac.utils.collisions.blocks;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Half;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.NoCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ public class TrapDoorHandler implements CollisionFactory {
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/* 15 */     double var2 = 0.1875D;
/*    */     
/* 17 */     if (block.isOpen()) {
/* 18 */       switch (block.getFacing()) {
/*    */         case SOUTH:
/* 20 */           return (CollisionBox)new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, var2, false);
/*    */         case NORTH:
/* 22 */           return (CollisionBox)new SimpleCollisionBox(0.0D, 0.0D, 1.0D - var2, 1.0D, 1.0D, 1.0D, false);
/*    */         case EAST:
/* 24 */           return (CollisionBox)new SimpleCollisionBox(0.0D, 0.0D, 0.0D, var2, 1.0D, 1.0D, false);
/*    */         case WEST:
/* 26 */           return (CollisionBox)new SimpleCollisionBox(1.0D - var2, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, false);
/*    */       } 
/*    */     } else {
/* 29 */       if (block.getHalf() == Half.BOTTOM) {
/* 30 */         return (CollisionBox)new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, var2, 1.0D, false);
/*    */       }
/* 32 */       return (CollisionBox)new SimpleCollisionBox(0.0D, 1.0D - var2, 0.0D, 1.0D, 1.0D, 1.0D, false);
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 37 */     return (CollisionBox)NoCollisionBox.INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\TrapDoorHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */