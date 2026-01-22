/*     */ package ac.grim.grimac.utils.nmsutil;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute.Attributes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.ComponentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemTool;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.utils.data.tags.SyncedTag;
/*     */ import ac.grim.grimac.utils.data.tags.SyncedTags;
/*     */ import ac.grim.grimac.utils.enums.FluidTag;
/*     */ import ac.grim.grimac.utils.inventory.EnchantmentHelper;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.util.Optional;
/*     */ import java.util.OptionalInt;
/*     */ import java.util.Set;
/*     */ import lombok.Generated;
/*     */ 
/*     */ public final class BlockBreakSpeed {
/*     */   @Generated
/*     */   private BlockBreakSpeed() {
/*  34 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   }
/*  36 */   private static final Set<StateType> HARVESTABLE_TYPES_1_21_4 = Sets.newHashSet((Object[])new StateType[] { StateTypes.BELL, StateTypes.LANTERN, StateTypes.SOUL_LANTERN, StateTypes.COPPER_DOOR, StateTypes.EXPOSED_COPPER_DOOR, StateTypes.OXIDIZED_COPPER_DOOR, StateTypes.WEATHERED_COPPER_DOOR, StateTypes.WAXED_COPPER_DOOR, StateTypes.WAXED_EXPOSED_COPPER_DOOR, StateTypes.WAXED_OXIDIZED_COPPER_DOOR, StateTypes.WAXED_WEATHERED_COPPER_DOOR, StateTypes.IRON_DOOR, StateTypes.HEAVY_WEIGHTED_PRESSURE_PLATE, StateTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, StateTypes.POLISHED_BLACKSTONE_PRESSURE_PLATE, StateTypes.STONE_PRESSURE_PLATE, StateTypes.BREWING_STAND, StateTypes.ENDER_CHEST });
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
/*  57 */   private static final boolean serverUsesComponentsAndRules = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5);
/*     */ 
/*     */ 
/*     */   
/*     */   public static double getBlockDamage(GrimPlayer player, WrappedBlockState block) {
/*  62 */     ItemStack tool = player.inventory.getHeldItem();
/*  63 */     ItemType toolType = tool.getType();
/*     */     
/*  65 */     if (player.gamemode == GameMode.CREATIVE) {
/*  66 */       if (serverUsesComponentsAndRules && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_5)) {
/*  67 */         return 
/*     */           
/*  69 */           ((Boolean)tool.getComponent(ComponentTypes.TOOL).map(ItemTool::isCanDestroyBlocksInCreative).orElse(Boolean.valueOf(true))).booleanValue() ? 1.0D : 0.0D;
/*     */       }
/*  71 */       if (toolType.hasAttribute(ItemTypes.ItemAttribute.SWORD) || toolType == ItemTypes.TRIDENT || (toolType == ItemTypes.DEBUG_STICK && player
/*  72 */         .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) || (toolType == ItemTypes.MACE && player
/*  73 */         .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5))) {
/*  74 */         return 0.0D;
/*     */       }
/*  76 */       return 1.0D;
/*     */     } 
/*     */ 
/*     */     
/*  80 */     float blockHardness = block.getType().getHardness();
/*     */ 
/*     */     
/*  83 */     if ((block.getType() == StateTypes.PISTON || block.getType() == StateTypes.PISTON_HEAD || block.getType() == StateTypes.STICKY_PISTON) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2)) {
/*  84 */       blockHardness = 0.5F;
/*     */     }
/*     */     
/*  87 */     if (blockHardness == -1.0F) return 0.0D;
/*     */     
/*  89 */     boolean isCorrectToolForDrop = false;
/*  90 */     float speedMultiplier = 1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  95 */     if (serverUsesComponentsAndRules && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/*  96 */       Optional<ItemTool> toolComponentOpt = tool.getComponent(ComponentTypes.TOOL);
/*  97 */       if (toolComponentOpt.isPresent()) {
/*  98 */         ItemTool itemTool = toolComponentOpt.get();
/*     */ 
/*     */ 
/*     */         
/* 102 */         speedMultiplier = itemTool.getDefaultMiningSpeed();
/*     */         
/* 104 */         boolean speedFound = false;
/* 105 */         boolean dropsFound = false;
/*     */         
/* 107 */         for (ItemTool.Rule rule : itemTool.getRules()) {
/* 108 */           boolean isMatch; MappedEntitySet<StateType.Mapped> predicate = rule.getBlocks();
/* 109 */           ResourceLocation tagKey = predicate.getTagKey();
/*     */ 
/*     */ 
/*     */           
/* 113 */           if (tagKey != null) {
/* 114 */             SyncedTag<StateType> playerTag = player.tagManager.block(tagKey);
/*     */             
/* 116 */             isMatch = ((playerTag != null && playerTag.contains(block.getType())) || BlockTags.getByName(tagKey.getKey()).contains(block.getType()));
/*     */           } else {
/* 118 */             isMatch = predicate.getEntities().contains(block.getType().getMapped());
/*     */           } 
/*     */ 
/*     */           
/* 122 */           if (isMatch) {
/*     */             
/* 124 */             if (!speedFound && rule.getSpeed() != null) {
/* 125 */               speedMultiplier = rule.getSpeed().floatValue();
/* 126 */               speedFound = true;
/*     */             } 
/*     */ 
/*     */             
/* 130 */             if (!dropsFound && rule.getCorrectForDrops() != null) {
/* 131 */               isCorrectToolForDrop = rule.getCorrectForDrops().booleanValue();
/* 132 */               dropsFound = true;
/*     */             } 
/*     */           } 
/*     */           
/* 136 */           if (speedFound && dropsFound) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 143 */       if (toolType.hasAttribute(ItemTypes.ItemAttribute.AXE)) {
/* 144 */         isCorrectToolForDrop = player.tagManager.block(SyncedTags.MINEABLE_AXE).contains(block.getType());
/* 145 */       } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.PICKAXE)) {
/* 146 */         isCorrectToolForDrop = player.tagManager.block(SyncedTags.MINEABLE_PICKAXE).contains(block.getType());
/* 147 */       } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.SHOVEL)) {
/* 148 */         isCorrectToolForDrop = player.tagManager.block(SyncedTags.MINEABLE_SHOVEL).contains(block.getType());
/* 149 */       } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.HOE)) {
/* 150 */         isCorrectToolForDrop = player.tagManager.block(SyncedTags.MINEABLE_HOE).contains(block.getType());
/*     */       } 
/*     */       
/* 153 */       if (isCorrectToolForDrop) {
/* 154 */         int tier = 0;
/* 155 */         if (toolType.hasAttribute(ItemTypes.ItemAttribute.WOOD_TIER)) {
/* 156 */           speedMultiplier = 2.0F;
/* 157 */         } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.STONE_TIER)) {
/* 158 */           speedMultiplier = 4.0F;
/* 159 */           tier = 1;
/* 160 */         } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.IRON_TIER)) {
/* 161 */           speedMultiplier = 6.0F;
/* 162 */           tier = 2;
/* 163 */         } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.DIAMOND_TIER)) {
/* 164 */           speedMultiplier = 8.0F;
/* 165 */           tier = 3;
/* 166 */         } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.GOLD_TIER)) {
/* 167 */           speedMultiplier = 12.0F;
/* 168 */         } else if (toolType.hasAttribute(ItemTypes.ItemAttribute.NETHERITE_TIER)) {
/* 169 */           speedMultiplier = 9.0F;
/* 170 */           tier = 4;
/*     */         } 
/*     */         
/* 173 */         if (tier < 3 && player.tagManager.block(SyncedTags.NEEDS_DIAMOND_TOOL).contains(block.getType())) {
/* 174 */           isCorrectToolForDrop = false;
/* 175 */         } else if (tier < 2 && player.tagManager.block(SyncedTags.NEEDS_IRON_TOOL).contains(block.getType())) {
/* 176 */           isCorrectToolForDrop = false;
/* 177 */         } else if (tier < 1 && player.tagManager.block(SyncedTags.NEEDS_STONE_TOOL).contains(block.getType())) {
/* 178 */           isCorrectToolForDrop = false;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 183 */       if (toolType == ItemTypes.SHEARS) {
/* 184 */         isCorrectToolForDrop = true;
/*     */         
/* 186 */         if (block.getType() == StateTypes.COBWEB || Materials.isLeaves(block.getType())) {
/* 187 */           speedMultiplier = 15.0F;
/* 188 */         } else if (BlockTags.WOOL.contains(block.getType())) {
/* 189 */           speedMultiplier = 5.0F;
/* 190 */         } else if (block.getType() == StateTypes.VINE || block
/* 191 */           .getType() == StateTypes.GLOW_LICHEN) {
/* 192 */           speedMultiplier = 2.0F;
/*     */         }
/*     */         else {
/*     */           
/* 196 */           isCorrectToolForDrop = (block.getType() == StateTypes.COBWEB || block.getType() == StateTypes.REDSTONE_WIRE || block.getType() == StateTypes.TRIPWIRE);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 201 */       if (toolType.hasAttribute(ItemTypes.ItemAttribute.SWORD)) {
/* 202 */         if (block.getType() == StateTypes.COBWEB) {
/* 203 */           speedMultiplier = 15.0F;
/* 204 */         } else if (player.tagManager.block(SyncedTags.SWORD_EFFICIENT).contains(block.getType())) {
/* 205 */           speedMultiplier = 1.5F;
/*     */         } 
/*     */         
/* 208 */         isCorrectToolForDrop = (block.getType() == StateTypes.COBWEB);
/*     */       } 
/*     */     } 
/*     */     
/* 212 */     if (speedMultiplier > 1.0F) {
/* 213 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21)) {
/* 214 */         speedMultiplier += (float)player.compensatedEntities.self.getAttributeValue(Attributes.MINING_EFFICIENCY);
/*     */       } else {
/* 216 */         int i = tool.getEnchantmentLevel(EnchantmentTypes.BLOCK_EFFICIENCY);
/* 217 */         if (i > 0) {
/* 218 */           speedMultiplier += (i * i + 1);
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 223 */     OptionalInt digSpeed = player.compensatedEntities.getPotionLevelForSelfPlayer(PotionTypes.HASTE);
/* 224 */     OptionalInt conduit = player.compensatedEntities.getPotionLevelForSelfPlayer(PotionTypes.CONDUIT_POWER);
/*     */     
/* 226 */     if (digSpeed.isPresent() || conduit.isPresent()) {
/* 227 */       int hasteLevel = Math.max(digSpeed.isEmpty() ? 0 : digSpeed.getAsInt(), conduit.isEmpty() ? 0 : conduit.getAsInt());
/* 228 */       speedMultiplier *= (float)(1.0D + 0.2D * (hasteLevel + 1));
/*     */     } 
/*     */     
/* 231 */     OptionalInt miningFatigue = player.compensatedEntities.getPotionLevelForSelfPlayer(PotionTypes.MINING_FATIGUE);
/*     */     
/* 233 */     if (miningFatigue.isPresent()) {
/* 234 */       switch (miningFatigue.getAsInt()) {
/*     */         case 0:
/* 236 */           speedMultiplier *= 0.3F;
/*     */           break;
/*     */         case 1:
/* 239 */           speedMultiplier *= 0.09F;
/*     */           break;
/*     */         case 2:
/* 242 */           speedMultiplier *= 0.0027F;
/*     */           break;
/*     */         default:
/* 245 */           speedMultiplier *= 8.1E-4F;
/*     */           break;
/*     */       } 
/*     */     }
/* 249 */     speedMultiplier *= (float)player.compensatedEntities.self.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
/*     */     
/* 251 */     if (player.fluidOnEyes == FluidTag.WATER) {
/* 252 */       if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_21)) {
/* 253 */         speedMultiplier *= (float)player.compensatedEntities.self.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
/*     */       }
/* 255 */       else if (EnchantmentHelper.getMaximumEnchantLevel(player.inventory, EnchantmentTypes.AQUA_AFFINITY) == 0) {
/* 256 */         speedMultiplier /= 5.0F;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 261 */     if (!player.packetStateData.packetPlayerOnGround) {
/* 262 */       speedMultiplier /= 5.0F;
/*     */     }
/*     */     
/* 265 */     float damage = speedMultiplier / blockHardness;
/*     */ 
/*     */ 
/*     */     
/* 269 */     boolean canHarvest = (!block.getType().isRequiresCorrectTool() || isCorrectToolForDrop || (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_4) && HARVESTABLE_TYPES_1_21_4.contains(block.getType())));
/* 270 */     damage /= canHarvest ? 30.0F : 100.0F;
/*     */     
/* 272 */     return damage;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\nmsutil\BlockBreakSpeed.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */