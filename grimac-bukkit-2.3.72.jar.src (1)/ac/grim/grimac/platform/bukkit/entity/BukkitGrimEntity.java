/*    */ package ac.grim.grimac.platform.bukkit.entity;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.entity.GrimEntity;
/*    */ import ac.grim.grimac.platform.api.world.PlatformWorld;
/*    */ import ac.grim.grimac.platform.bukkit.utils.convert.BukkitConversionUtils;
/*    */ import ac.grim.grimac.platform.bukkit.utils.reflection.PaperUtils;
/*    */ import ac.grim.grimac.platform.bukkit.world.BukkitPlatformWorld;
/*    */ import ac.grim.grimac.utils.math.Location;
/*    */ import java.util.Objects;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.entity.Entity;
/*    */ 
/*    */ public class BukkitGrimEntity
/*    */   implements GrimEntity
/*    */ {
/*    */   private final Entity entity;
/*    */   private BukkitPlatformWorld bukkitPlatformWorld;
/*    */   
/*    */   public BukkitGrimEntity(Entity entity) {
/* 22 */     Objects.requireNonNull(entity);
/* 23 */     this.entity = entity;
/*    */   }
/*    */   
/*    */   public Entity getBukkitEntity() {
/* 27 */     return this.entity;
/*    */   }
/*    */ 
/*    */   
/*    */   public UUID getUniqueId() {
/* 32 */     return this.entity.getUniqueId();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean eject() {
/* 37 */     return this.entity.eject();
/*    */   }
/*    */ 
/*    */   
/*    */   public CompletableFuture<Boolean> teleportAsync(Location location) {
/* 42 */     Location bLoc = BukkitConversionUtils.toBukkitLocation(location);
/* 43 */     return PaperUtils.teleportAsync(this.entity, bLoc);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Entity getNative() {
/* 49 */     return this.entity;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isDead() {
/* 54 */     return this.entity.isDead();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public PlatformWorld getWorld() {
/* 60 */     if (this.bukkitPlatformWorld == null || !this.bukkitPlatformWorld.getBukkitWorld().equals(this.entity.getWorld())) {
/* 61 */       this.bukkitPlatformWorld = new BukkitPlatformWorld(this.entity.getWorld());
/*    */     }
/*    */     
/* 64 */     return (PlatformWorld)this.bukkitPlatformWorld;
/*    */   }
/*    */ 
/*    */   
/*    */   public Location getLocation() {
/* 69 */     Location location = this.entity.getLocation();
/* 70 */     return new Location(
/* 71 */         getWorld(), location
/* 72 */         .getX(), location
/* 73 */         .getY(), location
/* 74 */         .getZ(), location
/* 75 */         .getYaw(), location
/* 76 */         .getPitch());
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\entity\BukkitGrimEntity.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */