/*    */ package ac.grim.grimac.platform.bukkit.scheduler.folia;
/*    */ import ac.grim.grimac.platform.api.scheduler.AsyncScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.EntityScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.GlobalRegionScheduler;
/*    */ import ac.grim.grimac.platform.api.scheduler.PlatformScheduler;
/*    */ import lombok.Generated;
/*    */ 
/*    */ public class FoliaPlatformScheduler implements PlatformScheduler {
/*  9 */   private final FoliaAsyncScheduler asyncScheduler = new FoliaAsyncScheduler(); @Generated public FoliaAsyncScheduler getAsyncScheduler() { return this.asyncScheduler; }
/* 10 */    private final FoliaGlobalRegionScheduler globalRegionScheduler = new FoliaGlobalRegionScheduler(); @Generated public FoliaGlobalRegionScheduler getGlobalRegionScheduler() { return this.globalRegionScheduler; }
/* 11 */    private final FoliaEntityScheduler entityScheduler = new FoliaEntityScheduler(); @Generated public FoliaEntityScheduler getEntityScheduler() { return this.entityScheduler; }
/* 12 */    private final FoliaRegionScheduler regionScheduler = new FoliaRegionScheduler(); @Generated public FoliaRegionScheduler getRegionScheduler() { return this.regionScheduler; }
/*    */ 
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\scheduler\folia\FoliaPlatformScheduler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */