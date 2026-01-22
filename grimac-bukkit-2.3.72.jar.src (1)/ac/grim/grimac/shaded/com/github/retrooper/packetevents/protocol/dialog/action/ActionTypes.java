/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.action;
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
/*    */ public final class ActionTypes
/*    */ {
/* 30 */   private static final VersionedRegistry<ActionType<?>> REGISTRY = new VersionedRegistry("dialog_action_type");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends Action> ActionType<T> define(String name, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 37 */     return (ActionType<T>)REGISTRY.define(name, data -> new StaticActionType<>(data, decoder, encoder));
/*    */   }
/*    */   
/* 40 */   public static final ActionType<StaticAction> OPEN_URL = define("open_url", StaticAction::decode, StaticAction::encode);
/*    */   
/* 42 */   public static final ActionType<StaticAction> RUN_COMMAND = define("run_command", StaticAction::decode, StaticAction::encode);
/*    */   
/* 44 */   public static final ActionType<StaticAction> SUGGEST_COMMAND = define("suggest_command", StaticAction::decode, StaticAction::encode);
/*    */   
/* 46 */   public static final ActionType<StaticAction> SHOW_DIALOG = define("show_dialog", StaticAction::decode, StaticAction::encode);
/*    */   
/* 48 */   public static final ActionType<StaticAction> CHANGE_PAGE = define("change_page", StaticAction::decode, StaticAction::encode);
/*    */   
/* 50 */   public static final ActionType<StaticAction> COPY_TO_CLIPBOARD = define("copy_to_clipboard", StaticAction::decode, StaticAction::encode);
/*    */   
/* 52 */   public static final ActionType<StaticAction> CUSTOM = define("custom", StaticAction::decode, StaticAction::encode);
/*    */   
/* 54 */   public static final ActionType<DynamicRunCommandAction> DYNAMIC_RUN_COMMAND = define("dynamic/run_command", DynamicRunCommandAction::decode, DynamicRunCommandAction::encode);
/*    */   
/* 56 */   public static final ActionType<DynamicCustomAction> DYNAMIC_CUSTOM = define("dynamic/custom", DynamicCustomAction::decode, DynamicCustomAction::encode);
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<ActionType<?>> getRegistry() {
/* 60 */     return REGISTRY;
/*    */   }
/*    */   
/*    */   static {
/* 64 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\action\ActionTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */