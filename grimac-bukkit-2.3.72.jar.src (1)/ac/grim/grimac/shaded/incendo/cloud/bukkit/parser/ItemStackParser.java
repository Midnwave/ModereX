/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.parser.WrappedBrigadierParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.ProtoItemStack;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CommandBuildContextSupplier;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.MinecraftArgumentTypes;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.component.CommandComponent;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandInput;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParseResult;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserDescriptor;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.BlockingSuggestionProvider;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.suggestion.SuggestionProvider;
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Material;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ItemStackParser<C>
/*     */   implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack>
/*     */ {
/*     */   private final ArgumentParser<C, ProtoItemStack> parser;
/*     */   
/*     */   @API(status = API.Status.STABLE, since = "2.0.0")
/*     */   public static <C> ParserDescriptor<C, ProtoItemStack> itemStackParser() {
/*  83 */     return ParserDescriptor.of((ArgumentParser)new ItemStackParser(), ProtoItemStack.class);
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
/*     */   public static <C> CommandComponent.Builder<C, ProtoItemStack> itemStackComponent() {
/*  95 */     return CommandComponent.builder().parser(itemStackParser());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<?> findItemInputClass() {
/* 104 */     Class<?>[] classes = new Class[] { CraftBukkitReflection.findNMSClass("ArgumentPredicateItemStack"), CraftBukkitReflection.findMCClass("commands.arguments.item.ArgumentPredicateItemStack"), CraftBukkitReflection.findMCClass("commands.arguments.item.ItemInput") };
/*     */     
/* 106 */     for (Class<?> clazz : classes) {
/* 107 */       if (clazz != null) {
/* 108 */         return clazz;
/*     */       }
/*     */     } 
/* 111 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemStackParser() {
/* 120 */     if (findItemInputClass() != null) {
/* 121 */       this.parser = (ArgumentParser<C, ProtoItemStack>)new ModernParser();
/*     */     } else {
/* 123 */       this.parser = (ArgumentParser<C, ProtoItemStack>)new LegacyParser();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final CompletableFuture<ArgumentParseResult<ProtoItemStack>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 132 */     return this.parser.parseFuture(commandContext, commandInput);
/*     */   }
/*     */ 
/*     */   
/*     */   public final SuggestionProvider<C> suggestionProvider() {
/* 137 */     return this.parser.suggestionProvider();
/*     */   }
/*     */   
/*     */   private static final class ModernParser<C>
/*     */     implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack>
/*     */   {
/* 143 */     private static final Class<?> NMS_ITEM_STACK_CLASS = CraftBukkitReflection.needNMSClassOrElse("ItemStack", new String[] { "net.minecraft.world.item.ItemStack" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     private static final Class<?> CRAFT_ITEM_STACK_CLASS = CraftBukkitReflection.needOBCClass("inventory.CraftItemStack");
/*     */     
/* 150 */     private static final Supplier<Class<?>> ARGUMENT_ITEM_STACK_CLASS = (Supplier<Class<?>>)Suppliers.memoize(() -> MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("item_stack")));
/* 151 */     private static final Class<?> ITEM_INPUT_CLASS = Objects.<Class<?>>requireNonNull(ItemStackParser.findItemInputClass(), "ItemInput class");
/* 152 */     private static final Class<?> NMS_ITEM_CLASS = CraftBukkitReflection.needNMSClassOrElse("Item", new String[] { "net.minecraft.world.item.Item" });
/*     */ 
/*     */ 
/*     */     
/* 156 */     private static final Supplier<Method> GET_MATERIAL_METHOD = (Supplier<Method>)Suppliers.memoize(() -> CraftBukkitReflection.needMethod(CraftBukkitReflection.needOBCClass("util.CraftMagicNumbers"), "getMaterial", new Class[] { NMS_ITEM_CLASS }));
/*     */     
/* 158 */     private static final Method CREATE_ITEM_STACK_METHOD = (Method)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find createItemStack method on ItemInput", (Object[])new Method[] {
/*     */           
/* 160 */           CraftBukkitReflection.findMethod(ITEM_INPUT_CLASS, "a", new Class[] { int.class, boolean.class
/* 161 */             }), CraftBukkitReflection.findMethod(ITEM_INPUT_CLASS, "createItemStack", new Class[] { int.class, boolean.class })
/*     */         });
/*     */     
/* 164 */     private static final Method AS_BUKKIT_COPY_METHOD = CraftBukkitReflection.needMethod(CRAFT_ITEM_STACK_CLASS, "asBukkitCopy", new Class[] { NMS_ITEM_STACK_CLASS });
/* 165 */     private static final Field ITEM_FIELD = (Field)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find item field on ItemInput", (Object[])new Field[] {
/*     */           
/* 167 */           CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "b"), 
/* 168 */           CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "item")
/*     */         });
/* 170 */     private static final Field EXTRA_DATA_FIELD = (Field)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find tag field on ItemInput", (Object[])new Field[] {
/*     */           
/* 172 */           CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "c"), 
/* 173 */           CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "tag"), 
/* 174 */           CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "components")
/*     */         });
/* 176 */     private static final Class<?> HOLDER_CLASS = CraftBukkitReflection.findMCClass("core.Holder");
/* 177 */     private static final Method VALUE_METHOD = (HOLDER_CLASS == null) ? 
/* 178 */       null : 
/* 179 */       (Method)CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find Holder#value", (Object[])new Method[] {
/*     */           
/* 181 */           CraftBukkitReflection.findMethod(HOLDER_CLASS, "value", new Class[0]), 
/* 182 */           CraftBukkitReflection.findMethod(HOLDER_CLASS, "a", new Class[0])
/*     */         });
/* 184 */     private static final Class<?> NBT_TAG_CLASS = (Class)CraftBukkitReflection.firstNonNullOrThrow(() -> "Cloud not find net.minecraft.nbt.Tag", (Object[])new Class[] {
/*     */           
/* 186 */           CraftBukkitReflection.findClass("net.minecraft.nbt.Tag"), 
/* 187 */           CraftBukkitReflection.findClass("net.minecraft.nbt.NBTBase"), 
/* 188 */           CraftBukkitReflection.findNMSClass("NBTBase")
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 194 */     private final ArgumentParser<C, ProtoItemStack> parser = createParser();
/*     */ 
/*     */ 
/*     */     
/*     */     private ArgumentParser<C, ProtoItemStack> createParser() {
/* 199 */       Supplier<ArgumentType<Object>> inst = () -> {
/*     */           Constructor<?> ctr = ((Class)ARGUMENT_ITEM_STACK_CLASS.get()).getDeclaredConstructors()[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           try {
/*     */             return (ctr.getParameterCount() == 0) ? (ArgumentType)ctr.newInstance(new Object[0]) : (ArgumentType)ctr.newInstance(new Object[] { CommandBuildContextSupplier.commandBuildContext() });
/* 208 */           } catch (ReflectiveOperationException e) {
/*     */             throw new RuntimeException("Failed to initialize modern ItemStack parser.", e);
/*     */           } 
/*     */         };
/* 212 */       return (ArgumentParser<C, ProtoItemStack>)(new WrappedBrigadierParser(inst))
/* 213 */         .flatMapSuccess((ctx, itemInput) -> ArgumentParseResult.successFuture(new ModernProtoItemStack(itemInput)));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompletableFuture<ArgumentParseResult<ProtoItemStack>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 223 */       return this.parser.parseFuture(commandContext, commandInput);
/*     */     }
/*     */ 
/*     */     
/*     */     public SuggestionProvider<C> suggestionProvider() {
/* 228 */       return this.parser.suggestionProvider();
/*     */     }
/*     */ 
/*     */     
/*     */     private static final class ModernProtoItemStack
/*     */       implements ProtoItemStack
/*     */     {
/*     */       private final Object itemInput;
/*     */ 
/*     */       
/*     */       ModernProtoItemStack(Object itemInput) {
/* 239 */         this.itemInput = itemInput;
/*     */         try {
/* 241 */           Object item = ItemStackParser.ModernParser.ITEM_FIELD.get(itemInput);
/* 242 */           if (ItemStackParser.ModernParser.HOLDER_CLASS != null && ItemStackParser.ModernParser.HOLDER_CLASS.isInstance(item)) {
/* 243 */             item = ItemStackParser.ModernParser.VALUE_METHOD.invoke(item, new Object[0]);
/*     */           }
/* 245 */           this.material = (Material)((Method)ItemStackParser.ModernParser.GET_MATERIAL_METHOD.get()).invoke(null, new Object[] { item });
/* 246 */           Object extraData = ItemStackParser.ModernParser.EXTRA_DATA_FIELD.get(itemInput);
/* 247 */           if (ItemStackParser.ModernParser.NBT_TAG_CLASS.isInstance(extraData) || extraData == null) {
/* 248 */             this.hasExtraData = (extraData != null);
/*     */           }
/*     */           else {
/*     */             
/* 252 */             List<Method> isEmptyMethod = (List<Method>)Arrays.<Method>stream(extraData.getClass().getMethods()).filter(it -> (it.getParameterCount() == 0 && it.getReturnType().equals(boolean.class))).collect(Collectors.toList());
/* 253 */             if (isEmptyMethod.size() != 1) {
/* 254 */               throw new IllegalStateException("Failed to locate DataComponentMap/Patch#isEmpty; size=" + isEmptyMethod
/* 255 */                   .size());
/*     */             }
/* 257 */             this.hasExtraData = !((Boolean)((Method)isEmptyMethod.get(0)).invoke(extraData, new Object[0])).booleanValue();
/*     */           } 
/* 259 */         } catch (ReflectiveOperationException ex) {
/* 260 */           throw new RuntimeException(ex);
/*     */         } 
/*     */       }
/*     */       private final Material material; private final boolean hasExtraData;
/*     */       
/*     */       public Material material() {
/* 266 */         return this.material;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean hasExtraData() {
/* 271 */         return this.hasExtraData;
/*     */       }
/*     */ 
/*     */       
/*     */       public ItemStack createItemStack(int stackSize, boolean respectMaximumStackSize) {
/*     */         try {
/* 277 */           return (ItemStack)ItemStackParser.ModernParser.AS_BUKKIT_COPY_METHOD.invoke(null, new Object[] {
/*     */                 
/* 279 */                 ItemStackParser.ModernParser.access$800().invoke(this.itemInput, new Object[] { Integer.valueOf(stackSize), Boolean.valueOf(respectMaximumStackSize) })
/*     */               });
/* 281 */         } catch (InvocationTargetException ex) {
/* 282 */           Throwable cause = ex.getCause();
/* 283 */           if (cause instanceof com.mojang.brigadier.exceptions.CommandSyntaxException) {
/* 284 */             throw new IllegalArgumentException(cause.getMessage(), cause);
/*     */           }
/* 286 */           throw new RuntimeException(ex);
/* 287 */         } catch (ReflectiveOperationException e) {
/* 288 */           throw new RuntimeException(e);
/*     */         } 
/*     */       } }
/*     */   }
/*     */   
/*     */   private static final class LegacyParser<C> implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack>, BlockingSuggestionProvider.Strings<C> {
/*     */     private final ArgumentParser<C, ProtoItemStack> parser;
/*     */     
/*     */     private LegacyParser() {
/* 297 */       this
/* 298 */         .parser = (ArgumentParser<C, ProtoItemStack>)(new MaterialParser()).mapSuccess((ctx, material) -> CompletableFuture.completedFuture(new LegacyProtoItemStack(material)));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CompletableFuture<ArgumentParseResult<ProtoItemStack>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
/* 305 */       return this.parser.parseFuture(commandContext, commandInput);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Iterable<String> stringSuggestions(CommandContext<C> commandContext, CommandInput input) {
/* 311 */       return (Iterable<String>)Arrays.<Material>stream(Material.values())
/* 312 */         .filter(Material::isItem)
/* 313 */         .map(value -> value.name().toLowerCase(Locale.ROOT))
/* 314 */         .collect(Collectors.toList());
/*     */     }
/*     */     
/*     */     private static final class LegacyProtoItemStack
/*     */       implements ProtoItemStack {
/*     */       private final Material material;
/*     */       
/*     */       private LegacyProtoItemStack(Material material) {
/* 322 */         this.material = material;
/*     */       }
/*     */ 
/*     */       
/*     */       public Material material() {
/* 327 */         return this.material;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean hasExtraData() {
/* 332 */         return false;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public ItemStack createItemStack(int stackSize, boolean respectMaximumStackSize) throws IllegalArgumentException {
/* 338 */         if (respectMaximumStackSize && stackSize > this.material.getMaxStackSize()) {
/* 339 */           throw new IllegalArgumentException(String.format("The maximum stack size for %s is %d", new Object[] { this.material, 
/*     */ 
/*     */                   
/* 342 */                   Integer.valueOf(this.material.getMaxStackSize()) }));
/*     */         }
/*     */         
/* 345 */         return new ItemStack(this.material, stackSize);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\parser\ItemStackParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */