/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.input;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtMapDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtMapEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
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
/*    */ public final class InputControlTypes
/*    */ {
/* 30 */   private static final VersionedRegistry<InputControlType<?>> REGISTRY = new VersionedRegistry("input_control_type");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<InputControlType<?>> getRegistry() {
/* 36 */     return REGISTRY;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends InputControl> InputControlType<T> define(String name, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 43 */     return (InputControlType<T>)REGISTRY.define(name, data -> new StaticInputControlType<>(data, decoder, encoder));
/*    */   }
/*    */ 
/*    */   
/* 47 */   public static final InputControlType<BooleanInputControl> BOOLEAN = define("boolean", BooleanInputControl::decode, BooleanInputControl::encode);
/*    */   
/* 49 */   public static final InputControlType<NumberRangeInputControl> NUMBER_RANGE = define("number_range", NumberRangeInputControl::decode, NumberRangeInputControl::encode);
/*    */   
/* 51 */   public static final InputControlType<SingleOptionInputControl> SINGLE_OPTION = define("single_option", SingleOptionInputControl::decode, SingleOptionInputControl::encode);
/*    */   
/* 53 */   public static final InputControlType<TextInputControl> TEXT = define("text", TextInputControl::decode, TextInputControl::encode);
/*    */ 
/*    */   
/*    */   static {
/* 57 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\input\InputControlTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */