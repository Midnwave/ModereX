/*     */ package ac.grim.grimac.platform.bukkit.player;
/*     */ import ac.grim.grimac.platform.api.entity.GrimEntity;
/*     */ import ac.grim.grimac.platform.api.player.PlatformInventory;
/*     */ import ac.grim.grimac.platform.api.player.PlatformPlayer;
/*     */ import ac.grim.grimac.platform.api.sender.Sender;
/*     */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*     */ import ac.grim.grimac.platform.bukkit.entity.BukkitGrimEntity;
/*     */ import ac.grim.grimac.platform.bukkit.utils.anticheat.MultiLibUtil;
/*     */ import ac.grim.grimac.platform.bukkit.utils.convert.BukkitConversionUtils;
/*     */ import ac.grim.grimac.platform.bukkit.utils.reflection.PaperUtils;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.GameMode;
/*     */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.Vector3d;
/*     */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotConversionUtil;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.platform.bukkit.BukkitAudiences;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.text.Component;
/*     */ import ac.grim.grimac.utils.math.Location;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import lombok.Generated;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.permissions.Permission;
/*     */ import org.bukkit.permissions.PermissionDefault;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class BukkitPlatformPlayer extends BukkitGrimEntity implements PlatformPlayer {
/*  30 */   private static final BukkitAudiences audiences = BukkitAudiences.create((Plugin)GrimACBukkitLoaderPlugin.LOADER); private final Player bukkitPlayer; @Generated
/*     */   public Player getBukkitPlayer() {
/*  32 */     return this.bukkitPlayer;
/*     */   }
/*     */   private final PlatformInventory inventory;
/*     */   
/*     */   public BukkitPlatformPlayer(Player bukkitPlayer) {
/*  37 */     super((Entity)bukkitPlayer);
/*  38 */     this.bukkitPlayer = bukkitPlayer;
/*  39 */     this.inventory = new BukkitPlatformInventory(bukkitPlayer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void kickPlayer(String textReason) {
/*  44 */     this.bukkitPlayer.kickPlayer(textReason);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPermission(String s) {
/*  49 */     return this.bukkitPlayer.hasPermission(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPermission(String s, boolean defaultIfUnset) {
/*  54 */     return this.bukkitPlayer.hasPermission(new Permission(s, defaultIfUnset ? PermissionDefault.TRUE : PermissionDefault.FALSE));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSneaking() {
/*  59 */     return this.bukkitPlayer.isSneaking();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSneaking(boolean isSneaking) {
/*  64 */     this.bukkitPlayer.setSneaking(isSneaking);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(String message) {
/*  69 */     this.bukkitPlayer.sendMessage(message);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendMessage(Component message) {
/*  74 */     audiences.player(this.bukkitPlayer).sendMessage(message);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOnline() {
/*  79 */     return this.bukkitPlayer.isOnline();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  84 */     return this.bukkitPlayer.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateInventory() {
/*  89 */     this.bukkitPlayer.updateInventory();
/*     */   }
/*     */ 
/*     */   
/*     */   public Vector3d getPosition() {
/*  94 */     Location location = this.bukkitPlayer.getLocation();
/*  95 */     return new Vector3d(location.getX(), location.getY(), location.getZ());
/*     */   }
/*     */ 
/*     */   
/*     */   public PlatformInventory getInventory() {
/* 100 */     return this.inventory;
/*     */   }
/*     */ 
/*     */   
/*     */   public GrimEntity getVehicle() {
/* 105 */     return (this.bukkitPlayer.getVehicle() == null) ? null : (GrimEntity)new BukkitGrimEntity(this.bukkitPlayer.getVehicle());
/*     */   }
/*     */ 
/*     */   
/*     */   public GameMode getGameMode() {
/* 110 */     return SpigotConversionUtil.fromBukkitGameMode(this.bukkitPlayer.getGameMode());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setGameMode(GameMode gameMode) {
/* 115 */     this.bukkitPlayer.setGameMode(SpigotConversionUtil.toBukkitGameMode(gameMode));
/*     */   }
/*     */   
/*     */   public World getBukkitWorld() {
/* 119 */     return this.bukkitPlayer.getWorld();
/*     */   }
/*     */ 
/*     */   
/*     */   public UUID getUniqueId() {
/* 124 */     return this.bukkitPlayer.getUniqueId();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean eject() {
/* 129 */     return this.bukkitPlayer.eject();
/*     */   }
/*     */ 
/*     */   
/*     */   public CompletableFuture<Boolean> teleportAsync(Location location) {
/* 134 */     Location bLoc = BukkitConversionUtils.toBukkitLocation(location);
/* 135 */     return PaperUtils.teleportAsync((Entity)this.bukkitPlayer, bLoc);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isExternalPlayer() {
/* 140 */     return MultiLibUtil.isExternalPlayer(this.bukkitPlayer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPluginMessage(String channelName, byte[] byteArray) {
/* 145 */     this.bukkitPlayer.sendPluginMessage((Plugin)GrimACBukkitLoaderPlugin.LOADER, channelName, byteArray);
/*     */   }
/*     */ 
/*     */   
/*     */   public Sender getSender() {
/* 150 */     return GrimACBukkitLoaderPlugin.LOADER.getBukkitSenderFactory().map((CommandSender)this.bukkitPlayer);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Player getNative() {
/* 156 */     return this.bukkitPlayer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\player\BukkitPlatformPlayer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */