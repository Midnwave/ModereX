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
/*    */ public class RunCommandClickEvent
/*    */   implements ClickEvent
/*    */ {
/*    */   private final String command;
/*    */   
/*    */   public RunCommandClickEvent(String command) {
/* 33 */     this.command = command;
/*    */   }
/*    */   
/*    */   public static RunCommandClickEvent decode(NBTCompound compound, PacketWrapper<?> wrapper) {
/* 37 */     boolean v1215 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5);
/* 38 */     String command = compound.getStringTagValueOrThrow(v1215 ? "command" : "value");
/* 39 */     return new RunCommandClickEvent(command);
/*    */   }
/*    */   
/*    */   public static void encode(NBTCompound compound, PacketWrapper<?> wrapper, RunCommandClickEvent clickEvent) {
/* 43 */     boolean v1215 = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5);
/* 44 */     compound.setTag(v1215 ? "command" : "value", (NBT)new NBTString(clickEvent.command));
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEventAction<?> getAction() {
/* 49 */     return ClickEventActions.RUN_COMMAND;
/*    */   }
/*    */ 
/*    */   
/*    */   public ClickEvent asAdventure() {
/* 54 */     return ClickEvent.runCommand(this.command);
/*    */   }
/*    */   
/*    */   public String getCommand() {
/* 58 */     return this.command;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\chat\clickevent\RunCommandClickEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */