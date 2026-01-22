/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ import ac.grim.grimac.utils.data.packetentity.PacketEntity;
/*    */ 
/*    */ @CheckData(name = "PacketOrderC")
/*    */ public class PacketOrderC
/*    */   extends Check implements PacketCheck {
/* 19 */   private final boolean exempt = this.player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10);
/*    */   private boolean sentInteractAt = false;
/*    */   private int requiredEntity;
/*    */   private InteractionHand requiredHand;
/*    */   private boolean requiredSneaking;
/*    */   
/*    */   public PacketOrderC(GrimPlayer player) {
/* 26 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 31 */     if (this.exempt) {
/*    */       return;
/*    */     }
/*    */     
/* 35 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 36 */       WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
/*    */       
/* 38 */       PacketEntity entity = (PacketEntity)this.player.compensatedEntities.entityMap.get(packet.getEntityId());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 44 */       if (entity != null && entity.type == EntityTypes.ARMOR_STAND)
/*    */         return; 
/* 46 */       boolean sneaking = ((Boolean)packet.isSneaking().orElse(Boolean.valueOf(false))).booleanValue();
/*    */       
/* 48 */       switch (packet.getAction()) {
/*    */         
/*    */         case INTERACT:
/* 51 */           if (!this.sentInteractAt) {
/* 52 */             if (flagAndAlert("Skipped Interact-At") && shouldModifyPackets()) {
/* 53 */               event.setCancelled(true);
/* 54 */               this.player.onPacketCancel();
/*    */             } 
/* 56 */           } else if (packet.getEntityId() != this.requiredEntity || packet.getHand() != this.requiredHand || sneaking != this.requiredSneaking) {
/*    */             
/* 58 */             String verbose = "requiredEntity=" + this.requiredEntity + ", entity=" + packet.getEntityId() + ", requiredHand=" + String.valueOf(this.requiredHand) + ", hand=" + String.valueOf(packet.getHand()) + ", requiredSneaking=" + this.requiredSneaking + ", sneaking=" + sneaking;
/*    */             
/* 60 */             if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 61 */               event.setCancelled(true);
/* 62 */               this.player.onPacketCancel();
/*    */             } 
/*    */           } 
/*    */           
/* 66 */           this.sentInteractAt = false;
/*    */           break;
/*    */         case INTERACT_AT:
/* 69 */           if (this.sentInteractAt && 
/* 70 */             flagAndAlert("Skipped Interact") && shouldModifyPackets()) {
/* 71 */             event.setCancelled(true);
/* 72 */             this.player.onPacketCancel();
/*    */           } 
/*    */ 
/*    */           
/* 76 */           this.requiredHand = packet.getHand();
/* 77 */           this.requiredEntity = packet.getEntityId();
/* 78 */           this.requiredSneaking = sneaking;
/* 79 */           this.sentInteractAt = true;
/*    */           break;
/*    */       } 
/*    */     
/*    */     } 
/* 84 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) && 
/* 85 */       this.sentInteractAt) {
/* 86 */       this.sentInteractAt = false;
/* 87 */       flagAndAlert("Skipped Interact (Tick)");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */