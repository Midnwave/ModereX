/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.nmsutil.BlockBreakSpeed;
/*    */ 
/*    */ @CheckData(name = "WrongBreak")
/*    */ public class WrongBreak
/*    */   extends Check implements BlockBreakCheck {
/* 20 */   private final int exemptedY = this.player.getClientVersion().isOlderThan(ClientVersion.V_1_8) ? 255 : (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14) ? -1 : 4095);
/*    */   private boolean lastBlockWasInstantBreak = false;
/* 22 */   private Vector3i lastLastBlock = null; private Vector3i lastBlock; private Vector3i lastCancelledBlock;
/*    */   
/*    */   public WrongBreak(GrimPlayer player) {
/* 25 */     super(player);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean shouldExempt(WrappedBlockState block, int yPos) {
/* 31 */     if (this.lastLastBlock != null || this.lastBlock == null) {
/* 32 */       return false;
/*    */     }
/*    */     
/* 35 */     if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4) && yPos != this.exemptedY) {
/* 36 */       return false;
/*    */     }
/*    */     
/* 39 */     return (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4) || BlockBreakSpeed.getBlockDamage(this.player, block) < 1.0D);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 44 */     if (blockBreak.action == DiggingAction.START_DIGGING) {
/* 45 */       Vector3i pos = blockBreak.position;
/*    */       
/* 47 */       this.lastBlockWasInstantBreak = (BlockBreakSpeed.getBlockDamage(this.player, blockBreak.block) >= 1.0D);
/* 48 */       this.lastCancelledBlock = null;
/* 49 */       this.lastLastBlock = this.lastBlock;
/* 50 */       this.lastBlock = pos;
/*    */     } 
/*    */     
/* 53 */     if (blockBreak.action == DiggingAction.CANCELLED_DIGGING) {
/* 54 */       Vector3i pos = blockBreak.position;
/*    */       
/* 56 */       if (!shouldExempt(blockBreak.block, pos.y) && !pos.equals(this.lastBlock))
/*    */       {
/* 58 */         if ((this.player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4) || (!this.lastBlockWasInstantBreak && pos.equals(this.lastCancelledBlock))) && 
/* 59 */           flagAndAlert("action=CANCELLED_DIGGING, last=" + MessageUtil.toUnlabledString(this.lastBlock) + ", pos=" + MessageUtil.toUnlabledString(pos)) && 
/* 60 */           shouldModifyPackets()) {
/* 61 */           blockBreak.cancel();
/*    */         }
/*    */       }
/*    */ 
/*    */ 
/*    */       
/* 67 */       this.lastCancelledBlock = pos;
/* 68 */       this.lastLastBlock = null;
/* 69 */       this.lastBlock = null;
/*    */       
/*    */       return;
/*    */     } 
/* 73 */     if (blockBreak.action == DiggingAction.FINISHED_DIGGING) {
/* 74 */       Vector3i pos = blockBreak.position;
/*    */ 
/*    */       
/* 77 */       if (!pos.equals(this.lastCancelledBlock) && (!this.lastBlockWasInstantBreak || this.player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4)) && !pos.equals(this.lastBlock) && 
/* 78 */         flagAndAlert("action=FINISHED_DIGGING, last=" + MessageUtil.toUnlabledString(this.lastBlock) + ", pos=" + MessageUtil.toUnlabledString(pos)) && 
/* 79 */         shouldModifyPackets()) {
/* 80 */         blockBreak.cancel();
/*    */       }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 86 */       if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4)) {
/* 87 */         this.lastCancelledBlock = null;
/* 88 */         this.lastLastBlock = null;
/* 89 */         this.lastBlock = null;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\WrongBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */