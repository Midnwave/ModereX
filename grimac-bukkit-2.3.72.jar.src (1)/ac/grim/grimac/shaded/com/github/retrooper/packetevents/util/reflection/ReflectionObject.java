/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.reflection;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class ReflectionObject
/*     */   implements ReflectionObjectReader, ReflectionObjectWriter
/*     */ {
/*  29 */   private static final Map<Class<?>, Map<Class<?>, Field[]>> FIELD_CACHE = new ConcurrentHashMap<>();
/*  30 */   private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
/*     */   protected final Object object;
/*     */   private final Class<?> clazz;
/*     */   
/*     */   public ReflectionObject() {
/*  35 */     this.object = null;
/*  36 */     this.clazz = null;
/*     */   }
/*     */   
/*     */   public ReflectionObject(Object object) {
/*  40 */     this.object = object;
/*  41 */     this.clazz = object.getClass();
/*     */   }
/*     */   
/*     */   public ReflectionObject(Object object, Class<?> clazz) {
/*  45 */     this.object = object;
/*  46 */     this.clazz = clazz;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean readBoolean(int index) {
/*  51 */     return ((Boolean)read(index, (Class)boolean.class)).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readByte(int index) {
/*  56 */     return ((Byte)read(index, (Class)byte.class)).byteValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShort(int index) {
/*  61 */     return ((Short)read(index, (Class)short.class)).shortValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInt(int index) {
/*  66 */     return ((Integer)read(index, (Class)int.class)).intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLong(int index) {
/*  71 */     return ((Long)read(index, (Class)long.class)).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public float readFloat(int index) {
/*  76 */     return ((Float)read(index, (Class)float.class)).floatValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public double readDouble(int index) {
/*  81 */     return ((Double)read(index, (Class)double.class)).doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] readBooleanArray(int index) {
/*  86 */     return read(index, (Class)boolean[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] readByteArray(int index) {
/*  91 */     return read(index, (Class)byte[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public short[] readShortArray(int index) {
/*  96 */     return read(index, (Class)short[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] readIntArray(int index) {
/* 101 */     return read(index, (Class)int[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public long[] readLongArray(int index) {
/* 106 */     return read(index, (Class)long[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public float[] readFloatArray(int index) {
/* 111 */     return read(index, (Class)float[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public double[] readDoubleArray(int index) {
/* 116 */     return read(index, (Class)double[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] readStringArray(int index) {
/* 121 */     return read(index, (Class)String[].class);
/*     */   }
/*     */ 
/*     */   
/*     */   public String readString(int index) {
/* 126 */     return read(index, String.class);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object readAnyObject(int index) {
/*     */     try {
/* 132 */       Field f = this.clazz.getDeclaredFields()[index];
/* 133 */       if (!f.isAccessible()) {
/* 134 */         f.setAccessible(true);
/*     */       }
/*     */       try {
/* 137 */         return f.get(this.object);
/* 138 */       } catch (IllegalAccessException|NullPointerException|ArrayIndexOutOfBoundsException e) {
/* 139 */         e.printStackTrace();
/*     */       } 
/* 141 */     } catch (ArrayIndexOutOfBoundsException e) {
/* 142 */       throw new IllegalStateException("PacketEvents failed to find any field indexed " + index + " in the " + this.clazz.getSimpleName() + " class!");
/*     */     } 
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T readObject(int index, Class<? extends T> type) {
/* 149 */     return read(index, type);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T[] readObjectArray(int index, Class<? extends T> type) {
/* 154 */     return (T[])read(0, Array.newInstance(type, 0).getClass());
/*     */   }
/*     */ 
/*     */   
/*     */   public Enum<?> readEnumConstant(int index, Class<? extends Enum<?>> type) {
/* 159 */     return read(index, (Class)type);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T read(int index, Class<? extends T> type) {
/*     */     try {
/* 165 */       Field field = getField(type, index);
/* 166 */       return (T)field.get(this.object);
/* 167 */     } catch (IllegalAccessException|NullPointerException|ArrayIndexOutOfBoundsException e) {
/* 168 */       throw new IllegalStateException("PacketEvents failed to find a " + type.getSimpleName() + " indexed " + index + " by its type in the " + this.clazz.getName() + " class!");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeBoolean(int index, boolean value) {
/* 175 */     write(boolean.class, index, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeByte(int index, byte value) {
/* 180 */     write(byte.class, index, Byte.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShort(int index, short value) {
/* 185 */     write(short.class, index, Short.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt(int index, int value) {
/* 190 */     write(int.class, index, Integer.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLong(int index, long value) {
/* 195 */     write(long.class, index, Long.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFloat(int index, float value) {
/* 200 */     write(float.class, index, Float.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDouble(int index, double value) {
/* 205 */     write(double.class, index, Double.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeString(int index, String value) {
/* 210 */     write(String.class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeObject(int index, Object value) {
/* 215 */     write(value.getClass(), index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBooleanArray(int index, boolean[] array) {
/* 220 */     write(boolean[].class, index, array);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeByteArray(int index, byte[] value) {
/* 225 */     write(byte[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShortArray(int index, short[] value) {
/* 230 */     write(short[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeIntArray(int index, int[] value) {
/* 235 */     write(int[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLongArray(int index, long[] value) {
/* 240 */     write(long[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFloatArray(int index, float[] value) {
/* 245 */     write(float[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDoubleArray(int index, double[] value) {
/* 250 */     write(double[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeStringArray(int index, String[] value) {
/* 255 */     write(String[].class, index, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeAnyObject(int index, Object value) {
/*     */     try {
/* 261 */       Field f = this.clazz.getDeclaredFields()[index];
/* 262 */       f.set(this.object, value);
/* 263 */     } catch (Exception e) {
/* 264 */       throw new IllegalStateException("PacketEvents failed to find any field indexed " + index + " in the " + this.clazz.getSimpleName() + " class!");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeEnumConstant(int index, Enum<?> enumConstant) {
/*     */     try {
/* 271 */       write(enumConstant.getClass(), index, enumConstant);
/* 272 */     } catch (IllegalStateException ex) {
/* 273 */       write(enumConstant.getDeclaringClass(), index, enumConstant);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void write(Class<?> type, int index, Object value) throws IllegalStateException {
/* 278 */     Field field = getField(type, index);
/* 279 */     if (field == null) {
/* 280 */       throw new IllegalStateException("PacketEvents failed to find a " + type.getSimpleName() + " indexed " + index + " by its type in the " + this.clazz.getName() + " class!");
/*     */     }
/*     */     try {
/* 283 */       field.set(this.object, value);
/* 284 */     } catch (IllegalAccessException|NullPointerException e) {
/* 285 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T> List<T> readList(int index) {
/* 290 */     return read(index, (Class)List.class);
/*     */   }
/*     */   
/*     */   public void writeList(int index, List<?> list) {
/* 294 */     write(List.class, index, list);
/*     */   }
/*     */   
/*     */   private Field getField(Class<?> type, int index) {
/* 298 */     Map<Class<?>, Field[]> cached = FIELD_CACHE.computeIfAbsent(this.clazz, k -> new ConcurrentHashMap<>());
/* 299 */     Field[] fields = cached.computeIfAbsent(type, typeClass -> getFields(typeClass, this.clazz.getDeclaredFields()));
/* 300 */     if (fields.length >= index + 1) {
/* 301 */       return fields[index];
/*     */     }
/* 303 */     throw new IllegalStateException("PacketEvents failed to find a " + type.getSimpleName() + " indexed " + index + " by its type in the " + this.clazz.getName() + " class!");
/*     */   }
/*     */ 
/*     */   
/*     */   private Field[] getFields(Class<?> type, Field[] fields) {
/* 308 */     List<Field> ret = new ArrayList<>();
/* 309 */     for (Field field : fields) {
/* 310 */       if (field.getType().equals(type)) {
/* 311 */         if (!field.isAccessible()) {
/* 312 */           field.setAccessible(true);
/*     */         }
/* 314 */         ret.add(field);
/*     */       } 
/*     */     } 
/* 317 */     return ret.<Field>toArray(EMPTY_FIELD_ARRAY);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevent\\util\reflection\ReflectionObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */