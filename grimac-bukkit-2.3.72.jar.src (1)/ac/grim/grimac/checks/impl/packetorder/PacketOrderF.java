/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayDeque;
/*    */ 
/*    */ @CheckData(name = "PacketOrderF", experimental = true)
/*    */ public class PacketOrderF
/*    */   extends Check implements PostPredictionCheck {
/*    */   public PacketOrderF(GrimPlayer player) {
/* 19 */     super(player);
/*    */ 
/*    */     
/* 22 */     this.flags = new ArrayDeque<>();
/*    */   }
/*    */   private final ArrayDeque<String> flags;
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if ((event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY || event
/* 27 */       .getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT || event
/* 28 */       .getPacketType() == PacketType.Play.Client.USE_ITEM || event
/* 29 */       .getPacketType() == PacketType.Play.Client.PICK_ITEM || event
/* 30 */       .getPacketType() == PacketType.Play.Client.PLAYER_DIGGING || (event
/* 31 */       .getPacketType() == PacketType.Play.Client.CLIENT_STATUS && (new WrapperPlayClientClientStatus(event))
/* 32 */       .getAction() == WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT)) && (
/* 33 */       this.player.packetOrderProcessor.isSprinting() || this.player.packetOrderProcessor.isSneaking())) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 41 */       String verbose = "action=" + ((event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) ? "interact" : ((event.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) ? "place" : ((event.getPacketType() == PacketType.Play.Client.USE_ITEM) ? "use" : ((event.getPacketType() == PacketType.Play.Client.PICK_ITEM) ? "pick" : ((event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) ? "dig" : "openInventory"))))) + ", sprinting=" + this.player.packetOrderProcessor.isSprinting() + ", sneaking=" + this.player.packetOrderProcessor.isSneaking();
/* 42 */       if (!this.player.canSkipTicks()) {
/* 43 */         if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 44 */           if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING && (new WrapperPlayClientPlayerDigging(event))
/* 45 */             .getAction() == DiggingAction.RELEASE_USE_ITEM) {
/*    */             return;
/*    */           }
/* 48 */           event.setCancelled(true);
/* 49 */           this.player.onPacketCancel();
/*    */         } 
/*    */       } else {
/* 52 */         this.flags.add(verbose);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 59 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 61 */     if (this.player.isTickingReliablyFor(3)) {
/* 62 */       for (String verbose : this.flags) {
/* 63 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 67 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */