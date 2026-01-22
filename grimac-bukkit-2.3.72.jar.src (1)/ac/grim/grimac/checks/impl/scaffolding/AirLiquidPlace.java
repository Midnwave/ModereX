/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.api.config.ConfigManager;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.change.BlockModification;
/*    */ import ac.grim.grimac.utils.nmsutil.Materials;
/*    */ 
/*    */ @CheckData(name = "AirLiquidPlace", description = "Placed a block against an invalid support")
/*    */ public class AirLiquidPlace extends BlockPlaceCheck {
/*    */   public AirLiquidPlace(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 59 */     if (this.player.gamemode == GameMode.CREATIVE)
/*    */       return; 
/* 61 */     Vector3i blockPos = place.position;
/* 62 */     StateType placeAgainst = this.player.compensatedWorld.getBlockType(blockPos.getX(), blockPos.getY(), blockPos.getZ());
/*    */     
/* 64 */     int currentTick = (GrimAPI.INSTANCE.getTickManager()).currentTick;
/*    */ 
/*    */     
/* 67 */     Iterable<BlockModification> blockModifications = this.player.blockHistory.getRecentModifications(blockModification -> (currentTick - blockModification.tick() < 2 && blockPos.equals(blockModification.location()) && (blockModification.cause() == BlockModification.Cause.START_DIGGING || blockModification.cause() == BlockModification.Cause.HANDLE_NETTY_SYNC_TRANSACTION)));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 73 */     for (BlockModification blockModification : blockModifications) {
/* 74 */       StateType stateType = blockModification.oldBlockContents().getType();
/* 75 */       if (!stateType.isAir() && !Materials.isNoPlaceLiquid(stateType)) {
/*    */         return;
/*    */       }
/*    */     } 
/*    */     
/* 80 */     if ((placeAgainst.isAir() || Materials.isNoPlaceLiquid(placeAgainst)) && 
/* 81 */       flagAndAlert() && shouldModifyPackets() && shouldCancel()) {
/* 82 */       place.resync();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onReload(ConfigManager config) {
/* 89 */     this.cancelVL = config.getIntElse(getConfigName() + ".cancelVL", 0);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\AirLiquidPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */