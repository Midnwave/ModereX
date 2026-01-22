/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
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
/*    */ public interface PacketTypeCommon
/*    */ {
/*    */   default String getName() {
/* 29 */     return ((Enum<Enum>)this).name();
/*    */   }
/*    */   
/*    */   int getId(ClientVersion paramClientVersion);
/*    */   
/*    */   PacketSide getSide();
/*    */   
/*    */   @Nullable
/*    */   Class<? extends PacketWrapper<?>> getWrapperClass();
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\protocol\packettype\PacketTypeCommon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */