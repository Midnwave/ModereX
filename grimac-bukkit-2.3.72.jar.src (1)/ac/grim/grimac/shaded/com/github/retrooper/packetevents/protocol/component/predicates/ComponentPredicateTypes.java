/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.component.predicates;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Experimental;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ComponentPredicateTypes
/*    */ {
/* 33 */   private static final VersionedRegistry<ComponentPredicateType<?>> REGISTRY = new VersionedRegistry("data_component_predicate_type");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends IComponentPredicate> ComponentPredicateType<T> define(String key, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 42 */     return (ComponentPredicateType<T>)REGISTRY.define(key, data -> new StaticComponentPredicateType<>(data, reader, writer));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<ComponentPredicateType<?>> getRegistry() {
/* 47 */     return REGISTRY;
/*    */   }
/*    */ 
/*    */   
/*    */   @Experimental
/* 52 */   public static final ComponentPredicateType<NbtComponentPredicate> DAMAGE = define("damage", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 55 */   public static final ComponentPredicateType<NbtComponentPredicate> ENCHANTMENTS = define("enchantments", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 58 */   public static final ComponentPredicateType<NbtComponentPredicate> STORED_ENCHANTMENTS = define("stored_enchantments", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 61 */   public static final ComponentPredicateType<NbtComponentPredicate> POTION_CONTENTS = define("potion_contents", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 64 */   public static final ComponentPredicateType<NbtComponentPredicate> CUSTOM_DATA = define("custom_data", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 67 */   public static final ComponentPredicateType<NbtComponentPredicate> CONTAINER = define("container", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 70 */   public static final ComponentPredicateType<NbtComponentPredicate> BUNDLE_CONTENTS = define("bundle_contents", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 73 */   public static final ComponentPredicateType<NbtComponentPredicate> FIREWORK_EXPLOSION = define("firework_explosion", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 76 */   public static final ComponentPredicateType<NbtComponentPredicate> FIREWORKS = define("fireworks", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 79 */   public static final ComponentPredicateType<NbtComponentPredicate> WRITABLE_BOOK_CONTENT = define("writable_book_content", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 82 */   public static final ComponentPredicateType<NbtComponentPredicate> WRITTEN_BOOK_CONTENT = define("written_book_content", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 85 */   public static final ComponentPredicateType<NbtComponentPredicate> ATTRIBUTE_MODIFIERS = define("attribute_modifiers", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 88 */   public static final ComponentPredicateType<NbtComponentPredicate> TRIM = define("trim", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   @Experimental
/* 91 */   public static final ComponentPredicateType<NbtComponentPredicate> JUKEBOX_PLAYABLE = define("jukebox_playable", NbtComponentPredicate::read, NbtComponentPredicate::write);
/*    */   
/*    */   static {
/* 94 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\component\predicates\ComponentPredicateTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */