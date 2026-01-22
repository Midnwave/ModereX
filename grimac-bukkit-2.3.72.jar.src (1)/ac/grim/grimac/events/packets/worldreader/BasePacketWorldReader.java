/*     */ package ac.grim.grimac.events.packets.worldreader;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAcknowledgeBlockChanges;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAcknowledgePlayerDigging;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChangeGameState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkDataBulk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUnloadChunk;
/*     */ import ac.grim.grimac.utils.chunks.Column;
/*     */ import ac.grim.grimac.utils.data.TeleportData;
/*     */ import java.util.Objects;
/*     */ 
/*     */ public class BasePacketWorldReader extends PacketListenerAbstract {
/*     */   public BasePacketWorldReader() {
/*  25 */     super(PacketListenerPriority.HIGH);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  30 */     if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
/*  31 */       WrapperPlayServerUnloadChunk unloadChunk = new WrapperPlayServerUnloadChunk(event);
/*  32 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  33 */       if (player == null)
/*     */         return; 
/*  35 */       unloadChunk(player, unloadChunk.getChunkX(), unloadChunk.getChunkZ());
/*     */     } 
/*     */ 
/*     */     
/*  39 */     if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
/*  40 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  41 */       if (player == null)
/*     */         return; 
/*  43 */       handleMapChunkBulk(player, event);
/*     */     } 
/*     */     
/*  46 */     if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
/*  47 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  48 */       if (player == null)
/*     */         return; 
/*  50 */       handleMapChunk(player, event);
/*     */     } 
/*     */     
/*  53 */     if (event.getPacketType() == PacketType.Play.Server.BLOCK_CHANGE) {
/*  54 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  55 */       if (player == null)
/*     */         return; 
/*  57 */       handleBlockChange(player, event);
/*     */     } 
/*     */     
/*  60 */     if (event.getPacketType() == PacketType.Play.Server.MULTI_BLOCK_CHANGE) {
/*  61 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  62 */       if (player == null)
/*     */         return; 
/*  64 */       handleMultiBlockChange(player, event);
/*     */     } 
/*     */     
/*  67 */     if (event.getPacketType() == PacketType.Play.Server.ACKNOWLEDGE_BLOCK_CHANGES) {
/*  68 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  69 */       if (player == null)
/*     */         return; 
/*  71 */       WrapperPlayServerAcknowledgeBlockChanges changes = new WrapperPlayServerAcknowledgeBlockChanges(event);
/*  72 */       player.compensatedWorld.handlePredictionConfirmation(changes.getSequence());
/*     */     } 
/*     */     
/*  75 */     if (event.getPacketType() == PacketType.Play.Server.ACKNOWLEDGE_PLAYER_DIGGING) {
/*  76 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  77 */       if (player == null)
/*     */         return; 
/*  79 */       WrapperPlayServerAcknowledgePlayerDigging ack = new WrapperPlayServerAcknowledgePlayerDigging(event);
/*  80 */       player.compensatedWorld.handleBlockBreakAck(ack.getBlockPosition(), ack.getBlockId(), ack.getAction(), ack.isSuccessful());
/*     */     } 
/*     */     
/*  83 */     if (event.getPacketType() == PacketType.Play.Server.CHANGE_GAME_STATE) {
/*  84 */       GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  85 */       if (player == null)
/*     */         return; 
/*  87 */       WrapperPlayServerChangeGameState newState = new WrapperPlayServerChangeGameState(event);
/*     */       
/*  89 */       player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*     */             if (newState.getReason() == WrapperPlayServerChangeGameState.Reason.BEGIN_RAINING) {
/*     */               player.compensatedWorld.isRaining = true;
/*     */             } else if (newState.getReason() == WrapperPlayServerChangeGameState.Reason.END_RAINING) {
/*     */               player.compensatedWorld.isRaining = false;
/*     */             } else if (newState.getReason() == WrapperPlayServerChangeGameState.Reason.RAIN_LEVEL_CHANGE) {
/*     */               player.compensatedWorld.isRaining = (newState.getValue() > 0.2F);
/*     */             } 
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleMapChunkBulk(GrimPlayer player, PacketSendEvent event) {
/* 103 */     WrapperPlayServerChunkDataBulk chunkData = new WrapperPlayServerChunkDataBulk(event);
/* 104 */     for (int i = 0; i < (chunkData.getChunks()).length; i++) {
/* 105 */       addChunkToCache(event, player, chunkData.getChunks()[i], true, chunkData.getX()[i], chunkData.getZ()[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleMapChunk(GrimPlayer player, PacketSendEvent event) {
/* 110 */     WrapperPlayServerChunkData chunkData = new WrapperPlayServerChunkData(event);
/* 111 */     addChunkToCache(event, player, chunkData.getColumn().getChunks(), chunkData.getColumn().isFullChunk(), chunkData.getColumn().getX(), chunkData.getColumn().getZ());
/* 112 */     event.setLastUsedWrapper(null);
/*     */   }
/*     */   
/*     */   public void addChunkToCache(PacketSendEvent event, GrimPlayer player, BaseChunk[] chunks, boolean isGroundUp, int chunkX, int chunkZ) {
/* 116 */     double chunkCenterX = ((chunkX << 4) + 8);
/* 117 */     double chunkCenterZ = ((chunkZ << 4) + 8);
/* 118 */     boolean shouldPostTrans = (Math.abs(player.x - chunkCenterX) < 16.0D && Math.abs(player.z - chunkCenterZ) < 16.0D);
/*     */     
/* 120 */     for (TeleportData teleports : (player.getSetbackTeleportUtil()).pendingTeleports) {
/* 121 */       if (teleports.getFlags().getMask() != 0) {
/*     */         continue;
/*     */       }
/* 124 */       shouldPostTrans = (shouldPostTrans || (Math.abs(teleports.getLocation().getX() - chunkCenterX) < 16.0D && Math.abs(teleports.getLocation().getZ() - chunkCenterZ) < 16.0D));
/*     */     } 
/*     */     
/* 127 */     if (shouldPostTrans) {
/* 128 */       Objects.requireNonNull(player); event.getTasksAfterSend().add(player::sendTransaction);
/*     */     } 
/* 130 */     if (isGroundUp) {
/* 131 */       Column column = new Column(chunkX, chunkZ, chunks, player.lastTransactionSent.get());
/* 132 */       player.compensatedWorld.addToCache(column, chunkX, chunkZ);
/*     */     } else {
/* 134 */       player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*     */             Column existingColumn = player.compensatedWorld.getChunk(chunkX, chunkZ);
/*     */             if (existingColumn == null) {
/*     */               return;
/*     */             }
/*     */             existingColumn.mergeChunks(chunks);
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unloadChunk(GrimPlayer player, int x, int z) {
/* 150 */     if (player == null)
/* 151 */       return;  player.compensatedWorld.removeChunkLater(x, z);
/*     */   }
/*     */   
/*     */   public void handleBlockChange(GrimPlayer player, PacketSendEvent event) {
/* 155 */     WrapperPlayServerBlockChange blockChange = new WrapperPlayServerBlockChange(event);
/* 156 */     int range = 16;
/*     */     
/* 158 */     Vector3i blockPosition = blockChange.getBlockPosition();
/*     */     
/* 160 */     if (Math.abs(blockPosition.getX() - player.x) < range && Math.abs(blockPosition.getY() - player.y) < range && Math.abs(blockPosition.getZ() - player.z) < range && player.lastTransSent + 2L < 
/* 161 */       System.currentTimeMillis()) {
/* 162 */       player.sendTransaction();
/*     */     }
/* 164 */     player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedWorld.updateBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), blockChange.getBlockId()));
/*     */   }
/*     */   
/*     */   public void handleMultiBlockChange(GrimPlayer player, PacketSendEvent event) {
/* 168 */     WrapperPlayServerMultiBlockChange multiBlockChange = new WrapperPlayServerMultiBlockChange(event);
/*     */     
/* 170 */     int range = 16;
/*     */     
/* 172 */     WrapperPlayServerMultiBlockChange.EncodedBlock[] blocks = multiBlockChange.getBlocks();
/* 173 */     for (WrapperPlayServerMultiBlockChange.EncodedBlock blockChange : blocks) {
/*     */       
/* 175 */       if (Math.abs(blockChange.getX() - player.x) < range && Math.abs(blockChange.getY() - player.y) < range && Math.abs(blockChange.getZ() - player.z) < range && player.lastTransSent + 2L < System.currentTimeMillis()) {
/* 176 */         player.sendTransaction();
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*     */     
/* 182 */     player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
/*     */           for (WrapperPlayServerMultiBlockChange.EncodedBlock blockChange : blocks)
/*     */             player.compensatedWorld.updateBlock(blockChange.getX(), blockChange.getY(), blockChange.getZ(), blockChange.getBlockId()); 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\worldreader\BasePacketWorldReader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */