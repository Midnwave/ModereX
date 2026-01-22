/*    */ package ac.grim.grimac.shaded.incendo.cloud.bukkit;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.server.PluginDisableEvent;
/*    */ import org.spigotmc.event.player.PlayerSpawnLocationEvent;
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
/*    */ final class CloudBukkitListener<C>
/*    */   implements Listener
/*    */ {
/*    */   private final BukkitCommandManager<C> bukkitCommandManager;
/*    */   
/*    */   CloudBukkitListener(BukkitCommandManager<C> bukkitCommandManager) {
/* 38 */     this.bukkitCommandManager = bukkitCommandManager;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
/*    */   void onPlayerLogin(PlayerSpawnLocationEvent event) {
/* 47 */     this.bukkitCommandManager.lockIfBrigadierCapable();
/*    */   }
/*    */   
/*    */   @EventHandler(priority = EventPriority.HIGHEST)
/*    */   void onPluginDisable(PluginDisableEvent event) {
/* 52 */     if (event.getPlugin().equals(this.bukkitCommandManager.owningPlugin())) {
/* 53 */       Objects.requireNonNull(this.bukkitCommandManager); this.bukkitCommandManager.rootCommands().forEach(this.bukkitCommandManager::deleteRootCommand);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\bukkit\CloudBukkitListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */