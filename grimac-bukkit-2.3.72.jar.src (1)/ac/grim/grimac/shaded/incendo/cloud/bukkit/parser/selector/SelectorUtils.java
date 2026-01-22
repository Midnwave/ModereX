/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.BukkitCommandContextKeys;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.MinecraftArgumentTypes;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.Suggestion;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.mojang.brigadier.StringReader;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.exceptions.CommandSyntaxException;
/*     */ import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SelectorUtils
/*     */ {
/*     */   private static <C, T> ArgumentParser<C, T> createModernParser(boolean single, boolean playersOnly, SelectorMapper<T> mapper) {
/*  79 */     if (CraftBukkitReflection.MAJOR_REVISION < 13) {
/*  80 */       return null;
/*     */     }
/*  82 */     WrappedBrigadierParser<C, Object> wrappedBrigParser = new WrappedBrigadierParser(() -> createEntityArgument(single, playersOnly), EntityArgumentParseFunction.INSTANCE);
/*     */ 
/*     */ 
/*     */     
/*  86 */     return (ArgumentParser<C, T>)new ModernSelectorParser<>(wrappedBrigParser, mapper);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static ArgumentType<Object> createEntityArgument(boolean single, boolean playersOnly) {
/*  92 */     Constructor<?> constructor = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("entity")).getDeclaredConstructors()[0];
/*  93 */     constructor.setAccessible(true);
/*     */     try {
/*  95 */       return (ArgumentType<Object>)constructor.newInstance(new Object[] { Boolean.valueOf(single), Boolean.valueOf(playersOnly) });
/*  96 */     } catch (ReflectiveOperationException ex) {
/*  97 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class EntityArgumentParseFunction
/*     */     implements WrappedBrigadierParser.ParseFunction<Object> {
/* 103 */     static final EntityArgumentParseFunction INSTANCE = new EntityArgumentParseFunction();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object apply(ArgumentType<Object> type, StringReader reader) throws CommandSyntaxException {
/* 110 */       Method specialParse = CraftBukkitReflection.findMethod(type
/* 111 */           .getClass(), "parse", new Class[] { StringReader.class, boolean.class });
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 116 */       if (specialParse == null) {
/* 117 */         return type.parse(reader);
/*     */       }
/*     */       try {
/* 120 */         return specialParse.invoke(type, new Object[] { reader, 
/*     */ 
/*     */               
/* 123 */               Boolean.valueOf(true) });
/*     */       }
/* 125 */       catch (InvocationTargetException ex) {
/* 126 */         Throwable cause = ex.getCause();
/* 127 */         if (cause instanceof CommandSyntaxException) {
/* 128 */           throw (CommandSyntaxException)cause;
/*     */         }
/* 130 */         throw new RuntimeException(ex);
/* 131 */       } catch (ReflectiveOperationException ex) {
/* 132 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static abstract class SelectorParser<C, T>
/*     */     implements ArgumentParser.FutureArgumentParser<C, T>, SelectorMapper<T>, SuggestionProvider<C>
/*     */   {
/* 142 */     protected static final Supplier<Object> NO_PLAYERS_EXCEPTION_TYPE = (Supplier<Object>)Suppliers.memoize(() -> findExceptionType("argument.entity.notfound.player"));
/*     */     
/* 144 */     protected static final Supplier<Object> NO_ENTITIES_EXCEPTION_TYPE = (Supplier<Object>)Suppliers.memoize(() -> findExceptionType("argument.entity.notfound.entity"));
/*     */     
/*     */     private final ArgumentParser<C, T> modernParser;
/*     */ 
/*     */     
/*     */     protected static final class Thrower
/*     */     {
/*     */       private final Object type;
/*     */       
/*     */       Thrower(Object simpleCommandExceptionType) {
/* 154 */         this.type = simpleCommandExceptionType;
/*     */       }
/*     */       
/*     */       void throwIt() {
/* 158 */         throw SelectorUtils.rethrow(((SimpleCommandExceptionType)this.type).create());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected SelectorParser(boolean single, boolean playersOnly) {
/* 166 */       this.modernParser = SelectorUtils.createModernParser(single, playersOnly, this);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected CompletableFuture<ArgumentParseResult<T>> legacyParse(CommandContext<C> commandContext, CommandInput commandInput) {
/* 173 */       return ArgumentParseResult.failureFuture((Throwable)new SelectorUnsupportedException(commandContext, 
/*     */             
/* 175 */             getClass()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Iterable<Suggestion> legacySuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 183 */       return Collections.emptyList();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompletableFuture<ArgumentParseResult<T>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 191 */       if (this.modernParser != null) {
/* 192 */         return this.modernParser.parseFuture(commandContext, commandInput);
/*     */       }
/* 194 */       return legacyParse(commandContext, commandInput);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> commandContext, CommandInput input) {
/* 202 */       if (this.modernParser != null) {
/* 203 */         return this.modernParser.suggestionProvider().suggestionsFuture(commandContext, input);
/*     */       }
/* 205 */       return CompletableFuture.completedFuture(legacySuggestions(commandContext, input));
/*     */     }
/*     */ 
/*     */     
/*     */     private static Object findExceptionType(String type) {
/* 210 */       Field[] fields = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("entity")).getDeclaredFields();
/* 211 */       return Arrays.<Field>stream(fields)
/* 212 */         .filter(field -> (Modifier.isStatic(field.getModifiers()) && field.getType() == SimpleCommandExceptionType.class))
/* 213 */         .map(field -> {
/*     */             try {
/*     */               Object fieldValue = field.get(null);
/*     */               if (fieldValue == null) {
/*     */                 return null;
/*     */               }
/*     */               Field messageField = SimpleCommandExceptionType.class.getDeclaredField("message");
/*     */               messageField.setAccessible(true);
/*     */               if (messageField.get(fieldValue).toString().contains(type)) {
/*     */                 return fieldValue;
/*     */               }
/* 224 */             } catch (ReflectiveOperationException ex) {
/*     */               throw new RuntimeException(ex);
/*     */             } 
/*     */             
/*     */             return null;
/* 229 */           }).filter(Objects::nonNull)
/* 230 */         .findFirst()
/* 231 */         .orElseThrow(() -> new IllegalArgumentException("Could not find exception type '" + type + "'"));
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract class EntitySelectorParser<C, T>
/*     */     extends SelectorParser<C, T> {
/*     */     protected EntitySelectorParser(boolean single) {
/* 238 */       super(single, false);
/*     */     }
/*     */   }
/*     */   
/*     */   static abstract class PlayerSelectorParser<C, T>
/*     */     extends SelectorParser<C, T> {
/*     */     protected PlayerSelectorParser(boolean single) {
/* 245 */       super(single, true);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Iterable<Suggestion> legacySuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 253 */       List<Suggestion> suggestions = new ArrayList<>();
/*     */       
/* 255 */       for (Player player : Bukkit.getOnlinePlayers()) {
/* 256 */         CommandSender bukkit = (CommandSender)commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER);
/* 257 */         if (bukkit instanceof Player && !((Player)bukkit).canSee(player)) {
/*     */           continue;
/*     */         }
/* 260 */         suggestions.add(Suggestion.suggestion(player.getName()));
/*     */       } 
/*     */       
/* 263 */       return suggestions;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ModernSelectorParser<C, T>
/*     */     implements ArgumentParser.FutureArgumentParser<C, T>, SuggestionProvider<C>
/*     */   {
/*     */     private final WrappedBrigadierParser<C, Object> wrappedBrigadierParser;
/*     */     
/*     */     private final SelectorUtils.SelectorMapper<T> mapper;
/*     */     
/*     */     ModernSelectorParser(WrappedBrigadierParser<C, Object> wrapperBrigParser, SelectorUtils.SelectorMapper<T> mapper) {
/* 276 */       this.wrappedBrigadierParser = wrapperBrigParser;
/* 277 */       this.mapper = mapper;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompletableFuture<ArgumentParseResult<T>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 286 */       return CompletableFuture.supplyAsync(() -> {
/*     */             CommandInput originalCommandInput = commandInput.copy();
/*     */ 
/*     */             
/*     */             ArgumentParseResult<Object> result = this.wrappedBrigadierParser.parse(commandContext, commandInput);
/*     */             
/*     */             if (result.failure().isPresent()) {
/*     */               return result;
/*     */             }
/*     */             
/*     */             String input = originalCommandInput.difference(commandInput);
/*     */             
/*     */             try {
/*     */               return ArgumentParseResult.success(this.mapper.mapResult(input, new SelectorUtils.EntitySelectorWrapper(commandContext, result.parsedValue().get())));
/* 300 */             } catch (CommandSyntaxException ex) {
/*     */               return ArgumentParseResult.failure((Throwable)ex);
/* 302 */             } catch (Exception ex) {
/*     */               throw SelectorUtils.rethrow(ex);
/*     */             } 
/* 305 */           }(Executor)commandContext.get(BukkitCommandContextKeys.SENDER_SCHEDULER_EXECUTOR));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(CommandContext<C> commandContext, CommandInput input) {
/* 313 */       Object commandSourceStack = commandContext.get("_cloud_brigadier_native_sender");
/*     */       
/* 315 */       Field bypassField = CraftBukkitReflection.findField(commandSourceStack.getClass(), "bypassSelectorPermissions");
/*     */       try {
/* 317 */         boolean prev = false;
/*     */         try {
/* 319 */           if (bypassField != null) {
/* 320 */             prev = bypassField.getBoolean(commandSourceStack);
/* 321 */             bypassField.setBoolean(commandSourceStack, true);
/*     */           } 
/*     */           
/* 324 */           return (CompletableFuture)CompletableFuture.completedFuture(this.wrappedBrigadierParser
/* 325 */               .suggestionProvider().suggestionsFuture(commandContext, input).join());
/*     */         } finally {
/*     */           
/* 328 */           if (bypassField != null) {
/* 329 */             bypassField.setBoolean(commandSourceStack, prev);
/*     */           }
/*     */         } 
/* 332 */       } catch (ReflectiveOperationException ex) {
/* 333 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static final class EntitySelectorWrapper
/*     */   {
/*     */     private static volatile Methods methods;
/*     */     private final CommandContext<?> commandContext;
/*     */     private final Object selector;
/*     */     
/*     */     private static final class Methods
/*     */     {
/*     */       private Method getBukkitEntity;
/*     */       private Method entity;
/*     */       private Method player;
/*     */       private Method entities;
/*     */       private Method players;
/*     */       
/*     */       Methods(CommandContext<?> commandContext, Object selector) {
/* 354 */         Object nativeSender = commandContext.get("_cloud_brigadier_native_sender");
/* 355 */         Class<?> nativeSenderClass = nativeSender.getClass();
/* 356 */         for (Method method : selector.getClass().getDeclaredMethods()) {
/* 357 */           if (method.getParameterCount() == 1 && method
/* 358 */             .getParameterTypes()[0].equals(nativeSenderClass) && 
/* 359 */             Modifier.isPublic(method.getModifiers())) {
/*     */ 
/*     */ 
/*     */             
/* 363 */             Class<?> returnType = method.getReturnType();
/* 364 */             if (List.class.isAssignableFrom(returnType)) {
/* 365 */               ParameterizedType stringListType = (ParameterizedType)method.getGenericReturnType();
/* 366 */               Type listType = stringListType.getActualTypeArguments()[0];
/* 367 */               while (listType instanceof WildcardType) {
/* 368 */                 listType = ((WildcardType)listType).getUpperBounds()[0];
/*     */               }
/*     */ 
/*     */               
/* 372 */               Class<?> clazz = (listType instanceof Class) ? (Class)listType : GenericTypeReflector.erase(listType);
/* 373 */               Method getBukkitEntity = findGetBukkitEntityMethod(clazz);
/* 374 */               if (getBukkitEntity != null) {
/*     */ 
/*     */                 
/* 377 */                 Class<?> bukkitType = getBukkitEntity.getReturnType();
/* 378 */                 if (Player.class.isAssignableFrom(bukkitType))
/* 379 */                 { if (this.players != null) {
/* 380 */                     throw new IllegalStateException();
/*     */                   }
/* 382 */                   this.players = method; }
/*     */                 else
/* 384 */                 { if (this.entities != null) {
/* 385 */                     throw new IllegalStateException();
/*     */                   }
/* 387 */                   this.entities = method; } 
/*     */               } 
/* 389 */             } else if (returnType != void.class) {
/* 390 */               Method getBukkitEntity = findGetBukkitEntityMethod(returnType);
/* 391 */               if (getBukkitEntity != null) {
/*     */ 
/*     */                 
/* 394 */                 Class<?> bukkitType = getBukkitEntity.getReturnType();
/* 395 */                 if (Player.class.isAssignableFrom(bukkitType))
/* 396 */                 { if (this.player != null) {
/* 397 */                     throw new IllegalStateException();
/*     */                   }
/* 399 */                   this.player = method; }
/*     */                 else
/* 401 */                 { if (this.entity != null || this.getBukkitEntity != null) {
/* 402 */                     throw new IllegalStateException();
/*     */                   }
/* 404 */                   this.entity = method;
/* 405 */                   this.getBukkitEntity = getBukkitEntity; } 
/*     */               } 
/*     */             } 
/*     */           } 
/* 409 */         }  Objects.requireNonNull(this.getBukkitEntity, "Failed to locate getBukkitEntity method");
/* 410 */         Objects.requireNonNull(this.player, "Failed to locate findPlayer method");
/* 411 */         Objects.requireNonNull(this.entity, "Failed to locate findEntity method");
/* 412 */         Objects.requireNonNull(this.players, "Failed to locate findPlayers method");
/* 413 */         Objects.requireNonNull(this.entities, "Failed to locate findEntities method");
/*     */       }
/*     */       
/*     */       private static Method findGetBukkitEntityMethod(Class<?> returnType) {
/*     */         Method getBukkitEntity;
/*     */         try {
/* 419 */           getBukkitEntity = returnType.getDeclaredMethod("getBukkitEntity", new Class[0]);
/* 420 */         } catch (ReflectiveOperationException ex) {
/*     */           try {
/* 422 */             getBukkitEntity = returnType.getMethod("getBukkitEntity", new Class[0]);
/* 423 */           } catch (ReflectiveOperationException ex0) {
/* 424 */             getBukkitEntity = null;
/*     */           } 
/*     */         } 
/* 427 */         return getBukkitEntity;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     EntitySelectorWrapper(CommandContext<?> commandContext, Object selector) {
/* 435 */       this.commandContext = commandContext;
/* 436 */       this.selector = selector;
/*     */     }
/*     */ 
/*     */     
/*     */     private static Methods methods(CommandContext<?> commandContext, Object selector) {
/* 441 */       if (methods == null) {
/* 442 */         synchronized (Methods.class) {
/* 443 */           if (methods == null) {
/* 444 */             methods = new Methods(commandContext, selector);
/*     */           }
/*     */         } 
/*     */       }
/* 448 */       return methods;
/*     */     }
/*     */     
/*     */     private Methods methods() {
/* 452 */       return methods(this.commandContext, this.selector);
/*     */     }
/*     */     
/*     */     Entity singleEntity() {
/* 456 */       return reflectiveOperation(() -> (Entity)(methods()).getBukkitEntity.invoke((methods()).entity.invoke(this.selector, new Object[] { this.commandContext.get("_cloud_brigadier_native_sender") }), new Object[0]));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Player singlePlayer() {
/* 463 */       return reflectiveOperation(() -> (Player)(methods()).getBukkitEntity.invoke((methods()).player.invoke(this.selector, new Object[] { this.commandContext.get("_cloud_brigadier_native_sender") }), new Object[0]));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     List<Entity> entities() {
/* 471 */       List<Object> internalEntities = reflectiveOperation(() -> (List)(methods()).entities.invoke(this.selector, new Object[] { this.commandContext.get("_cloud_brigadier_native_sender") }));
/*     */ 
/*     */ 
/*     */       
/* 475 */       return (List<Entity>)internalEntities.stream()
/* 476 */         .map(o -> (Entity)reflectiveOperation(()))
/* 477 */         .collect(Collectors.toList());
/*     */     }
/*     */ 
/*     */     
/*     */     List<Player> players() {
/* 482 */       List<Object> serverPlayers = reflectiveOperation(() -> (List)(methods()).players.invoke(this.selector, new Object[] { this.commandContext.get("_cloud_brigadier_native_sender") }));
/*     */ 
/*     */ 
/*     */       
/* 486 */       return (List<Player>)serverPlayers.stream()
/* 487 */         .map(o -> (Player)reflectiveOperation(()))
/* 488 */         .collect(Collectors.toList());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static <T> T reflectiveOperation(ReflectiveOperation<T> op) {
/*     */       try {
/* 499 */         return op.run();
/* 500 */       } catch (InvocationTargetException ex) {
/* 501 */         if (ex.getCause() instanceof CommandSyntaxException) {
/* 502 */           throw SelectorUtils.rethrow(ex.getCause());
/*     */         }
/* 504 */         throw new RuntimeException(ex);
/* 505 */       } catch (ReflectiveOperationException ex) {
/* 506 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     @FunctionalInterface
/*     */     static interface ReflectiveOperation<T>
/*     */     {
/*     */       T run() throws ReflectiveOperationException;
/*     */     }
/*     */   }
/*     */   
/*     */   private static <X extends Throwable> RuntimeException rethrow(Throwable t) throws X {
/* 519 */     throw (X)t;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   static interface SelectorMapper<T> {
/*     */     T mapResult(String param1String, SelectorUtils.EntitySelectorWrapper param1EntitySelectorWrapper) throws Exception;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\selector\SelectorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */