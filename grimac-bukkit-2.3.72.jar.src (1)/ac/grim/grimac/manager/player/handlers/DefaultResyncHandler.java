/*     */ package ac.grim.grimac.manager.player.handlers;
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.platform.api.world.PlatformChunk;
/*     */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAcknowledgeBlockChanges;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
/*     */ 
/*     */ public class DefaultResyncHandler implements ResyncHandler {
/*     */   @Generated
/*     */   public DefaultResyncHandler(GrimPlayer player) {
/*  17 */     this.player = player;
/*     */   }
/*     */ 
/*     */   
/*     */   private final GrimPlayer player;
/*     */   
/*     */   private static void resyncPositions(GrimPlayer player, int minBlockX, int mY, int minBlockZ, int maxBlockX, int mxY, int maxBlockZ) {
/*  24 */     if (!player.compensatedWorld.isChunkLoaded(minBlockX >> 4, minBlockZ >> 4) || !player.compensatedWorld.isChunkLoaded(minBlockX >> 4, maxBlockZ >> 4) || 
/*  25 */       !player.compensatedWorld.isChunkLoaded(maxBlockX >> 4, minBlockZ >> 4) || !player.compensatedWorld.isChunkLoaded(maxBlockX >> 4, maxBlockZ >> 4)) {
/*     */       return;
/*     */     }
/*  28 */     if (player.platformPlayer == null)
/*     */       return; 
/*  30 */     PlatformWorld world = player.platformPlayer.getWorld();
/*     */ 
/*     */ 
/*     */     
/*  34 */     GrimAPI.INSTANCE.getScheduler().getRegionScheduler().execute(GrimAPI.INSTANCE.getGrimPlugin(), world, minBlockX >> 4, minBlockZ >> 4, () -> {
/*     */           if (!(player.getSetbackTeleportUtil()).hasAcceptedSpawnTeleport) {
/*     */             return;
/*     */           }
/*     */           if (!world.isChunkLoaded(minBlockX >> 4, minBlockZ >> 4) || !world.isChunkLoaded(minBlockX >> 4, maxBlockZ >> 4) || !world.isChunkLoaded(maxBlockX >> 4, minBlockZ >> 4) || !world.isChunkLoaded(maxBlockX >> 4, maxBlockZ >> 4)) {
/*     */             return;
/*     */           }
/*     */           int minSection = player.compensatedWorld.getMinHeight() >> 4;
/*     */           int minBlock = minSection << 4;
/*     */           int maxBlock = player.compensatedWorld.getMaxHeight() - 1;
/*     */           int minBlockY = Math.max(minBlock, mY);
/*     */           int maxBlockY = Math.min(maxBlock, mxY);
/*     */           int minChunkX = minBlockX >> 4;
/*     */           int maxChunkX = maxBlockX >> 4;
/*     */           int minChunkY = minBlockY >> 4;
/*     */           int maxChunkY = maxBlockY >> 4;
/*     */           int minChunkZ = minBlockZ >> 4;
/*     */           int maxChunkZ = maxBlockZ >> 4;
/*     */           for (int currChunkZ = minChunkZ; currChunkZ <= maxChunkZ; currChunkZ++) {
/*     */             int minZ = (currChunkZ == minChunkZ) ? (minBlockZ & 0xF) : 0;
/*     */             int maxZ = (currChunkZ == maxChunkZ) ? (maxBlockZ & 0xF) : 15;
/*     */             for (int currChunkX = minChunkX; currChunkX <= maxChunkX; currChunkX++) {
/*     */               int minX = (currChunkX == minChunkX) ? (minBlockX & 0xF) : 0;
/*     */               int maxX = (currChunkX == maxChunkX) ? (maxBlockX & 0xF) : 15;
/*     */               PlatformChunk chunk = world.getChunkAt(currChunkX, currChunkZ);
/*     */               for (int currChunkY = minChunkY; currChunkY <= maxChunkY; currChunkY++) {
/*     */                 int minY = (currChunkY == minChunkY) ? (minBlockY & 0xF) : 0;
/*     */                 int maxY = (currChunkY == maxChunkY) ? (maxBlockY & 0xF) : 15;
/*     */                 int totalBlocks = (maxX - minX + 1) * (maxZ - minZ + 1) * (maxY - minY + 1);
/*     */                 WrapperPlayServerMultiBlockChange.EncodedBlock[] encodedBlocks = new WrapperPlayServerMultiBlockChange.EncodedBlock[totalBlocks];
/*     */                 int blockIndex = 0;
/*     */                 for (int currZ = minZ; currZ <= maxZ; currZ++) {
/*     */                   for (int currX = minX; currX <= maxX; currX++) {
/*     */                     for (int currY = minY; currY <= maxY; currY++) {
/*     */                       int blockId = chunk.getBlockID(currX, currY | currChunkY << 4, currZ);
/*     */                       encodedBlocks[blockIndex++] = new WrapperPlayServerMultiBlockChange.EncodedBlock(blockId, currX, currY | currChunkY << 4, currZ);
/*     */                     } 
/*     */                   } 
/*     */                 } 
/*     */                 WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(new Vector3i(currChunkX, currChunkY, currChunkZ), Boolean.valueOf(true), encodedBlocks);
/*     */                 player.runSafely(());
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         });
/*     */   }
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
/*     */   private static void resyncPosition(GrimPlayer player, int x, int y, int z, int sequence) {
/* 101 */     if (player.platformPlayer == null)
/*     */       return; 
/* 103 */     int chunkX = x >> 4;
/* 104 */     int chunkZ = z >> 4;
/* 105 */     if (!player.compensatedWorld.isChunkLoaded(chunkX, chunkZ)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     PlatformWorld world = player.platformPlayer.getWorld();
/*     */     
/* 116 */     GrimAPI.INSTANCE.getScheduler().getRegionScheduler().execute(GrimAPI.INSTANCE.getGrimPlugin(), world, chunkX, chunkZ, () -> {
/*     */           if (!player.platformPlayer.isOnline() || !(player.getSetbackTeleportUtil()).hasAcceptedSpawnTeleport) {
/*     */             return;
/*     */           }
/*     */           if (player.platformPlayer.getPosition().distance(new Vector3d(x, y, z)) >= 64.0D) {
/*     */             return;
/*     */           }
/*     */           if (!world.isChunkLoaded(chunkX, chunkZ)) {
/*     */             return;
/*     */           }
/*     */           int blockId = world.getChunkAt(chunkX, chunkZ).getBlockID(x & 0xF, y, z & 0xF);
/*     */           player.user.sendPacket((PacketWrapper)new WrapperPlayServerBlockChange(new Vector3i(x, y, z), blockId));
/*     */           if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19))
/*     */             player.user.sendPacket((PacketWrapper)new WrapperPlayServerAcknowledgeBlockChanges(sequence)); 
/*     */         });
/*     */   }
/*     */   
/*     */   public void resyncPosition(int x, int y, int z, int sequence) {
/* 134 */     resyncPosition(this.player, x, y, z, sequence);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resync(int minBlockX, int minBlockY, int minBlockZ, int maxBlockX, int maxBlockY, int maxBlockZ) {
/* 139 */     resyncPositions(this.player, minBlockX, minBlockY, minBlockZ, maxBlockX, maxBlockY, maxBlockZ);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\player\handlers\DefaultResyncHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */