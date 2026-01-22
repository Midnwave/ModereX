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
/*     */ public class ParticleDustData
/*     */   extends ParticleData
/*     */ {
/*     */   private float scale;
/*     */   private Color color;
/*     */   
/*     */   public ParticleDustData(float scale, float red, float green, float blue) {
/*  35 */     this(scale, new Color(red, green, blue));
/*     */   }
/*     */   
/*     */   public ParticleDustData(float scale, float[] rgb) {
/*  39 */     this(scale, rgb[0], rgb[1], rgb[2]);
/*     */   }
/*     */   
/*     */   public ParticleDustData(float scale, Vector3f rgb) {
/*  43 */     this(scale, rgb.getX(), rgb.getY(), rgb.getZ());
/*     */   }
/*     */   
/*     */   public ParticleDustData(float scale, int red, int green, int blue) {
/*  47 */     this(scale, new Color(red, green, blue));
/*     */   }
/*     */   
/*     */   public ParticleDustData(float scale, Color color) {
/*  51 */     this.scale = scale;
/*  52 */     this.color = color;
/*     */   }
/*     */   
/*     */   public static ParticleDustData read(PacketWrapper<?> wrapper) {
/*     */     Color color;
/*  57 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  58 */       color = new Color(wrapper.readInt());
/*     */     } else {
/*  60 */       float red = wrapper.readFloat();
/*  61 */       float green = wrapper.readFloat();
/*  62 */       float blue = wrapper.readFloat();
/*  63 */       color = new Color(red, green, blue);
/*     */     } 
/*  65 */     float scale = wrapper.readFloat();
/*  66 */     return new ParticleDustData(scale, color);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ParticleDustData data) {
/*  70 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2)) {
/*  71 */       wrapper.writeInt(data.color.asRGB());
/*     */     } else {
/*  73 */       wrapper.writeFloat(data.getRed());
/*  74 */       wrapper.writeFloat(data.getGreen());
/*  75 */       wrapper.writeFloat(data.getBlue());
/*     */     } 
/*  77 */     wrapper.writeFloat(data.scale);
/*     */   }
/*     */   
/*     */   public static ParticleDustData decode(NBTCompound compound, ClientVersion version) {
/*  81 */     Color color = Color.decode(compound.getTagOrThrow("color"), version);
/*  82 */     float scale = compound.getNumberTagOrThrow("scale").getAsFloat();
/*  83 */     return new ParticleDustData(scale, color);
/*     */   }
/*     */   
/*     */   public static void encode(ParticleDustData data, ClientVersion version, NBTCompound compound) {
/*  87 */     compound.setTag("color", Color.encode(data.color, version));
/*  88 */     compound.setTag("scale", (NBT)new NBTFloat(data.scale));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  93 */     return false;
/*     */   }
/*     */   
/*     */   public float getRed() {
/*  97 */     return this.color.red() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setRed(float red) {
/* 101 */     this.color = new Color(red, getGreen(), getBlue());
/*     */   }
/*     */   
/*     */   public float getGreen() {
/* 105 */     return this.color.green() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setGreen(float green) {
/* 109 */     this.color = new Color(getRed(), green, getBlue());
/*     */   }
/*     */   
/*     */   public float getBlue() {
/* 113 */     return this.color.blue() / 255.0F;
/*     */   }
/*     */   
/*     */   public void setBlue(float blue) {
/* 117 */     this.color = new Color(getRed(), getGreen(), blue);
/*     */   }
/*     */   
/*     */   public float getScale() {
/* 121 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 125 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public Color getColor() {
/* 129 */     return this.color;
/*     */   }
/*     */   
/*     */   public void setColor(Color color) {
/* 133 */     this.color = color;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleDustData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */