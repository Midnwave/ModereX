/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntityRefSet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntitySet;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.VersionedRegistry;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import java.util.Collections;
/*    */ import java.util.Objects;
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
/*    */ public final class Dialogs
/*    */ {
/* 35 */   private static final ActionButton DEFAULT_BACK_BUTTON = new ActionButton(new CommonButtonData(
/* 36 */         (Component)Component.translatable("gui.back"), null, 200), null);
/*    */ 
/*    */   
/* 39 */   private static final VersionedRegistry<Dialog> REGISTRY = new VersionedRegistry("dialog");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Internal
/*    */   public static Dialog define(String name, Dialog dialog) {
/* 46 */     Objects.requireNonNull(dialog); return (Dialog)REGISTRY.define(name, dialog::copy);
/*    */   }
/*    */   
/*    */   public static VersionedRegistry<Dialog> getRegistry() {
/* 50 */     return REGISTRY;
/*    */   }
/*    */   
/* 53 */   public static final Dialog SERVER_LINKS = define("server_links", new ServerLinksDialog(new CommonDialogData(
/*    */           
/* 55 */           (Component)Component.translatable("menu.server_links.title"), 
/* 56 */           (Component)Component.translatable("menu.server_links"), true, true, DialogAction.CLOSE, 
/*    */ 
/*    */ 
/*    */           
/* 60 */           Collections.emptyList(), 
/* 61 */           Collections.emptyList()), DEFAULT_BACK_BUTTON, 1, 310));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 67 */   public static final Dialog CUSTOM_OPTIONS = define("custom_options", new DialogListDialog(new CommonDialogData(
/*    */           
/* 69 */           (Component)Component.translatable("menu.custom_options.title"), 
/* 70 */           (Component)Component.translatable("menu.custom_options"), true, true, DialogAction.CLOSE, 
/*    */ 
/*    */ 
/*    */           
/* 74 */           Collections.emptyList(), 
/* 75 */           Collections.emptyList()), (MappedEntityRefSet<Dialog>)new MappedEntitySet(new ResourceLocation("pause_screen_additions")), DEFAULT_BACK_BUTTON, 1, 310));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 82 */   public static final Dialog QUICK_ACTIONS = define("quick_actions", new DialogListDialog(new CommonDialogData(
/*    */           
/* 84 */           (Component)Component.translatable("menu.quick_actions.title"), 
/* 85 */           (Component)Component.translatable("menu.quick_actions"), true, true, DialogAction.CLOSE, 
/*    */ 
/*    */ 
/*    */           
/* 89 */           Collections.emptyList(), 
/* 90 */           Collections.emptyList()), (MappedEntityRefSet<Dialog>)new MappedEntitySet(new ResourceLocation("quick_actions")), DEFAULT_BACK_BUTTON, 1, 310));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static {
/* 99 */     REGISTRY.unloadMappings();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\dialog\Dialogs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */