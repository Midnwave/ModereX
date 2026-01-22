/*     */ package ac.grim.grimac.shaded.kyori.adventure.platform.bukkit;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.Facet;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.facet.FacetComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.flattener.ComponentFlattener;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.JSONOptions;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
/*     */ import ac.grim.grimac.shaded.kyori.option.OptionState;
/*     */ import java.util.Collection;
/*     */ import java.util.function.Supplier;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BukkitComponentSerializer
/*     */ {
/*  50 */   private static final boolean IS_1_13 = (MinecraftReflection.findEnum(Material.class, "BLUE_ICE") != null);
/*  51 */   private static final boolean IS_1_16 = (MinecraftReflection.findEnum(Material.class, "NETHERITE_PICKAXE") != null);
/*     */   
/*  53 */   private static final Collection<FacetComponentFlattener.Translator<Server>> TRANSLATORS = Facet.of(new Supplier[] { Translator::new, Translator::new });
/*     */ 
/*     */   
/*     */   private static final LegacyComponentSerializer LEGACY_SERIALIZER;
/*     */ 
/*     */   
/*     */   private static final GsonComponentSerializer GSON_SERIALIZER;
/*     */ 
/*     */   
/*  62 */   static final ComponentFlattener FLATTENER = FacetComponentFlattener.get(Bukkit.getServer(), TRANSLATORS);
/*     */   static {
/*  64 */     if (IS_1_13) {
/*     */ 
/*     */       
/*  67 */       GSON_SERIALIZER = GsonComponentSerializer.builder().options((OptionState)JSONOptions.byDataVersion().at(Bukkit.getUnsafe().getDataVersion())).build();
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  72 */       GSON_SERIALIZER = GsonComponentSerializer.builder().legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get()).options((OptionState)JSONOptions.byDataVersion().at(0)).build();
/*     */     } 
/*     */     
/*  75 */     if (IS_1_16) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  80 */       LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().flattener(FLATTENER).build();
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  85 */       LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('§').flattener(FLATTENER).build();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static LegacyComponentSerializer legacy() {
/*  96 */     return LEGACY_SERIALIZER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static GsonComponentSerializer gson() {
/* 108 */     return GSON_SERIALIZER;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\platform\bukkit\BukkitComponentSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */