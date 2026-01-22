/*    */ package ac.grim.grimac.checks.impl.breaking;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockBreakCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ 
/*    */ @CheckData(name = "NoSwingBreak", description = "Did not swing while breaking block", experimental = true)
/*    */ public class NoSwingBreak extends Check implements BlockBreakCheck {
/*    */   private boolean sentAnimation;
/*    */   private boolean sentBreak;
/*    */   
/*    */   public NoSwingBreak(GrimPlayer playerData) {
/* 18 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 23 */     if (blockBreak.action != DiggingAction.CANCELLED_DIGGING) {
/* 24 */       this.sentBreak = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 30 */     if (event.getPacketType() == PacketType.Play.Client.ANIMATION) {
/* 31 */       this.sentAnimation = true;
/*    */     }
/*    */     
/* 34 */     if (isTickPacket(event.getPacketType())) {
/* 35 */       if (this.sentBreak && !this.sentAnimation) {
/* 36 */         flagAndAlert();
/*    */       }
/*    */       
/* 39 */       this.sentAnimation = this.sentBreak = false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\breaking\NoSwingBreak.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */