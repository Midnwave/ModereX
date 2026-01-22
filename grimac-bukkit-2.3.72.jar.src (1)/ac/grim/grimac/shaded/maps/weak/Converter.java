/*     */ package ac.grim.grimac.shaded.maps.weak;
/*     */ 
/*     */ import ac.grim.grimac.shaded.maps.Fluent;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.StreamSupport;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Converter
/*     */ {
/*     */   private static final String DEFAULT_MAP_KEY = "value";
/*     */   private static final Map<Class<?>, Function<Object, ? extends Converter>> typeConverters;
/*     */   protected final Object o;
/*     */   
/*     */   static {
/*  54 */     typeConverters = Collections.unmodifiableMap((Map<? extends Class<?>, ? extends Function<Object, ? extends Converter>>)(new Fluent.LinkedHashMap())
/*     */         
/*  56 */         .append(Integer.class, IntConverter::new)
/*  57 */         .append(Long.class, LongConverter::new)
/*  58 */         .append(Double.class, DoubleConverter::new)
/*  59 */         .append(BigDecimal.class, DecimalConverter::new)
/*  60 */         .append(Weak.class, o -> convert(((Weak)o).asObject()))
/*  61 */         .append(OptionalWeak.class, o -> new OptionalConverter(((OptionalWeak)o).asObject()))
/*  62 */         .append(Map.class, MapConverter::new)
/*  63 */         .append(Iterable.class, IterableConverter::new)
/*  64 */         .append(Optional.class, OptionalConverter::new)
/*  65 */         .append(Date.class, UtilDateInstantConverter::new)
/*     */         
/*  67 */         .append(Object.class, Converter::new));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Converter convert(Object value) {
/*  75 */     Objects.requireNonNull(value);
/*     */     
/*  77 */     if (value instanceof Object[]) {
/*  78 */       return convert(Arrays.asList((Object[])value));
/*     */     }
/*  80 */     return ((Function<Object, Converter>)typeConverters.getOrDefault(value.getClass(), typeConverters.entrySet().stream()
/*  81 */         .filter(entry -> (!((Class)entry.getKey()).equals(Date.class) && ((Class)entry.getKey()).isInstance(value)))
/*  82 */         .findFirst()
/*  83 */         .map(Map.Entry::getValue).get())).apply(value);
/*     */   }
/*     */   
/*     */   private static boolean doesNotThrow(Supplier<?> method) {
/*     */     try {
/*  88 */       method.get();
/*  89 */       return true;
/*     */     } catch (RuntimeException ex) {
/*  91 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Converter(Object o) {
/*  98 */     this.o = o;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String intoString() {
/* 106 */     return (this.o instanceof String) ? (String)this.o : this.o.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoStringWorks() {
/* 111 */     return doesNotThrow(this::intoString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int intoInteger() {
/* 119 */     return intoDecimal().setScale(0, RoundingMode.HALF_UP).intValueExact();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoIntegerWorks() {
/* 124 */     return doesNotThrow(this::intoInteger);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long intoLong() {
/* 132 */     return intoDecimal().setScale(0, RoundingMode.HALF_UP).longValueExact();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoLongWorks() {
/* 137 */     return doesNotThrow(this::intoLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double intoDouble() {
/* 145 */     return intoDecimal().doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoDoubleWorks() {
/* 150 */     return doesNotThrow(this::intoDouble);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigDecimal intoDecimal() {
/* 158 */     return new BigDecimal(intoString());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoDecimalWorks() {
/* 163 */     return doesNotThrow(this::intoDecimal);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map intoMap() {
/* 174 */     return (Map)(new Fluent.HashMap()).append("value", this.o);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoMapWorks() {
/* 179 */     return doesNotThrow(this::intoMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List intoList() {
/* 190 */     List<Object> list = new ArrayList();
/* 191 */     list.add(this.o);
/* 192 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoListWorks() {
/* 197 */     return doesNotThrow(this::intoList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LocalDateTime intoLocalDateTime() {
/* 206 */     return LocalDateTime.from(ConverterTimeFormats.parseWithDefaults(intoString()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoLocalDateTimeWorks() {
/* 211 */     return doesNotThrow(this::intoLocalDateTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZonedDateTime intoZonedDateTime() {
/* 220 */     return ZonedDateTime.from(ConverterTimeFormats.parseWithDefaults(intoString()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intoZonedDateTimeWorks() {
/* 225 */     return doesNotThrow(this::intoZonedDateTime);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZonedDateTime intoZonedDateTimeOrUse(ZoneId fallback) {
/*     */     
/* 235 */     try { return intoZonedDateTime(); }
/* 236 */     catch (RuntimeException ex) { return intoLocalDateTime().atZone(fallback); }
/*     */   
/*     */   }
/*     */   
/*     */   public ConverterMaybe maybe() {
/* 241 */     return new ConverterMaybe(this.o);
/*     */   }
/*     */   
/*     */   static abstract class TypeConverter<T>
/*     */     extends Converter {
/*     */     TypeConverter(Object o) {
/* 247 */       super(o);
/*     */     }
/*     */     
/*     */     protected T literal() {
/* 251 */       return (T)this.o;
/*     */     }
/*     */   }
/*     */   
/*     */   static class IntConverter
/*     */     extends TypeConverter<Integer> {
/*     */     IntConverter(Object o) {
/* 258 */       super(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public int intoInteger() {
/* 263 */       return literal().intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public long intoLong() {
/* 268 */       return literal().intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 273 */       return literal().intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 278 */       return new BigDecimal(literal().intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class LongConverter
/*     */     extends TypeConverter<Long> {
/*     */     LongConverter(Object o) {
/* 285 */       super(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public int intoInteger() {
/* 290 */       if (literal().longValue() < -2147483648L || literal().longValue() > 2147483647L)
/* 291 */         throw new IllegalArgumentException(literal() + " too large/small to be cast to int"); 
/* 292 */       return literal().intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public long intoLong() {
/* 297 */       return literal().longValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 302 */       return literal().longValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 307 */       return new BigDecimal(literal().longValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class DoubleConverter
/*     */     extends TypeConverter<Double> {
/*     */     DoubleConverter(Object o) {
/* 314 */       super(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 319 */       return literal().doubleValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 324 */       return new BigDecimal(literal().doubleValue());
/*     */     }
/*     */   }
/*     */   
/*     */   static class DecimalConverter
/*     */     extends TypeConverter<BigDecimal> {
/*     */     DecimalConverter(Object o) {
/* 331 */       super(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 336 */       return literal();
/*     */     }
/*     */   }
/*     */   
/*     */   static class MapConverter
/*     */     extends TypeConverter<Map<?, ?>> {
/*     */     MapConverter(Object o) {
/* 343 */       super(o);
/*     */     }
/*     */     
/*     */     private Optional<Object> value() {
/* 347 */       return Optional.ofNullable(literal().get("value"));
/*     */     }
/*     */ 
/*     */     
/*     */     public String intoString() {
/* 352 */       return value().<String>map(o -> convert(o).intoString()).orElseGet(() -> super.intoString());
/*     */     }
/*     */ 
/*     */     
/*     */     public int intoInteger() {
/* 357 */       return ((Integer)value().<Integer>map(o -> Integer.valueOf(convert(o).intoInteger())).orElseGet(() -> Integer.valueOf(super.intoInteger()))).intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public long intoLong() {
/* 362 */       return ((Long)value().<Long>map(o -> Long.valueOf(convert(o).intoLong())).orElseGet(() -> Long.valueOf(super.intoLong()))).longValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 367 */       return ((Double)value().<Double>map(o -> Double.valueOf(convert(o).intoDouble())).orElseGet(() -> Double.valueOf(super.intoDouble()))).doubleValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 372 */       return value().<BigDecimal>map(o -> convert(o).intoDecimal()).orElseGet(() -> super.intoDecimal());
/*     */     }
/*     */ 
/*     */     
/*     */     public Map intoMap() {
/* 377 */       return new LinkedHashMap<>(literal());
/*     */     }
/*     */ 
/*     */     
/*     */     public List intoList() {
/* 382 */       return new ArrayList(literal().values());
/*     */     }
/*     */   }
/*     */   
/*     */   static class IterableConverter
/*     */     extends TypeConverter<Iterable<?>> {
/*     */     IterableConverter(Object o) {
/* 389 */       super(o);
/*     */     }
/*     */     
/*     */     private Optional<Object> onlyElement() {
/* 393 */       Iterator<?> iterator = literal().iterator();
/* 394 */       return Optional.<Object>ofNullable(iterator.hasNext() ? iterator.next() : null)
/* 395 */         .filter(o -> !iterator.hasNext());
/*     */     }
/*     */ 
/*     */     
/*     */     public String intoString() {
/* 400 */       return onlyElement().<String>map(o -> convert(o).intoString()).orElseGet(() -> super.intoString());
/*     */     }
/*     */ 
/*     */     
/*     */     public int intoInteger() {
/* 405 */       return ((Integer)onlyElement().<Integer>map(o -> Integer.valueOf(convert(o).intoInteger())).orElseGet(() -> Integer.valueOf(super.intoInteger()))).intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public long intoLong() {
/* 410 */       return ((Long)onlyElement().<Long>map(o -> Long.valueOf(convert(o).intoLong())).orElseGet(() -> Long.valueOf(super.intoLong()))).longValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 415 */       return ((Double)onlyElement().<Double>map(o -> Double.valueOf(convert(o).intoDouble())).orElseGet(() -> Double.valueOf(super.intoDouble()))).doubleValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 420 */       return onlyElement().<BigDecimal>map(o -> convert(o).intoDecimal()).orElseGet(() -> super.intoDecimal());
/*     */     }
/*     */ 
/*     */     
/*     */     public Map intoMap() {
/* 425 */       Map<Integer, Object> map = new LinkedHashMap<>();
/* 426 */       Iterator<?> iterator = literal().iterator();
/* 427 */       for (int i = 0; iterator.hasNext(); i++)
/* 428 */         map.put(Integer.valueOf(i), iterator.next()); 
/* 429 */       return map;
/*     */     }
/*     */ 
/*     */     
/*     */     public List intoList() {
/* 434 */       return (List)StreamSupport.stream(literal().spliterator(), false).collect(Collectors.toCollection(ArrayList::new));
/*     */     }
/*     */   }
/*     */   
/*     */   static class OptionalConverter
/*     */     extends TypeConverter<Optional<?>> {
/*     */     OptionalConverter(Object o) {
/* 441 */       super(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public String intoString() {
/* 446 */       return literal().<String>map(o -> convert(o).intoString()).orElseGet(() -> super.intoString());
/*     */     }
/*     */ 
/*     */     
/*     */     public int intoInteger() {
/* 451 */       return ((Integer)literal().<Integer>map(o -> Integer.valueOf(convert(o).intoInteger())).orElseGet(() -> Integer.valueOf(super.intoInteger()))).intValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public long intoLong() {
/* 456 */       return ((Long)literal().<Long>map(o -> Long.valueOf(convert(o).intoLong())).orElseGet(() -> Long.valueOf(super.intoLong()))).longValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 461 */       return ((Double)literal().<Double>map(o -> Double.valueOf(convert(o).intoDouble())).orElseGet(() -> Double.valueOf(super.intoDouble()))).doubleValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 466 */       return literal().<BigDecimal>map(o -> convert(o).intoDecimal()).orElseGet(() -> super.intoDecimal());
/*     */     }
/*     */ 
/*     */     
/*     */     public Map intoMap() {
/* 471 */       return literal().<Map>map(o -> convert(o).intoMap()).orElseGet(LinkedHashMap::new);
/*     */     }
/*     */ 
/*     */     
/*     */     public List intoList() {
/* 476 */       return literal().<List>map(o -> convert(o).intoList()).orElseGet(ArrayList::new);
/*     */     }
/*     */   }
/*     */   
/*     */   static class UtilDateInstantConverter
/*     */     extends TypeConverter<Date> {
/*     */     UtilDateInstantConverter(Object o) {
/* 483 */       super(o);
/*     */     }
/*     */ 
/*     */     
/*     */     public BigDecimal intoDecimal() {
/* 488 */       return new BigDecimal(intoLong());
/*     */     }
/*     */ 
/*     */     
/*     */     public double intoDouble() {
/* 493 */       return intoLong();
/*     */     }
/*     */ 
/*     */     
/*     */     public long intoLong() {
/* 498 */       return literal().getTime();
/*     */     }
/*     */ 
/*     */     
/*     */     public String intoString() {
/* 503 */       return String.valueOf(literal().getTime());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\maps\weak\Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */