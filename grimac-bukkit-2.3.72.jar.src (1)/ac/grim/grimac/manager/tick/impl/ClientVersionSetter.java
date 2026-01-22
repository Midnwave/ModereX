/*    */ package ac.grim.grimac.manager.tick.impl;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.manager.tick.Tickable;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.netty.channel.ChannelHelper;
/*    */ 
/*    */ public class ClientVersionSetter
/*    */   implements Tickable {
/*    */   public void tick() {
/* 11 */     for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
/*    */       
/* 13 */       if (!ChannelHelper.isOpen(player.user.getChannel())) {
/* 14 */         GrimAPI.INSTANCE.getPlayerDataManager().onDisconnect(player.user);
/*    */         
/*    */         continue;
/*    */       } 
/* 18 */       player.pollData();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\tick\impl\ClientVersionSetter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */