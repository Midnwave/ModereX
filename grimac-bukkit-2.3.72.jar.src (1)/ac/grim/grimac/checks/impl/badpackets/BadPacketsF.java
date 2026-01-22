/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ 
/*    */ @CheckData(name = "BadPacketsF", description = "Sent duplicate sprinting status")
/*    */ public class BadPacketsF extends Check implements PacketCheck {
/*    */   public boolean lastSprinting;
/*    */   public boolean exemptNext = true;
/*    */   
/*    */   public BadPacketsF(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 22 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
/* 23 */       WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);
/*    */       
/* 25 */       if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
/* 26 */         if (this.lastSprinting) {
/* 27 */           if (this.exemptNext) {
/* 28 */             this.exemptNext = false;
/*    */             return;
/*    */           } 
/* 31 */           if (flagAndAlert("state=true") && shouldModifyPackets()) {
/* 32 */             event.setCancelled(true);
/* 33 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } 
/*    */         
/* 37 */         this.lastSprinting = true;
/* 38 */       } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
/* 39 */         if (!this.lastSprinting) {
/* 40 */           if (this.exemptNext) {
/* 41 */             this.exemptNext = false;
/*    */             return;
/*    */           } 
/* 44 */           if (flagAndAlert("state=false") && shouldModifyPackets()) {
/* 45 */             event.setCancelled(true);
/* 46 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } 
/*    */         
/* 50 */         this.lastSprinting = false;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */