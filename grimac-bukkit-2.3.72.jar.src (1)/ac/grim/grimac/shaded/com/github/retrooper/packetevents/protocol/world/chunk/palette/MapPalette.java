/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.stream.NetStreamInput;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MapPalette
/*     */   implements Palette
/*     */ {
/*     */   private final int bits;
/*     */   private final int[] idToState;
/*  40 */   private final HashMap<Object, Integer> stateToId = new HashMap<>();
/*  41 */   private int nextId = 0;
/*     */   
/*     */   public MapPalette(int bitsPerEntry) {
/*  44 */     this.bits = bitsPerEntry;
/*  45 */     this.idToState = new int[1 << bitsPerEntry];
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public MapPalette(int bitsPerEntry, NetStreamInput in) {
/*  50 */     this(bitsPerEntry);
/*     */     
/*  52 */     int paletteLength = in.readVarInt();
/*  53 */     for (int i = 0; i < paletteLength; i++) {
/*  54 */       int state = in.readVarInt();
/*  55 */       this.idToState[i] = state;
/*  56 */       this.stateToId.putIfAbsent(Integer.valueOf(state), Integer.valueOf(i));
/*     */     } 
/*  58 */     this.nextId = paletteLength;
/*     */   }
/*     */   
/*     */   public MapPalette(int bitsPerEntry, PacketWrapper<?> wrapper) {
/*  62 */     this(bitsPerEntry);
/*     */     
/*  64 */     int paletteLength = wrapper.readVarInt();
/*  65 */     for (int i = 0; i < paletteLength; i++) {
/*  66 */       int state = wrapper.readVarInt();
/*  67 */       this.idToState[i] = state;
/*  68 */       this.stateToId.putIfAbsent(Integer.valueOf(state), Integer.valueOf(i));
/*     */     } 
/*  70 */     this.nextId = paletteLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  75 */     return this.nextId;
/*     */   }
/*     */ 
/*     */   
/*     */   public int stateToId(int state) {
/*  80 */     Integer id = this.stateToId.get(Integer.valueOf(state));
/*  81 */     if (id == null && size() < this.idToState.length) {
/*  82 */       id = Integer.valueOf(this.nextId++);
/*  83 */       this.idToState[id.intValue()] = state;
/*  84 */       this.stateToId.put(Integer.valueOf(state), id);
/*     */     } 
/*     */     
/*  87 */     if (id != null) {
/*  88 */       return id.intValue();
/*     */     }
/*  90 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int idToState(int id) {
/*  96 */     if (id >= 0 && id < size()) {
/*  97 */       return this.idToState[id];
/*     */     }
/*  99 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBits() {
/* 105 */     return this.bits;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\palette\MapPalette.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */