/*     */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component;
/*     */ 
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.color.DyeColor;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.AxolotlVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.CatCollarComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.CatVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.ChickenVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.CowVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.FoxVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.FrogVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.HorseVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.LlamaVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.MooshroomVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.PaintingVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.ParrotVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.PigVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.RabbitVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.SalmonSizeComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.SheepColorComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.ShulkerColorComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.TropicalFishBaseColorComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.TropicalFishPatternColorComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.TropicalFishPatternComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.VillagerVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.WolfCollarComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.WolfSoundVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.WolfVariantComponent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ArmorTrim;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.BannerLayers;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.BundleContents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ChargedProjectiles;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.CustomData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.DebugStickState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.FireworkExplosion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.FoodProperties;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemAdventurePredicate;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemAttributeModifiers;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemBees;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemBlockStateProperties;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemBlocksAttacks;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemBreakSound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemConsumable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemContainerContents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemContainerLoot;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemDamageResistant;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemDeathProtection;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemDyeColor;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEquippable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemFireworks;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemInstrument;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemJukeboxPlayable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLock;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemMapDecorations;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemMapPostProcessingState;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemModel;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemPotionContents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemPotionDurationScale;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProvidesBannerPatterns;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProvidesTrimMaterial;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemRarity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemRecipes;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemRepairable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemTool;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemTooltipDisplay;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemTooltipStyle;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemUnbreakable;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemUseCooldown;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemUseRemainder;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.ItemWeapon;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.LodestoneTracker;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.PotDecorations;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.SuspiciousStewEffects;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.WritableBookContent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.builtin.item.WrittenBookContent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.item.instrument.Instrument;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MaybeMappedEntity;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Dummy;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import java.util.Collection;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ComponentTypes
/*     */ {
/* 121 */   private static final VersionedRegistry<ComponentType<?>> REGISTRY = new VersionedRegistry("data_component_type");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Internal
/*     */   public static <T> ComponentType<T> define(String key) {
/* 128 */     return define(key, null, null);
/*     */   }
/*     */   
/*     */   @Internal
/*     */   public static <T> ComponentType<T> define(String key, @Nullable PacketWrapper.Reader<T> reader, @Nullable PacketWrapper.Writer<T> writer) {
/* 133 */     return (ComponentType<T>)REGISTRY.define(key, data -> new StaticComponentType(data, reader, writer));
/*     */   }
/*     */   
/*     */   public static VersionedRegistry<ComponentType<?>> getRegistry() {
/* 137 */     return REGISTRY;
/*     */   }
/*     */   
/*     */   public static ComponentType<?> getByName(String name) {
/* 141 */     return (ComponentType)REGISTRY.getByName(name);
/*     */   }
/*     */   
/*     */   public static ComponentType<?> getById(ClientVersion version, int id) {
/* 145 */     return (ComponentType)REGISTRY.getById(version, id);
/*     */   }
/*     */   
/* 148 */   public static final ComponentType<NBTCompound> CUSTOM_DATA = define("custom_data", CustomData::read, CustomData::write);
/*     */ 
/*     */ 
/*     */   
/* 152 */   public static final ComponentType<Integer> MAX_STACK_SIZE = define("max_stack_size", PacketWrapper::readVarInt, PacketWrapper::writeVarInt);
/*     */   
/* 154 */   public static final ComponentType<Integer> MAX_DAMAGE = define("max_damage", PacketWrapper::readVarInt, PacketWrapper::writeVarInt);
/*     */   
/* 156 */   public static final ComponentType<Integer> DAMAGE = define("damage", PacketWrapper::readVarInt, PacketWrapper::writeVarInt);
/*     */   
/* 158 */   public static final ComponentType<ItemUnbreakable> UNBREAKABLE_MODERN = define("unbreakable", ItemUnbreakable::read, ItemUnbreakable::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 164 */   public static final ComponentType<Boolean> UNBREAKABLE = UNBREAKABLE_MODERN.legacyMap(ItemUnbreakable::isShowInTooltip, ItemUnbreakable::new);
/*     */   
/* 166 */   public static final ComponentType<Component> CUSTOM_NAME = define("custom_name", PacketWrapper::readComponent, PacketWrapper::writeComponent);
/*     */   
/* 168 */   public static final ComponentType<Component> ITEM_NAME = define("item_name", PacketWrapper::readComponent, PacketWrapper::writeComponent);
/*     */   public static final ComponentType<ItemRarity> RARITY;
/* 170 */   public static final ComponentType<ItemLore> LORE = define("lore", ItemLore::read, ItemLore::write);
/*     */   static {
/* 172 */     RARITY = define("rarity", wrapper -> (ItemRarity)wrapper.readEnum((Enum[])ItemRarity.values()), PacketWrapper::writeEnum);
/*     */   }
/* 174 */   public static final ComponentType<ItemEnchantments> ENCHANTMENTS = define("enchantments", ItemEnchantments::read, ItemEnchantments::write);
/*     */   
/* 176 */   public static final ComponentType<ItemAdventurePredicate> CAN_PLACE_ON = define("can_place_on", ItemAdventurePredicate::read, ItemAdventurePredicate::write);
/*     */   
/* 178 */   public static final ComponentType<ItemAdventurePredicate> CAN_BREAK = define("can_break", ItemAdventurePredicate::read, ItemAdventurePredicate::write);
/*     */   
/* 180 */   public static final ComponentType<ItemAttributeModifiers> ATTRIBUTE_MODIFIERS = define("attribute_modifiers", ItemAttributeModifiers::read, ItemAttributeModifiers::write);
/*     */   
/* 182 */   public static final ComponentType<ItemCustomModelData> CUSTOM_MODEL_DATA_LISTS = define("custom_model_data", ItemCustomModelData::read, ItemCustomModelData::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 188 */   public static final ComponentType<Integer> CUSTOM_MODEL_DATA = CUSTOM_MODEL_DATA_LISTS.legacyMap(ItemCustomModelData::getLegacyId, ItemCustomModelData::new);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/* 194 */   public static final ComponentType<Dummy> HIDE_ADDITIONAL_TOOLTIP = define("hide_additional_tooltip", Dummy::dummyRead, Dummy::dummyWrite);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/* 200 */   public static final ComponentType<Dummy> HIDE_TOOLTIP = define("hide_tooltip", Dummy::dummyRead, Dummy::dummyWrite);
/*     */   
/* 202 */   public static final ComponentType<Integer> REPAIR_COST = define("repair_cost", PacketWrapper::readVarInt, PacketWrapper::writeVarInt);
/*     */   
/* 204 */   public static final ComponentType<Dummy> CREATIVE_SLOT_LOCK = define("creative_slot_lock", Dummy::dummyRead, Dummy::dummyWrite);
/*     */   
/* 206 */   public static final ComponentType<Boolean> ENCHANTMENT_GLINT_OVERRIDE = define("enchantment_glint_override", PacketWrapper::readBoolean, PacketWrapper::writeBoolean);
/*     */   
/* 208 */   public static final ComponentType<Dummy> INTANGIBLE_PROJECTILE = define("intangible_projectile", Dummy::dummyReadNbt, Dummy::dummyWriteNbt);
/*     */   
/* 210 */   public static final ComponentType<FoodProperties> FOOD = define("food", FoodProperties::read, FoodProperties::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Obsolete
/* 216 */   public static final ComponentType<Dummy> FIRE_RESISTANT = define("fire_resistant", Dummy::dummyRead, Dummy::dummyWrite);
/*     */   
/* 218 */   public static final ComponentType<ItemTool> TOOL = define("tool", ItemTool::read, ItemTool::write);
/*     */   
/* 220 */   public static final ComponentType<ItemEnchantments> STORED_ENCHANTMENTS = define("stored_enchantments", ItemEnchantments::read, ItemEnchantments::write);
/*     */   
/* 222 */   public static final ComponentType<ItemDyeColor> DYED_COLOR = define("dyed_color", ItemDyeColor::read, ItemDyeColor::write);
/*     */   
/* 224 */   public static final ComponentType<Integer> MAP_COLOR = define("map_color", PacketWrapper::readInt, PacketWrapper::writeInt);
/*     */   
/* 226 */   public static final ComponentType<Integer> MAP_ID = define("map_id", PacketWrapper::readVarInt, PacketWrapper::writeVarInt);
/*     */   public static final ComponentType<ItemMapPostProcessingState> MAP_POST_PROCESSING;
/* 228 */   public static final ComponentType<ItemMapDecorations> MAP_DECORATIONS = define("map_decorations", ItemMapDecorations::read, ItemMapDecorations::write);
/*     */   static {
/* 230 */     MAP_POST_PROCESSING = define("map_post_processing", wrapper -> (ItemMapPostProcessingState)wrapper.readEnum((Enum[])ItemMapPostProcessingState.values()), PacketWrapper::writeEnum);
/*     */   }
/* 232 */   public static final ComponentType<ChargedProjectiles> CHARGED_PROJECTILES = define("charged_projectiles", ChargedProjectiles::read, ChargedProjectiles::write);
/*     */   
/* 234 */   public static final ComponentType<BundleContents> BUNDLE_CONTENTS = define("bundle_contents", BundleContents::read, BundleContents::write);
/*     */   
/* 236 */   public static final ComponentType<ItemPotionContents> POTION_CONTENTS = define("potion_contents", ItemPotionContents::read, ItemPotionContents::write);
/*     */   
/* 238 */   public static final ComponentType<SuspiciousStewEffects> SUSPICIOUS_STEW_EFFECTS = define("suspicious_stew_effects", SuspiciousStewEffects::read, SuspiciousStewEffects::write);
/*     */   
/* 240 */   public static final ComponentType<WritableBookContent> WRITABLE_BOOK_CONTENT = define("writable_book_content", WritableBookContent::read, WritableBookContent::write);
/*     */   
/* 242 */   public static final ComponentType<WrittenBookContent> WRITTEN_BOOK_CONTENT = define("written_book_content", WrittenBookContent::read, WrittenBookContent::write);
/*     */   
/* 244 */   public static final ComponentType<ArmorTrim> TRIM = define("trim", ArmorTrim::read, ArmorTrim::write);
/*     */   
/* 246 */   public static final ComponentType<DebugStickState> DEBUG_STICK_STATE = define("debug_stick_state", DebugStickState::read, DebugStickState::write);
/*     */   
/* 248 */   public static final ComponentType<NBTCompound> ENTITY_DATA = define("entity_data", PacketWrapper::readNBT, PacketWrapper::writeNBT);
/*     */   
/* 250 */   public static final ComponentType<NBTCompound> BUCKET_ENTITY_DATA = define("bucket_entity_data", PacketWrapper::readNBT, PacketWrapper::writeNBT);
/*     */   
/* 252 */   public static final ComponentType<NBTCompound> BLOCK_ENTITY_DATA = define("block_entity_data", PacketWrapper::readNBT, PacketWrapper::writeNBT);
/*     */   
/* 254 */   public static final ComponentType<ItemInstrument> ITEM_INSTRUMENT = define("instrument", ItemInstrument::read, ItemInstrument::write);
/*     */   
/*     */   static {
/* 257 */     INSTRUMENT = ITEM_INSTRUMENT.legacyMap(inst -> (Instrument)inst.getInstrument().getValue(), inst -> new ItemInstrument(new MaybeMappedEntity((MappedEntity)inst)));
/*     */   }
/*     */   @Deprecated
/* 260 */   public static final ComponentType<Instrument> INSTRUMENT; public static final ComponentType<Integer> OMINOUS_BOTTLE_AMPLIFIER = define("ominous_bottle_amplifier", PacketWrapper::readVarInt, PacketWrapper::writeVarInt);
/*     */   
/* 262 */   public static final ComponentType<ItemRecipes> RECIPES = define("recipes", ItemRecipes::read, ItemRecipes::write);
/*     */   
/* 264 */   public static final ComponentType<LodestoneTracker> LODESTONE_TRACKER = define("lodestone_tracker", LodestoneTracker::read, LodestoneTracker::write);
/*     */   
/* 266 */   public static final ComponentType<FireworkExplosion> FIREWORK_EXPLOSION = define("firework_explosion", FireworkExplosion::read, FireworkExplosion::write);
/*     */   
/* 268 */   public static final ComponentType<ItemFireworks> FIREWORKS = define("fireworks", ItemFireworks::read, ItemFireworks::write);
/*     */   
/* 270 */   public static final ComponentType<ItemProfile> PROFILE = define("profile", ItemProfile::read, ItemProfile::write);
/*     */   
/* 272 */   public static final ComponentType<ResourceLocation> NOTE_BLOCK_SOUND = define("note_block_sound", PacketWrapper::readIdentifier, PacketWrapper::writeIdentifier);
/*     */   
/* 274 */   public static final ComponentType<BannerLayers> BANNER_PATTERNS = define("banner_patterns", BannerLayers::read, BannerLayers::write); public static final ComponentType<DyeColor> BASE_COLOR;
/*     */   static {
/* 276 */     BASE_COLOR = define("base_color", wrapper -> (DyeColor)wrapper.readEnum((Enum[])DyeColor.values()), PacketWrapper::writeEnum);
/*     */   }
/* 278 */   public static final ComponentType<PotDecorations> POT_DECORATIONS = define("pot_decorations", PotDecorations::read, PotDecorations::write);
/*     */   
/* 280 */   public static final ComponentType<ItemContainerContents> CONTAINER = define("container", ItemContainerContents::read, ItemContainerContents::write);
/*     */   
/* 282 */   public static final ComponentType<ItemBlockStateProperties> BLOCK_STATE = define("block_state", ItemBlockStateProperties::read, ItemBlockStateProperties::write);
/*     */   
/* 284 */   public static final ComponentType<ItemBees> BEES = define("bees", ItemBees::read, ItemBees::write);
/*     */   
/* 286 */   public static final ComponentType<ItemLock> LOCK = define("lock", ItemLock::read, ItemLock::write);
/*     */   
/* 288 */   public static final ComponentType<ItemContainerLoot> CONTAINER_LOOT = define("container_loot", ItemContainerLoot::read, ItemContainerLoot::write);
/*     */ 
/*     */ 
/*     */   
/* 292 */   public static final ComponentType<ItemJukeboxPlayable> JUKEBOX_PLAYABLE = define("jukebox_playable", ItemJukeboxPlayable::read, ItemJukeboxPlayable::write);
/*     */ 
/*     */ 
/*     */   
/* 296 */   public static final ComponentType<ItemConsumable> CONSUMABLE = define("consumable", ItemConsumable::read, ItemConsumable::write);
/*     */   
/* 298 */   public static final ComponentType<ItemUseRemainder> USE_REMAINDER = define("use_remainder", ItemUseRemainder::read, ItemUseRemainder::write);
/*     */   
/* 300 */   public static final ComponentType<ItemUseCooldown> USE_COOLDOWN = define("use_cooldown", ItemUseCooldown::read, ItemUseCooldown::write);
/*     */   
/* 302 */   public static final ComponentType<ItemEnchantable> ENCHANTABLE = define("enchantable", ItemEnchantable::read, ItemEnchantable::write);
/*     */   
/* 304 */   public static final ComponentType<ItemRepairable> REPAIRABLE = define("repairable", ItemRepairable::read, ItemRepairable::write);
/*     */   
/* 306 */   public static final ComponentType<ItemModel> ITEM_MODEL = define("item_model", ItemModel::read, ItemModel::write);
/*     */   
/* 308 */   public static final ComponentType<ItemDamageResistant> DAMAGE_RESISTANT = define("damage_resistant", ItemDamageResistant::read, ItemDamageResistant::write);
/*     */   
/* 310 */   public static final ComponentType<ItemEquippable> EQUIPPABLE = define("equippable", ItemEquippable::read, ItemEquippable::write);
/*     */   
/* 312 */   public static final ComponentType<Dummy> GLIDER = define("glider", Dummy::dummyRead, Dummy::dummyWrite);
/*     */   
/* 314 */   public static final ComponentType<ItemDeathProtection> DEATH_PROTECTION = define("death_protection", ItemDeathProtection::read, ItemDeathProtection::write);
/*     */   
/* 316 */   public static final ComponentType<ItemTooltipStyle> TOOLTIP_STYLE = define("tooltip_style", ItemTooltipStyle::read, ItemTooltipStyle::write);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 322 */   public static final ComponentType<ItemTooltipDisplay> TOOLTIP_DISPLAY = define("tooltip_display", ItemTooltipDisplay::read, ItemTooltipDisplay::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 327 */   public static final ComponentType<ItemWeapon> WEAPON = define("weapon", ItemWeapon::read, ItemWeapon::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 332 */   public static final ComponentType<ItemBlocksAttacks> BLOCKS_ATTACKS = define("blocks_attacks", ItemBlocksAttacks::read, ItemBlocksAttacks::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 337 */   public static final ComponentType<ItemPotionDurationScale> POTION_DURATION_SCALE = define("potion_duration_scale", ItemPotionDurationScale::read, ItemPotionDurationScale::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 342 */   public static final ComponentType<ItemProvidesTrimMaterial> PROVIDES_TRIM_MATERIAL = define("provides_trim_material", ItemProvidesTrimMaterial::read, ItemProvidesTrimMaterial::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 347 */   public static final ComponentType<ItemProvidesBannerPatterns> PROVIDES_BANNER_PATTERNS = define("provides_banner_patterns", ItemProvidesBannerPatterns::read, ItemProvidesBannerPatterns::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 352 */   public static final ComponentType<ItemBreakSound> BREAK_SOUND = define("break_sound", ItemBreakSound::read, ItemBreakSound::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 357 */   public static final ComponentType<VillagerVariantComponent> VILLAGER_VARIANT = define("villager/variant", VillagerVariantComponent::read, VillagerVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 362 */   public static final ComponentType<WolfVariantComponent> WOLF_VARIANT = define("wolf/variant", WolfVariantComponent::read, WolfVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 367 */   public static final ComponentType<WolfSoundVariantComponent> WOLF_SOUND_VARIANT = define("wolf/sound_variant", WolfSoundVariantComponent::read, WolfSoundVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 372 */   public static final ComponentType<WolfCollarComponent> WOLF_COLLAR = define("wolf/collar", WolfCollarComponent::read, WolfCollarComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 377 */   public static final ComponentType<FoxVariantComponent> FOX_VARIANT = define("fox/variant", FoxVariantComponent::read, FoxVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 382 */   public static final ComponentType<SalmonSizeComponent> SALMON_SIZE = define("salmon/size", SalmonSizeComponent::read, SalmonSizeComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 387 */   public static final ComponentType<ParrotVariantComponent> PARROT_VARIANT = define("parrot/variant", ParrotVariantComponent::read, ParrotVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 392 */   public static final ComponentType<TropicalFishPatternComponent> TROPICAL_FISH_PATTERN = define("tropical_fish/pattern", TropicalFishPatternComponent::read, TropicalFishPatternComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 397 */   public static final ComponentType<TropicalFishBaseColorComponent> TROPICAL_FISH_BASE_COLOR = define("tropical_fish/base_color", TropicalFishBaseColorComponent::read, TropicalFishBaseColorComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 402 */   public static final ComponentType<TropicalFishPatternColorComponent> TROPICAL_FISH_PATTERN_COLOR = define("tropical_fish/pattern_color", TropicalFishPatternColorComponent::read, TropicalFishPatternColorComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 407 */   public static final ComponentType<MooshroomVariantComponent> MOOSHROOM_VARIANT = define("mooshroom/variant", MooshroomVariantComponent::read, MooshroomVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 412 */   public static final ComponentType<RabbitVariantComponent> RABBIT_VARIANT = define("rabbit/variant", RabbitVariantComponent::read, RabbitVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 417 */   public static final ComponentType<PigVariantComponent> PIG_VARIANT = define("pig/variant", PigVariantComponent::read, PigVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 422 */   public static final ComponentType<CowVariantComponent> COW_VARIANT = define("cow/variant", CowVariantComponent::read, CowVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 427 */   public static final ComponentType<ChickenVariantComponent> CHICKEN_VARIANT = define("chicken/variant", ChickenVariantComponent::read, ChickenVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 432 */   public static final ComponentType<FrogVariantComponent> FROG_VARIANT = define("frog/variant", FrogVariantComponent::read, FrogVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 437 */   public static final ComponentType<HorseVariantComponent> HORSE_VARIANT = define("horse/variant", HorseVariantComponent::read, HorseVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 442 */   public static final ComponentType<PaintingVariantComponent> PAINTING_VARIANT = define("painting/variant", PaintingVariantComponent::read, PaintingVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 447 */   public static final ComponentType<LlamaVariantComponent> LLAMA_VARIANT = define("llama/variant", LlamaVariantComponent::read, LlamaVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 452 */   public static final ComponentType<AxolotlVariantComponent> AXOLOTL_VARIANT = define("axolotl/variant", AxolotlVariantComponent::read, AxolotlVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 457 */   public static final ComponentType<CatVariantComponent> CAT_VARIANT = define("cat/variant", CatVariantComponent::read, CatVariantComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 462 */   public static final ComponentType<CatCollarComponent> CAT_COLLAR = define("cat/collar", CatCollarComponent::read, CatCollarComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 467 */   public static final ComponentType<SheepColorComponent> SHEEP_COLOR = define("sheep/color", SheepColorComponent::read, SheepColorComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 472 */   public static final ComponentType<ShulkerColorComponent> SHULKER_COLOR = define("shulker/color", ShulkerColorComponent::read, ShulkerColorComponent::write);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<ComponentType<?>> values() {
/* 481 */     return REGISTRY.getEntries();
/*     */   }
/*     */   
/*     */   static {
/* 485 */     REGISTRY.unloadMappings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\ComponentTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */