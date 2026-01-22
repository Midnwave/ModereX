/*     */ package ac.grim.grimac.shaded.io.github.retrooper.packetevents.util.folia;
/*     */ 
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.event.EventException;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FoliaScheduler
/*     */ {
/*     */   static final boolean isFolia;
/*     */   private static Class<? extends Event> regionizedServerInitEventClass;
/*     */   private static AsyncScheduler asyncScheduler;
/*     */   private static EntityScheduler entityScheduler;
/*     */   private static GlobalRegionScheduler globalRegionScheduler;
/*     */   private static RegionScheduler regionScheduler;
/*     */   
/*     */   static {
/*     */     boolean folia;
/*     */     try {
/*  44 */       Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
/*  45 */       folia = true;
/*     */ 
/*     */ 
/*     */       
/*  49 */       regionizedServerInitEventClass = (Class)Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent");
/*  50 */     } catch (ClassNotFoundException e) {
/*  51 */       folia = false;
/*     */     } 
/*     */     
/*  54 */     isFolia = folia;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isFolia() {
/*  61 */     return isFolia;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AsyncScheduler getAsyncScheduler() {
/*  70 */     if (asyncScheduler == null) {
/*  71 */       asyncScheduler = new AsyncScheduler();
/*     */     }
/*  73 */     return asyncScheduler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static EntityScheduler getEntityScheduler() {
/*  82 */     if (entityScheduler == null) {
/*  83 */       entityScheduler = new EntityScheduler();
/*     */     }
/*  85 */     return entityScheduler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GlobalRegionScheduler getGlobalRegionScheduler() {
/*  94 */     if (globalRegionScheduler == null) {
/*  95 */       globalRegionScheduler = new GlobalRegionScheduler();
/*     */     }
/*  97 */     return globalRegionScheduler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RegionScheduler getRegionScheduler() {
/* 106 */     if (regionScheduler == null) {
/* 107 */       regionScheduler = new RegionScheduler();
/*     */     }
/* 109 */     return regionScheduler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void runTaskOnInit(Plugin plugin, Runnable run) {
/* 122 */     if (!isFolia) {
/* 123 */       Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, run);
/*     */       
/*     */       return;
/*     */     } 
/* 127 */     Bukkit.getServer().getPluginManager().registerEvent(regionizedServerInitEventClass, new Listener() {  }, EventPriority.HIGHEST, (listener, event) -> run.run(), plugin);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\io\github\retrooper\packetevent\\util\folia\FoliaScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */