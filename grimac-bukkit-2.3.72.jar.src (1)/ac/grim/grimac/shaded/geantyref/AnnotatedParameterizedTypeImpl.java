/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedParameterizedType;
/*    */ import java.lang.reflect.AnnotatedType;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class AnnotatedParameterizedTypeImpl
/*    */   extends AnnotatedTypeImpl
/*    */   implements AnnotatedParameterizedType
/*    */ {
/*    */   private final AnnotatedType[] typeArguments;
/*    */   
/*    */   AnnotatedParameterizedTypeImpl(ParameterizedType rawType, Annotation[] annotations, AnnotatedType[] typeArguments) {
/* 20 */     super(rawType, annotations);
/* 21 */     this.typeArguments = typeArguments;
/*    */   }
/*    */ 
/*    */   
/*    */   public AnnotatedType[] getAnnotatedActualTypeArguments() {
/* 26 */     return this.typeArguments;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 31 */     if (this == other) return true; 
/* 32 */     if (!(other instanceof AnnotatedParameterizedType) || !super.equals(other)) {
/* 33 */       return false;
/*    */     }
/* 35 */     return GenericTypeReflector.typeArraysEqual(this.typeArguments, ((AnnotatedParameterizedType)other).getAnnotatedActualTypeArguments());
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 40 */     return 127 * super.hashCode() ^ GenericTypeReflector.hashCode(this.typeArguments);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 45 */     ParameterizedType rawType = (ParameterizedType)this.type;
/* 46 */     String rawName = GenericTypeReflector.getTypeName(rawType.getRawType());
/*    */     
/* 48 */     StringBuilder typeName = new StringBuilder();
/* 49 */     if (rawType.getOwnerType() != null) {
/* 50 */       typeName.append(GenericTypeReflector.getTypeName(rawType.getOwnerType())).append('$');
/*    */ 
/*    */       
/* 53 */       String prefix = (rawType.getOwnerType() instanceof ParameterizedType) ? (((Class)((ParameterizedType)rawType.getOwnerType()).getRawType()).getName() + '$') : (((Class)rawType.getOwnerType()).getName() + '$');
/* 54 */       if (rawName.startsWith(prefix))
/* 55 */         rawName = rawName.substring(prefix.length()); 
/*    */     } 
/* 57 */     typeName.append(rawName);
/* 58 */     return annotationsString() + typeName + "<" + typesString(this.typeArguments) + ">";
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */