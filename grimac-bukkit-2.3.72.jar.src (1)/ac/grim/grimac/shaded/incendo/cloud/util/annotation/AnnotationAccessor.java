/*     */ package ac.grim.grimac.shaded.incendo.cloud.util.annotation;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import org.apiguardian.api.API;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface AnnotationAccessor
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   static AnnotationAccessor empty() {
/*  48 */     return new NullAnnotationAccessor();
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
/*     */   static AnnotationAccessor of(AnnotatedElement element) {
/*  60 */     return new AnnotatedElementAccessor(element);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   static AnnotationAccessor of(AnnotationAccessor... accessors) {
/*  72 */     return new MultiDelegateAnnotationAccessor(accessors);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   <A extends Annotation> A annotation(Class<A> paramClass);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Collection<Annotation> annotations();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*     */   public static final class NullAnnotationAccessor
/*     */     implements AnnotationAccessor
/*     */   {
/*     */     public <A extends Annotation> A annotation(Class<A> clazz) {
/* 103 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection<Annotation> annotations() {
/* 108 */       return Collections.emptyList();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\clou\\util\annotation\AnnotationAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */