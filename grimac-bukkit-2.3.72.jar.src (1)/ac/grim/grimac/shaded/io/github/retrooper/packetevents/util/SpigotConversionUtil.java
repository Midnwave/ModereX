/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.HumanoidArm;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.potion.PotionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Dimension;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.Location;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.SimpleTypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.List;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.GameMode;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.data.BlockData;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.EntityType;
/*     */ import org.bukkit.entity.Pose;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.inventory.MainHand;
/*     */ import org.bukkit.material.MaterialData;
/*     */ import org.bukkit.potion.PotionEffectType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpigotConversionUtil
/*     */ {
/*     */   public static Location fromBukkitLocation(Location location) {
/*  54 */     return new Location(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
/*     */   }
/*     */   
/*     */   public static Location toBukkitLocation(World world, Location location) {
/*  58 */     return new Location(world, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
/*     */   }
/*     */   
/*     */   public static PotionType fromBukkitPotionEffectType(PotionEffectType potionEffectType) {
/*  62 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/*  63 */     int id = potionEffectType.getId();
/*  64 */     if (version.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
/*  65 */       id--;
/*     */     }
/*  67 */     return PotionTypes.getById(id, version);
/*     */   }
/*     */   
/*     */   public static PotionEffectType toBukkitPotionEffectType(PotionType potionType) {
/*  71 */     ClientVersion version = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
/*  72 */     int id = potionType.getId(version);
/*  73 */     if (version.isNewerThanOrEquals(ClientVersion.V_1_20_2)) {
/*  74 */       id++;
/*     */     }
/*  76 */     return PotionEffectType.getById(id);
/*     */   }
/*     */   
/*     */   public static GameMode fromBukkitGameMode(GameMode gameMode) {
/*  80 */     return GameMode.getById(gameMode.getValue());
/*     */   }
/*     */   
/*     */   public static GameMode toBukkitGameMode(GameMode gameMode) {
/*  84 */     return GameMode.getByValue(gameMode.getId());
/*     */   }
/*     */   
/*     */   public static WrappedBlockState fromBukkitBlockData(BlockData blockData) {
/*  88 */     String string = blockData.getAsString(false);
/*  89 */     return WrappedBlockState.getByString(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), string);
/*     */   }
/*     */   
/*     */   public static BlockData toBukkitBlockData(WrappedBlockState blockState) {
/*  93 */     return Bukkit.createBlockData(blockState.toString());
/*     */   }
/*     */   
/*     */   public static EntityType fromBukkitEntityType(EntityType entityType) {
/*  97 */     ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/*  98 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_14))
/*  99 */       return EntityTypes.getByName(entityType.getKey().toString()); 
/* 100 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 101 */       return EntityTypes.getByName("minecraft:" + entityType.getName());
/*     */     }
/* 103 */     if (entityType.getTypeId() == -1) {
/* 104 */       return null;
/*     */     }
/* 106 */     return EntityTypes.getById(serverVersion.toClientVersion(), entityType.getTypeId());
/*     */   }
/*     */ 
/*     */   
/*     */   public static EntityType toBukkitEntityType(EntityType entityType) {
/* 111 */     ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/* 112 */     if (serverVersion.isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 113 */       return EntityType.fromName(entityType.getName().getKey());
/*     */     }
/* 115 */     return EntityType.fromId(entityType.getId(serverVersion.toClientVersion()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ItemType fromBukkitItemMaterial(Material material) {
/* 121 */     ItemStack bukkitStack = new ItemStack(material);
/* 122 */     ItemStack stack = fromBukkitItemStack(bukkitStack);
/* 123 */     return stack.getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public static Material toBukkitItemMaterial(ItemType itemType) {
/* 128 */     ItemStack stack = ItemStack.builder().type(itemType).build();
/* 129 */     ItemStack bukkitStack = toBukkitItemStack(stack);
/* 130 */     return bukkitStack.getType();
/*     */   }
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
/*     */   public static WrappedBlockState fromBukkitMaterialData(MaterialData materialData) {
/* 143 */     int combinedID = SpigotReflectionUtil.getBlockDataCombinedId(materialData);
/* 144 */     ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();
/* 145 */     return WrappedBlockState.getByGlobalId(serverVersion.toClientVersion(), combinedID);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MaterialData toBukkitMaterialData(WrappedBlockState state) {
/* 155 */     return SpigotReflectionUtil.getBlockDataByCombinedId(state.getGlobalId());
/*     */   }
/*     */   
/*     */   public static ItemStack fromBukkitItemStack(ItemStack itemStack) {
/* 159 */     return SpigotReflectionUtil.decodeBukkitItemStack(itemStack);
/*     */   }
/*     */   
/*     */   public static ItemStack toBukkitItemStack(ItemStack itemStack) {
/* 163 */     return SpigotReflectionUtil.encodeBukkitItemStack(itemStack);
/*     */   }
/*     */   
/*     */   public static DimensionType typeFromBukkitWorld(World world) {
/* 167 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/* 168 */     if (version.isOlderThan(ServerVersion.V_1_14)) {
/* 169 */       int environmentId = world.getEnvironment().getId();
/* 170 */       return (DimensionType)DimensionTypes.getRegistry().getById(version.toClientVersion(), environmentId);
/* 171 */     }  if (version.isOlderThan(ServerVersion.V_1_16)) {
/* 172 */       Object worldServer = SpigotReflectionUtil.convertBukkitWorldToWorldServer(world);
/* 173 */       int i = SpigotReflectionUtil.getDimensionId(worldServer);
/* 174 */       return (DimensionType)DimensionTypes.getRegistry().getById(version.toClientVersion(), i);
/*     */     } 
/* 176 */     Object serverLevel = SpigotReflectionUtil.convertBukkitWorldToWorldServer(world);
/* 177 */     Object nbt = SpigotReflectionUtil.convertWorldServerDimensionToNMSNbt(serverLevel);
/*     */     
/* 179 */     NBTCompound peNbt = SpigotReflectionUtil.fromMinecraftNBT(nbt);
/* 180 */     ResourceLocation dimensionTypeName = new ResourceLocation(SpigotReflectionUtil.getDimensionKey(serverLevel));
/* 181 */     int dimensionTypeId = SpigotReflectionUtil.getDimensionId(serverLevel);
/* 182 */     return DimensionType.decode((NBT)peNbt, version.toClientVersion(), (TypesBuilderData)new SimpleTypesBuilderData(dimensionTypeName, dimensionTypeId));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Dimension fromBukkitWorld(World world) {
/* 189 */     ServerVersion version = PacketEvents.getAPI().getServerManager().getVersion();
/* 190 */     if (version.isOlderThan(ServerVersion.V_1_14))
/* 191 */       return new Dimension(world.getEnvironment().getId()); 
/* 192 */     if (version.isOlderThan(ServerVersion.V_1_16)) {
/* 193 */       Object worldServer = SpigotReflectionUtil.convertBukkitWorldToWorldServer(world);
/* 194 */       return new Dimension(SpigotReflectionUtil.getDimensionId(worldServer));
/*     */     } 
/* 196 */     Object serverLevel = SpigotReflectionUtil.convertBukkitWorldToWorldServer(world);
/* 197 */     Object nbt = SpigotReflectionUtil.convertWorldServerDimensionToNMSNbt(serverLevel);
/* 198 */     Dimension dimension = new Dimension(SpigotReflectionUtil.fromMinecraftNBT(nbt));
/* 199 */     if (version.isOlderThan(ServerVersion.V_1_16_2)) {
/* 200 */       dimension.setDimensionName(SpigotReflectionUtil.getDimensionKey(serverLevel));
/*     */     }
/* 202 */     dimension.setId(SpigotReflectionUtil.getDimensionId(serverLevel));
/* 203 */     return dimension;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ParticleType<?> fromBukkitParticle(Enum<?> particle) {
/* 208 */     return SpigotReflectionUtil.toPacketEventsParticle(particle);
/*     */   }
/*     */   
/*     */   public static Enum<?> toBukkitParticle(ParticleType<?> particle) {
/* 212 */     return SpigotReflectionUtil.fromPacketEventsParticle(particle);
/*     */   }
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
/*     */   public static Entity getEntityById(@Nullable World world, int entityId) {
/* 226 */     return SpigotReflectionUtil.getEntityById(world, entityId);
/*     */   }
/*     */   
/*     */   public static Pose toBukkitPose(EntityPose pose) {
/* 230 */     return Pose.values()[pose.ordinal()];
/*     */   }
/*     */   
/*     */   public static EntityPose fromBukkitPose(Pose pose) {
/* 234 */     return EntityPose.values()[pose.ordinal()];
/*     */   }
/*     */   
/*     */   public static MainHand toBukkitHand(HumanoidArm arm) {
/* 238 */     return MainHand.values()[arm.ordinal()];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<EntityData<?>> getEntityMetadata(Entity entity) {
/* 250 */     return SpigotReflectionUtil.getEntityMetadata(entity);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\SpigotConversionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */