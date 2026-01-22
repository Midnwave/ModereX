/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Executable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Stream;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
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
/*     */ public final class CraftBukkitReflection
/*     */ {
/*     */   private static final String PREFIX_NMS = "net.minecraft.server";
/*     */   private static final String PREFIX_MC = "net.minecraft.";
/*     */   private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";
/*     */   private static final String CRAFT_SERVER = "CraftServer";
/*     */   private static final String CB_PKG_VERSION;
/*     */   public static final int MAJOR_REVISION;
/*     */   
/*     */   static {
/*     */     Class<?> serverClass;
/*  55 */     if (Bukkit.getServer() == null) {
/*     */       
/*  57 */       serverClass = needClass("org.bukkit.craftbukkit.CraftServer");
/*     */     } else {
/*  59 */       serverClass = Bukkit.getServer().getClass();
/*     */     } 
/*  61 */     String pkg = serverClass.getPackage().getName();
/*  62 */     String nmsVersion = pkg.substring(pkg.lastIndexOf(".") + 1);
/*  63 */     if (!nmsVersion.contains("_")) {
/*  64 */       int fallbackVersion = -1;
/*  65 */       if (Bukkit.getServer() != null) {
/*     */         try {
/*  67 */           Method getMinecraftVersion = serverClass.getDeclaredMethod("getMinecraftVersion", new Class[0]);
/*  68 */           fallbackVersion = Integer.parseInt(getMinecraftVersion.invoke(Bukkit.getServer(), new Object[0]).toString().split("\\.")[1]);
/*  69 */         } catch (Exception exception) {}
/*     */       } else {
/*     */ 
/*     */         
/*     */         try {
/*  74 */           Class<?> sharedConstants = needClass("net.minecraft.SharedConstants");
/*  75 */           Method getCurrentVersion = sharedConstants.getDeclaredMethod("getCurrentVersion", new Class[0]);
/*  76 */           Object currentVersion = getCurrentVersion.invoke(null, new Object[0]);
/*  77 */           Method getName = null;
/*     */           try {
/*  79 */             getName = currentVersion.getClass().getDeclaredMethod("getName", new Class[0]);
/*  80 */           } catch (NoSuchMethodException noSuchMethodException) {}
/*     */           
/*  82 */           if (getName == null)
/*     */           {
/*  84 */             getName = currentVersion.getClass().getDeclaredMethod("name", new Class[0]);
/*     */           }
/*  86 */           String versionName = (String)getName.invoke(currentVersion, new Object[0]);
/*     */           try {
/*  88 */             fallbackVersion = Integer.parseInt(versionName.split("\\.")[1]);
/*  89 */           } catch (Exception exception) {}
/*     */         }
/*  91 */         catch (ReflectiveOperationException e) {
/*  92 */           throw new RuntimeException(e);
/*     */         } 
/*     */       } 
/*  95 */       MAJOR_REVISION = fallbackVersion;
/*     */     } else {
/*  97 */       MAJOR_REVISION = Integer.parseInt(nmsVersion.split("_")[1]);
/*     */     } 
/*  99 */     String name = serverClass.getName();
/* 100 */     name = name.substring("org.bukkit.craftbukkit".length());
/* 101 */     name = name.substring(0, name.length() - "CraftServer".length());
/* 102 */     CB_PKG_VERSION = name;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static <T> T firstNonNullOrNull(T... elements) {
/* 109 */     for (T element : elements) {
/* 110 */       if (element != null) {
/* 111 */         return element;
/*     */       }
/*     */     } 
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static <T> T firstNonNullOrThrow(Supplier<String> errorMessage, T... elements) {
/* 123 */     T t = firstNonNullOrNull(elements);
/* 124 */     if (t == null) {
/* 125 */       throw new IllegalArgumentException((String)errorMessage.get());
/*     */     }
/* 127 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> needNMSClassOrElse(String nms, String... classNames) throws RuntimeException {
/* 134 */     Class<?> nmsClass = findNMSClass(nms);
/* 135 */     if (nmsClass != null) {
/* 136 */       return nmsClass;
/*     */     }
/* 138 */     return firstNonNullOrThrow(() -> String.format("Cound't find the NMS class '%s', or any of the following fallbacks: %s", new Object[] {
/*     */             
/*     */             nms, Arrays.toString((Object[])classNames)
/*     */ 
/*     */ 
/*     */           
/* 144 */           }), (Class[])Arrays.<String>stream(classNames)
/* 145 */         .map(CraftBukkitReflection::findClass)
/* 146 */         .toArray(x$0 -> new Class[x$0]));
/*     */   }
/*     */ 
/*     */   
/*     */   public static Class<?> needMCClass(String name) throws RuntimeException {
/* 151 */     return needClass("net.minecraft." + name);
/*     */   }
/*     */   
/*     */   public static Class<?> needNMSClass(String className) throws RuntimeException {
/* 155 */     return needClass("net.minecraft.server" + CB_PKG_VERSION + className);
/*     */   }
/*     */   
/*     */   public static Class<?> needOBCClass(String className) throws RuntimeException {
/* 159 */     return needClass("org.bukkit.craftbukkit" + CB_PKG_VERSION + className);
/*     */   }
/*     */   
/*     */   public static Class<?> findMCClass(String name) throws RuntimeException {
/* 163 */     return findClass("net.minecraft." + name);
/*     */   }
/*     */   
/*     */   public static Class<?> findNMSClass(String className) throws RuntimeException {
/* 167 */     return findClass("net.minecraft.server" + CB_PKG_VERSION + className);
/*     */   }
/*     */   
/*     */   public static Class<?> findOBCClass(String className) throws RuntimeException {
/* 171 */     return findClass("org.bukkit.craftbukkit" + CB_PKG_VERSION + className);
/*     */   }
/*     */   
/*     */   public static Class<?> needClass(String className) throws RuntimeException {
/*     */     try {
/* 176 */       return Class.forName(className);
/* 177 */     } catch (ClassNotFoundException e) {
/* 178 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Class<?> findClass(String className) {
/*     */     try {
/* 184 */       return Class.forName(className);
/* 185 */     } catch (ClassNotFoundException e) {
/* 186 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Field needField(Class<?> holder, String name) throws RuntimeException {
/*     */     try {
/* 192 */       Field field = holder.getDeclaredField(name);
/* 193 */       field.setAccessible(true);
/* 194 */       return field;
/* 195 */     } catch (ReflectiveOperationException e) {
/* 196 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Field findField(Class<?> holder, String name) throws RuntimeException {
/*     */     try {
/* 202 */       return needField(holder, name);
/* 203 */     } catch (RuntimeException e) {
/* 204 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Constructor<?> needConstructor(Class<?> holder, Class<?>... parameters) {
/*     */     try {
/* 210 */       return holder.getDeclaredConstructor(parameters);
/* 211 */     } catch (NoSuchMethodException ex) {
/* 212 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Constructor<?> findConstructor(Class<?> holder, Class<?>... parameters) {
/*     */     try {
/* 218 */       return holder.getDeclaredConstructor(parameters);
/* 219 */     } catch (NoSuchMethodException ex) {
/* 220 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean classExists(String className) {
/* 225 */     return (findClass(className) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method findMethod(Class<?> holder, String name, Class<?>... params) throws RuntimeException {
/*     */     try {
/* 234 */       return holder.getMethod(name, params);
/* 235 */     } catch (NoSuchMethodException e) {
/* 236 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method needMethod(Class<?> holder, String name, Class<?>... params) throws RuntimeException {
/*     */     try {
/* 246 */       return holder.getMethod(name, params);
/* 247 */     } catch (NoSuchMethodException e) {
/* 248 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Stream<Method> streamMethods(Class<?> clazz) {
/* 253 */     return Arrays.stream(clazz.getDeclaredMethods());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object invokeConstructorOrStaticMethod(Executable executable, Object... args) throws ReflectiveOperationException {
/* 260 */     if (executable instanceof Constructor) {
/* 261 */       return ((Constructor)executable).newInstance(args);
/*     */     }
/* 263 */     if (!Modifier.isStatic(executable.getModifiers())) {
/* 264 */       throw new IllegalArgumentException("Method " + executable + " is not static.");
/*     */     }
/* 266 */     return ((Method)executable).invoke(null, args);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\CraftBukkitReflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */