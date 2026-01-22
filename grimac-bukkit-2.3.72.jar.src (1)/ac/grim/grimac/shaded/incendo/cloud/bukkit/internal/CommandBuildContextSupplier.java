/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.stream.Collectors;
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
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ public final class CommandBuildContextSupplier
/*     */ {
/*  41 */   private static final Class<?> COMMAND_BUILD_CONTEXT_CLASS = CraftBukkitReflection.needMCClass("commands.CommandBuildContext");
/*     */   private static final Constructor<?> COMMAND_BUILD_CONTEXT_CTR;
/*     */   private static final Method CREATE_CONTEXT_METHOD;
/*     */   private static final Method GET_WORLD_DATA_METHOD;
/*     */   private static final Method GET_FEATURE_FLAGS_METHOD;
/*     */   private static final Class<?> REG_ACC_CLASS;
/*  47 */   private static final Class<?> MC_SERVER_CLASS = CraftBukkitReflection.needNMSClassOrElse("MinecraftServer", new String[] { "net.minecraft.server.MinecraftServer" });
/*     */   
/*     */   private static final Method GET_SERVER_METHOD;
/*     */ 
/*     */   
/*     */   static {
/*     */     try {
/*  54 */       ctr = COMMAND_BUILD_CONTEXT_CLASS.getDeclaredConstructors()[0];
/*  55 */     } catch (Exception ex) {
/*  56 */       ctr = null;
/*     */     } 
/*  58 */     COMMAND_BUILD_CONTEXT_CTR = ctr;
/*     */     
/*  60 */     if (COMMAND_BUILD_CONTEXT_CTR == null) {
/*     */ 
/*     */ 
/*     */       
/*  64 */       List<Method> matchingFactoryMethods = (List<Method>)Arrays.<Method>stream(COMMAND_BUILD_CONTEXT_CLASS.getDeclaredMethods()).filter(it -> (it.getParameterCount() == 2 && COMMAND_BUILD_CONTEXT_CLASS.isAssignableFrom(it.getReturnType()) && Modifier.isStatic(it.getModifiers()))).collect(Collectors.toList());
/*  65 */       if (matchingFactoryMethods.size() == 1) {
/*     */         
/*  67 */         CREATE_CONTEXT_METHOD = matchingFactoryMethods.get(0);
/*  68 */       } else if (matchingFactoryMethods.size() > 1) {
/*     */         
/*  70 */         CREATE_CONTEXT_METHOD = matchingFactoryMethods.get(1);
/*     */       } else {
/*  72 */         throw new IllegalStateException("Could not find CommandBuildContext factory method");
/*     */       } 
/*     */       
/*  75 */       Class<?> worldDataCls = CraftBukkitReflection.<Class<?>>firstNonNullOrThrow(() -> "Could not find WorldData class", new Class[] {
/*     */             
/*  77 */             CraftBukkitReflection.findMCClass("world.level.storage.SaveData"), 
/*  78 */             CraftBukkitReflection.findMCClass("world.level.storage.WorldData")
/*     */           });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  85 */       GET_WORLD_DATA_METHOD = (Method)Arrays.<Method>stream(MC_SERVER_CLASS.getDeclaredMethods()).filter(it -> (it.getParameterCount() == 0 && !Modifier.isStatic(it.getModifiers()) && it.getReturnType().equals(worldDataCls))).findFirst().orElseThrow(() -> new IllegalStateException("Could not find MinecraftServer#getWorldData method"));
/*  86 */       Class<?> featureFlagSetCls = CraftBukkitReflection.needMCClass("world.flag.FeatureFlagSet");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  92 */       GET_FEATURE_FLAGS_METHOD = (Method)Arrays.<Method>stream(worldDataCls.getDeclaredMethods()).filter(it -> (it.getParameterCount() == 0 && it.getReturnType().equals(featureFlagSetCls) && !Modifier.isStatic(it.getModifiers()))).findFirst().orElseThrow(() -> new IllegalStateException("Could not find enabledFeatures method"));
/*     */     } else {
/*  94 */       CREATE_CONTEXT_METHOD = null;
/*  95 */       GET_WORLD_DATA_METHOD = null;
/*  96 */       GET_FEATURE_FLAGS_METHOD = null;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 101 */     REG_ACC_CLASS = (COMMAND_BUILD_CONTEXT_CTR != null) ? COMMAND_BUILD_CONTEXT_CTR.getParameterTypes()[0] : CREATE_CONTEXT_METHOD.getParameterTypes()[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     REGISTRY_ACCESS = (Method)Arrays.<Method>stream(MC_SERVER_CLASS.getDeclaredMethods()).filter(m -> REG_ACC_CLASS.isAssignableFrom(m.getReturnType())).findFirst().orElseThrow(() -> new IllegalStateException("Cannot find MinecraftServer#registryAccess"));
/*     */ 
/*     */     
/*     */     try {
/* 112 */       GET_SERVER_METHOD = MC_SERVER_CLASS.getDeclaredMethod("getServer", new Class[0]);
/* 113 */     } catch (NoSuchMethodException e) {
/* 114 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   private static final Method REGISTRY_ACCESS;
/*     */   static {
/*     */     Constructor<?> ctr;
/*     */   }
/*     */   public static Object commandBuildContext() {
/* 122 */     if (COMMAND_BUILD_CONTEXT_CTR != null)
/*     */       try {
/* 124 */         Object server = GET_SERVER_METHOD.invoke(null, new Object[0]);
/* 125 */         return COMMAND_BUILD_CONTEXT_CTR.newInstance(new Object[] { REGISTRY_ACCESS.invoke(server, new Object[0]) });
/* 126 */       } catch (ReflectiveOperationException e) {
/* 127 */         throw new RuntimeException(e);
/*     */       }  
/* 129 */     if (CREATE_CONTEXT_METHOD != null && GET_WORLD_DATA_METHOD != null && GET_FEATURE_FLAGS_METHOD != null) {
/*     */       try {
/* 131 */         Object server = GET_SERVER_METHOD.invoke(null, new Object[0]);
/* 132 */         Object worldData = GET_WORLD_DATA_METHOD.invoke(server, new Object[0]);
/* 133 */         Object flags = GET_FEATURE_FLAGS_METHOD.invoke(worldData, new Object[0]);
/* 134 */         return CREATE_CONTEXT_METHOD.invoke(null, new Object[] { REGISTRY_ACCESS.invoke(server, new Object[0]), flags });
/* 135 */       } catch (ReflectiveOperationException e) {
/* 136 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/* 139 */     throw new IllegalStateException();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\CommandBuildContextSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */