/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.NibbleArray3d;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.ShortArray3d;
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
/*    */ 
/*    */ public class Chunk_v1_8
/*    */   implements BaseChunk
/*    */ {
/*    */   private ShortArray3d blocks;
/*    */   private NibbleArray3d blocklight;
/*    */   private NibbleArray3d skylight;
/*    */   
/*    */   public Chunk_v1_8(boolean skylight) {
/* 33 */     this(new ShortArray3d(4096), new NibbleArray3d(4096), skylight ? new NibbleArray3d(4096) : null);
/*    */   }
/*    */   
/*    */   public Chunk_v1_8(ShortArray3d blocks, NibbleArray3d blocklight, NibbleArray3d skylight) {
/* 37 */     this.blocks = blocks;
/* 38 */     this.blocklight = blocklight;
/* 39 */     this.skylight = skylight;
/*    */   }
/*    */   
/*    */   public ShortArray3d getBlocks() {
/* 43 */     return this.blocks;
/*    */   }
/*    */   
/*    */   public NibbleArray3d getBlockLight() {
/* 47 */     return this.blocklight;
/*    */   }
/*    */   
/*    */   public NibbleArray3d getSkyLight() {
/* 51 */     return this.skylight;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getBlockId(int x, int y, int z) {
/* 56 */     return this.blocks.get(x, y, z);
/*    */   }
/*    */ 
/*    */   
/*    */   public void set(int x, int y, int z, int combinedID) {
/* 61 */     this.blocks.set(x, y, z, combinedID);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 66 */     for (short block : this.blocks.getData()) {
/* 67 */       if (block != 0) {
/* 68 */         return false;
/*    */       }
/*    */     } 
/*    */     
/* 72 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\impl\v1_8\Chunk_v1_8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */