/*     */ package ac.grim.grimac.shaded.incendo.cloud.brigadier;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapperHolder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.ArgumentTypeFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.BrigadierMapping;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.BrigadierMappingBuilder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.BrigadierMappingContributor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.BrigadierMappings;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.node.LiteralBrigadierNodeFactory;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.suggestion.TooltipSuggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.flag.CommandFlagParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.BooleanParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.ByteParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.DoubleParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.FloatParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.IntegerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.LongParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.ShortParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringArrayParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.StringParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.setting.Configurable;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.ByteRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.DoubleRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.FloatRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.IntRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.LongRange;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.type.range.ShortRange;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.arguments.BoolArgumentType;
/*     */ import com.mojang.brigadier.arguments.DoubleArgumentType;
/*     */ import com.mojang.brigadier.arguments.FloatArgumentType;
/*     */ import com.mojang.brigadier.arguments.IntegerArgumentType;
/*     */ import com.mojang.brigadier.arguments.LongArgumentType;
/*     */ import com.mojang.brigadier.arguments.StringArgumentType;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.function.Consumer;
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
/*     */ public final class CloudBrigadierManager<C, S>
/*     */   implements SenderMapperHolder<S, C>
/*     */ {
/*  80 */   private final BrigadierMappings<C, S> brigadierMappings = BrigadierMappings.create();
/*     */   private final LiteralBrigadierNodeFactory<C, S> literalBrigadierNodeFactory;
/*     */   private final Map<Class<?>, ArgumentTypeFactory<?>> defaultArgumentTypeSuppliers;
/*  83 */   private final Configurable<BrigadierSetting> settings = Configurable.enumConfigurable(BrigadierSetting.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final SenderMapper<S, C> brigadierSourceMapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloudBrigadierManager(CommandManager<C> commandManager, SenderMapper<S, C> brigadierSourceMapper) {
/*  96 */     this.brigadierSourceMapper = Objects.<SenderMapper<S, C>>requireNonNull(brigadierSourceMapper, "brigadierSourceMapper");
/*  97 */     this.defaultArgumentTypeSuppliers = new HashMap<>();
/*  98 */     this
/*     */ 
/*     */       
/* 101 */       .literalBrigadierNodeFactory = new LiteralBrigadierNodeFactory(this, commandManager, commandManager.suggestionFactory().mapped(TooltipSuggestion::tooltipSuggestion));
/*     */     
/* 103 */     registerInternalMappings();
/* 104 */     ServiceLoader<BrigadierMappingContributor> loader = ServiceLoader.load(BrigadierMappingContributor.class, BrigadierMappingContributor.class
/*     */         
/* 106 */         .getClassLoader());
/*     */     
/* 108 */     loader.iterator().forEachRemaining(contributor -> contributor.contribute(commandManager, this));
/* 109 */     commandManager.registerCommandPreProcessor(ctx -> {
/*     */           if (!ctx.commandContext().contains("_cloud_brigadier_native_sender")) {
/*     */             ctx.commandContext().store("_cloud_brigadier_native_sender", this.brigadierSourceMapper.reverse(ctx.commandContext().sender()));
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void registerInternalMappings() {
/* 121 */     registerMapping(new TypeToken<ByteParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(()).cloudSuggestions());
/*     */     
/* 126 */     registerMapping(new TypeToken<ShortParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(()).cloudSuggestions());
/*     */     
/* 131 */     registerMapping(new TypeToken<IntegerParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(()).cloudSuggestions());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     registerMapping(new TypeToken<FloatParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(()).cloudSuggestions());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 159 */     registerMapping(new TypeToken<DoubleParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(()).cloudSuggestions());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     registerMapping(new TypeToken<LongParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(()).cloudSuggestions());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 187 */     registerMapping(new TypeToken<BooleanParser<C>>() {
/*     */         
/*     */         },  builder -> builder.toConstant((ArgumentType)BoolArgumentType.bool()));
/* 190 */     registerMapping(new TypeToken<StringParser<C>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.cloudSuggestions().to(()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 203 */     registerMapping(new TypeToken<CommandFlagParser<C>>() {
/*     */         
/*     */         },  builder -> builder.cloudSuggestions().toConstant((ArgumentType)StringArgumentType.greedyString()));
/* 206 */     registerMapping(new TypeToken<StringArrayParser<C>>() {
/*     */         
/*     */         },  builder -> builder.cloudSuggestions().toConstant((ArgumentType)StringArgumentType.greedyString()));
/* 209 */     registerMapping(new TypeToken<WrappedBrigadierParser<C, ?>>()
/*     */         {
/*     */         
/*     */         },  builder -> builder.to(WrappedBrigadierParser::nativeArgumentType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public Configurable<BrigadierSetting> settings() {
/* 221 */     return this.settings;
/*     */   }
/*     */ 
/*     */   
/*     */   public SenderMapper<S, C> senderMapper() {
/* 226 */     return this.brigadierSourceMapper;
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
/*     */   @API(status = API.Status.STABLE, since = "1.2.0")
/*     */   public void setNativeNumberSuggestions(boolean nativeNumberSuggestions) {
/* 241 */     setNativeSuggestions(new TypeToken<ByteParser<C>>() {  }, nativeNumberSuggestions);
/* 242 */     setNativeSuggestions(new TypeToken<ShortParser<C>>() {  }, nativeNumberSuggestions);
/* 243 */     setNativeSuggestions(new TypeToken<IntegerParser<C>>() {  }, nativeNumberSuggestions);
/* 244 */     setNativeSuggestions(new TypeToken<FloatParser<C>>() {  }, nativeNumberSuggestions);
/* 245 */     setNativeSuggestions(new TypeToken<DoubleParser<C>>() {  }, nativeNumberSuggestions);
/* 246 */     setNativeSuggestions(new TypeToken<LongParser<C>>() {  }, nativeNumberSuggestions);
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
/*     */   @API(status = API.Status.STABLE, since = "1.2.0")
/*     */   public <T, K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, T>> void setNativeSuggestions(TypeToken<K> argumentType, boolean nativeSuggestions) throws IllegalArgumentException {
/* 266 */     Class<K> parserClass = GenericTypeReflector.erase(argumentType.getType());
/* 267 */     BrigadierMapping<C, K, S> mapping = this.brigadierMappings.mapping(parserClass);
/* 268 */     if (mapping == null) {
/* 269 */       throw new IllegalArgumentException("No mapper registered for type: " + 
/*     */           
/* 271 */           GenericTypeReflector.erase(argumentType.getType())
/* 272 */           .toGenericString());
/*     */     }
/*     */     
/* 275 */     this.brigadierMappings.registerMapping(parserClass, mapping.withNativeSuggestions(nativeSuggestions));
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
/*     */   @API(status = API.Status.STABLE, since = "1.5.0")
/*     */   public <K extends ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser<C, ?>> void registerMapping(TypeToken<K> parserType, Consumer<BrigadierMappingBuilder<K, S>> configurer) {
/* 291 */     BrigadierMappingBuilder<K, S> builder = BrigadierMapping.builder();
/* 292 */     configurer.accept(builder);
/* 293 */     mappings().registerMappingUnsafe(GenericTypeReflector.erase(parserType.getType()), builder.build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, since = "2.0.0")
/*     */   public BrigadierMappings<C, S> mappings() {
/* 304 */     return this.brigadierMappings;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public LiteralBrigadierNodeFactory<C, S> literalBrigadierNodeFactory() {
/* 315 */     return this.literalBrigadierNodeFactory;
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
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public <T> void registerDefaultArgumentTypeSupplier(Class<T> clazz, ArgumentTypeFactory<T> factory) {
/* 331 */     this.defaultArgumentTypeSuppliers.put(clazz, factory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, since = "2.0.0")
/*     */   public Map<Class<?>, ArgumentTypeFactory<?>> defaultArgumentTypeFactories() {
/* 342 */     return Collections.unmodifiableMap(this.defaultArgumentTypeSuppliers);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\CloudBrigadierManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */