/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import java.util.StringJoiner;
/*    */ 
/*    */ @CheckData(name = "MultiActionsC", description = "Clicked in inventory while moving", experimental = true)
/*    */ public class MultiActionsC
/*    */   extends Check implements PacketCheck {
/*    */   public MultiActionsC(GrimPlayer player) {
/* 18 */     super(player);
/*    */   }
/*    */   
/*    */   @Contract(pure = true)
/*    */   public static String getVerbose(@NotNull GrimPlayer player) {
/* 23 */     StringJoiner verbose = new StringJoiner(", ");
/* 24 */     if (player.isSprinting && (!player.isSwimming || !player.clientClaimsLastOnGround)) {
/* 25 */       verbose.add("sprinting");
/*    */     }
/*    */     
/* 28 */     if (player.isSneaking && player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) {
/* 29 */       verbose.add("sneaking");
/*    */     }
/*    */     
/* 32 */     if (player.supportsEndTick() && player.packetStateData.knownInput.moving()) {
/* 33 */       verbose.add("input");
/*    */     }
/*    */     
/* 36 */     return verbose.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 41 */     if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW && !this.player.serverOpenedInventoryThisTick) {
/* 42 */       String verbose = getVerbose(this.player);
/* 43 */       if (!verbose.isEmpty() && flagAndAlert(verbose) && shouldModifyPackets()) {
/* 44 */         event.setCancelled(true);
/* 45 */         this.player.onPacketCancel();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsC.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */