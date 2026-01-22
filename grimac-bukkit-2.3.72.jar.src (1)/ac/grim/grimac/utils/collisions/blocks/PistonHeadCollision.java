/*    */ package ac.grim.grimac.utils.collisions.blocks;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PistonHeadCollision
/*    */   implements CollisionFactory
/*    */ {
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState block, int x, int y, int z) {
/* 25 */     double longAmount = (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && block.isShort()) ? 0.0D : 4.0D;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 31 */     if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2) || PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
/* 32 */       longAmount = 4.0D;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 37 */     if (version.isOlderThan(ClientVersion.V_1_9) || PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
/* 38 */       longAmount = 0.0D;
/*    */     }
/*    */     
/* 41 */     switch (block.getFacing()) { case UP: case NORTH: case SOUTH: return 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */           
/* 51 */           version.isOlderThanOrEquals(ClientVersion.V_1_8) ? 
/* 52 */           (CollisionBox)new ComplexCollisionBox(2, new SimpleCollisionBox[] { (SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D), (SimpleCollisionBox)new HexCollisionBox(4.0D, 6.0D, 0.0D, 12.0D, 10.0D, 12.0D)
/*    */ 
/*    */ 
/*    */             
/* 56 */             }) : (CollisionBox)new ComplexCollisionBox(2, new SimpleCollisionBox[] { (SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D), (SimpleCollisionBox)new HexCollisionBox(6.0D, 6.0D, 0.0D - longAmount, 10.0D, 10.0D, 12.0D) });
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/*    */       case WEST:
/* 63 */         return version.isOlderThanOrEquals(ClientVersion.V_1_8) ? 
/* 64 */           (CollisionBox)new ComplexCollisionBox(2, new SimpleCollisionBox[] { (SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D), (SimpleCollisionBox)new HexCollisionBox(6.0D, 4.0D, 4.0D, 10.0D, 12.0D, 16.0D)
/*    */ 
/*    */ 
/*    */             
/* 68 */             }) : (CollisionBox)new ComplexCollisionBox(2, new SimpleCollisionBox[] { (SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D), (SimpleCollisionBox)new HexCollisionBox(4.0D, 6.0D, 6.0D, 16.0D + longAmount, 10.0D, 10.0D) });
/*    */ 
/*    */       
/*    */       case EAST:
/*    */        }
/*    */ 
/*    */     
/* 75 */     return (CollisionBox)new ComplexCollisionBox(2, new SimpleCollisionBox[] { (SimpleCollisionBox)new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), (SimpleCollisionBox)new HexCollisionBox(6.0D, 4.0D, 6.0D, 10.0D, 16.0D + longAmount, 10.0D) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\PistonHeadCollision.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */