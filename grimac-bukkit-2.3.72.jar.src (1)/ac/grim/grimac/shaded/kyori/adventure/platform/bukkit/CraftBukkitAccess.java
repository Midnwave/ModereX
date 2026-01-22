/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Knob;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import org.bukkit.inventory.ItemStack;
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
/*     */ final class CraftBukkitAccess
/*     */ {
/*     */   @Nullable
/*  49 */   static final Class<?> CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(new String[] {
/*  50 */         MinecraftReflection.findNmsClassName("IChatBaseComponent"), 
/*  51 */         MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), 
/*  52 */         MinecraftReflection.findMcClassName("network.chat.Component") });
/*     */   @Nullable
/*  54 */   static final Class<?> CLASS_REGISTRY = MinecraftReflection.findClass(new String[] {
/*  55 */         MinecraftReflection.findNmsClassName("IRegistry"), 
/*  56 */         MinecraftReflection.findMcClassName("core.IRegistry"), 
/*  57 */         MinecraftReflection.findMcClassName("core.Registry") });
/*     */   @Nullable
/*  59 */   static final Class<?> CLASS_SERVER_LEVEL = MinecraftReflection.findClass(new String[] {
/*  60 */         MinecraftReflection.findMcClassName("server.level.WorldServer"), 
/*  61 */         MinecraftReflection.findMcClassName("server.level.ServerLevel") });
/*     */   @Nullable
/*  63 */   static final Class<?> CLASS_LEVEL = MinecraftReflection.findClass(new String[] {
/*  64 */         MinecraftReflection.findMcClassName("world.level.World"), 
/*  65 */         MinecraftReflection.findMcClassName("world.level.Level") });
/*     */   @Nullable
/*  67 */   static final Class<?> CLASS_REGISTRY_ACCESS = MinecraftReflection.findClass(new String[] {
/*  68 */         MinecraftReflection.findMcClassName("core.IRegistryCustom"), 
/*  69 */         MinecraftReflection.findMcClassName("core.RegistryAccess") });
/*     */   @Nullable
/*  71 */   static final Class<?> CLASS_RESOURCE_KEY = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("resources.ResourceKey") }); @Nullable
/*  72 */   static final Class<?> CLASS_RESOURCE_LOCATION = MinecraftReflection.findClass(new String[] {
/*  73 */         MinecraftReflection.findNmsClassName("MinecraftKey"), 
/*  74 */         MinecraftReflection.findMcClassName("resources.MinecraftKey"), 
/*  75 */         MinecraftReflection.findMcClassName("resources.ResourceLocation") });
/*     */   @Nullable
/*  77 */   static final Class<?> CLASS_NMS_ENTITY = MinecraftReflection.findClass(new String[] {
/*  78 */         MinecraftReflection.findNmsClassName("Entity"), 
/*  79 */         MinecraftReflection.findMcClassName("world.entity.Entity") });
/*     */   @Nullable
/*  81 */   static final Class<?> CLASS_BUILT_IN_REGISTRIES = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("core.registries.BuiltInRegistries") }); @Nullable
/*  82 */   static final Class<?> CLASS_HOLDER = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("core.Holder") }); @Nullable
/*  83 */   static final Class<?> CLASS_WRITABLE_REGISTRY = MinecraftReflection.findClass(new String[] {
/*  84 */         MinecraftReflection.findNmsClassName("IRegistryWritable"), 
/*  85 */         MinecraftReflection.findMcClassName("core.IRegistryWritable"), 
/*  86 */         MinecraftReflection.findMcClassName("core.WritableRegistry") });
/*     */   @Nullable
/*     */   static final MethodHandle NEW_RESOURCE_LOCATION;
/*     */   
/*     */   static {
/*  91 */     MethodHandle newResourceLocation = MinecraftReflection.findConstructor(CLASS_RESOURCE_LOCATION, new Class[] { String.class, String.class });
/*  92 */     if (newResourceLocation == null) {
/*  93 */       newResourceLocation = MinecraftReflection.searchMethod(CLASS_RESOURCE_LOCATION, Integer.valueOf(9), "fromNamespaceAndPath", CLASS_RESOURCE_LOCATION, new Class[] { String.class, String.class });
/*     */     }
/*  95 */     NEW_RESOURCE_LOCATION = newResourceLocation;
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Chat1_19_3
/*     */   {
/*     */     @Nullable
/* 102 */     static final MethodHandle RESOURCE_KEY_CREATE = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_RESOURCE_KEY, Integer.valueOf(9), "create", CraftBukkitAccess.CLASS_RESOURCE_KEY, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_KEY, CraftBukkitAccess.CLASS_RESOURCE_LOCATION }); @Nullable
/* 103 */     static final MethodHandle SERVER_PLAYER_GET_LEVEL = MinecraftReflection.searchMethod(CraftBukkitFacet.CRAFT_PLAYER_GET_HANDLE.type().returnType(), Integer.valueOf(1), "getLevel", CraftBukkitAccess.CLASS_SERVER_LEVEL, new Class[0]); @Nullable
/* 104 */     static final MethodHandle SERVER_LEVEL_GET_REGISTRY_ACCESS = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_SERVER_LEVEL, Integer.valueOf(1), "registryAccess", CraftBukkitAccess.CLASS_REGISTRY_ACCESS, new Class[0]); @Nullable
/* 105 */     static final MethodHandle LEVEL_GET_REGISTRY_ACCESS = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_LEVEL, Integer.valueOf(1), "registryAccess", CraftBukkitAccess.CLASS_REGISTRY_ACCESS, new Class[0]); @Nullable
/* 106 */     static final MethodHandle ACTUAL_GET_REGISTRY_ACCESS = (SERVER_LEVEL_GET_REGISTRY_ACCESS == null) ? LEVEL_GET_REGISTRY_ACCESS : SERVER_LEVEL_GET_REGISTRY_ACCESS; @Nullable
/* 107 */     static final MethodHandle REGISTRY_ACCESS_GET_REGISTRY_OPTIONAL = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY_ACCESS, Integer.valueOf(1), "registry", Optional.class, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_KEY }); @Nullable
/* 108 */     static final MethodHandle REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "getOptional", Optional.class, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION }); @Nullable
/* 109 */     static final MethodHandle REGISTRY_GET_HOLDER = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "getHolder", Optional.class, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION }); @Nullable
/* 110 */     static final MethodHandle REGISTRY_GET_ID = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "getId", int.class, new Class[] { Object.class });
/*     */     
/*     */     @Nullable
/*     */     static final MethodHandle DISGUISED_CHAT_PACKET_CONSTRUCTOR;
/*     */     @Nullable
/*     */     static final MethodHandle CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR;
/*     */     
/*     */     static {
/* 118 */       MethodHandle boundNetworkConstructor = null;
/* 119 */       MethodHandle boundConstructor = null;
/* 120 */       MethodHandle disguisedChatPacketConstructor = null;
/* 121 */       Object chatTypeResourceKey = null;
/*     */       
/*     */       try {
/* 124 */         Class<?> classChatTypeBoundNetwork = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("network.chat.ChatType$BoundNetwork") });
/* 125 */         if (classChatTypeBoundNetwork == null) {
/* 126 */           Class<?> parentClass = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("network.chat.ChatMessageType") });
/* 127 */           if (parentClass != null) {
/* 128 */             for (Class<?> childClass : parentClass.getClasses()) {
/* 129 */               boundNetworkConstructor = MinecraftReflection.findConstructor(childClass, new Class[] { int.class, CraftBukkitAccess.CLASS_CHAT_COMPONENT, CraftBukkitAccess.CLASS_CHAT_COMPONENT });
/* 130 */               if (boundNetworkConstructor != null) {
/* 131 */                 classChatTypeBoundNetwork = childClass;
/*     */                 
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           }
/*     */         } 
/* 138 */         Class<?> classChatTypeBound = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("network.chat.ChatType$BoundNetwork") });
/* 139 */         if (classChatTypeBound == null) {
/* 140 */           Class<?> parentClass = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("network.chat.ChatMessageType") });
/* 141 */           if (parentClass != null) {
/* 142 */             for (Class<?> childClass : parentClass.getClasses()) {
/* 143 */               boundConstructor = MinecraftReflection.findConstructor(childClass, new Class[] { CraftBukkitAccess.CLASS_HOLDER, CraftBukkitAccess.CLASS_CHAT_COMPONENT, Optional.class });
/* 144 */               if (boundConstructor != null) {
/* 145 */                 classChatTypeBound = childClass;
/*     */                 
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           }
/*     */         } 
/* 152 */         Class<?> disguisedChatPacketClass = MinecraftReflection.findClass(new String[] { MinecraftReflection.findMcClassName("network.protocol.game.ClientboundDisguisedChatPacket") });
/* 153 */         if (disguisedChatPacketClass != null) {
/* 154 */           if (classChatTypeBoundNetwork != null) {
/* 155 */             disguisedChatPacketConstructor = MinecraftReflection.findConstructor(disguisedChatPacketClass, new Class[] { CraftBukkitAccess.CLASS_CHAT_COMPONENT, classChatTypeBoundNetwork });
/* 156 */           } else if (classChatTypeBound != null) {
/* 157 */             disguisedChatPacketConstructor = MinecraftReflection.findConstructor(disguisedChatPacketClass, new Class[] { CraftBukkitAccess.CLASS_CHAT_COMPONENT, classChatTypeBound });
/*     */           } 
/*     */         }
/*     */         
/* 161 */         if (CraftBukkitAccess.NEW_RESOURCE_LOCATION != null && RESOURCE_KEY_CREATE != null) {
/* 162 */           MethodHandle createRegistryKey = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_RESOURCE_KEY, Integer.valueOf(9), "createRegistryKey", CraftBukkitAccess.CLASS_RESOURCE_KEY, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION });
/* 163 */           if (createRegistryKey != null) {
/* 164 */             chatTypeResourceKey = createRegistryKey.invoke(CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke("minecraft", "chat_type"));
/*     */           }
/*     */         } 
/* 167 */       } catch (Throwable error) {
/* 168 */         Knob.logError(error, "Failed to initialize 1.19.3 chat support", new Object[0]);
/*     */       } 
/*     */       
/* 171 */       DISGUISED_CHAT_PACKET_CONSTRUCTOR = disguisedChatPacketConstructor;
/* 172 */       CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR = boundNetworkConstructor;
/* 173 */       CHAT_TYPE_BOUND_CONSTRUCTOR = boundConstructor;
/* 174 */       CHAT_TYPE_RESOURCE_KEY = chatTypeResourceKey;
/*     */     }
/*     */     @Nullable
/*     */     static final MethodHandle CHAT_TYPE_BOUND_CONSTRUCTOR;
/*     */     static final Object CHAT_TYPE_RESOURCE_KEY;
/*     */     
/*     */     static boolean isSupported() {
/* 181 */       return (ACTUAL_GET_REGISTRY_ACCESS != null && REGISTRY_ACCESS_GET_REGISTRY_OPTIONAL != null && REGISTRY_GET_OPTIONAL != null && (CHAT_TYPE_BOUND_NETWORK_CONSTRUCTOR != null || CHAT_TYPE_BOUND_CONSTRUCTOR != null) && DISGUISED_CHAT_PACKET_CONSTRUCTOR != null && CHAT_TYPE_RESOURCE_KEY != null);
/*     */     } }
/*     */   
/*     */   static final class EntitySound {
/*     */     @Nullable
/* 186 */     static final Class<?> CLASS_CLIENTBOUND_ENTITY_SOUND = MinecraftReflection.findClass(new String[] {
/* 187 */           MinecraftReflection.findNmsClassName("PacketPlayOutEntitySound"), 
/* 188 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutEntitySound"), 
/* 189 */           MinecraftReflection.findMcClassName("network.protocol.game.ClientboundSoundEntityPacket") });
/*     */     @Nullable
/* 191 */     static final Class<?> CLASS_SOUND_SOURCE = MinecraftReflection.findClass(new String[] {
/* 192 */           MinecraftReflection.findNmsClassName("SoundCategory"), 
/* 193 */           MinecraftReflection.findMcClassName("sounds.SoundCategory"), 
/* 194 */           MinecraftReflection.findMcClassName("sounds.SoundSource") });
/*     */     @Nullable
/* 196 */     static final Class<?> CLASS_SOUND_EVENT = MinecraftReflection.findClass(new String[] {
/* 197 */           MinecraftReflection.findNmsClassName("SoundEffect"), 
/* 198 */           MinecraftReflection.findMcClassName("sounds.SoundEffect"), 
/* 199 */           MinecraftReflection.findMcClassName("sounds.SoundEvent")
/*     */         });
/*     */     @Nullable
/*     */     static final MethodHandle SOUND_SOURCE_GET_NAME;
/*     */     
/*     */     static {
/* 205 */       MethodHandle soundSourceGetName = null;
/* 206 */       if (CLASS_SOUND_SOURCE != null) {
/* 207 */         for (Method method : CLASS_SOUND_SOURCE.getDeclaredMethods()) {
/* 208 */           if (method.getReturnType().equals(String.class) && method
/* 209 */             .getParameterCount() == 0 && 
/* 210 */             !"name".equals(method.getName()) && 
/* 211 */             Modifier.isPublic(method.getModifiers())) {
/*     */             
/*     */             try {
/* 214 */               soundSourceGetName = MinecraftReflection.lookup().unreflect(method);
/* 215 */             } catch (IllegalAccessException illegalAccessException) {}
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 222 */       SOUND_SOURCE_GET_NAME = soundSourceGetName;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static boolean isSupported() {
/* 229 */       return (SOUND_SOURCE_GET_NAME != null);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class EntitySound_1_19_3 {
/*     */     @Nullable
/* 235 */     static final MethodHandle REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "getOptional", Optional.class, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION }); @Nullable
/* 236 */     static final MethodHandle REGISTRY_WRAP_AS_HOLDER = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "wrapAsHolder", CraftBukkitAccess.CLASS_HOLDER, new Class[] { Object.class }); @Nullable
/* 237 */     static final MethodHandle SOUND_EVENT_CREATE_VARIABLE_RANGE = MinecraftReflection.searchMethod(CraftBukkitAccess.EntitySound.CLASS_SOUND_EVENT, Integer.valueOf(9), "createVariableRangeEvent", CraftBukkitAccess.EntitySound.CLASS_SOUND_EVENT, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION }); @Nullable
/* 238 */     static final MethodHandle NEW_CLIENTBOUND_ENTITY_SOUND = MinecraftReflection.findConstructor(CraftBukkitAccess.EntitySound.CLASS_CLIENTBOUND_ENTITY_SOUND, new Class[] { CraftBukkitAccess.CLASS_HOLDER, CraftBukkitAccess.EntitySound.CLASS_SOUND_SOURCE, CraftBukkitAccess.CLASS_NMS_ENTITY, float.class, float.class, long.class });
/*     */     @Nullable
/*     */     static final Object SOUND_EVENT_REGISTRY;
/*     */     
/*     */     static {
/* 243 */       Object soundEventRegistry = null;
/*     */       try {
/* 245 */         Field soundEventRegistryField = MinecraftReflection.findField(CraftBukkitAccess.CLASS_BUILT_IN_REGISTRIES, CraftBukkitAccess.CLASS_REGISTRY, new String[] { "SOUND_EVENT" });
/* 246 */         if (soundEventRegistryField != null) {
/* 247 */           soundEventRegistry = soundEventRegistryField.get(null);
/* 248 */         } else if (CraftBukkitAccess.CLASS_BUILT_IN_REGISTRIES != null && REGISTRY_GET_OPTIONAL != null && CraftBukkitAccess.NEW_RESOURCE_LOCATION != null) {
/* 249 */           Object rootRegistry = null;
/* 250 */           for (Field field : CraftBukkitAccess.CLASS_BUILT_IN_REGISTRIES.getDeclaredFields()) {
/* 251 */             int mask = 26;
/* 252 */             if ((field.getModifiers() & 0x1A) == 26 && field
/* 253 */               .getType().equals(CraftBukkitAccess.CLASS_WRITABLE_REGISTRY)) {
/* 254 */               field.setAccessible(true);
/* 255 */               rootRegistry = field.get(null);
/*     */               break;
/*     */             } 
/*     */           } 
/* 259 */           if (rootRegistry != null) {
/* 260 */             soundEventRegistry = REGISTRY_GET_OPTIONAL.invoke(rootRegistry, CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke("minecraft", "sound_event")).orElse(null);
/*     */           }
/*     */         } 
/* 263 */       } catch (Throwable error) {
/* 264 */         Knob.logError(error, "Failed to initialize EntitySound_1_19_3 CraftBukkit facet", new Object[0]);
/*     */       } 
/* 266 */       SOUND_EVENT_REGISTRY = soundEventRegistry;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static boolean isSupported() {
/* 273 */       return (NEW_CLIENTBOUND_ENTITY_SOUND != null && SOUND_EVENT_REGISTRY != null && CraftBukkitAccess.NEW_RESOURCE_LOCATION != null && REGISTRY_GET_OPTIONAL != null && REGISTRY_WRAP_AS_HOLDER != null && SOUND_EVENT_CREATE_VARIABLE_RANGE != null);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Book_1_20_5 {
/* 278 */     static final Class<?> CLASS_CRAFT_ITEMSTACK = MinecraftReflection.findCraftClass("inventory.CraftItemStack");
/* 279 */     static final Class<?> CLASS_MC_ITEMSTACK = MinecraftReflection.findMcClass(new String[] { "world.item.ItemStack" });
/* 280 */     static final Class<?> CLASS_MC_DATA_COMPONENT_TYPE = MinecraftReflection.findMcClass(new String[] { "core.component.DataComponentType" });
/* 281 */     static final Class<?> CLASS_MC_BOOK_CONTENT = MinecraftReflection.findMcClass(new String[] { "world.item.component.WrittenBookContent" });
/* 282 */     static final Class<?> CLASS_MC_FILTERABLE = MinecraftReflection.findMcClass(new String[] { "server.network.Filterable" });
/* 283 */     static final Class<?> CLASS_CRAFT_REGISTRY = MinecraftReflection.findCraftClass("CraftRegistry");
/* 284 */     static final MethodHandle CREATE_FILTERABLE = MinecraftReflection.searchMethod(CLASS_MC_FILTERABLE, Integer.valueOf(9), "passThrough", CLASS_MC_FILTERABLE, new Class[] { Object.class });
/* 285 */     static final MethodHandle GET_REGISTRY = MinecraftReflection.findStaticMethod(CLASS_CRAFT_REGISTRY, "getMinecraftRegistry", CraftBukkitAccess.CLASS_REGISTRY, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_KEY });
/* 286 */     static final MethodHandle CREATE_REGISTRY_KEY = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_RESOURCE_KEY, Integer.valueOf(9), "createRegistryKey", CraftBukkitAccess.CLASS_RESOURCE_KEY, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION });
/* 287 */     static final MethodHandle NEW_BOOK_CONTENT = MinecraftReflection.findConstructor(CLASS_MC_BOOK_CONTENT, new Class[] { CLASS_MC_FILTERABLE, String.class, int.class, List.class, boolean.class });
/* 288 */     static final MethodHandle REGISTRY_GET_OPTIONAL = MinecraftReflection.searchMethod(CraftBukkitAccess.CLASS_REGISTRY, Integer.valueOf(1), "getOptional", Optional.class, new Class[] { CraftBukkitAccess.CLASS_RESOURCE_LOCATION });
/* 289 */     static final Class<?> CLASS_ENUM_HAND = MinecraftReflection.findClass(new String[] {
/* 290 */           MinecraftReflection.findNmsClassName("EnumHand"), 
/* 291 */           MinecraftReflection.findMcClassName("world.EnumHand"), 
/* 292 */           MinecraftReflection.findMcClassName("world.InteractionHand")
/*     */         });
/* 294 */     static final Object HAND_MAIN = MinecraftReflection.findEnum(CLASS_ENUM_HAND, "MAIN_HAND", 0);
/* 295 */     static final MethodHandle MC_ITEMSTACK_SET = MinecraftReflection.searchMethod(CLASS_MC_ITEMSTACK, Integer.valueOf(1), "set", Object.class, new Class[] { CLASS_MC_DATA_COMPONENT_TYPE, Object.class });
/* 296 */     static final MethodHandle CRAFT_ITEMSTACK_NMS_COPY = MinecraftReflection.findStaticMethod(CLASS_CRAFT_ITEMSTACK, "asNMSCopy", CLASS_MC_ITEMSTACK, new Class[] { ItemStack.class });
/* 297 */     static final MethodHandle CRAFT_ITEMSTACK_CRAFT_MIRROR = MinecraftReflection.findStaticMethod(CLASS_CRAFT_ITEMSTACK, "asCraftMirror", CLASS_CRAFT_ITEMSTACK, new Class[] { CLASS_MC_ITEMSTACK });
/*     */     static final Object WRITTEN_BOOK_COMPONENT_TYPE;
/* 299 */     static final Class<?> PACKET_OPEN_BOOK = MinecraftReflection.findClass(new String[] {
/* 300 */           MinecraftReflection.findMcClassName("network.protocol.game.PacketPlayOutOpenBook"), 
/* 301 */           MinecraftReflection.findMcClassName("network.protocol.game.ClientboundOpenBookPacket")
/*     */         });
/* 303 */     static final MethodHandle NEW_PACKET_OPEN_BOOK = MinecraftReflection.findConstructor(PACKET_OPEN_BOOK, new Class[] { CLASS_ENUM_HAND });
/*     */     
/*     */     static {
/* 306 */       Object componentTypeRegistry = null;
/* 307 */       Object componentType = null;
/*     */       try {
/* 309 */         if (GET_REGISTRY != null && CREATE_REGISTRY_KEY != null && CraftBukkitAccess.NEW_RESOURCE_LOCATION != null && REGISTRY_GET_OPTIONAL != null) {
/* 310 */           Object registryKey = CREATE_REGISTRY_KEY.invoke(CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke("minecraft", "data_component_type"));
/*     */           try {
/* 312 */             componentTypeRegistry = GET_REGISTRY.invoke(registryKey);
/* 313 */           } catch (Exception exception) {}
/*     */           
/* 315 */           if (componentTypeRegistry != null) {
/* 316 */             componentType = REGISTRY_GET_OPTIONAL.invoke(componentTypeRegistry, CraftBukkitAccess.NEW_RESOURCE_LOCATION.invoke("minecraft", "written_book_content")).orElse(null);
/*     */           }
/*     */         } 
/* 319 */       } catch (Throwable error) {
/* 320 */         Knob.logError(error, "Failed to initialize Book_1_20_5 CraftBukkit facet", new Object[0]);
/*     */       } 
/* 322 */       WRITTEN_BOOK_COMPONENT_TYPE = componentType;
/*     */     }
/*     */     
/*     */     static boolean isSupported() {
/* 326 */       return (WRITTEN_BOOK_COMPONENT_TYPE != null && CREATE_FILTERABLE != null && NEW_BOOK_CONTENT != null && CRAFT_ITEMSTACK_NMS_COPY != null && MC_ITEMSTACK_SET != null && CRAFT_ITEMSTACK_CRAFT_MIRROR != null && NEW_PACKET_OPEN_BOOK != null && HAND_MAIN != null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\CraftBukkitAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */