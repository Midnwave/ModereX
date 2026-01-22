/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.AnnotatedTypeMap;
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.annotation.specifier.FlagYielding;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.annotation.specifier.Greedy;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.annotation.specifier.Liberal;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.annotation.specifier.Quoted;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.annotation.specifier.Range;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.BooleanParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.ByteParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.CharacterParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.DoubleParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.DurationParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.EnumParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.FloatParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.IntegerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.LongParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.ShortParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringArrayParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.UUIDParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.function.Function;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class StandardParserRegistry<C>
/*     */   implements ParserRegistry<C>
/*     */ {
/*  71 */   private static final Map<Class<?>, Class<?>> PRIMITIVE_MAPPINGS = new HashMap<Class<?>, Class<?>>()
/*     */     {
/*     */     
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   private final Map<String, Function<ParserParameters, ArgumentParser<C, ?>>> namedParsers = new HashMap<>();
/*  86 */   private final Map<AnnotatedType, Function<ParserParameters, ArgumentParser<C, ?>>> parserSuppliers = (Map<AnnotatedType, Function<ParserParameters, ArgumentParser<C, ?>>>)new AnnotatedTypeMap();
/*  87 */   private final Map<Class<? extends Annotation>, ParserRegistry.AnnotationMapper<?>> annotationMappers = new HashMap<>();
/*  88 */   private final Map<String, SuggestionProvider<C>> namedSuggestionProviders = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardParserRegistry() {
/*  96 */     registerAnnotationMapper(Range.class, new RangeMapper());
/*  97 */     registerAnnotationMapper(Greedy.class, new GreedyMapper());
/*  98 */     registerAnnotationMapper(Quoted.class, (quoted, typeToken) -> ParserParameters.single(StandardParameters.QUOTED, Boolean.valueOf(true)));
/*     */ 
/*     */ 
/*     */     
/* 102 */     registerAnnotationMapper(Liberal.class, (liberal, typeToken) -> ParserParameters.single(StandardParameters.LIBERAL, Boolean.valueOf(true)));
/*     */ 
/*     */ 
/*     */     
/* 106 */     registerAnnotationMapper(FlagYielding.class, (flagYielding, typeToken) -> ParserParameters.single(StandardParameters.FLAG_YIELDING, Boolean.valueOf(true)));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     registerParserSupplier(TypeToken.get(Byte.class), options -> new ByteParser(((Byte)options.<Number>get(StandardParameters.RANGE_MIN, Byte.valueOf(-128))).byteValue(), ((Byte)options.<Number>get(StandardParameters.RANGE_MAX, Byte.valueOf(127))).byteValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     registerParserSupplier(TypeToken.get(Short.class), options -> new ShortParser(((Short)options.<Number>get(StandardParameters.RANGE_MIN, Short.valueOf(-32768))).shortValue(), ((Short)options.<Number>get(StandardParameters.RANGE_MAX, Short.valueOf('翿'))).shortValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 122 */     registerParserSupplier(TypeToken.get(Integer.class), options -> new IntegerParser(((Integer)options.<Number>get(StandardParameters.RANGE_MIN, Integer.valueOf(-2147483648))).intValue(), ((Integer)options.<Number>get(StandardParameters.RANGE_MAX, Integer.valueOf(2147483647))).intValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     registerParserSupplier(TypeToken.get(Long.class), options -> new LongParser(((Long)options.<Number>get(StandardParameters.RANGE_MIN, Long.valueOf(Long.MIN_VALUE))).longValue(), ((Long)options.<Number>get(StandardParameters.RANGE_MAX, Long.valueOf(Long.MAX_VALUE))).longValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     registerParserSupplier(TypeToken.get(Float.class), options -> new FloatParser(((Float)options.<Number>get(StandardParameters.RANGE_MIN, Float.valueOf(Float.NEGATIVE_INFINITY))).floatValue(), ((Float)options.<Number>get(StandardParameters.RANGE_MAX, Float.valueOf(Float.POSITIVE_INFINITY))).floatValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     registerParserSupplier(TypeToken.get(Double.class), options -> new DoubleParser(((Double)options.<Number>get(StandardParameters.RANGE_MIN, Double.valueOf(Double.NEGATIVE_INFINITY))).doubleValue(), ((Double)options.<Number>get(StandardParameters.RANGE_MAX, Double.valueOf(Double.POSITIVE_INFINITY))).doubleValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     registerParserSupplier(TypeToken.get(Character.class), options -> new CharacterParser());
/* 143 */     registerParserSupplier(TypeToken.get(String[].class), options -> new StringArrayParser(((Boolean)options.<Boolean>get(StandardParameters.FLAG_YIELDING, Boolean.valueOf(false))).booleanValue()));
/*     */ 
/*     */ 
/*     */     
/* 147 */     registerParserSupplier(TypeToken.get(String.class), options -> {
/*     */           StringParser.StringMode stringMode;
/*     */           
/*     */           boolean greedy = ((Boolean)options.<Boolean>get(StandardParameters.GREEDY, Boolean.valueOf(false))).booleanValue();
/*     */           
/*     */           boolean greedyFlagAware = ((Boolean)options.<Boolean>get(StandardParameters.FLAG_YIELDING, Boolean.valueOf(false))).booleanValue();
/*     */           
/*     */           boolean quoted = ((Boolean)options.<Boolean>get(StandardParameters.QUOTED, Boolean.valueOf(false))).booleanValue();
/*     */           
/*     */           if (greedyFlagAware && quoted) {
/*     */             throw new IllegalArgumentException("Don't know whether to create GREEDY_FLAG_YIELDING or QUOTED StringArgument.StringParser, both specified.");
/*     */           }
/*     */           if (greedy && quoted) {
/*     */             throw new IllegalArgumentException("Don't know whether to create GREEDY or QUOTED StringArgument.StringParser, both specified.");
/*     */           }
/*     */           if (greedyFlagAware) {
/*     */             stringMode = StringParser.StringMode.GREEDY_FLAG_YIELDING;
/*     */           } else if (greedy) {
/*     */             stringMode = StringParser.StringMode.GREEDY;
/*     */           } else if (quoted) {
/*     */             stringMode = StringParser.StringMode.QUOTED;
/*     */           } else {
/*     */             stringMode = StringParser.StringMode.SINGLE;
/*     */           } 
/*     */           return (ArgumentParser)new StringParser(stringMode);
/*     */         });
/* 173 */     registerParserSupplier(TypeToken.get(Boolean.class), options -> {
/*     */           boolean liberal = ((Boolean)options.<Boolean>get(StandardParameters.LIBERAL, Boolean.valueOf(false))).booleanValue();
/*     */           return (ArgumentParser)new BooleanParser(liberal);
/*     */         });
/* 177 */     registerParser(UUIDParser.uuidParser());
/* 178 */     registerParser(DurationParser.durationParser());
/*     */     
/* 180 */     ServiceLoader<ParserContributor> loader = ServiceLoader.load(ParserContributor.class, ParserContributor.class
/*     */         
/* 182 */         .getClassLoader());
/*     */     
/* 184 */     loader.iterator().forEachRemaining(contributor -> contributor.contribute(this));
/*     */   }
/*     */   
/*     */   private static boolean isPrimitive(TypeToken<?> type) {
/* 188 */     return GenericTypeReflector.erase(type.getType()).isPrimitive();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> StandardParserRegistry<C> registerParserSupplier(TypeToken<T> type, Function<ParserParameters, ArgumentParser<C, ?>> supplier) {
/* 197 */     this.parserSuppliers.put(type.getAnnotatedType(), supplier);
/* 198 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StandardParserRegistry<C> registerNamedParserSupplier(String name, Function<ParserParameters, ArgumentParser<C, ?>> supplier) {
/* 207 */     this.namedParsers.put(name, supplier);
/* 208 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <A extends Annotation> StandardParserRegistry<C> registerAnnotationMapper(Class<A> annotation, ParserRegistry.AnnotationMapper<A> mapper) {
/* 216 */     this.annotationMappers.put(annotation, mapper);
/* 217 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserParameters parseAnnotations(TypeToken<?> parsingType, Collection<? extends Annotation> annotations) {
/* 226 */     ParserParameters parserParameters = new ParserParameters();
/* 227 */     annotations.forEach(annotation -> {
/*     */           ParserRegistry.AnnotationMapper<Annotation> mapper = (ParserRegistry.AnnotationMapper)this.annotationMappers.get(annotation.annotationType());
/*     */           
/*     */           if (mapper == null) {
/*     */             return;
/*     */           }
/*     */           ParserParameters parserParametersCasted = mapper.mapAnnotation(annotation, parsingType);
/*     */           parserParameters.merge(parserParametersCasted);
/*     */         });
/* 236 */     return parserParameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Optional<ArgumentParser<C, T>> createParser(TypeToken<T> type, ParserParameters parserParameters) {
/*     */     TypeToken<?> actualType;
/* 246 */     if (GenericTypeReflector.erase(type.getType()).isPrimitive()) {
/* 247 */       actualType = TypeToken.get(PRIMITIVE_MAPPINGS.get(GenericTypeReflector.erase(type.getType())));
/*     */     } else {
/* 249 */       actualType = type;
/*     */     } 
/* 251 */     Function<ParserParameters, ArgumentParser<C, ?>> producer = this.parserSuppliers.get(actualType.getAnnotatedType());
/* 252 */     if (producer == null) {
/*     */       
/* 254 */       if (GenericTypeReflector.isSuperType(Enum.class, actualType.getType())) {
/*     */         
/* 256 */         EnumParser enumArgument = new EnumParser(GenericTypeReflector.erase(actualType.getType()));
/*     */         
/* 258 */         return (Optional)Optional.of(enumArgument);
/*     */       } 
/* 260 */       return Optional.empty();
/*     */     } 
/* 262 */     ArgumentParser<C, T> parser = (ArgumentParser<C, T>)producer.apply(parserParameters);
/*     */     
/* 264 */     return Optional.of(parser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Optional<ArgumentParser<C, T>> createParser(String name, ParserParameters parserParameters) {
/* 272 */     Function<ParserParameters, ArgumentParser<C, ?>> producer = this.namedParsers.get(name);
/* 273 */     if (producer == null) {
/* 274 */       return Optional.empty();
/*     */     }
/* 276 */     ArgumentParser<C, T> parser = (ArgumentParser<C, T>)producer.apply(parserParameters);
/*     */     
/* 278 */     return Optional.of(parser);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerSuggestionProvider(String name, SuggestionProvider<C> suggestionProvider) {
/* 286 */     this.namedSuggestionProviders.put(name.toLowerCase(Locale.ENGLISH), suggestionProvider);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<SuggestionProvider<C>> getSuggestionProvider(String name) {
/* 293 */     SuggestionProvider<C> suggestionProvider = this.namedSuggestionProviders.get(name.toLowerCase(Locale.ENGLISH));
/* 294 */     return Optional.ofNullable(suggestionProvider);
/*     */   }
/*     */   
/*     */   private static final class RangeMapper
/*     */     implements ParserRegistry.AnnotationMapper<Range> {
/*     */     private RangeMapper() {}
/*     */     
/*     */     public ParserParameters mapAnnotation(Range range, TypeToken<?> type) {
/*     */       Class<?> clazz;
/* 303 */       if (StandardParserRegistry.isPrimitive(type)) {
/* 304 */         clazz = (Class)StandardParserRegistry.PRIMITIVE_MAPPINGS.get(GenericTypeReflector.erase(type.getType()));
/*     */       } else {
/* 306 */         clazz = GenericTypeReflector.erase(type.getType());
/*     */       } 
/* 308 */       if (!Number.class.isAssignableFrom(clazz)) {
/* 309 */         return ParserParameters.empty();
/*     */       }
/* 311 */       Number min = null;
/* 312 */       Number max = null;
/* 313 */       if (clazz.equals(Byte.class)) {
/* 314 */         if (!range.min().isEmpty()) {
/* 315 */           min = Byte.valueOf(Byte.parseByte(range.min()));
/*     */         }
/* 317 */         if (!range.max().isEmpty()) {
/* 318 */           max = Byte.valueOf(Byte.parseByte(range.max()));
/*     */         }
/* 320 */       } else if (clazz.equals(Short.class)) {
/* 321 */         if (!range.min().isEmpty()) {
/* 322 */           min = Short.valueOf(Short.parseShort(range.min()));
/*     */         }
/* 324 */         if (!range.max().isEmpty()) {
/* 325 */           max = Short.valueOf(Short.parseShort(range.max()));
/*     */         }
/* 327 */       } else if (clazz.equals(Integer.class)) {
/* 328 */         if (!range.min().isEmpty()) {
/* 329 */           min = Integer.valueOf(Integer.parseInt(range.min()));
/*     */         }
/* 331 */         if (!range.max().isEmpty()) {
/* 332 */           max = Integer.valueOf(Integer.parseInt(range.max()));
/*     */         }
/* 334 */       } else if (clazz.equals(Long.class)) {
/* 335 */         if (!range.min().isEmpty()) {
/* 336 */           min = Long.valueOf(Long.parseLong(range.min()));
/*     */         }
/* 338 */         if (!range.max().isEmpty()) {
/* 339 */           max = Long.valueOf(Long.parseLong(range.max()));
/*     */         }
/* 341 */       } else if (clazz.equals(Float.class)) {
/* 342 */         if (!range.min().isEmpty()) {
/* 343 */           min = Float.valueOf(Float.parseFloat(range.min()));
/*     */         }
/* 345 */         if (!range.max().isEmpty()) {
/* 346 */           max = Float.valueOf(Float.parseFloat(range.max()));
/*     */         }
/* 348 */       } else if (clazz.equals(Double.class)) {
/* 349 */         if (!range.min().isEmpty()) {
/* 350 */           min = Double.valueOf(Double.parseDouble(range.min()));
/*     */         }
/* 352 */         if (!range.max().isEmpty()) {
/* 353 */           max = Double.valueOf(Double.parseDouble(range.max()));
/*     */         }
/*     */       } 
/* 356 */       ParserParameters parserParameters = new ParserParameters();
/* 357 */       if (min != null) {
/* 358 */         parserParameters.store(StandardParameters.RANGE_MIN, min);
/*     */       }
/* 360 */       if (max != null) {
/* 361 */         parserParameters.store(StandardParameters.RANGE_MAX, max);
/*     */       }
/* 363 */       return parserParameters;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class GreedyMapper
/*     */     implements ParserRegistry.AnnotationMapper<Greedy> {
/*     */     private GreedyMapper() {}
/*     */     
/*     */     public ParserParameters mapAnnotation(Greedy greedy, TypeToken<?> typeToken) {
/* 372 */       return ParserParameters.single(StandardParameters.GREEDY, Boolean.valueOf(true));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\StandardParserRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */