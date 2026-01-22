/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.body;
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
/*    */ public final class DialogBodyTypes
/*    */ {
/* 30 */   private static final VersionedRegistry<DialogBodyType<?>> REGISTRY = new VersionedRegistry("dialog_body_type");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends DialogBody> DialogBodyType<T> define(String name, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 37 */     return (DialogBodyType<T>)REGISTRY.define(name, data -> new StaticDialogBodyType<>(data, decoder, encoder));
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<DialogBodyType<?>> getRegistry() {
/* 41 */     return REGISTRY;
/*    */   }
/*    */   
/* 44 */   public static final DialogBodyType<ItemDialogBody> ITEM = define("item", ItemDialogBody::decode, ItemDialogBody::encode);
/*    */   
/* 46 */   public static final DialogBodyType<PlainMessageDialogBody> PLAIN_MESSAGE = define("plain_message", PlainMessageDialogBody::decode, PlainMessageDialogBody::encode);
/*    */ 
/*    */   
/*    */   static {
/* 50 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\body\DialogBodyTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */