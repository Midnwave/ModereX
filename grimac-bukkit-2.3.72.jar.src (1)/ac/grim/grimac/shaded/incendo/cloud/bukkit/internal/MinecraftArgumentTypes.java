/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*     */ 
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.NamespacedKey;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class MinecraftArgumentTypes
/*     */ {
/*     */   private static final ArgumentTypeGetter ARGUMENT_TYPE_GETTER;
/*     */   
/*     */   static {
/*  53 */     if (CraftBukkitReflection.classExists("org.bukkit.entity.Warden")) {
/*  54 */       ARGUMENT_TYPE_GETTER = new ArgumentTypeGetterImpl();
/*     */     } else {
/*  56 */       ARGUMENT_TYPE_GETTER = new LegacyArgumentTypeGetter();
/*     */     } 
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
/*     */   public static Class<? extends ArgumentType<?>> getClassByKey(NamespacedKey key) throws IllegalArgumentException {
/*  70 */     return ARGUMENT_TYPE_GETTER.getClassByKey(key);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static interface ArgumentTypeGetter
/*     */   {
/*     */     Class<? extends ArgumentType<?>> getClassByKey(NamespacedKey param1NamespacedKey) throws IllegalArgumentException;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ArgumentTypeGetterImpl
/*     */     implements ArgumentTypeGetter
/*     */   {
/*  85 */     private final Supplier<Object> argumentRegistry = (Supplier<Object>)Suppliers.memoize(() -> RegistryReflection.builtInRegistryByName("command_argument_type")); private final Map<?, ?> byClassMap;
/*     */     private ArgumentTypeGetterImpl() {
/*     */       try {
/*  88 */         Field declaredField = CraftBukkitReflection.needMCClass("commands.synchronization.ArgumentTypeInfos").getDeclaredFields()[0];
/*  89 */         declaredField.setAccessible(true);
/*  90 */         this.byClassMap = (Map<?, ?>)declaredField.get(null);
/*  91 */       } catch (ReflectiveOperationException e) {
/*  92 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<? extends ArgumentType<?>> getClassByKey(NamespacedKey key) throws IllegalArgumentException {
/*  98 */       Object argTypeInfo = RegistryReflection.get(this.argumentRegistry.get(), key.getNamespace() + ":" + key.getKey());
/*  99 */       for (Map.Entry<?, ?> entry : this.byClassMap.entrySet()) {
/* 100 */         if (entry.getValue() == argTypeInfo) {
/* 101 */           return (Class<? extends ArgumentType<?>>)entry.getKey();
/*     */         }
/*     */       } 
/* 104 */       throw new IllegalArgumentException(key.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class LegacyArgumentTypeGetter
/*     */     implements ArgumentTypeGetter
/*     */   {
/*     */     private static final Constructor<?> MINECRAFT_KEY_CONSTRUCTOR;
/*     */     private static final Method ARGUMENT_REGISTRY_GET_BY_KEY_METHOD;
/*     */     private static final Field BY_CLASS_MAP_FIELD;
/*     */     
/*     */     private LegacyArgumentTypeGetter() {}
/*     */     
/*     */     static {
/*     */       try {
/*     */         Class<?> minecraftKey, argumentRegistry;
/* 120 */         if (CraftBukkitReflection.findMCClass("resources.ResourceLocation") != null) {
/* 121 */           minecraftKey = CraftBukkitReflection.needMCClass("resources.ResourceLocation");
/* 122 */           argumentRegistry = CraftBukkitReflection.needMCClass("commands.synchronization.ArgumentTypes");
/*     */         } else {
/* 124 */           minecraftKey = CraftBukkitReflection.needNMSClassOrElse("MinecraftKey", new String[] { "net.minecraft.resources.MinecraftKey" });
/*     */ 
/*     */ 
/*     */           
/* 128 */           argumentRegistry = CraftBukkitReflection.needNMSClassOrElse("ArgumentRegistry", new String[] { "net.minecraft.commands.synchronization.ArgumentRegistry" });
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 134 */         MINECRAFT_KEY_CONSTRUCTOR = minecraftKey.getConstructor(new Class[] { String.class, String.class });
/* 135 */         MINECRAFT_KEY_CONSTRUCTOR.setAccessible(true);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 140 */         ARGUMENT_REGISTRY_GET_BY_KEY_METHOD = (Method)Arrays.<Method>stream(argumentRegistry.getDeclaredMethods()).filter(method -> (method.getParameterCount() == 1)).filter(method -> minecraftKey.equals(method.getParameterTypes()[0])).findFirst().orElseThrow(NoSuchMethodException::new);
/* 141 */         ARGUMENT_REGISTRY_GET_BY_KEY_METHOD.setAccessible(true);
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
/* 155 */         BY_CLASS_MAP_FIELD = (Field)Arrays.<Field>stream(argumentRegistry.getDeclaredFields()).filter(field -> Modifier.isStatic(field.getModifiers())).filter(field -> field.getType().equals(Map.class)).filter(field -> { ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType(); Type param = parameterizedType.getActualTypeArguments()[0]; return !(param instanceof ParameterizedType) ? false : ((ParameterizedType)param).getRawType().equals(Class.class); }).findFirst().orElseThrow(NoSuchFieldException::new);
/* 156 */         BY_CLASS_MAP_FIELD.setAccessible(true);
/* 157 */       } catch (ReflectiveOperationException e) {
/* 158 */         throw new ExceptionInInitializerError(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<? extends ArgumentType<?>> getClassByKey(NamespacedKey key) throws IllegalArgumentException {
/*     */       try {
/* 165 */         Object minecraftKey = MINECRAFT_KEY_CONSTRUCTOR.newInstance(new Object[] { key.getNamespace(), key.getKey() });
/* 166 */         Object entry = ARGUMENT_REGISTRY_GET_BY_KEY_METHOD.invoke(null, new Object[] { minecraftKey });
/* 167 */         if (entry == null) {
/* 168 */           throw new IllegalArgumentException(key.toString());
/*     */         }
/*     */         
/* 171 */         Map<Class<?>, Object> map = (Map<Class<?>, Object>)BY_CLASS_MAP_FIELD.get(null);
/* 172 */         for (Map.Entry<Class<?>, Object> mapEntry : map.entrySet()) {
/* 173 */           if (mapEntry.getValue() == entry) {
/* 174 */             return (Class<? extends ArgumentType<?>>)mapEntry.getKey();
/*     */           }
/*     */         } 
/* 177 */         throw new IllegalArgumentException(key.toString());
/* 178 */       } catch (ReflectiveOperationException e) {
/* 179 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\MinecraftArgumentTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */