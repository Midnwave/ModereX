/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ 
/*    */ @CheckData(name = "AirLiquidBreak", description = "Breaking a block that cannot be broken")
/*    */ public class AirLiquidBreak
/*    */   extends Check implements BlockBreakCheck {
/* 19 */   public final boolean noFireHitbox = this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2);
/*    */   
/*    */   private int lastTick;
/*    */   private boolean didLastFlag;
/* 23 */   private Vector3i lastBreakLoc = new Vector3i();
/* 24 */   private StateType lastBlockType = StateTypes.AIR;
/*    */   
/*    */   public AirLiquidBreak(GrimPlayer player) {
/* 27 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 32 */     if (blockBreak.action != DiggingAction.START_DIGGING && blockBreak.action != DiggingAction.FINISHED_DIGGING) {
/*    */       return;
/*    */     }
/* 35 */     StateType block = blockBreak.block.getType();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 41 */     int newTick = (GrimAPI.INSTANCE.getTickManager()).currentTick;
/* 42 */     if (this.lastTick == newTick && this.lastBreakLoc
/* 43 */       .equals(blockBreak.position) && !this.didLastFlag && this.lastBlockType
/*    */       
/* 45 */       .getHardness() == 0.0F && this.lastBlockType
/* 46 */       .getBlastResistance() == 0.0F && block == StateTypes.WATER) {
/*    */       return;
/*    */     }
/* 49 */     this.lastTick = newTick;
/* 50 */     this.lastBreakLoc = blockBreak.position;
/* 51 */     this.lastBlockType = block;
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
/* 62 */     boolean invalid = ((block == StateTypes.LIGHT && !this.player.inventory.getHeldItem().is(ItemTypes.LIGHT) && !this.player.inventory.getOffHand().is(ItemTypes.LIGHT)) || block.isAir() || block == StateTypes.WATER || block == StateTypes.LAVA || block == StateTypes.BUBBLE_COLUMN || block == StateTypes.MOVING_PISTON || (block == StateTypes.FIRE && this.noFireHitbox) || (block.getHardness() == -1.0F && blockBreak.action == DiggingAction.FINISHED_DIGGING));
/*    */     
/* 64 */     if (invalid && flagAndAlert("block=" + block.getName() + ", type=" + String.valueOf(blockBreak.action)) && shouldModifyPackets()) {
/* 65 */       this.didLastFlag = true;
/* 66 */       blockBreak.cancel();
/*    */     } else {
/* 68 */       this.didLastFlag = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\AirLiquidBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */