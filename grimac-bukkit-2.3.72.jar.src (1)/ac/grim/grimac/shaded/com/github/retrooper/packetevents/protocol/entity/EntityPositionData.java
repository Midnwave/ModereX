/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ public final class EntityPositionData
/*     */ {
/*     */   private Vector3d position;
/*     */   private Vector3d deltaMovement;
/*     */   private float yaw;
/*     */   private float pitch;
/*     */   
/*     */   public EntityPositionData(Vector3d position, Vector3d deltaMovement, float yaw, float pitch) {
/*  36 */     this.position = position;
/*  37 */     this.deltaMovement = deltaMovement;
/*  38 */     this.yaw = yaw;
/*  39 */     this.pitch = pitch;
/*     */   }
/*     */   
/*     */   public static EntityPositionData read(PacketWrapper<?> wrapper) {
/*  43 */     Vector3d position = Vector3d.read(wrapper);
/*  44 */     Vector3d deltaMovement = Vector3d.read(wrapper);
/*  45 */     float yaw = wrapper.readFloat();
/*  46 */     float pitch = wrapper.readFloat();
/*  47 */     return new EntityPositionData(position, deltaMovement, yaw, pitch);
/*     */   }
/*     */   
/*     */   public static void write(PacketWrapper<?> wrapper, EntityPositionData positionData) {
/*  51 */     Vector3d.write(wrapper, positionData.position);
/*  52 */     Vector3d.write(wrapper, positionData.deltaMovement);
/*  53 */     wrapper.writeFloat(positionData.yaw);
/*  54 */     wrapper.writeFloat(positionData.pitch);
/*     */   }
/*     */   
/*     */   public Vector3d getPosition() {
/*  58 */     return this.position;
/*     */   }
/*     */   
/*     */   public void setPosition(Vector3d position) {
/*  62 */     this.position = position;
/*     */   }
/*     */   
/*     */   public Vector3d getDeltaMovement() {
/*  66 */     return this.deltaMovement;
/*     */   }
/*     */   
/*     */   public void setDeltaMovement(Vector3d deltaMovement) {
/*  70 */     this.deltaMovement = deltaMovement;
/*     */   }
/*     */   
/*     */   public float getYaw() {
/*  74 */     return this.yaw;
/*     */   }
/*     */   
/*     */   public void setYaw(float yaw) {
/*  78 */     this.yaw = yaw;
/*     */   }
/*     */   
/*     */   public float getPitch() {
/*  82 */     return this.pitch;
/*     */   }
/*     */   
/*     */   public void setPitch(float pitch) {
/*  86 */     this.pitch = pitch;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  91 */     if (this == obj) return true; 
/*  92 */     if (!(obj instanceof EntityPositionData)) return false; 
/*  93 */     EntityPositionData that = (EntityPositionData)obj;
/*  94 */     if (Float.compare(that.yaw, this.yaw) != 0) return false; 
/*  95 */     if (Float.compare(that.pitch, this.pitch) != 0) return false; 
/*  96 */     if (!this.position.equals(that.position)) return false; 
/*  97 */     return this.deltaMovement.equals(that.deltaMovement);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 102 */     return Objects.hash(new Object[] { this.position, this.deltaMovement, Float.valueOf(this.yaw), Float.valueOf(this.pitch) });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 107 */     return "EntityPositionData{position=" + this.position + ", deltaMovement=" + this.deltaMovement + ", yaw=" + this.yaw + ", pitch=" + this.pitch + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\entity\EntityPositionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */