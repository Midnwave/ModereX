/*     */ package ac.grim.grimac.shaded.kyori.adventure.nbt;
/*     */ 
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Debug.Renderer;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.NotNull;
/*     */ import ac.grim.grimac.shaded.jetbrains.annotations.Nullable;
/*     */ import ac.grim.grimac.shaded.kyori.examination.ExaminableProperty;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.PrimitiveIterator;
/*     */ import java.util.Spliterator;
/*     */ import java.util.function.IntConsumer;
/*     */ import java.util.stream.IntStream;
/*     */ import java.util.stream.Stream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Renderer(text = "\"int[\" + this.value.length + \"]\"", childrenArray = "this.value", hasChildren = "this.value.length > 0")
/*     */ final class IntArrayBinaryTagImpl
/*     */   extends ArrayBinaryTagImpl
/*     */   implements IntArrayBinaryTag
/*     */ {
/*     */   final int[] value;
/*     */   
/*     */   IntArrayBinaryTagImpl(int... value) {
/*  43 */     this.value = Arrays.copyOf(value, value.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] value() {
/*  48 */     return Arrays.copyOf(this.value, this.value.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  53 */     return this.value.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int get(int index) {
/*  58 */     checkIndex(index, this.value.length);
/*  59 */     return this.value[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public PrimitiveIterator.OfInt iterator() {
/*  64 */     return new PrimitiveIterator.OfInt()
/*     */       {
/*     */         private int index;
/*     */         
/*     */         public boolean hasNext() {
/*  69 */           return (this.index < IntArrayBinaryTagImpl.this.value.length - 1);
/*     */         }
/*     */ 
/*     */         
/*     */         public int nextInt() {
/*  74 */           if (!hasNext()) {
/*  75 */             throw new NoSuchElementException();
/*     */           }
/*  77 */           return IntArrayBinaryTagImpl.this.value[this.index++];
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public Spliterator.OfInt spliterator() {
/*  84 */     return Arrays.spliterator(this.value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public IntStream stream() {
/*  89 */     return Arrays.stream(this.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void forEachInt(@NotNull IntConsumer action) {
/*  94 */     for (int i = 0, length = this.value.length; i < length; i++) {
/*  95 */       action.accept(this.value[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static int[] value(IntArrayBinaryTag tag) {
/* 101 */     return (tag instanceof IntArrayBinaryTagImpl) ? ((IntArrayBinaryTagImpl)tag).value : tag.value();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object other) {
/* 106 */     if (this == other) return true; 
/* 107 */     if (other == null || getClass() != other.getClass()) return false; 
/* 108 */     IntArrayBinaryTagImpl that = (IntArrayBinaryTagImpl)other;
/* 109 */     return Arrays.equals(this.value, that.value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 114 */     return Arrays.hashCode(this.value);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Stream<? extends ExaminableProperty> examinableProperties() {
/* 119 */     return Stream.of(ExaminableProperty.of("value", this.value));
/*     */   }
/*     */ }


/* Location:              C:\Users\Cameron Crenshaw\Downloads\grimac-bukkit-2.3.72.jar!\ac\grim\grimac\shaded\kyori\adventure\nbt\IntArrayBinaryTagImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */