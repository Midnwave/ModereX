/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception.handling;
/*     */ 
/*     */ import ac.grim.grimac.shaded.geantyref.TypeToken;
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public final class ExceptionController<C>
/*     */ {
/*  49 */   private final ExceptionContextFactory<C> exceptionContextFactory = new ExceptionContextFactory<>(this);
/*     */ 
/*     */ 
/*     */   
/*     */   private final Map<Type, LinkedList<ExceptionHandlerRegistration<C, ?>>> registrations;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable unwrapCompletionException(Throwable throwable) {
/*  59 */     if (throwable instanceof java.util.concurrent.CompletionException) {
/*  60 */       return unwrapCompletionException(throwable.getCause());
/*     */     }
/*  62 */     return throwable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExceptionController() {
/*  69 */     this.registrations = new HashMap<>();
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
/*     */   public <T extends Throwable> void handleException(CommandContext<C> commandContext, T exception) throws Throwable {
/*  92 */     ExceptionContext<C, T> exceptionContext = this.exceptionContextFactory.createContext(commandContext, exception);
/*     */     
/*  94 */     Class<?> exceptionClass = exception.getClass();
/*  95 */     while (exceptionClass != Object.class) {
/*  96 */       List<ExceptionHandlerRegistration<C, ?>> registrations = registrations(exceptionClass);
/*  97 */       for (ExceptionHandlerRegistration<C, ?> registration : registrations) {
/*  98 */         if (!registration.exceptionFilter().test(exception)) {
/*     */           continue;
/*     */         }
/*     */         
/*     */         try {
/* 103 */           registration.exceptionHandler().handle(exceptionContext);
/* 104 */         } catch (Throwable throwable) {
/* 105 */           if (throwable.equals(exception)) {
/*     */             continue;
/*     */           }
/*     */           
/* 109 */           handleException(commandContext, throwable);
/*     */         } 
/*     */         return;
/*     */       } 
/* 113 */       exceptionClass = exceptionClass.getSuperclass();
/*     */     } 
/*     */ 
/*     */     
/* 117 */     throw exception;
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
/*     */   public synchronized <T extends Throwable> ExceptionController<C> register(ExceptionHandlerRegistration<C, ? extends T> registration) {
/* 133 */     ((LinkedList<ExceptionHandlerRegistration<C, ? extends T>>)this.registrations.computeIfAbsent(registration.exceptionType().getType(), t -> new LinkedList()))
/* 134 */       .addFirst(registration);
/* 135 */     return this;
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
/*     */   public <T extends Throwable> ExceptionController<C> register(TypeToken<T> exceptionType, ExceptionHandlerRegistration.BuilderDecorator<C, T> decorator) {
/* 153 */     return register(decorator.decorate(ExceptionHandlerRegistration.builder(exceptionType)).build());
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
/*     */   public <T extends Throwable> ExceptionController<C> register(Class<T> exceptionType, ExceptionHandlerRegistration.BuilderDecorator<C, T> decorator) {
/* 171 */     return register(decorator.decorate(ExceptionHandlerRegistration.builder(TypeToken.get(exceptionType))).build());
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
/*     */   public <T extends Throwable> ExceptionController<C> registerHandler(TypeToken<T> exceptionType, ExceptionHandler<C, ? extends T> exceptionHandler) {
/* 189 */     return register(ExceptionHandlerRegistration.of(exceptionType, exceptionHandler));
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
/*     */   public <T extends Throwable> ExceptionController<C> registerHandler(Class<T> exceptionType, ExceptionHandler<C, ? extends T> exceptionHandler) {
/* 207 */     return register(ExceptionHandlerRegistration.of(TypeToken.get(exceptionType), exceptionHandler));
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
/*     */   public void clearHandlers() {
/* 219 */     this.registrations.clear();
/*     */   }
/*     */   
/*     */   private List<ExceptionHandlerRegistration<C, ?>> registrations(Type type) {
/* 223 */     return Collections.unmodifiableList(this.registrations.getOrDefault(type, new LinkedList<>()));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\handling\ExceptionController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */