/*    */ package ac.grim.grimac.shaded.incendo.cloud.permission;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKeyHolder;
/*    */ import java.util.function.Predicate;
/*    */ import org.apiguardian.api.API;
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
/*    */ @FunctionalInterface
/*    */ @API(status = API.Status.STABLE)
/*    */ public interface PredicatePermission<C>
/*    */   extends Permission, CloudKeyHolder<Void>
/*    */ {
/*    */   static <C> PredicatePermission<C> of(CloudKey<Void> key, Predicate<C> predicate) {
/* 50 */     return new WrappingPredicatePermission<>(key, predicate);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static <C> PredicatePermission<C> of(final Predicate<C> predicate) {
/* 61 */     return new PredicatePermission<C>()
/*    */       {
/*    */         public PermissionResult testPermission(C sender) {
/* 64 */           return PermissionResult.of(predicate.test(sender), this);
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   default CloudKey<Void> key() {
/* 72 */     return CloudKey.of(getClass().getSimpleName());
/*    */   }
/*    */ 
/*    */   
/*    */   default String permissionString() {
/* 77 */     return key().name();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @API(status = API.Status.STABLE)
/*    */   PermissionResult testPermission(C paramC);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default boolean isEmpty() {
/* 91 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\PredicatePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */