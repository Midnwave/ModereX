/*    */ package ac.grim.grimac.shaded.incendo.cloud.permission;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Iterator;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
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
/*    */ public final class AndPermission
/*    */   implements Permission
/*    */ {
/*    */   private final Set<Permission> permissions;
/*    */   
/*    */   AndPermission(Set<Permission> permissions) {
/* 43 */     if (permissions.isEmpty()) {
/* 44 */       throw new IllegalArgumentException("AndPermission may not have an empty set of permissions");
/*    */     }
/* 46 */     this.permissions = Collections.unmodifiableSet(permissions);
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<Permission> permissions() {
/* 51 */     return this.permissions;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 56 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String permissionString() {
/* 61 */     StringBuilder stringBuilder = new StringBuilder();
/* 62 */     Iterator<Permission> iterator = this.permissions.iterator();
/* 63 */     while (iterator.hasNext()) {
/* 64 */       Permission permission = iterator.next();
/* 65 */       stringBuilder.append('(').append(permission.permissionString()).append(')');
/* 66 */       if (iterator.hasNext()) {
/* 67 */         stringBuilder.append(" & ");
/*    */       }
/*    */     } 
/* 70 */     return stringBuilder.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 75 */     if (this == o) {
/* 76 */       return true;
/*    */     }
/* 78 */     if (o == null || getClass() != o.getClass()) {
/* 79 */       return false;
/*    */     }
/* 81 */     AndPermission that = (AndPermission)o;
/* 82 */     return this.permissions.equals(that.permissions);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 87 */     return Objects.hash(new Object[] { permissions() });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 92 */     return permissionString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\AndPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */