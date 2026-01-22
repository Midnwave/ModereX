/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientHeldItemChange;
/*    */ 
/*    */ @CheckData(name = "BadPacketsY", description = "Sent out of bounds slot id")
/*    */ public class BadPacketsY extends Check implements PacketCheck {
/*    */   public BadPacketsY(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
/* 20 */       int slot = (new WrapperPlayClientHeldItemChange(event)).getSlot();
/* 21 */       if ((slot > 8 || slot < 0) && 
/* 22 */         flagAndAlert("slot=" + slot) && shouldModifyPackets()) {
/* 23 */         event.setCancelled(true);
/* 24 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsY.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */