/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.ChunkReader;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BaseStorage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.util.BitSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChunkReader_v1_18
/*    */   implements ChunkReader
/*    */ {
/*    */   @Internal
/*    */   public static int getMojangZeroByteSuffixLength(BaseChunk[] chunks) {
/* 38 */     int mojangPleaseFixThisZeroByteSuffixLength = 0;
/* 39 */     for (BaseChunk chunk : chunks) {
/* 40 */       BaseStorage chunkStorage = (((Chunk_v1_18)chunk).getChunkData()).storage;
/* 41 */       int chunkStorageLen = ByteBufHelper.getByteSize((chunkStorage != null) ? (chunkStorage.getData()).length : 0);
/* 42 */       BaseStorage biomeStorage = (((Chunk_v1_18)chunk).getBiomeData()).storage;
/* 43 */       int biomeStorageLen = ByteBufHelper.getByteSize((biomeStorage != null) ? (biomeStorage.getData()).length : 0);
/* 44 */       mojangPleaseFixThisZeroByteSuffixLength += chunkStorageLen + biomeStorageLen;
/*    */     } 
/* 46 */     return mojangPleaseFixThisZeroByteSuffixLength;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BaseChunk[] read(DimensionType dimensionType, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, int arrayLength, PacketWrapper<?> wrapper) {
/* 54 */     int ri = ByteBufHelper.readerIndex(wrapper.buffer);
/* 55 */     BaseChunk[] chunks = new BaseChunk[chunkSize];
/* 56 */     for (int i = 0; i < chunkSize; i++) {
/* 57 */       chunks[i] = (BaseChunk)Chunk_v1_18.read(wrapper);
/*    */     }
/* 59 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_6) && wrapper
/* 60 */       .getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) && 
/*    */       
/* 62 */       ByteBufHelper.readerIndex(wrapper.buffer) - ri < arrayLength) {
/* 63 */       ByteBufHelper.skipBytes(wrapper.buffer, getMojangZeroByteSuffixLength(chunks));
/*    */     }
/* 65 */     return chunks;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\reader\impl\ChunkReader_v1_18.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */