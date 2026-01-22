/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ @CheckData(name = "MultiBreak", experimental = true)
/*    */ public class MultiBreak
/*    */   extends Check implements BlockBreakCheck {
/* 21 */   private final List<String> flags = new ArrayList<>();
/*    */   private boolean hasBroken;
/*    */   private BlockFace lastFace;
/*    */   private Vector3i lastPos;
/*    */   
/*    */   public MultiBreak(GrimPlayer player) {
/* 27 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 32 */     if (blockBreak.action == DiggingAction.CANCELLED_DIGGING) {
/*    */       return;
/*    */     }
/*    */     
/* 36 */     if (this.hasBroken && (blockBreak.face != this.lastFace || !blockBreak.position.equals(this.lastPos))) {
/*    */ 
/*    */       
/* 39 */       String verbose = "face=" + String.valueOf(blockBreak.face) + ", lastFace=" + String.valueOf(this.lastFace) + ", pos=" + MessageUtil.toUnlabledString(blockBreak.position) + ", lastPos=" + MessageUtil.toUnlabledString(this.lastPos);
/* 40 */       if (!this.player.canSkipTicks()) {
/* 41 */         if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 42 */           blockBreak.cancel();
/*    */         }
/*    */       } else {
/* 45 */         this.flags.add(verbose);
/*    */       } 
/*    */     } 
/*    */     
/* 49 */     this.lastFace = blockBreak.face;
/* 50 */     this.lastPos = blockBreak.position;
/* 51 */     this.hasBroken = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 56 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 57 */       this.hasBroken = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 63 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 65 */     if (this.player.isTickingReliablyFor(3)) {
/* 66 */       for (String verbose : this.flags) {
/* 67 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 71 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\MultiBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */