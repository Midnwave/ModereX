/*     */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.CommandManager;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.annotation.specifier.AllowEmptySelection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.annotation.specifier.DefaultNamespace;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.annotation.specifier.RequireExplicitNamespace;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.MultipleEntitySelector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.data.MultiplePlayerSelector;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.internal.CraftBukkitReflection;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.BlockPredicateParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.EnchantmentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.ItemStackParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.ItemStackPredicateParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.MaterialParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.NamespacedKeyParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.OfflinePlayerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.PlayerParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.WorldParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location.Location2DParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.location.LocationParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.SingleEntitySelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.bukkit.parser.selector.SinglePlayerSelectorParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ArgumentParser;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.parser.ParserParameters;
/*     */ import java.lang.reflect.Method;
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
/*     */ 
/*     */ @API(status = API.Status.INTERNAL)
/*     */ public final class BukkitParsers
/*     */ {
/*     */   public static <C> void register(CommandManager<C> manager) {
/*  67 */     manager.parserRegistry()
/*  68 */       .registerParser(WorldParser.worldParser())
/*  69 */       .registerParser(MaterialParser.materialParser())
/*  70 */       .registerParser(PlayerParser.playerParser())
/*  71 */       .registerParser(OfflinePlayerParser.offlinePlayerParser())
/*  72 */       .registerParser(EnchantmentParser.enchantmentParser())
/*  73 */       .registerParser(LocationParser.locationParser())
/*  74 */       .registerParser(Location2DParser.location2DParser())
/*  75 */       .registerParser(ItemStackParser.itemStackParser())
/*  76 */       .registerParser(SingleEntitySelectorParser.singleEntitySelectorParser())
/*  77 */       .registerParser(SinglePlayerSelectorParser.singlePlayerSelectorParser());
/*     */ 
/*     */     
/*  80 */     manager.parserRegistry().registerAnnotationMapper(AllowEmptySelection.class, (annotation, type) -> ParserParameters.single(BukkitParserParameters.ALLOW_EMPTY_SELECTOR_RESULT, Boolean.valueOf(annotation.value())));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     manager.parserRegistry().registerParserSupplier(
/*  88 */         TypeToken.get(MultipleEntitySelector.class), parserParameters -> new MultipleEntitySelectorParser(((Boolean)parserParameters.get(BukkitParserParameters.ALLOW_EMPTY_SELECTOR_RESULT, Boolean.valueOf(true))).booleanValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     manager.parserRegistry().registerParserSupplier(
/*  94 */         TypeToken.get(MultiplePlayerSelector.class), parserParameters -> new MultiplePlayerSelectorParser(((Boolean)parserParameters.get(BukkitParserParameters.ALLOW_EMPTY_SELECTOR_RESULT, Boolean.valueOf(true))).booleanValue()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     if (CraftBukkitReflection.classExists("org.bukkit.NamespacedKey")) {
/* 101 */       registerParserSupplierFor(manager, NamespacedKeyParser.class);
/* 102 */       manager.parserRegistry().registerAnnotationMapper(RequireExplicitNamespace.class, (annotation, type) -> ParserParameters.single(BukkitParserParameters.REQUIRE_EXPLICIT_NAMESPACE, Boolean.valueOf(true)));
/*     */ 
/*     */ 
/*     */       
/* 106 */       manager.parserRegistry().registerAnnotationMapper(DefaultNamespace.class, (annotation, type) -> ParserParameters.single(BukkitParserParameters.DEFAULT_NAMESPACE, annotation.value()));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 113 */     if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
/* 114 */       registerParserSupplierFor(manager, ItemStackPredicateParser.class);
/* 115 */       registerParserSupplierFor(manager, BlockPredicateParser.class);
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
/*     */   private static void registerParserSupplierFor(CommandManager<?> manager, Class<?> argumentClass) {
/*     */     try {
/* 130 */       Method registerParserSuppliers = argumentClass.getDeclaredMethod("registerParserSupplier", new Class[] { CommandManager.class });
/* 131 */       registerParserSuppliers.setAccessible(true);
/* 132 */       registerParserSuppliers.invoke(null, new Object[] { manager });
/* 133 */     } catch (ReflectiveOperationException e) {
/* 134 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\BukkitParsers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */