/*      */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states;
/*      */ 
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTByte;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTNumber;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.serializer.SequentialNBTReader;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Attachment;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Axis;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Bloom;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.CreakingHeartState;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.East;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Face;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Half;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Hinge;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Instrument;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Leaves;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Mode;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.North;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Orientation;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Part;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.SculkSensorPhase;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Shape;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.South;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Thickness;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Tilt;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.TrialSpawnerState;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.Type;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.VerticalDirection;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.enums.West;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateType;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.states.type.StateValue;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.AdventureIndexUtil;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.MappingHelper;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*      */ import java.io.IOException;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumMap;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.function.Function;
/*      */ import java.util.logging.Logger;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public class WrappedBlockState
/*      */ {
/*   70 */   private static final ClientVersion[] MAPPING_VERSION_STEPS = new ClientVersion[] { ClientVersion.V_1_13, ClientVersion.V_1_13_2, ClientVersion.V_1_14, ClientVersion.V_1_15, ClientVersion.V_1_16, ClientVersion.V_1_16_2, ClientVersion.V_1_17, ClientVersion.V_1_19, ClientVersion.V_1_19_3, ClientVersion.V_1_19_4, ClientVersion.V_1_20, ClientVersion.V_1_20_2, ClientVersion.V_1_20_3, ClientVersion.V_1_20_5, ClientVersion.V_1_21_2, ClientVersion.V_1_21_4, ClientVersion.V_1_21_5, ClientVersion.V_1_21_6 };
/*      */   
/*      */   private static final byte[] MAPPING_INDEXES;
/*      */   
/*      */   private static final ClientVersion[] MAPPING_VERSIONS;
/*      */   
/*      */   private static final byte AIR_MAPPING_INDEX = 0;
/*      */   
/*      */   private static final byte LEGACY_MAPPING_INDEX = 1;
/*      */   
/*      */   private static final byte HIGHEST_MAPPING_INDEX;
/*      */   
/*      */   private static final String MAPPINGS_ASSETS_PREFIX = "mappings/data/block_state/";
/*      */   
/*      */   private static final String MAPPINGS_ASSETS_LEGACY = "mappings/data/block_state/legacy";
/*      */   
/*   86 */   private static final boolean PRELOAD_BLOCK_STATE_MAPPINGS = Boolean.getBoolean("packetevents.mappings.preload"); private static final WrappedBlockState AIR; private static final Map<String, WrappedBlockState>[] BY_STRING; private static final Map<Integer, WrappedBlockState>[] BY_ID; private static final Map<WrappedBlockState, String>[] INTO_STRING; private static final Map<WrappedBlockState, Integer>[] INTO_ID; private static final Map<StateType, WrappedBlockState>[] DEFAULT_STATES; private static final Map<String, String> STRING_UPDATER; int globalID; StateType type;
/*      */   
/*      */   static {
/*   89 */     ClientVersion[] versions = ClientVersion.values();
/*   90 */     MAPPING_INDEXES = new byte[versions.length];
/*   91 */     MAPPING_VERSIONS = new ClientVersion[versions.length];
/*      */     
/*   93 */     ClientVersion mappingVersion = versions[0];
/*   94 */     for (int i = 0, j = 0; i < versions.length; i++) {
/*   95 */       ClientVersion version = versions[i];
/*   96 */       if (j < MAPPING_VERSION_STEPS.length && version == MAPPING_VERSION_STEPS[j]) {
/*   97 */         j++;
/*   98 */         mappingVersion = version;
/*      */       } 
/*  100 */       MAPPING_INDEXES[version.ordinal()] = (byte)(1 + j);
/*  101 */       MAPPING_VERSIONS[version.ordinal()] = mappingVersion;
/*      */     } 
/*  103 */     HIGHEST_MAPPING_INDEX = MAPPING_INDEXES[versions.length - 1];
/*      */ 
/*      */     
/*  106 */     AIR = new WrappedBlockState(StateTypes.AIR, new EnumMap<>(StateValue.class), 0, (byte)0);
/*      */     
/*  108 */     BY_STRING = (Map<String, WrappedBlockState>[])new Map[HIGHEST_MAPPING_INDEX + 1];
/*  109 */     BY_ID = (Map<Integer, WrappedBlockState>[])new Map[HIGHEST_MAPPING_INDEX + 1];
/*  110 */     INTO_STRING = (Map<WrappedBlockState, String>[])new Map[HIGHEST_MAPPING_INDEX + 1];
/*  111 */     INTO_ID = (Map<WrappedBlockState, Integer>[])new Map[HIGHEST_MAPPING_INDEX + 1];
/*  112 */     DEFAULT_STATES = (Map<StateType, WrappedBlockState>[])new Map[HIGHEST_MAPPING_INDEX + 1];
/*      */     
/*  114 */     STRING_UPDATER = new HashMap<>();
/*      */ 
/*      */     
/*  117 */     STRING_UPDATER.put("grass_path", "dirt_path");
/*      */     
/*  119 */     Arrays.fill((Object[])BY_STRING, Collections.emptyMap());
/*  120 */     Arrays.fill((Object[])BY_ID, Collections.emptyMap());
/*  121 */     Arrays.fill((Object[])INTO_STRING, Collections.emptyMap());
/*  122 */     Arrays.fill((Object[])INTO_ID, Collections.emptyMap());
/*  123 */     Arrays.fill((Object[])DEFAULT_STATES, Collections.emptyMap());
/*      */ 
/*      */     
/*  126 */     String airName = AIR.getType().getMapped().getName().getKey();
/*  127 */     BY_STRING[0] = Collections.singletonMap(airName, AIR);
/*  128 */     BY_ID[0] = Collections.singletonMap(Integer.valueOf(AIR.getGlobalId()), AIR);
/*  129 */     INTO_STRING[0] = Collections.singletonMap(AIR, airName);
/*  130 */     INTO_ID[0] = Collections.singletonMap(AIR, Integer.valueOf(AIR.getGlobalId()));
/*  131 */     DEFAULT_STATES[0] = Collections.singletonMap(AIR.getType(), AIR);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*  136 */   Map<StateValue, Object> data = new HashMap<>(0);
/*      */   boolean hasClonedData = false;
/*      */   byte mappingsIndex;
/*      */   
/*      */   @Deprecated
/*      */   public WrappedBlockState(StateType type, String[] data, int globalID, byte mappingsIndex) {
/*  142 */     this.type = type;
/*  143 */     this.globalID = globalID;
/*      */     
/*  145 */     if (data != null) {
/*  146 */       for (String s : data) {
/*      */         try {
/*  148 */           String[] split = s.split("=");
/*  149 */           StateValue value = StateValue.byName(split[0]);
/*  150 */           this.data.put(value, value.getParser().apply(split[1].toUpperCase(Locale.ROOT)));
/*  151 */         } catch (Exception e) {
/*  152 */           e.printStackTrace();
/*  153 */           PacketEvents.getAPI().getLogManager().warn("Failed to parse block state: " + s);
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  158 */     this.mappingsIndex = mappingsIndex;
/*      */   }
/*      */   
/*      */   public WrappedBlockState(StateType type, Map<StateValue, Object> data, int globalID, byte mappingsIndex) {
/*  162 */     this.globalID = globalID;
/*  163 */     this.type = type;
/*  164 */     this.data = data;
/*  165 */     this.mappingsIndex = mappingsIndex;
/*      */   }
/*      */   
/*      */   private static byte loadMappings(ClientVersion version) {
/*  169 */     byte mappingsIndex = getMappingsIndex(version);
/*  170 */     if (!PRELOAD_BLOCK_STATE_MAPPINGS && BY_ID[mappingsIndex].isEmpty()) {
/*  171 */       loadMappings0(getMappingsVersion(version), mappingsIndex);
/*      */     }
/*  173 */     return mappingsIndex;
/*      */   }
/*      */   
/*      */   private static synchronized void loadMappings0(ClientVersion version, byte mappingsIndex) {
/*  177 */     if (!BY_ID[mappingsIndex].isEmpty()) {
/*      */       return;
/*      */     }
/*  180 */     PacketEvents.getAPI().getLogger().info("Loading block mappings for " + version + "/" + mappingsIndex + "...");
/*  181 */     long start = System.nanoTime();
/*      */     
/*  183 */     if (mappingsIndex == 1) {
/*  184 */       loadLegacy(buildStateDataCache());
/*      */     } else {
/*  186 */       loadModern(buildStateDataCache(), version);
/*      */     } 
/*      */     
/*  189 */     double timeDiff = (System.nanoTime() - start) / 1000000.0D;
/*  190 */     PacketEvents.getAPI().getLogger().info("Finished loading block mappings for " + version + "/" + mappingsIndex + " in " + timeDiff + "ms");
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
/*      */ 
/*      */ 
/*      */   
/*      */   private static Map<Map<StateValue, Object>, StateCacheValue> buildStateDataCache() {
/*  206 */     Map<Map<StateValue, Object>, StateCacheValue> cache = new HashMap<>();
/*  207 */     for (byte i = 0; i < HIGHEST_MAPPING_INDEX; i = (byte)(i + 1)) {
/*  208 */       Map<Integer, WrappedBlockState> map = BY_ID[i];
/*  209 */       if (map != null)
/*      */       {
/*      */         
/*  212 */         for (WrappedBlockState state : map.values())
/*  213 */           cache.computeIfAbsent(state.data, StateCacheValue::new); 
/*      */       }
/*      */     } 
/*  216 */     return cache;
/*      */   }
/*      */   
/*      */   public static WrappedBlockState decode(NBT nbt, ClientVersion version) {
/*  220 */     if (nbt instanceof NBTString) {
/*  221 */       StateType type = StateTypes.getByName(((NBTString)nbt).getValue());
/*  222 */       return getDefaultState(version, type);
/*      */     } 
/*      */     
/*  225 */     NBTCompound compound = (NBTCompound)nbt;
/*  226 */     String blockName = compound.getStringTagValueOrThrow("Name");
/*  227 */     StateType block = StateTypes.getByName(blockName);
/*  228 */     WrappedBlockState state = getDefaultState(version, block);
/*      */     
/*  230 */     if (state != AIR) {
/*  231 */       NBTCompound propsTag = compound.getCompoundTagOrNull("Properties");
/*  232 */       if (propsTag != null) {
/*  233 */         for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)propsTag.getTags().entrySet()) {
/*  234 */           Object value; StateValue stateValue = (StateValue)AdventureIndexUtil.indexValueOrThrow(StateValue.NAME_INDEX, entry.getKey());
/*      */           
/*  236 */           if (stateValue.getDataClass() == boolean.class) {
/*      */             
/*  238 */             value = Boolean.valueOf(((NBTByte)entry.getValue()).getAsBool());
/*  239 */           } else if (entry.getValue() instanceof NBTNumber) {
/*  240 */             Number num = ((NBTNumber)entry.getValue()).getAsNumber();
/*  241 */             value = stateValue.parse(num.toString());
/*      */           } else {
/*  243 */             value = stateValue.parse(((NBTString)entry.getValue()).getValue());
/*      */           } 
/*      */           
/*  246 */           state.getInternalData().put(stateValue, value);
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/*  251 */     return state;
/*      */   }
/*      */   
/*      */   public static NBT encode(WrappedBlockState state, ClientVersion version) {
/*  255 */     String stateTypeStr = state.type.getMapped().getName().toString();
/*      */     WrappedBlockState defaultState;
/*  257 */     if (state.getInternalData().isEmpty() || state.equals(defaultState = getDefaultState(version, state.type))) {
/*  258 */       return (NBT)new NBTString(stateTypeStr);
/*      */     }
/*      */     
/*  261 */     NBTCompound propsTag = new NBTCompound();
/*  262 */     for (Map.Entry<StateValue, Object> dataEntry : state.getInternalData().entrySet()) {
/*  263 */       NBTString nBTString; StateValue stateValue = dataEntry.getKey();
/*  264 */       if (Objects.equals(defaultState.getInternalData().get(stateValue), dataEntry.getValue())) {
/*      */         continue;
/*      */       }
/*      */       
/*  268 */       if (stateValue.getDataClass() == boolean.class) {
/*  269 */         NBTByte nBTByte = new NBTByte(((Boolean)dataEntry.getValue()).booleanValue());
/*  270 */       } else if (stateValue.getDataClass() == int.class) {
/*  271 */         NBTInt nBTInt = new NBTInt(((Integer)dataEntry.getValue()).intValue());
/*      */       } else {
/*  273 */         nBTString = new NBTString(dataEntry.getValue().toString());
/*      */       } 
/*  275 */       propsTag.setTag(stateValue.getName(), (NBT)nBTString);
/*      */     } 
/*      */     
/*  278 */     NBTCompound compound = new NBTCompound();
/*  279 */     compound.setTag("Name", (NBT)new NBTString(stateTypeStr));
/*  280 */     compound.setTag("Properties", (NBT)propsTag);
/*  281 */     return (NBT)compound;
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByGlobalId(int globalID) {
/*  286 */     return getByGlobalId(globalID, true);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByGlobalId(int globalID, boolean clone) {
/*  291 */     return getByGlobalId(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), globalID, clone);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByGlobalId(ClientVersion version, int globalID) {
/*  296 */     return getByGlobalId(version, globalID, true);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByGlobalId(ClientVersion version, int globalID, boolean clone) {
/*  301 */     if (globalID == 0) return AIR; 
/*  302 */     byte mappingsIndex = loadMappings(version);
/*  303 */     WrappedBlockState state = BY_ID[mappingsIndex].getOrDefault(Integer.valueOf(globalID), AIR);
/*  304 */     return clone ? state.clone() : state;
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByString(String string) {
/*  309 */     return getByString(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), string);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByString(ClientVersion version, String string) {
/*  314 */     return getByString(version, string, true);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getByString(ClientVersion version, String string, boolean clone) {
/*  319 */     byte mappingsIndex = loadMappings(version);
/*  320 */     WrappedBlockState state = BY_STRING[mappingsIndex].getOrDefault(string.replace("minecraft:", ""), AIR);
/*  321 */     return clone ? state.clone() : state;
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getDefaultState(StateType type) {
/*  326 */     return getDefaultState(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), type);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getDefaultState(ClientVersion version, StateType type) {
/*  331 */     return getDefaultState(version, type, true);
/*      */   }
/*      */   
/*      */   @NotNull
/*      */   public static WrappedBlockState getDefaultState(ClientVersion version, StateType type, boolean clone) {
/*  336 */     if (type == StateTypes.AIR) return AIR; 
/*  337 */     byte mappingsIndex = loadMappings(version);
/*  338 */     WrappedBlockState state = DEFAULT_STATES[mappingsIndex].get(type);
/*  339 */     if (state == null) {
/*  340 */       PacketEvents.getAPI().getLogger().config("Default state for " + type.getName() + " is null. Returning AIR");
/*  341 */       return AIR;
/*      */     } 
/*  343 */     return clone ? state.clone() : state;
/*      */   }
/*      */   
/*      */   private static byte getMappingsIndex(ClientVersion version) {
/*  347 */     return MAPPING_INDEXES[version.ordinal()];
/*      */   }
/*      */   
/*      */   private static ClientVersion getMappingsVersion(ClientVersion version) {
/*  351 */     return MAPPING_VERSIONS[version.ordinal()];
/*      */   }
/*      */   
/*      */   private static void loadLegacy(Map<Map<StateValue, Object>, StateCacheValue> cache) {
/*  355 */     Map<Integer, WrappedBlockState> stateByIdMap = new HashMap<>();
/*  356 */     Map<WrappedBlockState, Integer> stateToIdMap = new HashMap<>();
/*  357 */     Map<String, WrappedBlockState> stateByStringMap = new HashMap<>();
/*  358 */     Map<WrappedBlockState, String> stateToStringMap = new HashMap<>();
/*  359 */     Map<StateType, WrappedBlockState> stateTypeToBlockStateMap = new HashMap<>();
/*      */     
/*  361 */     try { SequentialNBTReader.Compound compound = MappingHelper.decompress("mappings/data/block_state/legacy"); 
/*  362 */       try { compound.skipOne();
/*      */         
/*  364 */         for (Map.Entry<String, NBT> entry : (Iterable<Map.Entry<String, NBT>>)compound.next().getValue()) {
/*  365 */           SequentialNBTReader.Compound inner = (SequentialNBTReader.Compound)entry.getValue();
/*      */           
/*  367 */           StateType type = StateTypes.getByName(entry.getKey());
/*  368 */           if (type == null) {
/*  369 */             PacketEvents.getAPI().getLogger().warning("Could not find type for " + (String)entry.getKey());
/*  370 */             inner.skip();
/*      */             
/*      */             continue;
/*      */           } 
/*  374 */           for (Map.Entry<String, NBT> element : (Iterable<Map.Entry<String, NBT>>)inner) {
/*  375 */             StateCacheValue stateCache; String elementName = element.getKey();
/*  376 */             int idIndex = elementName.indexOf(':');
/*  377 */             int id = Integer.parseInt(elementName.substring(0, idIndex));
/*  378 */             int data = Integer.parseInt(elementName.substring(idIndex + 1));
/*  379 */             int combinedID = id << 4 | data;
/*      */             
/*  381 */             SequentialNBTReader.Compound dataContent = (SequentialNBTReader.Compound)element.getValue();
/*      */             
/*  383 */             if (dataContent.hasNext()) {
/*  384 */               Map<StateValue, Object> dataMap = new LinkedHashMap<>(3);
/*  385 */               for (Map.Entry<String, NBT> props : (Iterable<Map.Entry<String, NBT>>)dataContent) {
/*  386 */                 Object v; StateValue stateValue = StateValue.byName(props.getKey());
/*  387 */                 if (stateValue == null) {
/*  388 */                   PacketEvents.getAPI().getLogger().warning("Could not find value for " + (String)props.getKey());
/*      */                   
/*      */                   continue;
/*      */                 } 
/*  392 */                 NBT value = props.getValue();
/*      */                 
/*  394 */                 if (value instanceof NBTByte) {
/*  395 */                   v = Boolean.valueOf((((NBTByte)value).getAsInt() == 1));
/*  396 */                 } else if (value instanceof NBTNumber) {
/*  397 */                   v = Integer.valueOf(((NBTNumber)value).getAsInt());
/*  398 */                 } else if (value instanceof NBTString) {
/*  399 */                   v = ((NBTString)value).getValue();
/*      */                 } else {
/*  401 */                   PacketEvents.getAPI().getLogger().warning("Unknown NBT type in legacy mapping: " + value.getClass().getSimpleName());
/*      */                   continue;
/*      */                 } 
/*  404 */                 dataMap.put(stateValue, stateValue.getParser().apply(v.toString().toUpperCase(Locale.ROOT)));
/*      */               } 
/*  406 */               stateCache = cache.computeIfAbsent(dataMap, StateCacheValue::new);
/*      */             } else {
/*  408 */               stateCache = StateCacheValue.EMPTY;
/*      */             } 
/*      */             
/*  411 */             String fullString = (String)entry.getKey() + stateCache.getString();
/*  412 */             WrappedBlockState state = new WrappedBlockState(type, stateCache.map, combinedID, (byte)1);
/*      */             
/*  414 */             stateByIdMap.put(Integer.valueOf(combinedID), state);
/*  415 */             stateToStringMap.put(state, fullString);
/*  416 */             stateToIdMap.put(state, Integer.valueOf(combinedID));
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  421 */             stateByStringMap.putIfAbsent(fullString, state);
/*      */ 
/*      */             
/*  424 */             stateTypeToBlockStateMap.putIfAbsent(type, state);
/*      */           } 
/*      */         } 
/*      */         
/*  428 */         BY_ID[1] = stateByIdMap;
/*  429 */         INTO_ID[1] = stateToIdMap;
/*  430 */         BY_STRING[1] = stateByStringMap;
/*  431 */         INTO_STRING[1] = stateToStringMap;
/*  432 */         DEFAULT_STATES[1] = stateTypeToBlockStateMap;
/*  433 */         if (compound != null) compound.close();  } catch (Throwable throwable) { if (compound != null) try { compound.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/*  434 */     { throw new RuntimeException("Failed to load legacy block mappings", e); }
/*      */   
/*      */   }
/*      */   private static void loadModern(Map<Map<StateValue, Object>, StateCacheValue> cache, ClientVersion version) {
/*      */     
/*  439 */     try { SequentialNBTReader.Compound compound = MappingHelper.decompress("mappings/data/block_state/" + version.name()); 
/*  440 */       try { compound.skipOne();
/*      */         
/*  442 */         byte mappingIndex = getMappingsIndex(version);
/*  443 */         SequentialNBTReader.List list = (SequentialNBTReader.List)compound.next().getValue();
/*      */         
/*  445 */         Map<Integer, WrappedBlockState> stateByIdMap = new HashMap<>();
/*  446 */         Map<WrappedBlockState, Integer> stateToIdMap = new HashMap<>();
/*  447 */         Map<String, WrappedBlockState> stateByStringMap = new HashMap<>();
/*  448 */         Map<WrappedBlockState, String> stateToStringMap = new HashMap<>();
/*  449 */         Map<StateType, WrappedBlockState> stateTypeToBlockStateMap = new HashMap<>();
/*      */         
/*  451 */         int id = 0;
/*  452 */         for (NBT e : list) {
/*  453 */           SequentialNBTReader.Compound element = (SequentialNBTReader.Compound)e;
/*  454 */           String typeString = ((NBTString)element.next().getValue()).getValue();
/*  455 */           StateType type = StateTypes.getByName(typeString);
/*  456 */           if (type == null) {
/*      */             
/*  458 */             for (Map.Entry<String, String> stringEntry : STRING_UPDATER.entrySet()) {
/*  459 */               typeString = typeString.replace(stringEntry.getKey(), stringEntry.getValue());
/*      */             }
/*      */             
/*  462 */             type = StateTypes.getByName(typeString);
/*      */             
/*  464 */             if (type == null) {
/*  465 */               PacketEvents.getAPI().getLogger().warning("Unknown block type: " + typeString);
/*  466 */               element.skip();
/*      */               
/*      */               continue;
/*      */             } 
/*      */           } 
/*  471 */           Map.Entry<String, NBT> next = element.next();
/*      */           
/*  473 */           int defaultIdx = 0;
/*  474 */           if (!((String)next.getKey()).equals("def")) {
/*  475 */             PacketEvents.getAPI().getLogger().warning("No default state for " + type + " using 0");
/*      */           } else {
/*  477 */             defaultIdx = ((NBTNumber)next.getValue()).getAsInt();
/*  478 */             next = element.next();
/*      */           } 
/*      */           
/*  481 */           int index = 0;
/*  482 */           for (NBT nbt : next.getValue()) {
/*  483 */             StateCacheValue stateCache; SequentialNBTReader.Compound dataContent = (SequentialNBTReader.Compound)nbt;
/*      */             
/*  485 */             if (dataContent.hasNext()) {
/*  486 */               Map<StateValue, Object> dataMap = new LinkedHashMap<>(3);
/*  487 */               for (Map.Entry<String, NBT> props : (Iterable<Map.Entry<String, NBT>>)dataContent) {
/*  488 */                 Object v; StateValue stateValue = StateValue.byName(props.getKey());
/*  489 */                 if (stateValue == null) {
/*  490 */                   PacketEvents.getAPI().getLogger().warning("Could not find value for " + (String)props.getKey());
/*      */                   
/*      */                   continue;
/*      */                 } 
/*  494 */                 NBT value = props.getValue();
/*      */                 
/*  496 */                 if (value instanceof NBTByte) {
/*  497 */                   v = Boolean.valueOf((((NBTByte)value).getAsInt() == 1));
/*  498 */                 } else if (value instanceof NBTNumber) {
/*  499 */                   v = Integer.valueOf(((NBTNumber)value).getAsInt());
/*  500 */                 } else if (value instanceof NBTString) {
/*  501 */                   v = ((NBTString)value).getValue();
/*      */                 } else {
/*  503 */                   PacketEvents.getAPI().getLogger().warning("Unknown NBT typeString in modern mapping: " + value.getClass().getSimpleName());
/*      */                   continue;
/*      */                 } 
/*  506 */                 dataMap.put(stateValue, stateValue.getParser().apply(v.toString().toUpperCase(Locale.ROOT)));
/*      */               } 
/*  508 */               stateCache = cache.computeIfAbsent(dataMap, StateCacheValue::new);
/*      */             } else {
/*  510 */               stateCache = StateCacheValue.EMPTY;
/*      */             } 
/*      */             
/*  513 */             String fullString = typeString + stateCache.getString();
/*  514 */             WrappedBlockState state = new WrappedBlockState(type, stateCache.map, id, mappingIndex);
/*      */             
/*  516 */             if (defaultIdx == index) {
/*  517 */               stateTypeToBlockStateMap.put(type, state);
/*      */             }
/*      */             
/*  520 */             stateByStringMap.put(fullString, state);
/*  521 */             stateByIdMap.put(Integer.valueOf(id), state);
/*  522 */             stateToStringMap.put(state, fullString);
/*  523 */             stateToIdMap.put(state, Integer.valueOf(id));
/*      */             
/*  525 */             id++;
/*  526 */             index++;
/*      */           } 
/*      */         } 
/*      */         
/*  530 */         BY_ID[mappingIndex] = stateByIdMap;
/*  531 */         INTO_ID[mappingIndex] = stateToIdMap;
/*  532 */         BY_STRING[mappingIndex] = stateByStringMap;
/*  533 */         INTO_STRING[mappingIndex] = stateToStringMap;
/*  534 */         DEFAULT_STATES[mappingIndex] = stateTypeToBlockStateMap;
/*  535 */         if (compound != null) compound.close();  } catch (Throwable throwable) { if (compound != null) try { compound.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/*  536 */     { throw new RuntimeException("Failed to load modern block mappings", e); }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public WrappedBlockState clone() {
/*  542 */     return new WrappedBlockState(this.type, this.data, this.globalID, this.mappingsIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/*  547 */     if (this == o) return true; 
/*  548 */     if (!(o instanceof WrappedBlockState)) return false; 
/*  549 */     WrappedBlockState that = (WrappedBlockState)o;
/*      */     
/*  551 */     return (this.type == that.type && this.data.equals(that.data));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  557 */     return Objects.hash(new Object[] { this.type, this.data });
/*      */   }
/*      */   
/*      */   public StateType getType() {
/*  561 */     return this.type;
/*      */   }
/*      */   
/*      */   public Object getData(StateValue stateValue) {
/*  565 */     return this.data.get(stateValue);
/*      */   }
/*      */   
/*      */   public void setData(StateValue stateValue, Object object) {
/*  569 */     checkIfCloneNeeded();
/*  570 */     this.data.put(stateValue, object);
/*  571 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */   
/*      */   public int getAge() {
/*  576 */     return ((Integer)this.data.get(StateValue.AGE)).intValue();
/*      */   }
/*      */   
/*      */   public void setAge(int age) {
/*  580 */     checkIfCloneNeeded();
/*  581 */     this.data.put(StateValue.AGE, Integer.valueOf(age));
/*  582 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isAttached() {
/*  586 */     return ((Boolean)this.data.get(StateValue.ATTACHED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setAttached(boolean attached) {
/*  590 */     checkIfCloneNeeded();
/*  591 */     this.data.put(StateValue.ATTACHED, Boolean.valueOf(attached));
/*  592 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Attachment getAttachment() {
/*  596 */     return (Attachment)this.data.get(StateValue.ATTACHMENT);
/*      */   }
/*      */   
/*      */   public void setAttachment(Attachment attachment) {
/*  600 */     checkIfCloneNeeded();
/*  601 */     this.data.put(StateValue.ATTACHMENT, attachment);
/*  602 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Axis getAxis() {
/*  606 */     return (Axis)this.data.get(StateValue.AXIS);
/*      */   }
/*      */   
/*      */   public void setAxis(Axis axis) {
/*  610 */     checkIfCloneNeeded();
/*  611 */     this.data.put(StateValue.AXIS, axis);
/*  612 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isBerries() {
/*  616 */     return ((Boolean)this.data.get(StateValue.BERRIES)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setBerries(boolean berries) {
/*  620 */     checkIfCloneNeeded();
/*  621 */     this.data.put(StateValue.BERRIES, Boolean.valueOf(berries));
/*  622 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getBites() {
/*  626 */     return ((Integer)this.data.get(StateValue.BITES)).intValue();
/*      */   }
/*      */   
/*      */   public void setBites(int bites) {
/*  630 */     checkIfCloneNeeded();
/*  631 */     this.data.put(StateValue.BITES, Integer.valueOf(bites));
/*  632 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isBottom() {
/*  636 */     return ((Boolean)this.data.get(StateValue.BOTTOM)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setBottom(boolean bottom) {
/*  640 */     checkIfCloneNeeded();
/*  641 */     this.data.put(StateValue.BOTTOM, Boolean.valueOf(bottom));
/*  642 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getCandles() {
/*  646 */     return ((Integer)this.data.get(StateValue.CANDLES)).intValue();
/*      */   }
/*      */   
/*      */   public void setCandles(int candles) {
/*  650 */     checkIfCloneNeeded();
/*  651 */     this.data.put(StateValue.CANDLES, Integer.valueOf(candles));
/*  652 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getCharges() {
/*  656 */     return ((Integer)this.data.get(StateValue.CHARGES)).intValue();
/*      */   }
/*      */   
/*      */   public void setCharges(int charges) {
/*  660 */     checkIfCloneNeeded();
/*  661 */     this.data.put(StateValue.CHARGES, Integer.valueOf(charges));
/*  662 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isConditional() {
/*  666 */     return ((Boolean)this.data.get(StateValue.CONDITIONAL)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setConditional(boolean conditional) {
/*  670 */     checkIfCloneNeeded();
/*  671 */     this.data.put(StateValue.CONDITIONAL, Boolean.valueOf(conditional));
/*  672 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getDelay() {
/*  676 */     return ((Integer)this.data.get(StateValue.DELAY)).intValue();
/*      */   }
/*      */   
/*      */   public void setDelay(int delay) {
/*  680 */     checkIfCloneNeeded();
/*  681 */     this.data.put(StateValue.DELAY, Integer.valueOf(delay));
/*  682 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isDisarmed() {
/*  686 */     return ((Boolean)this.data.get(StateValue.DISARMED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setDisarmed(boolean disarmed) {
/*  690 */     checkIfCloneNeeded();
/*  691 */     this.data.put(StateValue.DISARMED, Boolean.valueOf(disarmed));
/*  692 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getDistance() {
/*  696 */     return ((Integer)this.data.get(StateValue.DISTANCE)).intValue();
/*      */   }
/*      */   
/*      */   public void setDistance(int distance) {
/*  700 */     checkIfCloneNeeded();
/*  701 */     this.data.put(StateValue.DISTANCE, Integer.valueOf(distance));
/*  702 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isDown() {
/*  706 */     return ((Boolean)this.data.get(StateValue.DOWN)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setDown(boolean down) {
/*  710 */     checkIfCloneNeeded();
/*  711 */     this.data.put(StateValue.DOWN, Boolean.valueOf(down));
/*  712 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isDrag() {
/*  716 */     return ((Boolean)this.data.get(StateValue.DRAG)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setDrag(boolean drag) {
/*  720 */     checkIfCloneNeeded();
/*  721 */     this.data.put(StateValue.DRAG, Boolean.valueOf(drag));
/*  722 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isDusted() {
/*  726 */     return ((Boolean)this.data.get(StateValue.DUSTED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setDusted(boolean dusted) {
/*  730 */     checkIfCloneNeeded();
/*  731 */     this.data.put(StateValue.DUSTED, Boolean.valueOf(dusted));
/*  732 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getEggs() {
/*  736 */     return ((Integer)this.data.get(StateValue.EGGS)).intValue();
/*      */   }
/*      */   
/*      */   public void setEggs(int eggs) {
/*  740 */     checkIfCloneNeeded();
/*  741 */     this.data.put(StateValue.EGGS, Integer.valueOf(eggs));
/*  742 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isEnabled() {
/*  746 */     return ((Boolean)this.data.get(StateValue.ENABLED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setEnabled(boolean enabled) {
/*  750 */     checkIfCloneNeeded();
/*  751 */     this.data.put(StateValue.ENABLED, Boolean.valueOf(enabled));
/*  752 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isExtended() {
/*  756 */     return ((Boolean)this.data.get(StateValue.EXTENDED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setExtended(boolean extended) {
/*  760 */     checkIfCloneNeeded();
/*  761 */     this.data.put(StateValue.EXTENDED, Boolean.valueOf(extended));
/*  762 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isEye() {
/*  766 */     return ((Boolean)this.data.get(StateValue.EYE)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setEye(boolean eye) {
/*  770 */     checkIfCloneNeeded();
/*  771 */     this.data.put(StateValue.EYE, Boolean.valueOf(eye));
/*  772 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Face getFace() {
/*  776 */     return (Face)this.data.get(StateValue.FACE);
/*      */   }
/*      */   
/*      */   public void setFace(Face face) {
/*  780 */     checkIfCloneNeeded();
/*  781 */     this.data.put(StateValue.FACE, face);
/*  782 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public BlockFace getFacing() {
/*  786 */     return (BlockFace)this.data.get(StateValue.FACING);
/*      */   }
/*      */   
/*      */   public void setFacing(BlockFace facing) {
/*  790 */     checkIfCloneNeeded();
/*  791 */     this.data.put(StateValue.FACING, facing);
/*  792 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getFlowerAmount() {
/*  796 */     return ((Integer)this.data.get(StateValue.FLOWER_AMOUNT)).intValue();
/*      */   }
/*      */   
/*      */   public void setFlowerAmount(int flowerAmount) {
/*  800 */     checkIfCloneNeeded();
/*  801 */     this.data.put(StateValue.FLOWER_AMOUNT, Integer.valueOf(flowerAmount));
/*  802 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Half getHalf() {
/*  806 */     return (Half)this.data.get(StateValue.HALF);
/*      */   }
/*      */   
/*      */   public void setHalf(Half half) {
/*  810 */     checkIfCloneNeeded();
/*  811 */     this.data.put(StateValue.HALF, half);
/*  812 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isHanging() {
/*  816 */     return ((Boolean)this.data.get(StateValue.HANGING)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setHanging(boolean hanging) {
/*  820 */     checkIfCloneNeeded();
/*  821 */     this.data.put(StateValue.HANGING, Boolean.valueOf(hanging));
/*  822 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isHasBook() {
/*  826 */     return ((Boolean)this.data.get(StateValue.HAS_BOOK)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setHasBook(boolean hasBook) {
/*  830 */     checkIfCloneNeeded();
/*  831 */     this.data.put(StateValue.HAS_BOOK, Boolean.valueOf(hasBook));
/*  832 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isHasBottle0() {
/*  836 */     return ((Boolean)this.data.get(StateValue.HAS_BOTTLE_0)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setHasBottle0(boolean hasBottle0) {
/*  840 */     checkIfCloneNeeded();
/*  841 */     this.data.put(StateValue.HAS_BOTTLE_0, Boolean.valueOf(hasBottle0));
/*  842 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isHasBottle1() {
/*  846 */     return ((Boolean)this.data.get(StateValue.HAS_BOTTLE_1)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setHasBottle1(boolean hasBottle1) {
/*  850 */     checkIfCloneNeeded();
/*  851 */     this.data.put(StateValue.HAS_BOTTLE_1, Boolean.valueOf(hasBottle1));
/*  852 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isHasBottle2() {
/*  856 */     return ((Boolean)this.data.get(StateValue.HAS_BOTTLE_2)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setHasBottle2(boolean hasBottle2) {
/*  860 */     checkIfCloneNeeded();
/*  861 */     this.data.put(StateValue.HAS_BOTTLE_2, Boolean.valueOf(hasBottle2));
/*  862 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isHasRecord() {
/*  866 */     return ((Boolean)this.data.get(StateValue.HAS_RECORD)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setHasRecord(boolean hasRecord) {
/*  870 */     checkIfCloneNeeded();
/*  871 */     this.data.put(StateValue.HAS_RECORD, Boolean.valueOf(hasRecord));
/*  872 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getHatch() {
/*  876 */     return ((Integer)this.data.get(StateValue.HATCH)).intValue();
/*      */   }
/*      */   
/*      */   public void setHatch(int hatch) {
/*  880 */     checkIfCloneNeeded();
/*  881 */     this.data.put(StateValue.HATCH, Integer.valueOf(hatch));
/*  882 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Hinge getHinge() {
/*  886 */     return (Hinge)this.data.get(StateValue.HINGE);
/*      */   }
/*      */   
/*      */   public void setHinge(Hinge hinge) {
/*  890 */     checkIfCloneNeeded();
/*  891 */     this.data.put(StateValue.HINGE, hinge);
/*  892 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getHoneyLevel() {
/*  896 */     return ((Integer)this.data.get(StateValue.HONEY_LEVEL)).intValue();
/*      */   }
/*      */   
/*      */   public void setHoneyLevel(int honeyLevel) {
/*  900 */     checkIfCloneNeeded();
/*  901 */     this.data.put(StateValue.HONEY_LEVEL, Integer.valueOf(honeyLevel));
/*  902 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isInWall() {
/*  906 */     return ((Boolean)this.data.get(StateValue.IN_WALL)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setInWall(boolean inWall) {
/*  910 */     checkIfCloneNeeded();
/*  911 */     this.data.put(StateValue.IN_WALL, Boolean.valueOf(inWall));
/*  912 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Instrument getInstrument() {
/*  916 */     return (Instrument)this.data.get(StateValue.INSTRUMENT);
/*      */   }
/*      */   
/*      */   public void setInstrument(Instrument instrument) {
/*  920 */     checkIfCloneNeeded();
/*  921 */     this.data.put(StateValue.INSTRUMENT, instrument);
/*  922 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isInverted() {
/*  926 */     return ((Boolean)this.data.get(StateValue.INVERTED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setInverted(boolean inverted) {
/*  930 */     checkIfCloneNeeded();
/*  931 */     this.data.put(StateValue.INVERTED, Boolean.valueOf(inverted));
/*  932 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getLayers() {
/*  936 */     return ((Integer)this.data.get(StateValue.LAYERS)).intValue();
/*      */   }
/*      */   
/*      */   public void setLayers(int layers) {
/*  940 */     checkIfCloneNeeded();
/*  941 */     this.data.put(StateValue.LAYERS, Integer.valueOf(layers));
/*  942 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Leaves getLeaves() {
/*  946 */     return (Leaves)this.data.get(StateValue.LEAVES);
/*      */   }
/*      */   
/*      */   public void setLeaves(Leaves leaves) {
/*  950 */     checkIfCloneNeeded();
/*  951 */     this.data.put(StateValue.LEAVES, leaves);
/*  952 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getLevel() {
/*  956 */     return ((Integer)this.data.get(StateValue.LEVEL)).intValue();
/*      */   }
/*      */   
/*      */   public void setLevel(int level) {
/*  960 */     checkIfCloneNeeded();
/*  961 */     this.data.put(StateValue.LEVEL, Integer.valueOf(level));
/*  962 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isLit() {
/*  966 */     return ((Boolean)this.data.get(StateValue.LIT)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setLit(boolean lit) {
/*  970 */     checkIfCloneNeeded();
/*  971 */     this.data.put(StateValue.LIT, Boolean.valueOf(lit));
/*  972 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isTip() {
/*  976 */     return ((Boolean)this.data.get(StateValue.TIP)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setTip(boolean tip) {
/*  980 */     checkIfCloneNeeded();
/*  981 */     this.data.put(StateValue.TIP, Boolean.valueOf(tip));
/*  982 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isLocked() {
/*  986 */     return ((Boolean)this.data.get(StateValue.LOCKED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setLocked(boolean locked) {
/*  990 */     checkIfCloneNeeded();
/*  991 */     this.data.put(StateValue.LOCKED, Boolean.valueOf(locked));
/*  992 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Mode getMode() {
/*  996 */     return (Mode)this.data.get(StateValue.MODE);
/*      */   }
/*      */   
/*      */   public void setMode(Mode mode) {
/* 1000 */     checkIfCloneNeeded();
/* 1001 */     this.data.put(StateValue.MODE, mode);
/* 1002 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getMoisture() {
/* 1006 */     return ((Integer)this.data.get(StateValue.MOISTURE)).intValue();
/*      */   }
/*      */   
/*      */   public void setMoisture(int moisture) {
/* 1010 */     checkIfCloneNeeded();
/* 1011 */     this.data.put(StateValue.MOISTURE, Integer.valueOf(moisture));
/* 1012 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public North getNorth() {
/* 1016 */     return (North)this.data.get(StateValue.NORTH);
/*      */   }
/*      */   
/*      */   public void setNorth(North north) {
/* 1020 */     checkIfCloneNeeded();
/* 1021 */     this.data.put(StateValue.NORTH, north);
/* 1022 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getNote() {
/* 1026 */     return ((Integer)this.data.get(StateValue.NOTE)).intValue();
/*      */   }
/*      */   
/*      */   public void setNote(int note) {
/* 1030 */     checkIfCloneNeeded();
/* 1031 */     this.data.put(StateValue.NOTE, Integer.valueOf(note));
/* 1032 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isOccupied() {
/* 1036 */     return ((Boolean)this.data.get(StateValue.OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setOccupied(boolean occupied) {
/* 1040 */     checkIfCloneNeeded();
/* 1041 */     this.data.put(StateValue.OCCUPIED, Boolean.valueOf(occupied));
/* 1042 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isShrieking() {
/* 1046 */     return ((Boolean)this.data.get(StateValue.SHRIEKING)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setShrieking(boolean shrieking) {
/* 1050 */     checkIfCloneNeeded();
/* 1051 */     this.data.put(StateValue.SHRIEKING, Boolean.valueOf(shrieking));
/* 1052 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isCanSummon() {
/* 1056 */     return ((Boolean)this.data.get(StateValue.CAN_SUMMON)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setCanSummon(boolean canSummon) {
/* 1060 */     checkIfCloneNeeded();
/* 1061 */     this.data.put(StateValue.CAN_SUMMON, Boolean.valueOf(canSummon));
/* 1062 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isOpen() {
/* 1066 */     return ((Boolean)this.data.get(StateValue.OPEN)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setOpen(boolean open) {
/* 1070 */     checkIfCloneNeeded();
/* 1071 */     this.data.put(StateValue.OPEN, Boolean.valueOf(open));
/* 1072 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Orientation getOrientation() {
/* 1076 */     return (Orientation)this.data.get(StateValue.ORIENTATION);
/*      */   }
/*      */   
/*      */   public void setOrientation(Orientation orientation) {
/* 1080 */     checkIfCloneNeeded();
/* 1081 */     this.data.put(StateValue.ORIENTATION, orientation);
/* 1082 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Part getPart() {
/* 1086 */     return (Part)this.data.get(StateValue.PART);
/*      */   }
/*      */   
/*      */   public void setPart(Part part) {
/* 1090 */     checkIfCloneNeeded();
/* 1091 */     this.data.put(StateValue.PART, part);
/* 1092 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isPersistent() {
/* 1096 */     return ((Boolean)this.data.get(StateValue.PERSISTENT)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setPersistent(boolean persistent) {
/* 1100 */     checkIfCloneNeeded();
/* 1101 */     this.data.put(StateValue.PERSISTENT, Boolean.valueOf(persistent));
/* 1102 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getPickles() {
/* 1106 */     return ((Integer)this.data.get(StateValue.PICKLES)).intValue();
/*      */   }
/*      */   
/*      */   public void setPickles(int pickles) {
/* 1110 */     checkIfCloneNeeded();
/* 1111 */     this.data.put(StateValue.PICKLES, Integer.valueOf(pickles));
/* 1112 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getPower() {
/* 1116 */     return ((Integer)this.data.get(StateValue.POWER)).intValue();
/*      */   }
/*      */   
/*      */   public void setPower(int power) {
/* 1120 */     checkIfCloneNeeded();
/* 1121 */     this.data.put(StateValue.POWER, Integer.valueOf(power));
/* 1122 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isPowered() {
/* 1126 */     return ((Boolean)this.data.get(StateValue.POWERED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setPowered(boolean powered) {
/* 1130 */     checkIfCloneNeeded();
/* 1131 */     this.data.put(StateValue.POWERED, Boolean.valueOf(powered));
/* 1132 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getRotation() {
/* 1136 */     return ((Integer)this.data.get(StateValue.ROTATION)).intValue();
/*      */   }
/*      */   
/*      */   public void setRotation(int rotation) {
/* 1140 */     checkIfCloneNeeded();
/* 1141 */     this.data.put(StateValue.ROTATION, Integer.valueOf(rotation));
/* 1142 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public SculkSensorPhase getSculkSensorPhase() {
/* 1146 */     return (SculkSensorPhase)this.data.get(StateValue.SCULK_SENSOR_PHASE);
/*      */   }
/*      */   
/*      */   public void setSculkSensorPhase(SculkSensorPhase sculkSensorPhase) {
/* 1150 */     checkIfCloneNeeded();
/* 1151 */     this.data.put(StateValue.SCULK_SENSOR_PHASE, sculkSensorPhase);
/* 1152 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Shape getShape() {
/* 1156 */     return (Shape)this.data.get(StateValue.SHAPE);
/*      */   }
/*      */   
/*      */   public void setShape(Shape shape) {
/* 1160 */     checkIfCloneNeeded();
/* 1161 */     this.data.put(StateValue.SHAPE, shape);
/* 1162 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isShort() {
/* 1166 */     return ((Boolean)this.data.get(StateValue.SHORT)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setShort(boolean short_) {
/* 1170 */     checkIfCloneNeeded();
/* 1171 */     this.data.put(StateValue.SHORT, Boolean.valueOf(short_));
/* 1172 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSignalFire() {
/* 1176 */     return ((Boolean)this.data.get(StateValue.SIGNAL_FIRE)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSignalFire(boolean signalFire) {
/* 1180 */     checkIfCloneNeeded();
/* 1181 */     this.data.put(StateValue.SIGNAL_FIRE, Boolean.valueOf(signalFire));
/* 1182 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSlotZeroOccupied() {
/* 1186 */     return ((Boolean)this.data.get(StateValue.SLOT_0_OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSlotZeroOccupied(boolean slotZeroOccupied) {
/* 1190 */     checkIfCloneNeeded();
/* 1191 */     this.data.put(StateValue.SLOT_0_OCCUPIED, Boolean.valueOf(slotZeroOccupied));
/* 1192 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSlotOneOccupied() {
/* 1196 */     return ((Boolean)this.data.get(StateValue.SLOT_1_OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSlotOneOccupied(boolean slotOneOccupied) {
/* 1200 */     checkIfCloneNeeded();
/* 1201 */     this.data.put(StateValue.SLOT_1_OCCUPIED, Boolean.valueOf(slotOneOccupied));
/* 1202 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSlotTwoOccupied() {
/* 1206 */     return ((Boolean)this.data.get(StateValue.SLOT_2_OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSlotTwoOccupied(boolean slotTwoOccupied) {
/* 1210 */     checkIfCloneNeeded();
/* 1211 */     this.data.put(StateValue.SLOT_2_OCCUPIED, Boolean.valueOf(slotTwoOccupied));
/* 1212 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSlotThreeOccupied() {
/* 1216 */     return ((Boolean)this.data.get(StateValue.SLOT_3_OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSlotThreeOccupied(boolean slotThreeOccupied) {
/* 1220 */     checkIfCloneNeeded();
/* 1221 */     this.data.put(StateValue.SLOT_3_OCCUPIED, Boolean.valueOf(slotThreeOccupied));
/* 1222 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSlotFourOccupied() {
/* 1226 */     return ((Boolean)this.data.get(StateValue.SLOT_4_OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSlotFourOccupied(boolean slotFourOccupied) {
/* 1230 */     checkIfCloneNeeded();
/* 1231 */     this.data.put(StateValue.SLOT_4_OCCUPIED, Boolean.valueOf(slotFourOccupied));
/* 1232 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSlotFiveOccupied() {
/* 1236 */     return ((Boolean)this.data.get(StateValue.SLOT_5_OCCUPIED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSlotFiveOccupied(boolean slotFiveOccupied) {
/* 1240 */     checkIfCloneNeeded();
/* 1241 */     this.data.put(StateValue.SLOT_5_OCCUPIED, Boolean.valueOf(slotFiveOccupied));
/* 1242 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isSnowy() {
/* 1246 */     return ((Boolean)this.data.get(StateValue.SNOWY)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setSnowy(boolean snowy) {
/* 1250 */     checkIfCloneNeeded();
/* 1251 */     this.data.put(StateValue.SNOWY, Boolean.valueOf(snowy));
/* 1252 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public int getStage() {
/* 1256 */     return ((Integer)this.data.get(StateValue.STAGE)).intValue();
/*      */   }
/*      */   
/*      */   public void setStage(int stage) {
/* 1260 */     checkIfCloneNeeded();
/* 1261 */     this.data.put(StateValue.STAGE, Integer.valueOf(stage));
/* 1262 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public South getSouth() {
/* 1266 */     return (South)this.data.get(StateValue.SOUTH);
/*      */   }
/*      */   
/*      */   public void setSouth(South south) {
/* 1270 */     checkIfCloneNeeded();
/* 1271 */     this.data.put(StateValue.SOUTH, south);
/* 1272 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Thickness getThickness() {
/* 1276 */     return (Thickness)this.data.get(StateValue.THICKNESS);
/*      */   }
/*      */   
/*      */   public void setThickness(Thickness thickness) {
/* 1280 */     checkIfCloneNeeded();
/* 1281 */     this.data.put(StateValue.THICKNESS, thickness);
/* 1282 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Tilt getTilt() {
/* 1286 */     return (Tilt)this.data.get(StateValue.TILT);
/*      */   }
/*      */   
/*      */   public void setTilt(Tilt tilt) {
/* 1290 */     checkIfCloneNeeded();
/* 1291 */     this.data.put(StateValue.TILT, tilt);
/* 1292 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isTriggered() {
/* 1296 */     return ((Boolean)this.data.get(StateValue.TRIGGERED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setTriggered(boolean triggered) {
/* 1300 */     checkIfCloneNeeded();
/* 1301 */     this.data.put(StateValue.TRIGGERED, Boolean.valueOf(triggered));
/* 1302 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Type getTypeData() {
/* 1306 */     return (Type)this.data.get(StateValue.TYPE);
/*      */   }
/*      */   
/*      */   public void setTypeData(Type type) {
/* 1310 */     checkIfCloneNeeded();
/* 1311 */     this.data.put(StateValue.TYPE, type);
/* 1312 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isUnstable() {
/* 1316 */     return ((Boolean)this.data.get(StateValue.UNSTABLE)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setUnstable(boolean unstable) {
/* 1320 */     checkIfCloneNeeded();
/* 1321 */     this.data.put(StateValue.UNSTABLE, Boolean.valueOf(unstable));
/* 1322 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isUp() {
/* 1326 */     return ((Boolean)this.data.get(StateValue.UP)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setUp(boolean up) {
/* 1330 */     checkIfCloneNeeded();
/* 1331 */     this.data.put(StateValue.UP, Boolean.valueOf(up));
/* 1332 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public VerticalDirection getVerticalDirection() {
/* 1336 */     return (VerticalDirection)this.data.get(StateValue.VERTICAL_DIRECTION);
/*      */   }
/*      */   
/*      */   public void setVerticalDirection(VerticalDirection verticalDirection) {
/* 1340 */     checkIfCloneNeeded();
/* 1341 */     this.data.put(StateValue.VERTICAL_DIRECTION, verticalDirection);
/* 1342 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isWaterlogged() {
/* 1346 */     return ((Boolean)this.data.get(StateValue.WATERLOGGED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setWaterlogged(boolean waterlogged) {
/* 1350 */     checkIfCloneNeeded();
/* 1351 */     this.data.put(StateValue.WATERLOGGED, Boolean.valueOf(waterlogged));
/* 1352 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public East getEast() {
/* 1356 */     return (East)this.data.get(StateValue.EAST);
/*      */   }
/*      */   
/*      */   public void setEast(East west) {
/* 1360 */     checkIfCloneNeeded();
/* 1361 */     this.data.put(StateValue.EAST, west);
/* 1362 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public West getWest() {
/* 1366 */     return (West)this.data.get(StateValue.WEST);
/*      */   }
/*      */   
/*      */   public void setWest(West west) {
/* 1370 */     checkIfCloneNeeded();
/* 1371 */     this.data.put(StateValue.WEST, west);
/* 1372 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public Bloom getBloom() {
/* 1376 */     return (Bloom)this.data.get(StateValue.BLOOM);
/*      */   }
/*      */   
/*      */   public void setBloom(Bloom bloom) {
/* 1380 */     checkIfCloneNeeded();
/* 1381 */     this.data.put(StateValue.BLOOM, bloom);
/* 1382 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isCracked() {
/* 1386 */     return ((Boolean)this.data.get(StateValue.CRACKED)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setCracked(boolean cracked) {
/* 1390 */     checkIfCloneNeeded();
/* 1391 */     this.data.put(StateValue.CRACKED, Boolean.valueOf(cracked));
/* 1392 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public boolean isCrafting() {
/* 1396 */     return ((Boolean)this.data.get(StateValue.CRAFTING)).booleanValue();
/*      */   }
/*      */   
/*      */   public void setCrafting(boolean crafting) {
/* 1400 */     checkIfCloneNeeded();
/* 1401 */     this.data.put(StateValue.CRAFTING, Boolean.valueOf(crafting));
/* 1402 */     checkIsStillValid();
/*      */   }
/*      */   
/*      */   public TrialSpawnerState getTrialSpawnerState() {
/* 1406 */     return (TrialSpawnerState)this.data.get(StateValue.TRIAL_SPAWNER_STATE);
/*      */   }
/*      */   
/*      */   public void setTrialSpawnerState(TrialSpawnerState trialSpawnerState) {
/* 1410 */     checkIfCloneNeeded();
/* 1411 */     this.data.put(StateValue.TRIAL_SPAWNER_STATE, trialSpawnerState);
/* 1412 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/*      */   public CreakingHeartState getCreaking() {
/* 1420 */     return (CreakingHeartState)this.data.get(StateValue.CREAKING);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/*      */   public void setCreaking(CreakingHeartState creakingHeartState) {
/* 1428 */     checkIfCloneNeeded();
/* 1429 */     this.data.put(StateValue.CREAKING, creakingHeartState);
/* 1430 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/*      */   public boolean isActive() {
/* 1438 */     return ((Boolean)this.data.get(StateValue.ACTIVE)).booleanValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/*      */   public void setActive(boolean active) {
/* 1446 */     checkIfCloneNeeded();
/* 1447 */     this.data.put(StateValue.ACTIVE, Boolean.valueOf(active));
/* 1448 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNatural() {
/* 1455 */     return ((Boolean)this.data.get(StateValue.NATURAL)).booleanValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNatural(boolean natural) {
/* 1462 */     checkIfCloneNeeded();
/* 1463 */     this.data.put(StateValue.NATURAL, Boolean.valueOf(natural));
/* 1464 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSegmentAmount() {
/* 1471 */     return ((Integer)this.data.get(StateValue.SEGMENT_AMOUNT)).intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSegmentAmount(int segmentAmount) {
/* 1478 */     checkIfCloneNeeded();
/* 1479 */     this.data.put(StateValue.SEGMENT_AMOUNT, Integer.valueOf(segmentAmount));
/* 1480 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CreakingHeartState getCreakingHeartState() {
/* 1487 */     return (CreakingHeartState)this.data.get(StateValue.CREAKING_HEART_STATE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCreakingHeartState(CreakingHeartState creakingHeartState) {
/* 1494 */     checkIfCloneNeeded();
/* 1495 */     this.data.put(StateValue.CREAKING_HEART_STATE, creakingHeartState);
/* 1496 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isMap() {
/* 1503 */     return ((Boolean)this.data.get(StateValue.MAP)).booleanValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMap(boolean map) {
/* 1510 */     checkIfCloneNeeded();
/* 1511 */     this.data.put(StateValue.MAP, Boolean.valueOf(map));
/* 1512 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHydration() {
/* 1519 */     return ((Integer)this.data.get(StateValue.HYDRATION)).intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHydration(int hydration) {
/* 1526 */     checkIfCloneNeeded();
/* 1527 */     this.data.put(StateValue.HYDRATION, Integer.valueOf(hydration));
/* 1528 */     checkIsStillValid();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkIfCloneNeeded() {
/* 1538 */     if (!this.hasClonedData) {
/* 1539 */       this.data = new HashMap<>(this.data);
/* 1540 */       this.hasClonedData = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkIsStillValid() {
/* 1551 */     int oldGlobalID = this.globalID;
/* 1552 */     this.globalID = getGlobalIdNoCache();
/* 1553 */     if (this.globalID == -1) {
/* 1554 */       WrappedBlockState blockState = ((WrappedBlockState)BY_ID[this.mappingsIndex].getOrDefault(Integer.valueOf(oldGlobalID), AIR)).clone();
/* 1555 */       this.type = blockState.type;
/* 1556 */       this.globalID = blockState.globalID;
/* 1557 */       this.data = new HashMap<>(blockState.data);
/*      */ 
/*      */       
/* 1560 */       if (PacketEvents.getAPI().getSettings().isDebugEnabled()) {
/* 1561 */         PacketEvents.getAPI().getLogManager().warn("Attempt to modify an unknown property for this game version and block!");
/* 1562 */         PacketEvents.getAPI().getLogManager().warn("Block: " + this.type.getName());
/* 1563 */         for (Map.Entry<StateValue, Object> entry : this.data.entrySet()) {
/* 1564 */           PacketEvents.getAPI().getLogManager().warn((new StringBuilder()).append(entry.getKey()).append(": ").append(entry.getValue()).toString());
/*      */         }
/* 1566 */         (new IllegalStateException("An invalid modification was made to a block!")).printStackTrace();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Map<StateValue, Object> getInternalData() {
/* 1580 */     return this.data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getGlobalId() {
/* 1591 */     return this.globalID;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int getGlobalIdNoCache() {
/* 1598 */     return ((Integer)INTO_ID[this.mappingsIndex].getOrDefault(this, Integer.valueOf(-1))).intValue();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1603 */     return INTO_STRING[this.mappingsIndex].get(this);
/*      */   }
/*      */   
/*      */   @Internal
/*      */   public static void ensureLoad() {
/* 1608 */     if (!PRELOAD_BLOCK_STATE_MAPPINGS) {
/*      */       return;
/*      */     }
/* 1611 */     Logger logger = PacketEvents.getAPI().getLogger();
/* 1612 */     logger.info("Preloading block mappings...");
/* 1613 */     long start = System.nanoTime();
/*      */ 
/*      */     
/* 1616 */     Map<Map<StateValue, Object>, StateCacheValue> cache = new HashMap<>();
/* 1617 */     loadLegacy(cache);
/* 1618 */     for (ClientVersion version : MAPPING_VERSION_STEPS) {
/* 1619 */       loadModern(cache, version);
/*      */     }
/*      */ 
/*      */     
/* 1623 */     double timeDiff = (System.nanoTime() - start) / 1000000.0D;
/* 1624 */     logger.info("Finish preloading block mappings in " + timeDiff + "ms");
/*      */   }
/*      */   
/*      */   private static final class StateCacheValue
/*      */   {
/* 1629 */     public static final StateCacheValue EMPTY = new StateCacheValue(Collections.emptyMap());
/*      */     
/*      */     private final Map<StateValue, Object> map;
/*      */     private String string;
/*      */     
/*      */     public StateCacheValue(Map<StateValue, Object> map) {
/* 1635 */       this.map = map;
/*      */     }
/*      */     
/*      */     public String getString() {
/* 1639 */       if (this.string == null) {
/* 1640 */         StringBuilder builder = new StringBuilder();
/* 1641 */         for (Map.Entry<StateValue, Object> entry : this.map.entrySet()) {
/* 1642 */           builder
/* 1643 */             .append(((StateValue)entry.getKey()).getName())
/* 1644 */             .append('=')
/*      */ 
/*      */ 
/*      */             
/* 1648 */             .append(String.valueOf(entry.getValue()).toLowerCase(Locale.ROOT))
/* 1649 */             .append(',');
/*      */         }
/* 1651 */         this.string = (builder.length() == 0) ? "" : ('[' + builder.substring(0, builder.length() - 1) + ']');
/*      */       } 
/* 1653 */       return this.string;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\world\states\WrappedBlockState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */