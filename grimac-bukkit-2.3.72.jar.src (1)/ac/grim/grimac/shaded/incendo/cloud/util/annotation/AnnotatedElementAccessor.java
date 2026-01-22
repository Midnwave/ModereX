/*    */ package ac.grim.grimac.shaded.incendo.cloud.util.annotation;
/*    */ 
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.AnnotatedElement;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Objects;
/*    */ import org.apiguardian.api.API;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @API(status = API.Status.INTERNAL, consumers = {"ac.grim.grimac.shaded.incendo.cloud.*"})
/*    */ final class AnnotatedElementAccessor
/*    */   implements AnnotationAccessor
/*    */ {
/*    */   private final AnnotatedElement element;
/*    */   
/*    */   AnnotatedElementAccessor(AnnotatedElement element) {
/* 42 */     this.element = Objects.<AnnotatedElement>requireNonNull(element, "Method may not be null");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <A extends Annotation> A annotation(Class<A> clazz) {
/* 49 */     return this.element.getAnnotation(clazz);
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<Annotation> annotations() {
/* 54 */     return Collections.unmodifiableCollection(Arrays.asList(this.element.getAnnotations()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\clou\\util\annotation\AnnotatedElementAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */