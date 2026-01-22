/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.Dialog;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.Dialogs;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntity;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.mapper.MappedEntityRef;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.IRegistry;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.dialog.DialogLike;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent;
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
/*    */ @NullMarked
/*    */ public class ShowDialogClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final MappedEntityRef<Dialog> dialog;
/*    */   
/*    */   public ShowDialogClickEvent(Dialog dialog) {
/* 34 */     this((MappedEntityRef<Dialog>)new MappedEntityRef.Static((MappedEntity)dialog));
/*    */   }
/*    */   
/*    */   public ShowDialogClickEvent(MappedEntityRef<Dialog> dialog) {
/* 38 */     this.dialog = dialog;
/*    */   }
/*    */   
/*    */   public static ShowDialogClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 42 */     MappedEntityRef<Dialog> dialog = MappedEntityRef.decode(compound.getTagOrThrow("dialog"), 
/* 43 */         (IRegistry)Dialogs.getRegistry(), Dialog::decode, wrapper);
/* 44 */     return new ShowDialogClickEvent(dialog);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, ShowDialogClickEvent clickEvent) {
/* 48 */     compound.setTag("dialog", MappedEntityRef.encode(wrapper, Dialog::encode, clickEvent.dialog));
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 53 */     return ClickEventActions.SHOW_DIALOG;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 58 */     return ClickEvent.showDialog((DialogLike)this.dialog.get());
/*    */   }
/*    */   
/*    */   public MappedEntityRef<Dialog> getDialogRef() {
/* 62 */     return this.dialog;
/*    */   }
/*    */   
/*    */   public Dialog getDialog() {
/* 66 */     return (Dialog)this.dialog.get();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\ShowDialogClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */