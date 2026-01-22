/*     */ package ac.grim.grimac.utils.collisions.blocks.connecting;
/*     */ 
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.NoCollisionBox;
/*     */ import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
/*     */ import ac.grim.grimac.utils.nmsutil.Materials;
/*     */ 
/*     */ public class DynamicConnecting
/*     */ {
/*     */   public static CollisionBox[] makeShapes(float p_196408_1_, float p_196408_2_, float p_196408_3_, float p_196408_4_, float p_196408_5_, boolean includeCenter, int additionalMaxIndex) {
/*  22 */     float middleMin = 8.0F - p_196408_1_;
/*  23 */     float middleMax = 8.0F + p_196408_1_;
/*  24 */     float f2 = 8.0F - p_196408_2_;
/*  25 */     float f3 = 8.0F + p_196408_2_;
/*  26 */     HexCollisionBox hexCollisionBox1 = new HexCollisionBox(middleMin, 0.0D, middleMin, middleMax, p_196408_3_, middleMax);
/*  27 */     HexCollisionBox hexCollisionBox2 = new HexCollisionBox(f2, p_196408_4_, 0.0D, f3, p_196408_5_, f3);
/*  28 */     HexCollisionBox hexCollisionBox3 = new HexCollisionBox(f2, p_196408_4_, f2, f3, p_196408_5_, 16.0D);
/*  29 */     HexCollisionBox hexCollisionBox4 = new HexCollisionBox(0.0D, p_196408_4_, f2, f3, p_196408_5_, f3);
/*  30 */     HexCollisionBox hexCollisionBox5 = new HexCollisionBox(f2, p_196408_4_, f2, 16.0D, p_196408_5_, f3);
/*     */     
/*  32 */     ComplexCollisionBox voxelshape5 = new ComplexCollisionBox(2 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox2, (SimpleCollisionBox)hexCollisionBox5 });
/*  33 */     ComplexCollisionBox voxelshape6 = new ComplexCollisionBox(2 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox4 });
/*     */     
/*  35 */     CollisionBox[] avoxelshape = { (CollisionBox)NoCollisionBox.INSTANCE, (CollisionBox)hexCollisionBox3, (CollisionBox)hexCollisionBox4, (CollisionBox)voxelshape6, (CollisionBox)hexCollisionBox2, (CollisionBox)new ComplexCollisionBox(2 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox2 }), (CollisionBox)new ComplexCollisionBox(2 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox4, (SimpleCollisionBox)hexCollisionBox2 }), (CollisionBox)new ComplexCollisionBox(3 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox4, (SimpleCollisionBox)hexCollisionBox2 }), (CollisionBox)hexCollisionBox5, (CollisionBox)new ComplexCollisionBox(2 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox5 }), (CollisionBox)new ComplexCollisionBox(2 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox4, (SimpleCollisionBox)hexCollisionBox5 }), (CollisionBox)new ComplexCollisionBox(3 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox4, (SimpleCollisionBox)hexCollisionBox5 }), (CollisionBox)voxelshape5, (CollisionBox)new ComplexCollisionBox(3 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox2, (SimpleCollisionBox)hexCollisionBox5 }), (CollisionBox)new ComplexCollisionBox(3 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox4, (SimpleCollisionBox)hexCollisionBox2, (SimpleCollisionBox)hexCollisionBox5 }), (CollisionBox)new ComplexCollisionBox(4 + additionalMaxIndex, new SimpleCollisionBox[] { (SimpleCollisionBox)hexCollisionBox2, (SimpleCollisionBox)hexCollisionBox3, (SimpleCollisionBox)hexCollisionBox4, (SimpleCollisionBox)hexCollisionBox5 }) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  44 */     if (includeCenter) {
/*  45 */       for (int i = 0; i < 16; i++) {
/*  46 */         avoxelshape[i] = avoxelshape[i].union((SimpleCollisionBox)hexCollisionBox1);
/*     */       }
/*     */     }
/*     */     
/*  50 */     return avoxelshape;
/*     */   }
/*     */   
/*     */   public boolean connectsTo(GrimPlayer player, ClientVersion v, int currX, int currY, int currZ, BlockFace direction) {
/*  54 */     WrappedBlockState targetBlock = player.compensatedWorld.getBlock(currX + direction.getModX(), currY + direction.getModY(), currZ + direction.getModZ());
/*  55 */     WrappedBlockState currBlock = player.compensatedWorld.getBlock(currX, currY, currZ);
/*  56 */     StateType target = targetBlock.getType();
/*  57 */     StateType fence = currBlock.getType();
/*     */     
/*  59 */     if (!BlockTags.FENCES.contains(target) && isBlacklisted(target, fence, v)) {
/*  60 */       return false;
/*     */     }
/*     */     
/*  63 */     if (target == StateTypes.TNT) {
/*  64 */       return v.isNewerThanOrEquals(ClientVersion.V_1_12);
/*     */     }
/*     */ 
/*     */     
/*  68 */     if (target == StateTypes.BARRIER) {
/*  69 */       return (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10) || (player
/*  70 */         .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) && player
/*  71 */         .getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_11_1)));
/*     */     }
/*  73 */     if (BlockTags.STAIRS.contains(target)) {
/*     */ 
/*     */       
/*  76 */       if (v.isOlderThan(ClientVersion.V_1_12) || (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_11) && v.isNewerThanOrEquals(ClientVersion.V_1_13)))
/*  77 */         return false; 
/*  78 */       return (targetBlock.getFacing().getOppositeFace() == direction);
/*  79 */     }  if (canConnectToGate(fence) && BlockTags.FENCE_GATES.contains(target)) {
/*     */ 
/*     */       
/*  82 */       if (v.isOlderThanOrEquals(ClientVersion.V_1_11_1)) return true;
/*     */       
/*  84 */       BlockFace f1 = targetBlock.getFacing();
/*  85 */       BlockFace f2 = f1.getOppositeFace();
/*  86 */       return (direction != f1 && direction != f2);
/*     */     } 
/*  88 */     if (fence == target) return true;
/*     */     
/*  90 */     return checkCanConnect(player, targetBlock, target, fence, direction);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isBlacklisted(StateType m, StateType fence, ClientVersion clientVersion) {
/*  98 */     if (BlockTags.LEAVES.contains(m))
/*  99 */       return (clientVersion.isNewerThan(ClientVersion.V_1_8) || !Materials.isGlassPane(fence)); 
/* 100 */     if (BlockTags.SHULKER_BOXES.contains(m)) return true; 
/* 101 */     if (BlockTags.TRAPDOORS.contains(m)) return true;
/*     */     
/* 103 */     return (m == StateTypes.ENCHANTING_TABLE || m == StateTypes.FARMLAND || m == StateTypes.CARVED_PUMPKIN || m == StateTypes.JACK_O_LANTERN || m == StateTypes.PUMPKIN || m == StateTypes.MELON || m == StateTypes.BEACON || BlockTags.CAULDRONS
/* 104 */       .contains(m) || m == StateTypes.GLOWSTONE || m == StateTypes.SEA_LANTERN || m == StateTypes.ICE || m == StateTypes.PISTON || m == StateTypes.STICKY_PISTON || m == StateTypes.PISTON_HEAD || (
/* 105 */       !canConnectToGlassBlock() && BlockTags.GLASS_BLOCKS
/* 106 */       .contains(m)));
/*     */   }
/*     */   
/*     */   protected int getAABBIndex(boolean north, boolean east, boolean south, boolean west) {
/* 110 */     int i = 0;
/*     */     
/* 112 */     if (north) {
/* 113 */       i |= 0x4;
/*     */     }
/*     */     
/* 116 */     if (east) {
/* 117 */       i |= 0x8;
/*     */     }
/*     */     
/* 120 */     if (south) {
/* 121 */       i |= 0x1;
/*     */     }
/*     */     
/* 124 */     if (west) {
/* 125 */       i |= 0x2;
/*     */     }
/*     */     
/* 128 */     return i;
/*     */   }
/*     */   
/*     */   public boolean checkCanConnect(GrimPlayer player, WrappedBlockState state, StateType one, StateType two, BlockFace direction) {
/* 132 */     return false;
/*     */   }
/*     */   
/*     */   public boolean canConnectToGlassBlock() {
/* 136 */     return false;
/*     */   }
/*     */   
/*     */   public boolean canConnectToGate(StateType fence) {
/* 140 */     return !Materials.isGlassPane(fence);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\collisions\blocks\connecting\DynamicConnecting.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */