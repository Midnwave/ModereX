/*    */ package ac.grim.grimac.shaded.kyori.adventure.text.minimessage;
/*    */ 
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*    */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.Tag;
/*    */ import ac.grim.grimac.shaded.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
/*    */ import java.util.List;
/*    */ import java.util.Objects;
/*    */ import java.util.function.Supplier;
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
/*    */ final class ArgumentQueueImpl<T extends Tag.Argument>
/*    */   implements ArgumentQueue
/*    */ {
/*    */   private final Context context;
/*    */   final List<T> args;
/* 38 */   private int ptr = 0;
/*    */   
/*    */   ArgumentQueueImpl(Context context, List<T> args) {
/* 41 */     this.context = context;
/* 42 */     this.args = args;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public T pop() {
/* 47 */     if (!hasNext()) {
/* 48 */       throw this.context.newException("Missing argument for this tag!", this);
/*    */     }
/* 50 */     return this.args.get(this.ptr++);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public T popOr(@NotNull String errorMessage) {
/* 55 */     Objects.requireNonNull(errorMessage, "errorMessage");
/* 56 */     if (!hasNext()) {
/* 57 */       throw this.context.newException(errorMessage, this);
/*    */     }
/* 59 */     return this.args.get(this.ptr++);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public T popOr(@NotNull Supplier<String> errorMessage) {
/* 64 */     Objects.requireNonNull(errorMessage, "errorMessage");
/* 65 */     if (!hasNext()) {
/* 66 */       throw this.context.newException((String)Objects.requireNonNull((String)errorMessage.get(), "errorMessage.get()"), this);
/*    */     }
/* 68 */     return this.args.get(this.ptr++);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public T peek() {
/* 73 */     return hasNext() ? this.args.get(this.ptr) : null;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 78 */     return (this.ptr < this.args.size());
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 83 */     this.ptr = 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 88 */     return this.args.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\text\minimessage\ArgumentQueueImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */