/*     */ package ac.grim.grimac.shaded.incendo.cloud.permission;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.ParametersAreNonnullByDefault;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import org.apiguardian.api.API;
/*     */ import org.immutables.value.Generated;
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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "PermissionResult", generator = "Immutables")
/*     */ @Immutable
/*     */ final class PermissionResultImpl
/*     */   implements PermissionResult
/*     */ {
/*     */   private final boolean allowed;
/*     */   private final Permission permission;
/*     */   
/*     */   private PermissionResultImpl(boolean allowed, Permission permission) {
/*  56 */     this.allowed = allowed;
/*  57 */     this.permission = Objects.<Permission>requireNonNull(permission, "permission");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PermissionResultImpl(PermissionResultImpl original, boolean allowed, Permission permission) {
/*  64 */     this.allowed = allowed;
/*  65 */     this.permission = permission;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allowed() {
/*  73 */     return this.allowed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Permission permission() {
/*  81 */     return this.permission;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final PermissionResultImpl withAllowed(boolean value) {
/*  91 */     if (this.allowed == value) return this; 
/*  92 */     return new PermissionResultImpl(this, value, this.permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final PermissionResultImpl withPermission(Permission value) {
/* 102 */     if (this.permission == value) return this; 
/* 103 */     Permission newValue = Objects.<Permission>requireNonNull(value, "permission");
/* 104 */     return new PermissionResultImpl(this, this.allowed, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/* 113 */     if (this == another) return true; 
/* 114 */     return (another instanceof PermissionResultImpl && 
/* 115 */       equalTo(0, (PermissionResultImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, PermissionResultImpl another) {
/* 119 */     return (this.allowed == another.allowed && this.permission
/* 120 */       .equals(another.permission));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 129 */     int h = 5381;
/* 130 */     h += (h << 5) + Boolean.hashCode(this.allowed);
/* 131 */     h += (h << 5) + this.permission.hashCode();
/* 132 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 141 */     return "PermissionResult{allowed=" + this.allowed + ", permission=" + this.permission + "}";
/*     */   }
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
/*     */   public static PermissionResultImpl of(boolean allowed, Permission permission) {
/* 154 */     return new PermissionResultImpl(allowed, permission);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PermissionResultImpl copyOf(PermissionResult instance) {
/* 165 */     if (instance instanceof PermissionResultImpl) {
/* 166 */       return (PermissionResultImpl)instance;
/*     */     }
/* 168 */     return of(instance.allowed(), instance.permission());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\PermissionResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */