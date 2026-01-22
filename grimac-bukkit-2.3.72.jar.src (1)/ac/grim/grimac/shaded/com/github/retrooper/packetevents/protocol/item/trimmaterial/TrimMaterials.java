/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.trimmaterial;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.armormaterial.ArmorMaterial;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.armormaterial.ArmorMaterials;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.TranslatableComponent;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TrimMaterials
/*     */ {
/*  40 */   private static final VersionedRegistry<TrimMaterial> REGISTRY = new VersionedRegistry("trim_material");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static TrimMaterial define(String key, ItemType ingredient, float itemModelIndex, int color) {
/*  48 */     Map<ArmorMaterial, String> overrideArmorMaterials = new HashMap<>(2);
/*  49 */     String armorMaterialId = ResourceLocation.minecraft(key).toString();
/*  50 */     ArmorMaterial armorMaterial = ArmorMaterials.getByName(armorMaterialId);
/*  51 */     if (armorMaterial != null) {
/*  52 */       overrideArmorMaterials.put(armorMaterial, key + "_darker");
/*     */     }
/*     */     
/*  55 */     TranslatableComponent translatableComponent = Component.translatable("trim_material.minecraft." + key, TextColor.color(color));
/*  56 */     return define(key, key, ingredient, itemModelIndex, overrideArmorMaterials, (Component)translatableComponent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static TrimMaterial define(String key, String assetName, ItemType ingredient, float itemModelIndex, Map<ArmorMaterial, String> overrideArmorMaterials, Component description) {
/*  64 */     return (TrimMaterial)REGISTRY.define(key, data -> new StaticTrimMaterial(data, assetName, ingredient, itemModelIndex, overrideArmorMaterials, description));
/*     */   }
/*     */ 
/*     */   
/*     */   public static VersionedRegistry<TrimMaterial> getRegistry() {
/*  69 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static TrimMaterial getByName(String name) {
/*  73 */     return (TrimMaterial)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static TrimMaterial getById(ClientVersion version, int id) {
/*  77 */     return (TrimMaterial)REGISTRY.getById(version, id);
/*     */   }
/*     */ 
/*     */   
/*  81 */   public static final TrimMaterial AMETHYST = define("amethyst", ItemTypes.AMETHYST_SHARD, 1.0F, 10116294);
/*  82 */   public static final TrimMaterial COPPER = define("copper", ItemTypes.COPPER_INGOT, 0.5F, 11823181);
/*  83 */   public static final TrimMaterial DIAMOND = define("diamond", ItemTypes.DIAMOND, 0.8F, 7269586);
/*  84 */   public static final TrimMaterial EMERALD = define("emerald", ItemTypes.EMERALD, 0.7F, 1155126);
/*  85 */   public static final TrimMaterial GOLD = define("gold", ItemTypes.GOLD_INGOT, 0.6F, 14594349);
/*  86 */   public static final TrimMaterial IRON = define("iron", ItemTypes.IRON_INGOT, 0.2F, 15527148);
/*  87 */   public static final TrimMaterial LAPIS = define("lapis", ItemTypes.LAPIS_LAZULI, 0.9F, 4288151);
/*  88 */   public static final TrimMaterial NETHERITE = define("netherite", ItemTypes.NETHERITE_INGOT, 0.3F, 6445145);
/*  89 */   public static final TrimMaterial QUARTZ = define("quartz", ItemTypes.QUARTZ, 0.1F, 14931140);
/*  90 */   public static final TrimMaterial REDSTONE = define("redstone", ItemTypes.REDSTONE, 0.4F, 9901575);
/*     */ 
/*     */   
/*  93 */   public static final TrimMaterial RESIN = define("resin", ItemTypes.RESIN_BRICK, 0.11F, 16545810);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<TrimMaterial> values() {
/* 101 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 105 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\item\trimmaterial\TrimMaterials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */