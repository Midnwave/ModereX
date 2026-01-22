/*    */ package ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.player;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.ConnectionState;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.PacketWrapper;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Obsolete;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface PlayerManager
/*    */ {
/*    */   int getPing(@NotNull Object paramObject);
/*    */   
/*    */   @NotNull
/*    */   ClientVersion getClientVersion(@NotNull Object paramObject);
/*    */   
/*    */   Object getChannel(@NotNull Object paramObject);
/*    */   
/*    */   User getUser(@NotNull Object paramObject);
/*    */   
/*    */   @Obsolete
/*    */   default ConnectionState getConnectionState(@NotNull Object player) throws IllegalStateException {
/* 50 */     return getUser(player).getConnectionState();
/*    */   }
/*    */   
/*    */   default void sendPacket(@NotNull Object player, @NotNull Object byteBuf) {
/* 54 */     PacketEvents.getAPI().getProtocolManager().sendPacket(getChannel(player), byteBuf);
/*    */   }
/*    */   default void sendPacket(@NotNull Object player, @NotNull PacketWrapper<?> wrapper) {
/* 57 */     PacketEvents.getAPI().getProtocolManager().sendPacket(getChannel(player), wrapper);
/*    */   }
/*    */   
/*    */   default void sendPacketSilently(@NotNull Object player, @NotNull Object byteBuf) {
/* 61 */     PacketEvents.getAPI().getProtocolManager().sendPacketSilently(getChannel(player), byteBuf);
/*    */   }
/*    */   
/*    */   default void sendPacketSilently(@NotNull Object player, @NotNull PacketWrapper<?> wrapper) {
/* 65 */     PacketEvents.getAPI().getProtocolManager().sendPacketSilently(getChannel(player), wrapper);
/*    */   }
/*    */   
/*    */   default void writePacket(@NotNull Object player, @NotNull Object byteBuf) {
/* 69 */     PacketEvents.getAPI().getProtocolManager().writePacket(getChannel(player), byteBuf);
/*    */   }
/*    */   
/*    */   default void writePacket(@NotNull Object player, @NotNull PacketWrapper<?> wrapper) {
/* 73 */     PacketEvents.getAPI().getProtocolManager().writePacket(getChannel(player), wrapper);
/*    */   }
/*    */   
/*    */   default void writePacketSilently(@NotNull Object player, @NotNull Object byteBuf) {
/* 77 */     PacketEvents.getAPI().getProtocolManager().writePacketSilently(getChannel(player), byteBuf);
/*    */   }
/*    */   
/*    */   default void writePacketSilently(@NotNull Object player, @NotNull PacketWrapper<?> wrapper) {
/* 81 */     PacketEvents.getAPI().getProtocolManager().writePacketSilently(getChannel(player), wrapper);
/*    */   }
/*    */   
/*    */   default void receivePacket(Object player, Object byteBuf) {
/* 85 */     PacketEvents.getAPI().getProtocolManager().receivePacket(getChannel(player), byteBuf);
/*    */   }
/*    */   
/*    */   default void receivePacket(Object player, PacketWrapper<?> wrapper) {
/* 89 */     PacketEvents.getAPI().getProtocolManager().receivePacket(getChannel(player), wrapper);
/*    */   }
/*    */   
/*    */   default void receivePacketSilently(Object player, Object byteBuf) {
/* 93 */     PacketEvents.getAPI().getProtocolManager().receivePacketSilently(getChannel(player), byteBuf);
/*    */   }
/*    */   
/*    */   default void receivePacketSilently(Object player, PacketWrapper<?> wrapper) {
/* 97 */     PacketEvents.getAPI().getProtocolManager().receivePacketSilently(getChannel(player), wrapper);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\com\github\retrooper\packetevents\manager\player\PlayerManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */