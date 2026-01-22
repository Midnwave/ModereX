/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtMapDecoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.util.NbtMapEncoder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.TypesBuilderData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*    */ @NullMarked
/*    */ public final class ClickEventActions
/*    */ {
/* 30 */   private static final VersionedRegistry<ClickEventAction<?>> REGISTRY = new VersionedRegistry("click_event_action");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static <T extends ClickEvent> ClickEventAction<T> define(String name, boolean allowFromServer, NbtMapDecoder<T> decoder, NbtMapEncoder<T> encoder) {
/* 40 */     return (ClickEventAction<T>)REGISTRY.define(name, data -> new StaticClickEventAction<>(data, allowFromServer, decoder, encoder));
/*    */   }
/*    */ 
/*    */   
/*    */   public static VersionedRegistry<ClickEventAction<?>> getRegistry() {
/* 45 */     return REGISTRY;
/*    */   }
/*    */   
/* 48 */   public static final ClickEventAction<OpenUrlClickEvent> OPEN_URL = define("open_url", true, OpenUrlClickEvent::decode, OpenUrlClickEvent::encode);
/*    */   
/* 50 */   public static final ClickEventAction<OpenFileClickEvent> OPEN_FILE = define("open_file", false, OpenFileClickEvent::decode, OpenFileClickEvent::encode);
/*    */   
/* 52 */   public static final ClickEventAction<RunCommandClickEvent> RUN_COMMAND = define("run_command", true, RunCommandClickEvent::decode, RunCommandClickEvent::encode);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Obsolete
/* 58 */   public static final ClickEventAction<TwitchUserInfoClickEvent> TWITCH_USER_INFO = define("twitch_user_info", false, TwitchUserInfoClickEvent::decode, TwitchUserInfoClickEvent::encode);
/*    */   
/* 60 */   public static final ClickEventAction<SuggestCommandClickEvent> SUGGEST_COMMAND = define("suggest_command", true, SuggestCommandClickEvent::decode, SuggestCommandClickEvent::encode);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 66 */   public static final ClickEventAction<ChangePageClickEvent> CHANGE_PAGE = define("change_page", true, ChangePageClickEvent::decode, ChangePageClickEvent::encode);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 72 */   public static final ClickEventAction<CopyToClipboardClickEvent> COPY_TO_CLIPBOARD = define("copy_to_clipboard", true, CopyToClipboardClickEvent::decode, CopyToClipboardClickEvent::encode);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 78 */   public static final ClickEventAction<ShowDialogClickEvent> SHOW_DIALOG = define("show_dialog", true, ShowDialogClickEvent::decode, ShowDialogClickEvent::encode);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 83 */   public static final ClickEventAction<CustomClickEvent> CUSTOM = define("custom", true, CustomClickEvent::decode, CustomClickEvent::encode);
/*    */ 
/*    */   
/*    */   static {
/* 87 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\ClickEventActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */