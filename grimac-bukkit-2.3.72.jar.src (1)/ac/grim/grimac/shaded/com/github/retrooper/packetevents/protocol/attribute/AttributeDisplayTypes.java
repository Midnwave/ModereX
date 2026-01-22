/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.attribute;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ @NullMarked
/*    */ public final class AttributeDisplayTypes
/*    */ {
/* 29 */   private static final VersionedRegistry<AttributeDisplayType<?>> REGISTRY = new VersionedRegistry("attribute_display_type");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends AttributeDisplay> AttributeDisplayType<T> define(String name, PacketWrapper.Reader<T> reader, PacketWrapper.Writer<T> writer) {
/* 38 */     return (AttributeDisplayType<T>)REGISTRY.define(name, data -> new StaticAttributeDisplayType<>(data, reader, writer));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<AttributeDisplayType<?>> getRegistry() {
/* 43 */     return REGISTRY;
/*    */   }
/*    */   
/* 46 */   public static final AttributeDisplayType<DefaultAttributeDisplay> DEFAULT = define("default", DefaultAttributeDisplay::read, DefaultAttributeDisplay::write);
/*    */   
/* 48 */   public static final AttributeDisplayType<HiddenAttributeDisplay> HIDDEN = define("hidden", HiddenAttributeDisplay::read, HiddenAttributeDisplay::write);
/*    */   
/* 50 */   public static final AttributeDisplayType<OverrideAttributeDisplay> OVERRIDE = define("override", OverrideAttributeDisplay::read, OverrideAttributeDisplay::write);
/*    */ 
/*    */   
/*    */   static {
/* 54 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\attribute\AttributeDisplayTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */