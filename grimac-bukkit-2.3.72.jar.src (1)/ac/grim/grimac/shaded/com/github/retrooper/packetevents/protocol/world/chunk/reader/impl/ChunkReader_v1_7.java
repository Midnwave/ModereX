/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.impl;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.ByteArray3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NetworkChunkData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NibbleArray3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_7.Chunk_v1_7;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.ChunkReader;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*     */ public class ChunkReader_v1_7
/*     */   implements ChunkReader
/*     */ {
/*     */   public BaseChunk[] read(DimensionType dimensionType, BitSet chunkMask, BitSet secondaryChunkMask, boolean fullChunk, boolean hasBlockLight, boolean hasSkyLight, int chunkSize, int arrayLength, PacketWrapper<?> wrapper) {
/*  40 */     byte[] data = wrapper.readByteArrayOfSize(arrayLength);
/*     */     
/*  42 */     Chunk_v1_7[] chunks = new Chunk_v1_7[16];
/*  43 */     int pos = 0;
/*  44 */     int expected = 0;
/*  45 */     boolean sky = false;
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
/*  59 */     for (int pass = 0; pass < 5; pass++) {
/*  60 */       for (int ind = 0; ind < 16; ind++) {
/*  61 */         if (chunkMask.get(ind)) {
/*  62 */           if (pass == 0) {
/*  63 */             expected += 10240;
/*  64 */             if (secondaryChunkMask.get(ind)) {
/*  65 */               expected += 2048;
/*     */             }
/*     */           } 
/*     */           
/*  69 */           if (pass == 1) {
/*  70 */             chunks[ind] = new Chunk_v1_7(sky, secondaryChunkMask.get(ind));
/*  71 */             ByteArray3d blocks = chunks[ind].getBlocks();
/*  72 */             System.arraycopy(data, pos, blocks.getData(), 0, (blocks.getData()).length);
/*  73 */             pos += (blocks.getData()).length;
/*     */           } 
/*     */           
/*  76 */           if (pass == 2) {
/*  77 */             NibbleArray3d metadata = chunks[ind].getMetadata();
/*  78 */             System.arraycopy(data, pos, metadata.getData(), 0, (metadata.getData()).length);
/*  79 */             pos += (metadata.getData()).length;
/*     */           } 
/*     */           
/*  82 */           if (pass == 3) {
/*  83 */             NibbleArray3d blocklight = chunks[ind].getBlockLight();
/*  84 */             System.arraycopy(data, pos, blocklight.getData(), 0, (blocklight.getData()).length);
/*  85 */             pos += (blocklight.getData()).length;
/*     */           } 
/*     */           
/*  88 */           if (pass == 4 && sky) {
/*  89 */             NibbleArray3d skylight = chunks[ind].getSkyLight();
/*  90 */             System.arraycopy(data, pos, skylight.getData(), 0, (skylight.getData()).length);
/*  91 */             pos += (skylight.getData()).length;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/*  96 */       if (pass == 0 && data.length >= expected) {
/*  97 */         sky = true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 102 */     int ri = ByteBufHelper.readerIndex(wrapper.buffer);
/* 103 */     ByteBufHelper.readerIndex(wrapper.buffer, ri - arrayLength - pos);
/*     */     
/* 105 */     return (BaseChunk[])chunks;
/*     */   }
/*     */   
/*     */   public static NetworkChunkData chunksToData(Chunk_v1_7[] chunks, byte[] biomes) {
/* 109 */     int chunkMask = 0;
/* 110 */     int extendedChunkMask = 0;
/* 111 */     boolean fullChunk = (biomes != null);
/* 112 */     boolean sky = false;
/* 113 */     int length = fullChunk ? biomes.length : 0;
/* 114 */     byte[] data = null;
/* 115 */     int pos = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 122 */     for (int pass = 0; pass < 6; pass++) {
/* 123 */       for (int ind = 0; ind < chunks.length; ind++) {
/* 124 */         Chunk_v1_7 chunk = chunks[ind];
/* 125 */         if (chunk != null && (!fullChunk || !chunk.isEmpty())) {
/* 126 */           if (pass == 0) {
/* 127 */             chunkMask |= 1 << ind;
/* 128 */             if (chunk.getExtendedBlocks() != null) {
/* 129 */               extendedChunkMask |= 1 << ind;
/*     */             }
/*     */             
/* 132 */             length += (chunk.getBlocks().getData()).length;
/* 133 */             length += (chunk.getMetadata().getData()).length;
/* 134 */             length += (chunk.getBlockLight().getData()).length;
/* 135 */             if (chunk.getSkyLight() != null) {
/* 136 */               length += (chunk.getSkyLight().getData()).length;
/*     */             }
/*     */             
/* 139 */             if (chunk.getExtendedBlocks() != null) {
/* 140 */               length += (chunk.getExtendedBlocks().getData()).length;
/*     */             }
/*     */           } 
/*     */           
/* 144 */           if (pass == 1) {
/* 145 */             ByteArray3d blocks = chunk.getBlocks();
/* 146 */             System.arraycopy(blocks.getData(), 0, data, pos, (blocks.getData()).length);
/* 147 */             pos += (blocks.getData()).length;
/*     */           } 
/*     */           
/* 150 */           if (pass == 2) {
/* 151 */             byte[] meta = chunk.getMetadata().getData();
/* 152 */             System.arraycopy(meta, 0, data, pos, meta.length);
/* 153 */             pos += meta.length;
/*     */           } 
/*     */           
/* 156 */           if (pass == 3) {
/* 157 */             byte[] blocklight = chunk.getBlockLight().getData();
/* 158 */             System.arraycopy(blocklight, 0, data, pos, blocklight.length);
/* 159 */             pos += blocklight.length;
/*     */           } 
/*     */           
/* 162 */           if (pass == 4 && chunk.getSkyLight() != null) {
/* 163 */             byte[] skylight = chunk.getSkyLight().getData();
/* 164 */             System.arraycopy(skylight, 0, data, pos, skylight.length);
/* 165 */             pos += skylight.length;
/* 166 */             sky = true;
/*     */           } 
/*     */           
/* 169 */           if (pass == 5 && chunk.getExtendedBlocks() != null) {
/* 170 */             byte[] extended = chunk.getExtendedBlocks().getData();
/* 171 */             System.arraycopy(extended, 0, data, pos, extended.length);
/* 172 */             pos += extended.length;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 177 */       if (pass == 0) {
/* 178 */         data = new byte[length];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 183 */     if (fullChunk) {
/* 184 */       System.arraycopy(biomes, 0, data, pos, biomes.length);
/* 185 */       pos += biomes.length;
/*     */     } 
/*     */     
/* 188 */     return new NetworkChunkData(chunkMask, extendedChunkMask, fullChunk, sky, data);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\reader\impl\ChunkReader_v1_7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */