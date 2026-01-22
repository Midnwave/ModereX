/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.injector;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.PacketSide;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
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
/*    */ public interface ChannelInjector
/*    */ {
/*    */   default boolean isServerBound() {
/* 27 */     return true;
/*    */   }
/*    */   
/*    */   void inject();
/*    */   
/*    */   void uninject();
/*    */   
/*    */   void updateUser(Object paramObject, User paramUser);
/*    */   
/*    */   void setPlayer(Object paramObject1, Object paramObject2);
/*    */   
/*    */   boolean isPlayerSet(Object paramObject);
/*    */   
/*    */   boolean isProxy();
/*    */   
/*    */   default PacketSide getPacketSide() {
/* 43 */     return PacketSide.SERVER;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\injector\ChannelInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */