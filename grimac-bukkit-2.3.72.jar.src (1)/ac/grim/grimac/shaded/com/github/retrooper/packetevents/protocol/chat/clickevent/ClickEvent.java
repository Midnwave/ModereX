/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.dialog.Dialog;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.resources.ResourceLocation;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.adventure.NbtTagHolder;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ public interface ClickEvent
/*    */ {
/*    */   static ClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 34 */     String actionName = compound.getStringTagValueOrThrow("action");
/* 35 */     ClickEventAction<?> action = (ClickEventAction)ClickEventActions.getRegistry().getByNameOrThrow(actionName);
/* 36 */     return (ClickEvent)action.decode(compound, wrapper);
/*    */   }
/*    */ 
/*    */   
/*    */   static void encode(NBTCompound compound, PacketWrapper<?> wrapper, ClickEvent clickEvent) {
/* 41 */     compound.set("action", clickEvent.getAction().getName(), ResourceLocation::encode, wrapper);
/* 42 */     clickEvent.getAction().encode(compound, wrapper, clickEvent);
/*    */   }
/*    */   
/*    */   static ClickEvent fromAdventure(ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent clickEvent) {
/*    */     ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent.Payload.Custom payload;
/*    */     NbtTagHolder nbtTag;
/* 48 */     switch (clickEvent.action()) {
/*    */       case OPEN_URL:
/* 50 */         return new OpenUrlClickEvent(clickEvent.value());
/*    */       case OPEN_FILE:
/* 52 */         return new OpenFileClickEvent(clickEvent.value());
/*    */       case RUN_COMMAND:
/* 54 */         return new RunCommandClickEvent(clickEvent.value());
/*    */       case SUGGEST_COMMAND:
/* 56 */         return new SuggestCommandClickEvent(clickEvent.value());
/*    */       case CHANGE_PAGE:
/* 58 */         return new ChangePageClickEvent(clickEvent.value());
/*    */       case COPY_TO_CLIPBOARD:
/* 60 */         return new CopyToClipboardClickEvent(clickEvent.value());
/*    */       case SHOW_DIALOG:
/* 62 */         return new ShowDialogClickEvent((Dialog)((ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent.Payload.Dialog)clickEvent.payload()).dialog());
/*    */       case CUSTOM:
/* 64 */         payload = (ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent.Payload.Custom)clickEvent.payload();
/* 65 */         nbtTag = (NbtTagHolder)payload.nbt();
/* 66 */         return new CustomClickEvent(new ResourceLocation(payload
/* 67 */               .key()), 
/* 68 */             (nbtTag.getTag() instanceof ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTEnd) ? null : nbtTag.getTag());
/*    */     } 
/*    */     
/* 71 */     throw new UnsupportedOperationException("Unsupported clickevent: " + clickEvent);
/*    */   }
/*    */   
/*    */   ClickEventAction<?> getAction();
/*    */   
/*    */   ac.grim.grimac.shaded.kyori.adventure.text.event.ClickEvent asAdventure();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\ClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */