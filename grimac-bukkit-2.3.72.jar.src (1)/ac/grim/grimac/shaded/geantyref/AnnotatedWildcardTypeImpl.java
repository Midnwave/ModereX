/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedType;
/*    */ import java.lang.reflect.AnnotatedWildcardType;
/*    */ import java.lang.reflect.WildcardType;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class AnnotatedWildcardTypeImpl
/*    */   extends AnnotatedTypeImpl
/*    */   implements AnnotatedWildcardType
/*    */ {
/*    */   private final AnnotatedType[] lowerBounds;
/*    */   private final AnnotatedType[] upperBounds;
/*    */   
/*    */   AnnotatedWildcardTypeImpl(WildcardType type, Annotation[] annotations, AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds) {
/* 22 */     super(type, annotations);
/* 23 */     if (lowerBounds == null || lowerBounds.length == 0) {
/* 24 */       lowerBounds = new AnnotatedType[0];
/*    */     }
/* 26 */     if (upperBounds == null || upperBounds.length == 0) {
/* 27 */       upperBounds = new AnnotatedType[1];
/* 28 */       upperBounds[0] = GenericTypeReflector.annotate(Object.class);
/*    */     } 
/* 30 */     validateBounds(type, lowerBounds, upperBounds);
/* 31 */     this.lowerBounds = lowerBounds;
/* 32 */     this.upperBounds = upperBounds;
/*    */   }
/*    */ 
/*    */   
/*    */   public AnnotatedType[] getAnnotatedLowerBounds() {
/* 37 */     return this.lowerBounds;
/*    */   }
/*    */ 
/*    */   
/*    */   public AnnotatedType[] getAnnotatedUpperBounds() {
/* 42 */     return this.upperBounds;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 47 */     if (!(other instanceof AnnotatedWildcardType) || !super.equals(other)) {
/* 48 */       return false;
/*    */     }
/* 50 */     return (GenericTypeReflector.typeArraysEqual(this.lowerBounds, ((AnnotatedWildcardType)other).getAnnotatedLowerBounds()) && 
/* 51 */       GenericTypeReflector.typeArraysEqual(this.upperBounds, ((AnnotatedWildcardType)other).getAnnotatedUpperBounds()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 56 */     return 127 * super.hashCode() ^ GenericTypeReflector.hashCode(this.lowerBounds) + GenericTypeReflector.hashCode(this.upperBounds);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     if (this.lowerBounds.length > 0)
/* 62 */       return annotationsString() + "? super " + typesString(this.lowerBounds); 
/* 63 */     if (this.upperBounds.length == 0 || this.upperBounds[0].getType() == Object.class) {
/* 64 */       return annotationsString() + "?";
/*    */     }
/* 66 */     return annotationsString() + "? extends " + typesString(this.upperBounds);
/*    */   }
/*    */ 
/*    */   
/*    */   private static void validateBounds(WildcardType type, AnnotatedType[] lowerBounds, AnnotatedType[] upperBounds) {
/* 71 */     if ((type.getLowerBounds()).length != lowerBounds.length) {
/* 72 */       throw new IllegalArgumentException("Incompatible lower bounds " + Arrays.toString(lowerBounds) + " for type " + type);
/*    */     }
/* 74 */     if ((type.getUpperBounds()).length != upperBounds.length)
/* 75 */       throw new IllegalArgumentException("Incompatible upper bounds " + Arrays.toString(upperBounds) + " for type " + type); 
/*    */     int i;
/* 77 */     for (i = 0; i < (type.getLowerBounds()).length; i++) {
/* 78 */       if (GenericTypeReflector.erase(type.getLowerBounds()[i]) != GenericTypeReflector.erase(lowerBounds[i].getType())) {
/* 79 */         throw new IllegalArgumentException("Bound " + lowerBounds[i].getType() + " incompatible with " + type
/* 80 */             .getLowerBounds()[i] + " in type " + type);
/*    */       }
/*    */     } 
/* 83 */     for (i = 0; i < (type.getUpperBounds()).length; i++) {
/* 84 */       if (GenericTypeReflector.erase(type.getUpperBounds()[i]) != GenericTypeReflector.erase(upperBounds[i].getType()))
/* 85 */         throw new IllegalArgumentException("Bound " + upperBounds[i].getType() + " incompatible with " + type
/* 86 */             .getUpperBounds()[i] + " in type " + type); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedWildcardTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */