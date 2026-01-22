/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.builtin;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.PositionSource;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.positionsource.PositionSourceTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3i;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public class BlockPositionSource
/*    */   extends PositionSource
/*    */ {
/*    */   private Vector3i pos;
/*    */   
/*    */   public BlockPositionSource(Vector3i pos) {
/* 34 */     super(PositionSourceTypes.BLOCK);
/* 35 */     this.pos = pos;
/*    */   }
/*    */   
/*    */   public static BlockPositionSource read(PacketWrapper<?> wrapper) {
/* 39 */     return new BlockPositionSource(wrapper.readBlockPosition());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, BlockPositionSource source) {
/* 43 */     wrapper.writeBlockPosition(source.pos);
/*    */   }
/*    */   
/*    */   public static BlockPositionSource decodeSource(NBTCompound compound, ClientVersion version) {
/* 47 */     NBTIntArray arr = (NBTIntArray)compound.getTagOfTypeOrThrow("pos", NBTIntArray.class);
/* 48 */     return new BlockPositionSource(new Vector3i(arr.getValue()));
/*    */   }
/*    */   
/*    */   public static void encodeSource(BlockPositionSource source, ClientVersion version, NBTCompound compound) {
/* 52 */     compound.setTag("pos", (NBT)new NBTIntArray(new int[] { source.pos.x, source.pos.y, source.pos.z }));
/*    */   }
/*    */   
/*    */   public Vector3i getPos() {
/* 56 */     return this.pos;
/*    */   }
/*    */   
/*    */   public void setPos(Vector3i pos) {
/* 60 */     this.pos = pos;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\positionsource\builtin\BlockPositionSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */