/*    */ package ac.grim.grimac.utils.collisions.blocks;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ public class PistonBaseCollision
/*    */   implements CollisionFactory {
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/* 15 */     if (!block.isExtended()) return (CollisionBox)new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, true);
/*    */     
/* 17 */     switch (block.getFacing()) { case UP: case NORTH: case SOUTH: case WEST: case EAST:  }  return 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 23 */       (CollisionBox)new HexCollisionBox(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\PistonBaseCollision.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */