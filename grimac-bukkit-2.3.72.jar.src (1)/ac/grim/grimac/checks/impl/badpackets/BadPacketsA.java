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
/*    */ @CheckData(name = "BadPacketsA", description = "Sent duplicate slot id")
/*    */ public class BadPacketsA extends Check implements PacketCheck {
/* 13 */   int lastSlot = -1;
/*    */   
/*    */   public BadPacketsA(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 21 */     if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {
/* 22 */       int slot = (new WrapperPlayClientHeldItemChange(event)).getSlot();
/*    */       
/* 24 */       if (slot == this.lastSlot && flagAndAlert("slot=" + slot) && shouldModifyPackets()) {
/* 25 */         event.setCancelled(true);
/* 26 */         this.player.onPacketCancel();
/*    */       } 
/*    */       
/* 29 */       this.lastSlot = slot;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */