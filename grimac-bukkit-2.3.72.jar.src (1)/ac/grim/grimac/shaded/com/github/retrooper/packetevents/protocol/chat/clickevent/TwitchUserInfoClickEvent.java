/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.chat.clickevent;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBT;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @NullMarked
/*    */ @Obsolete
/*    */ public class TwitchUserInfoClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   public TwitchUserInfoClickEvent(String value) {
/* 37 */     this.value = value;
/*    */   }
/*    */   
/*    */   public static TwitchUserInfoClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 41 */     String value = compound.getStringTagValueOrThrow("value");
/* 42 */     return new TwitchUserInfoClickEvent(value);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, TwitchUserInfoClickEvent clickEvent) {
/* 46 */     compound.setTag("value", (NBT)new NBTString(clickEvent.value));
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 51 */     return ClickEventActions.TWITCH_USER_INFO;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 56 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 60 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\TwitchUserInfoClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */