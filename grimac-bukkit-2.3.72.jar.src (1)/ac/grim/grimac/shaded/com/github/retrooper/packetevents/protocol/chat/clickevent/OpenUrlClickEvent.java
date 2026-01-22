/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
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
/*    */ public class OpenUrlClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final String url;
/*    */   
/*    */   public OpenUrlClickEvent(String url) {
/* 33 */     this.url = url;
/*    */   }
/*    */   
/*    */   public static OpenUrlClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 37 */     boolean v1215 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5);
/* 38 */     String url = compound.getStringTagValueOrThrow(v1215 ? "url" : "value");
/* 39 */     return new OpenUrlClickEvent(url);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, OpenUrlClickEvent clickEvent) {
/* 43 */     boolean v1215 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5);
/* 44 */     compound.setTag(v1215 ? "url" : "value", (NBT)new NBTString(clickEvent.url));
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 49 */     return ClickEventActions.OPEN_URL;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 54 */     return ClickEvent.openUrl(this.url);
/*    */   }
/*    */   
/*    */   public String getUrl() {
/* 58 */     return this.url;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\OpenUrlClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */