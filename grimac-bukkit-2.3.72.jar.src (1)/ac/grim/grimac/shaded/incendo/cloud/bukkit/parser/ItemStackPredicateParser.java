/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.ItemStackPredicate;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CommandBuildContextSupplier;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.MinecraftArgumentTypes;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.context.CommandContext;
/*     */ import com.mojang.brigadier.context.StringRange;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.Supplier;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.NamespacedKey;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ItemStackPredicateParser<C>
/*     */   implements ArgumentParser.FutureArgumentParser<C, ItemStackPredicate>
/*     */ {
/*  70 */   private static final Class<?> CRAFT_ITEM_STACK_CLASS = CraftBukkitReflection.needOBCClass("inventory.CraftItemStack");
/*     */   
/*  72 */   private static final Supplier<Class<?>> ARGUMENT_ITEM_PREDICATE_CLASS = (Supplier<Class<?>>)Suppliers.memoize(() -> MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("item_predicate")));
/*  73 */   private static final Class<?> ARGUMENT_ITEM_PREDICATE_RESULT_CLASS = (Class)CraftBukkitReflection.firstNonNullOrNull((Object[])new Class[] {
/*  74 */         CraftBukkitReflection.findNMSClass("ArgumentItemPredicate$b"), 
/*  75 */         CraftBukkitReflection.findMCClass("commands.arguments.item.ArgumentItemPredicate$b"), 
/*  76 */         CraftBukkitReflection.findMCClass("commands.arguments.item.ItemPredicateArgument$Result")
/*     */       });
/*  78 */   private static final Method CREATE_PREDICATE_METHOD = (ARGUMENT_ITEM_PREDICATE_RESULT_CLASS == null) ? 
/*  79 */     null : 
/*  80 */     (Method)CraftBukkitReflection.firstNonNullOrNull((Object[])new Method[] {
/*  81 */         CraftBukkitReflection.findMethod(ARGUMENT_ITEM_PREDICATE_RESULT_CLASS, "create", new Class[] {
/*     */             
/*     */             CommandContext.class
/*     */ 
/*     */           
/*  86 */           }), CraftBukkitReflection.findMethod(ARGUMENT_ITEM_PREDICATE_RESULT_CLASS, "a", new Class[] { CommandContext.class })
/*     */       });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   private static final Method AS_NMS_COPY_METHOD = CraftBukkitReflection.needMethod(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", new Class[] { ItemStack.class });
/*     */ 
/*     */ 
/*     */   
/*     */   private final ArgumentParser<C, ItemStackPredicate> parser;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, ItemStackPredicate> itemStackPredicateParser() {
/* 104 */     return ParserDescriptor.of((ArgumentParser)new ItemStackPredicateParser(), ItemStackPredicate.class);
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
/*     */   public static <C> CommandComponent.Builder<C, ItemStackPredicate> itemStackPredicateComponent() {
/* 116 */     return CommandComponent.builder().parser(itemStackPredicateParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemStackPredicateParser() {
/* 127 */     this.parser = createParser();
/*     */   }
/*     */ 
/*     */   
/*     */   private ArgumentParser<C, ItemStackPredicate> createParser() {
/* 132 */     Supplier<ArgumentType<Object>> inst = () -> {
/*     */         Constructor<?> ctr = ((Class)ARGUMENT_ITEM_PREDICATE_CLASS.get()).getDeclaredConstructors()[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/*     */           return (ctr.getParameterCount() == 0) ? (ArgumentType)ctr.newInstance(new Object[0]) : (ArgumentType)ctr.newInstance(new Object[] { CommandBuildContextSupplier.commandBuildContext() });
/* 141 */         } catch (ReflectiveOperationException e) {
/*     */           throw new RuntimeException("Failed to initialize ItemPredicate parser.", e);
/*     */         } 
/*     */       };
/*     */     
/* 146 */     return (ArgumentParser<C, ItemStackPredicate>)(new WrappedBrigadierParser(inst)).flatMapSuccess((ctx, result) -> {
/*     */           if (result instanceof Predicate) {
/*     */             return ArgumentParseResult.successFuture(new ItemStackPredicateImpl((Predicate<Object>)result));
/*     */           }
/*     */           
/*     */           Object commandSourceStack = ctx.get("_cloud_brigadier_native_sender");
/*     */           CommandContext<Object> dummy = createDummyContext(ctx, commandSourceStack);
/*     */           Objects.requireNonNull(CREATE_PREDICATE_METHOD, "ItemPredicateArgument$Result#create");
/*     */           try {
/*     */             Predicate<Object> predicate = (Predicate<Object>)CREATE_PREDICATE_METHOD.invoke(result, new Object[] { dummy });
/*     */             return ArgumentParseResult.successFuture(new ItemStackPredicateImpl(predicate));
/* 157 */           } catch (ReflectiveOperationException ex) {
/*     */             throw new RuntimeException(ex);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <C> CommandContext<Object> createDummyContext(CommandContext<C> ctx, Object commandSourceStack) {
/* 167 */     return new CommandContext(commandSourceStack, ctx
/*     */         
/* 169 */         .rawInput().input(), 
/* 170 */         Collections.emptyMap(), null, null, 
/*     */ 
/*     */         
/* 173 */         Collections.emptyList(), 
/* 174 */         StringRange.at(0), null, null, false);
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
/*     */   private static <C> void registerParserSupplier(CommandManager<C> commandManager) {
/* 189 */     commandManager.parserRegistry().registerParser(itemStackPredicateParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompletableFuture<ArgumentParseResult<ItemStackPredicate>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 197 */     return this.parser.parseFuture(commandContext, commandInput);
/*     */   }
/*     */ 
/*     */   
/*     */   public SuggestionProvider<C> suggestionProvider() {
/* 202 */     return this.parser.suggestionProvider();
/*     */   }
/*     */   
/*     */   private static final class ItemStackPredicateImpl
/*     */     implements ItemStackPredicate
/*     */   {
/*     */     private final Predicate<Object> predicate;
/*     */     
/*     */     ItemStackPredicateImpl(Predicate<Object> predicate) {
/* 211 */       this.predicate = predicate;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean test(ItemStack itemStack) {
/*     */       try {
/* 217 */         return this.predicate.test(ItemStackPredicateParser.AS_NMS_COPY_METHOD.invoke(null, new Object[] { itemStack }));
/* 218 */       } catch (ReflectiveOperationException ex) {
/* 219 */         throw new RuntimeException(ex);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\ItemStackPredicateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */