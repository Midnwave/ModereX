/*    */ package ac.grim.grimac.events.packets;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.checks.impl.elytra.ElytraA;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
/*    */ 
/*    */ public class PacketEntityAction
/*    */   extends PacketListenerAbstract {
/*    */   public PacketEntityAction() {
/* 16 */     super(PacketListenerPriority.LOW);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isPreVia() {
/* 21 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 26 */     if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
/* 27 */       int jumpBoost; WrapperPlayClientEntityAction action = new WrapperPlayClientEntityAction(event);
/* 28 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*    */       
/* 30 */       if (player == null)
/*    */         return; 
/* 32 */       switch (action.getAction()) {
/*    */         case START_SPRINTING:
/* 34 */           player.isSprinting = true;
/*    */           break;
/*    */         case STOP_SPRINTING:
/* 37 */           player.isSprinting = false;
/*    */           break;
/*    */         case START_SNEAKING:
/* 40 */           player.isSneaking = true;
/*    */           break;
/*    */         case STOP_SNEAKING:
/* 43 */           player.isSneaking = false;
/*    */           break;
/*    */         case START_FLYING_WITH_ELYTRA:
/* 46 */           if (player.onGround || player.lastOnGround) {
/* 47 */             player.getSetbackTeleportUtil().executeForceResync();
/*    */             
/* 49 */             if (player.platformPlayer != null)
/*    */             {
/* 51 */               player.platformPlayer.setSneaking(!player.platformPlayer.isSneaking());
/*    */             }
/*    */             
/* 54 */             event.setCancelled(true);
/* 55 */             player.onPacketCancel();
/*    */             
/*    */             break;
/*    */           } 
/* 59 */           if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15))
/* 60 */             return;  ((ElytraA)player.checkManager.getPostPredictionCheck(ElytraA.class)).onStartGliding(event);
/*    */ 
/*    */ 
/*    */           
/* 64 */           if (player.canGlide()) {
/* 65 */             player.isGliding = true;
/* 66 */             player.pointThreeEstimator.updatePlayerGliding();
/*    */             break;
/*    */           } 
/* 69 */           player.getSetbackTeleportUtil().executeForceResync();
/* 70 */           if (player.platformPlayer != null)
/*    */           {
/* 72 */             player.platformPlayer.setSneaking(!player.platformPlayer.isSneaking());
/*    */           }
/* 74 */           event.setCancelled(true);
/* 75 */           player.onPacketCancel();
/*    */           break;
/*    */         
/*    */         case START_JUMPING_WITH_HORSE:
/* 79 */           jumpBoost = action.getJumpBoost();
/* 80 */           if (jumpBoost < 0) jumpBoost = 0; 
/* 81 */           if (jumpBoost >= 90) {
/* 82 */             player.vehicleData.nextHorseJump = 1.0F; break;
/*    */           } 
/* 84 */           player.vehicleData.nextHorseJump = 0.4F + 0.4F * jumpBoost / 90.0F;
/*    */           break;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketEntityAction.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */