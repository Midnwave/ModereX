/*     */ package ac.grim.grimac.shaded.kyori.adventure.text.event;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.NonExtendable;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.key.Key;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Services;
/*     */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DataComponentValueConverterRegistry
/*     */ {
/*  55 */   private static final Set<Provider> PROVIDERS = Services.services(Provider.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<Key> knownProviders() {
/*  67 */     return Collections.unmodifiableSet((Set<? extends Key>)PROVIDERS.stream()
/*  68 */         .map(Provider::id)
/*  69 */         .collect(Collectors.toSet()));
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
/*     */   public static <O extends DataComponentValue> O convert(@NotNull Class<O> target, @NotNull Key key, @NotNull DataComponentValue in) {
/*  84 */     if (target.isInstance(in)) {
/*  85 */       return target.cast(in);
/*     */     }
/*     */     
/*  88 */     RegisteredConversion converter = ConversionCache.converter((Class)in.getClass(), target);
/*  89 */     if (converter == null) {
/*  90 */       throw new IllegalArgumentException("There is no data holder converter registered to convert from a " + in.getClass() + " instance to a " + target + " (on field " + key + ")");
/*     */     }
/*     */     
/*     */     try {
/*  94 */       return (O)converter.conversion.convert(key, in);
/*  95 */     } catch (Exception ex) {
/*  96 */       throw new IllegalStateException("Failed to convert data component value of type " + in.getClass() + " to type " + target + " due to an error in a converter provided by " + converter.provider.asString() + "!", ex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NonExtendable
/*     */   public static interface Conversion<I, O>
/*     */     extends Examinable
/*     */   {
/*     */     @NotNull
/*     */     static <I1, O1> Conversion<I1, O1> convert(@NotNull Class<I1> src, @NotNull Class<O1> dst, @NotNull BiFunction<Key, I1, O1> op) {
/* 146 */       return new DataComponentValueConversionImpl<>(
/* 147 */           Objects.<Class<I1>>requireNonNull(src, "src"), 
/* 148 */           Objects.<Class<O1>>requireNonNull(dst, "dst"), 
/* 149 */           Objects.<BiFunction<Key, I1, O1>>requireNonNull(op, "op"));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract(pure = true)
/*     */     @NotNull
/*     */     Class<I> source();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Contract(pure = true)
/*     */     @NotNull
/*     */     Class<O> destination();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     O convert(@NotNull Key param1Key, @NotNull I param1I);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ConversionCache
/*     */   {
/* 184 */     private static final ConcurrentMap<Class<?>, ConcurrentMap<Class<?>, DataComponentValueConverterRegistry.RegisteredConversion>> CACHE = new ConcurrentHashMap<>();
/* 185 */     private static final Map<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> CONVERSIONS = collectConversions();
/*     */     
/*     */     private static Map<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> collectConversions() {
/* 188 */       Map<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> collected = new ConcurrentHashMap<>();
/* 189 */       for (DataComponentValueConverterRegistry.Provider provider : DataComponentValueConverterRegistry.PROVIDERS) {
/* 190 */         Key id = Objects.<Key>requireNonNull(provider.id(), () -> "ID of provider " + provider + " is null");
/* 191 */         for (DataComponentValueConverterRegistry.Conversion<?, ?> conv : provider.conversions()) {
/* 192 */           ((Set<DataComponentValueConverterRegistry.RegisteredConversion>)collected.computeIfAbsent(conv.source(), $ -> ConcurrentHashMap.newKeySet())).add(new DataComponentValueConverterRegistry.RegisteredConversion(id, conv));
/*     */         }
/*     */       } 
/*     */       
/* 196 */       for (Map.Entry<Class<?>, Set<DataComponentValueConverterRegistry.RegisteredConversion>> entry : collected.entrySet()) {
/* 197 */         entry.setValue(Collections.unmodifiableSet(entry.getValue()));
/*     */       }
/*     */       
/* 200 */       return new ConcurrentHashMap<>(collected);
/*     */     }
/*     */     
/*     */     static DataComponentValueConverterRegistry.RegisteredConversion compute(Class<?> src, Class<?> dst) {
/* 204 */       Deque<Class<?>> sourceTypes = new ArrayDeque<>();
/* 205 */       sourceTypes.add(src);
/*     */       Class<?> sourcePtr;
/* 207 */       while ((sourcePtr = sourceTypes.poll()) != null) {
/* 208 */         Set<DataComponentValueConverterRegistry.RegisteredConversion> conversions = CONVERSIONS.get(sourcePtr);
/* 209 */         if (conversions != null) {
/*     */           
/* 211 */           DataComponentValueConverterRegistry.RegisteredConversion nearest = null;
/* 212 */           for (DataComponentValueConverterRegistry.RegisteredConversion potential : conversions) {
/* 213 */             Class<?> potentialDst = potential.conversion.destination();
/*     */             
/* 215 */             if (dst.equals(potentialDst)) return potential; 
/* 216 */             if (!dst.isAssignableFrom(potentialDst)) {
/*     */               continue;
/*     */             }
/* 219 */             if (nearest == null || potentialDst.isAssignableFrom(nearest.conversion.destination())) {
/* 220 */               nearest = potential;
/*     */             }
/*     */           } 
/*     */           
/* 224 */           if (nearest != null) return nearest;
/*     */         
/*     */         } 
/* 227 */         addSupertypes(sourcePtr, sourceTypes);
/*     */       } 
/*     */       
/* 230 */       return DataComponentValueConverterRegistry.RegisteredConversion.NONE;
/*     */     }
/*     */     
/*     */     private static void addSupertypes(Class<?> clazz, Deque<Class<?>> queue) {
/* 234 */       if (clazz.getSuperclass() != null) {
/* 235 */         queue.add(clazz.getSuperclass());
/*     */       }
/*     */       
/* 238 */       queue.addAll(Arrays.asList(clazz.getInterfaces()));
/*     */     }
/*     */     @Nullable
/*     */     static DataComponentValueConverterRegistry.RegisteredConversion converter(Class<? extends DataComponentValue> src, Class<? extends DataComponentValue> dst) {
/* 242 */       DataComponentValueConverterRegistry.RegisteredConversion result = ((ConcurrentMap<Class<? extends DataComponentValue>, DataComponentValueConverterRegistry.RegisteredConversion>)CACHE.computeIfAbsent(src, $ -> new ConcurrentHashMap<>())).computeIfAbsent(dst, $$ -> compute(src, dst));
/* 243 */       if (result == DataComponentValueConverterRegistry.RegisteredConversion.NONE) return null;
/*     */       
/* 245 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   static final class RegisteredConversion {
/* 250 */     static final RegisteredConversion NONE = new RegisteredConversion(null, null);
/*     */     
/*     */     final Key provider;
/*     */     final DataComponentValueConverterRegistry.Conversion<?, ?> conversion;
/*     */     
/*     */     RegisteredConversion(Key provider, DataComponentValueConverterRegistry.Conversion<?, ?> conversion) {
/* 256 */       this.provider = provider;
/* 257 */       this.conversion = conversion;
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface Provider {
/*     */     @NotNull
/*     */     Key id();
/*     */     
/*     */     @NotNull
/*     */     Iterable<DataComponentValueConverterRegistry.Conversion<?, ?>> conversions();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\event\DataComponentValueConverterRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */