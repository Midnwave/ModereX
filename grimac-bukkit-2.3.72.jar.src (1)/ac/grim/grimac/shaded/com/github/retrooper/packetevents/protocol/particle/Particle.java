/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data.ParticleData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
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
/*    */ 
/*    */ public class Particle<T extends ParticleData>
/*    */ {
/*    */   private ParticleType<T> type;
/*    */   private T data;
/*    */   
/*    */   public Particle(ParticleType<T> type, T data) {
/* 37 */     this.type = type;
/* 38 */     this.data = data;
/*    */   }
/*    */   
/*    */   public Particle(ParticleType<T> type) {
/* 42 */     this(type, (T)ParticleData.emptyData());
/*    */   }
/*    */ 
/*    */   
/*    */   public static Particle<?> read(PacketWrapper<?> wrapper) {
/* 47 */     ParticleType<?> type = (ParticleType)wrapper.readMappedEntity(ParticleTypes::getById);
/* 48 */     return new Particle(type, type.readData(wrapper));
/*    */   }
/*    */   
/*    */   public static <T extends ParticleData> void write(PacketWrapper<?> wrapper, Particle<T> particle) {
/* 52 */     wrapper.writeMappedEntity((MappedEntity)particle.type);
/* 53 */     particle.getType().writeData(wrapper, (ParticleData)particle.data);
/*    */   }
/*    */ 
/*    */   
/*    */   public static Particle<?> decode(NBT nbt, ClientVersion version) {
/* 58 */     NBTCompound compound = (NBTCompound)nbt;
/* 59 */     NBT typeTag = compound.getTagOrThrow("type");
/*    */ 
/*    */     
/* 62 */     ParticleType<?> type = (typeTag instanceof NBTNumber) ? ParticleTypes.getById(version, ((NBTNumber)typeTag).getAsInt()) : ParticleTypes.getByName(((NBTString)typeTag).getValue());
/* 63 */     ParticleData data = type.decodeData(compound, version);
/* 64 */     return new Particle(type, data);
/*    */   }
/*    */   
/*    */   public static <T extends ParticleData> NBT encode(Particle<T> particle, ClientVersion version) {
/* 68 */     NBTCompound compound = new NBTCompound();
/* 69 */     compound.setTag("type", (NBT)new NBTString(particle.type.getName().toString()));
/* 70 */     particle.type.encodeData((ParticleData)particle.getData(), version, compound);
/* 71 */     return (NBT)compound;
/*    */   }
/*    */   
/*    */   public ParticleType<T> getType() {
/* 75 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(ParticleType<T> type) {
/* 79 */     this.type = type;
/*    */   }
/*    */   
/*    */   public T getData() {
/* 83 */     return this.data;
/*    */   }
/*    */   
/*    */   public void setData(T data) {
/* 87 */     this.data = data;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\Particle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */