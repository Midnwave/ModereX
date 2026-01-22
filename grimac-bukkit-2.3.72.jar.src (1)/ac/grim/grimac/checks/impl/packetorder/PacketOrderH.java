/*    */ package ac.grim.grimac.checks.impl.packetorder;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "PacketOrderH", experimental = true)
/*    */ public class PacketOrderH extends Check implements PostPredictionCheck {
/*    */   public PacketOrderH(GrimPlayer player) {
/* 16 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   private int invalid;
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 23 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
/* 24 */       switch ((new WrapperPlayClientEntityAction(event)).getAction()) { case START_SPRINTING:
/*    */         case STOP_SPRINTING:
/* 26 */           if (this.player.getClientVersion().isOlderThan(ClientVersion.V_1_21_2) && this.player.packetOrderProcessor.isSneaking()) {
/* 27 */             if (!this.player.canSkipTicks()) {
/* 28 */               flagAndAlert(); break;
/*    */             } 
/* 30 */             this.invalid++;
/*    */           } 
/*    */           break;
/*    */         case START_SNEAKING:
/*    */         case STOP_SNEAKING:
/* 35 */           if (this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2) && this.player.packetOrderProcessor.isSprinting()) {
/* 36 */             if (!this.player.canSkipTicks()) {
/* 37 */               flagAndAlert(); break;
/*    */             } 
/* 39 */             this.invalid++;
/*    */           } 
/*    */           break; }
/*    */     
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 49 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 51 */     if (this.player.isTickingReliablyFor(3)) {
/* 52 */       for (; this.invalid >= 1; this.invalid--) {
/* 53 */         flagAndAlert();
/*    */       }
/*    */     }
/*    */     
/* 57 */     this.invalid = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\packetorder\PacketOrderH.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */