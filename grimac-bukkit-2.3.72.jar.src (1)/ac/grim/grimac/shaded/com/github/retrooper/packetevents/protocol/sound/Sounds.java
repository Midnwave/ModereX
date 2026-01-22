/*      */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.sound;
/*      */ 
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*      */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*      */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*      */ import java.util.Collection;
/*      */ import org.jspecify.annotations.NullMarked;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @NullMarked
/*      */ public final class Sounds
/*      */ {
/*   33 */   private static final VersionedRegistry<Sound> REGISTRY = new VersionedRegistry("sound_event");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static VersionedRegistry<Sound> getRegistry() {
/*   39 */     return REGISTRY;
/*      */   }
/*      */   
/*      */   @Internal
/*      */   public static Sound define(String name) {
/*   44 */     return define(name, new ResourceLocation(name), null);
/*      */   }
/*      */   
/*      */   @Internal
/*      */   public static Sound define(String name, ResourceLocation soundId, @Nullable Float range) {
/*   49 */     return (Sound)REGISTRY.define(name, data -> new StaticSound(data, soundId, range));
/*      */   }
/*      */   
/*      */   public static Sound getByNameOrCreate(String name) {
/*   53 */     Sound builtinSound = getByName(name);
/*   54 */     if (builtinSound == null) {
/*   55 */       return new StaticSound(new ResourceLocation(name), null);
/*      */     }
/*   57 */     return builtinSound;
/*      */   }
/*      */   @Nullable
/*      */   public static Sound getByName(String name) {
/*   61 */     return (Sound)REGISTRY.getByName(name);
/*      */   }
/*      */   @Nullable
/*      */   public static Sound getById(ClientVersion version, int id) {
/*   65 */     return (Sound)REGISTRY.getById(version, id);
/*      */   }
/*      */   
/*      */   @Obsolete
/*   69 */   public static final Sound ENTITY_ZOMBIE_PIGMAN_HURT = define("entity.zombie_pigman.hurt");
/*      */   @Obsolete
/*   71 */   public static final Sound ENTITY_PARROT_IMITATE_POLAR_BEAR = define("entity.parrot.imitate.polar_bear");
/*      */   @Obsolete
/*   73 */   public static final Sound ENTITY_PARROT_IMITATE_PANDA = define("entity.parrot.imitate.panda");
/*      */   @Obsolete
/*   75 */   public static final Sound MUSIC_NETHER = define("music.nether");
/*      */   @Obsolete
/*   77 */   public static final Sound BLOCK_SWEET_BERRY_BUSH_PICK_FROM_BUSH = define("block.sweet_berry_bush.pick_from_bush");
/*      */   @Obsolete
/*   79 */   public static final Sound ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN = define("entity.parrot.imitate.zombie_pigman");
/*      */   @Obsolete
/*   81 */   public static final Sound ENTITY_ZOMBIE_PIGMAN_AMBIENT = define("entity.zombie_pigman.ambient");
/*      */   @Obsolete
/*   83 */   public static final Sound ENTITY_ZOMBIE_PIGMAN_DEATH = define("entity.zombie_pigman.death");
/*      */   @Obsolete
/*   85 */   public static final Sound ENTITY_PARROT_IMITATE_WOLF = define("entity.parrot.imitate.wolf");
/*      */   @Obsolete
/*   87 */   public static final Sound ENTITY_PARROT_IMITATE_ENDERMAN = define("entity.parrot.imitate.enderman");
/*      */   @Obsolete
/*   89 */   public static final Sound ENTITY_ZOMBIE_PIGMAN_ANGRY = define("entity.zombie_pigman.angry");
/*      */   @Obsolete
/*   91 */   public static final Sound BLOCK_TRIAL_SPAWNER_AMBIENT_CHARGED = define("block.trial_spawner.ambient_charged");
/*      */   @Obsolete
/*   93 */   public static final Sound BLOCK_TRIAL_SPAWNER_CHARGE_ACTIVATE = define("block.trial_spawner.charge_activate");
/*      */   @Obsolete
/*   95 */   public static final Sound ENTITY_ENDERDRAGON_FIREBALL_EXPLODE = define("entity.enderdragon_fireball.explode");
/*      */   @Obsolete
/*   97 */   public static final Sound BLOCK_ENDERCHEST_OPEN = define("block.enderchest.open");
/*      */   @Obsolete
/*   99 */   public static final Sound ENTITY_ENDERDRAGON_AMBIENT = define("entity.enderdragon.ambient");
/*      */   @Obsolete
/*  101 */   public static final Sound ENTITY_FIREWORK_LARGE_BLAST_FAR = define("entity.firework.large_blast_far");
/*      */   @Obsolete
/*  103 */   public static final Sound ENTITY_ENDERPEARL_THROW = define("entity.enderpearl.throw");
/*      */   @Obsolete
/*  105 */   public static final Sound RECORD_FAR = define("record.far");
/*      */   @Obsolete
/*  107 */   public static final Sound ENTITY_ENDEREYE_LAUNCH = define("entity.endereye.launch");
/*      */   @Obsolete
/*  109 */   public static final Sound BLOCK_CLOTH_HIT = define("block.cloth.hit");
/*      */   @Obsolete
/*  111 */   public static final Sound ENTITY_SNOWMAN_DEATH = define("entity.snowman.death");
/*      */   @Obsolete
/*  113 */   public static final Sound ENTITY_ARMORSTAND_BREAK = define("entity.armorstand.break");
/*      */   @Obsolete
/*  115 */   public static final Sound RECORD_STRAD = define("record.strad");
/*      */   @Obsolete
/*  117 */   public static final Sound BLOCK_CLOTH_BREAK = define("block.cloth.break");
/*      */   @Obsolete
/*  119 */   public static final Sound BLOCK_NOTE_SNARE = define("block.note.snare");
/*      */   @Obsolete
/*  121 */   public static final Sound BLOCK_WOOD_BUTTON_CLICK_OFF = define("block.wood_button.click_off");
/*      */   @Obsolete
/*  123 */   public static final Sound ENTITY_LEASHKNOT_BREAK = define("entity.leashknot.break");
/*      */   @Obsolete
/*  125 */   public static final Sound ENTITY_MAGMACUBE_HURT = define("entity.magmacube.hurt");
/*      */   @Obsolete
/*  127 */   public static final Sound ENTITY_SNOWMAN_AMBIENT = define("entity.snowman.ambient");
/*      */   @Obsolete
/*  129 */   public static final Sound ENTITY_ARMORSTAND_HIT = define("entity.armorstand.hit");
/*      */   @Obsolete
/*  131 */   public static final Sound ENTITY_MAGMACUBE_JUMP = define("entity.magmacube.jump");
/*      */   @Obsolete
/*  133 */   public static final Sound ENTITY_ZOMBIE_PIG_DEATH = define("entity.zombie_pig.death");
/*      */   @Obsolete
/*  135 */   public static final Sound ENTITY_ZOMBIE_ATTACK_DOOR_WOOD = define("entity.zombie.attack_door_wood");
/*      */   @Obsolete
/*  137 */   public static final Sound BLOCK_NOTE_HARP = define("block.note.harp");
/*      */   @Obsolete
/*  139 */   public static final Sound RECORD_BLOCKS = define("record.blocks");
/*      */   @Obsolete
/*  141 */   public static final Sound BLOCK_CLOTH_STEP = define("block.cloth.step");
/*      */   @Obsolete
/*  143 */   public static final Sound ENTITY_FIREWORK_SHOOT = define("entity.firework.shoot");
/*      */   @Obsolete
/*  145 */   public static final Sound ENTITY_ENDERDRAGON_FLAP = define("entity.enderdragon.flap");
/*      */   @Obsolete
/*  147 */   public static final Sound ENTITY_FIREWORK_TWINKLE_FAR = define("entity.firework.twinkle_far");
/*      */   @Obsolete
/*  149 */   public static final Sound RECORD_MELLOHI = define("record.mellohi");
/*      */   @Obsolete
/*  151 */   public static final Sound ENTITY_ENDERMEN_SCREAM = define("entity.endermen.scream");
/*      */   @Obsolete
/*  153 */   public static final Sound ENTITY_SMALL_MAGMACUBE_SQUISH = define("entity.small_magmacube.squish");
/*      */   @Obsolete
/*  155 */   public static final Sound ENTITY_IRONGOLEM_HURT = define("entity.irongolem.hurt");
/*      */   @Obsolete
/*  157 */   public static final Sound ENTITY_MAGMACUBE_DEATH = define("entity.magmacube.death");
/*      */   @Obsolete
/*  159 */   public static final Sound ENTITY_ENDERMEN_DEATH = define("entity.endermen.death");
/*      */   @Obsolete
/*  161 */   public static final Sound ENTITY_FIREWORK_BLAST_FAR = define("entity.firework.blast_far");
/*      */   @Obsolete
/*  163 */   public static final Sound BLOCK_WOOD_BUTTON_CLICK_ON = define("block.wood_button.click_on");
/*      */   @Obsolete
/*  165 */   public static final Sound ENTITY_ENDERDRAGON_HURT = define("entity.enderdragon.hurt");
/*      */   @Obsolete
/*  167 */   public static final Sound ENTITY_LEASHKNOT_PLACE = define("entity.leashknot.place");
/*      */   @Obsolete
/*  169 */   public static final Sound BLOCK_SLIME_BREAK = define("block.slime.break");
/*      */   @Obsolete
/*  171 */   public static final Sound BLOCK_WATERLILY_PLACE = define("block.waterlily.place");
/*      */   @Obsolete
/*  173 */   public static final Sound ENTITY_ARMORSTAND_PLACE = define("entity.armorstand.place");
/*      */   @Obsolete
/*  175 */   public static final Sound ENTITY_ENDERDRAGON_SHOOT = define("entity.enderdragon.shoot");
/*      */   @Obsolete
/*  177 */   public static final Sound ENTITY_LIGHTNING_THUNDER = define("entity.lightning.thunder");
/*      */   @Obsolete
/*  179 */   public static final Sound ENTITY_SMALL_MAGMACUBE_HURT = define("entity.small_magmacube.hurt");
/*      */   @Obsolete
/*  181 */   public static final Sound BLOCK_CLOTH_FALL = define("block.cloth.fall");
/*      */   @Obsolete
/*  183 */   public static final Sound ENTITY_ZOMBIE_PIG_AMBIENT = define("entity.zombie_pig.ambient");
/*      */   @Obsolete
/*  185 */   public static final Sound ENTITY_IRONGOLEM_ATTACK = define("entity.irongolem.attack");
/*      */   @Obsolete
/*  187 */   public static final Sound ENTITY_ZOMBIE_PIG_ANGRY = define("entity.zombie_pig.angry");
/*      */   @Obsolete
/*  189 */   public static final Sound ENTITY_FIREWORK_BLAST = define("entity.firework.blast");
/*      */   @Obsolete
/*  191 */   public static final Sound ENTITY_SNOWMAN_SHOOT = define("entity.snowman.shoot");
/*      */   @Obsolete
/*  193 */   public static final Sound BLOCK_CLOTH_PLACE = define("block.cloth.place");
/*      */   @Obsolete
/*  195 */   public static final Sound BLOCK_SLIME_PLACE = define("block.slime.place");
/*      */   @Obsolete
/*  197 */   public static final Sound BLOCK_SLIME_FALL = define("block.slime.fall");
/*      */   @Obsolete
/*  199 */   public static final Sound ENTITY_ITEMFRAME_REMOVE_ITEM = define("entity.itemframe.remove_item");
/*      */   @Obsolete
/*  201 */   public static final Sound ENTITY_SNOWMAN_HURT = define("entity.snowman.hurt");
/*      */   @Obsolete
/*  203 */   public static final Sound RECORD_STAL = define("record.stal");
/*      */   @Obsolete
/*  205 */   public static final Sound ENTITY_SMALL_SLIME_DEATH = define("entity.small_slime.death");
/*      */   @Obsolete
/*  207 */   public static final Sound RECORD_WARD = define("record.ward");
/*      */   @Obsolete
/*  209 */   public static final Sound ENTITY_IRONGOLEM_STEP = define("entity.irongolem.step");
/*      */   @Obsolete
/*  211 */   public static final Sound ENTITY_ZOMBIE_PIG_HURT = define("entity.zombie_pig.hurt");
/*      */   @Obsolete
/*  213 */   public static final Sound ENTITY_FIREWORK_LAUNCH = define("entity.firework.launch");
/*      */   @Obsolete
/*  215 */   public static final Sound RECORD_MALL = define("record.mall");
/*      */   @Obsolete
/*  217 */   public static final Sound ENTITY_ENDERMEN_HURT = define("entity.endermen.hurt");
/*      */   @Obsolete
/*  219 */   public static final Sound ENTITY_ENDERMEN_STARE = define("entity.endermen.stare");
/*      */   @Obsolete
/*  221 */   public static final Sound ENTITY_SMALL_SLIME_SQUISH = define("entity.small_slime.squish");
/*      */   @Obsolete
/*  223 */   public static final Sound ENTITY_FIREWORK_TWINKLE = define("entity.firework.twinkle");
/*      */   @Obsolete
/*  225 */   public static final Sound ENTITY_LIGHTNING_IMPACT = define("entity.lightning.impact");
/*      */   @Obsolete
/*  227 */   public static final Sound ENTITY_IRONGOLEM_DEATH = define("entity.irongolem.death");
/*      */   @Obsolete
/*  229 */   public static final Sound BLOCK_STONE_PRESSUREPLATE_CLICK_ON = define("block.stone_pressureplate.click_on");
/*      */   @Obsolete
/*  231 */   public static final Sound ENTITY_EXPERIENCE_ORB_TOUCH = define("entity.experience_orb.touch");
/*      */   @Obsolete
/*  233 */   public static final Sound ENTITY_BOBBER_THROW = define("entity.bobber.throw");
/*      */   @Obsolete
/*  235 */   public static final Sound ENTITY_ENDERDRAGON_DEATH = define("entity.enderdragon.death");
/*      */   @Obsolete
/*  237 */   public static final Sound ENTITY_BOBBER_SPLASH = define("entity.bobber.splash");
/*      */   @Obsolete
/*  239 */   public static final Sound ENTITY_ENDERMEN_AMBIENT = define("entity.endermen.ambient");
/*      */   @Obsolete
/*  241 */   public static final Sound BLOCK_SLIME_STEP = define("block.slime.step");
/*      */   @Obsolete
/*  243 */   public static final Sound BLOCK_NOTE_PLING = define("block.note.pling");
/*      */   @Obsolete
/*  245 */   public static final Sound ENTITY_ITEMFRAME_ROTATE_ITEM = define("entity.itemframe.rotate_item");
/*      */   @Obsolete
/*  247 */   public static final Sound ENTITY_ITEMFRAME_BREAK = define("entity.itemframe.break");
/*      */   @Obsolete
/*  249 */   public static final Sound BLOCK_WOOD_PRESSUREPLATE_CLICK_OFF = define("block.wood_pressureplate.click_off");
/*      */   @Obsolete
/*  251 */   public static final Sound RECORD_CAT = define("record.cat");
/*      */   @Obsolete
/*  253 */   public static final Sound RECORD_WAIT = define("record.wait");
/*      */   @Obsolete
/*  255 */   public static final Sound ENTITY_ITEMFRAME_ADD_ITEM = define("entity.itemframe.add_item");
/*      */   @Obsolete
/*  257 */   public static final Sound BLOCK_NOTE_HAT = define("block.note.hat");
/*      */   @Obsolete
/*  259 */   public static final Sound ENTITY_LINGERINGPOTION_THROW = define("entity.lingeringpotion.throw");
/*      */   @Obsolete
/*  261 */   public static final Sound ENTITY_VILLAGER_TRADING = define("entity.villager.trading");
/*      */   @Obsolete
/*  263 */   public static final Sound BLOCK_ENDERCHEST_CLOSE = define("block.enderchest.close");
/*      */   @Obsolete
/*  265 */   public static final Sound ENTITY_ITEMFRAME_PLACE = define("entity.itemframe.place");
/*      */   @Obsolete
/*  267 */   public static final Sound ENTITY_FIREWORK_LARGE_BLAST = define("entity.firework.large_blast");
/*      */   @Obsolete
/*  269 */   public static final Sound ENTITY_MAGMACUBE_SQUISH = define("entity.magmacube.squish");
/*      */   @Obsolete
/*  271 */   public static final Sound BLOCK_STONE_PRESSUREPLATE_CLICK_OFF = define("block.stone_pressureplate.click_off");
/*      */   @Obsolete
/*  273 */   public static final Sound BLOCK_NOTE_BASS = define("block.note.bass");
/*      */   @Obsolete
/*  275 */   public static final Sound ENTITY_SMALL_MAGMACUBE_DEATH = define("entity.small_magmacube.death");
/*      */   @Obsolete
/*  277 */   public static final Sound ENTITY_ARMORSTAND_FALL = define("entity.armorstand.fall");
/*      */   @Obsolete
/*  279 */   public static final Sound ENTITY_ENDERDRAGON_GROWL = define("entity.enderdragon.growl");
/*      */   @Obsolete
/*  281 */   public static final Sound BLOCK_METAL_PRESSUREPLATE_CLICK_OFF = define("block.metal_pressureplate.click_off");
/*      */   @Obsolete
/*  283 */   public static final Sound ENTITY_SMALL_SLIME_HURT = define("entity.small_slime.hurt");
/*      */   @Obsolete
/*  285 */   public static final Sound ENTITY_SMALL_SLIME_JUMP = define("entity.small_slime.jump");
/*      */   @Obsolete
/*  287 */   public static final Sound ENTITY_ZOMBIE_BREAK_DOOR_WOOD = define("entity.zombie.break_door_wood");
/*      */   @Obsolete
/*  289 */   public static final Sound RECORD_CHIRP = define("record.chirp");
/*      */   @Obsolete
/*  291 */   public static final Sound BLOCK_NOTE_BASEDRUM = define("block.note.basedrum");
/*      */   @Obsolete
/*  293 */   public static final Sound BLOCK_SLIME_HIT = define("block.slime.hit");
/*      */   @Obsolete
/*  295 */   public static final Sound RECORD_13 = define("record.13");
/*      */   @Obsolete
/*  297 */   public static final Sound RECORD_11 = define("record.11");
/*      */   @Obsolete
/*  299 */   public static final Sound BLOCK_METAL_PRESSUREPLATE_CLICK_ON = define("block.metal_pressureplate.click_on");
/*      */   @Obsolete
/*  301 */   public static final Sound BLOCK_WOOD_PRESSUREPLATE_CLICK_ON = define("block.wood_pressureplate.click_on");
/*      */   @Obsolete
/*  303 */   public static final Sound ENTITY_ENDERMEN_TELEPORT = define("entity.endermen.teleport");
/*      */   @Obsolete
/*  305 */   public static final Sound MUSIC_OVERWORLD_JUNGLE_AND_FOREST = define("music.overworld.jungle_and_forest");
/*      */   @Obsolete
/*  307 */   public static final Sound ITEM_BRUSH_BRUSHING = define("item.brush.brushing");
/*      */   @Obsolete
/*  309 */   public static final Sound ITEM_BRUSH_BRUSH_SAND_COMPLETED = define("item.brush.brush_sand_completed");
/*      */   @Obsolete
/*  311 */   public static final Sound ENTITY_GENERIC_WIND_BURST = define("entity.generic.wind_burst");
/*      */   @Obsolete
/*  313 */   public static final Sound ENTITY_POLAR_BEAR_BABY_AMBIENT = define("entity.polar_bear.baby_ambient");
/*      */   @Obsolete
/*  315 */   public static final Sound ENTITY_VINDICATION_ILLAGER_DEATH = define("entity.vindication_illager.death");
/*      */   @Obsolete
/*  317 */   public static final Sound ENTITY_VINDICATION_ILLAGER_AMBIENT = define("entity.vindication_illager.ambient");
/*      */   @Obsolete
/*  319 */   public static final Sound ENTITY_EVOCATION_ILLAGER_HURT = define("entity.evocation_illager.hurt");
/*      */   @Obsolete
/*  321 */   public static final Sound ENTITY_EVOCATION_ILLAGER_PREPARE_ATTACK = define("entity.evocation_illager.prepare_attack");
/*      */   @Obsolete
/*  323 */   public static final Sound ENTITY_EVOCATION_FANGS_ATTACK = define("entity.evocation_fangs.attack");
/*      */   @Obsolete
/*  325 */   public static final Sound ENTITY_EVOCATION_ILLAGER_CAST_SPELL = define("entity.evocation_illager.cast_spell");
/*      */   @Obsolete
/*  327 */   public static final Sound ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON = define("entity.evocation_illager.prepare_summon");
/*      */   @Obsolete
/*  329 */   public static final Sound ENTITY_EVOCATION_ILLAGER_AMBIENT = define("entity.evocation_illager.ambient");
/*      */   @Obsolete
/*  331 */   public static final Sound ENTITY_EVOCATION_ILLAGER_PREPARE_WOLOLO = define("entity.evocation_illager.prepare_wololo");
/*      */   @Obsolete
/*  333 */   public static final Sound ENTITY_VINDICATION_ILLAGER_HURT = define("entity.vindication_illager.hurt");
/*      */   @Obsolete
/*  335 */   public static final Sound ENTITY_EVOCATION_ILLAGER_DEATH = define("entity.evocation_illager.death");
/*      */   
/*  337 */   public static final Sound ENTITY_ALLAY_AMBIENT_WITH_ITEM = define("entity.allay.ambient_with_item");
/*  338 */   public static final Sound ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM = define("entity.allay.ambient_without_item");
/*  339 */   public static final Sound ENTITY_ALLAY_DEATH = define("entity.allay.death");
/*  340 */   public static final Sound ENTITY_ALLAY_HURT = define("entity.allay.hurt");
/*  341 */   public static final Sound ENTITY_ALLAY_ITEM_GIVEN = define("entity.allay.item_given");
/*  342 */   public static final Sound ENTITY_ALLAY_ITEM_TAKEN = define("entity.allay.item_taken");
/*  343 */   public static final Sound ENTITY_ALLAY_ITEM_THROWN = define("entity.allay.item_thrown");
/*  344 */   public static final Sound AMBIENT_CAVE = define("ambient.cave");
/*  345 */   public static final Sound AMBIENT_BASALT_DELTAS_ADDITIONS = define("ambient.basalt_deltas.additions");
/*  346 */   public static final Sound AMBIENT_BASALT_DELTAS_LOOP = define("ambient.basalt_deltas.loop");
/*  347 */   public static final Sound AMBIENT_BASALT_DELTAS_MOOD = define("ambient.basalt_deltas.mood");
/*  348 */   public static final Sound AMBIENT_CRIMSON_FOREST_ADDITIONS = define("ambient.crimson_forest.additions");
/*  349 */   public static final Sound AMBIENT_CRIMSON_FOREST_LOOP = define("ambient.crimson_forest.loop");
/*  350 */   public static final Sound AMBIENT_CRIMSON_FOREST_MOOD = define("ambient.crimson_forest.mood");
/*  351 */   public static final Sound AMBIENT_NETHER_WASTES_ADDITIONS = define("ambient.nether_wastes.additions");
/*  352 */   public static final Sound AMBIENT_NETHER_WASTES_LOOP = define("ambient.nether_wastes.loop");
/*  353 */   public static final Sound AMBIENT_NETHER_WASTES_MOOD = define("ambient.nether_wastes.mood");
/*  354 */   public static final Sound AMBIENT_SOUL_SAND_VALLEY_ADDITIONS = define("ambient.soul_sand_valley.additions");
/*  355 */   public static final Sound AMBIENT_SOUL_SAND_VALLEY_LOOP = define("ambient.soul_sand_valley.loop");
/*  356 */   public static final Sound AMBIENT_SOUL_SAND_VALLEY_MOOD = define("ambient.soul_sand_valley.mood");
/*  357 */   public static final Sound AMBIENT_WARPED_FOREST_ADDITIONS = define("ambient.warped_forest.additions");
/*  358 */   public static final Sound AMBIENT_WARPED_FOREST_LOOP = define("ambient.warped_forest.loop");
/*  359 */   public static final Sound AMBIENT_WARPED_FOREST_MOOD = define("ambient.warped_forest.mood");
/*  360 */   public static final Sound AMBIENT_UNDERWATER_ENTER = define("ambient.underwater.enter");
/*  361 */   public static final Sound AMBIENT_UNDERWATER_EXIT = define("ambient.underwater.exit");
/*  362 */   public static final Sound AMBIENT_UNDERWATER_LOOP = define("ambient.underwater.loop");
/*  363 */   public static final Sound AMBIENT_UNDERWATER_LOOP_ADDITIONS = define("ambient.underwater.loop.additions");
/*  364 */   public static final Sound AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE = define("ambient.underwater.loop.additions.rare");
/*  365 */   public static final Sound AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE = define("ambient.underwater.loop.additions.ultra_rare");
/*  366 */   public static final Sound BLOCK_AMETHYST_BLOCK_BREAK = define("block.amethyst_block.break");
/*  367 */   public static final Sound BLOCK_AMETHYST_BLOCK_CHIME = define("block.amethyst_block.chime");
/*  368 */   public static final Sound BLOCK_AMETHYST_BLOCK_FALL = define("block.amethyst_block.fall");
/*  369 */   public static final Sound BLOCK_AMETHYST_BLOCK_HIT = define("block.amethyst_block.hit");
/*  370 */   public static final Sound BLOCK_AMETHYST_BLOCK_PLACE = define("block.amethyst_block.place");
/*  371 */   public static final Sound BLOCK_AMETHYST_BLOCK_RESONATE = define("block.amethyst_block.resonate");
/*  372 */   public static final Sound BLOCK_AMETHYST_BLOCK_STEP = define("block.amethyst_block.step");
/*  373 */   public static final Sound BLOCK_AMETHYST_CLUSTER_BREAK = define("block.amethyst_cluster.break");
/*  374 */   public static final Sound BLOCK_AMETHYST_CLUSTER_FALL = define("block.amethyst_cluster.fall");
/*  375 */   public static final Sound BLOCK_AMETHYST_CLUSTER_HIT = define("block.amethyst_cluster.hit");
/*  376 */   public static final Sound BLOCK_AMETHYST_CLUSTER_PLACE = define("block.amethyst_cluster.place");
/*  377 */   public static final Sound BLOCK_AMETHYST_CLUSTER_STEP = define("block.amethyst_cluster.step");
/*  378 */   public static final Sound BLOCK_ANCIENT_DEBRIS_BREAK = define("block.ancient_debris.break");
/*  379 */   public static final Sound BLOCK_ANCIENT_DEBRIS_STEP = define("block.ancient_debris.step");
/*  380 */   public static final Sound BLOCK_ANCIENT_DEBRIS_PLACE = define("block.ancient_debris.place");
/*  381 */   public static final Sound BLOCK_ANCIENT_DEBRIS_HIT = define("block.ancient_debris.hit");
/*  382 */   public static final Sound BLOCK_ANCIENT_DEBRIS_FALL = define("block.ancient_debris.fall");
/*  383 */   public static final Sound BLOCK_ANVIL_BREAK = define("block.anvil.break");
/*  384 */   public static final Sound BLOCK_ANVIL_DESTROY = define("block.anvil.destroy");
/*  385 */   public static final Sound BLOCK_ANVIL_FALL = define("block.anvil.fall");
/*  386 */   public static final Sound BLOCK_ANVIL_HIT = define("block.anvil.hit");
/*  387 */   public static final Sound BLOCK_ANVIL_LAND = define("block.anvil.land");
/*  388 */   public static final Sound BLOCK_ANVIL_PLACE = define("block.anvil.place");
/*  389 */   public static final Sound BLOCK_ANVIL_STEP = define("block.anvil.step");
/*  390 */   public static final Sound BLOCK_ANVIL_USE = define("block.anvil.use");
/*  391 */   public static final Sound ENTITY_ARMADILLO_EAT = define("entity.armadillo.eat");
/*  392 */   public static final Sound ENTITY_ARMADILLO_HURT = define("entity.armadillo.hurt");
/*  393 */   public static final Sound ENTITY_ARMADILLO_HURT_REDUCED = define("entity.armadillo.hurt_reduced");
/*  394 */   public static final Sound ENTITY_ARMADILLO_AMBIENT = define("entity.armadillo.ambient");
/*  395 */   public static final Sound ENTITY_ARMADILLO_STEP = define("entity.armadillo.step");
/*  396 */   public static final Sound ENTITY_ARMADILLO_DEATH = define("entity.armadillo.death");
/*  397 */   public static final Sound ENTITY_ARMADILLO_ROLL = define("entity.armadillo.roll");
/*  398 */   public static final Sound ENTITY_ARMADILLO_LAND = define("entity.armadillo.land");
/*  399 */   public static final Sound ENTITY_ARMADILLO_SCUTE_DROP = define("entity.armadillo.scute_drop");
/*  400 */   public static final Sound ENTITY_ARMADILLO_UNROLL_FINISH = define("entity.armadillo.unroll_finish");
/*  401 */   public static final Sound ENTITY_ARMADILLO_PEEK = define("entity.armadillo.peek");
/*  402 */   public static final Sound ENTITY_ARMADILLO_UNROLL_START = define("entity.armadillo.unroll_start");
/*  403 */   public static final Sound ENTITY_ARMADILLO_BRUSH = define("entity.armadillo.brush");
/*  404 */   public static final Sound ITEM_ARMOR_EQUIP_CHAIN = define("item.armor.equip_chain");
/*  405 */   public static final Sound ITEM_ARMOR_EQUIP_DIAMOND = define("item.armor.equip_diamond");
/*  406 */   public static final Sound ITEM_ARMOR_EQUIP_ELYTRA = define("item.armor.equip_elytra");
/*  407 */   public static final Sound ITEM_ARMOR_EQUIP_GENERIC = define("item.armor.equip_generic");
/*  408 */   public static final Sound ITEM_ARMOR_EQUIP_GOLD = define("item.armor.equip_gold");
/*  409 */   public static final Sound ITEM_ARMOR_EQUIP_IRON = define("item.armor.equip_iron");
/*  410 */   public static final Sound ITEM_ARMOR_EQUIP_LEATHER = define("item.armor.equip_leather");
/*  411 */   public static final Sound ITEM_ARMOR_EQUIP_NETHERITE = define("item.armor.equip_netherite");
/*  412 */   public static final Sound ITEM_ARMOR_EQUIP_TURTLE = define("item.armor.equip_turtle");
/*  413 */   public static final Sound ITEM_ARMOR_EQUIP_WOLF = define("item.armor.equip_wolf");
/*  414 */   public static final Sound ITEM_ARMOR_UNEQUIP_WOLF = define("item.armor.unequip_wolf");
/*  415 */   public static final Sound ENTITY_ARMOR_STAND_BREAK = define("entity.armor_stand.break");
/*  416 */   public static final Sound ENTITY_ARMOR_STAND_FALL = define("entity.armor_stand.fall");
/*  417 */   public static final Sound ENTITY_ARMOR_STAND_HIT = define("entity.armor_stand.hit");
/*  418 */   public static final Sound ENTITY_ARMOR_STAND_PLACE = define("entity.armor_stand.place");
/*  419 */   public static final Sound ENTITY_ARROW_HIT = define("entity.arrow.hit");
/*  420 */   public static final Sound ENTITY_ARROW_HIT_PLAYER = define("entity.arrow.hit_player");
/*  421 */   public static final Sound ENTITY_ARROW_SHOOT = define("entity.arrow.shoot");
/*  422 */   public static final Sound ITEM_AXE_STRIP = define("item.axe.strip");
/*  423 */   public static final Sound ITEM_AXE_SCRAPE = define("item.axe.scrape");
/*  424 */   public static final Sound ITEM_AXE_WAX_OFF = define("item.axe.wax_off");
/*  425 */   public static final Sound ENTITY_AXOLOTL_ATTACK = define("entity.axolotl.attack");
/*  426 */   public static final Sound ENTITY_AXOLOTL_DEATH = define("entity.axolotl.death");
/*  427 */   public static final Sound ENTITY_AXOLOTL_HURT = define("entity.axolotl.hurt");
/*  428 */   public static final Sound ENTITY_AXOLOTL_IDLE_AIR = define("entity.axolotl.idle_air");
/*  429 */   public static final Sound ENTITY_AXOLOTL_IDLE_WATER = define("entity.axolotl.idle_water");
/*  430 */   public static final Sound ENTITY_AXOLOTL_SPLASH = define("entity.axolotl.splash");
/*  431 */   public static final Sound ENTITY_AXOLOTL_SWIM = define("entity.axolotl.swim");
/*  432 */   public static final Sound BLOCK_AZALEA_BREAK = define("block.azalea.break");
/*  433 */   public static final Sound BLOCK_AZALEA_FALL = define("block.azalea.fall");
/*  434 */   public static final Sound BLOCK_AZALEA_HIT = define("block.azalea.hit");
/*  435 */   public static final Sound BLOCK_AZALEA_PLACE = define("block.azalea.place");
/*  436 */   public static final Sound BLOCK_AZALEA_STEP = define("block.azalea.step");
/*  437 */   public static final Sound BLOCK_AZALEA_LEAVES_BREAK = define("block.azalea_leaves.break");
/*  438 */   public static final Sound BLOCK_AZALEA_LEAVES_FALL = define("block.azalea_leaves.fall");
/*  439 */   public static final Sound BLOCK_AZALEA_LEAVES_HIT = define("block.azalea_leaves.hit");
/*  440 */   public static final Sound BLOCK_AZALEA_LEAVES_PLACE = define("block.azalea_leaves.place");
/*  441 */   public static final Sound BLOCK_AZALEA_LEAVES_STEP = define("block.azalea_leaves.step");
/*  442 */   public static final Sound BLOCK_BAMBOO_BREAK = define("block.bamboo.break");
/*  443 */   public static final Sound BLOCK_BAMBOO_FALL = define("block.bamboo.fall");
/*  444 */   public static final Sound BLOCK_BAMBOO_HIT = define("block.bamboo.hit");
/*  445 */   public static final Sound BLOCK_BAMBOO_PLACE = define("block.bamboo.place");
/*  446 */   public static final Sound BLOCK_BAMBOO_STEP = define("block.bamboo.step");
/*  447 */   public static final Sound BLOCK_BAMBOO_SAPLING_BREAK = define("block.bamboo_sapling.break");
/*  448 */   public static final Sound BLOCK_BAMBOO_SAPLING_HIT = define("block.bamboo_sapling.hit");
/*  449 */   public static final Sound BLOCK_BAMBOO_SAPLING_PLACE = define("block.bamboo_sapling.place");
/*  450 */   public static final Sound BLOCK_BAMBOO_WOOD_BREAK = define("block.bamboo_wood.break");
/*  451 */   public static final Sound BLOCK_BAMBOO_WOOD_FALL = define("block.bamboo_wood.fall");
/*  452 */   public static final Sound BLOCK_BAMBOO_WOOD_HIT = define("block.bamboo_wood.hit");
/*  453 */   public static final Sound BLOCK_BAMBOO_WOOD_PLACE = define("block.bamboo_wood.place");
/*  454 */   public static final Sound BLOCK_BAMBOO_WOOD_STEP = define("block.bamboo_wood.step");
/*  455 */   public static final Sound BLOCK_BAMBOO_WOOD_DOOR_CLOSE = define("block.bamboo_wood_door.close");
/*  456 */   public static final Sound BLOCK_BAMBOO_WOOD_DOOR_OPEN = define("block.bamboo_wood_door.open");
/*  457 */   public static final Sound BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE = define("block.bamboo_wood_trapdoor.close");
/*  458 */   public static final Sound BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN = define("block.bamboo_wood_trapdoor.open");
/*  459 */   public static final Sound BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF = define("block.bamboo_wood_button.click_off");
/*  460 */   public static final Sound BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON = define("block.bamboo_wood_button.click_on");
/*  461 */   public static final Sound BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF = define("block.bamboo_wood_pressure_plate.click_off");
/*  462 */   public static final Sound BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON = define("block.bamboo_wood_pressure_plate.click_on");
/*  463 */   public static final Sound BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE = define("block.bamboo_wood_fence_gate.close");
/*  464 */   public static final Sound BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN = define("block.bamboo_wood_fence_gate.open");
/*  465 */   public static final Sound BLOCK_BARREL_CLOSE = define("block.barrel.close");
/*  466 */   public static final Sound BLOCK_BARREL_OPEN = define("block.barrel.open");
/*  467 */   public static final Sound BLOCK_BASALT_BREAK = define("block.basalt.break");
/*  468 */   public static final Sound BLOCK_BASALT_STEP = define("block.basalt.step");
/*  469 */   public static final Sound BLOCK_BASALT_PLACE = define("block.basalt.place");
/*  470 */   public static final Sound BLOCK_BASALT_HIT = define("block.basalt.hit");
/*  471 */   public static final Sound BLOCK_BASALT_FALL = define("block.basalt.fall");
/*  472 */   public static final Sound ENTITY_BAT_AMBIENT = define("entity.bat.ambient");
/*  473 */   public static final Sound ENTITY_BAT_DEATH = define("entity.bat.death");
/*  474 */   public static final Sound ENTITY_BAT_HURT = define("entity.bat.hurt");
/*  475 */   public static final Sound ENTITY_BAT_LOOP = define("entity.bat.loop");
/*  476 */   public static final Sound ENTITY_BAT_TAKEOFF = define("entity.bat.takeoff");
/*  477 */   public static final Sound BLOCK_BEACON_ACTIVATE = define("block.beacon.activate");
/*  478 */   public static final Sound BLOCK_BEACON_AMBIENT = define("block.beacon.ambient");
/*  479 */   public static final Sound BLOCK_BEACON_DEACTIVATE = define("block.beacon.deactivate");
/*  480 */   public static final Sound BLOCK_BEACON_POWER_SELECT = define("block.beacon.power_select");
/*  481 */   public static final Sound ENTITY_BEE_DEATH = define("entity.bee.death");
/*  482 */   public static final Sound ENTITY_BEE_HURT = define("entity.bee.hurt");
/*  483 */   public static final Sound ENTITY_BEE_LOOP_AGGRESSIVE = define("entity.bee.loop_aggressive");
/*  484 */   public static final Sound ENTITY_BEE_LOOP = define("entity.bee.loop");
/*  485 */   public static final Sound ENTITY_BEE_STING = define("entity.bee.sting");
/*  486 */   public static final Sound ENTITY_BEE_POLLINATE = define("entity.bee.pollinate");
/*  487 */   public static final Sound BLOCK_BEEHIVE_DRIP = define("block.beehive.drip");
/*  488 */   public static final Sound BLOCK_BEEHIVE_ENTER = define("block.beehive.enter");
/*  489 */   public static final Sound BLOCK_BEEHIVE_EXIT = define("block.beehive.exit");
/*  490 */   public static final Sound BLOCK_BEEHIVE_SHEAR = define("block.beehive.shear");
/*  491 */   public static final Sound BLOCK_BEEHIVE_WORK = define("block.beehive.work");
/*  492 */   public static final Sound BLOCK_BELL_USE = define("block.bell.use");
/*  493 */   public static final Sound BLOCK_BELL_RESONATE = define("block.bell.resonate");
/*  494 */   public static final Sound BLOCK_BIG_DRIPLEAF_BREAK = define("block.big_dripleaf.break");
/*  495 */   public static final Sound BLOCK_BIG_DRIPLEAF_FALL = define("block.big_dripleaf.fall");
/*  496 */   public static final Sound BLOCK_BIG_DRIPLEAF_HIT = define("block.big_dripleaf.hit");
/*  497 */   public static final Sound BLOCK_BIG_DRIPLEAF_PLACE = define("block.big_dripleaf.place");
/*  498 */   public static final Sound BLOCK_BIG_DRIPLEAF_STEP = define("block.big_dripleaf.step");
/*  499 */   public static final Sound ENTITY_BLAZE_AMBIENT = define("entity.blaze.ambient");
/*  500 */   public static final Sound ENTITY_BLAZE_BURN = define("entity.blaze.burn");
/*  501 */   public static final Sound ENTITY_BLAZE_DEATH = define("entity.blaze.death");
/*  502 */   public static final Sound ENTITY_BLAZE_HURT = define("entity.blaze.hurt");
/*  503 */   public static final Sound ENTITY_BLAZE_SHOOT = define("entity.blaze.shoot");
/*  504 */   public static final Sound ENTITY_BOAT_PADDLE_LAND = define("entity.boat.paddle_land");
/*  505 */   public static final Sound ENTITY_BOAT_PADDLE_WATER = define("entity.boat.paddle_water");
/*  506 */   public static final Sound ENTITY_BOGGED_AMBIENT = define("entity.bogged.ambient");
/*  507 */   public static final Sound ENTITY_BOGGED_DEATH = define("entity.bogged.death");
/*  508 */   public static final Sound ENTITY_BOGGED_HURT = define("entity.bogged.hurt");
/*  509 */   public static final Sound ENTITY_BOGGED_SHEAR = define("entity.bogged.shear");
/*  510 */   public static final Sound ENTITY_BOGGED_STEP = define("entity.bogged.step");
/*  511 */   public static final Sound BLOCK_BONE_BLOCK_BREAK = define("block.bone_block.break");
/*  512 */   public static final Sound BLOCK_BONE_BLOCK_FALL = define("block.bone_block.fall");
/*  513 */   public static final Sound BLOCK_BONE_BLOCK_HIT = define("block.bone_block.hit");
/*  514 */   public static final Sound BLOCK_BONE_BLOCK_PLACE = define("block.bone_block.place");
/*  515 */   public static final Sound BLOCK_BONE_BLOCK_STEP = define("block.bone_block.step");
/*  516 */   public static final Sound ITEM_BONE_MEAL_USE = define("item.bone_meal.use");
/*  517 */   public static final Sound ITEM_BOOK_PAGE_TURN = define("item.book.page_turn");
/*  518 */   public static final Sound ITEM_BOOK_PUT = define("item.book.put");
/*  519 */   public static final Sound BLOCK_BLASTFURNACE_FIRE_CRACKLE = define("block.blastfurnace.fire_crackle");
/*  520 */   public static final Sound ITEM_BOTTLE_EMPTY = define("item.bottle.empty");
/*  521 */   public static final Sound ITEM_BOTTLE_FILL = define("item.bottle.fill");
/*  522 */   public static final Sound ITEM_BOTTLE_FILL_DRAGONBREATH = define("item.bottle.fill_dragonbreath");
/*  523 */   public static final Sound ENTITY_BREEZE_CHARGE = define("entity.breeze.charge");
/*  524 */   public static final Sound ENTITY_BREEZE_DEFLECT = define("entity.breeze.deflect");
/*  525 */   public static final Sound ENTITY_BREEZE_INHALE = define("entity.breeze.inhale");
/*  526 */   public static final Sound ENTITY_BREEZE_IDLE_GROUND = define("entity.breeze.idle_ground");
/*  527 */   public static final Sound ENTITY_BREEZE_IDLE_AIR = define("entity.breeze.idle_air");
/*  528 */   public static final Sound ENTITY_BREEZE_SHOOT = define("entity.breeze.shoot");
/*  529 */   public static final Sound ENTITY_BREEZE_JUMP = define("entity.breeze.jump");
/*  530 */   public static final Sound ENTITY_BREEZE_LAND = define("entity.breeze.land");
/*  531 */   public static final Sound ENTITY_BREEZE_SLIDE = define("entity.breeze.slide");
/*  532 */   public static final Sound ENTITY_BREEZE_DEATH = define("entity.breeze.death");
/*  533 */   public static final Sound ENTITY_BREEZE_HURT = define("entity.breeze.hurt");
/*  534 */   public static final Sound ENTITY_BREEZE_WHIRL = define("entity.breeze.whirl");
/*  535 */   public static final Sound ENTITY_BREEZE_WIND_BURST = define("entity.breeze.wind_burst");
/*  536 */   public static final Sound BLOCK_BREWING_STAND_BREW = define("block.brewing_stand.brew");
/*  537 */   public static final Sound ITEM_BRUSH_BRUSHING_GENERIC = define("item.brush.brushing.generic");
/*  538 */   public static final Sound ITEM_BRUSH_BRUSHING_SAND = define("item.brush.brushing.sand");
/*  539 */   public static final Sound ITEM_BRUSH_BRUSHING_GRAVEL = define("item.brush.brushing.gravel");
/*  540 */   public static final Sound ITEM_BRUSH_BRUSHING_SAND_COMPLETE = define("item.brush.brushing.sand.complete");
/*  541 */   public static final Sound ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE = define("item.brush.brushing.gravel.complete");
/*  542 */   public static final Sound BLOCK_BUBBLE_COLUMN_BUBBLE_POP = define("block.bubble_column.bubble_pop");
/*  543 */   public static final Sound BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT = define("block.bubble_column.upwards_ambient");
/*  544 */   public static final Sound BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE = define("block.bubble_column.upwards_inside");
/*  545 */   public static final Sound BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT = define("block.bubble_column.whirlpool_ambient");
/*  546 */   public static final Sound BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE = define("block.bubble_column.whirlpool_inside");
/*  547 */   public static final Sound ITEM_BUCKET_EMPTY = define("item.bucket.empty");
/*  548 */   public static final Sound ITEM_BUCKET_EMPTY_AXOLOTL = define("item.bucket.empty_axolotl");
/*  549 */   public static final Sound ITEM_BUCKET_EMPTY_FISH = define("item.bucket.empty_fish");
/*  550 */   public static final Sound ITEM_BUCKET_EMPTY_LAVA = define("item.bucket.empty_lava");
/*  551 */   public static final Sound ITEM_BUCKET_EMPTY_POWDER_SNOW = define("item.bucket.empty_powder_snow");
/*  552 */   public static final Sound ITEM_BUCKET_EMPTY_TADPOLE = define("item.bucket.empty_tadpole");
/*  553 */   public static final Sound ITEM_BUCKET_FILL = define("item.bucket.fill");
/*  554 */   public static final Sound ITEM_BUCKET_FILL_AXOLOTL = define("item.bucket.fill_axolotl");
/*  555 */   public static final Sound ITEM_BUCKET_FILL_FISH = define("item.bucket.fill_fish");
/*  556 */   public static final Sound ITEM_BUCKET_FILL_LAVA = define("item.bucket.fill_lava");
/*  557 */   public static final Sound ITEM_BUCKET_FILL_POWDER_SNOW = define("item.bucket.fill_powder_snow");
/*  558 */   public static final Sound ITEM_BUCKET_FILL_TADPOLE = define("item.bucket.fill_tadpole");
/*  559 */   public static final Sound ITEM_BUNDLE_DROP_CONTENTS = define("item.bundle.drop_contents");
/*  560 */   public static final Sound ITEM_BUNDLE_INSERT = define("item.bundle.insert");
/*  561 */   public static final Sound ITEM_BUNDLE_REMOVE_ONE = define("item.bundle.remove_one");
/*  562 */   public static final Sound BLOCK_CAKE_ADD_CANDLE = define("block.cake.add_candle");
/*  563 */   public static final Sound BLOCK_CALCITE_BREAK = define("block.calcite.break");
/*  564 */   public static final Sound BLOCK_CALCITE_STEP = define("block.calcite.step");
/*  565 */   public static final Sound BLOCK_CALCITE_PLACE = define("block.calcite.place");
/*  566 */   public static final Sound BLOCK_CALCITE_HIT = define("block.calcite.hit");
/*  567 */   public static final Sound BLOCK_CALCITE_FALL = define("block.calcite.fall");
/*  568 */   public static final Sound ENTITY_CAMEL_AMBIENT = define("entity.camel.ambient");
/*  569 */   public static final Sound ENTITY_CAMEL_DASH = define("entity.camel.dash");
/*  570 */   public static final Sound ENTITY_CAMEL_DASH_READY = define("entity.camel.dash_ready");
/*  571 */   public static final Sound ENTITY_CAMEL_DEATH = define("entity.camel.death");
/*  572 */   public static final Sound ENTITY_CAMEL_EAT = define("entity.camel.eat");
/*  573 */   public static final Sound ENTITY_CAMEL_HURT = define("entity.camel.hurt");
/*  574 */   public static final Sound ENTITY_CAMEL_SADDLE = define("entity.camel.saddle");
/*  575 */   public static final Sound ENTITY_CAMEL_SIT = define("entity.camel.sit");
/*  576 */   public static final Sound ENTITY_CAMEL_STAND = define("entity.camel.stand");
/*  577 */   public static final Sound ENTITY_CAMEL_STEP = define("entity.camel.step");
/*  578 */   public static final Sound ENTITY_CAMEL_STEP_SAND = define("entity.camel.step_sand");
/*  579 */   public static final Sound BLOCK_CAMPFIRE_CRACKLE = define("block.campfire.crackle");
/*  580 */   public static final Sound BLOCK_CANDLE_AMBIENT = define("block.candle.ambient");
/*  581 */   public static final Sound BLOCK_CANDLE_BREAK = define("block.candle.break");
/*  582 */   public static final Sound BLOCK_CANDLE_EXTINGUISH = define("block.candle.extinguish");
/*  583 */   public static final Sound BLOCK_CANDLE_FALL = define("block.candle.fall");
/*  584 */   public static final Sound BLOCK_CANDLE_HIT = define("block.candle.hit");
/*  585 */   public static final Sound BLOCK_CANDLE_PLACE = define("block.candle.place");
/*  586 */   public static final Sound BLOCK_CANDLE_STEP = define("block.candle.step");
/*  587 */   public static final Sound ENTITY_CAT_AMBIENT = define("entity.cat.ambient");
/*  588 */   public static final Sound ENTITY_CAT_STRAY_AMBIENT = define("entity.cat.stray_ambient");
/*  589 */   public static final Sound ENTITY_CAT_DEATH = define("entity.cat.death");
/*  590 */   public static final Sound ENTITY_CAT_EAT = define("entity.cat.eat");
/*  591 */   public static final Sound ENTITY_CAT_HISS = define("entity.cat.hiss");
/*  592 */   public static final Sound ENTITY_CAT_BEG_FOR_FOOD = define("entity.cat.beg_for_food");
/*  593 */   public static final Sound ENTITY_CAT_HURT = define("entity.cat.hurt");
/*  594 */   public static final Sound ENTITY_CAT_PURR = define("entity.cat.purr");
/*  595 */   public static final Sound ENTITY_CAT_PURREOW = define("entity.cat.purreow");
/*  596 */   public static final Sound BLOCK_CAVE_VINES_BREAK = define("block.cave_vines.break");
/*  597 */   public static final Sound BLOCK_CAVE_VINES_FALL = define("block.cave_vines.fall");
/*  598 */   public static final Sound BLOCK_CAVE_VINES_HIT = define("block.cave_vines.hit");
/*  599 */   public static final Sound BLOCK_CAVE_VINES_PLACE = define("block.cave_vines.place");
/*  600 */   public static final Sound BLOCK_CAVE_VINES_STEP = define("block.cave_vines.step");
/*  601 */   public static final Sound BLOCK_CAVE_VINES_PICK_BERRIES = define("block.cave_vines.pick_berries");
/*  602 */   public static final Sound BLOCK_CHAIN_BREAK = define("block.chain.break");
/*  603 */   public static final Sound BLOCK_CHAIN_FALL = define("block.chain.fall");
/*  604 */   public static final Sound BLOCK_CHAIN_HIT = define("block.chain.hit");
/*  605 */   public static final Sound BLOCK_CHAIN_PLACE = define("block.chain.place");
/*  606 */   public static final Sound BLOCK_CHAIN_STEP = define("block.chain.step");
/*  607 */   public static final Sound BLOCK_CHERRY_WOOD_BREAK = define("block.cherry_wood.break");
/*  608 */   public static final Sound BLOCK_CHERRY_WOOD_FALL = define("block.cherry_wood.fall");
/*  609 */   public static final Sound BLOCK_CHERRY_WOOD_HIT = define("block.cherry_wood.hit");
/*  610 */   public static final Sound BLOCK_CHERRY_WOOD_PLACE = define("block.cherry_wood.place");
/*  611 */   public static final Sound BLOCK_CHERRY_WOOD_STEP = define("block.cherry_wood.step");
/*  612 */   public static final Sound BLOCK_CHERRY_SAPLING_BREAK = define("block.cherry_sapling.break");
/*  613 */   public static final Sound BLOCK_CHERRY_SAPLING_FALL = define("block.cherry_sapling.fall");
/*  614 */   public static final Sound BLOCK_CHERRY_SAPLING_HIT = define("block.cherry_sapling.hit");
/*  615 */   public static final Sound BLOCK_CHERRY_SAPLING_PLACE = define("block.cherry_sapling.place");
/*  616 */   public static final Sound BLOCK_CHERRY_SAPLING_STEP = define("block.cherry_sapling.step");
/*  617 */   public static final Sound BLOCK_CHERRY_LEAVES_BREAK = define("block.cherry_leaves.break");
/*  618 */   public static final Sound BLOCK_CHERRY_LEAVES_FALL = define("block.cherry_leaves.fall");
/*  619 */   public static final Sound BLOCK_CHERRY_LEAVES_HIT = define("block.cherry_leaves.hit");
/*  620 */   public static final Sound BLOCK_CHERRY_LEAVES_PLACE = define("block.cherry_leaves.place");
/*  621 */   public static final Sound BLOCK_CHERRY_LEAVES_STEP = define("block.cherry_leaves.step");
/*  622 */   public static final Sound BLOCK_CHERRY_WOOD_HANGING_SIGN_STEP = define("block.cherry_wood_hanging_sign.step");
/*  623 */   public static final Sound BLOCK_CHERRY_WOOD_HANGING_SIGN_BREAK = define("block.cherry_wood_hanging_sign.break");
/*  624 */   public static final Sound BLOCK_CHERRY_WOOD_HANGING_SIGN_FALL = define("block.cherry_wood_hanging_sign.fall");
/*  625 */   public static final Sound BLOCK_CHERRY_WOOD_HANGING_SIGN_HIT = define("block.cherry_wood_hanging_sign.hit");
/*  626 */   public static final Sound BLOCK_CHERRY_WOOD_HANGING_SIGN_PLACE = define("block.cherry_wood_hanging_sign.place");
/*  627 */   public static final Sound BLOCK_CHERRY_WOOD_DOOR_CLOSE = define("block.cherry_wood_door.close");
/*  628 */   public static final Sound BLOCK_CHERRY_WOOD_DOOR_OPEN = define("block.cherry_wood_door.open");
/*  629 */   public static final Sound BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE = define("block.cherry_wood_trapdoor.close");
/*  630 */   public static final Sound BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN = define("block.cherry_wood_trapdoor.open");
/*  631 */   public static final Sound BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF = define("block.cherry_wood_button.click_off");
/*  632 */   public static final Sound BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON = define("block.cherry_wood_button.click_on");
/*  633 */   public static final Sound BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF = define("block.cherry_wood_pressure_plate.click_off");
/*  634 */   public static final Sound BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON = define("block.cherry_wood_pressure_plate.click_on");
/*  635 */   public static final Sound BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE = define("block.cherry_wood_fence_gate.close");
/*  636 */   public static final Sound BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN = define("block.cherry_wood_fence_gate.open");
/*  637 */   public static final Sound BLOCK_CHEST_CLOSE = define("block.chest.close");
/*  638 */   public static final Sound BLOCK_CHEST_LOCKED = define("block.chest.locked");
/*  639 */   public static final Sound BLOCK_CHEST_OPEN = define("block.chest.open");
/*  640 */   public static final Sound ENTITY_CHICKEN_AMBIENT = define("entity.chicken.ambient");
/*  641 */   public static final Sound ENTITY_CHICKEN_DEATH = define("entity.chicken.death");
/*  642 */   public static final Sound ENTITY_CHICKEN_EGG = define("entity.chicken.egg");
/*  643 */   public static final Sound ENTITY_CHICKEN_HURT = define("entity.chicken.hurt");
/*  644 */   public static final Sound ENTITY_CHICKEN_STEP = define("entity.chicken.step");
/*  645 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_BREAK = define("block.chiseled_bookshelf.break");
/*  646 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_FALL = define("block.chiseled_bookshelf.fall");
/*  647 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_HIT = define("block.chiseled_bookshelf.hit");
/*  648 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_INSERT = define("block.chiseled_bookshelf.insert");
/*  649 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED = define("block.chiseled_bookshelf.insert.enchanted");
/*  650 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_STEP = define("block.chiseled_bookshelf.step");
/*  651 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_PICKUP = define("block.chiseled_bookshelf.pickup");
/*  652 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_PICKUP_ENCHANTED = define("block.chiseled_bookshelf.pickup.enchanted");
/*  653 */   public static final Sound BLOCK_CHISELED_BOOKSHELF_PLACE = define("block.chiseled_bookshelf.place");
/*  654 */   public static final Sound BLOCK_CHORUS_FLOWER_DEATH = define("block.chorus_flower.death");
/*  655 */   public static final Sound BLOCK_CHORUS_FLOWER_GROW = define("block.chorus_flower.grow");
/*  656 */   public static final Sound ITEM_CHORUS_FRUIT_TELEPORT = define("item.chorus_fruit.teleport");
/*  657 */   public static final Sound BLOCK_COBWEB_BREAK = define("block.cobweb.break");
/*  658 */   public static final Sound BLOCK_COBWEB_STEP = define("block.cobweb.step");
/*  659 */   public static final Sound BLOCK_COBWEB_PLACE = define("block.cobweb.place");
/*  660 */   public static final Sound BLOCK_COBWEB_HIT = define("block.cobweb.hit");
/*  661 */   public static final Sound BLOCK_COBWEB_FALL = define("block.cobweb.fall");
/*  662 */   public static final Sound ENTITY_COD_AMBIENT = define("entity.cod.ambient");
/*  663 */   public static final Sound ENTITY_COD_DEATH = define("entity.cod.death");
/*  664 */   public static final Sound ENTITY_COD_FLOP = define("entity.cod.flop");
/*  665 */   public static final Sound ENTITY_COD_HURT = define("entity.cod.hurt");
/*  666 */   public static final Sound BLOCK_COMPARATOR_CLICK = define("block.comparator.click");
/*  667 */   public static final Sound BLOCK_COMPOSTER_EMPTY = define("block.composter.empty");
/*  668 */   public static final Sound BLOCK_COMPOSTER_FILL = define("block.composter.fill");
/*  669 */   public static final Sound BLOCK_COMPOSTER_FILL_SUCCESS = define("block.composter.fill_success");
/*  670 */   public static final Sound BLOCK_COMPOSTER_READY = define("block.composter.ready");
/*  671 */   public static final Sound BLOCK_CONDUIT_ACTIVATE = define("block.conduit.activate");
/*  672 */   public static final Sound BLOCK_CONDUIT_AMBIENT = define("block.conduit.ambient");
/*  673 */   public static final Sound BLOCK_CONDUIT_AMBIENT_SHORT = define("block.conduit.ambient.short");
/*  674 */   public static final Sound BLOCK_CONDUIT_ATTACK_TARGET = define("block.conduit.attack.target");
/*  675 */   public static final Sound BLOCK_CONDUIT_DEACTIVATE = define("block.conduit.deactivate");
/*  676 */   public static final Sound BLOCK_COPPER_BULB_BREAK = define("block.copper_bulb.break");
/*  677 */   public static final Sound BLOCK_COPPER_BULB_STEP = define("block.copper_bulb.step");
/*  678 */   public static final Sound BLOCK_COPPER_BULB_PLACE = define("block.copper_bulb.place");
/*  679 */   public static final Sound BLOCK_COPPER_BULB_HIT = define("block.copper_bulb.hit");
/*  680 */   public static final Sound BLOCK_COPPER_BULB_FALL = define("block.copper_bulb.fall");
/*  681 */   public static final Sound BLOCK_COPPER_BULB_TURN_ON = define("block.copper_bulb.turn_on");
/*  682 */   public static final Sound BLOCK_COPPER_BULB_TURN_OFF = define("block.copper_bulb.turn_off");
/*  683 */   public static final Sound BLOCK_COPPER_BREAK = define("block.copper.break");
/*  684 */   public static final Sound BLOCK_COPPER_STEP = define("block.copper.step");
/*  685 */   public static final Sound BLOCK_COPPER_PLACE = define("block.copper.place");
/*  686 */   public static final Sound BLOCK_COPPER_HIT = define("block.copper.hit");
/*  687 */   public static final Sound BLOCK_COPPER_FALL = define("block.copper.fall");
/*  688 */   public static final Sound BLOCK_COPPER_DOOR_CLOSE = define("block.copper_door.close");
/*  689 */   public static final Sound BLOCK_COPPER_DOOR_OPEN = define("block.copper_door.open");
/*  690 */   public static final Sound BLOCK_COPPER_GRATE_BREAK = define("block.copper_grate.break");
/*  691 */   public static final Sound BLOCK_COPPER_GRATE_STEP = define("block.copper_grate.step");
/*  692 */   public static final Sound BLOCK_COPPER_GRATE_PLACE = define("block.copper_grate.place");
/*  693 */   public static final Sound BLOCK_COPPER_GRATE_HIT = define("block.copper_grate.hit");
/*  694 */   public static final Sound BLOCK_COPPER_GRATE_FALL = define("block.copper_grate.fall");
/*  695 */   public static final Sound BLOCK_COPPER_TRAPDOOR_CLOSE = define("block.copper_trapdoor.close");
/*  696 */   public static final Sound BLOCK_COPPER_TRAPDOOR_OPEN = define("block.copper_trapdoor.open");
/*  697 */   public static final Sound BLOCK_CORAL_BLOCK_BREAK = define("block.coral_block.break");
/*  698 */   public static final Sound BLOCK_CORAL_BLOCK_FALL = define("block.coral_block.fall");
/*  699 */   public static final Sound BLOCK_CORAL_BLOCK_HIT = define("block.coral_block.hit");
/*  700 */   public static final Sound BLOCK_CORAL_BLOCK_PLACE = define("block.coral_block.place");
/*  701 */   public static final Sound BLOCK_CORAL_BLOCK_STEP = define("block.coral_block.step");
/*  702 */   public static final Sound ENTITY_COW_AMBIENT = define("entity.cow.ambient");
/*  703 */   public static final Sound ENTITY_COW_DEATH = define("entity.cow.death");
/*  704 */   public static final Sound ENTITY_COW_HURT = define("entity.cow.hurt");
/*  705 */   public static final Sound ENTITY_COW_MILK = define("entity.cow.milk");
/*  706 */   public static final Sound ENTITY_COW_STEP = define("entity.cow.step");
/*  707 */   public static final Sound BLOCK_CRAFTER_CRAFT = define("block.crafter.craft");
/*  708 */   public static final Sound BLOCK_CRAFTER_FAIL = define("block.crafter.fail");
/*  709 */   public static final Sound ENTITY_CREEPER_DEATH = define("entity.creeper.death");
/*  710 */   public static final Sound ENTITY_CREEPER_HURT = define("entity.creeper.hurt");
/*  711 */   public static final Sound ENTITY_CREEPER_PRIMED = define("entity.creeper.primed");
/*  712 */   public static final Sound BLOCK_CROP_BREAK = define("block.crop.break");
/*  713 */   public static final Sound ITEM_CROP_PLANT = define("item.crop.plant");
/*  714 */   public static final Sound ITEM_CROSSBOW_HIT = define("item.crossbow.hit");
/*  715 */   public static final Sound ITEM_CROSSBOW_LOADING_END = define("item.crossbow.loading_end");
/*  716 */   public static final Sound ITEM_CROSSBOW_LOADING_MIDDLE = define("item.crossbow.loading_middle");
/*  717 */   public static final Sound ITEM_CROSSBOW_LOADING_START = define("item.crossbow.loading_start");
/*  718 */   public static final Sound ITEM_CROSSBOW_QUICK_CHARGE_1 = define("item.crossbow.quick_charge_1");
/*  719 */   public static final Sound ITEM_CROSSBOW_QUICK_CHARGE_2 = define("item.crossbow.quick_charge_2");
/*  720 */   public static final Sound ITEM_CROSSBOW_QUICK_CHARGE_3 = define("item.crossbow.quick_charge_3");
/*  721 */   public static final Sound ITEM_CROSSBOW_SHOOT = define("item.crossbow.shoot");
/*  722 */   public static final Sound BLOCK_DECORATED_POT_BREAK = define("block.decorated_pot.break");
/*  723 */   public static final Sound BLOCK_DECORATED_POT_FALL = define("block.decorated_pot.fall");
/*  724 */   public static final Sound BLOCK_DECORATED_POT_HIT = define("block.decorated_pot.hit");
/*  725 */   public static final Sound BLOCK_DECORATED_POT_INSERT = define("block.decorated_pot.insert");
/*  726 */   public static final Sound BLOCK_DECORATED_POT_INSERT_FAIL = define("block.decorated_pot.insert_fail");
/*  727 */   public static final Sound BLOCK_DECORATED_POT_STEP = define("block.decorated_pot.step");
/*  728 */   public static final Sound BLOCK_DECORATED_POT_PLACE = define("block.decorated_pot.place");
/*  729 */   public static final Sound BLOCK_DECORATED_POT_SHATTER = define("block.decorated_pot.shatter");
/*  730 */   public static final Sound BLOCK_DEEPSLATE_BRICKS_BREAK = define("block.deepslate_bricks.break");
/*  731 */   public static final Sound BLOCK_DEEPSLATE_BRICKS_FALL = define("block.deepslate_bricks.fall");
/*  732 */   public static final Sound BLOCK_DEEPSLATE_BRICKS_HIT = define("block.deepslate_bricks.hit");
/*  733 */   public static final Sound BLOCK_DEEPSLATE_BRICKS_PLACE = define("block.deepslate_bricks.place");
/*  734 */   public static final Sound BLOCK_DEEPSLATE_BRICKS_STEP = define("block.deepslate_bricks.step");
/*  735 */   public static final Sound BLOCK_DEEPSLATE_BREAK = define("block.deepslate.break");
/*  736 */   public static final Sound BLOCK_DEEPSLATE_FALL = define("block.deepslate.fall");
/*  737 */   public static final Sound BLOCK_DEEPSLATE_HIT = define("block.deepslate.hit");
/*  738 */   public static final Sound BLOCK_DEEPSLATE_PLACE = define("block.deepslate.place");
/*  739 */   public static final Sound BLOCK_DEEPSLATE_STEP = define("block.deepslate.step");
/*  740 */   public static final Sound BLOCK_DEEPSLATE_TILES_BREAK = define("block.deepslate_tiles.break");
/*  741 */   public static final Sound BLOCK_DEEPSLATE_TILES_FALL = define("block.deepslate_tiles.fall");
/*  742 */   public static final Sound BLOCK_DEEPSLATE_TILES_HIT = define("block.deepslate_tiles.hit");
/*  743 */   public static final Sound BLOCK_DEEPSLATE_TILES_PLACE = define("block.deepslate_tiles.place");
/*  744 */   public static final Sound BLOCK_DEEPSLATE_TILES_STEP = define("block.deepslate_tiles.step");
/*  745 */   public static final Sound BLOCK_DISPENSER_DISPENSE = define("block.dispenser.dispense");
/*  746 */   public static final Sound BLOCK_DISPENSER_FAIL = define("block.dispenser.fail");
/*  747 */   public static final Sound BLOCK_DISPENSER_LAUNCH = define("block.dispenser.launch");
/*  748 */   public static final Sound ENTITY_DOLPHIN_AMBIENT = define("entity.dolphin.ambient");
/*  749 */   public static final Sound ENTITY_DOLPHIN_AMBIENT_WATER = define("entity.dolphin.ambient_water");
/*  750 */   public static final Sound ENTITY_DOLPHIN_ATTACK = define("entity.dolphin.attack");
/*  751 */   public static final Sound ENTITY_DOLPHIN_DEATH = define("entity.dolphin.death");
/*  752 */   public static final Sound ENTITY_DOLPHIN_EAT = define("entity.dolphin.eat");
/*  753 */   public static final Sound ENTITY_DOLPHIN_HURT = define("entity.dolphin.hurt");
/*  754 */   public static final Sound ENTITY_DOLPHIN_JUMP = define("entity.dolphin.jump");
/*  755 */   public static final Sound ENTITY_DOLPHIN_PLAY = define("entity.dolphin.play");
/*  756 */   public static final Sound ENTITY_DOLPHIN_SPLASH = define("entity.dolphin.splash");
/*  757 */   public static final Sound ENTITY_DOLPHIN_SWIM = define("entity.dolphin.swim");
/*  758 */   public static final Sound ENTITY_DONKEY_AMBIENT = define("entity.donkey.ambient");
/*  759 */   public static final Sound ENTITY_DONKEY_ANGRY = define("entity.donkey.angry");
/*  760 */   public static final Sound ENTITY_DONKEY_CHEST = define("entity.donkey.chest");
/*  761 */   public static final Sound ENTITY_DONKEY_DEATH = define("entity.donkey.death");
/*  762 */   public static final Sound ENTITY_DONKEY_EAT = define("entity.donkey.eat");
/*  763 */   public static final Sound ENTITY_DONKEY_HURT = define("entity.donkey.hurt");
/*  764 */   public static final Sound ENTITY_DONKEY_JUMP = define("entity.donkey.jump");
/*  765 */   public static final Sound BLOCK_DRIPSTONE_BLOCK_BREAK = define("block.dripstone_block.break");
/*  766 */   public static final Sound BLOCK_DRIPSTONE_BLOCK_STEP = define("block.dripstone_block.step");
/*  767 */   public static final Sound BLOCK_DRIPSTONE_BLOCK_PLACE = define("block.dripstone_block.place");
/*  768 */   public static final Sound BLOCK_DRIPSTONE_BLOCK_HIT = define("block.dripstone_block.hit");
/*  769 */   public static final Sound BLOCK_DRIPSTONE_BLOCK_FALL = define("block.dripstone_block.fall");
/*  770 */   public static final Sound BLOCK_POINTED_DRIPSTONE_BREAK = define("block.pointed_dripstone.break");
/*  771 */   public static final Sound BLOCK_POINTED_DRIPSTONE_STEP = define("block.pointed_dripstone.step");
/*  772 */   public static final Sound BLOCK_POINTED_DRIPSTONE_PLACE = define("block.pointed_dripstone.place");
/*  773 */   public static final Sound BLOCK_POINTED_DRIPSTONE_HIT = define("block.pointed_dripstone.hit");
/*  774 */   public static final Sound BLOCK_POINTED_DRIPSTONE_FALL = define("block.pointed_dripstone.fall");
/*  775 */   public static final Sound BLOCK_POINTED_DRIPSTONE_LAND = define("block.pointed_dripstone.land");
/*  776 */   public static final Sound BLOCK_POINTED_DRIPSTONE_DRIP_LAVA = define("block.pointed_dripstone.drip_lava");
/*  777 */   public static final Sound BLOCK_POINTED_DRIPSTONE_DRIP_WATER = define("block.pointed_dripstone.drip_water");
/*  778 */   public static final Sound BLOCK_POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON = define("block.pointed_dripstone.drip_lava_into_cauldron");
/*  779 */   public static final Sound BLOCK_POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON = define("block.pointed_dripstone.drip_water_into_cauldron");
/*  780 */   public static final Sound BLOCK_BIG_DRIPLEAF_TILT_DOWN = define("block.big_dripleaf.tilt_down");
/*  781 */   public static final Sound BLOCK_BIG_DRIPLEAF_TILT_UP = define("block.big_dripleaf.tilt_up");
/*  782 */   public static final Sound ENTITY_DROWNED_AMBIENT = define("entity.drowned.ambient");
/*  783 */   public static final Sound ENTITY_DROWNED_AMBIENT_WATER = define("entity.drowned.ambient_water");
/*  784 */   public static final Sound ENTITY_DROWNED_DEATH = define("entity.drowned.death");
/*  785 */   public static final Sound ENTITY_DROWNED_DEATH_WATER = define("entity.drowned.death_water");
/*  786 */   public static final Sound ENTITY_DROWNED_HURT = define("entity.drowned.hurt");
/*  787 */   public static final Sound ENTITY_DROWNED_HURT_WATER = define("entity.drowned.hurt_water");
/*  788 */   public static final Sound ENTITY_DROWNED_SHOOT = define("entity.drowned.shoot");
/*  789 */   public static final Sound ENTITY_DROWNED_STEP = define("entity.drowned.step");
/*  790 */   public static final Sound ENTITY_DROWNED_SWIM = define("entity.drowned.swim");
/*  791 */   public static final Sound ITEM_DYE_USE = define("item.dye.use");
/*  792 */   public static final Sound ENTITY_EGG_THROW = define("entity.egg.throw");
/*  793 */   public static final Sound ENTITY_ELDER_GUARDIAN_AMBIENT = define("entity.elder_guardian.ambient");
/*  794 */   public static final Sound ENTITY_ELDER_GUARDIAN_AMBIENT_LAND = define("entity.elder_guardian.ambient_land");
/*  795 */   public static final Sound ENTITY_ELDER_GUARDIAN_CURSE = define("entity.elder_guardian.curse");
/*  796 */   public static final Sound ENTITY_ELDER_GUARDIAN_DEATH = define("entity.elder_guardian.death");
/*  797 */   public static final Sound ENTITY_ELDER_GUARDIAN_DEATH_LAND = define("entity.elder_guardian.death_land");
/*  798 */   public static final Sound ENTITY_ELDER_GUARDIAN_FLOP = define("entity.elder_guardian.flop");
/*  799 */   public static final Sound ENTITY_ELDER_GUARDIAN_HURT = define("entity.elder_guardian.hurt");
/*  800 */   public static final Sound ENTITY_ELDER_GUARDIAN_HURT_LAND = define("entity.elder_guardian.hurt_land");
/*  801 */   public static final Sound ITEM_ELYTRA_FLYING = define("item.elytra.flying");
/*  802 */   public static final Sound BLOCK_ENCHANTMENT_TABLE_USE = define("block.enchantment_table.use");
/*  803 */   public static final Sound BLOCK_ENDER_CHEST_CLOSE = define("block.ender_chest.close");
/*  804 */   public static final Sound BLOCK_ENDER_CHEST_OPEN = define("block.ender_chest.open");
/*  805 */   public static final Sound ENTITY_ENDER_DRAGON_AMBIENT = define("entity.ender_dragon.ambient");
/*  806 */   public static final Sound ENTITY_ENDER_DRAGON_DEATH = define("entity.ender_dragon.death");
/*  807 */   public static final Sound ENTITY_DRAGON_FIREBALL_EXPLODE = define("entity.dragon_fireball.explode");
/*  808 */   public static final Sound ENTITY_ENDER_DRAGON_FLAP = define("entity.ender_dragon.flap");
/*  809 */   public static final Sound ENTITY_ENDER_DRAGON_GROWL = define("entity.ender_dragon.growl");
/*  810 */   public static final Sound ENTITY_ENDER_DRAGON_HURT = define("entity.ender_dragon.hurt");
/*  811 */   public static final Sound ENTITY_ENDER_DRAGON_SHOOT = define("entity.ender_dragon.shoot");
/*  812 */   public static final Sound ENTITY_ENDER_EYE_DEATH = define("entity.ender_eye.death");
/*  813 */   public static final Sound ENTITY_ENDER_EYE_LAUNCH = define("entity.ender_eye.launch");
/*  814 */   public static final Sound ENTITY_ENDERMAN_AMBIENT = define("entity.enderman.ambient");
/*  815 */   public static final Sound ENTITY_ENDERMAN_DEATH = define("entity.enderman.death");
/*  816 */   public static final Sound ENTITY_ENDERMAN_HURT = define("entity.enderman.hurt");
/*  817 */   public static final Sound ENTITY_ENDERMAN_SCREAM = define("entity.enderman.scream");
/*  818 */   public static final Sound ENTITY_ENDERMAN_STARE = define("entity.enderman.stare");
/*  819 */   public static final Sound ENTITY_ENDERMAN_TELEPORT = define("entity.enderman.teleport");
/*  820 */   public static final Sound ENTITY_ENDERMITE_AMBIENT = define("entity.endermite.ambient");
/*  821 */   public static final Sound ENTITY_ENDERMITE_DEATH = define("entity.endermite.death");
/*  822 */   public static final Sound ENTITY_ENDERMITE_HURT = define("entity.endermite.hurt");
/*  823 */   public static final Sound ENTITY_ENDERMITE_STEP = define("entity.endermite.step");
/*  824 */   public static final Sound ENTITY_ENDER_PEARL_THROW = define("entity.ender_pearl.throw");
/*  825 */   public static final Sound BLOCK_END_GATEWAY_SPAWN = define("block.end_gateway.spawn");
/*  826 */   public static final Sound BLOCK_END_PORTAL_FRAME_FILL = define("block.end_portal_frame.fill");
/*  827 */   public static final Sound BLOCK_END_PORTAL_SPAWN = define("block.end_portal.spawn");
/*  828 */   public static final Sound ENTITY_EVOKER_AMBIENT = define("entity.evoker.ambient");
/*  829 */   public static final Sound ENTITY_EVOKER_CAST_SPELL = define("entity.evoker.cast_spell");
/*  830 */   public static final Sound ENTITY_EVOKER_CELEBRATE = define("entity.evoker.celebrate");
/*  831 */   public static final Sound ENTITY_EVOKER_DEATH = define("entity.evoker.death");
/*  832 */   public static final Sound ENTITY_EVOKER_FANGS_ATTACK = define("entity.evoker_fangs.attack");
/*  833 */   public static final Sound ENTITY_EVOKER_HURT = define("entity.evoker.hurt");
/*  834 */   public static final Sound ENTITY_EVOKER_PREPARE_ATTACK = define("entity.evoker.prepare_attack");
/*  835 */   public static final Sound ENTITY_EVOKER_PREPARE_SUMMON = define("entity.evoker.prepare_summon");
/*  836 */   public static final Sound ENTITY_EVOKER_PREPARE_WOLOLO = define("entity.evoker.prepare_wololo");
/*  837 */   public static final Sound ENTITY_EXPERIENCE_BOTTLE_THROW = define("entity.experience_bottle.throw");
/*  838 */   public static final Sound ENTITY_EXPERIENCE_ORB_PICKUP = define("entity.experience_orb.pickup");
/*  839 */   public static final Sound BLOCK_FENCE_GATE_CLOSE = define("block.fence_gate.close");
/*  840 */   public static final Sound BLOCK_FENCE_GATE_OPEN = define("block.fence_gate.open");
/*  841 */   public static final Sound ITEM_FIRECHARGE_USE = define("item.firecharge.use");
/*  842 */   public static final Sound ENTITY_FIREWORK_ROCKET_BLAST = define("entity.firework_rocket.blast");
/*  843 */   public static final Sound ENTITY_FIREWORK_ROCKET_BLAST_FAR = define("entity.firework_rocket.blast_far");
/*  844 */   public static final Sound ENTITY_FIREWORK_ROCKET_LARGE_BLAST = define("entity.firework_rocket.large_blast");
/*  845 */   public static final Sound ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR = define("entity.firework_rocket.large_blast_far");
/*  846 */   public static final Sound ENTITY_FIREWORK_ROCKET_LAUNCH = define("entity.firework_rocket.launch");
/*  847 */   public static final Sound ENTITY_FIREWORK_ROCKET_SHOOT = define("entity.firework_rocket.shoot");
/*  848 */   public static final Sound ENTITY_FIREWORK_ROCKET_TWINKLE = define("entity.firework_rocket.twinkle");
/*  849 */   public static final Sound ENTITY_FIREWORK_ROCKET_TWINKLE_FAR = define("entity.firework_rocket.twinkle_far");
/*  850 */   public static final Sound BLOCK_FIRE_AMBIENT = define("block.fire.ambient");
/*  851 */   public static final Sound BLOCK_FIRE_EXTINGUISH = define("block.fire.extinguish");
/*  852 */   public static final Sound ENTITY_FISH_SWIM = define("entity.fish.swim");
/*  853 */   public static final Sound ENTITY_FISHING_BOBBER_RETRIEVE = define("entity.fishing_bobber.retrieve");
/*  854 */   public static final Sound ENTITY_FISHING_BOBBER_SPLASH = define("entity.fishing_bobber.splash");
/*  855 */   public static final Sound ENTITY_FISHING_BOBBER_THROW = define("entity.fishing_bobber.throw");
/*  856 */   public static final Sound ITEM_FLINTANDSTEEL_USE = define("item.flintandsteel.use");
/*  857 */   public static final Sound BLOCK_FLOWERING_AZALEA_BREAK = define("block.flowering_azalea.break");
/*  858 */   public static final Sound BLOCK_FLOWERING_AZALEA_FALL = define("block.flowering_azalea.fall");
/*  859 */   public static final Sound BLOCK_FLOWERING_AZALEA_HIT = define("block.flowering_azalea.hit");
/*  860 */   public static final Sound BLOCK_FLOWERING_AZALEA_PLACE = define("block.flowering_azalea.place");
/*  861 */   public static final Sound BLOCK_FLOWERING_AZALEA_STEP = define("block.flowering_azalea.step");
/*  862 */   public static final Sound ENTITY_FOX_AGGRO = define("entity.fox.aggro");
/*  863 */   public static final Sound ENTITY_FOX_AMBIENT = define("entity.fox.ambient");
/*  864 */   public static final Sound ENTITY_FOX_BITE = define("entity.fox.bite");
/*  865 */   public static final Sound ENTITY_FOX_DEATH = define("entity.fox.death");
/*  866 */   public static final Sound ENTITY_FOX_EAT = define("entity.fox.eat");
/*  867 */   public static final Sound ENTITY_FOX_HURT = define("entity.fox.hurt");
/*  868 */   public static final Sound ENTITY_FOX_SCREECH = define("entity.fox.screech");
/*  869 */   public static final Sound ENTITY_FOX_SLEEP = define("entity.fox.sleep");
/*  870 */   public static final Sound ENTITY_FOX_SNIFF = define("entity.fox.sniff");
/*  871 */   public static final Sound ENTITY_FOX_SPIT = define("entity.fox.spit");
/*  872 */   public static final Sound ENTITY_FOX_TELEPORT = define("entity.fox.teleport");
/*  873 */   public static final Sound BLOCK_SUSPICIOUS_SAND_BREAK = define("block.suspicious_sand.break");
/*  874 */   public static final Sound BLOCK_SUSPICIOUS_SAND_STEP = define("block.suspicious_sand.step");
/*  875 */   public static final Sound BLOCK_SUSPICIOUS_SAND_PLACE = define("block.suspicious_sand.place");
/*  876 */   public static final Sound BLOCK_SUSPICIOUS_SAND_HIT = define("block.suspicious_sand.hit");
/*  877 */   public static final Sound BLOCK_SUSPICIOUS_SAND_FALL = define("block.suspicious_sand.fall");
/*  878 */   public static final Sound BLOCK_SUSPICIOUS_GRAVEL_BREAK = define("block.suspicious_gravel.break");
/*  879 */   public static final Sound BLOCK_SUSPICIOUS_GRAVEL_STEP = define("block.suspicious_gravel.step");
/*  880 */   public static final Sound BLOCK_SUSPICIOUS_GRAVEL_PLACE = define("block.suspicious_gravel.place");
/*  881 */   public static final Sound BLOCK_SUSPICIOUS_GRAVEL_HIT = define("block.suspicious_gravel.hit");
/*  882 */   public static final Sound BLOCK_SUSPICIOUS_GRAVEL_FALL = define("block.suspicious_gravel.fall");
/*  883 */   public static final Sound BLOCK_FROGLIGHT_BREAK = define("block.froglight.break");
/*  884 */   public static final Sound BLOCK_FROGLIGHT_FALL = define("block.froglight.fall");
/*  885 */   public static final Sound BLOCK_FROGLIGHT_HIT = define("block.froglight.hit");
/*  886 */   public static final Sound BLOCK_FROGLIGHT_PLACE = define("block.froglight.place");
/*  887 */   public static final Sound BLOCK_FROGLIGHT_STEP = define("block.froglight.step");
/*  888 */   public static final Sound BLOCK_FROGSPAWN_STEP = define("block.frogspawn.step");
/*  889 */   public static final Sound BLOCK_FROGSPAWN_BREAK = define("block.frogspawn.break");
/*  890 */   public static final Sound BLOCK_FROGSPAWN_FALL = define("block.frogspawn.fall");
/*  891 */   public static final Sound BLOCK_FROGSPAWN_HATCH = define("block.frogspawn.hatch");
/*  892 */   public static final Sound BLOCK_FROGSPAWN_HIT = define("block.frogspawn.hit");
/*  893 */   public static final Sound BLOCK_FROGSPAWN_PLACE = define("block.frogspawn.place");
/*  894 */   public static final Sound ENTITY_FROG_AMBIENT = define("entity.frog.ambient");
/*  895 */   public static final Sound ENTITY_FROG_DEATH = define("entity.frog.death");
/*  896 */   public static final Sound ENTITY_FROG_EAT = define("entity.frog.eat");
/*  897 */   public static final Sound ENTITY_FROG_HURT = define("entity.frog.hurt");
/*  898 */   public static final Sound ENTITY_FROG_LAY_SPAWN = define("entity.frog.lay_spawn");
/*  899 */   public static final Sound ENTITY_FROG_LONG_JUMP = define("entity.frog.long_jump");
/*  900 */   public static final Sound ENTITY_FROG_STEP = define("entity.frog.step");
/*  901 */   public static final Sound ENTITY_FROG_TONGUE = define("entity.frog.tongue");
/*  902 */   public static final Sound BLOCK_ROOTS_BREAK = define("block.roots.break");
/*  903 */   public static final Sound BLOCK_ROOTS_STEP = define("block.roots.step");
/*  904 */   public static final Sound BLOCK_ROOTS_PLACE = define("block.roots.place");
/*  905 */   public static final Sound BLOCK_ROOTS_HIT = define("block.roots.hit");
/*  906 */   public static final Sound BLOCK_ROOTS_FALL = define("block.roots.fall");
/*  907 */   public static final Sound BLOCK_FURNACE_FIRE_CRACKLE = define("block.furnace.fire_crackle");
/*  908 */   public static final Sound ENTITY_GENERIC_BIG_FALL = define("entity.generic.big_fall");
/*  909 */   public static final Sound ENTITY_GENERIC_BURN = define("entity.generic.burn");
/*  910 */   public static final Sound ENTITY_GENERIC_DEATH = define("entity.generic.death");
/*  911 */   public static final Sound ENTITY_GENERIC_DRINK = define("entity.generic.drink");
/*  912 */   public static final Sound ENTITY_GENERIC_EAT = define("entity.generic.eat");
/*  913 */   public static final Sound ENTITY_GENERIC_EXPLODE = define("entity.generic.explode");
/*  914 */   public static final Sound ENTITY_GENERIC_EXTINGUISH_FIRE = define("entity.generic.extinguish_fire");
/*  915 */   public static final Sound ENTITY_GENERIC_HURT = define("entity.generic.hurt");
/*  916 */   public static final Sound ENTITY_GENERIC_SMALL_FALL = define("entity.generic.small_fall");
/*  917 */   public static final Sound ENTITY_GENERIC_SPLASH = define("entity.generic.splash");
/*  918 */   public static final Sound ENTITY_GENERIC_SWIM = define("entity.generic.swim");
/*  919 */   public static final Sound ENTITY_GHAST_AMBIENT = define("entity.ghast.ambient");
/*  920 */   public static final Sound ENTITY_GHAST_DEATH = define("entity.ghast.death");
/*  921 */   public static final Sound ENTITY_GHAST_HURT = define("entity.ghast.hurt");
/*  922 */   public static final Sound ENTITY_GHAST_SCREAM = define("entity.ghast.scream");
/*  923 */   public static final Sound ENTITY_GHAST_SHOOT = define("entity.ghast.shoot");
/*  924 */   public static final Sound ENTITY_GHAST_WARN = define("entity.ghast.warn");
/*  925 */   public static final Sound BLOCK_GILDED_BLACKSTONE_BREAK = define("block.gilded_blackstone.break");
/*  926 */   public static final Sound BLOCK_GILDED_BLACKSTONE_FALL = define("block.gilded_blackstone.fall");
/*  927 */   public static final Sound BLOCK_GILDED_BLACKSTONE_HIT = define("block.gilded_blackstone.hit");
/*  928 */   public static final Sound BLOCK_GILDED_BLACKSTONE_PLACE = define("block.gilded_blackstone.place");
/*  929 */   public static final Sound BLOCK_GILDED_BLACKSTONE_STEP = define("block.gilded_blackstone.step");
/*  930 */   public static final Sound BLOCK_GLASS_BREAK = define("block.glass.break");
/*  931 */   public static final Sound BLOCK_GLASS_FALL = define("block.glass.fall");
/*  932 */   public static final Sound BLOCK_GLASS_HIT = define("block.glass.hit");
/*  933 */   public static final Sound BLOCK_GLASS_PLACE = define("block.glass.place");
/*  934 */   public static final Sound BLOCK_GLASS_STEP = define("block.glass.step");
/*  935 */   public static final Sound ITEM_GLOW_INK_SAC_USE = define("item.glow_ink_sac.use");
/*  936 */   public static final Sound ENTITY_GLOW_ITEM_FRAME_ADD_ITEM = define("entity.glow_item_frame.add_item");
/*  937 */   public static final Sound ENTITY_GLOW_ITEM_FRAME_BREAK = define("entity.glow_item_frame.break");
/*  938 */   public static final Sound ENTITY_GLOW_ITEM_FRAME_PLACE = define("entity.glow_item_frame.place");
/*  939 */   public static final Sound ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM = define("entity.glow_item_frame.remove_item");
/*  940 */   public static final Sound ENTITY_GLOW_ITEM_FRAME_ROTATE_ITEM = define("entity.glow_item_frame.rotate_item");
/*  941 */   public static final Sound ENTITY_GLOW_SQUID_AMBIENT = define("entity.glow_squid.ambient");
/*  942 */   public static final Sound ENTITY_GLOW_SQUID_DEATH = define("entity.glow_squid.death");
/*  943 */   public static final Sound ENTITY_GLOW_SQUID_HURT = define("entity.glow_squid.hurt");
/*  944 */   public static final Sound ENTITY_GLOW_SQUID_SQUIRT = define("entity.glow_squid.squirt");
/*  945 */   public static final Sound ENTITY_GOAT_AMBIENT = define("entity.goat.ambient");
/*  946 */   public static final Sound ENTITY_GOAT_DEATH = define("entity.goat.death");
/*  947 */   public static final Sound ENTITY_GOAT_EAT = define("entity.goat.eat");
/*  948 */   public static final Sound ENTITY_GOAT_HURT = define("entity.goat.hurt");
/*  949 */   public static final Sound ENTITY_GOAT_LONG_JUMP = define("entity.goat.long_jump");
/*  950 */   public static final Sound ENTITY_GOAT_MILK = define("entity.goat.milk");
/*  951 */   public static final Sound ENTITY_GOAT_PREPARE_RAM = define("entity.goat.prepare_ram");
/*  952 */   public static final Sound ENTITY_GOAT_RAM_IMPACT = define("entity.goat.ram_impact");
/*  953 */   public static final Sound ENTITY_GOAT_HORN_BREAK = define("entity.goat.horn_break");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/*  958 */   public static final Sound ITEM_GOAT_HORN_PLAY = define("item.goat_horn.play");
/*  959 */   public static final Sound ENTITY_GOAT_SCREAMING_AMBIENT = define("entity.goat.screaming.ambient");
/*  960 */   public static final Sound ENTITY_GOAT_SCREAMING_DEATH = define("entity.goat.screaming.death");
/*  961 */   public static final Sound ENTITY_GOAT_SCREAMING_EAT = define("entity.goat.screaming.eat");
/*  962 */   public static final Sound ENTITY_GOAT_SCREAMING_HURT = define("entity.goat.screaming.hurt");
/*  963 */   public static final Sound ENTITY_GOAT_SCREAMING_LONG_JUMP = define("entity.goat.screaming.long_jump");
/*  964 */   public static final Sound ENTITY_GOAT_SCREAMING_MILK = define("entity.goat.screaming.milk");
/*  965 */   public static final Sound ENTITY_GOAT_SCREAMING_PREPARE_RAM = define("entity.goat.screaming.prepare_ram");
/*  966 */   public static final Sound ENTITY_GOAT_SCREAMING_RAM_IMPACT = define("entity.goat.screaming.ram_impact");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/*  971 */   public static final Sound ENTITY_GOAT_SCREAMING_HORN_BREAK = define("entity.goat.screaming.horn_break");
/*  972 */   public static final Sound ENTITY_GOAT_STEP = define("entity.goat.step");
/*  973 */   public static final Sound BLOCK_GRASS_BREAK = define("block.grass.break");
/*  974 */   public static final Sound BLOCK_GRASS_FALL = define("block.grass.fall");
/*  975 */   public static final Sound BLOCK_GRASS_HIT = define("block.grass.hit");
/*  976 */   public static final Sound BLOCK_GRASS_PLACE = define("block.grass.place");
/*  977 */   public static final Sound BLOCK_GRASS_STEP = define("block.grass.step");
/*  978 */   public static final Sound BLOCK_GRAVEL_BREAK = define("block.gravel.break");
/*  979 */   public static final Sound BLOCK_GRAVEL_FALL = define("block.gravel.fall");
/*  980 */   public static final Sound BLOCK_GRAVEL_HIT = define("block.gravel.hit");
/*  981 */   public static final Sound BLOCK_GRAVEL_PLACE = define("block.gravel.place");
/*  982 */   public static final Sound BLOCK_GRAVEL_STEP = define("block.gravel.step");
/*  983 */   public static final Sound BLOCK_GRINDSTONE_USE = define("block.grindstone.use");
/*  984 */   public static final Sound BLOCK_GROWING_PLANT_CROP = define("block.growing_plant.crop");
/*  985 */   public static final Sound ENTITY_GUARDIAN_AMBIENT = define("entity.guardian.ambient");
/*  986 */   public static final Sound ENTITY_GUARDIAN_AMBIENT_LAND = define("entity.guardian.ambient_land");
/*  987 */   public static final Sound ENTITY_GUARDIAN_ATTACK = define("entity.guardian.attack");
/*  988 */   public static final Sound ENTITY_GUARDIAN_DEATH = define("entity.guardian.death");
/*  989 */   public static final Sound ENTITY_GUARDIAN_DEATH_LAND = define("entity.guardian.death_land");
/*  990 */   public static final Sound ENTITY_GUARDIAN_FLOP = define("entity.guardian.flop");
/*  991 */   public static final Sound ENTITY_GUARDIAN_HURT = define("entity.guardian.hurt");
/*  992 */   public static final Sound ENTITY_GUARDIAN_HURT_LAND = define("entity.guardian.hurt_land");
/*  993 */   public static final Sound BLOCK_HANGING_ROOTS_BREAK = define("block.hanging_roots.break");
/*  994 */   public static final Sound BLOCK_HANGING_ROOTS_FALL = define("block.hanging_roots.fall");
/*  995 */   public static final Sound BLOCK_HANGING_ROOTS_HIT = define("block.hanging_roots.hit");
/*  996 */   public static final Sound BLOCK_HANGING_ROOTS_PLACE = define("block.hanging_roots.place");
/*  997 */   public static final Sound BLOCK_HANGING_ROOTS_STEP = define("block.hanging_roots.step");
/*  998 */   public static final Sound BLOCK_HANGING_SIGN_STEP = define("block.hanging_sign.step");
/*  999 */   public static final Sound BLOCK_HANGING_SIGN_BREAK = define("block.hanging_sign.break");
/* 1000 */   public static final Sound BLOCK_HANGING_SIGN_FALL = define("block.hanging_sign.fall");
/* 1001 */   public static final Sound BLOCK_HANGING_SIGN_HIT = define("block.hanging_sign.hit");
/* 1002 */   public static final Sound BLOCK_HANGING_SIGN_PLACE = define("block.hanging_sign.place");
/* 1003 */   public static final Sound BLOCK_HEAVY_CORE_BREAK = define("block.heavy_core.break");
/* 1004 */   public static final Sound BLOCK_HEAVY_CORE_FALL = define("block.heavy_core.fall");
/* 1005 */   public static final Sound BLOCK_HEAVY_CORE_HIT = define("block.heavy_core.hit");
/* 1006 */   public static final Sound BLOCK_HEAVY_CORE_PLACE = define("block.heavy_core.place");
/* 1007 */   public static final Sound BLOCK_HEAVY_CORE_STEP = define("block.heavy_core.step");
/* 1008 */   public static final Sound BLOCK_NETHER_WOOD_HANGING_SIGN_STEP = define("block.nether_wood_hanging_sign.step");
/* 1009 */   public static final Sound BLOCK_NETHER_WOOD_HANGING_SIGN_BREAK = define("block.nether_wood_hanging_sign.break");
/* 1010 */   public static final Sound BLOCK_NETHER_WOOD_HANGING_SIGN_FALL = define("block.nether_wood_hanging_sign.fall");
/* 1011 */   public static final Sound BLOCK_NETHER_WOOD_HANGING_SIGN_HIT = define("block.nether_wood_hanging_sign.hit");
/* 1012 */   public static final Sound BLOCK_NETHER_WOOD_HANGING_SIGN_PLACE = define("block.nether_wood_hanging_sign.place");
/* 1013 */   public static final Sound BLOCK_BAMBOO_WOOD_HANGING_SIGN_STEP = define("block.bamboo_wood_hanging_sign.step");
/* 1014 */   public static final Sound BLOCK_BAMBOO_WOOD_HANGING_SIGN_BREAK = define("block.bamboo_wood_hanging_sign.break");
/* 1015 */   public static final Sound BLOCK_BAMBOO_WOOD_HANGING_SIGN_FALL = define("block.bamboo_wood_hanging_sign.fall");
/* 1016 */   public static final Sound BLOCK_BAMBOO_WOOD_HANGING_SIGN_HIT = define("block.bamboo_wood_hanging_sign.hit");
/* 1017 */   public static final Sound BLOCK_BAMBOO_WOOD_HANGING_SIGN_PLACE = define("block.bamboo_wood_hanging_sign.place");
/* 1018 */   public static final Sound BLOCK_TRIAL_SPAWNER_BREAK = define("block.trial_spawner.break");
/* 1019 */   public static final Sound BLOCK_TRIAL_SPAWNER_STEP = define("block.trial_spawner.step");
/* 1020 */   public static final Sound BLOCK_TRIAL_SPAWNER_PLACE = define("block.trial_spawner.place");
/* 1021 */   public static final Sound BLOCK_TRIAL_SPAWNER_HIT = define("block.trial_spawner.hit");
/* 1022 */   public static final Sound BLOCK_TRIAL_SPAWNER_FALL = define("block.trial_spawner.fall");
/* 1023 */   public static final Sound BLOCK_TRIAL_SPAWNER_SPAWN_MOB = define("block.trial_spawner.spawn_mob");
/* 1024 */   public static final Sound BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM = define("block.trial_spawner.about_to_spawn_item");
/* 1025 */   public static final Sound BLOCK_TRIAL_SPAWNER_SPAWN_ITEM = define("block.trial_spawner.spawn_item");
/* 1026 */   public static final Sound BLOCK_TRIAL_SPAWNER_SPAWN_ITEM_BEGIN = define("block.trial_spawner.spawn_item_begin");
/* 1027 */   public static final Sound BLOCK_TRIAL_SPAWNER_DETECT_PLAYER = define("block.trial_spawner.detect_player");
/* 1028 */   public static final Sound BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE = define("block.trial_spawner.ominous_activate");
/* 1029 */   public static final Sound BLOCK_TRIAL_SPAWNER_AMBIENT = define("block.trial_spawner.ambient");
/* 1030 */   public static final Sound BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS = define("block.trial_spawner.ambient_ominous");
/* 1031 */   public static final Sound BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER = define("block.trial_spawner.open_shutter");
/* 1032 */   public static final Sound BLOCK_TRIAL_SPAWNER_CLOSE_SHUTTER = define("block.trial_spawner.close_shutter");
/* 1033 */   public static final Sound BLOCK_TRIAL_SPAWNER_EJECT_ITEM = define("block.trial_spawner.eject_item");
/* 1034 */   public static final Sound ITEM_HOE_TILL = define("item.hoe.till");
/* 1035 */   public static final Sound ENTITY_HOGLIN_AMBIENT = define("entity.hoglin.ambient");
/* 1036 */   public static final Sound ENTITY_HOGLIN_ANGRY = define("entity.hoglin.angry");
/* 1037 */   public static final Sound ENTITY_HOGLIN_ATTACK = define("entity.hoglin.attack");
/* 1038 */   public static final Sound ENTITY_HOGLIN_CONVERTED_TO_ZOMBIFIED = define("entity.hoglin.converted_to_zombified");
/* 1039 */   public static final Sound ENTITY_HOGLIN_DEATH = define("entity.hoglin.death");
/* 1040 */   public static final Sound ENTITY_HOGLIN_HURT = define("entity.hoglin.hurt");
/* 1041 */   public static final Sound ENTITY_HOGLIN_RETREAT = define("entity.hoglin.retreat");
/* 1042 */   public static final Sound ENTITY_HOGLIN_STEP = define("entity.hoglin.step");
/* 1043 */   public static final Sound BLOCK_HONEY_BLOCK_BREAK = define("block.honey_block.break");
/* 1044 */   public static final Sound BLOCK_HONEY_BLOCK_FALL = define("block.honey_block.fall");
/* 1045 */   public static final Sound BLOCK_HONEY_BLOCK_HIT = define("block.honey_block.hit");
/* 1046 */   public static final Sound BLOCK_HONEY_BLOCK_PLACE = define("block.honey_block.place");
/* 1047 */   public static final Sound BLOCK_HONEY_BLOCK_SLIDE = define("block.honey_block.slide");
/* 1048 */   public static final Sound BLOCK_HONEY_BLOCK_STEP = define("block.honey_block.step");
/* 1049 */   public static final Sound ITEM_HONEYCOMB_WAX_ON = define("item.honeycomb.wax_on");
/* 1050 */   public static final Sound ITEM_HONEY_BOTTLE_DRINK = define("item.honey_bottle.drink");
/* 1051 */   public static final Sound ITEM_GOAT_HORN_SOUND_0 = define("item.goat_horn.sound.0");
/* 1052 */   public static final Sound ITEM_GOAT_HORN_SOUND_1 = define("item.goat_horn.sound.1");
/* 1053 */   public static final Sound ITEM_GOAT_HORN_SOUND_2 = define("item.goat_horn.sound.2");
/* 1054 */   public static final Sound ITEM_GOAT_HORN_SOUND_3 = define("item.goat_horn.sound.3");
/* 1055 */   public static final Sound ITEM_GOAT_HORN_SOUND_4 = define("item.goat_horn.sound.4");
/* 1056 */   public static final Sound ITEM_GOAT_HORN_SOUND_5 = define("item.goat_horn.sound.5");
/* 1057 */   public static final Sound ITEM_GOAT_HORN_SOUND_6 = define("item.goat_horn.sound.6");
/* 1058 */   public static final Sound ITEM_GOAT_HORN_SOUND_7 = define("item.goat_horn.sound.7");
/* 1059 */   public static final Sound ENTITY_HORSE_AMBIENT = define("entity.horse.ambient");
/* 1060 */   public static final Sound ENTITY_HORSE_ANGRY = define("entity.horse.angry");
/* 1061 */   public static final Sound ENTITY_HORSE_ARMOR = define("entity.horse.armor");
/* 1062 */   public static final Sound ENTITY_HORSE_BREATHE = define("entity.horse.breathe");
/* 1063 */   public static final Sound ENTITY_HORSE_DEATH = define("entity.horse.death");
/* 1064 */   public static final Sound ENTITY_HORSE_EAT = define("entity.horse.eat");
/* 1065 */   public static final Sound ENTITY_HORSE_GALLOP = define("entity.horse.gallop");
/* 1066 */   public static final Sound ENTITY_HORSE_HURT = define("entity.horse.hurt");
/* 1067 */   public static final Sound ENTITY_HORSE_JUMP = define("entity.horse.jump");
/* 1068 */   public static final Sound ENTITY_HORSE_LAND = define("entity.horse.land");
/* 1069 */   public static final Sound ENTITY_HORSE_SADDLE = define("entity.horse.saddle");
/* 1070 */   public static final Sound ENTITY_HORSE_STEP = define("entity.horse.step");
/* 1071 */   public static final Sound ENTITY_HORSE_STEP_WOOD = define("entity.horse.step_wood");
/* 1072 */   public static final Sound ENTITY_HOSTILE_BIG_FALL = define("entity.hostile.big_fall");
/* 1073 */   public static final Sound ENTITY_HOSTILE_DEATH = define("entity.hostile.death");
/* 1074 */   public static final Sound ENTITY_HOSTILE_HURT = define("entity.hostile.hurt");
/* 1075 */   public static final Sound ENTITY_HOSTILE_SMALL_FALL = define("entity.hostile.small_fall");
/* 1076 */   public static final Sound ENTITY_HOSTILE_SPLASH = define("entity.hostile.splash");
/* 1077 */   public static final Sound ENTITY_HOSTILE_SWIM = define("entity.hostile.swim");
/* 1078 */   public static final Sound ENTITY_HUSK_AMBIENT = define("entity.husk.ambient");
/* 1079 */   public static final Sound ENTITY_HUSK_CONVERTED_TO_ZOMBIE = define("entity.husk.converted_to_zombie");
/* 1080 */   public static final Sound ENTITY_HUSK_DEATH = define("entity.husk.death");
/* 1081 */   public static final Sound ENTITY_HUSK_HURT = define("entity.husk.hurt");
/* 1082 */   public static final Sound ENTITY_HUSK_STEP = define("entity.husk.step");
/* 1083 */   public static final Sound ENTITY_ILLUSIONER_AMBIENT = define("entity.illusioner.ambient");
/* 1084 */   public static final Sound ENTITY_ILLUSIONER_CAST_SPELL = define("entity.illusioner.cast_spell");
/* 1085 */   public static final Sound ENTITY_ILLUSIONER_DEATH = define("entity.illusioner.death");
/* 1086 */   public static final Sound ENTITY_ILLUSIONER_HURT = define("entity.illusioner.hurt");
/* 1087 */   public static final Sound ENTITY_ILLUSIONER_MIRROR_MOVE = define("entity.illusioner.mirror_move");
/* 1088 */   public static final Sound ENTITY_ILLUSIONER_PREPARE_BLINDNESS = define("entity.illusioner.prepare_blindness");
/* 1089 */   public static final Sound ENTITY_ILLUSIONER_PREPARE_MIRROR = define("entity.illusioner.prepare_mirror");
/* 1090 */   public static final Sound ITEM_INK_SAC_USE = define("item.ink_sac.use");
/* 1091 */   public static final Sound BLOCK_IRON_DOOR_CLOSE = define("block.iron_door.close");
/* 1092 */   public static final Sound BLOCK_IRON_DOOR_OPEN = define("block.iron_door.open");
/* 1093 */   public static final Sound ENTITY_IRON_GOLEM_ATTACK = define("entity.iron_golem.attack");
/* 1094 */   public static final Sound ENTITY_IRON_GOLEM_DAMAGE = define("entity.iron_golem.damage");
/* 1095 */   public static final Sound ENTITY_IRON_GOLEM_DEATH = define("entity.iron_golem.death");
/* 1096 */   public static final Sound ENTITY_IRON_GOLEM_HURT = define("entity.iron_golem.hurt");
/* 1097 */   public static final Sound ENTITY_IRON_GOLEM_REPAIR = define("entity.iron_golem.repair");
/* 1098 */   public static final Sound ENTITY_IRON_GOLEM_STEP = define("entity.iron_golem.step");
/* 1099 */   public static final Sound BLOCK_IRON_TRAPDOOR_CLOSE = define("block.iron_trapdoor.close");
/* 1100 */   public static final Sound BLOCK_IRON_TRAPDOOR_OPEN = define("block.iron_trapdoor.open");
/* 1101 */   public static final Sound ENTITY_ITEM_FRAME_ADD_ITEM = define("entity.item_frame.add_item");
/* 1102 */   public static final Sound ENTITY_ITEM_FRAME_BREAK = define("entity.item_frame.break");
/* 1103 */   public static final Sound ENTITY_ITEM_FRAME_PLACE = define("entity.item_frame.place");
/* 1104 */   public static final Sound ENTITY_ITEM_FRAME_REMOVE_ITEM = define("entity.item_frame.remove_item");
/* 1105 */   public static final Sound ENTITY_ITEM_FRAME_ROTATE_ITEM = define("entity.item_frame.rotate_item");
/* 1106 */   public static final Sound ENTITY_ITEM_BREAK = define("entity.item.break");
/* 1107 */   public static final Sound ENTITY_ITEM_PICKUP = define("entity.item.pickup");
/* 1108 */   public static final Sound BLOCK_LADDER_BREAK = define("block.ladder.break");
/* 1109 */   public static final Sound BLOCK_LADDER_FALL = define("block.ladder.fall");
/* 1110 */   public static final Sound BLOCK_LADDER_HIT = define("block.ladder.hit");
/* 1111 */   public static final Sound BLOCK_LADDER_PLACE = define("block.ladder.place");
/* 1112 */   public static final Sound BLOCK_LADDER_STEP = define("block.ladder.step");
/* 1113 */   public static final Sound BLOCK_LANTERN_BREAK = define("block.lantern.break");
/* 1114 */   public static final Sound BLOCK_LANTERN_FALL = define("block.lantern.fall");
/* 1115 */   public static final Sound BLOCK_LANTERN_HIT = define("block.lantern.hit");
/* 1116 */   public static final Sound BLOCK_LANTERN_PLACE = define("block.lantern.place");
/* 1117 */   public static final Sound BLOCK_LANTERN_STEP = define("block.lantern.step");
/* 1118 */   public static final Sound BLOCK_LARGE_AMETHYST_BUD_BREAK = define("block.large_amethyst_bud.break");
/* 1119 */   public static final Sound BLOCK_LARGE_AMETHYST_BUD_PLACE = define("block.large_amethyst_bud.place");
/* 1120 */   public static final Sound BLOCK_LAVA_AMBIENT = define("block.lava.ambient");
/* 1121 */   public static final Sound BLOCK_LAVA_EXTINGUISH = define("block.lava.extinguish");
/* 1122 */   public static final Sound BLOCK_LAVA_POP = define("block.lava.pop");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/* 1127 */   public static final Sound ENTITY_LEASH_KNOT_BREAK = define("entity.leash_knot.break");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/* 1132 */   public static final Sound ENTITY_LEASH_KNOT_PLACE = define("entity.leash_knot.place");
/* 1133 */   public static final Sound BLOCK_LEVER_CLICK = define("block.lever.click");
/* 1134 */   public static final Sound ENTITY_LIGHTNING_BOLT_IMPACT = define("entity.lightning_bolt.impact");
/* 1135 */   public static final Sound ENTITY_LIGHTNING_BOLT_THUNDER = define("entity.lightning_bolt.thunder");
/* 1136 */   public static final Sound ENTITY_LINGERING_POTION_THROW = define("entity.lingering_potion.throw");
/* 1137 */   public static final Sound ENTITY_LLAMA_AMBIENT = define("entity.llama.ambient");
/* 1138 */   public static final Sound ENTITY_LLAMA_ANGRY = define("entity.llama.angry");
/* 1139 */   public static final Sound ENTITY_LLAMA_CHEST = define("entity.llama.chest");
/* 1140 */   public static final Sound ENTITY_LLAMA_DEATH = define("entity.llama.death");
/* 1141 */   public static final Sound ENTITY_LLAMA_EAT = define("entity.llama.eat");
/* 1142 */   public static final Sound ENTITY_LLAMA_HURT = define("entity.llama.hurt");
/* 1143 */   public static final Sound ENTITY_LLAMA_SPIT = define("entity.llama.spit");
/* 1144 */   public static final Sound ENTITY_LLAMA_STEP = define("entity.llama.step");
/* 1145 */   public static final Sound ENTITY_LLAMA_SWAG = define("entity.llama.swag");
/* 1146 */   public static final Sound ENTITY_MAGMA_CUBE_DEATH_SMALL = define("entity.magma_cube.death_small");
/* 1147 */   public static final Sound BLOCK_LODESTONE_BREAK = define("block.lodestone.break");
/* 1148 */   public static final Sound BLOCK_LODESTONE_STEP = define("block.lodestone.step");
/* 1149 */   public static final Sound BLOCK_LODESTONE_PLACE = define("block.lodestone.place");
/* 1150 */   public static final Sound BLOCK_LODESTONE_HIT = define("block.lodestone.hit");
/* 1151 */   public static final Sound BLOCK_LODESTONE_FALL = define("block.lodestone.fall");
/* 1152 */   public static final Sound ITEM_LODESTONE_COMPASS_LOCK = define("item.lodestone_compass.lock");
/* 1153 */   public static final Sound ITEM_MACE_SMASH_AIR = define("item.mace.smash_air");
/* 1154 */   public static final Sound ITEM_MACE_SMASH_GROUND = define("item.mace.smash_ground");
/* 1155 */   public static final Sound ITEM_MACE_SMASH_GROUND_HEAVY = define("item.mace.smash_ground_heavy");
/* 1156 */   public static final Sound ENTITY_MAGMA_CUBE_DEATH = define("entity.magma_cube.death");
/* 1157 */   public static final Sound ENTITY_MAGMA_CUBE_HURT = define("entity.magma_cube.hurt");
/* 1158 */   public static final Sound ENTITY_MAGMA_CUBE_HURT_SMALL = define("entity.magma_cube.hurt_small");
/* 1159 */   public static final Sound ENTITY_MAGMA_CUBE_JUMP = define("entity.magma_cube.jump");
/* 1160 */   public static final Sound ENTITY_MAGMA_CUBE_SQUISH = define("entity.magma_cube.squish");
/* 1161 */   public static final Sound ENTITY_MAGMA_CUBE_SQUISH_SMALL = define("entity.magma_cube.squish_small");
/* 1162 */   public static final Sound BLOCK_MANGROVE_ROOTS_BREAK = define("block.mangrove_roots.break");
/* 1163 */   public static final Sound BLOCK_MANGROVE_ROOTS_FALL = define("block.mangrove_roots.fall");
/* 1164 */   public static final Sound BLOCK_MANGROVE_ROOTS_HIT = define("block.mangrove_roots.hit");
/* 1165 */   public static final Sound BLOCK_MANGROVE_ROOTS_PLACE = define("block.mangrove_roots.place");
/* 1166 */   public static final Sound BLOCK_MANGROVE_ROOTS_STEP = define("block.mangrove_roots.step");
/* 1167 */   public static final Sound BLOCK_MEDIUM_AMETHYST_BUD_BREAK = define("block.medium_amethyst_bud.break");
/* 1168 */   public static final Sound BLOCK_MEDIUM_AMETHYST_BUD_PLACE = define("block.medium_amethyst_bud.place");
/* 1169 */   public static final Sound BLOCK_METAL_BREAK = define("block.metal.break");
/* 1170 */   public static final Sound BLOCK_METAL_FALL = define("block.metal.fall");
/* 1171 */   public static final Sound BLOCK_METAL_HIT = define("block.metal.hit");
/* 1172 */   public static final Sound BLOCK_METAL_PLACE = define("block.metal.place");
/* 1173 */   public static final Sound BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF = define("block.metal_pressure_plate.click_off");
/* 1174 */   public static final Sound BLOCK_METAL_PRESSURE_PLATE_CLICK_ON = define("block.metal_pressure_plate.click_on");
/* 1175 */   public static final Sound BLOCK_METAL_STEP = define("block.metal.step");
/* 1176 */   public static final Sound ENTITY_MINECART_INSIDE_UNDERWATER = define("entity.minecart.inside.underwater");
/* 1177 */   public static final Sound ENTITY_MINECART_INSIDE = define("entity.minecart.inside");
/* 1178 */   public static final Sound ENTITY_MINECART_RIDING = define("entity.minecart.riding");
/* 1179 */   public static final Sound ENTITY_MOOSHROOM_CONVERT = define("entity.mooshroom.convert");
/* 1180 */   public static final Sound ENTITY_MOOSHROOM_EAT = define("entity.mooshroom.eat");
/* 1181 */   public static final Sound ENTITY_MOOSHROOM_MILK = define("entity.mooshroom.milk");
/* 1182 */   public static final Sound ENTITY_MOOSHROOM_SUSPICIOUS_MILK = define("entity.mooshroom.suspicious_milk");
/* 1183 */   public static final Sound ENTITY_MOOSHROOM_SHEAR = define("entity.mooshroom.shear");
/* 1184 */   public static final Sound BLOCK_MOSS_CARPET_BREAK = define("block.moss_carpet.break");
/* 1185 */   public static final Sound BLOCK_MOSS_CARPET_FALL = define("block.moss_carpet.fall");
/* 1186 */   public static final Sound BLOCK_MOSS_CARPET_HIT = define("block.moss_carpet.hit");
/* 1187 */   public static final Sound BLOCK_MOSS_CARPET_PLACE = define("block.moss_carpet.place");
/* 1188 */   public static final Sound BLOCK_MOSS_CARPET_STEP = define("block.moss_carpet.step");
/* 1189 */   public static final Sound BLOCK_PINK_PETALS_BREAK = define("block.pink_petals.break");
/* 1190 */   public static final Sound BLOCK_PINK_PETALS_FALL = define("block.pink_petals.fall");
/* 1191 */   public static final Sound BLOCK_PINK_PETALS_HIT = define("block.pink_petals.hit");
/* 1192 */   public static final Sound BLOCK_PINK_PETALS_PLACE = define("block.pink_petals.place");
/* 1193 */   public static final Sound BLOCK_PINK_PETALS_STEP = define("block.pink_petals.step");
/* 1194 */   public static final Sound BLOCK_MOSS_BREAK = define("block.moss.break");
/* 1195 */   public static final Sound BLOCK_MOSS_FALL = define("block.moss.fall");
/* 1196 */   public static final Sound BLOCK_MOSS_HIT = define("block.moss.hit");
/* 1197 */   public static final Sound BLOCK_MOSS_PLACE = define("block.moss.place");
/* 1198 */   public static final Sound BLOCK_MOSS_STEP = define("block.moss.step");
/* 1199 */   public static final Sound BLOCK_MUD_BREAK = define("block.mud.break");
/* 1200 */   public static final Sound BLOCK_MUD_FALL = define("block.mud.fall");
/* 1201 */   public static final Sound BLOCK_MUD_HIT = define("block.mud.hit");
/* 1202 */   public static final Sound BLOCK_MUD_PLACE = define("block.mud.place");
/* 1203 */   public static final Sound BLOCK_MUD_STEP = define("block.mud.step");
/* 1204 */   public static final Sound BLOCK_MUD_BRICKS_BREAK = define("block.mud_bricks.break");
/* 1205 */   public static final Sound BLOCK_MUD_BRICKS_FALL = define("block.mud_bricks.fall");
/* 1206 */   public static final Sound BLOCK_MUD_BRICKS_HIT = define("block.mud_bricks.hit");
/* 1207 */   public static final Sound BLOCK_MUD_BRICKS_PLACE = define("block.mud_bricks.place");
/* 1208 */   public static final Sound BLOCK_MUD_BRICKS_STEP = define("block.mud_bricks.step");
/* 1209 */   public static final Sound BLOCK_MUDDY_MANGROVE_ROOTS_BREAK = define("block.muddy_mangrove_roots.break");
/* 1210 */   public static final Sound BLOCK_MUDDY_MANGROVE_ROOTS_FALL = define("block.muddy_mangrove_roots.fall");
/* 1211 */   public static final Sound BLOCK_MUDDY_MANGROVE_ROOTS_HIT = define("block.muddy_mangrove_roots.hit");
/* 1212 */   public static final Sound BLOCK_MUDDY_MANGROVE_ROOTS_PLACE = define("block.muddy_mangrove_roots.place");
/* 1213 */   public static final Sound BLOCK_MUDDY_MANGROVE_ROOTS_STEP = define("block.muddy_mangrove_roots.step");
/* 1214 */   public static final Sound ENTITY_MULE_AMBIENT = define("entity.mule.ambient");
/* 1215 */   public static final Sound ENTITY_MULE_ANGRY = define("entity.mule.angry");
/* 1216 */   public static final Sound ENTITY_MULE_CHEST = define("entity.mule.chest");
/* 1217 */   public static final Sound ENTITY_MULE_DEATH = define("entity.mule.death");
/* 1218 */   public static final Sound ENTITY_MULE_EAT = define("entity.mule.eat");
/* 1219 */   public static final Sound ENTITY_MULE_HURT = define("entity.mule.hurt");
/* 1220 */   public static final Sound ENTITY_MULE_JUMP = define("entity.mule.jump");
/* 1221 */   public static final Sound MUSIC_CREATIVE = define("music.creative");
/* 1222 */   public static final Sound MUSIC_CREDITS = define("music.credits");
/* 1223 */   public static final Sound MUSIC_DISC_5 = define("music_disc.5");
/* 1224 */   public static final Sound MUSIC_DISC_11 = define("music_disc.11");
/* 1225 */   public static final Sound MUSIC_DISC_13 = define("music_disc.13");
/* 1226 */   public static final Sound MUSIC_DISC_BLOCKS = define("music_disc.blocks");
/* 1227 */   public static final Sound MUSIC_DISC_CAT = define("music_disc.cat");
/* 1228 */   public static final Sound MUSIC_DISC_CHIRP = define("music_disc.chirp");
/* 1229 */   public static final Sound MUSIC_DISC_FAR = define("music_disc.far");
/* 1230 */   public static final Sound MUSIC_DISC_MALL = define("music_disc.mall");
/* 1231 */   public static final Sound MUSIC_DISC_MELLOHI = define("music_disc.mellohi");
/* 1232 */   public static final Sound MUSIC_DISC_PIGSTEP = define("music_disc.pigstep");
/* 1233 */   public static final Sound MUSIC_DISC_STAL = define("music_disc.stal");
/* 1234 */   public static final Sound MUSIC_DISC_STRAD = define("music_disc.strad");
/* 1235 */   public static final Sound MUSIC_DISC_WAIT = define("music_disc.wait");
/* 1236 */   public static final Sound MUSIC_DISC_WARD = define("music_disc.ward");
/* 1237 */   public static final Sound MUSIC_DISC_OTHERSIDE = define("music_disc.otherside");
/* 1238 */   public static final Sound MUSIC_DISC_RELIC = define("music_disc.relic");
/* 1239 */   public static final Sound MUSIC_DRAGON = define("music.dragon");
/* 1240 */   public static final Sound MUSIC_END = define("music.end");
/* 1241 */   public static final Sound MUSIC_GAME = define("music.game");
/* 1242 */   public static final Sound MUSIC_MENU = define("music.menu");
/* 1243 */   public static final Sound MUSIC_NETHER_BASALT_DELTAS = define("music.nether.basalt_deltas");
/* 1244 */   public static final Sound MUSIC_NETHER_CRIMSON_FOREST = define("music.nether.crimson_forest");
/* 1245 */   public static final Sound MUSIC_OVERWORLD_DEEP_DARK = define("music.overworld.deep_dark");
/* 1246 */   public static final Sound MUSIC_OVERWORLD_DRIPSTONE_CAVES = define("music.overworld.dripstone_caves");
/* 1247 */   public static final Sound MUSIC_OVERWORLD_GROVE = define("music.overworld.grove");
/* 1248 */   public static final Sound MUSIC_OVERWORLD_JAGGED_PEAKS = define("music.overworld.jagged_peaks");
/* 1249 */   public static final Sound MUSIC_OVERWORLD_LUSH_CAVES = define("music.overworld.lush_caves");
/* 1250 */   public static final Sound MUSIC_OVERWORLD_SWAMP = define("music.overworld.swamp");
/* 1251 */   public static final Sound MUSIC_OVERWORLD_FOREST = define("music.overworld.forest");
/* 1252 */   public static final Sound MUSIC_OVERWORLD_OLD_GROWTH_TAIGA = define("music.overworld.old_growth_taiga");
/* 1253 */   public static final Sound MUSIC_OVERWORLD_MEADOW = define("music.overworld.meadow");
/* 1254 */   public static final Sound MUSIC_OVERWORLD_CHERRY_GROVE = define("music.overworld.cherry_grove");
/* 1255 */   public static final Sound MUSIC_NETHER_NETHER_WASTES = define("music.nether.nether_wastes");
/* 1256 */   public static final Sound MUSIC_OVERWORLD_FROZEN_PEAKS = define("music.overworld.frozen_peaks");
/* 1257 */   public static final Sound MUSIC_OVERWORLD_SNOWY_SLOPES = define("music.overworld.snowy_slopes");
/* 1258 */   public static final Sound MUSIC_NETHER_SOUL_SAND_VALLEY = define("music.nether.soul_sand_valley");
/* 1259 */   public static final Sound MUSIC_OVERWORLD_STONY_PEAKS = define("music.overworld.stony_peaks");
/* 1260 */   public static final Sound MUSIC_NETHER_WARPED_FOREST = define("music.nether.warped_forest");
/* 1261 */   public static final Sound MUSIC_OVERWORLD_FLOWER_FOREST = define("music.overworld.flower_forest");
/* 1262 */   public static final Sound MUSIC_OVERWORLD_DESERT = define("music.overworld.desert");
/* 1263 */   public static final Sound MUSIC_OVERWORLD_BADLANDS = define("music.overworld.badlands");
/* 1264 */   public static final Sound MUSIC_OVERWORLD_JUNGLE = define("music.overworld.jungle");
/* 1265 */   public static final Sound MUSIC_OVERWORLD_SPARSE_JUNGLE = define("music.overworld.sparse_jungle");
/* 1266 */   public static final Sound MUSIC_OVERWORLD_BAMBOO_JUNGLE = define("music.overworld.bamboo_jungle");
/* 1267 */   public static final Sound MUSIC_UNDER_WATER = define("music.under_water");
/* 1268 */   public static final Sound BLOCK_NETHER_BRICKS_BREAK = define("block.nether_bricks.break");
/* 1269 */   public static final Sound BLOCK_NETHER_BRICKS_STEP = define("block.nether_bricks.step");
/* 1270 */   public static final Sound BLOCK_NETHER_BRICKS_PLACE = define("block.nether_bricks.place");
/* 1271 */   public static final Sound BLOCK_NETHER_BRICKS_HIT = define("block.nether_bricks.hit");
/* 1272 */   public static final Sound BLOCK_NETHER_BRICKS_FALL = define("block.nether_bricks.fall");
/* 1273 */   public static final Sound BLOCK_NETHER_WART_BREAK = define("block.nether_wart.break");
/* 1274 */   public static final Sound ITEM_NETHER_WART_PLANT = define("item.nether_wart.plant");
/* 1275 */   public static final Sound BLOCK_NETHER_WOOD_BREAK = define("block.nether_wood.break");
/* 1276 */   public static final Sound BLOCK_NETHER_WOOD_FALL = define("block.nether_wood.fall");
/* 1277 */   public static final Sound BLOCK_NETHER_WOOD_HIT = define("block.nether_wood.hit");
/* 1278 */   public static final Sound BLOCK_NETHER_WOOD_PLACE = define("block.nether_wood.place");
/* 1279 */   public static final Sound BLOCK_NETHER_WOOD_STEP = define("block.nether_wood.step");
/* 1280 */   public static final Sound BLOCK_NETHER_WOOD_DOOR_CLOSE = define("block.nether_wood_door.close");
/* 1281 */   public static final Sound BLOCK_NETHER_WOOD_DOOR_OPEN = define("block.nether_wood_door.open");
/* 1282 */   public static final Sound BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE = define("block.nether_wood_trapdoor.close");
/* 1283 */   public static final Sound BLOCK_NETHER_WOOD_TRAPDOOR_OPEN = define("block.nether_wood_trapdoor.open");
/* 1284 */   public static final Sound BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF = define("block.nether_wood_button.click_off");
/* 1285 */   public static final Sound BLOCK_NETHER_WOOD_BUTTON_CLICK_ON = define("block.nether_wood_button.click_on");
/* 1286 */   public static final Sound BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF = define("block.nether_wood_pressure_plate.click_off");
/* 1287 */   public static final Sound BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON = define("block.nether_wood_pressure_plate.click_on");
/* 1288 */   public static final Sound BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE = define("block.nether_wood_fence_gate.close");
/* 1289 */   public static final Sound BLOCK_NETHER_WOOD_FENCE_GATE_OPEN = define("block.nether_wood_fence_gate.open");
/* 1290 */   public static final Sound INTENTIONALLY_EMPTY = define("intentionally_empty");
/* 1291 */   public static final Sound BLOCK_PACKED_MUD_BREAK = define("block.packed_mud.break");
/* 1292 */   public static final Sound BLOCK_PACKED_MUD_FALL = define("block.packed_mud.fall");
/* 1293 */   public static final Sound BLOCK_PACKED_MUD_HIT = define("block.packed_mud.hit");
/* 1294 */   public static final Sound BLOCK_PACKED_MUD_PLACE = define("block.packed_mud.place");
/* 1295 */   public static final Sound BLOCK_PACKED_MUD_STEP = define("block.packed_mud.step");
/* 1296 */   public static final Sound BLOCK_STEM_BREAK = define("block.stem.break");
/* 1297 */   public static final Sound BLOCK_STEM_STEP = define("block.stem.step");
/* 1298 */   public static final Sound BLOCK_STEM_PLACE = define("block.stem.place");
/* 1299 */   public static final Sound BLOCK_STEM_HIT = define("block.stem.hit");
/* 1300 */   public static final Sound BLOCK_STEM_FALL = define("block.stem.fall");
/* 1301 */   public static final Sound BLOCK_NYLIUM_BREAK = define("block.nylium.break");
/* 1302 */   public static final Sound BLOCK_NYLIUM_STEP = define("block.nylium.step");
/* 1303 */   public static final Sound BLOCK_NYLIUM_PLACE = define("block.nylium.place");
/* 1304 */   public static final Sound BLOCK_NYLIUM_HIT = define("block.nylium.hit");
/* 1305 */   public static final Sound BLOCK_NYLIUM_FALL = define("block.nylium.fall");
/* 1306 */   public static final Sound BLOCK_NETHER_SPROUTS_BREAK = define("block.nether_sprouts.break");
/* 1307 */   public static final Sound BLOCK_NETHER_SPROUTS_STEP = define("block.nether_sprouts.step");
/* 1308 */   public static final Sound BLOCK_NETHER_SPROUTS_PLACE = define("block.nether_sprouts.place");
/* 1309 */   public static final Sound BLOCK_NETHER_SPROUTS_HIT = define("block.nether_sprouts.hit");
/* 1310 */   public static final Sound BLOCK_NETHER_SPROUTS_FALL = define("block.nether_sprouts.fall");
/* 1311 */   public static final Sound BLOCK_FUNGUS_BREAK = define("block.fungus.break");
/* 1312 */   public static final Sound BLOCK_FUNGUS_STEP = define("block.fungus.step");
/* 1313 */   public static final Sound BLOCK_FUNGUS_PLACE = define("block.fungus.place");
/* 1314 */   public static final Sound BLOCK_FUNGUS_HIT = define("block.fungus.hit");
/* 1315 */   public static final Sound BLOCK_FUNGUS_FALL = define("block.fungus.fall");
/* 1316 */   public static final Sound BLOCK_WEEPING_VINES_BREAK = define("block.weeping_vines.break");
/* 1317 */   public static final Sound BLOCK_WEEPING_VINES_STEP = define("block.weeping_vines.step");
/* 1318 */   public static final Sound BLOCK_WEEPING_VINES_PLACE = define("block.weeping_vines.place");
/* 1319 */   public static final Sound BLOCK_WEEPING_VINES_HIT = define("block.weeping_vines.hit");
/* 1320 */   public static final Sound BLOCK_WEEPING_VINES_FALL = define("block.weeping_vines.fall");
/* 1321 */   public static final Sound BLOCK_WART_BLOCK_BREAK = define("block.wart_block.break");
/* 1322 */   public static final Sound BLOCK_WART_BLOCK_STEP = define("block.wart_block.step");
/* 1323 */   public static final Sound BLOCK_WART_BLOCK_PLACE = define("block.wart_block.place");
/* 1324 */   public static final Sound BLOCK_WART_BLOCK_HIT = define("block.wart_block.hit");
/* 1325 */   public static final Sound BLOCK_WART_BLOCK_FALL = define("block.wart_block.fall");
/* 1326 */   public static final Sound BLOCK_NETHERITE_BLOCK_BREAK = define("block.netherite_block.break");
/* 1327 */   public static final Sound BLOCK_NETHERITE_BLOCK_STEP = define("block.netherite_block.step");
/* 1328 */   public static final Sound BLOCK_NETHERITE_BLOCK_PLACE = define("block.netherite_block.place");
/* 1329 */   public static final Sound BLOCK_NETHERITE_BLOCK_HIT = define("block.netherite_block.hit");
/* 1330 */   public static final Sound BLOCK_NETHERITE_BLOCK_FALL = define("block.netherite_block.fall");
/* 1331 */   public static final Sound BLOCK_NETHERRACK_BREAK = define("block.netherrack.break");
/* 1332 */   public static final Sound BLOCK_NETHERRACK_STEP = define("block.netherrack.step");
/* 1333 */   public static final Sound BLOCK_NETHERRACK_PLACE = define("block.netherrack.place");
/* 1334 */   public static final Sound BLOCK_NETHERRACK_HIT = define("block.netherrack.hit");
/* 1335 */   public static final Sound BLOCK_NETHERRACK_FALL = define("block.netherrack.fall");
/* 1336 */   public static final Sound BLOCK_NOTE_BLOCK_BASEDRUM = define("block.note_block.basedrum");
/* 1337 */   public static final Sound BLOCK_NOTE_BLOCK_BASS = define("block.note_block.bass");
/* 1338 */   public static final Sound BLOCK_NOTE_BLOCK_BELL = define("block.note_block.bell");
/* 1339 */   public static final Sound BLOCK_NOTE_BLOCK_CHIME = define("block.note_block.chime");
/* 1340 */   public static final Sound BLOCK_NOTE_BLOCK_FLUTE = define("block.note_block.flute");
/* 1341 */   public static final Sound BLOCK_NOTE_BLOCK_GUITAR = define("block.note_block.guitar");
/* 1342 */   public static final Sound BLOCK_NOTE_BLOCK_HARP = define("block.note_block.harp");
/* 1343 */   public static final Sound BLOCK_NOTE_BLOCK_HAT = define("block.note_block.hat");
/* 1344 */   public static final Sound BLOCK_NOTE_BLOCK_PLING = define("block.note_block.pling");
/* 1345 */   public static final Sound BLOCK_NOTE_BLOCK_SNARE = define("block.note_block.snare");
/* 1346 */   public static final Sound BLOCK_NOTE_BLOCK_XYLOPHONE = define("block.note_block.xylophone");
/* 1347 */   public static final Sound BLOCK_NOTE_BLOCK_IRON_XYLOPHONE = define("block.note_block.iron_xylophone");
/* 1348 */   public static final Sound BLOCK_NOTE_BLOCK_COW_BELL = define("block.note_block.cow_bell");
/* 1349 */   public static final Sound BLOCK_NOTE_BLOCK_DIDGERIDOO = define("block.note_block.didgeridoo");
/* 1350 */   public static final Sound BLOCK_NOTE_BLOCK_BIT = define("block.note_block.bit");
/* 1351 */   public static final Sound BLOCK_NOTE_BLOCK_BANJO = define("block.note_block.banjo");
/* 1352 */   public static final Sound BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE = define("block.note_block.imitate.zombie");
/* 1353 */   public static final Sound BLOCK_NOTE_BLOCK_IMITATE_SKELETON = define("block.note_block.imitate.skeleton");
/* 1354 */   public static final Sound BLOCK_NOTE_BLOCK_IMITATE_CREEPER = define("block.note_block.imitate.creeper");
/* 1355 */   public static final Sound BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON = define("block.note_block.imitate.ender_dragon");
/* 1356 */   public static final Sound BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON = define("block.note_block.imitate.wither_skeleton");
/* 1357 */   public static final Sound BLOCK_NOTE_BLOCK_IMITATE_PIGLIN = define("block.note_block.imitate.piglin");
/* 1358 */   public static final Sound ENTITY_OCELOT_HURT = define("entity.ocelot.hurt");
/* 1359 */   public static final Sound ENTITY_OCELOT_AMBIENT = define("entity.ocelot.ambient");
/* 1360 */   public static final Sound ENTITY_OCELOT_DEATH = define("entity.ocelot.death");
/* 1361 */   public static final Sound ITEM_OMINOUS_BOTTLE_DISPOSE = define("item.ominous_bottle.dispose");
/* 1362 */   public static final Sound ENTITY_PAINTING_BREAK = define("entity.painting.break");
/* 1363 */   public static final Sound ENTITY_PAINTING_PLACE = define("entity.painting.place");
/* 1364 */   public static final Sound ENTITY_PANDA_PRE_SNEEZE = define("entity.panda.pre_sneeze");
/* 1365 */   public static final Sound ENTITY_PANDA_SNEEZE = define("entity.panda.sneeze");
/* 1366 */   public static final Sound ENTITY_PANDA_AMBIENT = define("entity.panda.ambient");
/* 1367 */   public static final Sound ENTITY_PANDA_DEATH = define("entity.panda.death");
/* 1368 */   public static final Sound ENTITY_PANDA_EAT = define("entity.panda.eat");
/* 1369 */   public static final Sound ENTITY_PANDA_STEP = define("entity.panda.step");
/* 1370 */   public static final Sound ENTITY_PANDA_CANT_BREED = define("entity.panda.cant_breed");
/* 1371 */   public static final Sound ENTITY_PANDA_AGGRESSIVE_AMBIENT = define("entity.panda.aggressive_ambient");
/* 1372 */   public static final Sound ENTITY_PANDA_WORRIED_AMBIENT = define("entity.panda.worried_ambient");
/* 1373 */   public static final Sound ENTITY_PANDA_HURT = define("entity.panda.hurt");
/* 1374 */   public static final Sound ENTITY_PANDA_BITE = define("entity.panda.bite");
/* 1375 */   public static final Sound ENTITY_PARROT_AMBIENT = define("entity.parrot.ambient");
/* 1376 */   public static final Sound ENTITY_PARROT_DEATH = define("entity.parrot.death");
/* 1377 */   public static final Sound ENTITY_PARROT_EAT = define("entity.parrot.eat");
/* 1378 */   public static final Sound ENTITY_PARROT_FLY = define("entity.parrot.fly");
/* 1379 */   public static final Sound ENTITY_PARROT_HURT = define("entity.parrot.hurt");
/* 1380 */   public static final Sound ENTITY_PARROT_IMITATE_BLAZE = define("entity.parrot.imitate.blaze");
/* 1381 */   public static final Sound ENTITY_PARROT_IMITATE_BOGGED = define("entity.parrot.imitate.bogged");
/* 1382 */   public static final Sound ENTITY_PARROT_IMITATE_BREEZE = define("entity.parrot.imitate.breeze");
/* 1383 */   public static final Sound ENTITY_PARROT_IMITATE_CREEPER = define("entity.parrot.imitate.creeper");
/* 1384 */   public static final Sound ENTITY_PARROT_IMITATE_DROWNED = define("entity.parrot.imitate.drowned");
/* 1385 */   public static final Sound ENTITY_PARROT_IMITATE_ELDER_GUARDIAN = define("entity.parrot.imitate.elder_guardian");
/* 1386 */   public static final Sound ENTITY_PARROT_IMITATE_ENDER_DRAGON = define("entity.parrot.imitate.ender_dragon");
/* 1387 */   public static final Sound ENTITY_PARROT_IMITATE_ENDERMITE = define("entity.parrot.imitate.endermite");
/* 1388 */   public static final Sound ENTITY_PARROT_IMITATE_EVOKER = define("entity.parrot.imitate.evoker");
/* 1389 */   public static final Sound ENTITY_PARROT_IMITATE_GHAST = define("entity.parrot.imitate.ghast");
/* 1390 */   public static final Sound ENTITY_PARROT_IMITATE_GUARDIAN = define("entity.parrot.imitate.guardian");
/* 1391 */   public static final Sound ENTITY_PARROT_IMITATE_HOGLIN = define("entity.parrot.imitate.hoglin");
/* 1392 */   public static final Sound ENTITY_PARROT_IMITATE_HUSK = define("entity.parrot.imitate.husk");
/* 1393 */   public static final Sound ENTITY_PARROT_IMITATE_ILLUSIONER = define("entity.parrot.imitate.illusioner");
/* 1394 */   public static final Sound ENTITY_PARROT_IMITATE_MAGMA_CUBE = define("entity.parrot.imitate.magma_cube");
/* 1395 */   public static final Sound ENTITY_PARROT_IMITATE_PHANTOM = define("entity.parrot.imitate.phantom");
/* 1396 */   public static final Sound ENTITY_PARROT_IMITATE_PIGLIN = define("entity.parrot.imitate.piglin");
/* 1397 */   public static final Sound ENTITY_PARROT_IMITATE_PIGLIN_BRUTE = define("entity.parrot.imitate.piglin_brute");
/* 1398 */   public static final Sound ENTITY_PARROT_IMITATE_PILLAGER = define("entity.parrot.imitate.pillager");
/* 1399 */   public static final Sound ENTITY_PARROT_IMITATE_RAVAGER = define("entity.parrot.imitate.ravager");
/* 1400 */   public static final Sound ENTITY_PARROT_IMITATE_SHULKER = define("entity.parrot.imitate.shulker");
/* 1401 */   public static final Sound ENTITY_PARROT_IMITATE_SILVERFISH = define("entity.parrot.imitate.silverfish");
/* 1402 */   public static final Sound ENTITY_PARROT_IMITATE_SKELETON = define("entity.parrot.imitate.skeleton");
/* 1403 */   public static final Sound ENTITY_PARROT_IMITATE_SLIME = define("entity.parrot.imitate.slime");
/* 1404 */   public static final Sound ENTITY_PARROT_IMITATE_SPIDER = define("entity.parrot.imitate.spider");
/* 1405 */   public static final Sound ENTITY_PARROT_IMITATE_STRAY = define("entity.parrot.imitate.stray");
/* 1406 */   public static final Sound ENTITY_PARROT_IMITATE_VEX = define("entity.parrot.imitate.vex");
/* 1407 */   public static final Sound ENTITY_PARROT_IMITATE_VINDICATOR = define("entity.parrot.imitate.vindicator");
/* 1408 */   public static final Sound ENTITY_PARROT_IMITATE_WARDEN = define("entity.parrot.imitate.warden");
/* 1409 */   public static final Sound ENTITY_PARROT_IMITATE_WITCH = define("entity.parrot.imitate.witch");
/* 1410 */   public static final Sound ENTITY_PARROT_IMITATE_WITHER = define("entity.parrot.imitate.wither");
/* 1411 */   public static final Sound ENTITY_PARROT_IMITATE_WITHER_SKELETON = define("entity.parrot.imitate.wither_skeleton");
/* 1412 */   public static final Sound ENTITY_PARROT_IMITATE_ZOGLIN = define("entity.parrot.imitate.zoglin");
/* 1413 */   public static final Sound ENTITY_PARROT_IMITATE_ZOMBIE = define("entity.parrot.imitate.zombie");
/* 1414 */   public static final Sound ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER = define("entity.parrot.imitate.zombie_villager");
/* 1415 */   public static final Sound ENTITY_PARROT_STEP = define("entity.parrot.step");
/* 1416 */   public static final Sound ENTITY_PHANTOM_AMBIENT = define("entity.phantom.ambient");
/* 1417 */   public static final Sound ENTITY_PHANTOM_BITE = define("entity.phantom.bite");
/* 1418 */   public static final Sound ENTITY_PHANTOM_DEATH = define("entity.phantom.death");
/* 1419 */   public static final Sound ENTITY_PHANTOM_FLAP = define("entity.phantom.flap");
/* 1420 */   public static final Sound ENTITY_PHANTOM_HURT = define("entity.phantom.hurt");
/* 1421 */   public static final Sound ENTITY_PHANTOM_SWOOP = define("entity.phantom.swoop");
/* 1422 */   public static final Sound ENTITY_PIG_AMBIENT = define("entity.pig.ambient");
/* 1423 */   public static final Sound ENTITY_PIG_DEATH = define("entity.pig.death");
/* 1424 */   public static final Sound ENTITY_PIG_HURT = define("entity.pig.hurt");
/* 1425 */   public static final Sound ENTITY_PIG_SADDLE = define("entity.pig.saddle");
/* 1426 */   public static final Sound ENTITY_PIG_STEP = define("entity.pig.step");
/* 1427 */   public static final Sound ENTITY_PIGLIN_ADMIRING_ITEM = define("entity.piglin.admiring_item");
/* 1428 */   public static final Sound ENTITY_PIGLIN_AMBIENT = define("entity.piglin.ambient");
/* 1429 */   public static final Sound ENTITY_PIGLIN_ANGRY = define("entity.piglin.angry");
/* 1430 */   public static final Sound ENTITY_PIGLIN_CELEBRATE = define("entity.piglin.celebrate");
/* 1431 */   public static final Sound ENTITY_PIGLIN_DEATH = define("entity.piglin.death");
/* 1432 */   public static final Sound ENTITY_PIGLIN_JEALOUS = define("entity.piglin.jealous");
/* 1433 */   public static final Sound ENTITY_PIGLIN_HURT = define("entity.piglin.hurt");
/* 1434 */   public static final Sound ENTITY_PIGLIN_RETREAT = define("entity.piglin.retreat");
/* 1435 */   public static final Sound ENTITY_PIGLIN_STEP = define("entity.piglin.step");
/* 1436 */   public static final Sound ENTITY_PIGLIN_CONVERTED_TO_ZOMBIFIED = define("entity.piglin.converted_to_zombified");
/* 1437 */   public static final Sound ENTITY_PIGLIN_BRUTE_AMBIENT = define("entity.piglin_brute.ambient");
/* 1438 */   public static final Sound ENTITY_PIGLIN_BRUTE_ANGRY = define("entity.piglin_brute.angry");
/* 1439 */   public static final Sound ENTITY_PIGLIN_BRUTE_DEATH = define("entity.piglin_brute.death");
/* 1440 */   public static final Sound ENTITY_PIGLIN_BRUTE_HURT = define("entity.piglin_brute.hurt");
/* 1441 */   public static final Sound ENTITY_PIGLIN_BRUTE_STEP = define("entity.piglin_brute.step");
/* 1442 */   public static final Sound ENTITY_PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED = define("entity.piglin_brute.converted_to_zombified");
/* 1443 */   public static final Sound ENTITY_PILLAGER_AMBIENT = define("entity.pillager.ambient");
/* 1444 */   public static final Sound ENTITY_PILLAGER_CELEBRATE = define("entity.pillager.celebrate");
/* 1445 */   public static final Sound ENTITY_PILLAGER_DEATH = define("entity.pillager.death");
/* 1446 */   public static final Sound ENTITY_PILLAGER_HURT = define("entity.pillager.hurt");
/* 1447 */   public static final Sound BLOCK_PISTON_CONTRACT = define("block.piston.contract");
/* 1448 */   public static final Sound BLOCK_PISTON_EXTEND = define("block.piston.extend");
/* 1449 */   public static final Sound ENTITY_PLAYER_ATTACK_CRIT = define("entity.player.attack.crit");
/* 1450 */   public static final Sound ENTITY_PLAYER_ATTACK_KNOCKBACK = define("entity.player.attack.knockback");
/* 1451 */   public static final Sound ENTITY_PLAYER_ATTACK_NODAMAGE = define("entity.player.attack.nodamage");
/* 1452 */   public static final Sound ENTITY_PLAYER_ATTACK_STRONG = define("entity.player.attack.strong");
/* 1453 */   public static final Sound ENTITY_PLAYER_ATTACK_SWEEP = define("entity.player.attack.sweep");
/* 1454 */   public static final Sound ENTITY_PLAYER_ATTACK_WEAK = define("entity.player.attack.weak");
/* 1455 */   public static final Sound ENTITY_PLAYER_BIG_FALL = define("entity.player.big_fall");
/* 1456 */   public static final Sound ENTITY_PLAYER_BREATH = define("entity.player.breath");
/* 1457 */   public static final Sound ENTITY_PLAYER_BURP = define("entity.player.burp");
/* 1458 */   public static final Sound ENTITY_PLAYER_DEATH = define("entity.player.death");
/* 1459 */   public static final Sound ENTITY_PLAYER_HURT = define("entity.player.hurt");
/* 1460 */   public static final Sound ENTITY_PLAYER_HURT_DROWN = define("entity.player.hurt_drown");
/* 1461 */   public static final Sound ENTITY_PLAYER_HURT_FREEZE = define("entity.player.hurt_freeze");
/* 1462 */   public static final Sound ENTITY_PLAYER_HURT_ON_FIRE = define("entity.player.hurt_on_fire");
/* 1463 */   public static final Sound ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH = define("entity.player.hurt_sweet_berry_bush");
/* 1464 */   public static final Sound ENTITY_PLAYER_LEVELUP = define("entity.player.levelup");
/* 1465 */   public static final Sound ENTITY_PLAYER_SMALL_FALL = define("entity.player.small_fall");
/* 1466 */   public static final Sound ENTITY_PLAYER_SPLASH = define("entity.player.splash");
/* 1467 */   public static final Sound ENTITY_PLAYER_SPLASH_HIGH_SPEED = define("entity.player.splash.high_speed");
/* 1468 */   public static final Sound ENTITY_PLAYER_SWIM = define("entity.player.swim");
/* 1469 */   public static final Sound ENTITY_PLAYER_TELEPORT = define("entity.player.teleport");
/* 1470 */   public static final Sound ENTITY_POLAR_BEAR_AMBIENT = define("entity.polar_bear.ambient");
/* 1471 */   public static final Sound ENTITY_POLAR_BEAR_AMBIENT_BABY = define("entity.polar_bear.ambient_baby");
/* 1472 */   public static final Sound ENTITY_POLAR_BEAR_DEATH = define("entity.polar_bear.death");
/* 1473 */   public static final Sound ENTITY_POLAR_BEAR_HURT = define("entity.polar_bear.hurt");
/* 1474 */   public static final Sound ENTITY_POLAR_BEAR_STEP = define("entity.polar_bear.step");
/* 1475 */   public static final Sound ENTITY_POLAR_BEAR_WARNING = define("entity.polar_bear.warning");
/* 1476 */   public static final Sound BLOCK_POLISHED_DEEPSLATE_BREAK = define("block.polished_deepslate.break");
/* 1477 */   public static final Sound BLOCK_POLISHED_DEEPSLATE_FALL = define("block.polished_deepslate.fall");
/* 1478 */   public static final Sound BLOCK_POLISHED_DEEPSLATE_HIT = define("block.polished_deepslate.hit");
/* 1479 */   public static final Sound BLOCK_POLISHED_DEEPSLATE_PLACE = define("block.polished_deepslate.place");
/* 1480 */   public static final Sound BLOCK_POLISHED_DEEPSLATE_STEP = define("block.polished_deepslate.step");
/* 1481 */   public static final Sound BLOCK_PORTAL_AMBIENT = define("block.portal.ambient");
/* 1482 */   public static final Sound BLOCK_PORTAL_TRAVEL = define("block.portal.travel");
/* 1483 */   public static final Sound BLOCK_PORTAL_TRIGGER = define("block.portal.trigger");
/* 1484 */   public static final Sound BLOCK_POWDER_SNOW_BREAK = define("block.powder_snow.break");
/* 1485 */   public static final Sound BLOCK_POWDER_SNOW_FALL = define("block.powder_snow.fall");
/* 1486 */   public static final Sound BLOCK_POWDER_SNOW_HIT = define("block.powder_snow.hit");
/* 1487 */   public static final Sound BLOCK_POWDER_SNOW_PLACE = define("block.powder_snow.place");
/* 1488 */   public static final Sound BLOCK_POWDER_SNOW_STEP = define("block.powder_snow.step");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/* 1493 */   public static final Sound ENTITY_PUFFER_FISH_AMBIENT = define("entity.puffer_fish.ambient");
/* 1494 */   public static final Sound ENTITY_PUFFER_FISH_BLOW_OUT = define("entity.puffer_fish.blow_out");
/* 1495 */   public static final Sound ENTITY_PUFFER_FISH_BLOW_UP = define("entity.puffer_fish.blow_up");
/* 1496 */   public static final Sound ENTITY_PUFFER_FISH_DEATH = define("entity.puffer_fish.death");
/* 1497 */   public static final Sound ENTITY_PUFFER_FISH_FLOP = define("entity.puffer_fish.flop");
/* 1498 */   public static final Sound ENTITY_PUFFER_FISH_HURT = define("entity.puffer_fish.hurt");
/* 1499 */   public static final Sound ENTITY_PUFFER_FISH_STING = define("entity.puffer_fish.sting");
/* 1500 */   public static final Sound BLOCK_PUMPKIN_CARVE = define("block.pumpkin.carve");
/* 1501 */   public static final Sound ENTITY_RABBIT_AMBIENT = define("entity.rabbit.ambient");
/* 1502 */   public static final Sound ENTITY_RABBIT_ATTACK = define("entity.rabbit.attack");
/* 1503 */   public static final Sound ENTITY_RABBIT_DEATH = define("entity.rabbit.death");
/* 1504 */   public static final Sound ENTITY_RABBIT_HURT = define("entity.rabbit.hurt");
/* 1505 */   public static final Sound ENTITY_RABBIT_JUMP = define("entity.rabbit.jump");
/* 1506 */   public static final Sound EVENT_RAID_HORN = define("event.raid.horn");
/* 1507 */   public static final Sound ENTITY_RAVAGER_AMBIENT = define("entity.ravager.ambient");
/* 1508 */   public static final Sound ENTITY_RAVAGER_ATTACK = define("entity.ravager.attack");
/* 1509 */   public static final Sound ENTITY_RAVAGER_CELEBRATE = define("entity.ravager.celebrate");
/* 1510 */   public static final Sound ENTITY_RAVAGER_DEATH = define("entity.ravager.death");
/* 1511 */   public static final Sound ENTITY_RAVAGER_HURT = define("entity.ravager.hurt");
/* 1512 */   public static final Sound ENTITY_RAVAGER_STEP = define("entity.ravager.step");
/* 1513 */   public static final Sound ENTITY_RAVAGER_STUNNED = define("entity.ravager.stunned");
/* 1514 */   public static final Sound ENTITY_RAVAGER_ROAR = define("entity.ravager.roar");
/* 1515 */   public static final Sound BLOCK_NETHER_GOLD_ORE_BREAK = define("block.nether_gold_ore.break");
/* 1516 */   public static final Sound BLOCK_NETHER_GOLD_ORE_FALL = define("block.nether_gold_ore.fall");
/* 1517 */   public static final Sound BLOCK_NETHER_GOLD_ORE_HIT = define("block.nether_gold_ore.hit");
/* 1518 */   public static final Sound BLOCK_NETHER_GOLD_ORE_PLACE = define("block.nether_gold_ore.place");
/* 1519 */   public static final Sound BLOCK_NETHER_GOLD_ORE_STEP = define("block.nether_gold_ore.step");
/* 1520 */   public static final Sound BLOCK_NETHER_ORE_BREAK = define("block.nether_ore.break");
/* 1521 */   public static final Sound BLOCK_NETHER_ORE_FALL = define("block.nether_ore.fall");
/* 1522 */   public static final Sound BLOCK_NETHER_ORE_HIT = define("block.nether_ore.hit");
/* 1523 */   public static final Sound BLOCK_NETHER_ORE_PLACE = define("block.nether_ore.place");
/* 1524 */   public static final Sound BLOCK_NETHER_ORE_STEP = define("block.nether_ore.step");
/* 1525 */   public static final Sound BLOCK_REDSTONE_TORCH_BURNOUT = define("block.redstone_torch.burnout");
/* 1526 */   public static final Sound BLOCK_RESPAWN_ANCHOR_AMBIENT = define("block.respawn_anchor.ambient");
/* 1527 */   public static final Sound BLOCK_RESPAWN_ANCHOR_CHARGE = define("block.respawn_anchor.charge");
/* 1528 */   public static final Sound BLOCK_RESPAWN_ANCHOR_DEPLETE = define("block.respawn_anchor.deplete");
/* 1529 */   public static final Sound BLOCK_RESPAWN_ANCHOR_SET_SPAWN = define("block.respawn_anchor.set_spawn");
/* 1530 */   public static final Sound BLOCK_ROOTED_DIRT_BREAK = define("block.rooted_dirt.break");
/* 1531 */   public static final Sound BLOCK_ROOTED_DIRT_FALL = define("block.rooted_dirt.fall");
/* 1532 */   public static final Sound BLOCK_ROOTED_DIRT_HIT = define("block.rooted_dirt.hit");
/* 1533 */   public static final Sound BLOCK_ROOTED_DIRT_PLACE = define("block.rooted_dirt.place");
/* 1534 */   public static final Sound BLOCK_ROOTED_DIRT_STEP = define("block.rooted_dirt.step");
/* 1535 */   public static final Sound ENTITY_SALMON_AMBIENT = define("entity.salmon.ambient");
/* 1536 */   public static final Sound ENTITY_SALMON_DEATH = define("entity.salmon.death");
/* 1537 */   public static final Sound ENTITY_SALMON_FLOP = define("entity.salmon.flop");
/* 1538 */   public static final Sound ENTITY_SALMON_HURT = define("entity.salmon.hurt");
/* 1539 */   public static final Sound BLOCK_SAND_BREAK = define("block.sand.break");
/* 1540 */   public static final Sound BLOCK_SAND_FALL = define("block.sand.fall");
/* 1541 */   public static final Sound BLOCK_SAND_HIT = define("block.sand.hit");
/* 1542 */   public static final Sound BLOCK_SAND_PLACE = define("block.sand.place");
/* 1543 */   public static final Sound BLOCK_SAND_STEP = define("block.sand.step");
/* 1544 */   public static final Sound BLOCK_SCAFFOLDING_BREAK = define("block.scaffolding.break");
/* 1545 */   public static final Sound BLOCK_SCAFFOLDING_FALL = define("block.scaffolding.fall");
/* 1546 */   public static final Sound BLOCK_SCAFFOLDING_HIT = define("block.scaffolding.hit");
/* 1547 */   public static final Sound BLOCK_SCAFFOLDING_PLACE = define("block.scaffolding.place");
/* 1548 */   public static final Sound BLOCK_SCAFFOLDING_STEP = define("block.scaffolding.step");
/* 1549 */   public static final Sound BLOCK_SCULK_SPREAD = define("block.sculk.spread");
/* 1550 */   public static final Sound BLOCK_SCULK_CHARGE = define("block.sculk.charge");
/* 1551 */   public static final Sound BLOCK_SCULK_BREAK = define("block.sculk.break");
/* 1552 */   public static final Sound BLOCK_SCULK_FALL = define("block.sculk.fall");
/* 1553 */   public static final Sound BLOCK_SCULK_HIT = define("block.sculk.hit");
/* 1554 */   public static final Sound BLOCK_SCULK_PLACE = define("block.sculk.place");
/* 1555 */   public static final Sound BLOCK_SCULK_STEP = define("block.sculk.step");
/* 1556 */   public static final Sound BLOCK_SCULK_CATALYST_BLOOM = define("block.sculk_catalyst.bloom");
/* 1557 */   public static final Sound BLOCK_SCULK_CATALYST_BREAK = define("block.sculk_catalyst.break");
/* 1558 */   public static final Sound BLOCK_SCULK_CATALYST_FALL = define("block.sculk_catalyst.fall");
/* 1559 */   public static final Sound BLOCK_SCULK_CATALYST_HIT = define("block.sculk_catalyst.hit");
/* 1560 */   public static final Sound BLOCK_SCULK_CATALYST_PLACE = define("block.sculk_catalyst.place");
/* 1561 */   public static final Sound BLOCK_SCULK_CATALYST_STEP = define("block.sculk_catalyst.step");
/* 1562 */   public static final Sound BLOCK_SCULK_SENSOR_CLICKING = define("block.sculk_sensor.clicking");
/* 1563 */   public static final Sound BLOCK_SCULK_SENSOR_CLICKING_STOP = define("block.sculk_sensor.clicking_stop");
/* 1564 */   public static final Sound BLOCK_SCULK_SENSOR_BREAK = define("block.sculk_sensor.break");
/* 1565 */   public static final Sound BLOCK_SCULK_SENSOR_FALL = define("block.sculk_sensor.fall");
/* 1566 */   public static final Sound BLOCK_SCULK_SENSOR_HIT = define("block.sculk_sensor.hit");
/* 1567 */   public static final Sound BLOCK_SCULK_SENSOR_PLACE = define("block.sculk_sensor.place");
/* 1568 */   public static final Sound BLOCK_SCULK_SENSOR_STEP = define("block.sculk_sensor.step");
/* 1569 */   public static final Sound BLOCK_SCULK_SHRIEKER_BREAK = define("block.sculk_shrieker.break");
/* 1570 */   public static final Sound BLOCK_SCULK_SHRIEKER_FALL = define("block.sculk_shrieker.fall");
/* 1571 */   public static final Sound BLOCK_SCULK_SHRIEKER_HIT = define("block.sculk_shrieker.hit");
/* 1572 */   public static final Sound BLOCK_SCULK_SHRIEKER_PLACE = define("block.sculk_shrieker.place");
/* 1573 */   public static final Sound BLOCK_SCULK_SHRIEKER_SHRIEK = define("block.sculk_shrieker.shriek");
/* 1574 */   public static final Sound BLOCK_SCULK_SHRIEKER_STEP = define("block.sculk_shrieker.step");
/* 1575 */   public static final Sound BLOCK_SCULK_VEIN_BREAK = define("block.sculk_vein.break");
/* 1576 */   public static final Sound BLOCK_SCULK_VEIN_FALL = define("block.sculk_vein.fall");
/* 1577 */   public static final Sound BLOCK_SCULK_VEIN_HIT = define("block.sculk_vein.hit");
/* 1578 */   public static final Sound BLOCK_SCULK_VEIN_PLACE = define("block.sculk_vein.place");
/* 1579 */   public static final Sound BLOCK_SCULK_VEIN_STEP = define("block.sculk_vein.step");
/* 1580 */   public static final Sound ENTITY_SHEEP_AMBIENT = define("entity.sheep.ambient");
/* 1581 */   public static final Sound ENTITY_SHEEP_DEATH = define("entity.sheep.death");
/* 1582 */   public static final Sound ENTITY_SHEEP_HURT = define("entity.sheep.hurt");
/* 1583 */   public static final Sound ENTITY_SHEEP_SHEAR = define("entity.sheep.shear");
/* 1584 */   public static final Sound ENTITY_SHEEP_STEP = define("entity.sheep.step");
/* 1585 */   public static final Sound ITEM_SHIELD_BLOCK = define("item.shield.block");
/* 1586 */   public static final Sound ITEM_SHIELD_BREAK = define("item.shield.break");
/* 1587 */   public static final Sound BLOCK_SHROOMLIGHT_BREAK = define("block.shroomlight.break");
/* 1588 */   public static final Sound BLOCK_SHROOMLIGHT_STEP = define("block.shroomlight.step");
/* 1589 */   public static final Sound BLOCK_SHROOMLIGHT_PLACE = define("block.shroomlight.place");
/* 1590 */   public static final Sound BLOCK_SHROOMLIGHT_HIT = define("block.shroomlight.hit");
/* 1591 */   public static final Sound BLOCK_SHROOMLIGHT_FALL = define("block.shroomlight.fall");
/* 1592 */   public static final Sound ITEM_SHOVEL_FLATTEN = define("item.shovel.flatten");
/* 1593 */   public static final Sound ENTITY_SHULKER_AMBIENT = define("entity.shulker.ambient");
/* 1594 */   public static final Sound BLOCK_SHULKER_BOX_CLOSE = define("block.shulker_box.close");
/* 1595 */   public static final Sound BLOCK_SHULKER_BOX_OPEN = define("block.shulker_box.open");
/* 1596 */   public static final Sound ENTITY_SHULKER_BULLET_HIT = define("entity.shulker_bullet.hit");
/* 1597 */   public static final Sound ENTITY_SHULKER_BULLET_HURT = define("entity.shulker_bullet.hurt");
/* 1598 */   public static final Sound ENTITY_SHULKER_CLOSE = define("entity.shulker.close");
/* 1599 */   public static final Sound ENTITY_SHULKER_DEATH = define("entity.shulker.death");
/* 1600 */   public static final Sound ENTITY_SHULKER_HURT = define("entity.shulker.hurt");
/* 1601 */   public static final Sound ENTITY_SHULKER_HURT_CLOSED = define("entity.shulker.hurt_closed");
/* 1602 */   public static final Sound ENTITY_SHULKER_OPEN = define("entity.shulker.open");
/* 1603 */   public static final Sound ENTITY_SHULKER_SHOOT = define("entity.shulker.shoot");
/* 1604 */   public static final Sound ENTITY_SHULKER_TELEPORT = define("entity.shulker.teleport");
/* 1605 */   public static final Sound ENTITY_SILVERFISH_AMBIENT = define("entity.silverfish.ambient");
/* 1606 */   public static final Sound ENTITY_SILVERFISH_DEATH = define("entity.silverfish.death");
/* 1607 */   public static final Sound ENTITY_SILVERFISH_HURT = define("entity.silverfish.hurt");
/* 1608 */   public static final Sound ENTITY_SILVERFISH_STEP = define("entity.silverfish.step");
/* 1609 */   public static final Sound ENTITY_SKELETON_AMBIENT = define("entity.skeleton.ambient");
/* 1610 */   public static final Sound ENTITY_SKELETON_CONVERTED_TO_STRAY = define("entity.skeleton.converted_to_stray");
/* 1611 */   public static final Sound ENTITY_SKELETON_DEATH = define("entity.skeleton.death");
/* 1612 */   public static final Sound ENTITY_SKELETON_HORSE_AMBIENT = define("entity.skeleton_horse.ambient");
/* 1613 */   public static final Sound ENTITY_SKELETON_HORSE_DEATH = define("entity.skeleton_horse.death");
/* 1614 */   public static final Sound ENTITY_SKELETON_HORSE_HURT = define("entity.skeleton_horse.hurt");
/* 1615 */   public static final Sound ENTITY_SKELETON_HORSE_SWIM = define("entity.skeleton_horse.swim");
/* 1616 */   public static final Sound ENTITY_SKELETON_HORSE_AMBIENT_WATER = define("entity.skeleton_horse.ambient_water");
/* 1617 */   public static final Sound ENTITY_SKELETON_HORSE_GALLOP_WATER = define("entity.skeleton_horse.gallop_water");
/* 1618 */   public static final Sound ENTITY_SKELETON_HORSE_JUMP_WATER = define("entity.skeleton_horse.jump_water");
/* 1619 */   public static final Sound ENTITY_SKELETON_HORSE_STEP_WATER = define("entity.skeleton_horse.step_water");
/* 1620 */   public static final Sound ENTITY_SKELETON_HURT = define("entity.skeleton.hurt");
/* 1621 */   public static final Sound ENTITY_SKELETON_SHOOT = define("entity.skeleton.shoot");
/* 1622 */   public static final Sound ENTITY_SKELETON_STEP = define("entity.skeleton.step");
/* 1623 */   public static final Sound ENTITY_SLIME_ATTACK = define("entity.slime.attack");
/* 1624 */   public static final Sound ENTITY_SLIME_DEATH = define("entity.slime.death");
/* 1625 */   public static final Sound ENTITY_SLIME_HURT = define("entity.slime.hurt");
/* 1626 */   public static final Sound ENTITY_SLIME_JUMP = define("entity.slime.jump");
/* 1627 */   public static final Sound ENTITY_SLIME_SQUISH = define("entity.slime.squish");
/* 1628 */   public static final Sound BLOCK_SLIME_BLOCK_BREAK = define("block.slime_block.break");
/* 1629 */   public static final Sound BLOCK_SLIME_BLOCK_FALL = define("block.slime_block.fall");
/* 1630 */   public static final Sound BLOCK_SLIME_BLOCK_HIT = define("block.slime_block.hit");
/* 1631 */   public static final Sound BLOCK_SLIME_BLOCK_PLACE = define("block.slime_block.place");
/* 1632 */   public static final Sound BLOCK_SLIME_BLOCK_STEP = define("block.slime_block.step");
/* 1633 */   public static final Sound BLOCK_SMALL_AMETHYST_BUD_BREAK = define("block.small_amethyst_bud.break");
/* 1634 */   public static final Sound BLOCK_SMALL_AMETHYST_BUD_PLACE = define("block.small_amethyst_bud.place");
/* 1635 */   public static final Sound BLOCK_SMALL_DRIPLEAF_BREAK = define("block.small_dripleaf.break");
/* 1636 */   public static final Sound BLOCK_SMALL_DRIPLEAF_FALL = define("block.small_dripleaf.fall");
/* 1637 */   public static final Sound BLOCK_SMALL_DRIPLEAF_HIT = define("block.small_dripleaf.hit");
/* 1638 */   public static final Sound BLOCK_SMALL_DRIPLEAF_PLACE = define("block.small_dripleaf.place");
/* 1639 */   public static final Sound BLOCK_SMALL_DRIPLEAF_STEP = define("block.small_dripleaf.step");
/* 1640 */   public static final Sound BLOCK_SOUL_SAND_BREAK = define("block.soul_sand.break");
/* 1641 */   public static final Sound BLOCK_SOUL_SAND_STEP = define("block.soul_sand.step");
/* 1642 */   public static final Sound BLOCK_SOUL_SAND_PLACE = define("block.soul_sand.place");
/* 1643 */   public static final Sound BLOCK_SOUL_SAND_HIT = define("block.soul_sand.hit");
/* 1644 */   public static final Sound BLOCK_SOUL_SAND_FALL = define("block.soul_sand.fall");
/* 1645 */   public static final Sound BLOCK_SOUL_SOIL_BREAK = define("block.soul_soil.break");
/* 1646 */   public static final Sound BLOCK_SOUL_SOIL_STEP = define("block.soul_soil.step");
/* 1647 */   public static final Sound BLOCK_SOUL_SOIL_PLACE = define("block.soul_soil.place");
/* 1648 */   public static final Sound BLOCK_SOUL_SOIL_HIT = define("block.soul_soil.hit");
/* 1649 */   public static final Sound BLOCK_SOUL_SOIL_FALL = define("block.soul_soil.fall");
/* 1650 */   public static final Sound PARTICLE_SOUL_ESCAPE = define("particle.soul_escape");
/* 1651 */   public static final Sound BLOCK_SPORE_BLOSSOM_BREAK = define("block.spore_blossom.break");
/* 1652 */   public static final Sound BLOCK_SPORE_BLOSSOM_FALL = define("block.spore_blossom.fall");
/* 1653 */   public static final Sound BLOCK_SPORE_BLOSSOM_HIT = define("block.spore_blossom.hit");
/* 1654 */   public static final Sound BLOCK_SPORE_BLOSSOM_PLACE = define("block.spore_blossom.place");
/* 1655 */   public static final Sound BLOCK_SPORE_BLOSSOM_STEP = define("block.spore_blossom.step");
/* 1656 */   public static final Sound ENTITY_STRIDER_AMBIENT = define("entity.strider.ambient");
/* 1657 */   public static final Sound ENTITY_STRIDER_HAPPY = define("entity.strider.happy");
/* 1658 */   public static final Sound ENTITY_STRIDER_RETREAT = define("entity.strider.retreat");
/* 1659 */   public static final Sound ENTITY_STRIDER_DEATH = define("entity.strider.death");
/* 1660 */   public static final Sound ENTITY_STRIDER_HURT = define("entity.strider.hurt");
/* 1661 */   public static final Sound ENTITY_STRIDER_STEP = define("entity.strider.step");
/* 1662 */   public static final Sound ENTITY_STRIDER_STEP_LAVA = define("entity.strider.step_lava");
/* 1663 */   public static final Sound ENTITY_STRIDER_EAT = define("entity.strider.eat");
/* 1664 */   public static final Sound ENTITY_STRIDER_SADDLE = define("entity.strider.saddle");
/* 1665 */   public static final Sound ENTITY_SLIME_DEATH_SMALL = define("entity.slime.death_small");
/* 1666 */   public static final Sound ENTITY_SLIME_HURT_SMALL = define("entity.slime.hurt_small");
/* 1667 */   public static final Sound ENTITY_SLIME_JUMP_SMALL = define("entity.slime.jump_small");
/* 1668 */   public static final Sound ENTITY_SLIME_SQUISH_SMALL = define("entity.slime.squish_small");
/* 1669 */   public static final Sound BLOCK_SMITHING_TABLE_USE = define("block.smithing_table.use");
/* 1670 */   public static final Sound BLOCK_SMOKER_SMOKE = define("block.smoker.smoke");
/* 1671 */   public static final Sound ENTITY_SNIFFER_STEP = define("entity.sniffer.step");
/* 1672 */   public static final Sound ENTITY_SNIFFER_EAT = define("entity.sniffer.eat");
/* 1673 */   public static final Sound ENTITY_SNIFFER_IDLE = define("entity.sniffer.idle");
/* 1674 */   public static final Sound ENTITY_SNIFFER_HURT = define("entity.sniffer.hurt");
/* 1675 */   public static final Sound ENTITY_SNIFFER_DEATH = define("entity.sniffer.death");
/* 1676 */   public static final Sound ENTITY_SNIFFER_DROP_SEED = define("entity.sniffer.drop_seed");
/* 1677 */   public static final Sound ENTITY_SNIFFER_SCENTING = define("entity.sniffer.scenting");
/* 1678 */   public static final Sound ENTITY_SNIFFER_SNIFFING = define("entity.sniffer.sniffing");
/* 1679 */   public static final Sound ENTITY_SNIFFER_SEARCHING = define("entity.sniffer.searching");
/* 1680 */   public static final Sound ENTITY_SNIFFER_DIGGING = define("entity.sniffer.digging");
/* 1681 */   public static final Sound ENTITY_SNIFFER_DIGGING_STOP = define("entity.sniffer.digging_stop");
/* 1682 */   public static final Sound ENTITY_SNIFFER_HAPPY = define("entity.sniffer.happy");
/* 1683 */   public static final Sound BLOCK_SNIFFER_EGG_PLOP = define("block.sniffer_egg.plop");
/* 1684 */   public static final Sound BLOCK_SNIFFER_EGG_CRACK = define("block.sniffer_egg.crack");
/* 1685 */   public static final Sound BLOCK_SNIFFER_EGG_HATCH = define("block.sniffer_egg.hatch");
/* 1686 */   public static final Sound ENTITY_SNOWBALL_THROW = define("entity.snowball.throw");
/* 1687 */   public static final Sound BLOCK_SNOW_BREAK = define("block.snow.break");
/* 1688 */   public static final Sound BLOCK_SNOW_FALL = define("block.snow.fall");
/* 1689 */   public static final Sound ENTITY_SNOW_GOLEM_AMBIENT = define("entity.snow_golem.ambient");
/* 1690 */   public static final Sound ENTITY_SNOW_GOLEM_DEATH = define("entity.snow_golem.death");
/* 1691 */   public static final Sound ENTITY_SNOW_GOLEM_HURT = define("entity.snow_golem.hurt");
/* 1692 */   public static final Sound ENTITY_SNOW_GOLEM_SHOOT = define("entity.snow_golem.shoot");
/* 1693 */   public static final Sound ENTITY_SNOW_GOLEM_SHEAR = define("entity.snow_golem.shear");
/* 1694 */   public static final Sound BLOCK_SNOW_HIT = define("block.snow.hit");
/* 1695 */   public static final Sound BLOCK_SNOW_PLACE = define("block.snow.place");
/* 1696 */   public static final Sound BLOCK_SNOW_STEP = define("block.snow.step");
/* 1697 */   public static final Sound ENTITY_SPIDER_AMBIENT = define("entity.spider.ambient");
/* 1698 */   public static final Sound ENTITY_SPIDER_DEATH = define("entity.spider.death");
/* 1699 */   public static final Sound ENTITY_SPIDER_HURT = define("entity.spider.hurt");
/* 1700 */   public static final Sound ENTITY_SPIDER_STEP = define("entity.spider.step");
/* 1701 */   public static final Sound ENTITY_SPLASH_POTION_BREAK = define("entity.splash_potion.break");
/* 1702 */   public static final Sound ENTITY_SPLASH_POTION_THROW = define("entity.splash_potion.throw");
/* 1703 */   public static final Sound BLOCK_SPONGE_BREAK = define("block.sponge.break");
/* 1704 */   public static final Sound BLOCK_SPONGE_FALL = define("block.sponge.fall");
/* 1705 */   public static final Sound BLOCK_SPONGE_HIT = define("block.sponge.hit");
/* 1706 */   public static final Sound BLOCK_SPONGE_PLACE = define("block.sponge.place");
/* 1707 */   public static final Sound BLOCK_SPONGE_STEP = define("block.sponge.step");
/* 1708 */   public static final Sound BLOCK_SPONGE_ABSORB = define("block.sponge.absorb");
/* 1709 */   public static final Sound ITEM_SPYGLASS_USE = define("item.spyglass.use");
/* 1710 */   public static final Sound ITEM_SPYGLASS_STOP_USING = define("item.spyglass.stop_using");
/* 1711 */   public static final Sound ENTITY_SQUID_AMBIENT = define("entity.squid.ambient");
/* 1712 */   public static final Sound ENTITY_SQUID_DEATH = define("entity.squid.death");
/* 1713 */   public static final Sound ENTITY_SQUID_HURT = define("entity.squid.hurt");
/* 1714 */   public static final Sound ENTITY_SQUID_SQUIRT = define("entity.squid.squirt");
/* 1715 */   public static final Sound BLOCK_STONE_BREAK = define("block.stone.break");
/* 1716 */   public static final Sound BLOCK_STONE_BUTTON_CLICK_OFF = define("block.stone_button.click_off");
/* 1717 */   public static final Sound BLOCK_STONE_BUTTON_CLICK_ON = define("block.stone_button.click_on");
/* 1718 */   public static final Sound BLOCK_STONE_FALL = define("block.stone.fall");
/* 1719 */   public static final Sound BLOCK_STONE_HIT = define("block.stone.hit");
/* 1720 */   public static final Sound BLOCK_STONE_PLACE = define("block.stone.place");
/* 1721 */   public static final Sound BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF = define("block.stone_pressure_plate.click_off");
/* 1722 */   public static final Sound BLOCK_STONE_PRESSURE_PLATE_CLICK_ON = define("block.stone_pressure_plate.click_on");
/* 1723 */   public static final Sound BLOCK_STONE_STEP = define("block.stone.step");
/* 1724 */   public static final Sound ENTITY_STRAY_AMBIENT = define("entity.stray.ambient");
/* 1725 */   public static final Sound ENTITY_STRAY_DEATH = define("entity.stray.death");
/* 1726 */   public static final Sound ENTITY_STRAY_HURT = define("entity.stray.hurt");
/* 1727 */   public static final Sound ENTITY_STRAY_STEP = define("entity.stray.step");
/* 1728 */   public static final Sound BLOCK_SWEET_BERRY_BUSH_BREAK = define("block.sweet_berry_bush.break");
/* 1729 */   public static final Sound BLOCK_SWEET_BERRY_BUSH_PLACE = define("block.sweet_berry_bush.place");
/* 1730 */   public static final Sound BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES = define("block.sweet_berry_bush.pick_berries");
/* 1731 */   public static final Sound ENTITY_TADPOLE_DEATH = define("entity.tadpole.death");
/* 1732 */   public static final Sound ENTITY_TADPOLE_FLOP = define("entity.tadpole.flop");
/* 1733 */   public static final Sound ENTITY_TADPOLE_GROW_UP = define("entity.tadpole.grow_up");
/* 1734 */   public static final Sound ENTITY_TADPOLE_HURT = define("entity.tadpole.hurt");
/* 1735 */   public static final Sound ENCHANT_THORNS_HIT = define("enchant.thorns.hit");
/* 1736 */   public static final Sound ENTITY_TNT_PRIMED = define("entity.tnt.primed");
/* 1737 */   public static final Sound ITEM_TOTEM_USE = define("item.totem.use");
/* 1738 */   public static final Sound ITEM_TRIDENT_HIT = define("item.trident.hit");
/* 1739 */   public static final Sound ITEM_TRIDENT_HIT_GROUND = define("item.trident.hit_ground");
/* 1740 */   public static final Sound ITEM_TRIDENT_RETURN = define("item.trident.return");
/* 1741 */   public static final Sound ITEM_TRIDENT_RIPTIDE_1 = define("item.trident.riptide_1");
/* 1742 */   public static final Sound ITEM_TRIDENT_RIPTIDE_2 = define("item.trident.riptide_2");
/* 1743 */   public static final Sound ITEM_TRIDENT_RIPTIDE_3 = define("item.trident.riptide_3");
/* 1744 */   public static final Sound ITEM_TRIDENT_THROW = define("item.trident.throw");
/* 1745 */   public static final Sound ITEM_TRIDENT_THUNDER = define("item.trident.thunder");
/* 1746 */   public static final Sound BLOCK_TRIPWIRE_ATTACH = define("block.tripwire.attach");
/* 1747 */   public static final Sound BLOCK_TRIPWIRE_CLICK_OFF = define("block.tripwire.click_off");
/* 1748 */   public static final Sound BLOCK_TRIPWIRE_CLICK_ON = define("block.tripwire.click_on");
/* 1749 */   public static final Sound BLOCK_TRIPWIRE_DETACH = define("block.tripwire.detach");
/* 1750 */   public static final Sound ENTITY_TROPICAL_FISH_AMBIENT = define("entity.tropical_fish.ambient");
/* 1751 */   public static final Sound ENTITY_TROPICAL_FISH_DEATH = define("entity.tropical_fish.death");
/* 1752 */   public static final Sound ENTITY_TROPICAL_FISH_FLOP = define("entity.tropical_fish.flop");
/* 1753 */   public static final Sound ENTITY_TROPICAL_FISH_HURT = define("entity.tropical_fish.hurt");
/* 1754 */   public static final Sound BLOCK_TUFF_BREAK = define("block.tuff.break");
/* 1755 */   public static final Sound BLOCK_TUFF_STEP = define("block.tuff.step");
/* 1756 */   public static final Sound BLOCK_TUFF_PLACE = define("block.tuff.place");
/* 1757 */   public static final Sound BLOCK_TUFF_HIT = define("block.tuff.hit");
/* 1758 */   public static final Sound BLOCK_TUFF_FALL = define("block.tuff.fall");
/* 1759 */   public static final Sound BLOCK_TUFF_BRICKS_BREAK = define("block.tuff_bricks.break");
/* 1760 */   public static final Sound BLOCK_TUFF_BRICKS_FALL = define("block.tuff_bricks.fall");
/* 1761 */   public static final Sound BLOCK_TUFF_BRICKS_HIT = define("block.tuff_bricks.hit");
/* 1762 */   public static final Sound BLOCK_TUFF_BRICKS_PLACE = define("block.tuff_bricks.place");
/* 1763 */   public static final Sound BLOCK_TUFF_BRICKS_STEP = define("block.tuff_bricks.step");
/* 1764 */   public static final Sound BLOCK_POLISHED_TUFF_BREAK = define("block.polished_tuff.break");
/* 1765 */   public static final Sound BLOCK_POLISHED_TUFF_FALL = define("block.polished_tuff.fall");
/* 1766 */   public static final Sound BLOCK_POLISHED_TUFF_HIT = define("block.polished_tuff.hit");
/* 1767 */   public static final Sound BLOCK_POLISHED_TUFF_PLACE = define("block.polished_tuff.place");
/* 1768 */   public static final Sound BLOCK_POLISHED_TUFF_STEP = define("block.polished_tuff.step");
/* 1769 */   public static final Sound ENTITY_TURTLE_AMBIENT_LAND = define("entity.turtle.ambient_land");
/* 1770 */   public static final Sound ENTITY_TURTLE_DEATH = define("entity.turtle.death");
/* 1771 */   public static final Sound ENTITY_TURTLE_DEATH_BABY = define("entity.turtle.death_baby");
/* 1772 */   public static final Sound ENTITY_TURTLE_EGG_BREAK = define("entity.turtle.egg_break");
/* 1773 */   public static final Sound ENTITY_TURTLE_EGG_CRACK = define("entity.turtle.egg_crack");
/* 1774 */   public static final Sound ENTITY_TURTLE_EGG_HATCH = define("entity.turtle.egg_hatch");
/* 1775 */   public static final Sound ENTITY_TURTLE_HURT = define("entity.turtle.hurt");
/* 1776 */   public static final Sound ENTITY_TURTLE_HURT_BABY = define("entity.turtle.hurt_baby");
/* 1777 */   public static final Sound ENTITY_TURTLE_LAY_EGG = define("entity.turtle.lay_egg");
/* 1778 */   public static final Sound ENTITY_TURTLE_SHAMBLE = define("entity.turtle.shamble");
/* 1779 */   public static final Sound ENTITY_TURTLE_SHAMBLE_BABY = define("entity.turtle.shamble_baby");
/* 1780 */   public static final Sound ENTITY_TURTLE_SWIM = define("entity.turtle.swim");
/* 1781 */   public static final Sound UI_BUTTON_CLICK = define("ui.button.click");
/* 1782 */   public static final Sound UI_LOOM_SELECT_PATTERN = define("ui.loom.select_pattern");
/* 1783 */   public static final Sound UI_LOOM_TAKE_RESULT = define("ui.loom.take_result");
/* 1784 */   public static final Sound UI_CARTOGRAPHY_TABLE_TAKE_RESULT = define("ui.cartography_table.take_result");
/* 1785 */   public static final Sound UI_STONECUTTER_TAKE_RESULT = define("ui.stonecutter.take_result");
/* 1786 */   public static final Sound UI_STONECUTTER_SELECT_RECIPE = define("ui.stonecutter.select_recipe");
/* 1787 */   public static final Sound UI_TOAST_CHALLENGE_COMPLETE = define("ui.toast.challenge_complete");
/* 1788 */   public static final Sound UI_TOAST_IN = define("ui.toast.in");
/* 1789 */   public static final Sound UI_TOAST_OUT = define("ui.toast.out");
/* 1790 */   public static final Sound BLOCK_VAULT_ACTIVATE = define("block.vault.activate");
/* 1791 */   public static final Sound BLOCK_VAULT_AMBIENT = define("block.vault.ambient");
/* 1792 */   public static final Sound BLOCK_VAULT_BREAK = define("block.vault.break");
/* 1793 */   public static final Sound BLOCK_VAULT_CLOSE_SHUTTER = define("block.vault.close_shutter");
/* 1794 */   public static final Sound BLOCK_VAULT_DEACTIVATE = define("block.vault.deactivate");
/* 1795 */   public static final Sound BLOCK_VAULT_EJECT_ITEM = define("block.vault.eject_item");
/* 1796 */   public static final Sound BLOCK_VAULT_FALL = define("block.vault.fall");
/* 1797 */   public static final Sound BLOCK_VAULT_HIT = define("block.vault.hit");
/* 1798 */   public static final Sound BLOCK_VAULT_INSERT_ITEM = define("block.vault.insert_item");
/* 1799 */   public static final Sound BLOCK_VAULT_INSERT_ITEM_FAIL = define("block.vault.insert_item_fail");
/* 1800 */   public static final Sound BLOCK_VAULT_OPEN_SHUTTER = define("block.vault.open_shutter");
/* 1801 */   public static final Sound BLOCK_VAULT_PLACE = define("block.vault.place");
/* 1802 */   public static final Sound BLOCK_VAULT_STEP = define("block.vault.step");
/* 1803 */   public static final Sound ENTITY_VEX_AMBIENT = define("entity.vex.ambient");
/* 1804 */   public static final Sound ENTITY_VEX_CHARGE = define("entity.vex.charge");
/* 1805 */   public static final Sound ENTITY_VEX_DEATH = define("entity.vex.death");
/* 1806 */   public static final Sound ENTITY_VEX_HURT = define("entity.vex.hurt");
/* 1807 */   public static final Sound ENTITY_VILLAGER_AMBIENT = define("entity.villager.ambient");
/* 1808 */   public static final Sound ENTITY_VILLAGER_CELEBRATE = define("entity.villager.celebrate");
/* 1809 */   public static final Sound ENTITY_VILLAGER_DEATH = define("entity.villager.death");
/* 1810 */   public static final Sound ENTITY_VILLAGER_HURT = define("entity.villager.hurt");
/* 1811 */   public static final Sound ENTITY_VILLAGER_NO = define("entity.villager.no");
/* 1812 */   public static final Sound ENTITY_VILLAGER_TRADE = define("entity.villager.trade");
/* 1813 */   public static final Sound ENTITY_VILLAGER_YES = define("entity.villager.yes");
/* 1814 */   public static final Sound ENTITY_VILLAGER_WORK_ARMORER = define("entity.villager.work_armorer");
/* 1815 */   public static final Sound ENTITY_VILLAGER_WORK_BUTCHER = define("entity.villager.work_butcher");
/* 1816 */   public static final Sound ENTITY_VILLAGER_WORK_CARTOGRAPHER = define("entity.villager.work_cartographer");
/* 1817 */   public static final Sound ENTITY_VILLAGER_WORK_CLERIC = define("entity.villager.work_cleric");
/* 1818 */   public static final Sound ENTITY_VILLAGER_WORK_FARMER = define("entity.villager.work_farmer");
/* 1819 */   public static final Sound ENTITY_VILLAGER_WORK_FISHERMAN = define("entity.villager.work_fisherman");
/* 1820 */   public static final Sound ENTITY_VILLAGER_WORK_FLETCHER = define("entity.villager.work_fletcher");
/* 1821 */   public static final Sound ENTITY_VILLAGER_WORK_LEATHERWORKER = define("entity.villager.work_leatherworker");
/* 1822 */   public static final Sound ENTITY_VILLAGER_WORK_LIBRARIAN = define("entity.villager.work_librarian");
/* 1823 */   public static final Sound ENTITY_VILLAGER_WORK_MASON = define("entity.villager.work_mason");
/* 1824 */   public static final Sound ENTITY_VILLAGER_WORK_SHEPHERD = define("entity.villager.work_shepherd");
/* 1825 */   public static final Sound ENTITY_VILLAGER_WORK_TOOLSMITH = define("entity.villager.work_toolsmith");
/* 1826 */   public static final Sound ENTITY_VILLAGER_WORK_WEAPONSMITH = define("entity.villager.work_weaponsmith");
/* 1827 */   public static final Sound ENTITY_VINDICATOR_AMBIENT = define("entity.vindicator.ambient");
/* 1828 */   public static final Sound ENTITY_VINDICATOR_CELEBRATE = define("entity.vindicator.celebrate");
/* 1829 */   public static final Sound ENTITY_VINDICATOR_DEATH = define("entity.vindicator.death");
/* 1830 */   public static final Sound ENTITY_VINDICATOR_HURT = define("entity.vindicator.hurt");
/* 1831 */   public static final Sound BLOCK_VINE_BREAK = define("block.vine.break");
/* 1832 */   public static final Sound BLOCK_VINE_FALL = define("block.vine.fall");
/* 1833 */   public static final Sound BLOCK_VINE_HIT = define("block.vine.hit");
/* 1834 */   public static final Sound BLOCK_VINE_PLACE = define("block.vine.place");
/* 1835 */   public static final Sound BLOCK_VINE_STEP = define("block.vine.step");
/* 1836 */   public static final Sound BLOCK_LILY_PAD_PLACE = define("block.lily_pad.place");
/* 1837 */   public static final Sound ENTITY_WANDERING_TRADER_AMBIENT = define("entity.wandering_trader.ambient");
/* 1838 */   public static final Sound ENTITY_WANDERING_TRADER_DEATH = define("entity.wandering_trader.death");
/* 1839 */   public static final Sound ENTITY_WANDERING_TRADER_DISAPPEARED = define("entity.wandering_trader.disappeared");
/* 1840 */   public static final Sound ENTITY_WANDERING_TRADER_DRINK_MILK = define("entity.wandering_trader.drink_milk");
/* 1841 */   public static final Sound ENTITY_WANDERING_TRADER_DRINK_POTION = define("entity.wandering_trader.drink_potion");
/* 1842 */   public static final Sound ENTITY_WANDERING_TRADER_HURT = define("entity.wandering_trader.hurt");
/* 1843 */   public static final Sound ENTITY_WANDERING_TRADER_NO = define("entity.wandering_trader.no");
/* 1844 */   public static final Sound ENTITY_WANDERING_TRADER_REAPPEARED = define("entity.wandering_trader.reappeared");
/* 1845 */   public static final Sound ENTITY_WANDERING_TRADER_TRADE = define("entity.wandering_trader.trade");
/* 1846 */   public static final Sound ENTITY_WANDERING_TRADER_YES = define("entity.wandering_trader.yes");
/* 1847 */   public static final Sound ENTITY_WARDEN_AGITATED = define("entity.warden.agitated");
/* 1848 */   public static final Sound ENTITY_WARDEN_AMBIENT = define("entity.warden.ambient");
/* 1849 */   public static final Sound ENTITY_WARDEN_ANGRY = define("entity.warden.angry");
/* 1850 */   public static final Sound ENTITY_WARDEN_ATTACK_IMPACT = define("entity.warden.attack_impact");
/* 1851 */   public static final Sound ENTITY_WARDEN_DEATH = define("entity.warden.death");
/* 1852 */   public static final Sound ENTITY_WARDEN_DIG = define("entity.warden.dig");
/* 1853 */   public static final Sound ENTITY_WARDEN_EMERGE = define("entity.warden.emerge");
/* 1854 */   public static final Sound ENTITY_WARDEN_HEARTBEAT = define("entity.warden.heartbeat");
/* 1855 */   public static final Sound ENTITY_WARDEN_HURT = define("entity.warden.hurt");
/* 1856 */   public static final Sound ENTITY_WARDEN_LISTENING = define("entity.warden.listening");
/* 1857 */   public static final Sound ENTITY_WARDEN_LISTENING_ANGRY = define("entity.warden.listening_angry");
/* 1858 */   public static final Sound ENTITY_WARDEN_NEARBY_CLOSE = define("entity.warden.nearby_close");
/* 1859 */   public static final Sound ENTITY_WARDEN_NEARBY_CLOSER = define("entity.warden.nearby_closer");
/* 1860 */   public static final Sound ENTITY_WARDEN_NEARBY_CLOSEST = define("entity.warden.nearby_closest");
/* 1861 */   public static final Sound ENTITY_WARDEN_ROAR = define("entity.warden.roar");
/* 1862 */   public static final Sound ENTITY_WARDEN_SNIFF = define("entity.warden.sniff");
/* 1863 */   public static final Sound ENTITY_WARDEN_SONIC_BOOM = define("entity.warden.sonic_boom");
/* 1864 */   public static final Sound ENTITY_WARDEN_SONIC_CHARGE = define("entity.warden.sonic_charge");
/* 1865 */   public static final Sound ENTITY_WARDEN_STEP = define("entity.warden.step");
/* 1866 */   public static final Sound ENTITY_WARDEN_TENDRIL_CLICKS = define("entity.warden.tendril_clicks");
/* 1867 */   public static final Sound BLOCK_HANGING_SIGN_WAXED_INTERACT_FAIL = define("block.hanging_sign.waxed_interact_fail");
/* 1868 */   public static final Sound BLOCK_SIGN_WAXED_INTERACT_FAIL = define("block.sign.waxed_interact_fail");
/* 1869 */   public static final Sound BLOCK_WATER_AMBIENT = define("block.water.ambient");
/* 1870 */   public static final Sound WEATHER_RAIN = define("weather.rain");
/* 1871 */   public static final Sound WEATHER_RAIN_ABOVE = define("weather.rain.above");
/* 1872 */   public static final Sound BLOCK_WET_GRASS_BREAK = define("block.wet_grass.break");
/* 1873 */   public static final Sound BLOCK_WET_GRASS_FALL = define("block.wet_grass.fall");
/* 1874 */   public static final Sound BLOCK_WET_GRASS_HIT = define("block.wet_grass.hit");
/* 1875 */   public static final Sound BLOCK_WET_GRASS_PLACE = define("block.wet_grass.place");
/* 1876 */   public static final Sound BLOCK_WET_GRASS_STEP = define("block.wet_grass.step");
/* 1877 */   public static final Sound BLOCK_WET_SPONGE_BREAK = define("block.wet_sponge.break");
/* 1878 */   public static final Sound BLOCK_WET_SPONGE_DRIES = define("block.wet_sponge.dries");
/* 1879 */   public static final Sound BLOCK_WET_SPONGE_FALL = define("block.wet_sponge.fall");
/* 1880 */   public static final Sound BLOCK_WET_SPONGE_HIT = define("block.wet_sponge.hit");
/* 1881 */   public static final Sound BLOCK_WET_SPONGE_PLACE = define("block.wet_sponge.place");
/* 1882 */   public static final Sound BLOCK_WET_SPONGE_STEP = define("block.wet_sponge.step");
/* 1883 */   public static final Sound ENTITY_WIND_CHARGE_WIND_BURST = define("entity.wind_charge.wind_burst");
/* 1884 */   public static final Sound ENTITY_WIND_CHARGE_THROW = define("entity.wind_charge.throw");
/* 1885 */   public static final Sound ENTITY_WITCH_AMBIENT = define("entity.witch.ambient");
/* 1886 */   public static final Sound ENTITY_WITCH_CELEBRATE = define("entity.witch.celebrate");
/* 1887 */   public static final Sound ENTITY_WITCH_DEATH = define("entity.witch.death");
/* 1888 */   public static final Sound ENTITY_WITCH_DRINK = define("entity.witch.drink");
/* 1889 */   public static final Sound ENTITY_WITCH_HURT = define("entity.witch.hurt");
/* 1890 */   public static final Sound ENTITY_WITCH_THROW = define("entity.witch.throw");
/* 1891 */   public static final Sound ENTITY_WITHER_AMBIENT = define("entity.wither.ambient");
/* 1892 */   public static final Sound ENTITY_WITHER_BREAK_BLOCK = define("entity.wither.break_block");
/* 1893 */   public static final Sound ENTITY_WITHER_DEATH = define("entity.wither.death");
/* 1894 */   public static final Sound ENTITY_WITHER_HURT = define("entity.wither.hurt");
/* 1895 */   public static final Sound ENTITY_WITHER_SHOOT = define("entity.wither.shoot");
/* 1896 */   public static final Sound ENTITY_WITHER_SKELETON_AMBIENT = define("entity.wither_skeleton.ambient");
/* 1897 */   public static final Sound ENTITY_WITHER_SKELETON_DEATH = define("entity.wither_skeleton.death");
/* 1898 */   public static final Sound ENTITY_WITHER_SKELETON_HURT = define("entity.wither_skeleton.hurt");
/* 1899 */   public static final Sound ENTITY_WITHER_SKELETON_STEP = define("entity.wither_skeleton.step");
/* 1900 */   public static final Sound ENTITY_WITHER_SPAWN = define("entity.wither.spawn");
/* 1901 */   public static final Sound ITEM_WOLF_ARMOR_BREAK = define("item.wolf_armor.break");
/* 1902 */   public static final Sound ITEM_WOLF_ARMOR_CRACK = define("item.wolf_armor.crack");
/* 1903 */   public static final Sound ITEM_WOLF_ARMOR_DAMAGE = define("item.wolf_armor.damage");
/* 1904 */   public static final Sound ITEM_WOLF_ARMOR_REPAIR = define("item.wolf_armor.repair");
/* 1905 */   public static final Sound ENTITY_WOLF_AMBIENT = define("entity.wolf.ambient");
/* 1906 */   public static final Sound ENTITY_WOLF_DEATH = define("entity.wolf.death");
/* 1907 */   public static final Sound ENTITY_WOLF_GROWL = define("entity.wolf.growl");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/* 1912 */   public static final Sound ENTITY_WOLF_HOWL = define("entity.wolf.howl");
/* 1913 */   public static final Sound ENTITY_WOLF_HURT = define("entity.wolf.hurt");
/* 1914 */   public static final Sound ENTITY_WOLF_PANT = define("entity.wolf.pant");
/* 1915 */   public static final Sound ENTITY_WOLF_SHAKE = define("entity.wolf.shake");
/* 1916 */   public static final Sound ENTITY_WOLF_STEP = define("entity.wolf.step");
/* 1917 */   public static final Sound ENTITY_WOLF_WHINE = define("entity.wolf.whine");
/* 1918 */   public static final Sound BLOCK_WOODEN_DOOR_CLOSE = define("block.wooden_door.close");
/* 1919 */   public static final Sound BLOCK_WOODEN_DOOR_OPEN = define("block.wooden_door.open");
/* 1920 */   public static final Sound BLOCK_WOODEN_TRAPDOOR_CLOSE = define("block.wooden_trapdoor.close");
/* 1921 */   public static final Sound BLOCK_WOODEN_TRAPDOOR_OPEN = define("block.wooden_trapdoor.open");
/* 1922 */   public static final Sound BLOCK_WOODEN_BUTTON_CLICK_OFF = define("block.wooden_button.click_off");
/* 1923 */   public static final Sound BLOCK_WOODEN_BUTTON_CLICK_ON = define("block.wooden_button.click_on");
/* 1924 */   public static final Sound BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF = define("block.wooden_pressure_plate.click_off");
/* 1925 */   public static final Sound BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON = define("block.wooden_pressure_plate.click_on");
/* 1926 */   public static final Sound BLOCK_WOOD_BREAK = define("block.wood.break");
/* 1927 */   public static final Sound BLOCK_WOOD_FALL = define("block.wood.fall");
/* 1928 */   public static final Sound BLOCK_WOOD_HIT = define("block.wood.hit");
/* 1929 */   public static final Sound BLOCK_WOOD_PLACE = define("block.wood.place");
/* 1930 */   public static final Sound BLOCK_WOOD_STEP = define("block.wood.step");
/* 1931 */   public static final Sound BLOCK_WOOL_BREAK = define("block.wool.break");
/* 1932 */   public static final Sound BLOCK_WOOL_FALL = define("block.wool.fall");
/* 1933 */   public static final Sound BLOCK_WOOL_HIT = define("block.wool.hit");
/* 1934 */   public static final Sound BLOCK_WOOL_PLACE = define("block.wool.place");
/* 1935 */   public static final Sound BLOCK_WOOL_STEP = define("block.wool.step");
/* 1936 */   public static final Sound ENTITY_ZOGLIN_AMBIENT = define("entity.zoglin.ambient");
/* 1937 */   public static final Sound ENTITY_ZOGLIN_ANGRY = define("entity.zoglin.angry");
/* 1938 */   public static final Sound ENTITY_ZOGLIN_ATTACK = define("entity.zoglin.attack");
/* 1939 */   public static final Sound ENTITY_ZOGLIN_DEATH = define("entity.zoglin.death");
/* 1940 */   public static final Sound ENTITY_ZOGLIN_HURT = define("entity.zoglin.hurt");
/* 1941 */   public static final Sound ENTITY_ZOGLIN_STEP = define("entity.zoglin.step");
/* 1942 */   public static final Sound ENTITY_ZOMBIE_AMBIENT = define("entity.zombie.ambient");
/* 1943 */   public static final Sound ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR = define("entity.zombie.attack_wooden_door");
/* 1944 */   public static final Sound ENTITY_ZOMBIE_ATTACK_IRON_DOOR = define("entity.zombie.attack_iron_door");
/* 1945 */   public static final Sound ENTITY_ZOMBIE_BREAK_WOODEN_DOOR = define("entity.zombie.break_wooden_door");
/* 1946 */   public static final Sound ENTITY_ZOMBIE_CONVERTED_TO_DROWNED = define("entity.zombie.converted_to_drowned");
/* 1947 */   public static final Sound ENTITY_ZOMBIE_DEATH = define("entity.zombie.death");
/* 1948 */   public static final Sound ENTITY_ZOMBIE_DESTROY_EGG = define("entity.zombie.destroy_egg");
/* 1949 */   public static final Sound ENTITY_ZOMBIE_HORSE_AMBIENT = define("entity.zombie_horse.ambient");
/* 1950 */   public static final Sound ENTITY_ZOMBIE_HORSE_DEATH = define("entity.zombie_horse.death");
/* 1951 */   public static final Sound ENTITY_ZOMBIE_HORSE_HURT = define("entity.zombie_horse.hurt");
/* 1952 */   public static final Sound ENTITY_ZOMBIE_HURT = define("entity.zombie.hurt");
/* 1953 */   public static final Sound ENTITY_ZOMBIE_INFECT = define("entity.zombie.infect");
/* 1954 */   public static final Sound ENTITY_ZOMBIFIED_PIGLIN_AMBIENT = define("entity.zombified_piglin.ambient");
/* 1955 */   public static final Sound ENTITY_ZOMBIFIED_PIGLIN_ANGRY = define("entity.zombified_piglin.angry");
/* 1956 */   public static final Sound ENTITY_ZOMBIFIED_PIGLIN_DEATH = define("entity.zombified_piglin.death");
/* 1957 */   public static final Sound ENTITY_ZOMBIFIED_PIGLIN_HURT = define("entity.zombified_piglin.hurt");
/* 1958 */   public static final Sound ENTITY_ZOMBIE_STEP = define("entity.zombie.step");
/* 1959 */   public static final Sound ENTITY_ZOMBIE_VILLAGER_AMBIENT = define("entity.zombie_villager.ambient");
/* 1960 */   public static final Sound ENTITY_ZOMBIE_VILLAGER_CONVERTED = define("entity.zombie_villager.converted");
/* 1961 */   public static final Sound ENTITY_ZOMBIE_VILLAGER_CURE = define("entity.zombie_villager.cure");
/* 1962 */   public static final Sound ENTITY_ZOMBIE_VILLAGER_DEATH = define("entity.zombie_villager.death");
/* 1963 */   public static final Sound ENTITY_ZOMBIE_VILLAGER_HURT = define("entity.zombie_villager.hurt");
/* 1964 */   public static final Sound ENTITY_ZOMBIE_VILLAGER_STEP = define("entity.zombie_villager.step");
/* 1965 */   public static final Sound EVENT_MOB_EFFECT_BAD_OMEN = define("event.mob_effect.bad_omen");
/* 1966 */   public static final Sound EVENT_MOB_EFFECT_TRIAL_OMEN = define("event.mob_effect.trial_omen");
/* 1967 */   public static final Sound EVENT_MOB_EFFECT_RAID_OMEN = define("event.mob_effect.raid_omen");
/*      */ 
/*      */   
/* 1970 */   public static final Sound MUSIC_DISC_CREATOR = define("music_disc.creator");
/* 1971 */   public static final Sound MUSIC_DISC_CREATOR_MUSIC_BOX = define("music_disc.creator_music_box");
/* 1972 */   public static final Sound MUSIC_DISC_PRECIPICE = define("music_disc.precipice");
/* 1973 */   public static final Sound BLOCK_VAULT_REJECT_REWARDED_PLAYER = define("block.vault.reject_rewarded_player");
/*      */ 
/*      */   
/* 1976 */   public static final Sound UI_HUD_BUBBLE_POP = define("ui.hud.bubble_pop");
/* 1977 */   public static final Sound ITEM_BUNDLE_INSERT_FAIL = define("item.bundle.insert_fail");
/* 1978 */   public static final Sound ENTITY_CREAKING_AMBIENT = define("entity.creaking.ambient");
/* 1979 */   public static final Sound ENTITY_CREAKING_ACTIVATE = define("entity.creaking.activate");
/* 1980 */   public static final Sound ENTITY_CREAKING_DEACTIVATE = define("entity.creaking.deactivate");
/* 1981 */   public static final Sound ENTITY_CREAKING_ATTACK = define("entity.creaking.attack");
/* 1982 */   public static final Sound ENTITY_CREAKING_DEATH = define("entity.creaking.death");
/* 1983 */   public static final Sound ENTITY_CREAKING_STEP = define("entity.creaking.step");
/* 1984 */   public static final Sound ENTITY_CREAKING_FREEZE = define("entity.creaking.freeze");
/* 1985 */   public static final Sound ENTITY_CREAKING_UNFREEZE = define("entity.creaking.unfreeze");
/* 1986 */   public static final Sound ENTITY_CREAKING_SPAWN = define("entity.creaking.spawn");
/* 1987 */   public static final Sound ENTITY_CREAKING_SWAY = define("entity.creaking.sway");
/* 1988 */   public static final Sound BLOCK_CREAKING_HEART_BREAK = define("block.creaking_heart.break");
/* 1989 */   public static final Sound BLOCK_CREAKING_HEART_FALL = define("block.creaking_heart.fall");
/* 1990 */   public static final Sound BLOCK_CREAKING_HEART_HIT = define("block.creaking_heart.hit");
/* 1991 */   public static final Sound BLOCK_CREAKING_HEART_HURT = define("block.creaking_heart.hurt");
/* 1992 */   public static final Sound BLOCK_CREAKING_HEART_PLACE = define("block.creaking_heart.place");
/* 1993 */   public static final Sound BLOCK_CREAKING_HEART_STEP = define("block.creaking_heart.step");
/* 1994 */   public static final Sound BLOCK_CREAKING_HEART_IDLE = define("block.creaking_heart.idle");
/* 1995 */   public static final Sound BLOCK_CREAKING_HEART_SPAWN = define("block.creaking_heart.spawn");
/* 1996 */   public static final Sound BLOCK_PALE_HANGING_MOSS_IDLE = define("block.pale_hanging_moss.idle");
/* 1997 */   public static final Sound ENTITY_PARROT_IMITATE_CREAKING = define("entity.parrot.imitate.creaking");
/* 1998 */   public static final Sound BLOCK_SPAWNER_BREAK = define("block.spawner.break");
/* 1999 */   public static final Sound BLOCK_SPAWNER_FALL = define("block.spawner.fall");
/* 2000 */   public static final Sound BLOCK_SPAWNER_HIT = define("block.spawner.hit");
/* 2001 */   public static final Sound BLOCK_SPAWNER_PLACE = define("block.spawner.place");
/* 2002 */   public static final Sound BLOCK_SPAWNER_STEP = define("block.spawner.step");
/*      */ 
/*      */   
/* 2005 */   public static final Sound ENTITY_CREAKING_TWITCH = define("entity.creaking.twitch");
/* 2006 */   public static final Sound BLOCK_EYEBLOSSOM_OPEN_LONG = define("block.eyeblossom.open_long");
/* 2007 */   public static final Sound BLOCK_EYEBLOSSOM_OPEN = define("block.eyeblossom.open");
/* 2008 */   public static final Sound BLOCK_EYEBLOSSOM_CLOSE_LONG = define("block.eyeblossom.close_long");
/* 2009 */   public static final Sound BLOCK_EYEBLOSSOM_CLOSE = define("block.eyeblossom.close");
/* 2010 */   public static final Sound BLOCK_EYEBLOSSOM_IDLE = define("block.eyeblossom.idle");
/* 2011 */   public static final Sound BLOCK_RESIN_BREAK = define("block.resin.break");
/* 2012 */   public static final Sound BLOCK_RESIN_FALL = define("block.resin.fall");
/* 2013 */   public static final Sound BLOCK_RESIN_PLACE = define("block.resin.place");
/* 2014 */   public static final Sound BLOCK_RESIN_STEP = define("block.resin.step");
/* 2015 */   public static final Sound BLOCK_RESIN_BRICKS_BREAK = define("block.resin_bricks.break");
/* 2016 */   public static final Sound BLOCK_RESIN_BRICKS_FALL = define("block.resin_bricks.fall");
/* 2017 */   public static final Sound BLOCK_RESIN_BRICKS_HIT = define("block.resin_bricks.hit");
/* 2018 */   public static final Sound BLOCK_RESIN_BRICKS_PLACE = define("block.resin_bricks.place");
/* 2019 */   public static final Sound BLOCK_RESIN_BRICKS_STEP = define("block.resin_bricks.step");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2024 */   public static final Sound BLOCK_CACTUS_FLOWER_BREAK = define("block.cactus_flower.break");
/*      */ 
/*      */ 
/*      */   
/* 2028 */   public static final Sound BLOCK_CACTUS_FLOWER_PLACE = define("block.cactus_flower.place");
/*      */ 
/*      */ 
/*      */   
/* 2032 */   public static final Sound BLOCK_DEADBUSH_IDLE = define("block.deadbush.idle");
/*      */ 
/*      */ 
/*      */   
/* 2036 */   public static final Sound BLOCK_FIREFLY_BUSH_IDLE = define("block.firefly_bush.idle");
/*      */ 
/*      */ 
/*      */   
/* 2040 */   public static final Sound BLOCK_IRON_BREAK = define("block.iron.break");
/*      */ 
/*      */ 
/*      */   
/* 2044 */   public static final Sound BLOCK_IRON_STEP = define("block.iron.step");
/*      */ 
/*      */ 
/*      */   
/* 2048 */   public static final Sound BLOCK_IRON_PLACE = define("block.iron.place");
/*      */ 
/*      */ 
/*      */   
/* 2052 */   public static final Sound BLOCK_IRON_HIT = define("block.iron.hit");
/*      */ 
/*      */ 
/*      */   
/* 2056 */   public static final Sound BLOCK_IRON_FALL = define("block.iron.fall");
/*      */ 
/*      */ 
/*      */   
/* 2060 */   public static final Sound BLOCK_LEAF_LITTER_BREAK = define("block.leaf_litter.break");
/*      */ 
/*      */ 
/*      */   
/* 2064 */   public static final Sound BLOCK_LEAF_LITTER_STEP = define("block.leaf_litter.step");
/*      */ 
/*      */ 
/*      */   
/* 2068 */   public static final Sound BLOCK_LEAF_LITTER_PLACE = define("block.leaf_litter.place");
/*      */ 
/*      */ 
/*      */   
/* 2072 */   public static final Sound BLOCK_LEAF_LITTER_HIT = define("block.leaf_litter.hit");
/*      */ 
/*      */ 
/*      */   
/* 2076 */   public static final Sound BLOCK_LEAF_LITTER_FALL = define("block.leaf_litter.fall");
/*      */ 
/*      */ 
/*      */   
/* 2080 */   public static final Sound BLOCK_SAND_IDLE = define("block.sand.idle");
/*      */ 
/*      */ 
/*      */   
/*      */   @Obsolete
/* 2085 */   public static final Sound BLOCK_SAND_WIND = define("block.sand.wind");
/*      */ 
/*      */ 
/*      */   
/* 2089 */   public static final Sound ENTITY_WOLF_PUGLIN_AMBIENT = define("entity.wolf_puglin.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2093 */   public static final Sound ENTITY_WOLF_PUGLIN_DEATH = define("entity.wolf_puglin.death");
/*      */ 
/*      */ 
/*      */   
/* 2097 */   public static final Sound ENTITY_WOLF_PUGLIN_GROWL = define("entity.wolf_puglin.growl");
/*      */ 
/*      */ 
/*      */   
/* 2101 */   public static final Sound ENTITY_WOLF_PUGLIN_HURT = define("entity.wolf_puglin.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2105 */   public static final Sound ENTITY_WOLF_PUGLIN_PANT = define("entity.wolf_puglin.pant");
/*      */ 
/*      */ 
/*      */   
/* 2109 */   public static final Sound ENTITY_WOLF_PUGLIN_WHINE = define("entity.wolf_puglin.whine");
/*      */ 
/*      */ 
/*      */   
/* 2113 */   public static final Sound ENTITY_WOLF_SAD_AMBIENT = define("entity.wolf_sad.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2117 */   public static final Sound ENTITY_WOLF_SAD_DEATH = define("entity.wolf_sad.death");
/*      */ 
/*      */ 
/*      */   
/* 2121 */   public static final Sound ENTITY_WOLF_SAD_GROWL = define("entity.wolf_sad.growl");
/*      */ 
/*      */ 
/*      */   
/* 2125 */   public static final Sound ENTITY_WOLF_SAD_HURT = define("entity.wolf_sad.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2129 */   public static final Sound ENTITY_WOLF_SAD_PANT = define("entity.wolf_sad.pant");
/*      */ 
/*      */ 
/*      */   
/* 2133 */   public static final Sound ENTITY_WOLF_SAD_WHINE = define("entity.wolf_sad.whine");
/*      */ 
/*      */ 
/*      */   
/* 2137 */   public static final Sound ENTITY_WOLF_ANGRY_AMBIENT = define("entity.wolf_angry.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2141 */   public static final Sound ENTITY_WOLF_ANGRY_DEATH = define("entity.wolf_angry.death");
/*      */ 
/*      */ 
/*      */   
/* 2145 */   public static final Sound ENTITY_WOLF_ANGRY_GROWL = define("entity.wolf_angry.growl");
/*      */ 
/*      */ 
/*      */   
/* 2149 */   public static final Sound ENTITY_WOLF_ANGRY_HURT = define("entity.wolf_angry.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2153 */   public static final Sound ENTITY_WOLF_ANGRY_PANT = define("entity.wolf_angry.pant");
/*      */ 
/*      */ 
/*      */   
/* 2157 */   public static final Sound ENTITY_WOLF_ANGRY_WHINE = define("entity.wolf_angry.whine");
/*      */ 
/*      */ 
/*      */   
/* 2161 */   public static final Sound ENTITY_WOLF_GRUMPY_AMBIENT = define("entity.wolf_grumpy.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2165 */   public static final Sound ENTITY_WOLF_GRUMPY_DEATH = define("entity.wolf_grumpy.death");
/*      */ 
/*      */ 
/*      */   
/* 2169 */   public static final Sound ENTITY_WOLF_GRUMPY_GROWL = define("entity.wolf_grumpy.growl");
/*      */ 
/*      */ 
/*      */   
/* 2173 */   public static final Sound ENTITY_WOLF_GRUMPY_HURT = define("entity.wolf_grumpy.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2177 */   public static final Sound ENTITY_WOLF_GRUMPY_PANT = define("entity.wolf_grumpy.pant");
/*      */ 
/*      */ 
/*      */   
/* 2181 */   public static final Sound ENTITY_WOLF_GRUMPY_WHINE = define("entity.wolf_grumpy.whine");
/*      */ 
/*      */ 
/*      */   
/* 2185 */   public static final Sound ENTITY_WOLF_BIG_AMBIENT = define("entity.wolf_big.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2189 */   public static final Sound ENTITY_WOLF_BIG_DEATH = define("entity.wolf_big.death");
/*      */ 
/*      */ 
/*      */   
/* 2193 */   public static final Sound ENTITY_WOLF_BIG_GROWL = define("entity.wolf_big.growl");
/*      */ 
/*      */ 
/*      */   
/* 2197 */   public static final Sound ENTITY_WOLF_BIG_HURT = define("entity.wolf_big.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2201 */   public static final Sound ENTITY_WOLF_BIG_PANT = define("entity.wolf_big.pant");
/*      */ 
/*      */ 
/*      */   
/* 2205 */   public static final Sound ENTITY_WOLF_BIG_WHINE = define("entity.wolf_big.whine");
/*      */ 
/*      */ 
/*      */   
/* 2209 */   public static final Sound ENTITY_WOLF_CUTE_AMBIENT = define("entity.wolf_cute.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2213 */   public static final Sound ENTITY_WOLF_CUTE_DEATH = define("entity.wolf_cute.death");
/*      */ 
/*      */ 
/*      */   
/* 2217 */   public static final Sound ENTITY_WOLF_CUTE_GROWL = define("entity.wolf_cute.growl");
/*      */ 
/*      */ 
/*      */   
/* 2221 */   public static final Sound ENTITY_WOLF_CUTE_HURT = define("entity.wolf_cute.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2225 */   public static final Sound ENTITY_WOLF_CUTE_PANT = define("entity.wolf_cute.pant");
/*      */ 
/*      */ 
/*      */   
/* 2229 */   public static final Sound ENTITY_WOLF_CUTE_WHINE = define("entity.wolf_cute.whine");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2234 */   public static final Sound BLOCK_DRIED_GHAST_BREAK = define("block.dried_ghast.break");
/*      */ 
/*      */ 
/*      */   
/* 2238 */   public static final Sound BLOCK_DRIED_GHAST_STEP = define("block.dried_ghast.step");
/*      */ 
/*      */ 
/*      */   
/* 2242 */   public static final Sound BLOCK_DRIED_GHAST_FALL = define("block.dried_ghast.fall");
/*      */ 
/*      */ 
/*      */   
/* 2246 */   public static final Sound BLOCK_DRIED_GHAST_AMBIENT = define("block.dried_ghast.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2250 */   public static final Sound BLOCK_DRIED_GHAST_AMBIENT_WATER = define("block.dried_ghast.ambient_water");
/*      */ 
/*      */ 
/*      */   
/* 2254 */   public static final Sound BLOCK_DRIED_GHAST_PLACE = define("block.dried_ghast.place");
/*      */ 
/*      */ 
/*      */   
/* 2258 */   public static final Sound BLOCK_DRIED_GHAST_PLACE_IN_WATER = define("block.dried_ghast.place_in_water");
/*      */ 
/*      */ 
/*      */   
/* 2262 */   public static final Sound BLOCK_DRIED_GHAST_TRANSITION = define("block.dried_ghast.transition");
/*      */ 
/*      */ 
/*      */   
/* 2266 */   public static final Sound BLOCK_DRY_GRASS_AMBIENT = define("block.dry_grass.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2270 */   public static final Sound ENTITY_GHASTLING_AMBIENT = define("entity.ghastling.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2274 */   public static final Sound ENTITY_GHASTLING_DEATH = define("entity.ghastling.death");
/*      */ 
/*      */ 
/*      */   
/* 2278 */   public static final Sound ENTITY_GHASTLING_HURT = define("entity.ghastling.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2282 */   public static final Sound ENTITY_GHASTLING_SPAWN = define("entity.ghastling.spawn");
/*      */ 
/*      */ 
/*      */   
/* 2286 */   public static final Sound ENTITY_HAPPY_GHAST_AMBIENT = define("entity.happy_ghast.ambient");
/*      */ 
/*      */ 
/*      */   
/* 2290 */   public static final Sound ENTITY_HAPPY_GHAST_DEATH = define("entity.happy_ghast.death");
/*      */ 
/*      */ 
/*      */   
/* 2294 */   public static final Sound ENTITY_HAPPY_GHAST_HURT = define("entity.happy_ghast.hurt");
/*      */ 
/*      */ 
/*      */   
/* 2298 */   public static final Sound ENTITY_HAPPY_GHAST_RIDING = define("entity.happy_ghast.riding");
/*      */ 
/*      */ 
/*      */   
/* 2302 */   public static final Sound ENTITY_HAPPY_GHAST_EQUIP = define("entity.happy_ghast.equip");
/*      */ 
/*      */ 
/*      */   
/* 2306 */   public static final Sound ENTITY_HAPPY_GHAST_UNEQUIP = define("entity.happy_ghast.unequip");
/*      */ 
/*      */ 
/*      */   
/* 2310 */   public static final Sound ENTITY_HAPPY_GHAST_HARNESS_GOGGLES_UP = define("entity.happy_ghast.harness_goggles_up");
/*      */ 
/*      */ 
/*      */   
/* 2314 */   public static final Sound ENTITY_HAPPY_GHAST_HARNESS_GOGGLES_DOWN = define("entity.happy_ghast.harness_goggles_down");
/*      */ 
/*      */ 
/*      */   
/* 2318 */   public static final Sound ITEM_HORSE_ARMOR_UNEQUIP = define("item.horse_armor.unequip");
/*      */ 
/*      */ 
/*      */   
/* 2322 */   public static final Sound ITEM_LEAD_UNTIED = define("item.lead.untied");
/*      */ 
/*      */ 
/*      */   
/* 2326 */   public static final Sound ITEM_LEAD_TIED = define("item.lead.tied");
/*      */ 
/*      */ 
/*      */   
/* 2330 */   public static final Sound ITEM_LEAD_BREAK = define("item.lead.break");
/*      */ 
/*      */ 
/*      */   
/* 2334 */   public static final Sound ITEM_LLAMA_CARPET_UNEQUIP = define("item.llama_carpet.unequip");
/*      */ 
/*      */ 
/*      */   
/* 2338 */   public static final Sound MUSIC_DISC_TEARS = define("music_disc.tears");
/*      */ 
/*      */ 
/*      */   
/* 2342 */   public static final Sound ITEM_SHEARS_SNIP = define("item.shears.snip");
/*      */ 
/*      */ 
/*      */   
/* 2346 */   public static final Sound ITEM_SADDLE_UNEQUIP = define("item.saddle.unequip");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2351 */   public static final Sound MUSIC_DISC_LAVA_CHICKEN = define("music_disc.lava_chicken");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Collection<Sound> values() {
/* 2359 */     return REGISTRY.getEntries();
/*      */   }
/*      */   
/*      */   static {
/* 2363 */     REGISTRY.unloadMappings();
/*      */   }
/*      */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\sound\Sounds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */