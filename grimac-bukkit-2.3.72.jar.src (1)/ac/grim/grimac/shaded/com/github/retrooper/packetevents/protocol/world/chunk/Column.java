/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLongArray;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ public class Column
/*     */ {
/*     */   private final int x;
/*     */   private final int z;
/*     */   private final boolean fullChunk;
/*     */   private final BaseChunk[] chunks;
/*     */   private final TileEntity[] tileEntities;
/*     */   private final boolean hasHeightmaps;
/*     */   @Nullable
/*     */   private NBTCompound heightmapsNbt;
/*     */   @Nullable
/*     */   private Map<HeightmapType, long[]> heightmaps;
/*     */   private final boolean hasBiomeData;
/*     */   private int[] biomeDataInts;
/*     */   private byte[] biomeDataBytes;
/*     */   
/*     */   public Column(int x, int z, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities, int[] biomeData) {
/*  52 */     this.x = x;
/*  53 */     this.z = z;
/*  54 */     this.fullChunk = fullChunk;
/*  55 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/*  56 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/*  57 */     this.hasHeightmaps = false;
/*  58 */     this.heightmapsNbt = new NBTCompound();
/*  59 */     this.hasBiomeData = true;
/*  60 */     this.biomeDataInts = (biomeData != null) ? Arrays.copyOf(biomeData, biomeData.length) : null;
/*     */   }
/*     */   
/*     */   public Column(int x, int z, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities) {
/*  64 */     this.x = x;
/*  65 */     this.z = z;
/*  66 */     this.fullChunk = fullChunk;
/*  67 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/*  68 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/*  69 */     this.hasHeightmaps = false;
/*  70 */     this.heightmapsNbt = new NBTCompound();
/*  71 */     this.hasBiomeData = false;
/*  72 */     this.biomeDataInts = new int[1024];
/*     */   }
/*     */   
/*     */   public Column(int x, int z, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities, NBTCompound heightmapsNbt) {
/*  76 */     this.x = x;
/*  77 */     this.z = z;
/*  78 */     this.fullChunk = fullChunk;
/*  79 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/*  80 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/*  81 */     this.hasHeightmaps = true;
/*  82 */     this.heightmapsNbt = heightmapsNbt;
/*  83 */     this.hasBiomeData = false;
/*  84 */     this.biomeDataInts = new int[1024];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Column(int x, int z, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities, Map<HeightmapType, long[]> heightmaps) {
/*  91 */     this.x = x;
/*  92 */     this.z = z;
/*  93 */     this.fullChunk = fullChunk;
/*  94 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/*  95 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/*  96 */     this.hasHeightmaps = true;
/*  97 */     this.heightmapsNbt = null;
/*  98 */     this.heightmaps = heightmaps;
/*  99 */     this.hasBiomeData = false;
/* 100 */     this.biomeDataInts = new int[1024];
/*     */   }
/*     */   
/*     */   public Column(int x, int z, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities, NBTCompound heightmapsNbt, int[] biomeDataInts) {
/* 104 */     this.x = x;
/* 105 */     this.z = z;
/* 106 */     this.fullChunk = fullChunk;
/* 107 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/* 108 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/* 109 */     this.hasHeightmaps = true;
/* 110 */     this.heightmapsNbt = heightmapsNbt;
/* 111 */     this.hasBiomeData = true;
/* 112 */     this.biomeDataInts = (biomeDataInts != null) ? Arrays.copyOf(biomeDataInts, biomeDataInts.length) : null;
/*     */   }
/*     */   
/*     */   public Column(int x, int z, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities, NBTCompound heightmapsNbt, byte[] biomeData) {
/* 116 */     this.x = x;
/* 117 */     this.z = z;
/* 118 */     this.fullChunk = fullChunk;
/* 119 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/* 120 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/* 121 */     this.hasHeightmaps = true;
/* 122 */     this.heightmapsNbt = heightmapsNbt;
/* 123 */     this.hasBiomeData = true;
/* 124 */     this.biomeDataBytes = (biomeData != null) ? Arrays.copyOf(biomeData, biomeData.length) : null;
/*     */   }
/*     */   
/*     */   public Column(int chunkX, int chunkZ, boolean fullChunk, BaseChunk[] chunks, TileEntity[] tileEntities, byte[] biomeDataBytes) {
/* 128 */     this.x = chunkX;
/* 129 */     this.z = chunkZ;
/* 130 */     this.fullChunk = fullChunk;
/* 131 */     this.chunks = Arrays.<BaseChunk>copyOf(chunks, chunks.length);
/* 132 */     this.tileEntities = (tileEntities != null) ? tileEntities : new TileEntity[0];
/* 133 */     this.hasHeightmaps = false;
/* 134 */     this.heightmapsNbt = new NBTCompound();
/* 135 */     this.hasBiomeData = true;
/* 136 */     this.biomeDataBytes = (biomeDataBytes != null) ? Arrays.copyOf(biomeDataBytes, biomeDataBytes.length) : null;
/*     */   }
/*     */   
/*     */   public int getX() {
/* 140 */     return this.x;
/*     */   }
/*     */   
/*     */   public int getZ() {
/* 144 */     return this.z;
/*     */   }
/*     */   
/*     */   public boolean isFullChunk() {
/* 148 */     return this.fullChunk;
/*     */   }
/*     */   
/*     */   public BaseChunk[] getChunks() {
/* 152 */     return this.chunks;
/*     */   }
/*     */   
/*     */   public TileEntity[] getTileEntities() {
/* 156 */     return this.tileEntities;
/*     */   }
/*     */   
/*     */   public boolean hasHeightMaps() {
/* 160 */     return this.hasHeightmaps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public NBTCompound getHeightMaps() {
/* 170 */     if (this.heightmapsNbt == null) {
/* 171 */       this.heightmapsNbt = new NBTCompound();
/* 172 */       for (Map.Entry<HeightmapType, long[]> entry : getHeightmaps().entrySet()) {
/* 173 */         this.heightmapsNbt.setTag(((HeightmapType)entry.getKey()).getSerializationKey(), (NBT)new NBTLongArray(entry.getValue()));
/*     */       }
/*     */     } 
/* 176 */     return this.heightmapsNbt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<HeightmapType, long[]> getHeightmaps() {
/* 184 */     if (this.heightmaps == null) {
/* 185 */       if (!this.hasHeightmaps || this.heightmapsNbt.isEmpty()) {
/* 186 */         this.heightmaps = Collections.emptyMap();
/*     */       } else {
/*     */         
/* 189 */         this.heightmaps = (Map)new EnumMap<>(HeightmapType.class);
/* 190 */         for (Map.Entry<String, NBT> tag : (Iterable<Map.Entry<String, NBT>>)this.heightmapsNbt.getTags().entrySet()) {
/* 191 */           HeightmapType heightmapType = HeightmapType.getHeightmapType(tag.getKey());
/* 192 */           if (heightmapType != null && tag.getValue() instanceof NBTLongArray) {
/* 193 */             long[] array = ((NBTLongArray)tag.getValue()).getValue();
/* 194 */             this.heightmaps.put(heightmapType, array);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 199 */     return this.heightmaps;
/*     */   }
/*     */   
/*     */   public boolean hasBiomeData() {
/* 203 */     return this.hasBiomeData;
/*     */   }
/*     */   
/*     */   public int[] getBiomeDataInts() {
/* 207 */     return this.biomeDataInts;
/*     */   }
/*     */   
/*     */   public byte[] getBiomeDataBytes() {
/* 211 */     return this.biomeDataBytes;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\Column.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */