/*    */ package ac.grim.grimac.checks.impl.badpackets;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ 
/*    */ @CheckData(name = "BadPacketsX", experimental = true)
/*    */ public class BadPacketsX extends Check implements PostPredictionCheck {
/*    */   private boolean sprint;
/*    */   private boolean sneak;
/*    */   private int flags;
/*    */   
/*    */   public BadPacketsX(GrimPlayer player) {
/* 20 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 25 */     if (!this.player.canSkipTicks()) {
/* 26 */       if (this.flags > 0) {
/* 27 */         setbackIfAboveSetbackVL();
/*    */       }
/*    */       
/* 30 */       this.flags = 0;
/*    */       
/*    */       return;
/*    */     } 
/* 34 */     if (this.player.isTickingReliablyFor(3)) {
/* 35 */       for (; this.flags > 0; this.flags--) {
/* 36 */         flagAndAlertWithSetback();
/*    */       }
/*    */     }
/*    */     
/* 40 */     this.flags = 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 45 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 46 */       this.sprint = this.sneak = false;
/*    */       
/*    */       return;
/*    */     } 
/* 50 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION)
/* 51 */       switch ((new WrapperPlayClientEntityAction(event)).getAction()) { case START_SNEAKING:
/*    */         case STOP_SNEAKING:
/* 53 */           if (this.sneak && (
/* 54 */             this.player.canSkipTicks() || flagAndAlert())) {
/* 55 */             this.flags++;
/*    */           }
/*    */ 
/*    */           
/* 59 */           this.sneak = true; break;
/*    */         case START_SPRINTING:
/*    */         case STOP_SPRINTING:
/* 62 */           if (this.player.inVehicle()) {
/*    */             return;
/*    */           }
/*    */           
/* 66 */           if (this.sprint && (
/* 67 */             this.player.canSkipTicks() || flagAndAlert())) {
/* 68 */             this.flags++;
/*    */           }
/*    */ 
/*    */           
/* 72 */           this.sprint = true;
/*    */           break; }
/*    */        
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\badpackets\BadPacketsX.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */