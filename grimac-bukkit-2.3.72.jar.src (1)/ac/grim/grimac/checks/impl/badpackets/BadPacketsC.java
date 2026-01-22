/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ 
/*    */ @CheckData(name = "BadPacketsC", description = "Interacted with self")
/*    */ public class BadPacketsC extends Check implements PacketCheck {
/*    */   public BadPacketsC(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 21 */       if (this.player.gamemode == GameMode.SPECTATOR)
/* 22 */         return;  if ((new WrapperPlayClientInteractEntity(event)).getEntityId() == this.player.entityID)
/*    */       {
/* 24 */         if (flagAndAlert() && shouldModifyPackets()) {
/* 25 */           event.setCancelled(true);
/* 26 */           this.player.onPacketCancel();
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */