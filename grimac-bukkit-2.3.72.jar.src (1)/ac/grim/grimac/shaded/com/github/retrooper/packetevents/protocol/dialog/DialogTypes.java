/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog;
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
/*    */ public final class DialogTypes
/*    */ {
/* 30 */   private static final VersionedRegistry<DialogType<?>> REGISTRY = new VersionedRegistry("dialog_type");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<DialogType<?>> getRegistry() {
/* 36 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   @Internal
/*    */   public static <T extends Dialog> DialogType<T> define(String name, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 41 */     return (DialogType<T>)REGISTRY.define(name, data -> new StaticDialogType<>(data, decoder, encoder));
/*    */   }
/*    */   
/* 44 */   public static final DialogType<NoticeDialog> NOTICE = define("notice", NoticeDialog::decode, NoticeDialog::encode);
/*    */   
/* 46 */   public static final DialogType<ServerLinksDialog> SERVER_LINKS = define("server_links", ServerLinksDialog::decode, ServerLinksDialog::encode);
/*    */   
/* 48 */   public static final DialogType<DialogListDialog> DIALOG_LIST = define("dialog_list", DialogListDialog::decode, DialogListDialog::encode);
/*    */   
/* 50 */   public static final DialogType<MultiActionDialog> MULTI_ACTION = define("multi_action", MultiActionDialog::decode, MultiActionDialog::encode);
/*    */   
/* 52 */   public static final DialogType<ConfirmationDialog> CONFIRMATION = define("confirmation", ConfirmationDialog::decode, ConfirmationDialog::encode);
/*    */ 
/*    */   
/*    */   static {
/* 56 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\DialogTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */