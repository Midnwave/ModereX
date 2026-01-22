/*    */ package ac.grim.grimac.checks.impl.multiactions;
/*    */ 
/*    */ import ac.grim.grimac.checks.CheckData;
/*    */ import ac.grim.grimac.checks.type.BlockPlaceCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.DiggingAction;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockBreak;
/*    */ import ac.grim.grimac.utils.anticheat.update.BlockPlace;
/*    */ import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ @CheckData(name = "MultiActionsF", description = "Interacting with a block and an entity in the same tick", experimental = true)
/*    */ public class MultiActionsF
/*    */   extends BlockPlaceCheck {
/* 18 */   private final List<String> flags = new ArrayList<>();
/*    */   boolean entity;
/*    */   
/*    */   public MultiActionsF(GrimPlayer player) {
/* 22 */     super(player);
/*    */   }
/*    */   boolean block;
/*    */   
/*    */   public void onBlockPlace(BlockPlace place) {
/* 27 */     this.block = true;
/* 28 */     if (this.entity) {
/* 29 */       if (!this.player.canSkipTicks()) {
/* 30 */         if (flagAndAlert("place") && shouldModifyPackets() && shouldCancel()) {
/* 31 */           place.resync();
/*    */         }
/*    */       } else {
/* 34 */         this.flags.add("place");
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 41 */     if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
/* 42 */       this.entity = true;
/* 43 */       if (this.block) {
/* 44 */         if (!this.player.canSkipTicks()) {
/* 45 */           if (flagAndAlert("entity") && shouldModifyPackets()) {
/* 46 */             event.setCancelled(true);
/* 47 */             this.player.onPacketCancel();
/*    */           } 
/*    */         } else {
/* 50 */           this.flags.add("entity");
/*    */         } 
/*    */       }
/*    */     } 
/*    */     
/* 55 */     if (isTickPacket(event.getPacketType())) {
/* 56 */       this.block = this.entity = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockBreak(BlockBreak blockBreak) {
/* 62 */     if (blockBreak.action == DiggingAction.START_DIGGING || blockBreak.action == DiggingAction.FINISHED_DIGGING) {
/* 63 */       this.block = true;
/* 64 */       if (this.entity) {
/* 65 */         if (!this.player.canSkipTicks()) {
/* 66 */           if (flagAndAlert("dig") && shouldModifyPackets()) {
/* 67 */             blockBreak.cancel();
/*    */           }
/*    */         } else {
/* 70 */           this.flags.add("dig");
/*    */         } 
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPredictionComplete(PredictionComplete predictionComplete) {
/* 78 */     if (!this.player.canSkipTicks())
/*    */       return; 
/* 80 */     if (this.player.isTickingReliablyFor(3)) {
/* 81 */       for (String verbose : this.flags) {
/* 82 */         flagAndAlert(verbose);
/*    */       }
/*    */     }
/*    */     
/* 86 */     this.flags.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\multiactions\MultiActionsF.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */