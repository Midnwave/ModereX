/*     */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*     */ 
/*     */ import ac.grim.grimac.shaded.snakeyaml.error.YAMLException;
/*     */ import ac.grim.grimac.shaded.snakeyaml.util.ArrayUtils;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
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
/*     */ public class MethodProperty
/*     */   extends GenericProperty
/*     */ {
/*     */   private final PropertyDescriptor property;
/*     */   private final boolean readable;
/*     */   private final boolean writable;
/*     */   
/*     */   private static Type discoverGenericType(PropertyDescriptor property) {
/*  39 */     Method readMethod = property.getReadMethod();
/*  40 */     if (readMethod != null) {
/*  41 */       return readMethod.getGenericReturnType();
/*     */     }
/*     */     
/*  44 */     Method writeMethod = property.getWriteMethod();
/*  45 */     if (writeMethod != null) {
/*  46 */       Type[] paramTypes = writeMethod.getGenericParameterTypes();
/*  47 */       if (paramTypes.length > 0) {
/*  48 */         return paramTypes[0];
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  55 */     return null;
/*     */   }
/*     */   
/*     */   public MethodProperty(PropertyDescriptor property) {
/*  59 */     super(property.getName(), property.getPropertyType(), 
/*  60 */         discoverGenericType(property));
/*  61 */     this.property = property;
/*  62 */     this.readable = (property.getReadMethod() != null);
/*  63 */     this.writable = (property.getWriteMethod() != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(Object object, Object value) throws Exception {
/*  68 */     if (!this.writable) {
/*  69 */       throw new YAMLException("No writable property '" + 
/*  70 */           getName() + "' on class: " + object.getClass().getName());
/*     */     }
/*  72 */     this.property.getWriteMethod().invoke(object, new Object[] { value });
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(Object object) {
/*     */     try {
/*  78 */       this.property.getReadMethod().setAccessible(true);
/*  79 */       return this.property.getReadMethod().invoke(object, new Object[0]);
/*  80 */     } catch (Exception e) {
/*  81 */       throw new YAMLException("Unable to find getter for property '" + this.property.getName() + "' on object " + object + ":" + e);
/*     */     } 
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
/*     */   public List<Annotation> getAnnotations() {
/*     */     List<Annotation> annotations;
/*  96 */     if (isReadable() && isWritable()) {
/*  97 */       annotations = ArrayUtils.toUnmodifiableCompositeList((Object[])this.property
/*  98 */           .getReadMethod().getAnnotations(), (Object[])this.property.getWriteMethod().getAnnotations());
/*  99 */     } else if (isReadable()) {
/* 100 */       annotations = ArrayUtils.toUnmodifiableList((Object[])this.property.getReadMethod().getAnnotations());
/*     */     } else {
/* 102 */       annotations = ArrayUtils.toUnmodifiableList((Object[])this.property.getWriteMethod().getAnnotations());
/*     */     } 
/* 104 */     return annotations;
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
/*     */   public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
/* 117 */     A annotation = null;
/* 118 */     if (isReadable()) {
/* 119 */       annotation = this.property.getReadMethod().getAnnotation(annotationType);
/*     */     }
/* 121 */     if (annotation == null && isWritable()) {
/* 122 */       annotation = this.property.getWriteMethod().getAnnotation(annotationType);
/*     */     }
/* 124 */     return annotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWritable() {
/* 129 */     return this.writable;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadable() {
/* 134 */     return this.readable;
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\MethodProperty.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */