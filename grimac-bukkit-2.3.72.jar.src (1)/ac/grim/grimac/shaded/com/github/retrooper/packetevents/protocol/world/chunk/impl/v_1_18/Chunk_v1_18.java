/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInputWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamOutput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamOutputWrapper;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.PaletteType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*     */ public class Chunk_v1_18
/*     */   implements BaseChunk
/*     */ {
/*     */   private static final int AIR = 0;
/*     */   private int blockCount;
/*     */   private final DataPalette chunkData;
/*     */   private final DataPalette biomeData;
/*     */   
/*     */   public Chunk_v1_18() {
/*  40 */     this.chunkData = PaletteType.CHUNK.create();
/*  41 */     this.biomeData = PaletteType.BIOME.create();
/*     */   }
/*     */   
/*     */   public Chunk_v1_18(int blockCount, DataPalette chunkData, DataPalette biomeData) {
/*  45 */     this.blockCount = blockCount;
/*  46 */     this.chunkData = chunkData;
/*  47 */     this.biomeData = biomeData;
/*     */   }
/*     */   
/*     */   public static Chunk_v1_18 read(PacketWrapper<?> wrapper) {
/*  51 */     boolean paletteLengthPrefix = wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5);
/*  52 */     return read((NetStreamInput)new NetStreamInputWrapper(wrapper), paletteLengthPrefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Chunk_v1_18 read(NetStreamInput in) {
/*  60 */     return read(in, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Chunk_v1_18 read(NetStreamInput in, boolean paletteLengthPrefix) {
/*  68 */     int blockCount = in.readShort();
/*  69 */     DataPalette chunkPalette = DataPalette.read(in, PaletteType.CHUNK, true, paletteLengthPrefix);
/*     */     
/*  71 */     DataPalette biomePalette = DataPalette.read(in, PaletteType.BIOME, true, paletteLengthPrefix);
/*     */     
/*  73 */     return new Chunk_v1_18(blockCount, chunkPalette, biomePalette);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, Chunk_v1_18 section) {
/*  77 */     boolean paletteLengthPrefix = wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5);
/*  78 */     write((NetStreamOutput)new NetStreamOutputWrapper(wrapper), section, paletteLengthPrefix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void write(NetStreamOutput out, Chunk_v1_18 section) {
/*  86 */     write(out, section, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void write(NetStreamOutput out, Chunk_v1_18 section, boolean paletteLengthPrefix) {
/*  94 */     out.writeShort(section.blockCount);
/*  95 */     DataPalette.write(out, section.chunkData, paletteLengthPrefix);
/*  96 */     DataPalette.write(out, section.biomeData, paletteLengthPrefix);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBlockId(int x, int y, int z) {
/* 101 */     return this.chunkData.get(x, y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(int x, int y, int z, int state) {
/* 106 */     int curr = this.chunkData.set(x, y, z, state);
/* 107 */     if (state != 0 && curr == 0) {
/* 108 */       this.blockCount++;
/* 109 */     } else if (state == 0 && curr != 0) {
/* 110 */       this.blockCount--;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 116 */     return (this.blockCount == 0);
/*     */   }
/*     */   
/*     */   public int getBlockCount() {
/* 120 */     return this.blockCount;
/*     */   }
/*     */   
/*     */   public void setBlockCount(int blockCount) {
/* 124 */     this.blockCount = blockCount;
/*     */   }
/*     */   
/*     */   public DataPalette getChunkData() {
/* 128 */     return this.chunkData;
/*     */   }
/*     */   
/*     */   public DataPalette getBiomeData() {
/* 132 */     return this.biomeData;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\impl\v_1_18\Chunk_v1_18.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */