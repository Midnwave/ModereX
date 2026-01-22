/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.manager.server;
/*    */ 
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.PacketEvents;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerManager;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.manager.server.ServerVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.ClientVersion;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.player.User;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.util.mappings.GlobalRegistryHolder;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.plugin.Plugin;
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
/*    */ public class ServerManagerImpl
/*    */   implements ServerManager
/*    */ {
/*    */   private ServerVersion serverVersion;
/*    */   
/*    */   private ServerVersion resolveVersionNoCache() {
/* 35 */     Plugin plugin = (Plugin)PacketEvents.getAPI().getPlugin();
/* 36 */     String bukkitVersion = Bukkit.getBukkitVersion();
/* 37 */     ServerVersion fallbackVersion = ServerVersion.V_1_8_8;
/*    */     
/* 39 */     String failureToDetectVersionMsg = "Your server software is preventing us from checking the Minecraft Server version. This is what we found: " + Bukkit.getBukkitVersion() + ". We will assume the Server version is " + fallbackVersion.name() + "...\n If you need assistance, join our Discord server: https://discord.gg/DVHxPPxHZc";
/*    */     
/* 41 */     if (bukkitVersion.contains("Unknown")) {
/* 42 */       plugin.getLogger().warning(failureToDetectVersionMsg);
/* 43 */       return fallbackVersion;
/*    */     } 
/*    */     
/* 46 */     for (ServerVersion val : ServerVersion.reversedValues()) {
/*    */       
/* 48 */       if (bukkitVersion.contains(val.getReleaseName())) {
/* 49 */         return val;
/*    */       }
/*    */     } 
/*    */     
/* 53 */     plugin.getLogger().warning(failureToDetectVersionMsg);
/* 54 */     return fallbackVersion;
/*    */   }
/*    */ 
/*    */   
/*    */   public ServerVersion getVersion() {
/* 59 */     if (this.serverVersion == null) {
/* 60 */       this.serverVersion = resolveVersionNoCache();
/*    */     }
/* 62 */     return this.serverVersion;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getRegistryCacheKey(User user, ClientVersion version) {
/* 67 */     return GlobalRegistryHolder.getGlobalRegistryCacheKey(user, version);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\manager\server\ServerManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */