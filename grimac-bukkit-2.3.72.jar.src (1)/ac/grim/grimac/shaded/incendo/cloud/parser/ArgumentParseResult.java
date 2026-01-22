/*     */ package ac.grim.grimac.shaded.incendo.cloud.parser;
/*     */ 
/*     */ import ac.grim.grimac.shaded.incendo.cloud.exception.handling.ExceptionController;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
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
/*     */ @API(status = API.Status.STABLE)
/*     */ public abstract class ArgumentParseResult<T>
/*     */ {
/*     */   private ArgumentParseResult() {}
/*     */   
/*     */   public static <T> ArgumentParseResult<T> failure(Throwable failure) {
/*  55 */     return new ParseFailure<>(failure);
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
/*     */   public static <T> CompletableFuture<ArgumentParseResult<T>> failureFuture(Throwable failure) {
/*  67 */     return (new ParseFailure<>(failure)).asFuture();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ArgumentParseResult<T> success(T value) {
/*  78 */     return new ParseSuccess<>(value);
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
/*     */   public static <T> CompletableFuture<ArgumentParseResult<T>> successFuture(T value) {
/*  90 */     return success(value).asFuture();
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
/*     */   @API(status = API.Status.STABLE)
/*     */   public final CompletableFuture<ArgumentParseResult<T>> asFuture() {
/* 116 */     return CompletableFuture.completedFuture(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public abstract Optional<T> parsedValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @API(status = API.Status.STABLE)
/*     */   public abstract Optional<Throwable> failure();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <O> CompletableFuture<ArgumentParseResult<O>> flatMapSuccessFuture(Function<T, CompletableFuture<ArgumentParseResult<O>>> paramFunction);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <O> CompletableFuture<ArgumentParseResult<O>> mapSuccessFuture(Function<T, CompletableFuture<O>> paramFunction);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <O> ArgumentParseResult<O> flatMapSuccess(Function<T, ArgumentParseResult<O>> paramFunction);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract <O> ArgumentParseResult<O> mapSuccess(Function<T, O> paramFunction);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ParseSuccess<T>
/*     */     extends ArgumentParseResult<T>
/*     */   {
/*     */     private final T value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private ParseSuccess(T value) {
/* 171 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public Optional<T> parsedValue() {
/* 176 */       return Optional.of(this.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public Optional<Throwable> failure() {
/* 181 */       return Optional.empty();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <O> CompletableFuture<ArgumentParseResult<O>> flatMapSuccessFuture(Function<T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
/* 188 */       return mapper.apply(this.value);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <O> CompletableFuture<ArgumentParseResult<O>> mapSuccessFuture(Function<T, CompletableFuture<O>> mapper) {
/* 195 */       return ((CompletableFuture)mapper.apply(this.value)).thenApply(ArgumentParseResult::success);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <O> ArgumentParseResult<O> flatMapSuccess(Function<T, ArgumentParseResult<O>> mapper) {
/* 202 */       return mapper.apply(this.value);
/*     */     }
/*     */ 
/*     */     
/*     */     public <O> ArgumentParseResult<O> mapSuccess(Function<T, O> mapper) {
/* 207 */       return ArgumentParseResult.success(mapper.apply(this.value));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class ParseFailure<T>
/*     */     extends ArgumentParseResult<T>
/*     */   {
/*     */     private final Throwable failure;
/*     */ 
/*     */     
/*     */     private ParseFailure(Throwable failure) {
/* 219 */       this.failure = ExceptionController.unwrapCompletionException(failure);
/*     */     }
/*     */ 
/*     */     
/*     */     public Optional<T> parsedValue() {
/* 224 */       return Optional.empty();
/*     */     }
/*     */ 
/*     */     
/*     */     public Optional<Throwable> failure() {
/* 229 */       return Optional.of(this.failure);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <O> CompletableFuture<ArgumentParseResult<O>> flatMapSuccessFuture(Function<T, CompletableFuture<ArgumentParseResult<O>>> mapper) {
/* 236 */       return CompletableFuture.completedFuture(self());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public <O> CompletableFuture<ArgumentParseResult<O>> mapSuccessFuture(Function<T, CompletableFuture<O>> mapper) {
/* 243 */       return CompletableFuture.completedFuture(self());
/*     */     }
/*     */ 
/*     */     
/*     */     public <O> ArgumentParseResult<O> flatMapSuccess(Function<T, ArgumentParseResult<O>> mapper) {
/* 248 */       return self();
/*     */     }
/*     */ 
/*     */     
/*     */     public <O> ArgumentParseResult<O> mapSuccess(Function<T, O> mapper) {
/* 253 */       return self();
/*     */     }
/*     */ 
/*     */     
/*     */     private <O> ArgumentParseResult<O> self() {
/* 258 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\incendo\cloud\parser\ArgumentParseResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */