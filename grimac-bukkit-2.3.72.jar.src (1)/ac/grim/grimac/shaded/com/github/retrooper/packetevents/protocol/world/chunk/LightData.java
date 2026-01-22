/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.BitSet;
/*     */ 
/*     */ public class LightData
/*     */   implements Cloneable
/*     */ {
/*     */   private boolean trustEdges;
/*     */   private BitSet blockLightMask;
/*     */   private BitSet skyLightMask;
/*     */   private BitSet emptyBlockLightMask;
/*     */   private BitSet emptySkyLightMask;
/*     */   private int skyLightCount;
/*     */   private int blockLightCount;
/*     */   private byte[][] skyLightArray;
/*     */   private byte[][] blockLightArray;
/*     */   
/*     */   public LightData() {}
/*     */   
/*     */   public LightData(boolean trustEdges, BitSet blockLightMask, BitSet skyLightMask, BitSet emptyBlockLightMask, BitSet emptySkyLightMask, int skyLightCount, int blockLightCount, byte[][] skyLightArray, byte[][] blockLightArray) {
/*  23 */     this.trustEdges = trustEdges;
/*  24 */     this.blockLightMask = blockLightMask;
/*  25 */     this.skyLightMask = skyLightMask;
/*  26 */     this.emptyBlockLightMask = emptyBlockLightMask;
/*  27 */     this.emptySkyLightMask = emptySkyLightMask;
/*  28 */     this.skyLightCount = skyLightCount;
/*  29 */     this.blockLightCount = blockLightCount;
/*  30 */     this.skyLightArray = skyLightArray;
/*  31 */     this.blockLightArray = blockLightArray;
/*     */   }
/*     */ 
/*     */   
/*     */   public LightData clone() {
/*     */     try {
/*  37 */       LightData clone = (LightData)super.clone();
/*  38 */       clone.blockLightMask = (BitSet)this.blockLightMask.clone();
/*  39 */       clone.skyLightMask = (BitSet)this.skyLightMask.clone();
/*  40 */       clone.emptyBlockLightMask = (BitSet)this.emptyBlockLightMask.clone();
/*  41 */       clone.emptySkyLightMask = (BitSet)this.emptySkyLightMask.clone();
/*  42 */       clone.skyLightArray = (byte[][])this.skyLightArray.clone();
/*  43 */       clone.blockLightArray = (byte[][])this.blockLightArray.clone();
/*  44 */       return clone;
/*  45 */     } catch (CloneNotSupportedException e) {
/*  46 */       throw new AssertionError();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isTrustEdges() {
/*  51 */     return this.trustEdges;
/*     */   }
/*     */   
/*     */   public void setTrustEdges(boolean trustEdges) {
/*  55 */     this.trustEdges = trustEdges;
/*     */   }
/*     */   
/*     */   public BitSet getBlockLightMask() {
/*  59 */     return this.blockLightMask;
/*     */   }
/*     */   
/*     */   public void setBlockLightMask(BitSet blockLightMask) {
/*  63 */     this.blockLightMask = blockLightMask;
/*     */   }
/*     */   
/*     */   public BitSet getSkyLightMask() {
/*  67 */     return this.skyLightMask;
/*     */   }
/*     */   
/*     */   public void setSkyLightMask(BitSet skyLightMask) {
/*  71 */     this.skyLightMask = skyLightMask;
/*     */   }
/*     */   
/*     */   public BitSet getEmptyBlockLightMask() {
/*  75 */     return this.emptyBlockLightMask;
/*     */   }
/*     */   
/*     */   public void setEmptyBlockLightMask(BitSet emptyBlockLightMask) {
/*  79 */     this.emptyBlockLightMask = emptyBlockLightMask;
/*     */   }
/*     */   
/*     */   public BitSet getEmptySkyLightMask() {
/*  83 */     return this.emptySkyLightMask;
/*     */   }
/*     */   
/*     */   public void setEmptySkyLightMask(BitSet emptySkyLightMask) {
/*  87 */     this.emptySkyLightMask = emptySkyLightMask;
/*     */   }
/*     */   
/*     */   public int getSkyLightCount() {
/*  91 */     return this.skyLightCount;
/*     */   }
/*     */   
/*     */   public void setSkyLightCount(int skyLightCount) {
/*  95 */     this.skyLightCount = skyLightCount;
/*     */   }
/*     */   
/*     */   public int getBlockLightCount() {
/*  99 */     return this.blockLightCount;
/*     */   }
/*     */   
/*     */   public void setBlockLightCount(int blockLightCount) {
/* 103 */     this.blockLightCount = blockLightCount;
/*     */   }
/*     */   
/*     */   public byte[][] getSkyLightArray() {
/* 107 */     return this.skyLightArray;
/*     */   }
/*     */   
/*     */   public void setSkyLightArray(byte[][] skyLightArray) {
/* 111 */     this.skyLightArray = skyLightArray;
/*     */   }
/*     */   
/*     */   public byte[][] getBlockLightArray() {
/* 115 */     return this.blockLightArray;
/*     */   }
/*     */   
/*     */   public void setBlockLightArray(byte[][] blockLightArray) {
/* 119 */     this.blockLightArray = blockLightArray;
/*     */   }
/*     */   
/*     */   public static LightData read(PacketWrapper<?> packet) {
/* 123 */     LightData lightData = new LightData();
/* 124 */     ServerVersion serverVersion = packet.getServerVersion();
/* 125 */     if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
/* 126 */       lightData.trustEdges = packet.readBoolean();
/*     */     }
/*     */     
/* 129 */     lightData.skyLightMask = ChunkBitMask.readChunkMask(packet);
/* 130 */     lightData.blockLightMask = ChunkBitMask.readChunkMask(packet);
/* 131 */     lightData.emptySkyLightMask = ChunkBitMask.readChunkMask(packet);
/* 132 */     lightData.emptyBlockLightMask = ChunkBitMask.readChunkMask(packet);
/*     */     
/* 134 */     boolean v17 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17);
/* 135 */     lightData.skyLightCount = v17 ? packet.readVarInt() : 18;
/* 136 */     lightData.skyLightArray = new byte[lightData.skyLightCount][]; int i;
/* 137 */     for (i = 0; i < lightData.skyLightCount; i++) {
/* 138 */       if (v17 || lightData.skyLightMask.get(i)) {
/* 139 */         lightData.skyLightArray[i] = packet.readByteArray();
/*     */       }
/*     */     } 
/*     */     
/* 143 */     lightData.blockLightCount = v17 ? packet.readVarInt() : 18;
/* 144 */     lightData.blockLightArray = new byte[lightData.blockLightCount][];
/* 145 */     for (i = 0; i < lightData.blockLightCount; i++) {
/* 146 */       if (v17 || lightData.blockLightMask.get(i)) {
/* 147 */         lightData.blockLightArray[i] = packet.readByteArray();
/*     */       }
/*     */     } 
/*     */     
/* 151 */     return lightData;
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> packet, LightData lightData) {
/* 155 */     ServerVersion serverVersion = packet.getServerVersion();
/* 156 */     if (serverVersion.isOlderThanOrEquals(ServerVersion.V_1_19_4)) {
/* 157 */       packet.writeBoolean(lightData.trustEdges);
/*     */     }
/* 159 */     ChunkBitMask.writeChunkMask(packet, lightData.skyLightMask);
/* 160 */     ChunkBitMask.writeChunkMask(packet, lightData.blockLightMask);
/* 161 */     ChunkBitMask.writeChunkMask(packet, lightData.emptySkyLightMask);
/* 162 */     ChunkBitMask.writeChunkMask(packet, lightData.emptyBlockLightMask);
/*     */     
/* 164 */     boolean v17 = serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17);
/* 165 */     if (v17)
/* 166 */       packet.writeVarInt(lightData.skyLightCount); 
/*     */     int i;
/* 168 */     for (i = 0; i < lightData.skyLightCount; i++) {
/* 169 */       if (v17 || lightData.skyLightMask.get(i)) {
/* 170 */         packet.writeByteArray(lightData.skyLightArray[i]);
/*     */       }
/*     */     } 
/*     */     
/* 174 */     if (v17) {
/* 175 */       packet.writeVarInt(lightData.blockLightCount);
/*     */     }
/* 177 */     for (i = 0; i < lightData.blockLightCount; i++) {
/* 178 */       if (v17 || lightData.blockLightMask.get(i))
/* 179 */         packet.writeByteArray(lightData.blockLightArray[i]); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\LightData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */