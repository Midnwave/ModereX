/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.chunk;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
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
/*     */ 
/*     */ public class TileEntity
/*     */ {
/*     */   byte packedByte;
/*     */   short y;
/*     */   int type;
/*     */   NBTCompound data;
/*     */   
/*     */   public TileEntity(NBTCompound data) {
/*  39 */     this.data = data;
/*     */   }
/*     */   
/*     */   public TileEntity(byte packedByte, short y, int type, NBTCompound data) {
/*  43 */     this.packedByte = packedByte;
/*  44 */     this.y = y;
/*  45 */     this.type = type;
/*  46 */     this.data = data;
/*     */   }
/*     */   
/*     */   public int getX() {
/*  50 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/*  51 */       return (this.packedByte & 0xF0) >> 4;
/*     */     }
/*  53 */     return ((NBTInt)this.data.getTagOfTypeOrNull("x", NBTInt.class)).getAsInt();
/*     */   }
/*     */   
/*     */   public int getZ() {
/*  57 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/*  58 */       return this.packedByte & 0xF;
/*     */     }
/*  60 */     return ((NBTInt)this.data.getTagOfTypeOrNull("z", NBTInt.class)).getAsInt();
/*     */   }
/*     */   
/*     */   public int getY() {
/*  64 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/*  65 */       return this.y;
/*     */     }
/*  67 */     return ((NBTInt)this.data.getTagOfTypeOrNull("y", NBTInt.class)).getAsInt();
/*     */   }
/*     */   
/*     */   public void setX(int x) {
/*  71 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/*  72 */       this.packedByte = (byte)(this.packedByte & 0xF | (x & 0xF) << 4);
/*     */     } else {
/*  74 */       this.data.setTag("x", (NBT)new NBTInt(x));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setY(int y) {
/*  79 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/*  80 */       this.y = (short)y;
/*     */     } else {
/*  82 */       this.data.setTag("y", (NBT)new NBTInt(y));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setZ(int z) {
/*  87 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_18)) {
/*  88 */       this.packedByte = (byte)(this.packedByte & 0xF0 | z & 0xF);
/*     */     } else {
/*  90 */       this.data.setTag("z", (NBT)new NBTInt(z));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  96 */     return this.type;
/*     */   }
/*     */   
/*     */   public byte getPackedByte() {
/* 100 */     return this.packedByte;
/*     */   }
/*     */   
/*     */   public short getYShort() {
/* 104 */     return this.y;
/*     */   }
/*     */   
/*     */   public NBTCompound getNBT() {
/* 108 */     return this.data;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\chunk\TileEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */