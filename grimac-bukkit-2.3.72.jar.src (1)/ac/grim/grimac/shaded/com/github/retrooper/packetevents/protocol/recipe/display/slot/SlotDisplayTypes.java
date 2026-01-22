/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.recipe.display.slot;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ public final class SlotDisplayTypes
/*    */ {
/* 26 */   private static final VersionedRegistry<SlotDisplayType<?>> REGISTRY = new VersionedRegistry("slot_display");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static <T extends SlotDisplay<?>> SlotDisplayType<T> register(String id, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 34 */     return (SlotDisplayType<T>)REGISTRY.define(id, data -> new StaticSlotDisplayType<>(data, reader, writer));
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<SlotDisplayType<?>> getRegistry() {
/* 38 */     return REGISTRY;
/*    */   }
/*    */   
/* 41 */   public static final SlotDisplayType<EmptySlotDisplay> EMPTY = register("empty", EmptySlotDisplay::read, EmptySlotDisplay::write);
/*    */   
/* 43 */   public static final SlotDisplayType<AnyFuelSlotDisplay> ANY_FUEL = register("any_fuel", AnyFuelSlotDisplay::read, AnyFuelSlotDisplay::write);
/*    */   
/* 45 */   public static final SlotDisplayType<ItemSlotDisplay> ITEM = register("item", ItemSlotDisplay::read, ItemSlotDisplay::write);
/*    */   
/* 47 */   public static final SlotDisplayType<ItemStackSlotDisplay> ITEM_STACK = register("item_stack", ItemStackSlotDisplay::read, ItemStackSlotDisplay::write);
/*    */   
/* 49 */   public static final SlotDisplayType<TagSlotDisplay> TAG = register("tag", TagSlotDisplay::read, TagSlotDisplay::write);
/*    */   
/* 51 */   public static final SlotDisplayType<SmithingTrimSlotDisplay> SMITHING_TRIM = register("smithing_trim", SmithingTrimSlotDisplay::read, SmithingTrimSlotDisplay::write);
/*    */   
/* 53 */   public static final SlotDisplayType<WithRemainderSlotDisplay> WITH_REMAINDER = register("with_remainder", WithRemainderSlotDisplay::read, WithRemainderSlotDisplay::write);
/*    */   
/* 55 */   public static final SlotDisplayType<CompositeSlotDisplay> COMPOSITE = register("composite", CompositeSlotDisplay::read, CompositeSlotDisplay::write);
/*    */ 
/*    */   
/*    */   static {
/* 59 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\recipe\display\slot\SlotDisplayTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */