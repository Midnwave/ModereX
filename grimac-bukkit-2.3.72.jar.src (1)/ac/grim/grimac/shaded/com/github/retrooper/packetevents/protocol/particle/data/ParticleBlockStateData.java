/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
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
/*    */ public class ParticleBlockStateData
/*    */   extends ParticleData
/*    */   implements LegacyConvertible
/*    */ {
/*    */   private WrappedBlockState blockState;
/*    */   
/*    */   public ParticleBlockStateData(WrappedBlockState blockState) {
/* 32 */     this.blockState = blockState;
/*    */   }
/*    */   
/*    */   public WrappedBlockState getBlockState() {
/* 36 */     return this.blockState;
/*    */   }
/*    */   
/*    */   public void setBlockState(WrappedBlockState blockState) {
/* 40 */     this.blockState = blockState;
/*    */   }
/*    */   
/*    */   public static ParticleBlockStateData read(PacketWrapper<?> wrapper) {
/*    */     int blockID;
/* 45 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/* 46 */       blockID = wrapper.readVarInt();
/* 47 */     } else if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 48 */       blockID = wrapper.readInt();
/*    */     } else {
/* 50 */       blockID = wrapper.readVarInt();
/*    */     } 
/* 52 */     return new ParticleBlockStateData(WrappedBlockState.getByGlobalId(wrapper.getServerVersion()
/* 53 */           .toClientVersion(), blockID));
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ParticleBlockStateData data) {
/* 57 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/* 58 */       wrapper.writeVarInt(data.getBlockState().getGlobalId());
/*    */     } else {
/* 60 */       wrapper.writeInt(data.getBlockState().getGlobalId());
/*    */     } 
/*    */   }
/*    */   
/*    */   public static ParticleBlockStateData decode(NBTCompound compound, ClientVersion version) {
/* 65 */     String key = version.isNewerThanOrEquals(ClientVersion.V_1_20_5) ? "block_state" : "value";
/* 66 */     WrappedBlockState state = WrappedBlockState.decode(compound.getTagOrThrow(key), version);
/* 67 */     return new ParticleBlockStateData(state);
/*    */   }
/*    */   
/*    */   public static void encode(ParticleBlockStateData data, ClientVersion version, NBTCompound compound) {
/* 71 */     String key = version.isNewerThanOrEquals(ClientVersion.V_1_20_5) ? "block_state" : "value";
/* 72 */     compound.setTag(key, WrappedBlockState.encode(data.blockState, version));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 77 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public LegacyParticleData toLegacy(ClientVersion version) {
/* 82 */     return LegacyParticleData.ofOne(this.blockState.getGlobalId());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleBlockStateData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */