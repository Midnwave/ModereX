/*     */ package ac.grim.grimac.shaded.kyori.adventure.pointer;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.kyori.adventure.util.Buildable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class PointersImpl
/*     */   implements Pointers
/*     */ {
/*  35 */   static final Pointers EMPTY = new Pointers() {
/*     */       @NotNull
/*     */       public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
/*  38 */         return Optional.empty();
/*     */       }
/*     */ 
/*     */       
/*     */       public <T> boolean supports(@NotNull Pointer<T> pointer) {
/*  43 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public Pointers.Builder toBuilder() {
/*  48 */         return new PointersImpl.BuilderImpl();
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/*  53 */         return "EmptyPointers";
/*     */       }
/*     */     };
/*     */   
/*     */   private final Map<Pointer<?>, Supplier<?>> pointers;
/*     */   
/*     */   PointersImpl(@NotNull BuilderImpl builder) {
/*  60 */     this.pointers = new HashMap<>(builder.pointers);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public <T> Optional<T> get(@NotNull Pointer<T> pointer) {
/*  66 */     Objects.requireNonNull(pointer, "pointer");
/*  67 */     Supplier<?> supplier = this.pointers.get(pointer);
/*  68 */     if (supplier == null) {
/*  69 */       return Optional.empty();
/*     */     }
/*  71 */     return Optional.ofNullable((T)supplier.get());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> boolean supports(@NotNull Pointer<T> pointer) {
/*  77 */     Objects.requireNonNull(pointer, "pointer");
/*  78 */     return this.pointers.containsKey(pointer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Pointers.Builder toBuilder() {
/*  83 */     return new BuilderImpl(this);
/*     */   }
/*     */   
/*     */   static final class BuilderImpl implements Pointers.Builder {
/*     */     private final Map<Pointer<?>, Supplier<?>> pointers;
/*     */     
/*     */     BuilderImpl() {
/*  90 */       this.pointers = new HashMap<>();
/*     */     }
/*     */     
/*     */     BuilderImpl(@NotNull PointersImpl pointers) {
/*  94 */       this.pointers = new HashMap<>(pointers.pointers);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public <T> Pointers.Builder withDynamic(@NotNull Pointer<T> pointer, @NotNull Supplier<T> value) {
/*  99 */       this.pointers.put(Objects.<Pointer>requireNonNull(pointer, "pointer"), Objects.<Supplier>requireNonNull(value, "value"));
/* 100 */       return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Pointers build() {
/* 105 */       return new PointersImpl(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\pointer\PointersImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */