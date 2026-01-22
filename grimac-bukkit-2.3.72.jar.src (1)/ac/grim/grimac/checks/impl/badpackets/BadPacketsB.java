/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.utils.data.RotationData;
/*    */ 
/*    */ @CheckData(name = "BadPacketsB", description = "Ignored set rotation packet")
/*    */ public class BadPacketsB extends Check implements PacketCheck {
/*    */   public BadPacketsB(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 18 */     if (isTransaction(event.getPacketType()))
/* 19 */       this.player.pendingRotations.removeIf(data -> {
/*    */             if (this.player.getLastTransactionReceived() > data.getTransaction()) {
/*    */               if (!data.isAccepted())
/*    */                 flagAndAlert(); 
/*    */               return true;
/*    */             } 
/*    */             return false;
/*    */           }); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */