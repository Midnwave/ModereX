/*     */ package ac.grim.grimac.shaded.kyori.examination;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.function.IntFunction;
/*     */ import java.util.stream.DoubleStream;
/*     */ import java.util.stream.IntStream;
/*     */ import java.util.stream.LongStream;
/*     */ import java.util.stream.Stream;
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
/*     */ public abstract class AbstractExaminer<R>
/*     */   implements Examiner<R>
/*     */ {
/*     */   @NotNull
/*     */   public R examine(@Nullable Object value) {
/*  48 */     if (value == null)
/*  49 */       return nil(); 
/*  50 */     if (value instanceof String)
/*  51 */       return examine((String)value); 
/*  52 */     if (value instanceof Examinable)
/*  53 */       return examine((Examinable)value); 
/*  54 */     if (value instanceof Collection)
/*  55 */       return collection((Collection)value); 
/*  56 */     if (value instanceof Map)
/*  57 */       return map((Map<?, ?>)value); 
/*  58 */     if (value.getClass().isArray()) {
/*  59 */       Class<?> type = value.getClass().getComponentType();
/*  60 */       if (type.isPrimitive()) {
/*  61 */         if (type == boolean.class)
/*  62 */           return examine((boolean[])value); 
/*  63 */         if (type == byte.class)
/*  64 */           return examine((byte[])value); 
/*  65 */         if (type == char.class)
/*  66 */           return examine((char[])value); 
/*  67 */         if (type == double.class)
/*  68 */           return examine((double[])value); 
/*  69 */         if (type == float.class)
/*  70 */           return examine((float[])value); 
/*  71 */         if (type == int.class)
/*  72 */           return examine((int[])value); 
/*  73 */         if (type == long.class)
/*  74 */           return examine((long[])value); 
/*  75 */         if (type == short.class) {
/*  76 */           return examine((short[])value);
/*     */         }
/*     */       } 
/*  79 */       return array((Object[])value);
/*  80 */     }  if (value instanceof Boolean)
/*  81 */       return examine(((Boolean)value).booleanValue()); 
/*  82 */     if (value instanceof Character)
/*  83 */       return examine(((Character)value).charValue()); 
/*  84 */     if (value instanceof Number) {
/*  85 */       if (value instanceof Byte)
/*  86 */         return examine(((Byte)value).byteValue()); 
/*  87 */       if (value instanceof Double)
/*  88 */         return examine(((Double)value).doubleValue()); 
/*  89 */       if (value instanceof Float)
/*  90 */         return examine(((Float)value).floatValue()); 
/*  91 */       if (value instanceof Integer)
/*  92 */         return examine(((Integer)value).intValue()); 
/*  93 */       if (value instanceof Long)
/*  94 */         return examine(((Long)value).longValue()); 
/*  95 */       if (value instanceof Short) {
/*  96 */         return examine(((Short)value).shortValue());
/*     */       }
/*  98 */     } else if (value instanceof java.util.stream.BaseStream) {
/*  99 */       if (value instanceof Stream)
/* 100 */         return stream((Stream)value); 
/* 101 */       if (value instanceof DoubleStream)
/* 102 */         return stream((DoubleStream)value); 
/* 103 */       if (value instanceof IntStream)
/* 104 */         return stream((IntStream)value); 
/* 105 */       if (value instanceof LongStream) {
/* 106 */         return stream((LongStream)value);
/*     */       }
/*     */     } 
/* 109 */     return scalar(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private <E> R array(E[] array) {
/* 120 */     return array(array, Arrays.<E>stream(array).map(this::examine));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private <E> R collection(@NotNull Collection<E> collection) {
/* 141 */     return collection(collection, collection.stream().map(this::examine));
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
/*     */   @NotNull
/*     */   public R examine(@NotNull String name, @NotNull Stream<? extends ExaminableProperty> properties) {
/* 156 */     return examinable(name, properties.map(property -> new AbstractMap.SimpleImmutableEntry<>(property.name(), property.examine(this))));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private <K, V> R map(@NotNull Map<K, V> map) {
/* 177 */     return map(map, map.entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(examine(entry.getKey()), examine(entry.getValue()))));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public R examine(boolean[] values) {
/* 241 */     if (values == null) return nil(); 
/* 242 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(byte[] values) {
/* 247 */     if (values == null) return nil(); 
/* 248 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(char[] values) {
/* 253 */     if (values == null) return nil(); 
/* 254 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(double[] values) {
/* 259 */     if (values == null) return nil(); 
/* 260 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(float[] values) {
/* 265 */     if (values == null) return nil(); 
/* 266 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(int[] values) {
/* 271 */     if (values == null) return nil(); 
/* 272 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(long[] values) {
/* 277 */     if (values == null) return nil(); 
/* 278 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public R examine(short[] values) {
/* 283 */     if (values == null) return nil(); 
/* 284 */     return array(values.length, index -> examine(values[index]));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected abstract <E> R array(E[] paramArrayOfE, @NotNull Stream<R> paramStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract <E> R collection(@NotNull Collection<E> paramCollection, @NotNull Stream<R> paramStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract R examinable(@NotNull String paramString, @NotNull Stream<Map.Entry<String, R>> paramStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract <K, V> R map(@NotNull Map<K, V> paramMap, @NotNull Stream<Map.Entry<R, R>> paramStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract R nil();
/*     */   
/*     */   @NotNull
/*     */   protected abstract R scalar(@NotNull Object paramObject);
/*     */   
/*     */   @NotNull
/*     */   protected abstract <T> R stream(@NotNull Stream<T> paramStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract R stream(@NotNull DoubleStream paramDoubleStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract R stream(@NotNull IntStream paramIntStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract R stream(@NotNull LongStream paramLongStream);
/*     */   
/*     */   @NotNull
/*     */   protected abstract R array(int paramInt, IntFunction<R> paramIntFunction);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\examination\AbstractExaminer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */