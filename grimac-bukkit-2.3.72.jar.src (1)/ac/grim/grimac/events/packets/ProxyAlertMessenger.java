/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketReceiveEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
/*     */ import ac.grim.grimac.shaded.configuralize.DynamicConfig;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*     */ import ac.grim.grimac.utils.anticheat.MessageUtil;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.io.ByteArrayDataInput;
/*     */ import com.google.common.io.ByteArrayDataOutput;
/*     */ import com.google.common.io.ByteStreams;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class ProxyAlertMessenger extends PacketListenerAbstract {
/*     */   public ProxyAlertMessenger() {
/*  28 */     usingProxy = (getBooleanFromFile("spigot.yml", "settings.bungeecord") || getBooleanFromFile("paper.yml", "settings.velocity-support.enabled") || (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19) && getBooleanFromFile("config/paper-global.yml", "proxies.velocity.enabled")));
/*     */     
/*  30 */     if (usingProxy) {
/*  31 */       LogUtil.info("Registering an outgoing plugin channel...");
/*  32 */       GrimAPI.INSTANCE.getPlatformServer().registerOutgoingPluginChannel("BungeeCord");
/*     */     } 
/*     */   }
/*     */   private static boolean usingProxy;
/*     */   public static void sendPluginMessage(String message) {
/*  37 */     if (!canSendAlerts()) {
/*     */       return;
/*     */     }
/*  40 */     ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
/*  41 */     ByteArrayDataOutput out = ByteStreams.newDataOutput();
/*  42 */     out.writeUTF("Forward");
/*  43 */     out.writeUTF("ONLINE");
/*  44 */     out.writeUTF("GRIMAC");
/*     */     
/*     */     try {
/*  47 */       (new DataOutputStream(messageBytes)).writeUTF(message);
/*  48 */     } catch (IOException exception) {
/*  49 */       LogUtil.error("Something went wrong whilst forwarding an alert to other servers!", exception);
/*     */       
/*     */       return;
/*     */     } 
/*  53 */     out.writeShort((messageBytes.toByteArray()).length);
/*  54 */     out.write(messageBytes.toByteArray());
/*     */     
/*  56 */     ((PlatformPlayer)Iterables.getFirst(GrimAPI.INSTANCE.getPlatformPlayerFactory().getOnlinePlayers(), null)).sendPluginMessage("BungeeCord", out.toByteArray());
/*     */   }
/*     */   
/*     */   public static boolean canSendAlerts() {
/*  60 */     return (usingProxy && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("alerts.proxy.send", false) && !GrimAPI.INSTANCE.getPlatformPlayerFactory().getOnlinePlayers().isEmpty());
/*     */   }
/*     */   
/*     */   public static boolean canReceiveAlerts() {
/*  64 */     return (usingProxy && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("alerts.proxy.receive", false) && GrimAPI.INSTANCE.getAlertManager().hasAlertListeners());
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean getBooleanFromFile(String pathToFile, String pathToValue) {
/*  69 */     File file = new File(pathToFile);
/*  70 */     if (!file.exists()) return false;
/*     */     
/*  72 */     DynamicConfig config = new DynamicConfig();
/*  73 */     config.addSource(ProxyAlertMessenger.class, "temp", file);
/*     */     try {
/*  75 */       config.loadAll();
/*  76 */       return config.getBoolean(pathToValue);
/*  77 */     } catch (Exception e) {
/*  78 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onPacketReceive(PacketReceiveEvent event) {
/*     */     String alert;
/*  84 */     if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE || !canReceiveAlerts()) {
/*     */       return;
/*     */     }
/*  87 */     WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
/*     */     
/*  89 */     if (!wrapper.getChannelName().equals("BungeeCord") && !wrapper.getChannelName().equals("bungeecord:main")) {
/*     */       return;
/*     */     }
/*  92 */     ByteArrayDataInput in = ByteStreams.newDataInput(wrapper.getData());
/*     */     
/*  94 */     if (!in.readUTF().equals("GRIMAC")) {
/*     */       return;
/*     */     }
/*  97 */     byte[] messageBytes = new byte[in.readShort()];
/*  98 */     in.readFully(messageBytes);
/*     */     
/*     */     try {
/* 101 */       alert = (new DataInputStream(new ByteArrayInputStream(messageBytes))).readUTF();
/* 102 */     } catch (IOException exception) {
/* 103 */       LogUtil.error("Something went wrong whilst reading an alert forwarded from another server!", exception);
/*     */       return;
/*     */     } 
/* 106 */     Component message = MessageUtil.miniMessage(alert);
/* 107 */     GrimAPI.INSTANCE.getAlertManager().sendAlert(message, null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\ProxyAlertMessenger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */