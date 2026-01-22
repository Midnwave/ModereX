/*     */ package ac.grim.grimac.events.packets.worldreader;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.ListPalette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.Palette;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.PaletteType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BaseStorage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BitStorage;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import java.util.BitSet;
/*     */ 
/*     */ public class PacketWorldReaderEight extends BasePacketWorldReader {
/*     */   public void handleMapChunkBulk(GrimPlayer player, PacketSendEvent event) {
/*  18 */     PacketWrapper<?> wrapper = new PacketWrapper(event);
/*  19 */     ByteBuf buffer = (ByteBuf)wrapper.getBuffer();
/*     */     
/*  21 */     boolean skylight = wrapper.readBoolean();
/*  22 */     int columns = wrapper.readVarInt();
/*  23 */     int[] x = new int[columns];
/*  24 */     int[] z = new int[columns];
/*  25 */     int[] mask = new int[columns];
/*     */     int column;
/*  27 */     for (column = 0; column < columns; column++) {
/*  28 */       x[column] = wrapper.readInt();
/*  29 */       z[column] = wrapper.readInt();
/*  30 */       mask[column] = wrapper.readUnsignedShort();
/*     */     } 
/*     */     
/*  33 */     for (column = 0; column < columns; column++) {
/*  34 */       BitSet bitset = BitSet.valueOf(new long[] { mask[column] });
/*  35 */       Chunk_v1_9[] chunkSections = new Chunk_v1_9[16];
/*  36 */       readChunk(buffer, chunkSections, bitset);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  41 */       int chunks = Integer.bitCount(mask[column]);
/*  42 */       buffer.readerIndex(buffer.readerIndex() + 256 + chunks * 2048 + (skylight ? (chunks * 2048) : 0));
/*     */       
/*  44 */       addChunkToCache(event, player, (BaseChunk[])chunkSections, true, x[column], z[column]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleMapChunk(GrimPlayer player, PacketSendEvent event) {
/*  50 */     PacketWrapper<?> wrapper = new PacketWrapper(event);
/*     */     
/*  52 */     int chunkX = wrapper.readInt();
/*  53 */     int chunkZ = wrapper.readInt();
/*  54 */     boolean groundUp = wrapper.readBoolean();
/*     */     
/*  56 */     BitSet mask = BitSet.valueOf(new long[] { wrapper.readUnsignedShort() });
/*  57 */     int size = wrapper.readVarInt();
/*     */     
/*  59 */     Chunk_v1_9[] chunks = new Chunk_v1_9[16];
/*  60 */     readChunk((ByteBuf)event.getByteBuf(), chunks, mask);
/*     */     
/*  62 */     addChunkToCache(event, player, (BaseChunk[])chunks, groundUp, chunkX, chunkZ);
/*     */     
/*  64 */     event.setLastUsedWrapper(null);
/*     */   }
/*     */   
/*     */   private void readChunk(ByteBuf buf, Chunk_v1_9[] chunks, BitSet set) {
/*  68 */     for (int ind = 0; ind < 16; ind++) {
/*  69 */       if (set.get(ind)) {
/*  70 */         chunks[ind] = readChunk(buf);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public Chunk_v1_9 readChunk(ByteBuf in) {
/*  76 */     ListPalette palette = new ListPalette(4);
/*  77 */     BitStorage storage = new BitStorage(4, 4096);
/*  78 */     DataPalette dataPalette = new DataPalette((Palette)palette, (BaseStorage)storage, PaletteType.CHUNK);
/*     */     
/*  80 */     palette.stateToId(0);
/*     */     
/*  82 */     int lastNext = -1;
/*  83 */     int lastID = -1;
/*  84 */     int blockCount = 0;
/*     */     
/*  86 */     for (int i = 0; i < 4096; i++) {
/*  87 */       int next = in.readShort();
/*     */       
/*  89 */       if (next != 0) {
/*  90 */         blockCount++;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 100 */       if (next != lastNext) {
/* 101 */         lastNext = next;
/* 102 */         next = (short)((next & 0xFF00) >> 8 | next << 8);
/* 103 */         dataPalette.set(i & 0xF, i >> 8 & 0xF, i >> 4 & 0xF, next);
/* 104 */         lastID = dataPalette.storage.get(i);
/*     */       }
/*     */       else {
/*     */         
/* 108 */         dataPalette.storage.set(i, lastID);
/*     */       } 
/*     */     } 
/* 111 */     return new Chunk_v1_9(blockCount, dataPalette);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\worldreader\PacketWorldReaderEight.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */