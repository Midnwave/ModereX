/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ 
/*    */ @CheckData(name = "PacketOrderB", description = "Did not swing for attack")
/*    */ public class PacketOrderB
/*    */   extends Check
/*    */   implements PacketCheck
/*    */ {
/* 19 */   private final boolean is1_9 = this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 30 */   private final boolean exempt = (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_9) && 
/* 31 */     PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9));
/*    */   
/* 33 */   private boolean sentAnimationSinceLastAttack = this.player.getClientVersion().isNewerThan(ClientVersion.V_1_8);
/*    */   private boolean sentAttack;
/*    */   
/*    */   public PacketOrderB(GrimPlayer player) {
/* 37 */     super(player);
/*    */   }
/*    */   private boolean sentAnimation; private boolean sentSlotSwitch;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 42 */     if (this.exempt)
/*    */       return; 
/* 44 */     if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
/* 45 */       this.sentAnimationSinceLastAttack = this.sentAnimation = true;
/* 46 */       this.sentAttack = this.sentSlotSwitch = false;
/*    */       
/*    */       return;
/*    */     } 
/* 50 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 51 */       WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
/* 52 */       if (packet.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
/* 53 */         this.sentAttack = true;
/*    */         
/* 55 */         if (this.is1_9 ? !this.sentAnimationSinceLastAttack : !this.sentAnimation) {
/* 56 */           this.sentAttack = false;
/* 57 */           if (flagAndAlert("pre-attack") && shouldModifyPackets()) {
/* 58 */             event.setCancelled(true);
/* 59 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } 
/*    */         
/* 63 */         this.sentAnimationSinceLastAttack = this.sentAnimation = this.sentSlotSwitch = false;
/*    */         
/*    */         return;
/*    */       } 
/*    */     } 
/* 68 */     if (event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE && !this.is1_9 && !this.sentSlotSwitch) {
/* 69 */       this.sentSlotSwitch = true;
/*    */       
/*    */       return;
/*    */     } 
/* 73 */     if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE) {
/* 74 */       if (this.sentAttack && this.is1_9) {
/* 75 */         flagAndAlert("post-attack");
/*    */       }
/*    */       
/* 78 */       this.sentAttack = this.sentAnimation = this.sentSlotSwitch = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */