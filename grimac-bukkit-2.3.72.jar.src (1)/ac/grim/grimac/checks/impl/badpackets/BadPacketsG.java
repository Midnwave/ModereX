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
/*    */ @CheckData(name = "BadPacketsG", description = "Sent duplicate sneaking status")
/*    */ public class BadPacketsG extends Check implements PacketCheck {
/*    */   private boolean lastSneaking;
/*    */   
/*    */   public BadPacketsG(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */   private boolean respawn;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 21 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
/* 22 */       WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event);
/*    */       
/* 24 */       if (packet.getAction() == WrapperPlayClientEntityAction.Action.START_SNEAKING) {
/*    */         
/* 26 */         if (this.lastSneaking && !this.respawn) {
/* 27 */           if (flagAndAlert("state=true") && shouldModifyPackets()) {
/* 28 */             event.setCancelled(true);
/* 29 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 32 */           this.lastSneaking = true;
/*    */         } 
/* 34 */         this.respawn = false;
/* 35 */       } else if (packet.getAction() == WrapperPlayClientEntityAction.Action.STOP_SNEAKING) {
/* 36 */         if (!this.lastSneaking && !this.respawn) {
/* 37 */           if (flagAndAlert("state=false") && shouldModifyPackets()) {
/* 38 */             event.setCancelled(true);
/* 39 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 42 */           this.lastSneaking = false;
/*    */         } 
/* 44 */         this.respawn = false;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRespawn() {
/* 51 */     this.respawn = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsG.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */