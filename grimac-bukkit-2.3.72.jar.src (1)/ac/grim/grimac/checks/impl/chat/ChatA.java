/*    */ package ac.grim.grimac.checks.impl.chat;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
/*    */ 
/*    */ @CheckData(name = "ChatA", experimental = true)
/*    */ public class ChatA
/*    */   extends Check implements PacketCheck {
/*    */   public ChatA(GrimPlayer player) {
/* 17 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 22 */     if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 23 */       WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
/* 24 */       String text = wrapper.getText();
/* 25 */       if ((text.equals("/") || text.trim().isEmpty()) && 
/* 26 */         flagAndAlert("")) {
/* 27 */         event.setCancelled(true);
/* 28 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\chat\ChatA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */