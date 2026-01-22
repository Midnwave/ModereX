/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
/*    */ 
/*    */ @CheckData(name = "CrashE")
/*    */ public class CrashE
/*    */   extends Check implements PacketCheck {
/*    */   public CrashE(GrimPlayer playerData) {
/* 15 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
/* 21 */       WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
/* 22 */       int viewDistance = wrapper.getViewDistance();
/* 23 */       if (viewDistance < 2) {
/* 24 */         flagAndAlert("distance=" + viewDistance);
/* 25 */         wrapper.setViewDistance(2);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */