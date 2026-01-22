/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.impl;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.ChunkReader;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class ChunkReader_v1_9
/*    */   implements ChunkReader
/*    */ {
/*    */   public BaseChunk[] read(DimensionType dimensionType, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, int arrayLength, PacketWrapper<?> wrapper) {
/* 36 */     BaseChunk[] chunks = new BaseChunk[chunkSize];
/*    */     
/* 38 */     for (int index = 0; index < chunks.length; index++) {
/* 39 */       if (chunkMask.get(index)) {
/* 40 */         chunks[index] = (BaseChunk)Chunk_v1_9.read(wrapper, hasBlockLight, hasSkyLight);
/*    */       }
/*    */     } 
/*    */     
/* 44 */     return chunks;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\reader\impl\ChunkReader_v1_9.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */