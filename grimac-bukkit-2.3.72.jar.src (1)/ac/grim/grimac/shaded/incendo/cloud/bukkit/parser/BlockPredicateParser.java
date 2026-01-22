/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.BlockPredicate;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CommandBuildContextSupplier;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.MinecraftArgumentTypes;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.RegistryReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.Supplier;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.block.Block;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BlockPredicateParser<C>
/*     */   implements ArgumentParser.FutureArgumentParser<C, BlockPredicate>
/*     */ {
/*     */   private static final Class<?> TAG_CONTAINER_CLASS;
/*     */   
/*     */   static {
/*     */     Class<?> tagContainerClass;
/*  72 */     if (CraftBukkitReflection.MAJOR_REVISION > 12 && CraftBukkitReflection.MAJOR_REVISION < 16) {
/*  73 */       tagContainerClass = CraftBukkitReflection.needNMSClass("TagRegistry");
/*     */     } else {
/*  75 */       tagContainerClass = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "tagContainerClass", (Object[])new Class[] {
/*     */             
/*  77 */             CraftBukkitReflection.findNMSClass("ITagRegistry"), 
/*  78 */             CraftBukkitReflection.findMCClass("tags.ITagRegistry"), 
/*  79 */             CraftBukkitReflection.findMCClass("tags.TagContainer"), 
/*  80 */             CraftBukkitReflection.findMCClass("core.IRegistry"), 
/*  81 */             CraftBukkitReflection.findMCClass("core.Registry")
/*     */           });
/*     */     } 
/*  84 */     TAG_CONTAINER_CLASS = tagContainerClass;
/*     */   }
/*     */   
/*  87 */   private static final Class<?> CRAFT_WORLD_CLASS = CraftBukkitReflection.needOBCClass("CraftWorld");
/*  88 */   private static final Class<?> MINECRAFT_SERVER_CLASS = CraftBukkitReflection.needNMSClassOrElse("MinecraftServer", new String[] { "net.minecraft.server.MinecraftServer" });
/*     */ 
/*     */ 
/*     */   
/*  92 */   private static final Class<?> COMMAND_LISTENER_WRAPPER_CLASS = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find CommandSourceStack class", (Object[])new Class[] {
/*     */         
/*  94 */         CraftBukkitReflection.findNMSClass("CommandListenerWrapper"), 
/*  95 */         CraftBukkitReflection.findMCClass("commands.CommandListenerWrapper"), 
/*  96 */         CraftBukkitReflection.findMCClass("commands.CommandSourceStack")
/*     */       });
/*     */   
/*  99 */   private static final Supplier<Class<?>> ARGUMENT_BLOCK_PREDICATE_CLASS = (Supplier<Class<?>>)Suppliers.memoize(() -> MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("block_predicate")));
/* 100 */   private static final Class<?> ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find BlockPredicateArgument$Result class", (Object[])new Class[] {
/*     */         
/* 102 */         CraftBukkitReflection.findNMSClass("ArgumentBlockPredicate$b"), 
/* 103 */         CraftBukkitReflection.findMCClass("commands.arguments.blocks.ArgumentBlockPredicate$b"), 
/* 104 */         CraftBukkitReflection.findMCClass("commands.arguments.blocks.BlockPredicateArgument$Result")
/*     */       });
/* 106 */   private static final Class<?> SHAPE_DETECTOR_BLOCK_CLASS = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find BlockInWorld class", (Object[])new Class[] {
/*     */         
/* 108 */         CraftBukkitReflection.findNMSClass("ShapeDetectorBlock"), 
/* 109 */         CraftBukkitReflection.findMCClass("world.level.block.state.pattern.ShapeDetectorBlock"), 
/* 110 */         CraftBukkitReflection.findMCClass("world.level.block.state.pattern.BlockInWorld")
/*     */       });
/* 112 */   private static final Class<?> LEVEL_READER_CLASS = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find LevelReader class", (Object[])new Class[] {
/*     */         
/* 114 */         CraftBukkitReflection.findNMSClass("IWorldReader"), 
/* 115 */         CraftBukkitReflection.findMCClass("world.level.IWorldReader"), 
/* 116 */         CraftBukkitReflection.findMCClass("world.level.LevelReader")
/*     */       });
/* 118 */   private static final Class<?> BLOCK_POSITION_CLASS = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find BlockPos class", (Object[])new Class[] {
/*     */         
/* 120 */         CraftBukkitReflection.findNMSClass("BlockPosition"), 
/* 121 */         CraftBukkitReflection.findMCClass("core.BlockPosition"), 
/* 122 */         CraftBukkitReflection.findMCClass("core.BlockPos")
/*     */       });
/*     */   
/* 125 */   private static final Constructor<?> BLOCK_POSITION_CTR = CraftBukkitReflection.needConstructor(BLOCK_POSITION_CLASS, new Class[] { int.class, int.class, int.class });
/*     */   
/* 127 */   private static final Constructor<?> SHAPE_DETECTOR_BLOCK_CTR = CraftBukkitReflection.needConstructor(SHAPE_DETECTOR_BLOCK_CLASS, new Class[] { LEVEL_READER_CLASS, BLOCK_POSITION_CLASS, boolean.class });
/* 128 */   private static final Method GET_HANDLE_METHOD = CraftBukkitReflection.needMethod(CRAFT_WORLD_CLASS, "getHandle", new Class[0]);
/* 129 */   private static final Method CREATE_PREDICATE_METHOD = (Method)CraftBukkitReflection.firstNonNullOrNull((Object[])new Method[] {
/* 130 */         CraftBukkitReflection.findMethod(ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS, "create", new Class[] { TAG_CONTAINER_CLASS
/* 131 */           }), CraftBukkitReflection.findMethod(ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS, "a", new Class[] { TAG_CONTAINER_CLASS }) });
/*     */   
/*     */   private static final Method GET_SERVER_METHOD;
/*     */   
/*     */   static {
/* 136 */     GET_SERVER_METHOD = (Method)CraftBukkitReflection.streamMethods(COMMAND_LISTENER_WRAPPER_CLASS).filter(it -> (it.getReturnType().equals(MINECRAFT_SERVER_CLASS) && it.getParameterCount() == 0)).findFirst().orElseThrow(() -> new IllegalStateException("Could not find CommandSourceStack#getServer."));
/* 137 */     GET_TAG_REGISTRY_METHOD = (Method)CraftBukkitReflection.firstNonNullOrNull((Object[])new Method[] {
/* 138 */           CraftBukkitReflection.findMethod(MINECRAFT_SERVER_CLASS, "getTagRegistry", new Class[0]), 
/* 139 */           CraftBukkitReflection.findMethod(MINECRAFT_SERVER_CLASS, "getTags", new Class[0]), 
/* 140 */           CraftBukkitReflection.streamMethods(MINECRAFT_SERVER_CLASS)
/* 141 */           .filter(it -> (it.getReturnType().equals(TAG_CONTAINER_CLASS) && it.getParameterCount() == 0))
/* 142 */           .findFirst()
/* 143 */           .orElse(null) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Method GET_TAG_REGISTRY_METHOD;
/*     */   
/*     */   private final ArgumentParser<C, BlockPredicate> parser;
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, BlockPredicate> blockPredicateParser() {
/* 155 */     return ParserDescriptor.of((ArgumentParser)new BlockPredicateParser(), BlockPredicate.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> CommandComponent.Builder<C, BlockPredicate> blockPredicateComponent() {
/* 167 */     return CommandComponent.builder().parser(blockPredicateParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockPredicateParser() {
/* 178 */     this.parser = createParser();
/*     */   }
/*     */ 
/*     */   
/*     */   private ArgumentParser<C, BlockPredicate> createParser() {
/* 183 */     Supplier<ArgumentType<Object>> inst = () -> {
/*     */         Constructor<?> ctr = ((Class)ARGUMENT_BLOCK_PREDICATE_CLASS.get()).getDeclaredConstructors()[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/*     */           return (ctr.getParameterCount() == 0) ? (ArgumentType)ctr.newInstance(new Object[0]) : (ArgumentType)ctr.newInstance(new Object[] { CommandBuildContextSupplier.commandBuildContext() });
/* 192 */         } catch (ReflectiveOperationException e) {
/*     */           throw new RuntimeException("Failed to initialize BlockPredicate parser.", e);
/*     */         } 
/*     */       };
/* 196 */     return (ArgumentParser<C, BlockPredicate>)(new WrappedBrigadierParser(inst)).flatMapSuccess((ctx, result) -> {
/*     */           if (result instanceof Predicate) {
/*     */             return ArgumentParseResult.successFuture(new BlockPredicateImpl((Predicate<Object>)result));
/*     */           }
/*     */           
/*     */           Object commandSourceStack = ctx.get("_cloud_brigadier_native_sender");
/*     */           try {
/*     */             Object obj;
/*     */             Object server = GET_SERVER_METHOD.invoke(commandSourceStack, new Object[0]);
/*     */             if (GET_TAG_REGISTRY_METHOD != null) {
/*     */               obj = GET_TAG_REGISTRY_METHOD.invoke(server, new Object[0]);
/*     */             } else {
/*     */               obj = RegistryReflection.builtInRegistryByName("block");
/*     */             } 
/*     */             Objects.requireNonNull(CREATE_PREDICATE_METHOD, "create on BlockPredicateArgument$Result");
/*     */             Predicate<Object> predicate = (Predicate<Object>)CREATE_PREDICATE_METHOD.invoke(result, new Object[] { obj });
/*     */             return ArgumentParseResult.successFuture(new BlockPredicateImpl(predicate));
/* 213 */           } catch (ReflectiveOperationException ex) {
/*     */             throw new RuntimeException(ex);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<ArgumentParseResult<BlockPredicate>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 224 */     return this.parser.parseFuture(commandContext, commandInput);
/*     */   }
/*     */ 
/*     */   
/*     */   public SuggestionProvider<C> suggestionProvider() {
/* 229 */     return this.parser.suggestionProvider();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <C> void registerParserSupplier(CommandManager<C> commandManager) {
/* 240 */     commandManager.parserRegistry().registerParser(blockPredicateParser());
/*     */   }
/*     */   
/*     */   private static final class BlockPredicateImpl
/*     */     implements BlockPredicate
/*     */   {
/*     */     private final Predicate<Object> predicate;
/*     */     
/*     */     BlockPredicateImpl(Predicate<Object> predicate) {
/* 249 */       this.predicate = predicate;
/*     */     }
/*     */     
/*     */     private boolean testImpl(Block block, boolean loadChunks) {
/*     */       try {
/* 254 */         Object blockInWorld = BlockPredicateParser.SHAPE_DETECTOR_BLOCK_CTR.newInstance(new Object[] {
/* 255 */               BlockPredicateParser.access$000().invoke(block.getWorld(), new Object[0]), 
/* 256 */               BlockPredicateParser.access$100().newInstance(new Object[] { Integer.valueOf(block.getX()), Integer.valueOf(block.getY()), Integer.valueOf(block.getZ())
/* 257 */                 }), Boolean.valueOf(loadChunks)
/*     */             });
/* 259 */         return this.predicate.test(blockInWorld);
/* 260 */       } catch (ReflectiveOperationException ex) {
/* 261 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean test(Block block) {
/* 267 */       return testImpl(block, false);
/*     */     }
/*     */ 
/*     */     
/*     */     public BlockPredicate loadChunks() {
/* 272 */       return new BlockPredicate()
/*     */         {
/*     */           public BlockPredicate loadChunks() {
/* 275 */             return this;
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean test(Block block) {
/* 280 */             return BlockPredicateParser.BlockPredicateImpl.this.testImpl(block, true);
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\BlockPredicateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */