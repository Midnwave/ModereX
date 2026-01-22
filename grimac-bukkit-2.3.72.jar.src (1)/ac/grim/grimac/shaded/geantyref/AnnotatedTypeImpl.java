/*    */ package ac.grim.grimac.shaded.geantyref;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
/*    */ import java.util.stream.Collectors;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class AnnotatedTypeImpl
/*    */   implements AnnotatedType
/*    */ {
/*    */   protected Type type;
/*    */   protected Map<Class<? extends Annotation>, Annotation> annotations;
/*    */   
/*    */   AnnotatedTypeImpl(Type type) {
/* 24 */     this(type, new Annotation[0]);
/*    */   }
/*    */   
/*    */   AnnotatedTypeImpl(Type type, Annotation[] annotations) {
/* 28 */     this.type = Objects.<Type>requireNonNull(type);
/* 29 */     this.annotations = toMap(annotations);
/*    */   }
/*    */ 
/*    */   
/*    */   public Type getType() {
/* 34 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
/* 40 */     return (T)this.annotations.get(annotationClass);
/*    */   }
/*    */ 
/*    */   
/*    */   public Annotation[] getAnnotations() {
/* 45 */     return (Annotation[])this.annotations.values().toArray((Object[])new Annotation[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Annotation[] getDeclaredAnnotations() {
/* 51 */     return getAnnotations();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 56 */     if (this == other) {
/* 57 */       return true;
/*    */     }
/* 59 */     if (!(other instanceof AnnotatedType)) {
/* 60 */       return false;
/*    */     }
/* 62 */     AnnotatedType that = (AnnotatedType)other;
/* 63 */     return (getType().equals(that.getType()) && Arrays.equals((Object[])getAnnotations(), (Object[])that.getAnnotations()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 68 */     return 127 * getType().hashCode() ^ Arrays.hashCode((Object[])getAnnotations());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 73 */     return annotationsString() + GenericTypeReflector.getTypeName(this.type);
/*    */   }
/*    */   
/*    */   String annotationsString() {
/* 77 */     return this.annotations.isEmpty() ? "" : (
/*    */       
/* 79 */       (String)this.annotations.values().stream().map(Annotation::toString).collect(Collectors.joining(", ")) + " ");
/*    */   }
/*    */   
/*    */   String typesString(AnnotatedType[] types) {
/* 83 */     return Arrays.<AnnotatedType>stream(types)
/* 84 */       .map(Object::toString)
/* 85 */       .collect(Collectors.joining(", "));
/*    */   }
/*    */   
/*    */   protected Map<Class<? extends Annotation>, Annotation> toMap(Annotation[] annotations) {
/* 89 */     Map<Class<? extends Annotation>, Annotation> map = new LinkedHashMap<>();
/* 90 */     for (Annotation annotation : annotations) {
/* 91 */       map.put(annotation.annotationType(), annotation);
/*    */     }
/* 93 */     return Collections.unmodifiableMap(map);
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\AnnotatedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */