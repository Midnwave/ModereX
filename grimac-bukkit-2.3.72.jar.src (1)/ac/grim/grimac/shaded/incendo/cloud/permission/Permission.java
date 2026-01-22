/*     */ package ac.grim.grimac.shaded.incendo.cloud.permission;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ @Immutable
/*     */ public interface Permission
/*     */ {
/*  51 */   public static final Permission EMPTY = permission("");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission permission(String permission) {
/*  60 */     return PermissionImpl.of(permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission of(String permission) {
/*  70 */     return permission(permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission empty() {
/*  79 */     return EMPTY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission allOf(Collection<Permission> permissions) {
/*  89 */     Set<Permission> objects = new HashSet<>();
/*  90 */     for (Permission permission : permissions) {
/*  91 */       if (permission instanceof AndPermission) {
/*  92 */         objects.addAll(permission.permissions()); continue;
/*     */       } 
/*  94 */       objects.add(permission);
/*     */     } 
/*     */     
/*  97 */     return new AndPermission(objects);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission allOf(Permission... permissions) {
/* 107 */     return allOf(Arrays.asList(permissions));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission anyOf(Collection<Permission> permissions) {
/* 118 */     Set<Permission> objects = new HashSet<>();
/* 119 */     for (Permission permission : permissions) {
/* 120 */       if (permission instanceof OrPermission) {
/* 121 */         objects.addAll(permission.permissions()); continue;
/*     */       } 
/* 123 */       objects.add(permission);
/*     */     } 
/*     */     
/* 126 */     return new OrPermission(objects);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Permission anyOf(Permission... permissions) {
/* 137 */     return anyOf(Arrays.asList(permissions));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default Collection<Permission> permissions() {
/* 146 */     return Collections.singleton(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String permissionString();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default boolean isEmpty() {
/* 164 */     return permissionString().isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default Permission or(Permission other) {
/* 175 */     Objects.requireNonNull(other, "other");
/* 176 */     Set<Permission> permission = new HashSet<>(2);
/* 177 */     permission.add(this);
/* 178 */     permission.add(other);
/* 179 */     return anyOf(permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   Permission or(Permission... other) {
/* 190 */     Objects.requireNonNull(other, "other");
/* 191 */     Set<Permission> permission = new HashSet<>(other.length + 1);
/* 192 */     permission.add(this);
/* 193 */     permission.addAll(Arrays.asList(other));
/* 194 */     return anyOf(permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   default Permission and(Permission other) {
/* 205 */     Objects.requireNonNull(other, "other");
/* 206 */     Set<Permission> permission = new HashSet<>(2);
/* 207 */     permission.add(this);
/* 208 */     permission.add(other);
/* 209 */     return allOf(permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   Permission and(Permission... other) {
/* 220 */     Objects.requireNonNull(other, "other");
/* 221 */     Set<Permission> permission = new HashSet<>(other.length + 1);
/* 222 */     permission.add(this);
/* 223 */     permission.addAll(Arrays.asList(other));
/* 224 */     return allOf(permission);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\Permission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */