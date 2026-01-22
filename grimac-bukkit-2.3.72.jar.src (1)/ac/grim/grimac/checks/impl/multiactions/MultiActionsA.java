/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ 
/*    */ @CheckData(name = "MultiActionsA", description = "Attacked while using an item", experimental = true)
/*    */ public class MultiActionsA extends Check implements PacketCheck {
/*    */   public MultiActionsA(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (this.player.packetStateData.isSlowedByUsingItem() && (this.player.packetStateData.lastSlotSelected == this.player.packetStateData.getSlowedByUsingItemSlot() || this.player.packetStateData.itemInUseHand == InteractionHand.OFF_HAND) && event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY && (
/* 21 */       new WrapperPlayClientInteractEntity(event)).getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK && 
/* 22 */       flagAndAlert() && shouldModifyPackets()) {
/* 23 */       event.setCancelled(true);
/* 24 */       this.player.onPacketCancel();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */