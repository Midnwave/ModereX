/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ 
/*    */ public class ParticleShriekData extends ParticleData {
/*    */   private int delay;
/*    */   
/*    */   public ParticleShriekData(int delay) {
/* 13 */     this.delay = delay;
/*    */   }
/*    */   
/*    */   public int getDelay() {
/* 17 */     return this.delay;
/*    */   }
/*    */   
/*    */   public void setDelay(int delay) {
/* 21 */     this.delay = delay;
/*    */   }
/*    */   
/*    */   public static ParticleShriekData read(PacketWrapper<?> wrapper) {
/* 25 */     return new ParticleShriekData(wrapper.readVarInt());
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ParticleShriekData data) {
/* 29 */     wrapper.writeVarInt(data.getDelay());
/*    */   }
/*    */   
/*    */   public static ParticleShriekData decode(NBTCompound compound, ClientVersion version) {
/* 33 */     int delay = compound.getNumberTagOrThrow("delay").getAsInt();
/* 34 */     return new ParticleShriekData(delay);
/*    */   }
/*    */   
/*    */   public static void encode(ParticleShriekData data, ClientVersion version, NBTCompound compound) {
/* 38 */     compound.setTag("delay", (NBT)new NBTInt(data.delay));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 43 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleShriekData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */