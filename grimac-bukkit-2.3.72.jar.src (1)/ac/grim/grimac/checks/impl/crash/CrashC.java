/*    */ package ac.grim.grimac.checks.impl.crash;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Location;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
/*    */ 
/*    */ @CheckData(name = "CrashC", description = "Sent non-finite position or rotation")
/*    */ public class CrashC extends Check implements PacketCheck {
/*    */   public CrashC(GrimPlayer playerData) {
/* 14 */     super(playerData);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 19 */     if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
/* 20 */       WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
/* 21 */       if (flying.hasPositionChanged()) {
/* 22 */         Location pos = flying.getLocation();
/* 23 */         if (Double.isNaN(pos.getX()) || Double.isNaN(pos.getY()) || Double.isNaN(pos.getZ()) || 
/* 24 */           Double.isInfinite(pos.getX()) || Double.isInfinite(pos.getY()) || Double.isInfinite(pos.getZ()) || 
/* 25 */           Float.isNaN(pos.getYaw()) || Float.isNaN(pos.getPitch()) || 
/* 26 */           Float.isInfinite(pos.getYaw()) || Float.isInfinite(pos.getPitch())) {
/* 27 */           flagAndAlert("xyzYP: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + pos.getYaw() + ", " + pos.getPitch());
/* 28 */           this.player.getSetbackTeleportUtil().executeViolationSetback();
/* 29 */           event.setCancelled(true);
/* 30 */           this.player.onPacketCancel();
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\crash\CrashC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */