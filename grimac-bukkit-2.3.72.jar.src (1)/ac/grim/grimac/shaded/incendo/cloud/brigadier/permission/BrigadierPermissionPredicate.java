/*    */ package ac.grim.grimac.shaded.incendo.cloud.brigadier.permission;
/*    */ 
/*    */ import ac.grim.grimac.shaded.geantyref.GenericTypeReflector;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.SenderMapper;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.internal.CommandNode;
/*    */ import ac.grim.grimac.shaded.incendo.cloud.permission.Permission;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
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
/*    */ @API(status = API.Status.INTERNAL, since = "2.0.0")
/*    */ public final class BrigadierPermissionPredicate<C, S>
/*    */   implements Predicate<S>
/*    */ {
/*    */   private final SenderMapper<S, C> senderMapper;
/*    */   private final BrigadierPermissionChecker<C> permissionChecker;
/*    */   private final CommandNode<?> node;
/*    */   
/*    */   public BrigadierPermissionPredicate(SenderMapper<S, C> senderMapper, BrigadierPermissionChecker<C> permissionChecker, CommandNode<?> node) {
/* 57 */     this.senderMapper = senderMapper;
/* 58 */     this.permissionChecker = permissionChecker;
/* 59 */     this.node = node;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean test(S source) {
/* 64 */     C cloudSender = (C)this.senderMapper.map(source);
/*    */     
/* 66 */     Map<Type, Permission> accessMap = (Map<Type, Permission>)this.node.nodeMeta().getOrDefault(CommandNode.META_KEY_ACCESS, Collections.emptyMap());
/* 67 */     for (Map.Entry<Type, Permission> entry : accessMap.entrySet()) {
/* 68 */       if (GenericTypeReflector.isSuperType(entry.getKey(), cloudSender.getClass()) && 
/* 69 */         this.permissionChecker.hasPermission(cloudSender, entry.getValue())) {
/* 70 */         return true;
/*    */       }
/*    */     } 
/*    */     
/* 74 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\brigadier\permission\BrigadierPermissionPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */