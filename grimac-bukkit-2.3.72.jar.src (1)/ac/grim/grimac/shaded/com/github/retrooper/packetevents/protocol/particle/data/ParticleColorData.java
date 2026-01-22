/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.AlphaColor;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
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
/*    */ public class ParticleColorData
/*    */   extends ParticleData
/*    */ {
/*    */   private AlphaColor color;
/*    */   
/*    */   public ParticleColorData(int color) {
/* 33 */     this(new AlphaColor(color));
/*    */   }
/*    */   
/*    */   public ParticleColorData(AlphaColor color) {
/* 37 */     this.color = color;
/*    */   }
/*    */   
/*    */   public static ParticleColorData read(PacketWrapper<?> wrapper) {
/* 41 */     int color = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5) ? wrapper.readInt() : 0;
/* 42 */     return new ParticleColorData(color);
/*    */   }
/*    */   
/*    */   public static void write(PacketWrapper<?> wrapper, ParticleColorData data) {
/* 46 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/* 47 */       wrapper.writeInt(data.color.asRGB());
/*    */     }
/*    */   }
/*    */   
/*    */   public static ParticleColorData decode(NBTCompound compound, ClientVersion version) {
/*    */     AlphaColor argb;
/* 53 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 54 */       NBT colorTag = compound.getTagOrThrow("color");
/* 55 */       argb = AlphaColor.decode(colorTag, version);
/*    */     } else {
/*    */       
/* 58 */       argb = AlphaColor.WHITE;
/*    */     } 
/* 60 */     return new ParticleColorData(argb);
/*    */   }
/*    */   
/*    */   public static void encode(ParticleColorData data, ClientVersion version, NBTCompound compound) {
/* 64 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_5)) {
/* 65 */       compound.setTag("color", AlphaColor.encode(data.color, version));
/*    */     }
/*    */   }
/*    */   
/*    */   public int getColor() {
/* 70 */     return this.color.asRGB();
/*    */   }
/*    */   
/*    */   public void setColor(int color) {
/* 74 */     this.color = new AlphaColor(color);
/*    */   }
/*    */   
/*    */   public void setAlphaColor(AlphaColor color) {
/* 78 */     this.color = color;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 83 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleColorData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */