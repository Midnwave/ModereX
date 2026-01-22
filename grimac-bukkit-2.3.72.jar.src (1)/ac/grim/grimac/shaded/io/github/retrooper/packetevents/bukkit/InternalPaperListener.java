/*    */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.bukkit;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.ApiStatus.Internal;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.PlayerJoinEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.jspecify.annotations.NullMarked;
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
/*    */ 
/*    */ @NullMarked
/*    */ @Internal
/*    */ public class InternalPaperListener
/*    */   implements Listener
/*    */ {
/*    */   private final InternalBukkitListener delegate;
/*    */   
/*    */   public InternalPaperListener(Plugin plugin) {
/* 43 */     this.delegate = new InternalBukkitListener(plugin);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @EventHandler(priority = EventPriority.LOWEST)
/*    */   public void onSpawnLocation(PlayerSpawnLocationEvent event) {
/* 51 */     this.delegate.onPreJoin(event.getPlayer());
/*    */   }
/*    */   
/*    */   @EventHandler(priority = EventPriority.LOWEST)
/*    */   public void onJoin(PlayerJoinEvent event) {
/* 56 */     this.delegate.onPostJoin(event.getPlayer());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevents\bukkit\InternalPaperListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */