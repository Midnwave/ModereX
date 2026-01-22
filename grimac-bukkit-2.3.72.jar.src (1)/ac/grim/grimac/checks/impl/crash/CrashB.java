/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ 
/*    */ @CheckData(name = "CrashB", description = "Sent creative mode inventory click packets while not in creative mode")
/*    */ public class CrashB extends Check implements PacketCheck {
/*    */   public CrashB(GrimPlayer player) {
/* 14 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION && 
/* 20 */       this.player.gamemode != GameMode.CREATIVE) {
/* 21 */       event.setCancelled(true);
/* 22 */       this.player.onPacketCancel();
/* 23 */       flagAndAlert();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */