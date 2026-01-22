/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.mapdecoration.MapDecorationTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.List;
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
/*     */ public class WrapperPlayServerMapData
/*     */   extends PacketWrapper<WrapperPlayServerMapData>
/*     */ {
/*     */   private int mapId;
/*     */   private byte scale;
/*     */   private boolean trackingPosition;
/*     */   private boolean locked;
/*     */   @Nullable
/*     */   private List<MapDecoration> decorations;
/*     */   private int columns;
/*     */   private int rows;
/*     */   private int x;
/*     */   private int z;
/*     */   private byte[] data;
/*     */   
/*     */   public WrapperPlayServerMapData(PacketSendEvent event) {
/*  45 */     super(event);
/*     */   }
/*     */   
/*     */   public WrapperPlayServerMapData(int mapId, byte scale, @Nullable List<MapDecoration> decorations) {
/*  49 */     this(mapId, scale, false, decorations);
/*     */   }
/*     */   
/*     */   public WrapperPlayServerMapData(int mapId, byte scale, boolean locked, @Nullable List<MapDecoration> decorations) {
/*  53 */     this(mapId, scale, false, locked, decorations, 0, 0, 0, 0, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WrapperPlayServerMapData(int mapId, byte scale, boolean trackingPosition, boolean locked, @Nullable List<MapDecoration> decorations, int columns, int rows, int x, int z, byte[] data) {
/*  61 */     super((PacketTypeCommon)PacketType.Play.Server.MAP_DATA);
/*  62 */     this.mapId = mapId;
/*  63 */     this.scale = scale;
/*  64 */     this.trackingPosition = trackingPosition;
/*  65 */     this.locked = locked;
/*  66 */     this.decorations = decorations;
/*  67 */     this.columns = columns;
/*  68 */     this.rows = rows;
/*  69 */     this.x = x;
/*  70 */     this.z = z;
/*  71 */     this.data = data;
/*     */   }
/*     */ 
/*     */   
/*     */   public void read() {
/*  76 */     this.mapId = readVarInt();
/*  77 */     this.scale = readByte();
/*  78 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9) && this.serverVersion
/*  79 */       .isOlderThan(ServerVersion.V_1_17)) {
/*  80 */       this.trackingPosition = readBoolean();
/*     */     }
/*  82 */     this.locked = (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14) && readBoolean());
/*     */     
/*  84 */     if (this.serverVersion.isOlderThan(ServerVersion.V_1_17) || readBoolean()) {
/*  85 */       this.decorations = readList(MapDecoration::read);
/*     */     }
/*     */     
/*  88 */     this.columns = readUnsignedByte();
/*  89 */     if (this.columns > 0) {
/*  90 */       this.rows = readUnsignedByte();
/*  91 */       this.x = readUnsignedByte();
/*  92 */       this.z = readUnsignedByte();
/*  93 */       this.data = readByteArray();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write() {
/*  99 */     writeVarInt(this.mapId);
/* 100 */     writeByte(this.scale);
/* 101 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_9) && this.serverVersion
/* 102 */       .isOlderThan(ServerVersion.V_1_17)) {
/* 103 */       writeBoolean(this.trackingPosition);
/*     */     }
/* 105 */     if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14)) {
/* 106 */       writeBoolean(this.locked);
/*     */     }
/*     */     
/* 109 */     if (this.decorations != null) {
/* 110 */       if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
/* 111 */         writeBoolean(true);
/*     */       }
/* 113 */       writeList(this.decorations, MapDecoration::write);
/* 114 */     } else if (this.serverVersion.isNewerThanOrEquals(ServerVersion.V_1_17)) {
/* 115 */       writeBoolean(false);
/*     */     } else {
/*     */       
/* 118 */       writeVarInt(0);
/*     */     } 
/*     */     
/* 121 */     if (this.data != null) {
/* 122 */       writeByte(this.columns);
/* 123 */       if (this.columns > 0) {
/* 124 */         writeByte(this.rows);
/* 125 */         writeByte(this.x);
/* 126 */         writeByte(this.z);
/* 127 */         writeByteArray(this.data);
/*     */       } 
/*     */     } else {
/* 130 */       writeByte(0);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class MapDecoration {
/*     */     private MapDecorationType type;
/*     */     private byte x;
/*     */     private byte y;
/*     */     private byte direction;
/*     */     @Nullable
/*     */     private Component displayName;
/*     */     
/*     */     public MapDecoration(MapDecorationType type, byte x, byte y, byte direction, @Nullable Component displayName) {
/* 143 */       this.type = type;
/* 144 */       this.x = x;
/* 145 */       this.y = y;
/* 146 */       this.direction = direction;
/* 147 */       this.displayName = displayName;
/*     */     }
/*     */ 
/*     */     
/*     */     public static MapDecoration read(PacketWrapper<?> wrapper) {
/* 152 */       boolean v113 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13);
/* 153 */       byte flags = v113 ? 0 : wrapper.readByte();
/*     */ 
/*     */       
/* 156 */       MapDecorationType type = !v113 ? MapDecorationTypes.getById(wrapper.getServerVersion().toClientVersion(), flags >> 4 & 0xF) : (MapDecorationType)wrapper.readMappedEntity(MapDecorationTypes::getById);
/*     */       
/* 158 */       byte x = wrapper.readByte();
/* 159 */       byte y = wrapper.readByte();
/* 160 */       byte direction = (byte)((v113 ? wrapper.readByte() : flags) & 0xF);
/* 161 */       Component displayName = v113 ? (Component)wrapper.readOptional(PacketWrapper::readComponent) : null;
/* 162 */       return new MapDecoration(type, x, y, direction, displayName);
/*     */     }
/*     */     
/*     */     public static void write(PacketWrapper<?> wrapper, MapDecoration decoration) {
/* 166 */       boolean v113 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13);
/* 167 */       if (v113) {
/* 168 */         wrapper.writeMappedEntity((MappedEntity)decoration.getType());
/*     */       } else {
/* 170 */         int typeId = decoration.getType().getId(wrapper.getServerVersion().toClientVersion());
/* 171 */         wrapper.writeByte((typeId & 0xF) << 4 | decoration.getDirection() & 0xF);
/*     */       } 
/*     */       
/* 174 */       wrapper.writeByte(decoration.getX());
/* 175 */       wrapper.writeByte(decoration.getY());
/* 176 */       if (v113) {
/* 177 */         wrapper.writeByte(decoration.getDirection());
/* 178 */         wrapper.writeOptional(decoration.getDisplayName(), PacketWrapper::writeComponent);
/*     */       } 
/*     */     }
/*     */     
/*     */     public MapDecorationType getType() {
/* 183 */       return this.type;
/*     */     }
/*     */     
/*     */     public void setType(MapDecorationType type) {
/* 187 */       this.type = type;
/*     */     }
/*     */     
/*     */     public byte getX() {
/* 191 */       return this.x;
/*     */     }
/*     */     
/*     */     public void setX(byte x) {
/* 195 */       this.x = x;
/*     */     }
/*     */     
/*     */     public byte getY() {
/* 199 */       return this.y;
/*     */     }
/*     */     
/*     */     public void setY(byte y) {
/* 203 */       this.y = y;
/*     */     }
/*     */     
/*     */     public byte getDirection() {
/* 207 */       return this.direction;
/*     */     }
/*     */     
/*     */     public void setDirection(byte direction) {
/* 211 */       this.direction = direction;
/*     */     }
/*     */     @Nullable
/*     */     public Component getDisplayName() {
/* 215 */       return this.displayName;
/*     */     }
/*     */     
/*     */     public void setDisplayName(@Nullable Component displayName) {
/* 219 */       this.displayName = displayName;
/*     */     }
/*     */   }
/*     */   
/*     */   public int getMapId() {
/* 224 */     return this.mapId;
/*     */   }
/*     */   
/*     */   public void setMapId(int mapId) {
/* 228 */     this.mapId = mapId;
/*     */   }
/*     */   
/*     */   public byte getScale() {
/* 232 */     return this.scale;
/*     */   }
/*     */   
/*     */   public void setScale(byte scale) {
/* 236 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public boolean isTrackingPosition() {
/* 240 */     return this.trackingPosition;
/*     */   }
/*     */   
/*     */   public void setTrackingPosition(boolean trackingPosition) {
/* 244 */     this.trackingPosition = trackingPosition;
/*     */   }
/*     */   
/*     */   public boolean isLocked() {
/* 248 */     return this.locked;
/*     */   }
/*     */   
/*     */   public void setLocked(boolean locked) {
/* 252 */     this.locked = locked;
/*     */   }
/*     */   @Nullable
/*     */   public List<MapDecoration> getDecorations() {
/* 256 */     return this.decorations;
/*     */   }
/*     */   
/*     */   public void setDecorations(@Nullable List<MapDecoration> decorations) {
/* 260 */     this.decorations = decorations;
/*     */   }
/*     */   
/*     */   public int getColumns() {
/* 264 */     return this.columns;
/*     */   }
/*     */   
/*     */   public void setColumns(int columns) {
/* 268 */     this.columns = columns;
/*     */   }
/*     */   
/*     */   public int getRows() {
/* 272 */     return this.rows;
/*     */   }
/*     */   
/*     */   public void setRows(int rows) {
/* 276 */     this.rows = rows;
/*     */   }
/*     */   
/*     */   public int getX() {
/* 280 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(int x) {
/* 284 */     this.x = x;
/*     */   }
/*     */   
/*     */   public int getZ() {
/* 288 */     return this.z;
/*     */   }
/*     */   
/*     */   public void setZ(int z) {
/* 292 */     this.z = z;
/*     */   }
/*     */   
/*     */   public byte[] getData() {
/* 296 */     return this.data;
/*     */   }
/*     */   
/*     */   public void setData(byte[] data) {
/* 300 */     this.data = data;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\wrapper\play\server\WrapperPlayServerMapData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */