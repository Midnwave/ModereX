/*     */ package ac.grim.grimac.shaded.incendo.cloud.permission;
/*     */ 
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Value.Immutable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ @Immutable
/*     */ public interface PermissionResult
/*     */ {
/*     */   boolean allowed();
/*     */   
/*     */   default boolean denied() {
/*  63 */     return !allowed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Permission permission();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static PermissionResult of(boolean result, Permission permission) {
/*  81 */     return PermissionResultImpl.of(result, permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static PermissionResult allowed(Permission permission) {
/*  91 */     return PermissionResultImpl.of(true, permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static PermissionResult denied(Permission permission) {
/* 101 */     return PermissionResultImpl.of(false, permission);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\PermissionResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */