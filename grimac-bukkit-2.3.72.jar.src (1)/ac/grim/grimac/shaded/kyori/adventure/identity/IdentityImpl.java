/*    */ package ac.grim.grimac.shaded.kyori.adventure.identity;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.internal.Internals;
/*    */ import ac.grim.grimac.shaded.kyori.examination.Examinable;
/*    */ import java.util.UUID;
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
/*    */ final class IdentityImpl
/*    */   implements Examinable, Identity
/*    */ {
/*    */   private final UUID uuid;
/*    */   
/*    */   IdentityImpl(UUID uuid) {
/* 36 */     this.uuid = uuid;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public UUID uuid() {
/* 41 */     return this.uuid;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return Internals.toString(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object other) {
/* 51 */     if (this == other) return true; 
/* 52 */     if (!(other instanceof Identity)) return false; 
/* 53 */     Identity that = (Identity)other;
/* 54 */     return this.uuid.equals(that.uuid());
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 59 */     return this.uuid.hashCode();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\identity\IdentityImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */