/*      */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util;
/*      */ 
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.entity.data.EntityData;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.ItemStack;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.TextureProperty;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.NestedClassUtil;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.Reflection;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection.ReflectionObject;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*      */ import com.google.common.collect.BiMap;
/*      */ import com.google.common.collect.MapMaker;
/*      */ import io.netty.buffer.PooledByteBufAllocator;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import org.bukkit.Bukkit;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.NamespacedKey;
/*      */ import org.bukkit.Particle;
/*      */ import org.bukkit.Registry;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.material.MaterialData;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class SpigotReflectionUtil
/*      */ {
/*      */   private static final String MODIFIED_PACKAGE_NAME;
/*      */   
/*      */   static {
/*   63 */     String temp, cbPackage = Bukkit.getServer().getClass().getPackage().getName();
/*      */     
/*      */     try {
/*   66 */       temp = cbPackage.replace(".", ",").split(",")[3];
/*   67 */     } catch (Exception ex) {
/*   68 */       temp = "";
/*      */     } 
/*   70 */     MODIFIED_PACKAGE_NAME = temp;
/*      */   }
/*   72 */   public static final String OBC_PACKAGE; public static ServerVersion VERSION; public static boolean V_1_19_OR_HIGHER; public static boolean V_1_17_OR_HIGHER; public static boolean V_1_12_OR_HIGHER; public static Class<?> MINECRAFT_SERVER_CLASS; public static Class<?> NMS_PACKET_DATA_SERIALIZER_CLASS; public static Class<?> NMS_ITEM_STACK_CLASS; public static Class<?> NMS_IMATERIAL_CLASS; public static Class<?> NMS_ENTITY_CLASS; public static Class<?> ENTITY_PLAYER_CLASS; public static Class<?> BOUNDING_BOX_CLASS; public static Class<?> NMS_MINECRAFT_KEY_CLASS; public static Class<?> ENTITY_HUMAN_CLASS; public static Class<?> PLAYER_CONNECTION_CLASS; public static Class<?> TRANSFER_COOKIE_CONNECTION_CLASS; public static Class<?> SERVER_LOGIN_PACKET_LISTENER_IMPL_CLASS; public static Class<?> SERVER_COMMON_PACKETLISTENER_IMPL_CLASS; public static Class<?> SERVER_CONNECTION_CLASS; public static Class<?> NETWORK_MANAGER_CLASS; public static Class<?> NMS_ENUM_PARTICLE_CLASS; public static Class<?> MOB_EFFECT_LIST_CLASS; public static Class<?> NMS_ITEM_CLASS; public static Class<?> DEDICATED_SERVER_CLASS; public static Class<?> LEVEL_CLASS; public static Class<?> SERVER_LEVEL_CLASS; public static Class<?> ENUM_PROTOCOL_DIRECTION_CLASS; public static Class<?> GAME_PROFILE_CLASS; public static Class<?> CRAFT_WORLD_CLASS; public static Class<?> CRAFT_SERVER_CLASS; public static Class<?> CRAFT_PLAYER_CLASS; public static Class<?> CRAFT_ENTITY_CLASS; public static Class<?> CRAFT_ITEM_STACK_CLASS; public static Class<?> CRAFT_PARTICLE_CLASS; public static Class<?> LEVEL_ENTITY_GETTER_CLASS; public static Class<?> ENTITY_ACCESS_CLASS; public static Class<?> PERSISTENT_ENTITY_SECTION_MANAGER_CLASS; public static final String LEGACY_NMS_PACKAGE = "net.minecraft.server." + MODIFIED_PACKAGE_NAME + "."; public static Class<?> PAPER_ENTITY_LOOKUP_CLASS; public static Class<?> CRAFT_MAGIC_NUMBERS_CLASS; public static Class<?> IBLOCK_DATA_CLASS; public static Class<?> BLOCK_CLASS; public static Class<?> CRAFT_BLOCK_DATA_CLASS; public static Class<?> PROPERTY_MAP_CLASS; public static Class<?> DIMENSION_MANAGER_CLASS; public static Class<?> MOJANG_CODEC_CLASS; public static Class<?> MOJANG_ENCODER_CLASS; public static Class<?> DATA_RESULT_CLASS; public static Class<?> DYNAMIC_OPS_NBT_CLASS; public static Class<?> NMS_NBT_COMPOUND_CLASS; public static Class<?> NMS_NBT_BASE_CLASS; public static Class<?> NBT_COMPRESSION_STREAM_TOOLS_CLASS; public static Class<?> STREAM_CODEC; public static Class<?> STREAM_DECODER; public static Class<?> STREAM_ENCODER; public static Class<?> REGISTRY_FRIENDLY_BYTE_BUF; public static Class<?> REGISTRY_ACCESS; public static Class<?> REGISTRY_ACCESS_FROZEN; public static Class<?> RESOURCE_KEY; public static Class<?> REGISTRY; public static Class<?> WRITABLE_REGISTRY; public static Class<?> NBT_ACCOUNTER; public static Class<?> CHUNK_PROVIDER_SERVER_CLASS; public static Class<?> ICHUNKPROVIDER_CLASS; public static Class<?> CHUNK_STATUS_CLASS; public static Class<?> BLOCK_POSITION_CLASS; public static Class<?> PLAYER_CHUNK_MAP_CLASS; public static Class<?> PLAYER_CHUNK_CLASS; public static Class<?> CHUNK_CLASS; public static Class<?> IBLOCKACCESS_CLASS; public static Class<?> ICHUNKACCESS_CLASS; public static Class<?> REMOTE_CHAT_SESSION_CLASS; public static Class<?> DATA_WATCHER_CLASS; public static Class<?> CLIENTBOUND_SET_ENTITY_DATA_PACKET_CLASS;
/*      */   static {
/*   74 */     OBC_PACKAGE = cbPackage + ".";
/*      */   }
/*      */   public static Class<?> DATA_WATCHER_ITEM_CLASS; public static Class<?> DATA_WATCHER_VALUE_CLASS; public static Class<?> CHANNEL_CLASS; public static Class<?> BYTE_BUF_CLASS; public static Class<?> BYTE_TO_MESSAGE_DECODER; public static Class<?> MESSAGE_TO_BYTE_ENCODER; public static Field ENTITY_PLAYER_PING_FIELD; public static Field ENTITY_BOUNDING_BOX_FIELD; public static Field BYTE_BUF_IN_PACKET_DATA_SERIALIZER; public static Field DIMENSION_CODEC_FIELD; public static Field DYNAMIC_OPS_NBT_INSTANCE_FIELD; public static Field CHUNK_PROVIDER_SERVER_FIELD; public static Field CRAFT_PARTICLE_PARTICLES_FIELD; public static Field NMS_MK_KEY_FIELD; public static Field LEGACY_NMS_PARTICLE_KEY_FIELD; public static Field LEGACY_NMS_KEY_TO_NMS_PARTICLE; public static Field REMOTE_CHAT_SESSION_FIELD; public static Field REGISTRY_KEY_LOCATION_FIELD; public static Field DATA_WATCHER_FIELD;
/*      */   public static Method IS_DEBUGGING;
/*      */   public static Method GET_CRAFT_PLAYER_HANDLE_METHOD;
/*      */   public static Method GET_CRAFT_ENTITY_HANDLE_METHOD;
/*      */   public static Method GET_CRAFT_WORLD_HANDLE_METHOD;
/*      */   public static Method GET_MOB_EFFECT_LIST_ID_METHOD;
/*      */   public static Method GET_MOB_EFFECT_LIST_BY_ID_METHOD;
/*      */   public static Method GET_ITEM_ID_METHOD;
/*      */   public static Method GET_ITEM_BY_ID_METHOD;
/*      */   public static Method GET_BUKKIT_ENTITY_METHOD;
/*      */   public static Method GET_LEVEL_ENTITY_GETTER_ITERABLE_METHOD;
/*      */   public static Method GET_ENTITY_BY_ID_LEVEL_ENTITY_GETTER_METHOD;
/*      */   public static Method GET_ENTITY_BY_ID_METHOD;
/*      */   public static Method CRAFT_ITEM_STACK_AS_BUKKIT_COPY;
/*      */   public static Method CRAFT_ITEM_STACK_AS_NMS_COPY;
/*      */   public static Method BUKKIT_PARTICLE_TO_NMS_ENUM_PARTICLE;
/*      */   public static Method NMS_ENUM_PARTICLE_TO_BUKKIT_PARTICLE;
/*      */   public static Method READ_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD;
/*      */   public static Method WRITE_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD;
/*      */   public static Method GET_COMBINED_ID;
/*      */   public static Method GET_BY_COMBINED_ID;
/*      */   public static Method GET_CRAFT_BLOCK_DATA_FROM_IBLOCKDATA;
/*      */   public static Method PROPERTY_MAP_GET_METHOD;
/*      */   public static Method GET_DIMENSION_MANAGER;
/*      */   public static Method GET_DIMENSION_ID;
/*      */   public static Method GET_DIMENSION_KEY;
/*      */   public static Method CODEC_ENCODE_METHOD;
/*      */   public static Method DATA_RESULT_GET_METHOD;
/*      */   public static Method READ_NBT_FROM_STREAM_METHOD;
/*      */   public static Method WRITE_NBT_TO_STREAM_METHOD;
/*      */   public static Method STREAM_DECODER_DECODE;
/*      */   public static Method STREAM_ENCODER_ENCODE;
/*      */   public static Method CREATE_REGISTRY_RESOURCE_KEY;
/*      */   public static Method GET_REGISTRY_OR_THROW;
/*      */   public static Method GET_DIMENSION_TYPES;
/*      */   public static Method GET_REGISTRY_ID;
/*      */   public static Method NBT_ACCOUNTER_UNLIMITED_HEAP;
/*      */   public static Method CHUNK_CACHE_GET_IBLOCKACCESS;
/*      */   public static Method CHUNK_CACHE_GET_ICHUNKACCESS;
/*      */   public static Method IBLOCKACCESS_GET_BLOCK_DATA;
/*      */   public static Method CHUNK_GET_BLOCK_DATA;
/*      */   public static Method PLAYER_CHUNK_MAP_GET_PLAYER_CHUNK;
/*      */   public static Method PLAYER_CHUNK_GET_CHUNK;
/*      */   public static Method LEGACY_DATA_WATCHER_WRITE_METHOD;
/*      */   public static Method CLIENTBOUND_SET_ENTITY_DATA_PACKET_WRITE_DATA_WATCHER_METHOD;
/*      */   public static Method GET_DATA_VALUE_FROM_DATA_ITEM_METHOD;
/*      */   private static Constructor<?> NMS_ITEM_STACK_CONSTRUCTOR;
/*      */   private static Constructor<?> NMS_PACKET_DATA_SERIALIZER_CONSTRUCTOR;
/*      */   private static Constructor<?> NMS_MINECRAFT_KEY_CONSTRUCTOR;
/*      */   private static Constructor<?> REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR;
/*      */   private static Constructor<?> BLOCK_POSITION_CONSTRUCTOR;
/*      */   private static Object MINECRAFT_SERVER_INSTANCE;
/*      */   private static Object MINECRAFT_SERVER_CONNECTION_INSTANCE;
/*      */   private static Object MINECRAFT_SERVER_REGISTRY_ACCESS;
/*      */   private static Object ITEM_STACK_OPTIONAL_STREAM_CODEC;
/*      */   private static Object DIMENSION_TYPE_REGISTRY_KEY;
/*      */   private static boolean PAPER_ENTITY_LOOKUP_EXISTS = false;
/*      */   private static boolean PAPER_ENTITY_LOOKUP_LEGACY = true;
/*      */   private static boolean IS_OBFUSCATED;
/*  135 */   public static Map<Integer, Entity> ENTITY_ID_CACHE = (new MapMaker()).weakValues().makeMap();
/*      */   
/*      */   private static void initConstructors() {
/*  138 */     Class<?> itemClass = (NMS_IMATERIAL_CLASS != null) ? NMS_IMATERIAL_CLASS : NMS_ITEM_CLASS;
/*      */     try {
/*  140 */       NMS_ITEM_STACK_CONSTRUCTOR = NMS_ITEM_STACK_CLASS.getConstructor(new Class[] { itemClass, int.class });
/*  141 */       NMS_PACKET_DATA_SERIALIZER_CONSTRUCTOR = NMS_PACKET_DATA_SERIALIZER_CLASS.getConstructor(new Class[] { BYTE_BUF_CLASS });
/*      */       
/*  143 */       if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*  144 */         NMS_MINECRAFT_KEY_CONSTRUCTOR = NMS_MINECRAFT_KEY_CLASS.getDeclaredConstructor(new Class[] { String.class, String.class });
/*  145 */         NMS_MINECRAFT_KEY_CONSTRUCTOR.setAccessible(true);
/*      */       } 
/*  147 */       if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  148 */         REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR = REGISTRY_FRIENDLY_BYTE_BUF.getConstructor(new Class[] { BYTE_BUF_CLASS, REGISTRY_ACCESS });
/*      */       }
/*      */       
/*  151 */       if (BLOCK_POSITION_CLASS != null) {
/*  152 */         BLOCK_POSITION_CONSTRUCTOR = BLOCK_POSITION_CLASS.getConstructor(new Class[] { int.class, int.class, int.class });
/*      */       }
/*  154 */     } catch (NoSuchMethodException e) {
/*  155 */       e.printStackTrace();
/*      */     } 
/*      */   }
/*      */   
/*      */   private static void initMethods() {
/*  160 */     IS_DEBUGGING = Reflection.getMethod(MINECRAFT_SERVER_CLASS, "isDebugging", 0);
/*  161 */     GET_BUKKIT_ENTITY_METHOD = Reflection.getMethod(NMS_ENTITY_CLASS, CRAFT_ENTITY_CLASS, 0);
/*  162 */     GET_CRAFT_PLAYER_HANDLE_METHOD = Reflection.getMethod(CRAFT_PLAYER_CLASS, "getHandle", 0);
/*  163 */     GET_CRAFT_ENTITY_HANDLE_METHOD = Reflection.getMethod(CRAFT_ENTITY_CLASS, "getHandle", 0);
/*  164 */     GET_CRAFT_WORLD_HANDLE_METHOD = Reflection.getMethod(CRAFT_WORLD_CLASS, "getHandle", 0);
/*  165 */     GET_MOB_EFFECT_LIST_ID_METHOD = Reflection.getMethod(MOB_EFFECT_LIST_CLASS, V_1_19_OR_HIGHER ? "g" : "getId", 0);
/*  166 */     GET_MOB_EFFECT_LIST_BY_ID_METHOD = Reflection.getMethod(MOB_EFFECT_LIST_CLASS, V_1_19_OR_HIGHER ? "a" : "fromId", 0);
/*  167 */     GET_ITEM_ID_METHOD = Reflection.getMethod(NMS_ITEM_CLASS, V_1_19_OR_HIGHER ? "g" : "getId", 0);
/*  168 */     GET_ITEM_BY_ID_METHOD = Reflection.getMethod(NMS_ITEM_CLASS, NMS_ITEM_CLASS, 0);
/*  169 */     if (V_1_17_OR_HIGHER) {
/*  170 */       GET_LEVEL_ENTITY_GETTER_ITERABLE_METHOD = Reflection.getMethod(LEVEL_ENTITY_GETTER_CLASS, Iterable.class, 0);
/*  171 */       GET_ENTITY_BY_ID_LEVEL_ENTITY_GETTER_METHOD = Reflection.getMethod(LEVEL_ENTITY_GETTER_CLASS, ENTITY_ACCESS_CLASS, 0, new Class[] { int.class });
/*      */     } 
/*  173 */     if (DIMENSION_MANAGER_CLASS != null) {
/*  174 */       GET_DIMENSION_KEY = Reflection.getMethod(LEVEL_CLASS, "getTypeKey", 0);
/*  175 */       GET_DIMENSION_MANAGER = Reflection.getMethod(LEVEL_CLASS, DIMENSION_MANAGER_CLASS, 0);
/*  176 */       GET_DIMENSION_ID = Reflection.getMethod(DIMENSION_MANAGER_CLASS, int.class, 0);
/*      */     } 
/*  178 */     CODEC_ENCODE_METHOD = Reflection.getMethod(MOJANG_ENCODER_CLASS, "encodeStart", 0);
/*  179 */     DATA_RESULT_GET_METHOD = Reflection.getMethod(DATA_RESULT_CLASS, "result", 0);
/*      */     
/*  181 */     String entityIdMethodName = VERSION.isOlderThan(ServerVersion.V_1_9) ? "a" : (VERSION.isOlderThan(ServerVersion.V_1_17) ? "getEntity" : "b");
/*  182 */     GET_ENTITY_BY_ID_METHOD = Reflection.getMethodExact(SERVER_LEVEL_CLASS, entityIdMethodName, NMS_ENTITY_CLASS, new Class[] { int.class });
/*  183 */     if (GET_ENTITY_BY_ID_METHOD == null) {
/*  184 */       GET_ENTITY_BY_ID_METHOD = Reflection.getMethodExact(SERVER_LEVEL_CLASS, "getEntity", NMS_ENTITY_CLASS, new Class[] { int.class });
/*      */     }
/*      */     
/*  187 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
/*  188 */       BUKKIT_PARTICLE_TO_NMS_ENUM_PARTICLE = Reflection.getMethod(CRAFT_PARTICLE_CLASS, "toNMS", new Class[] { NMS_ENUM_PARTICLE_CLASS });
/*      */       
/*  190 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
/*  191 */         Class<?> particleClass = Reflection.getClassByNameWithoutException("org.bukkit.Particle");
/*  192 */         NMS_ENUM_PARTICLE_TO_BUKKIT_PARTICLE = Reflection.getMethod(CRAFT_PARTICLE_CLASS, "toBukkit", new Class[] { particleClass });
/*      */       } 
/*      */     } 
/*      */     
/*  196 */     CRAFT_ITEM_STACK_AS_BUKKIT_COPY = Reflection.getMethod(CRAFT_ITEM_STACK_CLASS, "asBukkitCopy", 0);
/*  197 */     CRAFT_ITEM_STACK_AS_NMS_COPY = Reflection.getMethod(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", new Class[] { ItemStack.class });
/*      */ 
/*      */     
/*  200 */     READ_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD = Reflection.getMethodExact(NMS_PACKET_DATA_SERIALIZER_CLASS, "k", NMS_ITEM_STACK_CLASS, new Class[0]);
/*  201 */     if (READ_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD == null) {
/*  202 */       READ_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD = Reflection.getMethod(NMS_PACKET_DATA_SERIALIZER_CLASS, NMS_ITEM_STACK_CLASS, 0);
/*      */     }
/*  204 */     WRITE_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD = Reflection.getMethodExact(NMS_PACKET_DATA_SERIALIZER_CLASS, "a", NMS_PACKET_DATA_SERIALIZER_CLASS, new Class[] { NMS_ITEM_STACK_CLASS });
/*  205 */     if (WRITE_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD == null) {
/*  206 */       WRITE_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD = Reflection.getMethod(NMS_PACKET_DATA_SERIALIZER_CLASS, 0, new Class[] { NMS_ITEM_STACK_CLASS });
/*      */     }
/*      */     
/*  209 */     GET_COMBINED_ID = Reflection.getMethod(BLOCK_CLASS, int.class, 0, new Class[] { IBLOCK_DATA_CLASS });
/*  210 */     GET_BY_COMBINED_ID = Reflection.getMethod(BLOCK_CLASS, IBLOCK_DATA_CLASS, 0, new Class[] { int.class });
/*  211 */     if (CRAFT_BLOCK_DATA_CLASS != null) {
/*  212 */       GET_CRAFT_BLOCK_DATA_FROM_IBLOCKDATA = Reflection.getMethodExact(CRAFT_BLOCK_DATA_CLASS, "fromData", CRAFT_BLOCK_DATA_CLASS, new Class[] { IBLOCK_DATA_CLASS });
/*      */     }
/*      */     
/*  215 */     READ_NBT_FROM_STREAM_METHOD = Reflection.getMethod(NBT_COMPRESSION_STREAM_TOOLS_CLASS, 0, new Class[] { DataInputStream.class });
/*  216 */     if (READ_NBT_FROM_STREAM_METHOD == null) {
/*  217 */       if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
/*  218 */         READ_NBT_FROM_STREAM_METHOD = Reflection.getMethod(NBT_COMPRESSION_STREAM_TOOLS_CLASS, 0, new Class[] { DataInput.class, NBT_ACCOUNTER });
/*      */       } else {
/*  220 */         READ_NBT_FROM_STREAM_METHOD = Reflection.getMethod(NBT_COMPRESSION_STREAM_TOOLS_CLASS, 0, new Class[] { DataInput.class });
/*      */       } 
/*      */     }
/*  223 */     if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_2) && VERSION
/*  224 */       .isOlderThan(ServerVersion.V_1_20_5)) {
/*      */       
/*  226 */       WRITE_NBT_TO_STREAM_METHOD = Reflection.getMethod(NBT_COMPRESSION_STREAM_TOOLS_CLASS, "a", new Class[] { NMS_NBT_BASE_CLASS, DataOutput.class });
/*      */     } else {
/*      */       
/*  229 */       WRITE_NBT_TO_STREAM_METHOD = Reflection.getMethod(NBT_COMPRESSION_STREAM_TOOLS_CLASS, 0, new Class[] {
/*  230 */             VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_2) ? 
/*  231 */             NMS_NBT_BASE_CLASS : NMS_NBT_COMPOUND_CLASS, DataOutput.class
/*      */           });
/*      */     } 
/*  234 */     if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_2)) {
/*  235 */       NBT_ACCOUNTER_UNLIMITED_HEAP = Reflection.getMethod(NBT_ACCOUNTER, NBT_ACCOUNTER, 0);
/*      */     }
/*      */     
/*  238 */     if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  239 */       STREAM_DECODER_DECODE = STREAM_DECODER.getMethods()[0];
/*  240 */       STREAM_ENCODER_ENCODE = STREAM_ENCODER.getMethods()[0];
/*      */     } 
/*  242 */     CREATE_REGISTRY_RESOURCE_KEY = Reflection.getMethod(RESOURCE_KEY, 0, new Class[] { NMS_MINECRAFT_KEY_CLASS });
/*  243 */     GET_REGISTRY_OR_THROW = Reflection.getMethod(REGISTRY_ACCESS, 
/*  244 */         VERSION.isNewerThanOrEquals(ServerVersion.V_1_17) ? REGISTRY : WRITABLE_REGISTRY, 0, new Class[] { RESOURCE_KEY });
/*      */     
/*  246 */     GET_DIMENSION_TYPES = Reflection.getMethod(REGISTRY_ACCESS_FROZEN, REGISTRY, 0);
/*  247 */     GET_REGISTRY_ID = Reflection.getMethod(REGISTRY, int.class, 0, new Class[] { Object.class });
/*      */ 
/*      */     
/*  250 */     if (IBLOCKACCESS_CLASS != null) {
/*  251 */       CHUNK_CACHE_GET_IBLOCKACCESS = Reflection.getMethod(CHUNK_PROVIDER_SERVER_CLASS, IBLOCKACCESS_CLASS, 0, new Class[] { int.class, int.class });
/*  252 */       IBLOCKACCESS_GET_BLOCK_DATA = Reflection.getMethod(IBLOCKACCESS_CLASS, IBLOCK_DATA_CLASS, 0);
/*      */     } 
/*  254 */     if (ICHUNKACCESS_CLASS != null) {
/*  255 */       CHUNK_CACHE_GET_ICHUNKACCESS = Reflection.getMethod(CHUNK_PROVIDER_SERVER_CLASS, ICHUNKACCESS_CLASS, 0, new Class[] { int.class, int.class, boolean.class });
/*      */     }
/*  257 */     if (IBLOCK_DATA_CLASS != null) {
/*  258 */       CHUNK_GET_BLOCK_DATA = Reflection.getMethod(CHUNK_CLASS, IBLOCK_DATA_CLASS, 0, new Class[] { BLOCK_POSITION_CLASS });
/*      */     }
/*  260 */     if (PLAYER_CHUNK_CLASS != null) {
/*  261 */       PLAYER_CHUNK_MAP_GET_PLAYER_CHUNK = Reflection.getMethod(PLAYER_CHUNK_MAP_CLASS, PLAYER_CHUNK_CLASS, 0, new Class[] { long.class });
/*      */     }
/*      */     
/*  264 */     if (CHUNK_CLASS != null) {
/*  265 */       PLAYER_CHUNK_GET_CHUNK = Reflection.getMethod(PLAYER_CHUNK_CLASS, CHUNK_CLASS, 0);
/*      */     }
/*      */     
/*  268 */     LEGACY_DATA_WATCHER_WRITE_METHOD = Reflection.getMethod(DATA_WATCHER_CLASS, void.class, 0, new Class[] { NMS_PACKET_DATA_SERIALIZER_CLASS });
/*  269 */     CLIENTBOUND_SET_ENTITY_DATA_PACKET_WRITE_DATA_WATCHER_METHOD = Reflection.getMethod(CLIENTBOUND_SET_ENTITY_DATA_PACKET_CLASS, 0, new Class[] { List.class, REGISTRY_FRIENDLY_BYTE_BUF });
/*  270 */     GET_DATA_VALUE_FROM_DATA_ITEM_METHOD = Reflection.getMethod(DATA_WATCHER_ITEM_CLASS, DATA_WATCHER_VALUE_CLASS, 0);
/*      */   }
/*      */   
/*      */   private static void initFields() {
/*  274 */     ENTITY_BOUNDING_BOX_FIELD = Reflection.getField(NMS_ENTITY_CLASS, BOUNDING_BOX_CLASS, 0, true);
/*  275 */     ENTITY_PLAYER_PING_FIELD = Reflection.getField(ENTITY_PLAYER_CLASS, "ping");
/*  276 */     BYTE_BUF_IN_PACKET_DATA_SERIALIZER = Reflection.getField(NMS_PACKET_DATA_SERIALIZER_CLASS, BYTE_BUF_CLASS, 0, true);
/*  277 */     CRAFT_PARTICLE_PARTICLES_FIELD = Reflection.getField(CRAFT_PARTICLE_CLASS, "particles");
/*  278 */     NMS_MK_KEY_FIELD = Reflection.getField(NMS_MINECRAFT_KEY_CLASS, "key");
/*  279 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
/*  280 */       LEGACY_NMS_PARTICLE_KEY_FIELD = Reflection.getField(NMS_ENUM_PARTICLE_CLASS, "X");
/*  281 */       LEGACY_NMS_KEY_TO_NMS_PARTICLE = Reflection.getField(NMS_ENUM_PARTICLE_CLASS, "ac");
/*      */     } 
/*  283 */     DIMENSION_CODEC_FIELD = Reflection.getField(DIMENSION_MANAGER_CLASS, MOJANG_CODEC_CLASS, 0);
/*  284 */     DYNAMIC_OPS_NBT_INSTANCE_FIELD = Reflection.getField(DYNAMIC_OPS_NBT_CLASS, DYNAMIC_OPS_NBT_CLASS, 0);
/*  285 */     CHUNK_PROVIDER_SERVER_FIELD = Reflection.getField(SERVER_LEVEL_CLASS, CHUNK_PROVIDER_SERVER_CLASS, 0);
/*  286 */     if (CHUNK_PROVIDER_SERVER_FIELD == null) {
/*  287 */       CHUNK_PROVIDER_SERVER_FIELD = Reflection.getField(SERVER_LEVEL_CLASS, ICHUNKPROVIDER_CLASS, 0);
/*      */     }
/*      */     
/*  290 */     PAPER_ENTITY_LOOKUP_EXISTS = (Reflection.getField(SERVER_LEVEL_CLASS, PAPER_ENTITY_LOOKUP_CLASS, 0) != null);
/*  291 */     if (PAPER_ENTITY_LOOKUP_EXISTS)
/*      */     {
/*  293 */       PAPER_ENTITY_LOOKUP_LEGACY = (Reflection.getField(LEVEL_CLASS, PAPER_ENTITY_LOOKUP_CLASS, 0) == null);
/*      */     }
/*      */     
/*  296 */     REMOTE_CHAT_SESSION_FIELD = Reflection.getField(ENTITY_PLAYER_CLASS, REMOTE_CHAT_SESSION_CLASS, 0);
/*  297 */     REGISTRY_KEY_LOCATION_FIELD = Reflection.getField(RESOURCE_KEY, NMS_MINECRAFT_KEY_CLASS, 1);
/*  298 */     DATA_WATCHER_FIELD = Reflection.getField(NMS_ENTITY_CLASS, DATA_WATCHER_CLASS, 0, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private static void initClasses() {
/*  303 */     IS_OBFUSCATED = (Reflection.getClassByNameWithoutException("net.minecraft.server.network.PlayerConnection") != null);
/*      */     
/*  305 */     MINECRAFT_SERVER_CLASS = getServerClass("server.MinecraftServer", "MinecraftServer");
/*  306 */     NMS_PACKET_DATA_SERIALIZER_CLASS = getServerClass(IS_OBFUSCATED ? "network.PacketDataSerializer" : "network.FriendlyByteBuf", "PacketDataSerializer");
/*  307 */     NMS_ITEM_STACK_CLASS = getServerClass("world.item.ItemStack", "ItemStack");
/*  308 */     NMS_IMATERIAL_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.IMaterial" : "world.level.ItemLike", "IMaterial");
/*  309 */     NMS_ENTITY_CLASS = getServerClass("world.entity.Entity", "Entity");
/*  310 */     ENTITY_PLAYER_CLASS = getServerClass(IS_OBFUSCATED ? "server.level.EntityPlayer" : "server.level.ServerPlayer", "EntityPlayer");
/*  311 */     BOUNDING_BOX_CLASS = getServerClass(IS_OBFUSCATED ? "world.phys.AxisAlignedBB" : "world.phys.AABB", "AxisAlignedBB");
/*  312 */     NMS_MINECRAFT_KEY_CLASS = getServerClass(IS_OBFUSCATED ? "resources.MinecraftKey" : "resources.ResourceLocation", "MinecraftKey");
/*  313 */     ENTITY_HUMAN_CLASS = getServerClass(IS_OBFUSCATED ? "world.entity.player.EntityHuman" : "world.entity.player.Player", "EntityHuman");
/*  314 */     PLAYER_CONNECTION_CLASS = getServerClass(IS_OBFUSCATED ? "server.network.PlayerConnection" : "server.network.ServerGamePacketListenerImpl", "PlayerConnection");
/*  315 */     SERVER_LOGIN_PACKET_LISTENER_IMPL_CLASS = getServerClass(IS_OBFUSCATED ? "server.network.LoginListener" : "server.network.ServerLoginPacketListenerImpl", "LoginListener");
/*      */ 
/*      */     
/*  318 */     SERVER_COMMON_PACKETLISTENER_IMPL_CLASS = getServerClass("server.network.ServerCommonPacketListenerImpl", "ServerCommonPacketListenerImpl");
/*      */     
/*  320 */     TRANSFER_COOKIE_CONNECTION_CLASS = getOBCClass("entity.CraftPlayer$TransferCookieConnection");
/*      */     
/*  322 */     SERVER_CONNECTION_CLASS = getServerClass(IS_OBFUSCATED ? "server.network.ServerConnection" : "server.network.ServerConnectionListener", "ServerConnection");
/*  323 */     NETWORK_MANAGER_CLASS = getServerClass(IS_OBFUSCATED ? "network.NetworkManager" : "network.Connection", "NetworkManager");
/*  324 */     MOB_EFFECT_LIST_CLASS = getServerClass(IS_OBFUSCATED ? "world.effect.MobEffectList" : "world.effect.MobEffect", "MobEffectList");
/*  325 */     NMS_ITEM_CLASS = getServerClass("world.item.Item", "Item");
/*  326 */     DEDICATED_SERVER_CLASS = getServerClass("server.dedicated.DedicatedServer", "DedicatedServer");
/*  327 */     LEVEL_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.World" : "world.level.Level", "World");
/*  328 */     SERVER_LEVEL_CLASS = getServerClass(IS_OBFUSCATED ? "server.level.WorldServer" : "server.level.ServerLevel", "WorldServer");
/*  329 */     ENUM_PROTOCOL_DIRECTION_CLASS = getServerClass(IS_OBFUSCATED ? "network.protocol.EnumProtocolDirection" : "network.protocol.PacketFlow", "EnumProtocolDirection");
/*  330 */     if (V_1_17_OR_HIGHER) {
/*  331 */       LEVEL_ENTITY_GETTER_CLASS = getServerClass("world.level.entity.LevelEntityGetter", "");
/*  332 */       PERSISTENT_ENTITY_SECTION_MANAGER_CLASS = getServerClass("world.level.entity.PersistentEntitySectionManager", "");
/*  333 */       PAPER_ENTITY_LOOKUP_CLASS = Reflection.getClassByNameWithoutException("ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup");
/*  334 */       if (PAPER_ENTITY_LOOKUP_CLASS == null)
/*      */       {
/*  336 */         PAPER_ENTITY_LOOKUP_CLASS = Reflection.getClassByNameWithoutException("io.papermc.paper.chunk.system.entity.EntityLookup");
/*      */       }
/*  338 */       ENTITY_ACCESS_CLASS = getServerClass("world.level.entity.EntityAccess", "");
/*      */     } 
/*  340 */     DIMENSION_MANAGER_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.dimension.DimensionManager" : "world.level.dimension.DimensionType", "DimensionManager");
/*  341 */     MOJANG_CODEC_CLASS = Reflection.getClassByNameWithoutException("com.mojang.serialization.Codec");
/*  342 */     MOJANG_ENCODER_CLASS = Reflection.getClassByNameWithoutException("com.mojang.serialization.Encoder");
/*  343 */     DATA_RESULT_CLASS = Reflection.getClassByNameWithoutException("com.mojang.serialization.DataResult");
/*  344 */     DYNAMIC_OPS_NBT_CLASS = getServerClass(IS_OBFUSCATED ? "nbt.DynamicOpsNBT" : "nbt.NbtOps", "DynamicOpsNBT");
/*  345 */     if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
/*  346 */       NMS_ENUM_PARTICLE_CLASS = getServerClass(null, "EnumParticle");
/*      */     }
/*      */     
/*  349 */     CRAFT_MAGIC_NUMBERS_CLASS = getOBCClass("util.CraftMagicNumbers");
/*      */     
/*  351 */     IBLOCK_DATA_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.block.state.IBlockData" : "world.level.block.state.BlockState", "IBlockData");
/*  352 */     BLOCK_CLASS = getServerClass("world.level.block.Block", "Block");
/*  353 */     CRAFT_BLOCK_DATA_CLASS = getOBCClass("block.data.CraftBlockData");
/*      */     
/*  355 */     GAME_PROFILE_CLASS = Reflection.getClassByNameWithoutException("com.mojang.authlib.GameProfile");
/*      */     
/*  357 */     CRAFT_WORLD_CLASS = getOBCClass("CraftWorld");
/*  358 */     CRAFT_PLAYER_CLASS = getOBCClass("entity.CraftPlayer");
/*  359 */     CRAFT_SERVER_CLASS = getOBCClass("CraftServer");
/*  360 */     CRAFT_ENTITY_CLASS = getOBCClass("entity.CraftEntity");
/*  361 */     CRAFT_ITEM_STACK_CLASS = getOBCClass("inventory.CraftItemStack");
/*  362 */     CRAFT_PARTICLE_CLASS = getOBCClass("CraftParticle");
/*      */     
/*  364 */     CHANNEL_CLASS = getNettyClass("channel.Channel");
/*  365 */     BYTE_BUF_CLASS = getNettyClass("buffer.ByteBuf");
/*  366 */     BYTE_TO_MESSAGE_DECODER = getNettyClass("handler.codec.ByteToMessageDecoder");
/*  367 */     MESSAGE_TO_BYTE_ENCODER = getNettyClass("handler.codec.MessageToByteEncoder");
/*  368 */     NMS_NBT_COMPOUND_CLASS = getServerClass(IS_OBFUSCATED ? "nbt.NBTTagCompound" : "nbt.CompoundTag", "NBTTagCompound");
/*  369 */     NMS_NBT_BASE_CLASS = getServerClass(IS_OBFUSCATED ? "nbt.NBTBase" : "nbt.Tag", "NBTBase");
/*  370 */     NBT_COMPRESSION_STREAM_TOOLS_CLASS = getServerClass(IS_OBFUSCATED ? "nbt.NBTCompressedStreamTools" : "nbt.NbtIo", "NBTCompressedStreamTools");
/*  371 */     NBT_ACCOUNTER = getServerClass(IS_OBFUSCATED ? "nbt.NBTReadLimiter" : "nbt.NbtAccounter", "NBTReadLimiter");
/*  372 */     CHUNK_PROVIDER_SERVER_CLASS = getServerClass(IS_OBFUSCATED ? "server.level.ChunkProviderServer" : "server.level.ServerChunkCache", "ChunkProviderServer");
/*  373 */     ICHUNKPROVIDER_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.chunk.IChunkProvider" : "world.level.chunk.ChunkSource", "IChunkProvider");
/*  374 */     CHUNK_STATUS_CLASS = getServerClass("world.level.chunk.status.ChunkStatus", "");
/*  375 */     if (CHUNK_STATUS_CLASS == null) {
/*  376 */       CHUNK_STATUS_CLASS = getServerClass("world.level.ChunkStatus", "");
/*      */     }
/*  378 */     BLOCK_POSITION_CLASS = getServerClass(IS_OBFUSCATED ? "core.BlockPosition" : "core.BlockPos", "BlockPosition");
/*  379 */     PLAYER_CHUNK_MAP_CLASS = getServerClass(IS_OBFUSCATED ? "server.level.PlayerChunkMap" : "server.level.ChunkMap", "");
/*  380 */     PLAYER_CHUNK_CLASS = getServerClass(IS_OBFUSCATED ? "server.level.PlayerChunk" : "server.level.ChunkHolder", "");
/*  381 */     CHUNK_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.chunk.Chunk" : "world.level.chunk.LevelChunk", "Chunk");
/*  382 */     IBLOCKACCESS_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.IBlockAccess" : "world.level.BlockGetter", "IBlockAccess");
/*  383 */     ICHUNKACCESS_CLASS = getServerClass(IS_OBFUSCATED ? "world.level.chunk.IChunkAccess" : "world.level.chunk.ChunkAccess", "IChunkAccess");
/*      */ 
/*      */     
/*  386 */     if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  387 */       STREAM_CODEC = Reflection.getClassByNameWithoutException("net.minecraft.network.codec.StreamCodec");
/*  388 */       STREAM_DECODER = Reflection.getClassByNameWithoutException("net.minecraft.network.codec.StreamDecoder");
/*  389 */       STREAM_ENCODER = Reflection.getClassByNameWithoutException("net.minecraft.network.codec.StreamEncoder");
/*  390 */       REGISTRY_FRIENDLY_BYTE_BUF = Reflection.getClassByNameWithoutException("net.minecraft.network.RegistryFriendlyByteBuf");
/*      */     } 
/*  392 */     REGISTRY_ACCESS = getServerClass(IS_OBFUSCATED ? "core.IRegistryCustom" : "core.RegistryAccess", "IRegistryCustom");
/*  393 */     REGISTRY_ACCESS_FROZEN = getServerClass(IS_OBFUSCATED ? "core.IRegistryCustom$Dimension" : "core.RegistryAccess$Frozen", "IRegistryCustom$Dimension");
/*  394 */     RESOURCE_KEY = getServerClass("resources.ResourceKey", "ResourceKey");
/*  395 */     REGISTRY = getServerClass(IS_OBFUSCATED ? "core.IRegistry" : "core.Registry", "IRegistry");
/*  396 */     WRITABLE_REGISTRY = getServerClass(IS_OBFUSCATED ? "core.IRegistryWritable" : "core.WritableRegistry", "IRegistryWritable");
/*      */     
/*  398 */     REMOTE_CHAT_SESSION_CLASS = Reflection.getClassByNameWithoutException("net.minecraft.network.chat.RemoteChatSession");
/*  399 */     DATA_WATCHER_CLASS = getServerClass("network.syncher.DataWatcher", "DataWatcher");
/*  400 */     if (DATA_WATCHER_CLASS == null) {
/*  401 */       DATA_WATCHER_CLASS = getServerClass("network.syncher.SynchedEntityData", "DataWatcher");
/*      */     }
/*      */     
/*  404 */     CLIENTBOUND_SET_ENTITY_DATA_PACKET_CLASS = getServerClass("network.protocol.game.ClientboundSetEntityDataPacket", "PacketPlayOutEntityMetadata");
/*  405 */     DATA_WATCHER_ITEM_CLASS = NestedClassUtil.getNestedClass(DATA_WATCHER_CLASS, 0);
/*  406 */     DATA_WATCHER_VALUE_CLASS = NestedClassUtil.getNestedClass(DATA_WATCHER_CLASS, 1);
/*      */   }
/*      */   
/*      */   private static void initObjects() {
/*      */     try {
/*  411 */       if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_20_5)) {
/*  412 */         ITEM_STACK_OPTIONAL_STREAM_CODEC = Reflection.getField(NMS_ITEM_STACK_CLASS, STREAM_CODEC, 0).get(null);
/*      */       }
/*  414 */     } catch (IllegalAccessException exception) {
/*  415 */       exception.printStackTrace();
/*      */     } 
/*      */   }
/*      */   
/*      */   public static void init() {
/*  420 */     VERSION = PacketEvents.getAPI().getServerManager().getVersion();
/*  421 */     V_1_19_OR_HIGHER = VERSION.isNewerThanOrEquals(ServerVersion.V_1_19);
/*  422 */     V_1_17_OR_HIGHER = VERSION.isNewerThanOrEquals(ServerVersion.V_1_17);
/*  423 */     V_1_12_OR_HIGHER = VERSION.isNewerThanOrEquals(ServerVersion.V_1_12);
/*      */     
/*  425 */     initClasses();
/*  426 */     initFields();
/*  427 */     initMethods();
/*  428 */     initConstructors();
/*  429 */     initObjects();
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   public static Class<?> getServerClass(String modern, String legacy) {
/*  434 */     if (V_1_17_OR_HIGHER) {
/*  435 */       return Reflection.getClassByNameWithoutException("net.minecraft." + modern);
/*      */     }
/*  437 */     return Reflection.getClassByNameWithoutException(LEGACY_NMS_PACKAGE + legacy);
/*      */   }
/*      */ 
/*      */   
/*      */   public static boolean isMinecraftServerInstanceDebugging() {
/*  442 */     Object minecraftServerInstance = getMinecraftServerInstance(Bukkit.getServer());
/*  443 */     if (minecraftServerInstance != null && IS_DEBUGGING != null) {
/*      */       try {
/*  445 */         return ((Boolean)IS_DEBUGGING.invoke(minecraftServerInstance, new Object[0])).booleanValue();
/*  446 */       } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  447 */         IS_DEBUGGING = null;
/*  448 */         return false;
/*      */       } 
/*      */     }
/*  451 */     return false;
/*      */   }
/*      */   
/*      */   public static Object getMinecraftServerInstance(Server server) {
/*  455 */     if (MINECRAFT_SERVER_INSTANCE == null) {
/*      */       try {
/*  457 */         Field f = Reflection.getField(CRAFT_SERVER_CLASS, MINECRAFT_SERVER_CLASS, 0);
/*  458 */         if (f == null) {
/*      */           
/*  460 */           MINECRAFT_SERVER_INSTANCE = Reflection.getField(MINECRAFT_SERVER_CLASS, MINECRAFT_SERVER_CLASS, 0).get(null);
/*      */         } else {
/*  462 */           MINECRAFT_SERVER_INSTANCE = f.get(server);
/*      */         } 
/*  464 */       } catch (IllegalAccessException e) {
/*  465 */         e.printStackTrace();
/*      */       } 
/*      */     }
/*  468 */     return MINECRAFT_SERVER_INSTANCE;
/*      */   }
/*      */   
/*      */   public static Object getMinecraftServerConnectionInstance() {
/*  472 */     if (MINECRAFT_SERVER_CONNECTION_INSTANCE == null) {
/*      */       try {
/*  474 */         MINECRAFT_SERVER_CONNECTION_INSTANCE = Reflection.getField(MINECRAFT_SERVER_CLASS, SERVER_CONNECTION_CLASS, 0).get(getMinecraftServerInstance(Bukkit.getServer()));
/*  475 */       } catch (IllegalAccessException e) {
/*  476 */         e.printStackTrace();
/*      */       } 
/*      */     }
/*  479 */     return MINECRAFT_SERVER_CONNECTION_INSTANCE;
/*      */   }
/*      */   
/*      */   public static double getTPS() {
/*  483 */     return recentTPS()[0];
/*      */   }
/*      */   
/*      */   public static double[] recentTPS() {
/*  487 */     return (new ReflectionObject(getMinecraftServerInstance(Bukkit.getServer()), MINECRAFT_SERVER_CLASS)).readDoubleArray(0);
/*      */   }
/*      */   
/*      */   public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
/*  491 */     return Class.forName(LEGACY_NMS_PACKAGE + name);
/*      */   }
/*      */   
/*      */   public static Class<?> getOBCClass(String name) {
/*  495 */     return Reflection.getClassByNameWithoutException(OBC_PACKAGE + name);
/*      */   }
/*      */   
/*      */   public static Class<?> getNettyClass(String name) {
/*  499 */     return Reflection.getClassByNameWithoutException("io.netty." + name);
/*      */   }
/*      */   
/*      */   public static Entity getBukkitEntity(Object nmsEntity) {
/*  503 */     Object craftEntity = null;
/*      */     try {
/*  505 */       craftEntity = GET_BUKKIT_ENTITY_METHOD.invoke(nmsEntity, new Object[0]);
/*  506 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  507 */       e.printStackTrace();
/*      */     } 
/*  509 */     return (Entity)craftEntity;
/*      */   }
/*      */   
/*      */   public static Object getNMSEntity(Entity entity) {
/*  513 */     Object craftEntity = CRAFT_ENTITY_CLASS.cast(entity);
/*      */     try {
/*  515 */       return GET_CRAFT_ENTITY_HANDLE_METHOD.invoke(craftEntity, new Object[0]);
/*  516 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  517 */       e.printStackTrace();
/*      */       
/*  519 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object getNMSAxisAlignedBoundingBox(Object nmsEntity) {
/*      */     try {
/*  524 */       return ENTITY_BOUNDING_BOX_FIELD.get(NMS_ENTITY_CLASS.cast(nmsEntity));
/*  525 */     } catch (IllegalAccessException e) {
/*  526 */       e.printStackTrace();
/*      */       
/*  528 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object getCraftPlayer(Player player) {
/*  532 */     return CRAFT_PLAYER_CLASS.cast(player);
/*      */   }
/*      */   
/*      */   public static Object getEntityPlayer(Player player) {
/*  536 */     Object craftPlayer = getCraftPlayer(player);
/*      */     try {
/*  538 */       return GET_CRAFT_PLAYER_HANDLE_METHOD.invoke(craftPlayer, new Object[0]);
/*  539 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  540 */       e.printStackTrace();
/*      */       
/*  542 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object getPlayerConnection(Player player) {
/*  546 */     Object entityPlayer = getEntityPlayer(player);
/*  547 */     if (entityPlayer == null) {
/*  548 */       return null;
/*      */     }
/*  550 */     ReflectionObject wrappedEntityPlayer = new ReflectionObject(entityPlayer, ENTITY_PLAYER_CLASS);
/*  551 */     if (TRANSFER_COOKIE_CONNECTION_CLASS != null) {
/*  552 */       return wrappedEntityPlayer.readObject(0, TRANSFER_COOKIE_CONNECTION_CLASS);
/*      */     }
/*  554 */     return wrappedEntityPlayer.readObject(0, PLAYER_CONNECTION_CLASS);
/*      */   }
/*      */   
/*      */   public static Object getGameProfile(Player player) {
/*  558 */     Object entityPlayer = getEntityPlayer(player);
/*  559 */     ReflectionObject entityHumanWrapper = new ReflectionObject(entityPlayer, ENTITY_HUMAN_CLASS);
/*  560 */     return entityHumanWrapper.readObject(0, GAME_PROFILE_CLASS);
/*      */   }
/*      */   
/*      */   public static List<TextureProperty> getUserProfile(Player player) {
/*  564 */     if (PROPERTY_MAP_CLASS == null) {
/*  565 */       PROPERTY_MAP_CLASS = Reflection.getClassByNameWithoutException("com.mojang.authlib.properties.PropertyMap");
/*      */       
/*  567 */       PROPERTY_MAP_GET_METHOD = Reflection.getMethodExact(PROPERTY_MAP_CLASS, "get", Collection.class, new Class[] { Object.class });
/*      */     } 
/*      */ 
/*      */     
/*  571 */     Object nmsGameProfile = getGameProfile(player);
/*  572 */     ReflectionObject reflectGameProfile = new ReflectionObject(nmsGameProfile);
/*  573 */     Object nmsPropertyMap = reflectGameProfile.readObject(0, PROPERTY_MAP_CLASS);
/*      */     
/*  575 */     Collection<Object> nmsProperties = null;
/*      */     
/*      */     try {
/*  578 */       nmsProperties = (Collection<Object>)PROPERTY_MAP_GET_METHOD.invoke(nmsPropertyMap, new Object[] { "textures" });
/*  579 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  580 */       e.printStackTrace();
/*      */     } 
/*  582 */     List<TextureProperty> properties = new ArrayList<>();
/*      */     
/*  584 */     for (Object nmsProperty : nmsProperties) {
/*      */       
/*  586 */       ReflectionObject reflectProperty = new ReflectionObject(nmsProperty);
/*  587 */       String name = "textures";
/*  588 */       String value = reflectProperty.readString(1);
/*  589 */       String signature = reflectProperty.readString(2);
/*  590 */       TextureProperty textureProperty = new TextureProperty(name, value, signature);
/*      */       
/*  592 */       properties.add(textureProperty);
/*      */     } 
/*      */     
/*  595 */     return properties;
/*      */   }
/*      */   public static Object getNetworkManager(Player player) {
/*      */     Class<?> playerConnectionClass;
/*  599 */     Object playerConnection = getPlayerConnection(player);
/*  600 */     if (playerConnection == null) {
/*  601 */       return null;
/*      */     }
/*      */     
/*  604 */     if (SERVER_COMMON_PACKETLISTENER_IMPL_CLASS != null) {
/*      */       
/*  606 */       playerConnectionClass = (playerConnection.getClass() == SERVER_LOGIN_PACKET_LISTENER_IMPL_CLASS) ? SERVER_LOGIN_PACKET_LISTENER_IMPL_CLASS : SERVER_COMMON_PACKETLISTENER_IMPL_CLASS;
/*      */     } else {
/*  608 */       playerConnectionClass = PLAYER_CONNECTION_CLASS;
/*      */     } 
/*  610 */     ReflectionObject wrapper = new ReflectionObject(playerConnection, playerConnectionClass);
/*      */     try {
/*  612 */       return wrapper.readObject(0, NETWORK_MANAGER_CLASS);
/*  613 */     } catch (Exception ex) {
/*      */       
/*      */       try {
/*  616 */         playerConnection = wrapper.read(0, PLAYER_CONNECTION_CLASS);
/*  617 */         wrapper = new ReflectionObject(playerConnection, PLAYER_CONNECTION_CLASS);
/*  618 */         return wrapper.readObject(0, NETWORK_MANAGER_CLASS);
/*  619 */       } catch (Exception ex2) {
/*      */         
/*  621 */         ex.printStackTrace();
/*      */ 
/*      */         
/*  624 */         return null;
/*      */       } 
/*      */     } 
/*      */   } public static Object getChannel(Player player) {
/*  628 */     Object networkManager = getNetworkManager(player);
/*  629 */     if (networkManager == null) {
/*  630 */       return null;
/*      */     }
/*  632 */     ReflectionObject wrapper = new ReflectionObject(networkManager, NETWORK_MANAGER_CLASS);
/*  633 */     return wrapper.readObject(0, CHANNEL_CLASS);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public static int getPlayerPingLegacy(Player player) {
/*  638 */     if (V_1_17_OR_HIGHER) {
/*  639 */       return -1;
/*      */     }
/*  641 */     if (ENTITY_PLAYER_PING_FIELD != null) {
/*  642 */       Object entityPlayer = getEntityPlayer(player);
/*      */       try {
/*  644 */         return ENTITY_PLAYER_PING_FIELD.getInt(entityPlayer);
/*  645 */       } catch (IllegalAccessException e) {
/*  646 */         e.printStackTrace();
/*      */       } 
/*      */     } 
/*  649 */     return -1;
/*      */   }
/*      */   
/*      */   public static List<Object> getNetworkManagers() {
/*  653 */     ReflectionObject serverConnectionWrapper = new ReflectionObject(getMinecraftServerConnectionInstance());
/*  654 */     for (int i = 0;; i++) {
/*      */       try {
/*  656 */         List<?> list = (List)serverConnectionWrapper.readObject(i, List.class);
/*  657 */         for (Object obj : list) {
/*  658 */           if (obj.getClass().isAssignableFrom(NETWORK_MANAGER_CLASS)) {
/*  659 */             return (List)list;
/*      */           }
/*      */         } 
/*  662 */       } catch (Exception ex) {
/*      */         break;
/*      */       } 
/*      */     } 
/*      */     
/*  667 */     return (List<Object>)serverConnectionWrapper.readObject(1, List.class);
/*      */   }
/*      */   
/*      */   public static Object convertBukkitServerToNMSServer(Server server) {
/*  671 */     Object craftServer = CRAFT_SERVER_CLASS.cast(server);
/*  672 */     ReflectionObject wrapper = new ReflectionObject(craftServer);
/*      */     try {
/*  674 */       return wrapper.readObject(0, MINECRAFT_SERVER_CLASS);
/*  675 */     } catch (Exception ex) {
/*  676 */       wrapper.readObject(0, DEDICATED_SERVER_CLASS);
/*      */       
/*  678 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object convertBukkitWorldToWorldServer(World world) {
/*  682 */     Object craftWorld = CRAFT_WORLD_CLASS.cast(world);
/*      */     try {
/*  684 */       return GET_CRAFT_WORLD_HANDLE_METHOD.invoke(craftWorld, new Object[0]);
/*  685 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  686 */       e.printStackTrace();
/*      */       
/*  688 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object convertWorldServerDimensionToNMSNbt(Object worldServer) {
/*      */     try {
/*  693 */       Object dimensionType = GET_DIMENSION_MANAGER.invoke(worldServer, new Object[0]);
/*  694 */       Object dimensionTypeCodec = DIMENSION_CODEC_FIELD.get(null);
/*  695 */       Object nbtOps = DYNAMIC_OPS_NBT_INSTANCE_FIELD.get(null);
/*  696 */       if (VERSION.isOlderThan(ServerVersion.V_1_16_2)) {
/*  697 */         Object finalDimensionType = dimensionType;
/*  698 */         dimensionType = (() -> finalDimensionType);
/*      */       } 
/*  700 */       Object encodedDimType = CODEC_ENCODE_METHOD.invoke(dimensionTypeCodec, new Object[] { nbtOps, dimensionType });
/*  701 */       Optional<?> optionalDimType = (Optional)DATA_RESULT_GET_METHOD.invoke(encodedDimType, new Object[0]);
/*  702 */       return optionalDimType.orElse(null);
/*  703 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  704 */       e.printStackTrace();
/*      */       
/*  706 */       return null;
/*      */     } 
/*      */   }
/*      */   public static int getDimensionId(Object worldServer) {
/*      */     try {
/*  711 */       Object dimensionTypeRegistry, dimensionType = GET_DIMENSION_MANAGER.invoke(worldServer, new Object[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  716 */       if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_16_2)) {
/*  717 */         if (DIMENSION_TYPE_REGISTRY_KEY == null) {
/*  718 */           Object registryKeyLoc = NMS_MINECRAFT_KEY_CONSTRUCTOR.newInstance(new Object[] { "minecraft", "dimension_type" });
/*  719 */           DIMENSION_TYPE_REGISTRY_KEY = CREATE_REGISTRY_RESOURCE_KEY.invoke(null, new Object[] { registryKeyLoc });
/*      */         } 
/*  721 */         dimensionTypeRegistry = GET_REGISTRY_OR_THROW.invoke(getFrozenRegistryAccess(), new Object[] { DIMENSION_TYPE_REGISTRY_KEY });
/*      */       } else {
/*  723 */         dimensionTypeRegistry = GET_DIMENSION_TYPES.invoke(getFrozenRegistryAccess(), new Object[0]);
/*      */       } 
/*  725 */       return ((Integer)GET_REGISTRY_ID.invoke(dimensionTypeRegistry, new Object[] { dimensionType })).intValue();
/*  726 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|InstantiationException e) {
/*  727 */       e.printStackTrace();
/*      */       
/*  729 */       return 0;
/*      */     } 
/*      */   }
/*      */   
/*      */   public static String getDimensionKey(Object worldServer) {
/*      */     try {
/*  735 */       Object resourceKey = GET_DIMENSION_KEY.invoke(worldServer, new Object[0]);
/*  736 */       return REGISTRY_KEY_LOCATION_FIELD.get(resourceKey).toString();
/*  737 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  738 */       e.printStackTrace();
/*      */       
/*  740 */       return null;
/*      */     } 
/*      */   }
/*      */   public static String fromStringToJSON(String message) {
/*  744 */     if (message == null) {
/*  745 */       return null;
/*      */     }
/*  747 */     return "{\"text\": \"" + message + "\"}";
/*      */   }
/*      */   
/*      */   public static int generateEntityId() {
/*  751 */     Field field = Reflection.getField(NMS_ENTITY_CLASS, "entityCount");
/*  752 */     if (field == null) {
/*  753 */       field = Reflection.getField(NMS_ENTITY_CLASS, AtomicInteger.class, 0);
/*      */     }
/*      */     try {
/*  756 */       if (field.getType().equals(AtomicInteger.class)) {
/*      */         
/*  758 */         AtomicInteger atomicInteger = (AtomicInteger)field.get(null);
/*  759 */         return atomicInteger.incrementAndGet();
/*      */       } 
/*  761 */       int id = field.getInt(null);
/*  762 */       field.set(null, Integer.valueOf(id + 1));
/*  763 */       return id;
/*      */     }
/*  765 */     catch (IllegalAccessException ex) {
/*  766 */       ex.printStackTrace();
/*      */       
/*  768 */       throw new IllegalStateException("Failed to generate a new unique entity ID!");
/*      */     } 
/*      */   }
/*      */   public static int getEffectId(Object nmsMobEffectList) {
/*      */     try {
/*  773 */       return ((Integer)GET_MOB_EFFECT_LIST_ID_METHOD.invoke(null, new Object[] { nmsMobEffectList })).intValue();
/*  774 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  775 */       e.printStackTrace();
/*      */       
/*  777 */       return -1;
/*      */     } 
/*      */   }
/*      */   public static Object getMobEffectListById(int effectID) {
/*      */     try {
/*  782 */       return GET_MOB_EFFECT_LIST_BY_ID_METHOD.invoke(null, new Object[] { Integer.valueOf(effectID) });
/*  783 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  784 */       e.printStackTrace();
/*      */       
/*  786 */       return null;
/*      */     } 
/*      */   }
/*      */   public static int getNMSItemId(Object nmsItem) {
/*      */     try {
/*  791 */       return ((Integer)GET_ITEM_ID_METHOD.invoke(null, new Object[] { nmsItem })).intValue();
/*  792 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  793 */       e.printStackTrace();
/*      */       
/*  795 */       return -1;
/*      */     } 
/*      */   }
/*      */   public static Object getNMSItemById(int id) {
/*      */     try {
/*  800 */       return GET_ITEM_BY_ID_METHOD.invoke(null, new Object[] { Integer.valueOf(id) });
/*  801 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  802 */       e.printStackTrace();
/*      */       
/*  804 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object createNMSItemStack(Object nmsItem, int count) {
/*      */     try {
/*  809 */       return NMS_ITEM_STACK_CONSTRUCTOR.newInstance(new Object[] { nmsItem, Integer.valueOf(count) });
/*  810 */     } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  811 */       e.printStackTrace();
/*      */       
/*  813 */       return null;
/*      */     } 
/*      */   }
/*      */   public static ItemStack decodeBukkitItemStack(ItemStack in) {
/*  817 */     Object buffer = PooledByteBufAllocator.DEFAULT.buffer();
/*      */     
/*      */     try {
/*  820 */       Object packetDataSerializer = createPacketDataSerializer(buffer);
/*  821 */       Object nmsItemStack = toNMSItemStack(in);
/*  822 */       writeNMSItemStackPacketDataSerializer(packetDataSerializer, nmsItemStack);
/*      */       
/*  824 */       PacketWrapper<?> wrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
/*  825 */       ItemStack stack = wrapper.readItemStack();
/*  826 */       return stack;
/*      */     } finally {
/*  828 */       ByteBufHelper.release(buffer);
/*      */     } 
/*      */   }
/*      */   
/*      */   public static ItemStack encodeBukkitItemStack(ItemStack in) {
/*  833 */     Object buffer = PooledByteBufAllocator.DEFAULT.buffer();
/*      */     try {
/*  835 */       PacketWrapper<?> wrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
/*  836 */       wrapper.writeItemStack(in);
/*      */       
/*  838 */       Object packetDataSerializer = createPacketDataSerializer(wrapper.getBuffer());
/*  839 */       Object nmsItemStack = readNMSItemStackPacketDataSerializer(packetDataSerializer);
/*  840 */       ItemStack stack = toBukkitItemStack(nmsItemStack);
/*  841 */       return stack;
/*      */     } finally {
/*  843 */       ByteBufHelper.release(buffer);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getBlockDataCombinedId(MaterialData materialData) {
/*      */     int combinedID;
/*  851 */     if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/*  852 */       combinedID = -1;
/*      */     } else {
/*  854 */       combinedID = materialData.getItemType().getId() << 4 | materialData.getData();
/*      */     } 
/*      */     
/*  857 */     return combinedID;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static MaterialData getBlockDataByCombinedId(int combinedID) {
/*  870 */     Object iBlockDataObj = null;
/*      */     try {
/*  872 */       iBlockDataObj = GET_BY_COMBINED_ID.invoke(null, new Object[] { Integer.valueOf(combinedID) });
/*  873 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  874 */       e.printStackTrace();
/*      */     } 
/*      */     
/*      */     try {
/*  878 */       Class<?> blockData = Reflection.getClassByNameWithoutException("org.bukkit.block.data.BlockData");
/*  879 */       Object bd = blockData.cast(GET_CRAFT_BLOCK_DATA_FROM_IBLOCKDATA.invoke(null, new Object[] { iBlockDataObj }));
/*  880 */       Method materialMethod = Reflection.getMethod(blockData, Material.class, 0);
/*  881 */       return new MaterialData((Material)materialMethod.invoke(bd, new Object[0]));
/*  882 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  883 */       e.printStackTrace();
/*      */ 
/*      */       
/*  886 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object createNMSItemStack(int itemID, int count) {
/*      */     try {
/*  891 */       Object nmsItem = getNMSItemById(itemID);
/*  892 */       return NMS_ITEM_STACK_CONSTRUCTOR.newInstance(new Object[] { nmsItem, Integer.valueOf(count) });
/*  893 */     } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  894 */       e.printStackTrace();
/*      */       
/*  896 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object createPacketDataSerializer(Object byteBuf) {
/*      */     try {
/*  901 */       if (REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR != null) {
/*  902 */         return REGISTRY_FRIENDLY_BYTE_BUF_CONSTRUCTOR.newInstance(new Object[] { byteBuf, getFrozenRegistryAccess() });
/*      */       }
/*  904 */       return NMS_PACKET_DATA_SERIALIZER_CONSTRUCTOR.newInstance(new Object[] { byteBuf });
/*  905 */     } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  906 */       e.printStackTrace();
/*      */       
/*  908 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object createBlockPosition(int x, int y, int z) {
/*      */     try {
/*  913 */       return BLOCK_POSITION_CONSTRUCTOR.newInstance(new Object[] { Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z) });
/*  914 */     } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  915 */       e.printStackTrace();
/*      */       
/*  917 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object getFrozenRegistryAccess() {
/*  921 */     if (MINECRAFT_SERVER_REGISTRY_ACCESS == null) {
/*      */       try {
/*  923 */         if (VERSION.isNewerThanOrEquals(ServerVersion.V_1_16_2)) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  928 */           MINECRAFT_SERVER_REGISTRY_ACCESS = Reflection.getMethod(MINECRAFT_SERVER_CLASS, VERSION.isNewerThanOrEquals(ServerVersion.V_1_18_2) ? REGISTRY_ACCESS_FROZEN : REGISTRY_ACCESS, 0).invoke(getMinecraftServerInstance(Bukkit.getServer()), new Object[0]);
/*      */         }
/*      */         else {
/*      */           
/*  932 */           MINECRAFT_SERVER_REGISTRY_ACCESS = Reflection.getField(MINECRAFT_SERVER_CLASS, REGISTRY_ACCESS_FROZEN, 0).get(getMinecraftServerInstance(Bukkit.getServer()));
/*      */         } 
/*  934 */       } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException exception) {
/*  935 */         exception.printStackTrace();
/*      */       } 
/*      */     }
/*  938 */     return MINECRAFT_SERVER_REGISTRY_ACCESS;
/*      */   }
/*      */   
/*      */   public static ItemStack toBukkitItemStack(Object nmsItemStack) {
/*      */     try {
/*  943 */       return (ItemStack)CRAFT_ITEM_STACK_AS_BUKKIT_COPY.invoke(null, new Object[] { nmsItemStack });
/*  944 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  945 */       e.printStackTrace();
/*      */       
/*  947 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object toNMSItemStack(ItemStack itemStack) {
/*      */     try {
/*  952 */       return CRAFT_ITEM_STACK_AS_NMS_COPY.invoke(null, new Object[] { itemStack });
/*  953 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  954 */       e.printStackTrace();
/*      */       
/*  956 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public static Object readNMSItemStackPacketDataSerializer(Object packetDataSerializer) {
/*      */     try {
/*  962 */       if (READ_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD != null) {
/*  963 */         return READ_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD.invoke(packetDataSerializer, new Object[0]);
/*      */       }
/*  965 */       return STREAM_DECODER_DECODE.invoke(ITEM_STACK_OPTIONAL_STREAM_CODEC, new Object[] { packetDataSerializer });
/*  966 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  967 */       e.printStackTrace();
/*      */       
/*  969 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object writeNMSItemStackPacketDataSerializer(Object packetDataSerializer, Object nmsItemStack) {
/*      */     try {
/*  974 */       if (WRITE_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD != null) {
/*  975 */         return WRITE_ITEM_STACK_IN_PACKET_DATA_SERIALIZER_METHOD.invoke(packetDataSerializer, new Object[] { nmsItemStack });
/*      */       }
/*  977 */       return STREAM_ENCODER_ENCODE.invoke(ITEM_STACK_OPTIONAL_STREAM_CODEC, new Object[] { packetDataSerializer, nmsItemStack });
/*  978 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/*  979 */       e.printStackTrace();
/*      */       
/*  981 */       return null;
/*      */     } 
/*      */   } public static NBTCompound fromMinecraftNBT(Object nbtCompound) {
/*      */     byte[] bytes;
/*      */     
/*  986 */     try { ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); 
/*  987 */       try { DataOutputStream stream = new DataOutputStream(byteStream); 
/*  988 */         try { writeNmsNbtToStream(nbtCompound, stream);
/*  989 */           bytes = byteStream.toByteArray();
/*  990 */           stream.close(); } catch (Throwable throwable) { try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  byteStream.close(); } catch (Throwable throwable) { try { byteStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/*  991 */     { e.printStackTrace();
/*  992 */       return null; }
/*      */ 
/*      */     
/*  995 */     Object buffer = UnpooledByteBufAllocationHelper.wrappedBuffer(bytes);
/*      */     try {
/*  997 */       PacketWrapper<?> wrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
/*  998 */       return wrapper.readNBT();
/*      */     } finally {
/* 1000 */       ByteBufHelper.release(buffer);
/*      */     } 
/*      */   }
/*      */   
/*      */   public static Object toMinecraftNBT(NBTCompound nbtCompound) {
/*      */     byte[] bytes;
/* 1006 */     Object buffer = UnpooledByteBufAllocationHelper.buffer();
/*      */     try {
/* 1008 */       PacketWrapper<?> wrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
/* 1009 */       wrapper.writeNBT(nbtCompound);
/* 1010 */       bytes = ByteBufHelper.copyBytes(buffer);
/*      */     } finally {
/* 1012 */       ByteBufHelper.release(buffer);
/*      */     }  
/* 1014 */     try { ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes); 
/* 1015 */       try { DataInputStream stream = new DataInputStream(byteStream); 
/* 1016 */         try { Object object = readNmsNbtFromStream(stream);
/* 1017 */           stream.close(); byteStream.close(); return object; } catch (Throwable throwable) { try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Throwable throwable) { try { byteStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/* 1018 */     { e.printStackTrace();
/* 1019 */       return null; }
/*      */   
/*      */   }
/*      */   
/*      */   public static void writeNmsNbtToStream(Object compound, DataOutput out) {
/*      */     try {
/* 1025 */       WRITE_NBT_TO_STREAM_METHOD.invoke(null, new Object[] { compound, out });
/* 1026 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 1027 */       e.printStackTrace();
/*      */     } 
/*      */   }
/*      */   
/*      */   public static Object readNmsNbtFromStream(DataInputStream in) {
/*      */     try {
/* 1033 */       if (NBT_ACCOUNTER_UNLIMITED_HEAP != null) {
/* 1034 */         Object nbtAccounterUnlimitedHeap = NBT_ACCOUNTER_UNLIMITED_HEAP.invoke(null, new Object[0]);
/* 1035 */         return READ_NBT_FROM_STREAM_METHOD.invoke(null, new Object[] { in, nbtAccounterUnlimitedHeap });
/*      */       } 
/* 1037 */       return READ_NBT_FROM_STREAM_METHOD.invoke(null, new Object[] { in });
/* 1038 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 1039 */       e.printStackTrace();
/*      */       
/* 1041 */       return null;
/*      */     } 
/*      */   } @Nullable
/*      */   private static Entity getEntityByIdWithWorldUnsafe(World world, int id) {
/* 1045 */     if (world == null) {
/* 1046 */       return null;
/*      */     }
/* 1048 */     Entity cachedEntity = ENTITY_ID_CACHE.getOrDefault(Integer.valueOf(id), null);
/* 1049 */     if (cachedEntity != null) {
/* 1050 */       return cachedEntity;
/*      */     }
/*      */     try {
/* 1053 */       Object nmsEntity, serverLevel = GET_CRAFT_WORLD_HANDLE_METHOD.invoke(world, new Object[0]);
/*      */ 
/*      */       
/* 1056 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
/*      */         Object levelEntityGetter;
/*      */         
/* 1059 */         ReflectionObject reflectObj = PAPER_ENTITY_LOOKUP_LEGACY ? new ReflectionObject(serverLevel, SERVER_LEVEL_CLASS) : new ReflectionObject(serverLevel, LEVEL_CLASS);
/*      */         
/* 1061 */         if (PAPER_ENTITY_LOOKUP_EXISTS) {
/* 1062 */           levelEntityGetter = reflectObj.readObject(0, PAPER_ENTITY_LOOKUP_CLASS);
/*      */         } else {
/* 1064 */           Object entitySectionManager = reflectObj.readObject(0, PERSISTENT_ENTITY_SECTION_MANAGER_CLASS);
/* 1065 */           ReflectionObject reflectEntitySectionManager = new ReflectionObject(entitySectionManager);
/* 1066 */           levelEntityGetter = reflectEntitySectionManager.readObject(0, LEVEL_ENTITY_GETTER_CLASS);
/*      */         } 
/* 1068 */         nmsEntity = GET_ENTITY_BY_ID_LEVEL_ENTITY_GETTER_METHOD.invoke(levelEntityGetter, new Object[] { Integer.valueOf(id) });
/*      */       } else {
/* 1070 */         nmsEntity = GET_ENTITY_BY_ID_METHOD.invoke(serverLevel, new Object[] { Integer.valueOf(id) });
/*      */       } 
/* 1072 */       if (nmsEntity == null) {
/* 1073 */         return null;
/*      */       }
/* 1075 */       Entity entity = getBukkitEntity(nmsEntity);
/* 1076 */       ENTITY_ID_CACHE.put(Integer.valueOf(id), entity);
/* 1077 */       return entity;
/* 1078 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException exception) {
/* 1079 */       throw new RuntimeException("Error while looking up entity by id " + id + " in " + world, exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @Nullable
/*      */   public static Entity getEntityById(@Nullable World origin, int id) {
/* 1090 */     if (origin != null) {
/* 1091 */       Entity e = getEntityByIdWithWorldUnsafe(origin, id);
/* 1092 */       if (e != null) {
/* 1093 */         return e;
/*      */       }
/*      */     } 
/*      */     
/* 1097 */     for (World world : Bukkit.getWorlds()) {
/* 1098 */       Entity entity = getEntityByIdWithWorldUnsafe(world, id);
/* 1099 */       if (entity != null) {
/* 1100 */         return entity;
/*      */       }
/*      */     } 
/* 1103 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   @Nullable
/*      */   public static Entity getEntityById(int entityID) {
/* 1113 */     return getEntityById(null, entityID);
/*      */   }
/*      */   
/*      */   public static List<Entity> getEntityList(World world) {
/* 1117 */     if (V_1_17_OR_HIGHER) {
/* 1118 */       Object levelEntityGetter, worldServer = convertBukkitWorldToWorldServer(world);
/* 1119 */       ReflectionObject wrappedWorldServer = new ReflectionObject(worldServer);
/*      */       
/* 1121 */       if (PAPER_ENTITY_LOOKUP_EXISTS) {
/* 1122 */         if (!PAPER_ENTITY_LOOKUP_LEGACY)
/*      */         {
/* 1124 */           wrappedWorldServer = new ReflectionObject(worldServer, LEVEL_CLASS);
/*      */         }
/* 1126 */         levelEntityGetter = wrappedWorldServer.readObject(0, PAPER_ENTITY_LOOKUP_CLASS);
/*      */       } else {
/* 1128 */         Object persistentEntitySectionManager = wrappedWorldServer.readObject(0, PERSISTENT_ENTITY_SECTION_MANAGER_CLASS);
/* 1129 */         ReflectionObject wrappedPersistentEntitySectionManager = new ReflectionObject(persistentEntitySectionManager);
/* 1130 */         levelEntityGetter = wrappedPersistentEntitySectionManager.readObject(0, LEVEL_ENTITY_GETTER_CLASS);
/*      */       } 
/* 1132 */       Iterable<Object> nmsEntitiesIterable = null;
/*      */       try {
/* 1134 */         nmsEntitiesIterable = (Iterable<Object>)GET_LEVEL_ENTITY_GETTER_ITERABLE_METHOD.invoke(levelEntityGetter, new Object[0]);
/* 1135 */       } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 1136 */         e.printStackTrace();
/*      */       } 
/* 1138 */       List<Entity> entityList = new ArrayList<>();
/* 1139 */       if (nmsEntitiesIterable != null) {
/* 1140 */         for (Object nmsEntity : nmsEntitiesIterable) {
/* 1141 */           Entity bukkitEntity = getBukkitEntity(nmsEntity);
/* 1142 */           entityList.add(bukkitEntity);
/*      */         } 
/*      */       }
/* 1145 */       return entityList;
/*      */     } 
/* 1147 */     return world.getEntities();
/*      */   }
/*      */ 
/*      */   
/*      */   public static ParticleType<?> toPacketEventsParticle(Enum<?> particle) {
/*      */     try {
/* 1153 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 1154 */         if (CRAFT_PARTICLE_PARTICLES_FIELD == null) {
/* 1155 */           return ParticleTypes.getByName(((Particle)particle).getKey().toString());
/*      */         }
/*      */         
/* 1158 */         BiMap<?, ?> map = (BiMap<?, ?>)CRAFT_PARTICLE_PARTICLES_FIELD.get(null);
/*      */ 
/*      */         
/* 1161 */         if (particle.name().equals("BLOCK_DUST")) {
/* 1162 */           particle = Enum.valueOf(particle.getClass(), "BLOCK_CRACK");
/*      */         }
/* 1164 */         Object object = map.get(particle);
/* 1165 */         return ParticleTypes.getByName(object.toString());
/*      */       } 
/* 1167 */       Object nmsParticle = BUKKIT_PARTICLE_TO_NMS_ENUM_PARTICLE.invoke(null, new Object[] { particle });
/* 1168 */       String key = (String)LEGACY_NMS_PARTICLE_KEY_FIELD.get(nmsParticle);
/* 1169 */       Object minecraftKey = NMS_MINECRAFT_KEY_CONSTRUCTOR.newInstance(new Object[] { "minecraft", key });
/* 1170 */       return ParticleTypes.getByName(minecraftKey.toString());
/*      */     }
/* 1172 */     catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|InstantiationException e) {
/* 1173 */       e.printStackTrace();
/*      */       
/* 1175 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Enum<?> fromPacketEventsParticle(ParticleType<?> particle) {
/*      */     try {
/* 1180 */       if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
/* 1181 */         if (CRAFT_PARTICLE_PARTICLES_FIELD == null) {
/* 1182 */           ResourceLocation particleName = particle.getName();
/* 1183 */           return (Enum)Registry.PARTICLE_TYPE.get(new NamespacedKey(particleName
/* 1184 */                 .getNamespace(), particleName.getKey()));
/*      */         } 
/*      */         
/* 1187 */         BiMap<?, ?> map = (BiMap<?, ?>)CRAFT_PARTICLE_PARTICLES_FIELD.get(null);
/* 1188 */         Object minecraftKey = NMS_MINECRAFT_KEY_CONSTRUCTOR.newInstance(new Object[] { particle.getName().getNamespace(), particle.getName().getKey() });
/* 1189 */         Object object1 = map.inverse().get(minecraftKey);
/* 1190 */         return (Enum)object1;
/*      */       } 
/* 1192 */       Map<String, ?> keyToParticleMap = (Map<String, ?>)LEGACY_NMS_KEY_TO_NMS_PARTICLE.get(null);
/* 1193 */       Object enumParticle = keyToParticleMap.get(particle.getName().getKey());
/* 1194 */       Object bukkitParticle = NMS_ENUM_PARTICLE_TO_BUKKIT_PARTICLE.invoke(null, new Object[] { enumParticle });
/* 1195 */       return (Enum)bukkitParticle;
/*      */     }
/* 1197 */     catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|InstantiationException e) {
/* 1198 */       e.printStackTrace();
/*      */       
/* 1200 */       return null;
/*      */     } 
/*      */   }
/*      */   public static Object getRemoteChatSession(Player player) {
/* 1204 */     Object entityPlayer = getEntityPlayer(player);
/*      */     try {
/* 1206 */       return REMOTE_CHAT_SESSION_FIELD.get(entityPlayer);
/* 1207 */     } catch (IllegalAccessException e) {
/* 1208 */       e.printStackTrace();
/* 1209 */       return null;
/*      */     } 
/*      */   }
/*      */   
/*      */   public static List<EntityData<?>> getEntityMetadata(@NotNull Entity entity) {
/* 1214 */     Object byteBuf = PacketEvents.getAPI().getNettyManager().getByteBufAllocationOperator().buffer();
/*      */     try {
/* 1216 */       Object handle = getNMSEntity(entity);
/* 1217 */       Object dataWatcher = DATA_WATCHER_FIELD.get(handle);
/*      */       
/* 1219 */       Object packetDataSerializer = createPacketDataSerializer(byteBuf);
/*      */       
/* 1221 */       if (LEGACY_DATA_WATCHER_WRITE_METHOD != null) {
/*      */         
/* 1223 */         LEGACY_DATA_WATCHER_WRITE_METHOD.invoke(dataWatcher, new Object[] { packetDataSerializer });
/*      */       }
/*      */       else {
/*      */         
/* 1227 */         Object[] dataItems = (new ReflectionObject(dataWatcher)).readObjectArray(0, DATA_WATCHER_ITEM_CLASS);
/* 1228 */         List<Object> dataValues = new ArrayList(dataItems.length);
/* 1229 */         for (Object dataItem : dataItems) {
/* 1230 */           dataValues.add(GET_DATA_VALUE_FROM_DATA_ITEM_METHOD.invoke(dataItem, new Object[0]));
/*      */         }
/* 1232 */         CLIENTBOUND_SET_ENTITY_DATA_PACKET_WRITE_DATA_WATCHER_METHOD.invoke(null, new Object[] { dataValues, packetDataSerializer });
/*      */       } 
/* 1234 */       PacketWrapper<?> packetWrapper = PacketWrapper.createUniversalPacketWrapper(byteBuf);
/* 1235 */       return packetWrapper.readEntityMetadata();
/* 1236 */     } catch (Exception e) {
/* 1237 */       throw new RuntimeException(e);
/*      */     } finally {
/*      */       
/* 1240 */       ByteBufHelper.release(byteBuf);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\SpigotReflectionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */