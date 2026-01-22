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
/*    */ @CheckData(name = "BadPacketsQ")
/*    */ public class BadPacketsQ
/*    */   extends Check implements PacketCheck {
/*    */   public BadPacketsQ(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
/* 21 */       WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);
/*    */       
/* 23 */       if ((Math.abs(wrapper.getJumpBoost()) > 100 || wrapper
/* 24 */         .getEntityId() != this.player.entityID || (wrapper
/* 25 */         .getAction() != WrapperPlayClientEntityAction.Action.START_JUMPING_WITH_HORSE && wrapper.getJumpBoost() != 0)) && 
/* 26 */         flagAndAlert("boost=" + wrapper.getJumpBoost() + ", action=" + String.valueOf(wrapper.getAction()) + ", entity=" + wrapper.getEntityId()) && shouldModifyPackets()) {
/* 27 */         event.setCancelled(true);
/* 28 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsQ.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */