/*     */ package ac.grim.grimac.shaded.incendo.cloud.injection;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.util.annotation.AnnotationAccessor;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @FunctionalInterface
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface ParameterInjector<C, T>
/*     */ {
/*     */   @API(status = API.Status.STABLE)
/*     */   static <C, T> ParameterInjector<C, T> constantInjector(T value) {
/*  54 */     return new ConstantInjector<>(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   T create(CommandContext<C> paramCommandContext, AnnotationAccessor paramAnnotationAccessor);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class ConstantInjector<C, T>
/*     */     implements ParameterInjector<C, T>
/*     */   {
/*     */     private final T value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private ConstantInjector(T value) {
/*  76 */       this.value = value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public T create(CommandContext<C> context, AnnotationAccessor annotationAccessor) {
/*  84 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/*  89 */       if (this == o) {
/*  90 */         return true;
/*     */       }
/*  92 */       if (o == null || getClass() != o.getClass()) {
/*  93 */         return false;
/*     */       }
/*  95 */       ConstantInjector<?, ?> that = (ConstantInjector<?, ?>)o;
/*  96 */       return Objects.equals(this.value, that.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 101 */       return Objects.hash(new Object[] { this.value });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 106 */       return "ConstantInjector{value=" + this.value + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\injection\ParameterInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */