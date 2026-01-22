/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import java.util.BitSet;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Internal
/*    */ public class ChunkBitMask
/*    */ {
/*    */   public static long[] readBitSetLongs(PacketWrapper<?> packet) {
/* 16 */     if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_17))
/*    */     {
/* 18 */       return packet.readLongArray(); } 
/* 19 */     if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9))
/*    */     {
/* 21 */       return new long[] { packet.readVarInt() };
/*    */     }
/*    */     
/* 24 */     return new long[] { packet.readUnsignedShort() };
/*    */   }
/*    */ 
/*    */   
/*    */   public static BitSet readChunkMask(PacketWrapper<?> packet) {
/* 29 */     return BitSet.valueOf(readBitSetLongs(packet));
/*    */   }
/*    */   
/*    */   public static void writeChunkMask(PacketWrapper<?> packet, BitSet chunkMask) {
/* 33 */     long[] longArray = chunkMask.toLongArray();
/*    */     
/* 35 */     if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
/*    */       
/* 37 */       packet.writeLongArray(longArray);
/* 38 */     } else if (packet.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/* 39 */       if (longArray.length > 0) {
/* 40 */         packet.writeVarInt((int)longArray[0]);
/*    */       } else {
/* 42 */         packet.writeVarInt(0);
/*    */       }
/*    */     
/* 45 */     } else if (longArray.length > 0) {
/* 46 */       packet.writeShort((int)longArray[0]);
/*    */     } else {
/* 48 */       packet.writeShort(0);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\ChunkBitMask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */