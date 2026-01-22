/*    */ package ac.grim.grimac.checks.impl.misc;
/*    */ 
/*    */ import ac.grim.grimac.GrimAPI;
/*    */ import ac.grim.grimac.checks.Check;
/*    */ import ac.grim.grimac.checks.type.PacketCheck;
/*    */ import ac.grim.grimac.player.GrimPlayer;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientPluginMessage;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*    */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class ClientBrand
/*    */   extends Check implements PacketCheck {
/* 20 */   private static final String CHANNEL = PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13) ? "minecraft:brand" : "MC|Brand";
/*    */   
/* 22 */   private String brand = "vanilla"; @Generated public String getBrand() { return this.brand; }
/*    */   
/*    */   private boolean hasBrand = false;
/*    */   
/*    */   public ClientBrand(GrimPlayer player) {
/* 27 */     super(player);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onPacketReceive(PacketReceiveEvent event) {
/* 32 */     if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
/* 33 */       WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);
/* 34 */       handle(packet.getChannelName(), packet.getData());
/* 35 */     } else if (event.getPacketType() == PacketType.Configuration.Client.PLUGIN_MESSAGE) {
/* 36 */       WrapperConfigClientPluginMessage packet = new WrapperConfigClientPluginMessage(event);
/* 37 */       handle(packet.getChannelName(), packet.getData());
/*    */     } 
/*    */   }
/*    */   
/*    */   private void handle(String channel, byte[] data) {
/* 42 */     if (!channel.equals(CHANNEL))
/*    */       return; 
/* 44 */     if (data.length > 64 || data.length == 0) {
/* 45 */       this.brand = "sent " + data.length + " bytes as brand";
/* 46 */     } else if (!this.hasBrand) {
/* 47 */       byte[] minusLength = new byte[data.length - 1];
/* 48 */       System.arraycopy(data, 1, minusLength, 0, minusLength.length);
/*    */       
/* 50 */       this.brand = (new String(minusLength)).replace(" (Velocity)", "");
/* 51 */       this.brand = MessageUtil.stripColor(this.brand);
/* 52 */       if (!GrimAPI.INSTANCE.getConfigManager().isIgnoredClient(this.brand)) {
/* 53 */         String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("client-brand-format", "%prefix% &f%player% joined using %brand%");
/* 54 */         Component component = MessageUtil.replacePlaceholders(this.player, MessageUtil.miniMessage(message));
/*    */         
/* 56 */         GrimAPI.INSTANCE.getAlertManager().sendBrand(component, null);
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 67 */     boolean hasReachHacks = (this.brand.contains("forge") && this.player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_18_2) && this.player.getClientVersion().isOlderThan(ClientVersion.V_1_19_4));
/* 68 */     if (hasReachHacks && GrimAPI.INSTANCE.getConfigManager().isBlockBlacklistedForgeClients()) {
/* 69 */       this.player.disconnect(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(this.player, GrimAPI.INSTANCE.getConfigManager().getDisconnectBlacklistedForge())));
/*    */     }
/*    */     
/* 72 */     this.hasBrand = true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\checks\impl\misc\ClientBrand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */