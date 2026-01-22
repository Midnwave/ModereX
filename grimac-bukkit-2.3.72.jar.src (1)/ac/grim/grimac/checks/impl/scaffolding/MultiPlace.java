/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ @CheckData(name = "MultiPlace", description = "Placed multiple blocks in a tick", experimental = true)
/*    */ public class MultiPlace
/*    */   extends BlockPlaceCheck {
/* 20 */   private final List<String> flags = new ArrayList<>();
/*    */   private boolean hasPlaced;
/*    */   private BlockFace lastFace;
/*    */   private Vector3f lastCursor;
/*    */   private Vector3i lastPos;
/*    */   
/*    */   public MultiPlace(GrimPlayer player) {
/* 27 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 32 */     BlockFace face = place.getFace();
/* 33 */     Vector3f cursor = place.cursor;
/* 34 */     Vector3i pos = place.position;
/*    */     
/* 36 */     if (this.hasPlaced && (face != this.lastFace || !cursor.equals(this.lastCursor) || !pos.equals(this.lastPos))) {
/*    */ 
/*    */       
/* 39 */       String verbose = "face=" + String.valueOf(face) + ", lastFace=" + String.valueOf(this.lastFace) + ", cursor=" + MessageUtil.toUnlabledString(cursor) + ", lastCursor=" + MessageUtil.toUnlabledString(this.lastCursor) + ", pos=" + MessageUtil.toUnlabledString(pos) + ", lastPos=" + MessageUtil.toUnlabledString(this.lastPos);
/* 40 */       if (!this.player.canSkipTicks()) {
/* 41 */         if (flagAndAlert(verbose) && shouldModifyPackets() && shouldCancel()) {
/* 42 */           place.resync();
/*    */         }
/*    */       } else {
/* 45 */         this.flags.add(verbose);
/*    */       } 
/*    */     } 
/*    */     
/* 49 */     this.lastFace = face;
/* 50 */     this.lastCursor = cursor;
/* 51 */     this.lastPos = pos;
/* 52 */     this.hasPlaced = true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 57 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 58 */       this.hasPlaced = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 64 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 66 */     if (this.player.isTickingReliablyFor(3)) {
/* 67 */       for (String verbose : this.flags) {
/* 68 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 72 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\MultiPlace.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */