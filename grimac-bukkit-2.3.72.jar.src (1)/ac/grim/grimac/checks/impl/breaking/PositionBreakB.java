/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ 
/*    */ @CheckData(name = "PositionBreakB", experimental = true)
/*    */ public class PositionBreakB extends Check implements BlockBreakCheck {
/* 14 */   private final int releaseFace = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? 0 : 255;
/*    */   private BlockFace lastFace;
/*    */   
/*    */   public PositionBreakB(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 23 */     if (blockBreak.action == DiggingAction.START_DIGGING && 
/* 24 */       blockBreak.face == this.lastFace) {
/* 25 */       this.lastFace = null;
/*    */     }
/*    */ 
/*    */     
/* 29 */     if (this.lastFace != null) {
/* 30 */       flagAndAlert("lastFace=" + String.valueOf(this.lastFace) + ", action=" + String.valueOf(blockBreak.action));
/*    */     }
/*    */     
/* 33 */     if (blockBreak.action == DiggingAction.CANCELLED_DIGGING)
/* 34 */       this.lastFace = (blockBreak.faceId == this.releaseFace) ? null : blockBreak.face; 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\PositionBreakB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */