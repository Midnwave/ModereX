/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit.internal;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.CloudBrigadierManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.brigadier.argument.BrigadierMappingBuilder;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.BlockPredicateParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.EnchantmentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.ItemStackParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.ItemStackPredicateParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.NamespacedKeyParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location.Location2DParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location.LocationParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.SingleEntitySelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.SinglePlayerSelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.standard.UUIDParser;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.mojang.brigadier.arguments.ArgumentType;
/*     */ import com.mojang.brigadier.arguments.StringArgumentType;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.apiguardian.api.API;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.NamespacedKey;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.INTERNAL)
/*     */ public final class BukkitBrigadierMapper<C>
/*     */ {
/*     */   private final Logger logger;
/*     */   private final CloudBrigadierManager<C, ?> brigadierManager;
/*     */   
/*     */   public BukkitBrigadierMapper(Logger logger, CloudBrigadierManager<C, ?> brigadierManager) {
/*  74 */     this.logger = logger;
/*  75 */     this.brigadierManager = brigadierManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerBuiltInMappings() {
/*  82 */     registerUUID();
/*     */     
/*  84 */     mapSimpleNMS(new TypeToken<NamespacedKeyParser<C>>() {  }, "resource_location", true);
/*  85 */     registerEnchantment();
/*     */     
/*  87 */     mapSimpleNMS(new TypeToken<ItemStackParser<C>>() {  }, "item_stack");
/*  88 */     mapSimpleNMS(new TypeToken<ItemStackPredicateParser<C>>() {  }, "item_predicate");
/*     */     
/*  90 */     mapSimpleNMS(new TypeToken<BlockPredicateParser<C>>() {  }, "block_predicate");
/*     */     
/*  92 */     mapSelector(new TypeToken<SingleEntitySelectorParser<C>>() {  }, true, false);
/*  93 */     mapSelector(new TypeToken<SinglePlayerSelectorParser<C>>() {  }, true, true);
/*  94 */     mapSelector(new TypeToken<MultipleEntitySelectorParser<C>>() {  }, false, false);
/*  95 */     mapSelector(new TypeToken<MultiplePlayerSelectorParser<C>>() {  }, false, true);
/*     */     
/*  97 */     mapNMS(new TypeToken<LocationParser<C>>() {  }, "vec3", this::argumentVec3);
/*     */     
/*  99 */     mapNMS(new TypeToken<Location2DParser<C>>() {  }, "vec2", this::argumentVec2);
/*     */   } @API(status = API.Status.INTERNAL)
/*     */   @FunctionalInterface
/*     */   public static interface ArgumentTypeFactory {
/*     */     ArgumentType<?> makeInstance(Class<? extends ArgumentType<?>> param1Class) throws ReflectiveOperationException; } private void registerEnchantment() {
/* 104 */     if (Bukkit.getServer() == null) {
/*     */       
/* 106 */       mapResourceKey(new TypeToken<EnchantmentParser<C>>() {  }, "enchantment");
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*     */     try {
/* 113 */       Class<? extends ArgumentType<?>> ench = MinecraftArgumentTypes.getClassByKey(
/* 114 */           NamespacedKey.minecraft("item_enchantment"));
/* 115 */       mapSimpleNMS(new TypeToken<EnchantmentParser<C>>() {  }, "item_enchantment");
/* 116 */     } catch (IllegalArgumentException ignore) {
/*     */       
/* 118 */       mapResourceKey(new TypeToken<EnchantmentParser<C>>() {  }, "enchantment");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void registerUUID() {
/* 124 */     if (Bukkit.getServer() == null) {
/*     */       
/* 126 */       mapSimpleNMS(new TypeToken<UUIDParser<C>>() {  }, "uuid");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*     */     try {
/* 132 */       Class<? extends ArgumentType<?>> uuid = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft("uuid"));
/*     */       
/* 134 */       mapSimpleNMS(new TypeToken<UUIDParser<C>>() {  }, "uuid");
/* 135 */     } catch (IllegalArgumentException illegalArgumentException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T extends ArgumentParser<C, ?>> void mapResourceKey(TypeToken<T> parserType, String registryName) {
/* 144 */     mapNMS(parserType, "resource_key", type -> (ArgumentType)type.getDeclaredConstructors()[0].newInstance(new Object[] { RegistryReflection.registryKey(registryName) }));
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
/*     */   private <T extends ArgumentParser<C, ?>> void mapSelector(TypeToken<T> parserType, boolean single, boolean playersOnly) {
/* 159 */     mapNMS(parserType, "entity", argumentTypeCls -> {
/*     */           Constructor<?> constructor = argumentTypeCls.getDeclaredConstructors()[0];
/*     */           constructor.setAccessible(true);
/*     */           return (ArgumentType)constructor.newInstance(new Object[] { Boolean.valueOf(single), Boolean.valueOf(playersOnly) });
/*     */         });
/*     */   }
/*     */   
/*     */   private ArgumentType<?> argumentVec3(Class<? extends ArgumentType<?>> type) throws ReflectiveOperationException {
/* 167 */     return type.getDeclaredConstructor(new Class[] { boolean.class }).newInstance(new Object[] { Boolean.valueOf(true) });
/*     */   }
/*     */   
/*     */   private ArgumentType<?> argumentVec2(Class<? extends ArgumentType<?>> type) throws ReflectiveOperationException {
/* 171 */     return type.getDeclaredConstructor(new Class[] { boolean.class }).newInstance(new Object[] { Boolean.valueOf(true) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends ArgumentParser<C, ?>> void mapSimpleNMS(TypeToken<T> type, String argumentId) {
/* 182 */     mapSimpleNMS(type, argumentId, false);
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
/*     */   public <T extends ArgumentParser<C, ?>> void mapSimpleNMS(TypeToken<T> type, String argumentId, boolean useCloudSuggestions) {
/* 203 */     mapNMS(type, argumentId, cls -> { Constructor<?> ctr = cls.getDeclaredConstructors()[0]; (new Object[1])[0] = CommandBuildContextSupplier.commandBuildContext(); Object[] args = (ctr.getParameterCount() == 1) ? new Object[1] : new Object[0]; return (ArgumentType)ctr.newInstance(args); }useCloudSuggestions);
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
/*     */   public <T extends ArgumentParser<C, ?>> void mapNMS(TypeToken<T> type, String argumentId, ArgumentTypeFactory factory) {
/* 226 */     mapNMS(type, argumentId, factory, false);
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
/*     */   public <T extends ArgumentParser<C, ?>> void mapNMS(TypeToken<T> type, String argumentId, ArgumentTypeFactory factory, boolean cloudSuggestions) {
/* 244 */     Supplier supplier = Suppliers.memoize(() -> {
/*     */           try {
/*     */             return MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft(argumentId));
/* 247 */           } catch (Exception e) {
/*     */             throw new RuntimeException("Failed to locate class for " + argumentId, e);
/*     */           } 
/*     */         });
/* 251 */     this.brigadierManager.registerMapping(type, builder -> {
/*     */           builder.to(());
/*     */           if (cloudSuggestions)
/*     */             builder.cloudSuggestions(); 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\internal\BukkitBrigadierMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */