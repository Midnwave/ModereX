/*    */ package ac.grim.grimac.checks.impl.combat;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ @CheckData(name = "MultiInteractB", experimental = true)
/*    */ public class MultiInteractB
/*    */   extends Check implements PostPredictionCheck {
/* 19 */   private final ArrayList<String> flags = new ArrayList<>();
/*    */   private Vector3f lastPos;
/*    */   private boolean hasInteracted = false;
/*    */   
/*    */   public MultiInteractB(GrimPlayer player) {
/* 24 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 29 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 30 */       Vector3f pos = (new WrapperPlayClientInteractEntity(event)).getTarget().orElse(null);
/*    */       
/* 32 */       if (pos == null) {
/*    */         return;
/*    */       }
/*    */       
/* 36 */       if (this.hasInteracted && !pos.equals(this.lastPos)) {
/* 37 */         String verbose = "pos=" + MessageUtil.toUnlabledString(pos) + ", lastPos=" + MessageUtil.toUnlabledString(this.lastPos);
/* 38 */         if (!this.player.canSkipTicks()) {
/* 39 */           if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 40 */             event.setCancelled(true);
/* 41 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 44 */           this.flags.add(verbose);
/*    */         } 
/*    */       } 
/*    */       
/* 48 */       this.lastPos = pos;
/* 49 */       this.hasInteracted = true;
/*    */     } 
/*    */     
/* 52 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 53 */       this.hasInteracted = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 59 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 61 */     if (this.player.isTickingReliablyFor(3)) {
/* 62 */       for (String verbose : this.flags) {
/* 63 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 67 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\combat\MultiInteractB.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */