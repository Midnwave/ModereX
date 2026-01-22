/*    */ package ac.grim.grimac.shaded.incendo.cloud.permission;
/*    */ 
/*    */ import ac.grim.grimac.shaded.incendo.cloud.key.CloudKey;
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
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ final class WrappingPredicatePermission<C>
/*    */   implements PredicatePermission<C>
/*    */ {
/*    */   private final CloudKey<Void> key;
/*    */   private final Predicate<C> predicate;
/*    */   
/*    */   WrappingPredicatePermission(CloudKey<Void> key, Predicate<C> predicate) {
/* 41 */     this.key = key;
/* 42 */     this.predicate = predicate;
/*    */   }
/*    */ 
/*    */   
/*    */   public PermissionResult testPermission(C sender) {
/* 47 */     return PermissionResult.of(this.predicate.test(sender), this);
/*    */   }
/*    */ 
/*    */   
/*    */   public CloudKey<Void> key() {
/* 52 */     return this.key;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return this.key.name();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\WrappingPredicatePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */