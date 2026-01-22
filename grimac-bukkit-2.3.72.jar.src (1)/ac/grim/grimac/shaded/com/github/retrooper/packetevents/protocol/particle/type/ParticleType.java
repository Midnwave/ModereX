/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import java.util.function.BiConsumer;
/*    */ import java.util.function.Function;
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
/*    */ public interface ParticleType<T extends ParticleData>
/*    */   extends MappedEntity
/*    */ {
/*    */   T readData(PacketWrapper<?> paramPacketWrapper);
/*    */   
/*    */   void writeData(PacketWrapper<?> paramPacketWrapper, T paramT);
/*    */   
/*    */   T decodeData(NBTCompound paramNBTCompound, ClientVersion paramClientVersion);
/*    */   
/*    */   void encodeData(T paramT, ClientVersion paramClientVersion, NBTCompound paramNBTCompound);
/*    */   
/*    */   @Deprecated
/*    */   default Function<PacketWrapper<?>, ParticleData> readDataFunction() {
/* 42 */     return this::readData;
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   default BiConsumer<PacketWrapper<?>, ParticleData> writeDataFunction() {
/* 48 */     return (wrapper, data) -> writeData(wrapper, (T)data);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\type\ParticleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */