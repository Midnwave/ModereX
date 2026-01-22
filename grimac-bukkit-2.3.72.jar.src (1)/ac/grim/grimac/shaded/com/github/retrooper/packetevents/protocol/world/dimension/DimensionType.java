/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.CopyableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.DeepComparableEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTLong;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.OptionalLong;
/*     */ import org.jspecify.annotations.NullMarked;
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
/*     */ @NullMarked
/*     */ public interface DimensionType
/*     */   extends MappedEntity, CopyableEntity<DimensionType>, DeepComparableEntity
/*     */ {
/*     */   OptionalLong getFixedTime();
/*     */   
/*     */   boolean hasSkyLight();
/*     */   
/*     */   boolean hasCeiling();
/*     */   
/*     */   boolean isUltraWarm();
/*     */   
/*     */   boolean isNatural();
/*     */   
/*     */   double getCoordinateScale();
/*     */   
/*     */   default boolean isShrunk() {
/*  58 */     return (getCoordinateScale() > 1.0D);
/*     */   }
/*     */   
/*     */   boolean isBedWorking();
/*     */   
/*     */   boolean isRespawnAnchorWorking();
/*     */   
/*     */   default int getMinY() {
/*  66 */     return getMinY(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*     */   }
/*     */   
/*     */   int getMinY(ClientVersion paramClientVersion);
/*     */   
/*     */   default int getHeight() {
/*  72 */     return getHeight(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*     */   }
/*     */   
/*     */   int getHeight(ClientVersion paramClientVersion);
/*     */   
/*     */   default int getLogicalHeight() {
/*  78 */     return getLogicalHeight(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion());
/*     */   }
/*     */ 
/*     */   
/*     */   int getLogicalHeight(ClientVersion paramClientVersion);
/*     */ 
/*     */   
/*     */   String getInfiniburnTag();
/*     */ 
/*     */   
/*     */   ResourceLocation getEffectsLocation();
/*     */ 
/*     */   
/*     */   float getAmbientLight();
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   Integer getCloudHeight();
/*     */ 
/*     */   
/*     */   boolean isPiglinSafe();
/*     */   
/*     */   boolean hasRaids();
/*     */   
/*     */   NBT getMonsterSpawnLightLevel();
/*     */   
/*     */   int getMonsterSpawnBlockLightLimit();
/*     */   
/*     */   default DimensionTypeRef asRef(ClientVersion version) {
/* 107 */     return new DimensionTypeRef.DirectRef(this, version);
/*     */   }
/*     */ 
/*     */   
/*     */   static DimensionType decode(NBT nbt, ClientVersion version, @Nullable TypesBuilderData data) {
/*     */     double coordinateScale;
/* 113 */     NBTCompound compound = (NBTCompound)nbt;
/*     */     
/* 115 */     OptionalLong fixedTime = !compound.getTags().containsKey("fixed_time") ? OptionalLong.empty() : OptionalLong.of(compound.getNumberTagOrThrow("fixed_time").getAsLong());
/* 116 */     boolean hasSkylight = compound.getBoolean("has_skylight");
/* 117 */     boolean hasCeiling = compound.getBoolean("has_ceiling");
/* 118 */     boolean ultrawarm = compound.getBoolean("ultrawarm");
/* 119 */     boolean natural = compound.getBoolean("natural");
/* 120 */     boolean bedWorking = compound.getBoolean("bed_works");
/* 121 */     boolean respawnAnchorWorking = compound.getBoolean("respawn_anchor_works");
/* 122 */     int logicalHeight = compound.getNumberTagOrThrow("logical_height").getAsInt();
/* 123 */     String infiniburnTag = compound.getStringTagValueOrThrow("infiniburn");
/* 124 */     float ambientLight = compound.getNumberTagOrThrow("ambient_light").getAsFloat();
/* 125 */     boolean piglinSafe = compound.getBoolean("piglin_safe");
/* 126 */     boolean hasRaids = compound.getBoolean("has_raids");
/*     */ 
/*     */     
/* 129 */     int minY = 0;
/* 130 */     int height = 256;
/* 131 */     ResourceLocation effectsLocation = null;
/* 132 */     Integer cloudHeight = null;
/* 133 */     NBT monsterSpawnLightLevel = null;
/* 134 */     int monsterSpawnBlockLightLimit = 0;
/* 135 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_16_2)) {
/* 136 */       coordinateScale = compound.getNumberTagOrThrow("coordinate_scale").getAsDouble();
/* 137 */       effectsLocation = new ResourceLocation(compound.getStringTagValueOrThrow("effects"));
/* 138 */       if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
/* 139 */         minY = compound.getNumberTagOrThrow("min_y").getAsInt();
/* 140 */         height = compound.getNumberTagOrThrow("height").getAsInt();
/* 141 */         if (version.isNewerThanOrEquals(ClientVersion.V_1_19)) {
/* 142 */           monsterSpawnLightLevel = compound.getTagOrThrow("monster_spawn_light_level");
/* 143 */           monsterSpawnBlockLightLimit = compound.getNumberTagOrThrow("monster_spawn_block_light_limit").getAsInt();
/* 144 */           if (version.isNewerThanOrEquals(ClientVersion.V_1_21_6)) {
/* 145 */             NBTNumber cloudHeightTag = compound.getNumberTagOrNull("cloud_height");
/* 146 */             cloudHeight = (cloudHeightTag != null) ? Integer.valueOf(cloudHeightTag.getAsInt()) : null;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } else {
/* 151 */       coordinateScale = compound.getBoolean("shrunk") ? 8.0D : 1.0D;
/*     */     } 
/*     */     
/* 154 */     return new StaticDimensionType(data, fixedTime, hasSkylight, hasCeiling, ultrawarm, natural, coordinateScale, bedWorking, respawnAnchorWorking, minY, height, logicalHeight, infiniburnTag, effectsLocation, ambientLight, cloudHeight, piglinSafe, hasRaids, monsterSpawnLightLevel, monsterSpawnBlockLightLimit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static NBT encode(DimensionType dimensionType, ClientVersion version) {
/* 160 */     NBTCompound compound = new NBTCompound();
/* 161 */     dimensionType.getFixedTime().ifPresent(fixedTime -> compound.setTag("fixed_time", (NBT)new NBTLong(fixedTime)));
/*     */     
/* 163 */     compound.setTag("has_skylight", (NBT)new NBTByte(dimensionType.hasSkyLight()));
/* 164 */     compound.setTag("has_ceiling", (NBT)new NBTByte(dimensionType.hasCeiling()));
/* 165 */     compound.setTag("ultrawarm", (NBT)new NBTByte(dimensionType.isUltraWarm()));
/* 166 */     compound.setTag("natural", (NBT)new NBTByte(dimensionType.isNatural()));
/* 167 */     compound.setTag("bed_works", (NBT)new NBTByte(dimensionType.isBedWorking()));
/* 168 */     compound.setTag("respawn_anchor_works", (NBT)new NBTByte(dimensionType.isRespawnAnchorWorking()));
/* 169 */     compound.setTag("logical_height", (NBT)new NBTInt(dimensionType.getLogicalHeight(version)));
/* 170 */     compound.setTag("infiniburn", (NBT)new NBTString(dimensionType.getInfiniburnTag()));
/* 171 */     compound.setTag("ambient_light", (NBT)new NBTFloat(dimensionType.getAmbientLight()));
/* 172 */     compound.setTag("piglin_safe", (NBT)new NBTByte(dimensionType.isPiglinSafe()));
/* 173 */     compound.setTag("has_raids", (NBT)new NBTByte(dimensionType.hasRaids()));
/*     */     
/* 175 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_16_2)) {
/* 176 */       compound.setTag("coordinate_scale", (NBT)new NBTDouble(dimensionType.getCoordinateScale()));
/* 177 */       compound.setTag("effects", (NBT)new NBTString(dimensionType.getEffectsLocation().toString()));
/* 178 */       if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
/* 179 */         compound.setTag("min_y", (NBT)new NBTInt(dimensionType.getMinY(version)));
/* 180 */         compound.setTag("height", (NBT)new NBTInt(dimensionType.getHeight(version)));
/* 181 */         if (version.isNewerThanOrEquals(ClientVersion.V_1_19)) {
/* 182 */           compound.setTag("monster_spawn_light_level", dimensionType.getMonsterSpawnLightLevel());
/* 183 */           compound.setTag("monster_spawn_block_light_limit", (NBT)new NBTInt(dimensionType.getMonsterSpawnBlockLightLimit()));
/* 184 */           if (version.isNewerThanOrEquals(ClientVersion.V_1_21_6) && 
/* 185 */             dimensionType.getCloudHeight() != null) {
/* 186 */             compound.setTag("cloud_height", (NBT)new NBTInt(dimensionType.getCloudHeight().intValue()));
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 192 */       compound.setTag("shrunk", (NBT)new NBTByte(dimensionType.isShrunk()));
/*     */     } 
/* 194 */     return (NBT)compound;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\dimension\DimensionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */