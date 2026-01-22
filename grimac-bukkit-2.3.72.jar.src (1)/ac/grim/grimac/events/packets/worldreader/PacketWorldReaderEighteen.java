/*    */ package ac.grim.grimac.events.packets.worldreader;
/*    */ 
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.HeightmapType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.reader.impl.ChunkReader_v1_18;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ 
/*    */ public class PacketWorldReaderEighteen
/*    */   extends BasePacketWorldReader {
/* 16 */   private static final ChunkReader_v1_18 CHUNK_READER_V_1_18 = new ChunkReader_v1_18();
/* 17 */   private static final boolean PRE_1_21_5 = PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21_5);
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleMapChunk(GrimPlayer player, PacketSendEvent event) {
/* 22 */     PacketWrapper<?> wrapper = new PacketWrapper(event);
/*    */     
/* 24 */     int x = wrapper.readInt();
/* 25 */     int z = wrapper.readInt();
/*    */ 
/*    */     
/* 28 */     if (PRE_1_21_5) {
/* 29 */       wrapper.readNBT();
/*    */     } else {
/* 31 */       wrapper.readMap(HeightmapType::read, PacketWrapper::readLongArray);
/*    */     } 
/*    */     
/* 34 */     BaseChunk[] chunks = CHUNK_READER_V_1_18.read(DimensionTypes.OVERWORLD, null, null, true, false, false, event
/*    */         
/* 36 */         .getUser().getTotalWorldHeight() >> 4, wrapper
/* 37 */         .readVarInt(), wrapper);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 42 */     for (int i = 0; i < chunks.length; i++) {
/* 43 */       Chunk_v1_18 chunk = (Chunk_v1_18)chunks[i];
/* 44 */       if (chunk != null)
/*    */       {
/* 46 */         chunks[i] = (BaseChunk)new Chunk_v1_18(chunk.getBlockCount(), chunk.getChunkData(), null);
/*    */       }
/*    */     } 
/*    */     
/* 50 */     addChunkToCache(event, player, chunks, true, x, z);
/*    */     
/* 52 */     event.setLastUsedWrapper(null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\worldreader\PacketWorldReaderEighteen.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */