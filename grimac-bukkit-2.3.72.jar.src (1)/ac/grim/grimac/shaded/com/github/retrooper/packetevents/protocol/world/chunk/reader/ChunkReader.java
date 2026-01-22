/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Dimension;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.io.ByteArrayInputStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ChunkReader
/*    */ {
/*    */   @Deprecated
/*    */   default BaseChunk[] read(Dimension dimension, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, byte[] data, NetStreamInput dataIn) {
/* 40 */     DimensionType dimensionType = dimension.asDimensionType(null, null);
/* 41 */     PacketWrapper<?> wrapper = PacketWrapper.createUniversalPacketWrapper(
/* 42 */         UnpooledByteBufAllocationHelper.wrappedBuffer(data));
/*    */     try {
/* 44 */       return read(dimensionType, chunkMask, secondaryChunkMask, fullChunk, hasBlockLight, hasSkyLight, chunkSize, data.length, wrapper);
/*    */     } finally {
/*    */       
/* 47 */       ByteBufHelper.release(wrapper.buffer);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   default BaseChunk[] read(DimensionType dimensionType, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, byte[] data, NetStreamInput dataIn) {
/* 57 */     Dimension dimension = Dimension.fromDimensionType(dimensionType, null, null);
/* 58 */     return read(dimension, chunkMask, secondaryChunkMask, fullChunk, hasBlockLight, hasSkyLight, chunkSize, data, dataIn);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default BaseChunk[] read(DimensionType dimensionType, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, int arrayLength, PacketWrapper<?> wrapper) {
/* 68 */     byte[] data = wrapper.readByteArrayOfSize(arrayLength);
/* 69 */     NetStreamInput dataIn = new NetStreamInput(new ByteArrayInputStream(data));
/* 70 */     return read(dimensionType, chunkMask, secondaryChunkMask, fullChunk, hasBlockLight, hasSkyLight, chunkSize, data, dataIn);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\reader\ChunkReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */