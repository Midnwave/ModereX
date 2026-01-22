/*     */ package ac.grim.grimac.utils.collisions.blocks.connecting;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.utils.collisions.CollisionData;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.HitBoxFactory;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ 
/*     */ public class DynamicHitboxPane
/*     */   extends DynamicConnecting implements HitBoxFactory {
/*  23 */   private static final CollisionBox[] COLLISION_BOXES = makeShapes(1.0F, 1.0F, 16.0F, 0.0F, 16.0F, true, 1);
/*     */   
/*     */   public CollisionBox fetch(GrimPlayer player, StateType item, ClientVersion version, WrappedBlockState block, boolean isTargetBlock, int x, int y, int z) {
/*     */     boolean east;
/*     */     boolean north;
/*     */     boolean south;
/*     */     boolean west;
/*  30 */     if (isModernVersion(version)) {
/*  31 */       east = (block.getEast() != East.FALSE);
/*  32 */       north = (block.getNorth() != North.FALSE);
/*  33 */       south = (block.getSouth() != South.FALSE);
/*  34 */       west = (block.getWest() != West.FALSE);
/*     */     } else {
/*  36 */       east = connectsTo(player, version, x, y, z, BlockFace.EAST);
/*  37 */       north = connectsTo(player, version, x, y, z, BlockFace.NORTH);
/*  38 */       south = connectsTo(player, version, x, y, z, BlockFace.SOUTH);
/*  39 */       west = connectsTo(player, version, x, y, z, BlockFace.WEST);
/*     */     } 
/*     */ 
/*     */     
/*  43 */     if (shouldUseOldPaneShape(version, north, south, east, west)) {
/*  44 */       north = south = east = west = true;
/*     */     }
/*     */     
/*  47 */     return version.isNewerThanOrEquals(ClientVersion.V_1_9) ? 
/*  48 */       getModernCollisionBox(north, east, south, west) : 
/*  49 */       getLegacyCollisionBox(north, east, south, west);
/*     */   }
/*     */   
/*     */   private boolean isModernVersion(ClientVersion version) {
/*  53 */     return (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && version
/*  54 */       .isNewerThanOrEquals(ClientVersion.V_1_13));
/*     */   }
/*     */   
/*     */   private boolean shouldUseOldPaneShape(ClientVersion version, boolean north, boolean south, boolean east, boolean west) {
/*  58 */     return (!north && !south && !east && !west && (version
/*  59 */       .isOlderThanOrEquals(ClientVersion.V_1_8) || (
/*  60 */       PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8) && version
/*  61 */       .isNewerThanOrEquals(ClientVersion.V_1_13))));
/*     */   }
/*     */   
/*     */   private CollisionBox getModernCollisionBox(boolean north, boolean east, boolean south, boolean west) {
/*  65 */     return COLLISION_BOXES[getAABBIndex(north, east, south, west)].copy();
/*     */   }
/*     */   
/*     */   private CollisionBox getLegacyCollisionBox(boolean north, boolean east, boolean south, boolean west) {
/*  69 */     float minX = 0.4375F;
/*  70 */     float maxX = 0.5625F;
/*  71 */     float minZ = 0.4375F;
/*  72 */     float maxZ = 0.5625F;
/*     */     
/*  74 */     if ((!west || !east) && (west || east || north || south)) {
/*  75 */       if (west) {
/*  76 */         minX = 0.0F;
/*  77 */       } else if (east) {
/*  78 */         maxX = 1.0F;
/*     */       } 
/*     */     } else {
/*  81 */       minX = 0.0F;
/*  82 */       maxX = 1.0F;
/*     */     } 
/*     */     
/*  85 */     if ((!north || !south) && (west || east || north || south)) {
/*  86 */       if (north) {
/*  87 */         minZ = 0.0F;
/*  88 */       } else if (south) {
/*  89 */         maxZ = 1.0F;
/*     */       } 
/*     */     } else {
/*  92 */       minZ = 0.0F;
/*  93 */       maxZ = 1.0F;
/*     */     } 
/*     */     
/*  96 */     return (CollisionBox)new SimpleCollisionBox(minX, 0.0D, minZ, maxX, 1.0D, maxZ);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canConnectToGlassBlock() {
/* 101 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 106 */     if (BlockTags.GLASS_PANES.contains(one) || one == StateTypes.IRON_BARS) {
/* 107 */       return true;
/*     */     }
/* 109 */     return CollisionData.getData(one)
/* 110 */       .getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0)
/* 111 */       .isSideFullBlock(direction);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicHitboxPane.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */