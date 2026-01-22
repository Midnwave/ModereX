/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_16.Chunk_v1_9;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_7.Chunk_v1_7;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v1_8.Chunk_v1_8;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.DataPalette;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.ListPalette;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.Palette;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.palette.PaletteType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.BaseStorage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk.storage.LegacyFlexibleStorage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
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
/*    */ public interface BaseChunk
/*    */ {
/*    */   int getBlockId(int paramInt1, int paramInt2, int paramInt3);
/*    */   
/*    */   default WrappedBlockState get(ClientVersion version, int x, int y, int z) {
/* 38 */     return get(version, x, y, z, true);
/*    */   }
/*    */   
/*    */   default WrappedBlockState get(ClientVersion version, int x, int y, int z, boolean clone) {
/* 42 */     return WrappedBlockState.getByGlobalId(version, getBlockId(x, y, z), clone);
/*    */   }
/*    */   
/*    */   default WrappedBlockState get(int x, int y, int z) {
/* 46 */     return get(x, y, z, true);
/*    */   }
/*    */   
/*    */   default WrappedBlockState get(int x, int y, int z, boolean clone) {
/* 50 */     return get(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), x, y, z, clone);
/*    */   }
/*    */   
/*    */   default void set(int x, int y, int z, WrappedBlockState state) {
/* 54 */     set(x, y, z, state.getGlobalId());
/*    */   }
/*    */ 
/*    */   
/*    */   void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*    */   
/*    */   default void set(ClientVersion version, int x, int y, int z, int combinedID) {
/* 61 */     set(x, y, z, combinedID);
/*    */   }
/*    */   
/*    */   boolean isEmpty();
/*    */   
/*    */   static BaseChunk create() {
/* 67 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/* 68 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_18))
/* 69 */       return (BaseChunk)new Chunk_v1_18(); 
/* 70 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_16))
/* 71 */       return (BaseChunk)new Chunk_v1_9(0, PaletteType.CHUNK.create()); 
/* 72 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_9))
/* 73 */       return (BaseChunk)new Chunk_v1_9(0, new DataPalette((Palette)new ListPalette(4), (BaseStorage)new LegacyFlexibleStorage(4, 4096), PaletteType.CHUNK)); 
/* 74 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_8)) {
/* 75 */       return (BaseChunk)new Chunk_v1_8(new ShortArray3d(4096), null, null);
/*    */     }
/* 77 */     return (BaseChunk)new Chunk_v1_7(false, true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\BaseChunk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */