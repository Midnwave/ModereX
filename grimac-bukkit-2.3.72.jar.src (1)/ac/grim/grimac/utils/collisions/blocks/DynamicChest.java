/*    */ package ac.grim.grimac.utils.collisions.blocks;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Type;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
/*    */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DynamicChest
/*    */   implements CollisionFactory
/*    */ {
/*    */   public CollisionBox fetch(GrimPlayer player, ClientVersion version, WrappedBlockState chest, int x, int y, int z) {
/* 21 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && version
/* 22 */       .isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 23 */       if (chest.getTypeData() == Type.SINGLE) {
/* 24 */         return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
/*    */       }
/*    */       
/* 27 */       if ((chest.getFacing() == BlockFace.SOUTH && chest.getTypeData() == Type.RIGHT) || (chest.getFacing() == BlockFace.NORTH && chest.getTypeData() == Type.LEFT))
/* 28 */         return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D); 
/* 29 */       if ((chest.getFacing() == BlockFace.SOUTH && chest.getTypeData() == Type.LEFT) || (chest.getFacing() == BlockFace.NORTH && chest.getTypeData() == Type.RIGHT))
/* 30 */         return (CollisionBox)new HexCollisionBox(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D); 
/* 31 */       if ((chest.getFacing() == BlockFace.WEST && chest.getTypeData() == Type.RIGHT) || (chest.getFacing() == BlockFace.EAST && chest.getTypeData() == Type.LEFT)) {
/* 32 */         return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
/*    */       }
/* 34 */       return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 42 */     if (chest.getFacing() == BlockFace.EAST || chest.getFacing() == BlockFace.WEST) {
/* 43 */       WrappedBlockState westState = player.compensatedWorld.getBlock(x - 1, y, z);
/*    */       
/* 45 */       if (westState.getType() == chest.getType()) {
/* 46 */         return (CollisionBox)new HexCollisionBox(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
/*    */       }
/*    */       
/* 49 */       WrappedBlockState eastState = player.compensatedWorld.getBlock(x + 1, y, z);
/* 50 */       if (eastState.getType() == chest.getType()) {
/* 51 */         return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
/*    */       }
/*    */     } else {
/* 54 */       WrappedBlockState northState = player.compensatedWorld.getBlock(x, y, z - 1);
/* 55 */       if (northState.getType() == chest.getType()) {
/* 56 */         return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
/*    */       }
/*    */       
/* 59 */       WrappedBlockState southState = player.compensatedWorld.getBlock(x, y, z + 1);
/* 60 */       if (southState.getType() == chest.getType()) {
/* 61 */         return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 66 */     return (CollisionBox)new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\DynamicChest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */