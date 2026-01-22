/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_7;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.ByteArray3d;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NibbleArray3d;
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
/*    */ public class Chunk_v1_7
/*    */   implements BaseChunk
/*    */ {
/*    */   private final ByteArray3d blocks;
/*    */   private NibbleArray3d metadata;
/*    */   private NibbleArray3d blocklight;
/*    */   private NibbleArray3d skylight;
/*    */   private NibbleArray3d extendedBlocks;
/*    */   
/*    */   public Chunk_v1_7(boolean skylight, boolean extended) {
/* 33 */     this(new ByteArray3d(4096), new NibbleArray3d(4096), new NibbleArray3d(4096), skylight ? new NibbleArray3d(4096) : null, extended ? new NibbleArray3d(4096) : null);
/*    */   }
/*    */   
/*    */   public Chunk_v1_7(ByteArray3d blocks, NibbleArray3d metadata, NibbleArray3d blocklight, NibbleArray3d skylight, NibbleArray3d extendedBlocks) {
/* 37 */     this.blocks = blocks;
/* 38 */     this.metadata = metadata;
/* 39 */     this.blocklight = blocklight;
/* 40 */     this.skylight = skylight;
/* 41 */     this.extendedBlocks = extendedBlocks;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getBlockId(int x, int y, int z) {
/* 46 */     int blockId = (this.blocks.get(x, y, z) & 0xFF) << 4;
/* 47 */     blockId |= this.metadata.get(x, y, z) & 0xF;
/* 48 */     if (this.extendedBlocks != null) {
/* 49 */       blockId |= (this.extendedBlocks.get(x, y, z) & 0xF) << 8;
/*    */     }
/* 51 */     return blockId;
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(int x, int y, int z, int combinedID) {
/* 56 */     this.blocks.set(x, y, z, combinedID >> 4 & 0xFF);
/* 57 */     this.metadata.set(x, y, z, combinedID & 0xF);
/* 58 */     if (this.extendedBlocks != null) {
/* 59 */       this.extendedBlocks.set(x, y, z, combinedID >> 8 & 0xF);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 65 */     for (byte block : this.blocks.getData()) {
/* 66 */       if (block != 0) {
/* 67 */         return false;
/*    */       }
/*    */     } 
/* 70 */     return true;
/*    */   }
/*    */   
/*    */   public ByteArray3d getBlocks() {
/* 74 */     return this.blocks;
/*    */   }
/*    */   
/*    */   public NibbleArray3d getMetadata() {
/* 78 */     return this.metadata;
/*    */   }
/*    */   
/*    */   public NibbleArray3d getBlockLight() {
/* 82 */     return this.blocklight;
/*    */   }
/*    */   
/*    */   public NibbleArray3d getSkyLight() {
/* 86 */     return this.skylight;
/*    */   }
/*    */   
/*    */   public NibbleArray3d getExtendedBlocks() {
/* 90 */     return this.extendedBlocks;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\impl\v1_7\Chunk_v1_7.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */