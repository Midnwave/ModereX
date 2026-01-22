/*     */ package ac.grim.grimac.events.packets;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerAbstract;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketListenerPriority;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.event.PacketSendEvent;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.packettype.PacketType;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.UserProfile;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ 
/*     */ public class PacketHidePlayerInfo
/*     */   extends PacketListenerAbstract
/*     */ {
/*     */   public PacketHidePlayerInfo() {
/*  23 */     super(PacketListenerPriority.HIGHEST);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onPacketSend(PacketSendEvent event) {
/*  28 */     if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
/*     */       
/*  30 */       if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
/*     */         return;
/*     */       }
/*  33 */       GrimPlayer receiver = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*     */       
/*  35 */       if (receiver == null) {
/*     */         return;
/*     */       }
/*     */       
/*  39 */       WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event);
/*     */       
/*  41 */       if (info.getAction() == WrapperPlayServerPlayerInfo.Action.UPDATE_GAME_MODE || info.getAction() == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) {
/*  42 */         List<WrapperPlayServerPlayerInfo.PlayerData> nmsPlayerInfoDataList = info.getPlayerDataList();
/*     */         
/*  44 */         int hideCount = 0;
/*  45 */         for (WrapperPlayServerPlayerInfo.PlayerData playerData : nmsPlayerInfoDataList) {
/*  46 */           if (GrimAPI.INSTANCE.getSpectateManager().shouldHidePlayer(receiver, playerData)) {
/*  47 */             hideCount++;
/*  48 */             if (playerData.getGameMode() == GameMode.SPECTATOR) {
/*  49 */               playerData.setGameMode(GameMode.SURVIVAL);
/*     */             }
/*     */           } 
/*     */         } 
/*     */         
/*  54 */         if (hideCount == nmsPlayerInfoDataList.size() && info.getAction() == WrapperPlayServerPlayerInfo.Action.UPDATE_GAME_MODE) {
/*  55 */           event.setCancelled(true);
/*  56 */         } else if (hideCount > 0) {
/*  57 */           event.markForReEncode(true);
/*     */         } 
/*     */       } 
/*  60 */     } else if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
/*  61 */       GrimPlayer receiver = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
/*  62 */       if (receiver == null)
/*     */         return; 
/*  64 */       WrapperPlayServerPlayerInfoUpdate wrapper = new WrapperPlayServerPlayerInfoUpdate(event);
/*  65 */       EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = wrapper.getActions();
/*     */       
/*  67 */       if (actions.contains(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE)) {
/*  68 */         boolean onlyGameMode = (actions.size() == 1);
/*  69 */         int hideCount = 0;
/*  70 */         List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> modified = new ArrayList<>(wrapper.getEntries().size());
/*     */         
/*  72 */         for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo entry : wrapper.getEntries()) {
/*     */           
/*  74 */           WrapperPlayServerPlayerInfoUpdate.PlayerInfo modifiedPacket = null;
/*  75 */           UserProfile gameProfile = entry.getGameProfile();
/*  76 */           if (GrimAPI.INSTANCE.getSpectateManager().shouldHidePlayer(receiver, gameProfile.getUUID())) {
/*  77 */             hideCount++;
/*     */             
/*  79 */             if (entry.getGameMode() == GameMode.SPECTATOR) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*  86 */               modifiedPacket = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(gameProfile, entry.isListed(), entry.getLatency(), GameMode.SURVIVAL, entry.getDisplayName(), entry.getChatSession());
/*     */               
/*  88 */               modified.add(modifiedPacket);
/*     */             } 
/*     */           } 
/*     */           
/*  92 */           if (modifiedPacket == null) {
/*  93 */             modified.add(entry); continue;
/*  94 */           }  if (!onlyGameMode) {
/*  95 */             modified.add(modifiedPacket);
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 101 */         if (hideCount == modified.size()) {
/* 102 */           if (onlyGameMode) {
/* 103 */             event.setCancelled(true);
/*     */           } else {
/* 105 */             wrapper.getActions().remove(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE);
/* 106 */             event.markForReEncode(true);
/*     */           } 
/*     */         } else {
/* 109 */           wrapper.setEntries(modified);
/* 110 */           event.markForReEncode(true);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\events\packets\PacketHidePlayerInfo.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */