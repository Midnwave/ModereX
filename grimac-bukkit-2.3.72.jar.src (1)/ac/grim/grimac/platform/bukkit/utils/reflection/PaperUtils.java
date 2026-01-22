/*    */ package ac.grim.grimac.platform.bukkit.utils.reflection;
/*    */ import ac.grim.grimac.platform.bukkit.GrimACBukkitLoaderPlugin;
/*    */ import ac.grim.grimac.utils.anticheat.LogUtil;
/*    */ import ac.grim.grimac.utils.reflection.ReflectionUtils;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.entity.Entity;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.EventException;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class PaperUtils {
/* 15 */   public static final boolean PAPER = (ReflectionUtils.hasClass("com.destroystokyo.paper.PaperConfig") || 
/* 16 */     ReflectionUtils.hasClass("io.papermc.paper.configuration.Configuration"));
/*    */   
/*    */   public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location) {
/* 19 */     return PAPER ? entity.teleportAsync(location) : CompletableFuture.<Boolean>completedFuture(Boolean.valueOf(entity.teleport(location)));
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean registerTickEndEvent(Listener listener, Runnable runnable) {
/*    */     try {
/* 25 */       Class<?> clazz = ReflectionUtils.getClass("com.destroystokyo.paper.event.server.ServerTickEndEvent");
/* 26 */       if (clazz == null) return false; 
/* 27 */       GrimACBukkitLoaderPlugin.LOADER.getServer().getPluginManager().registerEvent(clazz, listener, EventPriority.NORMAL, (l, event) -> runnable.run(), (Plugin)GrimACBukkitLoaderPlugin.LOADER);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 34 */       return true;
/* 35 */     } catch (Exception e) {
/* 36 */       LogUtil.error("Failed to register tick end event", e);
/*    */       
/* 38 */       return false;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukki\\utils\reflection\PaperUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */