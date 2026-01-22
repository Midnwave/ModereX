/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ 
/*    */ @CheckData(name = "PacketOrderD", experimental = true)
/*    */ public class PacketOrderD
/*    */   extends Check implements PacketCheck {
/*    */   public PacketOrderD(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean sentMainhand;
/*    */   private int requiredEntity;
/*    */   private boolean requiredSneaking;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 27 */       WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
/* 28 */       WrapperPlayClientInteractEntity.InteractAction action = packet.getAction();
/* 29 */       if (action != WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/* 30 */         boolean sneaking = ((Boolean)packet.isSneaking().orElse(Boolean.valueOf(false))).booleanValue();
/* 31 */         int entity = packet.getEntityId();
/*    */         
/* 33 */         if (packet.getHand() == InteractionHand.OFF_HAND) {
/* 34 */           if (action == WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
/* 35 */             if (!this.sentMainhand && 
/* 36 */               flagAndAlert("Skipped Mainhand") && shouldModifyPackets()) {
/* 37 */               event.setCancelled(true);
/* 38 */               this.player.onPacketCancel();
/*    */             } 
/*    */             
/* 41 */             this.sentMainhand = false;
/* 42 */           } else if (sneaking != this.requiredSneaking || entity != this.requiredEntity) {
/* 43 */             String verbose = "requiredEntity=" + this.requiredEntity + ", entity=" + entity + ", requiredSneaking=" + this.requiredSneaking + ", sneaking=" + sneaking;
/*    */             
/* 45 */             if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 46 */               event.setCancelled(true);
/* 47 */               this.player.onPacketCancel();
/*    */             } 
/*    */           } 
/*    */         } else {
/* 51 */           this.requiredEntity = entity;
/* 52 */           this.requiredSneaking = sneaking;
/* 53 */           this.sentMainhand = true;
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 58 */     if (isTickPacket(event.getPacketType()))
/* 59 */       this.sentMainhand = false; 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderD.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */