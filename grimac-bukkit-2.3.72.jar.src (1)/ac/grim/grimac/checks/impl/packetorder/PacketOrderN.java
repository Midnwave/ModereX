/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "PacketOrderN", experimental = true)
/*    */ public class PacketOrderN extends BlockPlaceCheck {
/*    */   public PacketOrderN(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */   
/*    */   private int invalid;
/*    */   private boolean usingWithoutPlacing;
/*    */   private boolean placing;
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 25 */     this.placing = true;
/* 26 */     if (this.usingWithoutPlacing) {
/* 27 */       if (!this.player.canSkipTicks()) {
/* 28 */         if (flagAndAlert() && shouldModifyPackets() && shouldCancel()) {
/* 29 */           place.resync();
/*    */         }
/*    */       } else {
/* 32 */         this.invalid++;
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 39 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM || (event
/* 40 */       .getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT && (new WrapperPlayClientPlayerBlockPlacement(event))
/* 41 */       .getFace() == BlockFace.OTHER)) {
/* 42 */       if (!this.placing) {
/* 43 */         this.usingWithoutPlacing = true;
/*    */       }
/*    */       
/* 46 */       this.placing = false;
/*    */     } 
/*    */     
/* 49 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 50 */       this.usingWithoutPlacing = this.placing = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 56 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 58 */     if (this.player.isTickingReliablyFor(3)) {
/* 59 */       for (; this.invalid >= 1; this.invalid--) {
/* 60 */         flagAndAlert();
/*    */       }
/*    */     }
/*    */     
/* 64 */     this.invalid = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderN.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */