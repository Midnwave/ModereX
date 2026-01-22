/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedType;
/*    */ import java.lang.reflect.AnnotatedTypeVariable;
/*    */ import java.lang.reflect.GenericDeclaration;
/*    */ import java.lang.reflect.TypeVariable;
/*    */ 
/*    */ 
/*    */ 
/*    */ class AnnotatedTypeVariableImpl
/*    */   extends AnnotatedTypeImpl
/*    */   implements AnnotatedTypeVariable
/*    */ {
/*    */   private AnnotatedType[] annotatedBounds;
/*    */   
/*    */   AnnotatedTypeVariableImpl(TypeVariable<?> type) {
/* 18 */     this(type, type.getAnnotations());
/*    */   }
/*    */   
/*    */   AnnotatedTypeVariableImpl(TypeVariable<?> type, Annotation[] annotations) {
/* 22 */     super(type, annotations);
/* 23 */     AnnotatedType[] annotatedBounds = type.getAnnotatedBounds();
/* 24 */     if (annotatedBounds == null || annotatedBounds.length == 0) {
/* 25 */       annotatedBounds = new AnnotatedType[0];
/*    */     }
/* 27 */     this.annotatedBounds = annotatedBounds;
/*    */   }
/*    */   
/*    */   void init(AnnotatedType[] annotatedBounds) {
/* 31 */     this.type = new TypeVariableImpl<>((TypeVariable<GenericDeclaration>)this.type, getAnnotations(), annotatedBounds);
/* 32 */     this.annotatedBounds = annotatedBounds;
/*    */   }
/*    */ 
/*    */   
/*    */   public AnnotatedType[] getAnnotatedBounds() {
/* 37 */     return (AnnotatedType[])this.annotatedBounds.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 42 */     return (other instanceof AnnotatedTypeVariable && super.equals(other));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     return annotationsString() + ((TypeVariable)this.type).getName();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedTypeVariableImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */