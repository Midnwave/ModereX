/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
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
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ public class CopyToClipboardClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   public CopyToClipboardClickEvent(String value) {
/* 32 */     this.value = value;
/*    */   }
/*    */   
/*    */   public static CopyToClipboardClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 36 */     String value = compound.getStringTagValueOrThrow("value");
/* 37 */     return new CopyToClipboardClickEvent(value);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, CopyToClipboardClickEvent clickEvent) {
/* 41 */     compound.setTag("value", (NBT)new NBTString(clickEvent.value));
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 46 */     return ClickEventActions.COPY_TO_CLIPBOARD;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 51 */     return ClickEvent.copyToClipboard(this.value);
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 55 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\CopyToClipboardClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */