/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
/*    */ 
/*    */ @CheckData(name = "MultiActionsE", description = "Swinging while using an item", experimental = true)
/*    */ public class MultiActionsE extends Check implements PacketCheck {
/*    */   private boolean dropping;
/*    */   
/*    */   public MultiActionsE(GrimPlayer player) {
/* 19 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 24 */     if (!this.dropping && this.player.packetStateData.isSlowedByUsingItem() && (this.player.packetStateData.lastSlotSelected == this.player.packetStateData.getSlowedByUsingItemSlot() || this.player.packetStateData.itemInUseHand == InteractionHand.OFF_HAND) && event.getPacketType() == PacketType.Play.Client.ANIMATION) {
/*    */       
/* 26 */       if (this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10)) {
/*    */         return;
/*    */       }
/*    */       
/* 30 */       if (flagAndAlert() && shouldModifyPackets()) {
/* 31 */         event.setCancelled(true);
/* 32 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */     
/* 36 */     if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE) {
/* 37 */       this.dropping = false;
/*    */     }
/*    */     
/* 40 */     if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15)) {
/* 41 */       DiggingAction action = (new WrapperPlayClientPlayerDigging(event)).getAction();
/* 42 */       this.dropping = (action == DiggingAction.DROP_ITEM || action == DiggingAction.DROP_ITEM_STACK);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */