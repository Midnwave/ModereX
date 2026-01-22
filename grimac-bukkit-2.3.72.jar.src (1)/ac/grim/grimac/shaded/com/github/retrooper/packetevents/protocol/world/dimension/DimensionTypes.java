/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.dimension;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
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
/*     */ @NullMarked
/*     */ public final class DimensionTypes
/*     */ {
/*     */   private static final int PRE118_MIN_Y = 0;
/*     */   private static final int PRE118_HEIGHT = 256;
/*  34 */   private static final VersionedRegistry<DimensionType> REGISTRY = new VersionedRegistry("dimension_type");
/*     */   
/*     */   private static final int POST118_MIN_Y = -64;
/*     */   private static final int POST118_HEIGHT = 384;
/*     */   
/*     */   public static VersionedRegistry<DimensionType> getRegistry() {
/*  40 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static final DimensionType OVERWORLD;
/*     */   
/*     */   static {
/*  46 */     OVERWORLD = (DimensionType)REGISTRY.define("overworld", data -> {
/*     */           NBTCompound monsterSpawnLightLevel = new NBTCompound();
/*     */           
/*     */           monsterSpawnLightLevel.setTag("type", (NBT)new NBTString("minecraft:uniform"));
/*     */           
/*     */           monsterSpawnLightLevel.setTag("min_inclusive", (NBT)new NBTInt(0));
/*     */           
/*     */           monsterSpawnLightLevel.setTag("max_inclusive", (NBT)new NBTInt(7));
/*     */           
/*     */           return (null)new StaticDimensionType(data, OptionalLong.empty(), true, false, false, true, 1.0D, true, false, -64, 384, 384, "#minecraft:infiniburn_overworld", ResourceLocation.minecraft("overworld"), 0.0F, Integer.valueOf(192), false, true, (NBT)monsterSpawnLightLevel, 0)
/*     */             {
/*     */               public int getMinY(ClientVersion version)
/*     */               {
/*  59 */                 return version.isNewerThanOrEquals(ClientVersion.V_1_18) ? -64 : 0;
/*     */               }
/*     */ 
/*     */               
/*     */               public int getHeight(ClientVersion version) {
/*  64 */                 return version.isNewerThanOrEquals(ClientVersion.V_1_18) ? 384 : 256;
/*     */               }
/*     */ 
/*     */               
/*     */               public int getLogicalHeight(ClientVersion version) {
/*  69 */                 return version.isNewerThanOrEquals(ClientVersion.V_1_18) ? 384 : 256;
/*     */               }
/*     */             };
/*     */         });
/*     */     
/*  74 */     OVERWORLD_CAVES = (DimensionType)REGISTRY.define("overworld_caves", data -> {
/*     */           NBTCompound monsterSpawnLightLevel = new NBTCompound();
/*     */           
/*     */           monsterSpawnLightLevel.setTag("type", (NBT)new NBTString("minecraft:uniform"));
/*     */           
/*     */           monsterSpawnLightLevel.setTag("min_inclusive", (NBT)new NBTInt(0));
/*     */           
/*     */           monsterSpawnLightLevel.setTag("max_inclusive", (NBT)new NBTInt(7));
/*     */           
/*     */           return (null)new StaticDimensionType(data, OptionalLong.empty(), true, true, false, true, 1.0D, true, false, -64, 384, 384, "#minecraft:infiniburn_overworld", ResourceLocation.minecraft("overworld"), 0.0F, Integer.valueOf(192), false, true, (NBT)monsterSpawnLightLevel, 0)
/*     */             {
/*     */               public int getMinY(ClientVersion version)
/*     */               {
/*  87 */                 return version.isNewerThanOrEquals(ClientVersion.V_1_18) ? -64 : 0;
/*     */               }
/*     */ 
/*     */               
/*     */               public int getHeight(ClientVersion version) {
/*  92 */                 return version.isNewerThanOrEquals(ClientVersion.V_1_18) ? 384 : 256;
/*     */               }
/*     */ 
/*     */               
/*     */               public int getLogicalHeight(ClientVersion version) {
/*  97 */                 return version.isNewerThanOrEquals(ClientVersion.V_1_18) ? 384 : 256;
/*     */               }
/*     */             };
/*     */         });
/*     */     
/* 102 */     THE_END = (DimensionType)REGISTRY.define("the_end", data -> {
/*     */           NBTCompound monsterSpawnLightLevel = new NBTCompound();
/*     */ 
/*     */           
/*     */           monsterSpawnLightLevel.setTag("type", (NBT)new NBTString("minecraft:uniform"));
/*     */           
/*     */           monsterSpawnLightLevel.setTag("min_inclusive", (NBT)new NBTInt(0));
/*     */           
/*     */           monsterSpawnLightLevel.setTag("max_inclusive", (NBT)new NBTInt(7));
/*     */           
/*     */           return new StaticDimensionType(data, OptionalLong.of(6000L), false, false, false, false, 1.0D, false, false, 0, 256, 256, "#minecraft:infiniburn_end", ResourceLocation.minecraft("the_end"), 0.0F, null, false, true, (NBT)monsterSpawnLightLevel, 0);
/*     */         });
/*     */     
/* 115 */     THE_NETHER = (DimensionType)REGISTRY.define("the_nether", data -> new StaticDimensionType(data, OptionalLong.of(18000L), false, true, true, false, 8.0D, false, true, 0, 256, 128, "#minecraft:infiniburn_nether", ResourceLocation.minecraft("the_nether"), 0.1F, null, true, false, (NBT)new NBTInt(7), 15));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 124 */     REGISTRY.unloadMappings();
/*     */   }
/*     */   
/*     */   public static final DimensionType OVERWORLD_CAVES;
/*     */   public static final DimensionType THE_END;
/*     */   public static final DimensionType THE_NETHER;
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\dimension\DimensionTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */