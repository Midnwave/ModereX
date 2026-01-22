/*     */ package ac.grim.grimac.shaded.geantyref;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedType;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TypeVariableImpl<D extends GenericDeclaration>
/*     */   implements TypeVariable<D>
/*     */ {
/*     */   private final Map<Class<? extends Annotation>, Annotation> annotations;
/*     */   private final D genericDeclaration;
/*     */   private final String name;
/*     */   private final AnnotatedType[] bounds;
/*     */   
/*     */   TypeVariableImpl(TypeVariable<D> variable, AnnotatedType[] bounds) {
/*  27 */     this(variable, variable.getAnnotations(), bounds);
/*     */   }
/*     */   
/*     */   TypeVariableImpl(TypeVariable<D> variable, Annotation[] annotations, AnnotatedType[] bounds) {
/*  31 */     Objects.requireNonNull(variable);
/*  32 */     this.genericDeclaration = variable.getGenericDeclaration();
/*  33 */     this.name = variable.getName();
/*  34 */     this.annotations = new HashMap<>();
/*  35 */     for (Annotation annotation : annotations) {
/*  36 */       this.annotations.put(annotation.annotationType(), annotation);
/*     */     }
/*  38 */     if (bounds == null || bounds.length == 0) {
/*  39 */       throw new IllegalArgumentException("There must be at least one bound. For an unbound variable, the bound must be Object");
/*     */     }
/*  41 */     this.bounds = bounds;
/*     */   }
/*     */ 
/*     */   
/*     */   public Type[] getBounds() {
/*  46 */     return (Type[])Arrays.<AnnotatedType>stream(this.bounds).map(AnnotatedType::getType).toArray(x$0 -> new Type[x$0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public D getGenericDeclaration() {
/*  51 */     return this.genericDeclaration;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  56 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotatedType[] getAnnotatedBounds() {
/*  61 */     return this.bounds;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
/*  67 */     return (T)this.annotations.get(annotationClass);
/*     */   }
/*     */ 
/*     */   
/*     */   public Annotation[] getAnnotations() {
/*  72 */     return (Annotation[])this.annotations.values().toArray((Object[])new Annotation[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Annotation[] getDeclaredAnnotations() {
/*  78 */     return getAnnotations();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/*  83 */     if (this == other) {
/*  84 */       return true;
/*     */     }
/*  86 */     if (other instanceof TypeVariable) {
/*  87 */       TypeVariable<?> that = (TypeVariable)other;
/*  88 */       return (Objects.equals(this.genericDeclaration, that.getGenericDeclaration()) && Objects.equals(this.name, that.getName()));
/*     */     } 
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  96 */     return this.genericDeclaration.hashCode() ^ this.name.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 101 */     return annotationsString() + getName();
/*     */   }
/*     */   
/*     */   private String annotationsString() {
/* 105 */     return this.annotations.isEmpty() ? "" : (
/*     */       
/* 107 */       (String)this.annotations.values().stream().map(Annotation::toString).collect(Collectors.joining(", ")) + " ");
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\geantyref\TypeVariableImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */