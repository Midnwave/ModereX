/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception.handling;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import java.util.function.Predicate;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class ExceptionHandlerRegistration<C, T extends Throwable>
/*     */ {
/*     */   private final TypeToken<T> exceptionType;
/*     */   private final ExceptionHandler<C, ? extends T> exceptionHandler;
/*     */   private final Predicate<T> exceptionFilter;
/*     */   
/*     */   public static <C, T extends Throwable> ExceptionHandlerRegistration<C, ? extends T> of(TypeToken<T> exceptionType, ExceptionHandler<C, ? extends T> exceptionHandler) {
/*  54 */     return builder(exceptionType).exceptionHandler(exceptionHandler).build();
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
/*     */   public static <C, T extends Throwable> ExceptionControllerBuilder<C, T> builder(TypeToken<T> exceptionType) {
/*  70 */     return new ExceptionControllerBuilder<>(exceptionType);
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
/*     */   private ExceptionHandlerRegistration(TypeToken<T> exceptionType, ExceptionHandler<C, ? extends T> exceptionHandler, Predicate<T> exceptionFilter) {
/*  82 */     this.exceptionType = exceptionType;
/*  83 */     this.exceptionHandler = exceptionHandler;
/*  84 */     this.exceptionFilter = exceptionFilter;
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
/*     */   public TypeToken<T> exceptionType() {
/*  96 */     return this.exceptionType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExceptionHandler<C, ? extends T> exceptionHandler() {
/* 105 */     return this.exceptionHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Predicate<T> exceptionFilter() {
/* 116 */     return this.exceptionFilter;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public static final class ExceptionControllerBuilder<C, T extends Throwable>
/*     */   {
/*     */     private final TypeToken<T> exceptionType;
/*     */     
/*     */     private final ExceptionHandler<C, ? extends T> exceptionHandler;
/*     */     
/*     */     private final Predicate<T> exceptionFilter;
/*     */ 
/*     */     
/*     */     private ExceptionControllerBuilder(TypeToken<T> exceptionType, ExceptionHandler<C, ? extends T> exceptionHandler, Predicate<T> exceptionFilter) {
/* 132 */       this.exceptionType = exceptionType;
/* 133 */       this.exceptionHandler = exceptionHandler;
/* 134 */       this.exceptionFilter = exceptionFilter;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private ExceptionControllerBuilder(TypeToken<T> exceptionType) {
/* 140 */       this(exceptionType, ExceptionHandler.noopHandler(), exception -> true);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ExceptionControllerBuilder<C, T> exceptionHandler(ExceptionHandler<C, ? extends T> exceptionHandler) {
/* 152 */       return new ExceptionControllerBuilder(this.exceptionType, exceptionHandler, this.exceptionFilter);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ExceptionControllerBuilder<C, T> exceptionFilter(Predicate<T> exceptionFilter) {
/* 164 */       return new ExceptionControllerBuilder(this.exceptionType, this.exceptionHandler, exceptionFilter);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ExceptionHandlerRegistration<C, ? extends T> build() {
/* 173 */       return new ExceptionHandlerRegistration<>(this.exceptionType, this.exceptionHandler, this.exceptionFilter);
/*     */     }
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   @API(status = API.Status.STABLE)
/*     */   public static interface BuilderDecorator<C, T extends Throwable> {
/*     */     ExceptionHandlerRegistration.ExceptionControllerBuilder<C, T> decorate(ExceptionHandlerRegistration.ExceptionControllerBuilder<C, T> param1ExceptionControllerBuilder);
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\handling\ExceptionHandlerRegistration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */