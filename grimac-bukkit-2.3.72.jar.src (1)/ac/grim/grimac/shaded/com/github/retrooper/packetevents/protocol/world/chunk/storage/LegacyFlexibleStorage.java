/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage;
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
/*     */ public class LegacyFlexibleStorage
/*     */   extends BaseStorage
/*     */ {
/*     */   private final long[] data;
/*     */   private final int bitsPerEntry;
/*     */   private final int size;
/*     */   private final long maxEntryValue;
/*     */   
/*     */   public LegacyFlexibleStorage(int bitsPerEntry, int size) {
/*  30 */     this(bitsPerEntry, new long[roundToNearest(size * bitsPerEntry, 64) / 64]);
/*     */   }
/*     */   
/*     */   public LegacyFlexibleStorage(int bitsPerEntry, long[] data) {
/*  34 */     if (bitsPerEntry < 4) {
/*  35 */       bitsPerEntry = 4;
/*     */     }
/*     */     
/*  38 */     this.bitsPerEntry = bitsPerEntry;
/*  39 */     this.data = data;
/*     */     
/*  41 */     this.size = this.data.length * 64 / this.bitsPerEntry;
/*  42 */     this.maxEntryValue = (1L << this.bitsPerEntry) - 1L;
/*     */   }
/*     */   
/*     */   private static int roundToNearest(int value, int roundTo) {
/*  46 */     if (roundTo == 0)
/*  47 */       return 0; 
/*  48 */     if (value == 0) {
/*  49 */       return roundTo;
/*     */     }
/*  51 */     if (value < 0) {
/*  52 */       roundTo *= -1;
/*     */     }
/*     */     
/*  55 */     int remainder = value % roundTo;
/*  56 */     return (remainder != 0) ? (value + roundTo - remainder) : value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int get(int index) {
/*  62 */     int bitIndex = index * this.bitsPerEntry;
/*  63 */     int startIndex = bitIndex / 64;
/*  64 */     int endIndex = ((index + 1) * this.bitsPerEntry - 1) / 64;
/*  65 */     int startBitSubIndex = bitIndex % 64;
/*  66 */     if (startIndex == endIndex) {
/*  67 */       return (int)(this.data[startIndex] >>> startBitSubIndex & this.maxEntryValue);
/*     */     }
/*  69 */     int endBitSubIndex = 64 - startBitSubIndex;
/*  70 */     return (int)((this.data[startIndex] >>> startBitSubIndex | this.data[endIndex] << endBitSubIndex) & this.maxEntryValue);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(int index, int value) {
/*  76 */     int bitIndex = index * this.bitsPerEntry;
/*  77 */     int startIndex = bitIndex / 64;
/*  78 */     int endIndex = ((index + 1) * this.bitsPerEntry - 1) / 64;
/*  79 */     int startBitSubIndex = bitIndex % 64;
/*  80 */     this.data[startIndex] = this.data[startIndex] & (this.maxEntryValue << startBitSubIndex ^ 0xFFFFFFFFFFFFFFFFL) | (value & this.maxEntryValue) << startBitSubIndex;
/*  81 */     if (startIndex != endIndex) {
/*  82 */       int endBitSubIndex = 64 - startBitSubIndex;
/*  83 */       int j1 = this.bitsPerEntry - endBitSubIndex;
/*  84 */       this.data[endIndex] = this.data[endIndex] >>> j1 << j1 | (value & this.maxEntryValue) >> endBitSubIndex;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] getData() {
/*  90 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBitsPerEntry() {
/*  95 */     return this.bitsPerEntry;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 100 */     return this.size;
/*     */   }
/*     */   
/*     */   public long getMaxEntryValue() {
/* 104 */     return this.maxEntryValue;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\storage\LegacyFlexibleStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */