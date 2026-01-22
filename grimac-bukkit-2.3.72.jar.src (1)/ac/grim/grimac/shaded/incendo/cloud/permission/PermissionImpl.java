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
/*     */ @ParametersAreNonnullByDefault
/*     */ @CheckReturnValue
/*     */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */ @Generated(from = "Permission", generator = "Immutables")
/*     */ @Immutable
/*     */ final class PermissionImpl
/*     */   implements Permission
/*     */ {
/*     */   private final String permissionString;
/*     */   
/*     */   private PermissionImpl(String permissionString) {
/*  53 */     this.permissionString = Objects.<String>requireNonNull(permissionString, "permissionString");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private PermissionImpl(PermissionImpl original, String permissionString) {
/*  59 */     this.permissionString = permissionString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String permissionString() {
/*  67 */     return this.permissionString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final PermissionImpl withPermissionString(String value) {
/*  77 */     String newValue = Objects.<String>requireNonNull(value, "permissionString");
/*  78 */     if (this.permissionString.equals(newValue)) return this; 
/*  79 */     return new PermissionImpl(this, newValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object another) {
/*  88 */     if (this == another) return true; 
/*  89 */     return (another instanceof PermissionImpl && 
/*  90 */       equalTo(0, (PermissionImpl)another));
/*     */   }
/*     */   
/*     */   private boolean equalTo(int synthetic, PermissionImpl another) {
/*  94 */     return this.permissionString.equals(another.permissionString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 103 */     int h = 5381;
/* 104 */     h += (h << 5) + this.permissionString.hashCode();
/* 105 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 114 */     return "Permission{permissionString=" + this.permissionString + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PermissionImpl of(String permissionString) {
/* 125 */     return new PermissionImpl(permissionString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PermissionImpl copyOf(Permission instance) {
/* 136 */     if (instance instanceof PermissionImpl) {
/* 137 */       return (PermissionImpl)instance;
/*     */     }
/* 139 */     return of(instance.permissionString());
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\permission\PermissionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */