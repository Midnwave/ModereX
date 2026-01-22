/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.utils.data.MainSupportingBlockData;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*     */ import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
/*     */ import ac.grim.grimac.utils.math.GrimMath;
/*     */ 
/*     */ public final class BlockProperties {
/*     */   @Generated
/*     */   private BlockProperties() {
/*  21 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   } public static float getFrictionInfluencedSpeed(float f, GrimPlayer player) {
/*  23 */     if (player.lastOnGround) {
/*  24 */       return (float)(player.speed * (0.21600002F / f * f * f));
/*     */     }
/*     */ 
/*     */     
/*  28 */     if (player.inVehicle()) {
/*  29 */       if ((player.compensatedEntities.self.getRiding()).type == EntityTypes.PIG || player.compensatedEntities.self.getRiding() instanceof ac.grim.grimac.utils.data.packetentity.PacketEntityHorse) {
/*  30 */         return (float)(player.speed * 0.10000000149011612D);
/*     */       }
/*     */       
/*  33 */       PacketEntity packetEntity = player.compensatedEntities.self.getRiding(); if (packetEntity instanceof PacketEntityStrider) { PacketEntityStrider strider = (PacketEntityStrider)packetEntity;
/*     */         
/*  35 */         if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20)) {
/*  36 */           return (float)player.speed * 0.1F;
/*     */         }
/*     */ 
/*     */         
/*  40 */         return (float)strider.getAttributeValue(Attributes.MOVEMENT_SPEED) * (strider.isShaking ? 0.66F : 1.0F) * 0.1F; }
/*     */     
/*     */     } 
/*     */     
/*  44 */     if (player.isFlying) {
/*  45 */       return player.flySpeed * 20.0F * (player.isSprinting ? 0.1F : 0.05F);
/*     */     }
/*     */ 
/*     */     
/*  49 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_4)) {
/*  50 */       return player.isSprinting ? 0.025999999F : 0.02F;
/*     */     }
/*     */     
/*  53 */     return player.lastSprintingForSpeed ? 0.025999999F : 0.02F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StateType getOnPos(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Vector3d playerPos) {
/*  64 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4)) {
/*  65 */       return getOnBlock(player, playerPos.getX(), playerPos.getY(), playerPos.getZ());
/*     */     }
/*     */     
/*  68 */     Vector3i pos = getOnPos(player, playerPos, mainSupportingBlockData, 0.2F);
/*  69 */     return player.compensatedWorld.getBlockType(pos.x, pos.y, pos.z);
/*     */   }
/*     */   
/*     */   public static float getFriction(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Vector3d playerPos) {
/*  73 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4)) {
/*  74 */       double searchBelowAmount = 0.5000001D;
/*     */       
/*  76 */       if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) {
/*  77 */         searchBelowAmount = 1.0D;
/*     */       }
/*  79 */       StateType type = player.compensatedWorld.getBlockType(playerPos.getX(), playerPos.getY() - searchBelowAmount, playerPos.getZ());
/*  80 */       return getMaterialFriction(player, type);
/*     */     } 
/*     */     
/*  83 */     StateType underPlayer = getBlockPosBelowThatAffectsMyMovement(player, mainSupportingBlockData, playerPos);
/*  84 */     return getMaterialFriction(player, underPlayer);
/*     */   }
/*     */ 
/*     */   
/*     */   public static float getBlockSpeedFactor(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Vector3d playerPos) {
/*  89 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) return 1.0F; 
/*  90 */     if (player.isGliding || player.isFlying) return 1.0F;
/*     */     
/*  92 */     if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4)) {
/*  93 */       return getBlockSpeedFactorLegacy(player, playerPos);
/*     */     }
/*     */     
/*  96 */     WrappedBlockState inBlock = player.compensatedWorld.getBlock(playerPos.getX(), playerPos.getY(), playerPos.getZ());
/*  97 */     float inBlockSpeedFactor = getBlockSpeedFactor(player, inBlock.getType());
/*  98 */     if (inBlockSpeedFactor != 1.0F || inBlock.getType() == StateTypes.WATER || inBlock.getType() == StateTypes.BUBBLE_COLUMN) {
/*  99 */       return getModernVelocityMultiplier(player, inBlockSpeedFactor);
/*     */     }
/*     */     
/* 102 */     StateType underPlayer = getBlockPosBelowThatAffectsMyMovement(player, mainSupportingBlockData, playerPos);
/* 103 */     return getModernVelocityMultiplier(player, getBlockSpeedFactor(player, underPlayer));
/*     */   }
/*     */   
/*     */   public static boolean onHoneyBlock(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Vector3d playerPos) {
/* 107 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) return false;
/*     */     
/* 109 */     StateType inBlock = player.compensatedWorld.getBlockType(playerPos.getX(), playerPos.getY(), playerPos.getZ());
/* 110 */     return (inBlock == StateTypes.HONEY_BLOCK || getBlockPosBelowThatAffectsMyMovement(player, mainSupportingBlockData, playerPos) == StateTypes.HONEY_BLOCK);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static StateType getBlockPosBelowThatAffectsMyMovement(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Vector3d playerPos) {
/* 123 */     Vector3i pos = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4) ? new Vector3i(GrimMath.floor(playerPos.getX()), GrimMath.floor(playerPos.getY() - 0.5000001D), GrimMath.floor(playerPos.getZ())) : getOnPos(player, playerPos, mainSupportingBlockData, 0.500001F);
/* 124 */     return player.compensatedWorld.getBlockType(pos.x, pos.y, pos.z);
/*     */   }
/*     */   
/*     */   private static Vector3i getOnPos(GrimPlayer player, Vector3d playerPos, MainSupportingBlockData mainSupportingBlockData, float searchBelowPlayer) {
/* 128 */     Vector3i mainBlockPos = mainSupportingBlockData.blockPos();
/* 129 */     if (mainBlockPos != null) {
/* 130 */       StateType blockstate = player.compensatedWorld.getBlockType(mainBlockPos.x, mainBlockPos.y, mainBlockPos.z);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 135 */       boolean shouldReturn = ((searchBelowPlayer > 0.5D || !BlockTags.FENCES.contains(blockstate)) && !BlockTags.WALLS.contains(blockstate) && !BlockTags.FENCE_GATES.contains(blockstate));
/*     */       
/* 137 */       return shouldReturn ? mainBlockPos.withY(GrimMath.floor(playerPos.getY() - searchBelowPlayer)) : mainBlockPos;
/*     */     } 
/* 139 */     return new Vector3i(GrimMath.floor(playerPos.getX()), GrimMath.floor(playerPos.getY() - searchBelowPlayer), GrimMath.floor(playerPos.getZ()));
/*     */   }
/*     */ 
/*     */   
/*     */   public static float getMaterialFriction(GrimPlayer player, StateType material) {
/* 144 */     float friction = 0.6F;
/*     */     
/* 146 */     if (material == StateTypes.ICE) friction = 0.98F; 
/* 147 */     if (material == StateTypes.SLIME_BLOCK && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)) {
/* 148 */       friction = 0.8F;
/*     */     }
/* 150 */     if (material == StateTypes.HONEY_BLOCK && player.getClientVersion().isOlderThan(ClientVersion.V_1_15))
/* 151 */       friction = 0.8F; 
/* 152 */     if (material == StateTypes.PACKED_ICE) friction = 0.98F; 
/* 153 */     if (material == StateTypes.FROSTED_ICE) friction = 0.98F; 
/* 154 */     if (material == StateTypes.BLUE_ICE) {
/* 155 */       friction = 0.98F;
/* 156 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
/* 157 */         friction = 0.989F;
/*     */       }
/*     */     } 
/* 160 */     return friction;
/*     */   }
/*     */   
/*     */   private static StateType getOnBlock(GrimPlayer player, double x, double y, double z) {
/* 164 */     StateType block1 = player.compensatedWorld.getBlockType(GrimMath.floor(x), GrimMath.floor(y - 0.20000000298023224D), GrimMath.floor(z));
/*     */     
/* 166 */     if (block1.isAir()) {
/* 167 */       StateType block2 = player.compensatedWorld.getBlockType(GrimMath.floor(x), GrimMath.floor(y - 1.2000000476837158D), GrimMath.floor(z));
/*     */       
/* 169 */       if (Materials.isFence(block2) || Materials.isWall(block2) || Materials.isGate(block2)) {
/* 170 */         return block2;
/*     */       }
/*     */     } 
/*     */     
/* 174 */     return block1;
/*     */   }
/*     */   
/*     */   private static float getBlockSpeedFactorLegacy(GrimPlayer player, Vector3d pos) {
/* 178 */     StateType block = player.compensatedWorld.getBlockType(pos.getX(), pos.getY(), pos.getZ());
/*     */ 
/*     */     
/* 181 */     if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_16_1)) {
/* 182 */       StateType onBlock = getOnBlock(player, pos.getX(), pos.getY(), pos.getZ());
/* 183 */       if (onBlock == StateTypes.SOUL_SAND && player.inventory.getBoots().getEnchantmentLevel(EnchantmentTypes.SOUL_SPEED) > 0) {
/* 184 */         return 1.0F;
/*     */       }
/*     */     } 
/* 187 */     float speed = getBlockSpeedFactor(player, block);
/* 188 */     if (speed != 1.0F || block == StateTypes.SOUL_SAND || block == StateTypes.WATER || block == StateTypes.BUBBLE_COLUMN) {
/* 189 */       return speed;
/*     */     }
/* 191 */     StateType block2 = player.compensatedWorld.getBlockType(pos.getX(), pos.getY() - 0.5000001D, pos.getZ());
/* 192 */     return getBlockSpeedFactor(player, block2);
/*     */   }
/*     */   
/*     */   private static float getBlockSpeedFactor(GrimPlayer player, StateType type) {
/* 196 */     if (type == StateTypes.HONEY_BLOCK) return 0.4F; 
/* 197 */     if (type == StateTypes.SOUL_SAND) {
/*     */ 
/*     */ 
/*     */       
/* 201 */       if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21) && player
/* 202 */         .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16_2) && player.inventory
/* 203 */         .getBoots().getEnchantmentLevel(EnchantmentTypes.SOUL_SPEED) > 0)
/* 204 */         return 1.0F; 
/* 205 */       return 0.4F;
/*     */     } 
/* 207 */     return 1.0F;
/*     */   }
/*     */   
/*     */   private static float getModernVelocityMultiplier(GrimPlayer player, float blockSpeedFactor) {
/* 211 */     if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21)) return blockSpeedFactor; 
/* 212 */     return (float)GrimMath.lerp((float)player.compensatedEntities.self.getAttributeValue(Attributes.MOVEMENT_EFFICIENCY), blockSpeedFactor, 1.0D);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\BlockProperties.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */