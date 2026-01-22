/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ 
/*    */ @CheckData(name = "InvalidBreak", description = "Sent impossible block face id")
/*    */ public class InvalidBreak extends Check implements BlockBreakCheck {
/*    */   public InvalidBreak(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 19 */     if (blockBreak.faceId == 255 && blockBreak.action == DiggingAction.CANCELLED_DIGGING && this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
/*    */       return;
/*    */     }
/*    */     
/* 23 */     if (blockBreak.faceId < 0 || blockBreak.faceId > 5)
/*    */     {
/* 25 */       if (flagAndAlert("face=" + blockBreak.faceId + ", action=" + String.valueOf(blockBreak.action)) && shouldModifyPackets())
/* 26 */         blockBreak.cancel(); 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\InvalidBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */