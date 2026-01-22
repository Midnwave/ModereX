/*     */ package ac.grim.grimac.shaded.snakeyaml.introspector;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Property
/*     */   implements Comparable<Property>
/*     */ {
/*     */   private final String name;
/*     */   private final Class<?> type;
/*     */   
/*     */   public Property(String name, Class<?> type) {
/*  37 */     this.name = name;
/*  38 */     this.type = type;
/*     */   }
/*     */   
/*     */   public Class<?> getType() {
/*  42 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  48 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  53 */     return getName() + " of " + getType();
/*     */   }
/*     */   
/*     */   public int compareTo(Property o) {
/*  57 */     return getName().compareTo(o.getName());
/*     */   }
/*     */   
/*     */   public boolean isWritable() {
/*  61 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isReadable() {
/*  65 */     return true;
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
/*     */   public int hashCode() {
/*  93 */     return getName().hashCode() + getType().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/*  98 */     if (other instanceof Property) {
/*  99 */       Property p = (Property)other;
/* 100 */       return (getName().equals(p.getName()) && getType().equals(p.getType()));
/*     */     } 
/* 102 */     return false;
/*     */   }
/*     */   
/*     */   public abstract Class<?>[] getActualTypeArguments();
/*     */   
/*     */   public abstract void set(Object paramObject1, Object paramObject2) throws Exception;
/*     */   
/*     */   public abstract Object get(Object paramObject);
/*     */   
/*     */   public abstract List<Annotation> getAnnotations();
/*     */   
/*     */   public abstract <A extends Annotation> A getAnnotation(Class<A> paramClass);
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\snakeyaml\introspector\Property.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */