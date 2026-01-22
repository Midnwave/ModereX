/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;
/*    */ 
/*    */ @CheckData(name = "CrashH")
/*    */ public class CrashH
/*    */   extends Check implements PacketCheck {
/*    */   public CrashH(GrimPlayer player) {
/* 15 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 20 */     if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
/* 21 */       WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
/* 22 */       String text = wrapper.getText();
/* 23 */       int length = text.length();
/*    */       
/* 25 */       if (length > (!this.player.canUseGameMasterBlocks() ? 256 : 32500)) {
/* 26 */         if (shouldModifyPackets()) {
/* 27 */           event.setCancelled(true);
/* 28 */           this.player.onPacketCancel();
/*    */         } 
/* 30 */         flagAndAlert("(length) length=" + length);
/*    */         
/*    */         return;
/*    */       } 
/*    */       int index;
/* 35 */       if (length > 64 && ((index = text.indexOf(' ')) == -1 || index >= 64)) {
/* 36 */         if (shouldModifyPackets()) {
/* 37 */           event.setCancelled(true);
/* 38 */           this.player.onPacketCancel();
/*    */         } 
/* 40 */         flagAndAlert("(invalid) length=" + length);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashH.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */