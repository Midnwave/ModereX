/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInputWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamOutput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamOutputWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NibbleArray3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.PaletteType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*     */ public class Chunk_v1_9
/*     */   implements BaseChunk
/*     */ {
/*     */   private static final int AIR = 0;
/*     */   private static final int LIGHT_NIBBLES_SIZE = 2048;
/*     */   private int blockCount;
/*     */   private final DataPalette dataPalette;
/*     */   @Nullable
/*     */   private NibbleArray3d blockLight;
/*     */   @Nullable
/*     */   private NibbleArray3d skyLight;
/*     */   
/*     */   public Chunk_v1_9(int blockCount, DataPalette dataPalette) {
/*  49 */     this(blockCount, dataPalette, (NibbleArray3d)null, (NibbleArray3d)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Chunk_v1_9(int blockCount, DataPalette dataPalette, @Nullable NibbleArray3d blockLight, @Nullable NibbleArray3d skyLight) {
/*  58 */     this.blockCount = blockCount;
/*  59 */     this.dataPalette = dataPalette;
/*  60 */     this.blockLight = blockLight;
/*  61 */     this.skyLight = skyLight;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Chunk_v1_9(NetStreamInput in, boolean hasBlockLight, boolean hasSkyLight) {
/*  69 */     this(in, hasBlockLight, hasSkyLight, PacketEvents.getAPI().getServerManager().getVersion());
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   private Chunk_v1_9(NetStreamInput in, boolean hasBlockLight, boolean hasSkyLight, ServerVersion version) {
/*  75 */     this
/*  76 */       .blockCount = version.isNewerThanOrEquals(ServerVersion.V_1_14) ? in.readShort() : Integer.MAX_VALUE;
/*     */     
/*  78 */     this
/*     */       
/*  80 */       .dataPalette = version.isNewerThanOrEquals(ServerVersion.V_1_16) ? DataPalette.read(in, PaletteType.CHUNK, false) : DataPalette.readLegacy(in);
/*     */     
/*  82 */     this.blockLight = hasBlockLight ? new NibbleArray3d(in, 2048) : null;
/*  83 */     this.skyLight = hasSkyLight ? new NibbleArray3d(in, 2048) : null;
/*     */   }
/*     */   
/*     */   public static Chunk_v1_9 read(PacketWrapper<?> wrapper, boolean hasBlockLight, boolean hasSkyLight) {
/*  87 */     NetStreamInputWrapper legacyInput = new NetStreamInputWrapper(wrapper);
/*  88 */     return new Chunk_v1_9((NetStreamInput)legacyInput, hasBlockLight, hasSkyLight, wrapper.getServerVersion());
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, Chunk_v1_9 chunk) {
/*  92 */     NetStreamOutputWrapper legacyOutput = new NetStreamOutputWrapper(wrapper);
/*  93 */     write((NetStreamOutput)legacyOutput, chunk, wrapper.getServerVersion());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void write(NetStreamOutput out, Chunk_v1_9 chunk) {
/* 101 */     write(out, chunk, PacketEvents.getAPI().getServerManager().getVersion());
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   private static void write(NetStreamOutput out, Chunk_v1_9 chunk, ServerVersion version) {
/* 107 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_14)) {
/* 108 */       out.writeShort(chunk.blockCount);
/*     */     }
/*     */     
/* 111 */     DataPalette.write(out, chunk.dataPalette);
/*     */     
/* 113 */     if (chunk.blockLight != null) {
/* 114 */       out.writeBytes(chunk.blockLight.getData());
/*     */     }
/* 116 */     if (chunk.skyLight != null) {
/* 117 */       out.writeBytes(chunk.skyLight.getData());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBlockId(int x, int y, int z) {
/* 123 */     return this.dataPalette.get(x, y, z);
/*     */   }
/*     */   
/*     */   public void set(int x, int y, int z, int state) {
/* 127 */     int curr = this.dataPalette.set(x, y, z, state);
/*     */     
/* 129 */     if (this.blockCount == Integer.MAX_VALUE)
/* 130 */       return;  if (state != 0 && curr == 0) {
/* 131 */       this.blockCount++;
/* 132 */     } else if (state == 0 && curr != 0) {
/* 133 */       this.blockCount--;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 140 */     if (this.blockCount == Integer.MAX_VALUE) {
/* 141 */       for (int x = 0; x < 16; x++) {
/* 142 */         for (int y = 0; y < 16; y++) {
/* 143 */           for (int z = 0; z < 16; z++) {
/* 144 */             if (this.dataPalette.get(x, y, z) != 0) {
/* 145 */               return false;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/* 150 */       return true;
/*     */     } 
/*     */     
/* 153 */     return (this.blockCount == 0);
/*     */   }
/*     */   @Nullable
/*     */   public NibbleArray3d getSkyLight() {
/* 157 */     return this.skyLight;
/*     */   }
/*     */   
/*     */   public void setSkyLight(@Nullable NibbleArray3d skyLight) {
/* 161 */     this.skyLight = skyLight;
/*     */   }
/*     */   @Nullable
/*     */   public NibbleArray3d getBlockLight() {
/* 165 */     return this.blockLight;
/*     */   }
/*     */   
/*     */   public void setBlockLight(@Nullable NibbleArray3d blockLight) {
/* 169 */     this.blockLight = blockLight;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\impl\v1_16\Chunk_v1_9.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */