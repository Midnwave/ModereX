/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedArrayType;
/*    */ import java.lang.reflect.AnnotatedType;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class AnnotatedArrayTypeImpl
/*    */   extends AnnotatedTypeImpl
/*    */   implements AnnotatedArrayType
/*    */ {
/*    */   private final AnnotatedType componentType;
/*    */   
/*    */   AnnotatedArrayTypeImpl(Type type, Annotation[] annotations, AnnotatedType componentType) {
/* 18 */     super(type, annotations);
/* 19 */     this.componentType = componentType;
/*    */   }
/*    */   
/*    */   static AnnotatedArrayType createArrayType(AnnotatedType componentType, Annotation[] annotations) {
/* 23 */     return new AnnotatedArrayTypeImpl(GenericArrayTypeImpl.createArrayType(componentType.getType()), annotations, componentType);
/*    */   }
/*    */ 
/*    */   
/*    */   public AnnotatedType getAnnotatedGenericComponentType() {
/* 28 */     return this.componentType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 33 */     if (this == other) return true; 
/* 34 */     return (other instanceof AnnotatedArrayType && super
/* 35 */       .equals(other) && this.componentType
/* 36 */       .equals(((AnnotatedArrayType)other).getAnnotatedGenericComponentType()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 41 */     return 127 * super.hashCode() ^ this.componentType.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return this.componentType.toString() + " " + annotationsString() + "[]";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */