/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.Color;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3f;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParticleDustColorTransitionData
/*     */   extends ParticleData
/*     */ {
/*     */   private float scale;
/*     */   private Color start;
/*     */   private Color end;
/*     */   
/*     */   public ParticleDustColorTransitionData(float scale, float startRed, float startGreen, float startBlue, float endRed, float endGreen, float endBlue) {
/*  40 */     this(scale, new Color(startRed, startGreen, startBlue), new Color(endRed, endGreen, endBlue));
/*     */   }
/*     */ 
/*     */   
/*     */   public ParticleDustColorTransitionData(float scale, float[] startRGB, float[] endRGB) {
/*  45 */     this(scale, startRGB[0], startRGB[1], startRGB[2], endRGB[0], endRGB[1], endRGB[2]);
/*     */   }
/*     */ 
/*     */   
/*     */   public ParticleDustColorTransitionData(float scale, Vector3f startRGB, Vector3f endRGB) {
/*  50 */     this(scale, startRGB.getX(), startRGB.getY(), startRGB.getZ(), endRGB
/*  51 */         .getX(), endRGB.getY(), endRGB.getZ());
/*     */   }
/*     */   
/*     */   public ParticleDustColorTransitionData(float scale, Color start, Color end) {
/*  55 */     this.scale = scale;
/*  56 */     this.start = start;
/*  57 */     this.end = end;
/*     */   }
/*     */   
/*     */   public static ParticleDustColorTransitionData read(PacketWrapper<?> wrapper) {
/*     */     Color start, end;
/*  62 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  63 */       start = new Color(wrapper.readInt());
/*     */     } else {
/*  65 */       float startRed = wrapper.readFloat();
/*  66 */       float startGreen = wrapper.readFloat();
/*  67 */       float startBlue = wrapper.readFloat();
/*  68 */       start = new Color(startRed, startGreen, startBlue);
/*     */     } 
/*  70 */     float scale = 0.0F;
/*  71 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_5)) {
/*  72 */       scale = wrapper.readFloat();
/*     */     }
/*     */     
/*  75 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  76 */       end = new Color(wrapper.readInt());
/*     */     } else {
/*  78 */       float endRed = wrapper.readFloat();
/*  79 */       float endGreen = wrapper.readFloat();
/*  80 */       float endBlue = wrapper.readFloat();
/*  81 */       end = new Color(endRed, endGreen, endBlue);
/*     */     } 
/*  83 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  84 */       scale = wrapper.readFloat();
/*     */     }
/*  86 */     return new ParticleDustColorTransitionData(scale, start, end);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ParticleDustColorTransitionData data) {
/*  90 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  91 */       wrapper.writeInt(data.getStart().asRGB());
/*     */     } else {
/*  93 */       wrapper.writeFloat(data.getStartRed());
/*  94 */       wrapper.writeFloat(data.getStartGreen());
/*  95 */       wrapper.writeFloat(data.getStartBlue());
/*     */     } 
/*  97 */     if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_20_5)) {
/*  98 */       wrapper.writeFloat(data.getScale());
/*     */     }
/* 100 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/* 101 */       wrapper.writeInt(data.getEnd().asRGB());
/*     */     } else {
/* 103 */       wrapper.writeFloat(data.getEndRed());
/* 104 */       wrapper.writeFloat(data.getEndGreen());
/* 105 */       wrapper.writeFloat(data.getEndBlue());
/*     */     } 
/* 107 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/* 108 */       wrapper.writeFloat(data.getScale());
/*     */     }
/*     */   }
/*     */   
/*     */   public static ParticleDustColorTransitionData decode(NBTCompound compound, ClientVersion version) {
/* 113 */     String fromColorKey = "from_color";
/* 114 */     String toColorKey = "to_color";
/* 115 */     if (version.isOlderThan(ClientVersion.V_1_20_5)) {
/* 116 */       fromColorKey = "fromColor";
/* 117 */       toColorKey = "toColor";
/*     */     } 
/* 119 */     Color fromColor = Color.decode(compound.getTagOrThrow(fromColorKey), version);
/* 120 */     Color toColor = Color.decode(compound.getTagOrThrow(toColorKey), version);
/* 121 */     float scale = compound.getNumberTagOrThrow("scale").getAsFloat();
/* 122 */     return new ParticleDustColorTransitionData(scale, fromColor, toColor);
/*     */   }
/*     */   
/*     */   public static void encode(ParticleDustColorTransitionData data, ClientVersion version, NBTCompound compound) {
/* 126 */     String fromColorKey = "from_color";
/* 127 */     String toColorKey = "to_color";
/* 128 */     if (version.isOlderThan(ClientVersion.V_1_20_5)) {
/* 129 */       fromColorKey = "fromColor";
/* 130 */       toColorKey = "toColor";
/*     */     } 
/* 132 */     compound.setTag(fromColorKey, Color.encode(data.start, version));
/* 133 */     compound.setTag(toColorKey, Color.encode(data.end, version));
/* 134 */     compound.setTag("scale", (NBT)new NBTFloat(data.scale));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 139 */     return false;
/*     */   }
/*     */   
/*     */   public float getStartRed() {
/* 143 */     return this.start.red() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setStartRed(float startRed) {
/* 147 */     this.start = new Color(startRed, getStartGreen(), getStartBlue());
/*     */   }
/*     */   
/*     */   public float getStartGreen() {
/* 151 */     return this.start.green() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setStartGreen(float startGreen) {
/* 155 */     this.start = new Color(getStartRed(), startGreen, getStartBlue());
/*     */   }
/*     */   
/*     */   public float getStartBlue() {
/* 159 */     return this.start.blue() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setStartBlue(float startBlue) {
/* 163 */     this.start = new Color(getStartRed(), getStartGreen(), startBlue);
/*     */   }
/*     */   
/*     */   public float getEndRed() {
/* 167 */     return this.end.red() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setEndRed(float endRed) {
/* 171 */     this.end = new Color(endRed, getEndGreen(), getEndBlue());
/*     */   }
/*     */   
/*     */   public float getEndGreen() {
/* 175 */     return this.end.green() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setEndGreen(float endGreen) {
/* 179 */     this.end = new Color(getEndRed(), endGreen, getEndBlue());
/*     */   }
/*     */   
/*     */   public float getEndBlue() {
/* 183 */     return this.end.blue() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setEndBlue(float endBlue) {
/* 187 */     this.end = new Color(getEndRed(), getEndGreen(), endBlue);
/*     */   }
/*     */   
/*     */   public float getScale() {
/* 191 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 195 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public Color getStart() {
/* 199 */     return this.start;
/*     */   }
/*     */   
/*     */   public void setStart(Color start) {
/* 203 */     this.start = start;
/*     */   }
/*     */   
/*     */   public Color getEnd() {
/* 207 */     return this.end;
/*     */   }
/*     */   
/*     */   public void setEnd(Color end) {
/* 211 */     this.end = end;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleDustColorTransitionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */