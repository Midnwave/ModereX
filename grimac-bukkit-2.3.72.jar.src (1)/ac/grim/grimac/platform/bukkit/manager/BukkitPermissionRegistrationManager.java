/*    */ package ac.grim.grimac.platform.bukkit.manager;
/*    */ 
/*    */ import ac.grim.grimac.platform.api.manager.PermissionRegistrationManager;
/*    */ import ac.grim.grimac.platform.api.permissions.PermissionDefaultValue;
/*    */ import ac.grim.grimac.platform.bukkit.utils.convert.BukkitConversionUtils;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.permissions.Permission;
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
/*    */ public class BukkitPermissionRegistrationManager
/*    */   implements PermissionRegistrationManager
/*    */ {
/*    */   public void registerPermission(String name, PermissionDefaultValue defaultValue) {
/* 26 */     Permission bukkitPermission = Bukkit.getPluginManager().getPermission(name);
/* 27 */     if (bukkitPermission == null) {
/* 28 */       Bukkit.getPluginManager().addPermission(new Permission(name, BukkitConversionUtils.toBukkitPermissionDefault(defaultValue)));
/*    */     } else {
/* 30 */       bukkitPermission.setDefault(BukkitConversionUtils.toBukkitPermissionDefault(defaultValue));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\platform\bukkit\manager\BukkitPermissionRegistrationManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */