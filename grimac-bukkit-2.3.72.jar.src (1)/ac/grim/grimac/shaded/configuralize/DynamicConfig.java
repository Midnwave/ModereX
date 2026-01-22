/*     */ package ac.grim.grimac.shaded.configuralize;
/*     */ import ac.grim.grimac.shaded.configuralize.mapping.MappingFunction;
/*     */ import ac.grim.grimac.shaded.configuralize.mapping.Option;
/*     */ import ac.grim.grimac.shaded.json.simple.parser.JSONParser;
/*     */ import ac.grim.grimac.shaded.maps.weak.Dynamic;
/*     */ import ac.grim.grimac.shaded.maps.weak.Weak;
/*     */ import ac.grim.grimac.shaded.snakeyaml.Yaml;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ public class DynamicConfig {
/*  21 */   private final Map<Source, Provider> sources = new LinkedHashMap<>();
/*  22 */   private final Map<String, Object> runtimeValues = new HashMap<>(); private Language language; private JSONParser jsonParser;
/*     */   private Yaml yamlParser;
/*     */   
/*     */   public DynamicConfig() {
/*  26 */     this(Language.EN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLanguageAvailable() {
/*  36 */     return isLanguageAvailable(this.language);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLanguageAvailable(Language language) {
/*  44 */     return this.sources.keySet().stream().allMatch(source -> source.isLanguageAvailable(language));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addSource(Source source) {
/*  53 */     return (this.sources.put(source, new Provider(this, source)) == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addSource(Class<?> clazz, String resource, File file) {
/*  64 */     Source source = new Source(this, clazz, resource, file); return 
/*  65 */       (this.sources.put(source, new Provider(this, source)) == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeSource(Source source) {
/*  74 */     return (this.sources.remove(source) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void saveAllDefaults() throws IOException {
/*  82 */     saveAllDefaults(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void saveAllDefaults(boolean overwrite) throws IOException {
/*  91 */     for (Map.Entry<Source, Provider> source : this.sources.entrySet()) {
/*  92 */       ((Provider)source.getValue()).saveDefaults(overwrite);
/*     */     }
/*     */   }
/*     */   
/*     */   public void loadAll() throws IOException, ParseException {
/*  97 */     for (Map.Entry<Source, Provider> source : this.sources.entrySet()) {
/*  98 */       ((Provider)source.getValue()).load();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void map(MappingFunction<?>... mappingFunctions) {
/* 107 */     map(getClass(), mappingFunctions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void map(List<MappingFunction<?>> mappingFunctions) {
/* 114 */     map(getClass(), mappingFunctions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void map(Class<?> targetClass, MappingFunction<?>... mappings) {
/* 123 */     map(targetClass, Arrays.asList(mappings));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void map(Class<?> targetClass, List<MappingFunction<?>> mappings) {
/* 131 */     mapFields(targetClass, mappings);
/* 132 */     for (Class<?> declared : targetClass.getDeclaredClasses()) {
/* 133 */       map(declared, mappings);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void mapFields(Class<?> clazz, List<MappingFunction<?>> mappings) {
/* 143 */     for (Field field : clazz.getDeclaredFields()) {
/* 144 */       if (Modifier.isStatic(field.getModifiers()))
/*     */       {
/* 146 */         if (field.isAnnotationPresent((Class)Option.class)) {
/* 147 */           Option fieldAnnotation = field.<Option>getAnnotation(Option.class);
/* 148 */           String key = fieldAnnotation.key();
/*     */           
/* 150 */           if (!field.isAccessible()) {
/* 151 */             field.setAccessible(true);
/*     */           }
/*     */           
/* 154 */           boolean valueExists = Modifier.isFinal(field.getModifiers());
/* 155 */           if (valueExists) {
/*     */             try {
/* 157 */               Field modifiersField = Field.class.getDeclaredField("modifiers");
/* 158 */               modifiersField.setAccessible(true);
/* 159 */               modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
/* 160 */             } catch (IllegalAccessException|NoSuchFieldException e) {
/* 161 */               throw new RuntimeException("Failed to reflectively set field " + field + " to non-final", e);
/*     */             } 
/*     */           }
/*     */           
/*     */           try {
/*     */             Object value;
/* 167 */             Dynamic dynamic = dgetSilent(key);
/*     */ 
/*     */             
/* 170 */             MappingFunction<?> mappingFunction = (mappings != null) ? mappings.stream().filter(mf -> mf.getKey().equals(key)).findFirst().orElse(null) : null;
/*     */             
/* 172 */             if (mappingFunction != null) {
/* 173 */               value = mappingFunction.getFunction().apply(dynamic);
/*     */             } else {
/* 175 */               switch (field.getType().getName().toLowerCase()) {
/*     */                 case "int":
/*     */                 case "integer":
/* 178 */                   value = Integer.valueOf(dynamic.convert().intoInteger());
/*     */                   break;
/*     */                 case "double":
/* 181 */                   value = Double.valueOf(dynamic.convert().intoDouble());
/*     */                   break;
/*     */                 default:
/* 184 */                   value = dynamic.asObject();
/*     */                   break;
/*     */               } 
/*     */             } 
/* 188 */             field.set((Object)null, value);
/* 189 */           } catch (IllegalAccessException e) {
/* 190 */             throw new RuntimeException("Field " + field + " is not accessible");
/* 191 */           } catch (Throwable e) {
/* 192 */             throw new RuntimeException("Failed to map key " + key, e);
/*     */           } 
/*     */         }  } 
/*     */     } 
/*     */   }
/*     */   public Dynamic dget(String key) throws IllegalArgumentException {
/* 198 */     if (this.runtimeValues.containsKey(key)) return Dynamic.from(this.runtimeValues.get(key)); 
/* 199 */     return this.sources.values().stream()
/* 200 */       .filter(Objects::nonNull)
/* 201 */       .filter(provider -> (provider.getValues() != null))
/* 202 */       .map(provider -> provider.getValues().dget(key))
/* 203 */       .filter(Objects::nonNull)
/* 204 */       .filter(Weak::isPresent)
/* 205 */       .findFirst().orElseGet(() -> (Dynamic)this.sources.values().stream().map(()).filter(Weak::isPresent).findFirst().orElseThrow(()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Dynamic dgetSilent(String key) {
/*     */     try {
/* 212 */       return dget(key);
/* 213 */     } catch (IllegalArgumentException e) {
/* 214 */       return Dynamic.from(null);
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T> T get(String key) throws RuntimeException {
/* 219 */     return (T)dget(key).asObject();
/*     */   }
/*     */   public <T> Optional<T> getOptional(String key) {
/*     */     try {
/* 223 */       return Optional.ofNullable(get(key));
/* 224 */     } catch (Exception e) {
/* 225 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public <T> T getElse(String key, T otherwise) {
/*     */     try {
/* 230 */       return get(key);
/* 231 */     } catch (Exception e) {
/* 232 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public <K, V> Map<K, V> getMap(String key) throws RuntimeException {
/* 237 */     return dget(key).convert().intoMap();
/*     */   }
/*     */   public <K, V> Optional<Map<K, V>> getOptionalMap(String key) {
/*     */     try {
/* 241 */       return Optional.ofNullable(getMap(key));
/* 242 */     } catch (Exception e) {
/* 243 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public <K, V> Map<K, V> getMapElse(String key, Map<K, V> otherwise) {
/*     */     try {
/* 248 */       return getMap(key);
/* 249 */     } catch (Exception e) {
/* 250 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T> List<T> getList(String key) throws RuntimeException {
/* 255 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public <T> Optional<List<T>> getOptionalList(String key) {
/*     */     try {
/* 259 */       return Optional.ofNullable(
/* 260 */           getList(key));
/*     */     }
/* 262 */     catch (Exception e) {
/* 263 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public <T> List<T> getListElse(String key, List<T> otherwise) {
/*     */     try {
/* 268 */       return getList(key);
/* 269 */     } catch (Exception e) {
/* 270 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getString(String key) throws RuntimeException {
/* 275 */     return dget(key).convert().intoString();
/*     */   }
/*     */   public Optional<String> getOptionalString(String key) {
/*     */     try {
/* 279 */       return Optional.ofNullable(getString(key));
/* 280 */     } catch (Exception e) {
/* 281 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public String getStringElse(String key, String otherwise) {
/*     */     try {
/* 286 */       return getString(key);
/* 287 */     } catch (Exception e) {
/* 288 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<String> getStringList(String key) throws RuntimeException {
/* 293 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public Optional<List<String>> getOptionalStringList(String key) {
/*     */     try {
/* 297 */       return Optional.ofNullable(getStringList(key));
/* 298 */     } catch (Exception e) {
/* 299 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public List<String> getStringListElse(String key, List<String> otherwise) {
/*     */     try {
/* 304 */       return getStringList(key);
/* 305 */     } catch (Exception e) {
/* 306 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean getBoolean(String key) throws RuntimeException {
/* 311 */     String value = dget(key).convert().intoString();
/* 312 */     switch (value.toLowerCase()) {
/*     */       case "true":
/*     */       case "yes":
/*     */       case "on":
/*     */       case "1":
/* 317 */         return true;
/*     */       case "false":
/*     */       case "no":
/*     */       case "off":
/*     */       case "0":
/* 322 */         return false;
/*     */     } 
/* 324 */     throw new RuntimeException("Can't convert key " + key + " value \"" + value + "\" to boolean");
/*     */   }
/*     */   
/*     */   public Optional<Boolean> getOptionalBoolean(String key) {
/*     */     try {
/* 329 */       return Optional.of(Boolean.valueOf(getBoolean(key)));
/* 330 */     } catch (Exception e) {
/* 331 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public boolean getBooleanElse(String key, boolean otherwise) {
/*     */     try {
/* 336 */       return getBoolean(key);
/* 337 */     } catch (Exception e) {
/* 338 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Boolean> getBooleanList(String key) throws RuntimeException {
/* 343 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public Optional<List<Boolean>> getOptionalBooleanList(String key) {
/*     */     try {
/* 347 */       return Optional.ofNullable(getBooleanList(key));
/* 348 */     } catch (Exception e) {
/* 349 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public List<Boolean> getBooleanListElse(String key, List<Boolean> otherwise) {
/*     */     try {
/* 354 */       return getBooleanList(key);
/* 355 */     } catch (Exception e) {
/* 356 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getInt(String key) throws RuntimeException {
/* 361 */     return dget(key).convert().intoInteger();
/*     */   }
/*     */   public Optional<Integer> getOptionalInt(String key) {
/*     */     try {
/* 365 */       return Optional.of(Integer.valueOf(getInt(key)));
/* 366 */     } catch (Exception e) {
/* 367 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public int getIntElse(String key, int otherwise) {
/*     */     try {
/* 372 */       return getInt(key);
/* 373 */     } catch (Exception e) {
/* 374 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Integer> getIntList(String key) throws RuntimeException {
/* 379 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public Optional<List<Integer>> getOptionalIntList(String key) {
/*     */     try {
/* 383 */       return Optional.ofNullable(getIntList(key));
/* 384 */     } catch (Exception e) {
/* 385 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public List<Integer> getIntListElse(String key, List<Integer> otherwise) {
/*     */     try {
/* 390 */       return getIntList(key);
/* 391 */     } catch (Exception e) {
/* 392 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getLong(String key) throws RuntimeException {
/* 397 */     return dget(key).convert().intoLong();
/*     */   }
/*     */   public Optional<Long> getOptionalLong(String key) {
/*     */     try {
/* 401 */       return Optional.of(Long.valueOf(getLong(key)));
/* 402 */     } catch (Exception e) {
/* 403 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public long getLongElse(String key, long otherwise) {
/*     */     try {
/* 408 */       return getLong(key);
/* 409 */     } catch (Exception e) {
/* 410 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Long> getLongList(String key) throws RuntimeException {
/* 415 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public Optional<List<Long>> getOptionalLongList(String key) {
/*     */     try {
/* 419 */       return Optional.ofNullable(getLongList(key));
/* 420 */     } catch (Exception e) {
/* 421 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public List<Long> getLongListElse(String key, List<Long> otherwise) {
/*     */     try {
/* 426 */       return getLongList(key);
/* 427 */     } catch (Exception e) {
/* 428 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getDouble(String key) throws RuntimeException {
/* 433 */     return dget(key).convert().intoDouble();
/*     */   }
/*     */   public Optional<Double> getOptionalDouble(String key) {
/*     */     try {
/* 437 */       return Optional.of(Double.valueOf(getDouble(key)));
/* 438 */     } catch (Exception e) {
/* 439 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public double getDoubleElse(String key, double otherwise) {
/*     */     try {
/* 444 */       return getDouble(key);
/* 445 */     } catch (Exception e) {
/* 446 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Double> getDoubleList(String key) throws RuntimeException {
/* 451 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public Optional<List<Double>> getOptionalDoubleList(String key) {
/*     */     try {
/* 455 */       return Optional.ofNullable(getDoubleList(key));
/* 456 */     } catch (Exception e) {
/* 457 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public List<Double> getDoubleListElse(String key, List<Double> otherwise) {
/*     */     try {
/* 462 */       return getDoubleList(key);
/* 463 */     } catch (Exception e) {
/* 464 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public BigDecimal getDecimal(String key) throws RuntimeException {
/* 469 */     return dget(key).convert().intoDecimal();
/*     */   }
/*     */   public Optional<BigDecimal> getOptionalDecimal(String key) {
/*     */     try {
/* 473 */       return Optional.ofNullable(getDecimal(key));
/* 474 */     } catch (Exception e) {
/* 475 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public BigDecimal getDecimalElse(String key, BigDecimal otherwise) {
/*     */     try {
/* 480 */       return getDecimal(key);
/* 481 */     } catch (Exception e) {
/* 482 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<BigDecimal> getDecimalList(String key) throws RuntimeException {
/* 487 */     return dget(key).convert().intoList();
/*     */   }
/*     */   public Optional<List<BigDecimal>> getOptionalDecimalList(String key) {
/*     */     try {
/* 491 */       return Optional.ofNullable(getDecimalList(key));
/* 492 */     } catch (Exception e) {
/* 493 */       return Optional.empty();
/*     */     } 
/*     */   }
/*     */   public List<BigDecimal> getDecimalListElse(String key, List<BigDecimal> otherwise) {
/*     */     try {
/* 498 */       return getDecimalList(key);
/* 499 */     } catch (Exception e) {
/* 500 */       return otherwise;
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T> T getSilent(String key) {
/*     */     try {
/* 506 */       return (T)dget(key).asObject();
/* 507 */     } catch (IllegalArgumentException ignored) {
/* 508 */       return null;
/*     */     } 
/*     */   }
/*     */   public void getSilent(String key, Consumer<Dynamic> success) {
/* 512 */     getSilent(key, success, null);
/*     */   }
/*     */   public void getSilent(String key, Consumer<Dynamic> success, Runnable failure) {
/*     */     try {
/* 516 */       Dynamic dynamic = dget(key);
/* 517 */       if (success != null) success.accept(dynamic); 
/* 518 */     } catch (IllegalArgumentException e) {
/* 519 */       if (failure != null) failure.run(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setRuntimeValue(String key, Object value) {
/* 524 */     this.runtimeValues.put(key, value);
/*     */   }
/*     */   
/* 527 */   public DynamicConfig(Language language) { this.jsonParser = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 532 */     this.yamlParser = null;
/*     */     this.language = language; } Yaml getYamlParser() {
/* 534 */     return (this.yamlParser != null) ? this.yamlParser : (this.yamlParser = new Yaml());
/*     */   } JSONParser getJsonParser() {
/*     */     return (this.jsonParser != null) ? this.jsonParser : (this.jsonParser = new JSONParser());
/*     */   } public Language getLanguage() {
/* 538 */     return this.language;
/*     */   }
/*     */   public void setLanguage(Language language) {
/* 541 */     this.language = language;
/*     */   }
/*     */   
/*     */   public Map<Source, Provider> getSources() {
/* 545 */     return this.sources;
/*     */   }
/*     */   public Provider getProvider(String resource) {
/* 548 */     return (Provider)this.sources.entrySet().stream()
/* 549 */       .filter(entry -> ((Source)entry.getKey()).getResourceName().equals(resource))
/* 550 */       .map(Map.Entry::getValue)
/* 551 */       .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid resource " + resource));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\configuralize\DynamicConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */