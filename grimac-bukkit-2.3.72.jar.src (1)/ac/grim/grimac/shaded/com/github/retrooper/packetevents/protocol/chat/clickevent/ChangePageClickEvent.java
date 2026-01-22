/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTInt;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTString;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
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
/*    */ public class ChangePageClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final int page;
/*    */   
/*    */   @Obsolete
/*    */   public ChangePageClickEvent(String page) {
/* 36 */     this(Integer.parseInt(page));
/*    */   }
/*    */   
/*    */   public ChangePageClickEvent(int page) {
/* 40 */     this.page = page;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static ChangePageClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 46 */     int page = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) ? compound.getNumberTagValueOrThrow("page").intValue() : Integer.parseInt(compound.getStringTagValueOrThrow("value"));
/* 47 */     return new ChangePageClickEvent(page);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, ChangePageClickEvent clickEvent) {
/* 51 */     if (wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5)) {
/* 52 */       compound.setTag("page", (NBT)new NBTInt(clickEvent.page));
/*    */     } else {
/* 54 */       compound.setTag("value", (NBT)new NBTString(Integer.toString(clickEvent.page)));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 60 */     return ClickEventActions.CHANGE_PAGE;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 65 */     return ClickEvent.changePage(this.page);
/*    */   }
/*    */   
/*    */   public int getPage() {
/* 69 */     return this.page;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\ChangePageClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */