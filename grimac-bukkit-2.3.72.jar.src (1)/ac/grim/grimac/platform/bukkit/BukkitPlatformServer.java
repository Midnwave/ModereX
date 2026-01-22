/*    */ package ac.grim.grimac.platform.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.PlatformServer;
/*    */ import ac.grim.grimac.platform.api.sender.Sender;
/*    */ import ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.SpigotReflectionUtil;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class BukkitPlatformServer
/*    */   implements PlatformServer
/*    */ {
/*    */   public String getPlatformImplementationString() {
/* 14 */     return Bukkit.getVersion();
/*    */   }
/*    */ 
/*    */   
/*    */   public void dispatchCommand(Sender sender, String command) {
/* 19 */     CommandSender commandSender = GrimACBukkitLoaderPlugin.LOADER.getBukkitSenderFactory().reverse(sender);
/* 20 */     Bukkit.dispatchCommand(commandSender, command);
/*    */   }
/*    */ 
/*    */   
/*    */   public Sender getConsoleSender() {
/* 25 */     return GrimACBukkitLoaderPlugin.LOADER.getBukkitSenderFactory().map((CommandSender)Bukkit.getConsoleSender());
/*    */   }
/*    */ 
/*    */   
/*    */   public void registerOutgoingPluginChannel(String name) {
/* 30 */     GrimACBukkitLoaderPlugin.LOADER.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)GrimACBukkitLoaderPlugin.LOADER, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public double getTPS() {
/* 35 */     return SpigotReflectionUtil.getTPS();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\BukkitPlatformServer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */