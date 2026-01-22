/*    */ package ac.grim.grimac.checks.impl.scaffolding;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ 
/*    */ @CheckData(name = "InvalidPlaceB", description = "Sent impossible block face id")
/*    */ public class InvalidPlaceB extends BlockPlaceCheck {
/*    */   public InvalidPlaceB(GrimPlayer player) {
/* 13 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 18 */     if (place.getFaceId() == 255 && PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8)) {
/*    */       return;
/*    */     }
/*    */     
/* 22 */     if (place.getFaceId() < 0 || place.getFaceId() > 5)
/*    */     {
/* 24 */       if (flagAndAlert("direction=" + place.getFaceId()) && shouldModifyPackets() && shouldCancel())
/* 25 */         place.resync(); 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\scaffolding\InvalidPlaceB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */