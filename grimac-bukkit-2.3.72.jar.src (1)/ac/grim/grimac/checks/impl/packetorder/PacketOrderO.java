/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "PacketOrderO", experimental = true)
/*    */ public class PacketOrderO
/*    */   extends Check implements PacketCheck {
/*    */   public PacketOrderO(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean flying;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) {
/* 24 */       this.flying = false;
/*    */     }
/*    */     
/* 27 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType()) && this.player.supportsEndTick() && !this.player.packetStateData.lastPacketWasTeleport) {
/* 28 */       this.flying = true;
/*    */       
/*    */       return;
/*    */     } 
/* 32 */     if (this.flying && event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE && event
/* 33 */       .getPacketType() != PacketType.Play.Client.VEHICLE_MOVE) {
/*    */       
/* 35 */       if (this.player.inVehicle() && event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
/* 36 */         WrapperPlayClientEntityAction.Action action = (new WrapperPlayClientEntityAction(event)).getAction();
/* 37 */         if (action == WrapperPlayClientEntityAction.Action.START_SPRINTING || action == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
/*    */           return;
/*    */         }
/*    */       } 
/*    */       
/* 42 */       flagAndAlert("type=" + String.valueOf(event.getPacketType()));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderO.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */