/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.Objects;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class RegistryReflection
/*     */ {
/*     */   public static final Field REGISTRY_REGISTRY;
/*     */   public static final Method REGISTRY_GET;
/*     */   public static final Method REGISTRY_KEY;
/*  49 */   private static final Class<?> RESOURCE_LOCATION_CLASS = CraftBukkitReflection.needNMSClassOrElse("MinecraftKey", new String[] { "net.minecraft.resources.MinecraftKey", "net.minecraft.resources.ResourceLocation" });
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   private static final Class<?> RESOURCE_KEY_CLASS = CraftBukkitReflection.needNMSClassOrElse("ResourceKey", new String[] { "net.minecraft.resources.ResourceKey" });
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Executable NEW_RESOURCE_LOCATION;
/*     */ 
/*     */   
/*     */   private static final Executable CREATE_REGISTRY_RESOURCE_KEY;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  66 */     if (CraftBukkitReflection.MAJOR_REVISION < 17) {
/*  67 */       REGISTRY_REGISTRY = null;
/*  68 */       REGISTRY_GET = null;
/*  69 */       REGISTRY_KEY = null;
/*  70 */       NEW_RESOURCE_LOCATION = null;
/*  71 */       CREATE_REGISTRY_RESOURCE_KEY = null;
/*     */     } else {
/*  73 */       Class<?> registryClass = CraftBukkitReflection.<Class<?>>firstNonNullOrThrow(() -> "Registry", new Class[] {
/*     */             
/*  75 */             CraftBukkitReflection.findMCClass("core.IRegistry"), 
/*  76 */             CraftBukkitReflection.findMCClass("core.Registry")
/*     */           });
/*  78 */       REGISTRY_REGISTRY = registryRegistryField(registryClass);
/*  79 */       REGISTRY_REGISTRY.setAccessible(true);
/*  80 */       Class<?> resourceLocationClass = CraftBukkitReflection.<Class<?>>firstNonNullOrThrow(() -> "ResourceLocation class", new Class[] {
/*     */             
/*  82 */             CraftBukkitReflection.findMCClass("resources.ResourceLocation"), 
/*  83 */             CraftBukkitReflection.findMCClass("resources.MinecraftKey")
/*     */           });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  90 */       REGISTRY_GET = (Method)Arrays.<Method>stream(registryClass.getDeclaredMethods()).filter(it -> (it.getParameterCount() == 1 && it.getParameterTypes()[0].equals(resourceLocationClass) && it.getReturnType().equals(Object.class))).findFirst().orElseThrow(() -> new IllegalStateException("Could not find Registry#get(ResourceLocation)"));
/*     */       
/*  92 */       Class<?> resourceKeyClass = CraftBukkitReflection.needMCClass("resources.ResourceKey");
/*     */ 
/*     */ 
/*     */       
/*  96 */       REGISTRY_KEY = Arrays.<Method>stream(registryClass.getDeclaredMethods()).filter(m -> (m.getParameterCount() == 0 && m.getReturnType().equals(resourceKeyClass))).findFirst().orElse(null);
/*     */       
/*  98 */       NEW_RESOURCE_LOCATION = CraftBukkitReflection.<Executable>firstNonNullOrThrow(() -> "Could not find ResourceLocation#parse(String) or ResourceLocation#<init>(String)", new Executable[] {
/*     */             
/* 100 */             CraftBukkitReflection.findConstructor(RESOURCE_LOCATION_CLASS, new Class[] { String.class
/* 101 */               }), CraftBukkitReflection.findMethod(RESOURCE_LOCATION_CLASS, "parse", new Class[] { String.class
/* 102 */               }), CraftBukkitReflection.findMethod(RESOURCE_LOCATION_CLASS, "a", new Class[] { String.class })
/*     */           });
/*     */       
/* 105 */       CREATE_REGISTRY_RESOURCE_KEY = CraftBukkitReflection.<Executable>firstNonNullOrThrow(() -> "Could not find ResourceKey#createRegistryKey(ResourceLocation)", (Executable[])new Method[] {
/*     */             
/* 107 */             CraftBukkitReflection.findMethod(RESOURCE_KEY_CLASS, "createRegistryKey", new Class[] { RESOURCE_LOCATION_CLASS
/* 108 */               }), CraftBukkitReflection.findMethod(RESOURCE_KEY_CLASS, "a", new Class[] { RESOURCE_LOCATION_CLASS })
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Object registryKey(String registryName) {
/* 114 */     Objects.requireNonNull(CREATE_REGISTRY_RESOURCE_KEY, "CREATE_REGISTRY_RESOURCE_KEY");
/*     */     try {
/* 116 */       Object resourceLocation = createResourceLocation(registryName);
/* 117 */       return CraftBukkitReflection.invokeConstructorOrStaticMethod(CREATE_REGISTRY_RESOURCE_KEY, new Object[] { resourceLocation });
/* 118 */     } catch (ReflectiveOperationException e) {
/* 119 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Object get(Object registry, String resourceLocation) {
/* 124 */     Objects.requireNonNull(REGISTRY_GET, "REGISTRY_GET");
/*     */     try {
/* 126 */       return REGISTRY_GET.invoke(registry, new Object[] { createResourceLocation(resourceLocation) });
/* 127 */     } catch (ReflectiveOperationException e) {
/* 128 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Object builtInRegistryByName(String name) {
/* 133 */     Objects.requireNonNull(REGISTRY_REGISTRY, "REGISTRY_REGISTRY");
/*     */     try {
/* 135 */       return get(REGISTRY_REGISTRY.get(null), name);
/* 136 */     } catch (ReflectiveOperationException e) {
/* 137 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Object createResourceLocation(String str) {
/*     */     try {
/* 143 */       return CraftBukkitReflection.invokeConstructorOrStaticMethod(NEW_RESOURCE_LOCATION, new Object[] { str });
/* 144 */     } catch (ReflectiveOperationException e) {
/* 145 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Field registryRegistryField(Class<?> registryClass) {
/* 153 */     return Arrays.<Field>stream(registryClass.getDeclaredFields())
/* 154 */       .filter(it -> it.getType().equals(registryClass))
/* 155 */       .findFirst()
/* 156 */       .orElseGet(() -> registryRegistryFieldFromBuiltInRegistries(registryClass));
/*     */   }
/*     */ 
/*     */   
/*     */   private static Field registryRegistryFieldFromBuiltInRegistries(Class<?> registryClass) {
/* 161 */     Class<?> builtInRegistriesClass = CraftBukkitReflection.needMCClass("core.registries.BuiltInRegistries");
/* 162 */     return (Field)Arrays.<Field>stream(builtInRegistriesClass.getDeclaredFields())
/* 163 */       .filter(it -> {
/*     */           if (!it.getType().equals(registryClass) || !Modifier.isStatic(it.getModifiers())) {
/*     */             return false;
/*     */           }
/*     */           
/*     */           Type genericType = it.getGenericType();
/*     */           
/*     */           if (!(genericType instanceof ParameterizedType)) {
/*     */             return false;
/*     */           }
/*     */           
/*     */           Type valueType;
/*     */           for (valueType = ((ParameterizedType)genericType).getActualTypeArguments()[0]; valueType instanceof WildcardType; valueType = ((WildcardType)valueType).getUpperBounds()[0]);
/*     */           return GenericTypeReflector.erase(valueType).equals(registryClass);
/* 177 */         }).findFirst()
/* 178 */       .orElseThrow(() -> new IllegalStateException("Could not find Registry Registry field"));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\RegistryReflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */