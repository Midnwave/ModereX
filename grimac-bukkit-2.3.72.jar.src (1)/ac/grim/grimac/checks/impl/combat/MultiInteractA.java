/*    */ package ac.grim.grimac.checks.impl.combat;
/*    */ 
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.PostPredictionCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ @CheckData(name = "MultiInteractA", description = "Interacted with multiple entities in the same tick", experimental = true)
/*    */ public class MultiInteractA
/*    */   extends Check implements PostPredictionCheck {
/* 17 */   private final ArrayList<String> flags = new ArrayList<>();
/*    */   private int lastEntity;
/*    */   private boolean lastSneaking;
/*    */   private boolean hasInteracted = false;
/*    */   
/*    */   public MultiInteractA(GrimPlayer player) {
/* 23 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 28 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 29 */       WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
/* 30 */       int entity = packet.getEntityId();
/* 31 */       boolean sneaking = ((Boolean)packet.isSneaking().orElse(Boolean.valueOf(false))).booleanValue();
/*    */       
/* 33 */       if (this.hasInteracted && entity != this.lastEntity) {
/* 34 */         String verbose = "lastEntity=" + this.lastEntity + ", entity=" + entity + ", lastSneaking=" + this.lastSneaking + ", sneaking=" + sneaking;
/*    */         
/* 36 */         if (!this.player.canSkipTicks()) {
/* 37 */           if (flagAndAlert(verbose) && shouldModifyPackets()) {
/* 38 */             event.setCancelled(true);
/* 39 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 42 */           this.flags.add(verbose);
/*    */         } 
/*    */       } 
/*    */       
/* 46 */       this.lastEntity = entity;
/* 47 */       this.lastSneaking = sneaking;
/* 48 */       this.hasInteracted = true;
/*    */     } 
/*    */     
/* 51 */     if (this.player.gamemode == GameMode.SPECTATOR || isTickPacket(event.getPacketType())) {
/* 52 */       this.hasInteracted = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 58 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 60 */     if (this.player.isTickingReliablyFor(3)) {
/* 61 */       for (String verbose : this.flags) {
/* 62 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 66 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\combat\MultiInteractA.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */