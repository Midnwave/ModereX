/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.data;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.Color;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
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
/*     */ public class ParticleTrailData
/*     */   extends ParticleData
/*     */ {
/*     */   private static final int FALLBACK_DURATION = 25;
/*     */   private Vector3d target;
/*     */   private Color color;
/*     */   private int duration;
/*     */   
/*     */   public ParticleTrailData(Vector3d target, Color color) {
/*  41 */     this(target, color, 25);
/*     */   }
/*     */   
/*     */   public ParticleTrailData(Vector3d target, Color color, int duration) {
/*  45 */     this.target = target;
/*  46 */     this.color = color;
/*  47 */     this.duration = duration;
/*     */   }
/*     */   
/*     */   public static ParticleTrailData read(PacketWrapper<?> wrapper) {
/*  51 */     Vector3d target = Vector3d.read(wrapper);
/*  52 */     Color color = new Color(wrapper.readInt());
/*     */     
/*  54 */     int duration = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4) ? wrapper.readVarInt() : 25;
/*  55 */     return new ParticleTrailData(target, color, duration);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, ParticleTrailData data) {
/*  59 */     Vector3d.write(wrapper, data.target);
/*  60 */     wrapper.writeInt(data.color.asRGB());
/*  61 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_4)) {
/*  62 */       wrapper.writeVarInt(data.duration);
/*     */     }
/*     */   }
/*     */   
/*     */   public static ParticleTrailData decode(NBTCompound compound, ClientVersion version) {
/*  67 */     Vector3d target = Vector3d.decode(compound.getTagOrThrow("target"), version);
/*  68 */     Color color = Color.decode(compound.getTagOrThrow("color"), version);
/*  69 */     int duration = 25;
/*  70 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_21_4)) {
/*  71 */       duration = compound.getNumberTagOrThrow("duration").getAsInt();
/*     */     }
/*  73 */     return new ParticleTrailData(target, color, duration);
/*     */   }
/*     */   
/*     */   public static void encode(ParticleTrailData data, ClientVersion version, NBTCompound compound) {
/*  77 */     compound.setTag("target", Vector3d.encode(data.target, version));
/*  78 */     compound.setTag("color", Color.encode(data.color, version));
/*  79 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_21_4)) {
/*  80 */       compound.setTag("duration", (NBT)new NBTInt(data.duration));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  86 */     return false;
/*     */   }
/*     */   
/*     */   public Vector3d getTarget() {
/*  90 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(Vector3d target) {
/*  94 */     this.target = target;
/*     */   }
/*     */   
/*     */   public Color getColor() {
/*  98 */     return this.color;
/*     */   }
/*     */   
/*     */   public void setColor(Color color) {
/* 102 */     this.color = color;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDuration() {
/* 109 */     return this.duration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDuration(int duration) {
/* 116 */     this.duration = duration;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\particle\data\ParticleTrailData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */