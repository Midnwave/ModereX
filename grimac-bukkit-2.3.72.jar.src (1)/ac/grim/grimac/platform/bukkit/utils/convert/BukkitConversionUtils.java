/*    */ package ac.grim.grimac.platform.bukkit.utils.convert;
/*    */ import ac.grim.grimac.platform.api.permissions.PermissionDefaultValue;
/*    */ import ac.grim.grimac.shaded.com.github.retrooper.packetevents.protocol.world.BlockFace;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Contract;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.utils.math.Location;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.block.BlockFace;
/*    */ import org.bukkit.permissions.PermissionDefault;
/*    */ 
/*    */ public class BukkitConversionUtils {
/*    */   @Contract("null -> null; !null -> new")
/*    */   public static Location toBukkitLocation(Location location) {
/* 14 */     if (location == null) return null; 
/* 15 */     return new Location(((BukkitPlatformWorld)location.getWorld()).getBukkitWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Contract(value = "null -> null; !null -> !null", pure = true)
/*    */   @Nullable
/*    */   public static PermissionDefault toBukkitPermissionDefault(@Nullable PermissionDefaultValue permissionDefaultValue) {
/* 25 */     if (permissionDefaultValue == null) return null; 
/* 26 */     switch (permissionDefaultValue) { default: throw new IncompatibleClassChangeError();case NORTH: case SOUTH: case WEST: case EAST: break; }  return 
/*    */ 
/*    */ 
/*    */       
/* 30 */       PermissionDefault.NOT_OP;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static BlockFace fromBukkitFace(BlockFace face) {
/* 40 */     switch (face) { case NORTH: case SOUTH: case WEST: case EAST: case UP: case DOWN:  }  return 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 47 */       BlockFace.OTHER;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukki\\utils\convert\BukkitConversionUtils.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */