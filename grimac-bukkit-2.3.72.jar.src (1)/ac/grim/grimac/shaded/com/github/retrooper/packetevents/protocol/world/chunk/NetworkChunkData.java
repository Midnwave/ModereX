/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
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
/*    */ public class NetworkChunkData
/*    */ {
/*    */   private final int mask;
/*    */   private int extendedChunkMask;
/*    */   private final boolean fullChunk;
/*    */   private final boolean sky;
/*    */   private byte[] data;
/*    */   
/*    */   public NetworkChunkData(int mask, boolean fullChunk, boolean sky, byte[] data) {
/* 29 */     this.mask = mask;
/* 30 */     this.fullChunk = fullChunk;
/* 31 */     this.sky = sky;
/* 32 */     this.data = data;
/*    */   }
/*    */   
/*    */   public NetworkChunkData(int chunkMask, int extendedChunkMask, boolean fullChunk, boolean sky, byte[] data) {
/* 36 */     this(chunkMask, fullChunk, sky, data);
/* 37 */     this.extendedChunkMask = extendedChunkMask;
/*    */   }
/*    */   
/*    */   public int getMask() {
/* 41 */     return this.mask;
/*    */   }
/*    */   
/*    */   public int getExtendedChunkMask() {
/* 45 */     return this.extendedChunkMask;
/*    */   }
/*    */   
/*    */   public boolean isFullChunk() {
/* 49 */     return this.fullChunk;
/*    */   }
/*    */   
/*    */   public boolean hasSkyLight() {
/* 53 */     return this.sky;
/*    */   }
/*    */   
/*    */   public byte[] getData() {
/* 57 */     return this.data;
/*    */   }
/*    */   
/*    */   public void setData(byte[] data) {
/* 61 */     this.data = data;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\NetworkChunkData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */