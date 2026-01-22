/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.invoke.MethodType;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Arrays;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ final class MinecraftReflection
/*     */ {
/*  47 */   private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
/*     */   
/*     */   private static final String PREFIX_NMS = "net.minecraft.server";
/*     */   
/*     */   private static final String PREFIX_MC = "net.minecraft.";
/*     */ 
/*     */   
/*     */   static {
/*  55 */     Class<?> serverClass = Bukkit.getServer().getClass();
/*  56 */     if (!serverClass.getSimpleName().equals("CraftServer")) {
/*  57 */       VERSION = null;
/*  58 */     } else if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
/*  59 */       VERSION = ".";
/*     */     } else {
/*  61 */       String name = serverClass.getName();
/*  62 */       name = name.substring("org.bukkit.craftbukkit".length());
/*  63 */       name = name.substring(0, name.length() - "CraftServer".length());
/*  64 */       VERSION = name;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";
/*     */   private static final String CRAFT_SERVER = "CraftServer";
/*     */   @Nullable
/*     */   private static final String VERSION;
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> findClass(@Nullable String... classNames) {
/*  75 */     for (String clazz : classNames) {
/*  76 */       if (clazz != null) {
/*     */         
/*     */         try {
/*  79 */           Class<?> classObj = Class.forName(clazz);
/*  80 */           return classObj;
/*  81 */         } catch (ClassNotFoundException classNotFoundException) {}
/*     */       }
/*     */     } 
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Class<?> needClass(@Nullable String... className) {
/*  95 */     return Objects.<Class<?>>requireNonNull(findClass(className), "Could not find class from candidates" + Arrays.toString((Object[])className));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasClass(@NotNull String... classNames) {
/* 105 */     return (findClass(classNames) != null);
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
/*     */   @Nullable
/*     */   public static MethodHandle findMethod(@Nullable Class<?> holderClass, String methodName, @Nullable Class<?> returnClass, Class<?>... parameterClasses) {
/* 118 */     return findMethod(holderClass, new String[] { methodName }, returnClass, parameterClasses);
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
/*     */   @Nullable
/*     */   public static MethodHandle findMethod(@Nullable Class<?> holderClass, @Nullable String[] methodNames, @Nullable Class<?> returnClass, Class<?>... parameterClasses) {
/* 131 */     if (holderClass == null || returnClass == null) return null; 
/* 132 */     for (Class<?> parameterClass : parameterClasses) {
/* 133 */       if (parameterClass == null) return null;
/*     */     
/*     */     } 
/* 136 */     for (String methodName : methodNames) {
/* 137 */       if (methodName != null) {
/*     */         try {
/* 139 */           return LOOKUP.findVirtual(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
/* 140 */         } catch (NoSuchMethodException|IllegalAccessException noSuchMethodException) {}
/*     */       }
/*     */     } 
/* 143 */     return null;
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
/*     */   public static MethodHandle searchMethod(@Nullable Class<?> holderClass, @Nullable Integer modifier, String methodName, @Nullable Class<?> returnClass, Class<?>... parameterClasses) {
/* 157 */     return searchMethod(holderClass, modifier, new String[] { methodName }, returnClass, parameterClasses);
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
/*     */   public static MethodHandle searchMethod(@Nullable Class<?> holderClass, @Nullable Integer modifier, @Nullable String[] methodNames, @Nullable Class<?> returnClass, Class<?>... parameterClasses) {
/* 171 */     if (holderClass == null || returnClass == null) return null; 
/* 172 */     for (Class<?> parameterClass : parameterClasses) {
/* 173 */       if (parameterClass == null) return null;
/*     */     
/*     */     } 
/* 176 */     for (String methodName : methodNames) {
/* 177 */       if (methodName != null) {
/*     */         try {
/* 179 */           if (modifier != null && Modifier.isStatic(modifier.intValue())) {
/* 180 */             return LOOKUP.findStatic(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
/*     */           }
/* 182 */           return LOOKUP.findVirtual(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
/*     */         }
/* 184 */         catch (NoSuchMethodException|IllegalAccessException noSuchMethodException) {}
/*     */       }
/*     */     } 
/*     */     
/* 188 */     for (Method method : holderClass.getDeclaredMethods()) {
/* 189 */       if (modifier != null && (method.getModifiers() & modifier.intValue()) != 0 && 
/* 190 */         Arrays.equals((Object[])method.getParameterTypes(), (Object[])parameterClasses)) {
/*     */         try {
/* 192 */           if (Modifier.isStatic(modifier.intValue())) {
/* 193 */             return LOOKUP.findStatic(holderClass, method.getName(), MethodType.methodType(returnClass, parameterClasses));
/*     */           }
/* 195 */           return LOOKUP.findVirtual(holderClass, method.getName(), MethodType.methodType(returnClass, parameterClasses));
/*     */         }
/* 197 */         catch (NoSuchMethodException|IllegalAccessException noSuchMethodException) {}
/*     */       }
/*     */     } 
/* 200 */     return null;
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
/*     */   @Nullable
/*     */   public static MethodHandle findStaticMethod(@Nullable Class<?> holderClass, String methodNames, @Nullable Class<?> returnClass, Class<?>... parameterClasses) {
/* 213 */     return findStaticMethod(holderClass, new String[] { methodNames }, returnClass, parameterClasses);
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
/*     */   @Nullable
/*     */   public static MethodHandle findStaticMethod(@Nullable Class<?> holderClass, String[] methodNames, @Nullable Class<?> returnClass, Class<?>... parameterClasses) {
/* 227 */     if (holderClass == null || returnClass == null) return null; 
/* 228 */     for (Class<?> parameterClass : parameterClasses) {
/* 229 */       if (parameterClass == null) return null;
/*     */     
/*     */     } 
/* 232 */     for (String methodName : methodNames) {
/*     */       try {
/* 234 */         return LOOKUP.findStatic(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
/* 235 */       } catch (NoSuchMethodException|IllegalAccessException noSuchMethodException) {}
/*     */     } 
/*     */ 
/*     */     
/* 239 */     return null;
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
/*     */   public static boolean hasField(@Nullable Class<?> holderClass, Class<?> type, String... names) {
/* 251 */     if (holderClass == null) return false;
/*     */     
/* 253 */     for (String name : names) {
/*     */       try {
/* 255 */         Field field = holderClass.getDeclaredField(name);
/* 256 */         if (field.getType() == type) return true; 
/* 257 */       } catch (NoSuchFieldException noSuchFieldException) {}
/*     */     } 
/*     */ 
/*     */     
/* 261 */     return false;
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
/*     */   public static boolean hasMethod(@Nullable Class<?> holderClass, String methodName, Class<?>... parameterClasses) {
/* 273 */     return hasMethod(holderClass, new String[] { methodName }, parameterClasses);
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
/*     */   public static boolean hasMethod(@Nullable Class<?> holderClass, String[] methodNames, Class<?>... parameterClasses) {
/* 285 */     if (holderClass == null) return false; 
/* 286 */     for (Class<?> parameterClass : parameterClasses) {
/* 287 */       if (parameterClass == null) return false;
/*     */     
/*     */     } 
/* 290 */     for (String methodName : methodNames) {
/*     */       try {
/* 292 */         holderClass.getMethod(methodName, parameterClasses);
/* 293 */         return true;
/* 294 */       } catch (NoSuchMethodException noSuchMethodException) {}
/*     */     } 
/*     */ 
/*     */     
/* 298 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static MethodHandle findConstructor(@Nullable Class<?> holderClass, @Nullable Class<?>... parameterClasses) {
/* 309 */     if (holderClass == null) return null; 
/* 310 */     for (Class<?> parameterClass : parameterClasses) {
/* 311 */       if (parameterClass == null) return null;
/*     */     
/*     */     } 
/*     */     try {
/* 315 */       return LOOKUP.findConstructor(holderClass, MethodType.methodType(void.class, parameterClasses));
/* 316 */     } catch (NoSuchMethodException|IllegalAccessException e) {
/* 317 */       return null;
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
/*     */   @NotNull
/*     */   public static Field needField(@NotNull Class<?> holderClass, @NotNull String fieldName) throws NoSuchFieldException {
/* 330 */     Field field = holderClass.getDeclaredField(fieldName);
/* 331 */     field.setAccessible(true);
/* 332 */     return field;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Field findField(@Nullable Class<?> holderClass, @NotNull String... fieldName) {
/* 343 */     return findField(holderClass, null, fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Field findField(@Nullable Class<?> holderClass, @Nullable Class<?> expectedType, @NotNull String... fieldNames) {
/* 355 */     if (holderClass == null) return null;
/*     */ 
/*     */     
/* 358 */     for (String fieldName : fieldNames) {
/*     */       Field field; try {
/* 360 */         field = holderClass.getDeclaredField(fieldName);
/* 361 */       } catch (NoSuchFieldException ex) {}
/*     */ 
/*     */ 
/*     */       
/* 365 */       field.setAccessible(true);
/* 366 */       if (expectedType == null || expectedType.isAssignableFrom(field.getType()))
/*     */       {
/*     */ 
/*     */         
/* 370 */         return field;
/*     */       }
/*     */     } 
/* 373 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static MethodHandle findSetterOf(@Nullable Field field) {
/* 383 */     if (field == null) return null;
/*     */     
/*     */     try {
/* 386 */       return LOOKUP.unreflectSetter(field);
/* 387 */     } catch (IllegalAccessException e) {
/* 388 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static MethodHandle findGetterOf(@Nullable Field field) {
/* 399 */     if (field == null) return null;
/*     */     
/*     */     try {
/* 402 */       return LOOKUP.unreflectGetter(field);
/* 403 */     } catch (IllegalAccessException e) {
/* 404 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Object findEnum(@Nullable Class<?> enumClass, @NotNull String enumName) {
/* 416 */     return findEnum(enumClass, enumName, 2147483647);
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
/*     */   @Nullable
/*     */   public static Object findEnum(@Nullable Class<?> enumClass, @NotNull String enumName, int enumFallbackOrdinal) {
/* 429 */     if (enumClass == null || !Enum.class.isAssignableFrom(enumClass)) {
/* 430 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 434 */       return Enum.valueOf((Class)enumClass.asSubclass(Enum.class), enumName);
/* 435 */     } catch (IllegalArgumentException e) {
/* 436 */       Object[] constants = enumClass.getEnumConstants();
/* 437 */       if (constants.length > enumFallbackOrdinal) {
/* 438 */         return constants[enumFallbackOrdinal];
/*     */       }
/*     */ 
/*     */       
/* 442 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isCraftBukkit() {
/* 451 */     return (VERSION != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String findCraftClassName(@NotNull String className) {
/* 461 */     return isCraftBukkit() ? ("org.bukkit.craftbukkit" + VERSION + className) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> findCraftClass(@NotNull String className) {
/* 471 */     String craftClassName = findCraftClassName(className);
/* 472 */     if (craftClassName == null) {
/* 473 */       return null;
/*     */     }
/*     */     
/* 476 */     return findClass(new String[] { craftClassName });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static <T> Class<? extends T> findCraftClass(@NotNull String className, @NotNull Class<T> superClass) {
/* 488 */     Class<?> craftClass = findCraftClass(className);
/* 489 */     if (craftClass == null || !((Class)Objects.<Class<?>>requireNonNull(superClass, "superClass")).isAssignableFrom(craftClass)) {
/* 490 */       return null;
/*     */     }
/* 492 */     return craftClass.asSubclass(superClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Class<?> needCraftClass(@NotNull String className) {
/* 503 */     return Objects.<Class<?>>requireNonNull(findCraftClass(className), "Could not find org.bukkit.craftbukkit class " + className);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String findNmsClassName(@NotNull String className) {
/* 513 */     return isCraftBukkit() ? ("net.minecraft.server" + VERSION + className) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> findNmsClass(@NotNull String className) {
/* 523 */     String nmsClassName = findNmsClassName(className);
/* 524 */     if (nmsClassName == null) {
/* 525 */       return null;
/*     */     }
/*     */     
/* 528 */     return findClass(new String[] { nmsClassName });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Class<?> needNmsClass(@NotNull String className) {
/* 539 */     return Objects.<Class<?>>requireNonNull(findNmsClass(className), "Could not find net.minecraft.server class " + className);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String findMcClassName(@NotNull String className) {
/* 549 */     return isCraftBukkit() ? ("net.minecraft." + className) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> findMcClass(@NotNull String... classNames) {
/* 559 */     for (String clazz : classNames) {
/* 560 */       String nmsClassName = findMcClassName(clazz);
/* 561 */       if (nmsClassName != null) {
/* 562 */         Class<?> candidate = findClass(new String[] { nmsClassName });
/* 563 */         if (candidate != null) {
/* 564 */           return candidate;
/*     */         }
/*     */       } 
/*     */     } 
/* 568 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Class<?> needMcClass(@NotNull String... className) {
/* 579 */     return Objects.<Class<?>>requireNonNull(findMcClass(className), "Could not find net.minecraft class from candidates" + Arrays.toString((Object[])className));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MethodHandles.Lookup lookup() {
/* 588 */     return LOOKUP;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\MinecraftReflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */