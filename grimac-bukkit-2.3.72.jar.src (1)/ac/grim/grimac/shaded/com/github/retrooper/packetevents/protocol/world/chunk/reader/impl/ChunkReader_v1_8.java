/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.impl;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NetworkChunkData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NibbleArray3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.ShortArray3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.ChunkReader;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.ShortBuffer;
/*     */ import java.util.BitSet;
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
/*     */ 
/*     */ public class ChunkReader_v1_8
/*     */   implements ChunkReader
/*     */ {
/*     */   public BaseChunk[] read(DimensionType dimensionType, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, int arrayLength, PacketWrapper<?> wrapper) {
/*  43 */     byte[] data = wrapper.readByteArrayOfSize(arrayLength);
/*     */     
/*  45 */     Chunk_v1_8[] chunks = new Chunk_v1_8[16];
/*  46 */     int pos = 0;
/*  47 */     int expected = fullChunk ? 256 : 0;
/*  48 */     boolean sky = false;
/*     */     
/*  50 */     ShortBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  56 */     for (int pass = 0; pass < 4; pass++) {
/*  57 */       for (int ind = 0; ind < 16; ind++) {
/*  58 */         if (chunkMask.get(ind)) {
/*  59 */           if (pass == 0)
/*     */           {
/*  61 */             expected += 10240;
/*     */           }
/*     */           
/*  64 */           if (pass == 1) {
/*  65 */             chunks[ind] = new Chunk_v1_8((sky || hasBlockLight));
/*  66 */             ShortArray3d blocks = chunks[ind].getBlocks();
/*  67 */             buf.position(pos / 2);
/*  68 */             buf.get(blocks.getData(), 0, (blocks.getData()).length);
/*  69 */             pos += (blocks.getData()).length * 2;
/*     */           } 
/*     */           
/*  72 */           if (pass == 2) {
/*  73 */             NibbleArray3d blocklight = chunks[ind].getBlockLight();
/*  74 */             System.arraycopy(data, pos, blocklight.getData(), 0, (blocklight.getData()).length);
/*  75 */             pos += (blocklight.getData()).length;
/*     */           } 
/*     */           
/*  78 */           if (pass == 3 && (sky || hasBlockLight)) {
/*  79 */             NibbleArray3d skylight = chunks[ind].getSkyLight();
/*  80 */             System.arraycopy(data, pos, skylight.getData(), 0, (skylight.getData()).length);
/*  81 */             pos += (skylight.getData()).length;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/*  86 */       if (pass == 0 && data.length > expected)
/*     */       {
/*  88 */         sky = hasSkyLight;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  93 */     int ri = ByteBufHelper.readerIndex(wrapper.buffer);
/*  94 */     ByteBufHelper.readerIndex(wrapper.buffer, ri - arrayLength - pos);
/*     */     
/*  96 */     return (BaseChunk[])chunks;
/*     */   }
/*     */   
/*     */   public static NetworkChunkData chunksToData(Chunk_v1_8[] chunks, byte[] biomes) {
/* 100 */     int chunkMask = 0;
/* 101 */     boolean fullChunk = (biomes != null);
/* 102 */     boolean sky = false;
/* 103 */     int length = fullChunk ? biomes.length : 0;
/* 104 */     byte[] data = null;
/* 105 */     int pos = 0;
/* 106 */     ShortBuffer buf = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     for (int pass = 0; pass < 4; pass++) {
/* 113 */       for (int ind = 0; ind < chunks.length; ind++) {
/* 114 */         Chunk_v1_8 chunk = chunks[ind];
/* 115 */         if (chunk != null && (!fullChunk || !chunk.isEmpty())) {
/* 116 */           if (pass == 0) {
/* 117 */             chunkMask |= 1 << ind;
/* 118 */             length += (chunk.getBlocks().getData()).length * 2;
/* 119 */             length += (chunk.getBlockLight().getData()).length;
/* 120 */             if (chunk.getSkyLight() != null) {
/* 121 */               length += (chunk.getSkyLight().getData()).length;
/*     */             }
/*     */           } 
/*     */           
/* 125 */           if (pass == 1) {
/* 126 */             short[] blocks = chunk.getBlocks().getData();
/* 127 */             buf.position(pos / 2);
/* 128 */             buf.put(blocks, 0, blocks.length);
/* 129 */             pos += blocks.length * 2;
/*     */           } 
/*     */           
/* 132 */           if (pass == 2) {
/* 133 */             byte[] blocklight = chunk.getBlockLight().getData();
/* 134 */             System.arraycopy(blocklight, 0, data, pos, blocklight.length);
/* 135 */             pos += blocklight.length;
/*     */           } 
/*     */           
/* 138 */           if (pass == 3 && chunk.getSkyLight() != null) {
/* 139 */             byte[] skylight = chunk.getSkyLight().getData();
/* 140 */             System.arraycopy(skylight, 0, data, pos, skylight.length);
/* 141 */             pos += skylight.length;
/* 142 */             sky = true;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 147 */       if (pass == 0) {
/* 148 */         data = new byte[length];
/* 149 */         buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 154 */     if (fullChunk) {
/* 155 */       System.arraycopy(biomes, 0, data, pos, biomes.length);
/* 156 */       pos += biomes.length;
/*     */     } 
/*     */     
/* 159 */     return new NetworkChunkData(chunkMask, fullChunk, sky, data);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\reader\impl\ChunkReader_v1_8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */