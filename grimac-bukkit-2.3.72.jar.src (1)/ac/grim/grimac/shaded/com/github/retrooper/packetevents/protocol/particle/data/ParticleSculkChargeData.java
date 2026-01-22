/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ 
/*    */ public class ParticleSculkChargeData extends ParticleData {
/*    */   private float roll;
/*    */   
/*    */   public ParticleSculkChargeData(float roll) {
/* 13 */     this.roll = roll;
/*    */   }
/*    */   
/*    */   public float getRoll() {
/* 17 */     return this.roll;
/*    */   }
/*    */   
/*    */   public void setRoll(float roll) {
/* 21 */     this.roll = roll;
/*    */   }
/*    */   
/*    */   public static ParticleSculkChargeData read(PacketWrapper<?> wrapper) {
/* 25 */     return new ParticleSculkChargeData(wrapper.readFloat());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ParticleSculkChargeData data) {
/* 29 */     wrapper.writeFloat(data.getRoll());
/*    */   }
/*    */   
/*    */   public static ParticleSculkChargeData decode(NBTCompound compound, ClientVersion version) {
/* 33 */     float roll = compound.getNumberTagOrThrow("roll").getAsFloat();
/* 34 */     return new ParticleSculkChargeData(roll);
/*    */   }
/*    */   
/*    */   public static void encode(ParticleSculkChargeData data, ClientVersion version, NBTCompound compound) {
/* 38 */     compound.setTag("roll", (NBT)new NBTFloat(data.roll));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 43 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleSculkChargeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */