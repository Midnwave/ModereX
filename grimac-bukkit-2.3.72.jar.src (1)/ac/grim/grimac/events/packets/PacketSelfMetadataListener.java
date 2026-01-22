/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.InteractionHand;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUseBed;
/*     */ import ac.grim.grimac.utils.nmsutil.WatchableIndexUtil;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ 
/*     */ public class PacketSelfMetadataListener
/*     */   extends PacketListenerAbstract {
/*     */   public PacketSelfMetadataListener() {
/*  27 */     super(PacketListenerPriority.HIGH);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  32 */     if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
/*  33 */       WrapperPlayServerEntityMetadata entityMetadata = new WrapperPlayServerEntityMetadata(event);
/*     */       
/*  35 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  36 */       if (player == null) {
/*     */         return;
/*     */       }
/*  39 */       if (entityMetadata.getEntityId() == player.entityID) {
/*     */         
/*  41 */         boolean hasSendTransaction = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  64 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14)) {
/*  65 */           List<EntityData<?>> metadataStuff = entityMetadata.getEntityMetadata();
/*     */ 
/*     */           
/*  68 */           metadataStuff.removeIf(element -> (element.getIndex() == 6));
/*  69 */           entityMetadata.setEntityMetadata(metadataStuff);
/*  70 */           event.markForReEncode(true);
/*     */         } 
/*     */         
/*  73 */         EntityData<?> watchable = WatchableIndexUtil.getIndex(entityMetadata.getEntityMetadata(), 0);
/*     */         
/*  75 */         if (watchable != null) {
/*  76 */           Object zeroBitField = watchable.getValue();
/*     */           
/*  78 */           if (zeroBitField instanceof Byte) {
/*  79 */             byte field = ((Byte)zeroBitField).byteValue();
/*  80 */             boolean isGliding = ((field & 0x80) == 128 && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9));
/*  81 */             boolean isSwimming = ((field & 0x10) == 16);
/*  82 */             boolean isSprinting = ((field & 0x8) == 8);
/*     */             
/*  84 */             if (!hasSendTransaction) player.sendTransaction(); 
/*  85 */             hasSendTransaction = true;
/*     */             
/*  87 */             player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*     */                   player.isSwimming = isSwimming;
/*     */                   
/*     */                   player.lastSprinting = isSprinting;
/*     */                   
/*     */                   if (player.isGliding != isGliding) {
/*     */                     player.pointThreeEstimator.updatePlayerGliding();
/*     */                   }
/*     */                   player.isGliding = isGliding;
/*     */                 });
/*     */           } 
/*     */         } 
/*  99 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/* 100 */           EntityData<?> gravity = WatchableIndexUtil.getIndex(entityMetadata.getEntityMetadata(), 5);
/*     */           
/* 102 */           if (gravity != null) {
/* 103 */             Object gravityObject = gravity.getValue();
/*     */             
/* 105 */             if (gravityObject instanceof Boolean) {
/* 106 */               if (!hasSendTransaction) player.sendTransaction(); 
/* 107 */               hasSendTransaction = true;
/*     */               
/* 109 */               player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.playerEntityHasGravity = !((Boolean)gravityObject).booleanValue());
/*     */             } 
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 118 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
/* 119 */           EntityData<?> frozen = WatchableIndexUtil.getIndex(entityMetadata.getEntityMetadata(), 7);
/*     */           
/* 121 */           if (frozen != null) {
/* 122 */             if (!hasSendTransaction) player.sendTransaction(); 
/* 123 */             hasSendTransaction = true;
/* 124 */             player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.powderSnowFrozenTicks = ((Integer)frozen.getValue()).intValue());
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 129 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14)) {
/*     */           int id;
/*     */           
/* 132 */           if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
/* 133 */             id = 12;
/* 134 */           } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
/* 135 */             id = 13;
/*     */           } else {
/* 137 */             id = 14;
/*     */           } 
/*     */           
/* 140 */           EntityData<?> bedObject = WatchableIndexUtil.getIndex(entityMetadata.getEntityMetadata(), id);
/* 141 */           if (bedObject != null) {
/* 142 */             if (!hasSendTransaction) player.sendTransaction(); 
/* 143 */             hasSendTransaction = true;
/*     */             
/* 145 */             player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*     */                   Optional<Vector3i> bed = (Optional<Vector3i>)bedObject.getValue();
/*     */                   
/*     */                   if (bed.isPresent()) {
/*     */                     player.isInBed = true;
/*     */                     Vector3i bedPos = bed.get();
/*     */                     player.bedPosition = new Vector3d(bedPos.getX() + 0.5D, bedPos.getY(), bedPos.getZ() + 0.5D);
/*     */                   } else {
/*     */                     player.isInBed = false;
/*     */                   } 
/*     */                 });
/*     */           } 
/*     */         } 
/* 158 */         if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) && player
/* 159 */           .getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
/* 160 */           EntityData<?> riptide = WatchableIndexUtil.getIndex(entityMetadata.getEntityMetadata(), PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17) ? 8 : 7);
/*     */ 
/*     */           
/* 163 */           if (riptide != null && riptide.getValue() instanceof Byte) {
/* 164 */             boolean isRiptiding = ((((Byte)riptide.getValue()).byteValue() & 0x4) == 4);
/*     */             
/* 166 */             if (!hasSendTransaction) player.sendTransaction(); 
/* 167 */             hasSendTransaction = true;
/*     */             
/* 169 */             player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.isRiptidePose = isRiptiding);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 186 */             if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/* 187 */               boolean isActive = ((((Byte)riptide.getValue()).byteValue() & 0x1) > 0);
/* 188 */               boolean isOffhand = ((((Byte)riptide.getValue()).byteValue() & 0x2) > 0);
/*     */ 
/*     */               
/* 191 */               player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.packetStateData.setSlowedByUsingItem(false));
/*     */ 
/*     */               
/* 194 */               int markedTransaction = player.lastTransactionSent.get();
/*     */ 
/*     */               
/* 197 */               player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> {
/*     */                     if (player.packetStateData.slowedByUsingItemTransaction < markedTransaction) {
/*     */                       PacketPlayerDigging.handleUseItem(player, isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
/*     */ 
/*     */ 
/*     */                       
/*     */                       player.packetStateData.setSlowedByUsingItem(isActive);
/*     */ 
/*     */                       
/*     */                       if (isActive) {
/*     */                         player.packetStateData.itemInUseHand = isOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
/*     */                       }
/*     */                     } 
/*     */                   });
/*     */ 
/*     */               
/* 213 */               Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 220 */     if (event.getPacketType() == PacketType.Play.Server.USE_BED) {
/* 221 */       WrapperPlayServerUseBed bed = new WrapperPlayServerUseBed(event);
/*     */       
/* 223 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 224 */       if (player != null && player.entityID == bed.getEntityId())
/*     */       {
/* 226 */         player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*     */               player.isInBed = true;
/*     */               
/*     */               player.bedPosition = new Vector3d(bed.getPosition().getX() + 0.5D, bed.getPosition().getY(), bed.getPosition().getZ() + 0.5D);
/*     */             });
/*     */       }
/*     */     } 
/* 233 */     if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
/* 234 */       WrapperPlayServerEntityAnimation animation = new WrapperPlayServerEntityAnimation(event);
/*     */       
/* 236 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/* 237 */       if (player != null && player.entityID == animation.getEntityId() && animation
/* 238 */         .getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.WAKE_UP) {
/*     */         
/* 240 */         player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.isInBed = false);
/* 241 */         Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketSelfMetadataListener.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */