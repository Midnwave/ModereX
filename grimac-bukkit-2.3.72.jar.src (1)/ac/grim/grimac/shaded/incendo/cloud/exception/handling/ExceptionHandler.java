/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception.handling;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import java.util.function.Consumer;
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
/*     */ @FunctionalInterface
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface ExceptionHandler<C, T extends Throwable>
/*     */ {
/*     */   static <C, T extends Throwable> ExceptionHandler<C, T> noopHandler() {
/*  50 */     return ctx -> {
/*     */       
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static <C, T extends Throwable> ExceptionHandler<C, T> passThroughHandler() {
/*  63 */     return ctx -> {
/*     */         throw ctx.exception();
/*     */       };
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
/*     */   static <C, T extends Throwable> ExceptionHandler<C, T> passThroughHandler(Consumer<ExceptionContext<C, T>> consumer) {
/*  80 */     return ctx -> {
/*     */         consumer.accept(ctx);
/*     */         throw ctx.exception();
/*     */       };
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
/*     */   static <C, T extends Throwable> ExceptionHandler<C, T> unwrappingHandler(Predicate<Throwable> predicate) {
/*  99 */     return ctx -> {
/*     */         Throwable cause = ctx.exception().getCause();
/*     */         if (cause != null && predicate.test(cause)) {
/*     */           throw cause;
/*     */         }
/*     */         throw ctx.exception();
/*     */       };
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
/*     */   static <C, T extends Throwable> ExceptionHandler<C, T> unwrappingHandler(Class<? extends Throwable> causeClass) {
/* 121 */     Objects.requireNonNull(causeClass); return unwrappingHandler(causeClass::isInstance);
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
/*     */   static <C, T extends Throwable> ExceptionHandler<C, T> unwrappingHandler() {
/* 134 */     return unwrappingHandler(throwable -> true);
/*     */   }
/*     */   
/*     */   void handle(ExceptionContext<C, T> paramExceptionContext) throws Throwable;
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\handling\ExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */