/*     */ package ac.grim.grimac.shaded.incendo.cloud.exception.handling;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.context.CommandContext;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @API(status = API.Status.STABLE)
/*     */ public interface ExceptionContext<C, T extends Throwable>
/*     */ {
/*     */   T exception();
/*     */   
/*     */   CommandContext<C> context();
/*     */   
/*     */   ExceptionController<C> controller();
/*     */   
/*     */   @API(status = API.Status.INTERNAL)
/*     */   public static final class ExceptionContextImpl<C, T extends Throwable>
/*     */     implements ExceptionContext<C, T>
/*     */   {
/*     */     private final T exception;
/*     */     private final CommandContext<C> context;
/*     */     private final ExceptionController<C> controller;
/*     */     
/*     */     ExceptionContextImpl(T exception, CommandContext<C> context, ExceptionController<C> controller) {
/*  69 */       this.exception = exception;
/*  70 */       this.context = context;
/*  71 */       this.controller = controller;
/*     */     }
/*     */ 
/*     */     
/*     */     public T exception() {
/*  76 */       return this.exception;
/*     */     }
/*     */ 
/*     */     
/*     */     public CommandContext<C> context() {
/*  81 */       return this.context;
/*     */     }
/*     */ 
/*     */     
/*     */     public ExceptionController<C> controller() {
/*  86 */       return this.controller;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object object) {
/*  91 */       if (this == object) {
/*  92 */         return true;
/*     */       }
/*  94 */       if (object == null || getClass() != object.getClass()) {
/*  95 */         return false;
/*     */       }
/*  97 */       ExceptionContextImpl<?, ?> that = (ExceptionContextImpl<?, ?>)object;
/*  98 */       return (Objects.equals(this.exception, that.exception) && 
/*  99 */         Objects.equals(this.context, that.context) && 
/* 100 */         Objects.equals(this.controller, that.controller));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 105 */       return Objects.hash(new Object[] { this.exception, this.context, this.controller });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\exception\handling\ExceptionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */