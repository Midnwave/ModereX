/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public final class Reflection
/*     */ {
/*     */   public static Field[] getFields(Class<?> cls) {
/*  35 */     if (cls == null) {
/*  36 */       return new Field[0];
/*     */     }
/*  38 */     Field[] declaredFields = cls.getDeclaredFields();
/*  39 */     for (Field f : declaredFields) {
/*     */       try {
/*  41 */         f.setAccessible(true);
/*  42 */       } catch (Throwable throwable) {}
/*     */     } 
/*     */ 
/*     */     
/*  46 */     return declaredFields;
/*     */   }
/*     */   
/*     */   public static Field getField(Class<?> cls, String name) {
/*  50 */     if (cls == null) {
/*  51 */       return null;
/*     */     }
/*     */     try {
/*  54 */       Field field = cls.getDeclaredField(name);
/*  55 */       field.setAccessible(true);
/*  56 */       return field;
/*  57 */     } catch (NoSuchFieldException e) {
/*  58 */       if (cls.getSuperclass() != null) {
/*  59 */         return getField(cls.getSuperclass(), name);
/*     */       }
/*  61 */     } catch (Throwable throwable) {}
/*     */ 
/*     */     
/*  64 */     return null;
/*     */   }
/*     */   
/*     */   public static Field getField(Class<?> cls, Class<?> dataType, int index) {
/*  68 */     if (dataType == null || cls == null) {
/*  69 */       return null;
/*     */     }
/*  71 */     int currentIndex = 0;
/*  72 */     for (Field f : getFields(cls)) {
/*  73 */       if (dataType.isAssignableFrom(f.getType()) && 
/*  74 */         currentIndex++ == index) {
/*  75 */         return f;
/*     */       }
/*     */     } 
/*     */     
/*  79 */     if (cls.getSuperclass() != null) {
/*  80 */       return getField(cls.getSuperclass(), dataType, index);
/*     */     }
/*  82 */     return null;
/*     */   }
/*     */   
/*     */   public static Field getField(Class<?> cls, Class<?> dataType, int index, boolean ignoreStatic) {
/*  86 */     if (dataType == null || cls == null) {
/*  87 */       return null;
/*     */     }
/*  89 */     int currentIndex = 0;
/*  90 */     for (Field f : getFields(cls)) {
/*  91 */       if (dataType.isAssignableFrom(f.getType()) && (!ignoreStatic || !Modifier.isStatic(f.getModifiers())) && 
/*  92 */         currentIndex++ == index) {
/*  93 */         return f;
/*     */       }
/*     */     } 
/*     */     
/*  97 */     if (cls.getSuperclass() != null) {
/*  98 */       return getField(cls.getSuperclass(), dataType, index);
/*     */     }
/* 100 */     return null;
/*     */   }
/*     */   
/*     */   public static Field getField(Class<?> cls, int index) {
/* 104 */     if (cls == null) {
/* 105 */       return null;
/*     */     }
/*     */     try {
/* 108 */       return getFields(cls)[index];
/* 109 */     } catch (Exception ex) {
/* 110 */       if (cls.getSuperclass() != null) {
/* 111 */         return getFields(cls.getSuperclass())[index];
/*     */       }
/*     */       
/* 114 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Method getMethod(Class<?> cls, String name, Class<?>... params) {
/* 119 */     if (cls == null) {
/* 120 */       return null;
/*     */     }
/*     */     try {
/* 123 */       Method m = cls.getDeclaredMethod(name, params);
/* 124 */       m.setAccessible(true);
/* 125 */       return m;
/* 126 */     } catch (NoSuchMethodException e) {
/*     */       try {
/* 128 */         Method m = cls.getMethod(name, params);
/* 129 */         m.setAccessible(true);
/* 130 */         return m;
/* 131 */       } catch (NoSuchMethodException e1) {
/* 132 */         if (cls.getSuperclass() != null) {
/* 133 */           return getMethod(cls.getSuperclass(), name, params);
/*     */         }
/*     */ 
/*     */         
/* 137 */         return null;
/*     */       } 
/*     */     } 
/*     */   } public static Method getMethod(Class<?> cls, int index, Class<?>... params) {
/* 141 */     if (cls == null) {
/* 142 */       return null;
/*     */     }
/* 144 */     int currentIndex = 0;
/* 145 */     for (Method m : cls.getDeclaredMethods()) {
/* 146 */       if ((params == null || Arrays.equals((Object[])m.getParameterTypes(), (Object[])params)) && index == currentIndex++) {
/* 147 */         m.setAccessible(true);
/* 148 */         return m;
/*     */       } 
/*     */     } 
/* 151 */     if (cls.getSuperclass() != null) {
/* 152 */       return getMethod(cls.getSuperclass(), index, params);
/*     */     }
/* 154 */     return null;
/*     */   }
/*     */   
/*     */   public static Method getMethod(Class<?> cls, Class<?> returning, int index, Class<?>... params) {
/* 158 */     if (cls == null) {
/* 159 */       return null;
/*     */     }
/* 161 */     int currentIndex = 0;
/* 162 */     for (Method m : cls.getDeclaredMethods()) {
/* 163 */       if (Arrays.equals((Object[])m.getParameterTypes(), (Object[])params) && (returning == null || m
/* 164 */         .getReturnType().equals(returning)) && index == currentIndex++) {
/*     */         
/* 166 */         m.setAccessible(true);
/* 167 */         return m;
/*     */       } 
/*     */     } 
/* 170 */     if (cls.getSuperclass() != null) {
/* 171 */       return getMethod(cls.getSuperclass(), null, index, params);
/*     */     }
/* 173 */     return null;
/*     */   }
/*     */   
/*     */   public static Method getMethodExact(Class<?> cls, String name, Class<?> returning, Class<?>... params) {
/* 177 */     if (cls == null) {
/* 178 */       return null;
/*     */     }
/* 180 */     for (Method m : cls.getDeclaredMethods()) {
/* 181 */       if (m.getName().equals(name) && 
/* 182 */         Arrays.equals((Object[])m.getParameterTypes(), (Object[])params) && (returning == null || m
/* 183 */         .getReturnType().equals(returning))) {
/* 184 */         m.setAccessible(true);
/* 185 */         return m;
/*     */       } 
/*     */     } 
/* 188 */     if (cls.getSuperclass() != null) {
/* 189 */       return getMethodExact(cls.getSuperclass(), name, null, params);
/*     */     }
/* 191 */     return null;
/*     */   }
/*     */   
/*     */   public static Method getMethod(Class<?> cls, String name, int index) {
/* 195 */     if (cls == null) {
/* 196 */       return null;
/*     */     }
/* 198 */     int currentIndex = 0;
/* 199 */     for (Method m : cls.getDeclaredMethods()) {
/* 200 */       if (m.getName().equals(name) && index == currentIndex++) {
/* 201 */         m.setAccessible(true);
/* 202 */         return m;
/*     */       } 
/*     */     } 
/* 205 */     if (cls.getSuperclass() != null) {
/* 206 */       return getMethod(cls.getSuperclass(), name, index);
/*     */     }
/* 208 */     return null;
/*     */   }
/*     */   
/*     */   public static Method getMethod(Class<?> cls, Class<?> returning, int index) {
/* 212 */     if (cls == null) {
/* 213 */       return null;
/*     */     }
/* 215 */     int currentIndex = 0;
/* 216 */     for (Method m : cls.getDeclaredMethods()) {
/* 217 */       if ((returning == null || m.getReturnType().equals(returning)) && index == currentIndex++) {
/* 218 */         m.setAccessible(true);
/* 219 */         return m;
/*     */       } 
/*     */     } 
/* 222 */     if (cls.getSuperclass() != null) {
/* 223 */       return getMethod(cls.getSuperclass(), returning, index);
/*     */     }
/* 225 */     return null;
/*     */   }
/*     */   
/*     */   public static Method getMethodCheckContainsString(Class<?> cls, String nameContainsThisStr, Class<?> returning) {
/* 229 */     if (cls == null) {
/* 230 */       return null;
/*     */     }
/* 232 */     for (Method m : cls.getDeclaredMethods()) {
/* 233 */       if (m.getName().contains(nameContainsThisStr) && (returning == null || m.getReturnType().equals(returning))) {
/* 234 */         m.setAccessible(true);
/* 235 */         return m;
/*     */       } 
/*     */     } 
/* 238 */     if (cls.getSuperclass() != null) {
/* 239 */       return getMethodCheckContainsString(cls.getSuperclass(), nameContainsThisStr, returning);
/*     */     }
/* 241 */     return null;
/*     */   }
/*     */   
/*     */   public static List<Method> getMethods(Class<?> cls, String name, Class<?> returning) {
/* 245 */     if (cls == null) {
/* 246 */       return null;
/*     */     }
/* 248 */     List<Method> methods = new ArrayList<>();
/* 249 */     for (Method m : cls.getDeclaredMethods()) {
/* 250 */       if (m.getName().equals(name) && (returning == null || m
/* 251 */         .getReturnType().equals(returning))) {
/* 252 */         m.setAccessible(true);
/* 253 */         methods.add(m);
/*     */       } 
/*     */     } 
/* 256 */     if (cls.getSuperclass() != null) {
/* 257 */       methods.addAll(getMethods(cls.getSuperclass(), name, returning));
/*     */     }
/* 259 */     return methods;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Class<?> getClassByNameWithoutException(String name) {
/*     */     try {
/* 266 */       return Class.forName(name);
/* 267 */     } catch (ClassNotFoundException e) {
/* 268 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static Constructor<?> getConstructor(Class<?> cls, int index) {
/* 274 */     if (cls == null) {
/* 275 */       return null;
/*     */     }
/* 277 */     Constructor<?> constructor = cls.getDeclaredConstructors()[index];
/* 278 */     constructor.setAccessible(true);
/* 279 */     return constructor;
/*     */   }
/*     */   
/*     */   public static Constructor<?> getConstructor(Class<?> cls, Class<?>... params) {
/* 283 */     if (cls == null) {
/* 284 */       return null;
/*     */     }
/*     */     try {
/* 287 */       Constructor<?> c = cls.getDeclaredConstructor(params);
/* 288 */       c.setAccessible(true);
/* 289 */       return c;
/* 290 */     } catch (NoSuchMethodException e) {
/*     */       try {
/* 292 */         Constructor<?> c = cls.getConstructor(params);
/* 293 */         c.setAccessible(true);
/* 294 */         return c;
/* 295 */       } catch (NoSuchMethodException e1) {
/* 296 */         return null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\reflection\Reflection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */