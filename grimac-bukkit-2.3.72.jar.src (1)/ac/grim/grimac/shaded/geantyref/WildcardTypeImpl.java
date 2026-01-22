/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import java.lang.reflect.WildcardType;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class WildcardTypeImpl
/*    */   implements WildcardType
/*    */ {
/*    */   private final Type[] upperBounds;
/*    */   private final Type[] lowerBounds;
/*    */   
/*    */   WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
/* 17 */     if (upperBounds.length == 0)
/* 18 */       throw new IllegalArgumentException("There must be at least one upper bound. For an unbound wildcard, the upper bound must be Object"); 
/* 19 */     this.upperBounds = upperBounds;
/* 20 */     this.lowerBounds = lowerBounds;
/*    */   }
/*    */   
/*    */   public Type[] getUpperBounds() {
/* 24 */     return (Type[])this.upperBounds.clone();
/*    */   }
/*    */   
/*    */   public Type[] getLowerBounds() {
/* 28 */     return (Type[])this.lowerBounds.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 33 */     if (!(other instanceof WildcardType)) {
/* 34 */       return false;
/*    */     }
/* 36 */     WildcardType that = (WildcardType)other;
/* 37 */     return (Arrays.equals((Object[])getLowerBounds(), (Object[])that.getLowerBounds()) && Arrays.equals((Object[])getUpperBounds(), (Object[])that.getUpperBounds()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 42 */     return Arrays.hashCode((Object[])getLowerBounds()) ^ Arrays.hashCode((Object[])getUpperBounds());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     if (this.lowerBounds.length > 0)
/* 48 */       return "? super " + GenericTypeReflector.getTypeName(this.lowerBounds[0]); 
/* 49 */     if (this.upperBounds[0] == Object.class) {
/* 50 */       return "?";
/*    */     }
/* 52 */     return "? extends " + GenericTypeReflector.getTypeName(this.upperBounds[0]);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\WildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */