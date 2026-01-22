/*    */ package ac.grim.grimac.platform.bukkit.scheduler.bukkit;
/*    */ import ac.grim.grimac.platform.api.scheduler.AsyncScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.EntityScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.GlobalRegionScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.PlatformScheduler;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class BukkitPlatformScheduler implements PlatformScheduler {
/*  9 */   private final BukkitAsyncScheduler asyncScheduler = new BukkitAsyncScheduler(); @Generated public BukkitAsyncScheduler getAsyncScheduler() { return this.asyncScheduler; }
/* 10 */    private final BukkitGlobalRegionScheduler globalRegionScheduler = new BukkitGlobalRegionScheduler(); @Generated public BukkitGlobalRegionScheduler getGlobalRegionScheduler() { return this.globalRegionScheduler; }
/* 11 */    private final BukkitEntityScheduler entityScheduler = new BukkitEntityScheduler(); @Generated public BukkitEntityScheduler getEntityScheduler() { return this.entityScheduler; }
/* 12 */    private final BukkitRegionScheduler regionScheduler = new BukkitRegionScheduler(); @Generated public BukkitRegionScheduler getRegionScheduler() { return this.regionScheduler; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\bukkit\BukkitPlatformScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */