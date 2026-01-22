/*     */ package ac.grim.grimac.manager;
/*     */ 
/*     */ import ac.grim.grimac.GrimAPI;
/*     */ import ac.grim.grimac.manager.init.ReloadableInitable;
/*     */ import ac.grim.grimac.manager.init.start.StartableInitable;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.player.GrimPlayer;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.NamedTextColor;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.format.TextColor;
/*     */ import ac.grim.grimac.utils.math.Location;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class SpectateManager
/*     */   implements StartableInitable, ReloadableInitable
/*     */ {
/*  24 */   private final Map<UUID, PreviousState> spectatingPlayers = new ConcurrentHashMap<>();
/*  25 */   private final Set<UUID> hiddenPlayers = ConcurrentHashMap.newKeySet();
/*  26 */   private final Set<String> allowedWorlds = ConcurrentHashMap.newKeySet();
/*     */   
/*     */   private boolean checkWorld = false;
/*     */ 
/*     */   
/*     */   public void start() {
/*  32 */     reload();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reload() {
/*  37 */     this.allowedWorlds.clear();
/*  38 */     this.allowedWorlds.addAll(GrimAPI.INSTANCE.getConfigManager().getConfig().getStringListElse("spectators.allowed-worlds", new ArrayList()));
/*  39 */     this.checkWorld = (!this.allowedWorlds.isEmpty() && !((String)(new ArrayList<>(this.allowedWorlds)).get(0)).isEmpty());
/*     */   }
/*     */   
/*     */   public boolean isSpectating(UUID uuid) {
/*  43 */     return this.spectatingPlayers.containsKey(uuid);
/*     */   }
/*     */   
/*     */   public boolean shouldHidePlayer(GrimPlayer receiver, WrapperPlayServerPlayerInfo.PlayerData playerData) {
/*  47 */     return (playerData.getUser() != null && playerData
/*  48 */       .getUser().getUUID() != null && 
/*  49 */       shouldHidePlayer(receiver, playerData.getUser().getUUID()));
/*     */   }
/*     */   
/*     */   public boolean shouldHidePlayer(GrimPlayer receiver, UUID uuid) {
/*  53 */     return (!Objects.equals(uuid, receiver.uuid) && (this.spectatingPlayers
/*  54 */       .containsKey(uuid) || this.hiddenPlayers.contains(uuid)) && (receiver.uuid == null || (
/*  55 */       !this.spectatingPlayers.containsKey(receiver.uuid) && !this.hiddenPlayers.contains(receiver.uuid))) && (!this.checkWorld || (receiver.platformPlayer != null && this.allowedWorlds
/*  56 */       .contains(receiver.platformPlayer.getWorld().getName()))));
/*     */   }
/*     */   
/*     */   public boolean enable(PlatformPlayer platformPlayer) {
/*  60 */     if (this.spectatingPlayers.containsKey(platformPlayer.getUniqueId())) return false; 
/*  61 */     this.spectatingPlayers.put(platformPlayer.getUniqueId(), new PreviousState(platformPlayer.getGameMode(), platformPlayer.getLocation()));
/*  62 */     return true;
/*     */   }
/*     */   
/*     */   public void onLogin(UUID uuid) {
/*  66 */     this.hiddenPlayers.add(uuid);
/*     */   }
/*     */   
/*     */   public void onQuit(UUID uuid) {
/*  70 */     this.hiddenPlayers.remove(uuid);
/*  71 */     handlePlayerStopSpectating(uuid);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disable(PlatformPlayer platformPlayer, boolean teleportBack) {
/*  76 */     PreviousState previousState = this.spectatingPlayers.get(platformPlayer.getUniqueId());
/*  77 */     if (previousState != null) {
/*  78 */       if (teleportBack && previousState.location.isWorldLoaded()) {
/*  79 */         platformPlayer.teleportAsync(previousState.location).thenAccept(bool -> {
/*     */               if (bool.booleanValue()) {
/*     */                 onDisable(previousState, platformPlayer);
/*     */               } else {
/*     */                 platformPlayer.sendMessage((Component)Component.text("Teleport failed, please try again.", (TextColor)NamedTextColor.RED));
/*     */               } 
/*     */             });
/*     */       } else {
/*  87 */         onDisable(previousState, platformPlayer);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void onDisable(PreviousState previousState, PlatformPlayer platformPlayer) {
/*  93 */     platformPlayer.setGameMode(previousState.gameMode);
/*  94 */     handlePlayerStopSpectating(platformPlayer.getUniqueId());
/*     */   }
/*     */   
/*     */   public void handlePlayerStopSpectating(UUID uuid) {
/*  98 */     this.spectatingPlayers.remove(uuid);
/*     */   }
/*     */   private static final class PreviousState extends Record { private final GameMode gameMode; private final Location location;
/* 101 */     private PreviousState(GameMode gameMode, Location location) { this.gameMode = gameMode; this.location = location; } public final String toString() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lac/grim/grimac/manager/SpectateManager$PreviousState;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #101	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/* 101 */       //   0	7	0	this	Lac/grim/grimac/manager/SpectateManager$PreviousState; } public GameMode gameMode() { return this.gameMode; } public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lac/grim/grimac/manager/SpectateManager$PreviousState;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #101	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lac/grim/grimac/manager/SpectateManager$PreviousState; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lac/grim/grimac/manager/SpectateManager$PreviousState;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #101	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lac/grim/grimac/manager/SpectateManager$PreviousState;
/* 101 */       //   0	8	1	o	Ljava/lang/Object; } public Location location() { return this.location; }
/*     */      }
/*     */ 
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\manager\SpectateManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */