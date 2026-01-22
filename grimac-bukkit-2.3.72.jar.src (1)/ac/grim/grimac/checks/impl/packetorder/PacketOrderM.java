/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "PacketOrderM", experimental = true)
/*    */ public class PacketOrderM extends Check implements PostPredictionCheck {
/*    */   public PacketOrderM(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */   
/*    */   private int invalid;
/*    */   private boolean usingWithoutInteract;
/*    */   private boolean interacting;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY && (
/* 27 */       new WrapperPlayClientInteractEntity(event)).getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/* 28 */       this.interacting = true;
/* 29 */       if (this.usingWithoutInteract) {
/* 30 */         if (!this.player.canSkipTicks()) {
/* 31 */           if (flagAndAlert() && shouldModifyPackets()) {
/* 32 */             event.setCancelled(true);
/* 33 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 36 */           this.invalid++;
/*    */         } 
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 42 */     if (event.getPacketType() == PacketType.Play.Client.USE_ITEM || (event
/* 43 */       .getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT && (new WrapperPlayClientPlayerBlockPlacement(event))
/* 44 */       .getFace() == BlockFace.OTHER)) {
/* 45 */       if (!this.interacting) {
/* 46 */         this.usingWithoutInteract = true;
/*    */       }
/*    */       
/* 49 */       this.interacting = false;
/*    */     } 
/*    */     
/* 52 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 53 */       this.usingWithoutInteract = this.interacting = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 59 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 61 */     if (this.player.isTickingReliablyFor(3)) {
/* 62 */       for (; this.invalid >= 1; this.invalid--) {
/* 63 */         flagAndAlert();
/*    */       }
/*    */     }
/*    */     
/* 67 */     this.invalid = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderM.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */