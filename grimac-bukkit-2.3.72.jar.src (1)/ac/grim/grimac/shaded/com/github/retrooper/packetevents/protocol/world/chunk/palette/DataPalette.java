/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamOutput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BaseStorage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BitStorage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.LegacyFlexibleStorage;
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
/*     */ public class DataPalette
/*     */ {
/*     */   public final PaletteType paletteType;
/*     */   public Palette palette;
/*     */   public BaseStorage storage;
/*     */   
/*     */   public DataPalette(Palette palette, BaseStorage storage, PaletteType paletteType) {
/*  35 */     this.palette = palette;
/*  36 */     this.storage = storage;
/*  37 */     this.paletteType = paletteType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette createForChunk() {
/*  45 */     return PaletteType.CHUNK.create();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette createForBiome() {
/*  53 */     return PaletteType.BIOME.create();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette createEmpty(PaletteType paletteType) {
/*  61 */     return paletteType.create();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette read(NetStreamInput in, PaletteType paletteType) {
/*  69 */     return read(in, paletteType, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette read(NetStreamInput in, PaletteType paletteType, boolean allowSingletonPalette) {
/*  77 */     return read(in, paletteType, allowSingletonPalette, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette read(NetStreamInput in, PaletteType paletteType, boolean allowSingletonPalette, boolean lengthPrefix) {
/*     */     BitStorage storage;
/*  88 */     int bitsPerEntry = in.readByte();
/*  89 */     Palette palette = readPalette(paletteType, bitsPerEntry, in, allowSingletonPalette);
/*     */     
/*  91 */     if (!(palette instanceof SingletonPalette)) {
/*  92 */       long[] data = lengthPrefix ? in.readLongs(in.readVarInt()) : null;
/*  93 */       storage = new BitStorage(bitsPerEntry, paletteType.getStorageSize(), data);
/*  94 */       if (!lengthPrefix)
/*     */       {
/*  96 */         in.readLongs(storage.getData());
/*     */       }
/*     */     } else {
/*  99 */       if (lengthPrefix) {
/* 100 */         in.readLongs(in.readVarInt());
/*     */       }
/* 102 */       storage = null;
/*     */     } 
/*     */     
/* 105 */     return new DataPalette(palette, (BaseStorage)storage, paletteType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void write(NetStreamOutput out, DataPalette palette) {
/* 113 */     write(out, palette, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void write(NetStreamOutput out, DataPalette palette, boolean lengthPrefix) {
/* 121 */     if (palette.palette instanceof SingletonPalette) {
/* 122 */       out.writeByte(0);
/* 123 */       out.writeVarInt(palette.palette.idToState(0));
/* 124 */       if (lengthPrefix) {
/* 125 */         out.writeVarInt(0);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 130 */     out.writeByte(palette.storage.getBitsPerEntry());
/*     */     
/* 132 */     if (!(palette.palette instanceof GlobalPalette)) {
/* 133 */       int paletteLength = palette.palette.size();
/* 134 */       out.writeVarInt(paletteLength);
/* 135 */       for (int i = 0; i < paletteLength; i++) {
/* 136 */         out.writeVarInt(palette.palette.idToState(i));
/*     */       }
/*     */     } 
/*     */     
/* 140 */     long[] data = palette.storage.getData();
/* 141 */     if (lengthPrefix) {
/* 142 */       out.writeVarInt(data.length);
/*     */     }
/* 144 */     out.writeLongs(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static DataPalette readLegacy(NetStreamInput in) {
/* 152 */     int bitsPerEntry = Math.max(4, in.readByte() & 0xFF);
/* 153 */     Palette palette = readPalette(PaletteType.CHUNK, bitsPerEntry, in, false);
/* 154 */     LegacyFlexibleStorage legacyFlexibleStorage = new LegacyFlexibleStorage(bitsPerEntry, in.readLongs(in.readVarInt()));
/* 155 */     return new DataPalette(palette, (BaseStorage)legacyFlexibleStorage, PaletteType.CHUNK);
/*     */   }
/*     */   
/*     */   public int get(int x, int y, int z) {
/* 159 */     if (this.storage != null) {
/* 160 */       int id = this.storage.get(index(this.paletteType, x, y, z));
/* 161 */       return this.palette.idToState(id);
/*     */     } 
/* 163 */     return this.palette.idToState(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int set(int x, int y, int z, int state) {
/* 171 */     int id = this.palette.stateToId(state);
/* 172 */     if (id == -1) {
/* 173 */       resizeOneUp();
/* 174 */       id = this.palette.stateToId(state);
/*     */     } 
/*     */     
/* 177 */     if (this.storage != null) {
/* 178 */       int index = index(this.paletteType, x, y, z);
/* 179 */       int curr = this.storage.get(index);
/*     */       
/* 181 */       this.storage.set(index, id);
/* 182 */       return curr;
/*     */     } 
/*     */     
/* 185 */     return state;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   private static Palette readPalette(PaletteType paletteType, int bitsPerEntry, NetStreamInput in, boolean allowSingletonPalette) {
/* 196 */     if (bitsPerEntry == 0 && allowSingletonPalette)
/* 197 */       return new SingletonPalette(in); 
/* 198 */     if (bitsPerEntry <= paletteType.getMaxBitsPerEntryForList()) {
/*     */       
/* 200 */       int bits = paletteType.isForceMaxListPaletteSize() ? paletteType.getMaxBitsPerEntryForList() : bitsPerEntry;
/* 201 */       return new ListPalette(bits, in);
/* 202 */     }  if (bitsPerEntry <= paletteType.getMaxBitsPerEntryForMap()) {
/* 203 */       return new MapPalette(bitsPerEntry, in);
/*     */     }
/* 205 */     return GlobalPalette.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   private void resizeOneUp() {
/* 210 */     Palette oldPalette = this.palette;
/* 211 */     BaseStorage oldData = this.storage;
/*     */     
/* 213 */     int prevBitsPerEntry = (oldData != null) ? oldData.getBitsPerEntry() : 0;
/* 214 */     this.palette = createPalette(prevBitsPerEntry + 1, this.paletteType);
/* 215 */     this.storage = (BaseStorage)new BitStorage(this.palette.getBits(), this.paletteType.getStorageSize());
/*     */     
/* 217 */     if (oldData != null) {
/*     */       
/* 219 */       for (int i = 0, len = this.paletteType.getStorageSize(); i < len; i++) {
/* 220 */         this.storage.set(i, this.palette.stateToId(oldPalette.idToState(oldData.get(i))));
/*     */       }
/*     */     } else {
/* 223 */       this.palette.stateToId(oldPalette.idToState(0));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Palette createPalette(int bitsPerEntry, PaletteType paletteType) {
/* 228 */     if (bitsPerEntry <= paletteType.getMaxBitsPerEntryForList()) {
/* 229 */       int bits = paletteType.isForceMaxListPaletteSize() ? paletteType.getMaxBitsPerEntryForList() : bitsPerEntry;
/* 230 */       return new ListPalette(bits);
/* 231 */     }  if (bitsPerEntry <= paletteType.getMaxBitsPerEntryForMap()) {
/* 232 */       return new MapPalette(bitsPerEntry);
/*     */     }
/* 234 */     return GlobalPalette.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int index(PaletteType paletteType, int x, int y, int z) {
/* 239 */     return (y << paletteType.getBitShift() | z) << paletteType.getBitShift() | x;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\palette\DataPalette.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */