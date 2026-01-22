/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Experimental;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.ComponentSerializer;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonElement;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ @Experimental
/*     */ public final class MinecraftComponentSerializer
/*     */   implements ComponentSerializer<Component, Component, Object>
/*     */ {
/*  68 */   private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSupported() {
/*  77 */     return SUPPORTED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static MinecraftComponentSerializer get() {
/*  87 */     return INSTANCE;
/*     */   }
/*     */   @Nullable
/*  90 */   private static final Class<?> CLASS_JSON_DESERIALIZER = MinecraftReflection.findClass(new String[] { "com.goo".concat("gle.gson.JsonDeserializer") }); @Nullable
/*  91 */   private static final Class<?> CLASS_JSON_ELEMENT = MinecraftReflection.findClass(new String[] { "com.goo".concat("gle.gson.JsonElement") }); @Nullable
/*  92 */   private static final Class<?> CLASS_JSON_PARSER = MinecraftReflection.findClass(new String[] { "com.goo".concat("gle.gson.JsonParser") }); @Nullable
/*  93 */   private static final Class<?> CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(new String[] {
/*  94 */         MinecraftReflection.findNmsClassName("IChatBaseComponent"), 
/*  95 */         MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), 
/*  96 */         MinecraftReflection.findMcClassName("network.chat.Component") });
/*     */   @Nullable
/*  98 */   private static final Class<?> CLASS_CRAFT_REGISTRY = MinecraftReflection.findCraftClass("CraftRegistry"); @Nullable
/*  99 */   private static final Class<?> CLASS_REGISTRY_ACCESS = MinecraftReflection.findClass(new String[] {
/* 100 */         MinecraftReflection.findMcClassName("core.IRegistryCustom"), 
/* 101 */         MinecraftReflection.findMcClassName("core.RegistryAccess") });
/*     */   @Nullable
/* 103 */   private static final MethodHandle PARSE_JSON = MinecraftReflection.findMethod(CLASS_JSON_PARSER, "parse", CLASS_JSON_ELEMENT, new Class[] { String.class }); @Nullable
/* 104 */   private static final MethodHandle GET_REGISTRY = MinecraftReflection.findStaticMethod(CLASS_CRAFT_REGISTRY, "getMinecraftRegistry", CLASS_REGISTRY_ACCESS, new Class[0]);
/* 105 */   private static final AtomicReference<RuntimeException> INITIALIZATION_ERROR = new AtomicReference<>(new UnsupportedOperationException()); private static final Object JSON_PARSER_INSTANCE;
/*     */   private static final Object MC_TEXT_GSON;
/*     */   private static final MethodHandle TEXT_SERIALIZER_DESERIALIZE;
/*     */   private static final MethodHandle TEXT_SERIALIZER_SERIALIZE;
/*     */   private static final MethodHandle TEXT_SERIALIZER_DESERIALIZE_TREE;
/*     */   private static final MethodHandle TEXT_SERIALIZER_SERIALIZE_TREE;
/*     */   private static final boolean SUPPORTED;
/*     */   
/*     */   static {
/* 114 */     Object gson = null;
/* 115 */     Object jsonParserInstance = null;
/* 116 */     MethodHandle textSerializerDeserialize = null;
/* 117 */     MethodHandle textSerializerSerialize = null;
/* 118 */     MethodHandle textSerializerDeserializeTree = null;
/* 119 */     MethodHandle textSerializerSerializeTree = null;
/*     */     
/*     */     try {
/* 122 */       if (CLASS_JSON_PARSER != null) {
/* 123 */         jsonParserInstance = CLASS_JSON_PARSER.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*     */       }
/* 125 */       if (CLASS_CHAT_COMPONENT != null) {
/* 126 */         Object registryAccess = (GET_REGISTRY != null) ? GET_REGISTRY.invoke() : null;
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
/* 142 */         Class<?> chatSerializerClass = Arrays.<Class<?>>stream(CLASS_CHAT_COMPONENT.getClasses()).filter(c -> { if (CLASS_JSON_DESERIALIZER != null) return CLASS_JSON_DESERIALIZER.isAssignableFrom(c);  for (Class<?> itf : c.getInterfaces()) { if (itf.getSimpleName().equals("JsonDeserializer")) return true;  }  return false; }).findAny().orElse(MinecraftReflection.findNmsClass("ChatSerializer"));
/* 143 */         if (chatSerializerClass != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 148 */           Field gsonField = Arrays.<Field>stream(chatSerializerClass.getDeclaredFields()).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getType().equals(Gson.class)).findFirst().orElse(null);
/* 149 */           if (gsonField != null) {
/* 150 */             gsonField.setAccessible(true);
/* 151 */             gson = gsonField.get(null);
/*     */           } 
/*     */         } 
/* 154 */         List<Class<?>> candidates = new ArrayList<>();
/* 155 */         if (chatSerializerClass != null) {
/* 156 */           candidates.add(chatSerializerClass);
/*     */         }
/* 158 */         candidates.addAll(Arrays.asList(CLASS_CHAT_COMPONENT.getClasses()));
/* 159 */         for (Class<?> serializerClass : candidates) {
/* 160 */           Method[] declaredMethods = serializerClass.getDeclaredMethods();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 166 */           Method deserialize = Arrays.<Method>stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class))).min(Comparator.comparing(Method::getName)).orElse(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 172 */           Method serialize = Arrays.<Method>stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(String.class)).filter(m -> (m.getParameterCount() == 1 && CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0]))).findFirst().orElse(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 178 */           Method deserializeTree = Arrays.<Method>stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(CLASS_JSON_ELEMENT))).findFirst().orElse(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 184 */           Method serializeTree = Arrays.<Method>stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(CLASS_JSON_ELEMENT)).filter(m -> (m.getParameterCount() == 1 && CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0]))).findFirst().orElse(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 192 */           Method deserializeTreeWithRegistryAccess = Arrays.<Method>stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> (m.getParameterCount() == 2)).filter(m -> m.getParameterTypes()[0].equals(CLASS_JSON_ELEMENT)).filter(m -> m.getParameterTypes()[1].isInstance(registryAccess)).findFirst().orElse(null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 200 */           Method serializeTreeWithRegistryAccess = Arrays.<Method>stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(CLASS_JSON_ELEMENT)).filter(m -> (m.getParameterCount() == 2)).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0])).filter(m -> m.getParameterTypes()[1].isInstance(registryAccess)).findFirst().orElse(null);
/* 201 */           if (deserialize != null) {
/* 202 */             textSerializerDeserialize = MinecraftReflection.lookup().unreflect(deserialize);
/*     */           }
/* 204 */           if (serialize != null) {
/* 205 */             textSerializerSerialize = MinecraftReflection.lookup().unreflect(serialize);
/*     */           }
/* 207 */           if (deserializeTree != null) {
/* 208 */             textSerializerDeserializeTree = MinecraftReflection.lookup().unreflect(deserializeTree);
/* 209 */           } else if (deserializeTreeWithRegistryAccess != null) {
/* 210 */             deserializeTreeWithRegistryAccess.setAccessible(true);
/* 211 */             textSerializerDeserializeTree = MethodHandles.insertArguments(MinecraftReflection.lookup().unreflect(deserializeTreeWithRegistryAccess), 1, new Object[] { registryAccess });
/*     */           } 
/* 213 */           if (serializeTree != null) {
/* 214 */             textSerializerSerializeTree = MinecraftReflection.lookup().unreflect(serializeTree); continue;
/* 215 */           }  if (serializeTreeWithRegistryAccess != null) {
/* 216 */             serializeTreeWithRegistryAccess.setAccessible(true);
/* 217 */             textSerializerSerializeTree = MethodHandles.insertArguments(MinecraftReflection.lookup().unreflect(serializeTreeWithRegistryAccess), 1, new Object[] { registryAccess });
/*     */           } 
/*     */         } 
/*     */       } 
/* 221 */     } catch (Throwable error) {
/* 222 */       INITIALIZATION_ERROR.set(new UnsupportedOperationException("Error occurred during initialization", error));
/*     */     } 
/*     */     
/* 225 */     MC_TEXT_GSON = gson;
/* 226 */     JSON_PARSER_INSTANCE = jsonParserInstance;
/* 227 */     TEXT_SERIALIZER_DESERIALIZE = textSerializerDeserialize;
/* 228 */     TEXT_SERIALIZER_SERIALIZE = textSerializerSerialize;
/* 229 */     TEXT_SERIALIZER_DESERIALIZE_TREE = textSerializerDeserializeTree;
/* 230 */     TEXT_SERIALIZER_SERIALIZE_TREE = textSerializerSerializeTree;
/*     */ 
/*     */     
/* 233 */     SUPPORTED = (MC_TEXT_GSON != null || (TEXT_SERIALIZER_DESERIALIZE != null && TEXT_SERIALIZER_SERIALIZE != null) || (TEXT_SERIALIZER_DESERIALIZE_TREE != null && TEXT_SERIALIZER_SERIALIZE_TREE != null));
/*     */   }
/*     */   @NotNull
/*     */   public Component deserialize(@NotNull Object input) {
/* 237 */     if (!SUPPORTED) throw (RuntimeException)INITIALIZATION_ERROR.get();
/*     */     
/*     */     try {
/*     */       Object element;
/* 241 */       if (TEXT_SERIALIZER_SERIALIZE_TREE != null) {
/* 242 */         element = TEXT_SERIALIZER_SERIALIZE_TREE.invoke(input);
/* 243 */       } else if (MC_TEXT_GSON != null) {
/* 244 */         element = ((Gson)MC_TEXT_GSON).toJsonTree(input);
/*     */       } else {
/* 246 */         return BukkitComponentSerializer.gson().deserialize(TEXT_SERIALIZER_SERIALIZE.invoke(input));
/*     */       } 
/* 248 */       return (Component)BukkitComponentSerializer.gson().serializer().fromJson(element.toString(), Component.class);
/* 249 */     } catch (Throwable error) {
/* 250 */       Object element; throw new UnsupportedOperationException(element);
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Object serialize(@NotNull Component component) {
/* 256 */     if (!SUPPORTED) throw (RuntimeException)INITIALIZATION_ERROR.get();
/*     */     
/* 258 */     if (TEXT_SERIALIZER_DESERIALIZE_TREE != null || MC_TEXT_GSON != null) {
/* 259 */       JsonElement json = BukkitComponentSerializer.gson().serializer().toJsonTree(component);
/*     */       try {
/* 261 */         if (TEXT_SERIALIZER_DESERIALIZE_TREE != null) {
/* 262 */           Object unRelocatedJsonElement = PARSE_JSON.invoke(JSON_PARSER_INSTANCE, json.toString());
/* 263 */           return TEXT_SERIALIZER_DESERIALIZE_TREE.invoke(unRelocatedJsonElement);
/*     */         } 
/* 265 */         return ((Gson)MC_TEXT_GSON).fromJson(json, CLASS_CHAT_COMPONENT);
/* 266 */       } catch (Throwable error) {
/* 267 */         throw new UnsupportedOperationException(error);
/*     */       } 
/*     */     } 
/*     */     try {
/* 271 */       return TEXT_SERIALIZER_DESERIALIZE.invoke((String)BukkitComponentSerializer.gson().serialize(component));
/* 272 */     } catch (Throwable error) {
/* 273 */       throw new UnsupportedOperationException(error);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\MinecraftComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */