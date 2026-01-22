/*     */ package ac.grim.grimac.utils.blockplace;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.BlockTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.defaulttags.ItemTags;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Attachment;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*     */ import ac.grim.grimac.utils.collisions.AxisUtil;
/*     */ import ac.grim.grimac.utils.latency.CompensatedWorld;
/*     */ import ac.grim.grimac.utils.nmsutil.Materials;
/*     */ 
/*     */ public final class ConsumesBlockPlace {
/*     */   @Generated
/*     */   private ConsumesBlockPlace() {
/*  20 */     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
/*     */   }
/*     */   public static boolean consumesPlace(@NotNull GrimPlayer player, @NotNull WrappedBlockState state, @NotNull BlockPlace place) {
/*  23 */     if (state.getType() == StateTypes.BELL) {
/*  24 */       return goodBellHit(state, place);
/*     */     }
/*  26 */     if (BlockTags.CANDLE_CAKES.contains(state.getType())) {
/*  27 */       WrappedBlockState cake = StateTypes.CAKE.createBlockState(CompensatedWorld.blockVersion);
/*  28 */       cake.setBites(1);
/*  29 */       player.compensatedWorld.updateBlock(place.position, cake);
/*  30 */       return true;
/*     */     } 
/*  32 */     if (state.getType() == StateTypes.CAKE) {
/*  33 */       if (state.getBites() == 0 && BlockTags.CANDLES.contains(place.material)) {
/*  34 */         player.compensatedWorld.updateBlock(place.position, StateTypes.CANDLE_CAKE.createBlockState(CompensatedWorld.blockVersion));
/*  35 */         return true;
/*     */       } 
/*     */       
/*  38 */       if (player.gamemode == GameMode.CREATIVE || player.food < 20) {
/*  39 */         if (state.getBites() != 6) {
/*  40 */           state.setBites(state.getBites() + 1);
/*  41 */           player.compensatedWorld.updateBlock(place.position, state);
/*     */         } else {
/*  43 */           player.compensatedWorld.updateBlock(place.position, StateTypes.AIR.createBlockState(CompensatedWorld.blockVersion));
/*     */         } 
/*  45 */         return true;
/*     */       } 
/*     */       
/*  48 */       return false;
/*     */     } 
/*  50 */     if (state.getType() == StateTypes.CAVE_VINES || state.getType() == StateTypes.CAVE_VINES_PLANT) {
/*  51 */       if (state.isBerries()) {
/*  52 */         state.setBerries(false);
/*  53 */         player.compensatedWorld.updateBlock(place.position, state);
/*  54 */         return true;
/*     */       } 
/*  56 */       return false;
/*     */     } 
/*  58 */     if (state.getType() == StateTypes.SWEET_BERRY_BUSH) {
/*  59 */       if (state.getAge() != 3 && place.itemStack.getType() == ItemTypes.BONE_MEAL)
/*  60 */         return false; 
/*  61 */       if (state.getAge() > 1) {
/*  62 */         state.setAge(1);
/*  63 */         player.compensatedWorld.updateBlock(place.position, state);
/*  64 */         return true;
/*     */       } 
/*  66 */       return false;
/*     */     } 
/*     */     
/*  69 */     if (state.getType() == StateTypes.TNT && (
/*  70 */       place.itemStack.getType() == ItemTypes.FIRE_CHARGE || place.itemStack.getType() == ItemTypes.FLINT_AND_STEEL)) {
/*  71 */       player.compensatedWorld.updateBlock(place.position, StateTypes.AIR.createBlockState(CompensatedWorld.blockVersion));
/*  72 */       return true;
/*     */     } 
/*     */     
/*  75 */     if (state.getType() == StateTypes.RESPAWN_ANCHOR) {
/*  76 */       if (place.itemStack.getType() == ItemTypes.GLOWSTONE) return true; 
/*  77 */       return (!place.isBlock && player.inventory.getOffHand().getType() == ItemTypes.GLOWSTONE);
/*     */     } 
/*  79 */     if (state.getType() == StateTypes.COMMAND_BLOCK || state.getType() == StateTypes.CHAIN_COMMAND_BLOCK || state
/*  80 */       .getType() == StateTypes.REPEATING_COMMAND_BLOCK || state.getType() == StateTypes.JIGSAW || state
/*  81 */       .getType() == StateTypes.STRUCTURE_BLOCK) {
/*  82 */       return player.canPlaceGameMasterBlocks();
/*     */     }
/*  84 */     if (state.getType() == StateTypes.COMPOSTER) {
/*  85 */       if (Materials.isCompostable(place.itemStack.getType()) && state.getLevel() < 8) {
/*  86 */         return true;
/*     */       }
/*  88 */       return (state.getLevel() == 8);
/*     */     } 
/*  90 */     if (state.getType() == StateTypes.JUKEBOX) {
/*  91 */       return state.isHasRecord();
/*     */     }
/*  93 */     if (state.getType() == StateTypes.LECTERN) {
/*  94 */       if (state.isHasBook()) return true; 
/*  95 */       return ItemTags.LECTERN_BOOKS.contains(place.itemStack.getType());
/*     */     } 
/*     */     
/*  98 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean goodBellHit(@NotNull WrappedBlockState bell, @NotNull BlockPlace place) {
/* 102 */     BlockFace direction = place.getFace();
/* 103 */     return (place.hitData != null && isProperHit(bell, direction, place.hitData.getRelativeBlockHitLocation().getY()));
/*     */   }
/*     */   
/*     */   private static boolean isProperHit(@NotNull WrappedBlockState bell, @NotNull BlockFace direction, double p_49742_) {
/* 107 */     if (direction != BlockFace.UP && direction != BlockFace.DOWN && p_49742_ <= 0.8123999834060669D) switch (bell.getAttachment()) { default: throw new IncompatibleClassChangeError();
/* 108 */         case FLOOR: if (AxisUtil.isSameAxis(bell.getFacing(), direction)); break;
/* 109 */         case SINGLE_WALL: case DOUBLE_WALL: if (!AxisUtil.isSameAxis(bell.getFacing(), direction));
/*     */           break;
/*     */         case CEILING:
/*     */          }
/*     */        
/*     */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grima\\utils\blockplace\ConsumesBlockPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */